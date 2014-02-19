#Design Doc: HomeRole

##Data
```
	PersonAgent myPerson
	LandlordRole landlord
	boolean needToPayRent
	double debt
	int dirtinessLevel //How do we increment this?
```
##Scheduler
```	
	if needToPayRent = true
		then PayRent()
```
```
	if dirtinessLevel > 5
		then Clean()
```
##Messages
```
	msgPayRent(double moneyOwed) {
		needToPayRent = true
		debt = moneyOwed
	}
```
```
	msgPayLater() {
		//Do nothing?
	}
```
##Actions
```
	PayRent() {
		if(myPerson.money >= debt) {
			landlord.msgHereIsRent(debt)
			myPerson.money -= debt
		}
		else 
			landlord.msgCantPayRent()
		
		needToPayRent = false
	}
```
```
	Clean() {
		DoClean() //GUI
		dirtinessLevel = 0
	}
```