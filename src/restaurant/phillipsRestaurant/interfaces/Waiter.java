package restaurant.phillipsRestaurant.interfaces;

import restaurant.phillipsRestaurant.*;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Waiter {
	public abstract void msgSeatCustomerAtTable(Customer c, int table);
	public abstract void msgCustomerReadyToOrder(Customer c);
	public abstract void msgHereIsMyChoice(Customer cust, String choice);
	public abstract void msgWaiterOutOfFood(String order,int tableNum);
	public abstract void msgOrderReadyForPickup(String choice,int tablenum);
	public abstract void msgWantToPay(Customer cust);
	public abstract void msgPayFood(int table,double money);
	public abstract void msgLeavingTable(Customer cust);
	
	public abstract void msgAtHost();
	public abstract void msgAtCook();
	public abstract void msgAtCashier();
	public abstract void msgAtWaitingArea();
	public abstract void msgAtTable();
	public abstract int getCustomers();
}