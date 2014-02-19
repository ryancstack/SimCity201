package restaurant.huangRestaurant;

import gui.Building;
import gui.Gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.interfaces.MarketWorker;
import restaurant.CookRole;
import restaurant.FoodInformation;
import restaurant.Restaurant;
import restaurant.FoodInformation.FoodState;
import restaurant.huangRestaurant.Order.OrderState;
import restaurant.huangRestaurant.gui.CookGui;
import restaurant.huangRestaurant.interfaces.Cashier;
import restaurant.nakamuraRestaurant.NakamuraCookRole.SharedOrderState;
import city.helpers.Directory;




/**
 * Restaurant Cook Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HuangCookRole extends CookRole {
	private Semaphore actionComplete = new Semaphore(0, true);
	public HuangHostAgent host;
	public Cashier cashier;
	public CookGui gui;
	private HuangRestaurant restaurant;
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public enum SharedOrderState {NeedsChecking, Checked};
	SharedOrderState sharedState = SharedOrderState.NeedsChecking;
	Timer timer = new Timer();
	private class CookTimerTask extends TimerTask {
		Order o;
		HuangCookRole cook;
		public CookTimerTask(Order o, HuangCookRole cook) {
			this.o = o;
			this.cook = cook;
		}
		@Override
		public void run() {
	
		}
	}

//	public static class Food {
//		public String type;
//		public int preparationTime;
//		public int stock;
//		public Food(String type) {
//			this.type = type;
//			if (this.type == "Chicken") {
//				preparationTime = 5000;
//			}
//			else if (this.type == "Steak") {
//				preparationTime = 9000;
//			}
//			else if (this.type == "Salad") {
//				preparationTime = 4000;
//			}
//			else if (this.type == "Pizza") {
//				preparationTime = 7000;
//			}
//			stock = 0;
//		}
//	}
//	private List<Food> inventory = Collections.synchronizedList(new ArrayList<Food>());
	
	private enum MarketReqState {notSent,pending, canBeFulfilled, cannotBeFulfilled, Received}
	private class MarketRequest {
		private MarketReqState state;
		String request;
		public MarketWorker m;
		public int requirement;
		
		public MarketRequest(String request, int requestStock) {
			this.request = request;
			this.requirement = requestStock;
			this.state = MarketReqState.notSent;
		}
		public void setMarket(MarketWorker m) {
			this.m = m;
		}
	}
	private List<MarketRequest> marketRequests = Collections.synchronizedList(new ArrayList<MarketRequest>());
	
	private enum MarketState {inStock, outOfStock, delivering, delivered, accept, reject, uncalled};
	private class MyMarket {
		private int number;
		private MarketWorker m;
		private MarketState state;
		private boolean Chicken;
		private boolean Steak;
		private boolean Pizza;
		private boolean Salad;
		private Stack<String> rejected = new Stack<String>();
		public MyMarket(MarketWorker m, int i) {
			this.number = i;
			this.m = m;
			this.Chicken = true;
			this.Steak = true;
			this.Pizza = true;
			this.Salad = true;
			state = MarketState.inStock;
		}
	}
	private List<MyMarket> markets = Collections.synchronizedList(new ArrayList<MyMarket>());
	private static final int requestStock = 5;
	private static final int threshold = 2;
	
	private Semaphore processingMarketResponse = new Semaphore(1, true);
	//private Semaphore removingOrders = new Semaphore(0, true);
	//private Semaphore removingOrders = new Semaphore(0, true);
	private String name;
	public String myLocation;
	private boolean doneWorking = false;
	public enum CookState {
		Arrived, Working, ShiftEnded, WantsToLeave, Leaving, InTransit, DoneWorking, GoToStation, CollectPay, ReceivedPay;
	}
	CookState state;
	public HuangCookRole(String location) {
		super();
		host = (HuangHostAgent) Directory.sharedInstance().getAgents().get("HuangRestaurantHost");
		gui = new CookGui(this);
		myLocation = location;
		state = CookState.Arrived;
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui((Gui) gui);
			}
		}
		//Set up markets
		MyMarket mm;
		for(int i = 0; i < Directory.sharedInstance().getMarkets().size(); i++) {
			mm = new MyMarket(Directory.sharedInstance().getMarkets().get(i).getWorker(), i);
			markets.add(mm);
		}
	}

	public String getName() {
		return name;
	}

	// Messages
	public void msgActionComplete() {
		actionComplete.release();
	}
	public void msgMarketDeliveringOrder(int resupply, String type) {
		System.out.println(name + ": msgHereIsDelivery received: Cook: Kitchen inventory replenished!");
		restaurant.getFoodInventory().get(type).setQuantity(restaurant.getFoodInventory().get(type).getQuantity() + resupply);
		restaurant.msgChangeFoodInventory(type, restaurant.getFoodInventory().get(type).getQuantity());
		for (MarketRequest mr: marketRequests) {
			if (mr.request.equals(type)) {
				if(mr.requirement - resupply <= 0) {
					mr.state = MarketReqState.Received;
					break;
				}
				else {
					mr.requirement -= resupply;
					mr.state = MarketReqState.notSent;
					break;
				}
			}
			
		}
		stateChanged();
	}
	public void msgCanFillOrder(MarketWorker m, String request) {
		processingMarketResponse.acquireUninterruptibly();
		for (MarketRequest mr: marketRequests) {
			if(mr.request.equals(request)) {
				if (mr.state.equals(MarketReqState.canBeFulfilled)){
					for (MyMarket mm: markets) {
						if (mm.m.equals(m)) {
							mm.state = MarketState.reject;
							mm.rejected.add(request);
							break;
						}
					}
					break;
				}
				
				else {
					System.out.println(name + ": msgRequestGood received: Cook: Market has good stock.");
					mr.setMarket(m);
					mr.state = MarketReqState.canBeFulfilled;
					break;
				}
			}
		}
		processingMarketResponse.release();
		stateChanged();
	}
	public void msgInventoryOut(MarketWorker m, String request) {
		System.out.println(name + ": msgRequestBad received: Cook: Market is out of that food");
		if(request == "Chicken") {
			synchronized(markets) {
				for(MyMarket mm: markets) {
					if (mm.m.equals(m)) {
						mm.Chicken = false;
					}
				}
			}
		}
		else if(request == "Steak") {
			synchronized(markets) {
				for(MyMarket mm: markets) {
					if (mm.m.equals(m)) {
						mm.Steak = false;
					}
				}
			}
		}
		else if(request == "Pizza") {
			synchronized(markets) {
				for(MyMarket mm: markets) {
					if (mm.m.equals(m)) {
						mm.Pizza = false;
					}
				}
			}
		}
		else if(request == "Salad") {
			synchronized(markets) {
				for(MyMarket mm: markets) {
					if (mm.m.equals(m)) {
						mm.Salad = false;
					}
				}
			}
		}
		synchronized(marketRequests) {
			for (MarketRequest mr: marketRequests) {
				if(mr.request.equals(request)) {
					if(mr.state.equals(MarketReqState.pending)) {
						mr.setMarket(m);
						mr.state = MarketReqState.cannotBeFulfilled;
						break;
					}
				}
			}
		}
		stateChanged();
	}

	public void msgHereIsPaycheck(double payCheck) {
		state = CookState.ReceivedPay;
		getPersonAgent().setFunds(getPersonAgent().getFunds() + payCheck);
		stateChanged();
	}
	public void msgJobDone() {
		doneWorking = true;
		state = CookState.DoneWorking;
		stateChanged();
	}
	public void msgHereIsOrder(HuangWaiterRole w, String choice, int table) {
		orders.add(new Order(w, choice, table));
		System.out.println(name + ": msgHereIsOrder received: Cook: Got the Order!");
		stateChanged();
	}

	public void msgFoodDone(Order o){
		o.state = OrderState.Done;
		System.out.println(name + ": msgFoodDone received: This dish is done!");
		stateChanged();
	}
	public void msgYouCanLeave() {
		state = CookState.CollectPay;
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if (state == CookState.Arrived) {
			tellHostArrivedAtWork();
			return true;
		}
		if (state == CookState.ReceivedPay) {
			leaveWork();
			return true;
		}
		if (state == CookState.GoToStation) {
			goToStove();
			return true;
		}
		if (state == CookState.CollectPay) {
			collectPay();
			return true;
		}
		//rule 1
		synchronized(orders) {
			for (Order o: orders) {
				if(o.state.equals(OrderState.Pending)) {
					tryCook(o);
					return true;
				}
			}
		}
		synchronized(orders) {
			for (Order o: orders) {
				if(o.state.equals(OrderState.Done)) {
					plateIt(o);
					return true;
				}
			}
		}
		synchronized(markets) {
			for (MyMarket mm: markets) {
					if (mm.state.equals(MarketState.reject)) {
						//removingOrders.release();
						tellMarketCancelOrder(mm);
						mm.state = MarketState.uncalled;
						//mm.rejected.clear();
						return true;
					}
			}
		}
		synchronized(marketRequests) {
			for (MarketRequest mr: marketRequests) {
				if (mr.state.equals(MarketReqState.notSent)) {
					callForRestock(mr.request, mr.requirement);
					mr.state = MarketReqState.pending;
					return true;
				}
			}
		}
		synchronized(marketRequests) {
			for (MarketRequest mr: marketRequests) {
				if (mr.state.equals(MarketReqState.cannotBeFulfilled)) {
					/** Perhaps tell host that this item is now off the menu? */
					marketRequests.remove(mr);
					return true;
				}
			}
		}
		synchronized(marketRequests) {
			for (MarketRequest mr: marketRequests) {
				if (mr.state.equals(MarketReqState.Received)) {
					marketRequests.remove(mr);
					return true;
				}
			}
		}
		if (state == CookState.DoneWorking) {
			tellHostDoneWorking();
			return true;
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
		//rule 2
		checkInventory();
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions	
	private void leaveWork() {
		getPersonAgent().msgRoleFinished();
		state = CookState.Arrived;
		gui.DoLeaveRestaurant();
	}
	private void collectPay() {
		state = CookState.InTransit;
		gui.DoGoToCashier();
		actionComplete.acquireUninterruptibly();
		host.getCashier().msgAskForPayCheck(this);
		
	}
	private void tellHostDoneWorking() {
		state = CookState.CollectPay;
		host.msgDoneWorking(this);
	}
	private void goToStove() {
		state = CookState.InTransit;
		gui.DoGoToStove();
		actionComplete.acquireUninterruptibly();
	}
	private void tellHostArrivedAtWork() {
		state = CookState.GoToStation;
		gui.DoGoToHost();
		actionComplete.acquireUninterruptibly();
		host.msgArrivedToWork(this);
	}
	private void DoCook(Order o) {
		gui.DoCookDish(o.choice, o.table);
	}
	private void DoPlate(Order o) {
		gui.DoPlateDish(o.table);
	}
	private void tellMarketCancelOrder(MyMarket mm) {
		if(!mm.rejected.isEmpty()) {
					mm.m.msgCancelOrder(this);
		}
	}
	private void callForRestock(String foodType, int requirement) {
		/*int reportedStock = 0;
		for (Food f: inventory) {
			if(f.type.equals(foodType)) {
				reportedStock = f.stock;
				break;
			}
		}*/
		for (MyMarket m: markets) {
			if(foodType == "Chicken") {
				if (m.Chicken == true) {
					m.m.msgOrderFood(this, cashier, foodType, requirement);
				}
			}
			else if(foodType == "Steak") {
				if (m.Steak == true) {
					m.m.msgOrderFood(this, cashier, foodType, requirement);
				}
			}
			else if(foodType == "Salad") {
				if (m.Salad == true) {
					m.m.msgOrderFood(this, cashier, foodType, requirement);
				}
			}
			else if(foodType == "Pizza") {
				if (m.Pizza == true) {
					m.m.msgOrderFood(this, cashier, foodType, requirement);
				}
			}
		}
	}
	
	private void checkInventory() {
		boolean exists = false;
		synchronized(restaurant.getFoodInventory()) {
			for (Map.Entry<String, FoodInformation> food : restaurant.getFoodInventory().entrySet()) {
				if ((int)food.getValue().getQuantity() <= threshold) {
					if (!marketRequests.isEmpty()) {
						synchronized(marketRequests) {
							for (MarketRequest m: marketRequests) {
								if(m.request.equals(food.getKey())) {
									exists = true;
									break;
								}
							}
						}
						if (exists == true) {
							exists = false;
							continue;
						}
						MarketRequest mr = new MarketRequest(food.getKey(), requestStock);
						marketRequests.add(mr);
					}
				}
			}
		}
	}

	private void tryCook(Order o) {

		int time;
		
		synchronized(restaurant.getFoodInventory()) {
			for (Map.Entry<String, FoodInformation> f : restaurant.getFoodInventory().entrySet()) {
				if (f.getKey().equals(o.choice)) {
					if ((int) f.getValue().getQuantity() > threshold) {
						f.getValue().setQuantity(f.getValue().getQuantity() - 1);
						restaurant.msgChangeFoodInventory(f.getKey(), f.getValue().getQuantity());
						time = f.getValue().getCookTime();
						timer.schedule(new CookTimerTask(o, this) {
							public void run() {
								cook.msgFoodDone(o);
							}
						},
						time);//time for cooking
						o.state = OrderState.Cooking;
						DoCook(o);
						break;
					}
					else if((int) f.getValue().getQuantity() <= threshold) {
						if ((int) f.getValue().getQuantity() > 0) {
							f.getValue().setQuantity(f.getValue().getQuantity() - 1);
							restaurant.msgChangeFoodInventory(f.getKey(), f.getValue().getQuantity());
							time = f.getValue().getCookTime();
							timer.schedule(new CookTimerTask(o, this) {
								public void run() {
									cook.msgFoodDone(o);
								}
							},
							time);//time for cooking
							o.state = OrderState.Cooking;
							DoCook(o);
							if (!marketRequests.isEmpty()) {
								for (MarketRequest m: marketRequests) {
									if(m.request.equals(o.choice)) {
										break;
									}
								}
								break;
							}
							MarketRequest mr = new MarketRequest(o.choice, requestStock);
							marketRequests.add(mr);
						}
						else if ((int) f.getValue().getQuantity() <= 0) {
							o.state = OrderState.out;
							o.w.msgOutOfChoice(o.choice, o.table);
							if (!marketRequests.isEmpty()) {
								for (MarketRequest m: marketRequests) {
									if(m.request.equals(o.choice)) {
										break;
									}
								}
								break;
							}
							MarketRequest mr = new MarketRequest(o.choice, requestStock);
							marketRequests.add(mr);
							break;
						}
					}
				}
			}
		}
	}

	private void plateIt(Order o) {
		o.w.msgOrderDone(o.choice, o.table);
		o.state = OrderState.Plated;
		DoPlate(o);
	}
	//utilities
	private void addSharedOrders() {
		Order order = restaurant.getMyMonitor().remove();
		if(order != null) {
			orders.add(order);
		}
	}
	public void setGui(CookGui gui) {
		this.gui = gui;
	}
	public void setRestaurant(HuangRestaurant huang) {
		this.restaurant = huang;
	}
	public void setCashier(HuangCashierAgent cashier) {
		this.cashier = cashier;
	}
}

