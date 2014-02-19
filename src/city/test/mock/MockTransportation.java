package city.test.mock;

import city.test.mock.LoggedEvent;
import city.BusAgent;
import city.test.mock.EventLog;
import city.TransportationRole.TransportationState;
import city.interfaces.Transportation;


public class MockTransportation extends Mock implements Transportation {

	public EventLog log;
	public String startingLocation;
	public String destination;

	public MockTransportation(String name) {
		super(name);
		log = new EventLog();
		
	}
	
	public MockTransportation(String Destination, String StartingLocation) {
		super(Destination); //needs a name
		log = new EventLog();
		destination = Destination;
		startingLocation = StartingLocation;
		
	}
	
	public void msgActionComplete() {
		log.add(new LoggedEvent("Reached destination: "+destination));
	}
	
	public void msgGetOnBus(BusAgent b) {
		log.add(new LoggedEvent("Received message to get on Bus."));
	}
	
	public void msgArrivedAtDestination(String destination) {
		log.add(new LoggedEvent("Car dropped passenger off at"+ destination));
	}
	public void msgAtStop(int stopNumber) {
		log.add(new LoggedEvent("Notified passenger that bus has arrived at stop"+stopNumber));
	}
	
}
