package cqf.bcd.specs;

import cqf.core.State.BasisType;
import cqf.core.State.Frequency;
import cqf.reference.Entity;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.swing.AbstractListModel;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class ContractSpecs extends AbstractListModel {
    private List<Entity> entities = new ArrayList<Entity>();
    private double notional;
    private int k = 1;
    private Date effectiveDate;
    private Date maturityDate;
    private Frequency frequency;
    private BasisType basisType;
    private boolean premiumAccrued;

    public List<Entity> getEntities() {
        return entities;
    }

    public void addEntity(Entity entity) {
        if (entities.contains(entity)) return;
        entities.add(entity);
        fireContentsChanged(this,0,entities.size()-1);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
        fireContentsChanged(this,0,entities.size()-1);
    }

    public void removeAllEntities() {
        entities.clear();
        fireContentsChanged(this,0,entities.size()-1);
    }

    public int getSize() {
        return entities.size();
    }

    public Object getElementAt(int index) {
        return entities.get(index);
    }

    public BasisType getBasisType() {
        return basisType;
    }

    public void setBasisType(BasisType basisType) {
        this.basisType = basisType;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        this.frequency = frequency;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    public Date getMaturityDate() {
        return maturityDate;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public double getNotional() {
        return notional;
    }

    public void setNotional(double notional) {
        this.notional = notional;
    }

    public boolean isPremiumAccrued() {
        return premiumAccrued;
    }

    public void setPremiumAccrued(boolean premiumAccrued) {
        this.premiumAccrued = premiumAccrued;
    }

    public int getNoOfPeriods() {
        return (int) Math.ceil(getT() / frequency.getDelta());
    }

    private static final double MILLISECOND_TO_YEAR = 365.2425*24.0*60.0*60.0*1000.0;
    public double getT() {
	double diff = DifferenceInMilliseconds(maturityDate, effectiveDate);
	return diff / MILLISECOND_TO_YEAR;
    }

    private static double DifferenceInMilliseconds(Date date1, Date date2)
    {
	return Math.abs(GetTimeInMilliseconds(date1) - GetTimeInMilliseconds(date2));
    }

    private static long GetTimeInMilliseconds(Date date)
    {
	Calendar cal = Calendar.getInstance();
	cal.setTime(date);
	return cal.getTimeInMillis() + cal.getTimeZone().getOffset(cal.getTimeInMillis());
    }

}
