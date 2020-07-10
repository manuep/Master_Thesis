package Utils.Statistics;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.Box.Filler;

import model.schdeule.GlobalClock;

import Utils.Variables;

import jxl.write.WritableSheet;

public class StringDateComparator implements Comparator<String> {

	private static boolean debug = false;
	/**
	 * String type: d-hh:mm
	 * 
	 */
	@Override
	public int compare(String o1, String o2) {
		if(debug){System.out.println("Comparing datetimes: "+o1+" and "+o2);}
		int[] s1 = stringsToInts(o1); // -1
		int[] s2 = stringsToInts(o2); // 1

		if(s1[0] > s2[0]) {
			return 1;
		}
		else if(s1[0] < s2[0]) {
			return -1;
		}
		else {
			if(s1[1] > s2[1]) {
				return 1;
			}
			else if(s1[1] < s2[1]) {
				return -1;
			}
			else {
				if(s1[2] > s2[2]) {
					return 1;
				}
				else if(s1[2] < s2[2]) {
					return -1;
				}
				else {
					return 0;
				}
			}
		}


	}

	private static int[] stringsToInts(String s) {
		String[] dayAndValues = s.split("-");
		int day = Integer.parseInt(dayAndValues[0]);
		String[] hourAndMins = dayAndValues[1].split(":");
		int hour = Integer.parseInt(hourAndMins[0]);
		int mins = Integer.parseInt(hourAndMins[1]);
		int[] out = new int[3];
		out[0] = day;
		out[1] = hour;
		out[2] = mins;

		return out;
	}

	public static SortedMap<String, Double> sortHashMapNew(ConcurrentHashMap<String, Double> map) {
		ArrayList<String> datetimes = new ArrayList<String>(map.keySet());

		SortedMap<String, Double> sortedMap = new TreeMap<String, Double>();

		for(String datetime : datetimes) {
			sortedMap.put(datetime, map.get(datetime));
			//previousDatetime = datetime;
			if(debug){System.out.println("Sorted datetimes: "+datetime);} // fail to do this in natural order
		}

		SortedMap<String, Double> filledSortedMap = filloutGap(sortedMap);
		//for(Map.Entry<String, Double> entry : filledSortedMap.entrySet()) {
		//		System.out.println("Sorted datetimes after filling: "+entry.getKey().toString());
		//}

		ArrayList<String> alldatetimes = new ArrayList<String>(filledSortedMap.keySet());

		Collections.sort(alldatetimes,new StringDateComparator());

		SortedMap<String, Double> newSortedMap = new TreeMap<String, Double>();

		for(String datetime : alldatetimes) {
			newSortedMap.put(datetime, map.get(datetime));
			//previousDatetime = datetime;
			//if(debug){System.out.println("Sorted datetimes: "+datetime);} // fail to do this in natural order
		}

		return newSortedMap;

	}

	public static SortedMap<String, Double> sortHashMap(ConcurrentHashMap<String, Double> map) {
		ArrayList<String> datetimes = new ArrayList<String>(map.keySet());

		SortedMap<String, Double> sortedMap = new TreeMap<String, Double>(new StringDateComparator());
		//Collections.sort(datetimes);  //this one gives alphabetic, not by the compare above!!
		Collections.sort(datetimes,new StringDateComparator());

		for(String datetime : datetimes) {
			sortedMap.put(datetime, map.get(datetime));
			if(debug){System.out.println("Sorted datetimes: "+datetime);} // fail to do this in natural order
		}

		return  filloutGap(sortedMap);

	}

	public static SortedMap<String, ArrayList<Double>> sortHashMapDoubleList(ConcurrentHashMap<String, ArrayList<Double>> map) {
		ArrayList<String> datetimes = new ArrayList<String>(map.keySet());
		//ConcurrentHashMap<String, Double> sortedMap = new ConcurrentHashMap<>();

		SortedMap<String, ArrayList<Double>> sortedMap = new TreeMap<String, ArrayList<Double>>(new StringDateComparator());
		Collections.sort(datetimes, new StringDateComparator());

		String previousDatetime;
		for(String datetime : datetimes) {
			sortedMap.put(datetime, map.get(datetime));
			previousDatetime = datetime;
		}
		return sortedMap;
	}

	public static SortedMap<String, Double> sortHashMapInt(ConcurrentHashMap<String, Integer> map) {
		ConcurrentHashMap<String, Double> doubleToIntegerMap = new ConcurrentHashMap<String, Double>();
		for(Map.Entry<String, Integer> entry : map.entrySet()) {
			doubleToIntegerMap.put(entry.getKey(), Double.parseDouble(entry.getValue().toString()));
		}

		return sortHashMap(doubleToIntegerMap);
	}

	private static SortedMap<String, Double> filloutGap(SortedMap<String, Double> sortedMap) {
		int numberOfDays = GlobalClock.getInstance().getSimulationDays();
		int[] mockTime = new int[3]; //hour,min,day
		mockTime[0] = 0;
		mockTime[1] = 0;
		mockTime[2] = 1;

		while(mockTime[2] < numberOfDays+1) {
			if(debug){if(sortedMap.get(getTimeToString(mockTime)) != null){System.out.println("Time: "+getTimeToString(mockTime)+" and value "+sortedMap.get(getTimeToString(mockTime)));}}
			// fail to do this in natural order
			if(sortedMap.get(getTimeToString(mockTime)) == null) {
				if(sortedMap.get(getTimeToString(oneMinBack(mockTime))) == null) {
					sortedMap.put(getTimeToString(mockTime), 0.0);
				}
				else {
					double previousTimeValue = sortedMap.get(getTimeToString(oneMinBack(mockTime)));
					sortedMap.put(getTimeToString(mockTime), previousTimeValue);
				}
			}
			if(mockTime[1] >= 59) {
				mockTime[1] = 0;
				mockTime[0]++;
				if(mockTime[0] == 24) {
					mockTime[0] = 0;
					mockTime[2]++;
				}
			}
			else {
				mockTime[1]++;
			}

		}
		return sortedMap;
	}

	//doesnt work...
	public static SortedMap<Integer, SortedMap<String,Double>> sortHashMapMap(ConcurrentHashMap<Integer, ConcurrentHashMap<String,Double>> map){
		ArrayList<Integer> stationIDs = new ArrayList<Integer>(map.keySet());
		Collections.sort(stationIDs);

		SortedMap<Integer, SortedMap<String,Double>> sortedMap = new TreeMap<Integer, SortedMap<String, Double>>();

		for (Integer stationID : stationIDs){
			//Map.Entry<String, Double> entry = map.get(stationID).entrySet();
			ConcurrentHashMap<String,Double> map2 = map.get(stationID); //new ConcurrentHashMap<String,Double>();
			//if(true){System.out.println("map2.keySet().size: "+map2.keySet().size()+" map2 size: "+map2.size());} //
			//map2 = map.get(stationID);
			if(debug){System.out.println("Size keySet: "+map.get(stationID).keySet().size()+" for station: "+stationID);} //
			if(debug){System.out.println("Size entrySet: "+map.get(stationID).entrySet().size()+" for station: "+stationID);} //
			//Set<String> set = map.get(stationID).keySet();
			//List<String> datetimes = new ArrayList(set);
			ArrayList<String> datetimes = new ArrayList<String>(map2.keySet());
			SortedMap<String, Double> sortedMap2 = new TreeMap<String, Double>(new StringDateComparator());
			Collections.sort(datetimes,new StringDateComparator());

			for(String datetime : datetimes) {
				sortedMap2.put(datetime, map.get(stationID).get(datetime));
				if(debug){System.out.println("Sorted datetimes: "+datetime+" and value: "+map.get(stationID).get(datetime));} //
			}

			if(debug){System.out.println("Station ID: "+stationID+" is to be filled out");} //
			sortedMap2 = filloutGap(sortedMap2);

			// filloutGap(sortedMap2);
			sortedMap.put(stationID,sortedMap2);
		}

		return sortedMap;

	}

	public static SortedMap<Integer, SortedMap<String,Double>> sortHashMapMap(ConcurrentHashMap<String, ConcurrentHashMap<Integer,Double>> map, Set<Integer> IDs){
		ArrayList<Integer> stationIDs = new ArrayList<Integer>(IDs);
		Collections.sort(stationIDs);

		ArrayList<String> dateTimes = new ArrayList<String>(map.keySet());
		Collections.sort(dateTimes);

		SortedMap<Integer, SortedMap<String,Double>> sortedMap = new TreeMap<Integer, SortedMap<String, Double>>();

		for (Integer stationID : stationIDs){
			SortedMap<String, Double> sortedMap2 = new TreeMap<String, Double>(new StringDateComparator());

			for(String datetime : dateTimes) {
				if(map.get(datetime).containsKey(stationID))
				sortedMap2.put(datetime, map.get(datetime).get(stationID));
				if(debug){System.out.println("Sorted datetimes: "+datetime+" and value: "+map.get(datetime).get(stationID));} //
			}

			if(debug){System.out.println("Station ID: "+stationID+" is to be filled out");} //
			//sortedMap2 = filloutGap(sortedMap2);
			sortedMap.put(stationID,sortedMap2);
		}
		return sortedMap;

	}

	public static String getTimeToString(int[] time) {
		return time[2]+"-"+String.format("%02d", time[0])+":"+String.format("%02d", time[1]);
	}

	public static int[] oneMinBack(int[] time) {
		int[] back = new int[3];
		int min = time[1] - 1;
		int hour = time[0];
		int day = time[2];
		if(min < 0) {
			min = 59;
			hour = time[0] - 1;
			if(hour < 0) {
				hour = 23;
				day -= 1;
			}
		}
		back[0] = hour;
		back[1] = min;
		back[2] = day;
		return back;
	}

}

