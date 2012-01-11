package cqf.bcd.specs;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class Specs {
    private ContractSpecs contractSpecs;
    private SimulationSpecs simulationSpecs;

    public Specs() {
        contractSpecs = new ContractSpecs();
        simulationSpecs = new SimulationSpecs();
    }

    public ContractSpecs getContractSpecs() {
        return contractSpecs;
    }

    public SimulationSpecs getSimulationSpecs() {
        return simulationSpecs;
    }

}
