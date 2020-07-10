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
		System.out.println("Has entered the getpreferredstation loop");
		ChargingStation station;
		List <ChargingStation> stations= new ArrayList<ChargingStation>();
		stations= ChargingStationMap.getChargingStations();
		List<ChargingStation> stations2=new ArrayList<ChargingStation>();
//		if(stations.size()>15) {
//			for (int k=15;k<stations.size()+1;k++) {
//				stations.remove(k);
//			}
//				
//		}
		System.out.println(stations.size()+"have been imported");
		double batteryrange = getAgent().getCar().getbatteryrange(); 
		System.out.println("The battery range is"+ batteryrange);
		for (int i=0; i<stations.size();i++) {
			if(stations.get(i).getID()<1) {
				stations.remove(i);
			}
			else {
			System.out.println("Considering station  "+stations.get(i).getID());
			System.out.println("Open charging points for it are"+stations.get(i).getOpenChargingPoints());
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
//			Search.pricelist2(stations);
			int beforecp= station.getOpenChargingPoints();
			station.setOpenChargingPoints(beforecp-1);
			BufferedWriter writer3;
			try {
				writer3= new BufferedWriter(new FileWriter("C:\\Users\\manpe\\eclipse-workspace\\ABM-Steinkjer_random\\satisfaction.txt",true));
				writer3.newLine();
				writer3.append(String.format("%02d", GlobalClock.getTime()[2])+"/01/2020 "+String.format("%02d", GlobalClock.getTime()[0])+":"+String.format("%02d", GlobalClock.getTime()[1])+";");
				writer3.append(getAgent().getAgentId()+";"+station.getID()+";"+getAgent().getCar().getCurrentEnergy()+";"+getAgent().getCar().getMaxEnergy()+";"+getsatisfaction(station,getAgent())[0]+";"+getsatisfaction(station,getAgent())[1]+";"+getsatisfaction(station,getAgent())[2]+";"+getsatisfaction(station,getAgent())[3]+";"+getsatisfaction(station,getAgent())[4]+";"+getsatisfaction(station,getAgent())[5]);
				writer3.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Satisfaction was"+getsatisfaction(station,getAgent()));
			System.out.println("consumer price was"+station.getPriceConsumer());
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
//		boolean finished=false;
		boolean notC = false;
		double[] values= {getTargetChargingStation().getChargeRate(), getAgent().getCar().getChargeRate()};
		Arrays.sort(values);
		double value=values[0]/80;
//		double value = getTargetChargingStation().getChargeRate()/240;//getVirtualChargeRate(getTargetChargingStation().getChargeRateToUse(getAgent()));
		System.out.println("The station can charge up to "+getTargetChargingStation().getChargeRate());
		System.out.println("The agent is able to charge up to"+getAgent().getCar().getChargeRate());
		System.out.println("That is why it is charging at"+value);
		if(debug) {System.out.println("Charging rate by targetstation: "+value);}
		double startTime = System.currentTimeMillis();
		String virtStart = GlobalClock.getInstance().getTimeStamp();
		

		while(needToCharge() && !getAgent().isDoAction() && !Variables.needsmatlab) {
//			System.out.println("Is the agent allowed to charge?"+getTargetChargingStation().isAllowedToCharge());
			boolean allowedtocharge=true; // getTargetChargingStation().isAllowedToCharge()
			if(allowedtocharge) {
				if(debug) {
					System.out.println("Agent ("+getAgent().getAgentId()+"): I'm inside the while loop for charging. Time is: "+GlobalClock.getInstance().getTimeStamp());
					System.out.println("      ("+getAgent().getAgentId()+"): needToCharge()? "+needToCharge()+", isDoAction()? "+getAgent().isDoAction());
					System.out.println("      ("+getAgent().getAgentId()+"): My current energy level is: "+getAgent().getCar().getCurrentEnergy()+" kw.");
					System.out.println("Is the clock active "+GlobalClock.getInstance().active);
					System.out.println("Variable"+Variables.needsmatlab);
					System.out.println("Counter"+Variables.COUNTER);
				}
				double energyValue = getAgent().getCar().getCurrentEnergy();
				if(energyValue+value < getAgent().getCar().getMaxEnergy()) {
					if(getAgent().getag_counter()>=Variables.COUNTER) {
						System.out.println("no carga");
						getAgent().getCar().setCurrentEnergy(energyValue);
						System.out.println("counter es "+getAgent().getag_counter());
						System.out.println("clock is"+ Variables.COUNTER);
					
					}
					else if(getAgent().getag_counter()<Variables.COUNTER) {
						System.out.println("si carga");
						getAgent().getCar().setCurrentEnergy(energyValue+value);
						System.out.println("counter es "+getAgent().getag_counter());
						System.out.println("clock is"+ Variables.COUNTER);
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
			System.out.println("      ("+getAgent().getAgentId()+"): needToCharge()? "+needToCharge());
			System.out.println("      ("+getAgent().getAgentId()+"): isDoAction()? "+getAgent().isDoAction());
			System.out.println("      ("+getAgent().getAgentId()+"): I started charging at: "+virtStart);
			System.out.println("      ("+getAgent().getAgentId()+"): I stopped charging at: "+GlobalClock.getInstance().getTimeStamp());
			System.out.println("      ("+getAgent().getAgentId()+"): Stopped Charging. Ended with "+getAgent().getCar().getCurrentEnergy()+" kW");
			System.out.println("      ("+getAgent().getAgentId()+"): I drive "+getAgent().getCurrentRoute().getDistance()+" meters ("+getAgent().getCurrentRoute().getDistance()/1000+" km) to work.");
			System.out.println("Final energy is"+getAgent().getCar().getCurrentEnergy());
		}
		//Added by @Manuep
//		finished=true;
		//if(((WorkerAgent) getAgent()).haveErrand()) {
		//}
		if(getAgent().getCar().getCurrentEnergy()==getAgent().getCar().getMaxEnergy()) {
		getTargetChargingStation().unregisterCharger(getAgent());
		stopCharging();
		}
//		else if (getAgent().getCar().getCurrentEnergy()<getAgent().getCar().getMaxEnergy()) {
//			getAgent().getCar().setCurrentEnergy(getAgent().getCar().getCurrentEnergy()-value);
//		}
		if(debug) {System.err.println("Agent ("+getAgent().getAgentId()+"): Stopped Charging. Ended with "+getAgent().getCar().getCurrentEnergy()+" kW");}
		
//		return finished;
	}
	
	@Override
	public boolean chargingtime() {
		int tonto;
		while(needToCharge() && !getAgent().isDoAction()) {
			tonto=1;
		}
		boolean finished=true;
		return finished;
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
				System.out.print("Ahora el ischarging is"+isCharging());
				System.out.println("Has registered the agent");
				System.out.println("Agent("+getAgent().getAgentId()+") charges at rate"+cheapestChargingStation.getChargeRate());
				
				System.out.println("Agent  "+getAgent().getAgentId()+"has battery initially"+getAgent().getCar().getCurrentEnergy());
				if(debug) {System.out.println("Agent ("+getAgent().getAgentId()+"): Charging at work, with charging station at: "+cheapestChargingStation.getAddress()+"with price"+cheapestChargingStation.getPriceCurrent());
				}
				int tonto;
				while(needToCharge() && !getAgent().isDoAction()) {
					tonto=1;
				}


			}

//			else if(!reachablestations.isEmpty()) {
//				for(Map.Entry<Double, ChargingStation> entry : getReachableChargingStations(getAgent().getCurrentLocation()).entrySet()) {
//					if(entry.getValue().registedCharger(getAgent())) {
//
//						setTargetChargingStation(entry.getValue());
//						setCharging(true);
//						if(debug) {System.out.println("Agent ("+getAgent().getAgentId()+"): Charging at work, with charging station at: "+entry.getValue().getAddress()+"");}
//
//						break;
//					}
//				}
//			}



		}
		else if(!needToCharge()) {
			System.err.println("Agent ("+getAgent().getAgentId()+"): I don't have a need to charge at work.");
			System.err.println("   My current energy level: "+getAgent().getCar().getCurrentEnergy());
			System.err.println("   Do I have errands? "+((WorkerAgent) getAgent()).haveErrand());
			System.err.println("   And I drive "+getAgent().getCurrentRoute().getDistance()+" meters ("+getAgent().getCurrentRoute().getDistance()/1000+" km) to work.");
		}


	}


	@Override
	public TreeMap<Double,ChargingStation> getReachableChargingStations(Location currentlocation) {
		System.out.println("Has entered the multiplereachablestations loop");
		List <ChargingStation> stations= ChargingStationMap.getChargingStations();
		System.out.println(stations.size()+"have been imported");
		double batteryrange = getAgent().getCar().getbatteryrange(); 
		System.out.println("The battery range is"+ batteryrange);
		for (int i=0; i<15;i++) {
			Location locst=stations.get(i).getLocation();
			Location locag=currentlocation;
			Double distance =Utils.Distance.getDistanceBetweenPoints(locst.getLatitude(),locst.getLongitude(),locag.getLatitude(),locag.getLongitude())/1000;
			if (distance>(batteryrange) || stations.get(i).getOpenChargingPoints()<=0) {
				stations.remove(i);
				System.out.println("Remove station"+stations.get(i).getID()+"Agents energy is "+getAgent().getCar().getbatteryrange()+"km. And the station is"+distance+"km far");
			}
		}
		TreeMap<Double,ChargingStation> pricelisted = Search.pricelist(stations);
//		Search.pricelist2(stations);
//		if(debug) {System.out.println("Agent ("+getAgent().getAgentId()+"): Possible stations: "+reachablestations.size());}
		return pricelisted;
	}
	
	@Override
	public void setPossibleWorkChargingStations(Location workLocation) {
//		this.reachablestations = getReachableChargingStations(getAgent().getCurrentLocation());
//		if(debug) {System.out.println("Agent ("+getAgent().getAgentId()+"): Possible stations: "+reachablestations.size());}
	}

	@Override
	public void chargeAtWork() {
		// TODO Auto-generated method stub
		
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