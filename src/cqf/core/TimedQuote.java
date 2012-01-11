package cqf.core;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class TimedQuote extends Quote {
    protected double time;

    public TimedQuote(double time, double bid, double ask) {
        super(bid, ask);
        this.time = time;
    }

    public TimedQuote(double time, double quote) {
        this(time,quote,quote);
    }

    public double getTime() {
        return time;
    }

}
