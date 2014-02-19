package restaurant.nakamuraRestaurant.test.mock;

import restaurant.nakamuraRestaurant.interfaces.Cashier;
import restaurant.nakamuraRestaurant.interfaces.Market;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockMarket extends Mock implements Market {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;
	public EventLog log;

	public MockMarket(String name) {
		super(name);
		log = new EventLog();

	}

	@Override
	public void msgHeresPayment(double payment) {
		log.add(new LoggedEvent("Received msgHeresMarket from cashier. Total = $" + payment));
	}
}
