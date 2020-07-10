package model.agent;

public class Car {

	// Meta
	private int ID, typeID; //same as agend id, then 1 = Nissan Leaf, 2 = Tesla S, 3 = eGolf
	private String name, typeModel, description;
	
	// Restrictions and constraints
	private int maxSpeed, maxCurrent;
	
	// Variables
	private double currentSpeed, currentEnergy, maxEnergy, dischargeRate, chargeRate;

// All car properties were modified by Manuel Pérez (manperbra@outlook.es), updating their current features (2020) and intentionally for the master thesis (see Readme)

	public Car(int iD, int typeID, double currentEnergyFraction) {
		ID = iD;

		if (typeID == (int) 1){
			this.name = "Nissan";
			this.typeModel = "Leaf";
			this.description = "Sedan, 5 seats";
			this.maxEnergy = 40;
			this.dischargeRate = 5*0.174;
			this.chargeRate = 50;
			this.currentEnergy = this.maxEnergy*currentEnergyFraction;
		}
		else if (typeID == (int) 2){
			this.name = "Tesla";
			this.typeModel = "Model S";
			this.description = "Sedan, 5 seats";
			this.maxEnergy = 100;
			this.dischargeRate = 5*0.198;
			this.chargeRate = 150;
			this.currentEnergy = this.maxEnergy*currentEnergyFraction;
		}
		else if(typeID == (int) 3){
			this.name = "Volkswagen";
			this.typeModel = "eGolf";
			this.description = "Sedan, 5 seats";
			this.maxEnergy = 35.8;
			this.dischargeRate = 5*0.179;
			this.chargeRate = 50;
			this.currentEnergy = this.maxEnergy*currentEnergyFraction;
		}
		else{
			this.name = "Unknown";
			this.typeModel = "Default";
			this.description = "";
			this.maxEnergy = 25;
			this.dischargeRate = 0.22;
			this.chargeRate = 3.3;
			this.currentEnergy = this.maxEnergy;
			System.err.println("The car type for Agent ("+this.ID+") is not correctly specified. Setting the car to default values");

		}
	}

	public Car(int iD, int typeID, String name, String typeModel, String description,
			int maxSpeed, int maxEnergy, int maxCurrent, double dischargeRate,
			int chargeRate, int currentSpeed, int currentEnergy) {
		ID = iD;
		this.typeID = typeID;
		this.name = name;
		this.typeModel = typeModel;	
		this.description = description;
		this.maxSpeed = maxSpeed;
		this.maxEnergy = maxEnergy;
		this.maxCurrent = maxCurrent;
		this.dischargeRate = dischargeRate;
		this.chargeRate = chargeRate;
		this.currentSpeed = currentSpeed;
		this.currentEnergy = currentEnergy;
	}
	
	public Car(double maxEnergy, double currentEnergy) {
		this.maxEnergy = maxEnergy;
		this.currentEnergy = currentEnergy;
		this.dischargeRate = 0.212;
	}
	
	/**
	 * Energy used by driving a specific distance in meters.
	 * 
	 * @param meters
	 * @return kW
	 */
	public double getEnergyUsed(int meters) {
		double km = meters/1000.0;
		return getDischargeRate()*km;
	}

	/////////////////////////
	// GETTERS AND SETTERS
	////////////////////////
	
	
	public int getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTypeModel() {
		return typeModel;
	}

	public void setTypeModel(String typeModel) {
		this.typeModel = typeModel;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public double getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(int currentSpeed) {
		this.currentSpeed = currentSpeed;
	}

	public double getCurrentEnergy() {
		return currentEnergy;
	}

	public double getbatteryrange() {
		return (this.currentEnergy/this.dischargeRate);
	}
	public void setCurrentEnergy(double d) {
		this.currentEnergy = d;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public double getMaxEnergy() {
		return maxEnergy;
	}

	public int getMaxCurrent() {
		return maxCurrent;
	}

	/**
	 * Leaf's energy consumption to be 0.212 kWh/km
	 * @return Discharge rate of the electric vehicle per km. (kWh/km)
	 */
	public double getDischargeRate() {
		return dischargeRate;
	}

	public double getChargeRate() {
		return chargeRate;
	}
	
	public void setMaxEnergy(double max) {
		this.maxEnergy = max;
	}

	
	
	
	

}




