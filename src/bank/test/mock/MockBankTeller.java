package bank.test.mock;

import java.util.Map;

import bank.interfaces.BankCustomer;
import bank.interfaces.BankTeller;
import market.interfaces.MarketWorker;
import market.interfaces.MarketCustomer;
import bank.test.mock.LoggedEvent;

public class MockBankTeller extends Mock implements BankTeller {

	public MarketCustomer customer;
	public EventLog log;
	public Map<String, Integer> groceries;
	public int registerNumber;

	public MockBankTeller(String name) {
		super(name);
		log = new EventLog();
	}
	
	/*
	@Override
	public void msgGetGroceries(MarketCustomer customer, Map<String, Integer> groceryList) {
		log.add(new LoggedEvent("Received msgGetGroceries from MarketCustomer"));
		groceries = groceryList;
	}

	@Override
	public void msgHereIsMoney(MarketCustomer customer, double money) {
		log.add(new LoggedEvent("Received msgHereIsMoney from MarketCustomer. Money = $" + money));
	}

	@Override
	public void msgCantAffordGroceries(MarketCustomer customer) {
		log.add(new LoggedEvent("Received msgCantAffordGroceries from MarketCustomer"));
	}
	*/

	@Override
	public void msgAssigningCustomer(BankCustomer customer) {
		log.add(new LoggedEvent("Customer is getting assigned to teller"));
		
	}

	@Override
	public void msgOpenAccount(BankCustomer customer) {
		log.add(new LoggedEvent("Customer needs account opened"));
		
	}

	@Override
	public void msgDepositMoney(int accountNumber, double money) {
		log.add(new LoggedEvent("Customer wants to deposit money"));
		
	}

	@Override
	public void msgWithdrawMoney(int accountNumber, double money) {
		log.add(new LoggedEvent("Customer wants to withdraw money"));
		
	}

	@Override
	public void msgIWantLoan(int accountNumber, double moneyRequest) {
		log.add(new LoggedEvent("Customer wants to get a loan"));
		
	}

	@Override
	public void msgThankYouForAssistance(BankCustomer bankCustomer) {
		log.add(new LoggedEvent("Customer is leaving bank"));
		
	}

	@Override
	public void msgGoToRegister(int registerNumber) {
		log.add(new LoggedEvent("Teller is going to register"));
		
	}

	@Override
	public void msgDoneWorking() {
		log.add(new LoggedEvent("Teller is done with work for the day"));
		
	}

	@Override
	public void msgAtRegister() {
		log.add(new LoggedEvent("Teller is at register"));
		
	}

	@Override
	public void msgAtManager() {
		log.add(new LoggedEvent("Teller is at manager"));
		
	}

	@Override
	public void msgAnimationFinishedLeavingBank() {
		log.add(new LoggedEvent("Teller is leaving bank"));
		
	}

	@Override
	public void msgHereIsPaycheck(double paycheck) {
		log.add(new LoggedEvent("Teller is being paid for his work today"));
		
	}

	@Override
	public void msgHoldUpBank(double moneyRequired, BankCustomer person) {
		log.add(new LoggedEvent("Bank is getting robbed"));
		
	}
}
