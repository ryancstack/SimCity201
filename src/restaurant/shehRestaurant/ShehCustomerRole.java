package restaurant.shehRestaurant;

import restaurant.shehRestaurant.helpers.Bill;
import restaurant.shehRestaurant.gui.CustomerGui;
import restaurant.shehRestaurant.helpers.Menu;
//import restaurant.shehRestaurant.helpers.RestaurantGui;
import agent.Agent;
import agent.Role;
import restaurant.shehRestaurant.helpers.Table;
import restaurant.shehRestaurant.interfaces.Cashier;
import restaurant.shehRestaurant.interfaces.Customer;
import gui.Building;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import city.helpers.Directory;

/**
 * Restaurant customer agent.
 */
public class ShehCustomerRole extends Role implements Customer {
	//MONEY~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	private double money = 50;
	
	
	
	private String name;
	private int hungerLevel = 5; 
	//private int money = (int) (Math.random()*50);
	Timer timer = new Timer();
	private CustomerGui customerGui;
	Table table;
	Menu menu = new Menu(); //NULL POINTER EXCEPTION FOR NOW
	private Bill bill;
	Boolean reordering = false;
	Boolean cheapestItem = false;

	private ShehHostAgent host;
	private ShehWaiterRole waiter;
	private Cashier cashier;

	public enum AgentState
	{DoingNothing, WaitingInRestaurant, ThinkingAboutStaying, BeingSeated, Seated, Ordering, 
		DoneOrdering, Waiting, Eating, DoneEating, AskingForBill, WaitingForBill, PayingBill, 
		Leaving};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, thinkingAboutStaying, followHost, seated, leavingDueToPrices, ordering, 
		doneOrdering, receivedFood, doneEating, waitingForBill, receivingBill, paying, ReceivedChange, doneLeaving};
	AgentEvent event = AgentEvent.none;
	

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customer gui so the customer can send it messages
	 */
	public ShehCustomerRole(String location){
		super();
	
		customerGui = new CustomerGui(this);
		host = (ShehHostAgent) Directory.sharedInstance().getAgents().get("ShehRestaurantHost");
		
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		for(Building b : buildings) {
			if (b.getName() == location) {
				b.addGui(customerGui);
			}
		}
	}

	/**
	 * hack to establish connection to Host agent.
	 */
	// Messages

	public void msgGotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgTablesAreFull() {
		print("Do I want to stay?");
		event = AgentEvent.thinkingAboutStaying;
		stateChanged();
	}
	
	public void msgSitAtTable(Table t, Menu m) {
		this.table = t;
		menu = m;
		print("Received msgSitAtTable");
		event = AgentEvent.followHost;
		stateChanged();
	}

	public void msgAnimationFinishedGoToSeat() {
		print("Seated. Looking at menu");
		event = AgentEvent.seated;
		stateChanged();
	}
	
	public void msgWhatWouldYouLike() {
		event = AgentEvent.doneOrdering;
		stateChanged();
	}
	
	public void msgOrderSomethingElse() {
		event = AgentEvent.seated;
		reordering = true;
		stateChanged();
	}
	
	public void msgHereIsYourFood() {
		event = AgentEvent.receivedFood;
		stateChanged();
	}
	
	public void msgHereIsYourBill(Bill b, Cashier c) {
		bill = b;
		cashier = c;
		event = AgentEvent.receivingBill;
		stateChanged();
	}
	
	public void msgHereIsYourChange(Bill b) {
		bill = b;
		money = b.m;
		event = AgentEvent.ReceivedChange;
		stateChanged();
	}
	
	public void msgThisIsYourNumber(int num) {
		customerGui.DoWaitInRestaurant(num);
	}
	
	public void msgAnimationFinishedLeaveRestaurant() {
		event = AgentEvent.doneLeaving;
		stateChanged();
	}
	
	public void msgRestaurantIsClosed() {
		print("Restaurant is closed, I'm leaving.");
		customerGui.DoExitRestaurant();
		this.getPersonAgent().msgRoleFinished();	
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.thinkingAboutStaying) {
			state = AgentState.ThinkingAboutStaying;
			ThinkAboutStaying();
			return true;
		}
		
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followHost ){ //follow waiter
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Ordering;
			Ordering();
			return true;
		}
		
		if(state == AgentState.Ordering && event == AgentEvent.leavingDueToPrices) {
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		
		if (state == AgentState.Ordering && event == AgentEvent.doneOrdering) {
			state = AgentState.Waiting;
			HereIsMyChoice();
			return true;
		}
		
		if (state == AgentState.Waiting && event == AgentEvent.seated) { //CASE FOR REORDERING
			state = AgentState.Ordering;
			Ordering();
			return true;
		}
		
		if (state == AgentState.Waiting && event == AgentEvent.receivedFood) {
			state = AgentState.Eating;
			EatFood();
			return true;
		}
	
		if (state == AgentState.Eating && event == AgentEvent.doneEating) {
			state = AgentState.AskingForBill;
			AskForBill();
			return true; 
		}
		
		if (state == AgentState.AskingForBill && event == AgentEvent.receivingBill) {
			state = AgentState.PayingBill;
			PayBill();
			return true;
		}
			
		if (state == AgentState.PayingBill && event == AgentEvent.ReceivedChange) {
			state = AgentState.Leaving; 
			leaveTable();
			return true;
		}
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions
	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
		
	}

	private void ThinkAboutStaying() {
		int randomDecision = (int) (Math.random()*2);
		if(randomDecision == 0) {
			print("I'll stay.");
			event = AgentEvent.followHost;
			stateChanged(); //do I need double stateChanged?
			state = AgentState.WaitingInRestaurant;
			stateChanged();
		}
		if(randomDecision == 1) {
			print("I think I'll go.");
			event = AgentEvent.paying;
			stateChanged();
			state = AgentState.PayingBill;
			stateChanged();
		}
	}
	
	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(1, table);
	}

	private void Ordering() {
		if(this.getCustomerName() == "BO") {
			print("ORDERINGSTUB");
			/*
			timer.schedule(new TimerTask() {
				public void run() {
					ImReadyToOrder();
					//event = AgentEvent.doneOrdering;
					//stateChanged();
				}
			},
			5000);
			*/
		}
		else{
			if(money < menu.lowestprice) {
				event = AgentEvent.leavingDueToPrices;
				print("These prices are too high for me, I am leaving.");
			}
			else if(menu.secondlowestprice > money && money >= menu.lowestprice) {
				print("I'll have the cheapest item here.");
				cheapestItem = true;
				ImReadyToOrder();
			}
			else {
				timer.schedule(new TimerTask() {
					public void run() {
						ImReadyToOrder();
						//event = AgentEvent.doneOrdering;
						//stateChanged();
					}
				},
				5000);
			}
		}
	}
	
	private void ImReadyToOrder() {
		Do("I'm ready to order.");
		waiter.msgImReadyToOrder(this);
	}
	
	private void HereIsMyChoice() {
		String choice = null;
		if(cheapestItem) {
			choice = "Chicken";
		}
		if(cheapestItem && reordering) {
			state = AgentState.Ordering;
			stateChanged();
			event = AgentEvent.leavingDueToPrices;
			stateChanged();
		}
		else {
			int randomNumber = (int) (Math.random()*4);
			choice = (String) menu.choices[randomNumber];
		}
		
		Do("I would like to order " + choice);
		waiter.msgOrderFood(this, choice);
	}

	private void EatFood() {
		//waiter.msgThankYou(this);
		Do("Eating Food");
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
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		3000);//getHungerLevel() * 1000);//how long to wait before running task
	}
	
	private void AskForBill() {
		Do("I need the bill.");
		waiter.msgBillPlease(this);
		event = AgentEvent.waitingForBill;
		stateChanged();
	}
	
	private void PayBill() {
		Do("Going to the cashier to pay the bill.");
		
		cashier.msgHereToPay(this, money);
		
		event = AgentEvent.paying;
		stateChanged();
	}
	
	private void leaveTable() {
		Do("Leaving w/ $" + money + " left in my wallet.");
		//host.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
		this.getPersonAgent().msgRoleFinished();
	}

	//Utilities
	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}

	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
	}

	public String toString() {
		return "customer " + getName();
	}

	public void setGui(CustomerGui g) {
		customerGui = g;
	}

	public CustomerGui getGui() {
		return customerGui;
	}
	
	public void setHost(ShehHostAgent host) {
		this.host = host;
	}
	
	public void setWaiter(ShehWaiterRole waiter) {
		this.waiter = waiter;
	}

	public String getCustomerName() {
		return name;
	}
}