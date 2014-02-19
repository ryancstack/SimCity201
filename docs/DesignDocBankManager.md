#Design Doc: BankManager

##Data
```
List<Customer> customers;
List<MyBankTeller> bankTellers;
Class MyBankTeller {
	BankTeller teller;
	BankTellerState state;
}

enum BankTellerState {Idle, Busy} 

```
	
##Scheduler
```
if ∃ customer in customers
	if ∃ teller in bankTellers ∋ teller.state = Idle
		AssignCustomerToTeller(customer, teller);
 
```

##Messages
```
msgINeedAssistance(Customer customer) {
	customers.add(customer);
	stateChanged();
}
```
```

msgTellerFree(BankTeller teller) {
	MyBankTeller myTeller = bankTellers.find(teller);
	myTeller.state = Idle;
	stateChanged();
}

```
##Actions
```
AssignCustomerToTeller(Customer customer, BankTeller teller) {
	teller.teller.msgAssignCustomer(customer);
	teller.state = Busy;
	customers.remove(customer);
}
```
