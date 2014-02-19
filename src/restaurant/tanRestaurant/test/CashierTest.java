package restaurant.tanRestaurant.test;

import restaurant.tanRestaurant.TanCashierAgent;
import restaurant.tanRestaurant.MarketAgent;
import restaurant.tanRestaurant.TanCashierAgent.Bill.billState;
import restaurant.tanRestaurant.TanCashierAgent.MyBill.marketBS;
import restaurant.tanRestaurant.TanCashierAgent.MyBill;
//import restaurant.WaiterAgent.Bill;
import restaurant.tanRestaurant.TanCashierAgent.Bill;
import restaurant.tanRestaurant.MarketAgent.MarketBill;
import restaurant.tanRestaurant.interfaces.Customer;
import restaurant.tanRestaurant.interfaces.Waiter;
import restaurant.tanRestaurant.test.mock.MockCustomer;
import restaurant.tanRestaurant.test.mock.MockWaiter;
import restaurant.tanRestaurant.test.mock.MockMarket;
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
	TanCashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockMarket market;
	MockMarket market2;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new TanCashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");		
		waiter = new MockWaiter("mockwaiter");
		market= new MockMarket("mockmarket");
		market2= new MockMarket("mockmarket2");

	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//setUp(); runs first before this test!
		customer.cashier = cashier;//You can do almost anything in a unit test.			
		
		//check preconditions
		
		assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Bills.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's msgCollectingBill is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//Step 1: Waiter asks for bill
		cashier.msgCollectingBill("Steak", waiter, customer);
		

		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.Bills.size(), 1);		
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals("Bill state should be Pending, but it isn't", cashier.Bills.get(0).getState(), billState.Pending);
		
		
		//Step 2: Call Cashier's scheduler and send bill to Waiter
		assertTrue("Cashier's schedule should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
        
        // check step 2 postconditions and step 3 preconditions
        assertEquals("Cashier bill state should be Pending, but is not.", cashier.Bills.get(0).getState(),
        		billState.Pending);
        assertTrue("Mockwaiter should have gotten a bill, but didn't.", waiter.log.containsString("Received msgHereIsBill"));
		
        
        
		//Step 3: Customer paying
           
		cashier.msgHereIsMyMoney(customer, 20.00);
		
		//check postconditions for step 2 / preconditions for step 3
		assertTrue("CashierBill should contain a bill with state == Paid. It doesn't.",
				cashier.Bills.get(0).bs == billState.Paid);
				
		assertTrue("CashierBill should contain a bill of price = $15.99. It contains something else instead: $" 
				+ cashier.Bills.get(0).bill, cashier.Bills.get(0).bill == 15.99);
		
		assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
					cashier.Bills.get(0).getCustomer() == customer);
		
		
		//Step 4: Checking that change return mechanism is correct
		//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
		assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgHereIsMyMoney), but didn't.", 
					cashier.pickAndExecuteAnAction());
				
		//check postconditions for step 3 / preconditions for step 4
		assertTrue("MockCustomer should have logged an event for receiving /Here is Your Change/ with the correct balance, but his last event logged reads instead: " 
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Change = "+ 4.01));
		

		assertTrue("CashierBill should contain debt == 0.0. It contains something else instead: $" 
				+ cashier.Bills.get(0).debt, cashier.Bills.get(0).debt == 0);
		
		assertTrue("CashierBill should contain a bill with state == Settled. It doesn't.",
				cashier.Bills.get(0).bs == billState.Settled);
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
	
	}//end one normal customer scenario
	
	//scenario where Cashier has to pay the market
	public void testTwoNormalCustomerScenario()
	{
		
		MarketBill mb= new MarketBill("Salad", 5, market);
		//Step 1: Market delivers Bill to Cashier
		cashier.msgHereIsMarketBill(mb);
		
		//checking post conditions
		assertTrue("Cashier should have 1 marketbill in MyBills, but it has "+ cashier.MyBills.size(), cashier.MyBills.size()==1);

		assertTrue("MyBill state should be received, but isn't", cashier.MyBills.get(0).mbs==marketBS.received);
		
		assertEquals(
				"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
	
		//Step 2: Cashier pays market
		assertTrue("Cashier's schedule should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
		
		assertTrue("MyBill state should be paid, but isn't", cashier.MyBills.get(0).mbs==marketBS.paid);
		
		assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Payment="+ 15.0));
		
		assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Debt ="+ 0.0));
		
	}
	
	//scenario where Cashier has to pay two markets who jointly fulfill the demand
		public void testThreeNormalCustomerScenario()
		{
			
			MarketBill mb= new MarketBill("Salad", 1, market);
			//Step 1: Market delivers Bill to Cashier
			cashier.msgHereIsMarketBill(mb);
			
			//checking post conditions
			assertTrue("Cashier should have 1 marketbill in MyBills, but it has "+ cashier.MyBills.size(), cashier.MyBills.size()==1);

			assertTrue("MyBill state should be received, but isn't", cashier.MyBills.get(0).mbs==marketBS.received);
			
			assertEquals(
					"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
							+ market.log.toString(), 0, market.log.size());
		
			//Step 2: Cashier pays market
			assertTrue("Cashier's schedule should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
			
			assertTrue("MyBill state should be paid, but isn't", cashier.MyBills.get(0).mbs==marketBS.paid);
			
			assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
					+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Payment="+ 3.0));
			
			MarketBill mb2= new MarketBill("Salad", 4, market2);
			
			//Step 3: Market2 delivers Bill to Cashier
			cashier.msgHereIsMarketBill(mb2);
			
			//checking post conditions
			assertTrue("Cashier should have 2 marketbills in MyBills, but it has "+ cashier.MyBills.size(), cashier.MyBills.size()==2);

			assertTrue("MyBill state should be received, but isn't", cashier.MyBills.get(1).mbs==marketBS.received);
			
			assertEquals(
					"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
							+ market2.log.toString(), 0, market2.log.size());
		
			//Step 4: Cashier pays Market2
			assertTrue("Cashier's schedule should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
			
			assertTrue("MyBill state should be paid, but isn't", cashier.MyBills.get(1).mbs==marketBS.paid);
			
			assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
					+ market2.log.getLastLoggedEvent().toString(), market2.log.containsString("Payment="+ 12.0));
			
		}
		
		//scenario where customer doesn't have enough to pay cashier
		public void testFourNormalCustomerScenario()
		{
			customer.cashier = cashier;//You can do almost anything in a unit test.			
			
			//check preconditions
			
			assertEquals("Cashier should have 0 bills in it. It doesn't.",cashier.Bills.size(), 0);		
			assertEquals("CashierAgent should have an empty event log before the Cashier's msgCollectingBill is called. Instead, the Cashier's event log reads: "
							+ cashier.log.toString(), 0, cashier.log.size());
			
			//Step 1: Waiter asks for bill
			cashier.msgCollectingBill("Steak", waiter, customer);
			

			//check postconditions for step 1 and preconditions for step 2
			assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
							+ waiter.log.toString(), 0, waiter.log.size());
			
			assertEquals("Cashier should have 1 bill in it. It doesn't.", cashier.Bills.size(), 1);		
			
			assertEquals(
					"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
							+ waiter.log.toString(), 0, waiter.log.size());
			
			assertEquals(
					"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
							+ waiter.log.toString(), 0, waiter.log.size());
			
			assertEquals("Bill state should be Pending, but it isn't", cashier.Bills.get(0).getState(), billState.Pending);
			
			
			//Step 2: Call Cashier's scheduler and send bill to Waiter
			assertTrue("Cashier's schedule should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
	        
	        // check step 2 postconditions and step 3 preconditions
	        assertEquals("Cashier bill state should be Pending, but is not.", cashier.Bills.get(0).getState(),
	        		billState.Pending);
	        assertTrue("Mockwaiter should have gotten a bill, but didn't.", waiter.log.containsString("Received msgHereIsBill"));
			
	        
	        
			//Step 3: Customer paying
	           
			cashier.msgHereIsMyMoney(customer, 5.00);
			
			//check postconditions for step 2 / preconditions for step 3
			assertTrue("CashierBill should contain a bill with state == Paid. It doesn't.",
					cashier.Bills.get(0).bs == billState.Paid);
					
			assertTrue("CashierBill should contain a bill of price = $15.99. It contains something else instead: $" 
					+ cashier.Bills.get(0).bill, cashier.Bills.get(0).bill == 15.99);
			
			assertTrue("CashierBill should contain a bill with the right customer in it. It doesn't.", 
						cashier.Bills.get(0).getCustomer() == customer);
			
			
			//Step 4: Checking that change return mechanism is correct
			//NOTE: I called the scheduler in the assertTrue statement below (to succinctly check the return value at the same time)
			assertTrue("Cashier's scheduler should have returned true (needs to react to customer's msgHereIsMyMoney), but didn't.", 
						cashier.pickAndExecuteAnAction());
					
			//check postconditions for step 3 / preconditions for step 4
			assertTrue("MockCustomer should have logged an event for receiving /Here is Your Change/ with the correct balance, but his last event logged reads instead: " 
					+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Debt = "+ 10.99));
			

			assertTrue("CashierBill should contain debt == 0.0. It contains something else instead: $" 
					+ cashier.Bills.get(0).debt, cashier.Bills.get(0).change == 0);
			
			assertTrue("CashierBill should contain a bill with state == Settled. It doesn't.",
					cashier.Bills.get(0).bs == billState.Settled);
			
			assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
					cashier.pickAndExecuteAnAction());
			
		
		}//end four normal customer scenario
		
		//scenario where Cashier has to pay the market but has insufficient funds
		public void testFiveNormalCustomerScenario()
		{
			
			MarketBill mb= new MarketBill("Salad", 5, market);
			cashier.setRestaurantCash(3.0);
			//Step 1: Market delivers Bill to Cashier
			cashier.msgHereIsMarketBill(mb);
		
			
			//checking post conditions
			assertTrue("Cashier should have 1 marketbill in MyBills, but it has "+ cashier.MyBills.size(), cashier.MyBills.size()==1);

			assertTrue("MyBill state should be received, but isn't", cashier.MyBills.get(0).mbs==marketBS.received);
			
			assertEquals(
					"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
							+ waiter.log.toString(), 0, waiter.log.size());
		
			//Step 2: Cashier pays market
			assertTrue("Cashier's schedule should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
			
			assertTrue("MyBill state should be paid, but isn't", cashier.MyBills.get(0).mbs==marketBS.paid);
			
			assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
					+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Payment="+ 3.0));
			
			assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
					+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Debt ="+ 12.0));
			
		}

		//scenario where Customer is unable to pay and orders from the same market later when it is able to pay
		public void testSixNormalCustomerScenario()
		{
			System.out.println("entered testSIX");
			MarketBill mb= new MarketBill("Salad", 5, market);
			cashier.setRestaurantCash(3.0);
			//Step 1: Market delivers Bill to Cashier
			cashier.msgHereIsMarketBill(mb);
		
			
			//checking post conditions
			assertTrue("Cashier should have 1 marketbill in MyBills, but it has "+ cashier.MyBills.size(), cashier.MyBills.size()==1);

			assertTrue("MyBill state should be received, but isn't", cashier.MyBills.get(0).mbs==marketBS.received);
			
			assertEquals(
					"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
							+ waiter.log.toString(), 0, waiter.log.size());
		
			//Step 2: Cashier pays market
			assertTrue("Cashier's schedule should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
			
			assertTrue("MyBill state should be paid, but isn't", cashier.MyBills.get(0).mbs==marketBS.paid);
			
			assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
					+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Payment="+ 3.0));
			
			assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
					+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Debt ="+ 12.0));
						
			//Step 3: Market delivers new Bill to Cashier
			
			MarketBill mb2= new MarketBill("Salad", 7, market);
			cashier.setRestaurantCash(50.0);			
			cashier.msgHereIsMarketBill(mb2);
		
			
			//checking post conditions
			assertTrue("Cashier should have 2 marketbill in MyBills, but it has "+ cashier.MyBills.size(), cashier.MyBills.size()==2);

			assertTrue("MyBill state should be received, but isn't", cashier.MyBills.get(1).mbs==marketBS.received);
			
			assertEquals(
					"MockMarket should have an empty event log before the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
							+ waiter.log.toString(), 0, waiter.log.size());
		
			//Step 4: Cashier pays market with interest
			assertTrue("Cashier's schedule should have returned true, but didn't.", cashier.pickAndExecuteAnAction());
			
			assertTrue("MyBill state should be paid, but isn't", cashier.MyBills.get(1).mbs==marketBS.paid);
			
			assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
					+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Payment="+ 34.8));
			
			assertTrue("MockMarket should have logged an event for receiving /msgHereIsPayment/ with the correct balance, but his last event logged reads instead: " 
					+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Debt ="+ 0.0));
			
		}
}
