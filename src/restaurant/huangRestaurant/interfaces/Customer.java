package restaurant.huangRestaurant.interfaces;

import restaurant.huangRestaurant.Check;
import restaurant.huangRestaurant.Menu;
import restaurant.huangRestaurant.HuangWaiterRole;
import restaurant.huangRestaurant.gui.CustomerGui;


/**
 * A sample Customer interface built to unit test a CashierAgent.
 *
 * @author Monroe Ekilah
 *
 */
public interface Customer {
	public void msgRestaurantFull();
	public void msgGetOut();
	public void msgOutOfChoice();
	public void msgHereIsYourCheck(Cashier ca, Check cx);
	public void msgFollowMe(HuangWaiterRole w, Menu m, int table);
	public void msgWhatDoYouWant();
	public void msgHereIsYourFood();
	public void msgAnimationFinishedPay();
	public void msgAnimationFinishedGoToSeat();
	public void msgAnimationFinishedLeaveRestaurant();
	public CustomerGui getGui();
}