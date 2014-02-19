package restaurant;

import restaurant.CustomerAgent.AgentEvent;
import restaurant.WaiterAgent.AgentState;
import restaurant.CookAgent;
import restaurant.gui.RestaurantGui;
import agent.Agent;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;
/**
 * Restaurant cook agent.
 */
public class MarketAgent extends Agent {
	
	public enum MarketState {haveInventory,outOfInventory,done};
//	public enum MarketEvent {none,restock};
	private Map<String,Integer> inv = new HashMap<String,Integer>(4);
	private String currentOrder;
	public double moneyInMarket = 0;
	private final int INVENTORY = 1; //HACK
	
	MarketState state = MarketState.done;
	CookAgent cook = null;


	/**
	 * Constructor for MarketAgent class
	 *
	 */
	public MarketAgent(){
		super();
		inv.put("steak",INVENTORY);
		inv.put("chicken",INVENTORY);
		inv.put("salad",INVENTORY);
		inv.put("pizza",INVENTORY);
	}
	public void setCook(CookAgent c){
		cook = c;
	}
	
	// Messages
	public void msgNeedFoodFromMarket(String order){
		currentOrder = order;
		for (Map.Entry<String, Integer> entry : inv.entrySet()) { 
			if(entry.getKey() == order){
				if(entry.getValue() > 0){
					state = MarketState.haveInventory;
				}
				else{
					state = MarketState.outOfInventory;
				}
			} 
		}
		stateChanged();
	}
	public void msgHereIsCheck(double money){
		moneyInMarket += money;
		Do("Market received $" + money + " and has total: $" + moneyInMarket);
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		
		if(state == MarketState.haveInventory){
			restockCook();
			return true;
		}
		if(state == MarketState.outOfInventory){
			tellCookNoInventory();
			return true;
		}
		return false;    
	}

	// Actions

	public void restockCook(){
		for (Map.Entry<String, Integer> entry : inv.entrySet()) { 
			if(entry.getKey() == currentOrder){
				Do("Market restocking cook with " + entry.getValue() + " " + currentOrder);
				cook.msgCookHereIsInventory(currentOrder,entry.getValue(),this);
				entry.setValue(0);
			}
		} 
		state = MarketState.done;
		//orders.remove(0);
	}
	
	public void tellCookNoInventory(){
		Do("Market has no inventory left");
		cook.msgCookNoInventory(currentOrder, this);
		state = MarketState.done;
	}

}