package model.agent;

import java.io.File;

import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentHashMap;

import Environment.City;

import Utils.Variables;
import model.schdeule.GlobalClock;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;
import jxl.read.biff.BiffException;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;

import model.navigation.Location;





public class AgentSupervisoryManager {

	private static volatile AgentSupervisoryManager instance = null;

	private ArrayList<WorkerAgent> workerAgents;
	private List<Thread> agentThreads;
	private City city;
	private GlobalClock globalClock = GlobalClock.getInstance();
	private double totalStoredEnergy = 0;

	private ConcurrentHashMap<String, ArrayList<Double>> agentSOCs;

	private boolean debug = (Variables.DEBUG_ALL || Variables.DEBUG_AgentSupervisoryManager);


	//old
	public AgentSupervisoryManager(int noOfWorkerAgents, City city) {
		this.city = city;
		this.workerAgents = new ArrayList<>();
		this.agentThreads = new ArrayList<>();

		generateAgentWorkers(noOfWorkerAgents);

		registerAgentsToSchedule(Variables.SIMULATION_DAYS);
		startAgents();
	}

	public AgentSupervisoryManager() {
		this.city = City.getInstance();
		this.workerAgents = new ArrayList<>();
		this.agentThreads = new ArrayList<>();

		this.agentSOCs = new ConcurrentHashMap<String,ArrayList<Double>>();

		generateAgentWorkers(Variables.NUMBER_OF_WORKER_AGENTS);

		registerAgentsToSchedule(Variables.SIMULATION_DAYS);
		startAgents();
	}

	public static AgentSupervisoryManager getInstance() {
		if (instance == null) {
			synchronized (AgentSupervisoryManager .class){
				if (instance == null) {
					instance = new AgentSupervisoryManager();
				}
			}
		}
		return instance;
	}

	public void generateAgentWorkers(int size) {
		System.out.println("Creating "+size+" agents");
		for(int i = 0; i<size; i++) {
			// WorkerAgent agent = new WorkerAgent(i, city);
			WorkerAgent agent = new WorkerAgent(i, city, 3); //id, city, number of car types
			// WorkerAgent agent = new WorkerAgent(i, city, 3, 3); //id, city, number of car types, number of workerTypes
			workerAgents.add(agent);
			

			Thread t = new Thread(agent);
			agentThreads.add(t);
		}
		System.out.println("Done creating "+size+" agents");

	}


	public void updateAgentsScedules(){
		for(WorkerAgent wAgent: workerAgents) {
			wAgent.updateRandomStartAndEndTimes();
		}
		registerAgentsToSchedule(Variables.SIMULATION_DAYS);
	}


	// Modified by Manuel Pérez (manperbra@outlook.es) from here
	
	public void registerAgentsToSchedule(int days) {
		HashMap<String, ArrayList<Agent>> schedule = globalClock.getSchedule();
	for(int day = 0; day<days; day++) {
		// START TIME
		for (WorkerAgent wAgent : workerAgents) {
			String startTime = String.format("%02d", day+1)+"-"+doubleToTimeString(wAgent.getStartWorkTime(day));
			if(debug) {System.out.println("I'm worker agent (id: "+wAgent.getAgentId()+"), and for day "+(day+1)+" I start work at "+doubleToTimeString(wAgent.getStartWorkTime(day)));}
			if (schedule.get(startTime) == null) {
				ArrayList<Agent> newAgentSchedule = new ArrayList<>();
				newAgentSchedule.add(wAgent);

				schedule.put(startTime, newAgentSchedule);
			} else {
				ArrayList<Agent> agentSchedule = schedule.get(startTime);
				agentSchedule.add(wAgent);
				schedule.put(startTime, agentSchedule);

			}
		}

		// END TIME

		for (WorkerAgent wAgent : workerAgents) {
			String endTime = String.format("%02d", day+1)+"-"+doubleToTimeString(wAgent.getEndWorkTime(day));
			if(debug) {System.out.println("I'm worker agent (id: "+wAgent.getAgentId()+"), and for day "+(day+1)+" I end work at "+doubleToTimeString(wAgent.getEndWorkTime(day)));}
			if (schedule.get(endTime) == null) {
				ArrayList<Agent> newAgentSchedule = new ArrayList<>();
				newAgentSchedule.add(wAgent);

				schedule.put(endTime, newAgentSchedule);
			} else {
				ArrayList<Agent> agentSchedule = schedule.get(endTime);
				agentSchedule.add(wAgent);
				schedule.put(endTime, agentSchedule);

			}
		}
	}
	}


	// To be used? no..
	public void registerAgentToSchedule(WorkerAgent wAgent) {
		HashMap<String, ArrayList<Agent>> schedule = globalClock.getSchedule();
		//for information: public HashMap<String, ArrayList<Agent>> getSchedule() {return schedule;}

		// START TIME
		{
			String startTime = doubleToTimeString(wAgent.getStartWorkTime());

			if(schedule.get(startTime) == null) {
				ArrayList<Agent> newAgentSchedule = new ArrayList<>();
				newAgentSchedule.add(wAgent);

				schedule.put(startTime,newAgentSchedule);
			}
			else {
				ArrayList<Agent> agentSchedule = schedule.get(startTime);
				agentSchedule.add(wAgent);
				schedule.put(startTime ,agentSchedule);

			}
		}

		// END TIME

		{
			String endTime = doubleToTimeString(wAgent.getEndWorkTime());
			if(schedule.get(endTime) == null) {
				ArrayList<Agent> newAgentSchedule = new ArrayList<>();
				newAgentSchedule.add(wAgent);

				schedule.put(endTime,newAgentSchedule);
			}
			else {
				ArrayList<Agent> agentSchedule = schedule.get(endTime);
				agentSchedule.add(wAgent);
				schedule.put(endTime ,agentSchedule);

			}
		}
	}

	public void startAgents() {
		for(Thread agent: agentThreads) {
			agent.start();
		}
	}
	
	public List<Thread> getagentthreads(){
		return agentThreads;
	}

	public String doubleToTimeString(double d) {
		
		int hour = (int) d;
		double remainder = d-hour;
		int minutes = (int) (remainder*60);
		String res = String.format("%02d", hour)+String.format("%02d", minutes);
		return res;
	}

	public double getTotalStoredEnergy() {return totalStoredEnergy;}

	public ArrayList<Double> getagentSOCs() {
		ArrayList<Double> SOCs =  new ArrayList<Double>();
		for (WorkerAgent wAgent : workerAgents) {
			SOCs.add(wAgent.getCar().getCurrentEnergy());
		}
		return SOCs;
	}
	
	public ArrayList<Location> getagentLocations() {
		ArrayList<Location> Locations =  new ArrayList<Location>();
		for (WorkerAgent wAgent : workerAgents) {
			Locations.add(wAgent.getCurrentLocation());
		}
		return Locations;
	}
	

	public void noteAgentSOCsTime(){
		//HashMap<String, ArrayList<Double>> agentSOCs;
		this.agentSOCs.put(WAgetTimeStamp(),getagentSOCs());

	}

	public String WAgetTimeStamp() {
		return GlobalClock.getTime()[2]+"-"+String.format("%02d", GlobalClock.getTime()[0])+":"+String.format("%02d", GlobalClock.getTime()[1]);
	}


	public void doStatistics() {

			System.out.println("Generating agent statistics from Simulation...");
			SortedMap<String, ArrayList<Double>> sortedSOCs = Utils.Statistics.StringDateComparator.sortHashMapDoubleList(agentSOCs);
			Utils.Statistics.ExcelWriter.writeOutHashMapList(sortedSOCs);



			System.out.println(".... Done");
			System.out.println("No. of agents: " + Variables.NUMBER_OF_WORKER_AGENTS);

	}

	public void writeAgentInfo()
	throws BiffException, IOException, WriteException
	{
		System.out.println("Generating info of agent in the simulation...");

		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(new File(Variables.PATH+"/InfoAgents.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);

		Label row = new Label(0,0,"Agent ID");
		Label row1 = new Label(0,1,"Car type");

		sheet.addCell(row);
		sheet.addCell(row1);

		for (int i = 0 ; i<Variables.SIMULATION_DAYS; i++) {
			Label row2 = new Label(0, i*2+2, "Start time day "+(i+1));
			Label row3 = new Label(0,i*2+3,"End time day "+(i+1));
			sheet.addCell(row2);
			sheet.addCell(row3);
		}

		int cournter = 0;
		for(WorkerAgent wAgent : workerAgents){
			int agentID = wAgent.getAgentId();
			if(cournter>= Variables.AGENT_PRINT_LIMIT){
				break;
			}
			Label agntid = new Label(agentID+1, 0, "Agent ID: "+(agentID));
			Label cartype = new Label(agentID+1, 1, wAgent.getCar().getName()+" "+wAgent.getCar().getTypeModel());

			sheet.addCell(agntid);
			sheet.addCell(cartype);

			for (int i = 0 ; i<Variables.SIMULATION_DAYS; i++) {

				Label start = new Label(agentID+1, i*2+2,doubleToTimeString(wAgent.getStartWorkTime(i)));
				Label end = new Label(agentID+1,i*2+3,doubleToTimeString(wAgent.getEndWorkTime(i)));
				sheet.addCell(start);
				sheet.addCell(end);
			}
			cournter++;

		}

		workbook.write();
		workbook.close();


		System.out.println(".... Done");
	}
	
	public int getAgentsnumber() {
		int number=workerAgents.size();
		return number;
	}
	
	public ArrayList<int[]> getAgentTimes(Agent agent) {
		ArrayList<int[]> times =  agent.getTiemposCambio();
		return times;
	}
	
	public ArrayList<String> getAgentTimesString(Agent agent){
		ArrayList<String> timesString= new ArrayList<String>();
		for (int m=1;m<agent.getTiemposCambio().size();m++) {
			timesString.add(String.format("%02d",agent.getTiemposCambio().get(m)[0])+String.format("%02d", agent.getTiemposCambio().get(m)[1]));
	}
		return timesString;
	}
	
	public ArrayList<WorkerAgent> getAgents() {
		ArrayList<WorkerAgent> agents =  workerAgents;
		return agents;
	}


	//public ArrayList<WorkerAgent> getWorkerAgents() {return workerAgents;}
}
