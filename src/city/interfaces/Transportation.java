package city.interfaces;

import city.BusAgent;

public interface Transportation {

	void msgAtStop(int i);

	void msgGetOnBus(BusAgent busAgent);

	void msgArrivedAtDestination(String destination);

}
