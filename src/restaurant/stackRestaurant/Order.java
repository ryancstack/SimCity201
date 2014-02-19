package restaurant.stackRestaurant;

import restaurant.stackRestaurant.interfaces.Waiter;

public class Order {
	
		public Order(Waiter waiter, String choice, int table, int seat) {
			this.waiter = waiter;
			this.choice = choice;
			this.table = table;
			this.seat = seat;
		}
		
		Waiter waiter;
		String choice;
		int table;
		int seat;
}

