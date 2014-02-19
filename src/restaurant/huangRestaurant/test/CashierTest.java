package restaurant.huangRestaurant.test;

import restaurant.huangRestaurant.HuangCashierAgent;
import restaurant.huangRestaurant.Check;
import restaurant.huangRestaurant.FoodBill;
import restaurant.huangRestaurant.HuangCashierAgent.BillState;
import restaurant.huangRestaurant.HuangCashierAgent.Order;
import restaurant.huangRestaurant.HuangCashierAgent.OrderState;
import restaurant.huangRestaurant.HuangCashierAgent.freeLoaderState;
import restaurant.huangRestaurant.interfaces.Customer;
import restaurant.huangRestaurant.interfaces.Waiter;
import restaurant.huangRestaurant.test.mock.MockCustomer;
import restaurant.huangRestaurant.test.mock.MockMarket;
import restaurant.huangRestaurant.test.mock.MockWaiter;
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
	HuangCashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockCustomer customer2;
	MockCustomer customer3;
	MockMarket market1;
	MockMarket market2;
	FoodBill b1;
	FoodBill b2;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new HuangCashierAgent("cashier");		
		customer = new MockCustomer("mockcustomer");
		customer2 = new MockCustomer("mockcustomer");
		customer3 = new MockCustomer("mockcustomer");
		waiter = new MockWaiter("mockwaiter");
		market1 = new MockMarket("mockmarket1");
		market2 = new MockMarket("mockmarket2");
		b1 = new FoodBill("Steak", 5);
		b2 = new FoodBill ("Steak", 5);
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact Check.
	 */
	public void testOneNormalCustomerScenario() {	
		try {
			setUp();
		} 
		catch (Exception e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		customer.cashier = cashier;//You can do almost anything in a unit test.			
		customer.waiter = waiter;
		waiter.cashier = cashier;
		waiter.customer = customer;
		//check preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.orders.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsCustomerDish is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		cashier.msgHereIsCustomerDish(waiter, "Steak", 1, customer);//send the message from a waiter
		assertFalse("Cashier should have one order in its list. It does not.", cashier.orders.isEmpty());
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 cx in it. It doesn't.", cashier.orders.size(), 1);
		
		assertTrue("CashierCheck should exist, it doesn't", !cashier.orders.get(0).equals(null));
		System.out.println("state: " + cashier.orders.get(0).state);
		assertTrue(cashier.orders.get(0).state == OrderState.waitingForWaiter);
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		//step 2 of the test
		//check postconditions for step 2 / preconditions for step 3
		cashier.msgAskForCheck(customer);
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.orders.get(0).state == OrderState.withWaiter);
		assertTrue("Casher's check should now be with waiter as its state. It isn't", cashier.orders.get(0).state == OrderState.withWaiter);
		assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
					cashier.orders.get(0).cx.c == customer);
		
		//step 3
		cashier.msgHereIsMoney(customer);
		
		assertTrue("Cashier Order state should now be paid because customer has paid.",
				cashier.orders.get(0).state == OrderState.Paid);
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
		
	
	}//end one normal customer scenario
	public void testTwoOneFlakingCustomerScenario() {
		try {
			setUp();
		} 
		catch (Exception e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		customer.cashier = cashier;//You can do almost anything in a unit test.			
		customer.waiter = waiter;
		waiter.cashier = cashier;
		waiter.customer = customer;
		//check preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.orders.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsCustomerDish is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		cashier.msgHereIsCustomerDish(waiter, "Steak", 1, customer);//send the message from a waiter
		assertFalse("Cashier should have one order in its list. It does not.", cashier.orders.isEmpty());
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 cx in it. It doesn't.", cashier.orders.size(), 1);
		
		assertTrue("CashierCheck should exist, it doesn't", !cashier.orders.get(0).equals(null));
		System.out.println("state: " + cashier.orders.get(0).state);
		assertTrue(cashier.orders.get(0).state == OrderState.waitingForWaiter);
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		//step 2 of the test
		//check postconditions for step 2 / preconditions for step 3
		cashier.msgAskForCheck(customer);
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.orders.get(0).state == OrderState.withWaiter);
		assertTrue("Casher's check should now be with waiter as its state. It isn't", cashier.orders.get(0).state == OrderState.withWaiter);
		assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
					cashier.orders.get(0).cx.c == customer);
		
		//step 3 Prep Check
		assertTrue(cashier.freeLoaders.isEmpty());	
		cashier.msgNotEnoughMoney(customer);
		assertTrue(!cashier.freeLoaders.isEmpty());
		assertTrue(cashier.pickAndExecuteAnAction());//Kick Free Loader action
		assertTrue(cashier.freeLoaders.get(0).state == freeLoaderState.Penalized);
		assertTrue(cashier.orders.get(0).state == OrderState.cannotPay);
	
		
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
	}

	public void testThree3NormalCustomerScenario() {	
	try {
		setUp();
	} 
	catch (Exception e) {
		//TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	customer.cashier = cashier;//You can do almost anything in a unit test.			
	customer.waiter = waiter;
	customer2.cashier = cashier;//You can do almost anything in a unit test.			
	customer2.waiter = waiter;
	customer3.cashier = cashier;//You can do almost anything in a unit test.			
	customer3.waiter = waiter;
	waiter.cashier = cashier;
	waiter.customer = customer;
	//check preconditions
	assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.orders.size(), 0);		
	assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsCustomerDish is called. Instead, the Cashier's event log reads: "
					+ cashier.log.toString(), 0, cashier.log.size());
	
	//step 1 of the test
	cashier.msgHereIsCustomerDish(waiter, "Steak", 0, customer);//send the message from a waiter
	assertFalse("Cashier should have one order in its list. It does not.", cashier.orders.isEmpty());
	//check postconditions for step 1 and preconditions for step 2
	assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
					+ waiter.log.toString(), 0, waiter.log.size());
	assertEquals("Cashier should have 1 cx in it. It doesn't.", cashier.orders.size(), 1);
	
	assertTrue("CashierCheck should exist, it doesn't", !cashier.orders.get(0).equals(null));
	
	System.out.println("state: " + cashier.orders.get(0).state);
	
	assertTrue("Cashier should have 1 check and its state should be waitingForWaiter after 1 execution of scheduler. It does not.",cashier.orders.get(0).state == OrderState.waitingForWaiter);
	
	assertEquals( "MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
	
	assertEquals("MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: " + waiter.log.toString(), 0, waiter.log.size());
	
	cashier.msgHereIsCustomerDish(waiter, "Pizza", 1, customer2);
	
	cashier.msgHereIsCustomerDish(waiter, "Salad", 2, customer3);
	assertEquals("Cashier should have 3 cx in it. It doesn't.", cashier.orders.size(), 3);
	//step 2 of the test
	//check postconditions for step 2 / preconditions for step 3
	waiter.cashier.msgAskForCheck(customer);
	assertTrue(cashier.pickAndExecuteAnAction());
	assertTrue(cashier.orders.get(0).state == OrderState.withWaiter);
	assertTrue("Casher's check should now be with waiter as its state. It isn't", cashier.orders.get(0).state == OrderState.withWaiter);
	assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
				cashier.orders.get(0).cx.c == customer);
	
	//step 3
	assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
			cashier.orders.get(0).cx.c == customer);
	cashier.msgHereIsMoney(customer);
	
	assertTrue("Cashier Order state should now be paid because customer has paid.",
			cashier.orders.get(0).state == OrderState.Paid);
	assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
			cashier.pickAndExecuteAnAction());
	//step 4 two customers finish eating at same time, waiter asks for 2 checks back to back
	waiter.cashier.msgAskForCheck(customer2);
	waiter.cashier.msgAskForCheck(customer3);
	assertTrue(cashier.pickAndExecuteAnAction());
	
	assertTrue("Casher's check 2 for customer 2 should now be with waiter as its state. It isn't", cashier.orders.get(1).state == OrderState.withWaiter);
	assertTrue("Casher's check 3 for customer 3 should now be with waiter as its state. It isn't", cashier.orders.get(2).state == OrderState.withWaiter);
	assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
			cashier.orders.get(1).cx.c == customer2);
	assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
			cashier.orders.get(2).cx.c == customer3);
	cashier.msgHereIsMoney(customer2);
	cashier.msgHereIsMoney(customer3);
	assertTrue("Cashier Order state should now be paid because customer2 has paid.",
			cashier.orders.get(1).state == OrderState.Paid);
	assertTrue("Cashier Order state should now be paid because customer3 has paid.",
			cashier.orders.get(2).state == OrderState.Paid);
	
	assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
			cashier.pickAndExecuteAnAction());
	
	}
	public void testFour2Flaking1NormalCustomerScenario() {
		try {
			setUp();
		} 
		catch (Exception e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		customer.cashier = cashier;//You can do almost anything in a unit test.			
		customer.waiter = waiter;
		customer2.cashier = cashier;//You can do almost anything in a unit test.			
		customer2.waiter = waiter;
		customer3.cashier = cashier;//You can do almost anything in a unit test.			
		customer3.waiter = waiter;
		waiter.cashier = cashier;
		//check preconditions
		assertEquals("Cashier should have 0 checks in it. It doesn't.",cashier.orders.size(), 0);		
		assertEquals("CashierAgent should have an empty event log before the Cashier's HereIsCustomerDish is called. Instead, the Cashier's event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		//step 1 of the test
		cashier.msgHereIsCustomerDish(waiter, "Steak", 0, customer);//send the message from a waiter
		assertFalse("Cashier should have one order in its list. It does not.", cashier.orders.isEmpty());
		
		//check postconditions for step 1 and preconditions for step 2
		assertEquals("MockWaiter should have an empty event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		assertEquals("Cashier should have 1 cx in it. It doesn't.", cashier.orders.size(), 1);
		
		assertTrue("CashierCheck should exist, it doesn't", !cashier.orders.get(0).equals(null));
		System.out.println("state: " + cashier.orders.get(0).state);
		assertTrue(cashier.orders.get(0).state == OrderState.waitingForWaiter);
		
		assertEquals(
				"MockWaiter should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		assertEquals(
				"MockCustomer should have an empty event log after the Cashier's scheduler is called for the first time. Instead, the MockCustomer's event log reads: "
						+ waiter.log.toString(), 0, waiter.log.size());
		
		
		cashier.msgHereIsCustomerDish(waiter, "Pizza", 1, customer2);
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.orders.get(1).state == OrderState.waitingForWaiter);
		cashier.msgHereIsCustomerDish(waiter, "Chicken", 1, customer3);
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.orders.get(1).state == OrderState.waitingForWaiter);
		
		//step 2 of the test
		//check postconditions for step 2 / preconditions for step 3
		cashier.msgAskForCheck(customer);
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.orders.get(0).state == OrderState.withWaiter);
		assertTrue("Casher's check should now be with waiter as its state. It isn't", cashier.orders.get(0).state == OrderState.withWaiter);
		assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
					cashier.orders.get(0).cx.c == customer);
		cashier.msgAskForCheck(customer2);
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.orders.get(1).state == OrderState.withWaiter);
		assertTrue("Casher's check should now be with waiter as its state. It isn't", cashier.orders.get(1).state == OrderState.withWaiter);
		assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
					cashier.orders.get(1).cx.c == customer2);
		//step 3 Prep Check
		assertTrue(cashier.freeLoaders.isEmpty());
		cashier.msgNotEnoughMoney(customer);
		cashier.msgNotEnoughMoney(customer2);
		assertTrue(!cashier.freeLoaders.isEmpty());
		assertTrue(cashier.pickAndExecuteAnAction());//Kick Free Loader action
		assertTrue(cashier.freeLoaders.get(0).state == freeLoaderState.Penalized);
		assertTrue(cashier.orders.get(0).state == OrderState.cannotPay);
		assertTrue(cashier.freeLoaders.get(1).state == freeLoaderState.Penalized);
		assertTrue(cashier.orders.get(1).state == OrderState.cannotPay);
		assertTrue(cashier.freeLoaders.size() > 1);
		
		//step 4 normal customer pays
		cashier.msgAskForCheck(customer3);
		assertTrue(cashier.pickAndExecuteAnAction());
		assertTrue(cashier.orders.get(2).state == OrderState.withWaiter);
		assertTrue("Casher's check should now be with waiter as its state. It isn't", cashier.orders.get(2).state == OrderState.withWaiter);
		assertTrue("CashierCheck should contain a Check with the right customer in it. It doesn't.", 
					cashier.orders.get(2).cx.c == customer3);
		cashier.msgHereIsMoney(customer3);
		assertTrue("Cashier Order state should now be paid because customer has paid.",
				cashier.orders.get(2).state == OrderState.Paid);
	
		assertFalse("Cashier's scheduler should have returned false (no actions left to do), but didn't.", 
				cashier.pickAndExecuteAnAction());
	}
	public void testFive1Market1OrderNormativeScenario() {
		try {
			setUp();
		} 
		catch (Exception e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("Cashier should have zero bills to pay in the beginning of the test", cashier.bills.isEmpty());
		cashier.msgHereIsFoodDeliveryBill(b1, market1);
		assertTrue("Cashier should now have one bill in its bill list", cashier.bills.size() == 1);
		assertTrue("Bill should be equal to b1 in the sent up.", cashier.bills.get(0).b.order == b1.order);
		assertTrue("Bill should be equal to b1 in the sent up.", cashier.bills.get(0).m == market1);
		assertTrue("Cashier scheduler should return true as it executes pay bill", cashier.pickAndExecuteAnAction());
		assertTrue("Check the state of the bill after scheduler, should be paid.", cashier.bills.get(0).state == BillState.paid);
		assertFalse("Cashier scheduler should return false as there is nothing more for it to do in this scenario", cashier.pickAndExecuteAnAction());
	}
	public void testSix2Market1OrderScenario() {
		try {
			setUp();
		} 
		catch (Exception e) {
			//TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertTrue("Cashier should have zero bills to pay in the beginning of the test", cashier.bills.isEmpty());
		cashier.msgHereIsFoodDeliveryBill(b1, market1);
		cashier.msgHereIsFoodDeliveryBill(b1, market2);
		assertTrue("Cashier should now have one bill in its bill list", cashier.bills.size() == 2);
		assertTrue("Bill should be equal to b1 in the sent up.", cashier.bills.get(0).b.order == b1.order);
		assertTrue("Bill should be equal to b1 in the sent up.", cashier.bills.get(0).m == market1);
		assertTrue("Bill should be equal to b1 in the sent up.", cashier.bills.get(1).b.order == b2.order);
		assertTrue("Bill should be equal to b1 in the sent up.", cashier.bills.get(1).m == market2);
		assertTrue("Check the state of the bill2 before Scheduler, should be unpaid.", cashier.bills.get(1).state == BillState.unpaid);
		assertTrue("Cashier scheduler should return true as it executes pay bill, paying for both bills", cashier.pickAndExecuteAnAction());
		assertTrue("Check the state of the bill1 after scheduler, should be paid.", cashier.bills.get(0).state == BillState.paid);
		assertTrue("Check the state of the bill2 after scheduler runs once, should not be paid.", cashier.bills.get(1).state == BillState.unpaid);
		assertTrue("Cashier scheduler return true to pay for bill 2", cashier.pickAndExecuteAnAction());
		assertTrue("Check the state of the bill2 after scheduler runs once, should not be paid.", cashier.bills.get(1).state == BillState.paid);
	}
}
