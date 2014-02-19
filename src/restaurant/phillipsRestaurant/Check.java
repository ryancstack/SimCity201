package restaurant.phillipsRestaurant;

import java.util.ArrayList;

import restaurant.*;
import restaurant.phillipsRestaurant.PhillipsCashierAgent.OrderState;
import restaurant.phillipsRestaurant.interfaces.*;

public class Check{
	//Check for a customer
	public int tableNum;
	public double moneyOwed;
	public OrderState state;
	public Waiter waiter;
	
	//Check for paying a market
	public MarketAgent market = null;
	
	public Check(double money,OrderState s,MarketAgent m){
		market = m;
		moneyOwed = money;
		state = s;
	}
	public Check(int table, double mon, OrderState s,Waiter w){
		tableNum = table;
		moneyOwed = mon;
		state = s;
		waiter = w;
	}
}
