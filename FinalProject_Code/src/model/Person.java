
package model;

public class Person {
	
	// Status
    public boolean alive = true; // start as alive
    public boolean hasCancer = false; // start as healthy
    public boolean diedFromCRC = false;
    public boolean incidentCRC = false; // mark whether CRC occurred during simulation
    
    
    // Impact of screening
    public double totalCost = 0;
    public double totalQALY = 0;
    public int lifeYears = 0;
    
    
    // Diagnosis status
    public boolean detected = false;
    public boolean treatmentStarted = false;
    
}




