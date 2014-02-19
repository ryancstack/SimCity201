package restaurant.stackRestaurant.interfaces;

import restaurant.stackRestaurant.helpers.Check;


public interface Customer {
	
	public void msgGotHungry();
	
	public void msgRestaurantFull();

	public void msgSitAtTable(Waiter waiter, int table);
	
	public void msgHereToTakeOrder();
	
	public void msgHereIsCheck(Check check);
	
	public void msgReorder();
	
	public void msgHereIsFood();
	
	public void msgHereIsChange(double change);

	public void msgAnimationFinishedGoToSeat();
	
	public void msgAnimationFinishedLeaveRestaurant();
	
	public void msgAnimationFinishedGoToCashier();
	
	public void setFunds(double funds);

	public void msgRestaurantClosed();
}