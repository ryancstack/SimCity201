package restaurant.nakamuraRestaurant.interfaces;

import restaurant.CashierInterface;
import restaurant.nakamuraRestaurant.helpers.Check;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Cashier extends CashierInterface {
	public abstract void msgComputeCheck(Waiter w, Customer c, String choice);

	public abstract void msgPayment(Customer c, Check check, double payment);
}