package city;

import city.helpers.Directory;
import city.helpers.DrivewayHelper;
import city.interfaces.Car;
import city.interfaces.Vehicle;
import city.interfaces.Person;
import city.interfaces.Transportation;
import agent.Agent;
import city.gui.CarGui;
import city.TrafficAgent;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class CarAgent extends Agent implements Vehicle {
	
    /**
	*Data
	*/
	private Transportation passenger;
	CarGui carGui = null;
	
	public enum carState
	{inTransit, atDestination, Idle, stoppingAtIntersection, atIntersection, atIntersection1, atIntersection2, givenGreen, stoppedAtIntersection, continueDriving}
	public carState currentState = carState.Idle;
	
	int midOfScreen= 416;
	
	TrafficAgent trafficLight1= Directory.sharedInstance().getCityGui().getTrafficLight1();
	TrafficAgent trafficLight2= Directory.sharedInstance().getCityGui().getTrafficLight2();
	private String destination;
	private String currentLocation;
	private Semaphore driving = new Semaphore(0,true);
	
	String intersection= null;
	TrafficAgent currentLight= null;
	boolean needsTurn= false;
	String side=null;            //are these two necessary for agent or is it sufficient to do it in carGui?
	
	/*public enum destinationSide
	{eastSide, westSide,none}
	public destinationSide side = destinationSide.none;*/
	
	CarAgent(String currentLocation) {
		carGui= new CarGui(this, currentLocation);
		if(getX(currentLocation)<midOfScreen){
			side= "westSide";
		}
		else side= "eastSide";
		
	}
		
	/**
	*Scheduler
	*/	
		protected boolean pickAndExecuteAnAction(){
			if (currentState == carState.inTransit){
				goTo(destination);
				return true;
			}
			
			else if(currentState == carState.atIntersection1 || currentState == carState.atIntersection2){
				stopCar();
				return true;
			}
		
			else if(currentState == carState.givenGreen){
				keepDriving();
				return true;
			}
					
			else if (currentState == carState.atDestination){
				parkCar();
				return true;
			}
			
			else if (currentState == carState.Idle){
				return true;//do nothing
			}
			return false;
		}
		
	/**
	 * Messages
	 * @param myDestination
	 */
		public void msgTakeMeHere(Transportation Passenger, String myDestination){ //receives msg from passenger
			passenger= Passenger;
			destination = myDestination;
			
			currentState = carState.inTransit;
			stateChanged();
		}
		
		public void msgAtDestination(){ //from carGui when reached destination
			driving.release();
			currentLocation= destination;
			currentState = carState.atDestination;
			stateChanged();
		}

		public void msgAtIntersection1() {
			// TODO Auto-generated method stub
			trafficLight1.msgAtIntersection(this);
			currentState = carState.atIntersection1;
			driving.release();
			stateChanged();
		}
		
		public void msgAtIntersection2() {
			// TODO Auto-generated method stub
			trafficLight2.msgAtIntersection(this);
			currentState = carState.atIntersection2;
			driving.release();
			stateChanged();
		}
		
		public void msgGreenLight(TrafficAgent ta){
			currentLight= ta;
			currentState = carState.givenGreen;
			stateChanged();
		}
		
	/**
	 * Actions	
	 * @param myDestination
	 */
		private void keepDriving() {
			currentLight.msgRemoveFromIntersection(this);
			currentState = carState.continueDriving;
			carGui.DoKeepDriving();
		}

		
		private void goTo(String myDestination){

			doGoTo(myDestination); //sets destination in carGui
			try {
				driving.acquire(); //to ensure that the gui is uninterrupted on the way
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
		}
		
		private void doGoTo(String myDestination){
			carGui.DoGoTo(myDestination);
		}
		
		private void parkCar(){
		//msg gives passenger the destination so its gui can reappear at an appropriate place
			passenger.msgArrivedAtDestination(destination);
			carGui.DoParkCar();
			currentState = carState.Idle;
		}
		
		private void stopCar(){
			carGui.DoStopCar();
			currentState = carState.stoppedAtIntersection;
			stateChanged();
		}
		
		/**
		 * Utilities
		 */
		public int getX(String Destination){
			return DrivewayHelper.sharedInstance().locationDirectory.get(Destination).xCoordinate;
		}
		
		public int getY(String Destination){
			return DrivewayHelper.sharedInstance().locationDirectory.get(Destination).yCoordinate;
		}
		
		public void setGui(CarGui gui){
			carGui = gui;
		}
		
		public void removeGui(){
			carGui= null;
		}

		@Override
		public void msgGreenLight() {
			// TODO Auto-generated method stub
			
		}


}
