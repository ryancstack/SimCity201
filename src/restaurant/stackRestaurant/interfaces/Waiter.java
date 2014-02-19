package restaurant.stackRestaurant.interfaces;

import restaurant.Restaurant;
import restaurant.stackRestaurant.helpers.Check;


public interface Waiter {

	public abstract void msgHereIsCheck(Check check);
	
	public abstract void msgCheckPlease(Customer customer);
	
	public abstract void msgYouCanGoOnBreak(boolean canGoOnBreak);
	
	public abstract void msgReadyToOrder(Customer customer);
	
	public abstract void msgGiveOrder(Customer customer, String choice);
	
	public abstract void msgSeatCustomer(Customer customer, int tableNumber, int seatNumber);
	
	public abstract void msgOrderDone(String choice, int table, int seat);
	
	public abstract void msgDoneEating(Customer customer);
	
	public abstract void msgFoodEmpty(String choice, int table, int seat);
	
	public abstract void msgIWantToGoOnBreak();
	
	public abstract void msgImComingOffBreak();

	public abstract void msgCookHere(Cook cook);

	public abstract void setRestaurant(Restaurant restaurant);
}
