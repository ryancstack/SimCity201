#Design Doc: Transportation Agent

##Summary
Accounts for normative scenario for Transportation Agent. 

##Data
	int currentStopNumber = 0;
	int desiredStopNumber;
	string destination;
	enum state TransportationState {Walking, NeedsToTravel, InTransit, AtDestination, None};
	CarAgent car;
	BusAgent bus;
	Boolean hasCar = false;
	
##Scheduler
	if ∃ in TransportationAgent ∋ state.Walking()
		then WalkToDestination(); 

	if ∃ in TransportationAgent ∋ state.NeedsToTravel()
		then GetAVehicle(); 
	
	if ∃ in TransportationAgent ∋ state.AtDestination()
		then GetOffVehicle(); 
		
##Messages
	msgThisIsBusStop(int BusStopNumber) {
		currentStopNumber = BusStopNumber; //is this necessary?
		
		if(desiredStopNumber == BusStopNumber) {
			TransportationState = AtDestination;
		}
	}
	
	msgArrivedAtDestination(String destination){ //from carGui when reached destination
		TransportationState = atDestination;
	}

##Actions	
	WalkToDestination() {
		//gui to destination
		TransportationState = None;
	}

	GetAVehicle() {
		if(hasCar) {
			car.msgTakeMeHere();
		}
		else if(!hasCar) {
			bus.msgINeedARde()
		}
		TransportationState = InTransit;
	}
	
	GetOffVehicle() {
		if(hasCar) { //if got off car, arrived at destination
			TransportationState = None;
			//remove car gui
		}	
		if(!hasCar) { //if got off bus, walk from busstop to location
			TransportationState = Walking;
			//add person gui at bus stop
		}
	}	