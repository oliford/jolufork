package uk.co.oliford.jolu;

import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.util.Random;

/**
 * One-Line reading and writing of 2D matrix of values to/from a binary file.
 * 
 * Format is fixed and very simple:
 * 
 * 32-bit unsigned integer M - number of rows (might be -1, in which case work
 * it out from file size) 32-bit unsigned integer N - number of columns. 64-bit
 * IEEE floating point (double) data: row0-col0, row0-col1, ... row0-col(N-1),
 * row1-col0, row1-col1, ... row(M-1)-col(N-1)
 */
public class BinaryMatrixFile {

	/**
	 * mustLoad - simply calls load() but catches any IOException thrown. It simple
	 * re-throws it as a RuntimeException Try not to use this for final programs but
	 * it saves a lot of error catching in the meantime.
	 * 
	 * @param fileName The file to read
	 * @param swapDims True to load into arrays as [column][row], flase for
	 *                 [row][column]
	 * @returns 2D array of the data in the file
	 * @throws IOException
	 */
	public static double [][] mustLoad(String fileName, boolean swapDims){
		try {
			return load(fileName, swapDims);
		} catch (IOException e) {
			throw new UncheckedIOException("BinaryMatrixFile.mustLoad() caught the following error from load():", e);
		}
	}

	/**
	 * load - Loads a file, attempts to interpret it as a fixed size 2D matrix of doubles with cols seperated by \t and rows by \n 
	 * @params fileName The file to read
	 * @params swapDims True to load into arrays as [column][row], flase for [row][column]
	 * @returns 2D array of the data in the file
	 * @throws IOException 
	 */
	public static double [][] load(String fileName, boolean swapDims) throws IOException{

		double[][] data = null;

		//open the file
		try (FileInputStream file = new FileInputStream(fileName)) {
			try (DataInputStream ds = new DataInputStream(file)) {

				int i,j,ni,nj;


				ni = ds.readInt();
				nj = ds.readInt();

				if(ni < 0){ //-ve ni usually means it didn't finish writing, we can maybe work out the number of rows by the file size
					long size = (new File(fileName)).length();
					size -= 8; //don't count the ni and nj

					double dni = size / (nj * 8);
					ni = (int)dni;
					if( dni != (double)ni)
						throw new RuntimeException("'"+fileName+"' doesn't have a row count. Working it out from the size and given number of colums ("+nj+") gives non-integer number of rows "+dni+".");
				}

				if(swapDims){
					i=ni;
					ni = nj;
					nj = i;

					data = new double[ni][nj];

					for(j=0;j<nj;j++)			
						for(i=0;i<ni;i++)
							data[i][j] = ds.readDouble();
				}else{

					data = new double[ni][nj];

					for(i=0;i<ni;i++)
						for(j=0;j<nj;j++)	
							data[i][j] = ds.readDouble();
				}
			}
		}
		
		return data;
	}

	/** Write single row of data, throws RuntimeException, rather than IOException and will create directories if neccessary. */
	public static void mustWrite(String fileName, double data[]){ mustWrite(fileName,null,null,new double[][]{ data },true); }
	
	public static void mustWrite(String fileName, double data[][], boolean swapDims){ mustWrite(fileName, null, null, data, swapDims); }
	
	/** Write with column and row headers, throws RuntimeException, rather than IOException and will create directories if neccessary. 
	 * 
	 * @param fileName
	 * @param row0	The first row, will be at top of file. Is the header/coordinate for each column.
	 * @param col0	The first column, will be interleaved with each row. Is the header/coordinate for each row.
	 * @param data	double[rowNumber][columnNumber]
	 */
	public static void mustWrite(String fileName, double row0[], double col0[], double data[]){ mustWrite(fileName,row0,col0,new double[][]{ data },true); }
	
	/** Write with column and row headers, throws RuntimeException, rather than IOException and will create directories if neccessary.
	 * 
	 * @param fileName
	 * @param row0	The first row, will be at top of file. Is the header/coordinate for each column.
	 * @param col0	The first column, will be interleaved with each row. Is the header/coordinate for each row.
	 * @param data	double[rowNumber][columnNumber]
	 * @param swapDims	Transpose entire written matrix, including row0 and col0, so that now col0 will be at top of file.
	 */
	public static void mustWrite(String fileName, double row0[], double col0[], double data[][], boolean swapDims){
		try {
			OneLiners.mkdir(Paths.get(fileName).getParent());
			write(fileName, row0, col0, data, swapDims);
		}
		catch(IOException e){
			throw new UncheckedIOException("AsciiMatrixFile.mustWrite() caught the following error from write():\n", e);
		}		
	}
	
	/** Old non-NIO based write 
	 * @deprecated 
	 */
	@Deprecated
	public static void writeStd(String fileName, double data[][], boolean swapDims) throws IOException{
//		
//		FileOutputStream file = new FileOutputStream(fileName);
//		DataOutputStream ds = new DataOutputStream(file);
//		
//		int i,j;
//		int ni = data.length;
//		//if(ni<1)throw new RuntimeException("Nothing to put into '"+fileName+"', data has 0 rows!");
//		int nj = (ni > 0) ? data[0].length : 0;
//		
//		if(swapDims){
//			ds.writeInt(nj);
//			ds.writeInt(ni);
//			
//			for(j=0;j<nj;j++)		
//				for(i=0;i<ni;i++)
//					ds.writeDouble(data[i][j]);
//		}else{
//			ds.writeInt(ni);
//			ds.writeInt(nj);
//			
//			for(i=0;i<ni;i++)
//				for(j=0;j<nj;j++)			
//					ds.writeDouble(data[i][j]);
//		}
//		
//		// really really write it please
//		ds.flush();
//		file.flush();
//		file.close();
		
		throw new AssertionError("Please use write(String fileName, double data[][], boolean swapDims) instead!");
	}
	
	public static void write(String fileName, double data[][], boolean swapDims) throws IOException{
		write(fileName, null, null, data, swapDims);
	}
		
	/**
	 * Write with column and row headers.
	 * 
	 * @param fileName
	 * @param row0     The first row, will be at top of file. Is the
	 *                 header/coordinate for each column.
	 * @param col0     The first column, will be interleaved with each row. Is the
	 *                 header/coordinate for each row.
	 * @param data     double[rowNumber][columnNumber]
	 * @param swapDims Transpose entire written matrix, including row0 and col0, so
	 *                 that now col0 will be at top of file.
	 */
	public static void write(String fileName, double[] row0, double[] col0, double[][] data, boolean swapDims)
			throws IOException {

		try (FileOutputStream file = new FileOutputStream(fileName);
				FileChannel out = file.getChannel();) {

			int nDataRows = data.length;
			int nDataCols = (nDataRows > 0) ? data[0].length : 0;

			if ((row0 != null && row0.length != nDataCols) || (col0 != null && col0.length != nDataRows)) {
				throw new IllegalArgumentException("Data has " + nDataRows + " rows and " + nDataCols + " cols, but "
						+ " row0 has " + (row0 == null ? "null" : row0.length) + " elements and " + " col0 has "
						+ (col0 == null ? "null" : col0.length) + " elements.");
			}

			int nFileRows = (row0 == null) ? nDataRows : (nDataRows + 1);
			int nFileCols = (col0 == null) ? nDataCols : (nDataCols + 1);

			ByteBuffer bBuf = ByteBuffer.allocate(8);
			IntBuffer iBuf = bBuf.asIntBuffer();
			if (swapDims) {
				iBuf.put(nFileCols);
				iBuf.put(nFileRows);
			} else {
				iBuf.put(nFileRows);
				iBuf.put(nFileCols);
			}
			out.write(bBuf);

			if (swapDims) {
				bBuf = ByteBuffer.allocate(nFileRows * 8);
				DoubleBuffer dBuf = bBuf.asDoubleBuffer();
				if (col0 != null) {
					if (row0 != null) {
						dBuf.put(0); // corner
					}
					dBuf.put(col0);
					out.write(bBuf);
				}

				for (int j = 0; j < nDataCols; j++) {
					// see https://stackoverflow.com/questions/61267495/exception-in-thread-main-java-lang-nosuchmethoderror-java-nio-bytebuffer-flip
					// why these explicit casts may help
					((Buffer) dBuf).rewind();
					((Buffer) bBuf).rewind();
					if (row0 != null) {
						dBuf.put(row0[j]);
					}

					for (int i = 0; i < nDataRows; i++) {
						dBuf.put(data[i][j]);
					}

					out.write(bBuf);
				}

			} else {
				bBuf = ByteBuffer.allocate(nFileCols * 8);
				DoubleBuffer dBuf = bBuf.asDoubleBuffer();

				if (row0 != null) {
					if (col0 != null) {
						dBuf.put(0); // corner
					}
					dBuf.put(row0);
					out.write(bBuf);
				}

				for (int i = 0; i < nDataRows; i++) {
					// see https://stackoverflow.com/questions/61267495/exception-in-thread-main-java-lang-nosuchmethoderror-java-nio-bytebuffer-flip
					// why these explicit casts may help
					((Buffer) dBuf).rewind();
					((Buffer) bBuf).rewind();
					if (col0 != null) {
						dBuf.put(col0[i]);
					}
					dBuf.put(data[i]);

					out.write(bBuf);
				}
			}

		}
	}
	
	
	public static void main(String[] args) {
	
		int nx = 2000;
		int ny = 1000;
		Random randGen = new Random();
		double d[][] = new double[ny][nx];
		
		System.out.print("Randomise ");
		for(int y=0; y < ny; y++){
			for(int x=0; x < nx; x++)
				d[y][x] = randGen.nextDouble();
			System.out.print(".");
		}
		System.out.println("done.");
		
		try {
			long t0 = System.currentTimeMillis();
			BinaryMatrixFile.writeStd("/tmp/std.bin", d, false);
			System.out.println("Standard: " + (System.currentTimeMillis() - t0) + " ms");
			
			t0 = System.currentTimeMillis();
			BinaryMatrixFile.write("/tmp/nio.bin", d, false);
			System.out.println("NIO: " + (System.currentTimeMillis() - t0) + " ms");
			
			t0 = System.currentTimeMillis();
			BinaryMatrixFile.writeStd("/tmp/std-transposed.bin", d, true);
			System.out.println("Standard transposed: " + (System.currentTimeMillis() - t0) + " ms");
			
			t0 = System.currentTimeMillis();
			BinaryMatrixFile.write("/tmp/nio-transposed.bin", d, true);
			System.out.println("NIO transposed: " + (System.currentTimeMillis() - t0) + " ms");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
