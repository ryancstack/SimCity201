package restaurant.stackRestaurant.test.mock;



import restaurant.stackRestaurant.helpers.Check;
import restaurant.stackRestaurant.interfaces.Cashier;
import restaurant.stackRestaurant.interfaces.Customer;
import restaurant.stackRestaurant.interfaces.Waiter;
import restaurant.stackRestaurant.test.mock.LoggedEvent;
import restaurant.stackRestaurant.test.mock.EventLog;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */
	public Cashier cashier;

	public MockCustomer(String name) {
		super(name);

	}

	public void msgGotHungry() {//from animation
		log.add(new LoggedEvent("Got hungry"));
	}
	
	public void msgRestaurantFull() {

	}

	public void msgSitAtTable(Waiter waiter, int table) {
		log.add(new LoggedEvent("Received direction to sit at table " + table));
	}
	
	public void msgHereToTakeOrder() {
		log.add(new LoggedEvent("Waiter is here to take order"));
	}
	
	public void msgHereIsCheck(Check check) {
		log.add(new LoggedEvent("Received the check for " + check.cost()));
	}
	
	public void msgReorder() {
		log.add(new LoggedEvent("Told to reorder"));
	}
	
	public void msgHereIsFood() {
		log.add(new LoggedEvent("ReceivedFood"));
	}
	
	public void msgHereIsChange(double change) {
		log.add(new LoggedEvent("Received " + change + " in change"));
	}

	public void msgAnimationFinishedGoToSeat() {
		log.add(new LoggedEvent("Finished going to seat"));
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		log.add(new LoggedEvent("Finished leaving restaurant"));
	}
	public void msgAnimationFinishedGoToCashier() {
		log.add(new LoggedEvent("Finished goign to cashier"));
	}
	public void setFunds(double funds) {
		log.add(new LoggedEvent("Funds are set to" + funds));
	}

	@Override
	public void msgRestaurantClosed() {
		// TODO Auto-generated method stub
		
	}
	
	
}
