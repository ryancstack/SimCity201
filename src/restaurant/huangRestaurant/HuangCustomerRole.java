package restaurant.huangRestaurant;

import restaurant.huangRestaurant.HuangCookRole.CookState;
import restaurant.huangRestaurant.gui.CookGui;
import restaurant.huangRestaurant.gui.CustomerGui;
import restaurant.huangRestaurant.interfaces.Cashier;
import restaurant.huangRestaurant.interfaces.Customer;
import agent.Agent;
import agent.Role;
import gui.Building;
import gui.Gui;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import city.helpers.Directory;

/**
 * Restaurant customer agent.
 */
public class HuangCustomerRole extends Role implements Customer {
	private String name;
	private int hungerLevel = 5; // determines length of meal
	private int table;
	//hard coding tables without mapping
    private static final int tableSpawnX = 160;
	private static final int tableSpawnY = 170;
	private static final int tableOffSetX = 180;

	private Menu m;
	private String choice;
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private boolean freeLoader = false;
	private double Cash;
	private Check cx;
	private Semaphore checkRecieved= new Semaphore(0, true);
	
	// agent correspondents
	private HuangHostAgent host;
	private HuangWaiterRole myWaiter; 
	private Cashier ca;
	
	private Semaphore atCashier = new Semaphore(0, true);
	private Semaphore atHost = new Semaphore(0,true);

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, Choosing, Ordering, Eating, DoneEating, Paying, Leaving, kickedOut, outOfChoice, choiceLeave, possibleFlake, decideStay, fullRestaurant};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followWaiter, seated, choiceSelected, waitingForWaiter, waiterArrived, orderSaid, receivedFood, doneEating, donePaying, doneLeaving};
	AgentEvent event = AgentEvent.none;
	String myLocation;
	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public HuangCustomerRole(String location) {
		super();
		host = (HuangHostAgent) Directory.sharedInstance().getAgents().get("HuangRestaurantHost");
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
	public void setHost(HuangHostAgent host) {
		this.host = host;
	}
	public void setCashier(HuangCashierAgent ca) {
		this.ca = ca;
	}
	public void setWaiter(HuangWaiterRole w){
		this.myWaiter = w;
	}

	public String getCustomerName() {
		return name;
	}
	// Messages

	public void msgGotHungry() {//from animation
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	public void msgRestaurantFull() {
		System.out.println(name + ": msgRestaurantFull recieved: deciding whether to stay or not.");
		state = AgentState.fullRestaurant;
		stateChanged();
	}
	public void msgGetOut() {
		System.out.println(name + ": msgGetOut received: I Got kicked out...");
		state = AgentState.kickedOut;
		stateChanged();
	}
	public void msgOutOfChoice() {
		System.out.println(name + ": msgOutOfChoice received: My selection is not available...");
		state = AgentState.outOfChoice;
		stateChanged();
	}
	public void msgHereIsYourCheck(Cashier ca, Check cx) {
		System.out.println(name + ": msgHereIsYourCheck received: Check get. Paying");
		this.ca = ca;
		this.cx = cx;
		checkRecieved.release();
		stateChanged();

	}
	public void msgFollowMe(HuangWaiterRole w, Menu m, int table) {
		setWaiter(w);
		this.m = m;
		this.table = table;
		event = AgentEvent.followWaiter;
		System.out.println(name + ": msgFollowMe received: Following Waiter to Table");
		stateChanged();
	}
	public void msgWhatDoYouWant() {
		if (name.equals("Steak") || name.equals("Chicken") || name.equals("Salad") || name.equals("Salad")) {
			choice = name;
		}
		else {
			choice = m.randomChoice();
		}
		event = AgentEvent.waiterArrived;
		System.out.println(name + ": msgWhatDoYouWant received: Waiter has arrived and asked what I want");
		stateChanged();
	}
	public void msgHereIsYourFood() {
		event = AgentEvent.receivedFood;
		System.out.println(name + ": msgHereIsYourFood received: Got mah food yo");
		stateChanged();
	}
	public void msgAnimationFinishedPay() {
		atCashier.release();
		System.out.println(name + ": msgFinishedPay received: At Cashier and paying");
		stateChanged();
	}
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		state = AgentState.BeingSeated;
		event = AgentEvent.seated;
		System.out.println(name + ": msgFinishedGoingToSeat received: Seated");
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		getPersonAgent().msgRoleFinished();
		event = AgentEvent.doneLeaving;
		System.out.println(name + ": msgLeavingFinished received: Peace l8");
		stateChanged();
	}
	public void msgAnimationAtHost() {
		atHost.release();	
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (state == AgentState.fullRestaurant) {
			state = AgentState.decideStay;
			stayOrLeave();
			return true;
		}
		if (state == AgentState.kickedOut) {
			state = AgentState.Leaving;
			leaveTable();
			return true;
		}
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter ){
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state = AgentState.Choosing;
			ChooseFood();
			return true;
		}

		if (state == AgentState.Choosing && event == AgentEvent.choiceSelected){
			state = AgentState.Ordering;
			OrderFood();
			return true;
		}
		if (state == AgentState.Ordering && event == AgentEvent.waiterArrived){
			sayOrder();
			return true;
		}
		if (state == AgentState.outOfChoice) {
			state = AgentState.possibleFlake;
			decideBadChoiceAction();
			return true;
		}
		if (state == AgentState.Ordering && event == AgentEvent.receivedFood) {
			state = AgentState.Eating;
			EatFood();
			return true;
		}
		if (state == AgentState.Eating && event == AgentEvent.doneEating) {
			System.out.println("Paying, ");
			state = AgentState.Paying;
			Pay();
			return true;
		}
		if (state == AgentState.Paying && event == AgentEvent.donePaying){
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

	private void stayOrLeave() {
		Random rng = new Random();
		int r = rng.nextInt();
		if((r % 2) == 1) {
			System.out.println("Customer " + name + " leaving Restaurant since it is full.");
			state = AgentState.Leaving;
			Do("Leaving.");
			host.msgLeavingRest(this);
			customerGui.FullCaseExitRestaurant();
		}
		else {
			System.out.println("Customer " + name + " Staying for the long haul!");
			state = AgentState.DoingNothing;
			event = AgentEvent.none;
		}
	}
	private void decideBadChoiceAction() {
		String badChoice = choice;
		if (badChoice == "Salad" && Cash >= 5.99 && Cash < 8.99) {
			System.out.println("Customer " + name + " leaving Restaurant. Cheapest choice unavailable.");
			state = AgentState.Leaving;
			leaveTable();
			return;
		}
		Random rng = new Random();
		int r = rng.nextInt();
		if (freeLoader == true) {
			System.out.println("freeloader = true");
			while (choice == badChoice) {
				choice = m.randomChoice();
			}
			state = AgentState.BeingSeated;
			event = AgentEvent.seated;
		}
		else if ((r % 2) == 0) {
			//Leave the restaurant = FLAKE
			System.out.println("Customer " + name + " leaving Restaurant. Flaking because choice unavailable.");
			state = AgentState.Leaving;
			leaveTable();
		}
		else if ((r % 2) == 1) {
			if (Cash < 5.99) {
				System.out.println("Customer " + name + " leaving Restaurant. Flaking because too poor :(.");
				state = AgentState.Leaving;
				leaveTable();
			}
			else if (Cash >= 5.99 && Cash < 8.99) {
				choice = "Salad"; //cheapest
				state = AgentState.BeingSeated;
				event = AgentEvent.seated;
			}
			else if (Cash >= 15.99) {
				while (choice == badChoice) {
					choice = m.randomChoice();
				}
				state = AgentState.BeingSeated;
				event = AgentEvent.seated;
			}
		}
		else {
			//Leave the restaurant = FLAKE
			System.out.println("Customer " + name + " leaving Restaurant. Flaking because choice unavailable.");
			state = AgentState.Leaving;
			leaveTable();
		}
	}

	private void Pay() {
		myWaiter.msgDoneEating(this);
		checkRecieved.acquireUninterruptibly();
		customerGui.DoGoToPay();
		atCashier.acquireUninterruptibly();
		if (Cash >= cx.price) {
			print("I have " + Cash + " for " + cx.price);
			getPersonAgent().setFunds(Cash-=cx.price);
			ca.msgHereIsMoney(this);
		}
		else {
			ca.msgNotEnoughMoney(this);
		}
		event = AgentEvent.donePaying;
	}

	private void goToRestaurant() {
		customerGui.DoGoToHost();
		atHost.acquireUninterruptibly();
		name = getPersonAgent().getName();
		Cash = getPersonAgent().getFunds();
		host.msgIWantToEat(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		Do("Being seated. Going to table");
		customerGui.DoGoToSeat(tableSpawnX + (table * tableOffSetX), tableSpawnY);
	}
	private void ChooseFood() {
		System.out.println(name + ": Choosing Food");
		timer.schedule(new TimerTask() {
			public void run(){
				event = AgentEvent.choiceSelected;
				stateChanged();
			}
		}, 7000);
	}
	private void OrderFood() {
		System.out.println(name + ": Ordering Food");
		event = AgentEvent.waitingForWaiter;
		myWaiter.msgReadyToOrder(this);
	}
	private void sayOrder() {
		myWaiter.msgHereIsMyChoice(this, choice);
		event = AgentEvent.orderSaid;		
	}
	private void EatFood() {
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
			public void run() {
				print("Done eating, " + choice);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		5000);//getHungerLevel() * 1000);//how long to wait before running task
	}
	private void leaveTable() {
		Do("Leaving.");
		if (myWaiter!= null) {
			myWaiter.msgLeavingTable(this);
		}
		customerGui.DoExitRestaurant();
	}

	// Accessors, etc.
	public void checkGiven() {
		checkRecieved.release();
	}
	public String getName() {
		return name;
	}
	
	public int getHungerLevel() {
		return hungerLevel;
	}
	public void setHungerLevel(int hungerLevel) {
		this.hungerLevel = hungerLevel;
		//could be a state change. Maybe you don't
		//need to eat until hunger lever is > 5?
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
	//HACKS
	public void hackPoorFreeLoader() {
		System.out.println("Freeloader activated.");
		Cash = 0;
		freeLoader = true;
	}
	public HuangWaiterRole getWaiter() {
		return myWaiter;
	}

}

