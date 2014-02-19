package restaurant.nakamuraRestaurant.test.mock;

import restaurant.nakamuraRestaurant.helpers.Check;
import restaurant.nakamuraRestaurant.interfaces.Cashier;
import restaurant.nakamuraRestaurant.interfaces.Customer;

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
	public EventLog log;
	public Check check;

	public MockCustomer(String name) {
		super(name);
		log = new EventLog();

	}

/*	@Override
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
	public void msgHeresCheck(Check check) {
		log.add(new LoggedEvent("Received msgHeresCheck from waiter. Total = $" + check.getTotal()));
		this.check = check;
	}

	@Override
	public void msgHeresChange(double change) {
		log.add(new LoggedEvent("Received msgHeresChange from cashier. Change = $" + change));
	}

	@Override
	public void msgPayNextTime(double debt) {
		log.add(new LoggedEvent("Received msgPayNextTime from cashier. Debt = $" + debt));
	}

}
