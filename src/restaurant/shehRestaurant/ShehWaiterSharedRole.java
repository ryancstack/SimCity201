package restaurant.shehRestaurant;

import restaurant.shehRestaurant.ShehWaiterRole.CustomerState;
import restaurant.shehRestaurant.helpers.Order;

public class ShehWaiterSharedRole extends ShehWaiterRole {

	public ShehWaiterSharedRole(String location) {
		super(location);
	}
	
	protected void CookThisOrder(myCustomer c) {
		print("Adding " + c.c + "'s order to shared data for cook");
		c.s = CustomerState.Waiting;
		
		restaurant.getMyMonitor().insert(new Order(this, c.o, c.t.getTableNumber()));
	}
}
