package restaurant;

import market.MarketCheck;
import agent.Agent;

public abstract class CashierAgent extends Agent{
	
	public abstract void msgGiveBill(MarketCheck marketcheck);
}
