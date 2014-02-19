package restaurant.stackRestaurant.interfaces;

import market.interfaces.*;
import restaurant.CookInterface;
import restaurant.Restaurant;


public interface Cook extends CookInterface{
	
	public void msgCookOrder(Waiter waiter, String choice, int table, int seat);
	
	public void msgInventoryOut(MarketWorker market, String choice);
	
	public void msgMarketDeliveringOrder(int inventory, String choice);
	
	public void msgAddMarket(MarketWorker market);
	
	public void msgAtCooktop();

	public void msgAtPlating();
	
	public void msgAtFridge();

	public void setRestaurant(Restaurant restaurant);

}
