package restaurant.tanRestaurant;

import agent.Agent;
import restaurant.tanRestaurant.TanCashierAgent.Bill.billState;
//import restaurant.tanRestaurant.TanCashierAgent.MyBill.marketBS;
import restaurant.tanRestaurant.TanCashierAgent.Table;
//import restaurant.tanRestaurant.MarketAgent.MarketBill;
import restaurant.tanRestaurant.TanCookRole.MyMarket.shipmentState;
//import restaurant.tanRestaurant.TanCustomerRole.Order;
import restaurant.tanRestaurant.TanCookRole.MyOrder.orderState;
//import restaurant.tanRestaurant.MarketAgent;
import restaurant.tanRestaurant.TanCustomerRole.AgentEvent;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer.state;
import restaurant.tanRestaurant.gui.CashierGui;
import restaurant.tanRestaurant.interfaces.*;
import restaurant.tanRestaurant.test.mock.EventLog;

import java.util.*;
import java.util.concurrent.Semaphore;

/**
 * Restaurant Cook Agent
 */

//We only have 2 types of agents in this prototype. A customer and an agent that
//does all the rest. Rather than calling the other agent a waiter, we called him
//the HostAgent. A Host is the manager of a restaurant who sees that all
//is proceeded as he wishes.
public class TanCashierAgent extends Agent implements Cashier{
	static final int NTABLES = 3;//a global for the number of tables.

	public List<Customer> waitingCustomers
	= Collections.synchronizedList(new ArrayList<Customer>());
	public List<Bill> Bills = Collections.synchronizedList(new ArrayList<Bill>());
	//public List<MyBill> MyBills = Collections.synchronizedList(new ArrayList<MyBill>());
	public Collection<Table> tables;
	private String name;
	Timer timer = new Timer();
	
	
	private Semaphore handlingAgent = new Semaphore(1,true);
	
	public CashierGui cashierGui = null;
	public TanWaiterRole waiter;
	
	String food;
	int price;
	private double restaurantCash= 500;
	TanRestaurant restaurant;


	public EventLog log = new EventLog();
	
	public TanCashierAgent(String name) {
		super();

		this.name = name;

	}

	public static class MyMarket{
		MyMarket(Market m){
			ma= m;
			//order= o;
			s=shipmentState.orderedShipment;

			
		}
		
		Market ma; //to be implemented later with multiple waiters
		shipmentState s;
		enum shipmentState{orderedShipment, outOfSteak, outOfChicken, outOfSalad, outOfPizza};

	}
	
	/*
	public static class MyBill{
		MyBill(MarketBill b){
			mb=b;
			market=b.market;
			mbs= marketBS.received;
		}
		MarketBill mb;
		Market market;
		public marketBS mbs;
		public enum marketBS{received, paying, paid }
	}*/
	
	public static class Bill{
		Bill(String food, Waiter w, Customer c){
			waiter=w;
			customer= c;
			bs= billState.Pending;
			debt=0;
			
			if(food.equals("Chicken"))
				bill=10.99;
			else if(food.equals("Steak"))
				bill=15.99;
			else if(food.equals("Salad"))
				bill=5.99;
			else if(food.equals("Pizza"))
				bill=8.99;
		}

		Cashier cashier;
		Waiter waiter;
		Customer customer;
		public int tableNum;
		public double bill;
		public double change;
		public double debt;
		public billState bs;
		public enum billState{Pending, sentOut, Paid, Settled, passedToWaiter, givingChange};
		public Customer getCustomer(){
			return customer;
		}
		public billState getState(){
			return bs;
		}
	}

	
	public String getMaitreDName() {
		return name;
	}

	public String getName() {
		return name;
	}

	public List<Customer> getWaitingCustomers() {
		return waitingCustomers;
	}

	public Collection<Table> getTables() {
		return tables;
	}
	// Messages
	
	public void msgCollectingBill(String food, Waiter w, Customer c){
		print("waiter asking for bill");
		try{
			handlingAgent.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();			
		}
		Bills.add(new Bill(food,w,c));
		stateChanged();
	}
	
	/*
	public void msgHereIsMarketBill(MarketBill b){
		//print("received bill to pay "+b.market.getName()+" $"+b.bill);
		MyBills.add(new MyBill(b));	
		stateChanged();// recently added
	}*/
	
	public void msgHereIsMyMoney(Customer c, double cash){
		print("received customer's money"); //prolly need to pass in cust to give him his change OR pass bill which has cust
		
		try{
			handlingAgent.acquire();
		}
		catch (InterruptedException e) {
			e.printStackTrace();			
		}
		//for(Bill b:Bills){
		for(int i=0; i<Bills.size(); i++){
			if (Bills.get(i).customer==c){
				if (cash>= (Bills.get(i).bill + Customer.debt)){ //handles debt
					Bills.get(i).change= cash-Bills.get(i).bill -Customer.debt;
					Bills.get(i).debt= 0.00;
					Bills.get(i).bs= billState.Paid;
					stateChanged();
					}
				if (cash< (Bills.get(i).bill+Customer.debt)){ //if unable to pay
					Bills.get(i).debt= Bills.get(i).bill + Customer.debt - cash;
					Bills.get(i).change= 0.00;
					Bills.get(i).bs= billState.Paid;
					stateChanged();
					}
			}
		}
	}
	
	/*
	public void msgHereIsAnOrder(int tableNum, Order o, WaiterAgent w){
		Orders.add(new MyOrder(tableNum,o,w));
		stateChanged(); //added
	}*/


	
	/*public void msgLeavingTable(CustomerAgent cust) {
		//print("here i am!");
		for (Table table : tables) {
			if (table.getOccupant() == cust) { //doesn't enter here
				print(cust + " leaving " + table);
				table.setUnoccupied();
				stateChanged();
			}
		}
	}*/
	

	


	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	//protected boolean pickAndExecuteAnAction() {
	public boolean pickAndExecuteAnAction(){
	/* Think of this next rule as:
            Does there exist a table and customer,
            so that table is unoccupied and customer is waiting.
            If so seat him at the table.
		 */
		
		/*
		if(!MyBills.isEmpty()){
			synchronized(MyBills){
			for(MyBill mb: MyBills){
				if(mb.mbs==marketBS.received){
					mb.mbs=marketBS.paying;
					payMarket(mb);
					return true;
				}
			}
			}
		}*/
		
		if(!Bills.isEmpty()){
			synchronized(Bills){
			for(Bill b: Bills){
				if(b.bs==billState.Pending){
					b.bs= billState.passedToWaiter;
					passBillToWaiter(b);
					return true;
				}
			}
			
			for(Bill b: Bills){
				if(b.bs==billState.Paid){
					b.bs = billState.givingChange;
					giveChange(b);
					return true;
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

	/*
	private void payMarket(MyBill mb){
		double payment= 0;
		double debt= 0;
		if (mb.mb.bill > getRestaurantCash()){
			print("I'm sorry, I only have "+ getRestaurantCash());
			payment= getRestaurantCash();
			debt= mb.mb.bill-payment;
			setRestaurantCash(0);
		}
		else{
			print("here's the money, bub");
			payment= mb.mb.bill;
			setRestaurantCash(getRestaurantCash()-mb.mb.bill);
		}
		print("debt is: "+ debt);
		mb.market.msgHereIsPayment(payment, debt);
		mb.mbs= marketBS.paid;
		//MyBills.remove(mb);
	}*/
	
	private void passBillToWaiter(Bill b){
		b.waiter.msgHereIsBill(b);
		handlingAgent.release();
	}
	
	private void giveChange(Bill b){
		print("Here is your change!");
		b.bs= billState.Settled;
		b.customer.msgHereIsYourChange(b.change, b.debt);
		
		handlingAgent.release();
	}

	//utilities

	public void setGui(CashierGui gui) {
		cashierGui = gui;
	}
	
	public void setWaiter(TanWaiterRole w){
		waiter=w;
	}

	public CashierGui getGui() {
		return cashierGui;
	}
	
	/*
	public int getAmount(MyOrder o){
		if (o.order.getName().equals("Chicken")){
			return amtChicken;
		}
		else if (o.order.getName().equals("Steak")){
			return amtSteak;
		}
		else if (o.order.getName().equals("Salad")){
			return amtSalad;
		}
		else //if (o.order.getName().equals("Pizza")){
			return amtPizza;
		//}
	}*/
	
	/*
	public void subtractAmount(MyOrder o){
		if (o.order.getName().equals("Chicken")){
			amtChicken--;
		}
		else if (o.order.getName().equals("Steak")){
			amtSteak--;
		}
		else if (o.order.getName().equals("Salad")){
			amtSalad--;
		}
		else //if (o.order.getName().equals("Pizza")){
			amtPizza--;
	}*/
	

	
	public double getRestaurantCash() {
		return restaurantCash;
	}

	public void setRestaurantCash(double restaurantCash) {
		this.restaurantCash = restaurantCash;
	}

	public class Table {
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


	public void setRestaurant(TanRestaurant tanRestaurant) {
		TanRestaurant restaurant = tanRestaurant;
		
	}
}


