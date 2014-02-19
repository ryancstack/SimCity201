package restaurant.huangRestaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import market.MarketCheck;
import market.interfaces.MarketWorker;
import restaurant.CashierAgent;
import restaurant.Restaurant;
import restaurant.huangRestaurant.interfaces.Cashier;
import restaurant.huangRestaurant.interfaces.Customer;
import restaurant.huangRestaurant.interfaces.Waiter;
import agent.Role;


/**
 * Restaurant Cashier Agent. Creates checks.
 */

public class HuangCashierAgent extends CashierAgent implements Cashier {
	public enum OrderState {checkReady, withWaiter, Unpaid, Paid, cannotPay, waitingForWaiter, asked};
	public class Order {
		private Waiter w;
		private Customer c;
		public Check cx;
		public String choice;
		public int table;
		public double price;
		public OrderState state;
		public Order(Waiter w, String choice, int table, Customer c) {
			this.w = w;
			this.c = c;
			this.choice = choice;
			this.table = table;
			this.state = OrderState.checkReady;
			if (this.choice == "Chicken") {
				this.price = 10.99;
			}
			else if (this.choice == "Steak") {
				this.price = 15.99;
			}
			else if (this.choice == "Salad") {
				this.price = 5.99;
			}
			else if (this.choice == "Pizza") {
				this.price = 8.99;
			}
			cx = new Check(this.c, this.choice, this.price, this.table);
		}

	}
	public List<Order> orders = Collections.synchronizedList(new ArrayList<Order>());
	public enum BillState {unpaid, paid, tabbed};
	public class MarketBill {
		public BillState state;
		public MarketCheck c;
		public MarketWorker m;
		MarketBill(MarketCheck c) {
			this.m = c.getMarket();
			this.c = c;
			state = BillState.unpaid;
		}
	}
	public List<MarketBill> bills = Collections.synchronizedList(new ArrayList<MarketBill>());
	private double wallet = 9000.00;
	/*Timer timer = new Timer();
	private class CashierTimerTask extends TimerTask {
		Order o;
		CashierAgent c;
		public CashierTimerTask(Order o, CashierAgent c) {
			this.o = o;
			this.c = c;
		}
		@Override
		public void run() {
	
		}
	}*/
	
	public enum freeLoaderState {Recognized, Penalized};
	public class FreeLoader {
		Customer c;
		public freeLoaderState state;
		FreeLoader(Customer c) {
			this.c = c;
			this.state = freeLoaderState.Recognized;
		}
	}
	public List<FreeLoader> freeLoaders = Collections.synchronizedList(new ArrayList<FreeLoader>());
	public enum EmployeeState {AskedForPay, Paid};
	private class MyEmployee {
		Role r;
		public EmployeeState state;
		MyEmployee(Role r) {
			this.r = r;
			state = EmployeeState.AskedForPay;
		}
	}
	public List<MyEmployee> employees = new ArrayList<MyEmployee>();
	private String name;
	private Restaurant restaurant;
	public HuangCashierAgent(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	// Messages
	public void msgGiveBill(MarketCheck c) {
		System.out.println(name + ": msgHereIsFoodDeliveryBill received: Creating new bill to pay.");
		MarketBill mb = new MarketBill(c);
		bills.add(mb);
		System.out.println("state: " + mb.state);
		stateChanged();
	}
	public void msgHereIsCustomerDish(Waiter w, String type, int table, Customer c) {
		System.out.println(name + ": msgHereIsCustomerDish received: Cashier: Creating new Order to generate new check.");
		Order o = new Order(w, type, table, c);
		orders.add(o);
		System.out.println("state: " + o.state);
		stateChanged();
	}
	public void msgHereIsMoney(Customer c) {
		System.out.println(name + ":msgHereIsMoney received: Cashier: Money in the bank, pimpin ain't easy.");
		for (Order o : orders) {
			if (o.cx.c == c) {
				o.state = OrderState.Paid;
			}
		}
		stateChanged();
	}
	public void msgNotEnoughMoney(Customer c) {
		for (Order o : orders) {
			if (o.cx.c == c) {
				o.state = OrderState.cannotPay;
			}
		}
		System.out.println(name + ": msgNotEnoughMoney received by " + c.toString() +" Cashier: GET OUT FREELOADER!");
		FreeLoader fl = new FreeLoader(c);
		freeLoaders.add(fl);
		stateChanged();
	}
	public void msgAskForCheck(Customer c) {
		for (Order o : orders) {
			if (o.cx.c == c) {
				o.state = OrderState.asked;
			}
		}
		stateChanged();
	}
	public void msgAskForPayCheck(Role r) {
		System.out.println("Adding new employee to pay");
		employees.add(new MyEmployee(r));
		stateChanged();
	}
		
	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		//rule 1
			for(MyEmployee me : employees) {
				if (me.state == EmployeeState.AskedForPay) {
					payEmployee(me);
					System.out.println("Paid employee");
					return true;
				}
			}
		//synchronized(bills) {
			for(MarketBill mb : bills) {
				if (mb.state.equals(BillState.unpaid)) {
					payBill(mb);
					return true;
				}
			}
		//}
		//synchronized(orders) {
			for (Order o: orders) {
				if(o.state.equals(OrderState.checkReady)) {
					tellCheckToWaiter(o);
					return true;
				}
			}
		//}
		//synchronized(orders) {
			for (Order o: orders) {
				if(o.state.equals(OrderState.asked)) {
					giveCheckToWaiter(o);
					return true;
				}
			}
		//}
		//synchronized(freeLoaders) {
			for (FreeLoader fl : freeLoaders) {
				if (fl.state.equals(freeLoaderState.Recognized)) {
					kickFreeLoader(fl);
					return true;
				}
			}
		//}
		//rule 2

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}




	// Actions

	private void payEmployee(MyEmployee me) {
		me.state = EmployeeState.Paid;
		if(me.r.getClass().toString().contains("ter")) {
			me.r.msgHereIsPaycheck(25.00);
		}
		else {
			me.r.msgHereIsPaycheck(50.00);
		}
	}
	private void payBill(MarketBill mb) {
		System.out.println("Paying Bill.");
		if(wallet - mb.c.getCost() >=0) {
			mb.state = BillState.paid;
			wallet -= mb.c.getCost();
			mb.m.msgPayForOrder(this, mb.c.getCost());
		}
		else {
			mb.state = BillState.tabbed;
			mb.m.msgCannotPay(this, mb.c.getCost());
		}
	}
	private void kickFreeLoader(FreeLoader fl) {
		fl.c.msgGetOut();
		fl.state = freeLoaderState.Penalized;
	}
	private void tellCheckToWaiter(Order o) {
		o.w.msgGetCheck(o.cx);
		o.state = OrderState.waitingForWaiter;
	}
	private void giveCheckToWaiter(Order o) {
		o.w.msgHereIsCheck(o.cx);
		o.state = OrderState.withWaiter;
	}
	//utilities
	public void setRestaurant(Restaurant huang) {
		this.restaurant = huang;
	}


}

