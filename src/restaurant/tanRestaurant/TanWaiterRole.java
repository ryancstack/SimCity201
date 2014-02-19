package restaurant.tanRestaurant;

import gui.Building;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.concurrent.Semaphore;

import restaurant.tanRestaurant.TanCashierAgent.Bill;
import restaurant.tanRestaurant.TanCustomerRole.CustOrder;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer.state;
//import restaurant.gui.HostGui;
import restaurant.tanRestaurant.gui.WaiterGui;
import restaurant.tanRestaurant.interfaces.Cook;
//import restaurant.CustomerAgent;
import restaurant.tanRestaurant.interfaces.Waiter;
import restaurant.tanRestaurant.test.mock.EventLog;
import agent.Role;
import city.helpers.Directory;
//import restaurant.tanRestaurant.TanCustomerRole.Order;

/**
 * Restaurant Waiter Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class TanWaiterRole extends Role implements Waiter{
	static final int NTABLES = 3;//a global for the number of tables.
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	public List<MyCustomer> Customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public Collection<Table> tables;
	//note that tables is typed with Collection semantics.
	//Later we will see how it is implemented
	
	public int targetTable;

	private String name;
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atCashier = new Semaphore(0,true);
	public Semaphore atCook = new Semaphore(0,true);
	private Semaphore atWaitingCustomer = new Semaphore(0,true);
	public EventLog log = new EventLog();
	
	public enum roleState
	{dead, alive, living};
	roleState rstate = roleState.dead;

	public WaiterGui waiterGui; //=null;

	//WorkingStatus workingStatus;
	//enum WorkingStatus{Working, onBreak};

	public static WaiterState ws;
	enum WaiterState{askedOrder, approachingTable, atTable, outOfFood};

	public boolean onBreak;
	private TanHostAgent host;
	protected TanCookRole cook;
	private TanCashierAgent cashier;
	Timer timer = new Timer();

	/*public static class Bill{
		
		public Bill(Cashier c, Customer cust, int t, double price){ //(Cashier, Customer, int tableNum, double price)
			customer=cust;
			cashier= c;
			bs= billState.Pending;
			debt=0;
			
			bill=price;
		}
		Cashier cashier;
		WaiterAgent waiter;
		Customer customer;
		//CustomerAgent customer;
		public int tableNum;
		public double bill;
		public double change;
		public double debt;
		public billState bs;
		public enum billState{Pending, sentOut, Paid, Settled};
	}*/

	/*
	public static class MyCustomer extends CustomerAgent{
		public MyCustomer(String name) { //quickfix
			super(name);
			// TODO Auto-generated constructor stub
		}
		
		CustomerAgent c;
		int table;
		String choice;
		
		public enum AgentState
		{Waiting, Seated, ReadyToOrder, Asked, Eating};
		private AgentState state = AgentState.Waiting;//The start state	
		
	}*/

	public static class MyCustomer{

		MyCustomer(TanCustomerRole customer, int tab, int seat, TanWaiterRole w){
			c=customer;
			table= tab;
			mySeat= seat;
			s=state.waitingToBeSeated;
			waiter= w;
		}

		TanWaiterRole waiter;
		TanCustomerRole c;
		int table;
		int mySeat;
		Order o;
		//String choice;
		state s;
		enum state{waitingToBeSeated, Seated, readyToOrder, approachedByWaiter, doneOrdering, askedOrder, placedOrder, waitingForFood, Salivating, Served, whereIsMyFood, givenBill, payingBill, leaving, left};
	}

	public TanWaiterRole(String location) {
		super();
		
		host = (TanHostAgent) Directory.sharedInstance().getAgents().get("TanRestaurantHost");
		cashier = (TanCashierAgent) Directory.sharedInstance().getAgents().get("TanRestaurantCashier");

		//cashier = host.getCashier();
		name= "Cashier 1";
		
		waiterGui = new WaiterGui(this);
		rstate = roleState.alive;
		
		List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
		
		for(Building b : buildings) {
			if (b.getName() == location) {
				b.addGui(waiterGui);
			}
		}
	}
	/*
	public TanWaiterRole(String name) {
		super();

		//WaiterAgent w= this;
		this.name = name;
		onBreak= false;
		// make some tables //waiters don't create their own tables

		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}

	}
*/
	public String getWaiterName() {
		return name;
	}

	public String getName() {
		return name;
	}

	//public List getWaitingCustomers() {
	//	return waitingCustomers;
	//}

	public Collection getTables() {
		return tables;
	}
	// Messages

	public void msgSeatCustAtTable(TanCustomerRole cust, int tablenumber){
		print("Received instruction. Seating customer "+ cust.getName());
		Customers.add(new MyCustomer(cust, tablenumber, cust.mySeat, this));
		stateChanged(); 
	}

	public void msgReadyToOrder(TanCustomerRole cust){
		print("received msgReadyToOrder");
		synchronized(Customers){
		for (MyCustomer myc:Customers ){
			if (myc.c==cust){
				myc.s=state.readyToOrder;
				print("stateChanged in msgreadytoorder");
				stateChanged();
			}
		}
		}
	}

	public void msgHereIsMyChoice(TanCustomerRole cust, CustOrder co){ //implement choice later
		synchronized(Customers){
		for (MyCustomer myc:Customers ){
			if (myc.c==cust){
				print("co is "+co.getName());
				myc.o= new Order(this,co.getName(),myc.table,0);
				if(myc.o==null)
					print("in msgHereismychoice, myc.o is not properly set");
				myc.s=state.placedOrder;
				stateChanged();
			}
		}
		}
	}

	public void msgisAtTable(){
		if (ws == WaiterState.approachingTable) {
			ws = WaiterState.atTable;
		} else {
			ws=WaiterState.askedOrder;
			stateChanged();
		}
	}

	public void msgOrderIsReady(int t){
		//order.state= readyToServe;
		print("Received order pickup request from cook.");
		synchronized(Customers){
		for (MyCustomer myc:Customers ){
			if (myc.table==t){
				myc.s=state.Salivating;
				stateChanged();
			}
		}
		}
	}

	public void msgLeavingTable(TanCustomerRole cust) {
		synchronized(Customers){
		for (MyCustomer myc:Customers ){
			if (myc.c==cust){
				myc.s=state.leaving;
				stateChanged();
			}
		}
		}
	}	
	

	public void msgAtCashier(){
		atCashier.release();
	}
	
	/*
	public void msgBackToWork(){
		hasRe = false;
		gui.setCustomerEnabled(agent);
	}*/

	public void msgOutOfFood(Order ord){
		//ws = WaiterState.outOfFood;
		//stateChanged();
		print("in msgOutOfFood which should be from cook");
		synchronized(Customers){
		for (MyCustomer myc:Customers ){
			if (myc.o==ord){
				ws = WaiterState.outOfFood;
				myc.s=state.whereIsMyFood;
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

	//added new state for host at start
	public void msgAtStart(){//from animation
		stateChanged();
	}

	public void msgWantABreak(){ //from gui
		WantABreak();
	}
	
	public void msgHereIsBill(Bill b){
		for(MyCustomer myc: Customers){
			if(myc.c==b.customer){
				print("got the right cust");
				waiterGui.DoServeFood(myc.table);
				//waiterGui.DoLeaveCustomer();
				try {
				atTable.acquire();
				} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}
				myc.c.msgHereIsYourBill(b);
				myc.s=state.givenBill;

				waiterGui.DoLeaveCustomer();
			}
		}
	}
	
	public void msgAtWaitingCustomer(){
		print("released from waiting customer");
		atWaitingCustomer.release();
	}
	
	public void msgAtCook(){
		atCook.release();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		if (rstate==roleState.alive){
			GoToPost();
		}
		
		//print("Now i'm in waiter's scheduler");
		
		if (!Customers.isEmpty()){	

			synchronized(Customers){
			for(MyCustomer myc: Customers){
				if(myc.s==state.Served){
					getBillFromCashier(myc);//waiterGui.DoLeaveCustomer(); //hack because semaphores are hard
					myc.s= state.payingBill;
					return true;
				}
			}
			}
			
			synchronized(Customers){
			for(MyCustomer myc: Customers){
				if(myc.s==state.leaving){
					informHostCustLeft(myc); //hack neglecting cook and choice
					Customers.remove(myc); //THIS IS TO KEEP WAITER ASSIGNMENT EFFICIENT. SHOULD BE FIXED EVENTUALLY
					return true;
				}
			}
			}

			synchronized(Customers){
			for(MyCustomer myc: Customers){
				if(myc.s==state.Salivating){
					serveCustomer(myc, myc.o); //hack neglecting cook and choice
					return true;
				}
			}
			}
			
			synchronized(Customers){
			for(MyCustomer myc: Customers){
				if(myc.s==state.placedOrder){ //&& waiterGui.isAtStart()){
					print("table number: "+myc.table);
					if(myc.o== null)
						print("myc.o is null");
					print("order: "+myc.o.getName());
					
					PassOrderToCook(myc.table, myc, myc.o);
					return true;
				}
			}
			}

			synchronized(Customers){
			for(MyCustomer myc: Customers){
				if(myc.s==state.approachedByWaiter){ //&& (ws==WaiterState.askedOrder)){
					takenOrder(myc);
					return true;
				}
			}
			}
			
			if(ws==WaiterState.approachingTable){
				synchronized(Customers){
				for(MyCustomer myc: Customers){
					if(myc.s==state.readyToOrder){
						takeOrder(myc);; //state becomes approachedByWaiter
						return true;
					}
				}
				}
			}

			synchronized(Customers){
			for(MyCustomer myc: Customers){
				if(myc.s==state.readyToOrder){
					approachTable(myc); //gui approaches table, state goes to approachingtable
					return true;
				}
			}
			}

			/*
			for (MyCustomer myc: Customers){
				if (myc.s==state.waitingToBeSeated && waiterGui.isAtStart()){
					print("here i am");
					//approachCustomer(myc, myc.seat);
					return true;
				}
			}*/
			
			
			synchronized(Customers){
			for (MyCustomer myc: Customers){
				if (myc.s==state.waitingToBeSeated && waiterGui.isAtStart()){
					seatCustomer(myc, myc.table, myc.mySeat);
					return true;
				}
			}
			}

			synchronized(Customers){
			for(MyCustomer myc: Customers){
				if(myc.s==state.whereIsMyFood && ws==WaiterState.outOfFood ){
					print("whereisCust'sfood, informing no food");
					informCustomerNoFood(myc); //hack neglecting cook and choice
					return true;
				}
			}
			}
			
			
			if(ws==WaiterState.approachingTable){ //duplicate for timing purposes?
				synchronized(Customers){
				for(MyCustomer myc: Customers){
					if(myc.s==state.readyToOrder){
						takeOrder(myc);; //state becomes approachedByWaiter
						return true;
					}
				}
				}
			}

		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
		/*}
		catch(ExecutionException e){
			
		}*/
	}

	// Actions
	
	private void GoToPost() {
		rstate= roleState.living;
		host.msgReportingForDuty(this);	
		waiterGui.DoGoToPost();
	}
	
	private void getBillFromCashier(MyCustomer myc){
		waiterGui.GoToCashier();
		try{
			atCashier.acquire();
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
		cashier.msgCollectingBill(myc.o.getName(), this, myc.c);

	}
	
	private void informCustomerNoFood(MyCustomer myc){
		//myc.s=state.readyToOrder;
		//what about the animation?
		ws = WaiterState.atTable;
		waiterGui.DoServeFood(myc.table);
		//waiterGui.DoLeaveCustomer();
		try {
		atTable.acquire();
		} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		//print("Sorry, we're out of "+ myc.o.getName());
		myc.c.msgInformCustomerNoFood(myc.o);
		waiterGui.DoLeaveCustomer();
		print("Sorry, we're out of "+ myc.o.getName());
	}
	
	private void informHostCustLeft(MyCustomer c){
		host.msgLeavingTable(c.c);
		c.s=state.left;
		stateChanged();
	}
	
	private void seatCustomer(MyCustomer customer, int table, int seat) { //params given by Host
		//System.out.println("Seating customer");
		
		waiterGui.approachWaitingCustomer(seat);
		try {
			atWaitingCustomer.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		print("after released from waiting");
		customer.c.msgFollowMeToTable(table,this);//added tableNumber in message
		DoSeatCustomer(customer.c, table); //animation
		//customer.s=state.Seated;

		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		customer.s=state.Seated;
		//customer.state= Seated;
		waiterGui.DoLeaveCustomer();
		print("i'm at end of seatCustomer");
		if(name.equals("Break")||name.equals("Break1")||name.equals("Break2")){
			WantABreak();
		}
		stateChanged();
	}

	// The animation DoXYZ() routines
	private void DoSeatCustomer(TanCustomerRole customer, int table) { //changed from Table table to int table
		//Notice how we print "customer" directly. It's toString method will do it.
		//Same with "table"
		print("Seating " + customer + " at table " + table);
		waiterGui.DoBringToTable(customer, table); //added table as param

	}

	private void approachTable(MyCustomer c){
		waiterGui.ApproachTable(c.table);
		targetTable= c.table;
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ws = WaiterState.approachingTable;
	}

	private void takeOrder(MyCustomer c){
		print("What would you like?");
		c.c.msgWhatWouldYouLike();
		c.s=state.approachedByWaiter;
		stateChanged();
	}

	private void takenOrder(MyCustomer c){
		waiterGui.DoLeaveCustomer();
		//asdasd
		//c.s=state.placedOrder; asdasd
		stateChanged();
	}

	//public void PassOrderToCook(int tablenum, MyCustomer c, Order o){ //implement param order
		
		/*waiterGui.DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//print("Cook, please cook "+ o.getName());
		cook.msgHereIsAnOrder(tablenum, o, this);
		c.s=state.waitingForFood;
		waiterGui.DoLeaveCustomer();*/

		//stateChanged(); //added
	//}

	private void serveCustomer(MyCustomer c, Order o){ //implement param order
		//waiterGui.DoPickUpFood();
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("Picked up "+o.getName());
		
		waiterGui.DoServeFood(c.table);
		//waiterGui.DoLeaveCustomer();
		try {
		atTable.acquire();
		} catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		}
		waiterGui.DoLeaveCustomer();
		print("Here's your " + o.getName() +"!");
		c.c.msgHereIsYourFood();
		c.s=state.Served;
		stateChanged();
	}
	
	public void WantABreak(){
		//print("BREAEAEEAEAKEAKAEKAEKEAKEAKAEKEAKAEKEAKAEK");
		host.msgIWantBreak(this);
	}
	
	//utilities

	public void setHost(TanHostAgent h){
		host=h;
	}

	public void setCook(TanCookRole c){
		cook=c;
	}
	
	public void setCashier(TanCashierAgent c){
		cashier=c;
	}

	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}

	public WaiterGui getGui() {
		return waiterGui;
	}

	private class Table {
		TanCustomerRole occupiedBy;
		int tableNumber;

		Table(int tableNumber) {
			this.tableNumber = tableNumber;
		}

		void setOccupant(TanCustomerRole cust) {
			occupiedBy = cust;
		}

		void setUnoccupied() {
			occupiedBy = null;
		}

		TanCustomerRole getOccupant() {
			return occupiedBy;
		}

		boolean isOccupied() {
			return occupiedBy != null;
		}

		public String toString() {
			return "table " + tableNumber;
		}
	}


	@Override
	public void PassOrderToCook(int table, MyCustomer myc, Order o) {
		waiterGui.DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		print("Cook, please cook "+ o.getName()+"table num is"+table);
		cook.msgHereIsAnOrder(table, o, this);
		myc.s=state.waitingForFood;
		waiterGui.DoLeaveCustomer();

		//stateChanged();
	}
	protected void PassOrderToCook(MyCustomer myc) {
		// TODO Auto-generated method stub
		
	}
	public void msgSetCook(TanCookRole tanCookRole) {
		cook= tanCookRole;	
	}


	/*protected void PassOrderToCook(int table, MyCustomer myc,
			restaurant.tanRestaurant.Order o) {
		// TODO Auto-generated method stub
		
	}*/
}
