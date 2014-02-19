package restaurant.stackRestaurant.test.mock;

import restaurant.Restaurant;
import restaurant.stackRestaurant.helpers.Check;
import restaurant.stackRestaurant.interfaces.Cook;
import restaurant.stackRestaurant.interfaces.Customer;
import restaurant.stackRestaurant.interfaces.Waiter;

public class MockWaiter extends Mock implements Waiter {

	public MockWaiter(String name) {
		super(name);
	}
	
	public void msgHereIsCheck(Check check) {
		log.add(new LoggedEvent("Received check from waiter for " + check.getChoice() + " costing " + check.cost()));
	}
	
	public void msgCheckPlease(Customer customer) {
		log.add(new LoggedEvent(customer.toString() + " asked for the check"));
	}
	
	public void msgYouCanGoOnBreak(boolean canGoOnBreak) {
		if(canGoOnBreak)
			log.add(new LoggedEvent("I can go on break"));
		else
			log.add(new LoggedEvent("I cannot go on break"));
	}
	
	public void msgReadyToOrder(Customer customer) {
		log.add(new LoggedEvent(customer + " is ready to order"));
	}
	
	public void msgGiveOrder(Customer customer, String choice) {
		log.add(new LoggedEvent(customer + " ordered " + choice));
	}
	
	public void msgSeatCustomer(Customer customer, int tableNumber, int seatNumber) {
		log.add(new LoggedEvent("Seat " + customer + " at table " + tableNumber));
	}
	
	public void msgOrderDone(String choice, int table, int seat) {
		log.add(new LoggedEvent(choice + " is done cooking. Please deliver to table " + table));
	}
	
	public void msgDoneEating(Customer customer) {
		log.add(new LoggedEvent(customer + " is done eating"));
	}
	
	public void msgFoodEmpty(String choice, int table, int seat) {
		log.add(new LoggedEvent("The person at table " + table + "' s order of " + choice + "is empty"));
	}
	
	public void msgIWantToGoOnBreak() {
		log.add(new LoggedEvent("The waiter gui has asked to go on break"));
	}
	
	public void msgImComingOffBreak() {
		log.add(new LoggedEvent("The waiter gui is coming off break"));
	}

	public void msgCookHere(Cook cook) {
		log.add(new LoggedEvent("The cook has arrive. Rejoice"));
		
	}

	public void setRestaurant(Restaurant restaurant) {
		log.add(new LoggedEvent("Added restaurant"));
		
	}

}
