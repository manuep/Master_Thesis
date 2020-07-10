package model.agent;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.*;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.Random;

import com.mathworks.util.Log;

import java.io.BufferedWriter;
import java.io.FileWriter;

import Environment.City;
import Utils.Variables;
import Utils.Charging.ChargingStrategy;
import factory.RouteFactory;
import model.navigation.Direction;
import model.navigation.Location;
import model.navigation.Route;
import model.powerutilities.ChargingStation;
import model.powerutilities.ChargingStationMap;
import model.schdeule.Coord_matlab;
import model.schdeule.GlobalClock;

public class WorkerAgent extends Agent {
	private Location workLocation;
	private Route homeToWorkRoute;
	private boolean isAtWork;
	private List<Direction> direcciones;
	private boolean needchargeinway;
	
	

	private  static double START_WORK_TIME_MAX;
	private  static double START_WORK_TIME_MIN;
	private  static double WORKDAY_HOURS;


	private double startWorkTime, endWorkTime;
    private double startWorkTimes[];
    private double endWorkTimes[];


	private final static int ERRAND_RANGE_MAX = 30000;
	private final static int ERRAND_RANGE_MIN = 5000;
	private final static double AVG_DRIVE_SPEED = 60; // km/h
	private final static double ERRAND_PROBABILITY = 0.35;
	private Location errandLocation;
	private boolean haveErrand;
	private int errandRange;
	private long errandTime;
	

    private boolean haveErrands[];
    private int errandRanges[];
    private long errandTimes[]; 
    

	// set debug mode
	private boolean debug = (Variables.DEBUG_ALL || Variables.DEBUG_WorkerAgent);



	// for different cars, only implementet with three car types here
	public WorkerAgent(int id, City city, int numberOfCarTypes) {
		super(id, city, numberOfCarTypes);
		this.workLocation = city.getRandomWorkLocation();

		getChargingStragegy().setPossibleWorkChargingStations(workLocation);
		
		START_WORK_TIME_MAX = Variables.START_WORK_TIME_MAX;
		START_WORK_TIME_MIN = Variables.START_WORK_TIME_MIN;
		WORKDAY_HOURS = Variables.WORKDAY_HOURS;



		
		this.homeToWorkRoute = getRouteFactory().createRouteFromAndTo(getHomeLocation(), workLocation);
		this.direcciones=homeToWorkRoute.getDirections();

		
		while(homeToWorkRoute.getDistance() < Variables.MINIMUM_DISTANCE_TO_WORK) {
			System.err.println("Home to work route is only "+homeToWorkRoute.getDistance()+" meters, creating new route.");
			this.workLocation = city.getRandomWorkLocation();
			this.homeToWorkRoute = getRouteFactory().createRouteFromAndTo(getHomeLocation(), workLocation);

		}
		setCurrentRoute(this.homeToWorkRoute);


		this.isAtWork = false;
		setDoAction(false);
		System.out.println("Im agent"+id+"  &  I work at"+workLocation+"and I live at  "+getHomeLocation());
		
		if(debug) {System.out.println("I'm a worker agent (id: "+id+"), and I drive "+homeToWorkRoute.getDistance()+" meters ("+homeToWorkRoute.getDistance()/1000+" km) to work. (It takes "+homeToWorkRoute.getTime()+" sec to get there).");}
		if(debug) {System.out.println("  That is: "+(homeToWorkRoute.getTime()/60)+" min");}

        //this.updateRandomStartAndEndTimes();
        startWorkTimes = new double[Variables.SIMULATION_DAYS];
        endWorkTimes = new double[Variables.SIMULATION_DAYS];
        errandTimes = new long[Variables.SIMULATION_DAYS];
        haveErrands = new boolean[Variables.SIMULATION_DAYS];
        errandRanges = new int[Variables.SIMULATION_DAYS];
        //genetates times for all days
        for (int day = 0 ; day<Variables.SIMULATION_DAYS; day++){
            this.startWorkTimes[day] = getRandom(START_WORK_TIME_MIN, START_WORK_TIME_MAX);
            this.endWorkTimes[day] = this.startWorkTimes[day]+WORKDAY_HOURS+getRandom(-1,1);
            calcuateErrand(day);
			if(debug) {System.out.println("I'm a worker agent (id: "+id+"), for day "+(day+1)+" i start work at"+this.startWorkTimes[day]+" and end at "+this.endWorkTimes[day]+". Do I have an errand this day? "+haveErrand(day)+".");}
			System.out.println("Now there are"+ChargingStationMap.getChargingStations().size());
        }
        ChargingStationMap.getChargingStations().remove(Variables.stationscount);


	}

	// Used?
	public void updateRandomStartAndEndTimes(){
		this.startWorkTime = getRandom(START_WORK_TIME_MIN, START_WORK_TIME_MAX);  //should be done every day!!
		this.endWorkTime = this.startWorkTime+WORKDAY_HOURS+getRandom(-0.5,0.5);  //should be random and done every day!!
		this.calcuateErrand();
	}




	@Override
	public Location generateHomeLocation() {
		return getCity().getRandomResidence();
	}
	



	@Override
	public synchronized void loop() {
		while(isRunning()) {
			try {
				setRunning(false);
				this.wait();
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!isDoAction()) {
				//System.err.println("Agent ("+getId()+"): Was notified without any actions do to..");
				System.err.println("Agent ("+getAgentId()+"): Was notified without any actions do to..");

			}

			if(isDoAction()) {
//					model.schdeule.Coord_matlab.llamamiento2();
					doMove();
					setDoAction(false);	

			}

			if(getChargingStragegy().isCharging() && !Variables.needsmatlab) {
				System.out.println(isRunning()+"is running");
				System.out.println("En el loop esta haciendolo");
				System.out.println("La variable es "+Variables.needsmatlab);
				getChargingStragegy().doChargingTicks();
				
				if(GlobalClock.getTime()[0]<12 && !getChargingStragegy().isCharging()) {
				moveToWork();}
				else if (GlobalClock.getTime()[0]>12 && !getChargingStragegy().isCharging() ) {
					moveToHome();
				}
//				}
				System.out.println("Ha salido del ticks");

				
			}
			}

	}

	private void simulateDrive(Location origin, Location destination) {
		Route route=getRouteFactory().createRouteFromAndTo(origin, destination);
		System.out.println("Agent"+getAgentId()+"my origin is"+origin.getLatitude()+","+origin.getLongitude());
		if(debug) {System.out.println("Agent ("+getAgentId()+"): I'm now driving to my destination ("+calcuateWaitTime(homeToWorkRoute.getTime())+" ms) which is"+destination.getLatitude()+","+destination.getLongitude()+"the distance is"+route.getDistance());}
		System.out.println("And my worklocation is"+workLocation.getLatitude()+","+workLocation.getLongitude());
		List<Direction> directions = new ArrayList<Direction>();
		
		directions = route.getDirections();
		int [] times = new int [directions.size()];
		ArrayList<int[]> tiempos_direcciones = new ArrayList<int[]> ();
		long startSleep = System.currentTimeMillis();
		if(debug) {System.out.println("    Starting to drive at "+startSleep);}
		int initial_time[]=GlobalClock.getTime();
		for (int j=0; j<directions.size();j++) {
			times[j]=(int)(directions.get(j).getTime());
		}
		for (int i = 0;i<directions.size();i++) {
			int time_to_sum=Arrays.stream(Arrays.copyOfRange(times, 0, i)).sum();
			System.out.println(time_to_sum);
			int time_final[]= sumTime(time_to_sum,initial_time);
			tiempos_direcciones.add(time_final);
		}
		setTiemposCambio(tiempos_direcciones);
		for (int k = 1; k<tiempos_direcciones.size();k++) {
		}
	
		try {
			this.sleep(calcuateWaitTime(route.getTime()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(debug) {
			long endSleep = System.currentTimeMillis();
			System.out.println("Agent ("+getAgentId()+"): I'm now approaching my destination after "+calcuateWaitTime(homeToWorkRoute.getTime())+" ms)");
			System.out.println("    Stopped driving at: "+endSleep);
			System.out.println("    That is a diff of "+(endSleep-startSleep)+" ms");
		}
		
		updateAfterDrive((int)route.getDistance());
		
		if(debug) {System.out.println("Agent ("+getAgentId()+"): I have arrived at my destination.");}
	}



	private void calcuateErrand() {
		if(Math.random() <= ERRAND_PROBABILITY) {
			haveErrand = true;
			errandRange = ERRAND_RANGE_MIN + (int)(Math.random() * ((ERRAND_RANGE_MAX - ERRAND_RANGE_MIN) + 1));
			setErrandTime(errandRange);
		}
	}

    private void calcuateErrand(int day) {

        if(Math.random() <= ERRAND_PROBABILITY) {
            this.haveErrands[day] = true;
            this.errandRanges[day] = ERRAND_RANGE_MIN + (int)(Math.random() * ((ERRAND_RANGE_MAX - ERRAND_RANGE_MIN) + 1));
            setErrandTime(errandRange, day);
        }
        else {
			this.haveErrands[day] = false;
		}
    }

    //All the following dynamics of moving home-CS-work or work-CS-home were introduced by Manuel Pérez (manperbra@outlook.es) with the purpose
    //of making the agents charge in their way and not after they arrive to work. 

    public synchronized void doMove() {
		if(isAtWork) {
			boolean needchargeinway=getneedchargeinway();
			if (needchargeinway) {
				System.out.println("needchargeinway"+needchargeinway);
			moveToCS();
            int day =  GlobalClock.getTime()[2]-1;
			moveToHome();
			}
			else {
				moveToHome();
			}
    }
		else {
			boolean needchargeinway=getneedchargeinway();
			System.out.println("Agent"+getAgentId()+"needchargeinway"+needchargeinway);
			if(needchargeinway) {
				moveToCS();
				System.out.println("Ha terminado el movetocs");
				System.out.println("Ha salido del wait");
			}
			else {
				moveToWork();
			}
		}
	}

	private void moveToWork() {
		simulateDrive(getCurrentLocation(),workLocation);
		setCurrentLocation(workLocation);
		isAtWork = true;
		if(debug) {System.out.println("Agent ("+getAgentId()+"): I'm at work now. ("+GlobalClock.getInstance().getTimeStamp()+")");}
	}

	private void moveToHome() {
		simulateDrive(getCurrentLocation(),getHomeLocation());
		setCurrentLocation(getHomeLocation());
		isAtWork = false;
		//System.out.println("Agent ("+getId()+"): I'm home now. ("+GlobalClock.getInstance().getTimeStamp()+")");
		if(debug) {System.out.println("Agent ("+getAgentId()+"): I'm home now. ("+GlobalClock.getInstance().getTimeStamp()+")");}
//		getChargingStragegy().chargeAtHome();

	}
	private void moveToCS() {
		ChargingStation cstation= getChargingStragegy().getMostSuitableChargingStation();
		Location destination= cstation.getLocation();
		destination.setLatitude(cstation.getLocation().getLongitude());
		destination.setLongitude(cstation.getLocation().getLatitude());
		simulateDrive(getCurrentLocation(),destination);
		setCurrentLocation(cstation.getLocation());
		int bfcp= cstation.getOpenChargingPoints();
		cstation.setOpenChargingPoints(bfcp+1);
		getChargingStragegy().chargeAtWork(cstation);
		getChargingStragegy().chargingtime();
	}
	
	
	private void doErrand() {
		if(debug) {System.out.println("Agent ("+getAgentId()+"): I have an errand now (Distance: "+getErrandRange()/1000+" km |Time: "+getErrandTime()/60+" min), going to sleep.");}
		try {
			this.sleep(calcuateWaitTime(getErrandTime()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		updateAfterDrive(getErrandRange());
	}

	private void doErrand(int day) {
		if(debug) {System.out.println("Agent ("+getAgentId()+"): I have an errand now (Distance: "+getErrandRange(day)/1000+" km |Time: "+getErrandTime(day)/60+" min), going to sleep.");}
		try {
			this.sleep(calcuateWaitTime(getErrandTime(day)));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		updateAfterDrive(getErrandRange(day));
	}




	///////////////////////////////////////////////////////////////////////////
	// 							GETTERS AND SETTERS
	//////////////////////////////////////////////////////////////////////////
	/**
	 * 
	 * 
	 * @param long (seconds)
	 * @return the number of miliseconds required to simulate the waitingtime.
	 */
	private long calcuateWaitTime(long seconds) {
		return (long) ((seconds/60)*Variables.SIMULATION_SPEED);
	}

	public Location getWorkLocation() {
		return workLocation;
	}

	public double getStartWorkTime() {
		return startWorkTime;
	}

	public double getStartWorkTime(int day){ return startWorkTimes[day];}


	public void setWorkLocation(Location workLocation) {
		this.workLocation = workLocation;
	}


	public Route getHomeToWorkRoute() {
		return homeToWorkRoute;
	}


	public void setHomeToWorkRoute(Route homeToWorkRoute) {
		this.homeToWorkRoute = homeToWorkRoute;
	}

	@Override
	public boolean isAtWork() {
		return isAtWork;
	}


	public void setAtWork(boolean isAtWork) {
		this.isAtWork = isAtWork;
	}


	public double getEndWorkTime() {
		return endWorkTime;
	}

	public double getEndWorkTime(int day){ return endWorkTimes[day];}


	public void setEndWorkTime(double endWorkTime) {
		this.endWorkTime = endWorkTime;
	}


	public void setStartWorkTime(double startWorkTime) {
		this.startWorkTime = startWorkTime;
	}

	/**
	 * Sets the errandTime in total miliseconds.
	 * 
	 * @param distance (meters)
	 */
	public void setErrandTime(int distance) {

		double hrsReq = (distance/1000)/AVG_DRIVE_SPEED;
		double minReq = hrsReq*60;
		double secReq = minReq*60;
		this.errandTime = (long) secReq;
	}
	public void setErrandTime(int distance, int day) {

        double hrsReq = (distance / 1000) / AVG_DRIVE_SPEED;
        double minReq = hrsReq * 60;
        double secReq = minReq * 60;
        this.errandTimes[day] = (long) secReq;
    }

    /** Returns the number of seconds an agent will
	 * spent on his/her errands around the environment.
	 *
	 * @return long (seconds)
	 */
	public long getErrandTime() {
		return errandTime;
	}

	public int getErrandRange() {
		return errandRange;
	}

	public long getErrandTime(int day) {
		return errandTimes[day];
	}

	public int getErrandRange(int day) {
		return errandRanges[day];
	}
	
	public boolean haveErrand() {
		return haveErrand;
	}

	public boolean haveErrand(int day) {return haveErrands[day];}


 	public double getAvgStartWorkTime() {return ((START_WORK_TIME_MIN + START_WORK_TIME_MAX)/2);}

	public double getAvgEndWorkTime() {
		double time =  (this.getAvgStartWorkTime() + WORKDAY_HOURS)/2;
		return time;
	}

	private int[] sumTime(int seconds, int[] initial_time) {
		int[] time_final = {0,0,0};
		time_final[1]=(int)(initial_time[1]+(seconds/60));
		time_final[0]=initial_time[0];
		time_final[2]=initial_time[2];
		if (time_final[1]>59){
			time_final[1]=time_final[1]-59;
			time_final[0]=time_final[0]+1;
		}
				return time_final;
	}

	// The rest of this file was created by Manuel Pérez (manperbra@outlook.es)
	@Override
	public boolean getneedchargeinway2 () {
		Location destination;
		if (isAtWork) {
			destination=getHomeLocation();
		}
		else {
			destination=workLocation;
		}
		double energytodest= getCar().getCurrentEnergy()-getEnergyUsageToLocation(destination);
//		if(Variables.CHARGING_STRATEGY_FOR_AGENTS==1 || Variables.CHARGING_STRATEGY_FOR_AGENTS==2) {
		if(energytodest<=getMinimumEnergyPrefernce()) {
			needchargeinway=true;
			BufferedWriter writer5;
			try {
				writer5= new BufferedWriter(new FileWriter("C:\\Users\\manpe\\eclipse-workspace\\ABM-Steinkjer_random\\consults.txt",true));
				writer5.newLine();
				writer5.append(String.format("%02d", GlobalClock.getTime()[2])+"/01/2020 "+String.format("%02d", GlobalClock.getTime()[0])+":"+String.format("%02d", GlobalClock.getTime()[1])+";");
				writer5.append("1;");
				writer5.close();
				}
			catch (IOException e){
				e.printStackTrace();
			}
		}
		else {
			needchargeinway=false;
		}
		return needchargeinway;
	}
	
	
	@Override
	public boolean getneedchargeinway () {
		Location destination;
		if (isAtWork) {
			destination=getHomeLocation();
		}
		else {
			destination=workLocation;
		}
		double energytodest= getCar().getCurrentEnergy()-getEnergyUsageToLocation(destination);
		if(Variables.CHARGING_STRATEGY_FOR_AGENTS==1 || Variables.CHARGING_STRATEGY_FOR_AGENTS==2) {
		if(energytodest<=getMinimumEnergyPrefernce()) {
			needchargeinway=true;
		}
		else {
			needchargeinway=false;
		}
		return needchargeinway;
		}
		else  {
			if (energytodest<=0) {
				return true;
			}
			else {
			List <ChargingStation> stations= ChargingStationMap.getChargingStations();
			List<ChargingStation> stations2=new ArrayList<ChargingStation>();
			for (ChargingStation st:stations) {
				if(st.getOpenChargingPoints()>0) {
					stations2.add(st);
				}
			}
			TreeMap <Double,ChargingStation> preferencias = Utils.Search.preflist(stations2, this.getpreferences(), this.getCurrentLocation(), this.getCar().getbatteryrange(), this);
			System.out.println("stations2"+stations2);
			double randomnumber= new Random().nextDouble();
			System.out.println("Ha computado las preferencias");
			System.out.println("random is"+randomnumber+"and tree map is"+preferencias);
			System.out.println("el last entry es"+preferencias.lastEntry().getKey());
			if (randomnumber<preferencias.lastEntry().getKey()) {
				System.out.println("It will charge because random is"+randomnumber+"and last entry"+preferencias.lastEntry().getKey());
				return true;
			}
			else {
				System.out.println("It won't charge because random is"+randomnumber+"and last entry"+preferencias.lastEntry().getKey());
				return false;
			}
		}
		}
	}
//	
	private synchronized void llamarmatlab() {
	}



}
