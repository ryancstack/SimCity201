package restaurant.huangRestaurant.test.mock;


import restaurant.huangRestaurant.Check;
import restaurant.huangRestaurant.Menu;
import restaurant.huangRestaurant.HuangWaiterRole;
import restaurant.huangRestaurant.gui.CustomerGui;
import restaurant.huangRestaurant.interfaces.Cashier;
import restaurant.huangRestaurant.interfaces.Customer;
import restaurant.huangRestaurant.interfaces.Waiter;

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
	public Waiter waiter;

	public MockCustomer(String name) {
		super(name);

	}
	@Override
	public void msgRestaurantFull() {
		
	}
	@Override
	public void msgGetOut() {
		
	}
	@Override
	public void msgOutOfChoice() {
		
	}
	@Override
	public void msgHereIsYourCheck(Cashier ca, Check cx) {
		
	}
	@Override
	public void msgFollowMe(HuangWaiterRole w, Menu m, int table) {
		
	}
	@Override
	public void msgWhatDoYouWant() {
		
	}
	@Override
	public void msgHereIsYourFood() {
		
	}
	@Override
	public void msgAnimationFinishedPay() {
		
	}
	@Override
	public void msgAnimationFinishedGoToSeat() {
		
	}
	@Override
	public void msgAnimationFinishedLeaveRestaurant() {
		
	}
	@Override
	public CustomerGui getGui() {
		return null;
		
	}
	/*@Override
	public void HereIsYourTotal(double total) {
		log.add(new LoggedEvent("Received HereIsYourTotal from cashier. Total = "+ total));

		if(this.name.toLowerCase().contains("thief")){
			//test the non-normative scenario where the customer has no money if their name contains the string "theif"
			cashier.IAmShort(this, 0);

		}else if (this.name.toLowerCase().contains("rich")){
			//test the non-normative scenario where the customer overpays if their name contains the string "rich"
			cashier.HereIsMyPayment(this, Math.ceil(total));

		}else{
			//test the normative scenario
			cashier.HereIsMyPayment(this, total);
		}
	}

	@Override
	public void HereIsYourChange(double total) {
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ total));
	}

	@Override
	public void YouOweUs(double remaining_cost) {
		log.add(new LoggedEvent("Received YouOweUs from cashier. Debt = "+ remaining_cost));
	}*/

}
