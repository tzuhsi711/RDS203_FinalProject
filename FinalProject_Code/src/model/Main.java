package model;

import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
    	
    	// ----------------------------------
    	// Run simulated annealing to find best detection effect value
    	// ----------------------------------
        double calibrated = Calibration.runAnnealing();
        
        // ----------------------------------
        // Compute mortality reduction associated with the calibrated detection effect value
        // ----------------------------------
        double reduction = Calibration.computeCRC_Mortality_Reduction(calibrated);
        
        System.out.println("Calibrated detectionEffect = " + calibrated);
        System.out.println("Implied CRC mortality reduction (calibration scenario) = " + reduction);
        
        // ----------------------------------
        // Simulation size
        // ----------------------------------
        int N = 100000;

        // ----------------------------------
        // Output file
        // ----------------------------------
        FileWriter writer = new FileWriter("./output/results.csv");

        writer.append("strategy,startAge,stopAge,frequency,cost,qaly,lifeYears,crcDeath,incidentCRC,mortalityReduction\n");

        // ----------------------------------
        // Alternative screening strategies
        // ----------------------------------
        int[] startAges = {40, 45, 50};
        int[] stopAges = {69, 74, 84};
        int[] freqs = {1, 2};

        // ----------------------------------
        // Loop through all strategy combinations
        // ----------------------------------
        for (int start : startAges) {
            for (int stop : stopAges) {
                for (int freq : freqs) {

                    String name = "S" + start + "_" + stop + "_F" + freq;
                    
                    int totalIncidence = 0;
                    int totalCRCDeaths = 0;

                    // ----------------------------------
                    // Simulation object using calibrated detection effect
                    // ----------------------------------
                    Simulation sim = new Simulation(calibrated);

                    // ----------------------------------
                    // Compute strategy-specific mortality reduction
                    // ----------------------------------
                    double strategyReduction =
                        Calibration.computeCRC_Mortality_StrategyReduction(
                            calibrated,
                            start,
                            stop,
                            freq
                        );

                    // ----------------------------------
                    // Run simulation
                    // ----------------------------------
                    for (int i = 0; i < N; i++) {

                        Person p = sim.runSimulation(start, stop, freq);

                        // incidence count
                        if (p.incidentCRC) totalIncidence++;

                        // CRC death count
                        if (p.diedFromCRC) totalCRCDeaths++;

                        // ----------------------------------
                        // Individual-level output
                        // ----------------------------------
                        writer.append(name).append(",")
                              .append(start + ",")
                              .append(stop + ",")
                              .append(freq + ",")
                              .append(p.totalCost + ",")
                              .append(p.totalQALY + ",")
                              .append(p.lifeYears + ",")
                              .append(p.diedFromCRC ? "1" : "0").append(",")
                              .append(p.incidentCRC ? "1" : "0").append(",")
                              .append(String.valueOf(strategyReduction))
                              .append("\n");
                    }

                    // ----------------------------------
                    // Strategy summary output
                    // ----------------------------------
                    double incidenceProportion =
                        (double) totalIncidence / N;

                    double crcDeathProportion =
                        (double) totalCRCDeaths / N;

                    System.out.println(name + " incidence count = " + totalIncidence);

                    System.out.println(name + " incidence proportion = " + incidenceProportion);

                    System.out.println(name + " CRC death count = " + totalCRCDeaths);

                    System.out.println(name + " CRC death proportion = " + crcDeathProportion);

                    System.out.println(name + " mortality reduction = " + strategyReduction);
                }
            }
        }

        writer.close();

        System.out.println("CSV written.");
        System.out.println(System.getProperty("java.version"));
    }
}



