package restaurant.shehRestaurant.helpers;

import restaurant.shehRestaurant.ShehWaiterRole;
import restaurant.shehRestaurant.interfaces.Customer;
import restaurant.shehRestaurant.interfaces.Waiter;

public class Bill {
		public double m = 0; //money
		public String o;
		public Customer c;
		public Waiter w;
		public OrderBillState s;
		public PayCheckBillState ps;
		public ShehWaiterRole r;
		
		public Bill(double money, String order, Customer customer, Waiter waiter, OrderBillState state) {
			m = money;
			o = order;
			c = customer;
			w = waiter;
			s = state;
		}
		
		public Bill(double money, OrderBillState state) {
			m = money;
			s = state;
		}
		
		public Bill(ShehWaiterRole waiterRole, double money, PayCheckBillState state) {
			r = waiterRole;
			m = money;
			ps = state;
		}
		
	public double getBillMoney() { //change this to BillTotal
		return this.m;
	}
	
	public String getBillString() {
		return this.o;
	}	
	
	public Customer getCustomer() {
		return this.c;
	}
	
	public OrderBillState getState() {
		return this.s;
	}
	
	public void setMoney(int money) {
		m = money;
	}
	
	public enum OrderBillState {
		Pending, Calculating, BillSent, Paying, Complete, PayingMarketOrder
	}	
	
	public enum PayCheckBillState {
		CalculatingPayCheck, SentPayCheck
	}
}
