package bank.test.mock;

import java.util.Map;

import bank.BankTellerRole;
import bank.test.mock.LoggedEvent;
import bank.interfaces.BankCustomer;
import bank.interfaces.BankTeller;
import market.interfaces.MarketWorker;
import market.interfaces.MarketCustomer;

public class MockBankCustomer extends Mock implements BankCustomer {

	public MarketWorker market;
	public EventLog log;
	public double price;
	public Map<String, Integer> groceries;
	public double moneyRequired;
	public double moneyToDeposit;
	public MockBankTeller teller;
	public int accountNum;

	public MockBankCustomer (String name,double moneyToDeposit,double moneyRequired) {
		super(name);
		log = new EventLog();
		this.moneyRequired = moneyRequired;
		this.moneyToDeposit = moneyToDeposit;
	}
	
	//MARKET
	/*
	@Override
	public void msgHereIsBill(double price) {
		log.add(new LoggedEvent("Received msgHereIsBill from Market. Price = $" + price));
		this.price = price;
	}

	@Override
	public void msgHereAreYourGroceries(Map<String, Integer> groceries) {
		log.add(new LoggedEvent("Received msgHereAreYourGroceries from Market."));
		this.groceries = groceries;
	}

	@Override
	public void msgCantFillOrder(Map<String, Integer> groceries) {
		log.add(new LoggedEvent("Received msgCantFillOrder from Market."));
		this.groceries = groceries;
	}

	@Override
	public void msgHowCanIHelpYou(BankTeller teller, int tellerNumber) {
		// TODO Auto-generated method stub
		
	}
	*/
	
	//BANK 

	@Override
	public void msgLoanDenied() {
		log.add(new LoggedEvent("Customer's loan was denied"));
		
	}

	@Override
	public void msgHereAreFunds(double funds) {
		log.add(new LoggedEvent("Customer received his funds"));
		
	}

	@Override
	public void msgHereIsYourAccount(int accountNumber) {
		log.add(new LoggedEvent("Customer opened new account"));
		
	}

	@Override
	public void msgDepositSuccessful() {
		log.add(new LoggedEvent("Customer's deposit was successful"));
		
	}

	@Override
	public void msgHowCanIHelpYou(BankTeller teller, int tellerNumber) {
		log.add(new LoggedEvent("Customer is ready to be helped"));
		
	}
}
