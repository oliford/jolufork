package uk.co.oliford.jolufork;

import java.lang.reflect.Array;
import java.nio.*;

public class DataConvertPureJava {
	
	
	/* Java named types from Data routines */
	public static final byte[] ByteArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (byte[])arrayFromData(data, byte.class, dims, bo); }
	public static final short[] ShortArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (short[])arrayFromData(data, short.class, dims, bo); }
	public static final int[] IntArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (int[])arrayFromData(data, int.class, dims, bo); }
	public static final long[] LongArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (long[])arrayFromData(data, long.class, dims, bo); }
	public static final float[] FloatArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (float[])arrayFromData(data, float.class, dims, bo); }
	public static final double[] DoubleArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (double[])arrayFromData(data, double.class, dims, bo); }
	
	/* class/size named types from Data routines */
	public static final byte[] Int8ArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (byte[])arrayFromData(data, byte.class, dims, bo); }
	public static final short[] Int16ArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (short[])arrayFromData(data, short.class, dims, bo); }
	public static final int[] Int32ArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (int[])arrayFromData(data, int.class, dims, bo); }
	public static final long[] Int64ArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (long[])arrayFromData(data, long.class, dims, bo); }
	public static final float[] Float32ArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (float[])arrayFromData(data, float.class, dims, bo); }
	public static final double[] Float64ArrayFromData(byte data[], int dims[], ByteOrder bo){ 
		return (double[])arrayFromData(data, double.class, dims, bo); }

	
	/** Transfer data directly into multidimensional array of dimensions dims and type componentType */  
	public static final Object arrayFromData(byte[] data, Class<?> componentType, int[] dims, ByteOrder bo){
		int rank = dims.length;
		int nEntries = 1;
		
		for(int i=0;i<rank;i++)
			nEntries *= dims[i];
		
		Object linearData = arrayFromData1D(data, componentType, bo);
		if( Array.getLength(linearData) != nEntries )
			System.err.println("WARNING: data size ("+data.length+"bytes) is not correct for specified dimensions"); 
					
		if(rank <= 1) //if 1D, return as is
			return linearData;
		else if(rank == 2){ //easy and common 2D case - quicker to do as is
			Object dest[] = (Object[])Array.newInstance(componentType, dims);
			for(int i=0; i<dims[0]; i++)
				System.arraycopy(linearData, i*dims[1], dest[i], 0, dims[1]);
			return dest;
		}else{ //nD case
			return unflatten(linearData, componentType, dims);
		}
	}
	
	/** Transfers data directly into 1D array of type componentType */	
	public static final Object arrayFromData1D(byte[] data, Class<?> componentType, ByteOrder bo){
		ByteBuffer inBuffer = ByteBuffer.wrap(data);
		inBuffer.order(bo);
		
		Object typedData; //  1D array of target type
				
		if(componentType == byte.class){		
			typedData = data; //that was silly 
		}else{			
			if(componentType == short.class){
				typedData = new short[data.length / 2];
				inBuffer.asShortBuffer().get((short[])typedData);				
			}else if(componentType == int.class){
				typedData = new int[data.length / 4];
				inBuffer.asIntBuffer().get((int[])typedData);
				
			}else if(componentType == long.class){
				typedData = new long[data.length / 8];
				inBuffer.asLongBuffer().get((long[])typedData);
				
			}else if(componentType == float.class){
				typedData = new float[data.length / 4];
				inBuffer.asFloatBuffer().get((float[])typedData);
				
			}else if(componentType == double.class){
				typedData = new double[data.length / 8];
				inBuffer.asDoubleBuffer().get((double[])typedData);
				
			}else
				throw new IllegalArgumentException("Unsupported type");
		}
		
		return typedData;		
	}
	
	public static final byte[] arrayToData1D(Object array, Class<?> componentType, ByteOrder bo){
		byte[] data;
		int arrayLength = Array.getLength(array);
		
		if(componentType == short.class){
			data = new byte[arrayLength * 2];
			ByteBuffer outBuffer = ByteBuffer.wrap(data);
			outBuffer.order(bo);
			outBuffer.asShortBuffer().put((short [])array);
		}else if(componentType == int.class){
			data = new byte[arrayLength * 4];
			ByteBuffer outBuffer = ByteBuffer.wrap(data);
			outBuffer.order(bo);
			outBuffer.asIntBuffer().put((int [])array);
		}else if(componentType == long.class){
			data = new byte[arrayLength * 8];
			ByteBuffer outBuffer = ByteBuffer.wrap(data);
			outBuffer.order(bo);
			outBuffer.asLongBuffer().put((long [])array);
		}else if(componentType == float.class){
			data = new byte[arrayLength * 4];
			ByteBuffer outBuffer = ByteBuffer.wrap(data);
			outBuffer.order(bo);
			outBuffer.asFloatBuffer().put((float [])array);
		}else if(componentType == double.class){
			data = new byte[arrayLength * 8];
			ByteBuffer outBuffer = ByteBuffer.wrap(data);
			outBuffer.order(bo);
			outBuffer.asDoubleBuffer().put((double [])array);
		}else
			throw new IllegalArgumentException("Unsupported type");
		
		return data;
	}

	/** Unflattens linearData into object of dims[] dimensions of type componentType */
	public static final Object unflatten(Object linearData, Class<?> componentType, int[] dims){
		Object dest = Array.newInstance(componentType, dims);
		
		int bytesDone = unflattenRecurseFill(dest, dims, linearData, 0, 0);
		if(bytesDone != Array.getLength(linearData))
			System.err.println("WARNING: Specifed dims give array that isn't the same total size as supplied 1D data");
				
		return dest;
	}
	
	/** Recurse to lowest levels to unflatten multidimensional array */
	private static final int unflattenRecurseFill(Object dest, int[] dims, Object src, int pos, int depth ){		
		if(depth == (dims.length-1)){ //if we're at max depth, copy the elements
			System.arraycopy(src, pos, dest, 0, dims[depth]);
			return pos + dims[depth];
		}else{
			//otherwise, cast as another array and copy each sub array
			Object objArr[] = (Object[])dest;
			for(int i=0; i<dims[depth]; i++)
				pos = unflattenRecurseFill(objArr[i], dims, src, pos, depth+1);
			return pos;
		}
		
	}
	
	public static final double[] intToDouble1D(int[] intArr) {
		double ret[] = new double[intArr.length];
		for(int i=0; i < intArr.length; i++)
			ret[i] = (double)intArr[i];
		
		return ret;
	}
	
	public static final int[] doubleToInt1D(double[] dblArr) {
		int ret[] = new int[dblArr.length];
		for(int i=0; i < dblArr.length; i++)
			ret[i] = (int)dblArr[i];
		
		return ret;
	}
	
	public static final double[] floatToDouble1D(float[] arr) {
		double ret[] = new double[arr.length];
		for(int i=0; i < arr.length; i++)
			ret[i] = (double)arr[i];
		
		return ret;
	}
	
	public static double[] doubleFlatten2DTo1D(double[][] in){
		int n = in.length;
		int m = in[0].length;
		double out[] = new double[n * m];
		for(int i=0; i < n; i++)
			System.arraycopy(in[i], 0, out, i*m, m);
		return out;
	}
	
	public static double[][] deepCopyDoubleArray(double[][] in) {
		double[][] ret = new double[in.length][];
		for(int i=0;i<ret.length;++i) {
			ret[i] = new double[in[i].length];
			System.arraycopy(in[i], 0, ret[i], 0, ret[i].length);
		}
		
		return ret;
	}
	
	public static double[][][] deepCopyDoubleArray(double[][][] in) {
		double[][][] ret = new double[in.length][][];
		for(int i=0;i<ret.length;++i) {
			ret[i] = new double[in[i].length][];
			for(int j=0;j<ret[i].length;++j) {
				ret[i][j] = new double[in[i][j].length];
				System.arraycopy(in[i][j], 0, ret[i][j], 0, ret[i][j].length);
			}
		}
		
		return ret;
	}
	

	/** Fills nD dest array from same size/shape src array, converting types. */
	public static final void convertMultiDimArray(Object src, Object dest){
		if (src instanceof Object[] && dest instanceof Object[]) {
			Object[] srcThisLevel = (Object[]) src;
			Object[] destThisLevel = (Object[]) dest;
			for (int i = 0; i < srcThisLevel.length; i++) {
				convertMultiDimArray(srcThisLevel[i], destThisLevel[i]);
			}
		} else {
			convert1DArray(src, dest);
		}
	}
	
	/** Fills 1D dest array from same size src array, converting types. */
	public static final Object convert1DArray(Object src, Object dest){
		Object srcUnboxed = OneLiners.unbox(src);
		if (srcUnboxed instanceof byte[]) {
			byte[] srcArr = (byte[]) srcUnboxed;
			if(dest instanceof byte[]) System.arraycopy(srcUnboxed, 0, dest, 0, srcArr.length);				
			else if(dest instanceof short[]) for(int i=0;i<srcArr.length;i++) ((short[])dest)[i] = (short)srcArr[i];
			else if(dest instanceof int[]) for(int i=0;i<srcArr.length;i++) ((int[])dest)[i] = (int)srcArr[i];
			else if(dest instanceof long[]) for(int i=0;i<srcArr.length;i++) ((long[])dest)[i] = (long)srcArr[i];
			else if(dest instanceof float[]) for(int i=0;i<srcArr.length;i++) ((float[])dest)[i] = (float)srcArr[i];
			else if(dest instanceof double[]) for(int i=0;i<srcArr.length;i++) ((double[])dest)[i] = (double)srcArr[i];
			else throw new IllegalArgumentException("Unrecognised dest type");
		} else if (srcUnboxed instanceof short[]) {
			short[] srcArr = (short[]) srcUnboxed;
			if(dest instanceof byte[]) for(int i=0;i<srcArr.length;i++) ((byte[])dest)[i] = (byte)srcArr[i];			
			else if(dest instanceof short[]) System.arraycopy(srcUnboxed, 0, dest, 0, srcArr.length);
			else if(dest instanceof int[]) for(int i=0;i<srcArr.length;i++) ((int[])dest)[i] = (int)srcArr[i];
			else if(dest instanceof long[]) for(int i=0;i<srcArr.length;i++) ((long[])dest)[i] = (long)srcArr[i];
			else if(dest instanceof float[]) for(int i=0;i<srcArr.length;i++) ((float[])dest)[i] = (float)srcArr[i];
			else if(dest instanceof double[]) for(int i=0;i<srcArr.length;i++) ((double[])dest)[i] = (double)srcArr[i];
			else throw new IllegalArgumentException("Unrecognised dest type");
		} else if (srcUnboxed instanceof int[]) {
			int[] srcArr = (int[]) srcUnboxed;
			if(dest instanceof byte[]) for(int i=0;i<srcArr.length;i++) ((byte[])dest)[i] = (byte)srcArr[i];				
			else if(dest instanceof short[]) for(int i=0;i<srcArr.length;i++) ((short[])dest)[i] = (short)srcArr[i];
			else if(dest instanceof int[]) System.arraycopy(srcUnboxed, 0, dest, 0, srcArr.length);
			else if(dest instanceof long[]) for(int i=0;i<srcArr.length;i++) ((long[])dest)[i] = (long)srcArr[i];
			else if(dest instanceof float[]) for(int i=0;i<srcArr.length;i++) ((float[])dest)[i] = (float)srcArr[i];
			else if(dest instanceof double[]) for(int i=0;i<srcArr.length;i++) ((double[])dest)[i] = (double)srcArr[i];
			else throw new IllegalArgumentException("Unrecognised dest type");
		} else if (srcUnboxed instanceof long[]) {
			long[] srcArr = (long[]) srcUnboxed;
			if(dest instanceof byte[]) for(int i=0;i<srcArr.length;i++) ((byte[])dest)[i] = (byte)srcArr[i];				
			else if(dest instanceof short[]) for(int i=0;i<srcArr.length;i++) ((short[])dest)[i] = (short)srcArr[i];
			else if(dest instanceof int[]) for(int i=0;i<srcArr.length;i++) ((int[])dest)[i] = (int)srcArr[i];
			else if(dest instanceof long[]) System.arraycopy(srcUnboxed, 0, dest, 0, srcArr.length);
			else if(dest instanceof float[]) for(int i=0;i<srcArr.length;i++) ((float[])dest)[i] = (float)srcArr[i];
			else if(dest instanceof double[]) for(int i=0;i<srcArr.length;i++) ((double[])dest)[i] = (double)srcArr[i];
			else throw new IllegalArgumentException("Unrecognised dest type");
		} else if (srcUnboxed instanceof float[]) {
			float[] srcArr = (float[]) srcUnboxed;
			if(dest instanceof byte[]) for(int i=0;i<srcArr.length;i++) ((byte[])dest)[i] = (byte)srcArr[i];				
			else if(dest instanceof short[]) for(int i=0;i<srcArr.length;i++) ((short[])dest)[i] = (short)srcArr[i];
			else if(dest instanceof int[]) for(int i=0;i<srcArr.length;i++) ((int[])dest)[i] = (int)srcArr[i];
			else if(dest instanceof long[]) for(int i=0;i<srcArr.length;i++) ((long[])dest)[i] = (long)srcArr[i];
			else if(dest instanceof float[]) System.arraycopy(srcUnboxed, 0, dest, 0, srcArr.length);
			else if(dest instanceof double[]) for(int i=0;i<srcArr.length;i++) ((double[])dest)[i] = (double)srcArr[i];
			else throw new IllegalArgumentException("Unrecognised dest type");
		} else if (srcUnboxed instanceof double[]) {
			double[] srcArr = (double[]) srcUnboxed;
			if(dest instanceof byte[]) for(int i=0;i<srcArr.length;i++) ((byte[])dest)[i] = (byte)srcArr[i];				
			else if(dest instanceof short[]) for(int i=0;i<srcArr.length;i++) ((short[])dest)[i] = (short)srcArr[i];
			else if(dest instanceof int[]) for(int i=0;i<srcArr.length;i++) ((int[])dest)[i] = (int)srcArr[i];
			else if(dest instanceof long[]) for(int i=0;i<srcArr.length;i++) ((long[])dest)[i] = (long)srcArr[i];
			else if(dest instanceof float[]) for(int i=0;i<srcArr.length;i++) ((float[])dest)[i] = (float)srcArr[i];
			else if(dest instanceof double[]) System.arraycopy(srcUnboxed, 0, dest, 0, srcArr.length);
			else throw new IllegalArgumentException("Unrecognised dest type");
		} else {
			throw new IllegalArgumentException("Unrecognised src type: " + srcUnboxed.getClass() + ", target type: " + dest.getClass());
		}

		return dest;
	}


	/**
	 * Recursively flatten nD matrix array of strings into byte array All
	 * strings are null padded to strLen length
	 * **/
	public static int flattenAndNullPadStrings(byte buff[], int idx,
			Object strs, int maxLen) {

		if (strs instanceof String) {
			String str = (String) strs;
			int strLen = str.length();

			// copy the array
			System.arraycopy(str.getBytes(), 0, buff, idx, strLen);
			idx += str.length();

			// padd the remaining
			int toPad = maxLen - strLen;
			if (toPad < 0)
				throw new IllegalArgumentException("String '" + str
						+ "' too long (> " + strLen + ")");

			for (int i = 0; i < toPad; i++)
				buff[idx + i] = 0;

			return idx + toPad;

		} else if (strs instanceof Object[]) {
			Object[] strArray = (Object[]) strs;

			// Add each of the next level of the array, updating idx each time
			for (int i = 0; i < strArray.length; i++)
				idx = flattenAndNullPadStrings(buff, idx, strArray[i], maxLen);

			return idx; // return the new position
		} else
			throw new IllegalArgumentException(
					"Something is not a string, array of strings or generally stringy");
	}

	/**
	 * Recursively unflatten nD matrix array of strings into object All strings
	 * are null padded to strLen length
	 * **/
	public static int unflattenStrings(byte[] buff, int idx, Object strs,
			int maxLen) {

		if (strs instanceof String[]) { // Lowest level

			String[] strArray = (String[]) strs;

			// cycle through the lowst level creating the actual string objects
			for (int i = 0; i < strArray.length; i++) {
				int strLen;
				for (strLen = 0; strLen < maxLen; strLen++)
					if (buff[idx + strLen] == 0)
						break;
				strArray[i] = new String(buff, idx, strLen);
				idx += maxLen;

			}
			return idx;

		} else if (strs instanceof Object[]) {
			Object[] strArray = (Object[]) strs;

			// Add each of the next level of the array, updating idx each time
			for (int i = 0; i < strArray.length; i++)
				idx = unflattenStrings(buff, idx, strArray[i], maxLen);

			return idx; // return the new position
		} else
			throw new IllegalArgumentException(
					"Something is not a string, array of strings or generally stringy");

	}

	/** Recursively finds max string length in nD matrix array of strings */
	public static int getMaxStringLength(Object strs) {
		int len, maxLen = 0;
		Object[] strArray;

		if (strs instanceof String)
			return ((String) strs).length();
		else if (strs instanceof Object[]) {
			strArray = (Object[]) strs;

			for (int i = 0; i < strArray.length; i++) {
				len = getMaxStringLength(strArray[i]);
				if (len > maxLen)
					maxLen = len;
			}

			return maxLen;
		} else
			throw new IllegalArgumentException("Not a string");
	}

	/** Unflattens linearData into object of dims[] dimensions of type componentType, from a buffer */
	public static final Object unflatten(Buffer linearData, Class<?> componentType, int dims[]){
		Object dest = Array.newInstance(componentType, dims);
		
		int bytesDone = unflattenRecurseFill(dest, dims, linearData, 0, 0);		
		if(linearData.remaining() > 0)
			throw new RuntimeException("Specifed dims give array that isn't the same total size as supplied 1D data. " +
											"BufferLen = " + linearData.capacity() + ", bytesDone = " + bytesDone);
				
		return dest;
	}
	
	/** Recurse to lowest levels to unflatten multidimensional array */
	private static final int unflattenRecurseFill(Object dest, int dims[], Buffer src, int pos, int depth ){		
		if(depth == (dims.length-1)){ //if we're at max depth, copy the elements
			if(src instanceof FloatBuffer){
				((FloatBuffer)src).get((float[])dest);
			}else if(src instanceof IntBuffer){
				((IntBuffer)src).get((int[])dest);
			}else if(src instanceof DoubleBuffer){
				((DoubleBuffer)src).get((double[])dest);
			}else if(src instanceof ShortBuffer){
				((ShortBuffer)src).get((short[])dest);
			}else if(src instanceof LongBuffer){
				((LongBuffer)src).get((long[])dest);
			}else if(src instanceof CharBuffer){
				((CharBuffer)src).get((char[])dest);
			}else if(src instanceof ByteBuffer){ //they will probably all be this in the end
				((ByteBuffer)src).get((byte[])dest);
			}else
				throw new IllegalArgumentException("Unrecognised src buffer type");
			//System.arraycopy(src, pos, dest, 0, dims[depth]);
			return pos + dims[depth];
		}else{
			//otherwise, cast as another array and copy each sub array
			Object objArr[] = (Object[])dest;
			for(int i=0; i<dims[depth]; i++)
				pos = unflattenRecurseFill(objArr[i], dims, src, pos, depth+1);
			return pos;
		}
	}
}
