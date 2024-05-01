package uk.co.oliford.jolu;

import java.awt.Color;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.io.UncheckedIOException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.IntPredicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;

/**
 * Java routines to do various simple tasks.
 * To be called in a single short line only.
 *
 * The principal are:
 * 1) Someone else should be able to replace/rewrite the routine exactly without any information other than the routine's name (no JavaDoc or anything)
 *      and it shouldn't take them more than a few minutes at the very most.
 * 2) Write them as you need them, don't try to make it general or make the float[] version and the double[] version etc just for the point of it.
 * 3) Don't worry about them existing elsewhere or trying to find if already exist in here, just write it and carry on.
 *         (However if you see the same thing in here under different names, feel free to make one call the other and make it with [at]deprecate)
 *
 * @author oliford <codes(at)oliford.co.uk>
 * @author everyone-else
 */
public class OneLiners {

    private static final Logger logger = Logger.getLogger(OneLiners.class.getName());
    private static final String[] ORDINAL_NUMERAL = { "th", "st", "nd", "rd", "th", "th", "th", "th", "th", "th" };

    public static String printf(String fmt, Object... args) {
        String ret = String.format(Locale.ENGLISH, fmt, args);
        System.out.println(ret);
        return ret;
    }

    /**
     * Checks whether all keys are present in the map.
     *
     * @param map  the map to check
     * @param keys the keys to check
     * @return true if all keys are present
     */
    public static boolean mapHasKeys(Map<?, ?> map, Object... keys) {
        if (keys == null) return false;
        for (Object key : keys) {
            if (!map.containsKey(key)) return false;
        }
        return true;
    }

    public static void replaceNaN(double[] arr, double replacement) {
        for (int i = 0; i < arr.length; ++i) {
            if (Double.isNaN(arr[i])) arr[i] = replacement;
        }
    }

    public static void replaceNaN(double[][] arr, double replacement) {
        for (int i = 0; i < arr.length; ++i) {
            replaceNaN(arr[i], replacement);
        }
    }

    /**
     * Check if a given array contains (positive or negative) infinite values.
     * @param a the array to check for infinite values
     * @return true: a contains >=1 positive/negative infinity; false otherwise
     */
    public static final boolean hasInfinite(double[] a) {
        return DoubleStream.of(a).anyMatch(Double::isInfinite);
    }

    /**
     * Check if a given array contains (positive or negative) infinite values.
     * @param a the array to check for infinite values
     * @return true: a contains >=1 positive/negative infinity; false otherwise
     */
    public static final boolean hasInfinite(double[][] a) {
        return Stream.of(a).anyMatch(OneLiners::hasInfinite);
    }

    /**
     * Check if a given array contains (positive or negative) infinite values.
     * @param a the array to check for infinite values
     * @return true: a contains >=1 positive/negative infinity; false otherwise
     */
    public static final boolean hasInfinite(double[][][] a) {
        return Stream.of(a).anyMatch(OneLiners::hasInfinite);
    }

    /**
     * Check if a given array contains (positive or negative) infinite values.
     * @param a the array to check for infinite values
     * @return true: a contains >=1 positive/negative infinity; false otherwise
     */
    public static final boolean hasInfinite(double[][][][] a) {
        return Stream.of(a).anyMatch(OneLiners::hasInfinite);
    }

    /**
     * Check if a given array contains NaN values.
     * @param a the array to check for NaNs
     * @return true: a contains >=1 NaN; false otherwise
     */
    public static final boolean hasNaN(double[] a) {
        return DoubleStream.of(a).anyMatch(Double::isNaN);
    }

    /**
     * Check if a given array contains NaN values
     * @param a the array to check for NaNs
     * @return true: a contains >=1 NaN; false otherwise
     */
    public static final boolean hasNaN(double[][] a) {
        return Stream.of(a).anyMatch(OneLiners::hasNaN);
    }

    /**
     * Check if a given array contains NaN values
     * @param a the array to check for NaNs
     * @return true: a contains >=1 NaN; false otherwise
     */
    public static final boolean hasNaN(double[][][] a) {
        return Stream.of(a).anyMatch(OneLiners::hasNaN);
    }

    /**
     * Check if a given array contains NaN values
     * @param a the array to check for NaNs
     * @return true: a contains >=1 NaN; false otherwise
     */
    public static final boolean hasNaN(double[][][][] a) {
        return Stream.of(a).anyMatch(OneLiners::hasNaN);
    }

    /**
     * Check if a given array contains NaN values.
     * @param a the array to check for NaNs
     * @return true: a contains >=1 NaN; false otherwise
     */
    public static final boolean hasNotOnlyNaN(double[] a) {
        return DoubleStream.of(a).anyMatch(d -> !Double.isNaN(d));
    }

    /**
     * Check if a given array contains NaN values
     * @param a the array to check for NaNs
     * @return true: a contains >=1 NaN; false otherwise
     */
    public static final boolean hasNotOnlyNaN(double[][] a) {
        return Stream.of(a)
                .flatMapToDouble(Arrays::stream)
                .anyMatch(d -> !Double.isNaN(d));
    }

    /**
     * Check if a given array contains NaN values
     * @param a the array to check for NaNs
     * @return true: a contains >=1 NaN; false otherwise
     */
    public static final boolean hasNotOnlyNaN(double[][][] a) {
        return Stream.of(a)
                .flatMap(Arrays::stream)
                .flatMapToDouble(Arrays::stream)
                .anyMatch(d -> !Double.isNaN(d));
    }

    /**
     * Check if a given array contains NaN values
     * @param a the array to check for NaNs
     * @return true: a contains >=1 NaN; false otherwise
     */
    public static final boolean hasNotOnlyNaN(double[][][][] a) {
        return Stream.of(a)
                     .flatMap(Arrays::stream)
                     .flatMap(Arrays::stream)
                     .flatMapToDouble(Arrays::stream)
                     .anyMatch(d -> !Double.isNaN(d));
    }

    /**
     * 4-dimensional array subtraction;
     * works also for non-rectangular arrays
     * @param a
     * @param b
     * @return a[i][j][k][l] - b[i][j][k][l]
     */
    public static final double[][][][] subtract(double[][][][] a, double[][][][] b) {
        if (a.length != b.length) {
            return null;
        }

        double[][][][] ret = new double[a.length][][][];
        for (int i = 0; i < a.length; ++i) {
            ret[i] = subtract(a[i], b[i]);
        }

        return ret;
    }

    public static final double[][][] subtract(double[][][] a, double[][][] b) {
        if (a.length != b.length) {
            return null;
        }

        double[][][] ret = new double[a.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = subtract(a[i], b[i]);
        }

        return ret;
    }

    public static final double[][] subtract(double[][] a, double[][] b) {
        if (a.length != b.length) {
            return null;
        }

        double[][] ret = new double[a.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = subtract(a[i], b[i]);
        }

        return ret;
    }

    public static final double[] subtract(double[] a, double[] b) {
        if (a.length != b.length) {
            return null;
        }

        double[] ret = new double[a.length];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = a[i] - b[i];
        }

        return ret;
    }

    /**
     * 4-dimensional array summation;
     * works also for non-rectangular arrays
     * @param a
     * @return \sum_{i, j, k, l} a[i][j][k][l]
     */
    public static final double sum(double[][][][] a) {
        return Stream.of(a).mapToDouble(OneLiners::sum).sum();
    }

    public static final double sum(double[][][] a) {
        return Stream.of(a).mapToDouble(OneLiners::sum).sum();
    }

    public static final double sum(double[][] a) {
        return Stream.of(a).mapToDouble(OneLiners::sum).sum();
    }

    public static final double sum(double[] a) {
        return DoubleStream.of(a).sum();
    }

    /**
     * 4-dimensional array absolute value;
     * works also for non-rectangular arrays
     * @param a
     * @return abs(a[i][j][k][l])
     */
    public static final double[][][][] abs(double[][][][] a) {
        return Stream.of(a).map(OneLiners::abs).toArray(double[][][][]::new);
    }

    public static final double[][][] abs(double[][][] a) {
        return Stream.of(a).map(OneLiners::abs).toArray(double[][][]::new);
    }

    public static final double[][] abs(double[][] a) {
        return Stream.of(a).map(OneLiners::abs).toArray(double[][]::new);
    }

    public static final double[] abs(double[] a) {
        return DoubleStream.of(a).map(Math::abs).toArray();
    }

    public static double[][] extractRows(double[][] array, boolean[] keepRows) {
        int nRows = 0, rowIndex = 0;
        for (int i = 0; i < keepRows.length; ++i) if (keepRows[i]) ++nRows;

        double[][] ret = new double[nRows][];
        for (int i = 0; i < keepRows.length; ++i) {
            if (keepRows[i]) {
                ret[rowIndex] = array[i] == null ? null : array[i].clone();
                ++rowIndex;
            }
        }

        return ret;
    }

    public static double[][] extractCols(double[][] array, boolean[] keepCols) {
        return transpose(extractRows(transpose(array), keepCols));
    }

    /**
     * Given a 2d array {@code inArr} which is not rectangular, create a bounding array
     * which contains all data of {@code inArr} and the rest of the rows is filled with 0.0.
     * @param inArr input array, does not need to be rectangular.
     * @return rectangular 2d array
     */
    public static final double[][] rectangulize2DArray(double[][] inArr) {
        return rectangulize2DArray(inArr, 0.0);
    }

    /**
     * Given a 2d array {@code inArr} which is not rectangular, create a bounding array
     * which contains all data of {@code inArr} and the rest of the rows is filled with {@code fillValue}.
     * @param inArr input array, does not need to be rectangular.
     * @param fillValue value to fill rows which are shorter than the maximum column number of {@code inArr}
     * @return rectangular 2d array
     */
    public static final double[][] rectangulize2DArray(double[][] inArr, double fillValue) {
        int rows = inArr.length;
        int maxCols = 0;
        for (int row=0; row<rows; ++row) {
            if (inArr[row].length > maxCols) maxCols = inArr[row].length;
        }
        double[][] result = new double[rows][maxCols];
        for (int row=0; row<rows; ++row) {
            for (int col=0; col<inArr[row].length; ++col) {
                result[row][col] = inArr[row][col];
            }
            for (int col=inArr[row].length; col<maxCols; ++col) {
                result[row][col] = fillValue;
            }
        }
        return result;
    }

    /** Finds the index into xs[] nearest x where xs[] < x (integers)
     * @param xs Array of values
     * @param x Single value
     * @return
     */
    public static final int getNearestLowerIndex(int[] xs, int x){
        int k,klo,khi;
        klo = 0;
        khi = xs.length - 1;
        if (x <= xs[0]) return 0;
        if (x >= xs[khi]) return khi;
        while (khi - klo > 1) {
            k = (khi + klo) / 2;
            if (x < xs[k])
                khi = k;
            else
                klo = k;
        }
        return klo;
    }

    /** Determines the index below x, first assuming xs is regularly spaced and then with a bisection search if not */
    public static final int getNearestLowerIndexProbablyRegular(double[] xs, double x){
        if(x <= xs[0])return 0;
        if(x >= xs[xs.length-1])return xs.length-1;

        double dx = xs[1] - xs[0];
        int i = (int)((x - xs[0]) / dx);
        if(i >= 0 && i <= (xs.length-2) && (xs[i] <= x && xs[i+1] > x))
            return i;
        return getNearestLowerIndex(xs, x);
    }

    /** Finds the index into xs[] nearest x where xs[] < x (doubles)
     * @params xs Array of values
     * @param x Single value
     */
    public static final int getNearestLowerIndex(double[] xs, double x){
        int k,klo,khi;
        klo = 0;
        khi = xs.length - 1;
        if (x <= xs[0]) return 0;
        if (x >= xs[khi]) return khi;
        while (khi - klo > 1) {
            k = (khi + klo) / 2;
            if (x < xs[k]) khi = k;
            else klo = k;
        }
        return klo;
    }

    /** Finds the index into xs[] nearest x (doubles)
     * @params xs Array of values
     * @param x Single value
     */
    public static final int getNearestIndex(double[] xs, double x){
        int k,klo,khi;
        klo = 0;
        khi = xs.length - 1;
        if(x <= xs[0]) return 0;
        if(x >= xs[khi]) return khi;
        while (khi - klo > 1) {
            k = (khi + klo) / 2;
            if (x < xs[k]) khi = k;
            else klo = k;
        }
        if((x - xs[klo]) < (xs[khi] - xs[klo])/2) return klo;
        return khi;
    }

    /** Finds the index into xs[] nearest x (doubles)
     * @param xs the array of values
     * @param x Single value
     * @return
     */
    public static final int getNearestIndex(long[] xs, long x){
        int k,klo,khi;
        klo = 0;
        khi = xs.length - 1;
        if(x <= xs[0]) return 0;
        if(x >= xs[khi]) return khi;
        while (khi - klo > 1) {
            k = (khi + klo) / 2;
            if (x < xs[k]) khi = k;
            else klo = k;
        }
        if((x - xs[klo]) < (xs[khi] - xs[klo])/2) return klo;
        return khi;
    }

    /** Finds the index into xs[] nearest x (floats)
     * @param xs Array of values
     * @param x Single value
     * @return
     */
    public static final int getNearestIndex(float[] xs, float x){
        int k, klo, khi;
        klo = 0;
        khi = xs.length - 1;
        if (x <= xs[0]) return 0;
        if (x >= xs[khi]) return khi;
        while (khi - klo > 1) {
            k = (khi + klo) / 2;
            if (x < xs[k]) khi = k;
            else klo = k;
        }
        if ((x - xs[klo]) < (xs[khi] - xs[klo]) / 2) return klo;
        return khi;
    }

    /** does what is says on the tin */
    public static final void dumpArray(double[][] a, String fileName) {
        mkdir(Paths.get(fileName).getParent());
        try (PrintWriter out = new PrintWriter(fileName)) {
            dumpArray(a, out);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static final void dumpArray(int[][] a, String fileName){
        mkdir(Paths.get(fileName).getParent());
        try (PrintWriter out = new PrintWriter(fileName)) {
            dumpArray(a, out);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    /** does what is says on the tin */
    public static final void dumpArray(double[] a, String fileName){
        mkdir(Paths.get(fileName).getParent());
        try (PrintWriter out = new PrintWriter(fileName)) {
            dumpArray(a, out);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static final void dumpArray(int[] a, String fileName){
        mkdir(Paths.get(fileName).getParent());
        try (PrintWriter out = new PrintWriter(fileName)) {
            dumpArray(a, out);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /** does what is says on the tin */
    public static final void dumpArray(double[][] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }


    /** does what is says on the tin */
    public static final void dumpArray(int[][] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArray(long[][] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArray(double[] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArray(Double[] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }


    /** does what is says on the tin */
    public static final void dumpArray(float[] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArray(int[] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArray(boolean[] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArray(String[] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArray(byte[] a){
        dumpArray(a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArray(double[][] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            dumpArray(a[i], out);
    }

    /** does what is says on the tin */
    public static final void dumpArray(int[][] a, PrintWriter out){
        for (int i = 0; i < a.length; i++) {
            dumpArray(a[i], out);
        }
    }

    /** does what is says on the tin */
    public static final void dumpArray(long[][] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            dumpArray(a[i], out);
    }


    /** does what is says on the tin */
    public static final void dumpArray(double[] a, PrintWriter out){
        out.print(a[0]);
        for(int i=1;i<a.length;i++)
            out.print("\t"+a[i]);

        out.println("");
    }

    /** does what is says on the tin */
    public static final void dumpArray(Double[] a, PrintWriter out){
        out.print(a[0]);
        for(int i=1;i<a.length;i++)
            out.print("\t"+a[i]);

        out.println("");
    }

    /** does what is says on the tin */
    public static final void dumpArray(float[] a, PrintWriter out){
        out.print(a[0]);
        for(int i=1;i<a.length;i++)
            out.print("\t"+a[i]);

        out.println("");
    }

    /** does what is says on the tin */
    public static final void dumpArray(int[] a, PrintWriter out){
        out.print(a[0]);
        for(int i=1;i<a.length;i++)
            out.print("\t"+a[i]);

        out.println("");
    }

    /** does what is says on the tin */
    public static final void dumpArray(long[] a, PrintWriter out){
        out.print(a[0]);
        for(int i=1;i<a.length;i++)
            out.print("\t"+a[i]);

        out.println("");
    }

    /** does what is says on the tin */
    public static final void dumpArray(boolean[] a, PrintWriter out){
        out.print(a[0]);
        for(int i=1;i<a.length;i++)
            out.print("\t"+a[i]);

        out.println("");
    }

    /** does what is says on the tin */
    public static final void dumpArray(String[] a, PrintWriter out){
        out.print(a[0]);
        for(int i=1;i<a.length;i++)
            out.print("\t"+a[i]);

        out.println("");
    }

    /** does what is says on the tin */
    public static final void dumpArray(byte[] a, PrintWriter out){
        out.print(a[0]);
        for(int i=1;i<a.length;i++)
            out.print("\t"+a[i]);

        out.println("");
    }

    /**
     * Dumps an array in what way?
     **/
    public static final void dumpArray(double[] a, int n, int m, PrintWriter out){
        for (int i=0; i<m; i++)
            out.print("\t");
        out.print(a[0]);
        for(int i=1;i<a.length;i++) {
            if ((i)%n!=0)
                out.print("\t");
            else
                for (int j=0; j<m; j++)
                    out.print("\t");
            out.print(a[i]);
            if ((i+1)%n==0)
                out.print("\n");
        }
        out.println("");
    }

    /**
     * Dumps an array in what way?
     * @param a
     * @param n
     * @param m
     * @param fileName
     **/
    public static final void dumpArray(double[] a, int n, int m, String fileName) {
        mkdir(Paths.get(fileName).getParent());
        try {
            PrintWriter out = new PrintWriter(fileName);
            dumpArray(a, n, m, out);
            out.close();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, double[] a, String fileName) {
        if (ann != null && a != null) {
            if (ann.length == a.length) {
                mkdir(Paths.get(fileName).getParent());
                try (PrintWriter out = new PrintWriter(fileName)) {
                    dumpArrayAnnotated(ann, a, out);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            } else {
                String fileNameAnn = fileName+"_annotations";
                String fileNameA = fileName+"_values";
                System.out.println("WARNING: annotations have different length than values in dumpArrayAnnotated; dumping");
                System.out.println(" annotations into "+fileNameAnn);
                System.out.println("      values into "+fileNameA);
                mkdir(Paths.get(fileNameAnn).getParent());
                mkdir(Paths.get(fileNameA).getParent());
                try (PrintWriter outAnn = new PrintWriter(fileNameAnn)) {
                    dumpArray(ann, outAnn);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }

                try (PrintWriter outA = new PrintWriter(fileNameA)) {
                    dumpArray(a, outA);
                } catch (FileNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static final void dumpArrayAnnotated(String[] ann, int[] a, String fileName) {
        if (ann != null && a != null && ann.length == a.length) {
            mkdir(Paths.get(fileName).getParent());
            try (PrintWriter out = new PrintWriter(fileName);) {
                dumpArrayAnnotated(ann, a, out);
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, double[] a){
        dumpArrayAnnotated(ann, a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, Double[] a){
        dumpArrayAnnotated(ann, a, new PrintWriter(System.out, true));
    }


    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, float[] a){
        dumpArrayAnnotated(ann, a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, int[] a){
        dumpArrayAnnotated(ann, a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, boolean[] a){
        dumpArrayAnnotated(ann, a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, String[] a){
        dumpArrayAnnotated(ann, a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, byte[] a){
        dumpArrayAnnotated(ann, a, new PrintWriter(System.out, true));
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, double[] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            out.println(ann[i]+"\t"+a[i]);
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, Double[] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            out.println(ann[i]+"\t"+a[i]);
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, float[] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            out.println(ann[i]+"\t"+a[i]);
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, int[] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            out.println(ann[i]+"\t"+a[i]);
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, long[] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            out.println(ann[i]+"\t"+a[i]);
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, boolean[] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            out.println(ann[i]+"\t"+a[i]);
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, String[] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            out.println(ann[i]+"\t"+a[i]);
    }

    /** does what is says on the tin */
    public static final void dumpArrayAnnotated(String[] ann, byte[] a, PrintWriter out){
        for(int i=0;i<a.length;i++)
            out.println(ann[i]+"\t"+a[i]);
    }













    public static final double[][] transpose(double[][] dIn){
        int i,j,ni,nj;
        ni = dIn.length;
        if(ni <= 0)return new double[0][0];
        nj = dIn[0].length;
        double[][] dOut = new double[nj][ni];
        for(i=0;i<ni;i++)
            for(j=0;j<nj;j++)
                dOut[j][i]=dIn[i][j];

        return dOut;

    }

    public static final int[][] transpose(int[][] dIn){
        int i, j, ni, nj;
        ni = dIn.length;
        if (ni <= 0) return new int[0][0];
        nj = dIn[0].length;
        int[][] dOut = new int[nj][ni];
        for (i = 0; i < ni; i++) {
            for (j = 0; j < nj; j++) {
                dOut[j][i] = dIn[i][j];
            }
        }
        return dOut;

    }

    public static final double[] getColumn(double[][] mat, int idx){
        int n = mat.length;
        double[] ret = new double[n];
        for(int i=0; i < n; i++)
            ret[i] = mat[i][idx];
        return ret;
    }

    public static final double[][] getColumn(double[][][] mat, int idx){
        int n = mat.length;
        int m = mat[0].length;
        double[][] ret = new double[n][m];

        for(int k=0; k < m; k++) {
            for(int i=0; i < n; i++)
                ret[i][k] = mat[i][k][idx];
        }
        return ret;
    }



    public static final float[][] transpose(float[][] dIn){
        int i,j,ni,nj;
        ni = dIn.length;
        if(ni <= 0)return new float[0][0];
        nj = dIn[0].length;
        float[][] dOut = new float[nj][ni];
        for(i=0;i<ni;i++)
            for(j=0;j<nj;j++)
                dOut[j][i]=dIn[i][j];

        return dOut;
    }

    /**
     * Returns the diagonal elements of the given square matrix.
     * @param dIn
     * @return
     */
    public static final double[] diag(double[][] dIn) {
        Objects.requireNonNull(dIn);

        if (dIn.length != dIn[0].length) {
            throw new IllegalArgumentException("Matrix must be square: "+dIn.length+" != "+dIn[0].length);
        }
        double[] ret = new double[dIn.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = dIn[i][i];
        }

        return ret;
    }

    //? could also go to algorithms
    public static final double[][][] swapAxes(double[][][] a, int[] newAxesOrdering){
        int[] nAO = newAxesOrdering;
        int[] dimOld = new int[] {a.length, a[0].length, a[0][0].length};
        int[] dimNew = new int[] {dimOld[nAO[0]], dimOld[nAO[1]], dimOld[nAO[2]]};
        double[][][] ret = new double[dimNew[0]][dimNew[1]][dimNew[2]];

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                for (int k = 0; k < a[0][0].length; k++) {
                    int[] ids = new int[] {i, j, k};
                    int[] perm = new int[] {ids[nAO[0]],ids[nAO[1]], ids[nAO[2]]};
                    ret[perm[0]][perm[1]][perm[2]] = a[i][j][k];
                }
            }
        }
        return ret;
    }

    /**
     *
     * @param dirName
     * @deprecated See {@link #mkdir(String)}
     */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final void makeDir(String dirName) {
        mkdir(dirName);
    }

    /**
     * Create the given directories, if not already existent.
     * @param dirs the directories to make
     * @deprecated Please use {@link #mkdir(String)}
     */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final void makeDir(String... dirs) {
        for (int i = 0; i < dirs.length; i++) {
            mkdir(dirs[i]);
        }
    }

    /**
     *
     * @param dirName
     * @deprecated See {@link #mkdir(Path)}
     */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final void makeDir(Path dirName) {
        mkdir(dirName);
    }

    /** Makes the dirs in the path ready for the given file
     * @deprecated See {@link #mkdir(String)}
     * */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final void makePath(String fileName){
        File file = new File(fileName);

        File dirDesc = file.getAbsoluteFile().getParentFile();
        if (dirDesc.exists()) {
            if (!dirDesc.isDirectory())
                throw new RuntimeException(
                        "Cannot create directory '" + file.getParent() + "' because it already exists but is not a directory.");
            return; // already OK
        }

        //try to make it
        if(!dirDesc.mkdirs()) {
            if (dirDesc.exists()) {
                logger.log(Level.INFO, "Could not create directory, because it was probably already created by another process in the mean time...");
                return;
            }
            throw new RuntimeException("Directory '" + file.getParent() + "' does not exist and we cannot create it.");
        }
    }

    /**
     * Makes the specified directory.
     *
     * @param dir the directory to create. May not be null and may not point to an
     *            existing non-directory (it is fine if the directory already
     *            exists).
     * @throws NullPointerException if the given {@code dir} is null
     * @throws UncheckedIOException if an IO exception occurs
     * @see #mkdir(Path)
     */
    public static final void mkdir(String dir) {
        Objects.requireNonNull(dir);
        mkdir(Paths.get(dir));
    }

    /**
     * Makes the specified directory.
     *
     * @param dir the directory to create. May not be null and may not point to an
     *            existing non-directory (it is fine if the directory already
     *            exists).
     * @throws NullPointerException if the given {@code dir} is null
     * @throws UncheckedIOException if an IO exception occurs
     * @see #mkdir(String)
     */
    public static final void mkdir(Path dir) {
        Objects.requireNonNull(dir);        
        try {
            Files.createDirectories(dir);
        } catch (IOException e) {
            if(Files.isSymbolicLink(dir) && Files.exists(dir) && Files.isDirectory(dir)) {
                //failed because it was symlinked to a directory
                return; 
            }
            throw new UncheckedIOException("Unable to create directory " + dir, e);
        }
    }

    /** Makes the dirs in the path ready for the given file
     * @deprecated See {@link #mkdir(Path)}
     * */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final void makePath(Path fileName) {
        File file = fileName.toFile();

        File dirDesc = new File(file.getParent());
        if (dirDesc.exists()) {
            if (!dirDesc.isDirectory())
                throw new RuntimeException(
                        "Cannot create directory '" + file.getParent() + "' because it already exists but is not a directory.");

            return; // already OK
        }

        //try to make it
        if(!dirDesc.mkdirs()) {
            if (dirDesc.exists()) {
                logger.log(Level.INFO, "Could not create directory, because it was probably already created by another process in the mean time...");
                return;
            }
            throw new RuntimeException("Directory '" + file.getParent() + "' does not exist and we cannot create it.");
        }
    }

    /** Deletes all files and subdirectories under dir.
         Returns true if all deletions were successful.
         If a deletion fails, the method stops attempting to delete and returns false. */
    public static final boolean recursiveDelete(String path) { return recursiveDelete(path, false); }
    public static final boolean recursiveDelete(String path, boolean keepTopFolder){
        File dir = new File(path);
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                boolean success = recursiveDelete(path+"/"+children[i]);
                if (!success) {
                    return false;
                }
            }
        }

        // The directory is now empty so delete it
        return keepTopFolder ? true : dir.delete();

    }

    public static final String getSelectedFilePathViaGUI(String selectedFile) {
        JFileChooser jFileChooser = new JFileChooser();
        jFileChooser.setSelectedFile(new File(selectedFile));
        jFileChooser.showSaveDialog(null);
        return jFileChooser.getSelectedFile().getAbsolutePath();
    }

    /**
     * Ask the user a simple question without a default answer.
     * @param questionToUser something to explain the query to the user
     * @return a string entered by the user in response to the question
     */
    public static final String askString(String questionToUser) {
        return askString(questionToUser, "");
    }

    /**
     * Ask the user a simple question with a default answer.
     *
     * @param questionToUser something to explain what the code wants to know from
     *                       the user
     * @param defaultValue   default response proposed by the programmer
     * @return a string entered by the user in response to the question
     */
    public static final String askString(String questionToUser, String defaultValue) {
        String answer = (String) JOptionPane.showInputDialog(null, questionToUser, "askString", JOptionPane.PLAIN_MESSAGE,
                null, null, defaultValue);

        logger.log(Level.FINE, () -> String.format("User selected %s when asked %s with default answer %s.", answer, questionToUser, defaultValue));

        return answer;
    }

    public static final int shortToUnsignedInt(short a) {
        return Short.toUnsignedInt(a);
    }

    public static final int[] shortToUnsignedInt(short[] a) {
        int[] ret = new int[a.length];
        for (int i = 0; i < a.length; i++) {
            ret[i] = shortToUnsignedInt(a[i]);
        }

        return ret;
    }

    public static final int[][] shortToUnsignedInt(short[][] a) {
        int[][] ret = new int[a.length][];
        for (int i = 0; i < a.length; i++) {
            ret[i] = shortToUnsignedInt(a[i]);
        }

        return ret;
    }

    public static final int[][][] shortToUnsignedInt(short[][][] a) {
        int[][][] ret = new int[a.length][][];
        for (int i = 0; i < a.length; i++) {
            ret[i] = shortToUnsignedInt(a[i]);
        }

        return ret;
    }

    public static final long intToUnsignedLong(int a) {
        return Integer.toUnsignedLong(a);
    }

    public static final long[] intToUnsignedLong(int[] a) {
        return IntStream.of(a).mapToLong(Integer::toUnsignedLong).toArray();
    }

    public static final long[][] intToUnsignedLong(int[][] a) {
        return Stream.of(a).map(OneLiners::intToUnsignedLong).toArray(long[][]::new);
    }

    public static final long[][][] intToUnsignedLong(int[][][] a) {
        return Stream.of(a).map(OneLiners::intToUnsignedLong).toArray(long[][][]::new);
    }

    public static final boolean doubleToBoolean(double a) {
        return a > 0.5;
    }

    public static final boolean[] doubleToBoolean(double[] a) {
        boolean[] ret = new boolean[a.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = doubleToBoolean(a[i]);
        }

        return ret;
    }

    public static final boolean[][] doubleToBoolean(double[][] a) {
        boolean[][] ret = new boolean[a.length][];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = doubleToBoolean(a[i]);
        }

        return ret;
    }

    public static final boolean[][][] doubleToBoolean(double[][][] a) {
        boolean[][][] ret = new boolean[a.length][][];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = doubleToBoolean(a[i]);
        }

        return ret;
    }

    public static final double booleanToDouble(boolean a) {
        return a ? 1.0 : 0.0;
    }

    public static final double[] booleanToDouble(boolean[] a) {
        double[] ret = new double[a.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = booleanToDouble(a[i]);
        }

        return ret;
    }

    public static final double[][] booleanToDouble(boolean[][] a) {
        double[][] ret = new double[a.length][];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = booleanToDouble(a[i]);
        }

        return ret;
    }

    public static final double[][][] booleanToDouble(boolean[][][] a) {
        double[][][] ret = new double[a.length][][];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = booleanToDouble(a[i]);
        }

        return ret;
    }

    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final int doubleToInt(double a) {
        return (int) a; // that is nonsense, just cast it
    }

    public static final int[] doubleToInt(double[] a) {
        return DoubleStream.of(a).mapToInt(i -> (int) i).toArray();
    }

    public static final int[][] doubleToInt(double[][] a) {
        return Stream.of(a).map(OneLiners::doubleToInt).toArray(int[][]::new);
    }

    public static final int[][][] doubleToInt(double[][][] a) {
        return Stream.of(a).map(OneLiners::doubleToInt).toArray(int[][][]::new);
    }

    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final double intToDouble(int a) {
        return a; // that is nonsense, automatic widening will do it anyway
    }

    public static final double[] intToDouble(int[] a) {
        return IntStream.of(a).asDoubleStream().toArray();
    }

    public static final double[][] intToDouble(int[][] a) {
        return Stream.of(a).map(OneLiners::intToDouble).toArray(double[][]::new);
    }

    public static final double[][][] intToDouble(int[][][] a) {
        return Stream.of(a).map(OneLiners::intToDouble).toArray(double[][][]::new);
    }

    public static final short doubleToShort(double a) {
        return (short) a;
    }

    public static final short[] doubleToShort(double[] a) {
        short[] ret = new short[a.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = doubleToShort(a[i]);
        }

        return ret;
    }

    public static final short[][] doubleToShort(double[][] a) {
        short[][] ret = new short[a.length][];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = doubleToShort(a[i]);
        }

        return ret;
    }

    public static final short[][][] doubleToShort(double[][][] a) {
        short[][][] ret = new short[a.length][][];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = doubleToShort(a[i]);
        }

        return ret;
    }

    /** @deprecated This is nonsense, shorts can be widened automatically to doubles */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final double shortToDouble(short a) {
        return a;
    }

    public static final double[] shortToDouble(short[] a) {
        double[] ret = new double[a.length];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = a[i];
        }

        return ret;
    }

    public static final double[][] shortToDouble(short[][] a) {
        double[][] ret = new double[a.length][];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = shortToDouble(a[i]);
        }

        return ret;
    }

    public static final double[][][] shortToDouble(short[][][] a) {
        double[][][] ret = new double[a.length][][];
        for (int i = 0; i < ret.length; ++i) {
            ret[i] = shortToDouble(a[i]);
        }

        return ret;
    }

    public static final void TextToFile(String fileName, String text) {
        OneLiners.mkdir(Paths.get(fileName).toAbsolutePath().getParent());
        try (FileOutputStream fOut = new FileOutputStream(fileName)) {
            fOut.write(text.getBytes());
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    /**
     * From http://www.javapractices.com/topic/TopicAction.do?Id=42
     */
    public static final String fileToText(String fileName) {
        try {
            return fileToTextThrowingError(fileName);
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * @throws FileNotFoundException if the File does not exist
     */
    public static final String fileToTextThrowingError(String fileName) throws FileNotFoundException {
        try (FileReader fileReader = new FileReader(new File(fileName))) {
            return fileToText(fileReader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static final String urlToText(String urlString) {
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        String text = null;
        try (BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()))) {
            text = fileToText(in);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
        return text;
    }


    /**
     * From http://www.javapractices.com/topic/TopicAction.do?Id=42
     */
    public static String fileToText(Reader reader) {
        try (BufferedReader input = new BufferedReader(reader);
             Stream<String> lines = input.lines()) {
            return lines.collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException ex) {
            ex.printStackTrace();
            return null; // This needs to tell the caller something went wrong
        }
    }

    /**
     * Converts an input stream into a string.
     * Taken from http://stackoverflow.com/questions/309424/in-java-how-do-i-read-convert-an-inputstream-to-a-string
     *
     * @param is The input stream.
     * @param charsetName The character encoding, for example "UTF-8"
     * @return A string with the content of the input stream.
     */
    public static String streamToText(InputStream is, String charsetName) throws IOException {
        final char[] buffer = new char[0x10000];
        StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(is, charsetName);
        int read;
        do {
            read = in.read(buffer, 0, buffer.length);
            if (read>0) out.append(buffer, 0, read);
        } while (read >= 0);

        return out.toString();
    }

    /**
     * Converts an input stream into a string using default character set UTF-8.
     *
     * @param is The input stream.
     * @return A string with the content of the input stream.
     */
    public static String streamToText(InputStream is) throws IOException {
        return streamToText(is, "UTF-8");
    }

    /**
     * Reads the lines from a file.
     *
     * @param fileName the file to read in
     * @return the read in lines
     */
    public static final String[] linesFromFile(String fileName) {
        try (Stream<String> lines = Files.lines(Paths.get(fileName))) {
            return lines.toArray(String[]::new);
        } catch (IOException e) {
            e.printStackTrace();
            return null; // ugh...
        }
    }

    /** @return double[min/max][col] = min/max(f[*][col]) */
    public static final double[][] columnRanges(double[][] f){
        double[][] ret = new double[2][f[0].length];

        for(int j=0; j < f[0].length; j++){
            ret[0][j] = Double.POSITIVE_INFINITY;
            ret[1][j] = Double.NEGATIVE_INFINITY;
            for(int i=0; i < f.length; i++){
                if( f[i][j] < ret[0][j] )
                    ret[0][j] = f[i][j];
                if( f[i][j] > ret[1][j] )
                    ret[1][j] = f[i][j];
            }
        }
        return ret;
    }

    public static final double[] getRange(double[][] f) {
        double minF = Double.POSITIVE_INFINITY;
        double maxF = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < f.length; i++) {
            double[] range = getRange(f[i]);
            minF = Math.min(minF, range[0]);
            maxF = Math.max(maxF, range[1]);
        }
        return new double[]{ minF, maxF };
    }

    public static final double[] getRange(double[] f){
        double minF = Double.POSITIVE_INFINITY;
        double maxF = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < f.length; i++) {
            minF = Math.min(minF, f[i]);
            maxF = Math.max(maxF, f[i]);
        }
        return new double[]{ minF, maxF };
    }

    public static final int min(int... f){
        int minF = Integer.MAX_VALUE;
        for (int i = 0; i < f.length; i++) minF = Math.min(minF, f[i]);
        return minF;
    }

    public static final int max(int... f){
        int maxF = Integer.MIN_VALUE;
        for (int i = 0; i < f.length; i++) maxF = Math.max(maxF, f[i]);
        return maxF;
    }

    public static final double min(double... f){
        return DoubleStream.of(f).min().orElse(Double.POSITIVE_INFINITY);
    }

    public static final double max(double... f){
        return DoubleStream.of(f).max().orElse(Double.NEGATIVE_INFINITY);
    }

    public static final double mean(double... f) {
        double result = 0.0;
        for (int i = 0; i < f.length; i++) result += f[i];
        return result / f.length;
    }

    public static final double mean(double[][] f) {
        double result = 0.0;
        for (int i = 0; i < f.length; i++) result += mean(f[i]);
        return result / f.length;
    }

    public static final double[] linSpace(double x0, double x1, int n) {
        if (n == 1) return new double[] { (x0+x1)/2 };
        double dx = (x1 - x0) / (n - 1.0);
        double[] result = new double[n];
        for (int i = 0; i < n; i++) {
            result[i] = x0 + i*dx;
        }
        return result;
    }

    public static final double[][] linSpace3D(double[] startPoint, double[] endPoint, int numPoints){
        double[][] ret = new double[3][numPoints];
        for (int i = 0; i < 3; i++) {
            ret[i] = linSpace(startPoint[i], endPoint[i], numPoints);
        }
        return ret;
    }

    public static final double[] linSpace(double x0, double x1, double dx) {
        int num = (int) Math.round((x1 - x0) / dx + 1.0);
        double[] result = new double[num];
        for (int i = 0; i < num; i++) {
            result[i] = x0 + i*dx;
        }

        // the code below would be nicer when we can use a more modern version of Java
        // (and streams are a bit faster)
//        return IntStream.iterate(0, i -> i+1)
//                        .mapToDouble(i -> Math.fma(i, dx, x0))
//                        .takeWhile(i -> i <= x1)
//                        .toArray();

        return result;
    }

    public static final float[] linSpaceFloat(float x0, float x1, int n){
        if (n == 1) return new float[] { (x0+x1)/2 };
        float[] f = new float[n];
        float dx = (x1 - x0)/(float)(n - 1.0);
        for (int i = 0; i < n; i++) f[i] = x0 + i*dx;
        return f;
    }

    public static final float[] linSpaceFloat(float x0, float x1, float dx){
        int n = (int) Math.round((x1 - x0) / dx + 1.0);
        float[] f = new float[n];
        for (int i = 0; i < n; i++) f[i] = x0 + i*dx;
        return f;
    }

    /** Copies 2D arrays deeply. For 1D arrays use {@code double[].clone()} instead. */
    public static final double[][] copyArray(double[][] a) {
        return Stream.of(a)
                     .map(double[]::clone)
                     .toArray(double[][]::new);
    }

    /** Copies 3D arrays deeply. For 1D arrays use {@code double[].clone()} instead. */
    public static final double[][][] copyArray(double[][][] a) {
        return Stream.of(a)
                     .map(OneLiners::copyArray)
                     .toArray(double[][][]::new);
    }

    /** Copies 4D arrays deeply. For 1D arrays use {@code double[].clone()} instead. */
    public static final double[][][][] copyArray(double[][][][] a) {
        return Stream.of(a)
                 .map(OneLiners::copyArray)
                 .toArray(double[][][][]::new);
    }

    /**
     * Applies the given operator from the Math class to all elements
     * in the given array. Slow but sometimes convenient.
     *
     * Special operators: "+", "-", "*", "/" for the corresponding operations
     * between two arrays.
     *
     * @param op A Math method name, for example "sqrt"
     * @param x An array with doubles.
     * @param args Extra arguments for the operation.
     *
     * @return An array with the given operation performed on each element in the original array.
     */
    public static final double[] arrayOp(String op, double[] x, Object...args) {
        double[] ret = new double[x.length];

        if (op.equals("+")) {
            double[] y = (double[]) args[0];
            for(int i=0;i<ret.length;++i) {
                ret[i] = x[i]+y[i];
            }
            return ret;
        } else if (op.equals("-")) {
            double[] y = (double[]) args[0];
            for(int i=0;i<ret.length;++i) {
                ret[i] = x[i]-y[i];
            }
            return ret;
        } else if (op.equals("*")) {
            double[] y = (double[]) args[0];
            for(int i=0;i<ret.length;++i) {
                ret[i] = x[i]*y[i];
            }
            return ret;
        } else if (op.equals("/")) {
            double[] y = (double[]) args[0];
            for(int i=0;i<ret.length;++i) {
                ret[i] = x[i]/y[i];
            }
            return ret;
        }

        Method method = null;
        try {
            Class<?>[] argTypes = new Class<?>[1+args.length];
            argTypes[0] = double.class;
            for(int i=1;i<argTypes.length;++i) {
                argTypes[i] = args[i-1].getClass();
            }
            method = Math.class.getDeclaredMethod(op, argTypes);
            for(int i=0;i<ret.length;++i) {
                ret[i] = (Double) method.invoke(null, x[i]);
            }
        } catch (Exception ex) {
            throw new RuntimeException("Field "+op+" with double argument does not exist in Math class.");
        }

        return ret;
    }

    /** Multiplies all the elements of the given array by the given scalar */
    public static final double[] arrayMultiply(double x[], double c){
        double ret[] = new double[x.length];
        for(int i=0; i<x.length; i++)
            ret[i] = x[i] * c;
        return ret;
    }

    /** Multiplies all the elements of the given array by the given scalar */
    public static final double[][] arrayMultiply(double x[][], double c){
        double[][] ret = new double[x.length][x[0].length];
        for(int i=0; i<x.length; i++)
            for(int j=0; j<x[i].length; j++)
                ret[i][j] = x[i][j] * c;
        return ret;
    }

    /**
     * Allocates and fills a double array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the array size
     * @return the allocated array
     */
    public static final double[] fillArray(double value, int j) {
        double[] ret = new double[j];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Allocates and fills a double array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outer array size
     * @param k     the inner array size
     * @return the allocated array
     */
    public static final double[][] fillArray(double value, int j, int k) {
        double[][] ret = new double[j][k];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k);
        }
        return ret;
    }

    /**
     * Allocates and fills a double array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the in-between array size
     * @param l     the innermost array size
     * @return the allocated array
     */
    public static final double[][][] fillArray(double value, int j, int k, int l) {
        double[][][] ret = new double[j][k][l];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l);
        }
        return ret;
    }

    /**
     * Allocates and fills a double array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final double[][][][] fillArray(double value, int j, int k, int l, int m) {
        double[][][][] ret = new double[j][k][l][m];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m);
        }
        return ret;
    }

    /**
     * Allocates and fills a double array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final double[][][][][] fillArray(double value, int j, int k, int l, int m, int n) {
        double[][][][][] ret = new double[j][k][l][m][n];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m, n);
        }
        return ret;
    }

    /**
     * Allocates and fills a String array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the array size
     * @return the allocated array
     */
    public static final String[] fillArray(String value, int j) {
        String[] ret = new String[j];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Allocates and fills a String array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outer array size
     * @param k     the inner array size
     * @return the allocated array
     */
    public static final String[][] fillArray(String value, int j, int k) {
        String[][] ret = new String[j][k];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k);
        }
        return ret;
    }

    /**
     * Allocates and fills a String array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the in-between array size
     * @param l     the innermost array size
     * @return the allocated array
     */
    public static final String[][][] fillArray(String value, int j, int k, int l) {
        String[][][] ret = new String[j][k][l];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l);
        }
        return ret;
    }

    /**
     * Allocates and fills a String array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final String[][][][] fillArray(String value, int j, int k, int l, int m) {
        String[][][][] ret = new String[j][k][l][m];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m);
        }
        return ret;
    }

    /**
     * Allocates and fills a String array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final String[][][][][] fillArray(String value, int j, int k, int l, int m, int n) {
        String[][][][][] ret = new String[j][k][l][m][n];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m, n);
        }
        return ret;
    }

    /**
     * Allocates and fills an enum array with a specified value.
     *
     * @param <T> the type of the input enum
     * @param value the value for each element in the array
     * @param j     the array size
     * @return the allocated array
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Enum<?>> T[] fillArray(T value, int j) {
        T[] ret = (T[]) Array.newInstance(value.getClass(), j);
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Allocates and fills an enum array with a specified value.
     *
     * @param <T> the type of the input enum
     * @param value the value for each element in the array
     * @param j     the outer array size
     * @param k     the inner array size
     * @return the allocated array
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Enum<?>> T[][] fillArray(T value, int j, int k) {
        T[][] ret = (T[][]) Array.newInstance(getArrayType(value.getClass(), 1), j);
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k);
        }
        return ret;
    }

    /**
     * Allocates and fills an enum array with a specified value.
     *
     * @param <T> the type of the input enum
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the in-between array size
     * @param l     the innermost array size
     * @return the allocated array
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Enum<?>> T[][][] fillArray(T value, int j, int k, int l) {
        T[][][] ret = (T[][][]) Array.newInstance(getArrayType(value.getClass(), 2), j);
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l);
        }
        return ret;
    }

    /**
     * Allocates and fills an enum array with a specified value.
     *
     * @param <T> the type of the input enum
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Enum<?>> T[][][][] fillArray(T value, int j, int k, int l, int m) {
        T[][][][] ret = (T[][][][]) Array.newInstance(getArrayType(value.getClass(), 3), j);
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m);
        }
        return ret;
    }

    /**
     * Allocates and fills an enum array with a specified value.
     *
     * @param <T> the type of the input enum
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    @SuppressWarnings("unchecked")
    public static final <T extends Enum<?>> T[][][][][] fillArray(T value, int j, int k, int l, int m, int n) {
        T[][][][][] ret = (T[][][][][]) Array.newInstance(getArrayType(value.getClass(), 4), j);
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m, n);
        }
        return ret;
    }

    /**
     * Allocates and fills a boolean array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the array size
     * @return the allocated array
     */
    public static final boolean[] fillArray(boolean value, int j) {
        boolean[] ret = new boolean[j];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Allocates and fills a boolean array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outer array size
     * @param k     the inner array size
     * @return the allocated array
     */
    public static final boolean[][] fillArray(boolean value, int j, int k) {
        boolean[][] ret = new boolean[j][k];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k);
        }
        return ret;
    }

    /**
     * Allocates and fills a boolean array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the in-between array size
     * @param l     the innermost array size
     * @return the allocated array
     */
    public static final boolean[][][] fillArray(boolean value, int j, int k, int l) {
        boolean[][][] ret = new boolean[j][k][l];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l);
        }
        return ret;
    }

    /**
     * Allocates and fills a boolean array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final boolean[][][][] fillArray(boolean value, int j, int k, int l, int m) {
        boolean[][][][] ret = new boolean[j][k][l][m];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m);
        }
        return ret;
    }

    /**
     * Allocates and fills a boolean array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final boolean[][][][][] fillArray(boolean value, int j, int k, int l, int m, int n) {
        boolean[][][][][] ret = new boolean[j][k][l][m][n];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m, n);
        }
        return ret;
    }

    /**
     * Allocates and fills a int array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the array size
     * @return the allocated array
     */
    public static final int[] fillArray(int value, int j) {
        int[] ret = new int[j];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Allocates and fills a int array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outer array size
     * @param k     the inner array size
     * @return the allocated array
     */
    public static final int[][] fillArray(int value, int j, int k) {
        int[][] ret = new int[j][k];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k);
        }
        return ret;
    }

    /**
     * Allocates and fills a int array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the in-between array size
     * @param l     the innermost array size
     * @return the allocated array
     */
    public static final int[][][] fillArray(int value, int j, int k, int l) {
        int[][][] ret = new int[j][k][l];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l);
        }
        return ret;
    }

    /**
     * Allocates and fills a int array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final int[][][][] fillArray(int value, int j, int k, int l, int m) {
        int[][][][] ret = new int[j][k][l][m];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m);
        }
        return ret;
    }

    /**
     * Allocates and fills a int array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final int[][][][][] fillArray(int value, int j, int k, int l, int m, int n) {
        int[][][][][] ret = new int[j][k][l][m][n];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m, n);
        }
        return ret;
    }

    /**
     * Allocates and fills a long array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the array size
     * @return the allocated array
     */
    public static final long[] fillArray(long value, int j) {
        long[] ret = new long[j];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Allocates and fills a long array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outer array size
     * @param k     the inner array size
     * @return the allocated array
     */
    public static final long[][] fillArray(long value, int j, int k) {
        long[][] ret = new long[j][k];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k);
        }
        return ret;
    }

    /**
     * Allocates and fills a long array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the in-between array size
     * @param l     the innermost array size
     * @return the allocated array
     */
    public static final long[][][] fillArray(long value, int j, int k, int l) {
        long[][][] ret = new long[j][k][l];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l);
        }
        return ret;
    }

    /**
     * Allocates and fills a long array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final long[][][][] fillArray(long value, int j, int k, int l, int m) {
        long[][][][] ret = new long[j][k][l][m];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m);
        }
        return ret;
    }

    /**
     * Allocates and fills a long array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final long[][][][][] fillArray(long value, int j, int k, int l, int m, int n) {
        long[][][][][] ret = new long[j][k][l][m][n];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m, n);
        }
        return ret;
    }

    /**
     * Allocates and fills a float array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the array size
     * @return the allocated array
     */
    public static final float[] fillArray(float value, int j) {
        float[] ret = new float[j];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Allocates and fills a float array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outer array size
     * @param k     the inner array size
     * @return the allocated array
     */
    public static final float[][] fillArray(float value, int j, int k) {
        float[][] ret = new float[j][k];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k);
        }
        return ret;
    }

    /**
     * Allocates and fills a float array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the in-between array size
     * @param l     the innermost array size
     * @return the allocated array
     */
    public static final float[][][] fillArray(float value, int j, int k, int l) {
        float[][][] ret = new float[j][k][l];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l);
        }
        return ret;
    }

    /**
     * Allocates and fills a float array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final float[][][][] fillArray(float value, int j, int k, int l, int m) {
        float[][][][] ret = new float[j][k][l][m];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m);
        }
        return ret;
    }

    /**
     * Allocates and fills a float array with a specified value.
     *
     * @param value the value for each element in the array
     * @param j     the outermost array size
     * @param k     the outer in-between array size
     * @param l     the inner in-between array size
     * @param m     the innermost array size
     * @return the allocated array
     */
    public static final float[][][][][] fillArray(float value, int j, int k, int l, int m, int n) {
        float[][][][][] ret = new float[j][k][l][m][n];
        for (int i = 0; i < j; ++i) {
            ret[i] = fillArray(value, k, l, m, n);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final float[] fillArrayLike(float value, float[] shape) {
        float[] ret = new float[shape.length];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final float[][] fillArrayLike(float value, float[][] shape) {
        float[][] ret = new float[shape.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final float[][][] fillArrayLike(float value, float[][][] shape) {
        float[][][] ret = new float[shape.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final float[][][][] fillArrayLike(float value, float[][][][] shape) {
        float[][][][] ret = new float[shape.length][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final float[][][][][] fillArrayLike(float value, float[][][][][] shape) {
        float[][][][][] ret = new float[shape.length][][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final double[] fillArrayLike(double value, double[] shape) {
        double[] ret = new double[shape.length];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final double[][] fillArrayLike(double value, double[][] shape) {
        double[][] ret = new double[shape.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final double[][][] fillArrayLike(double value, double[][][] shape) {
        double[][][] ret = new double[shape.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final double[][][][] fillArrayLike(double value, double[][][][] shape) {
        double[][][][] ret = new double[shape.length][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final double[][][][][] fillArrayLike(double value, double[][][][][] shape) {
        double[][][][][] ret = new double[shape.length][][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final int[] fillArrayLike(int value, int[] shape) {
        int[] ret = new int[shape.length];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final int[][] fillArrayLike(int value, int[][] shape) {
        int[][] ret = new int[shape.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final int[][][] fillArrayLike(int value, int[][][] shape) {
        int[][][] ret = new int[shape.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final int[][][][] fillArrayLike(int value, int[][][][] shape) {
        int[][][][] ret = new int[shape.length][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final int[][][][][] fillArrayLike(int value, int[][][][][] shape) {
        int[][][][][] ret = new int[shape.length][][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final long[] fillArrayLike(long value, long[] shape) {
        long[] ret = new long[shape.length];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final long[][] fillArrayLike(long value, long[][] shape) {
        long[][] ret = new long[shape.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final long[][][] fillArrayLike(long value, long[][][] shape) {
        long[][][] ret = new long[shape.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final long[][][][] fillArrayLike(long value, long[][][][] shape) {
        long[][][][] ret = new long[shape.length][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final long[][][][][] fillArrayLike(long value, long[][][][][] shape) {
        long[][][][][] ret = new long[shape.length][][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final boolean[] fillArrayLike(boolean value, boolean[] shape) {
        boolean[] ret = new boolean[shape.length];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final boolean[][] fillArrayLike(boolean value, boolean[][] shape) {
        boolean[][] ret = new boolean[shape.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final boolean[][][] fillArrayLike(boolean value, boolean[][][] shape) {
        boolean[][][] ret = new boolean[shape.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final boolean[][][][] fillArrayLike(boolean value, boolean[][][][] shape) {
        boolean[][][][] ret = new boolean[shape.length][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final boolean[][][][][] fillArrayLike(boolean value, boolean[][][][][] shape) {
        boolean[][][][][] ret = new boolean[shape.length][][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final String[] fillArrayLike(String value, String[] shape) {
        String[] ret = new String[shape.length];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final String[][] fillArrayLike(String value, String[][] shape) {
        String[][] ret = new String[shape.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final String[][][] fillArrayLike(String value, String[][][] shape) {
        String[][][] ret = new String[shape.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final String[][][][] fillArrayLike(String value, String[][][][] shape) {
        String[][][][] ret = new String[shape.length][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    public static final String[][][][][] fillArrayLike(String value, String[][][][][] shape) {
        String[][][][][] ret = new String[shape.length][][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param <T> the type of the input enum
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[] fillArrayLike(Enum<?> value, Object[] shape) {
        Enum<?>[] ret = new Enum<?>[shape.length];
        Arrays.fill(ret, value);
        return (T[]) ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param <T> the type of the input enum
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[][] fillArrayLike(Enum<?> value, Object[][] shape) {
        Enum<?>[][] ret = new Enum<?>[shape.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return (T[][]) ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param <T> the type of the input enum
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[][][] fillArrayLike(Enum<?> value, Object[][][] shape) {
        Enum<?>[][][] ret = new Enum<?>[shape.length][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return (T[][][]) ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param <T> the type of the input enum
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[][][][] fillArrayLike(Enum<?> value, Object[][][][] shape) {
        Enum<?>[][][][] ret = new Enum<?>[shape.length][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return (T[][][][]) ret;
    }

    /**
     * Fills an array with {@code value} of the shape of {@code shape}.
     *
     * @param <T> the type of the input enum
     * @param value the fill value
     * @param shape the array of which you want the shape
     * @return the filled array
     */
    @SuppressWarnings("unchecked")
    public static final <T> T[][][][][] fillArrayLike(Enum<?> value, Object[][][][][] shape) {
        Enum<?>[][][][][] ret = new Enum<?>[shape.length][][][][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = fillArrayLike(value, shape[i]);
        }
        return (T[][][][][]) ret;
    }

    /**
     * Takes the specified subsection of the given array an puts it in a new array of length n
     * @param value The array to take n elements from
     * @param n The array size.
     * @return The allocated array.
     * @deprecated Use {@link Arrays#copyOfRange(boolean[], int, int)}
     */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final boolean[] subArray(boolean[] in, int i0, int n) {
        return Arrays.copyOfRange(in, i0, i0+n);
    }

    /**
     * Takes the specified subsection of the given array an puts it in a new array of length n
     * @param value The array to take n elements from
     * @param n The array size.
     * @return The allocated array.
     * @deprecated Use {@link Arrays#copyOfRange(boolean[], int, int)}
     */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final short[] subArray(short[] in, int i0, int n) {
        return Arrays.copyOfRange(in, i0, i0+n);
    }

    /**
     * Takes the specified subsection of the given array an puts it in a new array of length n
     * @param value The array to take n elements from
     * @param n The array size.
     * @return The allocated array.
     * @deprecated Use {@link Arrays#copyOfRange(boolean[], int, int)}
     */
    @Deprecated(forRemoval = true, since = "1.5.0")
    public static final double[] subArray(double[] in, int i0, int n) {
        return Arrays.copyOfRange(in, i0, i0+n);
    }

    /**
     * Extracts in[startRow:startRow+numRows][startCol:startCol+numCols] from given input array
     * @param in input array, must be >= [startRow+numRows][startCol+numCols]
     * @param startRow starting value for first index
     * @param numRows number of elements to take in first dimension
     * @param startCol starting value of second index
     * @param numCols number of elements to take in second dimension
     * @return sub-array from in
     */
    public static final double[][] subArray(double[][] in, int startRow, int numRows, int startCol, int numCols) {
        if (in == null) return null;
        if (startRow > in.length-1    || startRow+numRows > in.length)    throw new RuntimeException("Row range exceeded!");
        if (startCol > in[0].length-1 || startCol+numCols > in[0].length) throw new RuntimeException("Col range exceeded!");

        double[][] ret = new double[numRows][numCols];
        for (int row=startRow; row<startRow+numRows; ++row) {
            System.arraycopy(in[row], startCol, ret[row-startRow], 0, numCols);
        }

        return ret;
    }

    /**
     * Extracts in[startRow:startRow+numRows][startCol:startCol+numCols] from given input array
     * @param in input array, must be >= [startRow+numRows][startCol+numCols]
     * @param startRow starting value for first index
     * @param numRows number of elements to take in first dimension
     * @param startCol starting value of second index
     * @param numCols number of elements to take in second dimension
     * @return sub-array from in
     */
    public static final boolean[][] subArray(boolean[][] in, int startRow, int numRows, int startCol, int numCols) {
        if (in == null) return null;
        if (startRow > in.length-1    || startRow+numRows > in.length)      throw new RuntimeException("Row range exceeded!");
        if (startCol > in[0].length-1 || startCol+numCols > in[0].length) throw new RuntimeException("Col range exceeded!");

        boolean[][] ret = new boolean[numRows][numCols];
        for (int row = startRow; row < startRow + numRows; ++row) {
            System.arraycopy(in[row], startCol, ret[row-startRow], 0, numCols);
        }

        return ret;
    }


    /**
     * Allocates and fills a float array with a specified value.
     * @param value The value for each element in the array.
     * @param n The array size.
     * @return The allocated array.
     */
    public static final float[] fillFloatArray(float value, int n) {
        float[] ret = new float[n];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Allocates and fills an int array with a specified value.
     * @param value The value for each element in the array.
     * @param n The array size.
     * @return The allocated array.
     */
    public static final int[] fillIntArray(int value, int n) {
        int[] ret = new int[n];
        Arrays.fill(ret, value);
        return ret;
    }





    // flatten
    //   2d
    //     int
    //     long
    //     double
    //     String
    //     <T>
    //   3d
    //     int
    //     long
    //     double
    //     String
    //     <T>
    //   4d
    //     int
    //     long
    //     double
    //     String
    //   List
    //     <T>

    /**
     * Flattens a 2D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1] the 2D array
     * @return [len0 * len1] the flattened array
     */
    public static final int[] flatten(int[][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;

        final int numTotal = len0 * len1;

        final int[] ret = new int[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            System.arraycopy(mat[idx0], 0, ret, idxTotal, len1);
            idxTotal += len1;
        }

        return ret;
    }

    /**
     * Flattens a 2D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1] the 2D array
     * @return [len0 * len1] the flattened array
     */
    public static final long[] flatten(long[][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;

        final int numTotal = len0 * len1;

        final long[] ret = new long[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            System.arraycopy(mat[idx0], 0, ret, idxTotal, len1);
            idxTotal += len1;
        }

        return ret;
    }

    /**
     * Flattens a 2D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1] the 2D array
     * @return [len0 * len1] the flattened array
     */
    public static final double[] flatten(double[][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;

        final int numTotal = len0 * len1;

        final double[] ret = new double[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            System.arraycopy(mat[idx0], 0, ret, idxTotal, len1);
            idxTotal += len1;
        }

        return ret;
    }

    /**
     * Flattens a 2D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1] the 2D array
     * @return [len0 * len1] the flattened array
     */
    public static final String[] flatten(String[][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;

        final int numTotal = len0 * len1;

        final String[] ret = new String[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            System.arraycopy(mat[idx0], 0, ret, idxTotal, len1);
            idxTotal += len1;
        }

        return ret;
    }

    /**
     * Flattens the matrix, meant for primitive 2D arrays.
     *
     * @param <T> the type
     * @param mat the matrix
     * @return the flattened matrix
     */
    public static final <T> T flatten(T[] mat) {
        int size = 0;
        for (int i = 0; i < mat.length; i++) {
            size += Array.getLength(mat[i]);
        }

        @SuppressWarnings("unchecked")
        T flat = (T) Array.newInstance(mat.getClass().getComponentType().getComponentType(), size);
        int index = 0;
        for (int i = 0; i < mat.length; i++) {
            int numNew = Array.getLength(mat[i]);
            System.arraycopy(mat[i], 0, flat, index, numNew);
            index += numNew;
        }
        return flat;
    }

    /**
     * Flattens a 3D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1][len2] the 3D array
     * @return [len0 * len1 * len2] the flattened array
     */
    public static final int[] flatten(int[][][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;
        final int len2 = mat[0][0].length;

        final int numTotal = len0 * len1 * len2;

        final int[] ret = new int[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                System.arraycopy(mat[idx0][idx1], 0, ret, idxTotal, len2);
                idxTotal += len2;
            }
        }

        return ret;
    }

    /**
     * Flattens a 3D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1][len2] the 3D array
     * @return [len0 * len1 * len2] the flattened array
     */
    public static final long[] flatten(long[][][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;
        final int len2 = mat[0][0].length;

        final int numTotal = len0 * len1 * len2;

        final long[] ret = new long[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                System.arraycopy(mat[idx0][idx1], 0, ret, idxTotal, len2);
                idxTotal += len2;
            }
        }

        return ret;
    }

    /**
     * Flattens a 3D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1][len2] the 3D array
     * @return [len0 * len1 * len2] the flattened array
     */
    public static final double[] flatten(double[][][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;
        final int len2 = mat[0][0].length;

        final int numTotal = len0 * len1 * len2;

        final double[] ret = new double[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                System.arraycopy(mat[idx0][idx1], 0, ret, idxTotal, len2);
                idxTotal += len2;
            }
        }

        return ret;
    }

    /**
     * Flattens a 3D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1][len2] the 3D array
     * @return [len0 * len1 * len2] the flattened array
     */
    public static final String[] flatten(String[][][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;
        final int len2 = mat[0][0].length;

        final int numTotal = len0 * len1 * len2;

        final String[] ret = new String[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                System.arraycopy(mat[idx0][idx1], 0, ret, idxTotal, len2);
                idxTotal += len2;
            }
        }

        return ret;
    }

    /**
     * Flattens the matrix.
     *
     * @param <T> the type
     * @param mat the matrix
     * @return the flattened matrix
     */
    public static final <T> T[] flatten(T[][] mat) {
        int size = 0;
        for (int i = 0; i < mat.length; i++) {
            size += mat[i].length;
        }

        @SuppressWarnings("unchecked")
        T[] flat = (T[]) Array.newInstance(mat.getClass().getComponentType().getComponentType(), size);
        int index = 0;
        for (int i = 0; i < mat.length; i++) {
            int numNew = mat[i].length;
            System.arraycopy(mat[i], 0, flat, index, numNew);
            index += numNew;
        }
        return flat;
    }

    /**
     * Flattens a 4D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1][len2][len3] the 3D array
     * @return [len0 * len1 * len2 * len3] the flattened array
     */
    public static final int[] flatten(int[][][][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;
        final int len2 = mat[0][0].length;
        final int len3 = mat[0][0][0].length;

        final int numTotal = len0 * len1 * len2 * len3;

        final int[] ret = new int[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                for (int idx2 = 0; idx2 < len2; ++idx2) {
                    System.arraycopy(mat[idx0][idx1][idx2], 0, ret, idxTotal, len3);
                    idxTotal += len3;
                }
            }
        }

        return ret;
    }

    /**
     * Flattens a 4D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1][len2][len3] the 3D array
     * @return [len0 * len1 * len2 * len3] the flattened array
     */
    public static final long[] flatten(long[][][][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;
        final int len2 = mat[0][0].length;
        final int len3 = mat[0][0][0].length;

        final int numTotal = len0 * len1 * len2 * len3;

        final long[] ret = new long[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                for (int idx2 = 0; idx2 < len2; ++idx2) {
                    System.arraycopy(mat[idx0][idx1][idx2], 0, ret, idxTotal, len3);
                    idxTotal += len3;
                }
            }
        }

        return ret;
    }

    /**
     * Flattens a 4D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1][len2][len3] the 3D array
     * @return [len0 * len1 * len2 * len3] the flattened array
     */
    public static final double[] flatten(double[][][][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;
        final int len2 = mat[0][0].length;
        final int len3 = mat[0][0][0].length;

        final int numTotal = len0 * len1 * len2 * len3;

        final double[] ret = new double[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                for (int idx2 = 0; idx2 < len2; ++idx2) {
                    System.arraycopy(mat[idx0][idx1][idx2], 0, ret, idxTotal, len3);
                    idxTotal += len3;
                }
            }
        }

        return ret;
    }

    /**
     * Flattens a 4D array into 1D.
     * The returned data is stored row by row from {@code mat}.
     * It is assumed that {@code mat} is rectangular.
     *
     * @param mat [len0][len1][len2][len3] the 3D array
     * @return [len0 * len1 * len2 * len3] the flattened array
     */
    public static final String[] flatten(String[][][][] mat) {
        Objects.requireNonNull(mat);

        final int len0 = mat.length;
        final int len1 = mat[0].length;
        final int len2 = mat[0][0].length;
        final int len3 = mat[0][0][0].length;

        final int numTotal = len0 * len1 * len2 * len3;

        final String[] ret = new String[numTotal];

        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                for (int idx2 = 0; idx2 < len2; ++idx2) {
                    System.arraycopy(mat[idx0][idx1][idx2], 0, ret, idxTotal, len3);
                    idxTotal += len3;
                }
            }
        }

        return ret;
    }

    /**
     * Flattens a list containing arrays.
     *
     * @param <T>  the type of the arrays that the list contains
     * @param list the list to flatten
     * @return the flattened list
     */
    public static final <T> List<T> flatten(List<T[]> list) {
        Objects.requireNonNull(list);
        return list.stream().flatMap(Arrays::stream).toList();
    }

    /**
     * Flatten a jagged 2D double array to a 1D array.
     * @param mat array to flatten
     * @return the flattened array
     */
    public static final double[] flattenNonUniform(double[][] mat){
        Objects.requireNonNull(mat);
        return Stream.of(mat).flatMapToDouble(Arrays::stream).toArray();
    }

    /**
     * Unflatten a 1D array into an already-allocated 2D array.
     * It is assumed that {@code dest} is rectangular.
     *
     * @param src  [len0 * len1] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param dest [len0][len1] destination array
     */
    public static void unflattenInto(final int[] src, final int len0, final int len1, final int[][] dest) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; idx0++) {
            System.arraycopy(src, idxTotal, dest[idx0], 0, len1);
            idxTotal += len1;
        }
    }

    /**
     * Unflatten a 1D array into a 2D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @return [len0][len1] destination array
     */
    public static final int[][] unflatten(final int[] src, final int len0, final int len1) {
        final int[][] dest = new int[len0][len1];
        OneLiners.unflattenInto(src, len0, len1, dest);
        return dest;
    }

    /**
     * Unflatten a 1D array into an already-allocated 2D array.
     * It is assumed that {@code dest} is rectangular.
     *
     * @param src  [len0 * len1] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param dest [len0][len1] destination array
     */
    public static void unflattenInto(final long[] src, final int len0, final int len1, final long[][] dest) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; idx0++) {
            System.arraycopy(src, idxTotal, dest[idx0], 0, len1);
            idxTotal += len1;
        }
    }

    /**
     * Unflatten a 1D array into a 2D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @return [len0][len1] destination array
     */
    public static final long[][] unflatten(final long[] src, final int len0, final int len1) {
        final long[][] dest = new long[len0][len1];
        OneLiners.unflattenInto(src, len0, len1, dest);
        return dest;
    }

    /**
     * Unflatten a 1D array into an already-allocated 2D array.
     * It is assumed that {@code dest} is rectangular.
     *
     * @param src  [len0 * len1] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param dest [len0][len1] destination array
     */
    public static void unflattenInto(final double[] src, final int len0, final int len1, final double[][] dest) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; idx0++) {
            System.arraycopy(src, idxTotal, dest[idx0], 0, len1);
            idxTotal += len1;
        }
    }

    /**
     * Unflatten a 1D array into a 2D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @return [len0][len1] destination array
     */
    public static final double[][] unflatten(final double[] src, final int len0, final int len1) {
        final double[][] dest = new double[len0][len1];
        OneLiners.unflattenInto(src, len0, len1, dest);
        return dest;
    }

    /**
     * Unflatten a 1D array into an already-allocated 2D array.
     * It is assumed that {@code dest} is rectangular.
     *
     * @param src  [len0 * len1] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param dest [len0][len1] destination array
     */
    public static void unflattenInto(final String[] src, final int len0, final int len1, final String[][] dest) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; idx0++) {
            System.arraycopy(src, idxTotal, dest[idx0], 0, len1);
            idxTotal += len1;
        }
    }

    /**
     * Unflatten a 1D array into a 2D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @return [len0][len1] destination array
     */
    public static final String[][] unflatten(final String[] src, final int len0, final int len1) {
        final String[][] dest = new String[len0][len1];
        OneLiners.unflattenInto(src, len0, len1, dest);
        return dest;
    }

    /**
     * Unflattens the array.
     *
     * @param <T>   the type
     * @param array the array
     * @param n     the number of rows
     * @param m     the number of columns
     * @return the flattened matrix
     */
    public static final <T> T[] unflatten(T array, int n, int m) {
        if (!array.getClass().isArray()) {
            throw new IllegalArgumentException("'array' needs to be an array!");
        }

        @SuppressWarnings("unchecked")
        T[] ret = (T[]) Array.newInstance(array.getClass().getComponentType(), n, m);
        for (int i = 0; i < n; i++) {
            System.arraycopy(array, i*m, ret[i], 0, m);
        }
        return ret;
    }

    // 3d

    /**
     * Unflatten a 1D array into an already-allocated 3D array.
     * It is assumed that {@code dest} is rectangular.
     *
     * @param src  [len0 * len1 * len2] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param dest [len0][len1][len2] destination array
     */
    public static void unflattenInto(final int[] src, final int len0, final int len1, final int len2, final int[][][] dest) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; idx0++) {
            for (int idx1 = 0; idx1 < len1; idx1++) {
                System.arraycopy(src, idxTotal, dest[idx0][idx1], 0, len2);
                idxTotal += len2;
            }
        }
    }

    /**
     * Unflatten a 1D array into a 3D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @return [len0][len1][len2] destination array
     */
    public static final int[][][] unflatten(final int[] src, final int len0, final int len1, final int len2) {
        final int[][][] dest = new int[len0][len1][len2];
        OneLiners.unflattenInto(src, len0, len1, len2, dest);
        return dest;
    }

    /**
     * Unflatten a 1D array into an already-allocated 3D array.
     * It is assumed that {@code dest} is rectangular.
     *
     * @param src  [len0 * len1 * len2] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param dest [len0][len1][len2] destination array
     */
    public static void unflattenInto(final long[] src, final int len0, final int len1, final int len2, final long[][][] dest) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; idx0++) {
            for (int idx1 = 0; idx1 < len1; idx1++) {
                System.arraycopy(src, idxTotal, dest[idx0][idx1], 0, len2);
                idxTotal += len2;
            }
        }
    }

    /**
     * Unflatten a 1D array into a 3D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @return [len0][len1][len2] destination array
     */
    public static final long[][][] unflatten(final long[] src, final int len0, final int len1, final int len2) {
        final long[][][] dest = new long[len0][len1][len2];
        OneLiners.unflattenInto(src, len0, len1, len2, dest);
        return dest;
    }

    /**
     * Unflatten a 1D array into an already-allocated 3D array.
     * It is assumed that {@code dest} is rectangular.
     *
     * @param src  [len0 * len1 * len2] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param dest [len0][len1][len2] destination array
     */
    public static void unflattenInto(final double[] src, final int len0, final int len1, final int len2, final double[][][] dest) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; idx0++) {
            for (int idx1 = 0; idx1 < len1; idx1++) {
                System.arraycopy(src, idxTotal, dest[idx0][idx1], 0, len2);
                idxTotal += len2;
            }
        }
    }

    /**
     * Unflatten a 1D array into a 3D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @return [len0][len1][len2] destination array
     */
    public static final double[][][] unflatten(final double[] src, final int len0, final int len1, final int len2) {
        final double[][][] dest = new double[len0][len1][len2];
        OneLiners.unflattenInto(src, len0, len1, len2, dest);
        return dest;
    }

    /**
     * Unflatten a 1D array into an already-allocated 3D array.
     * It is assumed that {@code dest} is rectangular.
     *
     * @param src  [len0 * len1 * len2] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param dest [len0][len1][len2] destination array
     */
    public static void unflattenInto(final String[] src, final int len0, final int len1, final int len2, final String[][][] dest) {
        Objects.requireNonNull(src);
        Objects.requireNonNull(dest);
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; idx0++) {
            for (int idx1 = 0; idx1 < len1; idx1++) {
                System.arraycopy(src, idxTotal, dest[idx0][idx1], 0, len2);
                idxTotal += len2;
            }
        }
    }

    /**
     * Unflatten a 1D array into a 3D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @return [len0][len1][len2] destination array
     */
    public static final String[][][] unflatten(final String[] src, final int len0, final int len1, final int len2) {
        final String[][][] dest = new String[len0][len1][len2];
        OneLiners.unflattenInto(src, len0, len1, len2, dest);
        return dest;
    }

    /**
     * Unflattens a arrays to a higher dimension.
     *
     * @param flat The matrix data, stored row by row
     * @param n    the number of rows (shallowest dimension)
     * @param m    the number of columns (deepest dimension / fastest changing
     *             index)
     * @return the matrix mat[n][m]
     */
    public static <T> T[][] unflatten(T[] flat, int n, int m) {
        @SuppressWarnings("unchecked")
        T[][] ret = (T[][]) Array.newInstance(flat.getClass().getComponentType(), n, m);
        for (int i = 0; i < n; i++) {
            System.arraycopy(flat, i*m, ret[i], 0, m);
        }
        return ret;
    }

    // 4d

    /**
     * Unflatten a 1D array into a given 4D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2 * len3] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param len3 fourth dimension
     * @param dest [len0][len1][len2][len3] destination array
     */
    public static void unflattenInto(final int[] src, final int len0, final int len1, final int len2, final int len3, final int[][][][] dest) {
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                for (int idx2 = 0; idx2 < len2; ++idx2) {
                    System.arraycopy(src, idxTotal, dest[idx0][idx1][idx2], 0, len3);
                    idxTotal += len3;
                }
            }
        }
    }

    /**
     * Unflatten a 1D array into a 4D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2 * len3] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param len3 fourth dimension
     * @return [len0][len1][len2][len3] destination array
     */
    public static final int[][][][] unflatten(final int[] src, final int len0, final int len1, final int len2, final int len3) {
        final int[][][][] dest = new int[len0][len1][len2][len3];
        OneLiners.unflattenInto(src, len0, len1, len2, len3, dest);
        return dest;
    }

    /**
     * Unflatten a 1D array into a given 4D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2 * len3] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param len3 fourth dimension
     * @param dest [len0][len1][len2][len3] destination array
     */
    public static void unflattenInto(final long[] src, final int len0, final int len1, final int len2, final int len3, final long[][][][] dest) {
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                for (int idx2 = 0; idx2 < len2; ++idx2) {
                    System.arraycopy(src, idxTotal, dest[idx0][idx1][idx2], 0, len3);
                    idxTotal += len3;
                }
            }
        }
    }

    /**
     * Unflatten a 1D array into a 4D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2 * len3] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param len3 fourth dimension
     * @return [len0][len1][len2][len3] destination array
     */
    public static final long[][][][] unflatten(final long[] src, final int len0, final int len1, final int len2, final int len3) {
        final long[][][][] dest = new long[len0][len1][len2][len3];
        OneLiners.unflattenInto(src, len0, len1, len2, len3, dest);
        return dest;
    }

    /**
     * Unflatten a 1D array into a given 4D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2 * len3] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param len3 fourth dimension
     * @param dest [len0][len1][len2][len3] destination array
     */
    public static void unflattenInto(final double[] src, final int len0, final int len1, final int len2, final int len3, final double[][][][] dest) {
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                for (int idx2 = 0; idx2 < len2; ++idx2) {
                    System.arraycopy(src, idxTotal, dest[idx0][idx1][idx2], 0, len3);
                    idxTotal += len3;
                }
            }
        }
    }

    /**
     * Unflatten a 1D array into a 4D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2 * len3] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param len3 fourth dimension
     * @return [len0][len1][len2][len3] destination array
     */
    public static final double[][][][] unflatten(final double[] src, final int len0, final int len1, final int len2, final int len3) {
        final double[][][][] dest = new double[len0][len1][len2][len3];
        OneLiners.unflattenInto(src, len0, len1, len2, len3, dest);
        return dest;
    }

    /**
     * Unflatten a 1D array into a given 4D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2 * len3] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param len3 fourth dimension
     * @param dest [len0][len1][len2][len3] destination array
     */
    public static void unflattenInto(final String[] src, final int len0, final int len1, final int len2, final int len3, final String[][][][] dest) {
        int idxTotal = 0;
        for (int idx0 = 0; idx0 < len0; ++idx0) {
            for (int idx1 = 0; idx1 < len1; ++idx1) {
                for (int idx2 = 0; idx2 < len2; ++idx2) {
                    System.arraycopy(src, idxTotal, dest[idx0][idx1][idx2], 0, len3);
                    idxTotal += len3;
                }
            }
        }
    }

    /**
     * Unflatten a 1D array into a 4D array.
     * It is assumed that the destination array is rectangular.
     *
     * @param src  [len0 * len1 * len2 * len3] source array
     * @param len0 first  dimension
     * @param len1 second dimension
     * @param len2 third  dimension
     * @param len3 fourth dimension
     * @return [len0][len1][len2][len3] destination array
     */
    public static final String[][][][] unflatten(final String[] src, final int len0, final int len1, final int len2, final int len3) {
        final String[][][][] dest = new String[len0][len1][len2][len3];
        OneLiners.unflattenInto(src, len0, len1, len2, len3, dest);
        return dest;
    }














    /** Appends arrayTwo onto the end of arrayOne
     * @param arrayOne - array to be extended by adding arrayTwo to its end
     * @param arrayTwo - the array concatenated onto the end of arrayOne
     * @return the concatenated array
     */
    public static final int[] appendArray(int[] arrayOne, int[] arrayTwo) {
        if (arrayOne == null) arrayOne = new int[0];

        int n = arrayOne.length;
        int m = arrayTwo.length;
        int[] ans = new int[m+n];
        System.arraycopy(arrayOne, 0, ans, 0, n);
        System.arraycopy(arrayTwo, 0, ans, n, m);

        return ans;
    }

    /**
     * Appends arrayTwo onto the end of arrayOne
     * @param arrayOne - array to be extended by adding arrayTwo to its end
     * @param arrayTwo - the array concatenated onto the end of arrayOne
     * @return the concatenated array
     */
    public static final long[] appendArray(long[] arrayOne, long[] arrayTwo) {
        if (arrayOne == null) arrayOne = new long[0];

        int n = arrayOne.length;
        int m = arrayTwo.length;
        long[] ans = new long[m+n];
        System.arraycopy(arrayOne, 0, ans, 0, n);
        System.arraycopy(arrayTwo, 0, ans, n, m);

        return ans;
    }

    /** Appends arrayTwo onto the end of arrayOne
     * @param arrayOne - array to be extended by adding arrayTwo to its end
     * @param arrayTwo - the array concatenated onto the end of arrayOne
     * @return the concatenated array
     */
    public static final double[][] appendArray(double[][] arrayOne, double[][] arrayTwo) {
        if (arrayOne == null)
            arrayOne = new double[0][0];

        int n = arrayOne.length;
        int m = arrayTwo.length;
        double[][] ans = new double[m+n][];
        System.arraycopy(arrayOne, 0, ans, 0, n);
        System.arraycopy(arrayTwo, 0, ans, n, m);

        return ans;
    }


    /** Appends arrayTwo onto the end of arrayOne
     * @param arrayOne - array to be extended by adding arrayTwo to its end
     * @param arrayTwo - the array concatenated onto the end of arrayOne
     * @return the concatenated array
     */
    public static final double[] appendArray(double[] arrayOne, double[] arrayTwo) {
        if (arrayOne == null)
            arrayOne = new double[0];

        int n = arrayOne.length;
        int m = arrayTwo.length;
        double[] ans = new double[m+n];
        System.arraycopy(arrayOne, 0, ans, 0, n);
        System.arraycopy(arrayTwo, 0, ans, n, m);

        return ans;
    }


    /**
     * @deprecated Use {@link Files#copy(Path, Path, java.nio.file.CopyOption...)}
     */
    @Deprecated
    public static final void copyFile(String sourceName, String destName) throws IOException {
        copyFile(new File(sourceName), new File(destName));
    }

    /**
     *
     * @deprecated Use {@link Files#copy(Path, Path, java.nio.file.CopyOption...)}
     */
    @Deprecated
    public static final void copyFile(File sourceFile, File destFile) throws IOException {
        Files.copy(sourceFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

        if (!destFile.exists()) destFile.createNewFile();

        FileChannel source = null, destination = null;
        try (FileInputStream inputStream = new FileInputStream(sourceFile)) {
            source = inputStream.getChannel();
            try (FileOutputStream outputStream = new FileOutputStream(destFile)) {
                destination = outputStream.getChannel();
                destination.transferFrom(source, 0, source.size());
            }
        }
    }

    /**
     * Copies a file or directory recursively.
     *
     * @param src the source
     * @param dest the destination
     */
    public static final void mustCopy(File src, File dest) {
        try {
            copy(src, dest);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copies a file or directory recursively.
     *
     * @param src the source
     * @param dest the destination
     * @throws IOException if an I/O error occurs
     */
    public static final void copy(File src, File dest) throws IOException {
        if (src.isDirectory()) {
            copyDirectory(src, dest);
        } else {
            copyFileNew(src, dest);
        }
    }

    /**
     * Copies a directory recursively.
     *
     * @param src the source
     * @param dest the destination
     * @throws IOException if an I/O error occurs
     */
    private static final void copyDirectory(File src, File dest) throws IOException {
        if (!dest.exists()) dest.mkdir();
        for (String f : src.list()) {
            copy(new File(src, f), new File(dest, f));
        }
    }

    /**
     * Copies a file to the destination.
     *
     * @param src the source
     * @param dest the destination
     * @throws IOException if an I/O error occurs
     */
    private static final void copyFileNew(File src, File dest) throws IOException {
        if (dest.isDirectory()) {
            Files.copy(src.toPath(), Paths.get(dest.getParent(), src.getName()), StandardCopyOption.REPLACE_EXISTING);
        }
        Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
    }

    /**
     * Java doesn't have the error function, which is annoying.
     * This was copied from somewhere. I can't remember where, but it works.
     *
     * Fractional error in math formula less than 1.2 * 10 ^ -7.
     * although subject to catastrophic cancellation when z in very close to 0
     *
     * @param z
     * @return
     */
    public static final double erf(final double z) {
        final double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

        // use Horner's method
        double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                t * ( 1.00002368 +
                        t * ( 0.37409196 +
                                t * ( 0.09678418 +
                                        t * (-0.18628806 +
                                                t * ( 0.27886807 +
                                                        t * (-1.13520398 +
                                                                t * ( 1.48851587 +
                                                                        t * (-0.82215223 +
                                                                                t * ( 0.17087277))))))))));
        if (z >= 0) return  ans;
        else        return -ans;
    }

    public static final int indexOfStringInList(String[] list, String string) {
        Objects.requireNonNull(list);

        return Arrays.asList(list).indexOf(string);
    }

    /** NB: I've not yet checked atan2() for quadrants etc. correctness
      (R,phi,Z) <-- (x,y,z) = (R cos phi, R sin phi, Z)
      if y == null, (x, ,z) = (R, , z) and assume all R > 0 (not checked) */
    public static final double[][] XYZToRPhiZ(double[][] XYZ){
        // If y is null (shorthand for all 0), than we can have just R=X, Z=Z and hope all R > 0
        if(XYZ[1] == null) {
            return new double[][]{ XYZ[0], null, XYZ[2] };
        }
        int n = XYZ[0].length;
        double[] R = new double[n];
        double[] phi = new double[n];
        for (int i = 0; i < n; i++) {
            phi[i] = Math.atan2(XYZ[1][i], XYZ[0][i]);
            R[i] = Math.sqrt(XYZ[0][i]*XYZ[0][i] + XYZ[1][i]*XYZ[1][i]);
        }
        return new double[][]{ R, phi, XYZ[2] };
    }


    /** NB: I've not yet checked atan2() for quadrants etc. correctness
      (R,phi,Z) <-- (x,y,z) = (R cos phi, R sin phi, Z)
      if y == null, (x, ,z) = (R, , z) and assume all R > 0 (not checked) */
    public static final double[] XYZToRPhiZSingle(double[] XYZ){
        double[][] ret = XYZToRPhiZ(new double[][] { {XYZ[0]}, {XYZ[1]}, {XYZ[2]} });
        return new double[] { ret[0][0], ret[1][0], ret[2][0] };
    }

    /** (R,phi,Z) --> (x,y,z) = (R cos phi, R sin phi, Z)
     * if y == null, (x, ,z) = (R, , z) */
    public static final double[][] RPhiZToXYZ(double[][] RPhiZ){
        // If phi is null (shorthand for all 0), than we can have just X=R, Z=Z
        if (RPhiZ[1] == null) {
            return new double[][]{ RPhiZ[0], null, RPhiZ[2] };
        }
        int n = RPhiZ[0].length;
        double[] X = new double[n];
        double[] Y = new double[n];
        for(int i=0; i < n; i++){
            X[i] = RPhiZ[0][i] * Math.cos(RPhiZ[1][i]);
            Y[i] = RPhiZ[0][i] * Math.sin(RPhiZ[1][i]);
        }
        return new double[][]{ X, Y, RPhiZ[2] };
    }

    /** (R,phi,Z) --> (x,y,z) = (R cos phi, R sin phi, Z)
     * if y == null, (x, ,z) = (R, , z) */
    public static final double[] RPhiZToXYZSingle(double[] RPhiZ){
        double[][] ret = RPhiZToXYZ(new double[][] { {RPhiZ[0]}, {RPhiZ[1]}, {RPhiZ[2]} });
        return new double[] { ret[0][0], ret[1][0], ret[2][0] };
    }

    public static final double[][] addColumRowHeaders(double[][] in, double rowHead[], double colHead[]) {
        int nRows = in.length, nCols=in[0].length;

        double[][] out = new double[nRows+1][nCols+1];
        for (int j = 0; j < nCols; j++) {
            out[0][j + 1] = colHead[j];
        }

        for (int i = 0; i < nRows; i++) {
            out[i + 1][0] = rowHead[i];
            for (int j = 0; j < nCols; j++) {
                out[i + 1][j + 1] = in[i][j];
            }
        }

        return out;
    }

    public static final double[][] RPhiZVectorToXYZVector(double[][] posRPhiZ, double[][] vecRPhiZ){

        //otherwise we need to project accordingly
        double[][] vecXYZ = {
                new double[posRPhiZ[0].length],
                new double[posRPhiZ[0].length],
                vecRPhiZ[2]    /* Bz=Bz always */
        };

        for (int i = 0; i < posRPhiZ[0].length; i++) {
            double sinPhi = Math.sin(posRPhiZ[1][i]);
            double cosPhi = Math.cos(posRPhiZ[1][i]);
            vecXYZ[0][i] = vecRPhiZ[0][i] * cosPhi - vecRPhiZ[1][i] * sinPhi;
            vecXYZ[1][i] = vecRPhiZ[1][i] * cosPhi + vecRPhiZ[0][i] * sinPhi;
        }

        return vecXYZ;
    }

    public static final double[] RPhiZVectorToXYZVectorSingle(double[] posRPhiZ, double[] vecRPhiZ) {
        double[][] posRPhiZm = { { posRPhiZ[0] }, { posRPhiZ[1] }, { posRPhiZ[2]} };
        double[][] vecRPhiZm = { { vecRPhiZ[0] }, { vecRPhiZ[1] }, { vecRPhiZ[2]} };

        double[][] ret = RPhiZVectorToXYZVector(posRPhiZm, vecRPhiZm);

        return new double[] { ret[0][0], ret[1][0], ret[2][0] };
    }

    public static double[][] XYZVectorToRPZVector(double[] phiGrid, double[][] vecXYZ) {

        double[][] vecRPhiZ = new double[][]{
            new double[phiGrid.length],
            new double[phiGrid.length],
            vecXYZ[2]    /* Bz=Bz always */        };

            for(int i=0; i < phiGrid.length; i++){
                //TODO: trig avoidance
                double sinPhi = Math.sin(phiGrid[i]);
                double cosPhi = Math.cos(phiGrid[i]);
                vecRPhiZ[0][i] = vecXYZ[0][i] * cosPhi + vecXYZ[1][i] * sinPhi;
                vecRPhiZ[1][i] = vecXYZ[1][i] * cosPhi - vecXYZ[0][i] * sinPhi;
            }

            return vecRPhiZ;
    }

    public static double[][] XYZVectorToRPZVector(double[][] posXYZ, double[][] vecXYZ) {

        double[][] vecRPhiZ = new double[][]{     new double[posXYZ[0].length],
            new double[posXYZ[0].length],
            vecXYZ[2]    /* Bz=Bz always */        };

            for(int i=0; i < posXYZ[0].length; i++){
                //TODO: trig avoidance
                double phi = Math.atan2(posXYZ[1][i], posXYZ[0][i]);
                double sinPhi = Math.sin(phi);
                double cosPhi = Math.cos(phi);
                vecRPhiZ[0][i] = vecXYZ[0][i] * cosPhi + vecXYZ[1][i] * sinPhi;
                vecRPhiZ[1][i] = vecXYZ[1][i] * cosPhi - vecXYZ[0][i] * sinPhi;
            }

            return vecRPhiZ;
    }

    public static final double[] XYZVectorToRPZVectorSingle(double[] posXYZ, double[] vecXYZ) {
        double[][] posXYZm = { { posXYZ[0] }, { posXYZ[1] }, { posXYZ[2]} };
        double[][] vecXYZm = { { vecXYZ[0] }, { vecXYZ[1] }, { vecXYZ[2]} };

        double[][] ret = XYZVectorToRPZVector(posXYZm, vecXYZm);

        return new double[] { ret[0][0], ret[1][0], ret[2][0] };
    }


    /** (equ from wikipedia/rotation matrix) */
    public static final double[] rotateVectorAroundAxis(double angle, double[] rotVec, double[] sourceVec) {
        double cosAng = Math.cos(angle);
        double sinAng = Math.sin(angle);

        return new double[] {
                sourceVec[0] * (cosAng + rotVec[0]*rotVec[0]*(1.0 - cosAng) ) +
                sourceVec[1] * (rotVec[0]*rotVec[1]*(1.0 - cosAng) - rotVec[2]*sinAng) +
                sourceVec[2] * (rotVec[0]*rotVec[2]*(1.0 - cosAng) + rotVec[1]*sinAng),

                sourceVec[0] * (rotVec[1]*rotVec[0]*(1.0 - cosAng) + rotVec[2]*sinAng) +
                sourceVec[1] * (cosAng + rotVec[1]*rotVec[1]*(1.0 - cosAng) ) +
                sourceVec[2] * (rotVec[1]*rotVec[2]*(1.0 - cosAng) - rotVec[0]*sinAng),

                sourceVec[0] * (rotVec[2]*rotVec[0]*(1.0 - cosAng) - rotVec[1]*sinAng) +
                sourceVec[1] * (rotVec[2]*rotVec[1]*(1.0 - cosAng) + rotVec[0]*sinAng) +
                sourceVec[2] * (cosAng + rotVec[2]*rotVec[2]*(1.0 - cosAng) ),
        };
    }

    public static final double[] rotateVectorAroundAxis(double angle, double rotVecX, double rotVecY, double rotVecZ,
            double sourceVecX, double sourceVecY, double sourceVecZ, double[] ret) {

        double cosAng = Math.cos(angle);
        double sinAng = Math.sin(angle);

        ret[0] =     sourceVecX * (cosAng + rotVecX*rotVecX*(1.0 - cosAng) ) +
                sourceVecY * (rotVecX*rotVecY*(1.0 - cosAng) - rotVecZ*sinAng) +
                sourceVecZ * (rotVecX*rotVecZ*(1.0 - cosAng) + rotVecY*sinAng);

        ret[1] =     sourceVecX * (rotVecY*rotVecX*(1.0 - cosAng) + rotVecZ*sinAng) +
                sourceVecY * (cosAng + rotVecY*rotVecY*(1.0 - cosAng) ) +
                sourceVecZ * (rotVecY*rotVecZ*(1.0 - cosAng) - rotVecX*sinAng);

        ret[2] =     sourceVecX * (rotVecZ*rotVecX*(1.0 - cosAng) - rotVecY*sinAng) +
                sourceVecY * (rotVecZ*rotVecY*(1.0 - cosAng) + rotVecX*sinAng) +
                sourceVecZ * (cosAng + rotVecZ*rotVecZ*(1.0 - cosAng) );
        return ret;
    }

    /** Converts all any of %;?/:#&=+$, ><~ to % and the hex char code like in a URL
     * This is probably a little overkill, but meh */
    public static String sanitizeFilename(String name) {
        return name.replaceAll("\\%", "%25")
                .replaceAll("\\;", "%3B")
                .replaceAll("\\?", "%3F")
                .replaceAll("\\/", "%2F")
                .replaceAll("\\\\", "%92")
                .replaceAll("\\:", "%3A")
                .replaceAll("\\#", "%23")
                .replaceAll("\\&", "%26")
                .replaceAll("\\=", "%3D")
                .replaceAll("\\+", "%2B")
                .replaceAll("\\*", "%2A")
                .replaceAll("\\$", "%24")
                .replaceAll("\\,", "%2C")
                .replaceAll("\\ ", "%20")
                .replaceAll("\\<", "%3C")
                .replaceAll("\\>", "%3E")
                .replaceAll("\\~", "%7E");
        // ... yes, there's probably a more elegant way of doing that
        //and I escaped everything to be sure
    }

    public static String desanitizeFilename(String name) {
        StringBuffer sb = new StringBuffer();
        Matcher m = Pattern.compile("%([0-9A-Fa-f][0-9A-Fa-f])").matcher(name);
        while (m.find()) {
            String x = m.group(1);
            m.appendReplacement(sb, String.valueOf(Character.toChars(Integer.parseInt(x,16))));
        }
        m.appendTail(sb);

        return sb.toString();
    }

    /**
     * Generates flat arrays xx[], yy[] for all points in the grid (x[], y[])
     * The x coordinate changes fastest.
     *
     * @param x0    Min value of x coord
     * @param x1    Max value of x coord
     * @param nX    Number of x coord points
     * @param y0    Min value of y coord
     * @param y1    Max value of y coord
     * @param nY    Number of y coord points
     * @return    double[][]{ x[nX*nY], y[nX*nY] }
     */
    public static double[][] flatGrid(double[] x, double[] y) {
        double[] xx = new double[x.length*y.length];
        double[] yy = new double[x.length*y.length];

        for (int iY = 0; iY < y.length; iY++) {
            for (int iX = 0; iX < x.length; iX++) {
                xx[iY * x.length + iX] = x[iX];
                yy[iY * x.length + iX] = y[iY];
            }
        }

        return new double[][]{ xx, yy };
    }

    /** Generates flat arrays x[], y[] for all points in the grid nX x nY grid on (x0,y0) - (x1-y1)
     * The x coordinate changes fastest.
     *
     * @param x0    Min value of x coord
     * @param x1    Max value of x coord
     * @param nX    Number of x coord points
     * @param y0    Min value of y coord
     * @param y1    Max value of y coord
     * @param nY    Number of y coord points
     * @return    double[][]{ x[nX*nY], y[nX*nY] }
     */
    public static double[][] flatGrid(double x0, double x1, int nX, double y0, double y1, int nY) {
        double[] xx = new double[nX * nY];
        double[] yy = new double[nX * nY];
        double dx = (x1 - x0) / (nX - 1.0);
        double dy = (y1 - y0) / (nY - 1.0);

        for (int iY = 0; iY < nY; iY++) {
            for (int iX = 0; iX < nX; iX++) {
                xx[iY * nX + iX] = x0 + iX * dx;
                yy[iY * nX + iX] = y0 + iY * dy;
            }
        }

        return new double[][]{ xx, yy };
    }

    /** See OneLiners.fixWeirdContainers(Object obj, boolean forcePrimitive) */
    public static final Object fixWeirdContainers(Object obj){ return fixWeirdContainers(obj, false); }

    /** Rebuild n-dimension object as a proper array of numeric types.
     * Lists and Object[] are all converted to the base type.
     *
     * Unless forcePrimitiveTypes is true, it doesn't necessarily convert
     * to primitive types.
     *   i.e. the result might be a int[][][] or an Integer[][][]
     *
     * This will try to cast things where possible and only fills
     * and new array when necessary.
     *
     * @param obj
     * @param forcePrimitiveTypes
     * @return
     */
    public static final Object fixWeirdContainers(Object obj, boolean forcePrimitive){
        return fixWeirdContainersRecurse(obj, forcePrimitive, 0);
    }

    private static final Object fixWeirdContainersRecurse(Object obj, boolean forcePrimitive, int depth){
        if (depth > 1000)
            throw new RuntimeException("Trying to de-List a rank > 1000 object. You probably have a ciruclar links!");

        if(obj instanceof List<?>){
            List<?> oList = (List<?>)obj;

            Object oArrOut = null; //don't know type yet
            int i=0;
            for(Object o : oList){
                Object entry = fixWeirdContainersRecurse(o, forcePrimitive, depth+1);

                if(oArrOut == null){ //we can now type it correctly
                    Class<?> entryClass = entry.getClass();
                    if(forcePrimitive && !entryClass.isArray() &&  !entryClass.isPrimitive())
                        entryClass = getPrimitiveType(entryClass);
                    oArrOut = Array.newInstance(entryClass, oList.size());
                }

                Array.set(oArrOut, i, entry);
                i++;
            }
            return oArrOut;

        }else if(obj instanceof Object[]){
            Object[] oArrIn = (Object[])obj;
            Object oArrOut = null;

            for(int i=0; i < oArrIn.length; i++){
                Object entry = fixWeirdContainersRecurse(oArrIn[i], forcePrimitive, depth+1);
                if(oArrOut == null){
                    if(finalTypesMatch(oArrIn, entry) && !forcePrimitive){
                        oArrOut = oArrIn; //the array is OK as it is
                    }else{
                        Class<?> entryClass = entry.getClass();
                        if(forcePrimitive && !entryClass.isArray() &&  !entryClass.isPrimitive())
                            entryClass = getPrimitiveType(entryClass);
                        oArrOut = Array.newInstance(entryClass, oArrIn.length);
                    }
                }
                Array.set(oArrOut, i, entry);
            }
            return oArrOut;
        }

        return obj;
    }

    private static final Class<?> getPrimitiveType(Class<?> objType){
        if (objType == Byte.class) {
            return byte.class;
        } else if (objType == Character.class) {
            return char.class;
        } else if (objType == Short.class) {
            return short.class;
        } else if (objType == Integer.class) {
            return int.class;
        } else if (objType == Long.class) {
            return long.class;
        } else if (objType == Float.class) {
            return float.class;
        } else if (objType == Double.class) {
            return double.class;
        } else if (objType == Boolean.class) {
            return boolean.class;
        } else if (objType == String.class) {
            return String.class; // can't do anything with this one
        } else {
            throw new IllegalArgumentException(
                    "fixWeirdContainersRecurse() called with forcePrimitive=true, but found type '" + objType.getName()
                    + "' with no compatible primitive");
        }
    }

    /** Checks that the actual 'type' of the two objects match.
     * If the objects are arrays, this refers to the type that this claims
     * to be an array of.
     *
     * This counter-intuitive and actually just plain stupid, Java well and truly messed this stuff up
     *
     * Examples:
     *     new Integer[][][]{ { { 1, 2, 3} } } would be 'java.lang.Integer
     *     new Object[]{ new Integer[]{ 1, 2, 3 } } would be 'java.lang.Object' (i.e. not Integer)
     *  (Object[])(new Integer[]{ 1, 2, 3 }) would be 'java.lang.Integer' despite being viewes as an Object[]
     *  new int[][][] would probably be 'I'
     *  new (Object[][][])(new int[][][]{
     *
     *  The point is that both Integer[] and int[] can be cast to an Object[], but that the result is not the
     *  same things as an actual Object[].
     *
     *  -Actually, the latter statement is not correct. int[] cannot be cast to Object[], only to Object, whereas int[][] can be cast to Object[]
     */
    private static final boolean finalTypesMatch(Object a, Object b){
        //the regex removes all leading '[' and a single leading 'L'
        // and also a single trailing ';'

        String aClassName = a.getClass().getName();
        String aFinalType = aClassName.replaceFirst("^\\[*", "").replaceFirst("^L", "").replaceFirst(";$", "");

        String bClassName = b.getClass().getName();
        String bFinalType = bClassName.replaceFirst("^\\[*", "").replaceFirst("^L", "").replaceFirst(";$", "");

        return bFinalType.equals(aFinalType);

    }

    public static final int mustParseInt(String str){
        try {
            return Integer.parseInt(str);
        } catch (NumberFormatException err) {
            return 0;
        }
    }

    public static final long mustParseLong(String str) {
        try {
            return Long.parseLong(str);
        } catch (NumberFormatException err) {
            return 0;
        }
    }

    public static final double mustParseDouble(String str){
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException err) {
            return 0;
        }
    }

    public static boolean mustParseBoolean(String str) {
        try{
            return (str.startsWith("t") || str.startsWith("T") || str.startsWith("Y") || str.startsWith("y")
                    || Double.parseDouble(str) != 0.0);
        }catch(NumberFormatException err){
            return false;
        }

    }

    public static final double length(double[] x){
        return Math.sqrt(x[0]*x[0] + x[1]*x[1] + x[2]*x[2]);
    }

    /** Renormalise the given vector */
    public static final double[] reNorm(double[] x){
        double sum=0;

        for(int i=0;i<3;i++)
            sum += x[i] * x[i];

        sum = Math.sqrt(sum);

        for(int i=0;i<3;i++)
            x[i] /= sum;

        return x;
    }

    /** Calculates the cross production of A and B*/
    public static final double[] cross(double[] A, double[] B){
        double[] AxB = new double[3];

        AxB[0] =  A[1] * B[2] - A[2] * B[1];
        AxB[1] = -A[0] * B[2] + A[2] * B[0];
        AxB[2] =  A[0] * B[1] - A[1] * B[0];

        return AxB;
    }

    /** Dot product */
    public static final double dot(double[] A, double[] B){
        return A[0]*B[0] + A[1]*B[1] + A[2]*B[2];
    }

    /** @return a - b */
    public static final double[] minus(double[] a, double[] b) {
        return new double[] { a[0] - b[0], a[1] - b[1], a[2] - b[2] };
    }

    /** @return a + b */
    public static final double[] plus(double[] a, double[] b) {
        return new double[] { a[0] + b[0], a[1] + b[1], a[2] + b[2] };
    }

    /** @return a * b */
    public static final double[] mul(double[] a, double b) {
        return new double[] { a[0] * b, a[1] * b, a[2] * b };
    }

    /** @return new Object[]{ double row0[], double col0[], double data[], Double topLeftCornerVal }; */
    public static Object[] splitRowColHeaders(double[][] in) {
        int n = in.length - 1;
        int m = in[0].length - 1;
        double[] row0 = new double[m];
        double[] col0 = new double[n];
        double[][] out = new double[n][m];
        System.arraycopy(in[0], 1, row0, 0, m);
        for(int i=0; i < n; i++){
            col0[i] = in[i+1][0];
            System.arraycopy(in[i+1], 1, out[i], 0, m);
        }
        return new Object[]{ row0, col0, out, in[0][0] };
    }

    public static final int[] booleanToIntArray(boolean[] bool) {
        int[] ret = new int[bool.length];
        for (int i = 0; i < bool.length; i++) {
            ret[i] = bool[i] ? 1 : 0;
        }
        return ret;
    }

    public static final double[] booleanToDoubleArray(boolean[] bool) {
        double[] ret = new double[bool.length];
        for (int i = 0; i < bool.length; i++) {
            ret[i] = bool[i] ? 1 : 0;
        }
        return ret;
    }

    /** @return Index of the string in possible[] that matches text to the most characters */
    public static int findBestMatchingString(String possible[], String text) {
        int bestMatchIdx = -1;
        int bestMatchLen = 0;
        for(int i=0; i < possible.length; i++){
            int matchLen;
            int n = Math.min(text.length(), possible[i].length());
            for(matchLen=0; matchLen < n; matchLen++){ //for each character
                //check if the match fails here
                if(Character.toLowerCase(text.charAt(matchLen)) !=
                        Character.toLowerCase(possible[i].charAt(matchLen))){
                    break;
                }

            }
            if(matchLen > bestMatchLen){
                bestMatchIdx = i;
                bestMatchLen = matchLen;
            }
        }
        return bestMatchIdx;
    }

    /**
     * Converts an angle measured in degrees to an approximately equivalent angle
     * measured in radians. The conversion from degrees to radians is generally
     * inexact.
     *
     * @param deg an angle in degrees
     * @return the measurement of the angle deg in radians.
     * @deprecated Use Math::toRadians
     */
    @Deprecated
    public static final double deg2Rad(double deg){
        return Math.toRadians(deg);
    }

    /**
     * Converts angles measured in degrees to approximately equivalent angles
     * measured in radians. The conversion from degrees to radians is generally
     * inexact.
     *
     * @param deg angles in degrees
     * @return the measurement of the angles deg in radians.
     */
    public static final double[] deg2Rad(double[] deg){
        return DoubleStream.of(deg).map(Math::toRadians).toArray();
    }

    /**
     * Converts angles measured in degrees to approximately equivalent angles
     * measured in radians. The conversion from degrees to radians is generally
     * inexact.
     *
     * @param deg angles in degrees
     * @return the measurement of the angles deg in radians.
     */
    public static final double[][] deg2Rad(double[][] deg) {
        return Stream.of(deg).map(OneLiners::deg2Rad).toArray(double[][]::new);
    }

    public static double[] zeroNegatives(double[] values) {
        return DoubleStream.of(values).map(f -> Math.max(0, f)).toArray();
    }

    public static final <T> boolean[] find(T[] array, T element) {
        boolean[] find = new boolean[array.length];
        for (int i = 0; i < find.length; i++) {
            if(array[i].equals(element)) {
                find[i] = true;
            }
        }
        return find;
    }


    public static final <T> int[] findIndices(T[] array, T element) {
        return where(find(array, element));
    }


    public static double[] stringArrayToDoubleArray(String[] string) {
        Objects.requireNonNull(string);

        return Stream.of(string).mapToDouble(Double::parseDouble).toArray();
    }


    public static String[] getFileNames(String dir) {
        return Stream.of(getFiles(dir)).map(File::getName).toArray(String[]::new);
    }


    public static String[] getFilePaths(String dir) {
        return Stream.of(getFiles(dir)).map(f -> f.getPath() + "/").toArray(String[]::new);
    }

    /**
     * Gets all normal files (i.e. at least non-directory files) in the specified directory.
     *
     * @param dir the folder in which to look for files
     * @return the found normal files
     */
    public static File[] getFiles(String dir) {
        return new File(dir).listFiles(File::isFile);
    }


    public static String[] getSubdirNames(String dir) {
        return Stream.of(getSubdirs(dir)).map(File::getName).toArray(String[]::new);
    }


    public static String[] getSubdirPaths(String dir) {
        return Stream.of(getSubdirs(dir)).map(File::getPath).toArray(String[]::new);
    }


    public static File[] getSubdirs(String dir) {
        return new File(dir).listFiles(File::isDirectory);
    }


    public static <T> T concat(T a, T b) {
        if(a == null) return b;
        if(b == null) return a;

        T ret = (T) Array.newInstance(a.getClass().getComponentType(), Array.getLength(a) + Array.getLength(b));
        int index = 0;
        for (int i = 0; i < Array.getLength(a); i++) {
            Array.set(ret, index++, Array.get(a, i));
        }
        for (int i = 0; i < Array.getLength(b); i++) {
            Array.set(ret, index++, Array.get(b, i));
        }

        return ret;
    }

    /**
     * Extracts the elements in an array based on a logical (boolean) array indicating
     * which elements should be kept.
     * @param <T>
     *
     * @param array The array to extract elements from.
     * @param keep keep[i] == true means the i:th elements is kept.
     * @return A new array with only the chosen elements.
     */
    public static <T> T extractElems(T array, boolean[] keep) {

        int trues = 0;
        for (boolean val : keep) {
            if (val) trues++;
        }

        Class<?> type = getElementType(array.getClass());
        int dim = getArrayDimensions(array.getClass());
        Class<?> componentType = null;

        componentType = OneLiners.getArrayType(type, dim - 1);

        @SuppressWarnings("unchecked")
        T obj = (T) Array.newInstance(componentType, trues);

        int counter = 0;
        for (int i = 0; i < Array.getLength(array); i++) {
            if (keep[i]) {
                Array.set(obj, counter, Array.get(array, i));
                counter++;
            }
        }

        return obj;
    }

    /**
     * Extracts a range of elements (fromIndex:step:toIndex) in an array.
     *
     * @param array The array to extract elements from.
     * @param fromIndex First index in a to keep.
     * @param step The step in the sequence of values to be kept
     * @param toIndex Last index in a to keep.
     * @return A new array containing the elements with original indicies fromIndex:step:toIndex
     *
     */
    public static Object extractElems(Object array, int fromIndex, int step, int toIndex) {
        boolean[] keep = new boolean[Array.getLength(array)];
        for (int i = fromIndex; i <= toIndex; i += step) {
            keep[i] = true;
        }

        return extractElems(array, keep);
    }

    /**
     * Extracts the elements in an array based on an integer array of indices that should be kept.
     *
     * @param array The array to extract elements from.
     * @param keepIndicies An array with the indicies of the elements of the array a that should
     * be kept.
     * @return A new array with only the chosen elements.
     */
    public static Object extractElems(Object array, int[] keepIndicies) {
        boolean[] keep = new boolean[Array.getLength(array)];
        for (int i = 0; i < keepIndicies.length; i++) {
            keep[keepIndicies[i]] = true;
        }

        return extractElems(array, keep);
    }


    /**
     * Clone the non-structured objects, so handles primitives, object type
     * primitives (e.g. Long), Strings, and arbitrary multidimensional arrays of
     * those types. Structured objects will be copied by reference only.
     *
     * @param object the object to clone
     * @return the (shallow if structured objects are included, otherwise deep copied) clone
     * @see #shallowClone(Object)
     */
    public static <T> T cloneNonStructuredObject(T object) {
        if (object == null) {
            return null;
        } else if (!object.getClass().isArray()) {
            return object;
        } else {
            @SuppressWarnings("unchecked")
            T ret = (T) Array.newInstance(object.getClass().getComponentType(), Array.getLength(object));
            for (int i = 0; i < Array.getLength(object); ++i) {
                Array.set(ret, i, cloneNonStructuredObject(Array.get(object, i)));
            }
            return ret;
        }
    }

    /**
     * Clone the non-structured objects, so handles primitives, object type
     * primitives (e.g. Long), Strings, enums, and arbitrary multidimensional arrays
     * of those types. Structured objects will be copied by reference only.
     *
     * @param obj the object to clone
     * @return the (shallow if structured objects are included, otherwise deep
     *         copied) clone
     * @see #cloneNonStructuredObject(Object)
     */
    public static <T> T shallowClone(T obj) {
        return (T) cloneNonStructuredObject(obj);
    }


    public static Object castToPrimitiveTypeArray(Object object, Class<?> componentType) throws ClassNotFoundException {
        return castToPrimitiveType(object, getArrayType(componentType, getArrayDimensions(object.getClass())));
    }


    public static Object castToPrimitiveType(Object object, Class<?> type) {
        if (!type.isArray()) {
            if (type.equals(double.class)) {
                return Double.parseDouble(object.toString());
            } else if (type.equals(float.class)) {
                return Float.parseFloat(object.toString());
            } else if (type.equals(long.class)) {
                return Long.parseLong(object.toString().split("\\.")[0]);
            } else if (type.equals(int.class)) {
                return Integer.parseInt(object.toString().split("\\.")[0]);
            } else if (type.equals(short.class)) {
                return Short.parseShort(object.toString().split("\\.")[0]);
            } else if (type.equals(byte.class)) {
                return Byte.parseByte(object.toString().split("\\.")[0]);
            } else if (type.equals(boolean.class)) {
                return Boolean.parseBoolean(object.toString());
            } else if (type.equals(char.class)) {
                return object.toString().charAt(0);
            } else if (type.equals(String.class)) {
                return object.toString();
            } else {
                throw new RuntimeException(String.format("type to cast(%s) is neither primitive nor string", type));
            }
        } else {
            Object ret = Array.newInstance(type.getComponentType(), Array.getLength(object));
            for (int i = 0; i < Array.getLength(object); ++i) {
                Array.set(ret, i, castToPrimitiveType(Array.get(object, i), type.getComponentType()));
            }
            return ret;
        }
    }

    /**
     * Gets the array dimensions, i.e. e.g. the double[][] class will yield 2.
     *
     * @param type the class to check
     * @return the dimensionality of the type
     */
    public static int getArrayDimensions(Class<?> type) {
        if (type.getComponentType() == null) {
            return 0;
        } else {
            return getArrayDimensions(type.getComponentType()) + 1;
        }
    }

    /**
     * Gets the sizes of the array dimensions of the given Object.
     *
     * @param array the array
     * @return the sizes of the array dimensions
     */
    public static int[] getArraySizes(Object array) {
        if (array == null) return new int[0];
        int rank = OneLiners.getArrayDimensions(array.getClass());
        int[] ret = new int[rank];
        Object iarray = array;
        for (int i = 0; i < rank; ++i) {
            ret[i] = Array.getLength(iarray);
            if (ret[i] != 0) iarray = Array.get(iarray, 0);
        }
        return ret;
    }

    /** Returns the class of an element of an array of the given type.
     * e.g. getElementType(double[][].class) = double.class
     */
    public static Class<?> getElementType(Class<?> type) {
        if (type.getComponentType() == null) {
            return type;
        } else {
            return getElementType(type.getComponentType());
        }
    }

    /**
     * Returns the class of an array of the elements of the given type. e.g.
     * getArrayType(double.class, 1) = double[].class or
     * getArrayType(String[].class, 1) = String[][].class
     *
     * @param elementType the element type of the array class to be created
     * @param dimension   the dimension
     * @return the class of the array
     */
    public static Class<?> getArrayType(Class<?> elementType, int dimension) {
        if (dimension == 0) {
            return elementType;
        }
        return Array.newInstance(elementType, fillArray(0, dimension)).getClass();
    }

    /**
     * Converts e.g. [J to long[]. Furthermore, DataTable$Bla gets converted to Bla.
     *
     * @param str the Java type name
     * @return the human readable type name
     */
    public static final String simpleTypeName(String str) {
        int dim = (int) str.chars().filter(c -> c == '[').count();

        // no anonymous things
        String[] nonAnonymous = str.split("\\$");

        String ret = nonAnonymous[nonAnonymous.length - 1].replace("[", "").replace(";", "");
        if      ("B".equals(ret)) ret = "byte";
        else if ("C".equals(ret)) ret = "char";
        else if ("D".equals(ret)) ret = "double";
        else if ("F".equals(ret)) ret = "float";
        else if ("I".equals(ret)) ret = "int";
        else if ("J".equals(ret)) ret = "long";
        else if ("S".equals(ret)) ret = "short";
        else if ("Z".equals(ret)) ret = "boolean";
        else if (dim > 0
                && !"byte".equals(ret)
                && !"char".equals(ret)
                && !"double".equals(ret)
                && !"float".equals(ret)
                && !"int".equals(ret)
                && !"long".equals(ret)
                && !"short".equals(ret)
                && !"boolean".equals(ret))
            ret = ret.substring(1);

        return ret + "[]".repeat(dim);
    }

    /** Sets a given element of an nD array */
    public static void setArrayElement(Object array, Object element, int... indices) {
        if(indices.length == 1) {
            Array.set(array, indices[0], element);
        } else {
            int[] subindices = new int[indices.length-1];
            for(int i=0;i<subindices.length;++i) {
                subindices[i] = indices[i+1];
            }

            Object subarray = Array.get(array, indices[0]);
            setArrayElement(subarray, element, subindices);
        }
    }


    /** Sets a series of elements of an nD array, given by the series of indices
     * @param array nD array to set
     * @param elements Series of elements to be put into array
     * @param indices Series of indices of where to put the elements [elementNum][x/y...]
     */
    public static void setArrayElements(Object array, Object[] elements, int[][] indices) {
        if(elements.length != indices.length) {
            throw new RuntimeException(String.format("the dimensions should be same, elements (%s), indices (%s)", elements.length, indices.length));
        }
        for(int i=0;i<elements.length;++i) {
            setArrayElement(array, elements[i], indices[i]);
        }
    }


    public static void TextToFile(String fileName, String[] text) {
        OneLiners.mkdir(Paths.get(fileName).getParent());

        try (FileOutputStream fOut = new FileOutputStream(fileName)) {
            fOut.write(String.join("\n", text).getBytes());
        } catch (IOException err) {
            throw new UncheckedIOException(err);
        }

    }

    public static final double[][] reshape(double[] array, int numElems0, int numElems1) {
        if(array.length != numElems0*numElems1) throw new RuntimeException("Error using reshape! "
                + "the number of elements must not change from " + array.length + " to " + numElems0 + "*" + numElems1);

        double[][] ret = new double[numElems0][numElems1];
        for(int i=0;i<ret.length;++i) {
            for(int j=0;j<ret[0].length;++j) {
                ret[i][j] = array[i+j*numElems0];
            }
        }

        return ret;
    }

    /**
     * Reshape an array to 3D array. Example:
     ```java
      double[] array = new double[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12 };
      double[][][] ret = reshape(array, 2, 2, 3);


      ret[:][:][0] = { {1, 3},
                          {2, 4} }


      ret[:][:][1] = { {5, 6},
                          {7, 8} }
      ```
     * @param array the array to reshape
     * @param numElems0 the number of elements for 0th dimension
     * @param numElems1 the number of elements for 1st dimension
     * @param numElems2 the number of elements for 2nd dimension
     * @return the 3D array contains all the elements of the input array.
     */
    public static final double[][][] reshape(double[] array, int numElems0, int numElems1, int numElems2) {
        if(array.length != numElems0*numElems1*numElems2) throw new RuntimeException("Error using reshape! "
                + "the number of elements must not change.");

        double[][][] ret = new double[numElems0][numElems1][numElems2];
        for(int i=0;i<ret.length;++i) {
            for(int j=0;j<ret[0].length;++j) {
                for(int k=0;k<ret[0][0].length;++k) {
                    ret[i][j][k] = array[i+j*numElems0+k*numElems0*numElems1];
                }
            }
        }

        return ret;
    }

    /**
     * Convert pixels to wavelength.
     *
     * @param numChannels the number of pixels
     * @param numPixels the number of channels
     * @param dispersions the dispersions in unit nanometers per one pixel
     * @param pixelOfLine the pixel of calibration line
     * @param wavelengthOfLine the wavelength of calibration line [nm]
     * @return ret[channel][pixel] is the wavelength [nm]
     */
    public static final double[][] pixelToWavelength(int numChannels, int numPixels, double[] dispersions,
            double[] pixelOfLine, double wavelengthOfLine) {
        double[][] ret = new double[numChannels][numPixels];

        for(int i=0;i<ret.length;++i) {
            for(int j=0;j<ret[0].length;++j) {
                ret[i][j] = dispersions[i]*(j+1-pixelOfLine[i])+wavelengthOfLine;
            }
        }

        return ret;
    }


    public static final double[][] convolveDelta1D(double[] delta, double[][] convol) {
        double[][] convolDelta = new double[convol.length][convol[0].length];

        for(int j=0;j<convol[0].length;++j) {
            convolDelta[0][j] = convol[0][j]+delta[0];
            convolDelta[1][j] = convol[1][j]*delta[1];
        }
        return convolDelta;
    }


    public static final double[][] findConvolveDelta1D(double[][][] convolDelta, double[] delta) {
        double[][][] convolSet = new double[convolDelta.length][convolDelta[0].length][convolDelta[0][0].length];
        double[][] convol = new double[convolDelta[0].length][convolDelta[0][0].length];

        for(int j=0;j<convolDelta[0][0].length;++j) {
            for(int i=0;i<convolDelta.length;++i) {
                convolSet[i][0][j] = convolDelta[i][0][j]-delta[0];
                convolSet[i][1][j] = convolDelta[i][1][j]/delta[1];
                convol[0][j] += convolSet[i][0][j];
                convol[1][j] += convolSet[i][1][j];
            }
            convol[0][j] /= convolDelta.length;
            convol[1][j] /= convolDelta.length;
        }
        return convol;
    }


    public static double[][] meshGrid(double[]... values) {
        if(values.length == 1) {
            return transpose(values);
        } else {
            double[][] subvalues = new double[values.length-1][];
            for(int i=0;i<subvalues.length;++i) {
                subvalues[i] = values[i+1];
            }
            double[][] subgrid = meshGrid(subvalues);

            List<double[]> ret = new LinkedList<>();
            for(int i=0;i<values[0].length;++i) {
                for(int j=0;j<subgrid.length;++j) {
                    double[] array = new double[subgrid[0].length+1];
                    array[0] = values[0][i];
                    for(int k=0;k<subgrid[0].length;++k) {
                        array[k+1] = subgrid[j][k];
                    }
                    ret.add(array);
                }
            }
            return ret.toArray(new double[ret.size()][]);
        }

    }


    public static int[][] meshGrid(int[]... values) {
        if(values.length == 1) {
            return transpose(values);
        } else {
            int[][] subvalues = new int[values.length-1][];
            for(int i=0;i<subvalues.length;++i) {
                subvalues[i] = values[i+1];
            }
            int[][] subgrid = meshGrid(subvalues);

            List<int[]> ret = new LinkedList<>();
            for(int i=0;i<values[0].length;++i) {
                for(int j=0;j<subgrid.length;++j) {
                    int[] array = new int[subgrid[0].length+1];
                    array[0] = values[0][i];
                    for(int k=0;k<subgrid[0].length;++k) {
                        array[k+1] = subgrid[j][k];
                    }
                    ret.add(array);
                }
            }
            return ret.toArray(new int[ret.size()][]);
        }

    }


    public static final int[] linSpaceInt(int x0, int x1, int dx) {
        return IntStream.iterate(x0, x -> x + dx)
                        .takeWhile(x -> x <= x1)
                        .toArray();
    }


    public static final long[] linSpaceLong(long x0, long x1, long dx) {
        return LongStream.iterate(x0, x -> x + dx)
                .takeWhile(x -> x <= x1)
                .toArray();
    }

    /**
     * Allocates and fills a long array with a specified value.
     *
     * @param value The value for each element in the array.
     * @param n     The array size.
     * @return The allocated array.
     */
    public static final long[] fillLongArray(long value, int n) {
        long[] ret = new long[n];
        Arrays.fill(ret, value);
        return ret;
    }

    /**
     * Finds the index into xs[] nearest x where xs[] < x (long)
     *
     * @param xs Array of values
     * @param x  Single value
     */
    public static final int getNearestLowerIndex(long xs[], long x) {
        int k, klo, khi;
        klo = 0;
        khi = xs.length - 1;
        if (x <= xs[0])
            return 0;
        if (x >= xs[khi])
            return khi;
        while (khi - klo > 1) {
            k = (khi + klo) / 2;
            if (x < xs[k])
                khi = k;
            else
                klo = k;
        }
        return klo;
    }

    /**
     * Get current time as yyyy-MM-dd HH:mm:ss.SSS.
     *
     * @return current time
     */
    public static String getCurrentTime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
    }
    
    /**
     * generate lines of java IO code for files in dir.
     *
     * @param classMethodName    IO method, e.g. AsciiMatrixFile.load
     * @param dir                directory contains files
     * @param folderVariableName folder variable name
     * @param ext                file extension, e.g. txt
     * @param includedString     generate for only filename contains this string
     * @return lines of java IO code
     */
    public static String generateIOScript(String classMethodName, String dir, String folderVariableName, String ext, String includedString) {
        StringBuffer ret = new StringBuffer();
        String[] filenames = OneLiners.getFileNames(dir);
        for (int i = 0; i < filenames.length; i++) {
            if(filenames[i].endsWith(ext) && filenames[i].contains(includedString)) {
                ret.append(String.format("double[][] %s = %s(%s+\"/%s\");%n", filenames[i].substring(0, filenames[i].lastIndexOf(".")), classMethodName, folderVariableName, filenames[i]));
            }
        }

        return ret.toString();
    }

    public static String generateAsciiMatrixFileLoadScript(String dir, String folderVariableName, String ext, String includedString) {
        return generateIOScript("AsciiMatrixFile.load", dir, folderVariableName, ext, includedString);
    }

    public static String generateAsciiMatrixFileLoadScript(String dir, String folderVariableName, String ext) {
        return generateIOScript("AsciiMatrixFile.load", dir, folderVariableName, ext, "");
    }

    public static String generateAsciiMatrixFileLoadScript(String dir, String folderVariableName) {
        return generateIOScript("AsciiMatrixFile.load", dir, folderVariableName, "", "");
    }

    public static String generateAsciiMatrixFileLoadScriptTxt(String dir, String folderVariableName) {
        return generateIOScript("AsciiMatrixFile.load", dir, folderVariableName, "txt", "");
    }

    public static String generateAsciiMatrixFileLoadScriptTxt(String dir) {
        return generateIOScript("AsciiMatrixFile.load", dir, "folder", "txt", "");
    }

    public static String generateAsciiMatrixFileLoadScriptAutoRank(String dir, String folderVariableName, String ext, String includedString) {
        String classMethodName = "AsciiMatrixFile.load";
        StringBuffer ret = new StringBuffer();
        String[] filenames = OneLiners.getFileNames(dir);
        for (int i = 0; i < filenames.length; i++) {
            if(filenames[i].endsWith(ext) && filenames[i].contains(includedString)) {
                try {
                    double[][] data = AsciiMatrixFile.load(dir+"/"+filenames[i]);
                    if(data.length > 1 && data[0].length > 1) {
                        ret.append(String.format("double[][] %s = %s(%s+\"/%s\");%n", filenames[i].substring(0, filenames[i].lastIndexOf(".")), classMethodName, folderVariableName, filenames[i]));
                    } else if(data.length == 1 && data[0].length > 1) {
                        ret.append(String.format("double[] %s = %s(%s+\"/%s\")[0];%n", filenames[i].substring(0, filenames[i].lastIndexOf(".")), classMethodName, folderVariableName, filenames[i]));
                    } else if(data.length > 1 && data[0].length == 1) {
                        ret.append(String.format("double[] %s = %s(%s+\"/%s\", true)[0];%n", filenames[i].substring(0, filenames[i].lastIndexOf(".")), classMethodName, folderVariableName, filenames[i]));
                    } else if(data.length == 1 && data[0].length == 1) {
                        ret.append(String.format("double %s = %s(%s+\"/%s\")[0][0];%n", filenames[i].substring(0, filenames[i].lastIndexOf(".")), classMethodName, folderVariableName, filenames[i]));
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return ret.toString();
    }

    public static String generateAsciiMatrixFileLoadScriptAutoRank(String dir, String folderVariableName, String ext) {
        return generateAsciiMatrixFileLoadScriptAutoRank(dir, folderVariableName, ext, "");
    }

    public static String generateAsciiMatrixFileLoadScriptAutoRank(String dir, String folderVariableName) {
        return generateAsciiMatrixFileLoadScriptAutoRank(dir, folderVariableName, "", "");
    }

    public static String generateAsciiMatrixFileLoadScriptAutoRankTxt(String dir, String folderVariableName) {
        return generateAsciiMatrixFileLoadScriptAutoRank(dir, folderVariableName, "txt", "");
    }

    public static String generateAsciiMatrixFileLoadScriptAutoRankTxt(String dir) {
        return generateAsciiMatrixFileLoadScriptAutoRank(dir, "folder", "txt", "");
    }

    public static String getUsername() {
        return System.getProperty("user.name");
    }

    /** Apparently, getting the local hostname/computername is absurdly difficult in Java. */
    public static String getHostname() {

        //windows has a nice environment variable
        String host = System.getenv("COMPUTERNAME");
        if (host != null)
            return host.trim();

        //unix has an environment variable... sometimes
        host = System.getenv("HOSTNAME");
        if (host != null)
            return host.trim();


        //maybe we can get it from the kernel on linux
        try {
            host = OneLiners.fileToText("/proc/sys/kernel/hostname");
        } catch (RuntimeException err) { }
        if(host != null)
            return host.trim();

        //erm... try dragging it from the /etc that many linux systems have
        try {
            host = OneLiners.fileToText("/etc/hostname");
        } catch (RuntimeException err) { }
        if(host != null)
            return host.trim();

        //finally try the unreliable, somewhat ambiguous and possibly slow name resolution method
        try {
            host = InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) { }

        if(host != null && host.length() > 0)
            return host.trim();

        // maybe try "uname -n" as well?

        throw new RuntimeException("Failed all possible methods of finding the local host name. Sometimes, Java, I really hate you.");
    }

    /**
     * Make an 2D array with length n.
     * @param val the values to put in each outer dimension
     * @param n the length of the returned array
     * @return the constructed 2D array
     */
    public static double[][] fillArray(double[] val, int n) {
        double[][] ret = new double[n][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = val;
        }
        return ret;
    }

    /**
     * Make an 2D array with length n.
     * @param val the values to put in each outer dimension
     * @param n the length of the returned array
     * @return the constructed 2D array
     */
    public static int[][] fillArray(int[] val, int n) {
        int[][] ret = new int[n][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = val;
        }
        return ret;
    }

    /**
     * Make an 2D array with length n.
     * @param val the values to put in each outer dimension
     * @param n the length of the returned array
     * @return the constructed 2D array
     */
    public static boolean[][] fillArray(boolean[] val, int n) {
        boolean[][] ret = new boolean[n][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = val;
        }
        return ret;
    }

    /**
     * Gets the class that can be used to box primitives. For other classes returns
     * just the given class.
     *
     * @param clazz the class to determine the corresponding boxing class
     * @return the corresponding boxing class
     */
    public static final Class<?> getBoxingClass(Class<?> clazz) {
        if (clazz.equals(double.class)) {
            return Double.class;
        } else if (clazz.equals(int.class)) {
            return Integer.class;
        } else if (clazz.equals(long.class)) {
            return Long.class;
        } else if (clazz.equals(boolean.class)) {
            return Boolean.class;
        } else if (clazz.equals(float.class)) {
            return Float.class;
        } else if (clazz.equals(byte.class)) {
            return Byte.class;
        } else if (clazz.equals(short.class)) {
            return Short.class;
        } else if (clazz.equals(char.class)) {
            return Character.class;
        } else if (clazz.equals(void.class)) {
            return Void.class;
        } else {
            return clazz;
        }
    }

    /**
     * Gets the class that can be used to unbox boxed primitives. For other classes
     * returns just the given class.
     *
     * @param clazz
     *            the class to determine the corresponding primitive class
     * @return the corresponding primitive class
     */
    public static final Class<?> getUnboxingClass(Class<?> clazz) {
        if (clazz.equals(Double.class)) {
            return double.class;
        } else if (clazz.equals(Integer.class)) {
            return int.class;
        } else if (clazz.equals(Long.class)) {
            return long.class;
        } else if (clazz.equals(Boolean.class)) {
            return boolean.class;
        } else if (clazz.equals(Float.class)) {
            return float.class;
        } else if (clazz.equals(Byte.class)) {
            return byte.class;
        } else if (clazz.equals(Short.class)) {
            return short.class;
        } else if (clazz.equals(Character.class)) {
            return char.class;
        } else if (clazz.equals(Void.class)) {
            return void.class;
        } else {
            return clazz;
        }
    }

    /**
     * Box the object, if it is a primitive (array).
     *
     * @param object
     *            the object to be boxed if it is a primitive (array)
     * @return the (boxed) object
     */
    @SuppressWarnings("unchecked")
    public static final <T> T box(Object object) {
        if (object.getClass().isArray()) {
            int dimension = getArrayDimensions(object.getClass());
            Class<?> elementType = getElementType(object.getClass());
            if (elementType.isPrimitive()) {
                elementType = getBoxingClass(elementType);
            } else {
                return (T) object;
            }
            Class<?> componentType = getArrayType(elementType, dimension - 1);
            int length = Array.getLength(object);
            Object ret = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(ret, i, box(Array.get(object, i)));
            }
            return (T) ret;
        }
        return (T) object;
    }

    /**
     * Unbox the object if it is a primitive array. Unboxing single primitive values
     * does not work, because generics don't work with primitives.
     *
     * @param object
     *            the object that is unboxed if it is a primitive array
     * @return the (unboxed) object
     */
    @SuppressWarnings("unchecked")
    public static final <T> T unbox(Object object) {
        if (object.getClass().isArray()) {
            int dimension = getArrayDimensions(object.getClass());
            Class<?> elementType = getElementType(object.getClass());
            if (!elementType.isPrimitive()) {
                elementType = getUnboxingClass(elementType);
            } else {
                return (T) object;
            }
            Class<?> componentType = getArrayType(elementType, dimension - 1);
            int length = Array.getLength(object);
            Object ret = Array.newInstance(componentType, length);
            for (int i = 0; i < length; i++) {
                Array.set(ret, i, unbox(Array.get(object, i)));
            }
            return (T) ret;
        }
        return (T) object;
    }

    /**
     * Reverses the entries of an array. Only the outermost level is reversed. This
     * should work for every array except 1D primitive arrays, which will use
     * {@link OneLiners#reverse(Object)}.
     *
     * @param object
     *            the array to reverse
     * @return the reversed array
     */
    public static final <T> T[] reverse(T[] object) {
        T[] tmp = object.clone();
        int counter = 0;
        for (int i = tmp.length - 1; i >= 0; i--) {
            tmp[counter] = object[i];
            counter++;
        }
        return tmp;
    }

    /**
     * Reverses a 1D primitive array. Not optimized for speed, as it boxes the whole
     * array, reverts it, and unboxes it again.
     *
     * @param primitiveArray
     *            the array to revert
     * @return the reverted 1D primitive array
     */
    public static final <T> T reverse(T primitiveArray) {
        if (primitiveArray.getClass().isArray()
                && getElementType(primitiveArray.getClass()).isPrimitive()
                && getArrayDimensions(primitiveArray.getClass()) == 1) {
            T[] tmp = box(primitiveArray);
            return unbox(reverse(tmp));
        }
        throw new IllegalArgumentException("Don't know how to reverse for this class: " + primitiveArray.getClass());
    }

    /**
     * Gets the angle between two vectors.
     * TODO: Make more general and add to Algorithms.
     * @param vec1 3D vector
     * @param vec2 3D vector
     * @return the angle between vec1 and vec2
     */
    public static final double angle(double[] vec1, double[] vec2) {
        double dot = vec1[0]*vec2[0] + vec1[1]*vec2[1] + vec1[2]*vec2[2];
        double len1 = Math.sqrt(Math.pow(vec1[0], 2.0) + Math.pow(vec1[1], 2.0) + Math.pow(vec1[2], 2.0));
        double len2 = Math.sqrt(Math.pow(vec2[0], 2.0) + Math.pow(vec2[1], 2.0) + Math.pow(vec2[2], 2.0));
        return Math.acos(dot/(len1*len2));
    }

    /**
     * Makes sure that every entry of the array is at least as big as min.
     * @param a the array
     * @param min the minimum value
     * @return the array with all values >= min
     */
    public static final double[] atLeast(double[] a, double min) {
        return DoubleStream.of(a).map(val -> Math.max(min, val)).toArray();
    }

    /**
     * Makes sure that every entry of the array is at least as big as min.
     *
     * @param a   the array
     * @param min the minimum value
     * @return the array with all values >= min
     */
    public static final double[][] atLeast(double[][] a, double min) {
        return Stream.of(a).map(array -> atLeast(array, min)).toArray(double[][]::new);
    }

    /**
     * Gets the human readable time string for a given (duration) time in
     * milliseconds.
     *
     * @param millis the time to convert, typically a duration
     * @return the human readable time, typically a duration
     */
    public static String getTimeString(long millis) {
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(hours);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes);
        long milliseconds = millis - TimeUnit.HOURS.toMillis(hours) - TimeUnit.MINUTES.toMillis(minutes)- TimeUnit.SECONDS.toMillis(seconds);

        return String.format("%02d h, %02d min, %02d sec, %03d ms", hours, minutes, seconds, milliseconds);
    }

    /**
     * Checks whether on a windows system.
     *
     * @return true if on a windows system
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().contains("win");
    }

    /**
     * Checks whether on a mac system.
     *
     * @return true if on a mac system
     */
    public static boolean isMac() {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    /**
     * Checks whether on a unix system.
     *
     * @return true if on a unix system
     */
    public static boolean isUnix() {
        String os = System.getProperty("os.name").toLowerCase();
        return os.contains("nix") || os.contains("nux") || os.contains("aix");
    }

    /**
     * Checks whether on a solaris system.
     *
     * @return true if on a solaris system
     */
    public static boolean isSolaris() {
        return System.getProperty("os.name").toLowerCase().contains("sunos");
    }

    /**
     * Possible colors for {@link OneLiners#colorString(String, ANSI_COLOR)}.
     *
     * @author udoh
     */
    public enum ANSI_COLOR {
        NONE(""),
        BLACK("\u001B[30m"),
        RED("\u001B[31m"),
        GREEN("\u001B[32m"),
        YELLOW("\u001B[33m"),
        BLUE("\u001B[34m"),
        PURPLE("\u001B[35m"),
        CYAN("\u001B[36m"),
        WHITE("\u001B[37m");

        public static final String RESET = "\u001B[0m";

        private String escapeCode;

        ANSI_COLOR(String str) {
            escapeCode = str;
        }

        @Override
        public String toString() {
            return escapeCode;
        }
    }

    /**
     * Color a string by adding the corresponding ANSI escape codes. This might not
     * work in all terminals, it is deactivated for windows and solaris by default.
     *
     * @param str the string to color
     * @param color the color
     * @return the colored string (only if on unix or mac)
     */
    public static String colorString(String str, ANSI_COLOR color) {
        if (!isWindows() && !isSolaris()) {
            return color.toString() + str + ANSI_COLOR.RESET;
        }
        return str;
    }

    /**
     * Checks whether the new file has a higher version number than the old one.
     * @param oldFile the old file
     * @param newFile the new file
     * @return true if newFile has a higher version, otherwise false
     */
    public static boolean hasHigherVersion(File oldFile, File newFile) {
        String[] oldVersion = getVersionString(oldFile).split("\\.");
        String[] newVersion = getVersionString(newFile).split("\\.");

        long length = IntStream.of(oldVersion.length, newVersion.length).distinct().count();

        if (length != 1 || oldVersion.length != 3 || newVersion.length != 3) {
            throw new IllegalArgumentException("Invalid Maven version found in " + oldFile.getName()
            + " or " + newFile.getName() + ". The version has to be of the format "
            + "MajorVersion.MinorVersion.PatchVersion.");
        }

        for (int i = 0; i < 3; i++) {
            if (Integer.parseInt(oldVersion[i]) > Integer.parseInt(newVersion[i])) {
                return false;
            } else if (Integer.parseInt(oldVersion[i]) == Integer.parseInt(newVersion[i])) {
                // continue
            } else {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets the version string from a File.
     *
     * @param file the File to get the version key for
     * @return the version string
     */
    public static String getVersionString(File file) {
        String fileName = file.getName();
        String regex = "\\d+\\.\\d+\\.\\d+"; // Digits.Digits.Digits
        Matcher matcher = Pattern.compile(regex).matcher(fileName);
        if (matcher.find()) return matcher.group();
        return null;
    }

    /**
     * Gets the Enum (arrays) from a String (array)., e.g. String[][] ->
     * YourEnum[][].
     *
     * @param data  the String (or String array of arbitrary dimension)
     * @param clazz the Enum class to cast to
     * @return the Enum (arrays)
     */
    public static <T extends Enum<T>> Object stringToEnum(Object data, Class<T> clazz) {
        if (data.getClass().isArray()) {
            int len = Array.getLength(data);
            int dim = Math.max(0, OneLiners.getArrayDimensions(data.getClass()) - 1);

            Class<?> componentType = OneLiners.getArrayType(OneLiners.getElementType(clazz), dim);

            Object obj = Array.newInstance(componentType, len);

            for (int i = 0; i < len; i++) {
                Array.set(obj, i, stringToEnum(Array.get(data, i), clazz));
            }
            return obj;
        }

        return Enum.valueOf(clazz, (String) data);
    }

    /** @return Array of toString() outputs of array of objects. Useful for e.g. getting array of strings of Enum names */
    public static String[] toStringArray(Object[] values){
        String[] s = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            s[i] = values[i].toString();
        }
        return s;

    }

    /**
     * Checks whether a file is a binary file.
     *
     * @param file the file to check
     * @return true if file is not a text file
     * @throws IOException if an I/O error occurs
     */
    public static boolean isBinaryFile(File file) throws IOException {
        if (!file.isFile()) return false;
        String type = Files.probeContentType(file.toPath());
        if (type == null) {
            // type couldn't be determined, assume binary
            return true;
        }

        return !type.startsWith("text");
    }

    /**
     * Find indices of truth in request array. Similar to numpy's where() method
     * @param request array of boolean values
     * @return array of indices where a true value was found in request
     */
    public static int[] where(boolean[] request) {
        int[] indicesOfTruth = new int[request.length];
        int numTrue = 0;
        for (int i=0; i<request.length; ++i) {
            if (request[i]) {
                indicesOfTruth[numTrue] = i;
                numTrue++;
            }
        }
        int[] ret = new int[numTrue];
        System.arraycopy(indicesOfTruth, 0, ret, 0, numTrue);
        return ret;
    }

    /**
     * Replaces all occurrences of value in a with replacement
     *
     * @param a           the array to check
     * @param value       the value to replace
     * @param replacement the replacement for value
     * @return the array with value replaced
     */
    public static double[] replace(double[] a, double value, double replacement) {
        double[] ret = new double[a.length];
        for (int i = 0; i < ret.length; i++) {
            if (Double.isNaN(a[i]) && Double.isNaN(value)) {
                ret[i] = replacement;
            } else if (Double.isInfinite(a[i]) && Double.isInfinite(value)) {
                if ((a[i] > 0 && value > 0) || (a[i] < 0 && value < 0) ) {
                    ret[i] = replacement;
                } else {
                    ret[i] = a[i];
                }
            } else if (a[i] == value) {
                ret[i] = replacement;
            } else {
                ret[i] = a[i];
            }
        }
        return ret;
    }

    /**
     * Replaces all occurrences of value in a with replacement
     *
     * @param a           the array to check
     * @param value       the value to replace
     * @param replacement the replacement for value
     * @return the array with value replaced
     */
    public static double[][] replace(double[][] a, double value, double replacement) {
        double[][] ret = new double[a.length][];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = replace(a[i], value, replacement);
        }
        return ret;
    }

    /**
     * Checks whether an object is an array.
     *
     * @param obj the object to check
     * @return true if obj is an array
     */
    public static final boolean isArray(Object obj) {
        return obj != null && obj.getClass().isArray();
    }

    /**
     * Checks whether a file is in the PATH variables and executable. Does not need
     * the ".exe" file extension (gets appended automatically on windows).
     *
     * @param bin the name of the binary
     * @return true if binary is found and executable
     */
    public static final boolean isInPathAndExecutable(String bin) {
        if (isWindows()) bin += ".exe";
        final String binary = bin;
        return  Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
                .map(Paths::get)
                .anyMatch(path -> path.resolve(binary).toFile().exists() && Files.isExecutable(path.resolve(binary)));
    }

    /**
     * Checks whether two methods have the same parameter signature.
     *
     * @param m the first method
     * @param other the second method
     * @return true if their parameter signature is identical
     */
    public static final boolean hasSameParameterSignature(Method m, Method other) {
        if (m == null) return false;
        if (other == null) return false;

        Type[] mParams = m.getParameterTypes();
        Type[] otherParams = other.getParameterTypes();

        if (mParams.length != otherParams.length) return false;

        for (int i = 0; i < mParams.length; i++) {
            if (!mParams[i].equals(otherParams[i])) return false;
        }

        return true;
    }

    /**
     * Checks whether two methods have the same return signature.
     *
     * @param m the first method
     * @param other the second method
     * @return true if their return signature is identical
     */
    public static final boolean hasSameReturnSignature(Method m, Method other) {
        if (m == null) return false;
        if (other == null) return false;

        Type mReturn = m.getReturnType();
        Type otherReturn = other.getReturnType();

        return mReturn.equals(otherReturn);
    }

    /**
     * Checks whether two methods have the same signature.
     *
     * @param m the first method
     * @param other the second method
     * @return true if their signature is identical
     */
    public static final boolean hasSameSignature(Method m, Method other) {
        return hasSameSignature(m, "", other);
    }

    /**
     * Checks whether two methods have the same signature.
     *
     * @param m the first method
     * @param replacement the method name to use for m if not null or empty
     * @param other the second method
     * @return true if their signature is identical
     */
    public static final boolean hasSameSignature(Method m, String replacement, Method other) {
        return hasSameSignature(m, replacement, Arrays.asList(other), Arrays.asList(""));

    }

    /**
     * Checks whether a method has the same signature as several other methods.
     *
     * @param m            the first method
     * @param replacement  the method name to use for m if not null or empty
     * @param other        the other methods
     * @param replacements the method names replacements for the other methods
     * @return true if their signature is identical
     */
    public static final boolean hasSameSignature(Method m, String replacement, List<Method> others,
            List<String> replacements) {
        String mName = m.getName();
        if (replacement != null && !replacement.isEmpty())
            mName = replacement;
        for (int i = 0; i < others.size(); i++) {
            String otherReplacement = others.get(i).getName();
            if (replacements.get(i) != null && !replacements.get(i).isEmpty())
                otherReplacement = replacements.get(i);
            if (hasSameParameterSignature(m, others.get(i)) && mName.equals(otherReplacement))
                return true;
        }
        return false;
    }

    /**
     * Checks whether the method m could be (an Override of) the inherited method
     * classMethod.
     *
     * @param m           the method to check for
     * @param classMethod the potential "parent" method
     * @return true if m potentially is (an Override of) the classMethod
     */
    public static boolean inheritable(Method m, Method classMethod) {
        if (m == null || classMethod == null) return false;

        Package mPackage = m.getDeclaringClass().getPackage();
        Package classPackage = classMethod.getDeclaringClass().getPackage();

        int modifiers = classMethod.getModifiers();
        if (Modifier.isPublic(modifiers)) return true;
        if (Modifier.isProtected(modifiers)) return true;
        return !Modifier.isPrivate(modifiers) && mPackage.equals(classPackage);
    }

    /**
     * Checks whether a method could be (an Override of) the inherited method
     * classMethod.
     *
     * @param mPackage           the method's package to check for
     * @param classMethod the potential "parent" method
     * @return true if m potentially is (an Override of) the classMethod
     */
    public static boolean inheritable(String mPackage, Method classMethod) {
        if (mPackage == null || classMethod == null) return false;

        Package classPackage = classMethod.getDeclaringClass().getPackage();

        int modifiers = classMethod.getModifiers();
        if (Modifier.isPublic(modifiers)) return true;
        if (Modifier.isProtected(modifiers)) return true;
        return !Modifier.isPrivate(modifiers) && mPackage.equals(classPackage.toString());
    }

    /**
     * Checks whether clazz contains a method with the same signature.
     *
     * @param m     the method to check for
     * @param clazz the class to compare against
     * @return true if clazz contains a method with the same signature
     */
    public static final boolean containsSameSignatureMethod(Method m, Class<?> clazz) {
        if (clazz == null) return false;
        Method[] methods = clazz.getDeclaredMethods();
        boolean found = false;
        for (Method classMethod : methods) {
            if (!inheritable(m, classMethod)) continue;
            found = hasSameSignature(m, classMethod);
            if (found) return true;
        }
        return containsSameSignatureMethod(m, clazz.getSuperclass());
    }

    /**
     * Checks whether clazz contains a method with the same signature.
     *
     * @param m     the method to check for
     * @param replacement the method name to use for m if not null or empty
     * @param clazz the class to compare against
     * @return true if clazz contains a method with the same signature
     */
    public static final boolean containsSameSignatureMethod(Method m, String replacementName, Class<?> clazz) {
        if (clazz == null) return false;
        Method[] methods = clazz.getDeclaredMethods();
        boolean found = false;
        for (Method classMethod : methods) {
            if (!inheritable(m, classMethod)) continue;
            found = hasSameSignature(m, replacementName, classMethod);
            if (found) return true;
        }
        return containsSameSignatureMethod(m, replacementName, clazz.getSuperclass());
    }

    /**
     * Checks whether the classes contain a method with the same signature.
     *
     * @param m     the method to check for
     * @param replacement the method name to use for m if not null or empty
     * @param classes the classes to compare against
     * @return true if at least one class in classes contains a method with the same signature
     */
    public static final boolean containsSameSignatureMethod(Method m, String replacementName, List<Class<?>> classes) {
        return classes.stream().anyMatch(cls -> containsSameSignatureMethod(m, replacementName, cls));
    }

    /**
     * Gets all potentially clashing methods.
     *
     * @param targetPackage the package your method will reside in
     * @param classes the classes to check against
     * @param numParams the number of parameters you intend to have for your method
     * @return all potentially clashing methods (signature wise)
     */
    public static List<Method> getAllPotentiallyClashingMethods(String targetPackage, List<Class<?>> classes, int numParams) {
        List<Method> ret = new ArrayList<>();

        for (Class<?> clazz : classes) {
            ret.addAll(getAllPotentiallyClashingMethods(targetPackage, clazz, numParams));
        }
        return ret;
    }

    /**
     * Gets all potentially clashing methods.
     *
     * @param targetPackage the package your method will reside in
     * @param clazz the class to check against
     * @param numParams the number of parameters you intend to have for your method
     * @return all potentially clashing methods (signature wise)
     */
    public static List<Method> getAllPotentiallyClashingMethods(String targetPackage, Class<?> clazz, int numParams) {
        List<Method> ret = new ArrayList<>();

        if (clazz == null) return ret;
        Method[] methods = clazz.getDeclaredMethods();
        for (Method classMethod : methods) {
            if (!inheritable(targetPackage, classMethod)) continue;
            if (classMethod.getParameterCount() == numParams) ret.add(classMethod);
        }
        Class<?> parent = clazz.getSuperclass();
        while (parent != null) {
            ret.addAll(getAllPotentiallyClashingMethods(targetPackage, parent, numParams));
            parent = parent.getSuperclass();
        }
        return ret;
    }

    /**
     * Checks whether a method is deprecated.
     *
     * @param m the method to check
     * @return true if m is deprecated
     */
    public static final boolean isDeprecated(Method m) {
        return m.isAnnotationPresent(Deprecated.class);
    }

    /**
     * Gets the specified input stream from the resources.
     *
     * @param fileName the resource to find, not null
     * @param clazz the class of which to get the resources, not null
     * @return the input stream of the file
     */
    public static final String getStringFromResources(String fileName, Class<?> clazz) {
        return getStringFromResourceLocation("src/main/resources/", fileName, clazz);
    }

    /**
     * Gets the specified input stream from the test resources.
     *
     * @param fileName the resource to find, not null
     * @param clazz the class of which to get the resources, not null
     * @return the input stream of the file
     */
    public static final String getStringFromTestResources(String fileName, Class<?> clazz) {
        return getStringFromResourceLocation("src/test/resources/", fileName, clazz);
    }

    /**
     * Gets the specified resource content as a String from the resources
     * (particularly, one can choose between the src/main/resources/ and
     * src/test/resources/).
     *
     * @param resource the folder in which to look for the resource
     * @param fileName the resource to find
     * @param clazz    the class of which to get the resources
     * @return the input stream of the file
     */
    private static final String getStringFromResourceLocation(String resource, String fileName, Class<?> clazz) {
        StringBuilder textBuilder = new StringBuilder();
        useInputStreamFromResourceLocation(resource, fileName, clazz, is -> readInputStream(is, textBuilder));
        return textBuilder.toString();
    }

    /**
     * Reads the input stream into the textBuilder. Does not close the input stream.
     *
     * @param is          the input stream
     * @param textBuilder the text builder
     */
    private static final void readInputStream(InputStream is, StringBuilder textBuilder) {
        try (Scanner sc = new Scanner(is)) {
            while (sc.hasNextLine()) {
                textBuilder.append(sc.nextLine() + "\n");
            }
        }
    }

    /**
     * Uses the specified input stream consumer on the input created from the
     * resources (particularly, one can choose between the src/main/resources/ and
     * src/test/resources/).
     *
     * @param resource the folder in which to look for the resource, not null
     * @param fileName the resource to find, not null
     * @param clazz    the class of which to get the resources, not null
     * @param consumer the consumer that uses the created {@link InputStream}, not
     *                 null
     * @return the input stream of the file
     */
    public static final void useInputStreamFromResourceLocation(String resource, String fileName, Class<?> clazz,
            Consumer<InputStream> consumer) {
        Objects.requireNonNull(resource);
        Objects.requireNonNull(fileName);
        Objects.requireNonNull(clazz);
        Objects.requireNonNull(consumer);

        // loading from within jar
        try (InputStream is = clazz.getResourceAsStream(resource + fileName)) {
            if (is == null) {
                // loading within editor (e.g. eclipse)
                try (InputStream is2 = clazz.getClassLoader().getResourceAsStream(fileName)) {
                    if (is2 == null) throw new IllegalArgumentException("Found no file with that filename!");

                    InputStream is3 = is2;
                    if (fileName.endsWith(".zip")) {
                        is3 = new ZipInputStream(is2);
                        ((ZipInputStream) is3).getNextEntry();
                    }

                    consumer.accept(is3);
                    return;
                }
            }

            InputStream is3 = is;
            if (fileName.endsWith(".zip")) {
                is3 = new ZipInputStream(is);
                ((ZipInputStream) is3).getNextEntry();
            }

            consumer.accept(is3);
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }
        throw new IllegalArgumentException("Found no file with that filename!");
    }

    /**
     * Replaces all spaces with {@code &nbsp;}, except where the line would exceed
     * the specified letter maxlen upon inclusion of the preceding word.
     *
     * @param str    the String to prepare for (pseudo) block typesetting
     * @param maxlen the maximum number of characters in a line
     * @return the sanitized String that can only break at spaces that format the
     *         text to the given maxlen
     */
    public static final String fixSpacesForReasonableLinebreaks(String str, int maxlen) {
        StringBuilder sb = new StringBuilder(str);
        int lastWhitespaceIndex = -1;
        int currentLineLength = 0;
        int numSpaces = 0;
        for (int i = 0; i < sb.length(); i++) {
            char letter = sb.charAt(i);
            if (Character.isWhitespace(letter)) {
                if (currentLineLength > maxlen + numSpaces * ("&nbsp;".length() - 1)) {
                    // use the previous whitespace, which at this point is already a &nbsp;
                    sb.replace(lastWhitespaceIndex, lastWhitespaceIndex + "&nbsp;".length(), " ");
                    i -= "&nbsp;".length();
                    currentLineLength = i - lastWhitespaceIndex;
                    numSpaces = 1;
                } else {
                    sb.deleteCharAt(i);
                    sb.insert(i, "&nbsp;");
                    numSpaces++;
                }

                lastWhitespaceIndex = i;
            }
            currentLineLength++;
        }

        // as there might not be a whitespace in the last line after it should have been broken
        if (currentLineLength > maxlen + numSpaces * ("&nbsp;".length() - 1)) {
            // use the previous whitespace, which at this point is already a "&nbsp;"
            sb.replace(lastWhitespaceIndex, lastWhitespaceIndex + "&nbsp;".length(), " ");
        }
        return sb.toString();
    }

    /**
     * Generates a random lowercase alphabetic String of the specified length.
     *
     * @param length the length of the random String
     * @return a random String
     */
    public static final String getRandomLowercaseAlphabeticString(int length) {
        int left = 97; // -> a''
        int right = 122; // -> 'z'

        return new Random().ints(left, right + 1)
                .limit(length)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    /**
     * Converts an array to the corresponding int array, if possible.
     *
     * @param <T>   the input type, e.g. double[][]
     * @param <U>   the output int array, same dimension as <T>
     * @param array the array
     * @return the array converted to an int array
     */
    @SuppressWarnings("unchecked")
    public static final <T, U> U toInt(T array) {
        T localArray = unbox(array);
        if (getElementType(localArray.getClass()) == int.class) return (U) localArray;
        if (localArray instanceof double[]) {
            double[] cast = (double[]) localArray;
            int[] ret = new int[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = (int) cast[i];
            }
            return (U) ret;
        } else if (localArray instanceof double[][]) {
            double[][] cast = (double[][]) localArray;
            int[][] ret = new int[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = (int) cast[i][j];
                }
            }
            return (U) ret;
        } else if (localArray instanceof double[][][]) {
            double[][][] cast = (double[][][]) localArray;
            int[][][] ret = new int[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new int[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = (int) cast[i][j][k];
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof Enum[]) {
            Enum<?>[] cast = (Enum[]) localArray;
            int[] ret = new int[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = cast[i].ordinal();
            }
            return (U) ret;
        } else if (localArray instanceof Enum[][]) {
            Enum<?>[][] cast = (Enum[][]) localArray;
            int[][] ret = new int[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = cast[i][j].ordinal();
                }
            }
            return (U) ret;
        } else if (localArray instanceof Enum[][][]) {
            Enum<?>[][][] cast = (Enum[][][]) localArray;
            int[][][] ret = new int[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new int[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = cast[i][j][k].ordinal();
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof String[]) {
            String[] cast = (String[]) localArray;
            return (U) Stream.of(cast).mapToInt(Integer::parseInt).toArray();
        } else if (localArray instanceof String[][]) {
            String[][] cast = (String[][]) localArray;
            int[][] ret = new int[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = Integer.parseInt(cast[i][j]);
                }
            }
            return (U) ret;
        } else if (localArray instanceof String[][][]) {
            String[][][] cast = (String[][][]) localArray;
            int[][][] ret = new int[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new int[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = Integer.parseInt(cast[i][j][k]);
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof boolean[]) {
            boolean[] cast = (boolean[]) localArray;
            int[] ret = new int[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = cast[i] ? 1 : 0;
            }
            return (U) ret;
        } else if (localArray instanceof boolean[][]) {
            boolean[][] cast = (boolean[][]) localArray;
            int[][] ret = new int[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = cast[i][j] ? 1 : 0;
                }
            }
            return (U) ret;
        } else if (localArray instanceof boolean[][][]) {
            boolean[][][] cast = (boolean[][][]) localArray;
            int[][][] ret = new int[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new int[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = cast[i][j][k] ? 1 : 0;
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof short[]) {
            short[] cast = (short[]) localArray;
            int[] ret = new int[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = (int) cast[i];
            }
            return (U) ret;
        } else if (localArray instanceof short[][]) {
            short[][] cast = (short[][]) localArray;
            int[][] ret = new int[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = (int) cast[i][j];
                }
            }
            return (U) ret;
        } else if (localArray instanceof short[][][]) {
            short[][][] cast = (short[][][]) localArray;
            int[][][] ret = new int[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new int[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = (int) cast[i][j][k];
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof long[]) {
            long[] cast = (long[]) localArray;
            int[] ret = new int[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = Math.toIntExact(cast[i]);
            }
            return (U) ret;
        } else if (localArray instanceof long[][]) {
            long[][] cast = (long[][]) localArray;
            int[][] ret = new int[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = Math.toIntExact(cast[i][j]);
                }
            }
            return (U) ret;
        } else if (localArray instanceof long[][][]) {
            long[][][] cast = (long[][][]) localArray;
            int[][][] ret = new int[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new int[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new int[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = Math.toIntExact(cast[i][j][k]);
                    }
                }
            }
            return (U) ret;
        } else if (getArrayDimensions(localArray.getClass()) > 3) { // be generic for higher order arrays
            int dim = getArrayDimensions(localArray.getClass());
            Class<?> target = getArrayType(int.class, dim - 1);
            int len = Array.getLength(localArray);
            Object ret = Array.newInstance(target, len);

            for (int i = 0; i < len; i++) {
                Array.set(ret, i, toInt(Array.get(localArray, i)));
            }

            return (U) ret;
        }

        throw new UnsupportedOperationException();
    }

    /**
     * Converts an array to the corresponding double array, if possible.
     *
     * @param <T>   the input type, e.g. long[][]
     * @param <U>   the output double array, same dimension as <T>
     * @param array the array
     * @return the array converted to an int array
     */
    @SuppressWarnings("unchecked")
    public static final <T, U> U toDouble(T array) {
        T localArray = unbox(array);
        if (getElementType(localArray.getClass()) == double.class) return (U) localArray;
        if (localArray instanceof int[]) {
            int[] cast = (int[]) localArray;
            double[] ret = new double[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = cast[i];
            }
            return (U) ret;
        } else if (localArray instanceof int[][]) {
            int[][] cast = (int[][]) localArray;
            double[][] ret = new double[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = cast[i][j];
                }
            }
            return (U) ret;
        } else if (localArray instanceof int[][][]) {
            int[][][] cast = (int[][][]) localArray;
            double[][][] ret = new double[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new double[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = cast[i][j][k];
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof float[]) {
            float[] cast = (float[]) localArray;
            double[] ret = new double[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = cast[i];
            }
            return (U) ret;
        } else if (localArray instanceof float[][]) {
            float[][] cast = (float[][]) localArray;
            double[][] ret = new double[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = cast[i][j];
                }
            }
            return (U) ret;
        } else if (localArray instanceof float[][][]) {
            float[][][] cast = (float[][][]) localArray;
            double[][][] ret = new double[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new double[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = cast[i][j][k];
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof Enum[]) {
            Enum<?>[] cast = (Enum[]) localArray;
            double[] ret = new double[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = cast[i].ordinal();
            }
            return (U) ret;
        } else if (localArray instanceof Enum[][]) {
            Enum<?>[][] cast = (Enum[][]) localArray;
            double[][] ret = new double[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = cast[i][j].ordinal();
                }
            }
            return (U) ret;
        } else if (localArray instanceof Enum[][][]) {
            Enum<?>[][][] cast = (Enum[][][]) localArray;
            double[][][] ret = new double[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new double[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = cast[i][j][k].ordinal();
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof String[]) {
            String[] cast = (String[]) localArray;
            double[] ret = new double[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = Double.parseDouble(cast[i]);
            }
            return (U) ret;
        } else if (localArray instanceof String[][]) {
            String[][] cast = (String[][]) localArray;
            double[][] ret = new double[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = Double.parseDouble(cast[i][j]);
                }
            }
            return (U) ret;
        } else if (localArray instanceof String[][][]) {
            String[][][] cast = (String[][][]) localArray;
            double[][][] ret = new double[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new double[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = Double.parseDouble(cast[i][j][k]);
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof boolean[]) {
            boolean[] cast = (boolean[]) localArray;
            double[] ret = new double[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = cast[i] ? 1 : 0;
            }
            return (U) ret;
        } else if (localArray instanceof boolean[][]) {
            boolean[][] cast = (boolean[][]) localArray;
            double[][] ret = new double[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = cast[i][j] ? 1 : 0;
                }
            }
            return (U) ret;
        } else if (localArray instanceof boolean[][][]) {
            boolean[][][] cast = (boolean[][][]) localArray;
            double[][][] ret = new double[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new double[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = cast[i][j][k] ? 1 : 0;
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof short[]) {
            short[] cast = (short[]) localArray;
            double[] ret = new double[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = cast[i];
            }
            return (U) ret;
        } else if (localArray instanceof short[][]) {
            short[][] cast = (short[][]) localArray;
            double[][] ret = new double[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = cast[i][j];
                }
            }
            return (U) ret;
        } else if (localArray instanceof short[][][]) {
            short[][][] cast = (short[][][]) localArray;
            double[][][] ret = new double[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new double[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = cast[i][j][k];
                    }
                }
            }
            return (U) ret;
        } else if (localArray instanceof long[]) {
            long[] cast = (long[]) localArray;
            double[] ret = new double[cast.length];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = cast[i];
            }
            return (U) ret;
        } else if (localArray instanceof long[][]) {
            long[][] cast = (long[][]) localArray;
            double[][] ret = new double[cast.length][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = cast[i][j];
                }
            }
            return (U) ret;
        } else if (localArray instanceof long[][][]) {
            long[][][] cast = (long[][][]) localArray;
            double[][][] ret = new double[cast.length][][];
            for (int i = 0; i < ret.length; i++) {
                ret[i] = new double[cast[i].length][];
                for (int j = 0; j < ret[i].length; j++) {
                    ret[i][j] = new double[cast[i][j].length];
                    for (int k = 0; k < ret[i][j].length; k++) {
                        ret[i][j][k] = cast[i][j][k];
                    }
                }
            }
            return (U) ret;
        } else if (getArrayDimensions(localArray.getClass()) > 3) { // be generic for higher order arrays
            int dim = getArrayDimensions(localArray.getClass());
            Class<?> target = getArrayType(double.class, dim - 1);
            int len = Array.getLength(localArray);
            Object ret = Array.newInstance(target, len);

            for (int i = 0; i < len; i++) {
                Array.set(ret, i, toDouble(Array.get(localArray, i)));
            }

            return (U) ret;
        }

        throw new UnsupportedOperationException();
    }

    /**
     * Checks whether an url is reachable and returns true if successful.
     *
     * @param url     the url to check
     * @param timeout the time in ms after which a timeout occurs
     * @return true if reachable
     * @deprecated Doesn't always work as intended, so use with care
     */
    @Deprecated(forRemoval = false, since = "1.5.14")
    public static boolean ping(String url, int timeout) {

        // get rid of preceding http:// or https://
        if (url.startsWith("http://")) url = url.substring("http://".length());
        if (url.startsWith("https://")) url = url.substring("https://".length());

        // this is slightly idiotic, but whatever, we need it if some stupid firewall
        // redirects everything to a login page or smth along these lines
        String nonexistentUrl = "www.this_is_not_an_url_that_exists_as_far_as_I_know.gov";
        String canonicalHostNameForNonexistentUrl;
        try {
            canonicalHostNameForNonexistentUrl = InetAddress.getByName(nonexistentUrl).getCanonicalHostName();
        } catch (UnknownHostException e) {
            // yeah, kind of what it should be
            canonicalHostNameForNonexistentUrl = "";
        }

        try {
            if (canonicalHostNameForNonexistentUrl.equals(InetAddress.getByName(url).getCanonicalHostName())) {
                // seems like the firewall or smth is just blocking/forwarding/faking a page
                return false;
            }
        } catch (UnknownHostException e) {
            // if we cannot get the host for the url, it certainly is not a reachable url
            return false;
        }

        // use ping now
        String count = "";
        if (isWindows()) {
            count = "-n"; // for "number"
        } else if (isUnix() || isMac()) {
            count = "-c"; // for count
        } else {
            throw new UnsupportedOperationException("Only supported on windows, unix and mac!");
        }

        ProcessBuilder pb = new ProcessBuilder("ping", count, "1", url);

        // get rid of potentially blocking output
        if (isUnix() || isMac()) {
            pb.redirectError(new File("/dev/null"));
            pb.redirectOutput(new File("/dev/null"));
        } else if (isWindows()) {
            // this is untested!
            pb.redirectError(new File("NUL:"));
            pb.redirectOutput(new File("NUL:"));
        }

        try {
            return pb.start().waitFor() == 0;
        } catch (InterruptedException | IOException e) {
            return false;
        }
    }

    /**
     * Capitalizes the first letter of a String.
     *
     * @param str the String, e.g. "car"
     * @return the capitalized String, e.g. "Car"
     */
    public static final String capitalize(String str) {
        return str.substring(0, 1).toUpperCase(Locale.ENGLISH) + str.substring(1);
    }

    /**
     * Finds the local maven repository.
     *
     * @param dir the directory to run maven in. Often System.get("user.dir") will
     *            be what you want, but not necessarily always.
     * @return the absolute path to the local maven repository
     */
    public static String findLocalMavenRepo(String dir) {
        String cmd;
        if (isWindows()) {
            cmd = "mvn.cmd";
        } else {
            cmd = "cmd";
        }
        ProcessBuilder pb = new ProcessBuilder(cmd, "help:effective-settings");
        pb.directory(new File(dir));

        // get rid of potentially blocking output
        if (isUnix() || isMac()) {
            pb.redirectError(new File("/dev/null"));
        } else if (isWindows()) {
            pb.redirectError(new File("NUL:"));
        }

        try {
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            List<String> lines = new ArrayList<>();
            lines.add(reader.readLine());
            while (lines.get(lines.size() - 1) != null) {
                lines.add(reader.readLine());
            }

            StringBuilder sb = new StringBuilder();
            for (String l : lines) {
                if (l != null) sb.append(l).append("\n");
            }

            String stdout = sb.toString();
            Pattern pattern = Pattern.compile("(?<=<localRepository>).*(?=<\\/localRepository>)");
            Matcher m = pattern.matcher(stdout);
            if (m.find()) {
                return m.group();
            } else {
                logger.log(Level.SEVERE, "No maven repository found!");
                return "";
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Gets the effective pom. Potentially you want to call
     *
     * {@code MavenXpp3Reader mavenReader = new MavenXpp3Reader();}<br/>
     * {@code Model model = mavenReader.read(new ByteArrayInputStream(pom.getBytes()), true);}<br/>
     * afterwards.
     *
     * @param dir the directory to execute maven in. Often System.get("user.dir") will
     *            be what you want, but not necessarily always.
     * @return
     */
    public static String getEffectivePom(String dir) {
        String cmd;
        if (isWindows()) {
            cmd = "mvn.cmd";
        } else {
            cmd = "cmd";
        }
        ProcessBuilder pb = new ProcessBuilder(cmd, "help:effective-pom");
        pb.directory(new File(dir));

        // get rid of potentially blocking output
        if (isUnix() || isMac()) {
            pb.redirectError(new File("/dev/null"));
        } else if (isWindows()) {
            pb.redirectError(new File("NUL:"));
        }

        try {
            Process p = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            List<String> lines = new ArrayList<>();
            lines.add(reader.readLine());
            while (lines.get(lines.size() - 1) != null) {
                lines.add(reader.readLine());
            }

            StringBuilder sb = new StringBuilder();
            for (String l : lines) {
                if (l != null) sb.append(l).append("\n");
            }

            String stdout = sb.toString();
            Pattern pattern = Pattern.compile("<project.*<\\/project>", Pattern.DOTALL);
            Matcher m = pattern.matcher(stdout);
            if (m.find()) {
                return m.group();
            }
            throw new RuntimeException("Unable to determine the effective pom, "
                    + "as the <project>...</project> was not found in the response!");
        } catch (IOException e) {
            throw new UncheckedIOException("Unable to determine the effective pom!", e);
        }
    }

    /**
     * Transforms polar coordinates to cartesian coordinates.
     *
     * @param coords the polar coordinates (r,phi)
     * @return the cartesian coordinates (x,y)
     */
    public static final double[][] polarToCartesian(double[][] coords) {
        double[][] ret = new double[2][coords[0].length];
        for (int i = 0; i < ret[0].length; i++) {
            ret[0][i] = coords[0][i] * Math.cos(coords[1][i]);
            ret[1][i] = coords[0][i] * Math.sin(coords[1][i]);
        }
        return ret;
    }

    /**
     * Transforms cartesian coordinates to polar coordinates.
     *
     * @param coords the cartesian coordinates (x,y)
     * @return the polar coordinates (r,phi)
     */
    public static final double[][] cartesianToPolar(double[][] coords) {
        double[][] ret = new double[2][coords[0].length];
        for (int i = 0; i < ret[0].length; i++) {
            ret[0][i] = Math.sqrt(Math.pow(coords[0][i], 2) + Math.pow(coords[1][i], 2));
            ret[1][i] = Math.atan2(coords[1][i], coords[0][i]);
        }
        return ret;
    }

    /**
     * Transforms polar 2D coordinates into 3D cartesian coordinates.
     *
     * @param r      the r positions
     * @param phi    the phi positions
     * @param origin the origin of the polar plane
     * @param vec1   the first of the polar plane vectors in 3D
     * @param vec2   the second of the polar plane vectors in 3D
     * @return the 3D coordinates
     */
    public static final double[][] polarTo3D(double[] r, double[] phi, double[] origin, double[] vec1, double[] vec2) {
        double[][] xy = polarToCartesian(new double[][] { r, phi });
        double[][] ret = new double[3][r.length];
        for (int i = 0; i < ret[0].length; i++) {
            double[] p = new double[origin.length];
            for (int j = 0; j < p.length; j++) {
                p[j] = origin[j] + xy[0][i] * vec1[j] + xy[1][i] * vec2[j];
            }
            ret[0][i] = p[0];
            ret[1][i] = p[1];
            ret[2][i] = p[2];
        }
        return ret;
    }

    /**
     * This method is intended to be similar to the slicing that on can do in
     * python. If primitive/unstructured arrays are passed in as a source, they will
     * be deep copied.
     *
     * @param <T>       the return type
     * @param source    the source array
     * @param condition the condition, e.g. something along the lines of
     *                  {@code i->anotherArrayOfSameLength[i]==SomeValue}
     * @return the shrunk array
     */
    @SuppressWarnings("unchecked")
    public static final <T> T where(T source, IntPredicate condition) {
        Objects.requireNonNull(source);

        if (source instanceof double[]) {
            return (T) where((double[]) source, condition);
        } else if (source instanceof int[]) {
            return (T) where((int[]) source, condition);
        }

        if (!source.getClass().isArray()) {
            throw new IllegalArgumentException("source needs to be an array!");
        }
        int sourceLength = Array.getLength(source);
        boolean[] check = new boolean[sourceLength];
        int returnLength = 0;
        for (int i = 0; i < check.length; i++) {
            check[i] = condition.test(i);
            if (check[i]) returnLength++;
        }
        Object obj = Array.newInstance(source.getClass().getComponentType(), returnLength);
        int counter = 0;
        for (int i = 0; i < check.length; i++) {
            if (check[i]) {
                Array.set(obj, counter, cloneNonStructuredObject(Array.get(source, i)));
                counter++;
            }
        }
        if (getElementType(source.getClass()).isPrimitive()) obj = unbox(obj);
        return (T) obj;
    }

    /**
     * This method is intended to be similar to the slicing that on can do in
     * python.
     *
     * @param <T>       the return type
     * @param source    the source array
     * @param condition the condition, e.g. something along the lines of
     *                  {@code i->anotherArrayOfSameLength[i]==SomeValue}
     * @return the shrunk array
     */
    private static final int[] where(int[] source, IntPredicate condition) {
        int sourceLength = source.length;
        boolean[] check = new boolean[sourceLength];
        int returnLength = 0;
        for (int i = 0; i < check.length; i++) {
            check[i] = condition.test(i);
            if (check[i]) returnLength++;
        }
        int[] result = new int[returnLength];
        int counter = 0;
        for (int i = 0; i < check.length; i++) {
            if (check[i]) {
                result[counter++] = source[i];
            }
        }
        return result;
    }

    /**
     * This method is intended to be similar to the slicing that on can do in
     * python.
     *
     * @param <T>       the return type
     * @param source    the source array
     * @param condition the condition, e.g. something along the lines of
     *                  {@code i->anotherArrayOfSameLength[i]==SomeValue}
     * @return the shrunk array
     */
    private static final double[] where(double[] source, IntPredicate condition) {
        int sourceLength = source.length;
        boolean[] check = new boolean[sourceLength];
        int returnLength = 0;
        for (int i = 0; i < check.length; i++) {
            check[i] = condition.test(i);
            if (check[i]) returnLength++;
        }
        double[] result = new double[returnLength];
        int counter = 0;
        for (int i = 0; i < check.length; i++) {
            if (check[i]) {
                result[counter++] = source[i];
            }
        }
        return result;
    }
    /**
     * <pre>
     * Checks if a string is a valid file system path.
     * Null safe.
     *
     * Calling examples:
     *    isValidPath("c:/test");      // returns true
     *    isValidPath("c:/te:t");      // returns false
     *    isValidPath("c:/te?t");      // returns false
     *    isValidPath("c/te*t");       // returns false
     *    isValidPath("good.txt");     // returns true
     *    isValidPath("not|good.txt"); // returns false
     *    isValidPath("not:good.txt"); // returns false
     * </pre>
     *
     * @param path the file path to check
     * @return true if path is a valid file path
     */
    public static boolean isValidFilePath(String path) {
        // we limit paths to 199 chars, which is a bit anachronistic, but windows
        // explorer seems to go nuts if one goes above that
        if (path == null || path.length() >= 200) {
            return false;
        }

        try {
            Paths.get(path);
        } catch (InvalidPathException ex) {
            return false;
        }
        return true;
    }

    /**
     * Gets a progress bar, including elapsed time and predicted remaining time.
     * Ends with {@code \r}.
     *
     * @param pre       the message to display before the progress bar, e.g.
     *                  "Calculating matrices"
     * @param counter   the current iteration. In most for-loops this should be
     *                  "i+1"
     * @param max       the total number of iterations
     * @param length    the length of the progress bar in characters
     * @param nanoStart the {@link System#nanoTime()} the progress started, so
     *                  typically the time is stored in a local variable before the
     *                  for-loop.
     * @param post      a message to post after the predicted remaining time. Can be
     *                  useful e.g. to indicate different stages of the loop.
     * @return the formatted String
     */
    public static final String progressBar(String pre, long counter, long max, int length, long nanoStart, String post) {
        double percent = 100 * counter / (double) max;

        String progress;
        String remaining;
        if (percent == 100) {
            progress = "=".repeat(length);
            remaining = "";
        } else {
            int numRepeats = (int) Math.max(0, Math.floor(percent * length / 100 ));
            progress = "=".repeat(numRepeats) + ">";
            remaining = " ".repeat(length - progress.length());
        }

        int numberOfMaxDigits = Long.toString(max).length();

        String fs = "%s %5.1f%% [%s%s] %" + numberOfMaxDigits + "d/%d";

        long now = System.nanoTime();
        long timeDelta = now - nanoStart;

        // only shows completely elapsed time (truncated seconds!)
        long hours = TimeUnit.HOURS.convert(timeDelta, TimeUnit.NANOSECONDS);
        long minutes = TimeUnit.MINUTES.convert(timeDelta, TimeUnit.NANOSECONDS) - TimeUnit.MINUTES.convert(hours, TimeUnit.HOURS);
        long seconds = TimeUnit.SECONDS.convert(timeDelta, TimeUnit.NANOSECONDS) - TimeUnit.SECONDS.convert(hours, TimeUnit.HOURS) - TimeUnit.SECONDS.convert(minutes, TimeUnit.MINUTES);

        // do some rounding
        long estRemainingTime = (long) (timeDelta * (100 / percent - 1));
        double remainingSeconds = 1e-9 * estRemainingTime;
        long estSeconds = Math.round(remainingSeconds % 60);
        long estMinutes = (long) ((remainingSeconds - estSeconds) / 60 % 60);
        long estHours = (long) ((remainingSeconds - estSeconds - 60 * estMinutes) / 3600);

        fs += String.format(" (%1d:%02d:%02d/%1d:%02d:%02d)", hours, minutes, seconds, estHours, estMinutes, estSeconds);
        fs += " %s%s";

        String end = "";
        if (percent != 100) end = "\r";
        return String.format(Locale.ENGLISH, fs, pre, percent, progress, remaining, counter, max, post, end);
    }

    /**
     * Blends two colors.
     *
     * @param c1    the first color
     * @param c2    the second color
     * @param ratio the ratio between c1 and c2
     * @return the mixed color
     * @see <a href="https://stackoverflow.com/a/20332789/5599820">stackoverflow
     *      reference</a>
     */
    public static final Color blend(Color c1, Color c2, double ratio) {
        ratio = Math.min(1, ratio);
        ratio = Math.max(0, ratio);
        double iRatio = 1.0 - ratio;

        int i1 = c1.getRGB();
        int i2 = c2.getRGB();

        int a1 = (i1 >> 24 & 0xff);
        int r1 = ((i1 & 0xff0000) >> 16);
        int g1 = ((i1 & 0xff00) >> 8);
        int b1 = (i1 & 0xff);

        int a2 = (i2 >> 24 & 0xff);
        int r2 = ((i2 & 0xff0000) >> 16);
        int g2 = ((i2 & 0xff00) >> 8);
        int b2 = (i2 & 0xff);

        int a = (int)((a1 * iRatio) + (a2 * ratio));
        int r = (int)((r1 * iRatio) + (r2 * ratio));
        int g = (int)((g1 * iRatio) + (g2 * ratio));
        int b = (int)((b1 * iRatio) + (b2 * ratio));

        return new Color(a << 24 | r << 16 | g << 8 | b);
    }


    /**
     * Checks whether a display is supported.
     *
     * @return true if no display is supported
     * @see <a href="https://stackoverflow.com/a/16611566/5599820">stackoverflow
     *      reference</a>
     */
    private static final boolean isDisplaySupported() {
        if (GraphicsEnvironment.isHeadless()) {
            return false;
        }
        try {
            GraphicsDevice[] screenDevices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
            return !(screenDevices == null || screenDevices.length == 0);
        } catch (HeadlessException e) {
            // this should not happen, as we check for headlessness as it would throw here
            // explicitly beforehand
            return false;
        }
    }

    /**
     * Read a String from the URL. Returns a default empty signal response if
     * nothing found. Ugly, but (hopefully) doesn't break within Jars. Not slower
     * than the old method. Retries 10 times.
     *
     * @param url the URL
     * @return the resulting String
     */
    public static final String readUrl(String url) {
        return readUrl(url, 10);
    }

    /**
     * Read a String from the URL. Returns a default empty signal response if
     * nothing found. Ugly, but (hopefully) doesn't break within Jars. Not slower
     * than the old method.
     *
     * @param url the URL
     * @return the resulting String
     */
    public static final String readUrl(String url, int numRetries) {
        // !!!
        // SHOULD PROBABLY USE java.net.http AT SOME POINT IN THE FUTURE (JAVA 10+)
        // !!!
        HttpURLConnection connection = null;
        String retVal = null;
        try {
            URL resetEndpoint = new URL(url);
            connection = (HttpURLConnection) resetEndpoint.openConnection();
            // Set request method to GET as required from the API
            connection.setRequestMethod("GET");
            StringBuilder sb = new StringBuilder();
            // Read the response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));) {
                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            }
            retVal = sb.toString();
        } catch (Exception e) {
            String msg = "";
            if (connection != null) {
                InputStream is = connection.getErrorStream();
                if (is != null) {
                    msg = new BufferedReader(new InputStreamReader(is)).lines().collect(Collectors.joining("\n"));
                }
            }
            if (msg.isEmpty() && numRetries > 0) {
                try {
                    Thread.sleep(1_000L + new Random().nextInt(1_000));
                } catch (InterruptedException e1) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("Interrupted during wait to retry readURL(). Aborting.", e1);
                }
                if (connection != null) connection.disconnect();
                logger.fine(() -> String.format("Retrying %s, remaining tries: %d", url, numRetries-1));
                return readUrl(url, numRetries-1);
            } else {
                return msg;
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return retVal;
    }


    private static final String[] RN_M = {"", "M", "MM", "MMM"};
    private static final String[] RN_C = {"", "C", "CC", "CCC", "CD", "D", "DC", "DCC", "DCCC", "CM"};
    private static final String[] RN_X = {"", "X", "XX", "XXX", "XL", "L", "LX", "LXX", "LXXX", "XC"};
    private static final String[] RN_I = {"", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX"};

    /**
     * Gets the Roman numeral corresponding to a a given number.
     *
     * @param number, needs to be positive and &lt;4000
     * @return the Roman numeral
     * @see https://stackoverflow.com/a/39429499/5599820
     */
    public static final String romanNumeral(int number) {
        int minValue = 1;
        int maxValue = 3999;
        if (number < minValue || number > maxValue) {
            throw new IllegalArgumentException(
                    String.format("The number must be in the range [%d, %d]", minValue, maxValue));
        }

        return new StringBuilder()
                .append(RN_M[number / 1000])
                .append(RN_C[number % 1000 / 100])
                .append(RN_X[number % 100 / 10])
                .append(RN_I[number % 10])
                .toString();
    }


    /**
     * Backport replacement for java11's String.isBlank
     * 
     * @deprecated Use {@link String#isBlank()}
     */
    @Deprecated(forRemoval = true, since = "1.5.14")
    public static boolean isBlankBackport(String str) {
        throw new RuntimeException("Use String.isBlank()");
//        return str == null || str.replaceAll("\\s", "").length() == 0;
    }


    /**
     * Parses a non-primitive type to its corresponding class.
     *
     * @param className the name of the class. May not be null. Classes in package
     *                  "java.lang" do not need the class name to be fully
     *                  qualified.
     * @return the class corresponding to the given type
     */
    private static Class<?> parseNonprimitiveType(String className) {
        String fqn = className.contains(".") ? className : "java.lang.".concat(className);
        try {
            return Class.forName(fqn);
        } catch (ClassNotFoundException ex) {
            throw new IllegalArgumentException("Class not found: " + fqn);
        }
    }

    /**
     * Gets the superclasses, excluding {@link Object}.
     *
     * @param <T> the type of the class
     * @param <R> the type of the superclasses
     * @param cls the class, if <code>null</code> an empty list is returned
     * @return the superclasses
     */
    private static final <T,R extends T> List<Class<R>> superclassesWithoutObject(Class<T> cls) {
        if (cls == null) {
            return new ArrayList<>();
        }

        List<Class<R>> ret = new ArrayList<>();
        Class<?> tmpCls = cls;
        while (tmpCls != null && tmpCls != Object.class) {
            ret.add((Class<R>) tmpCls);
            tmpCls = tmpCls.getSuperclass();
        }
        return ret;
    }

    /**
     * Get the raw bytes of a serialized object.
     *
     * @param object object to serialize (via standard Java serialization)
     * @return serialized representation of the given object
     */
    public static byte[] bytesSerialisableObject(Serializable object) {
        byte[] bytes = null;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(bos)) {
            oos.writeObject(object);
            bytes = bos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        return bytes;
    }


    /**
     * Get a hex String representation (0-9, a-f) of a given byte array. A byte has
     * 8 bits. A single hex character (0-9, a-f) encodes 4 bits. Therefore, this
     * method returns two characters per input byte.
     *
     * @param bytes [n] input data
     * @return [2*n] hex String representation of the given byte array
     */
    public static String bytesToHex(byte[] bytes) {
        BigInteger bigInt = new BigInteger(1, bytes);
        String hashtext = bigInt.toString(16);

        while (hashtext.length() < bytes.length * 2) {
            hashtext = "0" + hashtext;
        }

        return hashtext;
    }

    /** Given a URL, replaces the hostname with the resolved IP address of the URL, cycling through
     * the possible addresses if mulitple are given.
     * 
     * This is useful on retries to webservices where multiple servers are available but one is broken
     * 
     * @param url
     * @param index Cycle index. The one used is Address[index % numAddresses]
     * @return
     */
	public static String cycleIPAddress(String url, int index) {
		try {
				URI uri = new URI(url);
			
			String hostname = uri.getAuthority();
			InetAddress[] addrs = InetAddress.getAllByName(hostname);
			InetAddress addr = addrs[index % addrs.length];
			
			uri = new URI(uri.getScheme().toLowerCase(Locale.US), addr.getHostAddress(),
						uri.getPath(), uri.getQuery(), uri.getFragment());
			
			return uri.toString();
			
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		} catch (UnknownHostException e) {
			throw new UncheckedIOException(e);
		}

	}

	/**
	 * Checks whether {@code path} is versioned according to the format {@code -X.X.X} with {@code X} representing an integer number, followed by {@code extension}.
	 * 
	 * @param path the path to check, not {@code null}
	 * @param extension the file path extension, may be empty but not {@code null}
	 * @return {@code true} if {@code path} is versioned
	 * @throws NullPointerException if {@code path} or {@code extension} is {@code null}
	 */
	public static final boolean isVersioned(Path path, String extension) {
	    return isVersioned(path.toFile(), extension);
	}

	/**
     * Checks whether {@code file} is versioned according to the format {@code -X.X.X} with {@code X} representing an integer number, followed by {@code extension}.
     * 
     * @param file the file to check, not {@code null}
     * @param extension the file path extension, may be empty but not {@code null}
     * @return {@code true} if {@code path} is versioned
     * @throws NullPointerException if {@code path} or {@code extension} is {@code null}
     */
    public static final boolean isVersioned(File file, String extension) {
        Objects.requireNonNull(file);
        Objects.requireNonNull(extension);

        String regexExtension = extension.replace(".", "\\.");

        return file.getName().matches(".*(?<version>-\\d+\\.\\d+\\.\\d"+regexExtension+")");
    }
}
