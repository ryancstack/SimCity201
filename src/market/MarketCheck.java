package market;

import java.util.List;
import market.interfaces.MarketWorker;

public class MarketCheck {
	private double cost;
	private String choice;
	private List<String> choices;
	private int amount;
	private MarketWorker market;


	public MarketCheck(double cost, String choice, int amount, MarketWorker market) {
		this.cost = cost;
		this.choice = choice;
		this.amount = amount;
		this.market = market;
	}


	public MarketCheck(double cost, List<String> choices, int amount, MarketWorker market) {
		this.cost = cost;
		this.choices = choices;
		this.amount = amount;
		this.market = market;
	}


	public MarketWorker getMarket() {
		return market;
	}

	public double getCost() {
		return cost;
	}

	public String getChoice() {
		return choice;
	}
	
	public List<String> getChoices() {
		return choices;
	}
	
	public int getAmount() {
		return amount;
	}
}
