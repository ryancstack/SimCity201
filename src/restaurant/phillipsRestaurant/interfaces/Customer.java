package restaurant.phillipsRestaurant.interfaces;

import restaurant.phillipsRestaurant.*;

/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	int tableNum = 0;
	public abstract void gotHungry();
	public abstract void msgFollowMe(Waiter w, int tNum, Menu menu);	
	public abstract void msgWhatDoYouWant();
	public abstract void msgEatMeal();
	public abstract void msgYouMayPay(double money);
	public abstract void msgYouMayLeave();
	public abstract void msgAnimationFinishedGoToSeat();
	public abstract void msgAnimationFinishedLeaveRestaurant();
	
	public abstract void msgAtCashier();
	public abstract void setWaiter(Waiter waiter);
	public abstract void setTableNum(int tableNumber);
}