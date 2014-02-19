package restaurant.nakamuraRestaurant;

import restaurant.nakamuraRestaurant.helpers.Order;

public class NakamuraWaiterSharedRole extends NakamuraWaiterRole {

	public NakamuraWaiterSharedRole(String location) {
		super(location);
	}
	
	@Override
	protected void PlaceOrder(Cust customer) {
		print("Adding " + customer.c + "'s order to shared data for cook");
		customer.s = state.waitingforfood;
		
		restaurant.getMyMonitor().insert(new Order(this, customer.choice, customer.tableNumber));
	}
}
