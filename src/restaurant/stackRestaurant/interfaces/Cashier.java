package restaurant.stackRestaurant.interfaces;

import agent.Role;
import restaurant.CashierInterface;
import restaurant.stackRestaurant.helpers.Check;
import market.MarketCheck;
import market.interfaces.*;

public interface Cashier extends CashierInterface{
	
	public abstract void msgComputeCheck(Waiter waiter, Customer cust, String choice);
	
	public abstract void msgPayCheck(Customer cust, Check check, double money);
	
	public abstract void msgNeedPaycheck(Role role);

}
