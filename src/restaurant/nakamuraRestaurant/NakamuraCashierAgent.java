package restaurant.nakamuraRestaurant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;

import city.helpers.Directory;
import agent.Role;
import market.MarketCheck;
import market.interfaces.MarketWorker;
import restaurant.CashierAgent;
import restaurant.Restaurant;
import restaurant.nakamuraRestaurant.NakamuraCookRole;
import restaurant.nakamuraRestaurant.helpers.Check;
import restaurant.nakamuraRestaurant.helpers.Check.state;
import restaurant.nakamuraRestaurant.helpers.Menu;
import restaurant.nakamuraRestaurant.interfaces.Cashier;
import restaurant.nakamuraRestaurant.interfaces.Customer;
import restaurant.nakamuraRestaurant.interfaces.Waiter;
import restaurant.nakamuraRestaurant.test.mock.EventLog;
import restaurant.nakamuraRestaurant.test.mock.LoggedEvent;

/**
 * Restaurant Cashier Agent
 */
public class NakamuraCashierAgent extends CashierAgent implements Cashier{
	public List<Check> Checks
	= Collections.synchronizedList(new ArrayList<Check>());
	public List<MarketBill> marketBills = Collections.synchronizedList(new ArrayList<MarketBill>());
	public List<Role> myEmployees = Collections.synchronizedList(new ArrayList<Role>());

	private Menu menu;
	private String name;
	Timer timer = new Timer();
	public EventLog log;
	
	NakamuraCookRole cook;
	Restaurant restaurant;
	enum billState {Received, Verified, Paid, BeingVerified, cantPay};

//	public HostGui hostGui = null;
//  private List<WaiterAgent> waiters = new ArrayList<WaiterAgent>();

	public NakamuraCashierAgent(String name) {
		super();

		this.name = name;
		menu = new Menu();
		log = new EventLog();
	}

	public String getCookName() {
		return name;
	}

	public String getName() {
		return name;
	}
	
	public void setMenu(Menu m) {
		this.menu = m;
	}

	public void setRestaurant(Restaurant rest){
		this.restaurant = rest;
	}
	
	public void msgComputeCheck(Waiter w, Customer c, String choice) {
		print("Received msgComputeCheck");
		Double total = 0.0;
		
		synchronized(Checks) {
			for(Check check : Checks) {
				if(check.getCustomer() == c) {
					total += check.getTotal();
				}
			}
		}
		print("Old debt: $" + total);
		Checks.add(new Check(w, c, choice, total));
		log.add(new LoggedEvent("Received msgComputeCheck from Waiter"));
		stateChanged();
	}

	public void msgPayment(Customer c, Check check, double payment) {
		print("Received msgPayment");
		if(check.getTotal() == payment) {
			check.setState(state.paid);
			log.add(new LoggedEvent("Received msgPayment from customer. Payment = $" + payment));
		}
		else {
			check.setState(state.shortChange);
			log.add(new LoggedEvent("No payment from customer"));
		}
		setTill(getTill() + payment);
		stateChanged();
	}
	
	public void msgGiveBill(MarketCheck check) {
		print("Received msgMarketBill");
		marketBills.add(new MarketBill(check));
		log.add(new LoggedEvent("Received msgMarketBill. Total = $" + check.getCost()));
		stateChanged();
	}
	
	public void msgBillIsCorrect(List<String> choices, int amount) {
		print("Received msgBillIsCorrect");
		
		synchronized(marketBills) {
			for(MarketBill b : marketBills) {
				if(b.state == billState.BeingVerified &&
					b.getChoices() == choices &&
					b.getAmount() == amount) {
					
					b.state = billState.Verified;
				}
			}
		}
		
		stateChanged();
	}
	
	public void msgNeedPay(Role employee) {
		print("Receied msgNeedPay from " + employee.getPersonAgent().getName());
		myEmployees.add(employee);
		stateChanged();
	}

	/**
	 * Scheduler.  Determine what action is called for, and do it.
	 */
	public boolean pickAndExecuteAnAction() {
		
		synchronized(Checks) {
			for(Check c : Checks) {
				if(c.getState() == state.paid) {
					GiveChange(c);
					return true;
				}
			}
			
			for(Check c : Checks) {
				if(c.getState() == state.shortChange) {
					PayLater(c);
					return true;
				}
			}
			
			for (Check c : Checks) {
				if (c.getState() == state.pending) {
					Calculate(c);
					return true;
				}
			}
		}
		
		synchronized(marketBills) {
			for(MarketBill m : marketBills) {
				if(m.state == billState.Received) {
					VerifyMarketBill(m);
					return true;
				}
			}
			
			for(MarketBill m : marketBills) {
				if(m.state == billState.Verified) {
					PayMarketBill(m);
					return true;
				}
			}
			
			for(MarketBill m : marketBills) {
				if(m.state == billState.Paid) {
					marketBills.remove(m);
					return true;
				}
			}
		}
		
		if(!myEmployees.isEmpty()) {
			PayEmployee(myEmployees.get(0));
			return true;
		}

		return false;
		//we have tried all our rules and found
		//nothing to do. So return false to main loop of abstract agent
		//and wait.
	}

	// Actions

	private void Calculate(Check check) {
		print("Calculating Check");
		check.setTotal(menu.prices.get(check.getChoice()));
		check.getWaiter().msgCheckReady(check);
		check.setState(state.delivered);
		log.add(new LoggedEvent("Calculated check. Total = $" + check.getTotal()));
	}

	private void GiveChange(Check check) {
		print("Giving Change");
		check.getCustomer().msgHeresChange(check.getChange());
		Checks.remove(check);
		log.add(new LoggedEvent("Deleted check."));
	}
	
	private void PayLater(Check check) {
		print("Pay Later");
		check.getCustomer().msgPayNextTime(check.getTotal());
		check.setState(state.debt);
		log.add(new LoggedEvent("Let customer pay later. Debt = $" + check.getTotal()));
	}
	
	private void VerifyMarketBill(MarketBill bill) {
		cook.msgVerifyMarketBill(bill.getChoices(), bill.getAmount());
		bill.state = billState.BeingVerified;
	}
	
	private void PayMarketBill(MarketBill bill) {
		if(getTill() >= bill.getCost()) {
			setTill(getTill() - bill.getCost());
			bill.getMarket().msgPayForOrder(this, bill.getCost());
			
			bill.state = billState.Paid;
		}
		else {
			bill.getMarket().msgCannotPay(this, bill.getCost());
			bill.state = billState.cantPay;
		}
	}
	
	private void PayEmployee(Role employee) {
		if(getTill() >= 100) {
			setTill(getTill() - 100);
			employee.msgHereIsPaycheck(100);
		}
		else
			employee.msgHereIsPaycheck(0);
		
		myEmployees.remove(employee);
	}

	public class MarketBill {
		MarketCheck marketcheck;
		billState state;
		
		MarketBill (MarketCheck marketcheck) {
			this.marketcheck = marketcheck;
			this.state = billState.Received;
		}
		
		public MarketWorker getMarket() {
			return marketcheck.getMarket();
		}
		
		public List<String> getChoices() {
			return marketcheck.getChoices();
		}
		
		public int getAmount() {
			return marketcheck.getAmount();
		}
		
		public double getCost() {
			return marketcheck.getCost();
		}
	}
	
	public void setCook(NakamuraCookRole cook) {
		this.cook = cook;
	}
	
	public double getTill() {
		return restaurant.getTill();
	}
	
	public void setTill(double amount) {
		restaurant.setTill(amount);
	}
}