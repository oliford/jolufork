package uk.co.oliford.jolu;



public abstract class ColorMaps {
    public static double[][] jet(int nStarts) {
        double col[][] = new double[nStarts][];
        double cx[] = new double[]{ 0, 0.125,  0.375,    0.625,    0.875, 1.0 };        
        double iRed[] = new double[]{ 0.0, 0.0, 0.0, 1.0, 1.0, 0.5 };
        double iGrn[] = new double[]{ 0.0, 0.0, 1.0, 1.0, 0.0, 0.0 };
        double iBlu[] = new double[]{ 0.5, 1.0, 1.0, 0.0, 0.0, 0.0 };
        for(int i=0; i<nStarts; i++){
            double x = (nStarts == 1) ? 0.5 : (double)i/(nStarts-1.0);
            int j = OneLiners.getNearestLowerIndex(cx, x);
            double f;
            if(j >= (cx.length-1)){
            	j = cx.length - 2; 
            	f = 1;
            }else{
            	f = (x - cx[j]) / (cx[j+1] - cx[j]);
            }
            col[i] = new double[]{ 
            		(1-f) * iRed[j] + f * iRed[j+1],
            		(1-f) * iGrn[j] + f * iGrn[j+1],
            		(1-f) * iBlu[j] + f * iBlu[j+1],
            	};
        }
        return col;
    }
    
    /** Create a colour map for a 2D grid of points, in repeating 2x2 cells of:<BR/>
     *  [ Yel, Red ]<BR/>
     *  [ Blu, Grn ]<BR/>
     *  
     * whe color map is indexed as col[x*nY+y]
     * 
     * @param nX
     * @param nY
     * @return
     */
    public static double[][] alternating2D2x2(int nX, int nY) {
        double quad[][][] = {
                { { 0.7, 0.7, 0 },  { 1, 0, 0 } }, // yel, red
                { { 0, 0, 1 },  { 0, 1, 0 } }  // blu,  grn
            };
            
                        
        double col[][] = new double[nX*nY][];
        for(int iX=0; iX<nX; iX++){
            for(int iY=0; iY < nY; iY++){
                int i = iX * nY + iY;
                col[i] = quad[iX % 2][iY % 2];
            }
        }
        return col;
    }

    /** Create a colour map for a 2D grid of points, in repeating 3x3 cells of:<br/>
     *  [ l.red, d.red, magenta ]<br/>
     *  [ l.green, d.green, d.yellow ]<br/>
     *  [ l.blue, d.blue, cyan ]<br/>
     *  
     * whe color map is indexed as col[x*nY+y]
     * 
     * @param nX
     * @param nY
     * @return
     */
    public static double[][] alternating2D3x3(int nX, int nY) {
        double quad[][][] = {
            { { 1.0, 0.0, 0.0 }, { 0.5, 0.0, 0.0 }, { 1.0, 0.0, 1.0 }, },
            { { 0.0, 1.0, 0.0 }, { 0.0, 0.5, 0.0 }, { 0.7, 0.7, 0.0 }, },
            { { 0.0, 0.0, 1.0 }, { 0.0, 0.0, 0.5 }, { 0.0, 1.0, 1.0 }, }
            };
            
                        
        double col[][] = new double[nX*nY][];
        for(int iX=0; iX<nX; iX++){
            for(int iY=0; iY < nY; iY++){
                int i = iX * nY + iY;
                col[i] = quad[iX % 3][iY % 3];
            }
        }
        return col;
    }
}
