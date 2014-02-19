package restaurant.shehRestaurant;

import agent.Role;
import restaurant.CashierAgent;
import restaurant.shehRestaurant.helpers.Bill;
import restaurant.shehRestaurant.helpers.Menu;
import restaurant.shehRestaurant.gui.WaiterGui;
import restaurant.shehRestaurant.helpers.Table;
import restaurant.shehRestaurant.interfaces.Cashier;
import restaurant.shehRestaurant.interfaces.Waiter;
import gui.Building;
import gui.Gui;

import java.util.*;
import java.util.concurrent.Semaphore;

import city.helpers.Directory;

/**
 * Restaurant Waiter Agent
 */
public class ShehWaiterRole extends Role implements Waiter {
	static final int NTABLES = 3;
	public List<ShehCustomerRole> waitingCustomers = Collections.synchronizedList(new ArrayList<ShehCustomerRole>());
	private List<myCustomer> customers = Collections.synchronizedList(new ArrayList<myCustomer>());
	public ArrayList<Table> tables;
	
	protected ShehCookRole cook;
	private ShehHostAgent host;
	protected ShehCashierAgent cashier;

	private Boolean breakGranted = false;

	private Menu menu;
	private Bill bill;
	
	protected ShehRestaurant restaurant = (ShehRestaurant) Directory.sharedInstance().getRestaurants().get(3);
	
	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atKiosk = new Semaphore(0,true);
	protected Semaphore atKitchen = new Semaphore(0,true);
	
	private double moneyEarned = 0;
	
	private int homePosition = 0;

	public WaiterGui waiterGui = null;

	public class myCustomer {
		ShehCustomerRole c;
		Table t;
		String o;
		CustomerState s;

		public myCustomer(ShehCustomerRole customer, Table table, CustomerState state) {
			c = customer;
			t = table;
			s = state;
			o = null;
		}
	}

	public enum AgentState 
		{NotArrived, JustArrived, Working, GettingPayCheck, ReceivingPayCheck, LeavingWork};
		
		AgentState state = AgentState.NotArrived;
	
	public enum CustomerState
		{WaitingInRestaurant, BeingSeated, Seated, ReadyToOrder, Ordering, ReOrdering, DoneOrdering, Waiting, ReceivingFood, Eating, AskingForBill, WaitingForBill, BeingBilled,
		Paying, Gone};
	
	public ShehWaiterRole(String location) {
		super();
		
		host = (ShehHostAgent) Directory.sharedInstance().getAgents().get("ShehRestaurantHost");
		cashier = (ShehCashierAgent) Directory.sharedInstance().getAgents().get("ShehRestaurantCashier");
		
		waiterGui = new WaiterGui(this);
		
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		
		for(Building b : buildings) {
			if (b.getName() == location) {
				b.addGui(waiterGui);
			}
		}
		
		state = AgentState.JustArrived;
	}
	
	public ShehWaiterRole(String name, ShehCashierAgent ca, ShehCookRole co, ShehHostAgent h) {
		super();

		this.name = name;
		
		cashier = ca;
		cook = co;
		host = h;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Table> getTables() {
		return tables;
	}
	
	// Messages
	/*public void msgIWantFood(CustomerAgent cust) {
		waitingCustomers.add(cust);
		stateChanged();
	}*/
	
	public void msgHomePosition(int num) {
		homePosition = num;
	}

	
	public void msgSeatThisCustomer(ShehCustomerRole cust, Table table) {
		customers.add(new myCustomer(cust, table, CustomerState.WaitingInRestaurant));
		stateChanged();
	}
	
	public void msgImReadyToOrder(ShehCustomerRole cust) {
		synchronized(customers) {
			for(myCustomer c : customers) {
				if(c.c == cust) {
					c.s = CustomerState.ReadyToOrder;
					stateChanged();
				}
			}
		}
	}

	public void msgOrderFood(ShehCustomerRole cust, String choice) {
		synchronized(customers) {	
			for(myCustomer c : customers) {
				if(c.c == cust) {
					c.s = CustomerState.DoneOrdering;
					c.o = choice;
					stateChanged();
				}
			}
		}
	}
	
	public void msgOutOfFood(int t, String o) {
		synchronized(customers) {
			for(myCustomer c : customers) {
				if(c.t.getTableNumber() == t) {
					c.s = CustomerState.ReOrdering;
					stateChanged();
				}
			}
		}
	}
	
	public void msgOrderIsCooked(int t, String o) {
		synchronized(customers) {	
			for(myCustomer c : customers) {
				if(c.t.getTableNumber() == t) {
					c.s = CustomerState.ReceivingFood;
					stateChanged();
				}
			}
		}
	}
	
	public void msgBillPlease(ShehCustomerRole cust) {
		synchronized(customers) {
			for(myCustomer c : customers) {
				if(c.c == cust) {
					c.s = CustomerState.AskingForBill;
					stateChanged();
				}
			}
		}
	}
	
	public void msgCollectBill(Bill b) {
		bill = b; //necessary?'
		synchronized(customers) {
			for(myCustomer c : customers) {
				if(c.c == b.c) {
					c.s = CustomerState.WaitingForBill;
					stateChanged();
				}
			}	
		}
	}

	public void msgLeavingTable(ShehCustomerRole cust) {
		synchronized(tables) {
			for (Table table : tables) {
				if (table.getOccupant() == cust) {
					print(cust + " leaving " + table);
					table.setUnoccupied();
					stateChanged();
				}
			}
		}
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atTable.release();// = true;
		stateChanged();
	}
	
	public void msgAtKiosk() {
		atKiosk.release();
		stateChanged();
	}
	
	public void msgAtKitchen() {
		atKitchen.release();
		stateChanged();
	}
	
	public void msgImTired() {
		print("I want to go on break");
		host.msgIWantToGoOnBreak(this);
	}
	
	public void msgBackFromBreak() {
		print("I'm back from my break");
		host.msgImBackFromBreak(this);
	}
	
	public void msgBreakRequestAccepted() {
		print("I'll go on break after serving all my customers.");
		breakGranted = true;
	}
	
	public void msgBreakRequestDenied() {
		print("Aww, I'm so tired.");
		breakGranted = false;
	}
	
	public void msgJobDone() {
		print("I'm done with work.");
		state = AgentState.GettingPayCheck;
		stateChanged();
	}
	
	public void msgHereIsPayCheck(Bill b) {
		print("Received my paycheck, received: $ " + b.m);
		
		moneyEarned = b.m;
		state = AgentState.LeavingWork;
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(state == AgentState.JustArrived) {
			checkInWithHost();
			return true;
		}
		
		synchronized(customers) {
			for (myCustomer c : customers) {
				if (c.s == CustomerState.WaitingInRestaurant) {
					seatCustomer(c);
					return true;
				}
			}
		}
		
		synchronized(customers) {
			for (myCustomer c : customers) {
				if (c.s == CustomerState.ReadyToOrder) {
					WhatWouldYouLike(c);
					return true;
				}
			}
		}
		
		synchronized(customers) {	
			for (myCustomer c : customers) {
				if (c.s == CustomerState.ReOrdering) {
					OrderSomethingElse(c);
					return true;
				}
			}
		}
		
		synchronized(customers) {
			for (myCustomer c : customers) {
				if (c.s == CustomerState.DoneOrdering) {
					CookThisOrder(c);
					return true;
				}
			}
		}
		
		synchronized(customers) {
			for (myCustomer c : customers) {
				if (c.s == CustomerState.ReceivingFood) {
					HereIsYourFood(c);
					return true;
				}
			}
		}
		
		synchronized(customers) {
			for (myCustomer c : customers) {
				if (c.s == CustomerState.AskingForBill) {
					RequestBill(c);
					return true;
				}
			}
		}
		
		synchronized(customers) {
			for (myCustomer c: customers) {
				if (c.s == CustomerState.WaitingForBill) {
					DeliverBill(c);
					return true;
				}
			}
		}
		
		synchronized(customers) {
			for (myCustomer c : customers) {
				if(c.s == CustomerState.Paying) {
					checkWaiterAvailability(c);
					return true;
				}
			}
		}
		
		synchronized(customers) {
			for (myCustomer c : customers) {
				if(c.s == CustomerState.Gone) 
					return true;
			}
		}
		
		if(state == AgentState.GettingPayCheck) {
			collectPayCheck();
			return true;
		}
		
		waiterGui.DoStandby(homePosition);
		return false;
	}

	// Actions
	
	private void checkInWithHost() {
		host.msgWaiterIsPresent(this);
		waiterGui.DoStandby(homePosition);
		state = AgentState.Working;
	}

	private void seatCustomer(myCustomer c) {
		waiterGui.DoGoToKiosk();
		try {
			atKiosk.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Do("I'm your waiter.");
		c.c.msgSitAtTable(c.t, new Menu());
		//host.msgRemoveWaitingCustomer(c.c);
		DoSeatCustomer(c.c, c.t);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		waiterGui.DoStandby(homePosition);
		c.s = CustomerState.Seated;
		stateChanged();
	}
	
	private void WhatWouldYouLike(myCustomer c) {
		DoTakeOrder(c.t);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Do("What would you like to order?");
		c.c.msgWhatWouldYouLike();
		c.s = CustomerState.Ordering;
		stateChanged();
	}
	
	private void OrderSomethingElse(myCustomer c) {
		DoTakeOrder(c.t);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Do("I'm sorry, we're out of that order, please order something else");
		c.c.msgOrderSomethingElse();
		c.s = CustomerState.Ordering;
		stateChanged();
	}
	
	protected void CookThisOrder(myCustomer c) {
		/*
		waiterGui.DoGoToKitchen(); //change to cooking area later
		try {
			atKitchen.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cook.msgCookThisOrder(this, c.o, c.t.getTableNumber(), cashier);
		c.s = CustomerState.Waiting;
		stateChanged();
		*/
	}
	
	private void HereIsYourFood(myCustomer c) {
		waiterGui.DoGoToKitchen(); //THIS ISN'T WORKING
		try {
			atKitchen.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		DoDeliverFood(c.t, c.o); 
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Do("Here is your food");
		c.c.msgHereIsYourFood();
		c.s = CustomerState.Eating;
		stateChanged();
		
		//waiterGui.DoLeaveCustomer();
		waiterGui.DoStandby(homePosition);
	}
	
	private void RequestBill(myCustomer c) {
		waiterGui.GoToTable(c.t); //change location to cashier's location
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Do("I need a bill for " + c.t + ".");
		cashier.msgProcessThisBill(c.o, c.c, this);
		c.s = CustomerState.BeingBilled;
		stateChanged();
	}
	
	private void DeliverBill(myCustomer c) {
		waiterGui.GoToTable(c.t);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Do("Here is your bill");
		c.c.msgHereIsYourBill(bill, cashier);
		c.s = CustomerState.Paying;
		stateChanged();
	
		waiterGui.DoStandby(homePosition);
	}
	
	private void checkWaiterAvailability(myCustomer c) {
		if(customers.size() == 1 && breakGranted) {
			print("I finished serving all my customers.");
			host.msgFreeOfCustomers(this);
			c.s = CustomerState.Gone;
			stateChanged();
			
			waiterGui.DoGoOnBreak();
			try {
				atKiosk.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
		}
		customers.remove(c);
	}
	
	private void collectPayCheck() {
		print("I need to collect my paycheck.");
		cashier.msgCollectPayCheck(this);
		state = AgentState.ReceivingPayCheck;
	}

	//Animation
	private void DoSeatCustomer(ShehCustomerRole customer, Table table) {
		print("Seating " + customer + " at " + table);
		waiterGui.DoBringToTable(customer, table);
	}
	
	private void DoTakeOrder(Table table) {
		waiterGui.GoToTable(table);
	}
	
	private void DoDeliverFood(Table table, String order) {
		waiterGui.DeliverFoodToTable(table, order/*, null*/);
	}

	//Utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}


	public void setCook(ShehCookRole cook) {
		this.cook = cook;	
	}
	
}