package uk.co.oliford.svg;

import java.io.*;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

/** VERY basic svg writer - only does lines */
public class SVGLineWriter {
	
	private PrintStream svgStream = null;
	private DecimalFormat fmt;
	
	private boolean drawingStarted = false;	
	private StringBuffer styleStr = new StringBuffer(1024);
	protected double[] bbox;
	
	/**
	 * @param fileName SVG file to write to
	 * @param bbox double[]{ x0, y0, x1, y1 }
	 */
	public SVGLineWriter(String fileName, double bbox[]){
	    this(fileName);
	    this.bbox = bbox;
	}
	
	public void setPrecision(int precision) {
        DecimalFormatSymbols explicitSymbols = new DecimalFormatSymbols();
        
        explicitSymbols.setZeroDigit('0');
        explicitSymbols.setExponentSeparator("E");
        explicitSymbols.setGroupingSeparator(',');
        explicitSymbols.setMinusSign('-');
        explicitSymbols.setDecimalSeparator('.');
        String fmtStr = "#.";
        for(int i=0; i < precision; i++)
            fmtStr += "#";
        fmt = new DecimalFormat(fmtStr, explicitSymbols);
        
    }
	
	public SVGLineWriter(String fileName) {
	    setPrecision(5);
       
		FileOutputStream file;
		try {
		    //OneLiners.makePath(fileName); <-- we don't have OneLiners in algorithm repo
			file = new FileOutputStream(fileName);
			svgStream = new PrintStream(file);
			
			svgStream.println("<?xml version=\"1.0\" encoding=\"iso-8859-1\"?>\n" +
			"<!DOCTYPE svg PUBLIC \"-//W3C//DTD SVG 03December 1999//EN\" \"http://www.w3.org/Graphics/SVG/SVG-19991203.dtd\">");
	       	         
		} catch (FileNotFoundException e) {
            System.err.println("Unable to open SVG file '"+fileName+"': " + e.getMessage());
            svgStream = null;
        }
	}
	
	public void startDrawing(){
	    if(drawingStarted)
            throw new RuntimeException("Already started drawing");
            
        double width = bbox[2] - bbox[0];
        double height = bbox[3] - bbox[1];


        addLineStyle("deflt", "none", (width + height) / 2000, "black");
        
        svgStream.println("<svg xml:space=\"preserve\" width=\"100cm\" height=\""+(100*height/width)+"cm\" " +
//        "width=\""+width+"px\" height=\""+height+"px\"" +
        " viewBox=\""+bbox[0]+" "+bbox[1]+" "+width+" "+height+"\" "+
        ">\n" + 
        "<style type=\"text/css\">\n" + styleStr + "</style>" + 
        "\n\n");
        
        //this is essentially a hack to get to display the right way up while maintaining
        // the coordinates as proper coordinates        
        svgStream.println("<!--transform coordinate up to page up--><g transform=\"scale(1,-1)\"><g transform=\"translate(0,"+(-bbox[1]-bbox[3])+")\">");
        //svgStream.println("<g><g>");
        drawingStarted = true;
	}
		
	public void addLineStyle(String name, String fill, double strokeWidth, String strokeColour){
	        if(drawingStarted)
	            throw new RuntimeException("Already started drawing");
	        	        
	        styleStr.append(" ." + name + 
	                        " {fill:" + fill + 
	                        ";stroke:" + strokeColour + 
                            ";stroke-width:"+fmt.format(strokeWidth) +
	                        ";}\n");
	}
	
	public void destroy(){
		if(svgStream==null)return;
		
		svgStream.println("\n\n</g></g></svg>");		
		svgStream.close();
		svgStream=null;
	}

	public void addLine(double x[], double y[]){
	    addLine(x, y, "deflt");
	}
	    
    public void addLine(double x[], double y[], String style){
        if(svgStream==null)return;
        if(!drawingStarted) startDrawing();
                
        svgStream.print("<path class=\""+style+"\" d=\"");
        if(x.length > 0)
            svgStream.print("M " + fmt.format(x[0]) + " " + fmt.format(y[0]) + " ");
        for(int i=1;i<x.length;i++)
            svgStream.print("L  " + fmt.format(x[i]) + " " + fmt.format(y[i]) + " ");
        svgStream.println("\" />");
    }
    
    /** One-Line writes an SVG files from specified array 
     * 
     * @param lines array of line coords [lineIdx][{x/y}][pointIdx]
     */
    public static void arrayToSVG(String fileName, double lines[][][]) {
        double bbox[] = new double[]{ 
            Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY,
            Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY
        };
        
        /* Find the min/max in both directions */
        for(int i=0; i < lines.length; i++)
            for(int j=0; j < lines[i][0].length; j++){
                if( lines[i][0][j] < bbox[0] ) bbox[0] = lines[i][0][j];
                if( lines[i][1][j] < bbox[1] ) bbox[1] = lines[i][1][j];
                if( lines[i][0][j] > bbox[2] ) bbox[2] = lines[i][0][j];
                if( lines[i][1][j] > bbox[3] ) bbox[3] = lines[i][1][j];
            }
        
        SVGLineWriter svg = new SVGLineWriter(fileName, bbox);
                
        for(int i=0; i < lines.length; i++)
            svg.addLine(lines[i][0], lines[i][1]);
        
        svg.destroy();
        
    }

    public static void main(String[] args) {
        SVGLineWriter svg = new SVGLineWriter("/tmp/lines.svg", new double[]{ 0, 0, 40, 40 } );
        
        svg.addLine(new double[]{ 1, 2, 3, 1} , new double[]{ 1, 2, 1.5, 3});
        
        svg.destroy();
        
    }

    public void startGroup(String name) {
        if(!drawingStarted) startDrawing();
        svgStream.println("<g id=\"" + name + "\">"); 
    }
    
    public void endGroup() { svgStream.println("</g>"); }

    public void addCircle(double x, double y, double r, String style) {
        if(!drawingStarted) startDrawing();
        svgStream.println("<circle class=\""+style+"\" cx=\"" + fmt.format(x) + "\" cy=\"" + fmt.format(y) + 
                                "\" r=\"" + fmt.format(r) + "\"/>");
    }

    public void addCircles(double x[], double y[], double r, String style) {
        for(int i=0; i < x.length; i++)
            addCircle(x[i], y[i], r, style);
    }

}
