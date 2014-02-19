package bank.interfaces;

public interface BankTeller {
	public void msgAssigningCustomer(BankCustomer customer);
	
	public void msgOpenAccount(BankCustomer customer);
	
	public void msgDepositMoney(int accountNumber, double money);
	
	public void msgWithdrawMoney(int accountNumber, double money);
	
	public void msgIWantLoan(int accountNumber, double moneyRequest);

	public void msgThankYouForAssistance(BankCustomer bankCustomer);
	
	public void msgGoToRegister(int registerNumber);
	
	public void msgHereIsPaycheck(double paycheck);
	
	public void msgDoneWorking();
	
	//animation
	
	public void msgAtRegister();
	
	public void msgAtManager();
	
	public void msgAnimationFinishedLeavingBank();

	public void msgHoldUpBank(double moneyRequired,
			BankCustomer person);

}
