//This model of the power grid is no longer used in the model developed by Manuel Pérez (manperbra@outlook.es). 

package model.powerutilities;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;

import model.agent.Agent;
import model.schdeule.GlobalClock;

import Utils.Variables;
import Utils.Statistics.StringDateComparator;
import factory.ChargingStationFactory;

public class PowerGrid {

	private static boolean debug = false;

	// CHARGING STATIONS
	private ChargingStationMap chargingStationMap;
	private ChargingStationFactory chargingStationFactory;
	private static volatile PowerGrid instance = null;

	// USAGE DATA
	private double currentEnergy;
	private int currentChargers;
	//private double stateOfCharge;

	private double currentHomeChargingEnergy;
	private int currentHomeChargningUsers;

	private ConcurrentHashMap<String, Double> minuteUsage; // <HourAndDay, kW>
	private ConcurrentHashMap<String,Integer> hourlyAgents; // <HourAndDay, noOfAgents>

	private ConcurrentHashMap<Integer, ConcurrentHashMap<String,Double>> stationsUsage;// <HourAndDay, <StationID, kW>>

	private Set<Integer> stationIDs;
	private ConcurrentHashMap<String, ConcurrentHashMap<Integer,Double>> stationsUsage2;// <HourAndDay, <StationID, kW>>

	//private ConcurrentHashMap<String, Double> minuteStateOfCharge; // <HourAndDay, kW>


    // Sondre stuff
    //private ConcurrentHashMap<String,Integer> agentIDs;
    //private ConcurrentHashMap<String,Integer> agentChargingStationIDs;
    //private ConcurrentHashMap<String, Double> agentChargingValues;

	private int maxPeak;

	private PriorityBlockingQueue<ChargingStation> idle;


	private PowerGrid() {
		this.chargingStationFactory = new ChargingStationFactory();
		this.chargingStationMap =  chargingStationFactory.createChargeStationMapFromJSON();

		this.currentEnergy = 0;
		this.currentChargers = 0;
		//this.stateOfCharge = 25*Variables.NUMBER_OF_WORKER_AGENTS;

		this.currentHomeChargingEnergy = 0;
		this.currentHomeChargningUsers = 0;

		this.minuteUsage = new ConcurrentHashMap<String,Double>();
		this.hourlyAgents = new ConcurrentHashMap<String,Integer>();
		//this.minuteStateOfCharge = new ConcurrentHashMap<String,Double>();

		this.stationsUsage = new ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>>();
		this.stationIDs = new HashSet<Integer>();
		this.stationsUsage2 = new ConcurrentHashMap<String, ConcurrentHashMap<Integer,Double>>();

        // Sondre stuff
        //this.agentIDs = new ConcurrentHashMap<String,Integer>();
        //this.agentChargingStationIDs = new ConcurrentHashMap<String,Integer>();
        //this.agentChargingValues =  new ConcurrentHashMap<String, Double>();


		//this.minuteStateOfCharge.put(GlobalClock.getInstance().getTimeStamp(),stateOfCharge);

		idle = new PriorityBlockingQueue<>(200, new Comparator<ChargingStation>() {
			public int compare(ChargingStation s1, ChargingStation s2) {
				if(s1.getStationPriority() < s2.getStationPriority()) {
					return 1;
				}
				if(s1.getStationPriority() > s2.getStationPriority()) {
					return -1;
				}
				return 0;
			}
		});
	}

	public static PowerGrid getInstance() {
		if (instance == null) {
			synchronized (PowerGrid .class){
				if (instance == null) {
					instance = new PowerGrid ();
				}
			}
		}
		return instance;
	}

	public synchronized void updateCurrentEnergy(double value, ChargingStation station, Agent agent) { //silenced alltogether by @manuep
//		if(closeToPeak(value) && value > 0) {
//			//something wrong here... Fast charging makes the thing go negative and terminate
//			System.out.println("PowerGrid: Station ("+station.getAddress()+") and Agent ("+agent.getAgentId()+") is too close to peak.");
//			System.out.println("          VariableRatio: "+getVariableRatio(value));
//			System.out.println("          Power grid Ratio:"+getRatio());
//			System.out.println("          Value to add: "+value);
//			idle.add(station);
//			station.stopService();
//		}
//		else {
//
//			this.currentEnergy += value;
//			//this.stateOfCharge -= value;
//			if(value < 0) {
//				this.currentChargers -= 1;
//			}
//			if(value > 0) {
//				this.currentChargers += 1;
//			}
//
//			if(this.currentEnergy < 0) {
//				System.err.println("PowerGrid: currentEnergy is negavtive: "+currentEnergy);
//				printErrorLocation(station, agent);
//				if(this.currentEnergy > -1.0E-12){
//					System.out.println("PowerGrid: currentEnergy is negavtivity is not significatly large and set to 0.");
//					this.currentEnergy = 0;
//				}
//
//			}
//			if(this.currentChargers <0 ) {
//				System.err.println("PowerGrid: currentChargers is negavtive: "+currentChargers);
//				printErrorLocation(station, agent);
//			}
//			if(currentChargers < 0 || currentEnergy < 0) {
//				doStatistics();
//				System.exit(0);
//			}
//			grantStationAccess();
//
//			if(debug){System.out.println("----Time: "+GlobalClock.getInstance().getTimeStamp()+" and current energy: "+currentEnergy);}
//			if(Variables.EXCEL_OUTPUT!=1) { this.hourlyAgents.put(GlobalClock.getInstance().getTimeStamp(), currentChargers);   }
//			this.minuteUsage.put(GlobalClock.getInstance().getTimeStamp(), currentEnergy); //charge speed determined by station primarily now, can use max of car and station
//			//this.minuteStateOfCharge.put(GlobalClock.getInstance().getTimeStamp(),stateOfCharge);
//
//			//not used
//			//ConcurrentHashMap<String,Double> tempTimeAndUsage = new ConcurrentHashMap<String, Double>();
//			//tempTimeAndUsage.put(GlobalClock.getInstance().getTimeStamp(),station.getTotalKW());
//			//if(debug){System.out.println("-Time: "+GlobalClock.getInstance().getTimeStamp()+" - Station ID: "+station.getID()+" and current energy at station: "+station.getTotalKW());}
//			//this.stationsUsage.put(station.getID(),tempTimeAndUsage);  //ConcurrentHashMap<Integer, ConcurrentHashMap<String, Double>>();
//
//
//
//			//if(debug){System.out.println("-Time: "stationsUsage.get(station.getID())." - Station ID: "+stationsUsage.get(station.getID())+" and current energy at station: "+stationsUsage.get(station.getID()).get(GlobalClock.getInstance().getTimeStamp());}
//
//			//this.stationsUsage.put(station.getID(), new ConcurrentHashMap<String, Double>());
//			//this.stationsUsage.get(station.getID()).put(GlobalClock.getInstance().getTimeStamp(),station.getTotalKW());
//			//if(debug){System.out.println("-Time: "+GlobalClock.getInstance().getTimeStamp()+" - Station ID: "+station.getID()+" and current energy at station: "+stationsUsage.get(station.getID()).get(GlobalClock.getInstance().getTimeStamp()));}
//
//
//			double totalKW = station.getTotalKW();
//			if(station.getID() == 0){
//				totalKW = currentHomeChargingEnergy;
//			}
//
//			if(Variables.EXCEL_OUTPUT!=1) {
//				ConcurrentHashMap<Integer, Double> tempStationAndUsage2 = new ConcurrentHashMap<Integer, Double>();
//				tempStationAndUsage2.put(station.getID(), totalKW);
//				this.stationsUsage2.put(GlobalClock.getInstance().getTimeStamp(), tempStationAndUsage2);
//				this.stationIDs.add(station.getID());
//			}
//            //sondre stuff
//            //this.agentIDs.put(GlobalClock.getInstance().getTimeStamp(),agent.getAgentId());
//            //this.agentChargingStationIDs.put(GlobalClock.getInstance().getTimeStamp(),station.getID());
//            //this.agentChargingValues.put(GlobalClock.getInstance().getTimeStamp(),value);
//
//
//		}
	}

	public ConcurrentHashMap<String, Double> getMinuteUsageUsage() {
		return minuteUsage;
	}

	public ConcurrentHashMap<String, Integer> getHourlyAgents() {
		return hourlyAgents;
	}

	public void addToChargingStationMap(ChargingStation station) {
		chargingStationMap.getChargingStations().add(station);
	}

	public void doStatistics() {
		if(true) { //evt have Variables.EXCEL_OUTPUT == something
			System.out.println("Generating power grid statistics from Simulation...");
			SortedMap<String, Double> sortedHourlyUsage = Utils.Statistics.StringDateComparator.sortHashMap(minuteUsage);
			Utils.Statistics.ExcelWriter.writeOutHashMap(sortedHourlyUsage);

			if(Variables.EXCEL_OUTPUT!=1) {
				SortedMap<String, Double> sortedNrChargersInUse = Utils.Statistics.StringDateComparator.sortHashMapInt(hourlyAgents);

				//SortedMap<String, ConcurrentHashMap<Integer,Double>> = Utils.Statistics.StringDateComparator.sortHashMapMap(stationsUsage2);


				//SortedMap<String, Double> sortedStateOfCharge = Utils.Statistics.StringDateComparator.sortHashMap(minuteStateOfCharge);

				//Sondre stuff
				//SortedMap<String, Double> sortedAgentIDs = Utils.Statistics.StringDateComparator.sortHashMapInt(agentIDs);
				//SortedMap<String, Double> sortedAgentChargingStationIDs = Utils.Statistics.StringDateComparator.sortHashMapInt(agentChargingStationIDs);
				//SortedMap<String, Double> sortedAgentChargingValues = Utils.Statistics.StringDateComparator.sortHashMap(agentChargingValues);

				Utils.Statistics.ExcelWriter.writeOutHashMap(sortedHourlyUsage, sortedNrChargersInUse);


				//Utils.Statistics.ExcelWriter.writeOutHashMap(sortedHourlyUsage, sortedChargersUsage, sortedStateOfCharge);

				//Utils.Statistics.ExcelWriter.writeOutHashMap(sortedHourlyUsage, sortedChargersUsage, sortedStateOfCharge, sortedAgentIDs, sortedAgentChargingStationIDs, sortedAgentChargingValues);
			}

			System.out.println(".... Done");
			System.out.println("No. of agents: " + Variables.NUMBER_OF_WORKER_AGENTS);
		}
		if (Variables.EXCEL_OUTPUT == 3 || Variables.EXCEL_OUTPUT == 4) {

			System.out.println("Generating station statistics from Simulation...");
			//SortedMap<Integer, SortedMap<String,Double>> storedStationUsage = Utils.Statistics.StringDateComparator.sortHashMapMap(stationsUsage); doesn't work

			SortedMap<Integer, SortedMap<String, Double>> storedStationUsage = Utils.Statistics.StringDateComparator.sortHashMapMap(stationsUsage2, stationIDs);
			Utils.Statistics.ExcelWriter.writeOutHashMapMap(storedStationUsage);
			//Utils.Statistics.ExcelWriter.writeOutConcurrentHashMapMap(stationsUsage);
			System.out.println("No. of stations: " + stationIDs.size());
			System.out.println(".... Done");
		}

	}

	public void grantStationAccess() {
		if(!idle.isEmpty()) {
			ChargingStation s = idle.remove();
			s.continueService();

		}
	}

	public double getRatio() {
		return currentEnergy/maxPeak;
	}

	public double getVariableRatio(double value) {
		return (currentEnergy+value)/maxPeak;
	}

	public boolean closeToPeak(double value) {
		if(debug){System.out.println("PowerGrid Ratio: "+getRatio());}
		if(getVariableRatio(value) >= 0.9) {
			return true;
		}
		return false;
	}

	private void printErrorLocation(ChargingStation station, Agent agent) {
		System.err.println(" Charging station that caused it: "+station.getAddress()+" with Station ID: "+station.getID());
		System.err.println("   ChargersList"+station.getChargersList());
		System.err.println("   It was Agent ("+agent.getAgentId()+") that left");
		System.err.println("   Route distance/duration: "+agent.getCurrentRoute().getDistance()+"/"+agent.getCurrentRoute().getTime());
		System.err.println("   The time was: "+GlobalClock.getInstance().getTimeStamp());
	}

	public int getMaxPeak() {
		return maxPeak;
	}

	public void setMaxPeak(int maxPeak) {
		this.maxPeak = maxPeak;
	}

 	public void updateHomeCharging(double value){
		if(!closeToPeak(value)){
			currentHomeChargingEnergy += value;
			if(value>0){
				currentHomeChargningUsers++;
			} else {
				currentHomeChargningUsers--;
			}
		}
	}


}
