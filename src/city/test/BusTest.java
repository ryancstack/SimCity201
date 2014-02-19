package city.test;

import city.interfaces.Bus;
import city.helpers.BusHelper;
import city.BusAgent;
import city.BusAgent.Event;
import city.BusAgent.State;
import city.test.mock.MockTransportation;
import junit.framework.*;

public class BusTest extends TestCase{
	
	BusAgent bus;
	MockTransportation passenger1;
	MockTransportation passenger2;
	MockTransportation passenger3;	
	
	public void setUp() throws Exception{
		super.setUp();		
		bus = new BusAgent(1);
		bus.state= State.driving;
		passenger1= new MockTransportation("Person 1");
		passenger2= new MockTransportation("Person 2");
		passenger3= new MockTransportation("Person 3");		
	}	
	
	/*
	 * Test for one passenger's full ride to the next stop.
	 */
	public void testOneFullPassenger(){
	//preconditions: 
		assertTrue("should be no passengers at stop 1, but there is, ", BusHelper.sharedInstance().getWaitingPassengersAtStop1().isEmpty());
		assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());
		assertTrue("bus's state should be driving but isn't", bus.state==State.driving);
	
	//Step 1: adding customer to bus stop
		BusHelper.sharedInstance().getWaitingPassengersAtStop1().add(passenger1);
		assertEquals("should be 1 passenger at stop 1, but there are "+BusHelper.sharedInstance().getWaitingPassengersAtStop1().size(), 1,BusHelper.sharedInstance().getWaitingPassengersAtStop1().size());	
		
	//Step 2: bus arrives at stop and should stop accordingly
		bus.msgAtStopOne();
		assertTrue("bus event should record reachedStop, but doesn't", bus.event==Event.reachedStop);
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record stopping, but doesn't", bus.state==State.stopping);
		assertTrue("bus event should record stopped after running scheduler", bus.event==Event.stopped);
	
	//Step 3: notifying passengers on board to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingToAlight after running scheduler", bus.state==State.notifyingPassengersToAlightBus);
		assertTrue("bus event should record notifiedToAlight after running scheduler", bus.event==Event.notifiedPassengersToAlightBus);
	
	//Step 4: waiting for passengers to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForAlighting after running scheduler", bus.state==State.waitForAlighting);
		bus.msgChangeEventToPassengersAlighted();
		assertTrue("bus event should record passengersAlighted after running scheduler", bus.event==Event.passengersAlighted);
		assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());
		
	//Step 5: notifying passengers to board bus
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingPassengersToBoardBus after running scheduler", bus.state==State.notifyingPassengersToBoardBus);
		assertTrue("bus event should record notifiedPassengersToBoardBus after running scheduler", bus.event==Event.notifiedPassengersToBoardBus);
		assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());
			
	//Step 6: passenger boards bus
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForBoarding after running scheduler", bus.state==State.waitForBoarding);
		bus.msgChangeEventToPassengersBoarded();
		assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
		bus.msgBoardingBus(passenger1);
		BusHelper.sharedInstance().getWaitingPassengersAtStop1().remove(passenger1);
		assertEquals("passengersOnBoard should have one passenger, but it has "+ bus.passengersOnBoard.size(), 1, bus.passengersOnBoard.size());
	
	//Step 7: bus continues driving
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record driving after running scheduler", bus.state==State.driving);
		assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
		assertEquals("passengersOnBoard should have one passenger, but it has "+ bus.passengersOnBoard.size(), 1, bus.passengersOnBoard.size());

	//Step 8: bus arrives at stop and should stop accordingly
		bus.msgAtStopTwo();
		assertTrue("bus event should record reachedStop, but doesn't", bus.event==Event.reachedStop);
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record stopping, but doesn't", bus.state==State.stopping);
		assertTrue("bus event should record stopped after running scheduler", bus.event==Event.stopped);
			
	//Step 9: notifying passengers on board to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingToAlight after running scheduler", bus.state==State.notifyingPassengersToAlightBus);
		assertTrue("Pssgr should have logged \"Notified passenger that bus has arrived at stop2\" but didn't. His log reads instead: " 
				+ passenger1.log.getLastLoggedEvent().toString(), passenger1.log.containsString("Notified passenger that bus has arrived at stop2"));
		assertTrue("bus event should record notifiedToAlight after running scheduler", bus.event==Event.notifiedPassengersToAlightBus);
			
	//Step 10: waiting for passengers to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForAlighting after running scheduler", bus.state==State.waitForAlighting);
		bus.msgLeavingBus(passenger1);
		bus.msgLeavingBus(passenger2);
		bus.msgLeavingBus(passenger3);
		bus.msgChangeEventToPassengersAlighted();
		assertTrue("bus event should record passengersAlighted after running scheduler", bus.event==Event.passengersAlighted);
		assertTrue("passengersOnBoard should be empty, but has "+ bus.passengersOnBoard.size(), bus.passengersOnBoard.isEmpty());
	}
	
	/* 
	 * Test for multiple passengers leaving from the same stop to the same destination
	 */
	public void testMultiplePassengersSameStopAndDestination(){
		//preconditions: 
				assertTrue("should be no passengers at stop 1, but there is, ", BusHelper.sharedInstance().getWaitingPassengersAtStop1().isEmpty());
				assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());
				assertTrue("bus's state should be driving but isn't", bus.state==State.driving);
			
			//Step 1: adding customer to bus stop
				BusHelper.sharedInstance().getWaitingPassengersAtStop1().add(passenger1);
				BusHelper.sharedInstance().getWaitingPassengersAtStop1().add(passenger2);
				BusHelper.sharedInstance().getWaitingPassengersAtStop1().add(passenger3);
				assertEquals("should be 3 passengers at stop 1, but there are "+BusHelper.sharedInstance().getWaitingPassengersAtStop1().size(), 3,BusHelper.sharedInstance().getWaitingPassengersAtStop1().size());	
				
			//Step 2: bus arrives at stop and should stop accordingly
				bus.msgAtStopOne();
				assertTrue("bus event should record reachedStop, but doesn't", bus.event==Event.reachedStop);
				assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
				assertTrue("bus state should record stopping, but doesn't", bus.state==State.stopping);
				assertTrue("bus event should record stopped after running scheduler", bus.event==Event.stopped);
			
			//Step 3: notifying passengers on board to alight
				assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
				assertTrue("bus state should record notifyingToAlight after running scheduler", bus.state==State.notifyingPassengersToAlightBus);
				assertTrue("bus event should record notifiedToAlight after running scheduler", bus.event==Event.notifiedPassengersToAlightBus);
			
			//Step 4: waiting for passengers to alight
				assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
				assertTrue("bus state should record waitForAlighting after running scheduler", bus.state==State.waitForAlighting);
				bus.msgChangeEventToPassengersAlighted();
				assertTrue("bus event should record passengersAlighted after running scheduler", bus.event==Event.passengersAlighted);
				assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());
				
			//Step 5: notifying passengers to board bus
				assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
				assertTrue("bus state should record notifyingPassengersToBoardBus after running scheduler", bus.state==State.notifyingPassengersToBoardBus);
				assertTrue("bus event should record notifiedPassengersToBoardBus after running scheduler", bus.event==Event.notifiedPassengersToBoardBus);
				assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());
					
			//Step 6: passenger boards bus
				assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
				assertTrue("bus state should record waitForBoarding after running scheduler", bus.state==State.waitForBoarding);
				bus.msgChangeEventToPassengersBoarded();
				assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
				bus.msgBoardingBus(passenger1);
				bus.msgBoardingBus(passenger2);
				bus.msgBoardingBus(passenger3);
				BusHelper.sharedInstance().getWaitingPassengersAtStop1().remove(passenger1);
				BusHelper.sharedInstance().getWaitingPassengersAtStop1().remove(passenger2);
				BusHelper.sharedInstance().getWaitingPassengersAtStop1().remove(passenger3);
				assertEquals("passengersOnBoard should have 3 passengers, but it has "+ bus.passengersOnBoard.size(), 3, bus.passengersOnBoard.size());
			
			//Step 7: bus continues driving
				assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
				assertTrue("bus state should record driving after running scheduler", bus.state==State.driving);
				assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
				assertEquals("passengersOnBoard should have 3 passengers, but it has "+ bus.passengersOnBoard.size(), 3, bus.passengersOnBoard.size());

			//Step 8: bus arrives at stop and should stop accordingly
				bus.msgAtStopTwo();
				assertTrue("bus event should record reachedStop, but doesn't", bus.event==Event.reachedStop);
				assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
				assertTrue("bus state should record stopping, but doesn't", bus.state==State.stopping);
				assertTrue("bus event should record stopped after running scheduler", bus.event==Event.stopped);
					
			//Step 9: notifying passengers on board to alight
				assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
				assertTrue("bus state should record notifyingToAlight after running scheduler", bus.state==State.notifyingPassengersToAlightBus);
				assertTrue("Pssgr should have logged \"Notified passenger that bus has arrived at stop2\" but didn't. His log reads instead: " 
						+ passenger1.log.getLastLoggedEvent().toString(), passenger1.log.containsString("Notified passenger that bus has arrived at stop2"));
				assertTrue("bus event should record notifiedToAlight after running scheduler", bus.event==Event.notifiedPassengersToAlightBus);
					
			//Step 10: waiting for passengers to alight
				assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
				assertTrue("bus state should record waitForAlighting after running scheduler", bus.state==State.waitForAlighting);
				bus.msgLeavingBus(passenger1);
				bus.msgLeavingBus(passenger2);
				bus.msgLeavingBus(passenger3);
				bus.msgChangeEventToPassengersAlighted();
				assertTrue("bus event should record passengersAlighted after running scheduler", bus.event==Event.passengersAlighted);
				assertTrue("passengersOnBoard should be empty, but has "+ bus.passengersOnBoard.size(), bus.passengersOnBoard.isEmpty());
	}
	
	/* Test for multiple passengers coming from different stops and going to different destinations. Includes 2 people who start
	 * at different stops and end at the same stop.
	 * at Stop1, p1 gets on.
	 * at Stop2, p2 gets on. 
	 * at Stop3, p3 gets on, p1 gets off.
	 * at Stop4, p2 and p4 get off.
	 */
	
	public void testMultiplePassengersDifferentStopsAndDestinations(){
	//preconditions: 
		assertTrue("should be no passengers at stop 1, but there is, ", BusHelper.sharedInstance().getWaitingPassengersAtStop1().isEmpty());
		assertTrue("should be no passengers at stop 2, but there is, ", BusHelper.sharedInstance().getWaitingPassengersAtStop2().isEmpty());
		assertTrue("should be no passengers at stop 3, but there is, ", BusHelper.sharedInstance().getWaitingPassengersAtStop3().isEmpty());
		assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());
		assertTrue("bus's state should be driving but isn't", bus.state==State.driving);
	
	//Step 1: adding customer to bus stop
		BusHelper.sharedInstance().getWaitingPassengersAtStop1().add(passenger1);
		BusHelper.sharedInstance().getWaitingPassengersAtStop2().add(passenger2);
		BusHelper.sharedInstance().getWaitingPassengersAtStop3().add(passenger3);
		assertEquals("should be 1 passengers at stop 1, but there are "+BusHelper.sharedInstance().getWaitingPassengersAtStop1().size(), 1,BusHelper.sharedInstance().getWaitingPassengersAtStop1().size());	
		assertEquals("should be 1 passengers at stop 2, but there are "+BusHelper.sharedInstance().getWaitingPassengersAtStop2().size(), 1,BusHelper.sharedInstance().getWaitingPassengersAtStop1().size());	
		assertEquals("should be 1 passengers at stop 3, but there are "+BusHelper.sharedInstance().getWaitingPassengersAtStop3().size(), 1,BusHelper.sharedInstance().getWaitingPassengersAtStop1().size());	

	//Step 2: ARRIVED AT STOP ONE---------------------------------------------------------------------------------------------------
		bus.msgAtStopOne();
		assertTrue("bus event should record reachedStop, but doesn't", bus.event==Event.reachedStop);
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record stopping, but doesn't", bus.state==State.stopping);
		assertTrue("bus event should record stopped after running scheduler", bus.event==Event.stopped);
	
	//Step 3: notifying passengers on board to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingToAlight after running scheduler", bus.state==State.notifyingPassengersToAlightBus);
		assertTrue("bus event should record notifiedToAlight after running scheduler", bus.event==Event.notifiedPassengersToAlightBus);
	
	//Step 4: waiting for passengers to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForAlighting after running scheduler", bus.state==State.waitForAlighting);
		bus.msgChangeEventToPassengersAlighted();
		assertTrue("bus event should record passengersAlighted after running scheduler", bus.event==Event.passengersAlighted);
		assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());
		
	//Step 5: notifying passengers to board bus
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingPassengersToBoardBus after running scheduler", bus.state==State.notifyingPassengersToBoardBus);
		assertTrue("bus event should record notifiedPassengersToBoardBus after running scheduler", bus.event==Event.notifiedPassengersToBoardBus);
		assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());
			
	//Step 6: passenger boards bus
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForBoarding after running scheduler", bus.state==State.waitForBoarding);
		bus.msgChangeEventToPassengersBoarded();
		assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
		bus.msgBoardingBus(passenger1);
		BusHelper.sharedInstance().getWaitingPassengersAtStop1().remove(passenger1);
		assertEquals("passengersOnBoard should have 1 passengers, but it has "+ bus.passengersOnBoard.size(), 1, bus.passengersOnBoard.size());
	
	//Step 7: bus continues driving
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record driving after running scheduler", bus.state==State.driving);
		assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
		assertEquals("passengersOnBoard should have 1 passengers, but it has "+ bus.passengersOnBoard.size(), 1, bus.passengersOnBoard.size());

	//Step 8: ARRIVED AT STOP TWO---------------------------------------------------------------------------------------------------
		bus.msgAtStopTwo();
		assertTrue("bus event should record reachedStop, but doesn't", bus.event==Event.reachedStop);
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record stopping, but doesn't", bus.state==State.stopping);
		assertTrue("bus event should record stopped after running scheduler", bus.event==Event.stopped);
			
	//Step 9: notifying passengers on board to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingToAlight after running scheduler", bus.state==State.notifyingPassengersToAlightBus);
		assertTrue("Pssgr should have logged \"Notified passenger that bus has arrived at stop2\" but didn't. His log reads instead: " 
				+ passenger1.log.getLastLoggedEvent().toString(), passenger1.log.containsString("Notified passenger that bus has arrived at stop2"));
		assertTrue("bus event should record notifiedToAlight after running scheduler", bus.event==Event.notifiedPassengersToAlightBus);
			
	//Step 10: waiting for passengers to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForAlighting after running scheduler", bus.state==State.waitForAlighting);
		bus.msgChangeEventToPassengersAlighted();
		assertTrue("bus event should record passengersAlighted after running scheduler", bus.event==Event.passengersAlighted);
		assertEquals("passengersOnBoard should be 1, but has "+ bus.passengersOnBoard.size(), 1, bus.passengersOnBoard.size());
	
	//Step 11: notifying passengers to board bus
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingPassengersToBoardBus after running scheduler", bus.state==State.notifyingPassengersToBoardBus);
		assertTrue("bus event should record notifiedPassengersToBoardBus after running scheduler", bus.event==Event.notifiedPassengersToBoardBus);
		assertEquals("passengersOnBoard should be 1, but has "+ bus.passengersOnBoard.size(), 1, bus.passengersOnBoard.size());
				
	//Step 12: passenger boards bus
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForBoarding after running scheduler", bus.state==State.waitForBoarding);
		bus.msgChangeEventToPassengersBoarded();
		assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
		bus.msgBoardingBus(passenger2);
		BusHelper.sharedInstance().getWaitingPassengersAtStop2().remove(passenger2);
		assertEquals("passengersOnBoard should have 2 passengers, but it has "+ bus.passengersOnBoard.size(), 2, bus.passengersOnBoard.size());
		
	//Step 13: bus continues driving
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record driving after running scheduler", bus.state==State.driving);
		assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
		assertEquals("passengersOnBoard should have 2 passengers, but it has "+ bus.passengersOnBoard.size(), 2, bus.passengersOnBoard.size());
		
	//Step 14: ARRIVED AT STOP THREE---------------------------------------------------------------------------------------------------
		bus.msgAtStopThree();
		assertTrue("bus event should record reachedStop, but doesn't", bus.event==Event.reachedStop);
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record stopping, but doesn't", bus.state==State.stopping);
		assertTrue("bus event should record stopped after running scheduler", bus.event==Event.stopped);
			
	//Step 15: notifying passengers on board to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingToAlight after running scheduler", bus.state==State.notifyingPassengersToAlightBus);
		assertTrue("bus event should record notifiedToAlight after running scheduler", bus.event==Event.notifiedPassengersToAlightBus);
			
	//Step 16: waiting for passengers to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForAlighting after running scheduler", bus.state==State.waitForAlighting);
		bus.msgChangeEventToPassengersAlighted();
		assertTrue("bus event should record passengersAlighted after running scheduler", bus.event==Event.passengersAlighted);
		bus.msgLeavingBus(passenger1);
		assertEquals("passengersOnBoard should be 1, but has "+ bus.passengersOnBoard.size(), 1, bus.passengersOnBoard.size());
				
	//Step 17: notifying passengers to board bus
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingPassengersToBoardBus after running scheduler", bus.state==State.notifyingPassengersToBoardBus);
		assertTrue("bus event should record notifiedPassengersToBoardBus after running scheduler", bus.event==Event.notifiedPassengersToBoardBus);
		assertEquals("passengersOnBoard should be 1, but has "+ bus.passengersOnBoard.size(), 1, bus.passengersOnBoard.size());
					
	//Step 18: passenger boards bus
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForBoarding after running scheduler", bus.state==State.waitForBoarding);
		bus.msgChangeEventToPassengersBoarded();
		assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
		bus.msgBoardingBus(passenger3);
		BusHelper.sharedInstance().getWaitingPassengersAtStop1().remove(passenger3);
		assertEquals("passengersOnBoard should have 2 passengers, but it has "+ bus.passengersOnBoard.size(), 2, bus.passengersOnBoard.size());
			
	//Step 19: bus continues driving
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record driving after running scheduler", bus.state==State.driving);
		assertTrue("bus event should record passengersBoarded after running scheduler", bus.event==Event.passengersBoarded);
		assertEquals("passengersOnBoard should have 2 passengers, but it has "+ bus.passengersOnBoard.size(), 2, bus.passengersOnBoard.size());
		
	//Step 20: ARRIVED AT STOP FOUR---------------------------------------------------------------------------------------------------
		bus.msgAtStopThree();
		assertTrue("bus event should record reachedStop, but doesn't", bus.event==Event.reachedStop);
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record stopping, but doesn't", bus.state==State.stopping);
		assertTrue("bus event should record stopped after running scheduler", bus.event==Event.stopped);
					
	//Step 21: notifying passengers on board to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record notifyingToAlight after running scheduler", bus.state==State.notifyingPassengersToAlightBus);
		assertTrue("bus event should record notifiedToAlight after running scheduler", bus.event==Event.notifiedPassengersToAlightBus);
					
	//Step 22: waiting for passengers to alight
		assertTrue("Bus's scheduler should have returned true (one action to do), but didn't.", bus.pickAndExecuteAnAction());
		assertTrue("bus state should record waitForAlighting after running scheduler", bus.state==State.waitForAlighting);
		bus.msgChangeEventToPassengersAlighted();
		assertTrue("bus event should record passengersAlighted after running scheduler", bus.event==Event.passengersAlighted);
		bus.msgLeavingBus(passenger2);
		bus.msgLeavingBus(passenger3);
		assertTrue("passengersOnBoard should be empty, but isn't", bus.passengersOnBoard.isEmpty());	
	}
	

}
