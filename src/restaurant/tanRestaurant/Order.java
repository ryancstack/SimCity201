package restaurant.tanRestaurant;

import restaurant.tanRestaurant.interfaces.Waiter;



public class Order {
	
		Order(TanWaiterRole waiter, String choice, int table, int seat) {
			this.waiter = waiter;
			this.choice = choice;
			this.table = table;
			this.seat = seat;
		}
		
		TanWaiterRole waiter;
		String choice;
		int table;
		int seat;
		public String getName() {
			return choice;
		}
}

