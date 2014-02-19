package restaurant.huangRestaurant.test.mock;


import restaurant.huangRestaurant.Check;
import restaurant.huangRestaurant.interfaces.Cashier;
import restaurant.huangRestaurant.interfaces.Customer;
import restaurant.huangRestaurant.interfaces.Waiter;

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

	public MockWaiter(String name) {
		super(name);
	}
	@Override
	public void msgWantsBreak() {
		
	}
	@Override
	public void msgGoOnBreak() {
		
	}
	@Override
	public void msgNoBreak() {
		
	}
	@Override
	public void msgPleaseSeatCustomer(Customer c, int table) {
		
	}
	@Override
	public void msgReadyToOrder(Customer c) {
		
	}
	@Override
	public void msgHereIsMyChoice(Customer c, String choice) {
		
	}
	@Override
	public void msgOrderDone(String choice, int table) {
		
	}
	@Override
	public void msgLeavingTable(Customer c) {
		
	}
	@Override
	public void msgOutOfChoice(String choice, int table) {
		
	}
	@Override
	public void msgDoneEating(Customer c) {
		
	}
	@Override
	public void msgGetCheck(Check cx) {
		
	}
	@Override
	public void msgHereIsCheck(Check cx) {
		
	}
}
