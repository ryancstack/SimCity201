package bank.test;

import java.util.HashMap;
import java.util.Map;

import bank.Bank;
import bank.BankManagerAgent;
import bank.BankTellerRole;
import bank.interfaces.BankTeller;
import bank.test.mock.MockBankCustomer;
import bank.test.mock.MockBankManager;
import bank.test.mock.MockBankTeller;
import market.MarketWorkerRole;
import market.MarketWorkerRole.orderState;
import market.test.mock.*;
import junit.framework.*;

public class BankManagerTest extends TestCase {

	//these are instantiated for each test separately via the setUp() method.
	BankManagerAgent manager;
	MockBankCustomer customer1;
	MockBankCustomer customer2;
	MockBankCustomer customer3;
	MockBankCustomer customer4;
	MockBankCustomer customer5;
	MockBankCustomer customer6;
	MockBankCustomer customer7;
	MockBankTeller teller1;
	MockBankTeller teller2;
	MockBankTeller teller3;
	MockBankTeller teller4;
	MockBankTeller teller5;
	MockBankTeller teller6;
	
	/**
	 * This method is run before each test. You can use it to instantiate the class variables
	 * for your agent and mocks, etc.
	 */
	public void setUp() throws Exception{
		super.setUp();
		
		manager = new BankManagerAgent();
		teller1 = new MockBankTeller("mockbankteller1");	
		teller2 = new MockBankTeller("mockbankteller2");	
		teller3 = new MockBankTeller("mockbankteller3");	
		teller4 = new MockBankTeller("mockbankteller4");	
		teller5 = new MockBankTeller("mockbankteller5");	
		teller6 = new MockBankTeller("mockbankteller6");	
		customer1 = new MockBankCustomer("mockcustomer1",300,0);
		customer2 = new MockBankCustomer("mockcustomer2",300,0);
		customer3 = new MockBankCustomer("mockcustomer3",300,0);
		customer4 = new MockBankCustomer("mockcustomer4",300,0);
		customer5 = new MockBankCustomer("mockcustomer5",300,0);
		customer6 = new MockBankCustomer("mockcustomer6",300,0);
		customer7 = new MockBankCustomer("mockcustomer7",300,0);
	}	
	
	//Manager assigning bankteller to teller register
	public void testOneBankMangerInteraction(){
		assertEquals("Manager should initially have 0 tellers in his list of tellers",
				manager.getTellers().size(),0);
		assertEquals("Manager should have an empty event log. Manager's event log reads: "
				+ manager.log.toString(), 0, manager.log.size());
		
		manager.msgHereForWork(teller1);
		assertEquals("Manager's first teller in his list of tellers should not be assigned to a register", 
				manager.getTeller(0).getTellerNum(),-1);
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager should have 1 tellers in his list of tellers",manager.getTellers().size(),1);
		assertEquals("Manager's first teller in his list of tellers should be assigned to register 0", 
				manager.getTeller(0).getTellerNum(),0);
		
		manager.msgHereForWork(teller2);
		assertEquals("Manager's second teller in his list of tellers should not be assigned to a register", 
				manager.getTeller(1).getTellerNum(),-1);
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager should have 2 tellers in his list of tellers",manager.getTellers().size(),2);
		assertEquals("Manager's second teller in his list of tellers should be assigned to register 1", 
				manager.getTeller(1).getTellerNum(),1);
		assertEquals("Manager's first teller in his list of tellers should still have state Idle since no customers", 
				manager.getTeller(0).getState(),"Idle");
		
	}
	
	//Manager assigning bank customer to a bank teller
	public void testTwoBankManagerInteraction(){
		assertEquals("Manager should initially have 0 tellers in his list of tellers",
				manager.getTellers().size(),0);
		assertEquals("Manager should have an empty event log. Manager's event log reads: "
				+ manager.log.toString(), 0, manager.log.size());
		
		manager.msgHereForWork(teller1);
		assertEquals("Manager should have 1 tellers in his list of tellers",manager.getTellers().size(),1);
		assertEquals("Manager's first teller in his list of tellers should not be assigned to a register", 
				manager.getTeller(0).getTellerNum(),-1);
		assertEquals("Manager's first teller's state should be GotToWork", 
				manager.getTeller(0).getState(),"GotToWork");
		
		assertEquals("Manager's should have 0 customers in his list", 
				manager.getCustomers().size(),0);
		
		manager.msgINeedAssistance(customer1);
		manager.msgINeedAssistance(customer2);
		
		assertEquals("Manager's should have 2 customers in his list", 
				manager.getCustomers().size(),2);
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager should have 1 tellers in his list of tellers",manager.getTellers().size(),1);
		assertEquals("Manager's first teller in his list of tellers should be assigned to register 0", 
				manager.getTeller(0).getTellerNum(),0);
		assertEquals("Manager's first teller's state should be Busy because taking care of 1st customer in customerlist", 
				manager.getTeller(0).getState(),"Busy");
		assertEquals("Manager's should have 1 customers in his list since the first teller is taking care of the 1st customer and 1st customer is deleted", 
				manager.getCustomers().size(),1);
		
		
		manager.pickAndExecuteAnAction();
		
		manager.msgHereForWork(teller2);
		assertEquals("Manager's second teller in his list of tellers should not be assigned to a register", 
				manager.getTeller(1).getTellerNum(),-1);
		assertEquals("Manager's second teller's state should be GotToWork", 
				manager.getTeller(1).getState(),"GotToWork");
		
		assertEquals("Manager should have 2 tellers in his list of tellers",manager.getTellers().size(),2);
		
		assertEquals("Manager's first teller in his list of tellers should have state Busy since taking care of 1st customer", 
				manager.getTeller(0).getState(),"Busy");
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager's second teller in his list of tellers should be assigned to register 1", 
				manager.getTeller(1).getTellerNum(),1);
		assertEquals("Manager's second teller in his list of tellers should still have state Busy because taking care of remaining customer", 
				manager.getTeller(1).getState(),"Busy");
		assertEquals("Manager's should have 0 customers in his list since the second teller is taking care of the remaining customer", 
				manager.getCustomers().size(),0);
	}
	
	//Manager not able to assign customer to bank teller because all tellers are full with customers
	public void testThreeBankManagerInteraction(){
		assertEquals("Manager should initially have 0 tellers in his list of tellers",
				manager.getTellers().size(),0);
		assertEquals("Manager should have an empty event log. Manager's event log reads: "
				+ manager.log.toString(), 0, manager.log.size());
		
		manager.msgHereForWork(teller1);
		assertEquals("Manager should have 1 tellers in his list of tellers",manager.getTellers().size(),1);
		assertEquals("Manager's first teller in his list of tellers should not be assigned to a register", 
				manager.getTeller(0).getTellerNum(),-1);
		assertEquals("Manager's first teller's state should be GotToWork", 
				manager.getTeller(0).getState(),"GotToWork");
		
		assertEquals("Manager's should have 0 customers in his list", 
				manager.getCustomers().size(),0);
		
		manager.msgINeedAssistance(customer1);
		manager.msgINeedAssistance(customer2);
		manager.msgINeedAssistance(customer3);
		
		assertEquals("Manager's should have 3 customers in his list", 
				manager.getCustomers().size(),3);
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager should have 1 tellers in his list of tellers",manager.getTellers().size(),1);
		assertEquals("Manager's first teller in his list of tellers should be assigned to register 0", 
				manager.getTeller(0).getTellerNum(),0);
		assertEquals("Manager's first teller's state should be Busy because taking care of 1st customer in customerlist", 
				manager.getTeller(0).getState(),"Busy");
		assertEquals("Manager's should have 2 customers in his list since the first teller is taking care of the 1st customer and 1st customer is deleted", 
				manager.getCustomers().size(),2);
		
		
		manager.pickAndExecuteAnAction();
		
		manager.msgHereForWork(teller2);
		assertEquals("Manager's second teller in his list of tellers should not be assigned to a register", 
				manager.getTeller(1).getTellerNum(),-1);
		assertEquals("Manager's second teller's state should be GotToWork", 
				manager.getTeller(1).getState(),"GotToWork");
		
		assertEquals("Manager should have 2 tellers in his list of tellers",manager.getTellers().size(),2);
		
		assertEquals("Manager's first teller in his list of tellers should have state Busy since taking care of 1st customer", 
				manager.getTeller(0).getState(),"Busy");
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager's second teller in his list of tellers should be assigned to register 1", 
				manager.getTeller(1).getTellerNum(),1);
		assertEquals("Manager's second teller in his list of tellers should still have state Busy because taking care of remaining customer", 
				manager.getTeller(1).getState(),"Busy");
		assertEquals("Manager's should have 1 customers in his list since the second teller is taking care of the 2nd customer", 
				manager.getCustomers().size(),1);
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager's should still have 1 customers in his list since all bank tellers are busy with customers. Remaining customer"
				+ "must wait to be taken care of", 
				manager.getCustomers().size(),1);
		
		manager.msgTellerFree(manager.getTeller(0).getTeller());
		
		assertEquals("Manager's 1st teller should have state Idle because manager was given the message that he is free",
				manager.getTeller(0).getState(), "Idle");
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager's 1st teller should have state Busy now because taking care of last waiting customer",
				manager.getTeller(0).getState(), "Busy");
		assertEquals("Manager's should have 0 customers in his list since the last waiting customer is taken care of the now free bank teller", 
				manager.getCustomers().size(),0);
		
		
	}
	
	//Manager paying bank tellers and allowing to leave when finished work
	public void testFourBackManagerInteraction(){
		assertEquals("Manager should initially have 0 tellers in his list of tellers",
				manager.getTellers().size(),0);
		assertEquals("Manager should have an empty event log. Manager's event log reads: "
				+ manager.log.toString(), 0, manager.log.size());
		
		manager.msgHereForWork(teller1);
		assertEquals("Manager's first teller in his list of tellers should not be assigned to a register", 
				manager.getTeller(0).getTellerNum(),-1);
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager should have 1 tellers in his list of tellers",manager.getTellers().size(),1);
		assertEquals("Manager's first teller in his list of tellers should be assigned to register 0", 
				manager.getTeller(0).getTellerNum(),0);
		
		manager.msgHereForWork(teller2);
		assertEquals("Manager's second teller in his list of tellers should not be assigned to a register", 
				manager.getTeller(1).getTellerNum(),-1);
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager should have 2 tellers in his list of tellers",manager.getTellers().size(),2);
		assertEquals("Manager's second teller in his list of tellers should be assigned to register 1", 
				manager.getTeller(1).getTellerNum(),1);
		assertEquals("Manager's first teller in his list of tellers should still have state Idle since no customers", 
				manager.getTeller(0).getState(),"Idle");
		
		manager.msgCollectPay(manager.getTeller(0).getTeller());
		
		assertEquals("Manager's first teller in his list of tellers should have state GettingPay since he wants to leave for work", 
				manager.getTeller(0).getState(),"GettingPay");
		
		manager.pickAndExecuteAnAction();
		
		manager.msgCollectPay(manager.getTeller(1).getTeller());
		
		assertEquals("Manager's first teller in his list of tellers should have state ReceivedPay since he was given paycheck", 
				manager.getTeller(0).getState(),"ReceivedPay");
		assertEquals("Manager's second teller in his list of tellers should have state GettingPay since he wants to leave for work", 
				manager.getTeller(1).getState(),"GettingPay");
		
		manager.msgTellerLeavingWork(manager.getTeller(0).getTeller());
		
		assertEquals("Manager should now have 1 teller now since he has given paycheck to the last teller and the teller has left work", 
				manager.getTellerNum(),1);
		assertEquals("Manager should now have 1 teller now since he has given paycheck to the last teller and the teller has left work", 
				manager.getTellers().size(),1);
		
		
		manager.pickAndExecuteAnAction();
		
		assertEquals("Manager's first teller in his list of tellers should have state ReceivedPay since he was given paycheck", 
				manager.getTeller(0).getState(),"ReceivedPay");
		
		manager.msgTellerLeavingWork(manager.getTeller(0).getTeller());
		
		assertEquals("Manager should now have 0 tellers now since he has given paycheck to the last teller and the teller has left work", 
				manager.getTellers().size(),0);
		assertEquals("Manager unique teller number to assign tellers to registers should be back to 0 now that he has no tellers left", 
				manager.getTellerNum(),0);
		
		
	}
	

}
