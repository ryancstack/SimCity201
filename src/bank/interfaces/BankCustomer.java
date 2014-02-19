package bank.interfaces;

public interface BankCustomer {
	public void msgHowCanIHelpYou(BankTeller teller, int tellerNumber);
	
	public void msgLoanDenied();
	
	public void msgHereAreFunds(double funds);
	
	public void msgHereIsYourAccount(int accountNumber);
	
	public void msgDepositSuccessful();

	public void msgBankIsClosed();
}
