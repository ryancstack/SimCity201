package restaurant.shehRestaurant.interfaces;

import restaurant.shehRestaurant.test.mock.MockCashier;
import restaurant.shehRestaurant.helpers.Bill;

public interface Customer {
	/**
	 * Input messages here that Customer receives.
	 * What does this accomplish though?
	 * Is this just supposed to be empty?
	 */
	public abstract void msgHereIsYourBill(Bill b, Cashier c);

	/**
	 * @param b change (if any) due to the customer
	 *
	 * Sent by the cashier to end the transaction between him and the customer. total will be >= 0 .
	 */
	public abstract void msgHereIsYourChange(Bill b);


	/**
	 * @param remaining_cost how much money is owed
	 * Sent by the cashier if the customer does not pay enough for the bill (in lieu of sending {@link #HereIsYourChange(double)}
	 */
}