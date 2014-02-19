package city;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import agent.Agent;
import city.interfaces.Transportation;
import city.interfaces.Vehicle;
import city.BusAgent.MyPassenger.Status;
import city.gui.BusGui;
import city.interfaces.Bus;
import city.helpers.Directory;
import city.helpers.BusHelper;


public class BusAgent extends Agent implements Vehicle {
	
    /**
	*Data
	*/	
	private BusGui busGui= null;
	
	public enum busState
	{inTransit, atStop}
	public busState currentState = busState.atStop;
	//public busState currentState = busState.inTransit;
	
	public enum Station
	{Stop1, Stop2, Stop3, Stop4}
	public Station lastStation = Station.Stop1;
	
	public List<MyPassenger> passengersOnBoard
	= Collections.synchronizedList(new ArrayList<MyPassenger>());
	
	private Semaphore driving = new Semaphore(0,true);
	
	boolean stopRequested= false;
	
	Timer timer = new Timer();
	
	public static class MyPassenger{

		MyPassenger(Transportation tr){
			passenger= tr;
			status= Status.none;
		}
		Transportation passenger;
		enum Status{Riding, requestingStop, Leaving, none};
		Status status;
		
	}
	
	public BusAgent(int stopNumber){//constructor
		busGui = new BusGui(this, stopNumber);
	}
		
	/**
	*Scheduler
	*/	
	
	/*the bus should always be running and not be controlled by states.
	 * It should stop at a station only if there is someone to pickup/dropoff
	 * (non-Javadoc)
	 * @see agent.Agent#pickAndExecuteAnAction()
	 */
	public enum State
	{driving, stopping, notifyingPassengersToAlightBus, waitForAlighting, notifyingPassengersToBoardBus, waitForBoarding, stoppingForStop}
	public State state= State.driving;
	
	public enum Event
	{reachedStop, stopped, notifiedPassengersToAlightBus, passengersAlighted, notifiedPassengersToBoardBus, passengersBoarded, reachedIntersection}
	public Event event= Event.reachedStop;
	
		public boolean pickAndExecuteAnAction(){
			if(state==State.driving && event==Event.reachedStop){
				state=State.stoppingForStop;
				stopBus();//change event to stopped
				return true;
			}
			/*
			if(state==State.driving && event==Event.reachedIntersection){
				state= State.stoppingForIntersection;
				stopBus();
				return true;
			}
			
			if(state==State.stoppingForIntersection && event==Event.stopped){
				state= State.waitingForGreen;
				alertAtIntersection(); //pass control to trafficLight who will 
			}
			
			if(state==State.waitingForGreen && event==Event.givenGreen){
				state= State.driving;
				keepDriving();
			}*/
			
			if(state==State.stoppingForStop && event==Event.stopped){
				state=State.notifyingPassengersToAlightBus;
				alertPassengersToAlightBus(); //change event to notified passengers
				return true;
			}
			
			if(state==State.notifyingPassengersToAlightBus && event==Event.notifiedPassengersToAlightBus){
				state=State.waitForAlighting;
				waitForPassengersToAlight(); //timer event when done changes to passengersAlighted
				return true;
			}
			
			if(state==State.waitForAlighting && event==Event.passengersAlighted){
				state=State.notifyingPassengersToBoardBus;
				alertPassengersToBoardBus(); //change event to notifiedPassengersToBoardBus 
				return true;
			}
			
			if(state==State.notifyingPassengersToBoardBus && event==Event.notifiedPassengersToBoardBus){
				state=State.waitForBoarding;
				waitForPassengersToBoard(); //timer event when done changes to passengersBoarded
				return true;
			}
			
			if(state==State.waitForBoarding && event==Event.passengersBoarded){
				state=State.driving;
				keepDriving(); //changes event to reachedStop when reaches stop
				return true;
			}
		
			return false;
		}
		

	/**
	 * Messages
	 * @param myDestination
	 */
		public void msgBoardingBus(Transportation person){
					passengersOnBoard.add(new MyPassenger(person));	
		}
		
		public void msgLeavingBus(Transportation person){
	
			synchronized(passengersOnBoard) {
				for(MyPassenger p: passengersOnBoard){
					if (p.passenger==person){
						p.status= Status.Leaving;
						passengersOnBoard.remove(p);
						break;
					}			
				}
			}
		}
		
		public void msgAtStopOne(){
			driving.release();
			event= Event.reachedStop;
			lastStation = Station.Stop1;
			stateChanged();
		}
		
		public void msgAtStopTwo(){
			driving.release();
			event= Event.reachedStop;
			lastStation = Station.Stop2;
			stateChanged();
		}

		public void msgAtStopThree(){
			driving.release();
			event= Event.reachedStop;
			lastStation = Station.Stop3;
			stateChanged();
		}
		
		public void msgAtStopFour(){
			driving.release();
			event= Event.reachedStop;
			lastStation = Station.Stop4;
			stateChanged();
		}
		
		public void msgAtIntersection(){
			driving.release();
			event= Event.reachedIntersection;
			stateChanged();
		}

		public void msgChangeEventToPassengersAlighted(){
			event=Event.passengersAlighted;
			stateChanged();
		}
		
		public void msgChangeEventToPassengersBoarded(){
			event=Event.passengersBoarded;
			stateChanged();
		}

	/**
	 * Actions	
	 * @param myDestination
	 */
		private void stopBus(){
			busGui.DoStopDriving();
			event = Event.stopped;
		}
		
		private void alertPassengersToAlightBus(){
			if((lastStation == Station.Stop1) && (state!=State.driving)){
				synchronized(passengersOnBoard){
					for(MyPassenger person: passengersOnBoard){
						person.passenger.msgAtStop(1);
					}
				}
			}
			if((lastStation == Station.Stop2) && (state!=State.driving)){
				synchronized(passengersOnBoard){
					for(MyPassenger person: passengersOnBoard){
						person.passenger.msgAtStop(2);
					}
				}
			}
			if((lastStation == Station.Stop3) && (state!=State.driving)){
				synchronized(passengersOnBoard){
					for(MyPassenger person: passengersOnBoard){
						person.passenger.msgAtStop(3);
					}
				}
			}
			if((lastStation == Station.Stop4) && (state!=State.driving)){
				synchronized(passengersOnBoard){
					for(MyPassenger person: passengersOnBoard){
						person.passenger.msgAtStop(4);
					}
				}
			}	
			event=Event.notifiedPassengersToAlightBus;
		}
		
		private void waitForPassengersToAlight(){
			timer.schedule(new TimerTask() {
				public void run() {
					msgChangeEventToPassengersAlighted();
				}
			},
			420);
		}
		
		private void alertPassengersToBoardBus(){
			if((lastStation == Station.Stop1) && (state!=State.driving)){
				synchronized(BusHelper.sharedInstance().getWaitingPassengersAtStop1()){
					for(Transportation person: BusHelper.sharedInstance().getWaitingPassengersAtStop1()){
						person.msgGetOnBus(this);
					}	
				}
			}
			if((lastStation == Station.Stop2) && (state!=State.driving)){
				synchronized(BusHelper.sharedInstance().getWaitingPassengersAtStop2()){
					for(Transportation person: BusHelper.sharedInstance().getWaitingPassengersAtStop2()){
						person.msgGetOnBus(this);
					}	
				}	
			}
			if((lastStation == Station.Stop3) && (state!=State.driving)){
				synchronized(BusHelper.sharedInstance().getWaitingPassengersAtStop3()){
					for(Transportation person: BusHelper.sharedInstance().getWaitingPassengersAtStop3()){
						person.msgGetOnBus(this);
					}	
				}
			}
			if((lastStation == Station.Stop4) && (state!=State.driving)){
				synchronized(BusHelper.sharedInstance().getWaitingPassengersAtStop4()){
					for(Transportation person: BusHelper.sharedInstance().getWaitingPassengersAtStop4()){
						person.msgGetOnBus(this);
					}	
				}
			}
			event=Event.notifiedPassengersToBoardBus;
		}
		
		private void waitForPassengersToBoard(){		
			timer.schedule(new TimerTask() {
				public void run() {
					msgChangeEventToPassengersBoarded();
				}
			},
			420);
			
		}
		
		private void keepDriving(){
			//doGoTo(myDestination); //sets destination in carGui
			doKeepDriving();
			try {
				driving.acquire(); //to ensure that the gui is uninterrupted on the way
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
		}
		
		private void doKeepDriving(){
			busGui.DoKeepDriving();
		}
		
		
		/*
		 * Utilities
		 */
		public void setGui(BusGui gui){
			busGui = gui;
		}


		@Override
		public void msgGreenLight() {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void msgGreenLight(TrafficAgent trafficAgent) {
			// TODO Auto-generated method stub
			
		}
}
