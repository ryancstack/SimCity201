package restaurant.nakamuraRestaurant;

import gui.Building;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.Market;
import restaurant.CookRole;
import restaurant.FoodInformation;
import restaurant.FoodInformation.FoodState;
import restaurant.Restaurant;
import restaurant.nakamuraRestaurant.helpers.Order;
import restaurant.nakamuraRestaurant.gui.CookGui;
import city.helpers.Directory;

/**
 * Restaurant Cook Agent
 */
public class NakamuraCookRole extends CookRole {
	public List<Order> Orders
	= Collections.synchronizedList(new ArrayList<Order>());

	public List<MarketOrder> marketOrders = Collections.synchronizedList(new ArrayList<MarketOrder>());
	public List<Market> markets = Collections.synchronizedList(new ArrayList<Market>());
	private Semaphore actionComplete = new Semaphore(0,true);

	public enum orderState {pending, cooking, done};
	public enum SharedOrderState {NeedsChecking, Checked};
	SharedOrderState sharedState = SharedOrderState.NeedsChecking;
	
	private enum marketOrderState {Ordered, Verifying, Done};
	
	private enum cookState {Arrived, Working, GettingPaycheck, Leaving, WaitingForPaycheck, DoneWorking, WaitingToLeave};
	cookState state;
	Timer timer = new Timer();
	boolean checkInventory;
	
	String myLocation;

	public CookGui cookGui;
	NakamuraHostAgent host;
	NakamuraCashierAgent cashier;
	private NakamuraRestaurant restaurant = (NakamuraRestaurant) Directory.sharedInstance().getRestaurants().get(2);

	public NakamuraCookRole(String location) {
		super();

		this.myLocation = location;

		host = (NakamuraHostAgent) Directory.sharedInstance().getAgents().get("NakamuraRestaurantHost");
		cashier = (NakamuraCashierAgent) Directory.sharedInstance().getAgents().get("NakamuraRestaurantCashier");

		for(int i = 0; i < Directory.sharedInstance().getMarkets().size(); i++) {
			markets.add(Directory.sharedInstance().getMarkets().get(i));
		}		
		
		state = cookState.Arrived;
		cookGui = new CookGui(this);
		
		myLocation = location;
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui(cookGui);
			}
		}
		
	}

	public List<Order> getOrders() {
		return Orders;
	}

	// Messages
	public void msgCookOrder(NakamuraWaiterRole w, String choice, int tableNumber) {
		print("Received msgCookOrder");
		Orders.add(new Order(w, choice, tableNumber));
		stateChanged();
	}

	public void msgFoodDone(Order o) {
		print("Received msgFoodDone");
		o.s = orderState.done;
		stateChanged();
	}
	
	public void msgInventoryOut(Market m, List<String> choices, int amount) {
		print("Received msgCantFillOrder");
		for(String c : choices) {
			getFood(c).getMarkets().add(m.getWorker());
			getFood(c).state = FoodState.Empty;
		}
		stateChanged();
	}

	public void msgMarketDeliveringOrder (int amount, List<String> choices) {
		for(String c : choices) {
			int quantity = getFood(c).getQuantity();
			getFood(c).setQuantity(quantity + amount);
			restaurant.msgChangeFoodInventory(c, amount);
			getFood(c).state = FoodState.Stocked;
		}
		stateChanged();
	}
	
	public void msgCheckInventory() {
		print("Received msgCheckInventory");
		checkInventory = true;
		stateChanged();
	}
	
	public void msgVerifyMarketBill(List<String> choices, int amount) {
		print("Received msgVerifyMarketBill");
		synchronized(marketOrders) {
			for(MarketOrder order : marketOrders) {
				if(order.choices.contains(choices) && order.amount == amount) {
					order.state = marketOrderState.Verifying;
				}
			}
		}
		stateChanged();
	}
	
	public void msgActionComplete() {
		print("msgActionComplete called");
		actionComplete.release();
		stateChanged();
	}
	
	public void msgJobDone() {
		print("Received msgJobDone");
		state = cookState.DoneWorking;
		stateChanged();
	}
	
	public void msgYouMayGo() {
		print("Received msgYouMayGo");
		state = cookState.GettingPaycheck;
		stateChanged();
	}
	
	public void msgHereIsPaycheck(double pay){
		print("Received msgHereIsPaycheck");
		getPersonAgent().setFunds(getPersonAgent().getFunds() + pay);
		state = cookState.Leaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(state == cookState.Arrived) {
			ArriveAtWork();
			return true;
		}
		
		if(state == cookState.DoneWorking) {
			NotifyHost();
			return true;
		}
		
		if(state == cookState.GettingPaycheck) {
			CollectPaycheck();
			return true;
		}
		
		if(state == cookState.Leaving) {
			LeaveRestaurant();
			return true;
		}
		
		if(checkInventory) {
			CheckInventory();
			checkInventory = false;
			return true;
		}
		
		synchronized(Orders) {
			for (Order o : Orders) {
				if(o.getState() == orderState.done) {
					PlateOrder(o);
					return true;
				}
			}
			for (Order o : Orders) {
				if (o.getState() == orderState.pending) {
					CookOrder(o);
					return true;
				}
			}
		}
		
		synchronized(marketOrders) {
			for(MarketOrder o : marketOrders) {
				if(o.state == marketOrderState.Verifying) {
					ConfirmOrder(o);
					return true;
				}
			}

			for(MarketOrder o : marketOrders) {
				if(o.state == marketOrderState.Done) {
					marketOrders.remove(o);
					return true;
				}
			}
		}
		
		if(sharedState == SharedOrderState.NeedsChecking) {
			timer.schedule(new TimerTask() {
				public void run() {
					addSharedOrders();
					sharedState = SharedOrderState.NeedsChecking;
					stateChanged();
				}
			},
			5000);
			sharedState = SharedOrderState.Checked;
			return true;
		}
		

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void ArriveAtWork() {
		host.msgNewCook(this);
		cashier.setCook(this);
		
		cookGui.setPresent();
		cookGui.DoGoToCooking();
		state = cookState.Working;
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void addSharedOrders() {
		Order order = restaurant.getMyMonitor().remove();
		if(order != null) {
			Orders.add(order);
		}
	}
	
	private void CookOrder(final Order o) {
		if (getFood(o.choice).getQuantity() <= 2 && getFood(o.choice).state != FoodState.Ordered) {
			List<String> order = new ArrayList<String>();
			order.add(o.choice);
			OrderFood(order);
		}
		
		if(getFood(o.choice).getQuantity() == 0) {
			o.w.msgOutofFood(o.choice, o.tableNumber);
			Orders.remove(o);
		}
		
		else {
			
			o.s = orderState.cooking;
			DoCookOrder(o);
			try {
				actionComplete.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			getFood(o.choice).setQuantity(getFood(o.choice).getQuantity() - 1);
			restaurant.msgChangeFoodInventory(o.choice, getFood(o.choice).getQuantity() - 1);
			print(o.choice + " remaining: " + getFood(o.choice).getQuantity());
			timer.schedule(new TimerTask() {
				public void run() {
					msgFoodDone(o);
					stateChanged();
				}
			},
			getFood(o.choice).getCookTime());
		}
	}

	private void PlateOrder(Order o) {
		DoPlateOrder(o); //animation
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		print("Plating food");
		o.w.msgFoodReady(o.choice, o.tableNumber);
		Orders.remove(o);
	}
	
	private void OrderFood(List<String> choices) {	
		synchronized(markets) {
			for(Market m : markets) {
				List<String> orders = new ArrayList<String>();
				for(String c : choices) {
					if(!getFood(c).getMarkets().contains(m)) {
						orders.add(c);
						getFood(c).state = FoodState.Ordered;
					}
				}
				
				if(!orders.isEmpty() && m.isOpen()){
					m.getWorker().msgOrderFood(this, cashier, orders, 5);
					marketOrders.add(new MarketOrder(orders, 5));
					choices.remove(orders);
				}
			}
		}
	}
	
	private void CheckInventory() {
		List<String> order = new ArrayList<String>();
			print(getFood("Steak").getQuantity() + " Steaks remaning");
			if(getFood("Steak").getQuantity() <= 5) {
				order.add("Steak");
				getFood("Steak").state = FoodState.Ordered;
			}
			print(getFood("Pizza").getQuantity() + " Pizzas remaning");
			if(getFood("Pizza").getQuantity() <= 5) {
				order.add("Pizza");
				getFood("Pizza").state = FoodState.Ordered;
			}
			print(getFood("Chicken").getQuantity() + " Chickens remaning");
			if(getFood("Chicken").getQuantity() <= 5) {
				order.add("Chicken");
				getFood("Chicken").state = FoodState.Ordered;
			}
			print(getFood("Salad").getQuantity() + " Salads remaning");
			if(getFood("Salad").getQuantity() <= 5) {
				order.add("Salad");
				getFood("Salad").state = FoodState.Ordered;
			}
		
		OrderFood(order);
	}
	
	private void ConfirmOrder(MarketOrder o) {
		cashier.msgBillIsCorrect(o.choices, o.amount);
		o.state = marketOrderState.Done;
	}
	
	private void NotifyHost() {
		host.msgCookDone(this);
		state = cookState.WaitingToLeave;
	}
	
	private void CollectPaycheck() {
		cookGui.DoGoToCashier();
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cashier.msgNeedPay(this);
		state = cookState.WaitingForPaycheck;
	}
	
	private void LeaveRestaurant() {
		DoLeaveRestaurant();
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		getPersonAgent().msgRoleFinished();
	}

	// The animation DoXYZ() routines
	private void DoCookOrder(Order o) {
		cookGui.DoGoToCooking();
		cookGui.AddCooking(o.choice);
	}
	
	private void DoPlateOrder(Order o) {
		cookGui.DoGoToPlating();
		cookGui.AddPlating(o.choice);
	}
	
	private void DoLeaveRestaurant() {
		cookGui.DoLeaveRestaurant();		
	}

	//utilities

	public void setGui(CookGui gui) {
		cookGui = gui;
	}

	public CookGui getGui() {
		return cookGui;
	}
	
	public FoodInformation getFood(String choice) {
		return restaurant.getFoodInventory().get(choice);
	}
	
	private class MarketOrder {
		List<String> choices;
		int amount;
		marketOrderState state;
		
		
		MarketOrder(List<String> choices, int amount) {
			this.choices = choices;
			this.amount = amount;
			this.state = marketOrderState.Ordered;
		}
	}
}

