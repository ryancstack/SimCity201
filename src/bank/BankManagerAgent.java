package bank;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import trace.AlertLog;
import trace.AlertTag;
import bank.gui.BankCustomerGui;
import bank.interfaces.*;
import agent.Agent;

public class BankManagerAgent extends Agent implements BankManager {
    //data--------------------------------------------------------------------------------
	private List<BankCustomer> customers = Collections.synchronizedList(new ArrayList<BankCustomer>());
	private List<MyBankTeller> tellers = Collections.synchronizedList(new ArrayList<MyBankTeller>());
	Bank bank;
	public class MyBankTeller {

		BankTeller teller;
	    BankTellerState state;
	    int tellerNum;
	    
	    public MyBankTeller(BankTeller teller, BankTellerState state){
	    	this.teller = teller;
	    	this.state = state;
	    	this.tellerNum = -1; // hack to test unit test for initial state (-1 equivalent to null)
	    }
	    /**
		 * @return the teller
		 */
		public BankTeller getTeller() {
			return teller;
		}

		/**
		 * @return the state
		 */
		public String getState() {
			return state.toString();
		}

		/**
		 * @return the tellerNum
		 */
		public int getTellerNum() {
			return tellerNum;
		}
	}
	public enum BankTellerState {GotToWork, Idle, Busy, GettingPay, ReceivedPay};
	private int tellerNum = 0;
	private String name;
	
	public BankManagerAgent(){
		this.name = "siri";
	}
	
    //messages----------------------------------------------------------------------------
	public void msgINeedAssistance(BankCustomer customer) {
		AlertLog.getInstance().logMessage(AlertTag.BANKMANAGER, getName(), "A customer needs assistance");
		synchronized(this.customers){
			customers.add(customer);
		}
	    stateChanged();
	}

	public void msgTellerFree(BankTeller teller) {
		AlertLog.getInstance().logMessage(AlertTag.BANKMANAGER, getName(), "Bank Teller is now free");
		for(MyBankTeller tempTeller : tellers){
			if(tempTeller.teller == teller){
				tempTeller.state = BankTellerState.Idle;
			}
		}
	    stateChanged();
	}
	
	public void msgHereForWork(BankTeller teller) {
		AlertLog.getInstance().logMessage(AlertTag.BANKMANAGER, getName(), "Here for work");
		synchronized(this.tellers){
			print("A new teller has arrived to work. Adding him to Teller list.");
			tellers.add(new MyBankTeller(teller, BankTellerState.GotToWork));
		}
		stateChanged();
	}
	
	public void msgCollectPay(BankTeller teller) {
		AlertLog.getInstance().logMessage(AlertTag.BANKMANAGER, getName(), "Collecting pay");
		for(MyBankTeller tempTeller: tellers){
			if(tempTeller.teller == teller){
				tempTeller.state = BankTellerState.GettingPay;
			}
		}
	}
	
	public void msgTellerLeavingWork(BankTeller teller) {
		AlertLog.getInstance().logMessage(AlertTag.BANKMANAGER, getName(), "Leaving work");
		synchronized(this.tellers) {
			for(MyBankTeller tempTeller: tellers){
				if(tempTeller.teller == teller){
					tellers.remove(tempTeller);
					break;
				}
			}
			tellerNum--;
		}
		stateChanged();
	}
    
    //scheduler---------------------------------------------------------------------------
	@Override
	public boolean pickAndExecuteAnAction() {
		if(bank.isOpen) {
			synchronized(this.tellers){
				for(MyBankTeller tempTeller: tellers){
					if(tempTeller.state == BankTellerState.GotToWork){
						AssignTellerToRegister(tempTeller);
					}
				}
			}
			synchronized(this.tellers){
				for(MyBankTeller tempTeller: tellers){
					if(tempTeller.state == BankTellerState.Idle){
						if(customers.size() != 0){
							AssignCustomerToTeller(customers.get(0),tempTeller);
							return true;
						}
					}
				}
			}
			synchronized(this.tellers){
				for(MyBankTeller tempTeller: tellers){
					if(tempTeller.state == BankTellerState.GettingPay){
						GiveTellerPay(tempTeller);
					}
				}
			}
		}
		else {
			synchronized(customers) {
				for(BankCustomer customer : customers) {
					customer.msgBankIsClosed();
				}
			}
		}
		return false;
	}
    //actions-----------------------------------------------------------------------------
	
	private void AssignTellerToRegister(MyBankTeller myTeller) {
		AlertLog.getInstance().logMessage(AlertTag.BANKMANAGER, getName(), "Assigning teller to register");
		myTeller.tellerNum = this.tellerNum;
		this.tellerNum++;
	    myTeller.teller.msgGoToRegister(myTeller.tellerNum);
	    myTeller.state = BankTellerState.Idle;
	}
	
	private void AssignCustomerToTeller(BankCustomer customer, MyBankTeller myTeller) {
		AlertLog.getInstance().logMessage(AlertTag.BANKMANAGER, getName(), "A customer to teller");
	    myTeller.teller.msgAssigningCustomer(customer);
	    myTeller.state = BankTellerState.Busy;
	    customers.remove(customer);
	    stateChanged();
	}
	
	private void GiveTellerPay(MyBankTeller myTeller) {
		AlertLog.getInstance().logMessage(AlertTag.BANKMANAGER, getName(), "Giving teller his paycheck");
		myTeller.teller.msgHereIsPaycheck(100.0); //100 is amount of money from unlimited bank funds
		myTeller.state = BankTellerState.ReceivedPay;
	}
	
	//getters for unit test-----------------------------------------------------------------
	/**
	 * @return the tellerNum
	 */
	public int getTellerNum() {
		return tellerNum;
	}

	/**
	 * @return the customers
	 */
	public List<BankCustomer> getCustomers() {
		return customers;
	}

	/**
	 * @return a specific teller
	 */
	public MyBankTeller getTeller(int tellerInstance) {
		return tellers.get(tellerInstance);
	}
	
	/**
	 * @return the tellers
	 */
	public List<MyBankTeller> getTellers() {
		return tellers;
	}

	public Bank getBank() {
		return bank;
	}

	public void setBank(Bank bank) {
		this.bank = bank;
	}
	
}
