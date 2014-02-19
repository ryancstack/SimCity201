package restaurant.tanRestaurant.interfaces;
import restaurant.tanRestaurant.Order;
import restaurant.tanRestaurant.TanCashierAgent.Bill;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer;
import restaurant.tanRestaurant.test.mock.EventLog;

public interface Cook {

	public EventLog log = new EventLog();
	
	public abstract void msgHereIsBill(Bill b);

	void PassOrderToCook(int table, MyCustomer myc, Order o);
}
