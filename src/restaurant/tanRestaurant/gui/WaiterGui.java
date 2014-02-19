package restaurant.tanRestaurant.gui;

import restaurant.tanRestaurant.TanCustomerRole;
//import restaurant.HostAgent;
import restaurant.tanRestaurant.TanWaiterRole;
import restaurant.tanRestaurant.TanWaiterRole.MyCustomer;
//import restaurant.tanRestaurant.gui.CustomerGui.Command;
import gui.Gui;
//import restaurant.gui.CustomerGui.Command;





import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class WaiterGui implements Gui {

    private TanWaiterRole agent = null;
    private boolean requestedBreak= false;

    private int xPos = 292, yPos= 410;//50, yPos = 50;//default waiter position
    private int xDestination = 292, yDestination = 410;//default start position
    private int xStart= 4, yStart=118;

   // public static final int xTable = 200;
   // public static final int yTable = 250;
    
	//public static final int xSeat = 70;
	//public static final int ySeat = 80;
	
	private int xCook=287, yCook=108;
	private int xCashier=434, yCashier=73;
	
	//Tables
	private int xTable1=38, xTable2=245, xTable3=452, xTable4=660;
	private int yTable=165;
	
	//Waiting seats
	private int xSeat1=31, xSeat2=95 ,xSeat3=129 ,xSeat4=158 ,xSeat5=199 ,xSeat6=381 ,xSeat7=420 ,
			xSeat8=450 ,xSeat9=482 ,xSeat10=512; 
	private int ySeat=320;
	
	public enum locationState
	{Away, Entered};
	private locationState locState = locationState.Away;
	
	BufferedImage waiterImage;
    BufferedImage chickenImage;
    BufferedImage pizzaImage;
    BufferedImage saladImage;
    BufferedImage steakImage;
    int waiternumber;
    //RestaurantGui gui;

    public WaiterGui(TanWaiterRole agent) {
        this.agent = agent;
        
        try {
        	waiterImage = ImageIO.read(getClass().getResource("stackRestaurantWaiter.png"));
        	chickenImage = ImageIO.read(getClass().getResource("chicken.png"));
            pizzaImage = ImageIO.read(getClass().getResource("pizza.png"));
            saladImage = ImageIO.read(getClass().getResource("salad.png"));
            steakImage = ImageIO.read(getClass().getResource("steak.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }   
    }

	public WaiterGui(TanWaiterRole w, int n){//RestaurantGui gui, int n){ //HostAgent m) {
		agent = w;
		waiternumber=n;
		xPos = -20;
		yPos = -20;
		if(waiternumber==1){
			xDestination = 140;
			yDestination = 20;
		}
		if(waiternumber==2){
			xDestination = 110;
			yDestination = 20;
		}
		if(waiternumber==3){
			xDestination = 80;
			yDestination = 20;
		}
		if(waiternumber==4){
			xDestination = 50;
			yDestination = 20;
		}
		if(waiternumber==5){
			xDestination = 20;
			yDestination = 20;
		}
		
        try {
        	waiterImage = ImageIO.read(getClass().getResource("stackRestaurantWaiter.png"));
        	chickenImage = ImageIO.read(getClass().getResource("chicken.png"));
            pizzaImage = ImageIO.read(getClass().getResource("pizza.png"));
            saladImage = ImageIO.read(getClass().getResource("salad.png"));
            steakImage = ImageIO.read(getClass().getResource("steak.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }   
		//maitreD = m;
		//this.gui = gui;
	}
    
    public void updatePosition() {
        /*if(xPos==-20 && yPos==-20){
        	agent.msgAtStart();
        }*/
    	if((waiternumber==1 && xPos==140 && yPos==20)||(waiternumber==2 && xPos==110 && yPos==20)||(waiternumber==3 && xPos==80 && yPos==20)||(waiternumber==4 && xPos==50 && yPos==20)||(waiternumber==5 && xPos==20 && yPos==20)){
    		agent.msgAtStart();
    	}
    	if (xPos < xDestination)
            xPos++;
    		//xPos=xPos+3;
        else if (xPos > xDestination)
            xPos--;
        	//xPos=xPos-3;
        if (yPos < yDestination)
            yPos++;
        	//yPos=yPos+3;
        else if (yPos > yDestination)
            yPos--;
        	//yPos=yPos-3;

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable1) & (yDestination == yTable)) {
        	agent.msgAtTable();
        }
        else if(xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable2) & (yDestination == yTable)) {
           agent.msgAtTable();
         }
        else if(xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable3) & (yDestination == yTable)) {
           agent.msgAtTable();
         }
        else if(xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable4) & (yDestination == yTable)) {
           agent.msgAtTable();
         }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xCashier) & (yDestination == yCashier)) {
        	agent.msgAtCashier();
        }
        if ((xPos == xDestination && yPos == yDestination)
        		&& ((xDestination==xSeat1 && yDestination ==ySeat) ||(xDestination==xSeat2 && yDestination ==ySeat)
        		|| (xDestination==xSeat3 && yDestination ==ySeat) ||(xDestination==xSeat4 && yDestination ==ySeat)
        		||(xDestination==xSeat5 && yDestination ==ySeat) ||(xDestination==xSeat6 && yDestination ==ySeat)
        		|| (xDestination==xSeat7 && yDestination ==ySeat) ||(xDestination==xSeat8 && yDestination ==ySeat)
        		|| (xDestination==xSeat9 && yDestination ==ySeat) ||(xDestination==xSeat10 && yDestination ==ySeat)
        		)) {
        	agent.msgAtWaitingCustomer();
        }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xCook) & (yDestination == yCook)) {
        	//if (agent.ws == WaiterState.approachingTable)
        	agent.msgAtCook();
        }
    }
    
    public void draw(Graphics2D g) {
    	g.drawImage(waiterImage, xPos, yPos, null);
        /*g.setColor(Color.BLUE);
        g.fillRect(xPos, yPos, 20, 20);
        
        Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.BLACK);
		g2.drawString(agent.getName(), xPos, yPos-2);*/
    }

    public boolean isPresent() {
        return true;
    }

	public void setRequestedBreak() {
		requestedBreak = true;
		agent.msgWantABreak();
		//setPresent(true);
	}
	public boolean requestedBreak() {
		return requestedBreak;
	}
	
    public void DoBringToTable(TanCustomerRole customer, int seatnumber) {
        if (seatnumber==1){
        	xDestination = xTable1;
            yDestination = yTable;
        }
        else if(seatnumber==2){
        	xDestination = xTable2;
            yDestination = yTable;
        }
        else if(seatnumber==3){
        	xDestination = xTable3;
            yDestination = yTable;
        }
        else if(seatnumber==4){
        	xDestination = xTable4;
            yDestination = yTable;
        }
    }
    
    public void GoToCashier(){
    	xDestination= xCashier;
    	yDestination= yCashier;
    }
    
    public void DoPickUpFood(){
    	xDestination =xCook;
    	yDestination =yCook;
    }
    
    public  void setWaiterEnabled(){
    	requestedBreak= false;
    	System.out.println("in setWaiterEnabled");
    	//gui.setWaiterEnabled(agent);
    }

    public void DoServeFood(int seatnumber){
    	if (seatnumber==1){
        	xDestination = xTable1;
            yDestination = yTable;
        }
        else if(seatnumber==2){
        	xDestination = xTable2;
            yDestination = yTable;
        }
        else if(seatnumber==3){
        	xDestination = xTable3;
            yDestination = yTable;
        }
        else if(seatnumber==4){
        	xDestination = xTable4;
            yDestination = yTable;
        }
    }
    
    public void ApproachTable(int seatnumber){
    	if (seatnumber==1){
        	xDestination = xTable1;
            yDestination = yTable;
        }
        else if(seatnumber==2){
        	xDestination = xTable2;
            yDestination = yTable;
        }
        else if(seatnumber==3){
        	xDestination = xTable3;
            yDestination = yTable;
        }
        else if(seatnumber==4){
        	xDestination = xTable4;
            yDestination = yTable;
        }
    }
    
    public void approachWaitingCustomer(int sn){
			
    	if(sn==1){
			xDestination = xSeat1;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
		if(sn==2){
			xDestination = xSeat2;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
		if(sn==3){
			xDestination = xSeat3;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
		if(sn==4){
			xDestination = xSeat4;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
		if(sn==5){
			xDestination = xSeat5;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
		if(sn==6){
			xDestination = xSeat6;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
		if(sn==7){
			xDestination = xSeat7;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
		if(sn==8){
			xDestination = xSeat8;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
		if(sn==9){
			xDestination = xSeat9;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
		if(sn==10){
			xDestination = xSeat10;
			yDestination = ySeat;
			//command=Command.GoToWaitingSeat;
		}
    }
    
    public void DoLeaveCustomer() {
    	/*
        xDestination = -20;
        yDestination = -20;*/
    	if(waiternumber==1){
			xDestination = 140;
			yDestination = 20;
		}
		if(waiternumber==2){
			xDestination = 110;
			yDestination = 20;
		}
		if(waiternumber==3){
			xDestination = 80;
			yDestination = 20;
		}
		if(waiternumber==4){
			xDestination = 50;
			yDestination = 20;
		}
		if(waiternumber==5){
			xDestination = 20;
			yDestination = 20;
		}
		else{
			xDestination= xStart;
			yDestination= yStart;
		}
    }
    
    public void DoGoToCook(){
    	xDestination = xCook;
    	yDestination = yCook;
    }
    
    public boolean isAtStart(){
    	/*
    	if ((xPos == -20) &&(yPos==-20)){
    		return true;
    	}*/
    	if((xPos==xStart && yPos==yStart)||(waiternumber==1 && xPos==140 && yPos==20)||
    			(waiternumber==2 && xPos==110 && yPos==20)||(waiternumber==3 && xPos==80 && yPos==20)||
    			(waiternumber==4 && xPos==50 && yPos==20)||(waiternumber==5 && xPos==20 && yPos==20)){
    		return true;
    	}
    	else return false;
    }

    /*
    public boolean isAtTable(){
    	if ((xPos == xTable +20) && (yPos== yTable-20)){
    		agent.msgisAtTable();
    		return true;
    	}
    	if ((xPos == xTable +150+ 20) && (yPos== yTable-20)){
    		agent.msgisAtTable();
    		return true;
    	}
    	if ((xPos == xTable +150 +20) && (yPos== yTable-20 -150)){
    		agent.msgisAtTable();
    		return true;
    	}
    	else return false;
    	//agent.msgisAtTable();//myc);
    }*/
    
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }

	public void DoGoToPost() {
		xDestination=xStart;
		yDestination=yStart;
	}
}
