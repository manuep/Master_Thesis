package Utils.Charging;

import java.util.ArrayList;
import java.util.TreeMap;

import model.navigation.Location;
import model.powerutilities.ChargingStation;
import model.powerutilities.PowerGrid;
import model.agent.Agent;
import model.agent.Car;

public abstract class ChargingStrategy {
	
	private boolean charging;
	private ChargingStation targetChargingStation;
	private ChargingStation homeChargingStation;
	private Agent agent;

	public ChargingStrategy(Agent agent) {
		this.charging = false;
		this.agent = agent;
		this.homeChargingStation = new ChargingStation(1,16,230); 
		this.homeChargingStation.setAddress("Home charging station for Agent ("+getAgent().getAgentId()+")");
		this.homeChargingStation.setID(0);
		PowerGrid.getInstance().addToChargingStationMap(homeChargingStation);

	}
	
	public abstract ChargingStation getMostSuitableChargingStation();
	public abstract ArrayList<ChargingStation> getMostSuitableChargingStations();
	public abstract void chargeAt(ChargingStation chargingStation);
	public abstract void stopCharging();
	public abstract void chargeAtHome();
	public abstract void doChargingTicks();
	public abstract void chargeAtWork();
	public abstract void setPossibleWorkChargingStations(Location workLocation);
	public abstract TreeMap<Double,ChargingStation> getReachableChargingStations(Location currentlocation);
	public abstract void chargeAtWork(ChargingStation cst);
	public abstract boolean chargingtime();
	

	public boolean needToCharge() {
		if(getAgent().getCar().getCurrentEnergy() >= getAgent().getCar().getMaxEnergy()) {
			return false; 
		}
		else {
			return true;
		}
	}

	public boolean isCharging() {
		return charging;
	}

	public void setCharging(boolean charging) {
		this.charging = charging;
	}

	public ChargingStation getTargetChargingStation() {
		return targetChargingStation;
	}

	public void setTargetChargingStation(ChargingStation target) {
		if(this.targetChargingStation != null && target != null) {
			System.err.println("Trying to set the target charging station from "+getTargetChargingStation().getAddress());
			System.err.println(" to: "+target.getAddress()+", without a null in between (done in stopCharging();)");
		}
		this.targetChargingStation = target;
	}

	public ChargingStation getHomeChargingStation() {
		return homeChargingStation;
	}

	public void setHomeChargingStation(ChargingStation homeChargingStation) {
		this.homeChargingStation = homeChargingStation;
	}


	public Agent getAgent() {
		return agent;
	}

	public void setAgent(Agent agent) {
		this.agent = agent;
	}


	
	
	
	
	

}
