package Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import java.util.*;

import Environment.City;
import Utils.Variables;
import factory.RouteFactory;
import model.navigation.Location;
import model.navigation.Route;
import model.navigation.Direction;
import model.powerutilities.ChargingStation;
import model.powerutilities.ChargingStationMap;
import model.schdeule.GlobalClock;
import model.agent.Agent;

public class Search {
	
	/**
	 * Searches a TreeMap with distance values @see Utils.Distance as key, returning
	 * the charging station with the closest distance to the given value
	 * 
	 * @param map
	 * @param value
	 * @return
	 */
	public static ChargingStation searchOnClosestDistance(double value) {
		TreeMap<Double, ChargingStation> map = ChargingStationMap.getStationDistances();
		Map.Entry<Double, ChargingStation> high = map.ceilingEntry(value);
		Map.Entry<Double, ChargingStation> low = map.floorEntry(value);

		double highSum = Double.MIN_VALUE;
		double lowSum = Double.MAX_VALUE;
		
		if(high != null) {
			highSum = high.getKey()-value;
		}
		if(low != null) {
			lowSum = value - low.getKey();
		}
		
		if(highSum < lowSum) {
			return high.getValue();
		}
		return low.getValue();
		
	}
	
	/**
	 * Finds the closest charging station from a given location, and allows the search to be limited
	 * to a max range.
	 * 
	 * @param maxRange - representing meters. If maxRange < 0, maxRange is ignored.
	 * @param location - representing query location to chargingstation map
	 * @return null if no satisfiable charging station is found, or a ChargingStation object.
	 */
	public static ChargingStation findClosestChargingStation(int maxRange, Location location) {
		double locationDistance = Utils.Distance.getDistanceFromReferencePoint(location);
		ChargingStation closestChargingStation = searchOnClosestDistance(locationDistance);
		double clostestChargingStationDistance = Utils.Distance.getDistanceFromReferencePoint(closestChargingStation);
				
		if(maxRange < 0) {
			return closestChargingStation;
		}
		else {
			//System.out.println("Searching for closest charging station:");
			double range = Math.abs(locationDistance-clostestChargingStationDistance);
			//System.out.println("  > The closest charging station is "+range*1000+" meters ("+range+" km) away");
			if(range*1000 > maxRange) {
				//System.err.println("  > Found a charging station that was out of range: "+range+" km");
				return null;
			}
			return closestChargingStation;
		}
	}
	
	/**
	 * Finds all charging stations within a given max search range within their given location.
	 * 
	 * @param maxRange - representing meters. If maxRange < 0, maxRange is ignored.
	 * @param location - representing query location to charging station map
	 * @return null if no satisfiable charging station is found, or a ChargingStation object.
	 */
	public static SortedMap<Double, ChargingStation> findClosestChargingStations(int maxRange, Location location) {
		double locationDistance = Utils.Distance.getDistanceFromReferencePoint(location); // in km
		double maxRangeKM = maxRange/1000.0;
		
		TreeMap<Double, ChargingStation> map = ChargingStationMap.getStationDistances();
		SortedMap<Double, ChargingStation> correntRangeMap = map.subMap(locationDistance-maxRangeKM, locationDistance+maxRangeKM);
		return correntRangeMap;
	}
	
	public static SortedMap<Double,ChargingStation> findReachableStations (double batteryrange, Location location){
		double locationDistance=Utils.Distance.getDistanceFromReferencePoint(location)/1000;

		TreeMap<Double,ChargingStation> map = ChargingStationMap.getStationDistances();
		for (Map.Entry<Double, ChargingStation> entry:map.entrySet()) {
			if (entry.getValue().getOpenChargingPoints()<=0) {
				map.remove(entry.getKey());
			}
		}
		SortedMap <Double,ChargingStation> reachablestations=map.subMap(locationDistance-batteryrange, locationDistance+batteryrange);
		return reachablestations;
	}

	public static TreeMap<Double,ChargingStation>  pricelist(List<ChargingStation> cstations){ 
		System.out.println(105);
		HashMap<Double,ChargingStation> unsortedmap= new HashMap<Double,ChargingStation>();
		for (int j=1; j<cstations.size();j++) {
			if(cstations.get(j).getMaxChargingPoints()>0) {
//			System.out.println("En el bucle");
//			entrynew.= (cstations.get(j).getPriceCurrent(), cstations.get(j));//(cstations.get(j).getPriceCurrent(), cstations.get(j));
			unsortedmap.put(cstations.get(j).getPriceCurrent(), cstations.get(j));//(cstations.get(j).getPriceCurrent(), cstations.get(j));
//			System.out.println(unsortedmap);
			}
		}
		TreeMap<Double,ChargingStation> sortedmap1=new TreeMap<Double,ChargingStation>();
		sortedmap1.putAll(unsortedmap);
		return sortedmap1;
	}
	
	public static TreeMap<Double,ChargingStation>  preflist(List<ChargingStation> cstations, double[] pref, Location location, double range, Agent ag){
		List<Double> latitudes = new ArrayList<>();
		List<Double> longitudes=new ArrayList<>();
		latitudes.add(11.50658995);
		latitudes.add(11.50858082);
		latitudes.add(11.51049973);
		latitudes.add(11.51510511);
		latitudes.add(11.51494047);
		latitudes.add(11.5052303);
		latitudes.add(11.51084003);
		latitudes.add(11.5119219);
		latitudes.add(11.51906763);
		latitudes.add(11.52489106);
		latitudes.add(11.53022031);
		latitudes.add(11.54236136);
		latitudes.add(11.54280213);
		latitudes.add(11.54413778);
		latitudes.add(11.54724984);
		longitudes.add(64.02307846);
		longitudes.add(64.01985286);
		longitudes.add(64.02053583);
		longitudes.add(64.02064091);
		longitudes.add(64.01902371);
		longitudes.add(64.01730342);
		longitudes.add(64.01786516);
		longitudes.add(64.01716299);
		longitudes.add(64.01638765);
		longitudes.add(64.01860535);
		longitudes.add(64.02026119);
		longitudes.add(64.02194034);
		longitudes.add(64.02929345);
		longitudes.add(64.02781948);
		longitudes.add(64.03445177);
		
		System.out.println("range"+range);
		System.out.println("Agent location is"+location.getLatitude()+","+location.getLongitude());
		HashMap<Double,ChargingStation> unsortedmap= new HashMap<Double,ChargingStation>();
		
		Location destination;
		if (ag.isAtWork()) {
			location=ag.getWorkLocation();
			destination = ag.getHomeLocation();
		}
		else {
			location=ag.getHomeLocation();
			destination=ag.getWorkLocation();
		}
		System.out.println("destination is"+destination.getLatitude()+";"+destination.getLongitude());
		System.out.println("Agent location is"+location.getLatitude()+","+location.getLongitude());

		double base_dist= Utils.Distance.getDistanceBetweenPoints(location.getLatitude(), location.getLongitude(),destination.getLatitude(),destination.getLongitude());
		System.out.println("Base dist is"+base_dist);
		for (int j=0; j<cstations.size();j++) {
			if(cstations.get(j).getMaxChargingPoints()>0) {
				double lat=latitudes.get(cstations.get(j).getID()-1);
				double longi=longitudes.get(cstations.get(j).getID()-1);
			double locationDistance1=Utils.Distance.getDistanceBetweenPoints(location.getLatitude(),location.getLongitude(),longi,lat);
			double locationDistance2=Utils.Distance.getDistanceBetweenPoints(destination.getLatitude(),destination.getLongitude(),longi,lat);
			double total_dist=locationDistance1+locationDistance2;
			double dist_Max= base_dist/(pref[0]*1.5);
			double probdist=1-(total_dist-base_dist)/(dist_Max-base_dist);
			if (probdist>1) {
				probdist=1;
			}
			else if(probdist<0) {
				probdist=0;
			}
			double price= cstations.get(j).getPriceConsumer();
			double SOC=ag.getCar().getCurrentEnergy()/ag.getCar().getMaxEnergy();
			double SOCmin=ag.getMinimumEnergyPrefernce()/ag.getCar().getMaxEnergy();
			double probSOC=1-(SOC-SOCmin)/(0.7-SOCmin);
			System.out.println("Initial SOC is"+SOC+"and the min SOC is"+SOCmin+"therefore the prob is"+probSOC);
			if (probSOC>1) {
				probSOC=1;
			}
			else if (probSOC<0) {
				probSOC=0;
			}
//			System.out.println("price"+price);
//			double prob=((2*base_time-total_time)/base_time);
			double prob=(probdist*pref[0]+((10-price)/10)*pref[1])*probSOC;
//			if(prob<0) {
//				double lat=latitudes.get(cstations.get(j).getID()-1);
//				double longi=longitudes.get(cstations.get(j).getID()-1);
//				System.out.println("prob was <0");
//				System.out.println("lat"+lat+"long"+longi);
//				locationDistance=Utils.Distance.getDistanceBetweenPoints(longi,lat,location.getLatitude(),location.getLongitude());
//				System.out.println("new distance is"+locationDistance);
//				prob=(((range-locationDistance)/range)*pref[0]+((10-price)/10)*pref[1])*probSOC;
//				System.out.println("new prob is"+prob);
//			}
			
//			System.out.println("prob"+prob);
			unsortedmap.put(prob, cstations.get(j));//(cstations.get(j).getPriceCurrent(), cstations.get(j));
//			System.out.println(unsortedmap);
			System.out.println("Location of station"+ cstations.get(j).getID()+"is"+cstations.get(j).getLocation().getLatitude()+","+cstations.get(j).getLocation().getLongitude()+"the distance is"+total_dist+"and the base dist is"+base_dist+"therefore the prob is"+probdist);
			System.out.println("Price is"+price);
			System.out.println("The total prob is"+prob);
		}
		}
		TreeMap<Double,ChargingStation> sortedmap1=new TreeMap<Double,ChargingStation>();
		sortedmap1.putAll(unsortedmap);
//		System.out.println(sortedmap1);
		for (Map.Entry<Double,ChargingStation> mp:sortedmap1.entrySet()) {
		System.out.println(mp.getValue().getID()+" which has prob "+mp.getKey());
		}
		return sortedmap1;
	}
	
	public static TreeMap<Double,ChargingStation>  closelist (List<ChargingStation> cstations, double[] pref, Location location, double range, Agent ag){
		List<Double> latitudes = new ArrayList<>();
		List<Double> longitudes=new ArrayList<>();
		latitudes.add(11.50658995);
		latitudes.add(11.50858082);
		latitudes.add(11.51049973);
		latitudes.add(11.51510511);
		latitudes.add(11.51494047);
		latitudes.add(11.5052303);
		latitudes.add(11.51084003);
		latitudes.add(11.5119219);
		latitudes.add(11.51906763);
		latitudes.add(11.52489106);
		latitudes.add(11.53022031);
		latitudes.add(11.54236136);
		latitudes.add(11.54280213);
		latitudes.add(11.54413778);
		latitudes.add(11.54724984);
		longitudes.add(64.02307846);
		longitudes.add(64.01985286);
		longitudes.add(64.02053583);
		longitudes.add(64.02064091);
		longitudes.add(64.01902371);
		longitudes.add(64.01730342);
		longitudes.add(64.01786516);
		longitudes.add(64.01716299);
		longitudes.add(64.01638765);
		longitudes.add(64.01860535);
		longitudes.add(64.02026119);
		longitudes.add(64.02194034);
		longitudes.add(64.02929345);
		longitudes.add(64.02781948);
		longitudes.add(64.03445177);
		

		System.out.println("range"+range);
		System.out.println("Agent location is"+location.getLatitude()+","+location.getLongitude());
		HashMap<Double,ChargingStation> unsortedmap= new HashMap<Double,ChargingStation>();
		double base_time= ag.getRouteFactory().createRouteFromAndTo(ag.getHomeLocation(),location).getTime();
		for (int j=0; j<cstations.size();j++) {
			if(cstations.get(j).getMaxChargingPoints()>0) {
//			System.out.println(pref[0]+","+pref[1]);
//			double time_toCS= ag.getRouteFactory().createRouteFromAndTo(ag.getHomeLocation(),cstations.get(j).getLocation()).getTime();
//			double time_fromCS=ag.getRouteFactory().createRouteFromAndTo(cstations.get(j).getLocation(), location).getTime();
//			double total_time= time_toCS+time_fromCS;
			double locationDistance=Utils.Distance.getDistanceBetweenPoints(location.getLongitude(), location.getLatitude(), cstations.get(j).getLocation().getLatitude(), cstations.get(j).getLocation().getLongitude());
//			System.out.println("price"+price);
//			double prob=((2*base_time-total_time)/base_time);
			double prob=((range-locationDistance)/range)*pref[0];//+((10-price)/10)*pref[1];
			if(locationDistance>range) {
				double lat=latitudes.get(cstations.get(j).getID()-1);
				double longi=longitudes.get(cstations.get(j).getID()-1);
				System.out.println("prob was <0");
				System.out.println("lat"+lat+"long"+longi);
				locationDistance=Utils.Distance.getDistanceBetweenPoints(longi,lat,location.getLatitude(),location.getLongitude());
				System.out.println("new distance is"+locationDistance);
			}
			
//			System.out.println("prob"+prob);
			unsortedmap.put(locationDistance, cstations.get(j));//(cstations.get(j).getPriceCurrent(), cstations.get(j));
//			System.out.println(unsortedmap);
			System.out.println("Location of station is"+cstations.get(j).getLocation().getLatitude()+","+cstations.get(j).getLocation().getLongitude()+"the distance is"+locationDistance+"therefore the prob is"+prob);
		}
		}
		TreeMap<Double,ChargingStation> sortedmap1=new TreeMap<Double,ChargingStation>();
		sortedmap1.putAll(unsortedmap);
//		System.out.println(sortedmap1);
		for (Map.Entry<Double,ChargingStation> mp:sortedmap1.entrySet()) {
		System.out.println(mp.getValue().getID()+" which has distance "+mp.getKey());
		}
		return sortedmap1;
	}
	
//	public static void pricelist2 (List<ChargingStation> cstations){ 
//		HashMap<Double,ChargingStation> unsortedmap= new HashMap<Double,ChargingStation>();
//		for (int j=1; j<unsortedmap.size();j++) {
//			unsortedmap.put(cstations.get(j).getPriceCurrent(), cstations.get(j));//(cstations.get(j).getPriceCurrent(), cstations.get(j));
//		}
//		System.out.println("The pricelist2 map has"+unsortedmap.size());
//		TreeMap<Double,ChargingStation> sortedmap=new TreeMap<Double,ChargingStation>();
//		sortedmap.putAll(unsortedmap);
//	}
	
	public static ChargingStation cheapeststation(List<ChargingStation> cstations){
		System.out.println("Linea 127");
		TreeMap<Double,ChargingStation> sortedmap2= pricelist(cstations);
		return sortedmap2.firstEntry().getValue();
	}
	
	public static ChargingStation preferredstation(List<ChargingStation> cstations, double[] pref, Location location, double range, Agent ag){
		TreeMap<Double,ChargingStation> sortedmap2= preflist(cstations,pref,location, range, ag);
		return sortedmap2.lastEntry().getValue();
//		return sortedmap2.lastEntry().getValue();
	}
	
	public static ChargingStation closeststation (List<ChargingStation> cstations, double[] pref, Location location, double range, Agent ag){
		TreeMap<Double,ChargingStation> sortedmap2= closelist(cstations,pref,location, range, ag);
		return sortedmap2.firstEntry().getValue();
//		return sortedmap2.lastEntry().getValue();
	}
	
	
}
