package Environment;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import Utils.Variables;
import model.agent.AgentSupervisoryManager;
import model.powerutilities.PowerGrid;
import model.schdeule.GlobalClock;
import model.powerutilities.PowerGrid;

public class World {
	
	private City city;
	private AgentSupervisoryManager agentSupervisoryManager; 
	private PowerGrid powerGrid;
	
	public World() {
		this.powerGrid=PowerGrid.getInstance();
		this.agentSupervisoryManager =  AgentSupervisoryManager.getInstance(); //generates all agents and make them ready for action

		GlobalClock.getInstance().start(); //starts the clock and the simulations
		
	}
	
	public static void userInput() {
		Variables.EXCEL_OUTPUT = 0;
		Scanner in = new Scanner(System.in);
		System.out.println("USER INPUT - Please enter the following ");
		
		System.out.println("Number of agents: ");
		int agents = Integer.parseInt(in.nextLine());
		Variables.NUMBER_OF_WORKER_AGENTS = agents;
		
		System.out.println("Number of simulation days (2 or higher): ");
		int days = Integer.parseInt(in.nextLine());
		GlobalClock.getInstance().setSimulationDays(days);
		Variables.SIMULATION_DAYS = days;
		Variables.SIMULATION_HOURS = days*24;
		
		System.out.println("Charging Strategy? 1=C, 2=D, 3=LP");
		int strat=Integer.parseInt(in.nextLine());
		Variables.CHARGING_STRATEGY_FOR_AGENTS=strat;
	}
	


	
	
	public static void main(String[] args) {
		userInput();
		
		World world = new World();
		

	}
	

	}


