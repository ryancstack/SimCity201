package bank.test.mock;

import home.interfaces.Landlord;

import java.util.Map;

import city.interfaces.Person;
import agent.Role;
import bank.interfaces.BankTeller;
import bank.interfaces.BankCustomer;
import bank.interfaces.BankManager;

public class MockPerson extends Mock implements Person {

	public BankTeller teller;
	public BankManager manager;
	public BankCustomer customer;
	
	public EventLog log;
	public Map<String, Integer> groceryList;
	public double funds;

	public MockPerson(String name) {
		super(name);
		log = new EventLog();
	}

	@Override
	public double getFunds() {
		log.add(new LoggedEvent("Get funds"));
		return funds;
	}

	@Override
	public void setFunds(double funds) {
		log.add(new LoggedEvent("Set funds to $" + funds));
		this.funds = funds;		
	}
	
	@Override
	public void msgRoleFinished() {
		log.add(new LoggedEvent("msgRoleFinished called"));
	}

	@Override
	public void stateChanged() {
	}

	@Override
	public void print(String msg) {
	}

	@Override
	public void Do(String msg) {
	}

	@Override
	public String getTransportationMethod() {
		return null;
	}

	@Override
	public void setAccountNumber(int accountNumber) {
		log.add(new LoggedEvent("Set account number"));
	}

	@Override
	public int getAccountNumber() {
		return 0;
	}

	@Override
	public void msgTransportFinished(String currentLocation) {
		
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
