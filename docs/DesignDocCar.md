#Design Doc: Car

##Summary
The Car is initially parked wherever the passenger last left it in carState= Idle. When the passenger needs the Car, he walks to the Car and sends it msgTakeMeHere(destination). The passengerGui then disappears into the Car and the carGui travels to the destination. The destination coordinates are hardcoded in carGui. When the carGui reaches its destination it sends the agent msgAtDestination(), which sends the car scheduler to perform action parkCar. Within parkCar, the Car messages the passenger that it has arrived at its destination, prompting the passenger to bring back its gui at the destination(or drop-off) coordinates.

##Data
private Person passenger;
public enum carState{inTransit, atDestination, Idle};
//public enum currentLocation{Home, Bank, Restaurant1, Restaurant2};	
private String destination;
public Semaphore driving(0, true);
	
##Scheduler
	
	/*if ∃ in host ∋ state.needWaiter() //just so we have access to the symbols
		then followMeToTable(menu);*/ 
		
	if (carState == inTransit){
		goTo(Destination);
	}
	
	else if (carState == atDestination){
		parkCar();
	}

	else if (carState == Idle){
		//do nothing
	}
	
##Messages
	msgTakeMeHere(String myDestination){ //receives msg from passenger
		carState = inTransit;
		destination= myDestination;
		stateChanged();
	}
	
	msgAtDestination(){ //from carGui when reached destination
		driving.release();
		carState= atDestination;
		stateChanged();
	}

##Actions	
	goTo(String myDestination){
		doGoTo(myDestination); //sets destination in carGui
		driving.acquire(); //to ensure that the gui is uninterrupted on the way
	}
	
	parkCar(){
	//msg gives passenger the destination so its gui can reappear at an appropriate place
		passenger.msgArrivedAtDestination(destination);
		carState= Idle;
	}

	