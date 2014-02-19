package restaurant.phillipsRestaurant.test.mock;


import restaurant.phillipsRestaurant.*;
import restaurant.phillipsRestaurant.interfaces.*;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockWaiter extends Mock implements Waiter {

	public MockWaiter(String name) {
        super(name);

	}
	public void msgSeatCustomerAtTable(Customer c, int table){
		log.add(new LoggedEvent("Waiter seating customer " + c.getCustomerName() + " at table " + table));	
	}
	
	public void msgCustomerReadyToOrder(Customer c){
		log.add(new LoggedEvent("Waiter going to customer " + c.getCustomerName() + " to get his order"));
	}
	public void msgHereIsMyChoice(Customer cust, String choice){
		log.add(new LoggedEvent("Waiter received customer " + cust.getCustomerName() + "'s choice, which is " + choice));
	}
	public void msgWaiterOutOfFood(String order,int tableNum){
		log.add(new LoggedEvent("Waiter is told there is no more " + order));
		
	}
	public void msgOrderReadyForPickup(String choice,int tablenum){
		log.add(new LoggedEvent("Waiter is going to cook to pick up " + choice + " for table " + tablenum));
	}
	public void msgWantToPay(Customer cust){
		log.add(new LoggedEvent("Waiter is told customer " + cust.getCustomerName() + " wants to pay"));
	}
	public void msgPayFood(String name,double money){
		log.add(new LoggedEvent("Waiter getting cashier to compute check for " + name));
	}
	public void msgLeavingTable(Customer cust){
		log.add(new LoggedEvent("Customer " + cust + " leaving table"));
	}
	@Override
	public void msgAtHost() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgAtCook() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgAtCashier() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgAtWaitingArea() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void msgAtTable() {
		// TODO Auto-generated method stub
		
	}
	@Override
	public int getCustomers() {
		// TODO Auto-generated method stub
		return 0;
	}

}
