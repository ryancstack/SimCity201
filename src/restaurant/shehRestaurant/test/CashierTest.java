package restaurant.shehRestaurant.test;

import restaurant.shehRestaurant.ShehCashierAgent;
import restaurant.shehRestaurant.helpers.Bill;
import restaurant.shehRestaurant.helpers.Bill.OrderBillState;
import restaurant.shehRestaurant.test.mock.MockCustomer;
import restaurant.shehRestaurant.test.mock.MockMarket;
import restaurant.shehRestaurant.test.mock.MockWaiter;
import junit.framework.*;

public class CashierTest extends TestCase
{
	// are instantiated for each test separately via the setUp() method.
	ShehCashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockMarket market;
	double money;
	
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new ShehCashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		market = new MockMarket("mockmarket");
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;			
		
		//empty bills
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.bills.size(), 0);
		
		//empty cashier log
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgProcessThisBill is called. "
				+ "Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//empty waiter log
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead the MockWaiter's "
				+ "event log reads: " + waiter.log.toString(), 0, waiter.log.size());
		
		//empty customer log
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead the MockCustomer's "
				+ "event log reads: " + customer.log.toString(), 0, customer.log.size());
		
		
		//msg from waiter
		cashier.msgProcessThisBill("Steak", customer, waiter); //sent by waiter
		
		//new bill
		Bill bill = new Bill(0, "Steak", customer, waiter, OrderBillState.Pending);
		
		//check bill contents
		assertTrue("CashierBill should contain a bill with state == Pending. It doesn't.", cashier.bills.get(0).s == 
				OrderBillState.Pending);
		
		
		//scheduler
		assertTrue("Cashier's scheduler should return true (action from OrderBillState.Pending), but doesn't", 
				cashier.pickAndExecuteAnAction());
		
		//# of bills after created
		assertEquals("Cashier should have 1 bill in it. It doesn't." , cashier.bills.size(), 1);
		
		//waiter log after scheduler called
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
		
		//customer log after scheduler called
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time."
				+ " Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
		
		//cashier log after scheduler called
		assertEquals("MockCashier should have an empty event log after the Cashier's scheduler is called for the first time."
				+ " Instead, the MockCashier event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		double money = 20;
		//msg from customer
		cashier.msgHereToPay(customer, money); 
		
		//scheduler
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Pending), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		assertTrue("CashierBill should contain a bill with state == Complete. It doesn't.", cashier.bills.get(0).s == 
				OrderBillState.Complete);
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", cashier.bills.get(0).c == customer);
		
		//check customer log
		assertEquals("MockCustomer should have one event log after the Cashier's scheduler is called. Instead the MockCustomer "
				+ "event log reads: " + customer.log.toString(), 1, customer.log.size());

		//check waiter log
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called. Instead the MockWaiter's "
				+ "event log reads: " + waiter.log.toString(), 0, waiter.log.size());
	
		//check cashier log
		assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead the MockCashier's "
				+ "event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//check change
		assertEquals("Change should = 0, but it doesn't.", 0.0, cashier.bills.get(0).m);
		
		//check state change
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Complete), but didn't.", 
				cashier.pickAndExecuteAnAction());
	}//end one normal customer scenario
	
	public void testTwoNotEnoughMoneyScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;			
		
		//empty bills
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.bills.size(), 0);
		
		//empty cashier log
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgProcessThisBill is called. "
				+ "Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//empty waiter log
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead the MockWaiter's "
				+ "event log reads: " + waiter.log.toString(), 0, waiter.log.size());
		
		//empty customer log
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead the MockCustomer's "
				+ "event log reads: " + customer.log.toString(), 0, customer.log.size());
		
		
		//msg from waiter
		cashier.msgProcessThisBill("Steak", customer, waiter); //sent by waiter
		
		//new bill
		Bill bill = new Bill(0, "Steak", customer, waiter, OrderBillState.Pending);
		
		//check bill contents
		assertTrue("CashierBill should contain a bill with state == Pending. It doesn't.", cashier.bills.get(0).s == 
				OrderBillState.Pending);
		
		
		//scheduler
		assertTrue("Cashier's scheduler should return true (action from OrderBillState.Pending), but doesn't", 
				cashier.pickAndExecuteAnAction());
		
		//# of bills after created
		assertEquals("Cashier should have 1 bill in it. It doesn't." , cashier.bills.size(), 1);
		
		//waiter log after scheduler called
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
		
		//customer log after scheduler called
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time."
				+ " Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
		
		//cashier log after scheduler called
		assertEquals("MockCashier should have an empty event log after the Cashier's scheduler is called for the first time."
				+ " Instead, the MockCashier event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		double money = 10;
		
		//msg from customer
		cashier.msgHereToPay(customer, money); 
		
		//scheduler
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Pending), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		assertTrue("CashierBill should contain a bill with state == Complete. It doesn't.", cashier.bills.get(0).s == 
				OrderBillState.Complete);
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", cashier.bills.get(0).c == customer);
		
		//check customer log
		assertEquals("MockCustomer should have one event log after the Cashier's scheduler is called. Instead the MockCustomer "
				+ "event log reads: " + customer.log.toString(), 1, customer.log.size());

		//check waiter log
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called. Instead the MockWaiter's "
				+ "event log reads: " + waiter.log.toString(), 0, waiter.log.size());
	
		//check cashier log
		assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead the MockCashier's "
				+ "event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//check change
		assertEquals("Change should = -10, but it doesn't.", 10.0, cashier.bills.get(0).m);
		
		//check state change
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Complete), but didn't.", 
				cashier.pickAndExecuteAnAction());
	}//end not enough money scenario
	
	
	
	public void testThreeMoreThanEnoughMoneyScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;			
		
		//empty bills
		assertEquals("Cashier should have 0 bills in it. It doesn't.", cashier.bills.size(), 0);
		
		//empty cashier log
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgProcessThisBill is called. "
				+ "Instead, the Cashier's event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//empty waiter log
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead the MockWaiter's "
				+ "event log reads: " + waiter.log.toString(), 0, waiter.log.size());
		
		//empty customer log
		assertEquals("MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead the MockCustomer's "
				+ "event log reads: " + customer.log.toString(), 0, customer.log.size());
		
		
		//msg from waiter
		cashier.msgProcessThisBill("Steak", customer, waiter); //sent by waiter
		
		//new bill
		Bill bill = new Bill(0, "Steak", customer, waiter, OrderBillState.Pending);
		
		//check bill contents
		assertTrue("CashierBill should contain a bill with state == Pending. It doesn't.", cashier.bills.get(0).s == 
				OrderBillState.Pending);
		
		//scheduler
		assertTrue("Cashier's scheduler should return true (action from OrderBillState.Pending), but doesn't", 
				cashier.pickAndExecuteAnAction());
		
		//# of bills after created
		assertEquals("Cashier should have 1 bill in it. It doesn't." , cashier.bills.size(), 1);
		
		//waiter log after scheduler called
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
		
		//customer log after scheduler called
		assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time."
				+ " Instead, the MockCustomer's event log reads: " + customer.log.toString(), 0, customer.log.size());
		
		//cashier log after scheduler called
		assertEquals("MockCashier should have an empty event log after the Cashier's scheduler is called for the first time."
				+ " Instead, the MockCashier event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		double money = 30;
		
		//msg from customer
		cashier.msgHereToPay(customer, money); 
		
		//scheduler
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Pending), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		assertTrue("CashierBill should contain a bill with state == Complete. It doesn't.", cashier.bills.get(0).s == 
				OrderBillState.Complete);
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
				cashier.bills.get(0).c == customer);
		
		//check customer log
		assertEquals("MockCustomer should have one event log after the Cashier's scheduler is called. Instead the MockCustomer "
				+ "event log reads: " + customer.log.toString(), 1, customer.log.size());

		//check waiter log
		assertEquals("MockWaiter should have an empty event log after the Cashier's scheduler is called. Instead the MockWaiter's "
				+ "event log reads: " + waiter.log.toString(), 0, waiter.log.size());
	
		//check cashier log
		assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called. Instead the MockCashier's "
				+ "event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//check change
		assertEquals("Change should = 10, but it doesn't.", 10.0, cashier.bills.get(0).m);
		
		//check state change
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Complete), but didn't.", 
				cashier.pickAndExecuteAnAction());
	}//end more than enough money scenario
	
	
	
	public void testFiveMarketPayingScenario()
	{
		//setUp() runs first before this test!
		
		market.cashier = cashier;
		
		//check cashier log
		assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//check market log
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + market.log.toString(), 0, market.log.size());
		
		//check scheduler
		assertFalse("Cashier's scheduelr should hav ereturned false (no actions to do from market) but didn't.", cashier.pickAndExecuteAnAction());
		
		//check that there are no bills
		assertEquals("Cashier should not have any bills before any of this all happens. Instead they have " + cashier.bills.size(), 
				0, cashier.bills.size());
		
		//create test bill
		Bill bill = new Bill(100, OrderBillState.PayingMarketOrder);
		
		//msg from market
		cashier.msgHereIsMarketBill(bill, market);
		
		assertTrue("CashierBill should contain a bill with state == Pending. It doesn't.", cashier.bills.get(0).s == 
				OrderBillState.PayingMarketOrder);
		
		//check one bill
		assertEquals("Cashier should have one bill now. Instead they have " + cashier.bills.size(), 
				1, cashier.bills.size());
	
		//check cost of bill
		assertEquals("Bill should have money costing $100, instead it costs " + bill.m, 100.0, bill.m);
		
		//check the state change/scheduler (Paying)
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.PayingMarketOrder), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check cashier log
		assertEquals("MockCashier should have an empty event log after the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//check market log
		assertEquals("MockMarket should have one event log after the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + market.log.toString(), 1, market.log.size());
		
		//check the scheduler (Complete)
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Complete), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
	}//end one normal customer scenario
	
	
	
	public void testSixSecondaryMarketPayingScenario()
	{
		//setUp() runs first before this test!
		
		market.cashier = cashier;
		
		//check cashier log
		assertEquals("MockCashier should have an empty event log before the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//check market log
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + market.log.toString(), 0, market.log.size());
		
		//check scheduler
		assertFalse("Cashier's scheduelr should hav ereturned false (no actions to do from market) but didn't.", cashier.pickAndExecuteAnAction());
		
		//check that there are no bills
		assertEquals("Cashier should not have any bills before any of this all happens. Instead they have " + cashier.bills.size(), 
				0, cashier.bills.size());
		
		//create test bill
		Bill bill = new Bill(100, OrderBillState.PayingMarketOrder);
		
		//msg from market
		cashier.msgHereIsMarketBill(bill, market);
		
		assertTrue("CashierBill should contain a bill with state == Pending. It doesn't.", cashier.bills.get(0).s == 
				OrderBillState.PayingMarketOrder);
		
		//check one bill
		assertEquals("Cashier should have one bill now. Instead they have " + cashier.bills.size(), 
				1, cashier.bills.size());
	
		//check cost of bill
		assertEquals("Bill should have money costing $100, instead it costs " + bill.m, 100.0, bill.m);
		
		//check the state change/scheduler (Paying)
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.PayingMarketOrder), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check cashier log
		assertEquals("MockCashier should have an empty event log after the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//check market log
		assertEquals("MockMarket should have one event log after the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + market.log.toString(), 1, market.log.size());
		
		//check the scheduler (Complete)
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Complete), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//create test bill
		Bill bill2 = new Bill(60, OrderBillState.PayingMarketOrder);
		
		//Second bill from second market
		cashier.msgHereIsMarketBill(bill2, market);
		
		//check cost of bill
		assertEquals("Bill should cost $60, instead it costs " + bill2.m, 60.0, bill2.m);
		
		//check the state change/scheduler (Paying)
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Paying), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
		//check cashier log
		assertEquals("MockCashier should have an empty event log after the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + cashier.log.toString(), 0, cashier.log.size());
		
		//check market log
		assertEquals("MockMarket should have two event logs after the Cashier's scheduler is called for the first time. "
				+ "Instead, the MockCashier event log reads: " + market.log.toString(), 2, market.log.size());
		
		//check the scheduler (Complete)
		assertTrue("Cashier's scheduler should have returned true (action from OrderBillState.Complete), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
				
	}//end one normal customer scenario
}
