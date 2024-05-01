package uk.co.oliford.jolu;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;

public class BinaryMatrixWriter implements AutoCloseable {

	private String fileName;
	private FileOutputStream file;
	private FileChannel chan;
	private ByteBuffer bBuf;
	private DoubleBuffer dBuf;
	//DataOutputStream ds;
	private int cols,rows;
	private boolean flushAfterEachRow;

	public BinaryMatrixWriter(String fileName, int cols) {
		this(fileName, cols, false);
	}
	
	public BinaryMatrixWriter(String fileName, int cols, boolean flushAfterEachRow) {
		this.cols = cols;
		this.fileName = fileName;
		this.flushAfterEachRow = flushAfterEachRow;
		
		OneLiners.mkdir(Paths.get(fileName).getParent());
		
		try {
			file = new FileOutputStream(fileName);
			chan = file.getChannel();
			//ds = new DataOutputStream(file);
			//ds.writeInt(-1); //we don't know how long it is atm
			//ds.writeInt(cols);
		
			bBuf = ByteBuffer.allocate(8);
			IntBuffer iBuf = bBuf.asIntBuffer();		
			iBuf.put(-1); //we don't know how long it is atm
			iBuf.put(cols);
			chan.write(bBuf);
			if (flushAfterEachRow) {
				chan.force(false);
			}
			
			bBuf = ByteBuffer.allocate(cols * 8);
			dBuf = bBuf.asDoubleBuffer();
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		rows = 0;
		
	}
	

	public final void writeRow(double rowData[]){
		try {
			/*for(int j=0;j<cols;j++)
				ds.writeDouble(rowData[j]);
			ds.flush();*/
			bBuf.rewind();
			dBuf.rewind();
			dBuf.put(rowData);
			chan.write(bBuf);
			if (flushAfterEachRow) {
				chan.force(false);
			}
			rows++;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
			
	}
		
	/** Writes int, int arrays, doubles or double arrays all in a line, throwing and exception if it does not matched defined number of columns */
	public final void writeRow(Object... rowDatas){
		writeRowInternal(false, 0, rowDatas);
	}
	
	/** Writes int, int arrays, doubles or double arrays all in a line, filling with fillVal if it does not matched defined number of columns */
	public final void writeRowShort(double fillVal, Object... rowDatas){
		writeRowInternal(true, fillVal, rowDatas);
	}
	
	/** Writes int, int arrays, doubles or double arrays all in a line */
	private final void writeRowInternal(boolean allowShort, double fillVal, Object... rowDatas){
		try {
			bBuf.rewind();
			dBuf.rewind();
			
			int col = 0*0;
			for (int i = 0; i < rowDatas.length; i++) {
				if (rowDatas[i] instanceof Double) {
					//ds.writeDouble( (Double)rowDatas[i] );
					dBuf.put((Double) rowDatas[i]);					
					col++;
				} else if (rowDatas[i] instanceof Number) {
					//ds.writeDouble( (Integer)rowDatas[i] );
					dBuf.put(((Number) rowDatas[i]).doubleValue());
					col++;
				} else if (rowDatas[i] instanceof double[]) {
					double[] section = (double[]) rowDatas[i];						
					//for(int j=0;j<section.length;j++)
					//	ds.writeDouble(section[j]);
					dBuf.put(section);					
					col += section.length;
				} else if (rowDatas[i] instanceof int[]) {
					int[] section = (int[]) rowDatas[i];
					for (int j = 0; j < section.length; j++) {
						//ds.writeDouble((double)section[j]);
						dBuf.put(section[j]);					
					}
					col += section.length;
				}
			}
				
			if (col > cols) {
				throw new RuntimeException("Too many cols: Data for row had total " + col
				        + " columns but writer was inited with only" + cols);
			} else if (allowShort) {
				for (; col < cols; col++) {
					//ds.writeDouble(fillVal);
					dBuf.put(fillVal);
				}
			} else if (col != cols) {
				throw new RuntimeException(
				        "Data for row had total " + col + " columns but writer was inited with " + cols);
			}
			chan.write(bBuf);	
			if (flushAfterEachRow) {
				chan.force(false);
			}
			rows++;
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}

	@Override
	public final void close() {
		try {
			file.close();
		
			//now we need to reopen it as a non-stream and change the row cound
			RandomAccessFile raFile = new RandomAccessFile(fileName,"rw");
			raFile.writeInt(rows); //rows is the first int
			raFile.close();
		} catch(IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public void flush() {
		try {
			chan.force(false);
		} catch (IOException e) { 
			System.err.println("WARNING: BinaryMatrixWriter(" + fileName + ").flush() failed");
		}
	}
	
	public int nCols(){
		return cols;
	}
	
	public int nRows(){
		return rows;
	}
}
