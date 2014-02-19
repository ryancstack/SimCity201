#Design Doc: Landlord Agent

##Data
```
 class Tenant {
 	PersonAgent inhabitant; 
 	double moneyOwed; 
 	PayingState state;
 }
 List<Tenant> tenants;
 enum PayState {NeedsToPay,WaitingForPayment,OwesMoney,PayLater,NothingOwed}
 double funds;
```	

##Scheduler
```
if ∃ t in tenants ∋ t.state = NeedsToPay
  then payRent(t);
if ∃ t in tenants ∋ t.state = OwesMoney
  then payRentLater(t);
```
##Messages
```
msgNeedsToPayRent(PersonAgent person, double moneyOwed){
	/*** How do we add new tenants to the list? ***
	bool newTenant = true;
	for(Tenant t : tenants){
		if(t.inhabitant == person){
			newTenant = false;
		}
	}
	if(newTenant == true){
		tenants.add(person,moneyOwed,PayState.NeedsToPay);
	}
	*/
	for(Tenant t : tenants){
		if(t.inhabitant == person){
			t.state = PayState.NeedsToPay;
			t.moneyOwed = moneyOwed;
		}
	}
	stateChanged();
   }
```
```
msgHereIsRent(PersonAgent person, double money){
	for(Tenant t : tenants){
		if(t.inhabitant == person){
			if(money == t.moneyOwed){
				funds += money;
				t.moneyOwed -= money;
				t.state = PayState.NothingOwed;
			}
			else{
			      t.moneyOwed -= money;
				t.state = PayState.OwesMoney;
		      }
		}
	}
	stateChanged();
   }
```

##Actions
```
payRent(Tenant tenant){
  tenant.inhabitant.msgPayRent(tenant.moneyOwed);
  tenant.state = PayState.WaitingForPayment;
  stateChanged();
}
```
```
payRentLater(Tenant tenant){
	tenant.inhabitant.msgPayRentLater(tenant.moneyOwed);
	tenant.state = PayState.PayLater;
	stateChanged();
}
```
