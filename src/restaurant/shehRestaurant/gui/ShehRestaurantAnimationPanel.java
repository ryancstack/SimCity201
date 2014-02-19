package restaurant.shehRestaurant.gui;

import javax.imageio.ImageIO;
import javax.swing.*;

import gui.BuildingPanel;
import gui.Gui;
import gui.SimCityGui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("serial")
public class ShehRestaurantAnimationPanel extends BuildingPanel implements ActionListener {

    private final int WINDOWX = 827;
    private final int WINDOWY = 406;
    
    private final int speed = 8;
    
    private BufferedImage restaurantImage;

    private List<Gui> guis = new ArrayList<Gui>();

    public ShehRestaurantAnimationPanel(Rectangle2D r, int i, SimCityGui sc) {
    	super(r, i, sc);
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
 
    	Timer timer = new Timer(speed, this );
    	timer.start();
    	
    	//IMAGE
    	try {
        	restaurantImage = ImageIO.read(getClass().getResource("shehRestaurant.png"));
        }
        catch(IOException e) {
        	System.out.println("Error w/ Background");
        }
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;
        
        g2.drawImage(restaurantImage, 0, 0, null);

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.updatePosition();
            }
        }

        for(Gui gui : guis) {
            if (gui.isPresent()) {
                gui.draw(g2);
            }
        }
    }
	public void updateGui() {
		for(Gui gui : guis) {
			if(gui.isPresent()) {
				gui.updatePosition();
			}
		}
	}

	public void addGui(Gui gui) {
		guis.add(gui);
	}
	
	public void removeGui(Gui gui) {
		guis.remove(gui); //not used
	}
	

    public void displayBuildingPanel() {
        myCity.displayBuildingPanel(this);
    }


}