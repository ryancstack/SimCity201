package restaurant.tanRestaurant;

import restaurant.shehRestaurant.ShehHostAgent;
import restaurant.tanRestaurant.gui.CustomerGui;
//import restaurant.tanRestaurant.gui.RestaurantGui;
import agent.Role;
import restaurant.tanRestaurant.TanCashierAgent.Bill;
//import restaurant.HostAgent.Table;
import restaurant.tanRestaurant.TanHostAgent.Seat;
import restaurant.tanRestaurant.interfaces.Customer;
import restaurant.tanRestaurant.test.mock.EventLog;
import agent.Agent;
import gui.Building;

import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;
import java.awt.event.*;

import city.helpers.Directory;

/**
 * Restaurant customer agent.
 */
public class TanCustomerRole extends Role implements Customer{
	private String name;
	private int hungerLevel = 5;        // determines length of meal
	Timer timer = new Timer();
	private CustomerGui customerGui;
	public int mySeat=0;
	public CustOrder o;
	double cash= 20.00;
	public double debt= 0.00;
	
	public EventLog log = new EventLog();

	private Semaphore atCashier = new Semaphore(0,true);
	
	// agent correspondents
	private TanHostAgent host = (TanHostAgent) Directory.sharedInstance().getAgents().get("TanRestaurantHost");
	private TanWaiterRole waiter;
	private TanCashierAgent cashier= (TanCashierAgent) Directory.sharedInstance().getAgents().get("TanRestaurantCashier");

	//    private boolean isHungry = false; //hack for gui
	
	public static class CustOrder{
		CustOrder(int num){
			if (num==1) Choice= Choices.Steak;
			else if (num==2) Choice= Choices.Chicken;
			else if (num==3) Choice= Choices.Salad;
			else Choice= Choices.Pizza;
					
		}
		
		public enum Choices{Steak, Chicken, Salad, Pizza};
		private Choices Choice;
		
		public String getName(){
			if (Choice==Choices.Steak){
				return "Steak";
			}
			if (Choice==Choices.Chicken){
				return "Chicken";
			}
			if (Choice==Choices.Salad){
				return "Salad";
			}
			else
				return "Pizza";
		}
		
	}
	
	
	public enum sanityCheck
	{approachingCashier, none}
	public sanityCheck sc = sanityCheck.none;
	
	public enum OrderStatus
	{ordered, received, unavailable, none}
	public OrderStatus os = OrderStatus.none;
	
	public enum AgentState
	{DoingNothing, WaitingInRestaurant, BeingSeated, Seated, ReadyToOrder,ReadyToReorder, WaitingForFood, Eating, givenBill, DoneEating, PayingBill, Leaving, actCasual, Escaping, GoingToRestaurant};
	private AgentState state = AgentState.DoingNothing;//The start state

	public enum AgentEvent 
	{none, gotHungry, followHost, followWaiter, seated, placeOrder, placeReorder, served, givenBill, doneEating, doneLeaving, settledBill, informedNoFood, seatedInWaiting};
	AgentEvent event = AgentEvent.none;

	/**
	 * Constructor for CustomerAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	
	public TanCustomerRole(String location){
		super();
		//print("i'm here right now");
		customerGui = new CustomerGui(this);

		//host = (TanHostAgent) Directory.sharedInstance().getAgents().get("TanHostAgent");
		
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		for(Building b : buildings) {
			if (b.getName() == location) {
				b.addGui(customerGui);
			}
		}

		state=AgentState.DoingNothing;
		event= AgentEvent.gotHungry;
		//host.msgIWantFood(this);
	}
	/*
	public TanCustomerRole(String name){
		super();
		this.name = name;
		if(name.equals("broke")||name.equals("hobo"))
			cash=0.00;
		else if(name.equals("poorSalad")|| name.equals("VIP"))
			cash=5.00;
		else if(name.equals("poorSteak"))
			cash=15.00;
		else if(name.equals("poorChicken"))
			cash=10.00;
		else if(name.equals("poorPizza"))
			cash=8.00;
		else if(name.equals("enoughForSalad"))
			cash=7.00;
		else
			this.name=name;
		print("I have $"+cash+ " and owe $"+debt);
	}*/

	/**
	 * hack to establish connection to Host agent.
	 */
	public void setHost(TanHostAgent host) {
		this.host = host;
	}

	public void setWaiter(TanWaiterRole waiter){
		this.waiter= waiter;
	}
	
	public void setCashier(TanCashierAgent cashier){
		this.cashier= cashier;
	}
	
	public String getCustomerName() {
		return name;
	}
	// Messages
	
	public void msgWouldYouLikeToWait(){
		Random rand= new Random();
		int Picked= rand.nextInt(100);
		if(Picked< 40){ //set to 40
			// initiate leave
			print("IMMA BOUNCE");
			host.msgLeaveQueue(this);
			state = AgentState.DoingNothing;
			//customerGui.clearHungry();
			//leaveTable();
		}
		else{
			print("I'll wait in line");
			host.msgJoinQueue(this);
			//normative scenario
		}
	}

	public void msgPleaseLeave(){
		print("i'm leaving!");
		customerGui.DoLeave();
		host.msgLeavingRestaurant(this);
		state = AgentState.DoingNothing;
		getPersonAgent().msgRoleFinished();
	}
	

	public void msgAtCashier(){
		if(sc== sanityCheck.approachingCashier){
			sc= sanityCheck.none;
			atCashier.release();
		}
	}
	
	public void gotHungry() {//from animation
		print("I'm hungry");
		event = AgentEvent.gotHungry;
		stateChanged();
	}

	public void msgFollowMeToTable(int seatnumber, TanWaiterRole w) { //should take in Menu
		print("received msgFollowMeToTable");
		waiter=w;
		mySeat=seatnumber;
		event = AgentEvent.followWaiter;
		stateChanged();
	}
	
	public void msgWhatWouldYouLike(){
		event=AgentEvent.placeOrder;
		stateChanged();
		//event=AgentEvent.placeOrderNow;
	}
	
	public void msgHereIsYourFood(){
		if (state==AgentState.WaitingForFood)
			print("i'm waiting for food");
		print(" i'm served");
		event=AgentEvent.served;
		stateChanged();
	}
/*
	public void msgInformCustomerNoFood(Order o){
		event=AgentEvent.informedNoFood;
		os= OrderStatus.unavailable;
		//print("1. in msgInformCustNoFood order is " + o.getName());
		stateChanged();
	}*/
	
	public void msgHereIsYourBill(Bill b){
		//print("I have to pay $"+b.bill);
		state=AgentState.givenBill;
		stateChanged();
	}
	
	public void msgHereIsYourChange(double change, double d){
		cash= change;
		debt= d;
		print("I now have $"+cash + " and owe $"+ debt);
		event=AgentEvent.settledBill;
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

	public void msgPleaseTakeASeat(Seat seat, int seatNumber){
		GoToWaitingSeat(seatNumber); //assign seating seats, have to assign the spots in gui AND BE SURE TO SET UNOCCUPIED
		event=AgentEvent.seatedInWaiting;
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//	CustomerAgent is a finite state machine
/*
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.WaitingInRestaurant;
			goToRestaurant();
			return true;
		}*/
		
		//print("in customer role's scheduler");
		
		if (state == AgentState.DoingNothing && event == AgentEvent.gotHungry ){
			state = AgentState.GoingToRestaurant;
			goToRestaurant();
			return true;
		}
		
		if (state == AgentState.GoingToRestaurant && event == AgentEvent.seatedInWaiting ){
			state = AgentState.WaitingInRestaurant;
			return true;
		}
		
		if (state == AgentState.WaitingInRestaurant && event == AgentEvent.followWaiter ){ //changed to follow waiter instead of Host
			state = AgentState.BeingSeated;
			SitDown();
			return true;
		}
		
		if (state == AgentState.BeingSeated && event == AgentEvent.seated){
			state=AgentState.ReadyToOrder;
			callWaiterToOrder();
			
			/*javax.swing.Timer t = new javax.swing.Timer(1000, new ActionListener() {
		          public void actionPerformed(ActionEvent e) {
		              print("Hmm...I think I know what to order now.");
		        	  state=AgentState.ReadyToOrder;
		        	  CallWaiterToOrder();
		          }
		       });
			
			t.start();*/
			//t.stop();
			/*timer.schedule(new TimerTask() {
			public void run() {
				print("Hmm...I think I know what to order now");
				state = AgentState.ReadyToOrder;
				stateChanged(); //this seems to go in messages
				timer.();				
			}
		},
		2500);*/ //decides for 2500 time units
			//state = AgentState.ReadyToOrder;
			//ReadyToOrder();
			return true;
		}
		
		if (state == AgentState.ReadyToOrder && event == AgentEvent.placeOrder){
			state = AgentState.WaitingForFood;
			//os = OrderStatus.ordered;
			PlaceOrder();
			return true;
		}
		
		if (state == AgentState.ReadyToReorder && event == AgentEvent.placeOrder){
			state = AgentState.WaitingForFood;
			//print("DID I GET IN HEEREREEEEEEEEEEEEEEEE?");
			//os = OrderStatus.ordered;
			//PlaceReorder();asd
			return true;
		}
		
		if (state == AgentState.WaitingForFood && event == AgentEvent.informedNoFood){
			//possibly handle random branching to leaving or reordering here.
			/*
			 * state=AgentState.ReadyToReorder
			 * PlaceReorder();
			 */
			
			state=AgentState.ReadyToReorder;
			//print("this should be stage 2 in scheduler");
			PlaceReorder();
			//state = AgentState.Leaving;
			//leaveTable();
			return true;
		}
		
		if (state == AgentState.WaitingForFood && event == AgentEvent.served){
		//if(event==AgentEvent.served){
			state = AgentState.Eating;
			EatFood();
			return true;
		}

		if (state == AgentState.givenBill && event == AgentEvent.doneEating){
			//if(!(name.equals("Douche"))){
				state = AgentState.PayingBill; //agentstate=waitingtopay.
				sc= sanityCheck.approachingCashier;
				payBill(this);
			/*	}
			else if(name.equals("Douche")){
				state = AgentState.actCasual;
				dineAndDash();
			}*/
			//leaveTable();
			return true;
		}
		
		if (state == AgentState.PayingBill && event == AgentEvent.settledBill){
			print("Settled bill.");
			state = AgentState.Leaving; //agentstate=waitingtopay. 
			leaveTable();
			return true;
		}
		/*
		 if (state == AgentState.WaitingToPay && event == AgentEvent.GivenBill){ //givenBill from waiter
			state = AgentState.PayingBill;
			payBill();
			return true;
		 }
		 
		 if (state == AgentState.PayingBill && event == AgentEvent.PaidBill){ //msg for paid bill sent from cashier
		 	state = AgentState.Leaving;
			leaveTable();
		 */
		
		if (state == AgentState.Leaving && event == AgentEvent.doneLeaving){
			state = AgentState.DoingNothing;
			//no action
			return true;
		}
		return false;
	}

	// Actions

	private void dineAndDash(){
		print("SEEYA SUCKERS.");
		state=AgentState.Escaping;
		escapeTable();
	}
	
	private void payBill(TanCustomerRole c){
		print("Going to cashier");
		customerGui.GoToCashier();
		try{
			atCashier.acquire();
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
		cashier.msgHereIsMyMoney(c, c.cash);
	}
	
	private void goToRestaurant() {
		Do("Going to restaurant");
		host.msgIWantFood(this);//send our instance, so he can respond to us
	}

	private void SitDown() {
		host.msgWaitingSeatIsFree(this);
		customerGui.DoGoToSeat(mySeat);//hack; only one table HACK NO MOREEEE!
	}
	
	private void callWaiterToOrder(){
		Do("I'm ready to order.");
		waiter.msgReadyToOrder(this);
	}
	
	private void PlaceReorder(){
		int Picked;
		Random rand= new Random();
		Picked= rand.nextInt(4) + 1;
		if(o.getName().equals("Steak"))
			while (Picked==1){
				Picked= rand.nextInt(4) + 1;
			}
		else if (o.getName().equals("Chicken")){
			while (Picked==2){
				Picked= rand.nextInt(4) + 1;
			}
		}
		else if (o.getName().equals("Salad")){
			while (Picked==3){
				Picked= rand.nextInt(4) + 1;
			}
		}
		else{
			while (Picked==4){
				Picked= rand.nextInt(4) + 1;
			}
		}
		o= new CustOrder(Picked);
		os= OrderStatus.ordered;
		print("I want to reorder "+ o.getName());
		if( !name.equals("VIP") &&  ((o.getName().equals("Steak") && (cash < 15.99))||(o.getName().equals("Chicken") && (cash < 10.99))||(o.getName().equals("Salad") && (cash < 5.99))||(o.getName().equals("Pizza") && (cash < 8.99)))){
			print("I don't have enough money for anything else...");
			state = AgentState.Leaving;
			leaveTable();
		}//event = AgentEvent.placedOrder;
		else{
		event= AgentEvent.placeOrder;
		print("ReOrdered "+ o.getName());
		waiter.msgHereIsMyChoice(this, o); //implement choices later
		}
	}
	
	private void PlaceOrder() {
		double cheapestItem= 5.99;
		if( ((cash<cheapestItem)&& !(name.equals("Douche"))) && ((cash<cheapestItem)&& !(name.equals("VIP"))) ){
			print("I'm too broke for this stuff!");
			state = AgentState.Leaving;
			leaveTable();
		}
		
		else{
		os= OrderStatus.ordered;
		int Picked;
	/*
		if(name.equals("Steak")||name.equals("poorSteak")){
			Picked=1;
		}
		else if(name.equals("Chicken")||name.equals("poorChicken")){
			Picked=2;
		}
		else if(name.equals("Salad")||name.equals("poorSalad")){
			Picked=3;
		}
		else if(name.equals("Pizza")||name.equals("poorPizza")){
			Picked=4;
		}
		else{*/
			Random rand= new Random();
			Picked= rand.nextInt(4) + 1;
		//}
		
		if(cash>=cheapestItem && cash<8.99)
			Picked=3;
		
		o= new CustOrder(Picked);
		//if(!name.equals("VIP")){
			if((o.getName().equals("Steak") && (cash < 15.99))||(o.getName().equals("Chicken") && (cash < 10.99))||(o.getName().equals("Pizza") && (cash < 8.99))){
				state=AgentState.ReadyToReorder;
				PlaceReorder();
			}
		//}
		//waiter.msgHereIsMyChoice(this, o); //implement choices later
		//event = AgentEvent.placedOrder;
		print("Ordered "+ o.getName());
		waiter.msgHereIsMyChoice(this, o);
		//stateChanged(); //this wasn't here before.
		}
	}

	private void EatFood() {
		Do("Eating food.");
		os=OrderStatus.received;
		//This next complicated line creates and starts a timer thread.
		//We schedule a deadline of getHungerLevel()*1000 milliseconds.
		//When that time elapses, it will call back to the run routine
		//located in the anonymous class created right there inline:
		//TimerTask is an interface that we implement right there inline.
		//Since Java does not all us to pass functions, only objects.
		//So, we use Java syntactic mechanism to create an
		//anonymous inner class that has the public method run() in it.
		timer.schedule(new TimerTask() {
			//Object cookie = 1;
			public void run() {
				print("Done eating.");
				os= OrderStatus.none;
				event = AgentEvent.doneEating;
				//isHungry = false;
				stateChanged();
			}
		},
		10000);//getHungerLevel() * 1000);//how long to wait before running task
	}

	private void leaveTable() {
		Do("Leaving.");
		waiter.msgLeavingTable(this);
		customerGui.DoExitRestaurant();
	}

	private void escapeTable() {
		//Do("RUN!");
		waiter.msgLeavingTable(this);
		customerGui.DoEscapeRestaurant();
	}
	
	private void GoToWaitingSeat(int seatNumber){
		customerGui.DoGoToWaitingSeat(seatNumber);
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

	public void msgInformCustomerNoFood(Order o2) {
		event=AgentEvent.informedNoFood;
		os= OrderStatus.unavailable;
		//print("1. in msgInformCustNoFood order is " + o.getName());
		stateChanged();
	}
}

