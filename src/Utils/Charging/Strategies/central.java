//This charging strategy has been largely modified by Manuel P�rez (manperbra@outlook.es) to satisfy the Centrally-controlled charging strategy
// proposed in his master thesis (see Readme file)

package Utils.Charging.Strategies;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import Utils.Charging.ChargingStrategy;
import Utils.Search;
import Utils.Variables;
import model.agent.Agent;
import model.agent.WorkerAgent;
import model.navigation.Location;
import model.powerutilities.ChargingStation;
import model.powerutilities.ChargingStationMap;
import model.schdeule.GlobalClock;

import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.Math;

public class central extends ChargingStrategy {
	private boolean debug = (Variables.DEBUG_ALL || Variables.DEBUG_DumbCharging);
	private TreeMap<Double,ChargingStation> reachablestations;
    private double lowestDesiredBatteryLevel;
   
    


    public central(Agent agent, double lowestDesiredBatteryLevel) {
        super(agent);
        this.lowestDesiredBatteryLevel = lowestDesiredBatteryLevel;
    }
	
	@Override
	public ChargingStation getMostSuitableChargingStation() {
		ChargingStation station;
		station= getCheapestStation();
		return station;
		
	}
	
	public ChargingStation getCheapestStation() {
		ChargingStation station;
		List <ChargingStation> stations= new ArrayList<ChargingStation>();
		stations= ChargingStationMap.getChargingStations();
		List<ChargingStation> stations2=new ArrayList<ChargingStation>();
		double batteryrange = getAgent().getCar().getbatteryrange(); 
		for (int i=0; i<stations.size();i++) {
			if(stations.get(i).getID()<1) {
				stations.remove(i);
			}
			else {
			Location locst=stations.get(i).getLocation();
			Location locag=getAgent().getCurrentLocation();
			Double distance =Utils.Distance.getDistanceBetweenPoints(locst.getLatitude(),locst.getLongitude(),locag.getLatitude(),locag.getLongitude())/1000;
			if (distance<(batteryrange) &&  stations.get(i).getOpenChargingPoints()>0) {
				
				stations2.add(stations.get(i));
			}
			}
		}
			System.out.println("After removal, there are"+stations2.size());
			station=Search.cheapeststation(stations2);
			int beforecp= station.getOpenChargingPoints();
			station.setOpenChargingPoints(beforecp-1);
			BufferedWriter writer3;
			try {
				writer3= new BufferedWriter(new FileWriter("directory\\satisfaction.txt",true));
				writer3.newLine();
				writer3.append(String.format("%02d", GlobalClock.getTime()[2])+"/01/2020 "+String.format("%02d", GlobalClock.getTime()[0])+":"+String.format("%02d", GlobalClock.getTime()[1])+";");
				writer3.append(getAgent().getAgentId()+";"+station.getID()+";"+getAgent().getCar().getCurrentEnergy()+";"+getAgent().getCar().getMaxEnergy()+";"+getsatisfaction(station,getAgent())[0]+";"+getsatisfaction(station,getAgent())[1]+";"+getsatisfaction(station,getAgent())[2]+";"+getsatisfaction(station,getAgent())[3]+";"+getsatisfaction(station,getAgent())[4]+";"+getsatisfaction(station,getAgent())[5]);
				writer3.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return station;
		
	}
	
	
	@Override
	public ArrayList<ChargingStation> getMostSuitableChargingStations() {
		return null;
	}
	
	@Override
	public void chargeAt(ChargingStation chargingStation) {

	}
	
	@Override
	public void stopCharging() {
		setCharging(false);
		setTargetChargingStation(null);
		if(debug) {System.out.println("Agent ("+getAgent().getAgentId()+"): I'have a stopped charging.");}


	}
	
	@Override
	public void chargeAtHome() {
		if(needToCharge()) {
			if(debug) {System.out.println("Agent ("+getAgent().getAgentId()+"): I'have a need to charge at home. "+GlobalClock.getInstance().getTimeStamp()+"");}

			if(getHomeChargingStation().registedCharger(getAgent())) {
				setTargetChargingStation(getHomeChargingStation());
				setCharging(true);
				if(debug) {System.out.println("Agent ("+getAgent().getAgentId()+"): Registered with home charging staton. "+GlobalClock.getInstance().getTimeStamp()+"");}

			}
			else {
				System.err.println("Agent ("+getAgent().getAgentId()+"): I was not able to register with my own home charging station.");
			}

		}
	}
	
	@Override
	public void doChargingTicks() {
		boolean notC = false;
		double[] values= {getTargetChargingStation().getChargeRate(), getAgent().getCar().getChargeRate()};
		Arrays.sort(values);
		double value=values[0]/80;
		if(debug) {System.out.println("Charging rate by targetstation: "+value);}
		double startTime = System.currentTimeMillis();
		String virtStart = GlobalClock.getInstance().getTimeStamp();
		

		while(needToCharge() && !getAgent().isDoAction() && !Variables.needsmatlab) {
			boolean allowedtocharge=true; 
			if(allowedtocharge) {
				if(debug) {
					System.out.println("Agent ("+getAgent().getAgentId()+"): I'm inside the while loop for charging. Time is: "+GlobalClock.getInstance().getTimeStamp());
					System.out.println("      ("+getAgent().getAgentId()+"): needToCharge()? "+needToCharge()+", isDoAction()? "+getAgent().isDoAction());
					System.out.println("      ("+getAgent().getAgentId()+"): My current energy level is: "+getAgent().getCar().getCurrentEnergy()+" kw.");
				}
				double energyValue = getAgent().getCar().getCurrentEnergy();
				if(energyValue+value < getAgent().getCar().getMaxEnergy()) {
					if(getAgent().getag_counter()>=Variables.COUNTER) {
						getAgent().getCar().setCurrentEnergy(energyValue);
					
					}
					else if(getAgent().getag_counter()<Variables.COUNTER) {
						getAgent().getCar().setCurrentEnergy(energyValue+value);
						getAgent().setag_counter(Variables.COUNTER-1);

					}
				}
				else {
					getAgent().getCar().setCurrentEnergy(getAgent().getCar().getMaxEnergy());
				}
				
				//Can print energy states per vehicle from here
			}
			else {
				if(!notC) {
					System.out.println("Agent ("+getAgent().getAgentId()+"): I'm not allowed to charge.");
					System.out.println("     My charge speed/rate is: "+value+" kW");
				}
				notC = true;

			}
			
			try {
				getAgent().sleep(Variables.SIMULATION_SPEED); // sleeps, in the mean while, the need to charge or doing actions might change
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			}
		if(debug) {
			System.out.println("Agent ("+getAgent().getAgentId()+"): I stopped charging now!");
			System.out.println("      ("+getAgent().getAgentId()+"): I started charging at: "+virtStart);
			System.out.println("      ("+getAgent().getAgentId()+"): I stopped charging at: "+GlobalClock.getInstance().getTimeStamp());
			System.out.println("      ("+getAgent().getAgentId()+"): Stopped Charging. Ended with "+getAgent().getCar().getCurrentEnergy()+" kW");
		}

		if(getAgent().getCar().getCurrentEnergy()==getAgent().getCar().getMaxEnergy()) {
		getTargetChargingStation().unregisterCharger(getAgent());
		stopCharging();
		}

		if(debug) {System.err.println("Agent ("+getAgent().getAgentId()+"): Stopped Charging. Ended with "+getAgent().getCar().getCurrentEnergy()+" kW");}
		

	}
	
	
	@Override
	public void chargeAtWork(ChargingStation cst) {
		System.out.println("Has entered the charthework loop");
		ChargingStation cheapestChargingStation = cst;
		if(cheapestChargingStation != null && needToCharge()) {
			System.out.println("Has entered the first if");

			if(cheapestChargingStation.registedCharger(getAgent())) {
				setTargetChargingStation(cheapestChargingStation);
				setCharging(true);
				if(debug) {System.out.println("Agent ("+getAgent().getAgentId()+"): Charging at work, with charging station at: "+cheapestChargingStation.getAddress()+"with price"+cheapestChargingStation.getPriceCurrent());
				}
				}


			}

		else if(!needToCharge()) {
			System.err.println("Agent ("+getAgent().getAgentId()+"): I don't have a need to charge at work.");
		}

	}


	@Override
	public TreeMap<Double,ChargingStation> getReachableChargingStations(Location currentlocation) {;
		List <ChargingStation> stations= ChargingStationMap.getChargingStations();
		double batteryrange = getAgent().getCar().getbatteryrange(); 
		for (int i=0; i<15;i++) {
			Location locst=stations.get(i).getLocation();
			Location locag=currentlocation;
			Double distance =Utils.Distance.getDistanceBetweenPoints(locst.getLatitude(),locst.getLongitude(),locag.getLatitude(),locag.getLongitude())/1000;
			if (distance>(batteryrange) || stations.get(i).getOpenChargingPoints()<=0) {
				stations.remove(i);
			}
		}
		TreeMap<Double,ChargingStation> pricelisted = Search.pricelist(stations);
		return pricelisted;
	}
	
	@Override
	public void setPossibleWorkChargingStations(Location workLocation) {
	}

	@Override
	public void chargeAtWork() {
		
	}
	
	public double[] getsatisfaction(ChargingStation station, Agent ag) {
		double distance= Utils.Distance.getDistanceBetweenPoints(ag.getHomeLocation().getLatitude(),ag.getHomeLocation().getLongitude(),ag.getWorkLocation().getLatitude(),ag.getWorkLocation().getLongitude())/1000;
		double distance_total;
		double distance1=Utils.Distance.getDistanceBetweenPoints(ag.getHomeLocation().getLatitude(), ag.getHomeLocation().getLongitude(), station.getLocation().getLatitude(), station.getLocation().getLongitude())/1000;
		double distance2=Utils.Distance.getDistanceBetweenPoints(ag.getWorkLocation().getLatitude(), ag.getWorkLocation().getLongitude(), station.getLocation().getLatitude(), station.getLocation().getLongitude())/1000;
		distance_total=distance1+distance2;
		double[] pref= ag.getpreferences();
		double price=station.getPriceConsumer();
		double en_price=station.getPriceCurrent();
		double[] result = {distance,distance_total,price,en_price,pref[0],pref[1]}; 
		return result;
	}

	@Override
	public boolean chargingtime() {
		// TODO Auto-generated method stub
		return false;
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