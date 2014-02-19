package restaurant.shehRestaurant;

import gui.Building;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.Market;
import restaurant.CookRole;
import restaurant.FoodInformation.FoodState;
import restaurant.Restaurant;
import restaurant.shehRestaurant.gui.CookGui;
import restaurant.shehRestaurant.helpers.Bill;
import restaurant.shehRestaurant.helpers.FoodData;
import restaurant.shehRestaurant.helpers.Menu;
import restaurant.shehRestaurant.helpers.Order;
import restaurant.shehRestaurant.helpers.Order.OrderCookState;
import restaurant.shehRestaurant.interfaces.Cashier;
import restaurant.shehRestaurant.interfaces.Cook;
import city.helpers.Directory;

/**
 * Restaurant Cook Agent
 */
public class ShehCookRole extends CookRole implements Cook {
	private List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	Timer timer = new Timer();
	Menu menu = new Menu();
	private String name;
	Bill bill;
	private Semaphore atPlating = new Semaphore(0, true);
	private Semaphore atCooking = new Semaphore(0, true);
	
	public CookGui cookGui = null;
	public ShehHostAgent host;
	private Market market1, market2; 
	private Cashier cashier;
	private Restaurant restaurant;
	
	private enum AgentState 
	{NeedsToWork, Arrived, Working, GettingPaycheck, Leaving, WaitingForPaycheck};
	AgentState state = AgentState.NeedsToWork;

	public ShehCookRole(String location) {
		super();
		host = (ShehHostAgent) Directory.sharedInstance().getAgents().get("ShehRestaurantHost");
		cashier = (Cashier) Directory.sharedInstance().getAgents().get("ShehRestaurantCashier");
		//instantiate markets
		
		cookGui = new CookGui(this);
		market1 = Directory.sharedInstance().getMarkets().get(0);
		market2 = Directory.sharedInstance().getMarkets().get(1);
		
		restaurant = Directory.sharedInstance().getRestaurants().get(3);
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		
		for(Building b : buildings) {
			if (b.getName() == location) {
				b.addGui(cookGui);
			}
		}
		
		state = AgentState.Arrived;
	}
		
	/*	
	public ShehCookRole(String n, Market m1, Market m2) {
		super();

		name = n;
		market1 = m1;
		market2 = m2;
	}
	*/
	
	// tMessages
	public void msgCookThisOrder(ShehWaiterRole w, String o, int t, Cashier ca) {
		cashier = ca;
		
		print("Received order");
		orders.add(new Order(w, o, t, OrderCookState.Pending));
		stateChanged();
	}
	
	public void msgfoodDone(Order o) {
		o.cs = OrderCookState.Done;
		stateChanged();
	}
	
	public void msgMarketDeliveringOrder(int supply, List<String> choices) {
		//update inventory

		for(String c : choices) {
			int quantity = restaurant.getFoodInventory().get(c).getQuantity();
			restaurant.getFoodInventory().get(c).setQuantity(quantity + supply);
			restaurant.msgChangeFoodInventory(c, supply);
			restaurant.getFoodInventory().get(c).state = FoodState.Stocked;
		}
		stateChanged();
		
		print("Shipment from market received!");
	}
	
	public void msgInventoryOut(List<String> order) {
		print("Need to order " + order + " from another market.");
		orders.add(new Order(order, OrderCookState.Ordering));
		stateChanged();
	}
	
	public void msgCooking() {
		atCooking.release();
		stateChanged();
	}
	
	public void msgPlating() {
		atPlating.release();
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(state == AgentState.Arrived) {
			ClockInWithHost();
		}
		
		for (Order o : orders) {
			if (o.cs == OrderCookState.Pending) {
				CookingOrder(o);
				return true;
			}
		}
		for (Order o : orders) {
			if (o.cs == OrderCookState.Done) {
				PlaceOrder(o);
				return true;
			}
		}
		for (Order o : orders) {
			if (o.cs == OrderCookState.Ordering) {
				ReOrder(o);
				return true;
			}
		}
		
		//StandBy();
		cookGui.DoStandby();
		return false;
	}

	// Actions
	private void ClockInWithHost() {
		host.msgCookIsPresent(this); 
		cookGui.DoStandby();
		state = AgentState.Working;
	}
	
	private void CookingOrder(final Order o)	{
		if(restaurant.getFoodInventory().get(o.o).getQuantity() == 0) {
			print("Out of this order");
			o.w.msgOutOfFood(o.t, o.o);
			orders.remove(o);
		}
		else if(restaurant.getFoodInventory().get(o.o).getQuantity() == 1){
			CheckInventory();
			
			//standard cooking procedure
			cookGui.DoCooking();
			try {
				atCooking.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			o.cs = OrderCookState.Nothing;
			restaurant.getFoodInventory().get(o.o).setQuantity(restaurant.getFoodInventory().get(o.o).getQuantity() - 1);
			restaurant.msgChangeFoodInventory(o.o, restaurant.getFoodInventory().get(o.o).getQuantity() - 1);
			stateChanged();
			 (timer).schedule(new TimerTask() {
				public void run() {
					msgfoodDone(o);
				}
			}, restaurant.getFoodInventory().get(o.o).getCookTime());
			 
		}
		else {
			cookGui.DoCooking();
			try {
				atCooking.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			o.cs = OrderCookState.Nothing;
			restaurant.getFoodInventory().get(o.o).setQuantity(restaurant.getFoodInventory().get(o.o).getQuantity() - 1);
			stateChanged();
			 (timer).schedule(new TimerTask() {
				public void run() {
					msgfoodDone(o);
				}
			}, restaurant.getFoodInventory().get(o.o).getCookTime());
		}
		 //atCooking.release();
		 //stateChanged();
	}
	
	private void StandBy() {
		CheckInventory();
		cookGui.DoStandby();
	}
	
	private void CheckInventory() {
		print("Checking inventory.");
		
		//search inventory for low items
		List<String> lowItems = new ArrayList<String>();
		
		if(restaurant.getFoodInventory().get("Steak").getQuantity() <= 1) {
			lowItems.add("Steak");
		}
		if(restaurant.getFoodInventory().get("Chicken").getQuantity() <= 1) {
			lowItems.add("Chicken");
		}
		if(restaurant.getFoodInventory().get("Fish").getQuantity() <= 1) {
			lowItems.add("Fish");
		}
		if(restaurant.getFoodInventory().get("Vegetarian").getQuantity() <= 1) {
			lowItems.add("Vegetarian");
		}

		
		//send order
		if(lowItems.size() > 0) {
			print("We have low inventory, must order from market.");
			if(market1.isOpen())
				market1.getWorker().msgOrderFood(this, cashier, lowItems, 5);
			else if(market2.isOpen())
				market1.getWorker().msgOrderFood(this, cashier, lowItems, 5);
			else
				print("All markets closed.");
		}
		else
			print("We have plenty of inventory.");
		
		
	}
	
	private void ReOrder(Order o) {
		print("Reordering from another market.");
		List<String> neededItem = new ArrayList<String>();
		neededItem.add(o.o);
		if(market2.isOpen())
			market2.getWorker().msgOrderFood(this, cashier, neededItem, 5);
		else
			print("market2 closed");
		o.cs = OrderCookState.Nothing;
	}
	
	private void PlaceOrder(Order o) {
		//DoPlacement(order); //animation
		cookGui.DoPlating();		
		try {
			atPlating.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("Order is cooked");
		o.w.msgOrderIsCooked(o.t, o.o);
		orders.remove(o);
	}

	public void setGui(CookGui gui) {
		cookGui = gui;
	}

	public CookGui getGui() {
		return cookGui;
	}
	
	public List<Order> getOrders() {
		return orders;
	}

}
