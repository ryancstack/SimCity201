package restaurant.huangRestaurant.interfaces;

import restaurant.huangRestaurant.FoodBill;





/**
 * A sample Market interface built to unit test a CashierAgent.
 *
 * @author Alex Huang
 *
 */
public interface Market {
	public void msgCancelOrder(String request); 
	public void msgPlsDeliverRequest(String request);;
	public void msgWhatIsYourStockState(String checkFood, int requirement); 
	public void msgHereIsPayment(FoodBill b); 
	public void msgCannotPay(FoodBill b);
}
