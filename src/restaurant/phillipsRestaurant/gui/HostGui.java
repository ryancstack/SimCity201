/*package restaurant.gui;


import restaurant.CustomerAgent;
import restaurant.HostAgent;
import restaurant.HostAgent.HostState;

import java.awt.*;

public class HostGui implements Gui {

    private HostAgent agent = null;
    
    public int xPos = -20, yPos = -20;//default waiter position
    public int xDestination = -20, yDestination = -20;//default start position

    public static final int xTable = 200;
    public static final int xTable2 = 260;
    public static final int xTable3 = 320;
    public static final int yTable = 250;

    public HostGui(HostAgent agent) {
        this.agent = agent;
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
        if (xPos == -20 && yPos == -20) {
            agent.msgReadyToServe();
         }
        if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable + 20) & (yDestination == yTable - 20)) {
           agent.msgAtTable();
        }
        else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable2 + 20) & (yDestination == yTable - 20)) {
            agent.msgAtTable();
         }
        else if (xPos == xDestination && yPos == yDestination
        		& (xDestination == xTable3 + 20) & (yDestination == yTable - 20)) {
            agent.msgAtTable();
         }
        
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.MAGENTA);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoBringToTable(CustomerAgent customer) {
    	if (customer.tableNum==1)
    	{
    		xDestination = xTable + 20;
    		yDestination = yTable - 20;
        }
    	if (customer.tableNum==2)
        {
    		xDestination = xTable2 + 20;
    		yDestination = yTable - 20;
        }
    	if (customer.tableNum==3)
        {
    		xDestination = xTable3 + 20;
    		yDestination = yTable - 20;
        }
    }

    public void DoLeaveCustomer() {
        xDestination = -20;
        yDestination = -20;
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
    
}*/
