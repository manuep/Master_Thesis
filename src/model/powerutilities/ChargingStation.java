package model.powerutilities;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import Utils.Variables;

import model.agent.Agent;
import model.navigation.Location;
import model.schdeule.GlobalClock;

public class ChargingStation {

	// META INFORMATION
	private int ID;
	private String name, address, postalName, placeDesc, type, owner, comments, accessType;
	private int postNo, countyNo, connection;
	private Location location;

	// CHARGING POINTS
	private int openChargingPoints, maxChargingPoints, timeLimit;
	private double avgUseage, avgChargeTime;

	// QUEUES
	private Queue<Agent> queue = new LinkedList<>();
	private boolean allowedToCharge;

	// PRICES (kW/h)
	private double priceCurrent, priceMin, priceMax;
	private double consumer_price;
	private boolean parkingFree;

	// SPEED
	private int amp, voltage;
	private String chargeSpeed; // '16A' or 'hurtiglading' (speedcharging)
	private double chargeRate; // kW/h

	// CHARGING DATA
	private double totalKW, currentKW; 
	private ArrayList<Agent> chargers;


	public ChargingStation(int id, String address, String owner, int maxChargingPoints, String chargeSpeed, 
			int timelimit, int postNo, String postalName, String accessType, boolean parkingFree, Location location, double price,double consumer_price, int connection) {
		this.ID = id;
		this.address = address;
		this.owner = owner;
		this.maxChargingPoints = maxChargingPoints;
		this.openChargingPoints = maxChargingPoints;
		this.timeLimit = timelimit;
		this.postNo = postNo;
		this.postalName = postalName;
		this.accessType = accessType;
		this.parkingFree = parkingFree;
		this.location = location;
		this.chargers = new ArrayList<>();
		this.allowedToCharge = true;
		setChargeSpeed(chargeSpeed);
		setPriceCurrent(price);
		setPriceConsumer(consumer_price);
		this.connection=connection;
	}
	public ChargingStation(int openChargingPoints, int amp, int voltage) {
		this.openChargingPoints = openChargingPoints;
		this.amp = amp;
		this.voltage = voltage;
		this.chargeRate = (amp*voltage)/1000;
		this.chargers = new ArrayList<>();
		this.allowedToCharge = true;

	}

	public synchronized boolean registedCharger(Agent agent) {
		if(isAvailableChargingSpot()) {
			openChargingPoints --;
			//new
			double maxChargeRateToUse = agent.getCar().getChargeRate();
			if (getChargeRate()<maxChargeRateToUse){maxChargeRateToUse=getChargeRate();}
			currentKW += maxChargeRateToUse;
			//old
			//currentKW += getChargeRate();
			if(chargers.contains(agent)) {
				System.out.println("ChargingStation ("+address+"): Adding duplicate Agent ("+agent.getAgentId()+")");
			}
			chargers.add(agent);

			if(getID() == 0){
				updateHomeCharging(maxChargeRateToUse);
			}

			updatePowerGrid(maxChargeRateToUse, agent);
			//updatePowerGrid(getChargeRate(), agent);

			return true;
		}
		System.out.println(this.getAddress()+": No spot available for Agent ("+agent.getAgentId()+") at: "+GlobalClock.getInstance().getTimeStamp());
		return false;
	}

	public void unregisterCharger(Agent agent, double chargeTime) {
		//new
		double maxChargeRateToUse = agent.getCar().getChargeRate();
		if (getChargeRate()<maxChargeRateToUse){maxChargeRateToUse=getChargeRate();}
		currentKW -= maxChargeRateToUse;
		//old
		//currentKW -= getChargeRate();
		if(!chargers.remove(agent)) {
			System.err.println("ChargingStation ("+address+"): Trying to remove Agent ("+agent.getAgentId()+") from my list, but he isn't in it.");
			String res = "";
			for(Agent a : chargers) {
				res+= "|"+a.getAgentId()+" ";
			}
			System.err.println("  My current chargers: "+res);
		}
		openChargingPoints++;

		updatePowerGrid(-maxChargeRateToUse, agent);
		//updatePowerGrid(-getChargeRate(), agent);
	}

	public void unregisterCharger(Agent agent) {
		//new
		double maxChargeRateToUse = agent.getCar().getChargeRate();
		if (this.getChargeRate()<maxChargeRateToUse){maxChargeRateToUse=this.getChargeRate();}
		currentKW -= maxChargeRateToUse;
		//old
		//currentKW -= getChargeRate();
		if(!chargers.remove(agent)) {
			System.err.println("ChargingStation ("+address+"): Trying to remove Agent ("+agent.getAgentId()+") from my list, but he isn't in it.");
			String res = "";
			for(Agent a : chargers) {
				res+= "|"+a.getAgentId()+" ";
			}
			System.err.println("  My current chargers: "+res);
		}
		openChargingPoints++;

		if(getID() == 0){
			updateHomeCharging(-maxChargeRateToUse);
		}

		updatePowerGrid(-maxChargeRateToUse, agent);
		//updatePowerGrid(-getChargeRate(), agent);
	}

	private void updatePowerGrid(double value, Agent agent) {
		PowerGrid.getInstance().updateCurrentEnergy(value, this, agent);
	}

	private void updateHomeCharging(double value) {
		PowerGrid.getInstance().updateHomeCharging(value);
	}

	private boolean isAvailableChargingSpot() {
		return openChargingPoints > 0;
	}

	public double getStationPriority() {
		double sum = 0;
		for(Agent agent : chargers) {
			sum += agent.getChargingPriority();

		}
		return sum/chargers.size();
	}

	public boolean hasChargers() {
		return chargers.size() > 0;
	}

	public void stopService() {
		double kwReduction = 0;
		setAllowedToCharge(false);
		for(Agent agent :chargers) {
			//old
			//kwReduction += getChargeRate();
			kwReduction += getChargeRate(agent);
		}

	}

	public void continueService() {
		double kwIncrease = 0;
		setAllowedToCharge(true);
		for(int i = 0; i<chargers.size(); i++) {
			kwIncrease += getChargeRate(chargers.get(i));
			updatePowerGrid(getChargeRate(chargers.get(i)), chargers.get(i));
//			kwIncrease += getChargeRate();
//			updatePowerGrid(getChargeRate(), chargers.get(i));
		}

	}


	/////////////////////////
	// GETTERS AND SETTERS
	////////////////////////
	
	public ArrayList<Agent> getChargers() {
		return chargers;
	}

	public void setChargeSpeed(String chargeSpeed) {
		this.chargeSpeed = chargeSpeed;
		if(chargeSpeed.equals("16A")) {
			this.amp = 16;
			this.voltage = 230;
		}
		else {
			this.amp = 375;
			this.voltage = 400;
		}
		updateChargeRate();
	}

	public void updateChargeRate() {
		this.chargeRate = (getAmp()*getVoltage())/1000;
	}

	/**
	 * Returns the virtual charge rate, with respect to the
	 * simulation speed. X kW/per-simulation-speed-tick
	 * 
	 * @see Variables
	 * 
	 */
	public double getVirtualChargeRate() {
		return getChargeRate()/60;
	}

	/**
	 * Return the kW/h charge rate of the charging station.
	 * @return
	 */
	public double getChargeRate() {
		return chargeRate;
	}


	public String getAccessType() {
		return accessType;
	}

	public int getAmp() {
		return amp;
	}

	public void setAmp(int amp) {
		this.amp = amp;
	}

	public int getVoltage() {
		return voltage;
	}

	public void setVoltage(int voltage) {
		this.voltage = voltage;
	}

	public void setAccessType(String accessType) {
		this.accessType = accessType;
	}

	public int getID() {
		return ID;
	}


	public void setID(int iD) {
		ID = iD;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getAddress() {
		return address;
	}


	public void setAddress(String address) {
		this.address = address;
	}


	public String getPostalName() {
		return postalName;
	}


	public void setPostalName(String postalName) {
		this.postalName = postalName;
	}


	public String getPlaceDesc() {
		return placeDesc;
	}


	public void setPlaceDesc(String placeDesc) {
		this.placeDesc = placeDesc;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public String getChargeSpeed() {
		return chargeSpeed;
	}


	public String getComments() {
		return comments;
	}


	public void setComments(String comments) {
		this.comments = comments;
	}


	public int getPostNo() {
		return postNo;
	}


	public void setPostNo(int postNo) {
		this.postNo = postNo;
	}


	public int getCountyNo() {
		return countyNo;
	}


	public void setCountyNo(int countyNo) {
		this.countyNo = countyNo;
	}

	public int getTimeLimit() {
		return timeLimit;
	}


	public void setTimeLimit(int timeLimit) {
		this.timeLimit = timeLimit;
	}


	public boolean isParkingFree() {
		return parkingFree;
	}


	public void setParkingFree(boolean parkingFree) {
		this.parkingFree = parkingFree;
	}


	public Location getLocation() {
		return location;
	}


	public void setLocation(Location location) {
		this.location = location;
	}

	public int getOpenChargingPoints() {
		return openChargingPoints;
	}

	public void setOpenChargingPoints(int openChargingPoints) {
		this.openChargingPoints = openChargingPoints;
	}

	public int getMaxChargingPoints() {
		return maxChargingPoints;
	}

	public void setMaxChargingPoints(int maxChargingPoints) {
		this.maxChargingPoints = maxChargingPoints;
	}

	public Queue<Agent> getQueue() {
		return queue;
	}

	public void setQueue(Queue<Agent> queue) {
		this.queue = queue;
	}

	public double getAvgUseage() {
		return avgUseage;
	}

	public void setAvgUseage(double avgUseage) {
		this.avgUseage = avgUseage;
	}

	public double getAvgChargeTime() {
		return avgChargeTime;
	}

	public void setAvgChargeTime(double avgChargeTime) {
		this.avgChargeTime = avgChargeTime;
	}

	public double getPriceCurrent() {
		return priceCurrent;
	}

	public void setPriceCurrent(double priceCurrent) {
		this.priceCurrent = priceCurrent;
	}
	public double getPriceConsumer() {
		return consumer_price;
	}

	public void setPriceConsumer(double priceconsumer) {
		this.consumer_price = priceconsumer;
	}
	
	public int getConnection() {
		return connection;
	}
	
	public void setConnection(int connection) {
		this.connection=connection;
	}

	public double getPriceMin() {
		return priceMin;
	}

	public void setPriceMin(double priceMin) {
		this.priceMin = priceMin;
	}

	public double getPriceMax() {
		return priceMax;
	}

	public void setPriceMax(double priceMax) {
		this.priceMax = priceMax;
	}

	public String getChargersList() {
		String res="";
		for(Agent agent: chargers) {
			res+= "|"+agent.getAgentId()+" ";
		}
		return res;
	}

	public boolean isAllowedToCharge() {
		return allowedToCharge;
	}

	public void setAllowedToCharge(boolean allowedToCharge) {
		this.allowedToCharge = allowedToCharge;
	}

	public double getCurrentKW(){return currentKW;}

	public double getTotalKW(){return currentKW*chargers.size();}


	public double getChargeRateToUse(Agent agent){
		double maxChargeRateToUse = agent.getCar().getChargeRate();
		if (getChargeRate()<maxChargeRateToUse){maxChargeRateToUse=getChargeRate();}
		return maxChargeRateToUse;
	}
	public double getChargeRate(Agent agent){
		return getChargeRateToUse(agent);
	}

	/**
	 * Returns the virtual charge rate, with respect to the
	 * simulation speed. X kW/per-simulation-speed-tick
	 *
	 * @see Variables
	 *
	 */
	public double getVirtualChargeRate(double chargeRateToUse) {
		return chargeRateToUse/60;
	}



}
