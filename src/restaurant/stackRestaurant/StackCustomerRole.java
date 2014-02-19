package restaurant.stackRestaurant;

import restaurant.stackRestaurant.gui.CustomerGui;
import restaurant.stackRestaurant.helpers.Menu;
import restaurant.stackRestaurant.helpers.Check;
import agent.Agent;
import agent.Role;
import gui.Building;

import java.util.List;
import java.util.Timer;
import java.util.Random;
import java.util.TimerTask;

import city.PersonAgent;
import city.helpers.Directory;
import restaurant.stackRestaurant.interfaces.*;
import trace.AlertLog;
import trace.AlertTag;

/**
 * Restaurant customer agent.
 */
public class StackCustomerRole extends Role implements Customer {
	private int hungerLevel = 5;        // determines length of meal
	private int tableNumber;
	private String choice;
	Timer timer = new Timer();
	Random rand = new Random(); 
	private CustomerGui customerGui;
	String myLocation;
	Check check;
	
	/**
	 * change this values for non normative cases
	 */
	private boolean cheapSkate = false;
	private boolean willingToWait = true;
	

	// agent correspondents
	private Host host;
	private Waiter waiter;
	private Cashier cashier;

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, WaitingForOpening, WaitingForWaiter, BeingSeated, Seated, Ordering, WaitingForFood, Eating, DoneEating, Paying, Paid, Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, doneEntering, waitingForSeating, followHost, seated, ordered, foodArrived, doneEating, waitingForCheck, gotCheck, gotToCashier, donePaying, doneLeaving, closed};
	AgentEvent event = AgentEvent.none;
	private String stringState;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public StackCustomerRole(String location){
		super();
		customerGui = new CustomerGui(this);
		
		myLocation = location;
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui(customerGui);
			}
		}
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(Agent host) {
		this.host = (Host) host;
	}
	
	public void setFunds(double funds) {
		getPersonAgent().setFunds(funds);
	}
	
	public double getFunds() {
		return getPersonAgent().getFunds();
	}
	
	public void setWaiter(Waiter waiter) {
		this.waiter = waiter;
	}
	
	public void setCashier(Agent cashier) {
		this.cashier = (Cashier) cashier;
	}

	// Messages
	public void msgGotHungry() {//from animation
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgRestaurantFull() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Restaurant is full");
		event = AgentEvent.waitingForSeating;
		stateChanged();
	}

	public void msgSitAtTable(Waiter waiter, int table) {
		tableNumber = table;
		event = AgentEvent.followHost;
		this.waiter = waiter;
		stateChanged();
	}
	
	public void msgHereToTakeOrder() {
		event = AgentEvent.ordered;
		stateChanged();
	}
	
	public void msgHereIsCheck(Check check) {
		this.check = check;
		event = AgentEvent.gotCheck;
		stateChanged();
		
	}
	
	public void msgReorder() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "I need to reorder");
		event = AgentEvent.ordered;
		state = AgentState.Ordering;
		stateChanged();
	}
	
	public void msgHereIsFood() {
		event = AgentEvent.foodArrived;
		stateChanged();
	}
	
	public void msgHereIsChange(double change) {
		setFunds(getFunds()+change);
		event = AgentEvent.donePaying;
		stateChanged();
	}
	public void msgRestaurantClosed() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Restaurant is closed");
		event = AgentEvent.closed;
		stateChanged();
		
	}

	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	public void msgAnimationFinishedGoToCashier() {
		event = AgentEvent.gotToCashier;
		stateChanged();
	}
	public void msgAnimationFinishedEnteringRestaurant() {
		event = AgentEvent.doneEntering;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(event == AgentEvent.closed) {
			setStringState(state.toString());
			leaveClosedRestaurant();
		}
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ) {
			state = AgentState.WaitingInRestaurant;
			setStringState(state.toString());
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.doneEntering) {
			state = AgentState.WaitingForWaiter;
			setStringState(state.toString());
			tellHostWaiting();
			return true;
		}
		if (state == AgentState.WaitingForWaiter && event == AgentEvent.waitingForSeating) {
			if(!willingToWait) {
				notWaitingAndLeaving();
				state = AgentState.Leaving;
				setStringState(state.toString());
			}
			else {
				state = AgentState.WaitingForOpening;
				setStringState(state.toString());
			}
			return true;
		}
		if (state == AgentState.WaitingForWaiter && event == AgentEvent.followHost ){
			state = AgentState.BeingSeated;
			setStringState(state.toString());
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated) {
			state = AgentState.Ordering;
			setStringState(state.toString());
			readyToOrder();
			return true;
		}
		if (state == AgentState.Ordering && event == AgentEvent.ordered) {
			state = AgentState.WaitingForFood;
			setStringState(state.toString());
			orderFood();
			
			return true;
		}
		if (state == AgentState.WaitingForFood && event == AgentEvent.foodArrived){
			state = AgentState.Eating;
			updateGui(choice.substring(0, 2));
			setStringState(state.toString());
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.DoneEating;
			event = AgentEvent.waitingForCheck;
			setStringState(state.toString());
			updateGui("");
			waiter.msgCheckPlease(this);
			return true;
		}
		if(state == AgentState.DoneEating && event == AgentEvent.gotCheck) {
			state = AgentState.Paying;
			setStringState(state.toString());
			customerGui.DoGoToCashier();
		}
		if(state == AgentState.Paying && event == AgentEvent.gotToCashier) {
			setStringState(state.toString());
			payCheck();
		}
		if(state == AgentState.Paid && event == AgentEvent.donePaying) {
			state = AgentState.Leaving;
			setStringState(state.toString());
			leaveRestaurant();
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			setStringState(state.toString());
			doneRole();
			return true;
		}
		return false;
	}

	// Actions
	
	private void doneRole() {
		Directory.sharedInstance().getCityGui().getMacroAnimationPanel().removeGui(customerGui);
		getPersonAgent().msgRoleFinished();
	}

	private void payCheck() {
		state = AgentState.Paid;
		cashier.msgPayCheck(this, check, getFunds());
		setFunds(getFunds() - check.cost());
	}
	
	private void updateGui(String choice) {
		customerGui.updateGui(choice);
	}
	
	private void orderFood() {
		//hack for food ordering
		
		if(!cheapSkate) {
			for(Menu.Food food : Menu.sharedInstance().getMenu()) {
				if(getFunds() > food.getPrice() 
						&& Menu.sharedInstance().getInventoryStock(food.getName())) {
					AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Ordering food");
					choice = food.getName();
					waiter.msgGiveOrder(this, choice);
					updateGui(choice.substring(0, 2) + "?");
					return;
				}
			}
			state = AgentState.Paid;
			event = AgentEvent.donePaying;
			AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Too expensive and going home");

		}
		else {
			for(int i = 0; i <=4; i++) {
				choice = Menu.sharedInstance().getMenu().get(rand.nextInt(Menu.sharedInstance().getMenu().size())).getName();
				if(Menu.sharedInstance().getInventoryStock(choice)) {
					AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Ordering food");
					waiter.msgGiveOrder(this, choice);
					updateGui(choice.substring(0, 2) + "?");
					return;
				}
			}
			state = AgentState.Paid;
			event = AgentEvent.donePaying;
			AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Going home");
		}
	}
	
	private void readyToOrder() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Telling waiter ready to order");
		waiter.msgReadyToOrder(this);
	}

	private void goToRestaurant() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Going to restaurant");
		customerGui.DoEnterRestaurant();
		
	}
	
	private void tellHostWaiting() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "waiting");
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Being seated. Going to table");
		customerGui.DoGoToSeat(1, tableNumber);
	}

	private void EatFood() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
		state = AgentState.Eating;
		stateChanged();
	}

	private void leaveRestaurant() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Leaving.");
		waiter.msgDoneEating(this);
		customerGui.DoExitRestaurant();
		getPersonAgent().msgRoleFinished();
	}
	
	private void notWaitingAndLeaving() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Leaving because it's too busy");
		host.msgNotWaiting(this);
		customerGui.DoExitRestaurant();
		getPersonAgent().msgRoleFinished();
	}
	
	private void leaveClosedRestaurant() {
		AlertLog.getInstance().logMessage(AlertTag.RESTAURANTCUSTOMER, getName(), "Leaving.");
		customerGui.DoExitRestaurant();
		getPersonAgent().msgRoleFinished();
	}

	// Accessors, etc.

	public String getName() {
		return getPersonAgent().getName();
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
	}

	public String toString() {
		return getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	public String getStringState() {
		return stringState;
	}
	
	public void setStringState(String stringState) {
		this.stringState = stringState;
	}
}

