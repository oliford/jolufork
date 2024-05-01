package uk.co.oliford.jolu;

import java.io.*;

public class BinaryMatrixReader implements AutoCloseable {

	private String fileName;
	private FileInputStream file;
	private DataInputStream ds;
	private int cols,nRows;
	private int currentRow;

	public BinaryMatrixReader(String fileName) {
		this.fileName = fileName;

		try {
			file = new FileInputStream(fileName);
			ds = new DataInputStream(file);

			nRows = ds.readInt();
			cols = ds.readInt();

			if (nRows < 0) {
				int nBytes = ds.available();
				nRows = (nBytes / (cols * 8));
				System.err.println("WARNING: Invalid/unknown row count in " + fileName + ", using " + nRows
						+ " based on file size.");
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}

		currentRow = 0;
	}
	
	public final double[] mustReadRow() {
		try {
			return readRow();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public final void skipRows(long nRows) {
		long bytesToSkip = nRows * cols * 8;
		try {
			// Note that skip has an unfortunate specification: it may skip *less* than the
			// given number of bytes, hence the extra method
			skipFully(file, bytesToSkip);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	private static final void skipFully(InputStream in, long nBytes) throws IOException {
		long remaining = nBytes;
		while (remaining != 0) {
			long skipped = in.skip(remaining);
			if (skipped == 0) {
				throw new EOFException();
			}
			remaining -= skipped;
		}
	}
	
	public final double[] readRow() throws IOException {
		double[] row = new double[cols];
		for (int j = 0; j < cols; j++) {
			row[j] = ds.readDouble();
		}
		currentRow++;
		return row;
	}
		
	/**
	 * Writes int, int arrays, doubles or double arrays all in a line, throwing and
	 * exception if it does not matched defined number of columns
	 * 
	 * @deprecated That method seems completely useless and even dangerous. You
	 *             probably don't want to use it, and if you want to use it, you
	 *             should first go through that thingie and make some sense out of
	 *             it
	 */
	@Deprecated
	public final void readRow(Object... rowDatas){
		readRowInternal(false, rowDatas);
	}
	
	/**
	 * Writes int, int arrays, doubles or double arrays all in a line, filling with
	 * fillVal if it does not matched defined number of columns
	 * 
	 * @deprecated That method seems completely useless and even dangerous. You
	 *             probably don't want to use it, and if you want to use it, you
	 *             should first go through that thingie and make some sense out of
	 *             it
	 */
	@Deprecated
	public final void readRowShort(Object... rowDatas){
		readRowInternal(true, rowDatas);
	}
	
	/**
	 * Writes int, int arrays, doubles or double arrays all in a line
	 * 
	 * @deprecated That method seems completely useless and even dangerous. You
	 *             probably don't want to use it, and if you want to use it, you
	 *             should first go through that thingie and make some sense out of
	 *             it
	 */
	@Deprecated
	private final void readRowInternal(boolean allowShort, Object... rowDatas){
		try{
//			int col = 0;
			for (int i = 0; i < rowDatas.length; i++) {
				if (rowDatas[i] instanceof double[]) {
					double[] section = (double[]) rowDatas[i];						
					for (int j = 0; j < section.length; j++) {
						section[j] = ds.readDouble();
					}
//					col += section.length;
				} else if (rowDatas[i] instanceof int[]) {
					int[] section = (int[]) rowDatas[i];
					for (int j = 0; j < section.length; j++) {
						section[j] = (int) ds.readDouble();
					}
//					col += section.length;
				}
			}
				
			/*
			if (col > cols) {
				throw new RuntimeException("Too many cols: Data for row had total " + col +" columns but writer was inited with only" + cols );
			} else if (!allowShort && (col != cols))
				throw new RuntimeException("Data for row had total " + col +" columns but writer was inited with " + cols );
			*/
			currentRow++;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}	
	}

	@Override
	public final void close() {
		try {
			file.close();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public int nCols() {
		return cols;
	}
	
	public int nRows() {
		return nRows;
	}

	public String fileName() {
		return fileName;
	}

	public int currentRow() {
		return currentRow;
	}
}
