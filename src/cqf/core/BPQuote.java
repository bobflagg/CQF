package cqf.core;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class BPQuote extends Quote {

    public BPQuote(double bid, double ask) {
        super(bid/100.0, ask/100.0);
    }

    public BPQuote(double quote) {
        this(quote,quote);
    }

}
