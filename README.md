#ELECTRIC VEHICLE SIMULATION with SERVICE REDUCTION#
 
## ABOUT ##
An implementation (prototype) of an ABM system to simulate EV behavior in Trondheim. Its coded in Java, but it may possibly be ported to Android.
Develop for a Master Thesis at NTNU


### Scenario description ###
Given an acceptable realistic number of todays electric vehicles that are on the move around Trondheim. It is an acceptable assumption to believe that most EV owners will recharge their vehicles at home or when its available related to their workplace, shopping, and other places. But the main recharging will happen during 7 p.m. to 7 a.m. (at home, after work).    
Additionally, the scenario will follow the these assumptions:     
	* Trondheim will have V electrical vehicles (EVs)
	* Trondheim is defined by a 4 pairs of longitude and latitude, 4 corners representing a square.
	* All V EV owners use their EV as their main form of transport, and are employed from 0800 to 1600
	* All V EVs will move a range of [X,Y] meters back and forth to work. The workplace is randomly assigned in the vicinity of Trondheim.
	* W% of V EVs will be plugged in while at work. It will be realistic number with respect to the actual number of charging stations in Trondheim.
	* H% of V EVs will be plugged in while at home. The number will be close to 100%
	* A% of V EVs will drive an additional of meters in the range of [N,M] meter, before or after work. This is done to introduce some variations and daily tasks.
	
	


Addition 2: A available database on actual charging stations in Trondheim (their locations are based on longitude and latitude, and having [1-4] available charging spots), to more accurately assign the W% of EVs. The assignment of EVs to a charging station will then be determined if its has an available spot and that if it is D meters away from their workplace.        

The "charging strategy" in Scenario 1 is a so-called "dumb" or uncontrolled charging strategy, EVs plug-in and charge whenever and wherever they can. Addition 1 will introduce a so called duel-tariff charging, with the idea of simulating EV  owners having a response to price changes, which you can call a form of mitigation strategy to reduce energy demand during peak hours.    
  

## HOW TO ##
1. Specfify the PATH to the source code in /src/Utils/Variables.java
	public static final String PATH = "E:/xample/path/to/source/code"

2. Run the project with World.java as main, and follow the input instructions OR

3. Export the java project as "Runnable JAR file" with World.java as launch configuration.

4. Run the simulation in a shell or cmd: 'java -jar path/to/exported/project.jar'
	, and follow the input instructions

## KNOWN BUGS ##
1. "PowerGrid: currentEnergy is negavtive: "      
	Retry or specify a higher peak limit (Concurrency bug).

2. "JSONException: End of input at character 0"       
	Delete "path/to/source/code/src/res/routes.json" and run the simulation again (use Google Routes option once or more to build a new sample size).

3. "java.lang.OutOfMemoryError: unable to create new native thread"       
	You're running the simulation out of Eclipse, try using a shell or cmd.