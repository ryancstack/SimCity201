package city.test.mock;

import home.interfaces.Landlord;

import java.util.Map;

import agent.Role;
import city.PersonAgent.TransportationMethod;
import city.test.mock.EventLog;
import city.interfaces.Person;


public class MockPerson extends Mock implements Person {
	
	public enum TransportationMethod {OwnsACar, TakesTheBus, Walks};

	public EventLog log;
	public city.PersonAgent.TransportationMethod transMethod;

	public MockPerson(String name) {
		super(name);
		log = new EventLog();
		//String startingLocation=
	}

	//@Override
	public void clearGroceries() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public double getFunds() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setFunds(double funds) {
		log.add(new LoggedEvent("Setting funds to " + funds));
		
	}

	@Override
	public String getTransportationMethod() {
		return ("Bus");
	}

	@Override
	public void setAccountNumber(int accountNumber) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Setting account number to " + accountNumber));
		
	}

	@Override
	public int getAccountNumber() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void stateChanged() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void print(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void Do(String msg) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgRoleFinished() {
		log.add(new LoggedEvent("Popping current role, role finished received."));
		
	}

	@Override
	public void msgTransportFinished(String currentLocation) {
		log.add(new LoggedEvent("Transportation finished, updating location to: " + currentLocation));
		
	}

	@Override
	public void clearGroceries(Map<String, Integer> groceries) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPayRent(Landlord landlord, double moneyOwed) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCurrentDay() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getCurrentLocation() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addRole(Role t) {
		// TODO Auto-generated method stub
		
	}
	
}
