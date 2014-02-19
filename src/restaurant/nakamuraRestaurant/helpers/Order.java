package restaurant.nakamuraRestaurant.helpers;

import restaurant.nakamuraRestaurant.NakamuraWaiterRole;
import restaurant.nakamuraRestaurant.NakamuraCookRole.orderState;


public class Order {
	public NakamuraWaiterRole w;
	public int tableNumber;
	public orderState s;
	public String choice;

	public Order(NakamuraWaiterRole w, String choice, int tableNumber) {
		this.tableNumber = tableNumber;
		this.w = w;
		this.choice = choice;
		s = orderState.pending;
	}

	public orderState getState() {
		return s;
	}
}
