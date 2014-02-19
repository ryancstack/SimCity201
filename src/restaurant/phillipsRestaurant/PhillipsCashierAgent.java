package restaurant.phillipsRestaurant;

import restaurant.phillipsRestaurant.*;
import restaurant.phillipsRestaurant.Check;
import restaurant.phillipsRestaurant.test.*;
import restaurant.phillipsRestaurant.test.mock.*;
import restaurant.phillipsRestaurant.gui.*;
import restaurant.phillipsRestaurant.interfaces.*;
import agent.Agent;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Timer;
import java.util.TimerTask;
import java.util.ArrayList;

import market.MarketCheck;
/**
 * Restaurant cook agent.
 */
public class PhillipsCashierAgent extends Agent implements Cashier{
	
	public EventLog log = new EventLog();
	public enum OrderState {payMarket,payMarketLater,waitingPayment,computing,paid,done};
	public double cashInRestaurant = 100;
	public double moneyOwedToMarket=0;
	private Menu menu = new Menu();
	public String name;
	//private Map<String,Double> checks = new HashMap<String,Double>();
	public List<Check> checks = Collections.synchronizedList(new ArrayList<Check>());
	
	public Waiter waiter1 = null,waiter2=null,waiter3=null;


	/**
	 * Constructor for CashierAgent class
	 *
	 * @param name name of the customer
	 * @param gui  reference to the customergui so the customer can send it messages
	 */
	public PhillipsCashierAgent(String name){
		super();
		this.name = name;
	}
	public void setWaiter(Waiter w){
		//waiters.add(w);
		if(waiter1 == null){
			waiter1 = w;
		}
		else if(waiter1 != null && waiter2 == null){
			waiter2 = w;
		}	
		else if(waiter1 != null && waiter2 != null && waiter3 == null){
			waiter3 = w;
		}
	}
	
	// Messages
	public void msgHereIsCheck(String choice, int table, Waiter w){
		Do("Received check from waiter");
		setWaiter(w);
		synchronized(this.checks){
			switch(choice){
				case "steak":
					checks.add(new Check(table, menu.costs.get(0),OrderState.computing,w));
					break;
				case "chicken":
					checks.add(new Check(table, menu.costs.get(1),OrderState.computing,w));
					break;
				case "salad":
					checks.add(new Check(table, menu.costs.get(2),OrderState.computing,w));
					break;
				case "pizza":
					checks.add(new Check(table, menu.costs.get(3),OrderState.computing,w));
					break;
				}
		}
		stateChanged();
	}

	public void msgPayBill(int table, double money){
		for(int i=0;i<checks.size();i++){
			if(checks.get(i).tableNum == table){
				cashInRestaurant += money;
				checks.get(i).state = OrderState.paid;
			}
		}
		stateChanged();
	}
	
	public void msgPayMarket(MarketAgent m,String food,int amount){
		//Do("Needs to pay market cash for " + amount + " " + food);
		synchronized(this.checks){
			switch(food){
			case "steak":
				checks.add(new Check(menu.costs.get(0)*((double)amount),OrderState.payMarket,m));
				break;
			case "chicken":
				checks.add(new Check(menu.costs.get(1)*((double)amount),OrderState.payMarket,m));
				break;
			case "salad":
				checks.add(new Check(menu.costs.get(2)*((double)amount),OrderState.payMarket,m));
				break;
			case "pizza":
				checks.add(new Check(menu.costs.get(3)*((double)amount),OrderState.payMarket,m));
				break;
			}
		}
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		synchronized(this.checks){
			for(int i=0;i<checks.size();i++){
				if(checks.get(i).state == OrderState.computing){
					checks.get(i).state = OrderState.waitingPayment;
					tellWaiterCheckNeedsPayment(checks.get(i));
					return true;
				}
			}
		}
		synchronized(this.checks){
			for(int i=0;i<checks.size();i++){
				if(checks.get(i).state == OrderState.paid){
					checks.get(i).state = OrderState.done;
					removeCheck(checks.get(i));
					return true;
				}
			}
		}
		
		synchronized(this.checks){
			for(int i=0;i<checks.size();i++){
				if(checks.get(i).state == OrderState.payMarket){
					payMarket(checks.get(i));
					return true;
				}
			}
		}
		
		return false;    
	}

	// Actions
	
	public void payMarket(Check c){
		Do("Paying market for " + c.moneyOwed);
		//Check if able to pay market's bill
		if(cashInRestaurant >= c.moneyOwed){
			c.market.msgHereIsCheck(c.moneyOwed);
			cashInRestaurant -= c.moneyOwed;
			checks.remove(c);
		}
		else{
			moneyOwedToMarket += c.moneyOwed;
			c.state = OrderState.payMarketLater; //state set to pay later so cashier can deal with those checks later (for later implementation)
		}
	}
	public void tellWaiterCheckNeedsPayment(Check c){
		Do("Telling waiter to give check to Customer");
		if(waiter1 == c.waiter){
			waiter1.msgPayFood(c.tableNum,c.moneyOwed);
			waiter1 = null;
		}
		else if(waiter2 == c.waiter){
			waiter2.msgPayFood(c.tableNum,c.moneyOwed);
			waiter2 = null;
		}
		else if(waiter3 == c.waiter){
			waiter3.msgPayFood(c.tableNum,c.moneyOwed);
			waiter3 = null;
		}
	}
	public void removeCheck(Check c){
		System.out.println("Cashier received money from customer and throwing away check");
		checks.remove(c);
	}
	@Override
	public void msgGiveBill(MarketCheck marketcheck) {
		// TODO Auto-generated method stub
		
	}
}

