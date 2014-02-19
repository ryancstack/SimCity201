package market;

import gui.Building;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import market.gui.MarketGui;
import market.interfaces.MarketWorker;
import market.interfaces.MarketCustomer;
import market.test.mock.EventLog;
import market.test.mock.LoggedEvent;
import restaurant.CashierInterface;
import restaurant.CookInterface;
import restaurant.Restaurant;
import trace.AlertLog;
import trace.AlertTag;
import agent.Role;
import city.TransportationRole;
import city.helpers.Directory;

public class MarketWorkerRole extends Role implements MarketWorker {

	//data--------------------------------------------------------------------------------
	public enum orderState {Ordered, CantFill, Filled, Billed, ReadyToDeliver, Paid, CantPay, Cancelled, InTransit};
	
	List<Order> MyOrders;
	List<RestaurantOrder> MyRestaurantOrders;
	enum marketState {NotStartedWorking, Working, Closed, DeliveringToRestaurants, DoneWorking};
	marketState state;
	boolean atWork;
	boolean deliverOrders;
	boolean jobDone;
	Map<String, MarketItemInformation> inventory;
//	Map<String, Food> inventory = new HashMap<String, Food>();
	double funds;
	Timer timer = new Timer();
	Market market;

	private Semaphore actionComplete = new Semaphore(0,true);
	private MarketGui gui;
	private String myLocation;
	
	public EventLog log;
	
	public class RestaurantOrder {
		CookInterface cook;
		CashierInterface cashier;
		String choice;
		List<String> choices;
		List<String> cantFill;
		List<String> filled;
		int amount;
		double price;
		orderState state;
		
		RestaurantOrder(CookInterface cook, CashierInterface cashier, String choice, int amount) {
			this.cook = cook;
			this.cashier = cashier;
			this.choice = choice;
			this.amount = amount;
			state = orderState.Ordered;
		}
		
		RestaurantOrder(CookInterface cook, CashierInterface cashier, List<String> choices, int amount) {
			this.cook = cook;
			this.cashier = cashier;
			this.choices = choices;
			this.cantFill = new ArrayList<String>();
			this.filled = new ArrayList<String>();
			this.amount = amount;
			state = orderState.Ordered;
		}

		public orderState getState() {
			return state;
		}

		public CookInterface getCook() {
			return cook;
		}

		public String getChoice() {
			return choice;
		}
		
		public List<String> getChoices() {
			return choices;
		}

		public double getPrice() {
			return price;
		}
	}
	
	public class Order {
	    MarketCustomer customer;
	    Map<String, Integer> groceryList = new HashMap<String, Integer>();
	    Map<String, Integer> retrievedGroceries = new HashMap<String, Integer>();
	    orderState state;
	    double price;
	    
	    Order(MarketCustomer c, Map<String, Integer> gl) {
	    	customer = c;
	    	groceryList = gl;
	    	state = orderState.Ordered;
	    }
	    
	    public MarketCustomer getCustomer() {
	    	return customer;
	    }
	    
	    public Map<String, Integer> getGroceryList() {
	    	return groceryList;
	    }

		public Map<String, Integer> getRetrievedGroceries() {
			return retrievedGroceries;
		}
	    
	    public orderState getState() {
	    	return state;
	    }

		public double getPrice() {
			return price;
		}
	}
	
	public MarketWorkerRole(String location) {		
		MyOrders = Collections.synchronizedList(new ArrayList<Order>());
		MyRestaurantOrders = Collections.synchronizedList(new ArrayList<RestaurantOrder>());

		state = marketState.NotStartedWorking;
		funds = 0.00;
		
		log = new EventLog();
		
		gui = new MarketGui(this);

		myLocation = location;
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui(gui);
			}
		}
	}
	
	//for unit testing
	public MarketWorkerRole() {		
		MyOrders = Collections.synchronizedList(new ArrayList<Order>());
		MyRestaurantOrders = Collections.synchronizedList(new ArrayList<RestaurantOrder>());
		state = marketState.NotStartedWorking;
		funds = 0.00;
		log = new EventLog();
		
		gui = new MarketGui(this);
	}
		
	
	public List<Order> getMyOrders() {
		return MyOrders;
	}
	public List<RestaurantOrder> getMyRestaurantOrders() {
		return MyRestaurantOrders;
	}
	public boolean getJobDone() {
		return jobDone;
	}
	public double getFunds() {
		return funds;
	}
	
	//MarketCustomer messages-------------------------------------------------------------
	public void msgGetGroceries(MarketCustomer customer, Map<String, Integer> groceryList) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Customer wants groceries");
		
	    MyOrders.add(new Order(customer, groceryList));
	    
	    log.add(new LoggedEvent("Received msgGetGroceries from MarketCustomer."));
	    
	    stateChanged();
	}
	
	public void msgHereIsMoney(MarketCustomer customer, double money) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Here is my money");
		
		synchronized(MyOrders){
		    for(Order o : MyOrders) {
		    	if(o.customer == customer)
		    		o.state = orderState.Paid;  
		    }
		}
		
		funds += money;
	    
	    log.add(new LoggedEvent("Received msgHereIsMoney from MarketCustomer. Amount = $" + money));
	    stateChanged();
	}
	
	public void msgCantAffordGroceries(MarketCustomer customer) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Customer can't afford the groceries");
		
		synchronized(MyOrders) {
			for(Order o : MyOrders) {
				if(o.customer == customer)
					o.state = orderState.CantPay;
			}
		}
		
	    log.add(new LoggedEvent("Received msgCantAffordGroceries from MarketCustomer."));
	    stateChanged();
	}
	
	//Restaurant messages-------------------------------------------------------------
	public void msgOrderFood(CookInterface cook, CashierInterface cashier, String choice, int amount) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Order this food");
		MyRestaurantOrders.add(new RestaurantOrder(cook, cashier, choice, amount));
		
		log.add(new LoggedEvent("Received msgOrderFood from Cook. Choice = " + choice));
		stateChanged();
	}
	
		/*No amount given*/
	public void msgOrderFood(CookInterface cook, CashierInterface cashier, String choice) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Order this food");
		MyRestaurantOrders.add(new RestaurantOrder(cook, cashier, choice, 5));
		
		log.add(new LoggedEvent("Received msgOrderFood from Cook. Choice = " + choice));
		stateChanged();
	}
	
		/*List of choices*/
	public void msgOrderFood(CookInterface cook, CashierInterface cashier, List<String> choices, int amount) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Order this food");
		MyRestaurantOrders.add(new RestaurantOrder(cook, cashier, choices, amount));
		
		log.add(new LoggedEvent("Received msgOrderFood from Cook. Choices = " + choices));
		stateChanged();
	}
	
	public void msgDeliverOrder(RestaurantOrder o) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Deliver this food");
		o.state = orderState.ReadyToDeliver;
		
		log.add(new LoggedEvent("Received msgDeliverOrder from Timer."));
		stateChanged();
	}
	
	public void msgPayForOrder(CashierInterface cashier, double funds) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Cashier paying for this food");
		this.funds += funds;
		
		synchronized(MyRestaurantOrders) {
			for(RestaurantOrder o : MyRestaurantOrders) {
				if(o.cashier == cashier)
					o.state = orderState.Paid;
			}
		}
		
		log.add(new LoggedEvent("Receieved msgPayFor Order from Cashier. Amount = $" + funds));
		stateChanged();
	}
	
	public void msgCannotPay(CashierInterface cashier, double funds) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Customer can't pay");
		synchronized(MyRestaurantOrders) {
			for(RestaurantOrder o : MyRestaurantOrders) {
				if(o.cashier == cashier)
					o.state = orderState.CantPay;
			}
		}
		
		log.add(new LoggedEvent("Receieved msgPayFor Order from Cashier. Amount = $" + funds));
		stateChanged();		
	}
	
	//Huang Restaurant messages----------------------------------------------------------
	public void msgCancelOrder(CookInterface cook) {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Cancelling order");
		synchronized(MyRestaurantOrders) {
			for (RestaurantOrder o : MyRestaurantOrders) {
				if(o.cook == cook) {
					o.state = orderState.Cancelled;
				}
			}
		}
		stateChanged();
	}	

	//Person/GUI messages-------------------------------------------------------------
	public void msgJobDone() {
		AlertLog.getInstance().logMessage(AlertTag.MARKETWORKER, getPersonAgent().getName(), "Finished job");
		state = marketState.Closed;
		
	    log.add(new LoggedEvent("Received msgJobDone from Person."));
		stateChanged();
	}
	
	public void msgActionComplete() {
		actionComplete.release();
		stateChanged();
	}
	
	//scheduler---------------------------------------------------------------------------
	public boolean pickAndExecuteAnAction() {
		if(state == marketState.NotStartedWorking) {
			ArriveAtJob();
			return true;
		}
		
		if(state == marketState.Closed) {
			CloseMarket();
			return true;
		}
		
		if(MyOrders.isEmpty() && MyRestaurantOrders.isEmpty() && state == marketState.DeliveringToRestaurants) {
			LeaveJob();
			return true;
		}		
		synchronized(MyOrders) {
			for(Order o : MyOrders) {
				if(o.state == orderState.Ordered) {
					if(jobDone == false)
						FillOrder(o);
					else
						TurnAwayCustomer(o);
					return true;
				}
			}
			
			for(Order o : MyOrders) {
				if(o.state == orderState.CantFill) {
					TurnAwayCustomer(o);
					return true;
				}
			}
			
			for(Order o : MyOrders) {
				if(o.state == orderState.Filled) {
					BillCustomer(o);
					return true;
				}
			}
			
			for(Order o : MyOrders) {
				if(o.state == orderState.Paid) {
						GiveGroceries(o);
					return true;
				}
			}
			
			for(Order o : MyOrders) {
				if(o.state == orderState.CantPay) {
					MyOrders.remove(o);
					
					log.add(new LoggedEvent("Deleting order."));
					return true;
					
				}
			}
		}
		if (state == marketState.DeliveringToRestaurants) {
			synchronized(MyRestaurantOrders) {
				for(RestaurantOrder o : MyRestaurantOrders) {
					if(o.state == orderState.Ordered) {
						FillRestaurantOrder(o);
						return true;
					}
				}
				for(RestaurantOrder o : MyRestaurantOrders) {
					if(o.state == orderState.Paid || o.state == orderState.Cancelled) {
						MyRestaurantOrders.remove(o);
						
						log.add(new LoggedEvent("Removing RestaurantOrder."));
						return true;
					}
				}
				for(RestaurantOrder o : MyRestaurantOrders) {
					if (o.state == orderState.InTransit) {
						DeliverOrder(o);
						return true;
					}
				}	
				for(RestaurantOrder o : MyRestaurantOrders) {
					if(o.state == orderState.ReadyToDeliver) {
						DriveToOrder(o);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	//Customer Order actions--------------------------------------------------------------
	private void FillOrder(Order o) {
		Iterator<String> i = o.groceryList.keySet().iterator();
		String choice;
		int amount;
		
	    while(i.hasNext()) {
	    	choice = (String) i.next();
	    	amount = o.groceryList.get(choice);
	    	
	    	if(inventory.get(choice).getSupply() >= amount) {
	    		o.price += inventory.get(choice).price * amount;
	    		o.retrievedGroceries.put(choice, amount);
	    		
	    		DoGetItem(choice); //GUI
	    		try {
	    			actionComplete.acquire();
	    		} catch (InterruptedException e) {
	    			e.printStackTrace();
	    		}
	    		
	    		inventory.get(choice).setSupply(inventory.get(choice).getSupply() - amount);
	    	}
	    }
		
	    if(o.retrievedGroceries.isEmpty())
	        o.state = orderState.CantFill;
	    else {
	        o.state = orderState.Filled;
	        
	        DoGoToCounter();
			try {
				actionComplete.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	    
	    log.add(new LoggedEvent("Got MarketCustomer order."));
	}
	private void TurnAwayCustomer(Order o) {
	    o.customer.msgCantFillOrder(o.groceryList);
	    MyOrders.remove(o);
	    
	    log.add(new LoggedEvent("Couldn't fill MarketCustomer's order."));
	}
	private void BillCustomer(Order o) {
	    o.customer.msgHereIsBill(o.price);
	    o.state = orderState.Billed;

	    log.add(new LoggedEvent("Sent MarketCustomer the bill."));
	}
	private void GiveGroceries(Order o) {
	    o.customer.msgHereAreYourGroceries(o.retrievedGroceries);
	    MyOrders.remove(o);
	    
	    log.add(new LoggedEvent("Gave MarketCustomer the groceries."));
	}

	//Restaurant Order actions--------------------------------------------------------------
	private void FillRestaurantOrder(final RestaurantOrder o) {		
		//If order is only one choice
		if(o.choices.isEmpty()) {
			if(inventory.get(o.choice).getSupply() >= o.amount) {
	    		inventory.get(o.choice).setSupply(inventory.get(o.choice).getSupply() - o.amount);
	    		
				o.cook.msgCanFillOrder(this, o.choice);
				log.add(new LoggedEvent("Filling Restaurant Order."));

				o.state = orderState.ReadyToDeliver;
			}
			else {
				log.add(new LoggedEvent("Can't fill RestaurantOrder."));
				o.cook.msgInventoryOut(this, o.choice);
				MyRestaurantOrders.remove(o);
			}
		}
		
		//If order is a list of choices
		else {
			for(String choice : o.choices) {
				if(inventory.get(choice).getSupply() >= o.amount) {
		    		inventory.get(choice).setSupply(inventory.get(choice).getSupply() - o.amount);
		    		
					o.filled.add(choice);
				}
				
				else {
					o.cantFill.add(choice);
				}
			}
			
			if(o.filled.isEmpty()) {
				o.cook.msgInventoryOut(this, o.cantFill, o.amount);
				MyRestaurantOrders.remove(o);
			}
			else {
				o.state = orderState.ReadyToDeliver;
			}
		}
	}
	
	private void DriveToOrder(RestaurantOrder o) {
		o.state = orderState.InTransit;
		
		//If order is only one choice
		if(o.choices.isEmpty()) 
			o.price = o.amount * inventory.get(o.choice).price;
		//If order is a list of choices
		else {
			for(String s : o.choices) {
				o.price += o.amount * inventory.get(s).price;
			}
		}
		
		Restaurant destination = null;
		List<Restaurant> restaurants = Directory.sharedInstance().getRestaurants();
		for (Restaurant r : restaurants) {
			if (r.getCashier() == o.cashier) {
				destination = r;
				print(destination.getName());
				break;
			}
		}
		
		if(destination.isOpen()) {
			o.state = orderState.InTransit;
			Role t = new TransportationRole(destination.getName(), getPersonAgent().getCurrentLocation());
			t.setPerson(getPersonAgent());
			getPersonAgent().addRole(t);
		}
	}
	private void DeliverOrder(RestaurantOrder o) {
		o.state = orderState.Billed;
	
		//If order is only one choice
		if(o.choices.isEmpty()) {
			o.price = o.amount * inventory.get(o.choice).price;
			
	
			o.cook.msgMarketDeliveringOrder(o.amount, o.choice);
			o.cashier.msgGiveBill(new MarketCheck(o.price, o.choice, o.amount, this));
		}
		
		//If order is a list of choices
		else {
			for(String choice : o.choices) {
				o.price += o.amount * inventory.get(choice).price;
			}
	
			o.cook.msgMarketDeliveringOrder(o.amount, o.choices);
			o.cashier.msgGiveBill(new MarketCheck(o.price, o.choices, o.amount, this));
			
			log.add(new LoggedEvent("Delivered order."));
		}
	}
	
	//PersonAgent actions----------------------------------------------------------------
	private void ArriveAtJob() {
		DoEnterMarket();
		
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		Directory.sharedInstance().marketDirectory.get(myLocation).setOpen();
		state = marketState.Working;
	}
	
	private void CloseMarket() {
		Directory.sharedInstance().marketDirectory.get(myLocation).setClosed();

		DoLeaveMarket();
		
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		gui.setIsNotPresent();
		state = marketState.DeliveringToRestaurants;		
	}
	
	private void LeaveJob() {
		log.add(new LoggedEvent("MarketRole leaving job"));
		getPersonAgent().setFunds(getPersonAgent().getFunds() + funds);
		state = marketState.NotStartedWorking;
		
		getPersonAgent().msgRoleFinished();
	}

	//GUI Actions-------------------------------------------------------------------------
	private void DoEnterMarket() {
		gui.setPresent();
		gui.DoEnterMarket();
	}
	
	private void DoLeaveMarket() {
		gui.DoLeaveMarket();		
	}
	
	private void DoGetItem(String s) {
		gui.DoGetFood();		
	}
	
	private void DoGoToCounter() {
		gui.DoGoToCounter();
	}

	public String getName() {
		if(getPersonAgent() != null) {
			return getPersonAgent().getName();
		}
		else {
			return "";
		}
	}
	
	public Market getMarket() {
		return market;
	}

	public void setMarket(Market market) {
		this.market = market;
		inventory = getMarket().getFoodInventory();
	}
	
}
