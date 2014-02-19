package bank.interfaces;

public interface BankManager {
	public void msgINeedAssistance(BankCustomer customer);
	
	public void msgTellerFree(BankTeller teller);
	
	public void msgHereForWork(BankTeller teller);

	public void msgTellerLeavingWork(BankTeller teller);

	public void msgCollectPay(BankTeller teller);
}
