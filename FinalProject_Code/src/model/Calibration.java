package model;

import java.util.Random;
import java.io.FileWriter;
import java.io.IOException;

public class Calibration {

    private static Random rand = new Random(203);

    private static double targetReduction = 0.35; // based on real-world data

    // --------------------------
    // (1) Compare screening vs no screening:
    // 		Assess estimated screening effect on mortality reduction
    // 		What is the mortality reduction under the standard screening scenario, given detection effect = X?
    // 		Identify effect for calibration
    // --------------------------
    public static double computeCRC_Mortality_Reduction(double effect) {

        int N = 100000;

        Simulation noScreen = new Simulation(1.0); // no reduction
        Simulation screen = new Simulation(effect); // unknown effect

        int deathsNo = 0; // CRC death without screening
        int deathsScreen = 0; // CRC death with screening

        for (int i = 0; i < N; i++) {
        	
        	// No screening
        	// start at 100 to disable screening
            if (noScreen.runSimulation(100, 100, 1).diedFromCRC) deathsNo++;
            
            // Screening
            // start at 45, ends at 74, every 2 year
            if (screen.runSimulation(45, 74, 2).diedFromCRC) deathsScreen++;
        }

        if (deathsNo == 0) return 0; // avoid division by zero
        
        // Compute reduction
        return 1.0 - ((double) deathsScreen / deathsNo);
    }

    // --------------------------
    // (2) Compare different strategies:
    // 		Compute mortality reduction per strategy
    // 		What is the mortality reduction associated with different screening scenario?
    // --------------------------
    public static double computeCRC_Mortality_StrategyReduction(double effect,
                                                  int startAge,
                                                  int stopAge,
                                                  int frequency) {

        int N = 100000;

        Simulation noScreen = new Simulation(1.0); // baseline
        Simulation screen = new Simulation(effect); // strategy

        int deathsNo = 0;
        int deathsScreen = 0;

        for (int i = 0; i < N; i++) {

            // baseline (no screening)
            if (noScreen.runSimulation(100, 100, 1).diedFromCRC) deathsNo++;

            // strategy-specific screening
            if (screen.runSimulation(startAge, stopAge, frequency).diedFromCRC) deathsScreen++;
        }

        if (deathsNo == 0) return 0;

        return 1.0 - ((double) deathsScreen / deathsNo);
    }

    // --------------------------
    // Compute error
    // --------------------------
    public static double computeError(double effect) {
        double reduction = computeCRC_Mortality_Reduction(effect); // compute reduction
        return Math.pow(reduction - targetReduction, 2); // squared error
    }

    // --------------------------
    // Simulated annealing to find parameters for optimal detection effect
    // --------------------------
    public static double runAnnealing() throws IOException {

        double current = 0.7;
        double best = current;

        double temp = 1.0; // high randomness, allowing more bad moves

        // ----------------------------------
        // Calibration trace output
        // ----------------------------------
        FileWriter writer = new FileWriter("./output/calibration_trace.csv");

        writer.append("iteration,currentEffect,currentError,bestEffect,bestError,temperature\n");

        for (int i = 0; i < 1000; i++) {
        	
        	// random step between -0.05 and + 0.05
            double candidate = current + (rand.nextDouble() - 0.5) * 0.1; 
            
            // restrict detection effect between 0.5 and 1.0
            if (candidate < 0.5) candidate = 0.5;
            if (candidate > 1.0) candidate = 1.0;

            double errCurrent = computeError(current);
            double errCandidate = computeError(candidate);
            
            // Acceptance rule
            if (errCandidate < errCurrent || // Accept if candidate is better 
            	// Sometimes accept if random number is less than acceptance probability
            	// P(Accept) = exp((E_current - E_candidate) / T)
                rand.nextDouble() < Math.exp((errCurrent - errCandidate) / temp)) {
                current = candidate;
            }
            
            // update best solution
            if (computeError(current) < computeError(best)) {
                best = current;
            }

            // ----------------------------------
            // Save calibration trace
            // ----------------------------------
            writer.append(i + ",")
                  .append(current + ",")
                  .append(errCurrent + ",")
                  .append(best + ",")
                  .append(computeError(best) + ",")
                  .append(temp + "\n");
            
            // slow cooling (reduce randomness)
            temp *= 0.98;
        }

        writer.close();

        return best;
    }
}









