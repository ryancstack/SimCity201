package restaurant.nakamuraRestaurant;

import gui.Building;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.concurrent.Semaphore;

import city.helpers.Directory;
import restaurant.Restaurant;
import restaurant.nakamuraRestaurant.gui.WaiterGui;
import restaurant.nakamuraRestaurant.helpers.Check;
import restaurant.nakamuraRestaurant.helpers.Menu;
import restaurant.nakamuraRestaurant.interfaces.Waiter;
import agent.Role;

/**
 * Restaurant Waiter Agent
 */

public class NakamuraWaiterRole extends Role implements Waiter{
	public List<Cust> MyCustomers
	= new ArrayList<Cust>();
	public List<Check> Checks = new ArrayList<Check>();

	public enum state {waiting, gettingcust, goingtoseat, seated, wanttoorder, tookorder, ordered, waitingforfood, reorder, foodready, eating, askedforcheck, waitingforcheck, gotcheck, paying, leaving, done};
	private String myLocation;
	
	protected Semaphore actionComplete = new Semaphore(0,true);

	public WaiterGui waiterGui = null;
	protected NakamuraCookRole cook;
	protected NakamuraHostAgent host;
	private NakamuraCashierAgent cashier;
	
	public enum WorkState {arrived, working, tired, waitingforbreak, goingonbreak, onbreak, backtowork, leaving, gettingPaycheck, WaitingForPaycheck, doneWorking};
	private WorkState status;
	
	protected NakamuraRestaurant restaurant = (NakamuraRestaurant) Directory.sharedInstance().getRestaurants().get(2);

	public NakamuraWaiterRole(String location) {
		super();

		this.status = WorkState.arrived;
		this.myLocation = location;
		waiterGui = new WaiterGui(this, 150, 50);

		host = (NakamuraHostAgent) Directory.sharedInstance().getAgents().get("NakamuraRestaurantHost");
		cashier = (NakamuraCashierAgent) Directory.sharedInstance().getAgents().get("NakamuraRestaurantCashier");

		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui(waiterGui);
			}
		}
	}

	public List<Cust> getMyCustomers() {
		return MyCustomers;
	}
	
	public void setHost(NakamuraHostAgent h) {
		host = h;
	}
	
	public void setCook(NakamuraCookRole c) {
		cook = c;
	}
	
	public void setCashier(NakamuraCashierAgent c) {
		cashier = c;
	}

	// Messages
	public void msgSetCook(NakamuraCookRole cook) {
		this.cook = cook;
	}
	
	public void msgWantToGoOnBreak() {
		print("Received msgWantToGoOnBreak");
		status = WorkState.tired;
		stateChanged();
	}
	
	public void msgNewCustomer(NakamuraCustomerRole c, int tablenum) {
		print("Received msgNewCustomer");
		MyCustomers.add(new Cust(c, tablenum));
		stateChanged();
	}
	
	public void msgHereIAm(NakamuraCustomerRole c) {
		print("Received msgHereIAm");
		for (Cust cust : MyCustomers) {
			if(cust.getCustomer() == c) {
				cust.s = state.goingtoseat;
				stateChanged();
			}
		}
	}
	
	public void msgReadyToOrder(NakamuraCustomerRole c) {
		print("Received msgReadyToOrder");
		for (Cust cust : MyCustomers) {
			if(cust.getCustomer() == c) {
				cust.s = state.wanttoorder;
				stateChanged();
			}
		}
	}
	
	public void msgHeresOrder (NakamuraCustomerRole c, String choice) {
		print("Received msgHeresOrder");
		for (Cust cust : MyCustomers) {
			if(cust.getCustomer() == c) {
				cust.choice = choice;
				cust.s = state.ordered;
			}
		}
	}
	
	public void msgOutofFood(String choice, int tableNum) {
		print("Received msgOutofFood");
		for (Cust cust : MyCustomers) {
			if(cust.getTableNumber() == tableNum) {
				cust.s = state.reorder;
			}
		}
		
	}
	
	public void msgFoodReady(String choice, int tableNum) {
		print("Received msgFoodReady");
		for (Cust cust : MyCustomers) {
			if(cust.getTableNumber() == tableNum) {
				cust.s = state.foodready;
			}
		}
	}
	
	public void msgCheckPlease(NakamuraCustomerRole c) {
		print("Received msgCheckPlease");
		for (Cust cust : MyCustomers) {
			if (cust.getCustomer() == c) {
				cust.s = state.askedforcheck;
				stateChanged();
			}
		}
	}
	
	public void msgCheckReady(Check check) {
		print("Received msgCheckReady");
		Checks.add(check);
		for (Cust cust : MyCustomers) {
			if (cust.getCustomer() == check.getCustomer()) {
				cust.s = state.gotcheck;
				stateChanged();
			}
		}
	}

	public void msgLeavingTable(NakamuraCustomerRole c) {
		print("Received msgLeavingTable");
		for (Cust cust : MyCustomers) {
			if (cust.getCustomer() == c) {
				cust.s = state.leaving;
				stateChanged();
			}
		}
	}
	
	public void msgGoOnBreak() {
		print("Received msgGoOnBreak");
		status = WorkState.goingonbreak;
		stateChanged();		
	}
	
	public void msgBreakOver() {
		print("Received msgBreakOver");
		status = WorkState.backtowork;
		stateChanged();
	}
	
	public void msgBreakDenied() {
		print("Received msgBreakDenied");
		status = WorkState.working;
		stateChanged();
	}
	
	public void msgJobDone() {
		print("Received msgJobDone");
		status = WorkState.doneWorking;
		stateChanged();
	}

	public void msgHereIsPaycheck(double pay){
		print("Received msgHereIsPaycheck");
		getPersonAgent().setFunds(getPersonAgent().getFunds() + pay);
		status = WorkState.leaving;
		stateChanged();
	}

	public void msgActionComplete() {//from animation
		print("msgActionComplete() called");
		actionComplete.release();// = true;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		try {
			if(status == WorkState.arrived) {
				ArriveAtWork();
				return true;
			}
			
			if(status == WorkState.doneWorking) {
				NotifyHost();
				return true;
			}
			
			if(status == WorkState.gettingPaycheck && MyCustomers.isEmpty()) {
				CollectPaycheck();
				return true;
			}
			
			if(status == WorkState.leaving && MyCustomers.isEmpty()) {
				LeaveRestaurant();
				return true;
			}
			
			if(status == WorkState.tired) {
				status = WorkState.waitingforbreak;
				AskToBreak();
			}
			
			else if(status == WorkState.goingonbreak && MyCustomers.isEmpty()) {
				GoOnBreak();
			}
			
			else if(status == WorkState.backtowork) {
				ReturnToWork();
			}
			
			for(Cust cust: MyCustomers) {
				if(cust.s == state.gettingcust) {
					return false;
				}
			}
			
			for (Cust cust : MyCustomers) {
				if (cust.s == state.goingtoseat) {
					GoToSeat(cust);
				}
				stateChanged();
			}
			
			for (Cust cust : MyCustomers) {
				if (cust.s == state.waiting) {
					GetCust(cust);
					return false;
				}
				stateChanged();
			}
	
			for (Cust cust : MyCustomers) {
				if (cust.s == state.wanttoorder) {
					TakeOrder(cust);
				}
				stateChanged();
			}
	
			for (Cust cust : MyCustomers) {
				if (cust.s == state.ordered) {
					PlaceOrder(cust);
				}
				stateChanged();
			}
	
			for (Cust cust : MyCustomers) {
				if (cust.s == state.foodready) {
					DeliverFood(cust);
				}
				stateChanged();
			}
			
			for (Cust cust : MyCustomers) {
				if (cust.s == state.reorder) {
					Reorder(cust);
				}
				stateChanged();
			}
			
			for (Cust cust : MyCustomers) {
				if (cust.s == state.askedforcheck) {
					GetCheck(cust);
				}
				stateChanged();
			}
			
			for (Cust cust : MyCustomers) {
				if (cust.s == state.gotcheck) {
					DeliverCheck(cust);
				}
				stateChanged();
			}
			
			for (Cust cust : MyCustomers) {
				if (cust.s == state.leaving) {
					RemoveCust(cust);
				}
				stateChanged();
			}
		}
		catch (ConcurrentModificationException e){
			return false;			
		}
		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions
	private void ArriveAtWork() {
		host.msgNewWaiter(this);
		waiterGui.setPresent();
		waiterGui.DoGoToHome();
		status = WorkState.working;
	}
	
	private void AskToBreak() {
		host.msgGoingOnBreak(this);
		stateChanged();
	}
	
	private void GetCust (Cust customer) {
		DoGetCustomer(customer);
		customer.s = state.gettingcust;
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.c.msgFollowMe(this, new Menu());
		stateChanged();
	}
	
	private void GoToSeat (Cust customer) {
		DoSeatCustomer(customer);
		customer.s = state.seated;
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stateChanged();
	}

	private void TakeOrder (Cust customer) {
		DoGetOrder(customer);
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.c.msgGetOrder();
		customer.s = state.tookorder;
		print("Took Order");
		stateChanged();
	}
	
	protected void PlaceOrder(Cust customer) {
	}
	
	private void Reorder (Cust customer) {
		print("Taking new order");
		DoGetOrder(customer);
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.c.msgReorder();
		customer.s = state.tookorder;
		stateChanged();
	}
	
	private void DeliverFood(Cust customer) {
		print("Delivering Food");
		DoPickUpOrder(customer);
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DoDeliverFood(customer);
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.s = state.eating;
		customer.c.msgEatFood();
		stateChanged();
	}
	
	private void GetCheck(Cust customer) {
		print("Getting Check from " + cashier);
		cashier.msgComputeCheck(this, customer.c, customer.choice);
		customer.s = state.waitingforcheck;
		stateChanged();
	}
	
	private void DeliverCheck(Cust customer) {
		print("Delivering Check");
		for(Check c : Checks) {
			if(c.getCustomer() == customer.c) {
				customer.c.msgHeresCheck(c);
				customer.s = state.paying;				
			}
		}
		stateChanged();
	}
	
	private void RemoveCust(Cust customer) {
		host.msgTableEmpty(customer.tableNumber);
		MyCustomers.remove(customer);
		stateChanged();
	}
	
	private void GoOnBreak() {
		print("Going on break");
		status = WorkState.onbreak;
		waiterGui.DoGoToHost();
	}
	
	private void ReturnToWork() {
		print("Going back to work");
		status = WorkState.working;
		host.msgBackToWork(this);
	}
	
	private void NotifyHost() {
		host.msgNoNewCustomers(this);
		status = WorkState.gettingPaycheck;
	}
	
	private void CollectPaycheck() {
		waiterGui.DoGoToCashier();
		try {
			actionComplete.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		cashier.msgNeedPay(this);
		status = WorkState.WaitingForPaycheck;
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
	private void DoGetCustomer(Cust customer) {
		print("Getting " + customer.c);
		waiterGui.DoGoToHost();
	}
	private void DoSeatCustomer(Cust customer) {
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer.c + " at " + customer.tableNumber);
		waiterGui.DoBringToTable(customer.c, customer.tableNumber); 
	}
	private void DoGetOrder(Cust customer) {
		print("Taking " + customer.c + "'s order");
		waiterGui.DoGoToTable(customer.tableNumber);
	}
	
	protected void DoPlaceOrder(Cust customer) {
		print("Taking " + customer.c + "'s order to cook");
		waiterGui.DoGoToCook();
	}
	
	private void DoPickUpOrder(Cust customer) {
		print("Getting " + customer.c + "'s order");
		waiterGui.DoGoToPlating();
	}
	
	private void DoDeliverFood(Cust customer) {
		print("Delivering " + customer.c + "'s food");
		waiterGui.DoDeliverFood(customer.tableNumber, customer.choice, cook);
	}
	
	private void DoLeaveRestaurant() {
		host.msgWaiterLeaving(this);
		waiterGui.DoLeaveRestaurant();
	}

	//utilities

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}

	class Cust {
		NakamuraCustomerRole c;
		int tableNumber;
		String choice;
		state s;

		Cust(NakamuraCustomerRole c, int tableNumber) {
			this.c = c;
			this.tableNumber = tableNumber;
			this.choice = null;
			this.s = state.waiting;
		}
		int getTableNumber() {
			return tableNumber;
		}

		NakamuraCustomerRole getCustomer() {
			return c;
		}
	}
}

