package cqf.bcd.specs;

import cqf.copula.Copula;
import cqf.core.State.CopulaType;
import cqf.core.State.RandomEngineType;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class SimulationSpecs {
    private double recoveryRate;
    private int noOfSimulations;
    private CopulaType copulaType;
    private RandomEngineType engineType;
    private Copula copula;

    public Copula getCopula() {
        return copula;
    }

    public void setCopula(Copula copula) {
        this.copula = copula;
    }

    public CopulaType getCopulaType() {
        return copulaType;
    }

    public void setCopulaType(CopulaType copulaType) {
        this.copulaType = copulaType;
    }

    public RandomEngineType getEngineType() {
        return engineType;
    }

    public void setEngineType(RandomEngineType engineType) {
        this.engineType = engineType;
    }

    public int getNoOfSimulations() {
        return noOfSimulations;
    }

    public void setNoOfSimulations(int noOfSimulations) {
        this.noOfSimulations = noOfSimulations;
    }

    public double getRecoveryRate() {
        return recoveryRate;
    }

    public void setRecoveryRate(double recoveryRate) {
        this.recoveryRate = recoveryRate;
    }

}
