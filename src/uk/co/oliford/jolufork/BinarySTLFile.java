package uk.co.oliford.jolufork;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.logging.Logger;


public class BinarySTLFile {

	public static class Triangles {
		public int count;
		public 	byte header[];
		public double normal[][];
		public double vertex1[][];
		public double vertex2[][];
		public double vertex3[][];
		public short attribByteCount[];
		
		public Triangles(int n) {
			header = new byte[80];
			count = n;
			vertex1 = new double[n][3];
			vertex2 = new double[n][3];
			vertex3 = new double[n][3];
			normal = new double[n][3];
			attribByteCount = new short[n];
		}
		
		public Triangles() { }

		public void addSquare(int i, double centre[], double normal[], double up[], double w, double h) {
			
			double r[] = OneLiners.cross(up, normal);
			double a[] = new double[]{ 	
					centre[0] - h/2*up[0] - w/2*r[0], 
					centre[1] - h/2*up[1] - w/2*r[1],
					centre[2] - h/2*up[2] - w/2*r[2] }; 

			double b[] = new double[]{ 	
					centre[0] - h/2*up[0] + w/2*r[0], 
					centre[1] - h/2*up[1] + w/2*r[1],
					centre[2] - h/2*up[2] + w/2*r[2] }; 

			double c[] = new double[]{ 	
					centre[0] + h/2*up[0] + w/2*r[0], 
					centre[1] + h/2*up[1] + w/2*r[1],
					centre[2] + h/2*up[2] + w/2*r[2] }; 

			double d[] = new double[]{ 	
					centre[0] + h/2*up[0] - w/2*r[0], 
					centre[1] + h/2*up[1] - w/2*r[1],
					centre[2] + h/2*up[2] - w/2*r[2] }; 

			this.vertex1[i] = a;
			this.vertex2[i] = b;
			this.vertex3[i] = c;
			
			this.vertex1[i+1] = a.clone();
			this.vertex2[i+1] = c.clone();
			this.vertex3[i+1] = d;
			
			this.normal[i] = normal.clone();
			this.normal[i+1] = normal.clone();
		}

		public static final double[] corner(double centre[], double v1[], double m1, double v2[], double m2, double v3[], double m3){
			return new double[]{
					centre[0] + m1 * v1[0] + m2 * v2[0] + m3 * v3[0],
					centre[1] + m1 * v1[1] + m2 * v2[1] + m3 * v3[1],
					centre[2] + m1 * v1[2] + m2 * v2[2] + m3 * v3[2]
			};
		}
		
		public void addBox(int i, double[] centre, double[] a, double[] b, double[] c) {
		
			double v1[] = corner(centre, a, +1, b, -1, c, -1);
			double v2[] = corner(centre, a, +1, b, +1, c, -1);
			double v3[] = corner(centre, a, +1, b, +1, c, +1);
			double v4[] = corner(centre, a, +1, b, -1, c, +1);
			double v5[] = corner(centre, a, -1, b, -1, c, -1);
			double v6[] = corner(centre, a, -1, b, +1, c, -1);
			double v7[] = corner(centre, a, -1, b, +1, c, +1);
			double v8[] = corner(centre, a, -1, b, -1, c, +1);
			
			/*for(double v[] : new double[][]{ v1, v2, v3, v4 }){
				double R = FastMath.sqrt(v[0]*v[0] + v[1]*v[1]);
				System.out.println("R = " + R +	"\tZ = " + v[1] + "\td = " + OneLiners.length(v));
			}*/			
			
			double ma[] = new double[]{ -a[0], -a[1], -a[2] };
			double mb[] = new double[]{ -b[0], -b[1], -b[2] };
			double mc[] = new double[]{ -c[0], -c[1], -c[2] };
			
			vertex1[0] = v2; vertex2[0] = v3; vertex3[0] = v1; normal[0] = a; //front
			vertex1[1] = v4; vertex2[1] = v1; vertex3[1] = v3; normal[1] = a; //front
			vertex1[2] = v5; vertex2[2] = v8; vertex3[2] = v6; normal[2] = ma;//back
			vertex1[3] = v6; vertex2[3] = v8; vertex3[3] = v7; normal[3] = ma;//back
			vertex1[4] = v1; vertex2[4] = v4; vertex3[4] = v5; normal[4] = mb;//left
			vertex1[5] = v8; vertex2[5] = v5; vertex3[5] = v4; normal[5] = mb;
			vertex1[6] = v2; vertex2[6] = v6; vertex3[6] = v3; normal[6] = b;//right
			vertex1[7] = v3; vertex2[7] = v6; vertex3[7] = v7; normal[7] = b;
			vertex1[8] = v3; vertex2[8] = v7; vertex3[8] = v4; normal[8] = c;//top
			vertex1[9] = v8; vertex2[9] = v4; vertex3[9] = v7; normal[9] = c;
			vertex1[10] = v1; vertex2[10] = v5; vertex3[10] = v2; normal[10] = mc; //bottom
			vertex1[11] = v6; vertex2[11] = v2; vertex3[11] = v5; normal[11] = mc;
		}
	}
	
	public static final Triangles mustRead(String fileName) {
		try {
			return read(fileName);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static final Triangles read(String fileName) throws IOException {
		Triangles ret = new Triangles();
		
		FileInputStream file = new FileInputStream(fileName);		
		FileChannel chan = file.getChannel();
			
		ret.header = new byte[80];
		chan.read(ByteBuffer.wrap(ret.header));
		
		ByteBuffer buff = ByteBuffer.allocate(4);
		chan.read(buff);
		buff.flip();
		buff.order(ByteOrder.LITTLE_ENDIAN);
		ret.count = buff.getInt();
		Logger.getLogger(BinarySTLFile.class.getName()).info("Loading " + ret.count + " triangles from " + fileName + ".");
		
		ret.normal = new double[ret.count][3];
		ret.vertex1 = new double[ret.count][3];
		ret.vertex2 = new double[ret.count][3];
		ret.vertex3 = new double[ret.count][3];
		ret.attribByteCount = new short[ret.count];
		
		buff = ByteBuffer.allocate(50);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		
		for(int i=0; i < ret.count; i++){
			buff.rewind();
			chan.read(buff);
			buff.flip();
			ret.normal[i][0] = buff.getFloat();
			ret.normal[i][1] = buff.getFloat();
			ret.normal[i][2] = buff.getFloat();

			ret.vertex1[i][0] = buff.getFloat();
			ret.vertex1[i][1] = buff.getFloat();
			ret.vertex1[i][2] = buff.getFloat();

			ret.vertex2[i][0] = buff.getFloat();
			ret.vertex2[i][1] = buff.getFloat();
			ret.vertex2[i][2] = buff.getFloat();

			ret.vertex3[i][0] = buff.getFloat();
			ret.vertex3[i][1] = buff.getFloat();
			ret.vertex3[i][2] = buff.getFloat();
			
			ret.attribByteCount[i] = buff.getShort();
		}
		
		file.close();
		
		return ret;
	}
	
	public static final void mustWrite(String fileName, Triangles triangles){
		try {
			OneLiners.mkdir(Paths.get(fileName).getParent());
			write(fileName, triangles);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static final void write(String fileName, Triangles triangles) throws IOException {
				
		FileOutputStream file = new FileOutputStream(fileName);		
		FileChannel chan = file.getChannel();
			
		chan.write(ByteBuffer.wrap(triangles.header));
		
		
		ByteBuffer buff = ByteBuffer.allocate(4);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		buff.putInt(triangles.count);
		buff.flip();
		chan.write(buff);
				
		buff = ByteBuffer.allocate(50);
		buff.order(ByteOrder.LITTLE_ENDIAN);
		
		for(int i=0; i < triangles.count; i++){
			buff.rewind();
			
			buff.putFloat((float)triangles.normal[i][0]);
			buff.putFloat((float)triangles.normal[i][1]);
			buff.putFloat((float)triangles.normal[i][2]);
			
			buff.putFloat((float)triangles.vertex1[i][0]);
			buff.putFloat((float)triangles.vertex1[i][1]);
			buff.putFloat((float)triangles.vertex1[i][2]);
			
			buff.putFloat((float)triangles.vertex2[i][0]);
			buff.putFloat((float)triangles.vertex2[i][1]);
			buff.putFloat((float)triangles.vertex2[i][2]);
			
			buff.putFloat((float)triangles.vertex3[i][0]);
			buff.putFloat((float)triangles.vertex3[i][1]);
			buff.putFloat((float)triangles.vertex3[i][2]);
			
			buff.putShort(triangles.attribByteCount[i]);
			
			buff.flip();
			chan.write(buff);
			
		}
		
		file.close();
		
	}
	
	/** Check that the vertices follow right-hand order w.r.t to normal */
	public static final void orderVerticies(Triangles triangles){
		for(int i=0; i < triangles.count; i++){
			double[] a = OneLiners.minus(triangles.vertex2[i], triangles.vertex1[i]);
			double[] b = OneLiners.minus(triangles.vertex3[i], triangles.vertex2[i]);
			double[] c = OneLiners.cross(a,b);
			
			if(OneLiners.dot(c, triangles.normal[i]) < 0){
				System.out.println("Redordered triangle " + i );
				double v[] = triangles.vertex2[i];
				triangles.vertex2[i] = triangles.vertex3[i];
				triangles.vertex3[i] = v;
			}
		}
	}
}
