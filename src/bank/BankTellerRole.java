package bank;

import gui.Building;

import java.util.ArrayList;

import city.helpers.Directory;
import city.interfaces.Person;

import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import trace.AlertLog;
import trace.AlertTag;
import bank.gui.BankTellerGui;
import bank.helpers.AccountSystem;
import bank.interfaces.*;
import agent.Role;

public class BankTellerRole extends Role implements BankTeller {
    //data--------------------------------------------------------------------------------
	private List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
	public class MyCustomer {
	    BankCustomer customer;
	    int accountNumber;
	    CustomerState custState;
	    double moneyToWithdraw;
	    double moneyToDeposit;
	    
	    public MyCustomer(BankCustomer customer, CustomerState state){
	    	this.customer = customer;
	    	this.custState = state;
	    }
	    public MyCustomer(BankCustomer customer, CustomerState state, double moneyDemanded){
	    	this.customer = customer;
	    	this.custState = state;
	    	this.moneyToWithdraw = moneyDemanded;
	    }
	    public String getState(){
	    	return custState.toString();
	    }
	    public void setAccount(int i){
	    	accountNumber = i;
	    }
	    public int getAccount(){
	    	return accountNumber;
	    }
	    public double getMoneyToWithdraw(){
	    	return moneyToWithdraw;
	    }
	    public double getMoneyToDeposit(){
	    	return moneyToDeposit;
	    }
    }
	
	private static final int MAXLOAN = 1000;
	private Semaphore doneAnimation = new Semaphore(0,true);
	private int registerNumber = 0;
	Timer timer = new Timer();
    public BankManager manager;
    public BankTellerGui tellerGui;
    public Person person;
    public enum TellerState {ArrivedAtWork, AtManager, GoingToRegister, ReadyForCustomers, DoneWorking, GettingPaycheck, ReceivedPaycheck, Gone, ToldManager};
    public TellerState state;
    
    private enum CustomerState {NeedingAssistance, 
    	AskedAssistance, OpeningAccount, OpenedAccount, 
    	DepositingMoney, WithdrawingMoney, LoanAccepted, 
    	LoanRejected, Robbing, Leaving};
    	
    private String myLocation;
    
    public BankTellerRole(String location){
    	state = TellerState.ArrivedAtWork;
    	myLocation = location;
    	manager = (BankManager) Directory.sharedInstance().getAgents().get(myLocation);
    	List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
    	tellerGui = new BankTellerGui(this);
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui(tellerGui);
			}
		}
    }
    
    //Constructor for unit test
    public BankTellerRole(){
    	state = TellerState.ReadyForCustomers;
    	tellerGui = new BankTellerGui(this);
    }
    
    public void setGui(BankTellerGui tellerGui){
    	this.tellerGui = tellerGui;
    }
    
    //messages----------------------------------------------------------------------------
	public void msgGoToRegister(int registerNumber) {
		this.registerNumber = registerNumber;
		state = TellerState.GoingToRegister;
		stateChanged();
		
	}
	public void msgAssigningCustomer(BankCustomer customer) {
		synchronized(this.customers){
			customers.add(new MyCustomer(customer, CustomerState.NeedingAssistance));
		}
	    stateChanged();
	}
	
	public void msgOpenAccount(BankCustomer customer) {
		int uniqueNum = AccountSystem.sharedInstance().newUniqueAccountNumber();
		AccountSystem.sharedInstance().addAccount(uniqueNum);
		for(MyCustomer tempCustomer : customers){
			if(tempCustomer.customer == customer){
				tempCustomer.accountNumber = uniqueNum;
				tempCustomer.custState = CustomerState.OpeningAccount;
			}
		}
	    stateChanged();
	}
	
	public void msgDepositMoney(int accountNumber, double money) {		
		for(MyCustomer tempCustomer : customers){
			if(tempCustomer.accountNumber == accountNumber){
				tempCustomer.moneyToDeposit = money;
				tempCustomer.custState = CustomerState.DepositingMoney;
			}
		}
	    stateChanged();
	}
	
	public void msgWithdrawMoney(int accountNumber, double money) {
		for(MyCustomer tempCustomer : customers){
			if(tempCustomer.accountNumber == accountNumber){
				tempCustomer.moneyToWithdraw = money;
				tempCustomer.custState = CustomerState.WithdrawingMoney;
			}
		}
	    stateChanged();
	}
	
	public void msgIWantLoan(int accountNumber, double moneyRequest) {
		for(MyCustomer customer : customers) {
			if(customer.accountNumber == accountNumber) {
				if(AccountSystem.sharedInstance().getAccounts().get(accountNumber).elligibleForLoan) {
					customer.custState = CustomerState.LoanAccepted;
				}
				else {
					customer.custState = CustomerState.LoanRejected;
				}
			}
		}
		stateChanged();
	}
	
	public void msgHoldUpBank(double moneyDemanded,BankCustomer person) {
		for(MyCustomer tempCustomer : customers) {
			if(tempCustomer.customer == person) {
				tempCustomer.moneyToWithdraw = moneyDemanded;
				tempCustomer.custState = CustomerState.Robbing;
			}
		}
		stateChanged();
		//person.getPersonAgent().setFunds(person.getPersonAgent().getFunds() + moneyDemanded);
	}
	
	public void msgThankYouForAssistance(BankCustomer customer) {
		for(MyCustomer tempCustomer : customers) {
			if(tempCustomer.equals(customer)) {
				tempCustomer.custState = CustomerState.Leaving;
			}
		}	
	}
	
	public void msgDoneWorking() {
		state = TellerState.DoneWorking;
		stateChanged();
	}
	
	public void msgHereIsPaycheck(double paycheck){
		getPersonAgent().setFunds(getPersonAgent().getFunds() + paycheck);
		state = TellerState.ReceivedPaycheck;
		stateChanged();
	}
	
	
    //scheduler---------------------------------------------------------------------------
	public boolean pickAndExecuteAnAction(){
		if(state == TellerState.ArrivedAtWork) {
			DoGoCheckInWithManager();
			return true;
		}
		if(state == TellerState.AtManager) {
			TellerAtWork();
			return true;
		}
		if(state == TellerState.GoingToRegister) {
			DoGoToRegister();
			return true;
		}
		if(state == TellerState.ReadyForCustomers) {
			synchronized(customers){
				for(MyCustomer tempCustomer: customers){
					if(tempCustomer.custState == CustomerState.Robbing){
						GiveUpMoney(tempCustomer);
						return true;
					}
				}
			}
			synchronized(customers){
				for(MyCustomer tempCustomer: customers){
					if(tempCustomer.custState == CustomerState.NeedingAssistance){
						OfferAssistance(tempCustomer);
						return true;
					}
				}
			}
			synchronized(customers){
				for(MyCustomer tempCustomer: customers){
					if(tempCustomer.custState == CustomerState.OpeningAccount){
						OpenedAccount(tempCustomer);
						return true;
					}
				}
			}
			synchronized(customers){
				for(MyCustomer tempCustomer: customers){
					if(tempCustomer.custState == CustomerState.DepositingMoney){
						DepositMoney(tempCustomer);
						return true;
					}
				}
			}
			synchronized(customers){
				for(MyCustomer tempCustomer: customers){
					if(tempCustomer.custState == CustomerState.WithdrawingMoney){
						WithdrawMoney(tempCustomer);
						return true;
					}
				}
			}
			synchronized(customers){
				for(MyCustomer tempCustomer: customers){
					if(tempCustomer.custState == CustomerState.LoanAccepted){
						GiveLoan(tempCustomer);
						return true;
					}
				}
			}
			synchronized(customers){
				for(MyCustomer tempCustomer: customers){
					if(tempCustomer.custState == CustomerState.LoanRejected){
						RejectLoan(tempCustomer);
						return true;
					}
				}
			}
			synchronized(customers){
				for(MyCustomer tempCustomer: customers){
					if(tempCustomer.custState == CustomerState.Leaving){
						CustomerFinished(tempCustomer);
						return true;
					}
				}
			}
			manager.msgTellerFree(this);
			return false;
		}
		if(state == TellerState.DoneWorking) {
			GetPayCheck();
			return true;
		}
		if(state == TellerState.ReceivedPaycheck) {
			LeaveBank();
			return true;
		}
		return false;
	}

	//actions-----------------------------------------------------------------------------
	private void DoGoCheckInWithManager() {
		tellerGui.DoGoToManager();
		try {
			doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		state = TellerState.AtManager;
		stateChanged();
	}
	private void TellerAtWork() {
		AlertLog.getInstance().logMessage(AlertTag.BANKTELLER, getPersonAgent().getName(), "Telling manager I am at work");
		state = TellerState.ToldManager;
		manager.msgHereForWork(this);
	}
    private void DoGoToRegister() {
		tellerGui.DoGoToRegister(registerNumber);
		try {
			doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		state = TellerState.ReadyForCustomers;
		manager.msgTellerFree(this);
	}
	private void OfferAssistance(MyCustomer account) {
		AlertLog.getInstance().logMessage(AlertTag.BANKTELLER, getPersonAgent().getName(), "What do you need help with?");
		account.customer.msgHowCanIHelpYou(this,registerNumber);
		account.custState = CustomerState.AskedAssistance;
	}
	
	private void OpenedAccount(MyCustomer myCustomer) {
		AlertLog.getInstance().logMessage(AlertTag.BANKTELLER, getPersonAgent().getName(), "Opening up your account");
		myCustomer.customer.msgHereIsYourAccount(myCustomer.accountNumber);
		myCustomer.custState = CustomerState.OpenedAccount;
	}
	
	private void DepositMoney(MyCustomer myCustomer) {
		AlertLog.getInstance().logMessage(AlertTag.BANKTELLER, getPersonAgent().getName(), "Depositing money into account");
		timer.schedule(new TimerTask() {
			public void run() {
				//run timer for gui to stop
			}
		},
		5000);
		AccountSystem.sharedInstance().getAccounts().get(myCustomer.accountNumber).depositMoney(myCustomer.moneyToDeposit);
		myCustomer.customer.msgDepositSuccessful();
		myCustomer.custState = CustomerState.Leaving;
	}
	
	private void WithdrawMoney(MyCustomer myCustomer) {
		AlertLog.getInstance().logMessage(AlertTag.BANKTELLER, getPersonAgent().getName(), "Withdrawing money from account");	
		timer.schedule(new TimerTask() {
			public void run() {
				//run timer for gui to stop
			}
		},
		5000);
		AccountSystem.sharedInstance().getAccounts().get(myCustomer.accountNumber).withdrawMoney(myCustomer.moneyToWithdraw);
		myCustomer.customer.msgHereAreFunds(myCustomer.moneyToWithdraw);
		myCustomer.moneyToWithdraw = 0;
		myCustomer.custState = CustomerState.Leaving;
	}
	
	private void CustomerFinished(MyCustomer myCustomer){
		print("Finished aiding cusomter");
		customers.remove(myCustomer);
	}
	
	private void GiveLoan(MyCustomer myCustomer) {
		print("Giving loan");
		timer.schedule(new TimerTask() {
			public void run() {
				//run timer for gui to stop
			}
		},
		5000);
		AccountSystem.sharedInstance().getAccounts().get(myCustomer.accountNumber).loanAccepted(MAXLOAN);
		myCustomer.customer.msgHereAreFunds(MAXLOAN);
		myCustomer.custState = CustomerState.Leaving;
	}
	
	private void RejectLoan(MyCustomer myCustomer) {
		AlertLog.getInstance().logMessage(AlertTag.BANKTELLER, getPersonAgent().getName(), "Rejecting loan");
		timer.schedule(new TimerTask() {
			public void run() {
				//run timer for gui to stop
			}
		},
		5000);
		myCustomer.customer.msgLoanDenied();
		myCustomer.custState = CustomerState.Leaving;
	}
	
	private void GiveUpMoney(MyCustomer myCustomer) {
		AlertLog.getInstance().logMessage(AlertTag.BANKTELLER, getPersonAgent().getName(), "Giving robber money");
		myCustomer.customer.msgHereAreFunds(myCustomer.moneyToWithdraw);
		myCustomer.custState = CustomerState.Leaving;
	}
	
	private void GetPayCheck(){
		AlertLog.getInstance().logMessage(AlertTag.BANKTELLER, getPersonAgent().getName(), "Getting paycheck");
		tellerGui.DoGoToManager();
		manager.msgCollectPay(this);
		/*	doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		state = TellerState.GettingPaycheck;
	}
	
	private void LeaveBank() {
		AlertLog.getInstance().logMessage(AlertTag.BANKTELLER, getPersonAgent().getName(), "Leaving bank");
		tellerGui.DoLeaveBank();
		manager.msgTellerLeavingWork(this);
		/*try {
			doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		state = TellerState.Gone;
		getPersonAgent().msgRoleFinished();
	}
	//animation messages-------------------------------------------------------------------
	public void msgAtRegister() {
		doneAnimation.release();
	}
	public void msgAtManager() {
		doneAnimation.release();
	}
	public void msgAnimationFinishedLeavingBank() {
		doneAnimation.release();
	}
	
	//Getters for unit testing--------------------------------------------------------------
	/**
	 * @return a customer
	 */
	public MyCustomer getCustomer(int customerInstance) {
		return customers.get(customerInstance);
	}

	/**
	 * @return the customers
	 */
	public List<MyCustomer> getCustomers() {
		return customers;
	}
	
	/**
	 * @return the registerNumber
	 */
	public int getRegisterNumber() {
		return registerNumber;
	}

	/**
	 * @return the state
	 */
	public String getState() {
		return state.toString();
	}
	
	/**
	 * @param ManagerAgent
	 */
	public void setManager(BankManagerAgent agent){
		manager = agent;
	}
	 

}
