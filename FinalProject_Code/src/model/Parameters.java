package model;

public class Parameters {

    // --------------------------
    // Helper function: Convert rate per 100,000 to probability
    // --------------------------
//	private static double toAnnualProb(double rate) {
//	    double annual = rate / 100000.0;
//	    
//	    // Math.pow(1 - annual, 5) -> probability of not getting the event over five years
//	    // 1 - Math.pow(1 - annual, 5) -> probability of getting the event at least once over five years
//	    return 1 - Math.pow(1 - annual, 5);
//	}

	private static double toAnnualProb(double rate) {
	    return rate / 100000.0;
	    }	

    // --------------------------
    // AGE GROUPS
    // Each index corresponds to 5-year block in Simulation
    // --------------------------

    // --------------------------
    // CRC Incidence (Taiwan 2021)
    // --------------------------
    public static final double[] incidenceRates = {
        toAnnualProb(35.79),   // 40–44
        toAnnualProb(60.32),   // 45–49
        toAnnualProb(108.23),  // 50–54
        toAnnualProb(155.81),  // 55–59
        toAnnualProb(212.46),  // 60–64
        toAnnualProb(260.08),  // 65–69
        toAnnualProb(306.94),  // 70–74
        toAnnualProb(454.23),  // 75–79
        toAnnualProb(491.46)   // 80–84
    };

    // --------------------------
    // CRC Mortality (Taiwan 2021)
    // --------------------------
    public static final double[] mortalityRates = {
        toAnnualProb(9.15),    // 40–44
        toAnnualProb(15.44),   // 45–49
        toAnnualProb(26.85),   // 50–54
        toAnnualProb(41.09),   // 55–59
        toAnnualProb(61.08),   // 60–64
        toAnnualProb(84.49),   // 65–69
        toAnnualProb(118.33),  // 70–74
        toAnnualProb(210.84),  // 75–79
        toAnnualProb(312.24)   // 80–84
    };

    // --------------------------
    // Background Mortality (All-cause, Taiwan 2021)
    // --------------------------
    public static final double[] backgroundMortality = {
        toAnnualProb(182.65),  // 40–44
        toAnnualProb(285.30),  // 45–49
        toAnnualProb(431.36),  // 50–54
        toAnnualProb(590.75),  // 55–59
        toAnnualProb(827.85),  // 60–64
        toAnnualProb(1184.23), // 65–69
        toAnnualProb(1769.17), // 70–74
        toAnnualProb(3315.64), // 75–79
        toAnnualProb(5651.01)  // 80–84
    };

    // --------------------------
    // FIT test performance
    // --------------------------
    public static final double sensitivityFIT = 0.90; // detects cancer
    public static final double specificityFIT = 0.83; // correctly rules out

    // --------------------------
    // Utilities (QALY)
    // --------------------------
    public static final double utilityHealthy = 1.0;
    public static final double utilityCancer = 0.7;

    // false positive disutility (short-term anxiety / procedure)
    public static final double disutilityFalsePositive = 0.08; // midpoint (0.031 ~ 0.111)
    public static final double disutilityColonoscopyDiscomfort = 0.0055;
    public static final double disutilityColonoscopyComplication = 0.0384;

    // --------------------------
    // Costs from healthcare system (USD)
    // --------------------------
    public static final double costFIT = 10;
    public static final double costColonoscopy = 150;
    public static final double costTreatment = 14334;

    // --------------------------
    // Colonoscopy complication risk
    // --------------------------
    public static final double complicationRisk = 0.015;
}




