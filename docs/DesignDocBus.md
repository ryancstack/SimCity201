#Design Doc: Bus

##Summary
Bus Agent is autonomous. The state of the bus depends on the stop. When the bus is at state/destination StopOne, scheduler prompts the bus to move the gui to the next stop, and the bus will drive around in circles. This code assumes that the city is surrounded by a square road, which the bus can drive around. 
At each bus stop: 
1) Customers will board the bus and send msgINeedARide() (adding them to list).
2) The bus agent messages all passengers on the bus the current stop.
3) If it is their stop, customer agent will get off and msgThanksForTheRide()

##Data
	enum BusState {StopOne, StopTwo, StopThree, StopFour};
	List<myCustomer> myPassengers; //Does this have to be myCustomer?
	
	class myCustomer {
		String name;
		int SourceStopNumber = 0;
		int DestinationStopNumber = 0;
		Boolean sittingOnBus = false;
		
		myCustomer(String customerName, int SourceNumber, int DestinationNumber) {
			name = customerName;
			SourceStopNumber = SourceNumber;
			DestinationStopNumber = DestinationNumber;
		}
	}
			
##Scheduler
	if ∃ in bus ∋ state.StopOne()
		then DriveToStopTwo();
		
	if ∃ in bus ∋ state.StopTwo()
		then DriveToDropThree();
		
	if ∃ in bus ∋ state.StopThree()
		then DriveToStopFour();
		
	if ∃ in bus ∋ state.StopFour()
		then DriveToStopOne();

##Messages
	msgINeedARide(name, source, destination) { 
		print("Adding " + this.name + " as a passenger.");
		myPassengers.add(new myCustomer(name, source, destination); //Is it okay to add customeragent to list within messages?
	}
	
	msgThanksForTheRide(name) {
		print("Removing " + this.name + " from passengers.");
		myCustomers.remove(this); 
	}

##Actions	
	DriveToStopOne() {
		//gui
		//timer to replicate bus boarding and unboarding
		for(myCustomer passenger : myPassengers) {
			if(passenger.source == 1) {
				sittingOnBus = true;
			}
		}
		for(CustomerAgent passenger : myPassengers) {
			passenger.msgThisIsBusStop(int 1);
		}
	}
	
	DriveToStopTwo() {
		//gui
		//timer to replicate bus boarding and unboarding
		for(myCustomer passenger : myPassengers) {
			if(passenger.source == 2) {
				sittingOnBus = true;
			}
		}
		for(CustomerAgent passenger : myPassengers) {
			passenger.msgThisIsBusStop(int 2);
		}
	}
	
	DriveToStopThree() {
		//gui
		//timer to replicate bus boarding and unboarding
		for(myCustomer passenger : myPassengers) {
			if(passenger.source == 3) {
				sittingOnBus = true;
			}
		}
		for(CustomerAgent passenger : myPassengers) {
			passenger.msgThisIsBusStop(int 3);
		}
	}
	
	DriveToStopFour() {
		//gui
		//timer to replicate bus boarding and unboarding
		for(myCustomer passenger : myPassengers) {
			if(passenger.source == 4) {
				sittingOnBus = true;
			}
		}
		for(CustomerAgent passenger : myPassengers) {
			passenger.msgThisIsBusStop(int 4);
		}
	}
	
	