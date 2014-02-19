package restaurant.tanRestaurant;

import agent.Agent;
import agent.Role;
import restaurant.shehRestaurant.ShehCashierAgent;
import restaurant.tanRestaurant.TanCashierAgent.Bill;
import restaurant.tanRestaurant.TanCookRole.roleState;
import restaurant.tanRestaurant.TanHostAgent.MyCust.WaitingStatus;
//import restaurant.HostAgent.MyWaiter.WorkingStatus;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer;
//import restaurant.WaiterAgent.WorkingStatus;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer.state;
import restaurant.tanRestaurant.gui.HostGui;
import restaurant.tanRestaurant.interfaces.Cashier;
import restaurant.tanRestaurant.interfaces.Cook;
import restaurant.tanRestaurant.test.mock.EventLog;

import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import city.helpers.Directory;

/**
 * Restaurant Host Agent
 */
//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class TanHostAgent extends Agent implements Cook{
	static final int NTABLES = 3;//a global for the number of tables.
	static final int NSEATS = 10;// waiting seats
	//Notice that we implement waitingCustomers using ArrayList, but type it
	//with List semantics.
	/*
	public List<CustomerAgent> waitingCustomers
	= new ArrayList<CustomerAgent>();*/
	public List<MyCust> waitingCustomers
	= Collections.synchronizedList(new ArrayList<MyCust>());
	public List<MyCust> waitingAreaCustomers
	= Collections.synchronizedList(new ArrayList<MyCust>());
	/*
	public List<WaiterAgent> Waiters
	= new ArrayList<WaiterAgent>();*/
	public List<MyWaiter> Waiters
	= Collections.synchronizedList(new ArrayList<MyWaiter>());
	public List<TanCookRole> Cooks
	= Collections.synchronizedList(new ArrayList<TanCookRole>());
	Cashier cashier;
	
	public Collection<Table> tables;
	public Collection<Seat> seats;
	
	public enum roleState
	{dead, alive, living};
	roleState state = roleState.dead;
	
	public enum CookStatus
	{none, arrived, working};
	CookStatus cookStatus = CookStatus.none;
	
	TanRestaurant restaurant;
	
	Timer timer = new Timer();
	
	public EventLog log = new EventLog();
	
	public int waitersOnBreak= 0;
	enum WorkingStatus{Working, wantsBreak, onBreak};
	public static class MyWaiter{

		MyWaiter(TanWaiterRole w){
			waiter= w;
			ws=WorkingStatus.Working;
		}
		TanWaiterRole waiter;
		WorkingStatus ws;
		
	}
	
	public static class MyCust{

		MyCust(TanCustomerRole c){
			cust= c;
			ws=WaitingStatus.pending;
		}
		TanCustomerRole cust;
		enum WaitingStatus{pending, asked, choseToWait, choseToLeave};
		WaitingStatus ws;
		
	}
	
	private String name;
	//private Semaphore atTable = new Semaphore(0,true);

	public HostGui hostGui = null;
	
	public TanHostAgent(String Location){//String location) {
		super();
		name= "Tan Host";
				
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
		seats= new ArrayList<Seat>(NSEATS);
		for (int x = 1; x <= NSEATS; x++) {
			seats.add(new Seat(x));//how you add to a collections
		}
		
		roleState state = roleState.alive;
		//cashier = (TanCashierAgent) Directory.sharedInstance().getAgents().get("TanRestaurantCashier");
	}
	
	public TanHostAgent() {
		super();
		name= "Tan Host";

		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
		seats= new ArrayList<Seat>(NSEATS);
		for (int x = 1; x <= NSEATS; x++) {
			seats.add(new Seat(x));//how you add to a collections
		}
	}
/*
	public TanHostAgent(String name) {
		super();

		this.name = name;
		//WaiterAgent w= new WaiterAgent("Tommy");
		//addWaiter(w);
		// make some tables
		tables = new ArrayList<Table>(NTABLES);
		for (int ix = 1; ix <= NTABLES; ix++) {
			tables.add(new Table(ix));//how you add to a collections
		}
		seats= new ArrayList<Seat>(NSEATS);
		for (int x = 1; x <= NSEATS; x++) {
			seats.add(new Seat(x));//how you add to a collections
		}
	}
*/
	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	/*
	public List getWaitingCustomers() {
		return waitingCustomers;
	}*/

	public Collection getTables() {
		return tables;
	}
	
	public Collection getSeats(){
		return seats;
	}
	// Messages
	
	public void msgBackToWork(){
		synchronized(Waiters){
		for(MyWaiter myw: Waiters){
			if(myw.ws==WorkingStatus.onBreak){
				myw.ws= WorkingStatus.Working;
				myw.waiter.waiterGui.setWaiterEnabled();
				//myw.msgBackToWork();
				return;
			}
		}
		}
	}

	public void msgIWantFood(TanCustomerRole c) {
		waitingAreaCustomers.add(new MyCust(c));
		print("Customer has entered.");
		stateChanged();
	}

	public void msgLeavingTable(TanCustomerRole cust) {
		for (Table table : tables) {
			if (table.getOccupant() == cust) { //doesn't enter here
				//print(cust + " leaving " + table);
				print(cust.getName() + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}

	public void msgAtTable() {//from animation
		//print("msgAtTable() called");
	//	atTable.release();// = true; LOOOOK HERERERERERE!!!!
		stateChanged();
	}
	
	//added new state for host at start
	public void msgAtStart(){//from animation
		stateChanged();
	}
	
	public void msgIWantBreak(TanWaiterRole w){
		print("i'm in msgIWantBreak");
		print("i've got "+ Waiters.size() + " waiters");
		if (!Waiters.isEmpty()){
			synchronized(Waiters){
			for (MyWaiter myw: Waiters ){
				if (myw.waiter==w){
					myw.ws= WorkingStatus.wantsBreak;
					print("okay i know you want a break, "+ myw.waiter.getName());
					stateChanged();
				}
			}
			}
		}
	}
	
	public void ReportForDuty(TanWaiterRole w){
		print(w.getName() + " reporting for duty.");
		addWaiter(w);
		//w.startThread();
		print("i've got "+ Waiters.size() + " waiters");/*
		for (MyWaiter myw: Waiters ){
			if (myw.waiter==w && myw.waiter.onBreak){
				myw.ws= WorkingStatus.wantsBreak;
				print("okay i know you want a break, "+ myw.waiter.getName());
				w.startThread();
				stateChanged();
				
			}
		}*/
		stateChanged();
	}
	
	public void msgLeaveQueue(TanCustomerRole c){
		synchronized(waitingCustomers){
		for(MyCust myc: waitingCustomers){
			if (c==myc.cust){
				myc.ws= WaitingStatus.choseToLeave;
			}
		}
		}
	}
	
	
	public void msgLeavingRestaurant(TanCustomerRole tanCustomerRole) {
		print("i'm gone!");
		//waitingAreaCustomers.remove(tanCustomerRole);
	}
	
	public void msgCookIsHere(TanCookRole tanCookRole){
		print("cook is here");
		Cooks.add(tanCookRole);
		cookStatus= CookStatus.arrived;
	}
	
	public void msgJoinQueue(TanCustomerRole c){
		synchronized(waitingCustomers){
		for(MyCust myc: waitingCustomers){
			if (c==myc.cust){
				myc.ws= WaitingStatus.choseToWait;
			}
		}
		}
	}
	
	public void msgWaitingSeatIsFree(TanCustomerRole c){
		for(Seat seat:seats){
			if (seat.getOccupant()==c)
				seat.setUnoccupied();
		}
	}

	public void msgReportingForDuty(TanWaiterRole w){
		print("WAITER REPORTING FOR DUTY");
		Waiters.add(new MyWaiter(w));
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
				
		if(cookStatus== CookStatus.arrived){
			synchronized(Waiters){
				for(MyWaiter w: Waiters){
					w.waiter.msgSetCook(Cooks.get(0));
					}
				cookStatus=CookStatus.working;
			}
		}
		
		if(!waitingAreaCustomers.isEmpty()){
			if(Waiters.isEmpty() || Cooks.isEmpty()){
				waitingAreaCustomers.get(0).cust.msgPleaseLeave();//msg leave bitches
				waitingAreaCustomers.remove(0);
			}
			
			else{
			for(Seat seat:seats){ //not entering here
				if(!seat.isOccupied()){
					print("Seating Customer!");

					seat.setOccupant(waitingAreaCustomers.get(0).cust);
					waitingAreaCustomers.get(0).cust.msgPleaseTakeASeat(seat, seat.seatNumber);
					waitingAreaCustomers.get(0).cust.mySeat=seat.seatNumber;
					waitingCustomers.add(waitingAreaCustomers.get(0)); //transfer to waiting customers
					waitingAreaCustomers.remove(waitingAreaCustomers.get(0));
					return true;
				}
			}
			}
		}
		
		if(restaurantIsFull()){
			for(int i=0; i< waitingCustomers.size(); i++){
				if(waitingCustomers.get(i).ws==WaitingStatus.choseToLeave){
					print("customer won't wait, removing from list. have a nice day!");
					waitingCustomers.remove(waitingCustomers.get(i));
				}
				else if(waitingCustomers.get(i).ws==WaitingStatus.pending){
					print("we're full. would you like to wait?");
					waitingCustomers.get(i).ws= WaitingStatus.asked;
					waitingCustomers.get(i).cust.msgWouldYouLikeToWait();
				}
					
			}
			/*
			if(restaurantIsFull() && waitingCustomers.get(0).ws==WaitingStatus.pending){
				print("we're full. would you like to wait?");
				waitingCustomers.get(0).ws= WaitingStatus.asked;
				waitingCustomers.get(0).cust.msgWouldYouLikeToWait();
				
			}*/
		}
		
		for (Table table : tables) { //runs too quickly for Host to return so goes through all tables false
			if (!table.isOccupied()) {
				if (!waitingCustomers.isEmpty()){ //&& hostGui.isAtStart()) {
					//if (restaurantIsFull()){
					//	waitingCustomers.get(0).msgWouldYouLikeToWait();
					//}
					//else{ //start else
					if(!Waiters.isEmpty()){
						
					MyWaiter assignedWaiter= Waiters.get(0);
					synchronized(Waiters){
					for(MyWaiter w:Waiters){
						if (w.ws==WorkingStatus.Working){
							assignedWaiter= w;
						}
					}
					}
					
					synchronized(Waiters){
					for(MyWaiter w: Waiters){
						if ((w.ws==WorkingStatus.Working) && (w.waiter.Customers.size()) < assignedWaiter.waiter.Customers.size()){
							assignedWaiter= w;
						}
					}
					}
					
					print("Instructing "+ assignedWaiter.waiter.getName()  +" to seat customer "+ waitingCustomers.get(0).cust.getName());
					WaiterSeatCustomer(assignedWaiter.waiter,waitingCustomers.get(0),table);
					//}
					//if there exists a waiter w such that w.state=notonbreak then
					
					return true;//return true to the abstract agent to reinvoke the scheduler.
					}
					//else print("Sorry...our waiters are on strike");
					//}//end else
				}
				
				return true;
			}
		}

		
		if(!Waiters.isEmpty()){
			synchronized(Waiters){
			for(MyWaiter myw:Waiters){
				if(myw.ws==WorkingStatus.wantsBreak){
					if(Waiters.size()==1){
						myw.ws= WorkingStatus.Working;
						print("No break for " + myw.waiter.getName());
						myw.waiter.waiterGui.setWaiterEnabled();
						//myw.waiter.msgNoBreakForYou();
					}
					else if((Waiters.size()-waitersOnBreak)==1){
						myw.ws= WorkingStatus.Working;
						print("No break for " + myw.waiter.getName());
						myw.waiter.waiterGui.setWaiterEnabled();
						//myw.waiter.msgNoBreakForYou();
					}
					else{
						myw.ws= WorkingStatus.onBreak;
						waitersOnBreak++;
						print("Go on your break, "+ myw.waiter.getName());
						timer.schedule(new TimerTask() {
							public void run() {
								msgBackToWork();
								print("Your break's up!");
								waitersOnBreak--;
							}
						},
						20000);//getHungerLevel() * 1000);//how long to wait before running task
						//print("IS THIS BEFORE OR AFTER YOUR BREAK IS UP!?");
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

	private void WaiterSeatCustomer(TanWaiterRole waiter, MyCust customer, Table table) {
		waiter.msgSeatCustAtTable(customer.cust, table.tableNumber);

		table.setOccupant(customer.cust); //originally below try catch
		
		waitingCustomers.remove(customer); //orignally below try catch

	}


	//utilities
	
	//severe not sure if this works...
	public void runthis(MyWaiter myw){
		print("Your break's up!");
		myw.ws= WorkingStatus.Working;
		waitersOnBreak--;
	}
	
//	public void setWaiter(WaiterAgent w){
//		MyWaiter= w;
//	}
//	
	public void addWaiter(TanWaiterRole w){
		Waiters.add(new MyWaiter(w));
	}

	public void setGui(HostGui gui) {
		hostGui = gui;
	}

	public HostGui getGui() {
		return hostGui;
	}
	

	public boolean restaurantIsFull(){
		boolean isFull= true;
		
		for (Table table : tables) { //runs too quickly for Host to return so goes through all tables false
			if (!table.isOccupied()) { //let's think...you've got 3 table and a for loop. if all 3=occ, return true
				isFull = false;
			}
			}
		return isFull;
	}
	
	public boolean waitingAreaIsFull(){
		boolean isFull= true;
		
		for (Seat seat : seats) { //runs too quickly for Host to return so goes through all tables false
			if (!seat.isOccupied()) { //let's think...you've got 3 table and a for loop. if all 3=occ, return true
				isFull = false;
			}
			}
		return isFull;
	}
	
	public static class Seat { //made waitingSeat static so accessible by waiter
		TanCustomerRole occupiedBy;
		int seatNumber;

		Seat(int seatNumber) {
			this.seatNumber = seatNumber;
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
			return "Seat " + seatNumber;
		}
	}
	
	private static class Table { //made table static so accessible by waiter
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

	public TanCashierAgent getCashier() {
		// TODO Auto-generated method stub
		return (TanCashierAgent) cashier;
	}

	public void setRestaurant(TanRestaurant tanRestaurant) {
		restaurant= tanRestaurant;
		
	}

	@Override
	public void msgHereIsBill(Bill b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void PassOrderToCook(int table, MyCustomer myc, Order o) {
		// TODO Auto-generated method stub
		
	}
}

