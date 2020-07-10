//The simulation tool created by Manuel Pérez (manperbra@outlook.es) no longer uses the Excel writer of outputs.

package Utils.Statistics;

import java.io.File;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.util.*;

import Utils.Variables;
import Utils.Statistics.StringDateComparator;

import jxl.Workbook;
import jxl.write.*;
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;
import model.schdeule.GlobalClock;

import java.util.concurrent.ConcurrentHashMap;

public class ExcelWriter {

	private static boolean debug = false;

	public static void writeOutHashMap(SortedMap<String, Double> map) {
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(new File(Variables.PATH+"/output.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);

	     /*The following code fragment puts a label in cell A3,
	      * Label label = new Label(0, 2, "A label record");
		  * sheet.addCell(label)
	      */

		Label header = new Label(0, 0, "Day and time");
		Label header2 = new Label(1, 0, "Total Charged Power");
		try{
			sheet.addCell(header);
			sheet.addCell(header2);
		}catch (RowsExceededException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int row = 1;
		for(Map.Entry<String, Double> entry : map.entrySet()) {
			Label label = new Label(0,row, entry.getKey().toString());
			Number label2 = new Number(1, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}

		row = 1;
		int col = 4;
		Label header3 = new Label(col, 0, "Time");
		try{
			sheet.addCell(header3);
		}catch (RowsExceededException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for(Map.Entry<String, Double> entry : map.entrySet()) {
			String dayAndTime = entry.getKey().toString();
			String day = dayAndTime.substring(0,dayAndTime.indexOf('-'));
			if (col==4){
				String timeOfDay = dayAndTime.substring(dayAndTime.indexOf('-')+1);
				Label label = new Label(col,row, timeOfDay);
				try {
					sheet.addCell(label);
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			Number label2 = new Number(col+1, row, entry.getValue());

			try {
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(row==24*60){
				Label header4 = new Label(col+1, 0, "Day "+day);
				row = 1;
				col++;
				try{
					sheet.addCell(header4);
				}catch (RowsExceededException e){
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				row++;
			}

		}
		try {
			if(debug){System.out.println(".... Writing to book: "+Variables.PATH+"/output.xls");}
			workbook.write();
			workbook.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	//public String getTimeStamp(int[] time) {return time[2]+"-"+String.format("%02d", time[0])+":"+String.format("%02d", time[1]);}

	public static void writeOutHashMap(SortedMap<String, Double> map, SortedMap<String, Double> map2) {
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(new File(Variables.PATH+"/output2.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);

	     /*The following code fragment puts a label in cell A3,
	      * Label label = new Label(0, 2, "A label record");
		  * sheet.addCell(label)
	      */
		int row = 0;
		for(Map.Entry<String, Double> entry : map.entrySet()) {
			Label label = new Label(0,row, entry.getKey().toString());
			Number label2 = new Number(1, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}

		row = 0;
		for(Map.Entry<String, Double> entry : map2.entrySet()) {
			Label label = new Label(4,row, entry.getKey().toString());
			Number label2 = new Number(5, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}
		try {
			if(debug){System.out.println(".... Writing to book: "+Variables.PATH+"/output.xls");}
			workbook.write();
			workbook.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public static void writeOutHashMap(SortedMap<String, Double> map, SortedMap<String, Double> map2, SortedMap<String, Double> map3) {
		WritableWorkbook workbook = null; 
		try {
			workbook = Workbook.createWorkbook(new File(Variables.PATH+"/output.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	     WritableSheet sheet = workbook.createSheet("First Sheet", 0); 
	     
	     /*The following code fragment puts a label in cell A3,
	      * Label label = new Label(0, 2, "A label record");
		  * sheet.addCell(label)
	      */
	     int row = 0;
	     for(Map.Entry<String, Double> entry : map.entrySet()) {
	    	 Label label = new Label(0,row, entry.getKey().toString());
	    	 Number label2 = new Number(1, row, entry.getValue());
	    	 
	    	 try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	 row++;
	 	    	 
	     }
	     
	     row = 0;
	     for(Map.Entry<String, Double> entry : map2.entrySet()) {
	    	 Label label = new Label(4,row, entry.getKey().toString());
	    	 Number label2 = new Number(5, row, entry.getValue());
	    	 
	    	 try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	 row++;
	 	    	 
	     }

		row = 0;
		for(Map.Entry<String, Double> entry : map3.entrySet()) {
			Label label = new Label(8,row, entry.getKey().toString());
			Number label2 = new Number(9, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}
	     
	     try {
			workbook.write();
		    workbook.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Sondre stuff

	public static void writeOutHashMap(SortedMap<String, Double> map, SortedMap<String, Double> map2, SortedMap<String, Double> map3, SortedMap<String, Double> map4, SortedMap<String, Double> map5, SortedMap<String, Double> map6) {
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(new File(Variables.PATH+"/output2.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);

	     /*The following code fragment puts a label in cell A3,
	      * Label label = new Label(0, 2, "A label record");
		  * sheet.addCell(label)
	      */
		int row = 0;
		for(Map.Entry<String, Double> entry : map.entrySet()) {
			Label label = new Label(0,row, entry.getKey().toString());
			Number label2 = new Number(1, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}

		row = 0;
		for(Map.Entry<String, Double> entry : map2.entrySet()) {
			Label label = new Label(4,row, entry.getKey().toString());
			Number label2 = new Number(5, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}

		row = 0;
		for(Map.Entry<String, Double> entry : map3.entrySet()) {
			Label label = new Label(8,row, entry.getKey().toString());
			Number label2 = new Number(9, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}

		row = 0;
		for(Map.Entry<String, Double> entry : map4.entrySet()) {
			Label label = new Label(12,row, entry.getKey().toString());
			Number label2 = new Number(13, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}

		row = 0;
		for(Map.Entry<String, Double> entry : map5.entrySet()) {
			Label label = new Label(16,row, entry.getKey().toString());
			Number label2 = new Number(17, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}

		row = 0;
		for(Map.Entry<String, Double> entry : map6.entrySet()) {
			Label label = new Label(20,row, entry.getKey().toString());
			Number label2 = new Number(21, row, entry.getValue());

			try {
				sheet.addCell(label);
				sheet.addCell(label2);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			row++;

		}

		try {
			workbook.write();
			workbook.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void writeOutHashMapList(SortedMap<String, ArrayList<Double>> map) {
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(new File(Variables.PATH+"/outputAgents.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);

	     /*The following code fragment puts a label in cell A3,
	      * Label label = new Label(0, 2, "A label record");
		  * sheet.addCell(label)
	      */
		Label header = new Label(0, 0, "Day and time");
		try{
			sheet.addCell(header);
		}catch (RowsExceededException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int maxIterations = Variables.NUMBER_OF_WORKER_AGENTS;
		if(Variables.NUMBER_OF_WORKER_AGENTS>Variables.AGENT_PRINT_LIMIT) {maxIterations =  Variables.AGENT_PRINT_LIMIT-1;}

		//for(int i = 0; i < Variables.NUMBER_OF_WORKER_AGENTS; i++){
		for(int i = 0; i < maxIterations; i++){
			Label header1 = new Label(i+1, 0, "Agent"+(i+1));
			try{
				sheet.addCell(header1);
			}catch (RowsExceededException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			}
		}

		int row = 1;
		for(Map.Entry<String, ArrayList<Double>> entry : map.entrySet()) {
			Label label1 = new Label(0,row, entry.getKey().toString());

			try {
				sheet.addCell(label1);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ArrayList<Double> values =  entry.getValue();

			for(int i = 0; i < maxIterations; i++){
			//Number label2 = new Number(i+1, row, entry.getValue());


				Number label2 = new Number(i+1, row, values.get(i));

				try {
					sheet.addCell(label2);
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			row++;

		}


		/*The following code fragment puts a label in cell A3,
	      * Label label = new Label(0, 2, "A label record");
		  * sheet.addCell(label)
	      */

		/*WritableSheet sheet2 = workbook.createSheet("Second Sheet", 0);

		Label row = new Label(0,0,"Agent ID");
		Label row1 = new Label(0,0,"Car type");
		Label ro2 = new Label(0,0,"Agent Info");

		for(int i = 0; i < Variables.NUMBER_OF_WORKER_AGENTS; i++){
			Label header1 = new Label(i+1, 0, "Agent"+(i+1));
			Label label = new Label(i+1, 0, "Agent"+(i+1));
			try{
				sheet2.addCell(header1);
			}catch (RowsExceededException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		*/


		try {
			workbook.write();
			workbook.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}



	public static void writeOutHashMapMap(SortedMap<Integer, SortedMap<String,Double>> map) {
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(new File(Variables.PATH+"/outputStations.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);

		Label header = new Label(0, 0, "Day and time");
		try{
			sheet.addCell(header);
		}catch (RowsExceededException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int numberOfDays = GlobalClock.getInstance().getSimulationDays();
		int[] mockTime = new int[3]; //hour,min,day
		mockTime[0] = 0;
		mockTime[1] = 0;
		mockTime[2] = 1;

		ArrayList<Integer> stationIDs = new ArrayList<Integer>(map.keySet());

		int stationNr = 1;
		for(Integer stationID : stationIDs){
			if(debug){System.out.println("- Station ID: "+stationID);}
			Label header1 = new Label(stationNr, 0, "StationID: "+stationID);
			try{
				sheet.addCell(header1);
			}catch (RowsExceededException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stationNr++;
		}


		double[] latestValue = new double[stationIDs.size()];
		for(int i = 0; i < latestValue.length ; i++) {latestValue[i] = 0;}

		int row = 1;
		while(mockTime[2] < numberOfDays+1) {

			Label label1 = new Label(0, row, StringDateComparator.getTimeToString(mockTime)); //prints date and time
			try {
				sheet.addCell(label1);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int stationNr2 = 1;
			for (Integer stationID : stationIDs) {

				double timeValue = latestValue[stationNr2-1];

				if(debug){if(map.get(stationID).get(StringDateComparator.getTimeToString(mockTime)) != null){System.out.println("Time: "+StringDateComparator.getTimeToString(mockTime)+" and value "+map.get(stationID).get(StringDateComparator.getTimeToString(mockTime)));}}

				if(map.get(stationID).get(StringDateComparator.getTimeToString(mockTime)) != null) {
					timeValue = map.get(stationID).get(StringDateComparator.getTimeToString(mockTime));
					latestValue[stationNr2-1] = timeValue;
				}

				Number number = new Number(stationNr2, row, timeValue); //prints date and time with number
				try {
					sheet.addCell(number);
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(false){System.out.println("- Station nr: "+stationNr2+" Timevalue: "+timeValue);}
				stationNr2++;

			}

			//increments mockTime
			if (mockTime[1] >= 59) {
				mockTime[1] = 0;
				mockTime[0]++;
				if (mockTime[0] == 24) {
					mockTime[0] = 0;
					mockTime[2]++;
				}
			} else {
				mockTime[1]++;
			}

			row++;
		}

		try {
			workbook.write();
			workbook.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public static void writeOutConcurrentHashMapMap(ConcurrentHashMap<Integer, ConcurrentHashMap<String,Double>> map) {
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(new File(Variables.PATH+"/outputStations2.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);

		Label header = new Label(0, 0, "Day and time");
		try{
			sheet.addCell(header);
		}catch (RowsExceededException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int numberOfDays = GlobalClock.getInstance().getSimulationDays();
		int[] mockTime = new int[3]; //hour,min,day
		mockTime[0] = 0;
		mockTime[1] = 0;
		mockTime[2] = 1;

		ArrayList<Integer> stationIDs = new ArrayList<Integer>(map.keySet());
		Collections.sort(stationIDs);

		int stationNr = 1;
		for(Integer stationID : stationIDs){
			if(debug){System.out.println("- Station ID: "+stationID);}
			Label header1 = new Label(stationNr, 0, "StationID: "+stationID);
			try{
				sheet.addCell(header1);
			}catch (RowsExceededException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stationNr++;
		}


		double[] latestValue = new double[stationIDs.size()];
		for(int i = 0; i < latestValue.length ; i++) {latestValue[i] = 0;}

		int row = 1;
		while(mockTime[2] < numberOfDays+1) {

			Label label1 = new Label(0, row, StringDateComparator.getTimeToString(mockTime)); //prints date and time
			try {
				sheet.addCell(label1);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int stationNr2 = 1;
			for (Integer stationID : stationIDs) {

				double timeValue = latestValue[stationNr2-1];

				if(debug){if(map.get(stationID).get(StringDateComparator.getTimeToString(mockTime)) != null){System.out.println("Time: "+StringDateComparator.getTimeToString(mockTime)+" and value "+map.get(stationID).get(StringDateComparator.getTimeToString(mockTime)));}}

				if(map.get(stationID).get(StringDateComparator.getTimeToString(mockTime)) != null) {
					timeValue = map.get(stationID).get(StringDateComparator.getTimeToString(mockTime));
					latestValue[stationNr2-1] = timeValue;
				}

				Number number = new Number(stationNr2, row, timeValue); //prints date and time with number
				try {
					sheet.addCell(number);
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(false){System.out.println("- Station nr: "+stationNr2+" Timevalue: "+timeValue);}
				stationNr2++;

			}

			//increments mockTime
			if (mockTime[1] >= 59) {
				mockTime[1] = 0;
				mockTime[0]++;
				if (mockTime[0] == 24) {
					mockTime[0] = 0;
					mockTime[2]++;
				}
			} else {
				mockTime[1]++;
			}

			row++;
		}

		try {
			workbook.write();
			workbook.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}

	public static void writeOutHashMapMapOld(SortedMap<Integer, SortedMap<String,Double>> map) {
		WritableWorkbook workbook = null;
		try {
			workbook = Workbook.createWorkbook(new File(Variables.PATH+"/outputStations.xls"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		WritableSheet sheet = workbook.createSheet("First Sheet", 0);

		Label header = new Label(0, 0, "Day and time");
		try{
			sheet.addCell(header);
		}catch (RowsExceededException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int numberOfDays = GlobalClock.getInstance().getSimulationDays();
		int[] mockTime = new int[3]; //hour,min,day
		mockTime[0] = 0;
		mockTime[1] = 0;
		mockTime[2] = 1;

		ArrayList<Integer> stationIDs = new ArrayList<Integer>(map.keySet());

		int stationNr = 1;
		for(Integer stationID : stationIDs){
			if(debug){System.out.println("- Station ID: "+stationID);}
			Label header1 = new Label(stationNr, 0, "StationID: "+stationID);
			try{
				sheet.addCell(header1);
			}catch (RowsExceededException e){
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			stationNr++;
		}

		int row = 1;
		while(mockTime[2] < numberOfDays+1) {

			Label label1 = new Label(0, row, StringDateComparator.getTimeToString(mockTime)); //prints date and time
			try {
				sheet.addCell(label1);
			} catch (RowsExceededException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (WriteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			int stationNr2 = 1;
			for (Integer stationID : stationIDs) {
				//if(true){System.out.println("- Station ID: "+stationID);}
				SortedMap<String, Double> map2 = new TreeMap<String, Double>(map.get(stationID));
				// or

				double timeValue = map2.get(StringDateComparator.getTimeToString(mockTime));
				//double timeValue = 0; //map2.get(StringDateComparator.getTimeToString(mockTime));
				//if (map2.get(StringDateComparator.getTimeToString(mockTime)) == null) {
				//	if (map2.get(StringDateComparator.getTimeToString(StringDateComparator.oneMinBack(mockTime))) == null) {
				//		//timeValue = 0; //prints date and time
				//	} else {
				//		double previousTimeValue = map2.get(StringDateComparator.getTimeToString(StringDateComparator.oneMinBack(mockTime)));
				//		timeValue = previousTimeValue;
				//	}
				//} else
				//{
				//	timeValue = map2.get(StringDateComparator.getTimeToString(mockTime));
				//}


				Number number = new Number(stationNr2, row, timeValue); //prints date and time with number
				try {
					sheet.addCell(number);
				} catch (RowsExceededException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (WriteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				if(false){System.out.println("- Station nr: "+stationNr2+" Timevalue: "+timeValue);}
				stationNr2++;

			}

			//increments mockTime
			if (mockTime[1] >= 59) {
				mockTime[1] = 0;
				mockTime[0]++;
				if (mockTime[0] == 24) {
					mockTime[0] = 0;
					mockTime[2]++;
				}
			} else {
				mockTime[1]++;
			}

			row++;
		}

		try {
			workbook.write();
			workbook.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}


}
