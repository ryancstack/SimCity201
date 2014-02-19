package restaurant.phillipsRestaurant.interfaces;

import market.interfaces.*;
import restaurant.CookInterface;


public interface Cook extends CookInterface{

	void msgAtFridge();

	void msgAtCookingArea();

	void msgHereIsOrder(Waiter waiter, String choice,
			int table);


}
