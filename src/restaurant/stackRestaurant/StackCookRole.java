package restaurant.stackRestaurant;

import gui.Building;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.interfaces.MarketWorker;
import restaurant.CookRole;
import restaurant.stackRestaurant.gui.CookGui;
import restaurant.FoodInformation.FoodState;
import restaurant.Restaurant;
import restaurant.FoodInformation;
import restaurant.stackRestaurant.helpers.Menu;
import restaurant.stackRestaurant.interfaces.Cashier;
import restaurant.stackRestaurant.interfaces.Cook;
import restaurant.stackRestaurant.interfaces.Host;
import restaurant.stackRestaurant.interfaces.Waiter;
import trace.AlertLog;
import trace.AlertTag;
import city.helpers.Directory;

public class StackCookRole extends CookRole implements Cook {
	
	private List<MyOrder> orders = Collections.synchronizedList(new ArrayList<MyOrder>());
	private List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());
	private CookGui cookGui;
	private String myLocation;
	private Timer timer = new Timer();
	private Host host;
	private MarketWorker market1;
	private Cashier cashier;
	private Restaurant restaurant = Directory.sharedInstance().getRestaurants().get(0);
	private String stringState;
	
	private Semaphore doneAnimation = new Semaphore(0,true);
	private enum AgentState 
	{Arrived, Working, GettingPaycheck, Leaving, WaitingForPaycheck};
	AgentState state;
	
	private enum OrderState
	{Pending, Cooking, Done, Notified};
	
	private enum SharedOrderState
	{Checked, NeedsChecking};
	SharedOrderState sharedState = SharedOrderState.NeedsChecking;
	
	public StackCookRole(String location) {
		super();
		cookGui = new CookGui(this);
		state = AgentState.Arrived;
		
		host = (Host) Directory.sharedInstance().getAgents().get("StackRestaurantHost");
		cashier = (Cashier) Directory.sharedInstance().getRestaurants().get(0).getCashier();
		market1 = (MarketWorker) Directory.sharedInstance().marketDirectory.get("Market").getWorker();
		markets.add(new MyMarket(market1));
		
		
		myLocation = location;
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui(cookGui);
			}
		}
	}
	
	public String getName() {
		if(getPersonAgent() != null) {
			return getPersonAgent().getName();
		}
		else {
			return "";
		}
	}
	
	public void setGui(CookGui g) {
		cookGui = g;
	}
	
	public CookGui getGui() {
		return cookGui;
	}
	
	@Override
	public boolean pickAndExecuteAnAction() {
		if(state == AgentState.Arrived) {
			setStringState(state.toString());
			tellHostAtWork();
			return true;
		}
		if(state == AgentState.GettingPaycheck) {
			setStringState(state.toString());
			goGetPaycheck();
			return true;
		}
		if(state == AgentState.Leaving) {
			setStringState(state.toString());
			leaveRestaurant();
			return true;
		}
		synchronized(orders) {
			for(MyOrder order : orders) {
				if(order.state == OrderState.Done) {
					setStringState(order.state.toString());
					AlertLog.getInstance().logMessage(AlertTag.COOK, getName(), "Plate it");
					plateIt(order);
					return true;
				}
			}
		}
		synchronized(orders) {
			for(MyOrder order : orders) {
				if(order.state == OrderState.Pending) {
					setStringState(order.state.toString());
					AlertLog.getInstance().logMessage(AlertTag.COOK, getName(), "Cook it");

					cookIt(order);
					return true;
				}
			}
		}
		
		synchronized(restaurant.getFoodInventory()) {
			for(Map.Entry<String, FoodInformation> food : restaurant.getFoodInventory().entrySet()) {
				if(food.getValue().state == FoodState.Empty) {
					setStringState(food.getValue().state.toString());
					orderIt(food.getKey());
					return true;
				}
			}
		}
		if(sharedState == SharedOrderState.NeedsChecking) {
			setStringState(sharedState.toString());
			timer.schedule(new TimerTask() {
				public void run() {
					addSharedOrders();
					setStringState(sharedState.toString());
					sharedState = SharedOrderState.NeedsChecking;
					stateChanged();
				}
			},
			5000);
			sharedState = SharedOrderState.Checked;
			setStringState(sharedState.toString());
			return true;
		}
		return false;
	}
	
	//actions
	private void addSharedOrders() {
		Order order = Directory.sharedInstance().getRestaurants().get(0).getMonitor().remove();
		if(order != null) {
			orders.add(new MyOrder(order, OrderState.Pending));
		}
		doneAnimation.release();
	}
	
	private void cookIt(final MyOrder order) {
		int cookingTime = restaurant.getFoodInventory().get(order.choice).getCookTime();
		int inventory = restaurant.getFoodInventory().get(order.choice).getQuantity();
		if(inventory == 0) {
			Menu.sharedInstance().setInventoryStock(order.choice, false);
			order.waiter.msgFoodEmpty(order.choice, order.table, order.seat);
			restaurant.getFoodInventory().get(order.choice).state = FoodState.Empty;
			order.state = OrderState.Notified;
			return;
		}
		else {
			int quantity = restaurant.getFoodInventory().get(order.choice).getQuantity();
			restaurant.getFoodInventory().get(order.choice).setQuantity(quantity--);
			restaurant.msgChangeFoodInventory(order.choice, quantity--);
		}
		cookGui.DoGoToFridge();
		try {
			doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cookGui.DoGoToCookTop();
		try {
			doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				cookGui.DoGoToPlatingArea();
				try {
					doneAnimation.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				order.state = OrderState.Done;
				stateChanged();
			}
		},
		cookingTime);
		order.state = OrderState.Cooking;
		stateChanged();
	}
	
	private void plateIt(MyOrder order) {
		order.state = OrderState.Notified;
		order.waiter.msgOrderDone(order.choice, order.table, order.seat);	
	}
	
	private void orderIt(String choice) {
		for(MyMarket market : markets) {
			if(market.market != null) {
				if(market.foodStock.get(choice)) {
					AlertLog.getInstance().logMessage(AlertTag.COOK, getName(), "Trying to order food");
					market.market.msgOrderFood(this, cashier, choice);
					restaurant.getFoodInventory().get(choice).state = FoodState.Ordered;
					return;
				}
			}
		}
		restaurant.getFoodInventory().get(choice).state = FoodState.PermanentlyEmpty;
		
	}
	
	private void tellHostAtWork() {
		host.msgAddCook(this);
		cookGui.DoGoHome();
		state = AgentState.Working;
	}
	
	private void leaveRestaurant() {
		AlertLog.getInstance().logMessage(AlertTag.COOK, getName(), "Leaving");
		DoLeaveRestaurant();
		try {
			doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		getPersonAgent().msgRoleFinished();
	}

	private void goGetPaycheck() {
		AlertLog.getInstance().logMessage(AlertTag.COOK, getName(), "Getting paycheck");
		cookGui.DoGoToPaycheck();
		try {
			doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cashier.msgNeedPaycheck(this);
		state = AgentState.WaitingForPaycheck;
	}

	private void DoLeaveRestaurant() {
		host.msgCookLeaving(this);
		cookGui.DoExitRestaurant();
	}
		
	//messages
	public void msgHereIsPaycheck(double funds) {
		getPersonAgent().setFunds(getPersonAgent().getFunds() + funds);
		state = AgentState.Leaving;
		stateChanged();
	}
	
	public void msgJobDone() {
		state = AgentState.GettingPaycheck;
		stateChanged();
		
	}
	
	public void msgCheckOrders() {
		sharedState = SharedOrderState.NeedsChecking;
		stateChanged();
	}
	public void msgCookOrder(Waiter waiter, String choice, int table, int seat) {
		orders.add(new MyOrder(waiter, choice, table, seat, OrderState.Pending));
		AlertLog.getInstance().logMessage(AlertTag.COOK, getName(), "New order to cook");
		stateChanged();
	}
	
	public void msgInventoryOut(MarketWorker market, String choice) {
		restaurant.getFoodInventory().get(choice).state = FoodState.Empty;
		for(MyMarket mMarket : markets) {
			if(market.equals(mMarket.market)) {
				mMarket.foodStock.put(choice, false);
			}
		}
		AlertLog.getInstance().logMessage(AlertTag.COOK, getName(), "Ran out of " + choice);
		stateChanged();
	}
	
	public void msgMarketDeliveringOrder(int inventory, String choice) {
		restaurant.getFoodInventory().get(choice).setQuantity(inventory);
		restaurant.msgChangeFoodInventory(choice, inventory);
		restaurant.getFoodInventory().get(choice).state = FoodState.Stocked;
		Menu.sharedInstance().setInventoryStock(choice, true);
		AlertLog.getInstance().logMessage(AlertTag.COOK, getName(), choice + " has arrived");
	}
	
	public void msgAddMarket(MarketWorker market) {
		markets.add(new MyMarket(market));
		stateChanged();
	}
	
	public void msgAtCooktop() {
		doneAnimation.release();
	}

	public void msgAtPlating() {
		doneAnimation.release();
	}
	
	public void msgAtFridge() {
		doneAnimation.release();
	}
	
	public void msgAtCashier() {
		doneAnimation.release();
	}
	public void msgAnimationFinishedLeavingRestaurant() {
		doneAnimation.release();
	}
	
	private class MyOrder {
		MyOrder(Waiter waiter, String choice, int table, int seat, OrderState state) {
			this.waiter = waiter;
			this.choice = choice;
			this.table = table;
			this.seat = seat;
			this.state = state;
		}
		MyOrder(Order order, OrderState state) {
			waiter = order.waiter;
			choice = order.choice;
			table = order.table;
			seat = order.seat;
			this.state = state;
		}
		Waiter waiter;
		String choice;
		int table;
		int seat;
		OrderState state;
	}
	
	
	private class MyMarket {
		public MyMarket(MarketWorker market) {
			this.market = market;
		}
		MarketWorker market;
		@SuppressWarnings("serial")
		Map<String, Boolean> foodStock = new HashMap<String, Boolean>() {
			{
				put("Steak", true);
				put("Chicken", true);
				put("Salad", true);
				put("Pizza", true);
			}
		};
	}

	public void setRestaurant(Restaurant restaurant) {
		this.restaurant = restaurant;
		
	}
	
	public String getStringState() {
		return stringState;
	}
	
	public void setStringState(String stringState) {
		this.stringState = stringState;
	}
}
