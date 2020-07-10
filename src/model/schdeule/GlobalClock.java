package model.schdeule;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.BufferedWriter;


import com.mathworks.engine.EngineException;
import com.mathworks.engine.MatlabEngine;

import Utils.Variables;
import factory.ChargingStationFactory;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import model.agent.Agent;
import model.agent.AgentSupervisoryManager;
import model.agent.WorkerAgent;
import model.powerutilities.ChargingStationMap;
import model.powerutilities.ChargingStation;
import model.powerutilities.PowerGrid;

import static model.agent.AgentSupervisoryManager.*;
import com.mathworks.engine.EngineException;
import com.mathworks.engine.MatlabEngine;
import com.mathworks.engine.MatlabExecutionException;
import com.mathworks.engine.MatlabSyntaxException;


public class GlobalClock extends Thread{

	// Singleton
    private static volatile GlobalClock instance = null;
    public boolean active;
	
    // Virtual time
	private long counter;
	private static int[] time;
	private int speed;
	private int simulationDays;
	private int simulationHours;
	boolean stopwatch;

	// Schedule
	private HashMap<String,ArrayList<Agent>> schedule;
	//private HashMap<Integer, HashMap<String,ArrayList<Agent>>> totalSchedule;

	// SOCs agent id, time, soc
	//private HashMap<String, ArrayList<Double>> agentSOCs;

	// Debug mode
	private boolean debug = (Variables.DEBUG_ALL || Variables.DEBUG_GlobalClock);
    
    private GlobalClock() {
    	this.schedule = new HashMap<String,ArrayList<Agent>>();
		//this.totalSchedule = new HashMap<Integer, HashMap<String,ArrayList<Agent>>>();
		//this.agentSOCs = new HashMap<String, ArrayList<Double>>();

		this.active = true;
		
		this.time = new int[3]; 
		this.time[0] = 0; // Hour
		this.time[1] = 0; // Minute
		this.time[2] = 1; // Day
		
		this.speed = Variables.SIMULATION_SPEED;
		this.counter = Variables.COUNTER;
		
		//this.simulationDays = 2;
		
	}
	
	public static GlobalClock getInstance() {
		 if (instance == null) {
             synchronized (GlobalClock .class){
                     if (instance == null) {
                             instance = new GlobalClock ();
                     }
           }
     }
     return instance;
	}
	
	public void run() {
	

		while(active && !Variables.needsmatlab) {
			try {
				this.sleep((long) speed);

				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			printTimeAt30();
			incrementTime();
			

//			System.out.println("Im running");
 			
//			try {
//				incrementTime();
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalStateException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			checkScheduleNew();

				//notefies agents if they are to do stuff
//			try {
//				checkScheduleNew();
//			} catch (IllegalArgumentException | IllegalStateException | IllegalExecutionException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			if(Variables.EXCEL_OUTPUT!=1) { noteagentSOCs();}
		}
	}

	private void noteagentSOCs(){
		//ArrayList<double> SOCs = AgentSupervisoryManager.getagentSOCs();
		//agentSOCs.put(getTimeStamp(),AgentSupervisoryManager.getagentSOCs());
		//AgentSupervisoryManager.getagentSOCs();
		AgentSupervisoryManager.getInstance().noteAgentSOCsTime();

		//HashMap<String, ArrayList<Double>> agentSOCs;
		//Variables.NUMBER_OF_WORKER_AGENTS

	}

	private void checkScheduleNew() { //throws IllegalArgumentException, IllegalStateException, ExecutionException {
		if(time[0] > simulationHours || time[2]>simulationDays) {
			active = false;  // terminates simulation clock
			System.out.println(Variables.COUNTER);
			System.out.println("STOPPING THE SIMULATION.");
			
			PowerGrid.getInstance().doStatistics();


			if (Variables.EXCEL_OUTPUT == 2 || Variables.EXCEL_OUTPUT == 4) {

				AgentSupervisoryManager.getInstance().doStatistics();

				try {
					AgentSupervisoryManager.getInstance().writeAgentInfo();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (BiffException e) {
					e.printStackTrace();
				} catch (WriteException e) {
					e.printStackTrace();
				}
			}

			System.exit(0);
		}
		else if(schedule.get(dayAndTimeToString()) != null) { //ONly enters when exception 
			Boolean really_needs;
			really_needs=false;
			ArrayList<Agent> agents = schedule.get(dayAndTimeToString());
			if(debug){System.out.println("The time is "+dayAndTimeToString()+" (That is, day "+time[2]+", at "+timeToString()+")");
			System.out.println("Notifying "+agents.size()+" agents.");}
			Variables.needsmatlab=true;
			for (Agent ag:agents) {
				if(ag.getneedchargeinway2()) {
					really_needs=true;
				}
			} 
			while(Variables.needsmatlab) {
			try {
				System.out.println("Llamando a matlab");
				model.schdeule.Coord_matlab.llamamiento2(really_needs);
				this.wait();
				System.out.println("hola");
//				Variables.needsmatlab=false;
			}
			catch (Exception e) {}
			}
			System.out.println("Matlab fini");
			for(Agent agent: agents) {
				if(debug){System.out.println("Agent ("+agent.getAgentId()+") has been notified.");}
				agent.setDoAction(true);
				agent.up(); // notefies the agent


			}
			ArrayList<WorkerAgent> agencitos = AgentSupervisoryManager.getInstance().getAgents();
			System.out.println("ok");
			System.out.println("Variable"+Variables.needsmatlab);
			for(Agent ag:agencitos) {
				System.out.println("Is agent"+ag.getAgentId()+" runninr"+ag.isRunning());
				System.out.println("Is agent"+ag.getAgentId()+" charging"+ag.getChargingStragegy().isCharging());
				System.out.println("Is agent need to charge"+ag.getChargingStragegy().needToCharge());
				System.out.println("Is agent"+ag.getAgentId()+"doaction"+ag.isDoAction());
				if(ag.getChargingStragegy().isCharging()) {
					ag.up();
					ag.getCar().setCurrentEnergy(ag.getCar().getCurrentEnergy()- (ag.getCar().getChargeRate()/80));
					ag.setag_counter(Variables.COUNTER-1);
				}
			}
			
			if(debug){System.out.println("Done notifying the "+agents.size()+" agents.");}
		}

	}

	
	private synchronized void incrementTime() { //throws MatlabExecutionException, MatlabSyntaxException, IllegalArgumentException, IllegalStateException, ExecutionException  {
		Variables.COUNTER++;
		counter=Variables.COUNTER;
		active=true;
		Variables.COUNTERMATLAB++;
//		Variables.COUNTERMATLAB++;
//		
		
//		while (Variables.COUNTER>30) {
//			try {
//				// Aqui deberia ir mandar la matriz y poner a 0 la variable recibido
//				System.out.println("Stopping simulation, calculating energy prices");
//				System.out.println("The location of agents are:"+AgentSupervisoryManager.getInstance().getagentLocations());
//				System.out.println("The battery of agents are"+AgentSupervisoryManager.getInstance().getagentSOCs());
//				Variables.COUNTER--;
//				Variables.COUNTERMATLAB--;
//				
//				System.out.println(Variables.COUNTER);
//				System.out.println(Variables.COUNTERMATLAB);
//				//Variable recibido a 0, iniciar proceso de mandar y recibir
//				//Coord_matlab.llamamiento();
//				//wait(5000);
//				
//				Variables.COUNTER=0;
				//Quien debe reintroducir counter=0 es la funcion de toma de matriz de matpower 
//				Scanner in = new Scanner(System.in);
//				int reactivar = Integer.parseInt(in.nextLine());
//				if (reactivar==1) {
//					notifyAll();
//				}
//			}
//			catch (InterruptedException e) {
//				Thread.currentThread().interrupt();		
//				System.out.println("Holis");
//			}
			
//		}
//		ArrayList<WorkerAgent> agents = AgentSupervisoryManager.getInstance().getAgents();
//		int indice= 0;
//		for (WorkerAgent wAgent:agents) {
//			//System.out.println(AgentSupervisoryManager.getInstance().getAgentTimesString(wAgent));
//			if (AgentSupervisoryManager.getInstance().getAgentTimesString(wAgent).contains(timeToString())) {
//				indice = AgentSupervisoryManager.getInstance().getAgentTimesString(wAgent).indexOf(timeToString());	
//				System.out.println("The agent"+wAgent.getAgentId()+"is moving towards"+wAgent.getCurrentRoute().getDirections().get(indice).getTarget());
				//wAgent.setCurrentLocation(wAgent.getCurrentRoute().getDirections().get(indice).getTarget());
				//System.out.println("Bingo");
//			}
//		}
//		Variables.COUNTERMATLAB++;
//		try {
//			while(Variables.COUNTERMATLAB>10) {
//				llamarmatlab();
//				wait(5000);
//				
//				Variables.COUNTERMATLAB=0;
//			}
//		}
//			catch (InterruptedException e) {}
			
		
		//System.out.println(AgentSupervisoryManager.getInstance().getagentSOCs());
		//System.out.println(AgentSupervisoryManager.getInstance().getagentLocations());
		if(time[1] >= 59) {
			time[1] = 0;
			time[0]++;
			if(time[0] == 24) {
				time[0] = 0;
				//nextSchedule();
				time[2]++;
			}
		}
		else {
			time[1]++;
		}
		
		BufferedWriter writer;
		BufferedWriter writer2;
		BufferedWriter writer3;
		BufferedWriter writer5;
		
		try {
			writer = new BufferedWriter(new FileWriter("C:\\Users\\manpe\\eclipse-workspace\\ABM-Steinkjer_random\\stations_file.txt",true));
			writer.newLine();
			writer.append(String.format("%02d", time[2])+"/01/2020 "+String.format("%02d", time[0])+":"+String.format("%02d", time[1])+";");
			
			List<ChargingStation> cstations = ChargingStationMap.getChargingStations();
			for (int i=0;i<cstations.size();i++) {
				writer.append(";"+cstations.get(i).getCurrentKW());			
			}
			for (int i=0;i<cstations.size();i++) {
				writer.append(";"+cstations.get(i).getPriceCurrent());			
			}
			
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			writer2 = new BufferedWriter(new FileWriter("C:\\Users\\manpe\\eclipse-workspace\\ABM-Steinkjer_random\\agents_file.txt",true));
			writer2.newLine();
			writer2.append(String.format("%02d", time[2])+"/01/2020 "+String.format("%02d", time[0])+":"+String.format("%02d", time[1])+";");
			List<WorkerAgent> agents = AgentSupervisoryManager.getInstance().getAgents();
			for (Agent ag:agents) {
				if(ag.getChargingStragegy().isCharging()) {
					double price=ag.getChargingStragegy().getTargetChargingStation().getPriceCurrent();
					writer2.append(""+ag.getAgentId()+";"+ag.getCar().getCurrentEnergy()+";"+ag.getChargingStragegy().getTargetChargingStation().getID()+";1;"+price+";");
			}
				else {
					writer2.append(ag.getAgentId()+";"+ag.getCar().getCurrentEnergy()+";;0;0;");
				}
				}
			for (Agent ag:agents) {
				writer2.append(ag.getCurrentLocation().getLatitude()+","+ag.getCurrentLocation().getLongitude()+";");
			}
			writer2.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			writer3 = new BufferedWriter(new FileWriter("C:\\Users\\manpe\\eclipse-workspace\\ABM-Steinkjer_random\\losses.txt",true));
			writer3.newLine();
			List<WorkerAgent> agents = AgentSupervisoryManager.getInstance().getAgents();
			boolean ch=false;
			for (Agent ag:agents) {
				if (ag.getChargingStragegy().isCharging()) {
					ch=true;
				}
			}
			if (ch) {
			writer3.append(String.format("%02d", time[2])+"/01/2020 "+String.format("%02d", time[0])+":"+String.format("%02d", time[1])+";");
			writer3.append(Variables.losses_real+";"+Variables.losses_imag);
			
			}
			else {
				writer3.append(String.format("%02d", time[2])+"/01/2020 "+String.format("%02d", time[0])+":"+String.format("%02d", time[1])+";");
				writer3.append(0+";"+0);
			}
			writer3.close();
		}catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		try {
			writer5 = new BufferedWriter(new FileWriter("C:\\Users\\manpe\\eclipse-workspace\\ABM-Steinkjer_random\\voltages.txt",true));
			writer5.newLine();
			writer5.append(String.format("%02d", time[2])+"/01/2020 "+String.format("%02d", time[0])+":"+String.format("%02d", time[1])+";");
			writer5.append(Arrays.toString(Variables.voltage_magnitudes)+";"+Arrays.toString(Variables.voltage_angles));
			writer5.close();
		}catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
//		List<WorkerAgent> agents = AgentSupervisoryManager.getInstance().getAgents();
//		double[] charging_loads= new double[agents.size()];
//		for (Agent ag:agents) {
//			if (ag.getChargingStragegy().isCharging()) {
//			charging_loads[ag.getAgentId()]=ag.getCar().getChargeRate();
//		}
//			else {
//				charging_loads[ag.getAgentId()]=0;
//			}
//				
//		}
//		Arrays.sort(charging_loads);	
//		List<ChargingStation> cstations=ChargingStationMap.getChargingStations();
//		TreeMap <Double,ChargingStation> st_ordered= Utils.Search.pricelist(cstations);
//		int k=agents.size()-1;
//		double totalito=0;
//		for (Map.Entry<Double, ChargingStation>entry:st_ordered.entrySet()) {
//			for (int j=0;j<entry.getValue().getMaxChargingPoints();j++) {
//				if (k>=0) {
//				totalito=totalito+(entry.getValue().getPriceCurrent()*charging_loads[k]);
//				}
//				k--;
//			}
//		}
//		
//		BufferedWriter writer4;
//		try {
//			writer4 = new BufferedWriter(new FileWriter("C:\\Users\\manpe\\eclipse-workspace\\ABM-Steinkjer_random\\minimum_cost.txt",true));
//			writer4.newLine();
//			writer4.append(String.format("%02d", time[2])+"/01/2020 "+String.format("%02d", time[0])+":"+String.format("%02d", time[1])+";");
//			writer4.append(totalito+";");
//			writer4.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
			
			
			
		
		
	}



//	private void nextSchedule() {
//		totalSchedule.put(time[2], schedule);
		// update probs (times) and schedule?
//		if(time[2] < simulationDays) {
			// update scedule
//			this.schedule = new HashMap<String,ArrayList<Agent>>();
			// update probs --> this is done in the agents!!!?
			//AgentSupervisoryManager.updateAgentsScedules();

//			ArrayList<WorkerAgent> workerAgents = new ArrayList<>();
//			workerAgents = AgentSupervisoryManager.getWorkerAgents();
//			for(WorkerAgent wAgent: workerAgents) {
//				wAgent.updateRandomStartAndEndTimes();
//				AgentSupervisoryManager.registerAgentToSchedule(wAgent);
//			}
			// make new schedule, save old?
			//schedule = new HashMap<String, ArrayList<Agent>>;
			//globalClock.setSchedule(schedule);
			//AgentSupervisoryManager.registerAgentsToSchedule();
			//
//		}


	/**
	 * Return the current virtual time in a String format "hhmm".<br>
	 * i.e. time[0] = 6 and time[1] = 8 -> "0608"
	 * 
	 */
	public String timeToString() {
		//timeToString @ time[0] = 6 and time[1] = 8, i.e. "0608".
		return String.format("%02d", time[0])+String.format("%02d", time[1]);
	}

    public String dayAndTimeToString() {
        //timeToString @ time[0] = 6, time[1] = 8 and time[2]=1 i.e. "01-0608".
        return String.format("%02d", time[2])+"-"+String.format("%02d", time[0])+String.format("%02d", time[1]);
    }
	
	public static int[] getTime() {
		return time;
	}

	public HashMap<String, ArrayList<Agent>> getSchedule() {
		return schedule;
	}

	public void setSchedule(HashMap<String, ArrayList<Agent>> schedule) {
		this.schedule = schedule;
	}
	
	public void printTimeAt30() {
		if(time[1] == 00) {
			System.out.println("The time is "+timeToString()+" (Day "+time[2]+")");
			System.out.println("losses= "+Variables.losses_real+";"+Variables.losses_imag);
			//System.out.println(AgentSupervisoryManager.getInstance().getagentSOCs());
			//System.out.println(AgentSupervisoryManager.getInstance().getagentLocations());
			}
		
		if(time[1] == 30) {
			System.out.println("The time is "+timeToString()+" (Day "+time[2]+")");
		}
		if(time[1] == 15) {
			System.out.println("The time is "+timeToString()+" (Day "+time[2]+")");
		}
		if(time[1] == 45) {
			System.out.println("The time is "+timeToString()+" (Day "+time[2]+")");
		}
		if(time[1] == 0) {
			System.out.println("The time is "+timeToString()+" (Day "+time[2]+")");
		}
		if(time[1] == 20) {
			System.out.println("The time is "+timeToString()+" (Day "+time[2]+")");
		}
		if(time[1] == 40) {
			System.out.println("The time is "+timeToString()+" (Day "+time[2]+")");
		}
		if(time[1] == 50) {
			System.out.println("The time is "+timeToString()+" (Day "+time[2]+")");
		}
	
	}
	

	
	
	/**
	 * Returns the current hour and day in a String format "d-hh". <br>
	 * e.g. 06:21 at day 3, "3-06"
	 * 
	 */
	public String getHourDay() {
		return time[2]+"-"+String.format("%02d", time[0]);
	}
	
	/**
	 * Returns the current hour, halfhour, and day in a String format "d-hh:mm". <br>
	 * e.g. 06:21 at day 3, "3-06:00"
	 * e.g. 05:45 at day 4, "4-05:30"
	 * 
	 */
	public String getHourMinDays() {
		String mins;
		if(time[1] >= 30) {
			mins = "30";
		}
		else {
			mins = "00";
		}
		return time[2]+"-"+String.format("%02d", time[0])+":"+mins;
	}
	
	public void setSimulationDays(int simulationDays) {
		this.simulationDays = simulationDays;
	}
	
	public void setSimulationHours(int simulationHours) {
		this.simulationHours = simulationHours;
	}
	
	public int getSimulationDays() {
		return simulationDays;
	}
	
	public int getSimulationHours() {
		return simulationHours;
	}
	
	public String getTimeStamp() {
		return time[2]+"-"+String.format("%02d", time[0])+":"+String.format("%02d", time[1]);
	}

	public ArrayList<String> tiemposString(ArrayList<int[]> tiemposint){
		ArrayList<String> tiemposenstring = new ArrayList<String>();
		for (int m=1;m<tiemposint.size();m++) {
			tiemposenstring.add(String.format("%02d", tiemposint.get(m)[0])+String.format("%02d", tiemposint.get(m)[1]));
		}
		return tiemposenstring;
	}

	
//	public synchronized void parar(int contador) {
//		contador= Variables.COUNTER;
//		if (contador>180)
//			try {
//				Thread.currentThread().wait();
//				System.out.println("The clock is stopped");
//				System.out.println("Enter 1 when you want to restart:");
//				Scanner in = new Scanner(System.in);
//				int answer = Integer.parseInt(in.nextLine());
//				while (answer!=1) {
//					System.out.println("Enter 1 when you want to restart:");
//					answer = Integer.parseInt(in.nextLine());
//				}
//				if (answer==1) {
//					contador=0;
//					Thread.currentThread().notify();
//				}
//				
//			}
//		catch (InterruptedException e) {
//			Thread.currentThread().interrupt();
//			
//		}
//	}


	//public void doScheduleStatistics() {
	//	System.out.println("Generating statistics from Simulation...");
	//	SortedMap<String, Double> sortedHourlyUsage = Utils.Statistics.StringDateComparator.sortHashMap();
	//	SortedMap<String, Double> sortedChargersUsage = Utils.Statistics.StringDateComparator.sortHashMapInt(hourlyAgents);
	//	//SortedMap<String, Double> sortedStateOfCharge = Utils.Statistics.StringDateComparator.sortHashMap(minuteStateOfCharge);
	//	//Sondre stuff
	//	//SortedMap<String, Double> sortedAgentIDs = Utils.Statistics.StringDateComparator.sortHashMapInt(agentIDs);
	//	//SortedMap<String, Double> sortedAgentChargingStationIDs = Utils.Statistics.StringDateComparator.sortHashMapInt(agentChargingStationIDs);
	//	//SortedMap<String, Double> sortedAgentChargingValues = Utils.Statistics.StringDateComparator.sortHashMap(agentChargingValues);
//
//		Utils.Statistics.ExcelWriter.writeOutHashMap(sortedHourlyUsage, sortedChargersUsage);

		//Utils.Statistics.ExcelWriter.writeOutHashMap(sortedHourlyUsage, sortedChargersUsage, sortedStateOfCharge);

		//Utils.Statistics.ExcelWriter.writeOutHashMap(sortedHourlyUsage, sortedChargersUsage, sortedStateOfCharge, sortedAgentIDs, sortedAgentChargingStationIDs, sortedAgentChargingValues);
//		System.out.println(".... Done");
//		System.out.println("No. of agents: "+Variables.NUMBER_OF_WORKER_AGENTS);
//	}


	
}
