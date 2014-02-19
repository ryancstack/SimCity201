package restaurant.huangRestaurant.gui;

import gui.Gui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.imageio.ImageIO;

import restaurant.huangRestaurant.HuangCookRole;




public class CookGui implements Gui {
	private HuangCookRole agent = null;
	private String currentDish;
    private int xPos = 35, yPos = 450;//default cook position
    private int xDestination = 35, yDestination = 450;//default start position
    private int xHome = 630, yHome = 320;//Cook Home positions
    private int xCookArea = xHome + 70, yCookArea = yHome;//Customer Waiting area.
    private int xPlateArea = xHome + 120, yPlateArea = yHome -80;
    private int xCashier = 780, yCashier = 40;
    private static final int hostX = 27, hostY = 48;
    private int xExit = 0, yExit = 450;//Exit
    public enum Command { GoToHost, GoToStove, GoToStorage, InTransit, LeaveRestaurant, GoToCashier};
    Command command;
    public class CookingFood {;
    	String food;
    	int xPos;
    	int yPos;
    	int table;
    	CookingFood(String food, int table) {
    		this.food = food;
    		this.xPos = xCookArea + dishOffSetX * table;
    		this.yPos = yCookArea;
    		this.table = table;
    	}
    }
    public class PlatedFood {;
    	String food;
    	int xPos;
    	int yPos;
    	int table;
    	PlatedFood(String food, int table) {
    		this.food = food;
    		this.xPos = xPlateArea + dishOffSetX * table;
    		this.yPos = yPlateArea;
    		this.table = table;
    	}
    }
    private List<PlatedFood> pFood = Collections.synchronizedList(new ArrayList<PlatedFood>());
    private List<CookingFood> cFood = Collections.synchronizedList(new ArrayList<CookingFood>());
	private static final int dishOffSetX = 20;
	BufferedImage cookImage;

	//private HostAgent host;
	
    public CookGui(HuangCookRole agent) {
        this.agent = agent;

        try {
        	cookImage = ImageIO.read(getClass().getResource("huangRestaurantCook.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
    }
    public void updatePosition() {

        if (xPos < xDestination)
            xPos++;
        else if (xPos > xDestination)
            xPos--;

        if (yPos < yDestination)
            yPos++;
        else if (yPos > yDestination)
            yPos--;
        
       if(xPos == xCookArea && yPos == yCookArea && command == Command.GoToStove) {
    	   agent.msgActionComplete();
    	   command = Command.InTransit;
       }
       if(xPos == hostX && yPos == hostY && command == Command.GoToHost) {
    	   agent.msgActionComplete();
    	   command = Command.InTransit;
       }
       if(xPos == xExit && yPos == yExit && command == Command.LeaveRestaurant) {
    	   agent.msgActionComplete();
    	   command = Command.InTransit;
       }
       if(xPos == xCashier && yPos == yCashier && command == Command.GoToCashier) {
    	   agent.msgActionComplete();
    	   command = Command.InTransit;
       }
    }

    public void draw(Graphics2D g) {
    	g.drawImage(cookImage, xPos, yPos, null);
	    
	    if(!cFood.isEmpty()) {
	    	synchronized(cFood) {
		    	for(CookingFood cf : cFood) {
	    			g.drawString(cf.food, cf.xPos, cf.yPos);
	    		}
	    	}
    	}
	    if(!pFood.isEmpty()) {
	    	synchronized(pFood) {
		    	for(PlatedFood pf : pFood) {
	    			g.drawString(pf.food, pf.xPos, pf.yPos);
	    		}
	    	}
    	}
    }
    public boolean isPresent() {
        return true;
    }
    public void DoRemovePlatedDish(int table) {
    	synchronized(pFood) {
	    	for(PlatedFood pf: pFood) {
	    		if (pf.table == table) {
	    			pFood.remove(pf);
	    			break;
	    		}
	    	}
    	}
    }
    public void DoPlateDish(int table) {
    	synchronized(cFood) {
	    	for(CookingFood cf: cFood) {
	    		if (cf.table == table) {
	    			PlatedFood pf = new PlatedFood(cf.food, cf.table);
	    			pFood.add(pf);
	    			cFood.remove(cf);
	    			break;
	    		}
	    	}
    	}
    }
    public void DoCookDish(String choice, int table) {
    	currentDish = (String) choice.subSequence(0,2);
    	CookingFood cf = new CookingFood(currentDish, table);
    	cFood.add(cf);
    }
    
    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
	public void DoGoToHost() {
		command = Command.GoToHost;
		xDestination = hostX;
		yDestination = hostY;
	}
	public void DoLeaveRestaurant() {
		command = Command.LeaveRestaurant;
		xDestination = xExit;
		yDestination = yExit;
	}
	public void DoGoToStove() {
		command = Command.GoToStove;
		xDestination = xCookArea;
		yDestination = yCookArea;
	}
	public void DoGoToCashier() {
		command = Command.GoToCashier;
		xDestination = xCashier;
		yDestination = yCashier;
		
	}
	
}
