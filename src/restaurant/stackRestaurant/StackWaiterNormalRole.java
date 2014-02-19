package restaurant.stackRestaurant;

import trace.AlertLog;
import trace.AlertTag;


public class StackWaiterNormalRole extends StackWaiterRole {
	
	public StackWaiterNormalRole(String location) {
		super(location);
	}

	@Override
	protected void takeOrderToCook(MyCustomer customer) {
		host.msgWaiterBusy(this);
		DoGoToCook();
		AlertLog.getInstance().logMessage(AlertTag.WAITER, getName(),"Taking " + customer.customer + "'s order to cook");
		try {
			doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		customer.state = CustomerState.AtCook;
		cook.msgCookOrder(this, customer.choice, customer.table, customer.seatNum);
	}
}
