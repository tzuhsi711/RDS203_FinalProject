package model;

import java.util.Random;

public class Simulation {

    protected Random rand = new Random(203);

    private double detectionEffect; // calibrated mortality reduction multiplier
    
    // ----------------------------------
    // Store screening effect on mortality
    // ----------------------------------
    public Simulation(double detectionEffect) {
    	// store effect
        this.detectionEffect = detectionEffect;
    }
  
    // ----------------------------------
    // Simulate one year of life for an individual
    // a) Screening (if eligible)
    // b) Incidence
    // c) Mortality
    // d) Costs
    // e) QALYs and Life expectancy
    // ----------------------------------
    protected void simulateYear(Person p, int ageIndex, int currentAge,
                                int startAge, int stopAge, int frequency) {
    	
    	// if dead, stop simulation
        if (!p.alive) return;
        
        // ----------------------------------
        // a) Screening
        // ----------------------------------
        if (currentAge >= startAge && currentAge <= stopAge) { // screen within eligible range (45-74)
            if ((currentAge - startAge) % frequency == 0) { // control screening frequency
            	
            	// Total cost
                p.totalCost += Parameters.costFIT; // add FIT cost for both positive and negative cases
                
                // If has cancer, screening might detect cancer
                // Sensitivity = Correct classification of positive cases
                if (p.hasCancer && rand.nextDouble() < Parameters.sensitivityFIT) {
                    p.detected = true; // mark as correctly detected
                    p.totalCost += Parameters.costColonoscopy; // follow-up colonoscopy cost
                    p.totalQALY -= Parameters.disutilityColonoscopyDiscomfort; // QALYs loss from colonoscopy discomfort
                    
                    // Potential chance of complication from colonoscopy
                    if (rand.nextDouble() < Parameters.complicationRisk) {
//                    	p.totalQALY -= 0.13; // additional QALY loss
                    	p.totalQALY -= Parameters.disutilityColonoscopyComplication; // QALYs loss from colonoscopy complication
                    }
                }
                
                // If doesn't have cancer, screening might produce false positive
                // Specificity = Correct classification of negative cases
                // FP = 1 - specificity
                if (!p.hasCancer && rand.nextDouble() > Parameters.specificityFIT) {
                    p.totalCost += Parameters.costColonoscopy; // follow-up colonoscopy cost
                    p.totalQALY -= Parameters.disutilityColonoscopyDiscomfort; // QALYs loss from colonoscopy discomfort
                    p.totalQALY -= Parameters.disutilityFalsePositive; // trade off (mental disutility)
                    
                    // Potential chance of complication from colonoscopy
                    if (rand.nextDouble() < Parameters.complicationRisk) {
//                        p.totalQALY -= 0.13; // additional QALY loss
                    	p.totalQALY -= Parameters.disutilityColonoscopyComplication; // QALYs loss from colonoscopy complication
                    }
                }
            }
        }
        
        // ----------------------------------
        // b) Incidence
        // ----------------------------------
        if (!p.hasCancer && rand.nextDouble() < Parameters.incidenceRates[ageIndex]) {
            p.hasCancer = true; // change health state (healthy -> cancer)
            p.incidentCRC = true; // mark incidence event
        }
        
        // ----------------------------------
        // c) Mortality
        // ----------------------------------
        double deathProb = Parameters.backgroundMortality[ageIndex]; // probability of dying from all causes

        if (rand.nextDouble() < deathProb) { // 1. if individual dies this year 

            p.alive = false; // classify as dead

            if (p.hasCancer) { // 2. if dead + cancer 

                double crcProb = Parameters.mortalityRates[ageIndex]; // probability of dying from CRC

                // 3. apply mortality reduction only if screening detects cancer 
                if (p.detected) {
                    crcProb *= detectionEffect; // screening benefit
                }
                
                
                // conditional prob: CRC death given death occurred
                // P(CRC | Death) = P(CRC death) / P(Death)
                double probCRCgivenDeath = crcProb / deathProb; 

                if (rand.nextDouble() < probCRCgivenDeath) {
                    p.diedFromCRC = true; // 4. CRC mortality
                }
            }
            return; // 5. stop simulation after death
        }
        
        // ----------------------------------
        // d) Treatment cost (assuming only once)
        // ----------------------------------
        if (p.detected && !p.treatmentStarted) {
            p.totalCost += Parameters.costTreatment; // apply cost only once at when diagnosed
            p.treatmentStarted = true; // reset so only apply cost once
        }
        
        // ----------------------------------
        // e) QALY + Life expectancy
        // ----------------------------------
        p.totalQALY += p.hasCancer ? 
        		Parameters.utilityCancer // if has cancer -> lower QALY with cancer
        		: Parameters.utilityHealthy; // if no cancer -> full healthy QALY

        p.lifeYears++; // increment survival time
    }
    
    // ----------------------------------
    // Simulates the entire life course of one individual from age 40 onward under a given screening strategy
    // ----------------------------------
    public Person runSimulation(int startAge, int stopAge, int frequency) { // return person object

        Person p = new Person();
        int currentAge = 40; // start simulation at age 40
        
        // loop through age range
        for (int ageIndex = 0; ageIndex < Parameters.incidenceRates.length; ageIndex++) {

            for (int i = 0; i < 5; i++) { // loop through 5 years (e.g., 40-44)
                simulateYear(p, ageIndex, currentAge, startAge, stopAge, frequency);
                currentAge++;

                if (!p.alive) return p; // stop early if dead
            }
        }

        return p; // return final stage
    }
}







