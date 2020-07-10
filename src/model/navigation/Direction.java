package model.navigation;

/**
 * Direction is a sub-path of a Route, having a source and target location (often represented
 * by intersections).
 * 
 * @see Route
 * @see Location
 * 
 * @author David
 *
 */
public class Direction {
	private Location source, target;
	private long distance, time; // meters, seconds
	
	public Direction(Location source, Location target, long distance, long time) {
		this.source = source;
		this.target = target;
		this.distance = distance;
		this.time = time;
	}
	
	public Direction(Location source, Location target) {
		this.source = source;
		this.target = target;
		
	}

	/**
	 * Returns the source or starting location of this Direction.
	 * 
	 * @see Location
	 */
	public Location getSource() {
		return source;
	}

	public void setSource(Location source) {
		this.source = source;
	}
	/**
	 * Returns the ending or target location of this Direction.
	 * 
	 * @see Location
	 */
	public Location getTarget() {
		return target;
	}

	public void setTarget(Location target) {
		this.target = target;
	}

	/**
	 * Returns the distance (meters) from source location to target location
	 * 
	 * @see Location
	 */
	public long getDistance() {
		return distance;
	}

	public void setDistance(long distance) {
		this.distance = distance;
	}
	/**
	 * Returns the time (seconds) from source location to target location
	 * 
	 * @see Location
	 */
	public long getTime() {
		return time;
	}
	
	

}
