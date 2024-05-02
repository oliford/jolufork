package uk.co.oliford.jolu;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Vector;

/** Very basic script parsing support
 * Opens a text file and splits each line by space, tabs and commas
 * 
 * The first element is taken as the name of the method to call in the given class
 * 
 * Methods can have a parameters of ints, doubles or Strings (no arrays) and the 
 * string will be converted to these.
 * 
 * Multiple methods can be used with different numbers of parameters (but not different types with the same number)
 * 
 * Alternatively, a single method taking a single 1D String array can be called.
 * 
 */
public class SimpleScriptParser {
	public static void parse(Object handler, String fileName){ parse(handler, fileName, ""); }
	
	public static void parse(Object handler, String fileName, String methodPrefix){
		parse(handler, fileName, methodPrefix, false);
	}
	
	public static void parse(Object handler, String fileName, String methodPrefix, boolean ignoreUnknown){
		Class<?> cls = handler.getClass();
		Method[] methods = cls.getMethods();
		
		
		Vector<String> lines = new Vector<String>();
		FileInputStream file;
		try{
			String line = "";
					
			file = new FileInputStream(fileName);			
			BufferedReader reader = new BufferedReader( new InputStreamReader(file));
			
			line = reader.readLine();
			do{
				lines.add(line);
				line = reader.readLine();
			}while(line!=null);
		
		}catch(IOException e){ throw new RuntimeException(e); }
	
		String line = "";
		int lineNo = -1;
		
		
		try{
			String parts[];
			String skipUntil = null; //looping support, currently unused
			for(lineNo=0;lineNo < lines.size();lineNo++){
				line = (String)lines.elementAt(lineNo);
				
				line=line.trim();
				parts = line.split("//"); //get rid of comment
				if(parts.length == 0)continue; //comment only
				parts = parts[0].split("[\\t\\s,]+"); //split by spaces or tabs or commas
				
				if(parts[0].length() == 0)continue; //blank line
				
				if(skipUntil != null){
					if(parts[0].equalsIgnoreCase(skipUntil))
						skipUntil = null;
					else
						continue;					
				}
				
				
				//Find a method in the handler class with the same name as the first element, and the same number of params as we have
				Method lineMethod = null;
				boolean foundNameMatch = false;
				for(Method m : methods){
					//make it case insensitive to be easier. Anyone with multiple methods of the same name with different 
					// cases needs beating over the head anyway.
					if(m.getName().equalsIgnoreCase(methodPrefix + parts[0])){
						foundNameMatch = true;
						Class<?>[] paramTypes = m.getParameterTypes();
						
						//need to match number of parameters, or have a method that takes an array, or single string
						if( (paramTypes.length == 1 && 
								(paramTypes[0] == String.class 
										|| paramTypes[0] == String[].class
										|| paramTypes[0] == int[].class
										|| paramTypes[0] == double[].class
										|| paramTypes[0] == boolean[].class)) ||
								(parts.length - 1) == paramTypes.length){
							lineMethod = m;
							break;
						}
					}
				}
				
				if(lineMethod == null){
					if(ignoreUnknown){
						if(foundNameMatch){
							System.err.println("WARNING: SimpleScriptParser: Found a method with matching name for command '"+
													parts[0]+"', but none with the correct parameters.");
						}
						continue;
					}else
						throw new SimpleScriptException("Cannot find a useable method for '"+parts[0]+"'.");
				}
				
				//build the parameters list by converting any ints/doubles
				Class<?>[] paramTypes = lineMethod.getParameterTypes();
				Object params[] = new Object[paramTypes.length];
								
				if(paramTypes.length == 1 && paramTypes[0] == String.class){
					String twoParts[] = line.split("[\\t\\s,]+",2); //split by spaces or tabs or commas
					params[0] = twoParts[1];
					
				}else if(paramTypes.length == 1 && paramTypes[0] == String[].class){
					String s[] = new String[parts.length - 1];
					for(int i=0; i < (parts.length - 1); i++)
						s[i] = parts[i+1];
					params[0] = s;
					
				}else if(paramTypes.length == 1 && paramTypes[0] == int[].class){
					int pi[] = new int[parts.length - 1];
					for(int i=0; i < (parts.length - 1); i++)
						pi[i] = Integer.parseInt(parts[i+1]);
					params[0] = pi;
					
				}else if(paramTypes.length == 1 && paramTypes[0] == double[].class){
					double pd[] = new double[parts.length - 1];
					for(int i=0; i < (parts.length - 1); i++)
						pd[i] = parseDoubleWithINF(parts[i+1]);
					params[0] = pd;
					
				}else if(paramTypes.length == 1 && paramTypes[0] == boolean[].class){
					boolean pb[] = new boolean[parts.length - 1];
					for(int i=0; i < (parts.length - 1); i++)
						pb[i] = Boolean.parseBoolean(parts[i+1]);
					params[0] = pb;
					
				}else{
					
					for(int i=0; i < paramTypes.length; i++){
						if(paramTypes[i].isArray())
							throw new SimpleScriptException("Script methods can only have Strings, ints, bools or doubles or take a single String[].");
						else if(paramTypes[i] == String.class)
							params[i] = parts[i+1];
						else if(paramTypes[i] == int.class)
							params[i] = Integer.parseInt(parts[i+1]);
						else if(paramTypes[i] == double.class){
							params[i] = parseDoubleWithINF(parts[i+1]);
						}else if(paramTypes[i] == boolean.class)
							params[i] = Boolean.parseBoolean(parts[i+1]);
						else
							throw new SimpleScriptException("Script methods can only have Strings, ints, bools or doubles or take a single String[].");
					}
				}
				
				try {
					lineMethod.invoke(handler, params);
				} catch (IllegalAccessException e) { throw new RuntimeException(e); }
				catch (InvocationTargetException e) {	throw new RuntimeException(e); }

				
			}			
		}catch(RuntimeException e){
			System.err.println("ERROR: Exception during processing of line "+lineNo+" in '"+fileName+"'\nLine: '"+line+"'\n");
			System.err.println("ERROR: Exception thrown was:");
			e.printStackTrace();
			return;
		}
	}
	
	public static double parseDoubleWithINF(String str) {
		if(str.equalsIgnoreCase("INFINITY"))
			return Double.POSITIVE_INFINITY;
		else
			return Double.parseDouble(str);
	}
			
}
