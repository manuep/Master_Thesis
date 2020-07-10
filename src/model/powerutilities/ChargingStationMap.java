package model.powerutilities;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
/**
 * A Class containing an list of charging stations, and min/max latitudes and longitudes
 * to indicate the size of the area.
 * 
 * @author davidae
 *
 */
public class ChargingStationMap {
	// CHARGING STATIONS
	private static List<ChargingStation> chargingStations;
	private static TreeMap<Double, ChargingStation> stationDistances;
	private static TreeMap<Double,ChargingStation> stationPrices;

	// COORDINATES - The maximum and minimum coordinates of the charging stations.
	private double maxLatitude, minLatitude, maxLongitude, minLongitude;

	// AVERAGE VALUES
	private double avgPrice, avgQueue, avgChargeTime, avgUseage;

	// MAX VALUES
	private double maxPrice, maxQueue, maxChargeTime, maxUseage;

	// TIMEKEEPING - Avoiding overuse of updateAverageInformation()
	private long lastUpdated;
	private final static int MINUPDATEINTERVAL = 20000; // 20 seconds



	public ChargingStationMap(List<ChargingStation> chargingStations, double maxLat, double minLat,
			double maxLong, double minLong) {
		this.chargingStations = chargingStations;
		this.stationDistances = constructBST(chargingStations);
//		System.out.println("The system has created"+chargingStations.size());

		this.maxLatitude = maxLat;
		this.minLatitude = minLat;
		this.maxLongitude = maxLong;
		this.minLongitude = minLong;

		updateAverageInformation();

	}

	public ChargingStationMap(List<ChargingStation> chargingStations) {
		this.chargingStations = chargingStations;
		this.stationDistances = constructBST(chargingStations);


		// Finding and setting min/max of longitude and latitude.
		this.maxLatitude = Double.MIN_VALUE;
		this.minLatitude = Double.MAX_VALUE;
		this.maxLongitude = Double.MIN_VALUE;
		this.minLongitude = Double.MAX_VALUE;
		for (ChargingStation chargingStation: chargingStations) {
			if (chargingStation.getLocation().getLatitude() > this.maxLatitude) {
				this.maxLatitude = chargingStation.getLocation().getLatitude();
			}
			if (chargingStation.getLocation().getLatitude() < this.minLatitude) {
				this.minLatitude = chargingStation.getLocation().getLatitude();
			}

			if (chargingStation.getLocation().getLongitude() > this.maxLongitude) {
				this.maxLongitude = chargingStation.getLocation().getLongitude();
			}
			if (chargingStation.getLocation().getLongitude() < this.minLongitude) {
				this.minLongitude = chargingStation.getLocation().getLongitude();
			}
		}


	}
	/**
	 * Creates a Binary Search Tree of all charging stations. The Key is the distance between the charging station's
	 * longitude and latitude, and our reference point (longitude = 0, latitude = 0).
	 * 
	 * @param chargingStations
	 * @return
	 */
	private TreeMap<Double, ChargingStation> constructBST(List<ChargingStation> chargingStations) {
		HashMap<Double, ChargingStation> distanceOrigoMap = new HashMap<Double, ChargingStation>();
		ValueComparator bvc =  new ValueComparator(distanceOrigoMap);
		TreeMap<Double, ChargingStation> sorted_map = new TreeMap<Double, ChargingStation>(bvc);


		for (ChargingStation station: chargingStations) {
			distanceOrigoMap.put(
					Utils.Distance.getDistanceFromReferencePoint(
							station.getLocation().getLatitude(), station.getLocation().getLongitude()), station);
		}
		sorted_map.putAll(distanceOrigoMap);

		return sorted_map;
	}
	


	public synchronized void updateAverageInformation() {
		if(System.currentTimeMillis() - lastUpdated > MINUPDATEINTERVAL) {
			int count = getChargingStations().size();

			//maxPrice, maxQueue, maxChargeTime, tempMaxUseage
			double tempMaxPrice = chargingStations.get(0).getPriceMax();
			double tempMaxUseage = chargingStations.get(0).getAvgUseage();
			double tempMaxChargeTime = chargingStations.get(0).getAvgChargeTime();

			//avgPrice, avgQueue, avgChargeTime, avgUseage
			double tempAvgPrice = 0;
			double tempAvgQueue = 0;
			double tempAvgUseage = 0;
			double tempAvgChargeTime = 0;


			for(ChargingStation chargingStation: chargingStations) {
				if(tempMaxChargeTime < chargingStation.getAvgChargeTime()) {
					tempMaxChargeTime = chargingStation.getAvgChargeTime();
				}
				if(tempMaxUseage < chargingStation.getAvgUseage()) {
					tempMaxUseage = chargingStation.getAvgUseage();
				}

				if(tempMaxPrice < chargingStation.getPriceMax()) {
					tempMaxPrice = chargingStation.getPriceMax();
				}
				tempAvgPrice =+ chargingStation.getPriceCurrent();
				tempAvgQueue =+ chargingStation.getQueue().size();
				tempAvgUseage =+ chargingStation.getAvgUseage();
				tempAvgChargeTime =+ chargingStation.getAvgChargeTime();
			}

			maxChargeTime = tempMaxChargeTime;
			maxUseage = tempMaxUseage;
			maxPrice = tempMaxPrice;

			avgPrice = tempAvgPrice/count;
			avgQueue = tempAvgQueue/count;
			avgUseage = tempAvgUseage/count;
			avgChargeTime = tempAvgChargeTime/count;

			lastUpdated = System.currentTimeMillis();
		}

	}

	public ChargingStation getRandomChargingStation() {
		int min = 0;
		int max = chargingStations.size()-1;
		int index = min+(int)(Math.random()*((max-min) + 1));
		return chargingStations.get(index);
	}

	/**	
	 * Returns a charging station that has Agents charging at it and
	 * where the priority of the charging station is less or equal to the priority.
	 * e.g. 0.6 => All stations with 0.6 to 0.0 priority.
	 * 
	 * @param priority (less or equal to a charging station's priority).
	 * @return
	 */
	public ChargingStation getRandomActiveChargingStation(double priority) {
		ChargingStation cs = getRandomChargingStation();
		while(cs.getChargers().size() != 0) {
			cs = getRandomChargingStation();
		}
		return cs;
	}


	//Some minor changes were introduced by Manuel Pérez (manperbra@outlook.es) in the getters and setters part of the code
	
	/////////////////////////
	// GETTERS AND SETTERS
	////////////////////////

	public static List<ChargingStation> getChargingStations() {
		return chargingStations;
	}

	public void setChargingStations(List<ChargingStation> chargingStations) {
		this.chargingStations = chargingStations;
	}

	public double getMaxLatitude() {
		return maxLatitude;
	}

	public void setMaxLatitude(double maxLatitude) {
		this.maxLatitude = maxLatitude;
	}

	public double getMinLatitude() {
		return minLatitude;
	}

	public void setMinLatitude(double minLatitude) {
		this.minLatitude = minLatitude;
	}

	public double getMinLongitude() {
		return minLongitude;
	}

	public void setMinLongitude(double minLongitude) {
		this.minLongitude = minLongitude;
	}

	/**
	 * A TreeMap (binary search tree) with a key on distance from the reference point (in km)
	 * and the charging station.
	 * 
	 * @return the tree map.
	 */
	public static TreeMap<Double, ChargingStation> getStationDistances() {
		return stationDistances;
	}

	public void setStationDistances(
			TreeMap<Double, ChargingStation> stationDistances) {
		this.stationDistances = stationDistances;
	}
	

	public void setMaxLongitude(double maxLongitude) {
		this.maxLongitude = maxLongitude;
	}

	public double getMaxLongitude() {
		return maxLongitude;
	}

	public double getAvgPrice() {
		updateAverageInformation();
		return avgPrice;
	}

	public double getAvgQueue() {
		updateAverageInformation();
		return avgQueue;
	}

	public double getAvgChargeTime() {
		updateAverageInformation();
		return avgChargeTime;
	}

	public double getAvgUseage() {
		updateAverageInformation();
		return avgUseage;
	}

	public double getMaxPrice() {
		updateAverageInformation();
		return maxPrice;
	}

	public double getMaxQueue() {
		updateAverageInformation();
		return maxQueue;
	}

	public double getMaxChargeTime() {
		updateAverageInformation();
		return maxChargeTime;
	}

	public double getMaxUseage() {
		updateAverageInformation();
		return maxUseage;
	}


	public long getLastUpdated() {
		return lastUpdated;
	}




}

class ValueComparator implements Comparator<Double> {

	Map<Double, ChargingStation> base;
	public ValueComparator(Map<Double, ChargingStation> base) {
		this.base = base;
	}

	// Note: this comparator imposes orderings that are inconsistent with equals.    
	public int compare(Double a, Double b) {
		if (a <= b) {
			return -1;
		} else {
			return 1;
		} // returning 0 would merge keys
	}


}
