package restaurant.huangRestaurant.interfaces;


import restaurant.huangRestaurant.Check;




/**
 * A sample Waiter interface built to unit test a CashierAgent.
 *
 * @author Alex Huang
 *
 */
public interface Waiter {
	public void msgWantsBreak();
	public void msgGoOnBreak();
	public void msgNoBreak();
	public void msgPleaseSeatCustomer(Customer c, int table); 
	public void msgReadyToOrder(Customer c);
	public void msgHereIsMyChoice(Customer c, String choice); 
	public void msgOrderDone(String choice, int table);
	public void msgLeavingTable(Customer c);
	public void msgOutOfChoice(String choice, int table);
	public void msgDoneEating(Customer c);
	public void msgGetCheck(Check cx);
	public void msgHereIsCheck(Check cx);


}