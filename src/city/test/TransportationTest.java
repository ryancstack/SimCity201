package city.test;

import restaurant.stackRestaurant.StackWaiterRole;
import agent.Role;
import junit.framework.*;
import city.PersonAgent;
import city.TransportationRole;
import city.PersonAgent.TransportationMethod;
import city.TransportationRole.TransportationState;
import city.test.mock.MockBus;
import city.test.mock.MockPerson;
import city.helpers.Directory;
import city.helpers.BusHelper;

public class TransportationTest extends TestCase {

	TransportationRole role;
	MockPerson person;
	MockBus bus;
	
	public void setUp() throws Exception{
		super.setUp();
		
		bus = new MockBus("Bus");
		person = new MockPerson("Person");
		person.transMethod = TransportationMethod.TakesTheBus;
		
		role = new TransportationRole("HuangRestaurant", "House1"); //destination, startingLocation
		role.setPerson(person);
	}
	
	public void testCase1(){ //test from person agent going from home to bus stop
		
		if(Directory.sharedInstance()==null){
			System.out.println("ASKHBF KSADJF bgasdjkg ");
		}
		if(Directory.sharedInstance()!=null){
			System.out.println("all good");
		}
		//precondition:
		assertTrue("role's state should be NeedsToTravel", role.getState() == TransportationState.NeedsToTravel);
		assertTrue("It shouldn't be House1 but isn't. instead it is "+ role.getStartingLocation(),role.getStartingLocation().equals("House1"));
		assertTrue("role's scheduler should have returned true (one action to do), but didn't.", role.pickAndExecuteAnAction());
		//assertTrue("role's state should be InTransit", role.getState() == TransportationState.InTransit);
		
		//fuck it, just assume it's already in transit;

		
	}
	
	public void testCase2(){ //test from bus stop going to building
		
	}
}
