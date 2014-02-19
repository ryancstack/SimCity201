package city.gui;

import gui.Gui;
import city.BusAgent;
import city.helpers.Directory;
import city.helpers.BusHelper;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class BusGui implements Gui {
	/**
	 * Data
	 */
	String destination;
	//start at top left
	int xPos = 110;
	int yPos = 73;
	
	int xDestination = 20;
	int yDestination = 20;
	
	int topTopLane;
	int topBottomLane;
	int bottomTopLane;
	int bottomBottomLane;
	int midLeftLane;
	int midRightLane;
	int leftLeftLane;
	int leftRightLane;
	int rightLeftLane;
	int rightRightLane;
	
	int TopRow = 105-12;
	int BottomRow = 325+10;
	int LeftCol = 135-15-1;
	int RightCol = 700-8+10;	
	
	int Stop1xPos = 171;
	int Stop2xPos = 675;
	int Stop3xPos = RightCol;//610;
	int Stop4xPos = LeftCol;
	
	int xStart = LeftCol;
	int yStart = BottomRow;
	
	int xStart2= RightCol;
	int yStart2= TopRow+40;
	
	private BusAgent agent = null;
	
	BufferedImage busUpImage;
	BufferedImage busDownImage;
	BufferedImage busLeftImage;
	BufferedImage busRightImage;
	
	boolean stopRequested= false;
	
	/**
	 * Received Messages/Actions
	 */
	public void DoGoTo(String place){
		destination = place;
		
		if (destination.equals("StackRestaurant")){
			//sets xDestination & yDestination
			//need to figure out how to travel on road
			//agent.msgAtDestination(); //hack
			
		}
	}
	
	public void DoParkBus(){
		//may not be necessary for bus system
		//send msg to animation panel to remove gui from the list
	}

	/**
	 * Utilities
	 */
	/*
	public int getX(String Destination){
		return Directory.sharedInstance().locationDirectory.get(Destination).xCoordinate;
	}
	
	public int getY(String Destination){
		return Directory.sharedInstance().locationDirectory.get(Destination).yCoordinate;
	}*/
	
	public BusGui(BusAgent agent, int stopNumber) {
        this.agent = agent;
        
        if(stopNumber==1){
        xPos = xStart;
        yPos = yStart;
        xDestination = xStart;
        yDestination = yStart;
        }
        else{
        	xPos = xStart2;
        	yPos = yStart2;
        	xDestination = xStart2;
        	yDestination = yStart2;
        }
        
        try {
        	busLeftImage = ImageIO.read(getClass().getResource("busLeft.png"));
        	busRightImage = ImageIO.read(getClass().getResource("busRight.png"));
        	busUpImage = ImageIO.read(getClass().getResource("busUp.png"));
        	busDownImage = ImageIO.read(getClass().getResource("busDown.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ busGui");
        }
    }
	
	@Override
	public void updatePosition() {
		// TODO Auto-generated method stub
		
		//Counter Clockwise motion that goes on as long as stop is not requested
		if(stopRequested==false){

		if ((xPos == LeftCol) && (yPos != BottomRow)) //at left, coming down
            yPos++;
		else if ((yPos == BottomRow) && (xPos != RightCol)) //at bottom, going right
            xPos++;
        else if ((xPos == RightCol) && (yPos != TopRow)) //at right, going up
            yPos--;
        else if ((yPos == TopRow) && (xPos != LeftCol)) //at top, going left
            xPos--;
		
		
		if((xPos==Stop1xPos) && (yPos==BottomRow)) //leftcol
			agent.msgAtStopOne();
		else if((xPos==Stop2xPos) && (yPos==BottomRow)) //rightcol
			agent.msgAtStopTwo();
		else if((xPos==Stop3xPos) && (yPos==TopRow)) //xPos==RightCol
			agent.msgAtStopThree();
		else if((xPos==Stop4xPos) && (yPos==TopRow)) //LeftCol
			agent.msgAtStopFour();
		
		}
		
	}

	@Override
	public void draw(Graphics2D g) {
		//counter clockwise
		if(yPos==BottomRow)
			g.drawImage(busRightImage, xPos, yPos, null);
		else if (xPos==RightCol)
			g.drawImage(busUpImage, xPos, yPos, null);
		else if (yPos==TopRow)
			g.drawImage(busLeftImage, xPos, yPos, null);
		else if (xPos==LeftCol)
			g.drawImage(busDownImage, xPos, yPos, null);	
	}

	@Override
	public boolean isPresent() {
		// TODO Auto-generated method stub
		return true;
	}
	
	public void DoKeepDriving(){
		stopRequested=false;
	}
	
	public void DoStopDriving(){
		stopRequested=true;
	}

}