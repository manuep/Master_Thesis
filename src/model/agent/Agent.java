package model.agent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Environment.City;
import Utils.Charging.*;
import Utils.Charging.Strategies.LP;
//import Utils.Charging.Strategies.DumbCharging;
//import Utils.Charging.Strategies.DumbScaledProb;
//import Utils.Charging.Strategies.ProbFromSOCandPrice;
//import Utils.Charging.Strategies.FullyConnectedAndSmart;
//import Utils.Charging.Strategies.FullyConnectedAndSmartV2;
import Utils.Charging.Strategies.central;
import Utils.Charging.Strategies.decentral;
import Utils.Variables;

import factory.RouteFactory;

import model.navigation.Direction;
import model.navigation.Location;
import model.navigation.Route;
import model.powerutilities.ChargingStation;

/**
 * This Class will contain most of the decision making and AI for an agent.
 * 
 *TODO -> Find the most suitable charging station based on price
 *TODO -> Create an algorithm to find the most suitable charging station all-over (i.e. 
 *			most suitable based on price distance)
 * 
 * @author davidae
 *
 */
public abstract class Agent extends Thread{

	// set debug mode
	private boolean debug = (Variables.DEBUG_ALL || Variables.DEBUG_Agent);

	// ATTRIBUTES
	
	int counter_ag;
	
	private final int agentId;
	private Car car;
	private City city;
	private Location homeLocation;
	private Location workLocation;
	private boolean isRunning;
	private ChargingStrategy chargingStrategy;
	private boolean doAction;


	// DRIVING SCHEDULE AND LOCATION
	private List<Route> routes;
	private Route currentRoute;
	private Direction currentDirection;
	private RouteFactory routeFactory;
	private Location currentLocation;
	private ArrayList <int[]> tiempos_cambio= new ArrayList<int[]>();


	private int minimumEnergyPrefernce; // kW
	private double[] preferences =new double[2] ;



	public Agent(int agentId, City city, int numberOfCarTypes) {
		if(debug){System.out.println("Agent "+(agentId)+" is created.");}
		
		if (Variables.CHARGING_STRATEGY_FOR_AGENTS==1) {
		this.chargingStrategy = new central (this,Variables.MIN_DESIRED_BATTERY_LEVEL);
		}
		else if (Variables.CHARGING_STRATEGY_FOR_AGENTS==2) {
			this.chargingStrategy = new decentral (this,Variables.MIN_DESIRED_BATTERY_LEVEL);
		}
		else if (Variables.CHARGING_STRATEGY_FOR_AGENTS==3) {
			this.chargingStrategy = new LP (this,Variables.MIN_DESIRED_BATTERY_LEVEL);
		}
		int[] car_types= {2,	2,	2,	3,	3,	3,	1,	1,	2,	3,	1,	2,	1,	1,	1,	1,	1,	1,	1,	2};
		int[] energy_pref= {24,	51,	21,	19,	8,	5,	12,	22,	56,	14,	9,	78,	19,	23,	17,	7,	23,	24,	9,	22};
		double[] pref_0= {0.522447372,	0.544200201,	0.484098013,	0.389684115,	0.246252107,	0.589456119,	0.720242108,	0.278910887,	0.255277175,	0.616756997,	0.256187486,	0.403365367,	0.693139595,	0.756523494,	0.736875112,	0.680224422,	0.65784007,	0.297411018,	0.225970056,	0.269378249};
		double[] pref_1= {0.477552628,	0.455799799,	0.515901987,	0.610315885,	0.753747893,	0.410543881,	0.279757892,	0.721089113,	0.744722825,	0.383243003,	0.743812514,	0.596634633,	0.306860405,	0.243476506,	0.263124888,	0.319775578,	0.34215993,	0.702588982,	0.774029944,	0.730621751};


		this.counter_ag=0;
		
		
		this.agentId = agentId;
		this.city = city;
		this.homeLocation = generateHomeLocation();  //random in city
//		this.workLocation= generateWorkLocation();
		this.routeFactory = new RouteFactory();
		this.isRunning = true;
		double someRandomNumber =new Random().nextDouble();
		double someRandomNumber2 = new Random().nextDouble() * (1-0.4)+ 0.4;
//		this.car=new Car(agentId,car_types[agentId],someRandomNumber);
		if(Variables.StartSOCsAtOne){ someRandomNumber2 = 1;}
		if (someRandomNumber2>1){someRandomNumber2=0.90;}
		if(debug){System.out.println("   Random number 2: "+someRandomNumber2);}
        //if(debug){System.out.println("   Random number: "+someRandomNumber+" then; "+(someRandomNumber < 0.33)+(someRandomNumber < 0.66));}

		if(someRandomNumber < 0.33) {
			//if(someRandomNumber < 1/numberOfCarTypes)
            if(debug){System.out.println("   Agent ID: "+agentId+", cartypeId: "+(numberOfCarTypes-2));}
			//this.car = new Car(agentId,(int)  numberOfCarTypes-2, 0.95);
			this.car = new Car(agentId,(int)  numberOfCarTypes-2, someRandomNumber2);

		}
		else if(someRandomNumber < 0.66) {
            if(debug){System.out.println("   Agent ID: "+agentId+", cartypeId: "+(numberOfCarTypes-1));}
            //this.car = new Car(agentId, (int) numberOfCarTypes - 1, 0.95);
			this.car = new Car(agentId, (int) numberOfCarTypes - 1, someRandomNumber2);
        }
		else {
			//this.car = new Car(agentId,(int)  numberOfCarTypes, 0.95);
			this.car = new Car(agentId,(int)  numberOfCarTypes, someRandomNumber2);
		}
		if(debug){System.out.println(" I'm agent"+agentId+" &  I drive a "+this.getCar().getName()+" "+this.getCar().getTypeModel()+".");}
		this.currentLocation = homeLocation;
		double someRandomNumber3= new Random().nextInt((int) (car.getMaxEnergy()*0.35)) + car.getMaxEnergy()*0.15;
		this.minimumEnergyPrefernce= (int) someRandomNumber3;
		double randomcut1= new Random().nextDouble()*0.6;
		double pref1= randomcut1;
		double pref2= (1-randomcut1);
		this.preferences[0]=pref1;
		this.preferences[1]=pref2;
		System.out.println("His energy preference is"+getMinimumEnergyPrefernce()+"and the initial SOC"+someRandomNumber2*car.getMaxEnergy()+"and his preferences are "+preferences[0]+","+preferences[1]);
		
		BufferedWriter writer8;
		try {
			writer8=new BufferedWriter(new FileWriter("C:\\Users\\manpe\\eclipse-workspace\\ABM-Steinkjer_random\\agents_prop.txt",true));
			writer8.newLine();
			writer8.append(agentId+";"+car.getMaxEnergy()+";"+car.getCurrentEnergy()+";"+minimumEnergyPrefernce+";"+preferences[0]+";"+preferences[1]+";");
			writer8.close();
		}
		catch (IOException e){
			e.printStackTrace();
		}
				
	}

	///////////////////////////////////////////////////////////////////////////
	// 						MAIN LOOP
	//////////////////////////////////////////////////////////////////////////

	public void run() {
		loop();
	}

	///////////////////////////////////////////////////////////////////////////
	// 						ABSTRACT
	//////////////////////////////////////////////////////////////////////////
	public abstract Location generateHomeLocation();
	public abstract boolean getneedchargeinway();
	public abstract boolean getneedchargeinway2(); // solo para matlab
//	public abstract Location generateWorkLocation();
	public abstract void loop();
	public abstract boolean isAtWork();

	///////////////////////////////////////////////////////////////////////////
	// 						DRIVING
	//////////////////////////////////////////////////////////////////////////
	
	public synchronized void up() {
		setRunning(true);
		this.notify();
	}
	/**
	 * Updates the Agent's car current energy level with respect 
	 * to do distance driven in meters.
	 * 
	 * @param meters
	 */
	public void updateAfterDrive(int meters) {
		double energyUsed = getCar().getEnergyUsed(meters);
		double currentEnergy = getCar().getCurrentEnergy();
		if(debug){System.out.println("Agent ("+agentId+"): I have now driven "+meters+" meters ("+meters/1000+" km) ("+energyUsed+" kW)");}

		getCar().setCurrentEnergy(currentEnergy-energyUsed);
	}


	///////////////////////////////////////////////////////////////////////////
	// 						AGENT/USER PREFERENCES
	//////////////////////////////////////////////////////////////////////////

	/**
	 * Is the driving distance suitable to this charging station, or do you need a closer one?
	 * 
	 * @param location
	 * @return
	 */
	public boolean isDrivingDistancePossible(ChargingStation station) {
		double remainingEnergy = getCar().getCurrentEnergy() - getEnergyUsageToLocation(station.getLocation());
		if(remainingEnergy <= getMinimumEnergyPrefernce()) {
			return false;
		}

		return true;
	}
	
	public boolean isDrivingToDestinationPossible(Location loc) {
		double remainingEnergy = getCar().getCurrentEnergy() - getEnergyUsageToLocation(loc);
		if(remainingEnergy <= getCar().getMaxEnergy()) {
			return true;
		}

		return false;
	}
	
	/**
	 * Returns the ratio between current and max energy
	 * of the Agent's Car. 
	 * A high value represents a high priority.
	 * 
	 * @return double in range [0.0 - 1.0]
	 */
	public double getChargingPriority() {
		return 1.0 - (double) (getCar().getCurrentEnergy()/getCar().getMaxEnergy());
	}


	///////////////////////////////////////////////////////////////////////////
	// 						DISTANCE CALCULATIONS
	//////////////////////////////////////////////////////////////////////////

	
	/**
	 * Calculates the driving distance from a location to another location.
	 * It queries the Google Direction API.
	 * 
	 * @param Location source, Location target
	 * @return an integer, representing meters
	 */
	public int getDistanceFromAndTo(Location source, Location target) {
		Route route = routeFactory.createRouteFromAndTo(source, target);
		return (int)route.getDistance();
	}

	/**
	 * Calculates the driving distance from your current location to another location.
	 * It queries the Google Direction API.
	 * 
	 * @param Location source, Location target
	 * @return an integer, representing meters
	 */
	public int getDistanceTo(Location target) {
		Route route = routeFactory.createRouteFromAndTo(getCurrentLocation(), target);
		return (int)route.getDistance();
	}
	

	///////////////////////////////////////////////////////////////////////////
	// 						ENERGY CALCULATIONS
	//////////////////////////////////////////////////////////////////////////


	/**
	 * Calculates the energy usage from a source location to another target location.
	 * It queries to Google Directions API
	 * @param Location source, Location target
	 * @return an integer, representing meters
	 */
	public double getEnergyUsageToLocation(Location source, Location target) {
		int distanceToSource = getDistanceFromAndTo(source, target);

		return car.getDischargeRate()*(distanceToSource/1000);
	}

	/**
	 * Calculates the energy usage from your current location to another target location.
	 * It queries to Google Directions API
	 * @param Location destination
	 * @return
	 */
	public double getEnergyUsageToLocation(Location destination) {
		return getEnergyUsageToLocation(getCurrentLocation(), destination);
	}

	///////////////////////////////////////////////////////////////////////////
	// 									TIME
	//////////////////////////////////////////////////////////////////////////

	//TODO
	public double getIdleTimeDurationAtDestination() {
		return -1;
	}
	//TODO
	public boolean isAheadOfSchedule() {
		return false;
	}

	///////////////////////////////////////////////////////////////////////////
	// 							GETTERS AND SETTERS
	//////////////////////////////////////////////////////////////////////////


	public ChargingStrategy getChargingStragegy() {
		return chargingStrategy;
	}

	public double getRandom(double min, double max) {
		double random = new Random().nextDouble();
		double randomValue = min + (max - min) * random;
		return randomValue;
	}
	/**
	 * 
	 * @return the target location of our current direction in a route.
	 */
	public Location getCurrentLocation() {
		return currentLocation;
	}


	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public Location getHomeLocation() {
		return homeLocation;
	}
	
	public Location getWorkLocation() {
		return workLocation;
	}

	public void setHomeLocation(Location homeLocation) {
		this.homeLocation = homeLocation;
	}

	public RouteFactory getRouteFactory() {
		return routeFactory;
	}

	public void setRouteFactory(RouteFactory routeFactory) {
		this.routeFactory = routeFactory;
	}

	public int getMinimumEnergyPrefernce() {
		return minimumEnergyPrefernce;
	}
	public void setMinimumEnergyPrefernce(int minimumEnergyPrefernce) {
		this.minimumEnergyPrefernce = minimumEnergyPrefernce;
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}
	public List<Route> getRoutes() {
		return routes;
	}
	public void setRoutes(List<Route> routes) {
		this.routes = routes;
	}
	public Route getCurrentRoute() {
		return currentRoute;
	}
	public void setCurrentRoute(Route currentRoute) {
		this.currentRoute = currentRoute;
	}

	public int getAgentId() {
		return agentId;
	}

	public Direction getCurrentDirection() {
		return currentDirection;
	}

	public void setCurrentDirection(Direction currentDirection) {
		this.currentDirection = currentDirection;
	}
	
	public boolean isDoAction() {
		return doAction;
	}


	public void setDoAction(boolean doAction) {
		this.doAction = doAction;
	}

	public void setCurrentLocation(Location currentLocation) {
		this.currentLocation = currentLocation;
	}
	
	public void setTiemposCambio(ArrayList<int[]> tiempos) {
		this.tiempos_cambio=tiempos;
	}
	
	public ArrayList<int[]> getTiemposCambio() {
		return tiempos_cambio;
	}

	public double probOfChargingFromSOC(){
		double lowestDesiredBatteryEnergy = this.getCar().getMaxEnergy() *Variables.MIN_DESIRED_BATTERY_LEVEL;
		return 1 - (this.getCar().getCurrentEnergy() - lowestDesiredBatteryEnergy) / (this.getCar().getMaxEnergy() - lowestDesiredBatteryEnergy);
	}
	
	public double[] getpreferences() {
		return preferences;
	}
	
	public void setag_counter(int counter) {
		this.counter_ag=counter;
	}
	
	public int getag_counter() {
		return this.counter_ag;
	}



}
