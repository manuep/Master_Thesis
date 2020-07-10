package Utils;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A simple class for trying out the Google Direction API.
 * Checkout https://developers.google.com/maps/documentation/directions/ for more information.
 * Status: Working.
 * 
 * @author davidae
 * Modified by Manuel Pérez, specially to shift from the Google Direction API (requires an API KEY, and the subscription to the Google Cloud Services)
 * The direction requests were moved to the Open Source Routing Machine (OSRM) project. The JSON response structure differs. Visit: http://project-osrm.org/
 */
public class GoogleDirection {
	private String queryPath = "http://router.project-osrm.org/route/v1/driving/";

	//TODO
	public JSONObject getJSONDirection(double startLng, double startLat, double endLng, double endLat) {
//		String from = startLat+","+startLng;
//		String to = endLat+","+endLng;
		String from = startLng+","+startLat;
		String to = endLng+","+endLat;
		return getJSONDirection(from, to);
	}

	public JSONObject getJSONDirection(String from, String to) {
		System.out.println("Starting");
		String from_array[]=from.split(",");
		if (from_array[0].charAt(0)=='6') {
			from=from_array[1]+","+from_array[0];
		}
		else {
			from=from_array[0]+","+from_array[1];
		}
		String to_array[]= to.split(",");
		if (to_array[0].charAt(0)=='6') {
			to=to_array[1]+","+to_array[0];
		}
		else {
			to=to_array[0]+","+to_array[1];	
		}
		JSONParser parser = new JSONParser();
		JSONObject obj = new JSONObject();
		boolean usedLocal = false;
		String query = queryPath+from+";"+to+"?alternatives=false&steps=true&geometries=geojson&overview=simplified&annotations=false";

		System.out.println(query);
		
		
		String response = getStringResponseJSON(query);
		

		try {
			obj = (JSONObject) parser.parse(response);
		} catch (ParseException e) {
			System.out.println("da capo1");
			getJSONDirection(from,to);
			
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		
		JSONArray arrayRoutes = (JSONArray) obj.get("routes");
		JSONObject routes = null;
		if(arrayRoutes!=null) {
		
		try {
			routes = (JSONObject) arrayRoutes.get(0);

		}catch (IndexOutOfBoundsException e) {
			System.out.println("da capo2");
			getJSONDirection(from,to);
		}
		}
		else {
			getJSONDirection(from,to);
		}
		if(!usedLocal) {
		}
		return routes;
		
	}

	private String getStringResponseJSON(String query) {
		String response = "";
		try {
			URL url = new URL(query);

			InputStreamReader inputStream = new InputStreamReader(url.openStream());
			BufferedReader reader = new BufferedReader(inputStream);

			String line = reader.readLine();
			while (line != null) {
				response += line.trim();
				line = reader.readLine();
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
//			e.printStackTrace();
			getStringResponseJSON(query);
			System.out.println("ha vuelto a llmar");		}
		return response;

	}

}
