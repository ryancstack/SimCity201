package bank.test;

import java.util.HashMap;
import java.util.Map;

import bank.Bank;
import bank.BankCustomerRole;
import bank.BankTellerRole;
import bank.BankTellerRole.TellerState;
import bank.helpers.AccountSystem;
import bank.interfaces.BankTeller;
import bank.test.mock.MockBankCustomer;
import bank.test.mock.MockBankManager;
//import bank.test.mock.MockBankTeller;
import market.MarketWorkerRole;
import market.MarketWorkerRole.orderState;
import market.test.mock.*;
import junit.framework.*;

public class BankTellerTest extends TestCase {

	//these are instantiated for each test separately via the setUp() method.
	MockBankCustomer customer;
	MockBankManager manager;
	MockPerson person;
	
	BankTellerRole teller;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
		manager = new MockBankManager("mockbankmanager");		
		customer = new MockBankCustomer("mockcustomer",300,0);
		person = new MockPerson("mockperson");
		teller = new BankTellerRole();
	}	
	
	//teller initial state Arrived At Work
	//teller state = AtManager 
	//manager messages msgGoToRegister
	//teller state = GoingToRegister
	
	//teller state = ReadyForCustomers;
	//manager messages teller .msgAssigningCustomer
	
	//teller's customer list should be empty
	//teller receives .msgAssigningCustomer
	//teller adds customer
	//mycustomer state should initially be NeedingAssistance

	//pickandexec
	
	//mycustomerstate = AskedAssistance
	//customerstate = GoingToTeller
	//customer receives message from gui mse
	//customer state = BeingHelped
	//customer messages teller msgOpenAccount
	//account should not exist for user
	//mycustomerstate == OpeningAccount
	//teller messages customer .msgHereIsYourAccount
	//mycustomer state == OpenedAccount	
	
	//Deposit w/ Account
	public void testOneBankInteraction(){	
		teller.setPerson(person);
		teller.getPersonAgent().setFunds(400.0);
		teller.manager = manager;
		AccountSystem.sharedInstance().addAccount(1);
		
		//precondition
		//empty customer log
		assertEquals("MockCustomer should have an empty event log before the Teller scheduler is called. Instead the MockWaiter's "
				+ "event log reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Teller state should be ReadyForCustomers.", teller.getState(), "ReadyForCustomers");
		assertEquals("Teller's person funds should be 400.0.", teller.getPersonAgent().getFunds(), 400.0);
		assertEquals("Teller's list of customers should be empty", teller.getCustomers().size(), 0);
		
		teller.msgAssigningCustomer(customer);
		
		assertEquals("Teller's list of customers size should now be 1", teller.getCustomers().size(), 1);
		assertEquals("Teller's first customer in the list should have state NeedingAssistance", 
				teller.getCustomer(0).getState(), "NeedingAssistance");
		teller.getCustomers().get(0).setAccount(1);
		
		teller.pickAndExecuteAnAction();
		
		assertEquals("Teller's first customer in the list should have the state AskedAssistance", 
				teller.getCustomer(0).getState(), "AskedAssistance");
		assertEquals("Teller's first customer in the list should have 0 dollars in his account", 
				teller.getCustomer(0).getMoneyToDeposit(),0.0);
				
		teller.msgDepositMoney(1,100.0);
		
		assertEquals("Teller's first customer in the list should have the state DepositingMoney", 
				teller.getCustomer(0).getState(), "DepositingMoney");
		assertEquals("Teller's first customer in the list should have account number 1", 
				teller.getCustomer(0).getAccount(), 1);
		assertEquals("Teller's first customer in the list should have 100 dollars in his account", 
				teller.getCustomer(0).getMoneyToDeposit(), 100.0);
		
		
	}
	
	
	//Withdraw w/ Account
	public void testTwoBankInteraction(){
		teller.setPerson(person);
		teller.getPersonAgent().setFunds(400.0);
		teller.manager = manager;
		AccountSystem.sharedInstance().addAccount(1);
		
		//precondition
		//empty customer log
		assertEquals("MockCustomer should have an empty event log before the Teller scheduler is called. Instead the MockWaiter's "
				+ "event log reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Teller state should be ReadyForCustomers.", teller.getState(), "ReadyForCustomers");
		assertEquals("Teller's person funds should be 400.0.", teller.getPersonAgent().getFunds(), 400.0);
		assertEquals("Teller's list of customers should be empty", teller.getCustomers().size(), 0);
		
		teller.msgAssigningCustomer(customer);
		
		assertEquals("Teller's list of customers size should now be 1", teller.getCustomers().size(), 1);
		assertEquals("Teller's first customer in the list should have state NeedingAssistance", 
				teller.getCustomer(0).getState(), "NeedingAssistance");
		teller.getCustomers().get(0).setAccount(1);
		
		teller.pickAndExecuteAnAction();
		
		assertEquals("Teller's first customer in the list should have the state AskedAssistance", 
				teller.getCustomer(0).getState(), "AskedAssistance");
				
		teller.msgWithdrawMoney(1,100.0);
		
		assertEquals("Teller's first customer in the list should have 100 dollars in his account", 
				teller.getCustomer(0).getMoneyToWithdraw(),100.0);
		assertEquals("Teller's first customer in the list should have the state WithdrawingMoney", 
				teller.getCustomer(0).getState(), "WithdrawingMoney");
		assertEquals("Teller's first customer in the list should have account number 1", 
				teller.getCustomer(0).getAccount(), 1);
		
		teller.pickAndExecuteAnAction();
		
		assertEquals("Teller's first customer in the list should have the state Leaving", 
				teller.getCustomer(0).getState(), "Leaving");
		
		assertEquals("Teller's first customer in the list should have 0 dollars in his account now", 
				teller.getCustomer(0).getMoneyToWithdraw(),0.0);
		
	}
	
	
	//Bank Tellers get paid for the day and leave work
	public void testThreeBackInteraction(){
		teller.setPerson(person);
		teller.getPersonAgent().setFunds(400.0);
		teller.manager = manager;
		teller.state = TellerState.ReadyForCustomers;
		
		//precondition
		//empty customer log
		assertEquals("MockCustomer should have an empty event log before the Teller scheduler is called. Instead the MockWaiter's "
				+ "event log reads: " + customer.log.toString(), 0, customer.log.size());
		assertEquals("Teller state should be ReadyForCustomers.", teller.getState(), "ReadyForCustomers");
		assertEquals("Teller's person funds should be 400.0.", teller.getPersonAgent().getFunds(), 400.0);
		
		teller.msgDoneWorking();
		
		assertEquals("Teller state should be DoneWorking.", teller.getState(), "DoneWorking");
		
		teller.pickAndExecuteAnAction();
		
		assertEquals("Teller state should be GettingPaycheck.", teller.getState(), "GettingPaycheck");
		assertEquals("Customer's x gui position/destination should be x teller",
				teller.tellerGui.getxDestination(),teller.tellerGui.getXmanager());
		assertEquals("Customer's y gui position/destination should be y teller",
				teller.tellerGui.getyDestination(),teller.tellerGui.getYmanager());
		
		teller.msgHereIsPaycheck(100.0);
		
		assertEquals("Teller's person funds should be 500.0 now since he was paid 100 for his day's work", teller.getPersonAgent().getFunds(), 500.0);
		assertEquals("Teller state should be ReceivedPaycheck.", teller.getState(), "ReceivedPaycheck");
		
		teller.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be x exit",
				teller.tellerGui.getxDestination(),teller.tellerGui.getXexit());
		assertEquals("Customer's y gui position/destination should be y exit",
				teller.tellerGui.getyDestination(),teller.tellerGui.getYexit());
		assertEquals("Teller state should be Done.", teller.getState(), "Gone");
			
	}

}
