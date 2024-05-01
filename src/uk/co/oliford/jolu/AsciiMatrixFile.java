package uk.co.oliford.jolu;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;

/** One-Line reading and writing of 2D matrix of values to/from an ascii file.
 * 
 * For reading and writing matricies into ascii files.
 * For really large things this is slow, use BinaryMatrxFile or BinaryMatrixWriter instead
 * 
 * TODO: Write this as an extrenal library or something more efficient than java string processing!
 * 
 */
public class AsciiMatrixFile{
	
	public static double[][] mustLoad(InputStream in) {
		return mustLoad(new InputStreamReader(in), false);
	}
	
	
	public static double[][] mustLoad(String fileName) {
		return mustLoad(fileName, false);
	}
	
	
	public static double[][] mustLoad(Reader reader) {
		return mustLoad(reader, false);
	}
	
	
	public static double[][] mustLoad(InputStream in, boolean swapDims) {
		return mustLoad(new InputStreamReader(in), swapDims);
	}
	
	
	public static double[][] mustLoad(Reader reader, boolean swapDims) {
		try {
			return load(reader, swapDims);
		} catch (IOException e) {
			throw new UncheckedIOException("AsciiMatrixFile.mustLoad() caught the following error from load():", e);
		}	
	}
	
	/**
	 * mustLoad - simply calls load() but catches any IOException thrown. It simple re-throws it as a RuntimeException
	 * Try not to use this for final programs but it saves a lot of error catching in the meantime.
	 * @param fileName The file to read
	 * @param swapDims True to load into arrays as [column][row], false for [row][column]
	 * @returns 2D array of the data in the file
	 * @throws IOException 
	 */	
	public static double[][] mustLoad(String fileName, boolean swapDims) {
		try{
			return load(fileName, swapDims);
		} catch(IOException e){
			throw new UncheckedIOException("AsciiMatrixFile.mustLoad() caught the following error from load():", e);
		}		
	}
	
	/**
	 * load - Loads a file, attempts to interpret it as a fixed size 2D matrix of doubles with cols seperated by \t and rows by \n 
	 * @param in The file to read
	 * @returns 2D array of the data in the file
	 * @throws IOException 
	 */
	public static double[][] load(InputStream in) throws IOException {
		return load(new InputStreamReader(in), false);
	}
	
	
	public static double[][] load(String fileName) throws IOException {
		return load(fileName, false);
	}
	
	
	public static double[][] load(Reader reader) throws IOException {
		return load(reader, false);
	}
	
	
	public static double[][] load(InputStream in, boolean swapDims) throws IOException {
		return load(new InputStreamReader(in), swapDims);
	}
	
	/**
	 * load - Loads a file, attempts to interpret it as a fixed size 2D matrix of doubles with cols seperated by \t and rows by \n 
	 * @param fileName The file to read
	 * @param swapDims True to load into arrays as [column][row], flase for [row][column]
	 * @returns 2D array of the data in the file
	 * @throws IOException 
	 */
	public static double[][] load(String fileName, boolean swapDims) throws IOException {
		//open the file
		FileInputStream file = new FileInputStream(fileName);		
		Reader reader = new InputStreamReader(file);
		
		return loadUsingArrayList(reader, swapDims, fileName);
	}
	
	/**
	 * load - Loads data from a Reader: attempts to interpret it as a fixed size 2D matrix of doubles with cols seperated by \t and rows by \n 
	 * @param reader A reader from which to read the ascii data
	 * @param swapDims True to load into arrays as [column][row], flase for [row][column]
	 * @returns 2D array of the data in the file
	 * @throws IOException 
	 */
	public static double[][] load(Reader reader, boolean swapDims) throws IOException {
		return loadUsingArrayList(reader, swapDims, "resource");
	}
	
	/** Load using ArrayList, written by Jakob and probably much better than my one (loadBlockBuffered) <oliford> */
	private static double[][] loadUsingArrayList(Reader r, boolean swapDims, String sourceIdentifier) throws IOException {
		ArrayList<double[]> rows = new ArrayList<>(1024);
		BufferedReader reader = new BufferedReader(r);
		double[] row = 	getLine(reader);
		if(row == null) throw new RuntimeException("There is no data in '"+sourceIdentifier+"'.");
		int numCols = row.length;
		rows.add(row);		
		while( (row = getLine(reader)) != null ) {			
			if(row.length != numCols)
				throw new RuntimeException("Line " + (rows.size()+1) + " of file '" + sourceIdentifier + "' has " + row.length + " columns but the 1st line had " + numCols);
			
			rows.add(row);
		}
		reader.close();
		
		double[][] ret = new double[rows.size()][];
		for(int i=0;i<rows.size();++i) {
			ret[i] = rows.get(i);
		}
		
		if (swapDims) ret = OneLiners.transpose(ret);
		
		return ret;		
	}
	
	/**
	 * getLine - reads a line from 'reader' and attempts to get an array of at least 1 double value
	 * the function also ignores any line starting with //
	 * 
	 *  @param reader - BufferedReader object to read from
	 *  @throws IOException
	 *  @returns The array of doubles
	 */
	private static double[] getLine(BufferedReader reader) throws IOException{
		String line,fields[];
		double[] vals;
		int i,n;
		  
		do{
			do{
				line = reader.readLine();				
				if(line == null)break;
				line = line.trim();
			}
			while(line.length() == 0 || (line.charAt(0) == '/' && line.charAt(1) == '/'));
				  
			if(line == null)
				return null;
			
			line = line.replaceAll("\\s+", "\t");
				  
			fields = line.split("\t");
			n = fields.length;
			
			/*if(n==1){
				fields2 = line.split("\\s+");
				if(fields2.length > 1){
					fields = fields2;
					n = fields2.length;
				}
			}*/
			
			vals = new double[n];
				  
			try{
				for(i=0;i<n;i++){
					vals[i] = Double.parseDouble(fields[i]);
				}
				break;
			}catch(NumberFormatException nfe){
				
			}
			
		}while(true);
		
		return vals;
	}
	
	
	public static void mustWrite(String fileName, double data[]){ mustWrite(fileName,data,null,true); }
	public static void mustWrite(String fileName, double data[], String initialComments){ mustWrite(fileName,new double[][]{ data },initialComments,true); }
	public static void mustWrite(String fileName, double data[], boolean swapDims){ mustWrite(fileName,data,null,swapDims); }
	public static void mustWrite(String fileName, double data[], String initialComments, boolean swapDims){ mustWrite(fileName,new double[][]{ data },initialComments,swapDims); }
	public static void mustWrite(String fileName, double data[][]){ mustWrite(fileName,data,null,false); }
	public static void mustWrite(String fileName, double data[][], boolean swapDims){ mustWrite(fileName,data,null,swapDims); }
	public static void mustWrite(String fileName, double data[][], String initialComments){ mustWrite(fileName,data,initialComments,false); }
	
	
	public static void mustWrite(String fileName, double data[][], String initialComments, boolean swapDims){
		try{
			OneLiners.mkdir(Paths.get(fileName).getParent());
			write(fileName, data, initialComments, swapDims);
		}catch(IOException e){
 			throw new UncheckedIOException("AsciiMatrixFile.mustWrite() caught the following error from write():\n", e);
		}		
	}
	
	
	public static void write(String fileName, double data[][]) throws IOException{ write(fileName,data,null); }
	public static void write(String fileName, double data[][], String initialComments) throws IOException { write(fileName,data,initialComments,false); }
	
	
	public static void write(String fileName, double data[][], String initialComments, boolean swapDims) throws IOException{
		FileOutputStream file = new FileOutputStream(fileName);
		PrintStream ps = new PrintStream(file);
		
		if(initialComments != null)
			ps.println(initialComments);
		
		int i,j;
		int ni = data.length;
		if (ni<1) {
			ps.close();
			throw new RuntimeException("Nothing to put into '"+fileName+"', data has 0 rows!");
		}
		int nj = data[0].length;
		
		if(ni > 0 && nj > 0){ //if we have something to write (no dim is size 0)
			if(swapDims){
				for(j=0;j<nj;j++){			
					for(i=0;i<(ni-1);i++){
						ps.print(data[i][j] + "\t");
					}
					
					ps.println(data[i][j]);
				}			
			}
			else{
				for(i=0;i<ni;i++){
					for(j=0;j<(nj-1);j++){			
						ps.print(data[i][j] + "\t");
					}
						ps.println(data[i][j]);
				}
			}
		}
		file.close();
	}
}
