package city.test.mock;

import city.test.mock.EventLog;
import city.interfaces.Car;


public class MockCar extends Mock implements Car {

	public EventLog log;

	public MockCar(String name) {
		super(name);
		log = new EventLog();
	}
	
}
