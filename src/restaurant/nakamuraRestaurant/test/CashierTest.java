package restaurant.nakamuraRestaurant.test;

import restaurant.nakamuraRestaurant.NakamuraCashierAgent;
import restaurant.nakamuraRestaurant.helpers.Check.state;
import restaurant.nakamuraRestaurant.test.mock.MockCustomer;
import restaurant.nakamuraRestaurant.test.mock.MockMarket;
import restaurant.nakamuraRestaurant.test.mock.MockWaiter;
import junit.framework.*;

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
	NakamuraCashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockCustomer customer2;
	MockMarket market;
	MockMarket market2;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new NakamuraCashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");
		customer2 = new MockCustomer("mockcustomer2");		
		waiter = new MockWaiter("mockwaiter");
		market = new MockMarket("mockmarket");
		market2 = new MockMarket("mockmarket2");
	}	
	
	public void testOneMarketBill(){
		market.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 marketBills in it. It doesn't", cashier.Bills.size(), 0);
		assertTrue("Cashier should have $50.00 in cash. Instead it has: $" + cashier.getCash(), cashier.getCash() == 50.0);
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgMarketBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
		+ market.log.toString(), 0, market.log.size());
		
		//step 1
		cashier.msgMarketBill(market, 20.00);
		
		//check postconditions step 1/ preconditions step 2
		assertEquals("Cashier should have 1 marketBills in it. It doesn't", cashier.Bills.size(), 1);
		assertTrue("Cashier should have a bill with total $20.00. Instead the total is: $" + cashier.Bills.get(0).getPayment(), cashier.Bills.get(0).getPayment() == 20.00);
		assertTrue("Bills should contain a marketBill with the right market in it. It doesn't.", 
					cashier.Bills.get(0).getMarket() == market);
		
		//step 2
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions step 2
		assertEquals("Cashier should have 0 marketBills in it. It doesn't", cashier.Bills.size(), 0);
		assertTrue("Cashier should have logged \"Paid Bill.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Paid Bill. Total = $20.0"));
		assertTrue("Cashier should have $30.00 in cash. Instead it has: $" + cashier.getCash(), cashier.getCash() == 30.0);
		assertTrue("Market should have logged \"Received msgHeresMarket from cashier.\" but didn't. His log reads instead: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgHeresMarket from cashier. Total = $20.0"));
	}	
	
	public void testTwoMarketBills(){
		market.cashier = cashier;
		market2.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 marketBills in it. It doesn't", cashier.Bills.size(), 0);
		assertTrue("Cashier should have $50.00 in cash. Instead it has: $" + cashier.getCash(), cashier.getCash() == 50.0);
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgMarketBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("First MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
		+ market.log.toString(), 0, market.log.size());
		assertEquals("Second MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
		+ market2.log.toString(), 0, market2.log.size());
		
		//step 1
		cashier.msgMarketBill(market, 20.00);
		
		//check postconditions step 1/ preconditions step 2
		assertEquals("Cashier should have 1 marketBills in it. It doesn't", cashier.Bills.size(), 1);
		assertTrue("Cashier should have a bill with total $20.00. Instead the total is: $" + cashier.Bills.get(0).getPayment(), cashier.Bills.get(0).getPayment() == 20.00);
		assertTrue("Bills should contain a marketBill with the right market in it. It doesn't.", 
					cashier.Bills.get(0).getMarket() == market);
		
		//step 2
		cashier.msgMarketBill(market2, 20.00);
		
		//check postconditions step 2/ preconditions step 3
		assertEquals("Cashier should have 2 marketBills in it. It doesn't", cashier.Bills.size(), 2);
		assertTrue("Cashier should have a new bill with total $20.00. Instead the total is: $" + cashier.Bills.get(1).getPayment(), cashier.Bills.get(0).getPayment() == 20.00);
		assertTrue("Bills should contain a marketBill with the right market in it. It doesn't.", 
					cashier.Bills.get(1).getMarket() == market2);
		
		//step 3
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions step 3/ preconditions step 4
		assertEquals("Cashier should have 1 marketBills in it. It doesn't", cashier.Bills.size(), 1);
		assertTrue("Cashier should have logged \"Paid Bill.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Paid Bill. Total = $20.0"));
		assertTrue("Cashier should have $30.00 in cash. Instead it has: $" + cashier.getCash(), cashier.getCash() == 30.0);
		assertTrue("First MockMarket should have logged \"Received msgHeresMarket from cashier.\" but didn't. His log reads instead: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgHeresMarket from cashier. Total = $20.0"));
		
		//step 4
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions step 4/ preconditions step 5
		assertEquals("Cashier should have 0 marketBills in it. It doesn't", cashier.Bills.size(), 0);
		assertTrue("Cashier should have logged \"Paid Bill.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Paid Bill. Total = $20.0"));
		assertTrue("Cashier should have $10.00 in cash. Instead it has: $" + cashier.getCash(), cashier.getCash() == 10.0);
		assertTrue("Second MockMarket should have logged \"Received msgHeresMarket from cashier.\" but didn't. His log reads instead: " 
				+ market2.log.getLastLoggedEvent().toString(), market2.log.containsString("Received msgHeresMarket from cashier. Total = $20.0"));
	}
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.	
		waiter.cashier = cashier;
		waiter.customer = customer;
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgComputeCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());

		
		//step 1 of the test
		cashier.msgComputeCheck(waiter, customer, "Steak");

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.Checks.size(), 1);

		assertTrue("Checks should contain a bill with state == pending. It doesn't.",
				cashier.Checks.get(0).getState() == state.pending);
		
		assertTrue("Checks should contain a check with the right customer in it. It doesn't.", 
					cashier.Checks.get(0).getCustomer() == customer);

		//step 2 of the test
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 2 and preconditions for step 3
		assertTrue("Checks should contain a bill with state == delivered. It doesn't.",
				cashier.Checks.get(0).getState() == state.delivered);
		assertTrue("Checks should contain a bill with total == 20.0 It doesn't.",
				cashier.Checks.get(0).getTotal() == 20.0);
		
		assertEquals(
				"MockWaiter should have one event logged after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have one event logged after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 1, customer.log.size());
		assertTrue("Cashier should have logged \"Calculated check.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Calculated check. Total = $20.0"));
		
		//step 3 of the test
		cashier.msgPayment(customer, customer.check, 20.00);
		
		//check postconditions for step 3 / preconditions for step 4
		assertTrue("Checks should contain a bill with state == paid. It doesn't.",
				cashier.Checks.get(0).getState() == state.paid);
		
		assertTrue("Cashier should have logged \"Received msgPayment from customer.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Payment = $20.0"));
		
		
		//step 4
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 4
		assertTrue("MockCustomer should have logged \"Received msgHeresChange from cashier.\", but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHeresChange from cashier. Change = $0.0"));
	
			
		assertTrue("Cashier should have logged \"Deleted check.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Deleted check."));
		
		
		assertEquals("Checks should no checks. It doesn't", cashier.Checks.size(), 0);
		
	}//end one normal customer scenario
	
	public void testOneBrokeCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.	
		waiter.cashier = cashier;
		waiter.customer = customer;
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgComputeCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());

		
		//step 1 of the test
		cashier.msgComputeCheck(waiter, customer, "Steak");

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.Checks.size(), 1);

		assertTrue("Checks should contain a bill with state == pending. It doesn't.",
				cashier.Checks.get(0).getState() == state.pending);
		
		assertTrue("Checks should contain a check with the right customer in it. It doesn't.", 
					cashier.Checks.get(0).getCustomer() == customer);

		//step 2 of the test
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 2 and preconditions for step 3
		assertTrue("Checks should contain a bill with state == delivered. It doesn't.",
				cashier.Checks.get(0).getState() == state.delivered);
		assertTrue("Checks should contain a bill with total == 20.0 It doesn't.",
				cashier.Checks.get(0).getTotal() == 20.0);
		
		assertEquals(
				"MockWaiter should have one event logged after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have one event logged after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 1, customer.log.size());
		assertTrue("Cashier should have logged \"Calculated check.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Calculated check. Total = $20.0"));
		
		//step 3 of the test
		cashier.msgPayment(customer, customer.check, 0.00);
		
		//check postconditions for step 3 / preconditions for step 4
		assertTrue("Checks should contain a bill with state == shortChange. It doesn't.",
				cashier.Checks.get(0).getState() == state.shortChange);
		
		assertTrue("Cashier should have logged \"No payment from customer.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("No payment from customer"));
		
		
		//step 4
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 4
		assertTrue("MockCustomer should have logged \"Received msgPayNextTime from cashier.\", but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgPayNextTime from cashier. Debt = $20.0"));
	
			
		assertTrue("Cashier should have logged \"Let customer pay later.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Let customer pay later. Debt = $20.0"));
		
		
		assertEquals("Checks should one check. It doesn't", cashier.Checks.size(), 1);
		assertTrue("Checks should contain a bill with state == debt. It doesn't.",
				cashier.Checks.get(0).getState() == state.debt);
		
	}//end one customer scenario	
	
	public void testTwoNormalCustomerScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;
		customer2.cashier = cashier;//You can do almost anything in a unit test.	
		waiter.cashier = cashier;
		waiter.customer = customer;
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgComputeCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("First MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the First MockWaiter's event log reads: "
				+ customer.log.toString(), 0, customer.log.size());
		assertEquals("Second MockCustomer should have an empty event log before the Cashier's scheduler is called. Instead, the Second MockWaiter's event log reads: "
				+ customer2.log.toString(), 0, customer2.log.size());

		
		//step 1 of the test
		cashier.msgComputeCheck(waiter, customer, "Steak");

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.Checks.size(), 1);
		assertTrue("Cashier should have logged \"Received msgComputeCheck from Waiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeCheck from Waiter"));

		assertTrue("Checks should contain a bill with state == pending. It doesn't.",
				cashier.Checks.get(0).getState() == state.pending);
		
		assertTrue("Checks should contain a check with the right customer in it. It doesn't.", 
					cashier.Checks.get(0).getCustomer() == customer);
		
		//step 2 of the test
		cashier.msgComputeCheck(waiter, customer2, "Steak");

		//check postconditions for step 2 and preconditions for step 3
		assertEquals("Cashier should have 2 checks in it. It doesn't.", cashier.Checks.size(), 2);
		assertTrue("Cashier should have logged \"Received msgComputeCheck from Waiter\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgComputeCheck from Waiter"));

		assertTrue("Checks should contain a new check with state == pending. It doesn't.",
				cashier.Checks.get(1).getState() == state.pending);
		
		assertTrue("Checks should contain a new check with the right customer in it. It doesn't.", 
					cashier.Checks.get(1).getCustomer() == customer2);

		//step 3 of the test
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 3 and preconditions for step 4
		assertTrue("Checks should contain a bill with state == delivered. It doesn't.",
				cashier.Checks.get(0).getState() == state.delivered);
		assertTrue("Checks should contain a bill with total == 20.0 It doesn't.",
				cashier.Checks.get(0).getTotal() == 20.0);
		
		assertTrue("Checks should contain a second bill with state == pending. It doesn't.",
				cashier.Checks.get(1).getState() == state.pending);
		
		assertEquals(
				"MockWaiter should have one event logged after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertEquals(
				"First MockCustomer should have one event logged after the Cashier's scheduler is called for the first time. Instead, the First MockCustomer's event log reads: "
						+ customer.log.toString(), 1, customer.log.size());
		assertTrue("Cashier should have logged \"Calculated check.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Calculated check. Total = $20.0"));
		
		//step 4 of the test
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 4 and preconditions for step 5
		assertTrue("Checks should contain a bill with state == delivered. It doesn't.",
				cashier.Checks.get(0).getState() == state.delivered);
		assertTrue("Checks should contain a bill with total == 20.0 It doesn't.",
				cashier.Checks.get(0).getTotal() == 20.0);
		
		assertTrue("Checks should contain a second bill with state == delivered. It doesn't.",
				cashier.Checks.get(1).getState() == state.delivered);
		assertTrue("Checks should contain a second bill with total == 20.0 It doesn't.",
				cashier.Checks.get(1).getTotal() == 20.0);
		
		assertEquals(
				"MockWaiter should have two events logged after the Cashier's scheduler is called for the second time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 2, waiter.log.size());
		
		assertEquals(
				"Second MockCustomer should have one event logged after the Cashier's scheduler is called for the second time. Instead, the Second MockCustomer's event log reads: "
						+ customer2.log.toString(), 1, customer2.log.size());
		assertTrue("Cashier should have logged \"Calculated check.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Calculated check. Total = $20.0"));
		
		//step 5 of the test
		cashier.msgPayment(customer, customer.check, 20.00);
		
		//check postconditions for step 5 / preconditions for step 6
		assertTrue("Checks should contain a bill with state == paid. It doesn't.",
				cashier.Checks.get(0).getState() == state.paid);
		
		assertTrue("Cashier should have logged \"Received msgPayment from customer.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Payment = $20.0"));
		
		
		//step 4
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 4 / preconditions for step 5
		assertTrue("First MockCustomer should have logged \"Received msgHeresChange from cashier.\", but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHeresChange from cashier. Change = $0.0"));
	
			
		assertTrue("Cashier should have logged \"Deleted check.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Deleted check."));
		
		
		assertEquals("Checks should one remaining check. It doesn't", cashier.Checks.size(), 1);
		
		//step 5 of the test
		cashier.msgPayment(customer, customer2.check, 20.00);
		
		//check postconditions for step 5 / preconditions for step 6
		assertTrue("Checks should contain a bill with state == paid. It doesn't.",
				cashier.Checks.get(0).getState() == state.paid);
		
		assertTrue("Cashier should have logged \"Received msgPayment from customer.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Payment = $20.0"));
		
		
		//step 6
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 6
		assertTrue("Second MockCustomer should have logged \"Received msgHeresChange from cashier.\", but his last event logged reads instead: " 
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received msgHeresChange from cashier. Change = $0.0"));
	
			
		assertTrue("Cashier should have logged \"Deleted check.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Deleted check."));
		
		
		assertEquals("Checks should no checks. It doesn't", cashier.Checks.size(), 0);
		
	}//end one normal customer scenario

	public void testOneNormalCustomerOneNormalMarketBillScenario()
	{
		//setUp() runs first before this test!
		
		customer.cashier = cashier;//You can do almost anything in a unit test.	
		waiter.cashier = cashier;
		waiter.customer = customer;
		market.cashier = cashier;
		
		//check preconditions
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Checks.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgComputeCheck is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
				+ waiter.log.toString(), 0, waiter.log.size());
		

		assertEquals("Cashier should have 0 marketBills in it. It doesn't", cashier.Bills.size(), 0);
		assertTrue("Cashier should have $50.00 in cash. Instead it has: $" + cashier.getCash(), cashier.getCash() == 50.0);
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgMarketBill is called. Instead, the Cashier's event log reads: "
				+ cashier.log.toString(), 0, cashier.log.size());
		assertEquals("MockMarket should have an empty event log before the Cashier's scheduler is called. Instead, the MockMarket's event log reads: "
		+ market.log.toString(), 0, market.log.size());

		
		//step 1 of the test
		cashier.msgComputeCheck(waiter, customer, "Steak");

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("Cashier should have 1 check in it. It doesn't.", cashier.Checks.size(), 1);

		assertTrue("Checks should contain a bill with state == pending. It doesn't.",
				cashier.Checks.get(0).getState() == state.pending);
		
		assertTrue("Checks should contain a check with the right customer in it. It doesn't.", 
					cashier.Checks.get(0).getCustomer() == customer);
		
		//step 2
		cashier.msgMarketBill(market, 20.00);
		
		//check postconditions step 2/ preconditions step 3
		assertEquals("Cashier should have 1 marketBills in it. It doesn't", cashier.Bills.size(), 1);
		assertTrue("Cashier should have a bill with total $20.00. Instead the total is: $" + cashier.Bills.get(0).getPayment(), cashier.Bills.get(0).getPayment() == 20.00);
		assertTrue("Bills should contain a marketBill with the right market in it. It doesn't.", 
					cashier.Bills.get(0).getMarket() == market);

		//step 4 of the test
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 4 and preconditions for step 5
		assertTrue("Checks should contain a bill with state == delivered. It doesn't.",
				cashier.Checks.get(0).getState() == state.delivered);
		assertTrue("Checks should contain a bill with total == 20.0 It doesn't.",
				cashier.Checks.get(0).getTotal() == 20.0);
		
		assertEquals(
				"MockWaiter should have one event logged after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have one event logged after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ customer.log.toString(), 1, customer.log.size());
		assertTrue("Cashier should have logged \"Calculated check.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Calculated check. Total = $20.0"));
		
		//step 5
		assertTrue("Cashier's scheduler should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		//check postconditions step 5 / preconditions step 6
		assertEquals("Cashier should have 0 marketBills in it. It doesn't", cashier.Bills.size(), 0);
		assertTrue("Cashier should have logged \"Paid Bill.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Paid Bill. Total = $20.0"));
		assertTrue("Cashier should have $30.00 in cash. Instead it has: $" + cashier.getCash(), cashier.getCash() == 30.0);
		assertTrue("Market should have logged \"Received msgHeresMarket from cashier.\" but didn't. His log reads instead: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgHeresMarket from cashier. Total = $20.0"));
		
		//step 6 of the test
		cashier.msgPayment(customer, customer.check, 20.00);
		
		//check postconditions for step 6 / preconditions for step 7
		assertTrue("Checks should contain a bill with state == paid. It doesn't.",
				cashier.Checks.get(0).getState() == state.paid);
		
		assertTrue("Cashier should have logged \"Received msgPayment from customer.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Received msgPayment from customer. Payment = $20.0"));
		
		
		//step 7
		assertTrue("Cashier's scheduler should have returned true, but didn't.", 
					cashier.pickAndExecuteAnAction());
		
		//check postconditions for step 7
		assertTrue("MockCustomer should have logged \"Received msgHeresChange from cashier.\", but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received msgHeresChange from cashier. Change = $0.0"));
	
			
		assertTrue("Cashier should have logged \"Deleted check.\" but didn't. His log reads instead: " 
				+ cashier.log.getLastLoggedEvent().toString(), cashier.log.containsString("Deleted check."));
		
		
		assertEquals("Checks should no checks. It doesn't", cashier.Checks.size(), 0);
		
	}//end one normal customer scenario
	
}
