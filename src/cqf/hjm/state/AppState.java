/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cqf.hjm.state;

import cern.jet.random.engine.DRand;
import cern.jet.random.engine.MersenneTwister;
import cern.jet.random.engine.RandomEngine;
import cqf.core.State.RandomEngineType;
import cqf.core.State.Frequency;
import cqf.hjm.product.Cap;
import cqf.hjm.product.Product;
import cqf.hjm.product.ZeroCouponBond;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Bob Flagg <bob@calcworks.net>
 */
public class AppState {
    private List<StateManager> managers = new ArrayList<StateManager>();
    private static final RandomEngine engineMersenneTwister = new MersenneTwister();
    private static final RandomEngine engineDRand = new DRand();

    public Product getProduct() {
        Product product = null;
        switch(productType) {
            case ZCB: {
                product = new ZeroCouponBond(1.0, maturity);
                break;
            }
            case Cap: {
                product = new Cap(1.0, maturity, interestRate, getDelta());
                break;
            }
            default: {
                product = new Cap(1.0, maturity, interestRate, getDelta());
                break;
            }
        }
        return product;
    }

    public RandomEngine getEngine() {
        RandomEngine randomEngine = null;
        switch(engine) {
            case MersenneTwister: {
                randomEngine = engineMersenneTwister;
                break;
            }
            default: {
                randomEngine = engineDRand;
            }
        }
        return randomEngine;
    }

    public String productName() {
        switch(productType) {
            case ZCB: {
                return "zero coupon bond";
            }
            case Cap: {
                return "cap";
            }
            default: {
                return "floor";
            }
        }
    }
    public enum ProductType {ZCB, Cap, Floor}
    
    public ProductType productType;
    public double maturity;
    public double interestRate;
    public Frequency frequency;
    public RandomEngineType engine;
    public int noOfFactors;
    public int noOfSimulations;

    public double getDelta() {
        switch (frequency) {
            case Monthly: return 1.0/12.0;
            case Quarterly: return 0.25;
            case Semiannually: return 0.5;
            default: return 1.0;
        }
    }
    public void addManager(StateManager manager) {
        managers.add(manager);
    }

    public void refresh() {
        for (int i=0;i<managers.size();i++) managers.get(i).refreshState();
    }


}
