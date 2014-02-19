package city;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import agent.Agent;
import city.CarAgent.carState;
import city.interfaces.Transportation;
import city.interfaces.Vehicle;

public class TrafficAgent extends Agent{

	public void trafficAgent(){
		
	}

	
	public List<Vehicle> vehiclesAtStop
	= Collections.synchronizedList(new ArrayList<Vehicle>());
	public List<Transportation> crossingPedestrians
	= Collections.synchronizedList(new ArrayList<Transportation>());
	Timer timer = new Timer();

	public enum lightState
	{red, green, none}
	public lightState currentLight= lightState.none;

	
	//@Override
	public boolean pickAndExecuteAnAction() {
		
		//this is the version once we have pedestrians crossing properly
		/*
		while(!crossingPedestrians.isEmpty()){ //if there are pedestrians then no greenLight
			
		}
		
		if(crossingPedestrians.isEmpty() && !vehiclesAtStop.isEmpty()){ //if no one's there, stop to be sure then go
			timer.schedule(new TimerTask() {
				public void run() {
					greenLight();
				}
			},
			400);
			return true;
		}*/
		
		//this version is without pedestrians crossing. ppl just stop for a while then go
		if(currentLight==lightState.red && !vehiclesAtStop.isEmpty()){
			timer.schedule(new TimerTask() {
				public void run() {
					greenLight();
				}
			},
			400);
			return true;
		}
		
		return false;
	}


	protected void greenLight(){
		synchronized(vehiclesAtStop){
		for(Vehicle v:vehiclesAtStop){
			v.msgGreenLight(this);
		}
		}
		currentLight= lightState.green;
	}
	
	public void msgAtIntersection(Vehicle v) {
		synchronized(vehiclesAtStop){
			vehiclesAtStop.add(v);
		}
			currentLight= lightState.red;
			stateChanged();
	}

	public void msgRemoveFromIntersection(Vehicle v) {
		synchronized(vehiclesAtStop){
		vehiclesAtStop.remove(v);	
		}
	}
	
	

}
