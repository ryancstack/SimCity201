package restaurant.nakamuraRestaurant;

import restaurant.nakamuraRestaurant.gui.CustomerGui;
import restaurant.nakamuraRestaurant.helpers.Check;
import restaurant.nakamuraRestaurant.helpers.Menu;
import restaurant.nakamuraRestaurant.interfaces.Customer;
import agent.Role;
import gui.Building;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import city.helpers.Directory;

/**
 * Restaurant customer agent.
 */
public class NakamuraCustomerRole extends Role implements Customer{
	private String name;
	private String myLocation;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	private Random generator = new Random();

	// agent correspondents
	private NakamuraHostAgent host;
	private NakamuraWaiterRole waiter;
	private NakamuraCashierAgent cashier;
	private Menu menu;
	private Check check;
	private String choice = null;
	private List<String> cantOrder = new ArrayList<String>();

	//    private boolean isHungry = false; //hack for gui
	public enum AgentState
	{DoingNothing, Entering, WaitingInRestaurant, BeingSeated, Sitting, Seated, Ordering, WaitingForFood, Eating, DoneEating, WaitingForCheck, Paying, Leaving, GoingToPay};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, inside, inWaitingArea, noSeat, meetWaiter, followWaiter, seated, ordered, reorder, gotFood, doneEating, gotCheck, donePaying, doneLeaving, closed, finishedPaying, reachedCashier};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public NakamuraCustomerRole(String location){
		super();
		
		customerGui = new CustomerGui(this);

		host = (NakamuraHostAgent) Directory.sharedInstance().getAgents().get("NakamuraRestaurantHost");
		cashier = (NakamuraCashierAgent) Directory.sharedInstance().getAgents().get("NakamuraRestaurantCashier");

		myLocation = location;
		menu = new Menu();
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui(customerGui);
			}
		}
	}
	
	public void setWaiter(NakamuraWaiterRole waiter) {
		this.waiter = waiter;
	}
	
	public void setMenu(Menu m) {
		this.menu = m;
	}

	public String getCustomerName() {
		return name;
	}
	
	// Messages
	public void msgGotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}
	
	public void msgRestaurantFull() {
		print("Received msgRestaurantFull");
		event = AgentEvent.noSeat;
		stateChanged();
	}
	
	public void msgRestaurantClosed() {
		print("Received msgRestaurantClosed");
		event = AgentEvent.closed;
		stateChanged();
	}

	public void msgFollowMe(NakamuraWaiterRole w, Menu menu) {
		print("Received msgFollowMe");
		setWaiter(w);
		setMenu(menu);
		event = AgentEvent.meetWaiter;
		stateChanged();
	}
	
	public void msgGetOrder() {
		print("Received msgGetOrder");
		event = AgentEvent.ordered;
		stateChanged();
	}

	public void msgReorder() {
		print("Received msgGetOrder");
		event = AgentEvent.reorder;
		cantOrder.add(choice);
		stateChanged();
	}
	
	public void msgEatFood() {
		print("Received msgEatFood");
		event = AgentEvent.gotFood;
		stateChanged();
	}
	
	public void msgHeresCheck(Check c) {
		print("Received msgHeresCheck");
		this.check = c;
		event = AgentEvent.gotCheck;
		stateChanged();
	}
	
	public void msgHeresChange(double change) {
		print("Received msgHeresChange");
		event = AgentEvent.donePaying;
		stateChanged();
	}
	
	public void msgPayNextTime(double debt) {
		print("Received msgPayNextTime");
		event = AgentEvent.donePaying;
		stateChanged();
	}
	
	public void msgAnimationFinishedEnter() {
		event = AgentEvent.inside;
		stateChanged();
	}
	
	public void msgAnimationFinishedWaiting() {
		event = AgentEvent.inWaitingArea;
		stateChanged();
	}
	
	public void msgAnimationFinishedSitting() {
		event = AgentEvent.followWaiter;
		stateChanged();
	}
	
	public void msgAnimationFinishedGoToSeat() {
		//from animation
		event = AgentEvent.seated;
		stateChanged();
	}
	public void msgAnimationFinishedGoingToCashier() {
		event = AgentEvent.reachedCashier;
		stateChanged();
	}
	public void msgAnimationFinishedLeaveRestaurant() {
		//from animation
		event = AgentEvent.doneLeaving;
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.Entering;
			goToRestaurant();
			return true;
		}
		if (state == AgentState.Entering && event == AgentEvent.inside){
			state = AgentState.WaitingInRestaurant;
			AskForSeat();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.meetWaiter){
			state = AgentState.BeingSeated;
			print("Being Seated");
			SitDown();
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.noSeat){
			if(generator.nextInt(2) == 1) {
				state = AgentState.Leaving;
				leaveRestaurant();
			}
			else {
				event = AgentEvent.none;
				print("Staying");
				host.msgStaying(this);
			}
			return true;
		}
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.closed){
			state = AgentState.Leaving;
			print("Leaving");
			leaveRestaurant();
			return true;
		}
		if (state == AgentState.BeingSeated && event == AgentEvent.followWaiter){
			state = AgentState.Sitting;
			GoToSeat();
			return true;
		}
		if (state == AgentState.Sitting && event == AgentEvent.seated){
			state = AgentState.Ordering;
			Choose();
			return true;
		}
		if (state == AgentState.Ordering && event == AgentEvent.ordered){
			state = AgentState.WaitingForFood;
			Order();
			//no action
			return true;
		}
		if (state == AgentState.WaitingForFood && event == AgentEvent.reorder){
			state = AgentState.Ordering;
			Choose();
			return true;
		}
		if (state == AgentState.WaitingForFood && event == AgentEvent.gotFood){
			state = AgentState.Eating;
			print("Eating");
			EatFood();
			return true;
		}
		
		if (state == AgentState.Eating && event == AgentEvent.doneEating){
			state = AgentState.WaitingForCheck;
			getCheck();
			return true;
		}
		
		if (state == AgentState.WaitingForCheck && event == AgentEvent.gotCheck){
			state = AgentState.GoingToPay;
			GoToCashier();
			return true;
		}
		
		if (state == AgentState.GoingToPay && event == AgentEvent.reachedCashier){
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
			print("Left");
			getPersonAgent().msgRoleFinished();
			return true;
		}
		return false;
	}

	// Actions

	private void goToRestaurant() {
		Do("Going to restaurant");
		customerGui.setPresent(true);
		customerGui.DoEnterRestaurant();
	}
	
	private void AskForSeat() {
		host.msgIWantFood(this);//send our instance, so he can respond to us
		customerGui.DoGoToWaiting();
	}

	private void SitDown() {
		customerGui.DoGoToHost();
	}
	
	private void GoToSeat() {
		waiter.msgHereIAm(this);
	}
	
	private void Choose() {
		//Choose food
		String c;
		timer.schedule(new TimerTask() {
			public void run() {
				ChooseMessage();
				stateChanged();
			}
		},
		5000);
		do{
			c = menu.choices.get(generator.nextInt(4));
			if(menu.prices.get(c) > getPersonAgent().getFunds() && !name.equals("Broke") && !cantOrder.contains(c)) {
				print("Can't afford " + c);
				cantOrder.add(c);
			}
			if(cantOrder.size() == 4){
				leaveTable();
				state  = AgentState.Leaving;
				break;
			}
		} while(cantOrder.contains(c));
		
		choice = c;
		stateChanged();
	}
	
	private void ChooseMessage() {
		waiter.msgReadyToOrder(this);
	}
	
	private void Order() {
		print("Ordering " + choice);
		waiter.msgHeresOrder(this, choice);
		customerGui.DoSetChoice(choice);
	}

	private void EatFood() {
		//Do("Eating Food");
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		customerGui.DoEat();
		timer.schedule(new TimerTask() {
			Object cookie = 1;
			public void run() {
				print("Done eating, cookie=" + cookie);
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		10000);//getHungerLevel() * 1000);//how long to wait before running task
		stateChanged();
	}
	
	private void getCheck() {
		print("Getting Check");
		waiter.msgCheckPlease(this);
	}
	private void GoToCashier() {
		print("Going to cashier");
		customerGui.DoGoToCashier();
	}
	
	private void Pay() {
		print("Paying");
		print("Amount due: $" + check.getTotal() + ".00");
//		if(name.equals("Broke")) {
//			print("Not Enough Money");
//			cashier.msgPayment(this, check, 0);
//		}
//		else {
		
			cashier.msgPayment(this, check, menu.prices.get(choice));
			getPersonAgent().setFunds(getPersonAgent().getFunds() - menu.prices.get(choice));
//		}
	}

	private void leaveTable() {
		Do("Leaving.");
		waiter.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
	}
	
	private void leaveRestaurant() {
		print("Leaving");
		host.msgLeaving(this);
		customerGui.DoExitRestaurant();
		getPersonAgent().msgRoleFinished();
	}

	// Accessors, etc.

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
}

