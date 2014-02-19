package restaurant;

import gui.CurrentBuildingPanel;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

import restaurant.stackRestaurant.ProducerConsumerMonitor;

public class Restaurant {
	
	private boolean isOpen = true;
	private Map<String, FoodInformation> foodInventory = Collections.synchronizedMap(new HashMap<String, FoodInformation>());
	CurrentBuildingPanel restPanel;
	

	protected ProducerConsumerMonitor monitor;
	double till = 1000;
	
	public double getTill() {
		return till;
	}

	public void setTill(double till) {
		if(restPanel != null) {
			restPanel.msgChangeTillInformation(till);
		}
		this.till = till;
	}

	public Restaurant() {
		monitor = new ProducerConsumerMonitor();
	}
	
	public Object getHost() {
		return null;
	}
	
	public Object getCashier() {
		return null;
	}
	
	public String getName() {
		return "";
	}
	
	public ProducerConsumerMonitor getMonitor() {
		return monitor;
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
		}
		foodInventory.get(type).setQuantity(quantity);
	}
	
	public void msgSetOpen() {
		setOpen(true);
	}
	
	public void msgSetClosed() {
		setOpen(false);
	}
	
	public Map<String, FoodInformation> getFoodInventory() {
		return foodInventory;
	}

	public JPanel getInfoPanel() {
		return restPanel;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public void setInfoPanel(CurrentBuildingPanel restPanel) {
		this.restPanel = restPanel;
	}

	public Object getMyMonitor() {
		// TODO Auto-generated method stub
		return null;
	}

}
