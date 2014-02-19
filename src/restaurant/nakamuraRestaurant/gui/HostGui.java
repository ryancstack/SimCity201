package restaurant.nakamuraRestaurant.gui;

import gui.Gui;

import java.awt.*;
import java.util.List;

import restaurant.nakamuraRestaurant.NakamuraCustomerRole;
import restaurant.nakamuraRestaurant.NakamuraHostAgent;

public class HostGui implements Gui {

    private NakamuraHostAgent role = null;

    private int xPos = 100, yPos = 50;//default host position
    private int xDestination = 100, yDestination = 50;//default start position

    public static final int xTable = 200;
    public static final int yTable = 250;

    public HostGui(NakamuraHostAgent role) {
        this.role = role;
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

//        if (xPos == xDestination && yPos == yDestination
//        		& (xDestination > 0) & (yDestination > 0)) {
//        }
//        else if (xPos == -25 && yPos == -25 && xDestination == -25 && yDestination == -25){
//        	xDestination = -20;
//        	yDestination = -20;
//        }
    }

    public void draw(Graphics2D g) {
        g.setColor(Color.BLUE);
        g.fillRect(xPos, yPos, 20, 20);
    }

    public boolean isPresent() {
        return true;
    }

    public void DoUpdateSeat(List<NakamuraCustomerRole> customers) {
    	for(int i = 0; i < customers.size(); i++) {
    		customers.get(i).getGui().DoWaitAt(i);
    	}
    	role.msgAnimationSeatsUpdated();
    }

    public int getXPos() {
        return xPos;
    }

    public int getYPos() {
        return yPos;
    }
}
