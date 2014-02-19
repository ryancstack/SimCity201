package restaurant.nakamuraRestaurant;

import gui.Building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import city.helpers.Directory;
import restaurant.nakamuraRestaurant.gui.HostGui;
import agent.Agent;

/**
 * NakamuraRestaurant Host Agent
 */
public class NakamuraHostAgent extends Agent {
	static final int NTABLES = 4;
	public List<NakamuraCustomerRole> waitingCustomers
		= Collections.synchronizedList(new ArrayList<NakamuraCustomerRole>());
    private List<Waiters> waiters = Collections.synchronizedList(new ArrayList<Waiters>());
	public Collection<Table> tables;
	public enum TableState {empty, occupied, dirty};
	public enum WaiterState {arrived, working, askedforbreak, onbreak, doneWork};
	public enum CookState {noCook, arrived, working, doneWork};
	private Semaphore actionComplete = new Semaphore(0,true);

	private String name;

	public HostGui hostGui = null;
	public NakamuraCookRole cook = null;
	boolean newCook = false;
	CookState cookState;

	public NakamuraHostAgent(String name) {
		super();

		this.name = name;
		// make some tables
		tables = Collections.synchronizedList(new ArrayList<Table>(NTABLES));
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
		hostGui = new HostGui(this);
		
		cookState = CookState.noCook;
	}

	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List<NakamuraCustomerRole> getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection<Table> getTables() {
		return tables;
	}
	
	public boolean tablesFull() {
		synchronized(tables) {
			for(Table table : tables) {
				if(!table.isOccupied())
					return false;
			}
		}
		return true;
	}
	// Messages
	
	public void msgNewWaiter(NakamuraWaiterRole waiter) {
		waiters.add(new Waiters(waiter));
		stateChanged();
	}
	
	public void msgNoNewCustomers(NakamuraWaiterRole waiter) {
		synchronized(waiters) {
			for(Waiters w : waiters) {
				if(w.waiter == waiter) {
					w.state = WaiterState.doneWork;
				}
			}
		}
	}
	
	public void msgWaiterLeaving(NakamuraWaiterRole waiter) {
		synchronized(waiters) {
			for(Waiters w : waiters) {
				if(w.waiter == waiter) {
					waiters.remove(w);
					break;
				}
			}
		}
		
		stateChanged();
	}
	
	public void msgNewCook(NakamuraCookRole c) {
		this.cook = c;
		cookState = CookState.arrived;
		stateChanged();
	}
	
	public void msgCookDone(NakamuraCookRole c) {
		cookState = CookState.doneWork;
		stateChanged();
	}

	public void msgIWantFood(NakamuraCustomerRole cust) {
		print("Received msgIWantFood");
		if(!tablesFull())
			waitingCustomers.add(cust);
		
		stateChanged();
	}
	
	public void msgLeaving(NakamuraCustomerRole cust) {
		print("Received msgLeaving");
		stateChanged();
	}
	
	public void msgStaying(NakamuraCustomerRole cust) {
		print("Received msgStaying");
		waitingCustomers.add(cust);
		stateChanged();
	}
	
	public void msgGoingOnBreak(NakamuraWaiterRole w) {
		print("Received msgGoingOnBreak");
		synchronized(waiters) {
			for(Waiters wait : waiters) {
				if(wait.waiter == w) 
					wait.state = WaiterState.askedforbreak;
			}
		}
		stateChanged();
	}
	
	public void msgBackToWork(NakamuraWaiterRole w) {
		print("received msgBackToWork");
		synchronized(waiters) {
			for(Waiters wait : waiters) {
				if(wait.waiter == w) {
					wait.state = WaiterState.working;
					stateChanged();
				}
			}
		}
	}

	public void msgTableEmpty(int tablenum) {
		print("Received msgTableEmpty");
		synchronized(tables) {
			for (Table table : tables) {
				if (table.tableNumber == tablenum) {
					table.s = TableState.dirty;
					stateChanged();
				}
			}
		}
	}
	
	public void msgAnimationSeatsUpdated() {
		actionComplete.release();
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		if(cookState == CookState.arrived) {
			NotifyWaitersOfCook();
			return true;
		}
		
		if(cookState == CookState.doneWork) {
			if(waiters.isEmpty()) {
				ReleaseCook();
			}
		}
		
		
		synchronized(tables) {
			for (Table table : tables) {
				if (table.s == TableState.dirty) {
					cleanTable(table);
					return true;
				}
			}
		}
		
		synchronized(waiters) {
			for(Waiters w : waiters) {
				if(w.state == WaiterState.arrived) {
					PutWaiterToWork(w);
				}
			}
			
			for(Waiters w : waiters) {
				if(w.state == WaiterState.askedforbreak) {
					if(waiters.size() > 1) {	//Only 1 Waiter
						for(Waiters wait : waiters) {
							if(wait.state != WaiterState.onbreak && !wait.waiter.equals(w.waiter)) { //Another Waiter not on break
								w.waiter.msgGoOnBreak();
								w.state = WaiterState.onbreak;
								return true;
							}
						}
						w.waiter.msgBreakDenied();
						w.state = WaiterState.working;
					}
					else {
						w.waiter.msgBreakDenied();
						w.state = WaiterState.working;
					}
					return true;
				}
			}
		}

		if(cookState == CookState.noCook || waiters.isEmpty()) {
			synchronized(waitingCustomers) {
				for(NakamuraCustomerRole customer : waitingCustomers) {
					customer.msgRestaurantClosed();
				}
				
				waitingCustomers.clear();
			}
			return true;
		}
		
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		synchronized(tables) {
			for (Table table : tables) {
				if (!table.isOccupied()) {
					if (!waitingCustomers.isEmpty()) {
						if(!waiters.isEmpty()) {
							seatCustomer(waitingCustomers.get(0), table);//the action
							return true;//return true to the abstract agent to reinvoke the scheduler.
						}
					}
				}
			}
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void seatCustomer(NakamuraCustomerRole customer, Table table) {
		int least = 0;
		synchronized(waiters) {
			for(Waiters w: waiters){ //Find waiter with least customers
				if(w.getCustomers() < waiters.get(least).getCustomers() && w.state == WaiterState.working)
						least = waiters.indexOf(w);		
			}
		}
		
		waiters.get(least).waiter.msgNewCustomer(customer, table.tableNumber);
		waiters.get(least).addCustomer();
		table.setOccupant(customer, waiters.get(least));
		waitingCustomers.remove(customer);
		
		
		hostGui.DoUpdateSeat(waitingCustomers);
		try{
			actionComplete.acquire();
		} 
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}			
	}

	private void cleanTable(Table table) {
		synchronized(waiters) {
			for(Waiters w : waiters) {
				if(w == table.getWaiter())
					w.removeCustomer();
			}		
		}
		table.setUnoccupied();	
	}
	
	private void PutWaiterToWork(Waiters w) {
		w.waiter.msgSetCook(cook);
		w.state = WaiterState.working;
	}
	
	private void NotifyWaitersOfCook() {
		synchronized(waiters) {
			for(Waiters w : waiters) {
				w.waiter.msgSetCook(cook);
			}
		}
		cookState = CookState.working;
	}
	
	private void ReleaseCook() {
		cook.msgYouMayGo();
		this.cook = null;
		cookState = CookState.noCook;
	}

	//utilities

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}

	private class Table {
		NakamuraCustomerRole occupiedBy;
		Waiters servedBy;
		int tableNumber;
		TableState s;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
			s = TableState.empty;
		}

		void setOccupant(NakamuraCustomerRole cust, Waiters waiter) {
			occupiedBy = cust;
			servedBy = waiter;
			s = TableState.occupied;
		}

		void setUnoccupied() {
			occupiedBy = null;
			servedBy = null;
			s = TableState.empty;
		}

		NakamuraCustomerRole getOccupant() {
			return occupiedBy;
		}
		
		Waiters getWaiter() {
			return servedBy;
		}

		boolean isOccupied() {
			return s != s.empty;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}
	
	private class Waiters {
		NakamuraWaiterRole waiter;
		int numCustomers;
		WaiterState state;

		Waiters(NakamuraWaiterRole w) {
			this.waiter = w;
			this.state = WaiterState.arrived;
		}

		void addCustomer() {
			numCustomers++;
		}

		void removeCustomer() {
			numCustomers--;
		}
		
		int getCustomers() {
			return numCustomers;
		}
	}
}

