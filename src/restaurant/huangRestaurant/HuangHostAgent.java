package restaurant.huangRestaurant;

import agent.Agent;
import agent.Role;
import restaurant.Restaurant;
import restaurant.huangRestaurant.HuangWaiterRole.CustomerState;
import restaurant.huangRestaurant.gui.HostGui;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class HuangHostAgent extends Agent {
	public enum HostState {
		
	}
	HostState state;
	static final int NTABLES = 4;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	static final int NWAITERS = 0;
	public enum CustomerState {hasWaiter, noWaiter, toldFull};
	private class HungryCustomer {
		HuangCustomerRole c;
		CustomerState state;
		HungryCustomer(HuangCustomerRole c) {
			this.c = c;
			this.state = CustomerState.noWaiter;
			c.getGui().setWaitingPos(hungryCustomers.size());
		}
	}
	public List<HuangCustomerRole> customers = Collections.synchronizedList(new ArrayList<HuangCustomerRole>());
	public List<HungryCustomer> hungryCustomers = Collections.synchronizedList(new ArrayList<HungryCustomer>());
	public List<MyCook> cooks = Collections.synchronizedList(new ArrayList<MyCook>());
	public List<MyWaiter> waiters = Collections.synchronizedList(new ArrayList<MyWaiter>());
	public Collection<Table> tables;
	private HuangRestaurant restaurant;
	private HuangCashierAgent ca;
	
	public enum WaiterState {Free, Busy, NeedsToKnowCook};
	public enum WaiterEvent {JustArrived, Working, wantsBreak, onBreak, breakAllowed, DoneWorking};
	private class MyWaiter {
		HuangWaiterRole w;
		WaiterState state;
		WaiterEvent event;
		
		MyWaiter(HuangHostAgent host, HuangWaiterRole w) {
			this.w = w;
			state = WaiterState.Free;
			event = WaiterEvent.Working;
			w.getGui().setHome(waiters.size());
			w.setRestaurant(restaurant);
		}
	}
	public enum CookState {JustArrived, OnShift, OffShift, AllowedToLeave, AskedToLeave};
	private class MyCook {
		CookState state;
		HuangCookRole c;
		MyCook(HuangCookRole c) {
			state = CookState.JustArrived;
			this.c = c;
			c.setRestaurant(restaurant);
		}
	}
	//note that tables is typed with Collection semantics.
    private static final int tableSpawnX = 160;
	private static final int tableSpawnY = 170;
	private static final int tableOffSetX = 180;
	//Later we will see how it is implemented
	
	private class Table {
		HuangCustomerRole occupiedBy;
		int tableNumber;
		int tableX;
		int tableY;
		boolean isDrawn = false; 

		Table(int tableNumber, int tableX, int tableY) {
			this.tableNumber = tableNumber;
			this.tableX = tableX;
			this.tableY = tableY;
			
		}

		void setOccupant(HuangCustomerRole cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		HuangCustomerRole getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			if (occupiedBy == null) {
				return false;
			}
			else {
				return true;
			}
		}
		public String toString() {
			return "table " + tableNumber;
		}
		void setDrawn() {
			isDrawn = true;
		}
	}

	private String name;
	public HostGui hostGui = null;

	public HuangHostAgent(String name, HuangCashierAgent ca) {
		super();
		this.ca = ca;
		this.name = name;
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 0; ix < NTABLES; ix++) {
			tables.add(new Table(ix, tableSpawnX + (tableOffSetX * ix), tableSpawnY));//how you add to a collections
		}
	}
	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List<HuangCustomerRole> getWaitingCustomers() {
		return customers;
	}
	public void addWaiter(HuangWaiterRole w) {
		waiters.add(new MyWaiter (this, w));
		stateChanged();
	}
	public Collection getTables() {
		return tables;
	}
	// Messages
	public void msgLeavingRest(HuangCustomerRole c) {
		synchronized(hungryCustomers) {
			for(HungryCustomer hc : hungryCustomers) {
				if(hc.c.equals(c)) {
					hungryCustomers.remove(hc);
					break;
				}
			}
		}
		synchronized(customers) {
			customers.remove(c);
		}
	}
	public void msgIWantToEat(HuangCustomerRole c) {
		customers.add(c);
		hungryCustomers.add(new HungryCustomer(c));
		System.out.println("msgIWantToEat received: Adding new hungry customer");
		stateChanged();
	}
	public void msgTableIsFree(int tableNumber){
		for(Table table : tables) {
			if(table.tableNumber == tableNumber) {
				table.setUnoccupied(); 
				System.out.println("msgTableIsFree received: Clearing table");
				stateChanged();
				break;
			}
		}
	}
	public void msgIamFree(HuangWaiterRole w) {
		for(MyWaiter waiter : waiters) {
			if (waiter.w.equals(w)) {
				waiter.state = WaiterState.Free;
				//System.out.println("msgIamFree received: Waiter State updated to free");
				stateChanged();
				break;
			}
		}
	}
	public void msgIamBusy(HuangWaiterRole w) {
		for(MyWaiter waiter : waiters) {
			if (waiter.w.equals(w)) {
				waiter.state = WaiterState.Busy;
				//System.out.println("msgIamFree received: Waiter State updated to Busy");
				stateChanged();
				break;
			}
		}
	}
	public void msgIWantBreak(HuangWaiterRole w) {
		for(MyWaiter waiter : waiters) {
			if (waiter.w.equals(w)) {
				waiter.event = WaiterEvent.wantsBreak;
				System.out.println("msgIWantBreak received: Waiter State updated to wantsBreak");
				stateChanged();
				break;
			}
		}
	}
	public void msgOnBreak(HuangWaiterRole w) {
		for(MyWaiter waiter : waiters) {
			if (waiter.w.equals(w)) {
				waiter.event = WaiterEvent.onBreak;
				System.out.println("msgOnBreak received: Waiter State updated to onBreak");
				stateChanged();
				break;
			}
		}	
	}
	public void msgBackFromBreak(HuangWaiterRole w) {
		for(MyWaiter waiter : waiters) {
			if (waiter.w.equals(w)) {
				waiter.state = WaiterState.Free;
				waiter.event = WaiterEvent.Working;
				System.out.println("msgBackFromBreak received: Waiter State updated to free");
				stateChanged();
				break;
			}
		}	
	}
	public void msgWhoIsCook(HuangWaiterRole w) {
		for(MyWaiter waiter : waiters) {
			if (waiter.w.equals(w)) {
				waiter.state = WaiterState.NeedsToKnowCook;
			}
		}	
		stateChanged();
	}
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	protected boolean pickAndExecuteAnAction() {
		for (MyWaiter mw : waiters) {
			if (mw.state == WaiterState.NeedsToKnowCook) {
				for (MyCook mc : cooks) {
					if(mc.state == CookState.JustArrived) {
						mc.state = CookState.OnShift;
						mw.state = WaiterState.Busy;
						tellWaitersCookHere(mc.c);
						return true;
					}
				}
			}
		}
		for (MyCook mc : cooks) {
			if(mc.state == CookState.AllowedToLeave) {
				cooks.remove(mc);
				return true;
			}
		}
		for (MyWaiter mw : waiters) {
			if(mw.event == WaiterEvent.DoneWorking) {
				waiters.remove(mw);
				return true;
			}
		}
		for (MyCook mc : cooks) {
			if(mc.state == CookState.JustArrived) {
				mc.state = CookState.OnShift;
				tellWaitersCookHere(mc.c);
				return true;
			}
		}
		for (MyWaiter w : waiters) {
			if(w.event == WaiterEvent.JustArrived) {
				w.event = WaiterEvent.Working;
				for (MyCook mc : cooks) {
					if(mc.state == CookState.OnShift) {
						tellWaitersCookHere(mc.c);
						return true;
					}
				}
				return true;
			}
		}
		for (MyWaiter w : waiters) {
			int waitersWantBreak = 0;
			for(MyWaiter w1: waiters) {
				if (w1.event.equals(WaiterEvent.wantsBreak) || w1.event.equals(WaiterEvent.onBreak)) {
					waitersWantBreak++;
				}
			}
			if (w.event.equals(WaiterEvent.wantsBreak)) {
				if (waitersWantBreak <= 1 && !w.event.equals(WaiterEvent.breakAllowed) && waiters.size() > 1) {
					goOnBreak(w);
					return true;
				}
				else {
					denyBreak(w);
					return true;
				}
			}
		}
		if (!hungryCustomers.isEmpty()) {
			synchronized(hungryCustomers) {
				for (HungryCustomer hc: hungryCustomers) {
					if(!cooks.isEmpty()) {
						for (Table table : tables) {
							if (!table.isOccupied()) {
								if(!waiters.isEmpty()) {
									synchronized(waiters) {
										for (MyWaiter w : waiters) {
											if(w.state.equals(WaiterState.Free) && w.w.getCustomers().isEmpty() && !w.event.equals(WaiterEvent.onBreak)) {
												seatCustomerAtTable(hc, table, w);
												hc.state = CustomerState.hasWaiter;
												return true;
											}
										}
										for (MyWaiter w1: waiters) {
											if (w1.state.equals(WaiterState.Free) && !w1.event.equals(WaiterEvent.onBreak)) {
												seatCustomerAtTable(hc, table, w1);
												hc.state = CustomerState.hasWaiter;
												return true;
											}
										}
										for (MyWaiter w2 : waiters) {
											if (!w2.event.equals(WaiterEvent.onBreak)) {
												seatCustomerAtTable(hc, table, w2);
												hc.state = CustomerState.hasWaiter;
												return true;
											}
										}
									}
								}
							}
						}
						if (!waiters.isEmpty() && hc.state.equals(CustomerState.noWaiter)) {
							hc.state = CustomerState.toldFull;
							tellCustomerFull(hc.c);
							return true;
						}
					}
					else {
						tellCustomerClosed(hc.c);
						return true;
					}
				}
			}
		}
		for (MyCook mc : cooks) {
			if (mc.state == CookState.AskedToLeave) {
				if (waiters.isEmpty()) {
					tellCookCanLeave(mc);
				}
			}
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}


	// Actions
	private void tellCookCanLeave(MyCook mc) {
		mc.state = CookState.AllowedToLeave;
		mc.c.msgYouCanLeave();
	}
	private void tellCustomerClosed(HuangCustomerRole c) {
		c.msgGetOut();
		synchronized(hungryCustomers) {
			for(HungryCustomer hc : hungryCustomers) {
				if(hc.c.equals(c)) {
					hungryCustomers.remove(hc);
					break;
				}
			}
		}
		customers.remove(c);

	}

	private void tellWaitersCookHere(HuangCookRole c) {
		synchronized(waiters) {
			for (MyWaiter w : waiters) {
				if (w.w.getCook() == null) {
					w.w.msgCookHere(c);
				}
			}
		}
	}
	private void tellCustomerFull(HuangCustomerRole c) {
		c.msgRestaurantFull();
	}
	private void seatCustomerAtTable(HungryCustomer hc, Table table, MyWaiter w) {
		w.w.msgPleaseSeatCustomer(hc.c, table.tableNumber);
		w.state = WaiterState.Busy;
		table.setOccupant(hc.c);
		guiShuffleCustomerQueue();
		synchronized(hungryCustomers) {
			hungryCustomers.remove(hc);
		}
		synchronized(customers) {
			customers.remove(hc.c);
		}
	}
	private void goOnBreak(MyWaiter w) {
		w.event = WaiterEvent.breakAllowed;
		w.w.msgGoOnBreak();
	}
	private void denyBreak(MyWaiter w) {
		w.w.msgNoBreak();
		w.event = WaiterEvent.Working;
	}
	//utilities
	public void guiShuffleCustomerQueue() {
		synchronized(hungryCustomers) {
			for(HungryCustomer hc: hungryCustomers) {
				hc.c.getGui().moveUpQueue();
			}
		}
	}
	public HuangCookRole getCook() {
		synchronized(cooks) {
			for(MyCook mc : cooks) {
				if (mc.state == CookState.OnShift) {
					return mc.c;
				}
			}
		}
		return cooks.get(0).c;
	}
	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	public HuangCashierAgent getCashier() {
		return ca;
	}
	public void msgArrivedToWork(Role r) {
		if(r.getClass().toString().contains("Cook")) {
			synchronized(cooks) {
				cooks.add(new MyCook((HuangCookRole) r));
			}
			stateChanged();
		}
		else if(r.getClass().toString().contains("Waiter")) {
			synchronized(waiters) {
				waiters.add(new MyWaiter (this, (HuangWaiterRole) r));
			}
			stateChanged();
		}
	}
	public void msgDoneWorking(Role r) {
		if(r.getClass().toString().contains("Cook")) {
			synchronized(cooks) {
				for(MyCook mc : cooks) {
					if (mc.c == r) {
						mc.state = CookState.AskedToLeave;
					}
				}
			}
		}
		else if(r.getClass().toString().contains("Waiter")) {
			synchronized(waiters) {
				for(MyWaiter mw : waiters) {
					if (mw.w == r) {
						mw.event = WaiterEvent.DoneWorking;
					}
				}
			}
		}
		stateChanged();
	}
	public void setRestaurant(HuangRestaurant huang) {
		this.restaurant = huang;
	}

}

	

