#Design Doc: BankCustomer Agent

##Summary
When the customer enters the bank, its state is set to needAssistance, prompting him to msg the manager that he needs assistance. For as long as he needs assistance, he'll run through a loop in scheduler of all possible services he might need. This is largely governed by "moneyRequired", which should be set beforehand by the customer according to whether he needs a car or is simply going about his daily routine with a standard moneyRequired. It is also required by a boolean "tryingToLoan", which may be set to true when he needs a car desperately. Right now I have the customer set its "tryingToLoan" to false after being denied a loan. If the customer runs through his scheduler without any service needs, he will leave the bank.

##Data
	private double money;
	private double moneyRequired;
	private double funds=0;
	private double fundsInBank;
	private int accountNumber;
	boolean hasAccount;
	boolean tryingToLoan;
	BankManager bankManager;
	BankTeller teller;
	
	public enum servicesNeeded{depositMoney, withdrawMoney, loanMoney}
	
##Scheduler

	if (∃ in customer ∋ state.needAssistance && event==doingNothing){
		goToBank();
	}
	
	if (∃ in customer ∋ state.needAssistance && event==approachedByTeller){
		if(!hasAccount){
			openAccount();
		}
		else if(money> moneyRequired){
			depositMoney();
		}
		else if(money< moneyRequired && moneyRequired < money+fundsInBank){
			withdrawMoney();
		}
		else if(money<moneyRequired && moneyRequired > money+fundsInBank && tryingToLoan){
			loanMoney();
		}
		else
			leaveBank();
	}
		
	if (∃ in customer ∋ state.leavingBank && event==leftBank){
		state= notInBank;
		event= doingNothing;
	}

	
##Messages
	msgHereIsAccountNumber(int num){
		accountNumber= num;
		hasAccount= true;
		event=approachedByTeller;
		stateChanged();
	}
	
	msgDepositSuccessful(int depositAmount){
		money=- depositAmount;
		moneyInBank=+ depositAmount;
		event= approachedByTeller; //check if anything else needs to be done
		stateChanged();
	}
	
	msgHereAreFunds(double withdrawAmount){
		money=+ withdrawAmount;
		moneyInBank=- withdrawAmount;
		event= approachedByTeller;
		stateChanged();
	}
	
	msgLoanDenied(){
		tryingToLoan=false;
		event= approachedByTeller;
		stateChanged();
	}
	
	msgLeftBank(){ //from animation
		event= leftBank;
		stateChanged();
	}
	

##Actions	
	goToBank(){
		bankManager.msgINeedAssistance(this);
	}
	
	openAccount(){
		teller.msgOpenAccount(this); //so teller knows who to return to
		event=beingAssisted;
	}

	depositMoney(){
		funds= money-moneyRequired; //so he has some cash in pocket
		msgDepositMoney(accountNumber, funds);
		event=beingAssisted;
	}
	
	withdrawMoney(){
		funds= moneyRequired-money;
		msgWithdrawMoney(accountNumber, funds);
		event=beingAssisted;
	}
	
	loanMoney(){
		funds= moneyRequired - money - fundsInBank;
		teller.msgIWantALoan(funds);
		event=beingAssisted;
	}
	
	leaveBank(){
		teller.msgLeavingBank(this);
		state= leavingBank;
	}
	

	