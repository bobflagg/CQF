/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cqf.bcd.specs;

import cqf.reference.Entity;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.swing.AbstractListModel;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class BCDContract extends AbstractListModel {
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    private List<Entity> entities;
    private Entity selectedEntity;
    private double notional;
    private int k = 1;
    private double recoveryRate;
    private Date effectiveDate;
    private Date maturityDate;
    private String frequency; // Monthly, Quarterly, Semiannually, Yearly
    private String basis;
    private boolean premiumAccrued;
    private int noOfSimulations = 5000;
    private String copula;
    private double[][] correlationData;
    private List<ContractObserver> observers = new ArrayList<ContractObserver>();

    public Entity getSelectedEntity() {
        return selectedEntity;
    }

    public void setSelectedEntity(Entity selectedEntity) {
        this.selectedEntity = selectedEntity;
        notifyEntitySelected();
    }

    public void addObserver(ContractObserver observer) {
        observers.add(observer);
    }

    public void notifyEntitySelected() {
        for (ContractObserver observer:observers) observer.entitySelected();
    }
    public void notifyCorrelationUpdated() {
        for (ContractObserver observer:observers) observer.correlationUpdated();

    }

    public void callibrate() {
        Random random = new Random();
        correlationData = new double[getSize()][getSize()];
        for (int i=0;i<getSize();i++) {
            for (int j=0;j<i;j++) {
                correlationData[i][j] = random.nextDouble();
                correlationData[j][i] = correlationData[i][j];
            }
            correlationData[i][i] = 1.0;
        }
        notifyCorrelationUpdated();
    }

    public double[][] getCorrelationData() {
        if (correlationData == null) callibrate();
        return correlationData;
    }
    public String[] tickers() {
        String[] tickers = new String[getSize()];
        for (int i=0;i<getSize();i++) tickers[i] = entities.get(i).getTicker();
        return tickers;
    }
    public String getCopula() {
        return copula;
    }

    public void setCopula(String copula) {
        this.copula = copula;
    }

    public int getNoOfSimulations() {
        return noOfSimulations;
    }

    public void setNoOfSimulations(int noOfSimulations) {
        this.noOfSimulations = noOfSimulations;
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer("BCDContract:\n");
        buffer.append(String.format("\tNotional: %.2f\n",notional));
        buffer.append(String.format("\tnoOfNames: %d\n",getSize()));
        buffer.append(String.format("\tkth: %d\n",k));
        buffer.append(String.format("\tdelta: %.2f\n",getDelta()));
        buffer.append(String.format("\tm: %d\n",(int) (getT() / getDelta())));
        buffer.append(String.format("\trecoveryRate: %.2f\n",getRecoveryRate()));
        buffer.append(String.format("\tT: %.2f\n",getT()));
        buffer.append(String.format("\tEffective Date: "+DATE_FORMAT.format(effectiveDate)+"\n"));
        buffer.append(String.format("\tMaturity Date: "+DATE_FORMAT.format(maturityDate)+"\n"));
        buffer.append(String.format("\tFrequency: "+frequency+"\n"));
        buffer.append(String.format("\tBasis: "+basis+"\n"));
        buffer.append(String.format("\tPremium Accrued: "+premiumAccrued+"\n"));
        return buffer.toString();
    }

    public double getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(double recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        System.out.println("setting k to "+k);
        this.k = k;
    }

    public void setBasis(String basis) {
        this.basis = basis;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public void setMaturityDate(Date maturityDate) {
        this.maturityDate = maturityDate;
    }

    public void setNotional(double notional) {
        System.out.println("setNotional --->"+notional);
        this.notional = notional;
    }

    public BCDContract() {
        entities = new ArrayList<Entity>();
    }

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

    public void setPremiumAccrued(boolean premiumAccrued) {
        this.premiumAccrued = premiumAccrued;
    }

    public double getDelta() {
        if (frequency.equals("Monthly")) return 0.08333333;
        if (frequency.equals("Quarterly")) return 0.25;
        if (frequency.equals("Semiannually")) return 0.5;
        return 1.0;
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
