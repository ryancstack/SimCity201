package city.interfaces;

import home.interfaces.Landlord;

import java.util.Map;

import agent.Role;

public interface Person{

	public void clearGroceries(Map<String, Integer> groceries);
	public double getFunds();
	public void setFunds(double funds);
	public String getName();
	public String getTransportationMethod();
	public void setAccountNumber(int accountNumber);
	public int getAccountNumber();
	
	public void stateChanged();
	public void print(String msg);
	public void Do(String msg);

	public void msgRoleFinished();
	public void msgTransportFinished(String currentLocation);
	public void msgPayRent(Landlord landlord, double moneyOwed);
	public int getCurrentDay();
	public String getCurrentLocation();
	public void addRole(Role t);
}
