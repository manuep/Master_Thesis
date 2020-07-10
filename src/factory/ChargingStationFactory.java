package factory;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import model.navigation.Location;
import model.powerutilities.ChargingStation;
import model.powerutilities.ChargingStationMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Utils.Variables;


public class ChargingStationFactory {
	private int creationCount;
	
	public ChargingStationFactory() {
		creationCount = 0;
	}
	
	public ChargingStationMap createChargeStationMapFromJSON() {
		double minLat = Double.MAX_VALUE;
		double maxLat = Double.MIN_VALUE;
		double minLong = Double.MAX_VALUE;
		double maxLong = Double.MIN_VALUE;
		
		System.out.println("Factory creating ChargingStations..");
		List<ChargingStation> chargingStations = new ArrayList<>();
		JSONParser parser = new JSONParser();
		JSONArray array = null;
		try {
			//array = (JSONArray) parser.parse(new InputStreamReader(Variables.LOCAL_CHARGING_STATIONS_JSON_DIR));
			array = (JSONArray) parser.parse(new FileReader(Variables.LOCAL_CHARGING_STATIONS_JSON_DIR));
			System.out.println(array.size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(Object obj: array) {
			creationCount++;
			
			JSONObject jsonStation = (JSONObject) obj;
			
			String position = (String) jsonStation.get("latlong");
			System.out.println(position);


			// Henceforth, the code was modified by Manuel Pérez (manperbra@outlook.es) 
			String[] positionArray = position.split(",");
			double latitude = Double.parseDouble(positionArray[0]);
			double longitude = Double.parseDouble(positionArray[1]);
			String address = (String) jsonStation.get("Adress");
			System.out.println(address);
			long cpoints = (long) jsonStation.get("Charging_points");
			int ledigeplasser=(int) cpoints;
			System.out.println(ledigeplasser);
			String ladefart = ("fast");
			String eier = ("Kommune");
			int tidsbegrensing = 0;
			int postnr=7050;
			String poststed=("Steinkjer");
			boolean parkeringsavgift=true;
			Location location = new Location(address, longitude, latitude);
			
			if (minLat > latitude) {
				minLat = latitude;
			}
			if (maxLat < latitude) {
				maxLat = latitude;
			}
			
			if (minLong > longitude) {
				minLong = longitude;
			}
			if (maxLong < longitude) {
				maxLong = longitude;
			}
		
			ChargingStation chargingStation = new ChargingStation(
					Integer.parseInt(jsonStation.get("ID").toString()), 
					address,
					eier, 
					ledigeplasser, 
					ladefart,
					tidsbegrensing,
					postnr,
					poststed,
					(String) jsonStation.get("Name"), 
					parkeringsavgift,
					location,
					130,
					2.5,
					Integer.parseInt(jsonStation.get("connection").toString())
					); 
			//ChargingStation(int id, String address, String owner, int maxChargingPoints, String chargeSpeed,
			//                int timelimit, int postNo, String postalName, String accessType, boolean parkingFree, Location location)
			chargingStations.add(chargingStation);
			System.out.println("   > Created charging station (ID: "+chargingStation.getID()+") with "+chargingStation.getMaxChargingPoints()+" spots at" + chargingStation.getLocation().getLatitude()+"connected to"+chargingStation.getConnection()+"@"+chargingStation.getPriceCurrent());
			
		}


		ChargingStationMap chargingStationMap = new ChargingStationMap(chargingStations, maxLat, 
				minLat, maxLong, minLong);
		System.out.println("   > Complete: Created "+chargingStationMap.getChargingStations().size()+" charging stations.");
		Variables.stationscount=creationCount;
		try {
			model.schdeule.Coord_matlab.llamamiento2(true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		creationCount = 0;
		return chargingStationMap;
		
	}
}
