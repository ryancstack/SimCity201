package bank;

import gui.Building;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import trace.AlertLog;
import trace.AlertTag;
import city.helpers.Directory;
import city.interfaces.Person;
import bank.gui.BankCustomerGui;
import bank.interfaces.*;
import agent.Agent;
import agent.Role;

public class BankCustomerRole extends Role implements BankCustomer {
    //data--------------------------------------------------------------------------------
	private BankTeller teller;
	public BankManager manager;

	//public Person person;
	private enum CustomerState {DoingNothing, Waiting, GoingToTeller, BeingHelped, AtManager, Done, Gone, WaitingForHelpResponse, InTransit, FinishedRole};

	private CustomerState state = CustomerState.DoingNothing;
	
	public BankCustomerGui customerGui;
	private Semaphore doneAnimation = new Semaphore(0,true);
	private Timer timer;
	
	private double moneyToDeposit = 0;
	private double moneyToWithdraw = 100;
	private double moneyRequired = 0;
	
	private int accountNumber = 0;
	private int tellerNumber = -1;  //hack initializer for unit tests (-1 as null)
	private String name;
    
	private String task;
	private String myLocation = "Bank";

	public BankCustomerRole(String task, double moneyToDeposit, double moneyRequired) {
		this.task = task;
		//this.manager = Directory.sharedInstance().getBanks().get(0).getManager();
		this.moneyRequired = moneyRequired;		
		this.moneyToDeposit = moneyToDeposit;
		customerGui = new BankCustomerGui(this);
    	List<Building> buildings = Directory.sharedInstance().getCityGui().getMacroAnimationPanel().getBuildings();
    	this.manager = (BankManager) Directory.sharedInstance().getAgents().get("Bank");
    	
		for(Building b : buildings) {
			if (b.getName() == myLocation) {
				b.addGui(customerGui);
			}
		}
		
	}
	
	//Constructor for unit testing
	public BankCustomerRole(String task, double moneyToDeposit, double moneyRequired, String name) {
		this.task = task;
		this.name = name;
		this.moneyRequired = moneyRequired;
		this.moneyToDeposit = moneyToDeposit;
		customerGui = new BankCustomerGui(this);
	}
	
	
    //messages from animation-------------------------------------------------------------
	public void msgAtTeller() {
		//from animation
		doneAnimation.release();
		state = CustomerState.BeingHelped;
		stateChanged();
	}
	public void msgAtManager() {
		//from animation
		doneAnimation.release();
		state = CustomerState.AtManager;
		stateChanged();
	}
	public void msgAnimationFinishedLeavingBank() {
		//from animation
		doneAnimation.release();
		state = CustomerState.Gone;
		stateChanged();
	}
    
	//setters----------------------------------------------------------------------------
	public void setManager(Agent agent){
		manager = (BankManagerAgent) agent;
	}
	public void setGui(BankCustomerGui gui){
		customerGui = gui;
	}
	/*public void setPerson(Person person){
		this.person = person;
	} */
    //messages----------------------------------------------------------------------------
	public void msgHowCanIHelpYou(BankTeller teller, int tellerNumber) {	
		state = CustomerState.GoingToTeller;
		this.teller = teller;
		this.tellerNumber = tellerNumber;
	    stateChanged();
	}
	
	public void msgLoanDenied() {
		state = CustomerState.Done;
		teller.msgThankYouForAssistance(this);
		stateChanged();
	}
	
	public void msgHereAreFunds(double funds) {
		getPersonAgent().setFunds(getPersonAgent().getFunds() + funds);
		state = CustomerState.Done;
		stateChanged();
	}
	
	public void msgHereIsYourAccount(int accountNumber) {
		getPersonAgent().setAccountNumber(accountNumber);
		state = CustomerState.BeingHelped;
		this.accountNumber = accountNumber;
		stateChanged();
	}
	
	public void msgDepositSuccessful() {
		state = CustomerState.Done;
		teller.msgThankYouForAssistance(this);
		stateChanged();
	}
	
	public void msgBankIsClosed() {
		state = CustomerState.Done;
		stateChanged();
	}
	
	
    //scheduler---------------------------------------------------------------------------
	
	public boolean pickAndExecuteAnAction(){
		if(state == CustomerState.DoingNothing){
			askForAssistance();
			return true;
		}
		if(state == CustomerState.GoingToTeller) {
			goToTeller();
			return true;
		}
		if(state == CustomerState.BeingHelped && task.contains("Rob")) {
			robBank();
			return true;
		}
		if(state == CustomerState.BeingHelped && accountNumber == 0){
			openAccount();
			return true;
		}
		if(state == CustomerState.BeingHelped && accountNumber != 0){
			if(state == CustomerState.BeingHelped && task.contains("Deposit")){
				depositMoney();
				return true;
			}
			else if(state == CustomerState.BeingHelped && task.contains("Withdraw")){
				withdrawMoney();
				return true;
			}
			else if(state == CustomerState.BeingHelped && task.contains("Loan")) {
				takeOutLoan();
				return true;
			}
			else {
				return false;
			}
		}
		if(state == CustomerState.Done) {
			leaveBank();
			return true;
		}
		if(state == CustomerState.Gone) {
			roleDone();
			return true;
		}
		return false;
	}
	


	//actions-----------------------------------------------------------------------------
    private void roleDone() {
		getPersonAgent().msgRoleFinished();
		state = CustomerState.FinishedRole;
	}
	private void askForAssistance() {
		AlertLog.getInstance().logMessage(AlertTag.BANKCUSTOMER, getPersonAgent().getName(), "I need assistance");
		customerGui.DoGoToManager();
		manager.msgINeedAssistance(this);
		if(task.contains("Rob")){
			customerGui.DoExplodeBank();
		}
		state = CustomerState.Waiting;
	}
	
	private void openAccount() {
		state = CustomerState.WaitingForHelpResponse;
		AlertLog.getInstance().logMessage(AlertTag.BANKCUSTOMER, getPersonAgent().getName(), "I need an account opened");
		teller.msgOpenAccount(this);
	}
	
	private void takeOutLoan() {
		state = CustomerState.WaitingForHelpResponse;
		AlertLog.getInstance().logMessage(AlertTag.BANKCUSTOMER, getPersonAgent().getName(), "I need to take out a loan");
		teller.msgIWantLoan(accountNumber, moneyRequired);
	}
	
	private void depositMoney() {
		state = CustomerState.WaitingForHelpResponse;
		AlertLog.getInstance().logMessage(AlertTag.BANKCUSTOMER, getPersonAgent().getName(), "I need to deposit money");
		teller.msgDepositMoney(accountNumber, moneyToDeposit);
		getPersonAgent().setFunds(getPersonAgent().getFunds() - moneyToDeposit);
		moneyToDeposit = 0; 
	}
	
	private void withdrawMoney() {
		state = CustomerState.WaitingForHelpResponse;
		AlertLog.getInstance().logMessage(AlertTag.BANKCUSTOMER, getPersonAgent().getName(), "I need to withdraw money");
		teller.msgWithdrawMoney(accountNumber, moneyToWithdraw);
	}
	
	private void robBank() {
		state = CustomerState.WaitingForHelpResponse;
		AlertLog.getInstance().logMessage(AlertTag.BANKCUSTOMER, getPersonAgent().getName(), "I am robbing the bank");
		teller.msgHoldUpBank(moneyRequired,this);
	}
	
	private void leaveBank() {
		state = CustomerState.InTransit;
		AlertLog.getInstance().logMessage(AlertTag.BANKCUSTOMER, getPersonAgent().getName(), "Leaving bank");
		if(task != "Rob"){
			teller.msgThankYouForAssistance(this);
		}
		customerGui.DoLeaveBank();
	}
	
	private void goToTeller() {
		state = CustomerState.InTransit;
		AlertLog.getInstance().logMessage(AlertTag.BANKCUSTOMER, getPersonAgent().getName(), "Going to bank teller");
		customerGui.DoGoToTeller(tellerNumber);
		/*try {
			doneAnimation.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}*/
		
	}
    //GUI Actions-------------------------------------------------------------------------
	
	
	//Getters/Setters for unit tests--------------------------------------------------------------
	/**
	 * @return the state
	 */
	public String getState() {
		return state.toString();
	}


	/**
	 * @return the moneyToDeposit
	 */
	public double getMoneyToDeposit() {
		return moneyToDeposit;
	}


	/**
	 * @return the moneyToWithdraw
	 */
	public double getMoneyToWithdraw() {
		return moneyToWithdraw;
	}


	/**
	 * @return the moneyRequired
	 */
	public double getMoneyRequired() {
		return moneyRequired;
	}


	/**
	 * @return the accountNumber
	 */
	public int getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param the accountNumber
	 */
	public void setAccountNumber(int accNum) {
		accountNumber = accNum;
	}
	
	/**
	 * @return the tellerNumber
	 */
	public int getTellerNumber() {
		return tellerNumber;
	}
	
	/**
	 * @return the task
	 */
	public String getTask() {
		return task;
	}
}
