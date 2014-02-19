package bank.test;

import java.util.HashMap;
import java.util.Map;

import market.test.mock.MockPerson;
import bank.Bank;
import bank.BankCustomerRole;
import bank.BankTellerRole;
import bank.helpers.AccountSystem;
import bank.interfaces.BankTeller;
import bank.test.mock.*;
import junit.framework.*;

public class BankCustomerTest extends TestCase {

	//these are instantiated for each test separately via the setUp() method.
	BankCustomerRole customer;
	MockBankManager manager;
	MockBankTeller teller;
	MockPerson person;
	
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
		manager = new MockBankManager("mockbankmanager");
		teller = new MockBankTeller("mockbankteller");		
		person = new MockPerson("person");
		//customer = new MockBankCustomer("mockcustomer",300,0);
	}	
	
	//Deposit w/ Account
	public void testOneBankInteraction(){
		customer = new BankCustomerRole("Deposit",300.0,0,"FakeCustomer");
		customer.manager = manager;
		customer.setPerson(person);
		customer.setAccountNumber(1);
		assertTrue(customer.getPersonAgent().equals(person));
		customer.getPersonAgent().setFunds(400.0);
		
		//precondition
		assertEquals("Customers account number should be 1", customer.getAccountNumber(),1);
		assertEquals("Customer should have the manager from setUp()", customer.manager,manager);
		assertEquals("Customer's state should be DoingNothing ",customer.getState(),"DoingNothing");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be waiting ",customer.getState(),"Waiting");
		assertEquals("Customer's x gui position/destination should be at x manager",
				customer.customerGui.getxDestination(),400);
		assertEquals("Customer's y gui position/destination should be at y manager",
				customer.customerGui.getyDestination(),68);
		
		
		assertEquals("Customer's teller number to go to should be -1 (null)",customer.getTellerNumber(),-1);
		customer.msgHowCanIHelpYou(teller, 0);
		assertEquals("Customer's teller number to go to should be 0",customer.getTellerNumber(),0);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be x teller",
				customer.customerGui.getxDestination(),customer.customerGui.getxTeller());
		assertEquals("Customer's y gui position/destination should be y teller",
				customer.customerGui.getyDestination(),customer.customerGui.getyTeller());
		
		customer.msgAtTeller();
		
		assertEquals("Customer's state should be BeingHelped ",customer.getState(),"BeingHelped");
		assertEquals("Customer's task should be Deposit ",customer.getTask(),"Deposit");
		
		assertEquals("Customer's funds should be 400 before depositing",
				customer.getPersonAgent().getFunds(),400.0);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be WaitingForHelpResponse ",
				customer.getState(),"WaitingForHelpResponse");
		assertEquals("Customer's funds should be 100 now that hes deposited",
				customer.getPersonAgent().getFunds(),100.0);
		
		customer.msgDepositSuccessful();
		assertEquals("Customer's x gui position/destination should be at x teller 1",
				customer.customerGui.getxDestination(),87);
		assertEquals("Customer's y gui position/destination should be at y teller 1",
				customer.customerGui.getyDestination(),51);
		
		assertEquals("Customer's state should be Done ",customer.getState(),"Done");
		customer.pickAndExecuteAnAction();
		
		customer.msgAnimationFinishedLeavingBank();
		
		assertEquals("Customer's x gui position/destination should be at x exit",
				customer.customerGui.getxDestination(),customer.customerGui.getxExit());
		assertEquals("Customer's y gui position/destination should be at y exit",
				customer.customerGui.getyDestination(),customer.customerGui.getyExit());
		assertEquals("Customer's state should be Gone ",customer.getState(),"Gone");
		
	}
	
	//Deposit w/o Account
	public void testTwoBankInteraction(){
		customer = new BankCustomerRole("Deposit",300.0,0,"FakeCustomer");
		customer.manager = manager;
		customer.setPerson(person);
		customer.setAccountNumber(0);
		assertTrue(customer.getPersonAgent().equals(person));
		customer.getPersonAgent().setFunds(400.0);
		
		//precondition
		assertEquals("Customers account number should not be set- 0", customer.getAccountNumber(),0);
		assertEquals("Customer should have the manager from setUp()", customer.manager,manager);
		assertEquals("Customer's state should be DoingNothing ",customer.getState(),"DoingNothing");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be waiting ",customer.getState(),"Waiting");
		assertEquals("Customer's x gui position/destination should be at x manager",
				customer.customerGui.getxDestination(),400);
		assertEquals("Customer's y gui position/destination should be at y manager",
				customer.customerGui.getyDestination(),68);
		
		
		assertEquals("Customer's teller number to go to should be -1 (null)",customer.getTellerNumber(),-1);
		
		customer.msgHowCanIHelpYou(teller, 1);
		
		assertEquals("Customer's teller number to go to should be 1",customer.getTellerNumber(),1);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be x teller",
				customer.customerGui.getxDestination(),customer.customerGui.getxTeller());
		assertEquals("Customer's y gui position/destination should be y teller",
				customer.customerGui.getyDestination(),customer.customerGui.getyTeller());
		
		customer.msgAtTeller();
		
		assertEquals("Customer's state should be BeingHelped ",customer.getState(),"BeingHelped");
		assertEquals("Customer's task should be Deposit ",customer.getTask(),"Deposit");
		
		assertEquals("Customer's funds should be 400 before depositing",
				customer.getPersonAgent().getFunds(),400.0);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be WaitingForHelpResponse ",customer.getState(),"WaitingForHelpResponse");
		assertNotSame("Customer's funds should not be 100 because he doesn't have an open account",customer.getPersonAgent().getFunds(),100.0);
		
		customer.msgHereIsYourAccount(1);
		
		assertEquals("Customer's account number should now be 1 ",customer.getAccountNumber(),1);
		assertEquals("Customer's state should now be BeingHelped ",customer.getState(),"BeingHelped");
		
		customer.pickAndExecuteAnAction();
		
		customer.msgDepositSuccessful();
		
		assertEquals("Customer's state should be Done ",customer.getState(),"Done");
		
		customer.msgAnimationFinishedLeavingBank();
		
		assertEquals("Customer's state should be Gone ",customer.getState(),"Gone");
		
		
		
		
	}
	
	//Withdraw w/ Account
	public void testThreeBankInteraction(){
		customer = new BankCustomerRole("Withdraw",300.0,0,"FakeCustomer");
		customer.manager = manager;
		customer.setPerson(person);
		customer.setAccountNumber(1);
		assertTrue(customer.getPersonAgent().equals(person));
		customer.getPersonAgent().setFunds(400.0);
		
		//precondition
		assertEquals("Customers account number should be 1", customer.getAccountNumber(),1);
		assertEquals("Customer should have the manager from setUp()", customer.manager,manager);
		assertEquals("Customer's state should be DoingNothing ",customer.getState(),"DoingNothing");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be waiting ",customer.getState(),"Waiting");
		assertEquals("Customer's x gui position/destination should be at x manager",
				customer.customerGui.getxDestination(),400);
		assertEquals("Customer's y gui position/destination should be at y manager",
				customer.customerGui.getyDestination(),68);
		
		
		assertEquals("Customer's teller number to go to should be -1 (null)",customer.getTellerNumber(),-1);
		customer.msgHowCanIHelpYou(teller, 1);
		assertEquals("Customer's teller number to go to should be 1",customer.getTellerNumber(),1);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be x teller",
				customer.customerGui.getxDestination(),customer.customerGui.getxTeller());
		assertEquals("Customer's y gui position/destination should be y teller",
				customer.customerGui.getyDestination(),customer.customerGui.getyTeller());
		
		customer.msgAtTeller();
		
		assertEquals("Customer's state should be BeingHelped ",customer.getState(),"BeingHelped");
		assertEquals("Customer's task should be Withdraw ",customer.getTask(),"Withdraw");
		
		assertEquals("Customer's funds should be 400 before withdrawing",
				customer.getPersonAgent().getFunds(),400.0);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be WaitingForHelpResponse ",
				customer.getState(),"WaitingForHelpResponse");
		
		customer.msgHereAreFunds(customer.getMoneyToWithdraw());
		
		assertEquals("Customer's funds should be 500 now that hes withdrawn 100",
				customer.getPersonAgent().getFunds(),500.0);
		assertEquals("Customer's state should be Done ",customer.getState(),"Done");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be at x exit",
				customer.customerGui.getxDestination(),customer.customerGui.getxExit());
		assertEquals("Customer's y gui position/destination should be at y exit",
				customer.customerGui.getyDestination(),customer.customerGui.getyExit());
		
		customer.msgAnimationFinishedLeavingBank();
		
		assertEquals("Customer's state should be Gone ",customer.getState(),"Gone");
	}
	
	//Withdraw w/o Account
	public void testFourBankInteraction(){
		customer = new BankCustomerRole("Withdraw",300.0,0,"FakeCustomer");
		customer.manager = manager;
		customer.setPerson(person);
		customer.setAccountNumber(0);
		assertTrue(customer.getPersonAgent().equals(person));
		customer.getPersonAgent().setFunds(400.0);
		
		//precondition
		assertEquals("Customers account number should be 0", customer.getAccountNumber(),0);
		assertEquals("Customer should have the manager from setUp()", customer.manager,manager);
		assertEquals("Customer's state should be DoingNothing ",customer.getState(),"DoingNothing");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be waiting ",customer.getState(),"Waiting");
		assertEquals("Customer's x gui position/destination should be at x manager",
				customer.customerGui.getxDestination(),400);
		assertEquals("Customer's y gui position/destination should be at y manager",
				customer.customerGui.getyDestination(),68);
		
		
		assertEquals("Customer's teller number to go to should be -1 (null)",customer.getTellerNumber(),-1);
		customer.msgHowCanIHelpYou(teller, 1);
		assertEquals("Customer's teller number to go to should be 1",customer.getTellerNumber(),1);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be x teller",
				customer.customerGui.getxDestination(),customer.customerGui.getxTeller());
		assertEquals("Customer's y gui position/destination should be y teller",
				customer.customerGui.getyDestination(),customer.customerGui.getyTeller());
		
		customer.msgAtTeller();
		
		assertEquals("Customer's state should be BeingHelped ",customer.getState(),"BeingHelped");
		assertEquals("Customer's task should be Withdraw ",customer.getTask(),"Withdraw");
		
		assertEquals("Customer's funds should be 400 before withdrawing",
				customer.getPersonAgent().getFunds(),400.0);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be WaitingForHelpResponse ",
				customer.getState(),"WaitingForHelpResponse");
		assertNotSame("Customer's funds should not be 500 because he doesn't have an open account",
				customer.getPersonAgent().getFunds(),500.0);
		
		customer.msgHereIsYourAccount(1);
		
		assertEquals("Customer's account number should now be 1 ",customer.getAccountNumber(),1);
		assertEquals("Customer's state should now be BeingHelped ",customer.getState(),"BeingHelped");
		
		customer.pickAndExecuteAnAction();
		
		customer.msgHereAreFunds(customer.getMoneyToWithdraw());
		
		assertEquals("Customer's funds should be 500 now that hes withdrawn 100",
				customer.getPersonAgent().getFunds(),500.0);
		assertEquals("Customer's state should be Done ",customer.getState(),"Done");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be at x exit",
				customer.customerGui.getxDestination(),customer.customerGui.getxExit());
		assertEquals("Customer's y gui position/destination should be at y exit",
				customer.customerGui.getyDestination(),customer.customerGui.getyExit());
		
		customer.msgAnimationFinishedLeavingBank();
		
		assertEquals("Customer's state should be Gone ",customer.getState(),"Gone");
	}
	
	//Loan w/ Good Credit (No taking a loan)
	public void testSixBankInteraction(){
		customer = new BankCustomerRole("Loan",0,1000.0,"FakeCustomer");
		customer.manager = manager;
		customer.setPerson(person);
		customer.setAccountNumber(1);
		assertTrue(customer.getPersonAgent().equals(person));
		customer.getPersonAgent().setFunds(400.0);
		
		//precondition
		assertEquals("Customers account number should be 1", customer.getAccountNumber(),1);
		assertEquals("Customer should have the manager from setUp()", customer.manager,manager);
		assertEquals("Customer's state should be DoingNothing ",customer.getState(),"DoingNothing");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be waiting ",customer.getState(),"Waiting");
		assertEquals("Customer's x gui position/destination should be at x manager",
				customer.customerGui.getxDestination(),400);
		assertEquals("Customer's y gui position/destination should be at y manager",
				customer.customerGui.getyDestination(),68);
		
		
		assertEquals("Customer's teller number to go to should be -1 (null)",customer.getTellerNumber(),-1);
		customer.msgHowCanIHelpYou(teller, 4);
		assertEquals("Customer's teller number to go to should be 4",customer.getTellerNumber(),4);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be x teller",
				customer.customerGui.getxDestination(),customer.customerGui.getxTeller());
		assertEquals("Customer's y gui position/destination should be y teller",
				customer.customerGui.getyDestination(),customer.customerGui.getyTeller());
		
		customer.msgAtTeller();
		
		assertEquals("Customer's state should be BeingHelped ",customer.getState(),"BeingHelped");
		assertEquals("Customer's task should be Loan ",customer.getTask(),"Loan");
		
		assertEquals("Customer's funds should be 400 before attempting to get a loan",
				customer.getPersonAgent().getFunds(),400.0);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be WaitingForHelpResponse ",
				customer.getState(),"WaitingForHelpResponse");
		
		customer.msgHereAreFunds(customer.getMoneyRequired());
		
		assertEquals("Customer's funds should be 1400 now that hes gotten a loan of $1000",
				customer.getPersonAgent().getFunds(),1400.0);
		assertEquals("Customer's state should be InTransit ",customer.getState(),"Done");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be at x exit",
				customer.customerGui.getxDestination(),customer.customerGui.getxExit());
		assertEquals("Customer's y gui position/destination should be at y exit",
				customer.customerGui.getyDestination(),customer.customerGui.getyExit());
		
		customer.msgAnimationFinishedLeavingBank();
		
		assertEquals("Customer's state should be Gone ",customer.getState(),"Gone");
	}
	
	//Loan w/ Bad Credit (Has taken loan)
	public void testSevenBankInteraction(){
		customer = new BankCustomerRole("Loan",0,1000.0,"FakeCustomer");
		customer.manager = manager;
		customer.setPerson(person);
		customer.setAccountNumber(1);
		assertTrue(customer.getPersonAgent().equals(person));
		customer.getPersonAgent().setFunds(400.0);
		AccountSystem.sharedInstance().addAccount(1);
		AccountSystem.sharedInstance().getAccounts().get(1).elligibleForLoan = false;
		boolean badCredit = AccountSystem.sharedInstance().getAccounts().get(1).elligibleForLoan;
		
		//precondition
		assertEquals("Customers account number should be 1", customer.getAccountNumber(),1);
		assertEquals("Customer should have the manager from setUp()", customer.manager,manager);
		assertEquals("Customer's state should be DoingNothing ",customer.getState(),"DoingNothing");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be waiting ",customer.getState(),"Waiting");
		assertEquals("Customer's x gui position/destination should be at x manager",
				customer.customerGui.getxDestination(),400);
		assertEquals("Customer's y gui position/destination should be at y manager",
				customer.customerGui.getyDestination(),68);
		
		
		assertEquals("Customer's teller number to go to should be -1 (null)",customer.getTellerNumber(),-1);
		customer.msgHowCanIHelpYou(teller, 4);
		assertEquals("Customer's teller number to go to should be 4",customer.getTellerNumber(),4);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be x teller",
				customer.customerGui.getxDestination(),customer.customerGui.getxTeller());
		assertEquals("Customer's y gui position/destination should be y teller",
				customer.customerGui.getyDestination(),customer.customerGui.getyTeller());
		
		customer.msgAtTeller();
		
		assertEquals("Customer's state should be BeingHelped ",customer.getState(),"BeingHelped");
		assertEquals("Customer's task should be Loan ",customer.getTask(),"Loan");
		
		assertEquals("Customer's funds should be 400 before attempting to get a loan with bad credit",
				customer.getPersonAgent().getFunds(),400.0);
		
		assertEquals("Customer's funds should be 400 before attempting to get a loan with bad credit",
				badCredit,AccountSystem.sharedInstance().getAccounts().get(customer.getAccountNumber()).elligibleForLoan);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be WaitingForHelpResponse ",
				customer.getState(),"WaitingForHelpResponse");
		
		customer.msgLoanDenied();
		
		assertEquals("Customer's funds should be 400 still now because his loan was denied",
				customer.getPersonAgent().getFunds(),400.0);
		assertEquals("Customer's state should be Done ",customer.getState(),"Done");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be InTransit ",customer.getState(),"InTransit");
		assertEquals("Customer's x gui position/destination should be at x exit",
				customer.customerGui.getxDestination(),customer.customerGui.getxExit());
		assertEquals("Customer's y gui position/destination should be at y exit",
				customer.customerGui.getyDestination(),customer.customerGui.getyExit());
		
		customer.msgAnimationFinishedLeavingBank();
		
		assertEquals("Customer's state should be Gone ",customer.getState(),"Gone");
	}
	//NON NORM ROB BANK
	public void testEightBankInteraction(){
		customer = new BankCustomerRole("Rob",0,1000.0,"Crook");
		customer.manager = manager;
		customer.setPerson(person);
		//no account number needed for robber
		assertTrue(customer.getPersonAgent().equals(person));
		customer.getPersonAgent().setFunds(400.0);
		
		//precondition
		assertEquals("Customer should have the manager from setUp()", customer.manager,manager);
		assertEquals("Customer's state should be DoingNothing ",customer.getState(),"DoingNothing");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be waiting ",customer.getState(),"Waiting");
		assertEquals("Customer's x gui position/destination should be at x manager",
				customer.customerGui.getxDestination(),400);
		assertEquals("Customer's y gui position/destination should be at y manager",
				customer.customerGui.getyDestination(),68);
		
		
		assertEquals("Customer's teller number to go to should be -1 (null)",customer.getTellerNumber(),-1);
		customer.msgHowCanIHelpYou(teller, 4);
		assertEquals("Customer's teller number to go to should be 4",customer.getTellerNumber(),4);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's x gui position/destination should be x teller",
				customer.customerGui.getxDestination(),customer.customerGui.getxTeller());
		assertEquals("Customer's y gui position/destination should be y teller",
				customer.customerGui.getyDestination(),customer.customerGui.getyTeller());
		
		customer.msgAtTeller();
		
		assertEquals("Customer's state should be BeingHelped ",customer.getState(),"BeingHelped");
		assertEquals("Customer's task should be Rob ",customer.getTask(),"Rob");
		
		assertEquals("Customer's funds should be 400 before robbing the bank",
				customer.getPersonAgent().getFunds(),400.0);
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be WaitingForHelpResponse ",
				customer.getState(),"WaitingForHelpResponse");
		
		customer.msgHereAreFunds(customer.getMoneyRequired());
		
		assertEquals("Customer's funds should be 1400 now because he stole 1000 from the bank",
				customer.getPersonAgent().getFunds(),1400.0);
		assertEquals("Customer's state should be Done ",customer.getState(),"Done");
		
		customer.pickAndExecuteAnAction();
		
		assertEquals("Customer's state should be InTransit ",customer.getState(),"InTransit");
		assertEquals("Customer's x gui position/destination should be at x exit",
				customer.customerGui.getxDestination(),customer.customerGui.getxExit());
		assertEquals("Customer's y gui position/destination should be at y exit",
				customer.customerGui.getyDestination(),customer.customerGui.getyExit());
		
		customer.msgAnimationFinishedLeavingBank();
		
		assertEquals("Customer's state should be Gone ",customer.getState(),"Gone");
	}
	
	
}
