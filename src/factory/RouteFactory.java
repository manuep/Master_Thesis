package factory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;



import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Utils.GoogleDirection;
import Utils.GoogleDirectionRepo;
import Utils.Variables;


import model.navigation.*;
import model.powerutilities.ChargingStation;

public class RouteFactory {
	GoogleDirection googleDirection = new GoogleDirection();
	private boolean debug = false;

	public Route createRouteFromTo(String from, String to) {
		if(debug) {System.out.println("Creating a Route..");}
		ArrayList<Direction> directions = new ArrayList<>();
		
		JSONObject routes=null;
		while(routes==null) {
		routes = googleDirection.getJSONDirection(from, to);
		}


		//JSONObject bounds = (JSONObject) routes.get("bounds"); //SW and NE lng and lat
		//TODO -> Do we need the bounds of the Route?

		JSONArray legsArray = (JSONArray) routes.get("legs"); 
		JSONObject legs = (JSONObject) legsArray.get(0); // Summary, and steps

		// DIRECTION OBJECTS
		JSONArray stepsArray = (JSONArray) legs.get("steps"); // Direction Objects
		Iterator iter = stepsArray.iterator();
		while (iter.hasNext()) {
			JSONObject item = (JSONObject) iter.next();
//			JSONObject duration = (JSONObject) item.get("duration");
//			JSONObject distance = (JSONObject) item.get("distance");
			
			long duration=0;
			long distance=0;
			if(item.get("duration") instanceof Long) {
				 duration =(long) item.get("duration");
			}
			else if(item.get("duration") instanceof Double) {
				double duration_d= (double) item.get("duration");
				 duration =new Double(duration_d).longValue();
			}
			if(item.get("distance") instanceof Long) {
				 distance = (long) item.get("distance");
			}
			else if(item.get("distance") instanceof Double) {
				double distance_d = (double) item.get("distance");
				distance = new Double(distance_d).longValue();
			}
			System.out.println("distance"+distance);
			System.out.println("duration"+duration);
			JSONObject geometry = (JSONObject) item.get("geometry");
			JSONArray coordinates_array =(JSONArray) geometry.get("coordinates");
			
			int size_array=coordinates_array.size();
			JSONArray start_loc=(JSONArray) coordinates_array.get(0);
			JSONArray end_loc= (JSONArray) coordinates_array.get(size_array-1);
			Location source= new Location((double)start_loc.get(1),(double) start_loc.get(0));
			Location target=new Location((double)end_loc.get(1),(double) end_loc.get(0));
			
			Direction direction = new Direction (source, target, distance,duration);
					
					
//			JSONObject startLocation = (JSONObject) item.get("start_location");
//			Location source = new Location((double)startLocation.get("lat"), (double) startLocation.get("lng"));
//
//			JSONObject endLocation = (JSONObject) item.get("end_location");
//			Location target = new Location((double)endLocation.get("lat"), (double) endLocation.get("lng"));
//
//			Direction direction = new Direction(source, target, (long)distance.get("value"), (long)duration.get("value"));
			directions.add(direction);

		}

		// ROUTE OBJECT
//		JSONObject totalDuration = (JSONObject) legs.get("duration");
//		JSONObject totalDistance = (JSONObject) legs.get("distance");
		
		long totalDuration =0;
		long totalDistance=0;

		if(legs.get("duration") instanceof Long) {
		 totalDuration = (long) legs.get("duration");}
		else if (legs.get("duration") instanceof Double) {
			double totalDuration_d=(double) legs.get("duration");
			 totalDuration= new Double(totalDuration_d).longValue();
		}
		if(legs.get("distance") instanceof Long) {
		 totalDistance = (long) legs.get("distance");}
		else if (legs.get("distance") instanceof Double) {
			double totalDistance_d=(double) legs.get("distance");
			totalDistance=new Double(totalDistance_d).longValue();
		}
		
//		Route route = new Route(directions, (long)totalDuration.get("value"), (long) totalDistance.get("value"));
		Route route = new Route(directions, totalDuration, totalDistance);
		if(debug) {System.out.println("   > Completed a route with "+directions.size()+" sub-directions");}
		
		return route;
	}

	public Route createRouteFromAndTo(double sourceLat, double  sourceLng, double targetLat, double  targetLng) {
		String sourceString = sourceLat+","+sourceLng;
		String targetString = targetLat+","+targetLng;

		return createRouteFromTo(sourceString, targetString);
	}

	public Route createRouteFromAndTo(Location source, Location target) {
		String from = source.getLatitude()+","+source.getLongitude();
		String to = target.getLatitude()+","+source.getLongitude();
		return createRouteFromTo(from, to);
	}

	/**
	 * Creates a two-way route in Trondheim (from home, to office, and back again).
	 * @return returns two sized List, with .get(0) being the original and .get(1) being the reverse.
	 */
	public List<Route> createTwoWayRoute(Route route) {
		ArrayList<Direction> reverseDirections = new ArrayList<>();
		for(int i = route.getDirections().size()-1; i>-1;i--) {
			//Switching target and source from original Route route.
			Direction direction = new Direction(
					route.getDirections().get(i).getTarget(),
					route.getDirections().get(i).getSource(), route.getDirections().get(i).getDistance(),
					route.getDirections().get(i).getTime());
			reverseDirections.add(direction);
		}
		Route reverse = new Route(reverseDirections, route.getTime(), route.getDistance());
		List<Route> routes = new ArrayList<>();
		routes.add(route);
		routes.add(reverse);

		return routes;
	}
}
