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

	//private final static double START_WORK_TIME_MAX = 9.0;
	//private final static double START_WORK_TIME_MIN = 7.0;
	//private final static double WORKDAY_HOURS = 8.0;

    //private double START_WORK_TIME_MAX = 8.0;
    //private double START_WORK_TIME_MIN = 7.0;
    //private double WORKDAY_HOURS = 9.0;

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

	// for different working schedules // TODO: 13/04/17 update this
	//public WorkerAgent(int id, City city, int numberOfCarTypes, int numberOfWorkerAgents){
	//
	//}

	// for different cars, only implementet with three car types here
	public WorkerAgent(int id, City city, int numberOfCarTypes) {
		super(id, city, numberOfCarTypes);
		this.workLocation = city.getRandomWorkLocation();

		getChargingStragegy().setPossibleWorkChargingStations(workLocation);
		
		START_WORK_TIME_MAX = Variables.START_WORK_TIME_MAX;
		START_WORK_TIME_MIN = Variables.START_WORK_TIME_MIN;
		WORKDAY_HOURS = Variables.WORKDAY_HOURS;
		
//		List<Double> Start_work_times = new ArrayList<>();
//		Start_work_times.add(7.15976774951916);
//		Start_work_times.add(7.30941202457178);
//		Start_work_times.add(7.49526663184353);
//		Start_work_times.add(7.0050827468657);
//		Start_work_times.add(7.47908720233204);
//		Start_work_times.add(7.28657159725872);
//		Start_work_times.add(7.31185085786459);
//		Start_work_times.add(7.24201579473045);
//		Start_work_times.add(7.28790260582126);
//		Start_work_times.add(7.39608361002898);
//		Start_work_times.add(7.18521235022478);
//		Start_work_times.add(7.29941141471255);
//		Start_work_times.add(7.42323001255602);
//		Start_work_times.add(7.40436353609275);
//		Start_work_times.add(7.34376540005075);
//		Start_work_times.add(7.38015344876765);
//		Start_work_times.add(7.36195642474718);
//		Start_work_times.add(7.09207290701002);
//		Start_work_times.add(7.41660902650238);
//		Start_work_times.add(7.11702200186048);
//		
//		List<Double> End_work_times = new ArrayList<>();
//		End_work_times.add(15.2948743533417);
//		End_work_times.add(16.0727657340133);
//		End_work_times.add(15.2096898310188);
//		End_work_times.add(14.3828064020748);
//		End_work_times.add(15.6365256942323);
//		End_work_times.add(16.2436984165775);
//		End_work_times.add(15.7597016460769);
//		End_work_times.add(15.9115591149569);
//		End_work_times.add(15.452632295372);
//		End_work_times.add(16.3889812160196);
//		End_work_times.add(15.4276500531578);
//		End_work_times.add(14.5209308392465);
//		End_work_times.add(15.1825515758905);
//		End_work_times.add(14.9917326725902);
//		End_work_times.add(14.4389433594555);
//		End_work_times.add(15.3402970640525);
//		End_work_times.add(14.7650569500012);
//		End_work_times.add(14.5308461095834);
//		End_work_times.add(14.4842834624595);
//		End_work_times.add(14.9584275582085);


		
		this.homeToWorkRoute = getRouteFactory().createRouteFromAndTo(getHomeLocation(), workLocation);
		//System.out.println("Lives at"+getHomeLocation());
		//System.out.println("Works at"+workLocation);
		//System.out.println(homeToWorkRoute.getDistance());
		//System.out.println(homeToWorkRoute.getTime());
		this.direcciones=homeToWorkRoute.getDirections();
		//System.out.println(direcciones);
		//System.out.println(direcciones.size());
//		for (int i=0; i<direcciones.size(); i++) {
//			System.out.println("("+homeToWorkRoute.getDirections().get(i).getTarget().getLatitude()+","+homeToWorkRoute.getDirections().get(i).getTarget().getLongitude()+")");
//		}

		
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
		//this.startWorkTime = getRandom(START_WORK_TIME_MIN, START_WORK_TIME_MAX);  //should be done every day!!
		//this.endWorkTime = this.startWorkTime+WORKDAY_HOURS+getRandom(-0.5,0.5);  //should be random and done every day!!

		//calcuateErrand(); //should be done every day!!

	}

	// Used?
	public void updateRandomStartAndEndTimes(){
		this.startWorkTime = getRandom(START_WORK_TIME_MIN, START_WORK_TIME_MAX);  //should be done every day!!
		this.endWorkTime = this.startWorkTime+WORKDAY_HOURS+getRandom(-0.5,0.5);  //should be random and done every day!!
		this.calcuateErrand();
	}



//	public WorkerAgent(int id, City city) {
//		super(id, city);
//		this.workLocation = city.getRandomWorkLocation();
////		getChargingStragegy().setPossibleWorkChargingStations(workLocation);
////		getChargingStragegy().setReachableChargingStations(getCurrentLocation());
//	
//		this.homeToWorkRoute = getRouteFactory().createRouteFromAndTo(getHomeLocation(), workLocation);
//		while(homeToWorkRoute.getDistance() < Variables.MINIMUM_DISTANCE_TO_WORK) {
//			System.err.println("Home to work route is only "+homeToWorkRoute.getDistance()+" meters, creating new route.");
//			this.workLocation = city.getRandomWorkLocation();
//			this.homeToWorkRoute = getRouteFactory().createRouteFromAndTo(getHomeLocation(), workLocation);
//
//		}
//		setCurrentRoute(this.homeToWorkRoute);
//
//		this.isAtWork = false;
//		setDoAction(false);
//		if(debug) {System.out.println("I'm a worker agent (id: "+id+"), and I drive "+homeToWorkRoute.getDistance()+" meters ("+homeToWorkRoute.getDistance()/1000+" km) to work. (It takes "+homeToWorkRoute.getTime()+" sec to get there).");}
//		if(debug) {System.out.println("  That is: "+(homeToWorkRoute.getTime()/60)+" min");}
//		this.startWorkTime = getRandom(START_WORK_TIME_MIN, START_WORK_TIME_MAX);  //should be done every day!!
//		this.endWorkTime = this.startWorkTime+WORKDAY_HOURS;  //should be random and done every day!!
//
//		calcuateErrand(); //should be done every day!!
//
//	}


	@Override
	public Location generateHomeLocation() {
		return getCity().getRandomResidence();
	}
	
//	@Override
//	public Location generateWorkLocation() {
//		return getCity().getDeterminedWorkLocation(getAgentId());
//	}
	



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
//				while(!Variables.needsmatlab) {
//				Variables.needsmatlab=false;
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
		//if(debug) {System.out.println("Agent ("+getId()+"): I'm now dirving to my destination ("+calcuateWaitTime(homeToWorkRoute.getTime())+" ms)");}
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
		//System.out.println("Son las"+initial_time[0]+"."+initial_time[1]);
		for (int j=0; j<directions.size();j++) {
			times[j]=(int)(directions.get(j).getTime());
		}
		for (int i = 0;i<directions.size();i++) {
			int time_to_sum=Arrays.stream(Arrays.copyOfRange(times, 0, i)).sum();
			System.out.println(time_to_sum);
			//System.out.println("Sumar"+time_to_sum);
			int time_final[]= sumTime(time_to_sum,initial_time);
			tiempos_direcciones.add(time_final);
			//System.out.println("Tiempo final "+time_final[0]+":"+time_final[1]);
		}
		setTiemposCambio(tiempos_direcciones);
		for (int k = 1; k<tiempos_direcciones.size();k++) {
		//System.out.println(tiempos_direcciones.get(k)[0]+"."+tiempos_direcciones.get(k)[1]);
		//System.out.println(tiempos_direcciones.get(k)[0]+"."+tiempos_direcciones.get(k)[1]);
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
		//if(debug) {System.out.println("Agent ("+getId()+"): I have arrived at my destination.");}
		
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


//	public void doMove() {
//		simulateDrive();
//		if(isAtWork) {
//			if(haveErrand) {doErrand();}
//			moveToHome();
//		}
//		else { moveToWork();}
//	}

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
//				long tiempo_espera= Double.doubleToLongBits((getCar().getMaxEnergy()-getCar().getCurrentEnergy())/(getChargingStragegy().getTargetChargingStation().getChargeRate()/60));
//				try {
//					wait(tiempo_espera);
//				}
//				catch(InterruptedException e){
//					
//				}
//				moveToWork();
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
//		getChargingStragegy().chargeAtWork();

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
		System.out.println("Ive entered charging moveToCS");
		ChargingStation cstation= getChargingStragegy().getMostSuitableChargingStation();
		System.out.println("Station chosen is"+cstation.getID()+"for its price is"+cstation.getPriceCurrent());
		Location destination= cstation.getLocation();
		destination.setLatitude(cstation.getLocation().getLongitude());
		destination.setLongitude(cstation.getLocation().getLatitude());
		simulateDrive(getCurrentLocation(),destination);
		setCurrentLocation(cstation.getLocation());
		System.out.println("I am now at the CS!");
		int bfcp= cstation.getOpenChargingPoints();
		cstation.setOpenChargingPoints(bfcp+1);
		getChargingStragegy().chargeAtWork(cstation);
		getChargingStragegy().chargingtime();
	}
	
//	private void moveToCS() {
//		setCurrentLocation(getChargingStrategy().chargeintheway().
//	}
	
	private void doErrand() {
		//if(debug) {System.out.println("Agent ("+getId()+"): I have an errand now (Distance: "+getErrandRange()/1000+" km |Time: "+getErrandTime()/60+" min), going to sleep.");}
		if(debug) {System.out.println("Agent ("+getAgentId()+"): I have an errand now (Distance: "+getErrandRange()/1000+" km |Time: "+getErrandTime()/60+" min), going to sleep.");}
		try {
			this.sleep(calcuateWaitTime(getErrandTime()));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Agent ("+getId()+"): I woke up from my errand sleep now.");
		updateAfterDrive(getErrandRange());
	}

	private void doErrand(int day) {
		//if(debug) {System.out.println("Agent ("+getId()+"): I have an errand now (Distance: "+getErrandRange()/1000+" km |Time: "+getErrandTime()/60+" min), going to sleep.");}
		if(debug) {System.out.println("Agent ("+getAgentId()+"): I have an errand now (Distance: "+getErrandRange(day)/1000+" km |Time: "+getErrandTime(day)/60+" min), going to sleep.");}
		try {
			this.sleep(calcuateWaitTime(getErrandTime(day)));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.out.println("Agent ("+getId()+"): I woke up from my errand sleep now.");
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
		//System.out.println(initial_time[0]+":"+initial_time[1]);
		time_final[1]=(int)(initial_time[1]+(seconds/60));
		time_final[0]=initial_time[0];
		time_final[2]=initial_time[2];
		if (time_final[1]>59){
			time_final[1]=time_final[1]-59;
			time_final[0]=time_final[0]+1;
		}
				return time_final;
	}

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
//		setRunning(false);
//		Variables.needsmatlab=true;
//				try {
//			model.schdeule.Coord_matlab.llamamiento2();
//			setRunning(true);
//			Variables.needsmatlab=false;
////			this.wait(5000);
//		}
//		catch(Exception e) {}
//		finally {Variables.needsmatlab=false;}
//		System.out.println("Hola");
//		Variables.needsmatlab=false;
//		try {
//			Variables.needsmatlab=true;
//			System.out.println("Hola estoy aqui");
//			ChargingStationMap.getChargingStations().get(1).setPriceCurrent(140);
//			System.out.println("El precio se ha cambiado a "+ChargingStationMap.getChargingStations().get(1).getPriceCurrent());
//			Variables.needsmatlab=false;
//		}
//		catch(InterruptedException e) {}
	}



}
