package restaurant.phillipsRestaurant.test.mock;


import restaurant.phillipsRestaurant.*;
import restaurant.phillipsRestaurant.interfaces.*;

/**
 * A sample MockCustomer built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public class MockCustomer extends Mock implements Customer {

	public MockCustomer(String name) {
        super(name);

}
	
	@Override
	public void gotHungry() {
		log.add(new LoggedEvent("Customer got hungry"));
		
	}
	
	@Override
	public void msgFollowMe(Waiter w, int tNum, Menu menu) {
		log.add(new LoggedEvent("Customer following waiter to table " + tNum));
		
	}

	@Override
	public void msgWhatDoYouWant() {
		log.add(new LoggedEvent("Customer was asked what he wants to order"));
		
	}

	@Override
	public void msgEatMeal() {
		log.add(new LoggedEvent("Customer eating meal"));
		
	}

	@Override
	public void msgYouMayPay(double money) {
		log.add(new LoggedEvent("Customer going to pay "+ money));
		
	}

	@Override
	public void msgYouMayLeave() {
		log.add(new LoggedEvent("Customer is ready to leave"));
		
	}

	@Override
	public void msgAnimationFinishedGoToSeat() {

		
	}

	@Override
	public void msgAnimationFinishedLeaveRestaurant() {
		log.add(new LoggedEvent("Customer has left restaurant"));
		
	}

	@Override
	public String getCustomerName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void msgAtCashier() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setWaiter(Waiter waiter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setTableNum(int tableNumber) {
		// TODO Auto-generated method stub
		
	}

}
