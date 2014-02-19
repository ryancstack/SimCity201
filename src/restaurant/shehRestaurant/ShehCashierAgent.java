package restaurant.shehRestaurant;

import agent.Agent;
import restaurant.CashierAgent;
import restaurant.Restaurant;
import restaurant.shehRestaurant.helpers.Bill;
import restaurant.shehRestaurant.helpers.Bill.PayCheckBillState;
import restaurant.shehRestaurant.helpers.Menu;
import restaurant.shehRestaurant.helpers.Table;
import restaurant.shehRestaurant.interfaces.Cashier;
import restaurant.shehRestaurant.interfaces.Customer;
import restaurant.shehRestaurant.interfaces.Market;
import restaurant.shehRestaurant.interfaces.Waiter;
import restaurant.shehRestaurant.helpers.Bill.OrderBillState;
import restaurant.shehRestaurant.test.mock.EventLog;

import java.util.*;

import city.helpers.Directory;
import market.MarketCheck;
import market.interfaces.MarketWorker;

/**
 * Restaurant Cashier Agent
 */
public class ShehCashierAgent extends CashierAgent implements Cashier {
	public List<myCustomer> myCustomers = Collections.synchronizedList(new ArrayList<myCustomer>());
	public List<Bill> bills = Collections.synchronizedList(new ArrayList<Bill>());
	
	Timer timer = new Timer();
	Menu menu = new Menu();
	Table table;
	Bill bill;
	
	Restaurant restaurant;
	public EventLog log = new EventLog();

	private double money = 0;
	private String name;
	private MarketWorker market;
	private ShehWaiterRole waiter;
	
	private class myCustomer {
		Customer c;
		double debt;
		
		myCustomer(Customer c2, double change) {
			c = c2;
			debt = change;
		}
		
		private double getMoney() {
			return debt;
		}
	}
	
	public class FoodData {
		public int price;
		public int cookTime;
		public int quantity;
		
		FoodData(int p, int t, int q) {
			price = p;
			cookTime = t;
			quantity = q;
		}
	}
	
	FoodData steak = new FoodData(20, 5000, 1);
	FoodData chicken = new FoodData(15, 5000, 1);
	FoodData pizza = new FoodData(20, 5000, 1);
	FoodData salad = new FoodData(20, 5000, 1);
	
	private Map<String, FoodData> inventory = new HashMap<String, FoodData>(); {
		inventory.put("Steak", steak);
		inventory.put("Chicken", chicken);
		inventory.put("Pizza", pizza);
		inventory.put("Salad", salad);
	}
	/*
	public ShehCashierAgent(String n) {
		super();
		this.name = "Cashier";
		name = n;
	}
	*/

	public ShehCashierAgent() {
		super();
	}

	// Messages
	public void msgProcessThisBill(String o, Customer c, Waiter w) {
		bills.add(new Bill(0, o, c, w, OrderBillState.Pending));
		stateChanged();
	}
	
	public void msgGiveBill(MarketCheck marketcheck) {
		print("Received bill from market of $" + marketcheck.getAmount() + ".");
		double price = marketcheck.getAmount();
		bills.add(new Bill(price, OrderBillState.PayingMarketOrder));
		market = marketcheck.getMarket();
		
		stateChanged(); 
	}
	
	public void msgHereToPay(Customer c, double money2) {
		money = money2;
		restaurant.setTill(restaurant.getTill() + money2);
		for(Bill b : bills) {
			if(b.c == c) {
				b.s = OrderBillState.Paying;
				stateChanged();
			}
		}
	}
	
	public void msgCollectPayCheck(ShehWaiterRole waiterRole) {
		print("Printing paycheck to waiter.");
		Bill bill = new Bill(waiterRole, 100, Bill.PayCheckBillState.CalculatingPayCheck);
		stateChanged();
	}
	
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		synchronized(bills) {
			for (Bill b : bills) {
				if (b.s == OrderBillState.Pending) {
					ProcessBill(b);
					return true;
				}
			}
		}
			
		synchronized(bills) {
			for (Bill b : bills) {
				if (b.s == OrderBillState.Calculating) {
					BillIsProcessed(b);
					return true;
				}
			}
		}
		
		synchronized(bills) {
			for (Bill b : bills) {
				if (b.s == OrderBillState.BillSent) {
					return true;
				}
			}
		}
			
		synchronized(bills) {
			for (Bill b : bills) {
				if(b.s == OrderBillState.Paying) {
					ReturnChange(b);
					return true;
				}
			}
		}
		
		synchronized(bills) {
			for (Bill b : bills) { 
				if(b.s == OrderBillState.PayingMarketOrder) {
					PayMarketOrder(b);
					return true;
				}
			}
		}
		
		synchronized(bills) {
			for (Bill b : bills) {
				if(b.s == OrderBillState.Complete) {
					return true;
				}
			}
		}
		
		synchronized(bills) {
			for (Bill b : bills) {
				if(b.ps == PayCheckBillState.CalculatingPayCheck) {
					PrintPayCheck(b);
					return true;
				}
			}
		}
	
		return false;
	}

	// Actions
	private void ProcessBill(Bill b) {
		//DoPlacement(order); //animation
		print("Bill is processing.");
		//b.m = inventory.get(b.o).price; 
		b.setMoney(inventory.get(b.o).price);
		
		for(myCustomer mc : myCustomers) {
			if(mc.c == b.c) {
				print("They owe us money from last time.");
				b.m = b.m + mc.debt;
			}
		}
		
		b.s = OrderBillState.Calculating;
		stateChanged();
	}
	
	private void BillIsProcessed(Bill b) {
		print("Bill is processed.");
		b.w.msgCollectBill(b);
		b.s = OrderBillState.BillSent;
		stateChanged();
	}
	
	private void ReturnChange(Bill b) {
		double change = 0; 
		
		if(money > b.m) {
			change = money - b.m;
			restaurant.setTill(restaurant.getTill() - change);
			print("$" + change + " is your change. Come again!");
			
		}
		else if(money == b.m) {
			print("Perfect amount, no change. Come again!");
		}
		else if(money < b.m) {
			change = b.m - money;
			print("You owe us $" + change + " next time you come in.");
			myCustomers.add(new myCustomer(b.c, change));
		}
		
		b.m = change;
		b.c.msgHereIsYourChange(b); //can change this to bill possibly
		b.s = OrderBillState.Complete;
	}
	
	private void PayMarketOrder(Bill b) {
		print("Paying market order now.");
		
		market.msgPayForOrder(this, b.getBillMoney());
		restaurant.setTill(restaurant.getTill() - b.getBillMoney());
		b.s = OrderBillState.Complete;
		
	}
	
	private void PrintPayCheck(Bill b) {
		print("Printing: Paycheck");
		
		waiter.msgHereIsPayCheck(b);
		restaurant.setTill(restaurant.getTill() - b.m);
		b.ps = PayCheckBillState.SentPayCheck;
	}

	public void setRestaurant(Restaurant rest) {
		restaurant = rest;
	}
	
	public Restaurant getRestaurant() {
		return restaurant;
	}

}