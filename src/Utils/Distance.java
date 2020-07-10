package Utils;

import model.navigation.Location;
import model.powerutilities.ChargingStation;

public class Distance {
	
	/**
	 * This uses the haversine formula to calculate the great-circle distance between two points - 
	 * that is, the shortest distance over the earth's surface giving an 'as-the-crow-flies' distance.
	 * distance between the points.<br>
	 * <br>
	 * The distance is in kilometers
	 * 
	 * @param srcLat
	 * @param srcLng
	 * @param targetLat
	 * @param targetLng
	 * @return distance in kilometers
	 */
	public static double getDistanceBetweenPoints(double srcLat, double srcLng, double targetLat, double targetLng) {
		double R = 6371; // Radius of Planet Earth in km
		double dLat = Math.toRadians(targetLat-srcLat);
		double dLng = Math.toRadians(targetLng-srcLng);
		
		double dSrcLat = Math.toRadians(srcLat);
		double dTargetLat = Math.toRadians(targetLat);
		
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
		        Math.sin(dLng/2) * Math.sin(dLng/2) * Math.cos(dSrcLat) * Math.cos(dTargetLat); 
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a)); 
		double d = R * c;
		
		return d;
	}
	
	public static double getDistanceFromReferencePoint(double latitude, double longitude) {
		return getDistanceBetweenPoints(
				Utils.Variables.LATITUDE_REFERNCE_POINT, Utils.Variables.LONGITUDE_REFERNCE_POINT, 
				latitude, longitude);
	}
	
	public static double getDistanceFromReferencePoint(ChargingStation chargingStation) {
		return getDistanceFromReferencePoint(chargingStation.getLocation());
	}
	
	/**
	 * Gets the distance from a location to a static reference point in Utils.Variables
	 * @see Utils.Variables
	 * 
	 * @return current distance from a global and static reference point (in KM).
	 */	
	public static double getDistanceFromReferencePoint(Location location) {
		double latitude = location.getLatitude();
		double longitude = location.getLongitude();
		return getDistanceFromReferencePoint(latitude, longitude);
		
	}


}
