package model.navigation;

import java.util.List;

/**
 * A Route, having two or more Directions, is a class representation
 * of the Google Direction information then traveling from A to B.
 * 
 * @author davidae
 *
 */
public class Route {
	private List<Direction> directions;
	private long time, distance;
	
	public Route(List<Direction> directions, long time, long distance) {
		this.directions = directions;
		this.time = time;
		this.distance = distance;
	}
	
	/**
	 * Returns all Directions in a route.
	 * @see Direction
	 * 
	 */
	public List<Direction> getDirections() {
		return directions;
	}
	public void setDirections(List<Direction> directions) {
		this.directions = directions;
	}
	/**
	 * Returns the total estimated time (seconds) for 
	 * driving this Route.
	 * 
	 */
	public long getTime() {
		return time;
	}
	public void setTime(long time) {
		this.time = time;
	}
	
	/**
	 * Returns the total estimated distance (meters) for
	 * driving this Route.
	 * 
	 */
	public long getDistance() {
		return distance;
	}
	public void setDistance(long distance) {
		this.distance = distance;
	}
	
	/**
	 * Return the latitude and longitude of the end/target location.
	 * 
	 * @return
	 */
	public String endLocationToString() {
		double lat = getDirections().get(getDirections().size()-1).getTarget().getLatitude();
		double longi = getDirections().get(getDirections().size()-1).getTarget().getLongitude();
		return lat+" "+longi;
	}
	
	/**
	 * Return the latitude and longitude of the end/target location.
	 * 
	 * @return
	 */
	public String startLocationToString() {
		double lat = getDirections().get(0).getTarget().getLatitude();
		double longi = getDirections().get(0).getTarget().getLongitude();
		return lat+" "+longi;
	}
	
	
	
	

}
