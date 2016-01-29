package ir;

/**
 * A simple stopwatch.
 *
 * Created by swebo_000 on 2016-01-29.
 */
public class Stopwatch {

    private long start;

    public void start() {
        start = System.currentTimeMillis();
    }

    public long stop() {
        long stop = System.currentTimeMillis();
        return stop - start;
    }
}
