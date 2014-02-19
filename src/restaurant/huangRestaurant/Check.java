package restaurant.huangRestaurant;

import restaurant.huangRestaurant.interfaces.Customer;
enum CheckState {atCashier, atTable};
public class Check {
	public String order;
	public Customer c;
	public int table;
	public double price;
	public CheckState state;
	Check(Customer c, String choice, double price, int table) {
		this.c = c;
		this.order = choice;
		this.price = price;
		this.state = CheckState.atCashier;
	}
}
