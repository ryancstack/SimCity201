package restaurant.nakamuraRestaurant.test.mock;

import restaurant.nakamuraRestaurant.helpers.Check;
import restaurant.nakamuraRestaurant.interfaces.Cashier;
import restaurant.nakamuraRestaurant.interfaces.Customer;
import restaurant.nakamuraRestaurant.interfaces.Waiter;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockWaiter extends Mock implements Waiter {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public Customer customer;
	public EventLog log;

	public MockWaiter(String name) {
		super(name);
		log = new EventLog();
	}

	@Override
	public void msgCheckReady(Check check) {
		log.add(new LoggedEvent("Received msgCheckReady from cashier."));
		check.getCustomer().msgHeresCheck(check);
	}

}
