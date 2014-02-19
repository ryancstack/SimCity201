package restaurant.nakamuraRestaurant.helpers;

import restaurant.nakamuraRestaurant.interfaces.Customer;
import restaurant.nakamuraRestaurant.interfaces.Waiter;

public class Check {
	public enum state {pending, delivered, shortChange, paid, debt};
	
	Waiter w;
	Customer c;
	state s;
	String choice;
	double total;
	double change;

	public Check(Waiter w, Customer c, String choice, double total) {
		this.c = c;
		this.w = w;
		this.choice = choice;
		this.total = total;
		this.change = 0;
		s = state.pending;
	}

	public void setState(state s) {
		this.s = s;
	}
	
	public String getChoice() {
		return choice;
	}
	
	public void setTotal(double price) {
		total += price;
	}
	
	public double getTotal() {
		return total;
	}

	public state getState() {
		return s;
	}
	
	public Customer getCustomer() {
		return c;
	}
	
	public Waiter getWaiter() {
		return w;
	}
	
	public double getChange() {
		return change;
	}
}
