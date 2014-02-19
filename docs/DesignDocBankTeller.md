#Design Doc: BankTeller

##Data
```
 Bank bank = bank.sharedInstance();
 List<MyBankCustomer> customers
 Class MyBankCustomer {
 	Customer customer;
 	double moneyToDeposit;
 	double moneyToWithdraw;
 	double moneyRequest;
 	int accountNumber;
 	CustomerState state;
 }
 BankManager manager;
 CustomerState(NeedingAssistance, AskedAssistance, OpeningAccount, DepositingMoney, WithdrawingMoney, GettingLoan}
```
	
##Scheduler
```
 if ∃ customer in customers ∋ customer.state = NeedingAssistance
 	then OfferAssistance(customer)
 if ∃ customer in customers ∋ customer.state = OpeningAccount
	then CreateAccount(customer);
 if ∃ customer in customers ∋ customer.state = DepositingMoney
	then DepositMoney(customer);
 if ∃ customer in customers ∋ customer.state = WithdrawingMoney
	then GiveCustomerMoney(customer);
 if ∃ customer in customers ∋ customer.state = GettingLoan
	then GiveLoan(customer);
```

##Messages
```
msgAssigningCustomer(CustomerAgent customer) {
	customers.add(new MyBankCustomer(customer, NeedingAssistance);
	statecChanged();
}
```

```
msgOpenAccount(CustomerAgent customer) {
	MyBankCustomer bankCustomer = customers.find(customer);
	bankCustomer.state = OpeningAccount;
	stateChanged();
}
```
```
msgDepositMoney(int accountNumber, double money) {
	MyBankCustomer bankCustomer = customers.find(customer);
	bankCustomer.moneyToDeposit = money;
	bankCustomer.accountNumber = accountNumber;
	bankCustomer.state = DepositingMoney;
	stateChanged();
}
```
```
msgWithdrawMoney(int accountNumber, double money) {
	MyBankCustomer bankCustomer = customers.find(customer);
	bankCustomer.moneyToWithdraw = money;
	bankCustomer.accountNumber = accountNumber;
	bankCustomer.state = WithdrawingMoney;
	stateChanged();
   }
```
```
msgIWantLoan(int accountNumber, double moneyRequest) {
	MyBankCustomer bankCustomer = customers.find(customer);
	bankCustomer.moneyToRequest = moenyRequest;
	bankCustomer.accountNumber = accountNumber;
	bankCustomer.state = GettingLoan;
	stateChanged();
}
```
##Actions
```
OfferAssistance(MyBankCustomer customer) {
	customer.customer.msgHowMayIHelpYou(this);
	customer.state = AskedAssistance;
}
```
```
CreateAccount(MyBankCustomer customer) {
	int UUID = //unique account number
	bank.sharedInstance().addAccount(customer, UUID);
	customer.customer.msgHereIsAccount(UUID);
	customers.remove(customer);
}
```
```
DepositMoney(MyBankCustomer customer) {
	bank.sharedInstance().depositMoney(customer.accountNumber, customer.moneyToDeposity);
	customer.customer.msgDepositSuccessful();
	customers.remove(customer);
	manager.msgTellerFree(this);
}
```
```
GiveCustomerMoney(MyBankCustomer customer) {
	double funds = bank.sharedInstance().getAccountFunds(customer.accountNumber);
	if(funds >= customer.moneyToWithdraw) {
		bank.sharedInstance().withdrawCash(customer.accountNumber, customer.moneyToWithdraw);
		customer.customer.msgHereIsMoney(customer.moneyToWithdraw);
	}
	else {
		bank.sharedInstance().withdrawMax(customer.accountNumber);
		customer.customer.msgHereIsMoney(funds);
	}
	customer.remove(customer);
	manager.msgTellerFree(this);
}
```
```
GiveLoan(MyBankCustomer customer) {
	//determine if eligible
	if(eligible) {
		customer.customer.msgHereAreFunds(customer.moneyRequest);
	else {
		customer.customer.msgLoanDenied()
	}
	customer.remove(customer);
	manager.msgTellerFree(this);
}
```
