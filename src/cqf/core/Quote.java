package cqf.core;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Quote {
    protected double bid, ask;

    public Quote(double bid, double ask) {
        this.bid = bid/100.0;
        this.ask = ask/100.0;
    }

    public Quote(double quote) {
        this(quote,quote);
    }

    public double getAsk() {
        return ask;
    }

    public double getBid() {
        return bid;
    }

    public double average() {
        return 0.5*(ask+bid);
    }
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Quote other = (Quote) obj;
        if (this.bid != other.bid) {
            return false;
        }
        if (this.ask != other.ask) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.bid) ^ (Double.doubleToLongBits(this.bid) >>> 32));
        hash = 47 * hash + (int) (Double.doubleToLongBits(this.ask) ^ (Double.doubleToLongBits(this.ask) >>> 32));
        return hash;
    }


}
