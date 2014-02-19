package restaurant.shehRestaurant.gui;


import restaurant.shehRestaurant.ShehCustomerRole;
import restaurant.shehRestaurant.ShehHostAgent; 
import restaurant.shehRestaurant.helpers.Table;
import gui.Gui;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;

public class HostGui implements Gui {

    private ShehHostAgent agent = null;
    
    private int xPos = 766, yPos = 266;//default waiter position
    private int xDestination = 766, yDestination = 266;//default start position
    private int agentSize = 20; //default agent size
 
	private int XTABLE1 = 422;
	private int YTABLE1 = 126;
	private int XTABLE2 = 332;
	private int YTABLE2 = 274;
	private int XTABLE3 = 513;
	private int YTABLE3 = 279;
	private int xTable = 0;
	private int yTable = 0;
	
    public ArrayList<Table> table; 
    
    public HostGui(ShehHostAgent agent) {
        this.agent = agent;
        table = agent.getTables();
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

        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + agentSize) & (yDestination == yTable - agentSize)) { 
           agent.msgAtTable();
        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, agentSize, agentSize);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(ShehCustomerRole customer, Table table) { //SEATING AT TABLE
    		xDestination = xTable + agentSize;
    		yDestination = yTable - agentSize;
    }

    public void DoLeaveCustomer() {
        xDestination = -agentSize;
        yDestination = -agentSize;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}