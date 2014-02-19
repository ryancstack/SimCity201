#Design Doc: PersonAgent extends Agent


##Summary
```
This agent uses a Stack of Roles in order to execute transportation and its next role after reaching a destination. The only downside of using a stack is that we will not have a memory of the different roles's data that the agent assumed during run but it is not part of the requirements to do so. 

Everytime a role finishes, it should send a message to this agent saying it is done so that the agent can move onto the next role in the stack. 


```

##Data
```
Stack<Roles> roles;
RoleFactory factory;
Role workRole;
double funds;
boolean hasCar;
boolean hasWorked;
boolean atHome;
String name;
String homeName;
enum PersonPosition {AtHome, InTransit, AtMarket, AtRestaurant, AtBank};
enum HouseState { owns House..ownsAppt..};
enum PersonState {
	Idle, WantsToGoHome, WantFood, CookHome, WaitingForCooking, GoOutEat, StartEating, 	Eating, NeedsToWork, Working,
	//Bank Scenario States
	OutToBank, WantsToWithdraw, WantsToGetLoan, WantsToDesposit, WantsToRob,	
};
PersonPosition personPosition;
HouseState houseState;
PersonState personState;
int hungerLevel;
Clock clock = clock.sharedInstance();
Timer timer = new Timer();
public class PersonTimerTask extends TimerTask {};
//Restaurant class within person so he knows how to go to a certain restaurant. 

public class RoleFactory {
	Role newRole;
	Role createRole(String order);

};
public class WorkDetails {
	Location location;
	Role workRole;
};


```
	
##Scheduler
```
if roles is not empty{
	then boolean b = roles.peek.pickAndExecuteAnAction();
	return b;
}
//Non-Norm Rules
if (personState == PersonState.WantsToWithdraw) {
	goWithdraw();
	return true;
}
if (personState == PersonState.WantsToGetLoan) {
	goLoan();
	return true;
}
if (personState == PersonState.WantsToDeposit) {
	goDeposit();
	return true;
}
if (personState == PersonState.WantsToRob) {
	goRob();
	return true;
}
//Normative Scenario Rules
if personState == WantsToGoHome { 
	goHome();
	return true;
}
if personState == CookHome && atHome == true {
	cookHomeFood();
	return true;
}
if personState == CookHome && atHome == false {
	goHome();
	return true;
}
if personState == GoOutEat {
	goRestaurant();
	return true;
}
if personState == WantFood {
	decideFood(); //change state in decideFood(); and all other actions
	//decides whether to stay home and cook/go home and cook or go to restaurant
	return true;

}
if personState == StartEating {
	eatFood();
	return true;
}
if personState == NeedsToWork {
	goWork();
	return true;
}
return evaluateStatus();
//evaluateStatus becomes our return false for our scheduler of the personAgent.
```

##Messages
```
msgWakeUp() {
	hasWorked = false;
	personState = WantFood;
	stateChanged();

}
```

```
msgCookingDone() {
	personState = startEating;
	stateChanged();

}
```

```
msgDoneEating() {
	personState = Idle;
	hungerLevel = 0;
	stateChanged();
	//Go to Evaluate status

}

```

```
msgGoWork() { //Not Really used. Can be used for hacks.
	personState = NeedsToWork;
	stateChanged();
}

```

```
msgDoneWorking() {
	Deactivate all roles;//not yet sure how to implement.
	personState = WantFood;
	stateChanged();
}

```

```
msgGoHome() {
	personState = WantsToGoHome;
	stateChanged();
}

```

```
msgAtHome() { //GUI message
	atHome = true;
	stateChanged();
}

```
```
msgRoleFinished() {
	roles.pop();
	stateChanged();
}

```

```
msg//() {

}

```

##Actions
```
public boolean evaluateStatus() {
	//Intermediate states = Eating, Cooking, Working.
	if personState = Cooking || intermediate states waiting for personal timerTask {
		return false;
	}
	else if hasWorked = false {
		personState = NeedsToWork;
		return true;
	}
	.
	.
	.
	//Needs group designing IMO. This is our algoritm for evaluation outside of norms
}
```
```
public void goHome() {
	Deactivate current active role;
	activate transportation role with home as destination;
	personState = InTransit;
	gui.GoHome//;
}
```
```
public void cookHomeFood() {
	Create new TimerTask {
		msgCookingDone();
	}(Random time for cooking);
	personState = Cooking;

}
```
```
public void goRestaurant() {
	personState = OutToEat;
	Restaurant r = restaurants.chooseOne();
	//
	roles.clear();//deactivates current role;
	//We need to figure out how transporation works. If our personAgent takes on a transport role, our scheduler needs to accomodate.
	roles.add(r.cr);
	roles.add(new TransportationRole(r.location));
	//roles is a stack so the scheduler will execute the last added role until it exits.

}
```
```
public void decideFood() {
	//Decide cook or eat out
	if cook {
		personState = CookHome;
	}
	else {
		personState = GoOutEat;
	}
}
```
```
public void eatFood() {
	Create new TimerTask {
		msgDoneEating();
	}(Random time for eating);
	personState = Eating;
}
```
```
public void goWork() {
	hasWorked = true;
	roles.clear();
	roles.add(workDetails.workRole);
	roles.add(new TransportationRole(workDetails.workLocation);
	Create new TimerTask {
		msgDoneWorking();
	}(Random time for Working);
	personState = Working;
	//Where does the logic for activate worker role go?

}
```
//FILLER ACTIONS FOR FUTURE DESIGNING
```
public void // {


}
```
```
public void // {


}
```
