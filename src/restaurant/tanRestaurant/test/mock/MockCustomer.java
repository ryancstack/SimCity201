package restaurant.tanRestaurant.test.mock;


import restaurant.tanRestaurant.interfaces.Cashier;
import restaurant.tanRestaurant.interfaces.Customer;
import restaurant.tanRestaurant.test.mock.EventLog;

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
	//double debt =0;
	
	public EventLog log = new EventLog();

	public MockCustomer(String name) {
		super(name);

	}

	/*
	@Override
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
	*/
	
	@Override
	public void msgHereIsYourChange(double change, double debt) {
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ change +". Debt = " + debt));

	}

	/*
	@Override
	public void YouOweUs(double remaining_cost) {
		log.add(new LoggedEvent("Received YouOweUs from cashier. Debt = "+ remaining_cost));
	}*/

}
