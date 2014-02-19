package restaurant.shehRestaurant.interfaces;

import java.util.Vector;
import restaurant.shehRestaurant.helpers.Bill;

public interface Market {

	void msgHereIsPayment(Bill b);

	void msgOrderForReplenishment(Vector<String> lowItems, Cook cookAgent,
			Cashier cashier);

}