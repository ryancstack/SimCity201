package bank.helpers;

import java.util.*;



public class AccountSystem {
	
	private Map<Integer,BankAccount> accounts = new HashMap<Integer,BankAccount>();
    private int uniqueAccountNumber = 1;
    
    private static AccountSystem sharedInstance = null;
    
    public AccountSystem() {
    	
    }
    
    public static AccountSystem sharedInstance() {
    	if(sharedInstance == null) {
    		sharedInstance = new AccountSystem();
    	}
    	return sharedInstance;
    }
    
    public int newUniqueAccountNumber(){
    	uniqueAccountNumber++;
    	return uniqueAccountNumber;
    }

    public void addAccount(int uniqueNum){
    	accounts.put(uniqueNum,new BankAccount());
    }
    
    public Map<Integer,BankAccount> getAccounts() {
    	return accounts;
    }
    
    public class BankAccount {
	    double totalFunds;
	    public boolean elligibleForLoan;
	    double debt;
	    //int accountNumber;
	    
	    public BankAccount(){
	    	totalFunds = 0;
	    	elligibleForLoan = true;
	    }
	    public void depositMoney(double money){
	    	totalFunds += money;
	    }
	    public void withdrawMoney(double money) {
	    	if(money >= totalFunds) {
	    		totalFunds = 0;
	    	}
	    	else {
	    		totalFunds -= money;
	    	}
	    }
	    public void loanAccepted(double money) {
	    	debt += money;
	    }
    }
	
}
