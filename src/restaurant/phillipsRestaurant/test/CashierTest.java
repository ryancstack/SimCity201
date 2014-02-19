package restaurant.test;

import java.util.List;

import restaurant.*;
import restaurant.test.mock.*;
import junit.framework.*;
import restaurant.CashierAgent.OrderState;
import restaurant.CookAgent.MarketState;

/**
 * 
 * This class is a JUnit test class to unit test the CashierAgent's basic interaction
 * with waiters, customers, and the host.
 * It is provided as an example to students in CS201 for their unit testing lab.
 *
 * @author Monroe Ekilah
 */
public class CashierTest extends TestCase
{
	//these are instantiated for each test separately via the setUp() method.
	CashierAgent cashier;
	//List<MarketAgent> markets;
	MarketAgent market1;
	MarketAgent market2;
	MarketAgent market3;
	MockWaiter waiter;
	MockCustomer customer;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new CashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		market1 = new MarketAgent();
		market2 = new MarketAgent();
		market3 = new MarketAgent();
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		cashier.setWaiter(waiter);		
		
		//test: check from one market
		assertEquals("Cashier should have 0 bills in it.",cashier.checks.size(), 0);	
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		Check check1 = new Check(15.99,OrderState.payMarket, market1);
		cashier.msgPayMarket(market1,"steak",2);
		assertEquals("Cashier should have 1 bill in it.",cashier.checks.size(), 1);
		
		cashier.pickAndExecuteAnAction();
		assertEquals("The amount of money in market 1 should be 31.98",market1.moneyInMarket,31.98);
		assertEquals("Their should be no bills in cashier",cashier.checks.size(),0);
		
		//test: check from two markets, 1 order
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		Check check2 = new Check(10.99,OrderState.payMarket, market1);
		Check check3 = new Check(10.99,OrderState.payMarket, market2);
		

		assertEquals("Cashier should have 0 bills in it.",cashier.checks.size(), 0);
		cashier.msgPayMarket(market1,"chicken",2);
		assertEquals("Cashier should have 1 bill in it.",cashier.checks.size(), 1);
		assertEquals("The bill total should be 21.98",cashier.checks.get(0).moneyOwed, 21.98);
		
		cashier.pickAndExecuteAnAction();
		
		assertEquals("Cashier should have 0 bills in it.",cashier.checks.size(), 0);
		cashier.msgPayMarket(market2, "chicken", 1);
		
		assertEquals("Cashier should have 1 bills in it.",cashier.checks.size(), 1);
		assertEquals("The bill total should be 10.99",cashier.checks.get(0).moneyOwed, 10.99);
		
		cashier.pickAndExecuteAnAction();

		assertEquals("Cashier should have 0 bills in it.",cashier.checks.size(), 0);
		
		

		//test: Check normative case, 1 customer 
		assertEquals("Cashier should have 0 bills in it.",cashier.checks.size(), 0);	
		Check check4 = new Check("steak", 15.99, OrderState.computing , waiter);
		cashier.msgHereIsCheck("steak", customer.getName(), waiter);

		assertEquals("The check for steak should be 15.99.",cashier.checks.get(0).moneyOwed, 15.99);
		assertEquals("Cashier should have 1 bill in it.",cashier.checks.size(), 1);
		
		cashier.pickAndExecuteAnAction();
		
		assertEquals("MockWaiter should have 1 item in the log after the Cashier's scheduler is called. "
						+ waiter.log.toString(), 1, waiter.log.size());
		assertEquals("Cashier's waiter for last order should be removed.", cashier.waiter1, null);
		cashier.cashInRestaurant = 100;
		cashier.msgPayBill(cashier.checks.get(0).name,15.99);
		assertEquals("Money in cashier is " + cashier.cashInRestaurant,cashier.cashInRestaurant, 115.99);
		cashier.pickAndExecuteAnAction();
		assertEquals("There should be 0 checks in cashier", cashier.checks.size(), 0);
		
		
		 
		/*
		
		assertFalse("Cashier's scheduler should have returned false (no actions to do on a bill from a waiter), but didn't.", cashier.pickAndExecuteAnAction());
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		//step 2 of the test
		cashier.ReadyToPay(customer, bill);
		
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("CashierBill should contain a bill with state == customerApproached. It doesn't.",
				cashier.bills.get(0).state == cashierBillState.customerApproached);
		
		assertTrue("Cashier should have logged \"Received ReadyToPay\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received ReadyToPay"));

		assertTrue("CashierBill should contain a bill of price = $7.98. It contains something else instead: $" 
				+ cashier.bills.get(0).bill.netCost, cashier.bills.get(0).bill.netCost == 7.98);
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.bills.get(0).bill.customer == customer);
		
		
		//step 3
		//NOTE: I called the scheduler in the assertTrue statement below (to succintly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 3 / preconditions for step 4
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourTotal\" with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourTotal from cashier. Total = 7.98"));
	
			
		assertTrue("Cashier should have logged \"Received HereIsMyPayment\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received HereIsMyPayment"));
		
		
		assertTrue("CashierBill should contain changeDue == 0.0. It contains something else instead: $" 
				+ cashier.bills.get(0).changeDue, cashier.bills.get(0).changeDue == 0);
		
		
		
		//step 4
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's ReadyToPay), but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 4
		assertTrue("MockCustomer should have logged an event for receiving \"HereIsYourChange\" with the correct change, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received HereIsYourChange from cashier. Change = 0.0"));
	
		
		assertTrue("CashierBill should contain a bill with state == done. It doesn't.",
				cashier.bills.get(0).state == cashierBillState.done);
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
		*/
	
	}//end one normal customer scenario
	
	
}
