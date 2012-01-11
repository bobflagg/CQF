package cqf.cds;

// http://www.quantobjects.com/index.php?id=34
import cern.jet.random.Normal;
import cern.jet.random.engine.RandomEngine;
import cqf.core.Basis;
import cqf.distribution.Distribution;
import cqf.interest.Discounter;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import org.jfin.date.BusinessDayConvention;
import org.jfin.date.Frequency;
import org.jfin.date.Period;
import org.jfin.date.ScheduleException;
import org.jfin.date.ScheduleGenerator;
import org.jfin.date.StubType;
import org.jfin.date.daycount.DaycountCalculator;
import org.jfin.date.daycount.DaycountCalculatorFactory;
import org.jfin.date.holiday.HolidayCalendar;
import org.jfin.date.holiday.HolidayCalendarFactory;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class CreditDefaultSwap {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    public static final HolidayCalendar HOLIDAY_CALENDAR = HolidayCalendarFactory.newInstance().getHolidayCalendar("WE");
    public static final int FIXING_BUSINESS_DAY_OFFSET = -2;
    public static final int DISCRETATION = 3;
    protected double notional;
    protected Calendar effectiveDate;
    protected Calendar maturityDate;
    protected Frequency frequency;
    protected boolean premiumAccrued;
    protected Basis basis;
    protected BusinessDayConvention businessDayConvention;
    protected DaycountCalculator daycountBasis;
    protected List<Period> schedule;

    public CreditDefaultSwap(
            double notional,
            Calendar effectiveDate,
            Calendar maturityDate,
            Frequency frequency,
            boolean premiumAccrued,
            String basis,
            BusinessDayConvention businessDayConvention) throws Exception {
        this.notional = notional;
        this.effectiveDate = effectiveDate;
        this.maturityDate = maturityDate;
        this.frequency = frequency;
        this.premiumAccrued = premiumAccrued;
        daycountBasis = dayCountCalculator(basis);
        this.businessDayConvention = businessDayConvention;
        schedule = buildSchedule();
    }

    public double premiumLeg(double spread, Distribution distribution, Discounter discounter) {
        double value = 0.0, time=0.0;
        Iterator<Period> iterator = schedule.iterator();
        while (iterator.hasNext()) {
            Period period = adjust(iterator.next());
            double delta = dayCountFraction(period);
            time += delta;
            value += delta*discounter.discountFactor(time)*distribution.Q(time);
        }
        return spread*value*notional;
    }

    public double premiumLegFactor(Distribution distribution, Discounter discounter, double defaulTime) {
        double value=0.0, time=0.0;
        Iterator<Period> iterator = schedule.iterator();
        while (iterator.hasNext()) {
            Period period = adjust(iterator.next());
            double delta = dayCountFraction(period);
            double discount = discounter.discountFactor(time+delta);
            if (defaulTime <= time+delta) {
                value += (defaulTime-time)*discount;
                break;
            }
            time += delta;
            value += delta*discount;
        }
        return value;
    }
    public double protectionLeg(double recovery, Distribution distribution, Discounter discounter) {
        double value = 0.0, time=0.0;
        Calendar startDate = (Calendar) effectiveDate.clone();
        Calendar endDate = (Calendar) effectiveDate.clone();
        while (endDate.before(maturityDate)) {
            endDate.set(Calendar.MONTH, endDate.get(Calendar.MONTH)+DISCRETATION);
            //System.out.println(DATE_FORMAT.format(startDate.getTime())+" - "+DATE_FORMAT.format(endDate.getTime()));
            double delta = dayCountFraction(startDate, endDate);
            value += discounter.discountFactor(time+delta)*(distribution.Q(time)-distribution.Q(time+delta));
            time += delta;
            startDate.set(Calendar.MONTH, startDate.get(Calendar.MONTH)+DISCRETATION);
        }
        return (1-recovery)*value*notional;
    }

    // Assuming monthly discretization
    public double protectionLegFactor(double recovery, Distribution distribution, Discounter discounter, double defaulTime) {
        double time=0.0;
        Calendar startDate = (Calendar) effectiveDate.clone();
        Calendar endDate = (Calendar) effectiveDate.clone();
        while (endDate.before(maturityDate)) {
            endDate.set(Calendar.MONTH, endDate.get(Calendar.MONTH)+DISCRETATION);
            //System.out.println(DATE_FORMAT.format(startDate.getTime())+" - "+DATE_FORMAT.format(endDate.getTime()));
            double delta = dayCountFraction(startDate, endDate);
            double discount = discounter.discountFactor(time+delta);
            if (defaulTime <= time+delta) { // default occurred in ith period
                return (1 - recovery) * discount;
            }
            time += delta;
            startDate.set(Calendar.MONTH, startDate.get(Calendar.MONTH)+DISCRETATION);
        }
        return 0.0;
    }

    public double parSpreadMC(
        double recovery,
        Distribution distribution,
        Discounter discounter,
        RandomEngine engine,
        int noSimulations
    ) throws Exception {
        Normal normal = new Normal(0.0, 1.0, engine);
        double dlSum = 0;
        double plSum = 0;
        for (int i = 0; i < noSimulations; i++) {
            double z = normal.nextDouble();
            double u = normal.cdf(z);
            double defaulTime = distribution.inverseCdf(u);
            dlSum += protectionLegFactor(recovery, distribution, discounter, defaulTime);
            plSum += premiumLegFactor(distribution, discounter, defaulTime);
        }
        if (plSum > 0.0) {
            return dlSum / plSum;
        }
        throw new Exception("Got zero for premium leg factor in CDS MC simulation.");
    }

    public double parSpread(double recovery, Distribution distribution, Discounter discounter) throws Exception {
        double pLeg = premiumLeg(1.0, distribution, discounter);
        double dLeg = protectionLeg(recovery, distribution, discounter);
        if (pLeg > 0.0) {
            return dLeg / pLeg;
        }
        throw new Exception("Got zero for premium leg factor in CDS par spread calculation.");
    }

    public List<Period> buildSchedule() throws ScheduleException {
        return ScheduleGenerator.generateSchedule(effectiveDate, maturityDate, frequency, StubType.SHORT_FIRST);
    }


    public Period adjust(Period period) {
        return HOLIDAY_CALENDAR.adjust(period, businessDayConvention);
    }

    public Calendar fixingDate(Period period) {
        return HOLIDAY_CALENDAR.advanceBusinessDays(
                (Calendar) period.getStartCalendar().clone(), FIXING_BUSINESS_DAY_OFFSET);
    }
    public double dayCountFraction(String start, String end) throws ParseException {
        Calendar startDate = new GregorianCalendar();
        startDate.setTime(DATE_FORMAT.parse(start));
        Calendar endDate = new GregorianCalendar();
        endDate.setTime(DATE_FORMAT.parse(end));
        Calendar startDateAdj = HOLIDAY_CALENDAR.adjust(startDate, BusinessDayConvention.FOLLOWING);
        Calendar endDateAdj = HOLIDAY_CALENDAR.adjust(endDate, BusinessDayConvention.FOLLOWING);
        return daycountBasis.calculateDaycountFraction(startDateAdj, endDateAdj);
    }
    public double dayCountFraction(Calendar startDate, Calendar endDate) {
        Calendar startDateAdj = HOLIDAY_CALENDAR.adjust(startDate, BusinessDayConvention.FOLLOWING);
        Calendar endDateAdj = HOLIDAY_CALENDAR.adjust(endDate, BusinessDayConvention.FOLLOWING);
        return daycountBasis.calculateDaycountFraction(startDateAdj, endDateAdj);
    }

    public double dayCountFraction(Period period) {
        return daycountBasis.calculateDaycountFraction(period);
    }

    public static DaycountCalculator dayCountCalculator(String basis) throws Exception {
        DaycountCalculator daycountBasis;
        if (basis.equals("ACTACT")) {
            daycountBasis = DaycountCalculatorFactory.newInstance().getISDAActualActual();
        } else if (basis.equals("HIST")) {
            daycountBasis = DaycountCalculatorFactory.newInstance().getISMAActualActual();
        } else if (basis.equals("AFB")) {
            daycountBasis = DaycountCalculatorFactory.newInstance().getAFBActualActual();
        } else if (basis.equals("30360")) {
            daycountBasis = DaycountCalculatorFactory.newInstance().getUS30360();
        } else if (basis.equals("30E360")) {
            daycountBasis = DaycountCalculatorFactory.newInstance().getEU30360();
        } else if (basis.equals("ACT360")) {
            daycountBasis = DaycountCalculatorFactory.newInstance().getActual360();
        } else if (basis.equals("ACT365")) {
            daycountBasis = DaycountCalculatorFactory.newInstance().getActual365Fixed();
        } else {
            throw new Exception("Couldn't find day count calculator.");
        }
        return daycountBasis;
    }
}
