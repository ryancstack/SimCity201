package restaurant.stackRestaurant;

import trace.AlertLog;
import trace.AlertTag;
import city.helpers.Directory;

public class StackWaiterSharedRole extends StackWaiterRole {

	public StackWaiterSharedRole(String location) {
		super(location);
	}
	
	@Override
	protected void takeOrderToCook(MyCustomer customer) {
		//do go to stuff;
		host.msgWaiterBusy(this);
		AlertLog.getInstance().logMessage(AlertTag.WAITER, getName(),"Adding " + customer.customer + "'s order to shared data for cook");
		customer.state = CustomerState.AtCook;
		Directory.sharedInstance().getRestaurants().get(0).getMonitor().insert(new Order(this, customer.choice, customer.table, customer.seatNum));;
	}

}
