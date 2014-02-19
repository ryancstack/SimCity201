#Design Doc: MarketCustomer

##Data
```
 Class MarketCustomerRole extends Role implements MarketCustomer
MarketWorkerAgent myWorker;
enum State {DoingNothing, DecidingGroceries, WaitingForService, Paying, DoneTransaction};
enum Event {WantsGroceries, DecidedGroceries, GotBill, GotGroceries};
Timer timer = new Timer();
Bill myBill;
Map<String, int> myGroceries;


```
	
##Scheduler
```
if State == DoingNothing && Event == WantsGroceries {
	State = DecidingGroceries;
	DoDecideGroceries();
}
if State == DecidingGroceries && Event == DecidedGroceries {
	State = WaitingForService
	DoGiveGroceryOrder();
}
if State == WaitingForService && Event == GotBill{
	State = Paying;
	DoPay();
}
if State == Paying && Event == GotGroceries {
	State = DoneTransaction;
	DoLeaveMarket();
}
return false;
```

##Messages
```
msgDecidedGroceriesList(Map<String, int> groceries) {
	myGroceries = groceries;
	Event = DecidedGroceries;
}
```

```
msgHereIsYourBill(Bill bill) {
	myBill = bill;
	Event = GotBill;
}
```

```
msgHereAreYourGroceries() {
	Event = GotGroceries;
}

```

##Actions
```
More of a method instead of an action.
public Map<String, int> groceries DoDecideGroceries() {
//Timertask that returns a map of groceries?

}
```
```
public void DoGiveGroceyOrder(){
	myWorker.msgGetGroceries(myGroceries);

}
```
```
public void DoPay() {
	myWorker.HereIsMoney(myBill.price);

}
```
```
public void DoLeaveMarket() {


}
```
