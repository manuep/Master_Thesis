
package Environment;

import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Random;


import model.navigation.Location;

public class City {

	private static volatile City instance = null;
	
	// (Rectangle) City of Steinkjer
	private Location NW = new Location(64.0409961910912,11.4789062837012);
	private Location SE = new Location(64.0059194817354,11.559935973129);

	private Location NE = new Location(NW.getLatitude(), SE.getLongitude());
	private Location SW = new Location(SE.getLatitude(), NW.getLongitude());
	
	private Path2D area;
	private Random random;
	
	
	// WORKING LOCATIONS IN STEINKJER
	private ArrayList<ArrayList<Location>> workingAreas = new ArrayList<>();
	
	private Location downtownNW = new Location(64.0160317978745,11.4916177653125); 
	private Location downtownSE = new Location(64.0077517436938,11.5047588259937);
	
	
	private Location svenskbyenNW = new Location(64.0184796178268,11.5047588259937);
	private Location svenskbyenSE = new Location(64.0137728895331,11.5273959816196);

	
	private Location hakkdalenNW = new Location(64.0238157517865,11.5034159438803);
	private Location hakkdalenSE = new Location(64.0184796178268,11.5158855635047);

	
	private Location nordishaugenNW = new Location(64.0217990651409,11.4938945984043);
	private Location nordishaugenSE = new Location(64.0167146876825,11.5034159438803);

	//RESIDENCE AREAS IN STEINKJER
	
	private ArrayList<ArrayList<Location>> residenceAreas=new ArrayList<>();
	
	private Location zone1NW= new Location(64.0409961910912,11.4789062837012); // Point 1
	private Location zone1SE=new Location (64.0061124247694,11.5065714067763); // POint 4
	
	private Location zone2NW= new Location(64.0240899898303,11.5064986038208); // Point 5
	private Location zone2SE=new Location (64.0148524200343,11.5371486480698); // Point 8
	
	private Location zone3NW=new Location (64.0409961910912,11.5373785972959); // Point 9
	private Location zone3SE=new Location(64.0148998091779,11.559935973129); // Point 11
	
	

	public City() {
		random = new Random();
		
		area = new Path2D.Double();
		area.moveTo(NW.getLatitude(), NW.getLongitude());
		area.lineTo(NE.getLatitude(), NE.getLongitude());
		area.lineTo(SE.getLatitude(), SE.getLongitude());
		area.lineTo(SW.getLatitude(), SW.getLongitude());
		area.closePath();
		
		
		ArrayList<Location> downtown = new ArrayList<>();
		downtown.add(downtownNW);
		downtown.add(downtownSE);
		workingAreas.add(downtown);
		
		ArrayList<Location> svenskbyen = new ArrayList<>();
		svenskbyen.add(svenskbyenNW);
		svenskbyen.add(svenskbyenSE);
		workingAreas.add(svenskbyen);
		
		ArrayList<Location> hakkdalen = new ArrayList<>();
		hakkdalen.add(hakkdalenNW);
		hakkdalen.add(hakkdalenSE);
		workingAreas.add(hakkdalen);
		
		ArrayList<Location> nordishaugen = new ArrayList<>();
		nordishaugen.add(nordishaugenNW);
		nordishaugen.add(nordishaugenSE);
		workingAreas.add(nordishaugen);
		
		ArrayList<Location> zone1= new ArrayList<>();
		zone1.add(zone1NW);
		zone1.add(zone1SE);
		residenceAreas.add(zone1);
		
		ArrayList<Location> zone2= new ArrayList<>();
		zone2.add(zone2NW);
		zone2.add(zone2SE);
		residenceAreas.add(zone2);
	
		ArrayList<Location> zone3= new ArrayList<>();
		zone3.add(zone3NW);
		zone3.add(zone3SE);
		residenceAreas.add(zone3);

	}

	public static City getInstance() {
		if (instance == null) {
			synchronized (City .class){
				if (instance == null) {
					instance = new City();
				}
			}
		}
		return instance;
	}
	
	public boolean insideCity(double latitude, double longitude) {
		return area.contains(latitude, longitude);
	}
	
	/**
	 * The City location needs to be a rectangle.
	 * 
	 * @return
	 */
	public Location getRandomLocationInCity() {
		double randomLat = getRandomDouble(SW.getLatitude(), NW.getLatitude());
		double randomLong = getRandomDouble(NW.getLongitude(), NE.getLongitude());
		return new Location(randomLat, randomLong);
		
	}
	
	public Location getRandomWorkLocation() {
		ArrayList<Location> randomArea = workingAreas.get(random.nextInt(workingAreas.size()));
		
		Location NW = randomArea.get(0);
		Location NE = new Location(randomArea.get(0).getLatitude(), randomArea.get(1).getLongitude());
		Location SW = new Location(randomArea.get(1).getLatitude(), randomArea.get(0).getLongitude());

		double randomLat = getRandomDouble(SW.getLatitude(), NW.getLatitude());
		double randomLong = getRandomDouble(NW.getLongitude(), NE.getLongitude());
		return new Location(randomLat, randomLong);
	}
	
	public Location getRandomResidence() {
		ArrayList<Location> randomAreaR = residenceAreas.get(random.nextInt(residenceAreas.size()));
		
		Location NW = randomAreaR.get(0);
		Location NE = new Location(randomAreaR.get(0).getLatitude(), randomAreaR.get(1).getLongitude());
		Location SW = new Location(randomAreaR.get(1).getLatitude(), randomAreaR.get(0).getLongitude());

		double randomLat = getRandomDouble(SW.getLatitude(), NW.getLatitude());
		double randomLong = getRandomDouble(NW.getLongitude(), NE.getLongitude());
		return new Location(randomLat, randomLong);
	}
		
	
	public Path2D createAreaFromBounds(Location NW, Location SE) {
		Path2D area = new Path2D.Double();
		
		area.moveTo(NW.getLatitude(), NW.getLongitude()); // NW
		area.lineTo(NW.getLatitude(), SE.getLongitude()); // NE
		area.lineTo(SE.getLatitude(), SE.getLongitude()); // SE
		area.lineTo(SE.getLatitude(), NW.getLongitude()); // SW
		area.closePath();
		return area;
	}
	
	private double getRandomDouble(double min, double max) {
		return min+(max-min)*random.nextDouble();
	}


}
