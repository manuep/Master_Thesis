package model.schdeule;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.concurrent.ExecutionException;
import java.io.*;
import java.lang.*;
import java.util.*;

import com.mathworks.engine.EngineException;
import com.mathworks.engine.MatlabEngine;

import Utils.Variables;
import factory.ChargingStationFactory;
import jxl.read.biff.BiffException;
import jxl.write.WriteException;
import model.agent.Agent;
import model.agent.AgentSupervisoryManager;
import model.agent.WorkerAgent;
//import model.powerutilities.PowerGrid;
import model.powerutilities.ChargingStation;
import model.powerutilities.ChargingStationMap;

import static model.agent.AgentSupervisoryManager.*;
import com.mathworks.engine.MatlabEngine;
import com.mathworks.engine.MatlabExecutionException;
import com.mathworks.engine.MatlabSyntaxException;
import com.mathworks.engine.EngineException;
import com.mathworks.engine.FutureMatlab;

public class Coord_matlab {
	
//	public void run() {
//		try {
//			loop();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	public synchronized void loop() throws Exception {
//		while(Variables.needsmatlab) {
//			llamamiento();
//		
//		}
//	}
	
	public static void llamamiento2(boolean really_needs) throws Exception {
		if (really_needs) {
		llamamiento();
		}
		else {
			Variables.needsmatlab=false;
			System.out.println("Ahorrando tiempo");
		}
	}
	
	
	
	public static void llamamiento ()  throws Exception {
//			double[] nordpool_prices= new double[] {140.74,	139.62,	138.39,	138.19,	139.01,	143.4,	144.93,	145.13,	151.77,	171.48,	173.73,	173.73,	173.22,	173.02,	173.53,	177.82,	183.84,	193.14,	192.63,	188.13,	182.11,	181.09,	179.45,	173.22};
			double[][] nordpool_prices= new double[][] {
				{140.67,138.74,136.00,134.07,133.16,132.65,133.46,135.19,138.95,141.48,142.40,142.80,141.99,141.38,141.79,142.80,146.16,157.33,311.00,157.02,138.13,135.69,131.84,123.41},
				{129.60,125.55,121.30,92.87,76.28,42.90,38.95,51.19,74.97,94.19,101.78,102.08,101.88,100.97,85.39,84.48,100.46,118.97,121.10,87.00,41.38,33.08,13.15,0.40},
				{-26.10,-27.11,-26.91,-26.91,-7.59,0.10,90.85,132.53,182.91,133.54,126.86,127.17,121.50,121.30,121.20,123.83,129.19,141.53,140.93,135.26,132.63,126.16,118.16,52.00},
				{110.88,48.86,1.11,0.91,0.91,14.06,128.28,141.13,142.75,134.76,132.63,131.52,130.00,129.19,131.52,130.91,138.30,142.85,142.24,138.90,130.91,123.63,120.59,72.94},
				{114.32,106.94,112.40,113.21,116.64,127.55,136.33,145.32,143.30,142.69,144.61,140.67,138.25,138.15,136.74,136.94,138.45,141.58,140.88,139.36,137.44,135.52,131.18,128.35},
				{128.78,128.98,128.88,129.38,130.39,132.70,140.05,388.15,517.84,475.08,407.47,383.63,370.75,358.17,359.48,362.40,379.90,432.62,533.53,451.03,412.00,382.32,371.15,317.63},
				{275.13,265.88,234.30,245.66,257.23,267.89,385.14,451.10,570.57,496.66,441.75,416.71,394.99,365.73,362.91,363.82,385.14,417.11,427.97,199.00,136.86,131.03,127.11,126.70},
				{117.48,114.97,112.77,111.37,112.17,114.77,114.87,117.78,120.49,123.69,125.50,125.60,124.90,124.20,123.90,124.30,125.70,128.21,125.60,120.89,111.37,100.44,93.32,17.24},
				{95.96,93.65,87.53,80.32,74.90,71.09,59.76,76.91,85.63,87.94,92.95,95.66,96.46,96.06,92.15,93.35,99.77,104.98,105.28,102.68,98.46,94.65,88.14,84.13},
				{40.21,40.71,39.00,36.90,47.73,95.16,112.00,116.31,119.62,118.42,117.92,116.31,115.31,114.91,115.11,114.81,116.01,122.93,122.83,115.91,113.00,111.50,108.69,98.06},
				{97.82,97.12,90.39,86.17,89.78,102.14,109.17,171.84,242.54,115.59,112.88,109.57,105.05,105.15,111.18,112.28,114.89,255.29,381.03,246.96,160.69,115.70,112.98,109.87},
				{103.39,102.98,104.19,110.04,113.47,120.84,291.60,375.62,393.17,393.27,373.20,342.94,279.30,259.02,253.07,270.82,330.33,444.41,471.64,437.55,372.60,342.03,316.01,251.56},
				{127.54,119.62,117.11,114.20,113.30,130.85,339.10,387.22,403.17,370.68,336.69,337.59,215.57,107.18,105.48,103.47,102.87,108.69,106.48,101.47,97.96,93.25,88.23,80.51},
				{83.14,76.31,74.00,79.72,85.05,88.16,95.79,177.52,220.70,149.01,110.15,105.53,97.60,94.38,91.77,90.17,90.57,94.69,95.39,90.77,87.76,85.45,81.43,71.09},
				{78.82,33.52,21.74,20.23,28.69,57.88,67.75,70.07,74.90,81.04,85.97,86.37,86.27,84.06,80.33,80.33,85.87,90.50,91.21,89.19,79.83,66.74,56.98,59.50},
				{68.47,68.97,62.09,54.08,65.33,70.39,66.85,70.80,73.94,88.32,93.18,91.36,90.14,90.75,89.43,88.22,88.62,101.28,107.56,101.69,89.23,86.49,88.42,82.85},
				{64.01,73.23,71.40,88.01,94.80,105.43,273.86,435.51,481.39,462.96,449.49,446.14,417.99,405.53,389.02,364.41,324.81,405.53,365.62,355.29,289.87,219.58,161.95,110.09}
				};
		
		MatlabEngine matEng;
			matEng = MatlabEngine.startMatlab();
		StringWriter output = new StringWriter();
		StringWriter output1 = new StringWriter();
		StringWriter output2 = new StringWriter();
//		System.out.println(GlobalClock.getTime()[0]);
		//System.out.println(output.toString());
		matEng.eval("cd 'C:\\Users\\manpe\\eclipse-workspace\\ABM-Steinkjer'", null, null);
		matEng.eval("loadbuses",null,null);
		matEng.eval("Steinkjer=SteinkjerModified",null,null);
//		matEng.eval("Steinkjer.gencost(1,5)=nordpool_prices("+((GlobalClock.getTime()[0])+1)+")");
//		matEng.eval("Steinkjer.gencost(2,5)=nordpool_prices("+((GlobalClock.getTime()[0])+1)+")");
		matEng.eval("Steinkjer.gencost(1,5)="+nordpool_prices[(GlobalClock.getTime()[2])][(GlobalClock.getTime()[0])]);
		matEng.eval("Steinkjer.gencost(2,5)="+nordpool_prices[(GlobalClock.getTime()[2])][(GlobalClock.getTime()[0])]);
//		System.out.println(((Variables.COUNTERMATLAB/15)-1)+769);
		matEng.eval("Steinkjer.bus(1:974,3)=Pd(1:974,"+(((Variables.COUNTERMATLAB/15)-1)+769)+")",null,null);
		matEng.eval("Steinkjer.bus(1:974,4)=Qd(1:974,"+(((Variables.COUNTERMATLAB/15)-1)+769)+")",null,null);
//		System.out.println("Estaba calculando para"+(((Variables.COUNTERMATLAB/15)-1)+769));
		List <ChargingStation> cstations =(ChargingStationMap.getChargingStations());
		System.out.println("Size is"+cstations.size());
		for (int i=0;i<cstations.size();i++) {
			if(cstations.get(i).getConnection()>0) {
			String currentkw= Double.toString(cstations.get(i).getCurrentKW()/1000);
			String connected=Integer.toString(cstations.get(i).getConnection());
			matEng.eval("Steinkjer.bus("+connected+",3)=Steinkjer.bus("+connected+",3)+"+currentkw,null,null);
			System.out.println("The station"+cstations.get(i).getID()+"is currently used to"+cstations.get(i).getCurrentKW()+"and connected to"+connected);
		}
		}
		
		matEng.eval("results=runopf(Steinkjer)",null,null);
		matEng.eval("losses_r=real(sum(get_losses(results)))",null,null);
		matEng.eval("losses_im=imag(sum(get_losses(results)))",null,null);
		Variables.losses_real=matEng.getVariable("losses_r");
		Variables.losses_imag=matEng.getVariable("losses_im");
		System.out.println("matlab= "+Variables.losses_real+";"+Variables.losses_imag);
//		System.out.println("losses_matlab = "+loss);
		
		List <ChargingStation> cstations2 =(ChargingStationMap.getChargingStations());
		System.out.println("Size is"+cstations2.size());
		for (int j=0;j<cstations.size();j++) {
		if(cstations2.get(j).getConnection()>0) {
			String bus=Integer.toString(cstations2.get(j).getConnection());
			
			matEng.eval("v_m=results.bus("+bus+",8)",output1,null);
			matEng.eval("v_a=results.bus("+bus+",9)",output2,null);
			Variables.voltage_magnitudes[(cstations2.get(j).getID())-1]=matEng.getVariable("v_m");
			Variables.voltage_angles[(cstations2.get(j).getID())-1]=matEng.getVariable("v_a");
			matEng.eval("precio=results.bus("+bus+",14)",output,null);
			double precio=matEng.getVariable("precio");
			System.out.println("The price of station"+cstations2.get(j).getID()+"is"+precio);
			double preciofinal= ((nordpool_prices[(GlobalClock.getTime()[2])][(GlobalClock.getTime()[0])])/80)+333.3*((precio-nordpool_prices[(GlobalClock.getTime()[2])][(GlobalClock.getTime()[0])])/(nordpool_prices[(GlobalClock.getTime()[2])][(GlobalClock.getTime()[0])]));
			if (preciofinal>10) {
				preciofinal=10;
			}
			System.out.println("The nordpool price is"+nordpool_prices[(GlobalClock.getTime()[2])][(GlobalClock.getTime()[0])]);
			System.out.println("The final price is"+preciofinal);
			cstations2.get(j).setPriceCurrent(precio);
			cstations2.get(j).setPriceConsumer(preciofinal);
			System.out.println(cstations2.get(j).getOpenChargingPoints());
			//System.out.println("The price of station"+cstations.get(i).getID()+"is"+output.toString());

		}
	}
//		matEng.eval("losses=sum(get_losses(results))",output,null);
//		Variables.losses=matEng.getVariable("losses");
			System.out.println("Estoy en matlab");
			matEng.close();
			Variables.needsmatlab=false;
	}
}
	
	

