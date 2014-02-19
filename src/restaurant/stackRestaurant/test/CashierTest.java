package restaurant.stackRestaurant.test;

import gui.CurrentBuildingPanel;
import restaurant.stackRestaurant.StackCashierAgent;
import restaurant.stackRestaurant.StackCashierAgent.CheckState;
import restaurant.stackRestaurant.StackCashierAgent.CustomerState;
import restaurant.stackRestaurant.StackRestaurant;
import restaurant.stackRestaurant.helpers.Check;
import restaurant.stackRestaurant.test.mock.MockCustomer;
import restaurant.stackRestaurant.test.mock.MockWaiter;
import market.MarketCheck;
import market.test.mock.MockMarket;
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
	StackCashierAgent cashier;
	MockWaiter waiter;
	MockCustomer customer;
	MockCustomer customer2;
	MockCustomer customer3;
	MockMarket market;
	StackRestaurant stackRestaurant;
	CurrentBuildingPanel panel;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		cashier = new StackCashierAgent();		
		customer = new MockCustomer("mockcustomer");
		customer2 = new MockCustomer("mockcustomer2");
		customer3 = new MockCustomer("mockcustomer3");
		waiter = new MockWaiter("mockwaiter");
		market = new MockMarket("mockmarket");
		stackRestaurant = new StackRestaurant("StackRestaurant");
		cashier.setRestaurant(stackRestaurant);
		waiter.setRestaurant(stackRestaurant);
		panel = new CurrentBuildingPanel(stackRestaurant);
		stackRestaurant.setInfoPanel(panel);
	}	
	/**
	 * This tests the cashier under very simple terms: one customer is ready to pay the exact bill.
	 */
	public void testOneNormalCustomerScenario()
	{
		//check preconditions
		assertEquals("Till should have 1000. It is not.", cashier.getTill(), 1000.0);
		
//		//step 1 of the test
		cashier.msgComputeCheck(waiter, customer, "Pizza");
		assertEquals("MockWaiter should have one event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		assertEquals("Cashier should have 1 customer with a check. It doesn't.", cashier.getCustomers().size(), 1);
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.NeedComputing);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.Computed);
		assertTrue("Waiter should have logged \"Received check from waiter for Pizza costing 8.99\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check from waiter for Pizza costing 8.99"));
		
//		//step 2 of the test
		Check check = new Check(8.99, customer, "Pizza");
		cashier.msgPayCheck(customer, check, 10.00);
		assertTrue("Cashier's customer should have a state of NeedPaying. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.NeedPaying);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of Paid. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.Paid);
		assertTrue("Customer should have logged \"Received 1.0099999999999998 in change\" but didn't. His log reads instead: "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received 1.0099999999999998 in change"));
		assertTrue("Customer should have 0 in debt. Instead he has " + cashier.getCustomers().get(0).debt, 0 == cashier.getCustomers().get(0).debt);
		assertTrue("Till should now have 1008.99 in it. Instead it has: " + cashier.getTill(), 1008.99 == cashier.getTill());
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
	}
	
	public void testCheapCustomerScenario() {
		//check preconditions
		assertEquals("Till should be full. It is not.", cashier.getTill(), 1000.0);
		//step 1 of the test
		cashier.msgComputeCheck(waiter, customer, "Pizza");
		assertEquals("MockWaiter should have one event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		assertEquals("Cashier should have 1 customer with a check. It doesn't.", cashier.getCustomers().size(), 1);
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.NeedComputing);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.Computed);
		assertTrue("Waiter should have logged \"Received check from waiter for Pizza costing 8.99\" but didn't. His log reads instead: " 
						+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check from waiter for Pizza costing 8.99"));
				
		//step 2 of the test
		Check check = new Check(8.99, customer, "Pizza");
		cashier.msgPayCheck(customer, check, 5.00);
		assertTrue("Cashier's customer should have a state of NeedPaying. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.NeedPaying);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of Paid. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.Paid);
		assertTrue("Customer should have logged \"Received 0.0 in change\" but didn't. His log reads instead: "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received 0.0 in change"));
		assertTrue("Customer should have 3.99 in debt. Instead he has " + cashier.getCustomers().get(0).debt, 3.99 == cashier.getCustomers().get(0).debt);
		assertTrue("Till should now have 1005.0 in it. Instead it has: " + cashier.getTill(), 1005.0 == cashier.getTill());
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());		
	}
	
	public void testThreeCustomerScenario() {
		Check check1 = new Check(15.99, customer, "Steak");
		Check check2 = new Check(8.99, customer, "Pizza");
		Check check3 = new Check(5.99, customer, "Salad");
		
		assertEquals("Till should be full. It is not.", cashier.getTill(), 1000.0);
		
		cashier.msgComputeCheck(waiter, customer, "Steak");
		assertEquals("Cashier should have 1 customer with a check. It doesn't.", cashier.getCustomers().size(), 1);
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.NeedComputing);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.Computed);
		assertTrue("Waiter should have logged \"Received check from waiter for Steak costing 15.99\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check from waiter for Steak costing 15.99"));
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());

		
		cashier.msgComputeCheck(waiter, customer2, "Pizza");
		assertEquals("Cashier should have 2 customer with a check. It doesn't.", cashier.getCustomers().size(), 2);
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(1).state == CustomerState.NeedComputing);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(1).state == CustomerState.Computed);
		assertTrue("Waiter should have logged \"Received check from waiter for Pizza costing 15.99\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check from waiter for Pizza costing 8.99"));
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());

		
		
		cashier.msgComputeCheck(waiter, customer3, "Salad");
		assertEquals("Cashier should have 3 customer with a check. It doesn't.", cashier.getCustomers().size(), 3);
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(2).state == CustomerState.NeedComputing);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(2).state == CustomerState.Computed);		
		assertTrue("Waiter should have logged \"Received check from waiter for Salad costing 5.99\" but didn't. His log reads instead: " 
				+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check from waiter for Salad costing 5.99"));
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		
		cashier.msgPayCheck(customer, check1, 20.00);
		assertTrue("Cashier's customer should have a state of NeedPaying. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.NeedPaying);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of Paid. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.Paid);
		assertTrue("Customer should have logged \"Received 4.01 in change\" but didn't. His log reads instead: "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received 4.01 in change"));
		assertTrue("Till should now have 1015.99 in it. Instead it has: " + cashier.getTill(), 1015.99 == cashier.getTill());
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		
		cashier.msgPayCheck(customer2, check2, 20.00);
		assertTrue("Cashier's customer should have a state of NeedPaying. It doesn't.", cashier.getCustomers().get(1).state == CustomerState.NeedPaying);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of Paid. It doesn't.", cashier.getCustomers().get(1).state == CustomerState.Paid);
		assertTrue("Customer should have logged \"Received 11.01 in change\" but didn't. His log reads instead: "
				+ customer2.log.getLastLoggedEvent().toString(), customer2.log.containsString("Received 11.01 in change"));
		assertTrue("Till should now have 1024.98 in it. Instead it has: " + cashier.getTill(), 1024.98 == cashier.getTill());
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		
		cashier.msgPayCheck(customer3, check3, 3.00);
		assertTrue("Cashier's customer should have a state of NeedPaying. It doesn't.", cashier.getCustomers().get(2).state == CustomerState.NeedPaying);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of Paid. It doesn't.", cashier.getCustomers().get(2).state == CustomerState.Paid);
		assertTrue("Customer should have logged \"Received 0.0 in change\" but didn't. His log reads instead: "
				+ customer3.log.getLastLoggedEvent().toString(), customer3.log.containsString("Received 0.0 in change"));
		assertTrue("Customer should have 2.99 in debt. Instead he has " + cashier.getCustomers().get(0).debt, 2.99 == cashier.getCustomers().get(2).debt);
		assertTrue("Till should now have 1027.98 in it. Instead it has: " + cashier.getTill(), 1027.98 == cashier.getTill());
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
	}
	
	public void testRepeatCheap() {
		assertEquals("Till should have 1000. It is not.", cashier.getTill(), 1000.0);
		
		//step 1 of the test
		cashier.msgComputeCheck(waiter, customer, "Pizza");
		assertEquals("MockWaiter should have one event log before the Cashier's scheduler is called. Instead, the MockWaiter's event log reads: "
						+ waiter.log.toString(), 1, waiter.log.size());
		assertEquals("Cashier should have 1 customer with a check. It doesn't.", cashier.getCustomers().size(), 1);
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.NeedComputing);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of NeedComputing. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.Computed);
		assertTrue("Waiter should have logged \"Received check from waiter for Pizza costing 8.99\" but didn't. His log reads instead: " 
						+ waiter.log.getLastLoggedEvent().toString(), waiter.log.containsString("Received check from waiter for Pizza costing 8.99"));
				
		//step 2 of the test
		Check check = new Check(8.99, customer, "Pizza");
		cashier.msgPayCheck(customer, check, 10.00);
		assertTrue("Cashier's customer should have a state of NeedPaying. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.NeedPaying);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of Paid. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.Paid);
		System.out.println(customer.log.getLastLoggedEvent().toString());
		assertTrue("Customer should have logged \"Received 1.0099999999999998 in change\" but didn't. His log reads instead: "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received 1.0099999999999998 in change"));
		assertTrue("Customer should have 0.0 in debt. Instead he has " + cashier.getCustomers().get(0).debt, 0.0 == cashier.getCustomers().get(0).debt);
		assertTrue("Till should now have 1008.99 in it. Instead it has: " + cashier.getTill(), 1008.99 == cashier.getTill());
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		
		cashier.msgPayCheck(customer, check, 1.01);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's customer should have a state of Paid. It doesn't.", cashier.getCustomers().get(0).state == CustomerState.Paid);
		assertTrue("Customer should have logged \"Received 0.0 in change\" but didn't. His log reads instead: "
				+ customer.log.getLastLoggedEvent().toString(), customer.log.containsString("Received 0.0 in change"));
		assertTrue("Customer should have 7.98 in debt. Instead he has " + cashier.getCustomers().get(0).debt, 7.98 == cashier.getCustomers().get(0).debt);
		assertTrue("Till should now have 1010.0 in it. Instead it has: " + cashier.getTill(), 1010.0 == cashier.getTill());
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
	}
	
	
//	in the scenario that the restaurant doesn't have enough money, 
//	it goes into debt and will soon make the money back from selling food
	public void testPayingMarketScenarioNonnorm() {
		assertEquals("Till should have 1000. It is not.", cashier.getTill(), 1000.0);

		MarketCheck check = new MarketCheck(200.0, "Steak", 20, market);
		cashier.msgGiveBill(check);
		assertEquals("Cashier should have 1 check to pay. It doesn't.", cashier.getChecks().size(), 1);
		assertTrue("Cashier's check should have a state of NeedPaying. It doesn't.", cashier.getChecks().get(0).state == CheckState.NeedPaying);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's check should have a state of NeedPaying. It doesn't.", cashier.getChecks().get(0).state == CheckState.Paid);
		assertTrue("Market should have logged \"Received msgPayForOrder from cook. Money = $20\" but didn't. His log reads instead: "
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPayForOrder from cook. Money = $200"));
		assertTrue("Cashier till should be 980.0. Instead it's " + cashier.getTill(), 800.0 == cashier.getTill());
	}
	
	public void testPayingMarketScenarioRegular() {
		assertEquals("Till should be at 1000. It is not.", cashier.getTill(), 1000.0);
		cashier.setTill(1020);
		assertEquals("Till should be 1020. It is not.", cashier.getTill(), 1020.0);

		MarketCheck check = new MarketCheck(200.0, "Steak", 20, market);
		cashier.msgGiveBill(check);
		assertEquals("Cashier should have 1 check to pay. It doesn't.", cashier.getChecks().size(), 1);
		assertTrue("Cashier's check should have a state of NeedPaying. It doesn't.", cashier.getChecks().get(0).state == CheckState.NeedPaying);
		cashier.pickAndExecuteAnAction();
		assertFalse("Cashier's scheduler should have returned true (one action to do), but didn't.", cashier.pickAndExecuteAnAction());
		assertTrue("Cashier's check should have a state of NeedPaying. It doesn't.", cashier.getChecks().get(0).state == CheckState.Paid);
		assertTrue("Market should have logged \"Received msgPayForOrder from cook. Money = $20\" but didn't. His log reads instead: "
				+ market.log.getLastLoggedEvent().toString(), market.log.containsString("Received msgPayForOrder from cook. Money = $200"));
		assertTrue("Cashier till should be 820.0. Instead it's " + cashier.getTill(), 820.0 == cashier.getTill());
	}
}
