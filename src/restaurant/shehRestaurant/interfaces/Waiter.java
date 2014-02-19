package restaurant.shehRestaurant.interfaces;

import restaurant.shehRestaurant.helpers.Bill;

public interface Waiter {


	public abstract void msgCollectBill(Bill b);


	/**
	 * @param remaining_cost how much money is owed
	 * Sent by the cashier if the customer does not pay enough for the bill (in lieu of sending {@link #HereIsYourChange(double)}
	 */
}