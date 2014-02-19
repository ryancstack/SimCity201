package restaurant.phillipsRestaurant.gui;


import restaurant.phillipsRestaurant.*;
import restaurant.phillipsRestaurant.interfaces.Customer;
import restaurant.phillipsRestaurant.interfaces.Waiter;
import gui.Gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WaiterGui implements Gui {

    private Waiter agent = null;
    
    boolean atDestination = false;

    String choice = "";
    public int xPos = 0, yPos = 0;//default waiter position
    public int xDestination = 0, yDestination = 0;//default start position

    public static final int xTable = 240, xTable2 = 380, xTable3 = 310;
    public static final int yTable12 = 115, yTable3 = 230;
    private int xHome, yHome;
    private static final int WAITINGX = 70, WAITINGY = 180;
    private static final int CASHIERX = 165, CASHIERY = 35;
    private static final int COOKX = 530, COOKY = 200;
    private static final int HOSTX = 80, HOSTY = 100;
	private static final int EXITX = 0, EXITY = 450;
    
	 private static final int PERSONSIZEX = 30, PERSONSIZEY = 40;
	
    BufferedImage waiterImage;

    public WaiterGui(Waiter agent, int waiterNum) {
        this.agent = agent;
        switch(waiterNum%4){
        case 0:
        	xPos = 0;
        	yPos = 0;
        	xDestination = 0;
        	yDestination = 0;
        	xHome = xDestination;
        	yHome = yDestination;
        	break;
        case 1:
        	xPos = 20;
        	yPos = 0;
        	xDestination = 20;
        	yDestination = 0;
        	xHome = xDestination;
        	yHome = yDestination;
        	break;
        case 2:
        	xPos = 0;
        	yPos = 20;
        	xDestination = 0;
        	yDestination = 20;
        	xHome = xDestination;
        	yHome = yDestination;
        	break;
        case 3:
        	xPos = 20;
        	yPos = 20;
        	xDestination = 20;
        	yDestination = 20;
        	xHome = xDestination;
        	yHome = yDestination;
        	break;
        
        }
        try {
        	waiterImage = ImageIO.read(getClass().getResource("richardRestaurantWaiter.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }   
    }

    public void updatePosition() {
        if (xPos < xDestination)
            xPos = xPos+5;
        else if (xPos > xDestination)
            xPos = xPos-5;

        if (yPos < yDestination)
            yPos = yPos+5;
        else if (yPos > yDestination)
            yPos = yPos-5;
        
        if (atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == HOSTX) & (yDestination == HOSTY)) {
            atDestination = true;
            agent.msgAtHost();
         }
        
        if(atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == COOKX) & (yDestination == COOKY)) {
            atDestination = true;
            agent.msgAtCook();
        }
        
        if(atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == CASHIERX) & (yDestination == CASHIERY)) {
            atDestination = true;
            agent.msgAtCashier();
        }
        if (atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == WAITINGX) & (yDestination == WAITINGY)) {
           atDestination = true;
           agent.msgAtWaitingArea();
        }
        if (atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + PERSONSIZEX) & (yDestination == yTable12 - PERSONSIZEY)) {
           atDestination = true;
           agent.msgAtTable();
        }
        else if (atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable2 + PERSONSIZEX) & (yDestination == yTable12 - PERSONSIZEY)) {
            atDestination = true;
            agent.msgAtTable();
         }
        else if (atDestination == false && xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable3 + PERSONSIZEX) & (yDestination == yTable3 - PERSONSIZEY)) {
            atDestination = true;
            agent.msgAtTable();
         }
        
    }

    public void draw(Graphics2D g) {
    	if(choice.equals("")) {
			g.drawImage(waiterImage, xPos, yPos, null);
		}
    }

    public void updateGui(String choice) {
    	this.choice = choice;
    }
    
    public boolean isPresent() {
        return true;
    }
    public void DoGoToHost(){
    	atDestination = false;
    	xDestination = HOSTX;
		yDestination = HOSTY;
    }
    public void DoGoToWaitingArea(){
    	atDestination = false;
    	xDestination = WAITINGX;
		yDestination = WAITINGY;
    }
    public void DoBringToTable(Customer customer,int tableNum) {
    	atDestination = false;
    	if (tableNum==1)
    	{
    		xDestination = xTable + PERSONSIZEX;
            yDestination = yTable12 - PERSONSIZEY;
        }
    	if (tableNum==2)
        {
    		xDestination = xTable2 + PERSONSIZEX;
            yDestination = yTable12 - PERSONSIZEY;
        }
    	if (tableNum==3)
        {
    		xDestination = xTable3 + PERSONSIZEX;
            yDestination = yTable3 - PERSONSIZEY;
        }
    }
    public void DoBringFoodToTable(Customer customer,String food,int tableNum){
    	atDestination = false;
    	if (tableNum==1)
    	{
    		xDestination = xTable + PERSONSIZEX;
            yDestination = yTable12 - PERSONSIZEY;
        }
    	if (tableNum==2)
        {
    		xDestination = xTable2 + PERSONSIZEX;
            yDestination = yTable12 - PERSONSIZEY;
        }
    	if (tableNum==3)
        {
    		xDestination = xTable3 + PERSONSIZEX;
            yDestination = yTable3 - PERSONSIZEY;
        }
    }
    public void DoTakeCustomerOrder(Customer customer){
    	atDestination = false;
    	if (customer.tableNum==1)
    	{
    		xDestination = xTable + PERSONSIZEX;
            yDestination = yTable12 - PERSONSIZEY;
        }
    	if (customer.tableNum==2)
        {
    		xDestination = xTable2 + PERSONSIZEX;
            yDestination = yTable12 - PERSONSIZEY;
        }
    	if (customer.tableNum==3)
        {
    		xDestination = xTable3 + PERSONSIZEX;
            yDestination = yTable3 - PERSONSIZEY;
        }
    }
    public void DoGoToCook() {
    	atDestination = false;
    	xDestination = COOKX;
    	yDestination = COOKY;
    }
    public void DoLeaveCustomer() {
    	atDestination = false;
        xDestination = EXITX;
        yDestination = EXITY;
    }
    public void DoGoToCashier() {
    	atDestination = false;
        xDestination = CASHIERX;
        yDestination = CASHIERY;
    }
    

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
