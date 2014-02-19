package market.test;

import java.util.HashMap;
import java.util.Map;

import market.MarketCustomerRole;
import market.MarketCustomerRole.State;
import market.MarketCustomerRole.Event;
import market.test.mock.*;
import junit.framework.*;

public class MarketCustomerTest extends TestCase {

	//these are instantiated for each test separately via the setUp() method.
	MarketCustomerRole marketCustomer;
	MockMarket market;
	MockPerson person;
	
	MarketCustomerRole poorMarketCustomer;
	MockPerson poorPerson;
	
	MockMarket emptyMarket;
	
	Map<String, Integer> groceryList;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();		
	
		//Setup for Normative Scenario
		groceryList = new HashMap<String, Integer>();
		groceryList.put("Steak", 1);
		groceryList.put("Chicken", 5);
		
		marketCustomer = new MarketCustomerRole(groceryList);
		person = new MockPerson("person");
		person.groceryList = groceryList;
		person.funds = 10.00;
		
		marketCustomer.setPerson(person);
		market = new MockMarket("mockMarket");
		marketCustomer.setMarket(market);
		
		//Setup for Can't Pay Scenario
		poorMarketCustomer = new MarketCustomerRole(groceryList);
		poorPerson = new MockPerson("poorPerson");
		poorPerson.groceryList = groceryList;
		poorPerson.funds = 0.00;
		poorMarketCustomer.setPerson(poorPerson);
		poorMarketCustomer.setMarket(market);
		
		

	}	

	
	public void testNormativeMarketScenario(){

		//Check Preconditions----------------------------------------------------------------
		assertEquals("MarketCustomer should have an empty event log. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 0, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.DoingNothing);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.WantsGroceries);
		assertEquals("MarketCustomer should have reference to market. It doesn't",
						marketCustomer.getMarket(), market);
		
		assertEquals("Market should have an empty event log. Instead, the event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("Person should have an empty event log. Instead, the event log reads: "
						+ person.log.toString(), 0, person.log.size());

		//Step 1-----------------------------------------------------------------------------
		marketCustomer.msgActionComplete();
		
		assertTrue("MarketCustomer scheduler should've returned true. It didn't",
						marketCustomer.pickAndExecuteAnAction());

		//Check Postconditions of Step 1/Preconditions of Step 2-----------------------------
		assertEquals("MarketCustomer should have one logged event. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 1, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.WaitingForService);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.WantsGroceries);

		assertEquals("Market should have one logged event. Instead, the event log reads: "
						+ market.log.toString(), 1, market.log.size());
		assertEquals("Market should have received groceryList. It didn't.", 
						market.groceries, groceryList);

		//Step 2-----------------------------------------------------------------------------
		marketCustomer.msgHereIsBill(7.00);

		//Check Postconditions of Step 2/Preconditions of Step 3-----------------------------
		assertEquals("MarketCustomer should have 2 logged events. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 2, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.WaitingForService);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.GotBill);
		assertEquals("MarketCustomer should have correct orderCost. orderCost is instead: $" + 
						marketCustomer.getOrderCost(), marketCustomer.getOrderCost(), 7.00);
			
		//Step 3-----------------------------------------------------------------------------
		assertTrue("MarketCustomer scheduler should've returned true. It didn't",
						marketCustomer.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 3/Preconditions of Step 4-----------------------------
		assertEquals("MarketCustomer should have 3 logged events. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 3, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.Paying);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.GotBill);

		assertEquals("Person should have 1 logged events. Instead, the event log reads: "
						+ person.log.toString(), 1, person.log.size());
		assertEquals("Person should have correct funds. Instead, funds = $ "
						+ person.funds, 3.00, person.funds);
		
		assertEquals("Market should have 2 logged events. Instead, the event log reads: "
						+ market.log.toString(), 2, market.log.size());
		assertEquals("Market should have received correct amount of money. Instead, money = $"
						+ market.money, market.money, 7.00);
		
		//Step 4-----------------------------------------------------------------------------
		marketCustomer.msgHereAreYourGroceries(groceryList);
		
		//Check Postconditions of Step 4/Preconditions of Step 5-----------------------------
		assertEquals("MarketCustomer should have 4 logged events. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 4, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.Paying);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.GotGroceries);
		
		assertEquals("Person should have one logged event. Instead, the event log reads: "
						+ person.log.toString(), 1, person.log.size());
		
		//Step 5-----------------------------------------------------------------------------
		marketCustomer.msgActionComplete();
		
		assertTrue("MarketCustomer scheduler should've returned true. It didn't",
						marketCustomer.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 5-----------------------------------------------------
		assertEquals("MarketCustomer should have 5 logged events. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 5, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.DoneTransaction);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.GotGroceries);

		assertEquals("Person should have 3 logged events. Instead, the event log reads: "
						+ person.log.toString(), 3, person.log.size());
		assertTrue("Person should have cleared groceryList. It doesn't ",
				person.groceryList.isEmpty());
		
	}	

	public void testCantPayScenario(){

		//Check Preconditions----------------------------------------------------------------
		assertEquals("MarketCustomer should have an empty event log. Instead, the MarketCustomer's event log reads: "
						+ poorMarketCustomer.log.toString(), 0, poorMarketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						poorMarketCustomer.getState(), poorMarketCustomer.getState(), State.DoingNothing);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						poorMarketCustomer.getEvent(), poorMarketCustomer.getEvent(), Event.WantsGroceries);
		assertEquals("MarketCustomer should have reference to market. It doesn't",
						poorMarketCustomer.getMarket(), market);
		
		assertEquals("Market should have an empty event log. Instead, the event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("Person should have an empty event log. Instead, the event log reads: "
						+ poorPerson.log.toString(), 0, poorPerson.log.size());

		//Step 1-----------------------------------------------------------------------------
		poorMarketCustomer.msgActionComplete();
		
		assertTrue("MarketCustomer scheduler should've returned true. It didn't",
						poorMarketCustomer.pickAndExecuteAnAction());

		//Check Postconditions of Step 1/Preconditions of Step 2-----------------------------
		assertEquals("MarketCustomer should have one logged event. Instead, the MarketCustomer's event log reads: "
						+ poorMarketCustomer.log.toString(), 1, poorMarketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						poorMarketCustomer.getState(), poorMarketCustomer.getState(), State.WaitingForService);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						poorMarketCustomer.getEvent(), poorMarketCustomer.getEvent(), Event.WantsGroceries);

		assertEquals("Market should have one logged event. Instead, the event log reads: "
						+ market.log.toString(), 1, market.log.size());
		assertEquals("Market should have received groceryList. It didn't.", 
						market.groceries, groceryList);

		//Step 2-----------------------------------------------------------------------------
		poorMarketCustomer.msgHereIsBill(7.00);

		//Check Postconditions of Step 2/Preconditions of Step 3-----------------------------
		assertEquals("MarketCustomer should have 2 logged events. Instead, the MarketCustomer's event log reads: "
						+ poorMarketCustomer.log.toString(), 2, poorMarketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						poorMarketCustomer.getState(), poorMarketCustomer.getState(), State.WaitingForService);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						poorMarketCustomer.getEvent(), poorMarketCustomer.getEvent(), Event.GotBill);
		assertEquals("MarketCustomer should have correct orderCost. orderCost is instead: $" + 
						poorMarketCustomer.getOrderCost(), poorMarketCustomer.getOrderCost(), 7.00);
			
		//Step 3-----------------------------------------------------------------------------
		assertTrue("MarketCustomer scheduler should've returned true. It didn't",
						poorMarketCustomer.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 3/Preconditions of Step 4-----------------------------
		assertEquals("MarketCustomer should have 3 logged events. Instead, the MarketCustomer's event log reads: "
						+ poorMarketCustomer.log.toString(), 3, poorMarketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						poorMarketCustomer.getState(), poorMarketCustomer.getState(), State.CantPay);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						poorMarketCustomer.getEvent(), poorMarketCustomer.getEvent(), Event.GotBill);

		assertEquals("Person should have 0 logged events. Instead, the event log reads: "
						+ poorPerson.log.toString(), 0, poorPerson.log.size());
		assertEquals("Person should have correct funds. Instead, funds = $ "
						+ poorPerson.funds, 0.00, poorPerson.funds);
		
		assertEquals("Market should have 2 logged events. Instead, the event log reads: "
						+ market.log.toString(), 2, market.log.size());
		
		//Step 4-----------------------------------------------------------------------------
		poorMarketCustomer.msgActionComplete();
		
		assertTrue("MarketCustomer scheduler should've returned true. It didn't",
						poorMarketCustomer.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 5-----------------------------------------------------
		assertEquals("MarketCustomer should have 4 logged events. Instead, the MarketCustomer's event log reads: "
						+ poorMarketCustomer.log.toString(), 4, poorMarketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						poorMarketCustomer.getState(), poorMarketCustomer.getState(), State.DoneTransaction);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						poorMarketCustomer.getEvent(), poorMarketCustomer.getEvent(), Event.GotBill);

		assertEquals("Person should have 2 logged event. Instead, the event log reads: "
						+ poorPerson.log.toString(), 2, poorPerson.log.size());	
		assertEquals("Person should not have cleared groceryList. It did.",
						person.groceryList, groceryList);	
	}	
	
	public void testMarketEmptyScenario(){

		//Check Preconditions----------------------------------------------------------------
		assertEquals("MarketCustomer should have an empty event log. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 0, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.DoingNothing);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.WantsGroceries);
		assertEquals("MarketCustomer should have reference to market. It doesn't",
						marketCustomer.getMarket(), market);
		
		assertEquals("Market should have an empty event log. Instead, the event log reads: "
						+ market.log.toString(), 0, market.log.size());
		assertEquals("Person should have an empty event log. Instead, the event log reads: "
						+ person.log.toString(), 0, person.log.size());

		//Step 1-----------------------------------------------------------------------------
		marketCustomer.msgActionComplete();
		
		assertTrue("MarketCustomer scheduler should've returned true. It didn't",
						marketCustomer.pickAndExecuteAnAction());

		//Check Postconditions of Step 1/Preconditions of Step 2-----------------------------
		assertEquals("MarketCustomer should have one logged event. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 1, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.WaitingForService);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.WantsGroceries);

		assertEquals("Market should have one logged event. Instead, the event log reads: "
						+ market.log.toString(), 1, market.log.size());
		assertEquals("Market should have received groceryList. It didn't.", 
						market.groceries, groceryList);

		//Step 2-----------------------------------------------------------------------------
		marketCustomer.msgCantFillOrder(groceryList);

		//Check Postconditions of Step 2/Preconditions of Step 3-----------------------------
		assertEquals("MarketCustomer should have 2 logged events. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 2, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoingNothing'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.WaitingForService);
		assertEquals("MarketCustomer should have event 'WantsGroceries'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.TurnedAway);
		assertEquals("MarketCustomer should have no orderCost. orderCost is instead: $" + 
						marketCustomer.getOrderCost(), marketCustomer.getOrderCost(), 0.00);
			
		//Step 3-----------------------------------------------------------------------------
		marketCustomer.msgActionComplete();
		
		assertTrue("MarketCustomer scheduler should've returned true. It didn't",
						marketCustomer.pickAndExecuteAnAction());
		
		//Check Postconditions of Step 3-----------------------------------------------------
		assertEquals("MarketCustomer should have 3 logged events. Instead, the MarketCustomer's event log reads: "
						+ marketCustomer.log.toString(), 3, marketCustomer.log.size());
		assertEquals("MarketCustomer should have state 'DoneTransaction'. State is instead: " + 
						marketCustomer.getState(), marketCustomer.getState(), State.DoneTransaction);
		assertEquals("MarketCustomer should have event 'TurnedAway'. Event is instead: " + 
						marketCustomer.getEvent(), marketCustomer.getEvent(), Event.TurnedAway);

		assertEquals("Person should have 2 logged event. Instead, the event log reads: "
						+ person.log.toString(), 2, person.log.size());
		assertEquals("Person should not have cleared groceryList. It did.",
						person.groceryList, groceryList);
		
	}	

}
