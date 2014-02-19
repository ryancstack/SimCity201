package restaurant.stackRestaurant.helpers;

import restaurant.stackRestaurant.interfaces.Customer;

public class Check {
	private double cost;
	private Customer customer;
	private String choice;
	
	public Check(double cost, Customer customer, String choice) {
		this.cost = cost;
		this.choice = choice;
		this.customer = customer;
	}
	
	public Check(double cost, String choice) {
		this.cost = cost;
		this.choice = choice;
	}
	
	
	public double cost() {
		return cost;
	}
	
	public Customer getCustomer() {
		return customer;
	}
	
	public String getChoice() {
		return choice;
	}

}
