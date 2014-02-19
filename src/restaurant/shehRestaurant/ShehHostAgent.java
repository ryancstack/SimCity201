package restaurant.shehRestaurant;

import agent.Agent;
import restaurant.shehRestaurant.gui.HostGui;
import restaurant.shehRestaurant.helpers.Table;
import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */  

public class ShehHostAgent extends Agent {
	static final int NTABLES = 3;
	public List<ShehCustomerRole> waitingCustomers = Collections.synchronizedList(new ArrayList<ShehCustomerRole>());
	public List<ShehWaiterRole> waiters = Collections.synchronizedList(new ArrayList<ShehWaiterRole>());
	//private Cashier cashier;
	
	public List<myWaiter> breakWaiters = Collections.synchronizedList(new ArrayList<myWaiter>());
	private List<myCustomer> customers = Collections.synchronizedList(new ArrayList<myCustomer>());
	private ShehCookRole cook;
	
	public ArrayList<Table> tables;

	private Semaphore atTable = new Semaphore(0,true);

	public HostGui hostGui = null;
	
	Boolean cookPresent = false;
	private int counter = 0;
	private int numOfCustomers = 0;
	
	ShehRestaurant restaurant;

	public ShehHostAgent() {
		super();
		
		//cashier = (ShehCashierAgent) Directory.sharedInstance().getAgents().get("ShehRestaurantCashier");
		tables = new ArrayList<Table>(NTABLES);
		synchronized(tables) {
			for (int ix = 1; ix <= NTABLES; ix++) {
				tables.add(new Table(ix));
			} 
		}
	}
	
	public class myCustomer {
		ShehCustomerRole c;
		CustomerState s;

		public myCustomer(ShehCustomerRole customer, CustomerState state) {
			c = customer;
			s = state;
		}
	}
	
	public class myWaiter {
		ShehWaiterRole w;
		WaiterState s;
		
		public myWaiter(ShehWaiterRole waiter, WaiterState state) {
			w = waiter;
			s = state;
		}
	}

	public enum CustomerState
	{Waiting, Thinking, Leaving};
	
	public enum WaiterState
	{Waiting, WantBreak, OnBreak, Gone, Returned};

	public ArrayList<Table> getTables() {
		return tables;
	}
	
	// Messages
	public void msgWaiterIsPresent(ShehWaiterRole waiter) {
		print(waiter.getPersonAgent().getName() + " clocked in");
		waiters.add(waiter);
	}
	
	public void msgCookIsPresent(ShehCookRole cookRole) {
		print(cookRole.getPersonAgent().getName() + " clocked in");
		cook = cookRole;
		cookPresent = true;
	}
	
	public void msgIWantFood(ShehCustomerRole cust) {
		waitingCustomers.add(cust);
		stateChanged();
		//numOfCustomers++;
	}
	
	public void msgIWantToGoOnBreak(ShehWaiterRole waiter) {
		print("Let's me check if you can go on break right now.");
		breakWaiters.add(new myWaiter(waiter, WaiterState.WantBreak));
		stateChanged();
	}
	
	public void msgImBackFromBreak(ShehWaiterRole waiter) {
		print("Welcome back, I'll assign customers to you.");
		breakWaiters.remove(waiter);
		waiters.add(waiter);
	}
	
	public void msgFreeOfCustomers(ShehWaiterRole waiter) {
		print("You may go on break");
		synchronized(breakWaiters) {
			for(myWaiter w : breakWaiters) {
				if(w.w == waiter) {
					w.s = WaiterState.OnBreak;
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
					
					numOfCustomers--;
				}
			}
		}
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
		atTable.release();// = true;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		
		if (waiters.size() == 0) {
			noWaiters();
			//return true;
		}
		
		/*
		if (waitingCustomers.size() > 0) {
			organizeCustomers();
			//return true;
		}
		*/
		
		if(waiters.size() > 0 && cookPresent == true) { //if there are waiters and customers
			if(waitingCustomers.size() >= 1) {
				organizeCustomers();
				if(numOfCustomers <= 3) {
					synchronized(tables) {
						for (Table table : tables) {
							if(!table.isOccupied() && !waitingCustomers.isEmpty() /*waitingCustomers.size() >= 1*/) { //there are available tables
								assignWaiterToCustomer(waitingCustomers.get(0), waiters, table); //seat them
								return true;
							}
						}
					}
				}
			}
		}
		
		synchronized(breakWaiters) {
			for(myWaiter bw : breakWaiters) {
				if(bw.s == WaiterState.WantBreak) {
					checkForBreak(bw);
					return true;
				}
			}
		}
		
		synchronized(breakWaiters) {
			for(myWaiter bw : breakWaiters) {
				if(bw.s == WaiterState.OnBreak) {
					reorganizeWaiters(bw);
					return true;
				}
			}
		}
			
		synchronized(breakWaiters) {
			for(myWaiter bw : breakWaiters) { //Wait going on Break
				if(bw.s == WaiterState.Gone)
					return true;
			}
		}
			
		synchronized(breakWaiters) {
			for(myCustomer c : customers) { //Reconsidering Staying Scenario
				if(c.s == CustomerState.Thinking) {
					return true;
				}
			}
		}
		
		return false;
	}
 
	// Actions
	private void noWaiters() {
		print("We are not open.");
		synchronized(waitingCustomers) {
			for(ShehCustomerRole cust : waitingCustomers) {
				cust.msgRestaurantIsClosed();
				waitingCustomers.remove(cust);
				break;
			}
		}

	}
	
	private void organizeCustomers() { //alerts customer which location they should wai
		print("We have waiters and I'm going to organize them.");
		int queue = 0;
		int num = 0;
		synchronized(waiters) {
			for(ShehWaiterRole w : waiters) {
				w.setCook(cook);
			}
		}
		num = (waitingCustomers.size() + queue -1) % 10;
		queue++;
		waitingCustomers.get(waitingCustomers.size()-1).msgThisIsYourNumber(num); //causes indexing errors
	}
	
	private void assignWaiterToCustomer(ShehCustomerRole customer, List<ShehWaiterRole> w, Table table) {
		//organizeCustomers();
		Do("Welcome, I'll assign a waiter to you.");
		int waiterIndex = w.size() + counter;
		int queue = waiterIndex % w.size();
		counter++;
		numOfCustomers++;

		ShehWaiterRole waiter = w.get(queue);
		
		waiter.msgSeatThisCustomer(customer, table);
		table.setOccupant(customer);
		customer.setWaiter(waiter);
	
		waitingCustomers.remove(customer);
		
		synchronized(customers) {
			for(myCustomer c : customers) {
				if(c.c == customer) {
					customers.remove(customer);
				}
			}
		}
	}
	
	private void tablesAreFull(ShehCustomerRole customer) {
		Do("We're at capacity. Would you like to put your name down?");

		//customer.msgTablesAreFull();

		waitingCustomers.remove(customer);
	}
	
	private void checkForBreak(myWaiter w) {
		if(waiters.size() >= 2) {
			print("You may go on break, we have plenty of waiters.");
			w.w.msgBreakRequestAccepted();
		}
		if(waiters.size() == 1) {
			print("You're our only waiter, you'll have to wait.");
			w.w.msgBreakRequestDenied();
		}
		w.s = WaiterState.Waiting;
		stateChanged();
	}
	
	private void reorganizeWaiters(myWaiter w) {
		print("Updating our available waiters.");
		w.s = WaiterState.Gone;
		stateChanged();
	}
	//Utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}
	
	public HostGui getGui() {
		return hostGui;
	}

	public void setRestaurant(ShehRestaurant rest) {
		this.restaurant = rest;	
	}
	
	public ShehRestaurant getRestaurant() {
		return restaurant;
	}
}