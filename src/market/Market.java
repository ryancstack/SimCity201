package market;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;


import gui.CurrentBuildingPanel;
import market.MarketWorkerRole;

public class Market {

	private String name;
	private MarketWorkerRole worker;
	private boolean open;
	double till;

	CurrentBuildingPanel restPanel;
	private Map<String, MarketItemInformation> marketInventory = Collections.synchronizedMap(new HashMap<String, MarketItemInformation>());
	
	public Market(String buildingName) {
		name = buildingName;
		open = false;
		
		marketInventory.put("Chicken", new MarketItemInformation("Chicken", 10, 1.00));
		marketInventory.put("Steak", new MarketItemInformation("Steak", 10, 2.00));
		marketInventory.put("Pizza", new MarketItemInformation("Pizza", 10, 3.00));
		marketInventory.put("Salad", new MarketItemInformation("Salad", 10, 4.00));
		marketInventory.put("Car", new MarketItemInformation("Salad", 10, 400.00));
	}
	
	public MarketWorkerRole getWorker() {
		return worker;
	}
	
	public void setWorker(MarketWorkerRole m) {
		worker = m;
	}
	
	public String getName() {
		return name;
	}
	
	public void setOpen() {
		open = true;
	}
	
	public void setClosed() {
		open = false;
	}
	
	public boolean isOpen() {
		return open;
	}

	public void msgChangeFoodInventory(String type, int quantity) {
		if(restPanel != null) {
			if(type.equals("Steak")) {
				restPanel.msgChangeSteakInventory(quantity);
			}
			else if(type.equals("Chicken")) {
				restPanel.msgChangeChickenInventory(quantity);
			}
			else if(type.equals("Salad")) {
				restPanel.msgChangeSaladInventory(quantity);
			}
			else if(type.equals("Pizza")) {
				restPanel.msgChangePizzaInventory(quantity);
			}
			else if(type.equals("Car")) {
				restPanel.msgChangeCarInventory(quantity);
			}
		}
		getFoodInventory().get(type).setSupply(quantity);
	}
	
	public Map<String, MarketItemInformation> getFoodInventory() {
		return marketInventory;
	}

	public JPanel getInfoPanel() {
		return restPanel;
	}

	public void setInfoPanel(CurrentBuildingPanel restPanel) {
		this.restPanel = restPanel;
	}

}