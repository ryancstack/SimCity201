package restaurant.phillipsRestaurant;


import restaurant.phillipsRestaurant.interfaces.*;
import restaurant.phillipsRestaurant.gui.*;
import agent.Agent;
import agent.Role;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

/**
 * Restaurant waiter agent.
 */
public class PhillipsWaiterRole extends Role implements Waiter{
	class MyCustomerW{
		String choice;
		Customer c;
		int table; 
		double moneyOwed;
		AgentState st;
		
		public MyCustomerW(String choice,Customer c, int table, AgentState cs){		
			this.choice = choice;
			this.c = c;
			this.table = table;
			this.st = cs;
			moneyOwed = 0;
		
		}
	}
	private List<MyCustomerW> customers;
	//map<String choice, int eatingTime> timeForEating;
	Timer timer = new Timer();
	boolean onBreak = false;
	private String name;
	public enum AgentState {Break,WaitingAtRestaurant,WaitingToBeSeated,Seated,ReadyToOrder,Ordering,Ordered,WaitingForFoodToCook,WaitingForFoodToTable,Eating,NeedToPay,CheckingPayment,WaitingForPayment,GoingToPay,Paying,Paid,Leaving,Left};
	private Cook cook;
	private Host host;
	private Cashier cashier;
	private Semaphore atTable = new Semaphore(0,true);
	private Semaphore atCook = new Semaphore(0,true);
	private Semaphore atHost = new Semaphore(0,true);
	private Semaphore atCashier = new Semaphore(0,true);
	private Semaphore atWaitingArea = new Semaphore(0,true);
	AgentState state = AgentState.WaitingAtRestaurant;
	public WaiterGui waiterGui = null;
	
	/**
	 * Constructor for WaiterAgent class
	 *
	 * @param name name of the waiter
	 */
	public PhillipsWaiterRole(String n){
		name = n;	
		customers = Collections.synchronizedList(new ArrayList<MyCustomerW>());
	}
	
	public void setCook(Cook c){
		cook = c;
	}
	public void setHost(Agent host){
		this.host = (Host) host;
	}
	public void setCashier(Agent cashier){
		this.cashier = (Cashier) cashier;
	}
	public int getCustomers(){
		return customers.size();
	}
	
	// Messages
	public void msgAtTable() {//from animation
		atTable.release();// = true;
		stateChanged();
	}
	public void msgAtCook() {//from animation		
		atCook.release();// = true;
		stateChanged();
	}
	public void msgAtHost() {//from animation		
		atHost.release();// = true;
		stateChanged();
	}
	public void msgAtCashier() {//from animation		
		atCashier.release();// = true;
		System.out.println("OUT OF CASHIER SEM");
		stateChanged();
	}
	public void msgAtWaitingArea() {//from animation		
		atWaitingArea.release();// = true;
		stateChanged();
	}
	public void msgTakeBreak(){
		state = AgentState.Break;
		stateChanged();
	}
	
	
	public void msgSeatCustomerAtTable(Customer c, int table){
		//print("Waiter received msgSeatCustomerAtTable");
		synchronized(this.customers){
			customers.add(new MyCustomerW(null,c,table,AgentState.WaitingAtRestaurant));
		}
		stateChanged();	
	}
	
	public void msgCustomerReadyToOrder(Customer c){
		//print("Waiter received msgCustomerReadyToOrder");
		for(int i=0; i<customers.size();i++){
			if(customers.get(i).c == c){
				customers.get(i).st = AgentState.ReadyToOrder;
			}
		}
		stateChanged();
	}
	public void msgHereIsMyChoice(Customer cust, String choice){
	//	print("Waiter received msgHereIsMyChoice");
		for (MyCustomerW c1: customers){
			if (c1.c == cust){
				c1.st = AgentState.Ordered;
				c1.choice = choice;
			}
		}
		stateChanged();
	}
	public void msgWaiterOutOfFood(String order,int tableNum){
	//	print("Waiter received message that it is out of " + order + " and is asking customer to reorder");
		for(MyCustomerW c:customers){
			if(c.table == tableNum && c.choice == order){
				//takeOrder(c);
				c.st = AgentState.ReadyToOrder;
			}
		}	
		stateChanged();
	}
	public void msgOrderReadyForPickup(String choice,int tablenum){
		//print("Waiter received msgOrderReadyForPickup");
		for (MyCustomerW c1: customers){
			if (c1.table == tablenum && c1.choice == choice){
				c1.st = AgentState.WaitingForFoodToTable;
			}
		}
		stateChanged();
	}
	public void msgWantToPay(Customer cust){
		//print("waiter received msgWantToPay");
		for (MyCustomerW c1: customers){
			if (c1.c == cust){
				c1.st = AgentState.NeedToPay;
			}
		}
		stateChanged();
	}
	public void msgPayFood(int table,double money){
		//print("waiter received msgPayFood");
		for (MyCustomerW c1: customers){
			if (c1.c.tableNum == table){
				c1.moneyOwed = money;
				c1.st = AgentState.GoingToPay;
			}
		}
		stateChanged();
	}
	public void msgLeavingTable(Customer cust){
		//print("waiter received msgLeavingTable");
		for (MyCustomerW c1: customers){
			if (c1.c == cust){
				c1.st = AgentState.Leaving;
			}
		}
		stateChanged();
	}
	public void doneEatingandLeaving(MyCustomerW mc){
	//in the background- timer for customer to eat food and when done //eating food, customer���s state is turned to leaving
	//mc.t.start(run(doneEatingFood(o)),timeForEating.get(o.choice//).eatingTime);
		mc.st = AgentState.Leaving;
	}



	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		if(onBreak == false){
			
		//if(customers.size()==0){
		//	takeBreak();
		//}
		synchronized(this.customers){
			for (int i=0; i< customers.size();i++){
				if(customers.get(i).st == AgentState.WaitingAtRestaurant){
					goToCustomer(customers.get(i));
					return true;
				}
			}
			
		}
		synchronized(this.customers){
			for (int i=0; i< customers.size();i++){
				if(customers.get(i).st == AgentState.WaitingToBeSeated){
					seatCustomer(customers.get(i));
					return true;
				}
			}
		}
		synchronized(this.customers){
			for (int i=0; i< customers.size();i++){
				if(customers.get(i).st == AgentState.ReadyToOrder){
					customers.get(i).st = AgentState.Ordering;
					takeOrder(customers.get(i));
					return true;
				}
			}
		}
		synchronized(this.customers){
			for (int i=0; i< customers.size();i++){
				if(customers.get(i).st == AgentState.Ordered && customers.get(i).choice != null){
					orderToCook(customers.get(i));
					return true;
				}
			}
		}
		synchronized(this.customers){
			for (int i=0; i< customers.size();i++){
				if(customers.get(i).st == AgentState.WaitingForFoodToTable){
					customers.get(i).st = AgentState.Eating;
					orderToCustomer(customers.get(i));
					return true;
				}
			}
		}
		synchronized(this.customers){
			for (int i=0; i< customers.size();i++){
				if(customers.get(i).st == AgentState.NeedToPay){
					pickUpCheck(customers.get(i));
					return true;
				}
			}
		}
		synchronized(this.customers){
			for (int i=0; i< customers.size();i++){
				if(customers.get(i).st == AgentState.CheckingPayment){
					takeCheckToCashier(customers.get(i));
					return true;
				}
			}
		}
		synchronized(this.customers){
			for (int i=0; i< customers.size();i++){
				if(customers.get(i).st == AgentState.GoingToPay){
					customerReadyToPay(customers.get(i));
					return true;
				}
			}
		}
		synchronized(this.customers){
			for (int i=0; i< customers.size();i++){
				if(customers.get(i).st == AgentState.Leaving){
					tellHostTablesFree(customers.get(i));
					return true;
				}
			}
		}
		}
		return false;
	}

	// Actions
	private void takeBreak(){
		onBreak = true;
		timer.schedule(new TimerTask() {
			public void run() {
				onBreak = false;
				//isHungry = false;
				stateChanged();
			}
		},
		10000);
	}
	private void GoToHost(){
		//Do("Going to Host");
		DoGoToHost();

		try {
			atHost.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	private void goToCustomer(MyCustomerW cust){
		Do("Going to waiting area"); 
		DoGoToWaitingArea();
		try {
			atWaitingArea.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cust.st = AgentState.WaitingToBeSeated;
		stateChanged();
	}
	private void seatCustomer(MyCustomerW cust){
		Do("Seating customer"); 
		cust.c.msgFollowMe(this,cust.table,new Menu());
		DoSeatCustomer(cust);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cust.st = AgentState.Seated;
	}
	private void takeOrder(MyCustomerW cust){
		Do("Taking order");
		DoTakeOrder(cust);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cust.st = AgentState.Ordered;
		cust.c.msgWhatDoYouWant();
	}
	
	private void orderToCook(MyCustomerW cust){
		Do("Taking order to cook");
		GiveCookOrder(cust);
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cust.st = AgentState.WaitingForFoodToCook;
		cook.msgHereIsOrder(this,cust.choice,cust.table);
	}
	private void orderToCustomer(MyCustomerW cust){
		Do("Taking food to customer");
		DoGoToCook();
		try {
			atCook.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		DeliverOrderToTable(cust); 
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cust.c.msgEatMeal();
	}
	private void pickUpCheck(MyCustomerW cust){
		Do("Picking up check from customer");
		DoSeatCustomer(cust);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cust.st = AgentState.CheckingPayment;
		stateChanged();
	}
	private void takeCheckToCashier(MyCustomerW cust){
		Do("Taking check to cashier");
		DoGoToCashier();
		try {
			atCashier.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cust.st = AgentState.WaitingForPayment;
		cashier.msgHereIsCheck(cust.choice,cust.c.tableNum,this);
		stateChanged();
	}
	private void customerReadyToPay(MyCustomerW cust){
		Do("Going to customer to tell him to pay at cashier");
		DoSeatCustomer(cust);
		try {
			atTable.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		cust.st = AgentState.Paying;
		cust.c.msgYouMayPay(cust.moneyOwed);
		stateChanged();
	}
	private void tellHostTablesFree(MyCustomerW cust){
		/*Do("Telling host tables free");
		DoGoToHost();
		try {
			atHost.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		cust.st = AgentState.Left;
		host.msgLeavingTable(cust.c);
		customers.remove(0);
	}
	
	public void askHostForBreak(){
		host.msgCanWaiterTakeBreak(this);
	}
	
//GUI actions
	public void DoGoToHost(){
		waiterGui.DoGoToHost();
	}
	public void DoGoToCook(){
		waiterGui.DoGoToCook();
	}
	public void DoGoToWaitingArea(){
		waiterGui.DoGoToWaitingArea();
	}
	public void DoSeatCustomer(MyCustomerW mc){
		waiterGui.DoBringToTable(mc.c,mc.table);
	}
	public void DoTakeOrder(MyCustomerW mc){
		waiterGui.DoTakeCustomerOrder(mc.c);
	}
	public void GiveCookOrder(MyCustomerW mc){
		waiterGui.DoGoToCook();
	}
	public void DeliverOrderToTable(MyCustomerW mc){
		waiterGui.DoBringFoodToTable(mc.c,mc.choice,mc.table);
	}
	public void DoGoToCashier(){
		waiterGui.DoGoToCashier();
	}

//utilities
	public void setGui(WaiterGui gui) {
		waiterGui = gui;
	}	
	
	public WaiterGui getGui() {
		return waiterGui;
	}


}	