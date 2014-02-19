package city.test.mock;

import city.test.mock.EventLog;
import city.interfaces.Bus;


public class MockBus extends Mock implements Bus {

	public EventLog log;

	public MockBus(String name) {
		super(name);
		log = new EventLog();
	}
	
}
