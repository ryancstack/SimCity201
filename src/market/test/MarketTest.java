package market.test;

import java.util.HashMap;
import java.util.Map;

import market.MarketWorkerRole;
import market.MarketWorkerRole.orderState;
import market.test.mock.*;
import junit.framework.*;

public class MarketTest extends TestCase {

	//these are instantiated for each test separately via the setUp() method.
	MarketWorkerRole market;
	MockMarketCustomer customer1;
	MockMarketCustomer customer2;
	MockPerson person;
	MockCook cook;
	MockCashier cashier;
	
	Map<String, Integer> groceryList;
	Map<String, Integer> cantFillGroceryList;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
		market = new MarketWorkerRole();
		person = new MockPerson("person");
		market.setPerson(person);
		customer1 = new MockMarketCustomer("mockcustomer1");
		customer2 = new MockMarketCustomer("mockcustomer2");
		cook = new MockCook("mockcook");
		cashier = new MockCashier("mockcashier");
		
		groceryList = new HashMap<String, Integer>();
		groceryList.put("Steak", 1);
		groceryList.put("Chicken", 5);
		cantFillGroceryList = new HashMap<String, Integer>();
		cantFillGroceryList.put("Steak", 50);
	}	

	
	public void testOneNormativeMarketOrder(){

		//Check Preconditions----------------------------------------------------------------
		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);
		assertEquals("Market should have 4 kinds of food. It doesn't", market.getInventory().size(), 4);
		
		assertEquals("Market should have an empty event log. Instead, the Market's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("MockMarketCustomer should have an empty event log. Instead, the event log reads: "
						+ customer1.log.toString(), 0, customer1.log.size());
		
		assertFalse("Market scheduler should've returned false. It didn't",
				market.pickAndExecuteAnAction());

		//Step 1-----------------------------------------------------------------------------
		market.msgGetGroceries(customer1, groceryList);

		//Check Postconditions of Step 1/Preconditions of Step 2-----------------------------
		assertEquals("Market should have one logged event. Instead, the Market's event log reads: "
				+ market.log.toString(), 1, market.log.size());
		
		assertEquals("Market should have 1 order. It doesn't", market.getMyOrders().size(), 1);
		
		assertEquals("Order should have state 'Ordered'. State is instead: " + 
						market.getMyOrders().get(0).getState(), 
						market.getMyOrders().get(0).getState(), orderState.Ordered);		
		assertEquals("Order should have MockMarketCustomer as Customer. It doesn't.", 
						market.getMyOrders().get(0).getCustomer(), customer1);
		assertEquals("Order should have groceryList as order. It doesn't.", 
						market.getMyOrders().get(0).getGroceryList(), groceryList);

		//Step 2-----------------------------------------------------------------------------
		market.msgActionComplete();
		market.msgActionComplete();
		market.msgActionComplete();
		
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());

		//Check Postconditions of Step 2/Preconditions of Step 3-----------------------------
		assertEquals("Market should have 2 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 2, market.log.size());
		
		assertEquals("Order should have state 'Filled'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Filled);	
		assertEquals("Order should have price of $7.00. Price is instead: $" + 
				market.getMyOrders().get(0).getPrice(), 
				market.getMyOrders().get(0).getPrice(), 7.00);	
		
		assertEquals("Order should have retrieved all groceries. It didn't.", 
				market.getMyOrders().get(0).getRetrievedGroceries(), groceryList);
		
		//Step 3-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 3/Preconditions of Step 4-----------------------------
		assertEquals("Market should have 3 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 3, market.log.size());
		
		assertEquals("Order should have state 'Billed'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Billed);	
		
		assertEquals("MarketCustomer should have 1 logged event. Instead, the event log reads: "
				+ customer1.log.toString(), 1, customer1.log.size());
		assertEquals("MarketCustomer should have been charged $7.00. Instead, the price was: $"
				+ customer1.price, customer1.price, 7.00);
		
		//Step 4-----------------------------------------------------------------------------
		market.msgHereIsMoney(customer1, 7.00);
		
		//Check Postconditions of Step 4/Preconditions of Step 5-----------------------------
		assertEquals("Market should have 4 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 4, market.log.size());
		
		assertEquals("Order should have state 'Paid'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Paid);	
		
		//Step 5-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 5-----------------------------------------------------
		assertEquals("Market should have 5 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 5, market.log.size());

		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);

		assertEquals("MarketCustomer should have 2 logged events. Instead, the event log reads: "
						+ customer1.log.toString(), 2, customer1.log.size());
		assertEquals("MarketCustomer should have received groceries. It didn't",
						customer1.groceries, groceryList);
		
	}	
	
	public void testTwoNormativeMarketOrders(){

		//Check Preconditions----------------------------------------------------------------
		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);
		assertEquals("Market should have 4 kinds of food. It doesn't", market.getInventory().size(), 4);
		
		assertEquals("Market should have an empty event log. Instead, the Market's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("MockMarketCustomer should have an empty event log. Instead, the event log reads: "
						+ customer1.log.toString(), 0, customer1.log.size());
		
		assertFalse("Market scheduler should've returned false. It didn't",
				market.pickAndExecuteAnAction());

		//Step 1-----------------------------------------------------------------------------
		market.msgGetGroceries(customer1, groceryList);

		//Check Postconditions of Step 1/Preconditions of Step 2-----------------------------
		assertEquals("Market should have one logged event. Instead, the Market's event log reads: "
				+ market.log.toString(), 1, market.log.size());
		
		assertEquals("Market should have 1 order. It doesn't", market.getMyOrders().size(), 1);
		
		assertEquals("Order should have state 'Ordered'. State is instead: " + 
						market.getMyOrders().get(0).getState(), 
						market.getMyOrders().get(0).getState(), orderState.Ordered);		
		assertEquals("Order should have MockMarketCustomer as Customer. It doesn't.", 
						market.getMyOrders().get(0).getCustomer(), customer1);
		assertEquals("Order should have groceryList as order. It doesn't.", 
						market.getMyOrders().get(0).getGroceryList(), groceryList);
		
		//Step 2-----------------------------------------------------------------------------
		market.msgGetGroceries(customer2, groceryList);

		//Check Postconditions of Step 2/Preconditions of Step 3-----------------------------
		assertEquals("Market should have one logged event. Instead, the Market's event log reads: "
				+ market.log.toString(), 2, market.log.size());
		
		assertEquals("Market should have 2 orders. It doesn't", market.getMyOrders().size(), 2);
		
		assertEquals("Order 2 should have state 'Ordered'. State is instead: " + 
						market.getMyOrders().get(1).getState(), 
						market.getMyOrders().get(1).getState(), orderState.Ordered);		
		assertEquals("Order 2 should have MockMarketCustomer2 as Customer. It doesn't.", 
						market.getMyOrders().get(1).getCustomer(), customer2);
		assertEquals("Order 2 should have groceryList as order. It doesn't.", 
						market.getMyOrders().get(1).getGroceryList(), groceryList);

		//Step 3-----------------------------------------------------------------------------
		market.msgActionComplete();
		market.msgActionComplete();
		market.msgActionComplete();
		
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());

		//Check Postconditions of Step 3/Preconditions of Step 4-----------------------------
		assertEquals("Market should have 3 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 3, market.log.size());
		
		assertEquals("Order 1 should have state 'Filled'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Filled);	
		assertEquals("Order 1 should have price of $7.00. Price is instead: $" + 
				market.getMyOrders().get(0).getPrice(), 
				market.getMyOrders().get(0).getPrice(), 7.00);	
		
		assertEquals("Order 1 should have retrieved all groceries. It didn't.", 
				market.getMyOrders().get(0).getRetrievedGroceries(), groceryList);

		//Step 4-----------------------------------------------------------------------------
		market.msgActionComplete();
		market.msgActionComplete();
		market.msgActionComplete();
		
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());

		//Check Postconditions of Step 4/Preconditions of Step 5-----------------------------
		assertEquals("Market should have 4 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 4, market.log.size());
		
		assertEquals("Order 2 should have state 'Filled'. State is instead: " + 
				market.getMyOrders().get(1).getState(), 
				market.getMyOrders().get(1).getState(), orderState.Filled);	
		assertEquals("Order 2 should have price of $7.00. Price is instead: $" + 
				market.getMyOrders().get(1).getPrice(), 
				market.getMyOrders().get(1).getPrice(), 7.00);	
		
		assertEquals("Order 2 should have retrieved all groceries. It didn't.", 
				market.getMyOrders().get(1).getRetrievedGroceries(), groceryList);
		
		//Step 5-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 5/Preconditions of Step 6-----------------------------
		assertEquals("Market should have 5 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 5, market.log.size());
		
		assertEquals("Order 1 should have state 'Billed'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Billed);	
		
		assertEquals("MarketCustomer 1 should have 1 logged event. Instead, the event log reads: "
				+ customer1.log.toString(), 1, customer1.log.size());
		assertEquals("MarketCustomer 1 should have been charged $7.00. Instead, the price was: $"
				+ customer1.price, customer1.price, 7.00);
		
		//Step 6-----------------------------------------------------------------------------	
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 6/Preconditions of Step 7-----------------------------
		assertEquals("Market should have 6 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 6, market.log.size());
		
		assertEquals("Order 2 should have state 'Billed'. State is instead: " + 
				market.getMyOrders().get(1).getState(), 
				market.getMyOrders().get(1).getState(), orderState.Billed);	
		
		assertEquals("MarketCustomer 2 should have 1 logged event. Instead, the event log reads: "
				+ customer2.log.toString(), 1, customer2.log.size());
		assertEquals("MarketCustomer 2 should have been charged $7.00. Instead, the price was: $"
				+ customer2.price, customer2.price, 7.00);
		
		//Step 7-----------------------------------------------------------------------------
		market.msgHereIsMoney(customer1, 7.00);
		
		//Check Postconditions of Step 7/Preconditions of Step 8-----------------------------
		assertEquals("Market should have 7 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 7, market.log.size());
		
		assertEquals("Order 1 should have state 'Paid'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Paid);	
		
		//Step 8-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 8/Preconditions of Step 9-----------------------------
		assertEquals("Market should have 8 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 8, market.log.size());

		assertEquals("Market should have 1 orders. It doesn't", market.getMyOrders().size(), 1);

		assertEquals("MarketCustomer 1 should have 2 logged events. Instead, the event log reads: "
						+ customer1.log.toString(), 2, customer1.log.size());
		assertEquals("MarketCustomer 1 should have received groceries. It didn't",
						customer1.groceries, groceryList);
		//Step 9-----------------------------------------------------------------------------
		market.msgHereIsMoney(customer2, 7.00);
		
		//Check Postconditions of Step 9/Preconditions of Step 10-----------------------------
		assertEquals("Market should have 9 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 9, market.log.size());
		
		assertEquals("Order 2 should have state 'Paid'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Paid);	
		
		//Step 10----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 10----------------------------------------------------
		assertEquals("Market should have 10 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 10, market.log.size());

		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);

		assertEquals("MarketCustomer 2 should have 2 logged events. Instead, the event log reads: "
						+ customer2.log.toString(), 2, customer2.log.size());
		assertEquals("MarketCustomer 2 should have received groceries. It didn't",
						customer2.groceries, groceryList);
		
	}	

	public void testCustomerCantPayForOrder(){

		//Check Preconditions----------------------------------------------------------------
		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);
		assertEquals("Market should have 4 kinds of food. It doesn't", market.getInventory().size(), 4);
		
		assertEquals("Market should have an empty event log. Instead, the Market's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("MockMarketCustomer should have an empty event log. Instead, the event log reads: "
						+ customer1.log.toString(), 0, customer1.log.size());
		
		assertFalse("Market scheduler should've returned false. It didn't",
				market.pickAndExecuteAnAction());

		//Step 1-----------------------------------------------------------------------------
		market.msgGetGroceries(customer1, groceryList);

		//Check Postconditions of Step 1/Preconditions of Step 2-----------------------------
		assertEquals("Market should have one logged event. Instead, the Market's event log reads: "
				+ market.log.toString(), 1, market.log.size());
		
		assertEquals("Market should have 1 order. It doesn't", market.getMyOrders().size(), 1);
		
		assertEquals("Order should have state 'Ordered'. State is instead: " + 
						market.getMyOrders().get(0).getState(), 
						market.getMyOrders().get(0).getState(), orderState.Ordered);		
		assertEquals("Order should have MockMarketCustomer as Customer. It doesn't.", 
						market.getMyOrders().get(0).getCustomer(), customer1);
		assertEquals("Order should have groceryList as order. It doesn't.", 
						market.getMyOrders().get(0).getGroceryList(), groceryList);

		//Step 2-----------------------------------------------------------------------------
		market.msgActionComplete();
		market.msgActionComplete();
		market.msgActionComplete();
		
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());

		//Check Postconditions of Step 2/Preconditions of Step 3-----------------------------
		assertEquals("Market should have 2 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 2, market.log.size());
		
		assertEquals("Order should have state 'Filled'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Filled);	
		assertEquals("Order should have price of $7.00. Price is instead: $" + 
				market.getMyOrders().get(0).getPrice(), 
				market.getMyOrders().get(0).getPrice(), 7.00);	
		
		assertEquals("Order should have retrieved all groceries. It didn't.", 
				market.getMyOrders().get(0).getRetrievedGroceries(), groceryList);
		
		//Step 3-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 3/Preconditions of Step 4-----------------------------
		assertEquals("Market should have 3 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 3, market.log.size());
		
		assertEquals("Order should have state 'Billed'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Billed);	
		
		assertEquals("MarketCustomer should have 1 logged event. Instead, the event log reads: "
				+ customer1.log.toString(), 1, customer1.log.size());
		assertEquals("MarketCustomer should have been charged $7.00. Instead, the price was: $"
				+ customer1.price, customer1.price, 7.00);
		
		//Step 4-----------------------------------------------------------------------------
		market.msgCantAffordGroceries(customer1);
		
		//Check Postconditions of Step 4/Preconditions of Step 5-----------------------------
		assertEquals("Market should have 4 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 4, market.log.size());
		
		assertEquals("Order should have state 'CantPay'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.CantPay);	
		
		//Step 5-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 5-----------------------------------------------------
		assertEquals("Market should have 5 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 5, market.log.size());

		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);

		assertEquals("MarketCustomer should have 1 logged events. Instead, the event log reads: "
						+ customer1.log.toString(), 1, customer1.log.size());
		assertEquals("MarketCustomer should not have received groceries. It didn't",
						customer1.groceries, null);
		
	}	

	public void testMarketCantFillOrder(){

		//Check Preconditions----------------------------------------------------------------
		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);
		assertEquals("Market should have 4 kinds of food. It doesn't", market.getInventory().size(), 4);
		
		assertEquals("Market should have an empty event log. Instead, the Market's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("MockMarketCustomer should have an empty event log. Instead, the event log reads: "
						+ customer1.log.toString(), 0, customer1.log.size());
		
		assertFalse("Market scheduler should've returned false. It didn't",
				market.pickAndExecuteAnAction());

		//Step 1-----------------------------------------------------------------------------
		market.msgGetGroceries(customer1, cantFillGroceryList);

		//Check Postconditions of Step 1/Preconditions of Step 2-----------------------------
		assertEquals("Market should have one logged event. Instead, the Market's event log reads: "
				+ market.log.toString(), 1, market.log.size());
		
		assertEquals("Market should have 1 order. It doesn't", market.getMyOrders().size(), 1);
		
		assertEquals("Order should have state 'Ordered'. State is instead: " + 
						market.getMyOrders().get(0).getState(), 
						market.getMyOrders().get(0).getState(), orderState.Ordered);		
		assertEquals("Order should have MockMarketCustomer as Customer. It doesn't.", 
						market.getMyOrders().get(0).getCustomer(), customer1);
		assertEquals("Order should have cantFillGroceryList as order. It doesn't.", 
						market.getMyOrders().get(0).getGroceryList(), cantFillGroceryList);

		//Step 2-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());

		//Check Postconditions of Step 2/Preconditions of Step 3-----------------------------
		assertEquals("Market should have 2 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 2, market.log.size());
		
		assertEquals("Order should have state 'CantFill'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.CantFill);	
		assertEquals("Order should have price of $0.00. Price is instead: $" + 
				market.getMyOrders().get(0).getPrice(), 
				market.getMyOrders().get(0).getPrice(), 0.00);	
		
		assertTrue("retrievedGroceries should be empty. It isn't.", 
				market.getMyOrders().get(0).getRetrievedGroceries().isEmpty());
		
		//Step 3-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 3/Preconditions of Step 4-----------------------------
		assertEquals("Market should have 3 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 3, market.log.size());
		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);

		assertEquals("MarketCustomer should have 1 logged events. Instead, the event log reads: "
						+ customer1.log.toString(), 1, customer1.log.size());
		assertEquals("MarketCustomer should not have received groceries. It didn't",
						customer1.groceries, null);
		
	}	

	public void testMarketFinishingWork(){

		//Check Preconditions----------------------------------------------------------------
		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);
		assertEquals("Market should have 4 kinds of food. It doesn't", market.getInventory().size(), 4);
		
		assertEquals("Market should have an empty event log. Instead, the Market's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("MockMarketCustomer should have an empty event log. Instead, the event log reads: "
						+ customer1.log.toString(), 0, customer1.log.size());
		
		assertFalse("Market scheduler should've returned false. It didn't",
				market.pickAndExecuteAnAction());

		//Step 1-----------------------------------------------------------------------------
		market.msgGetGroceries(customer1, groceryList);

		//Check Postconditions of Step 1/Preconditions of Step 2-----------------------------
		assertEquals("Market should have one logged event. Instead, the Market's event log reads: "
				+ market.log.toString(), 1, market.log.size());
		
		assertEquals("Market should have 1 order. It doesn't", market.getMyOrders().size(), 1);
		
		assertEquals("Order should have state 'Ordered'. State is instead: " + 
						market.getMyOrders().get(0).getState(), 
						market.getMyOrders().get(0).getState(), orderState.Ordered);		
		assertEquals("Order should have MockMarketCustomer as Customer. It doesn't.", 
						market.getMyOrders().get(0).getCustomer(), customer1);
		assertEquals("Order should have groceryList as order. It doesn't.", 
						market.getMyOrders().get(0).getGroceryList(), groceryList);

		//Step 2-----------------------------------------------------------------------------
		market.msgActionComplete();
		market.msgActionComplete();
		market.msgActionComplete();
		
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());

		//Check Postconditions of Step 2/Preconditions of Step 3-----------------------------
		assertEquals("Market should have 2 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 2, market.log.size());
		
		assertEquals("Order 1 should have state 'Filled'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Filled);	
		assertEquals("Order 1 should have price of $7.00. Price is instead: $" + 
				market.getMyOrders().get(0).getPrice(), 
				market.getMyOrders().get(0).getPrice(), 7.00);	
		
		assertEquals("Order 1 should have retrieved all groceries. It didn't.", 
				market.getMyOrders().get(0).getRetrievedGroceries(), groceryList);
		
		//Step 3-----------------------------------------------------------------------------
		market.msgJobDone();
		
		//Check Postconditions of Step 3/Preconditions of Step 4-----------------------------
		assertEquals("Market should have 3 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 3, market.log.size());
		assertTrue("Market's jobDone boolean should be true. It isn't", market.getJobDone());

		
		//Step 4-----------------------------------------------------------------------------
		market.msgGetGroceries(customer2, groceryList);

		//Check Postconditions of Step 4/Preconditions of Step 5-----------------------------
		assertEquals("Market should have 4 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 4, market.log.size());
		
		assertEquals("Market should have 2 orders. It doesn't", market.getMyOrders().size(), 2);
		
		assertEquals("Order 2 should have state 'Ordered'. State is instead: " + 
						market.getMyOrders().get(1).getState(), 
						market.getMyOrders().get(1).getState(), orderState.Ordered);		
		assertEquals("Order 2 should have MockMarketCustomer2 as Customer. It doesn't.", 
						market.getMyOrders().get(1).getCustomer(), customer2);
		assertEquals("Order 2 should have groceryList as order. It doesn't.", 
						market.getMyOrders().get(1).getGroceryList(), groceryList);

		//Step 5-----------------------------------------------------------------------------	
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());

		//Check Postconditions of Step 5/Preconditions of Step 6-----------------------------
		assertEquals("Market should have 5 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 5, market.log.size());
		assertEquals("Market should have 1 order. It doesn't", market.getMyOrders().size(), 1);

		assertEquals("MockMarketCustomer 2 should have one logged event. Instead, the event log reads: "
						+ customer2.log.toString(), 1, customer2.log.size());
		assertEquals("MockMarketCustomer 2 should not have received groceries. It didn't",
						customer2.groceries, null);
		
		//Step 6-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 6/Preconditions of Step 7-----------------------------
		assertEquals("Market should have 6 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 6, market.log.size());
		
		assertEquals("Order 1 should have state 'Billed'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Billed);	
		
		assertEquals("MarketCustomer 1 should have 1 logged event. Instead, the event log reads: "
				+ customer1.log.toString(), 1, customer1.log.size());
		assertEquals("MarketCustomer 1 should have been charged $7.00. Instead, the price was: $"
				+ customer1.price, customer1.price, 7.00);
		
		//Step 7-----------------------------------------------------------------------------
		market.msgHereIsMoney(customer1, 7.00);
		
		//Check Postconditions of Step 7/Preconditions of Step 8-----------------------------
		assertEquals("Market should have 7 logged events. Instead, the Market's event log reads: "
				+ market.log.toString(), 7, market.log.size());
		
		assertEquals("Order 1 should have state 'Paid'. State is instead: " + 
				market.getMyOrders().get(0).getState(), 
				market.getMyOrders().get(0).getState(), orderState.Paid);	
		
		//Step 8-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 8/Preconditions of Step 9-----------------------------
		assertEquals("Market should have 8 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 8, market.log.size());

		assertEquals("Market should have 0 orders. It doesn't", market.getMyOrders().size(), 0);

		assertEquals("MarketCustomer 1 should have 2 logged events. Instead, the event log reads: "
						+ customer1.log.toString(), 2, customer1.log.size());
		assertEquals("MarketCustomer 1 should have received groceries. It didn't",
						customer1.groceries, groceryList);	
		
		//Step 9----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 10----------------------------------------------------
		assertEquals("Market should have 9 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 9, market.log.size());

		assertEquals("Person should have 2 logged events. Instead, the event log reads: "
						+ person.log.toString(), 2, person.log.size());
		assertEquals("Person should have correct funds. Instead, funds = $"
						+ person.funds, 7.00, person.funds);
		
	}	
	
	public void testOneNormativeRestaurantOrder(){

		//Check Preconditions----------------------------------------------------------------
		assertEquals("Market should have 0 RestaurantOrders. It doesn't", market.getMyRestaurantOrders().size(), 0);
		assertEquals("Market should have 4 kinds of food. It doesn't", market.getInventory().size(), 4);
		
		assertEquals("Market should have an empty event log. Instead, the Market's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("MockCook should have an empty event log. Instead, the event log reads: "
						+ cook.log.toString(), 0, cook.log.size());
		assertEquals("MockCashier should have an empty event log. Instead, the event log reads: "
						+ cashier.log.toString(), 0, cashier.log.size());
		
		assertFalse("Market scheduler should've returned false. It didn't",
						market.pickAndExecuteAnAction());

		//Step 1-----------------------------------------------------------------------------
		market.msgOrderFood(cook, cashier, "Steak");

		//Check Postconditions of Step 1/Preconditions of Step 2-----------------------------
		assertEquals("Market should have one logged event. Instead, the Market's event log reads: "
						+ market.log.toString(), 1, market.log.size());
		
		assertEquals("Market should have 1 RestaurantOrder. It doesn't", market.getMyRestaurantOrders().size(), 1);
		
		assertEquals("RestaurantOrder should have state 'Ordered'. State is instead: " + 
						market.getMyRestaurantOrders().get(0).getState(), 
						market.getMyRestaurantOrders().get(0).getState(), orderState.Ordered);		
		assertEquals("RestaurantOrder should have MockCook as Cook. It doesn't.", 
						market.getMyRestaurantOrders().get(0).getCook(), cook);
		assertEquals("RestaurantOrder should have correct choice as order. It doesn't.", 
						market.getMyRestaurantOrders().get(0).getChoice(), "Steak");

		//Step 2-----------------------------------------------------------------------------
		
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());

		//Check Postconditions of Step 2/Preconditions of Step 3-----------------------------
		assertEquals("Market should have 2 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 2, market.log.size());
		
		assertEquals("Order should have state 'Filled'. State is instead: " + 
						market.getMyRestaurantOrders().get(0).getState(), 
						market.getMyRestaurantOrders().get(0).getState(), orderState.Filled);
		
		assertEquals("Inventory of choice should be unchanged. Instead, inventory = "
						+ market.getInventory().get("Steak").getSupply(),
						market.getInventory().get("Steak").getSupply(), 10);
		
		//Step 3-----------------------------------------------------------------------------
		market.msgDeliverOrder(market.getMyRestaurantOrders().get(0));
		
		//Check Postconditions of Step 3/Preconditions of Step 4-----------------------------
		assertEquals("Market should have 3 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 3, market.log.size());
		
		assertEquals("RestaurantOrder should have state 'ReadyToDeliver'. State is instead: " + 
						market.getMyRestaurantOrders().get(0).getState(), 
						market.getMyRestaurantOrders().get(0).getState(), orderState.ReadyToDeliver);	
		
		//Step 4------------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());

		//Check Postconditions of Step 4/Preconditions of Step 5-----------------------------
		assertEquals("Market should have 4 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 4, market.log.size());
		
		assertEquals("Cook should have 1 logged event. Instead, the event log reads: "
						+ cook.log.toString(), 1, cook.log.size());
		assertEquals("Cook should have correct choice. Instead, choice is: "
						+ cook.choice, cook.choice, "Steak");
		assertEquals("Cook should have correct inventory. Instead, the inventory is: "
						+ cook.inventory, cook.inventory, 5);
		assertEquals("Cashier should have 1 logged event. Instead, the event log reads: "
						+ cashier.log.toString(), 1, cashier.log.size());
		
		assertEquals("RestaurantOrder should have correct price. Instead, price = $"
						+ market.getMyRestaurantOrders().get(0).getPrice(),
						market.getMyRestaurantOrders().get(0).getPrice(), 10.00);
		
		//Step 4-----------------------------------------------------------------------------
		market.msgPayForOrder(cashier, 10.00);
		
		//Check Postconditions of Step 4/Preconditions of Step 5-----------------------------
		assertEquals("Market should have 5 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 5, market.log.size());
		assertEquals("Market should have correct funds. Instead, funds = $" + market.getFunds(),
						market.getFunds(), 10.00);
		
		assertEquals("RestaurantOrder should have state 'Paid'. State is instead: " + 
						market.getMyRestaurantOrders().get(0).getState(), 
						market.getMyRestaurantOrders().get(0).getState(), orderState.Paid);	
		
		//Step 5-----------------------------------------------------------------------------
		assertTrue("Market scheduler should've returned true. It didn't",
						market.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 5-----------------------------------------------------
		assertEquals("Market should have 6 logged events. Instead, the Market's event log reads: "
						+ market.log.toString(), 6, market.log.size());

		assertEquals("Market should have 0 RestaurantOrders. It doesn't",
						market.getMyRestaurantOrders().size(), 0);		
	}	
}
