package restaurant.shehRestaurant;

import restaurant.shehRestaurant.ShehWaiterRole;


public class ShehWaiterNormalRole extends ShehWaiterRole {
	
	public ShehWaiterNormalRole(String location) {
		super(location);
	}

	protected void CookThisOrder(myCustomer c) {
		waiterGui.DoGoToKitchen(); //change to cooking area later
		try {
			atKitchen.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cook.msgCookThisOrder(this, c.o, c.t.getTableNumber(), cashier);
		c.s = CustomerState.Waiting;
		stateChanged();
	}
}
