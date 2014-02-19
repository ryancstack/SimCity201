package restaurant.huangRestaurant.interfaces;

import restaurant.CashierInterface;
import agent.Role;






/**
 * A sample Cashier interface built to unit test a CashierAgent.
 *
 * @author Alex Huang
 *
 */
public interface Cashier extends CashierInterface {
	public void msgHereIsCustomerDish(Waiter w, String type, int table, Customer c);
	public void msgHereIsMoney(Customer c);
	public void msgNotEnoughMoney(Customer c);
	public void msgAskForCheck(Customer c);
	public void msgAskForPayCheck(Role r);
}