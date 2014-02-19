package restaurant;

import java.util.List;
import market.interfaces.MarketWorker;

public interface CookInterface {
	public void msgMarketDeliveringOrder(int supply, String choice);
	
	public void msgMarketDeliveringOrder(int supply, List<String> choices);
	
	public void msgInventoryOut(MarketWorker market, String choice);
	
	public void msgInventoryOut(MarketWorker market, List<String> choices, int amount);

	public void msgCanFillOrder(MarketWorker market, String choice);
}
