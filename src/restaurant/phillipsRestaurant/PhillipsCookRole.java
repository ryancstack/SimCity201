package restaurant.phillipsRestaurant;

import gui.Building;

import restaurant.CookRole;
import restaurant.phillipsRestaurant.gui.*;
import restaurant.Restaurant;
import restaurant.FoodInformation;
import restaurant.phillipsRestaurant.Menu;
import restaurant.phillipsRestaurant.interfaces.*;
import restaurant.phillipsRestaurant.interfaces.Waiter;
import city.helpers.Directory;

import market.interfaces.MarketWorker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;


/**
 * Restaurant cook agent.
 */
public class PhillipsCookRole extends CookRole implements Cook {
	
	private class Food{ 
		String choice; 
		int cookTime;

		public Food(String c) {
			switch(c){
			case "steak":
				choice = "steak";
				cookTime = 10000;
				break;
			case "chicken":
				choice = "chicken";
				cookTime = 8000;
				break;
			case "salad":
				choice = "salad";
				cookTime = 5000;
				break;
			case "pizza":
				choice = "pizza";
				cookTime = 8000;
				break;
			}
		}
	}
	
	private class Order{
		Waiter waiter; 
		String order; 
		int tableNumber;
		OrderState os;
		OrderEvent oe;

		Order(Waiter w,String c,int t,OrderState st) {
			waiter = w;
			order = c;
			tableNumber = t;
			os = st;
		}
	}
	
	private class MyMarket{
		MarketAgent market; 
		MarketState ms;
		boolean hasSteak = true,hasChicken = true,hasSalad = true,hasPizza = true;

		MyMarket(MarketAgent m) {
			market = m;
			ms = MarketState.yesInventory;
		}
	}
	
	private class MarketOrder{
		MarketAgent market;
		String food;
		OrderState st;
		int amount;
		
		MarketOrder(String f,int a,MarketAgent m, OrderState s) {
			food = f;
			st = s;
			amount = a;
			market = m;
		}
	}
	

	List<Order> orders;
	List<MyMarket> markets;
	List<MarketOrder> marketOrders;
	public enum OrderState {pending,haveInventory,lowInventory,outOfInventory,oweMarketMoney,gettingIngredients,cooking,plating,done,noFood};
	public enum OrderEvent {gotFromFridge,doneCooked};
	public enum MarketState {yesInventory,noInventory};
	Timer timer = new Timer();
	private Map<String,Integer> inv = new HashMap<String,Integer>(4);
	//map<String choice,Food f> foods;
	OrderState state = OrderState.lowInventory, payStatus = OrderState.pending;
	boolean marketsOut = false;
	private final int INVENTORY = 3;  //HACK

	private Semaphore atFridge = new Semaphore(0,true);
	private Semaphore atCookingArea = new Semaphore(0,true);
	private Semaphore atPlatingArea = new Semaphore(0,true);
	
	public CookGui cookGui = null;
	private Waiter waiter=null;
	private Cashier cashier=null;


	/**
	 * Constructor for CookAgent class
	 *
	 */
	public PhillipsCookRole(){
		super();
		orders = Collections.synchronizedList(new ArrayList<Order>());
		markets = Collections.synchronizedList(new ArrayList<MyMarket>());
		marketOrders = Collections.synchronizedList(new ArrayList<MarketOrder>());
		inv.put("steak",INVENTORY);
		inv.put("chicken",2);
		inv.put("salad",INVENTORY);
		inv.put("pizza",INVENTORY);
	}
	//setters
	public void setMarket(MarketAgent m){
		markets.add(new MyMarket(m));
	}
	public void setGui(CookGui gui) {
		cookGui = gui;
	}	
	public void setCashier(Cashier c){
		cashier = c;
	}
	
	//Animation msgs
	public void msgAtFridge() {//from animation
		timer.schedule(new TimerTask() {
			public void run() {
				//getting ingredients
				atFridge.release();
				stateChanged();
			}
		},
		2000);
		//atFridge.release();// = true;
		//stateChanged();
	}
	public void msgAtCookingArea() {//from animation		
		atCookingArea.release();// = true;
		stateChanged();
	}
	public void msgAtPlatingArea() {//from animation		
		atPlatingArea.release();// = true;
		stateChanged();
	}
	
	// Messages
	public void msgHereIsOrder(Waiter w,String choice,int table){
		Do("Cook received order");
		//waiter = w;
		synchronized(this.orders){		
			orders.add(new Order(w,choice,table,OrderState.pending));
		}
		stateChanged();
	}
	
	
	public void msgGotFoodFromFridge(Order o){
		Do("Cook got food from fridge");
		o.oe = OrderEvent.gotFromFridge;
		stateChanged();
	}

	public void msgFoodCooked(Order o){
		Do("Food is cooked");
		//o.os=OrderState.done;
		o.oe = OrderEvent.doneCooked;
		stateChanged();
	}
	public void msgCookHereIsInventory(String order,int i,MarketAgent m){
		for (Map.Entry<String, Integer> entry : inv.entrySet()) { 
			if(entry.getKey() == order){
				entry.setValue(entry.getValue()+i);
				//System.err.println("Cook received " + i + " " + entry.getKey() + " from the market");
				marketOrders.add(new MarketOrder(order,i,m,OrderState.oweMarketMoney));
			}
		} 
		
	}
	public void msgCookNoInventory(String order, MarketAgent m){
		//state = OrderState.noFood;
		for(int i=0;i<markets.size();i++)
		{
			if(markets.get(i).market==m){
				switch(order){
				case "steak":
					markets.get(i).hasSteak = false;
					break;
				case "chicken":
					markets.get(i).hasChicken = false;
					break;
				case "salad":
					markets.get(i).hasSalad = false;
					break;
				case "pizza":
					markets.get(i).hasPizza = false;
					break;
				}
				if(markets.get(i).hasSteak == false && markets.get(i).hasChicken == false && 
						markets.get(i).hasSalad == false && markets.get(i).hasPizza == false)
					markets.get(i).ms = MarketState.noInventory;
			}
		}
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(state != OrderState.noFood){
			
			if(marketsOut == false){
				if(state == OrderState.lowInventory){
					orderFromMarket();
				}
				int temp=0;
				synchronized(this.markets){
					for(int i=0;i<markets.size();i++){
						if(markets.get(i).ms == MarketState.noInventory){ 
							temp++;
						}
					}
				}
				if(temp == 3) {
					System.out.println("All markets are out of inventory");
					marketsOut = true;
					state = OrderState.noFood;
				}
			}
			synchronized(this.marketOrders){
				for(int i=0;i<marketOrders.size();i++){
					if(marketOrders.get(i).st == OrderState.oweMarketMoney){ 
						tellCashierPayMarket(marketOrders.get(i));
						return true;
					}
				}
			}
			synchronized(this.orders){
				for(int i=0;i<orders.size();i++){
					if(orders.get(i).os == OrderState.pending){
						checkInventory(orders.get(i));
						return true;
					}
				}
			}
			synchronized(this.orders){
				for(int i=0;i<orders.size();i++){
					if(orders.get(i).os == OrderState.haveInventory){
						getFromFridge(orders.get(i));
						return true;
					}
				}
			}
			synchronized(this.orders){
				for(int i=0;i<orders.size();i++){
					if(orders.get(i).os == OrderState.gettingIngredients && orders.get(i).oe == OrderEvent.gotFromFridge){
						cookIt(orders.get(i));
						return true;
					}
				}
			}
			synchronized(this.orders){
				for(int i=0;i<orders.size();i++){
					if(orders.get(i).os == OrderState.cooking && orders.get(i).oe == OrderEvent.doneCooked){
						plateIt(orders.get(i));
						return true;
					}
				}
			}
		}
		return false;    
	}

	// Actions

	public void checkInventory(Order o){
		//System.out.println("Cook checking inventory for " + o.order);
		for (Map.Entry<String, Integer> entry : inv.entrySet()) { 
			if(entry.getKey() == o.order){
				if(entry.getValue()>0){
					o.os = OrderState.haveInventory;
				}
				else if(entry.getValue() == 0){
					//System.err.println("Cook out of inventory for " + o.order);
					if(state != OrderState.noFood){
						waiter.msgWaiterOutOfFood(o.order,o.tableNumber);
						o.os = OrderState.outOfInventory;
					}	
					//else System.out.println("Cook: Completely out of food");
				}
			}
			if(entry.getValue() < 3){
				state = OrderState.lowInventory;
			}
		}
		stateChanged();
	}
	
	public void orderFromMarket(){
		Do("Ordering from market");
		for (Map.Entry<String, Integer> entry : inv.entrySet()) { 
			while(entry.getValue() < 3){
				switch(entry.getKey()){
					case "steak":
						if(markets.get(0).hasSteak == true){
							markets.get(0).market.msgNeedFoodFromMarket(entry.getKey());
						}
						else if(markets.get(1).hasSteak == true){
							markets.get(1).market.msgNeedFoodFromMarket(entry.getKey());
						}
						else if(markets.get(2).hasSteak == true){
							markets.get(2).market.msgNeedFoodFromMarket(entry.getKey());
						}
						break;
					case "chicken":
						if(markets.get(0).hasChicken == true){
							markets.get(0).market.msgNeedFoodFromMarket(entry.getKey());
						}
						else if(markets.get(1).hasChicken == true){
							markets.get(1).market.msgNeedFoodFromMarket(entry.getKey());
						}
						else if(markets.get(2).hasChicken == true){
							markets.get(2).market.msgNeedFoodFromMarket(entry.getKey());
						}
						break;
					case "salad":
						if(markets.get(0).hasSalad == true){
							markets.get(0).market.msgNeedFoodFromMarket(entry.getKey());
						}
						else if(markets.get(1).hasSalad == true){
							markets.get(1).market.msgNeedFoodFromMarket(entry.getKey());
						}
						else if(markets.get(2).hasSalad == true){
							markets.get(2).market.msgNeedFoodFromMarket(entry.getKey());
						}
						break;
					case "pizza":
						if(markets.get(0).hasPizza == true){
							markets.get(0).market.msgNeedFoodFromMarket(entry.getKey());
						}
						else if(markets.get(1).hasPizza == true){
							markets.get(1).market.msgNeedFoodFromMarket(entry.getKey());
						}
						else if(markets.get(2).hasPizza == true){
							markets.get(2).market.msgNeedFoodFromMarket(entry.getKey());
						}
						break;
				} 
			}
		}
		state = OrderState.haveInventory;
				
		/*for(int i=0;i<markets.size();i++){
			switch(order){
			case "steak":
				if(markets.get(i).hasSteak == true){
					markets.get(i).market.msgNeedFoodFromMarket(order);
				}
				break;
			case "chicken":
				if(markets.get(i).hasChicken == true){
					markets.get(i).market.msgNeedFoodFromMarket(order);
				}
				break;
			case "salad":
				if(markets.get(i).hasSalad == true){
					markets.get(i).market.msgNeedFoodFromMarket(order);
				}
				break;
			case "pizza":
				if(markets.get(i).hasPizza == true){
					markets.get(i).market.msgNeedFoodFromMarket(order);
				}
				break;
			}
		} */
		/*
		for(int i = 0;i < markets.size(); i++){
			if(markets.get(i).market == m){
				markets.get(i).market.msgNeedFoodFromMarket(order);
			}
		}*/
	}
	
	public void tellCashierPayMarket(MarketOrder mo){
		cashier.msgPayMarket(mo.market,mo.food,mo.amount);
		marketOrders.remove(mo);
	}
	
	public void getFromFridge(final Order o){
		cookGui.DoGoToFridge();
		try {
			atFridge.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		o.os = OrderState.gettingIngredients;
		msgGotFoodFromFridge(o);
		stateChanged();
	}
	public void cookIt(final Order o){
		cookGui.DoGoToCookingArea();
		try {
			atCookingArea.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Food food = new Food(o.order);
		o.os = OrderState.cooking;
		for (Map.Entry<String, Integer> entry : inv.entrySet()) { 
			if(entry.getKey() == o.order){
				entry.setValue(entry.getValue()-1);
			//	System.err.println("Inventory for " + entry.getKey() + " is " + entry.getValue());
			} 
		}
		timer.schedule(new TimerTask() {
			public void run() {
				msgFoodCooked(o);
				//isHungry = false;
				stateChanged();
			}
		},
		food.cookTime);
	}
	public void plateIt(Order o){
		Do("Putting food on plate");
		cookGui.DoGoToCookingArea();
		try {
			atCookingArea.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cookGui.DoGoToPlatingArea();
		try {
			atPlatingArea.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		o.os = OrderState.done;
		o.waiter.msgOrderReadyForPickup(o.order, o.tableNumber);
		orders.remove(o);
		stateChanged();
	}

}

