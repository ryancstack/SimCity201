#Design Doc: BankCustomer

##Data
```
BankTeller teller;
BankManager manager;

enum CustomerState{DoingNothing, Waiting, BeingHelped, Done}
CustomerState state = DoingNothing;

//from super class
enum Agenda{NeedsAccount, NeedsLoan, NeedsDeposit, NeedsWithdrawal, NoAction}
Agenda agenda;

double moneyToDeposit;
double moneyToWithdraw;
double moneyRequired;
double totalFunds;

```
	
##Scheduler
```
if state = DoingNothing
	askForAssistance();
if state = BeingHelped
	DoGoToHost();
	if agenda = NeedsAccount
		openAccount();
	if agenda = NeedsLoan
		takeOutLoan();
	if agenda = NeedsDeposit
		depositMoney();
	if agenda = NeedsWithdrawal
		withdrawMoney();
if state = Done
	leaveBank()
```

##Messages
```
msgHowCanIHelpYou(BankTeller teller) {
	state = BeingHelped;
	stateChanged();
}
```
```
msgLoanDenied() {
	state = Done;
	stateChanged();
}
```
```
msgHereAreFunds(double funds) {
	totalFunds+=funds;
	state = Done;
	stateChanged();
}
```
```
msgHereIsYourAccount(int accountNumber) {
	state = Done;
	this.accountNumber = accountNumber;
	stateChanged();
}
```
```
msgDepositSuccessful() {
	state = Done;
	stateChanged();
}
```
##Actions
```
askForAssistance() {
	manager.msgINeedAssistance(this)
	state = Waiting;
}
```
```
openAccount() {
	teller.msgOpenAccount(this)
}
```
```
takeOutLoan() {
	teller.msgIWantLoan(int accountNumber, double moneyRequired)
}
```
```
depositMoney() {
	teller.msgDepositMoney(int accountNumber, double moneyToDeposit)
}
```
```
withdrawMoney() {
	teller.msgWithdrawMoney(int accountNumber, double moneyToWithdraw)
```
```
leaveBank() {
	agenda = NoAction;
	DoLeaveBank();
}
```
