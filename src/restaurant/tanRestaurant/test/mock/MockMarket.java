package restaurant.tanRestaurant.test.mock;


import restaurant.tanRestaurant.interfaces.Cashier;
import restaurant.tanRestaurant.interfaces.Customer;
import restaurant.tanRestaurant.interfaces.Waiter;
import restaurant.tanRestaurant.TanCashierAgent.Bill;
import restaurant.tanRestaurant.MarketAgent.MarketBill.billState;
import restaurant.tanRestaurant.gui.CustomerGui;
import restaurant.tanRestaurant.interfaces.Market;
import restaurant.tanRestaurant.TanCashierAgent;
import restaurant.tanRestaurant.test.mock.EventLog;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockMarket extends Mock implements Market {

	public EventLog log = new EventLog();
	
	/**
	 * Reference to the Cashier under test that can be set by the unit test.
	 */

	public Cashier cashier;
	
	public MockMarket(String name) {
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
	public void msgHereIsPayment(double payment, double debt){
		log.add(new LoggedEvent("Received msgHereIsPayment from cashier. Payment="+ payment+". Debt ="+ debt));
	}
	
	/*
	@Override
	public void msgHereIsYourChange(double change, double debt) {
		log.add(new LoggedEvent("Received HereIsYourChange from cashier. Change = "+ change +". Debt = " + debt));

	}*/

	/*
	@Override
	public void YouOweUs(double remaining_cost) {
		log.add(new LoggedEvent("Received YouOweUs from cashier. Debt = "+ remaining_cost));
	}*/

}
