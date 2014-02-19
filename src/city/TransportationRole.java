package city;


//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.List;
//import java.util.concurrent.Semaphore;

import java.util.concurrent.Semaphore;

import agent.Role;
import city.gui.CarGui;
import city.gui.TransportationGui;
import city.helpers.BusHelper;
import city.helpers.Directory;
import city.interfaces.Transportation;

public class TransportationRole extends Role implements Transportation  {

	String destination;
	private String startingLocation;
	String currentLocation;
	//String stopDestination; //for bus stop
	private Semaphore actionComplete = new Semaphore(0,true);
	CarAgent car;
	BusAgent bus;
	Boolean hasCar = false;
	int startX, startY, startStopX, startStopY, endStopX, endStopY, startStopNumber, finalStopNumber;
	String startStop, endStop;
	
	public enum BusStop
		{stop1, stop2, stop3, stop4, none}
	BusStop stopDestination = BusStop.none; 
	
	public enum TransportationState 
		{Walking, NeedsToTravel, InTransit, AtDestination, None, WaitingForBus, OnBus, GettingOnBus, AtFinalStop, JustGotOffBus, Finished};	
	private TransportationState state = TransportationState.None;
	
	TransportationGui guiToStop;//use for bus stop
	TransportationGui guiToDestination;
	CarGui carGui;
	
	public TransportationRole(String destination, String startingLocation) {
		super();
		//hasCar = false; //hack for normative
		hasCar = true; //testing for CarAgent
		setState(TransportationState.NeedsToTravel); // hack for normative;
		this.destination = destination;
		this.setStartingLocation(startingLocation);
		this.currentLocation = startingLocation;
	}
	
	/*
	 * Messages
	 */

	public void msgActionComplete() {
		actionComplete.release();
	}
	
	public void msgGetOnBus(BusAgent b) {
		this.bus = b;
		setState(TransportationState.GettingOnBus);
		stateChanged();
	}
	
	public void msgArrivedAtDestination(String destination) {
		currentLocation= destination;
		setState(TransportationState.AtDestination);
		stateChanged();
	}
	public void msgAtStop(int stopNumber) {
		if(stopNumber == finalStopNumber) {
			setState(TransportationState.AtFinalStop);
			currentLocation= "BusStop"+stopNumber;
		}
		stateChanged();
	}
		
	/*
	 * Scheduler
	 * @see agent.Agent#pickAndExecuteAnAction()
	 */
	public boolean pickAndExecuteAnAction() {
		if (getState() == TransportationState.AtDestination) {
			EnterBuilding(); //should this be walk to destination too?
			return true;
		}
		if	(getState() == TransportationState.JustGotOffBus) {
			WalkToFinalDestination();
			return true;
		}
		if (getState() == TransportationState.AtFinalStop) {
			GetOffBus();
			return true;
		}
		if(getState() == TransportationState.GettingOnBus) {
			GetOnBus();
			return true;
		}	
		if(getState() == TransportationState.NeedsToTravel) {
			GetAVehicle();
			return true;
		}
		
		return false;
	}
	/*
	 * Actions
	 */
	
	private void EnterBuilding() {
		setState(TransportationState.Finished);
		Directory.sharedInstance().getCityGui().getMacroAnimationPanel().removeGui(guiToDestination);
		getPersonAgent().msgTransportFinished(destination);
		stateChanged();
	}

	private void WalkToFinalDestination() {
		setState(TransportationState.Walking);
		guiToDestination = new TransportationGui(this, endStop, destination);
		Directory.sharedInstance().getCityGui().getMacroAnimationPanel().addGui(guiToDestination);
		actionComplete.acquireUninterruptibly();
		setState(TransportationState.AtDestination);
		stateChanged();
	}

	private void GetOnBus() {
		Directory.sharedInstance().getCityGui().getMacroAnimationPanel().removeGui(guiToStop);
		BusHelper.sharedInstance().removeWaitingPerson(this, startStopNumber);
		bus.msgBoardingBus(this);
		setState(TransportationState.OnBus);
		stateChanged();
	}


	private void WalkToDestination() {
		//gui
		
		setState(TransportationState.None);
		stateChanged();
	}
	
	private void GetAVehicle() {
		setState(TransportationState.InTransit);

		if (getPersonAgent().getTransportationMethod().contains("Bus")) {
			if(needsBusChecker(startingLocation, destination)) {
				startStop = BusHelper.sharedInstance().busStopToString.get(currentLocation);
				endStop = BusHelper.sharedInstance().busStopToString.get(destination);
				
				startStopNumber = BusHelper.sharedInstance().busStopToInt.get(currentLocation);
				finalStopNumber = BusHelper.sharedInstance().busStopToInt.get(destination);
				guiToStop = new TransportationGui(this, currentLocation, startStop);
				print("Want " + startStop);
				
				Directory.sharedInstance().getCityGui().getMacroAnimationPanel().addGui(guiToStop);
				//print("adding transport gui to macro");
				actionComplete.acquireUninterruptibly();
				BusHelper.sharedInstance().addWaitingPerson(this, startStopNumber);
				setState(TransportationState.WaitingForBus);
			}
			else {
				/**
				 * Walk to destination after checking for buses
				 */
				guiToDestination = new TransportationGui(this, currentLocation, destination);
				Directory.sharedInstance().getCityGui().getMacroAnimationPanel().addGui(guiToDestination);
				actionComplete.acquireUninterruptibly();
				EnterBuilding();
			}
		}
//		else if (getPersonAgent().getTransportationMethod().contains("Car")) {
//			//set car agent
//			car = new CarAgent(startingLocation);
//			car.startThread();
//			//set destination
//			car.msgTakeMeHere(this, destination);
//			//create car gui
//			//carGui = new CarGui(car, startingLocation);
//			//car.setGui(carGui);
//			//.sharedInstance().getCityGui().getMacroAnimationPanel().addGui(carGui);
//			//setState(TransportationState.InTransit); //SET STATE
//		}
		else {
			/**
			 * Walk to destination don't check buses
			 */
			guiToDestination = new TransportationGui(this, currentLocation, destination);
			Directory.sharedInstance().getCityGui().getMacroAnimationPanel().addGui(guiToDestination);
			actionComplete.acquireUninterruptibly();
			EnterBuilding();
		}
		stateChanged();
	}
	private void GetOffBus() {
		bus.msgLeavingBus(this);
		setState(TransportationState.JustGotOffBus);
		stateChanged();
	}
	private void GetOffVehicle() {
		setState(TransportationState.None);
		getPersonAgent().msgTransportFinished(currentLocation); //haven't implemented updating the currentLoc for cars
		//change roles
	}

	public TransportationState getState() {
		return state;
	}

	public void setState(TransportationState state) {
		this.state = state;
	}

	public String getStartingLocation() {
		return startingLocation;
	}

	public void setStartingLocation(String startingLocation) {
		this.startingLocation = startingLocation;
	}
	public boolean needsBusChecker(String startingLocation, String destination) {
		int startingStop = BusHelper.sharedInstance().busStopToInt.get(startingLocation);
		int endStop = BusHelper.sharedInstance().busStopToInt.get(destination);
		if (startingStop == endStop) {
			return false;
		}
		else {
			return true;
		}
	}
}
