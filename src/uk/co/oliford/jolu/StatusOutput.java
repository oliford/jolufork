package uk.co.oliford.jolu;

import java.text.DecimalFormat;

/**
 * A little common status outputter, for classes that don't know if they're
 * going to be taking a long time or not. Construct with the maximum count of
 * the loop and call .doStatus() on each iteration. Nothing is outputted until
 * the 'user attention span' limit is reached, at which point the name will be
 * outputted, followed by a % complete status every update period. At crude
 * attempt at estimated time left is made.
 */
public final class StatusOutput {

    // This class probably could make good use of OneLiners.progressBar

    private static final long millisecondsToNanoseconds = 1_000_000;

    private DecimalFormat fmt = new DecimalFormat("##.##");

    private long initTime;
    private long n;
    private String firstText;
    private long lastTime;
    private boolean active;
    private int charsThisLine;
    private int maxCharsOnLine = 160;
    private double lastPC;

    private long attentionSpan;
    private long updatePeriod;

    private void defaultTiming() {
        attentionSpan =  millisecondsToNanoseconds*Integer.parseInt(SettingsManager.defaultGlobal().getProperty("minerva.user.attentionSpanMilisecs", "5000"));
        updatePeriod = millisecondsToNanoseconds*Integer.parseInt(SettingsManager.defaultGlobal().getProperty("minerva.user.updatePeriod", "1000"));
    }

    public StatusOutput(long n) {
        StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
        StackTraceElement e = stacktrace[2]; // maybe this number needs to be corrected
        this.firstText = e.getClassName() + "." + e.getMethodName() + "()";
        this.n = n;
        this.initTime = System.nanoTime();
        this.active = false;
        defaultTiming();
    }

    /**
     * Creates a StatusOutput class.
     *
     * @param caller The class that creates this object. The class name will be displayed in the output.
     * @param n The number of tasks to do.
     */
    public StatusOutput(Class<?> caller, long n) {
        this.firstText = caller.getCanonicalName();
        this.n = n;
        this.initTime = System.nanoTime();
        this.active = false;
        defaultTiming();
    }

    /**
     * Creates a StatusOutput class.
     *
     * @param firstText A string with the text to show in the output.
     * @param n The number of tasks to do.
     */
    public StatusOutput(String firstText, long n) {
        this.firstText = firstText;
        this.n = n;
        this.initTime = System.nanoTime();
        this.active = false;
        defaultTiming();
    }

    public StatusOutput(Class<?> caller, long n, int attentionSpan, int updatePeriod) {
        this.firstText = caller.getCanonicalName();
        this.n = n;
        this.initTime = System.nanoTime();
        this.active = false;
        this.attentionSpan = millisecondsToNanoseconds*attentionSpan;
        this.updatePeriod = millisecondsToNanoseconds*updatePeriod;
    }

    public StatusOutput(String firstText, long n, int attentionSpan, int updatePeriod) {
        this.firstText = firstText;
        this.n = n;
        this.initTime = System.nanoTime();
        this.active = false;
        this.attentionSpan = millisecondsToNanoseconds*attentionSpan;
        this.updatePeriod = millisecondsToNanoseconds*updatePeriod;
    }

    /**
     * Call for each task done/todo. The stdout will be updated if the time from the creation of this class is larger than
     * the minerva-settings property {@code minerva.user.attentionSpanMilisecs}, and will update every {@code minerva.user.updatePeriod}.
     *
     * @param i The task index.
     * @return true if it actually outputs something
     */
    public final boolean doStatus(int i) {
        long now = System.nanoTime();
        if (!active && (now - initTime) > attentionSpan) {
            //activate
            active = true;
            System.out.print(firstText + ": ");
            charsThisLine += firstText.length() + 2;
            lastTime = 0;
            lastPC = -1;
        }
        if (active && (now - lastTime) > updatePeriod) {
            double nowPC =  i*100.0 / n;

            //make sure we're outputting with enough precision that it actually changes
            int reqDigits = -((int)Math.log10(lastPC - nowPC)) + 2;
            fmt.setMinimumFractionDigits(reqDigits);
            fmt.setMaximumFractionDigits(reqDigits);

            String outText = fmt.format(nowPC) + "% ";
            System.out.print(outText);
            charsThisLine += outText.length();
            if (charsThisLine > maxCharsOnLine) {
                // estimate ETA
                long estTimeSecs = (n - i) * (now - initTime) / i / 1_000_000_000;
                long estTimeMin = estTimeSecs / 60;
                estTimeSecs -= estTimeMin * 60;
                outText = firstText + "[" + (estTimeMin > 0 ? estTimeMin + "m " : "") + estTimeSecs + "s]: ";

                charsThisLine += outText.length();
                System.out.print("\n" + outText);
                charsThisLine = 0;
            }
            lastTime = System.nanoTime();
            lastPC = nowPC;

            return true;
        }
        return false;
    }

    /**
     * Call this method when all tasks are done, which will print a "Done" message
     * to the stdout.
     */
    public void done() {
        if(active)
            System.out.println(firstText + ": Done (" + (System.nanoTime() - initTime)/ 1_000_000_000 + "s)");
    }
}
