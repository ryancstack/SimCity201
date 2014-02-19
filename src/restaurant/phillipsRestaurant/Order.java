package restaurant.phillipsRestaurant;

import restaurant.phillipsRestaurant.interfaces.Waiter;

public class Order {
	
	Waiter waiter;
	String choice;
	int table;
	
	public Order(Waiter waiter, String choice, int table) {
			this.waiter = waiter;
			this.choice = choice;
			this.table = table;
	}
}

