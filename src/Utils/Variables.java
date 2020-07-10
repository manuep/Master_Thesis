package Utils;

import java.io.IOException;
import java.util.ArrayList;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import com.mathworks.util.Log;

import Environment.City;
import Utils.Variables;
import factory.RouteFactory;
import model.navigation.Direction;
import model.navigation.Location;
import model.navigation.Route;
import model.powerutilities.ChargingStation;
import model.powerutilities.ChargingStationMap;
import model.schdeule.Coord_matlab;
import model.schdeule.GlobalClock;

public final class Variables {
	
	// Significant changes introduced by Manuel Pérez (manperbra@outlook.es) on the public variables
	public static double losses_real= 0;
	public static double losses_imag=0;
	public static double[] voltage_magnitudes= {1,1,1,1,1,1,1,1,1,1,1,1,1,1,1};
	public static double[] voltage_angles= {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
	
	public static int COUNTER=0;
	public static int COUNTERMATLAB=0;
	public static boolean needsmatlab=false;
	public static int stationscount=0;
	public static final double LONGITUDE_REFERNCE_POINT = 0; // Used for distance calculations.
	public static final double LATITUDE_REFERNCE_POINT = 0; // Used for distance calculations.
	public static final int SIMULATION_SPEED = 10; // One virtual minute simulated in SIMULATION_SPEED ms.
	public static int NUMBER_OF_WORKER_AGENTS = 2; // How many agents (worker types)
	public static final int MAX_DISTANCE_TO_CHARGING_STATION_AT_WORK = 300; // meters
	public static final double MIN_DESIRED_BATTERY_LEVEL = 0.2;


	public static int MINIMUM_DISTANCE_TO_WORK = 1000; // In meters
	public static int SIMULATION_DAYS = 2;
	public static int SIMULATION_HOURS = 1;

	public static boolean LOCAL_ROUTES = false;
	public static int PEAK_LIMIT = Integer.MAX_VALUE;
	public static int AGENT_PRINT_LIMIT = 100;
	
	public static final String PATH = "directory";
	public static final String LOCAL_CHARGING_STATIONS_JSON_DIR=PATH+"/src/res/steinkjer.json";

	public static final String LOCAL_GDIR_JSON_ROUTES_DIR = PATH+"/src/res/routes-sondre.json";


	public final static double START_WORK_TIME_MAX = 8.0;
	public final static double START_WORK_TIME_MIN = 7.0;
	public final static double WORKDAY_HOURS = 8.0;

	public final static double PRICE_MIN = 30.0;
	public final static double PRICE_HOMES = 35.0;
	//public final static double PRICE_HOMES_ROOT = 5.916079783; //root of price homes
	public final static double PRICE_SCALING = 15.0;
	public final static double PRICE_MAX_FOR_AGENTS = PRICE_MIN+PRICE_SCALING;

	// debugs
	public static boolean DEBUG_ALL = false;
	public static boolean DEBUG_WorkerAgent = true;
	public static boolean DEBUG_GlobalClock = true;
	public static boolean DEBUG_DumbCharging = true;
	public static boolean DEBUG_Agent = true;
	public static boolean DEBUG_AgentSupervisoryManager = false;


	// input for Strat and output
	public static int CHARGING_STRATEGY_FOR_AGENTS = 3;

	public static int EXCEL_OUTPUT = 1;

	public static boolean StartSOCsAtOne = false;

	public static boolean LOW_MEMORY_MODE = true;


}
