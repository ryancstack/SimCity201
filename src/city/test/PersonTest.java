package city.test;

import agent.Role;
import bank.BankTellerRole;
import junit.framework.*;
import city.PersonAgent.PersonState;
import city.test.mock.MockTransportation;
import city.PersonAgent;
import city.test.mock.*;
import city.helpers.*;



public class PersonTest extends TestCase {
	
	MockTransportation passenger;
	PersonAgent person;
	MockTransportation trole;

	public void setUp() throws Exception{
		super.setUp();	
		String jobLocation = "Bank";
		String house = "House5";
		String name = "JUNITTestPersonTeller";
		int aggroLevel = 3;
		MockRole transport = new MockRole("jobLocation");
		MockRole job = new MockRole("jobLocation");
		person = new PersonAgent(job, jobLocation, house, name, 3);
			
	}	
	
	//wake up, decide to eat in or out, assign role and 
	public void testCase1(){
		//preconditions:
		
		//Waking up:
		person.msgWakeUp();
		assertFalse("person's hasWorked should be false but is true", person.hasWorked);
		assertTrue("person's state should be wantfood but isn't", person.getPersonState() == PersonState.WantFood);
		assertTrue("Person should complete one action to decide food.", person.pickAndExecuteAnAction());
		assertTrue("Person should now have decided eat in or out.", person.getPersonState() == PersonState.GoOutEat || person.getPersonState() == PersonState.CookHome);

	}
}
