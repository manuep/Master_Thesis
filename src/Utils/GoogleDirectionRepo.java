//This repository of routes is no longer used in the model developed by Manuel Pérez (manperbra@outlook.es)

package Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * A repo connector for reading and writing a local JSON file to store and use already pulled Google Direction. 
 * It is used when Google Directions API returns the following error: TOO_MANY_QURIES.
 * 
 * @author David
 *
 */
public class GoogleDirectionRepo {

	/**
	 * Stores a Google Directions queried route to use in the future.  
	 * 
	 * @param routes
	 */
	public static void storeRoutesJSONObjects(JSONObject routes) {
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		
		File f = new File(Variables.LOCAL_GDIR_JSON_ROUTES_DIR);
		
		try {
			
			if(!f.exists()) {
				obj = new JSONObject();
				obj.put("local_routes", new JSONArray());
			}
			else {
				obj = (JSONObject) parser.parse(new FileReader(Variables.LOCAL_GDIR_JSON_ROUTES_DIR));
			}
			JSONArray array = (JSONArray) obj.get("local_routes");
			array.add(routes);
			
			FileWriter file = new FileWriter(Variables.LOCAL_GDIR_JSON_ROUTES_DIR);
			file.write(obj.toJSONString());
			file.flush();
			file.close();


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
			
	}
	
	/**
	 * Returns a random route stored locally. To be used when Google Directions API is 
	 * overused (Query limit).
	 * 
	 * @return
	 */
	public static JSONObject getRandomRoutes() {
		JSONParser parser = new JSONParser();
		JSONObject obj = null;
		JSONArray array = null;
		int randomIndex = -1;
		try {
			obj = (JSONObject) parser.parse(new FileReader(Variables.LOCAL_GDIR_JSON_ROUTES_DIR));
			array = (JSONArray) obj.get("local_routes");
			int noOfRoutes = array.size();
			randomIndex = (int)(Math.random() * (noOfRoutes));
			
			
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
		
		return (JSONObject) array.get(randomIndex);

	}


}
