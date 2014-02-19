package gui;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.*;

import restaurant.Restaurant;

public class MicroAnimationPanel extends JPanel implements ActionListener, MouseListener {
	private final int WINDOWX = 835;
    private final int WINDOWY = 400;
    
	Rectangle2D myRectangle;
	String myName;
	SimCityGui myCity;
	CardLayout layout;
   
    BufferedImage restaurantImage;
   
    private final int DELAY = 5;

    private List<Gui> guis = new ArrayList<Gui>();
    
    private HashMap<String, CityCard> cards = new HashMap<String, CityCard>();
  
    public MicroAnimationPanel() {
    	setSize(WINDOWX, WINDOWY);
        setVisible(true);
        //setBackground(Color.BLUE);
 
        addMouseListener(this);
        
    	Timer timer = new Timer(DELAY, this);
    	timer.start();
    	
    	cards.put("null", new CityCard(myCity, Color.green));
    	
    	cards.put("stackRestaurant", new CityCard(myCity, Color.pink));
    	cards.put("huangRestaurant", new CityCard(myCity, Color.pink));
    	cards.put("phillipsRestaurant", new CityCard(myCity, Color.pink));
    	cards.put("nakamuraRestaurant", new CityCard(myCity, Color.pink));
    	cards.put("shehRestaurant", new CityCard(myCity, Color.pink));
    	cards.put("tanRestaurant", new CityCard(myCity, Color.pink));
    	
    	cards.put("house1", new CityCard(myCity, Color.pink));
    	cards.put("house2", new CityCard(myCity, Color.pink));
    	cards.put("house3", new CityCard(myCity, Color.pink));
    	cards.put("house4", new CityCard(myCity, Color.pink));
    	cards.put("house5", new CityCard(myCity, Color.pink));
    	cards.put("house6", new CityCard(myCity, Color.pink));
    	
    	cards.put("apartmentA", new CityCard(myCity, Color.pink));
    	cards.put("apartmentB", new CityCard(myCity, Color.pink));
    	cards.put("apartmentC", new CityCard(myCity, Color.pink));
    	
    	cards.put("market1", new CityCard(myCity, Color.yellow));
    	cards.put("market2", new CityCard(myCity, Color.yellow));
    	
    	cards.put("bank", new CityCard(myCity, Color.yellow));
    	cards.put("bank2", new CityCard(myCity, Color.yellow));
    	
    	layout = new CardLayout();
    	this.setLayout(layout);
    	
    	for(String key: cards.keySet()) {
    		this.add(cards.get(key), key);
    	}
    	
    	layout.show(this, "null");
    }
    
	public MicroAnimationPanel( Rectangle2D r, int i, SimCityGui sc ) {
		myRectangle = r;
		myName = "" + i;
		myCity = sc;
		
		setBackground( Color.RED );
		setMinimumSize( new Dimension( 500, 250 ) ); //DO WE NEED MIN/MAX SIZE? WHAT IS PURPOSE?
		setMaximumSize( new Dimension( 500, 250 ) );
		setPreferredSize( new Dimension( 500, 250 ) );
		
		JLabel j = new JLabel( myName );
		add( j );
	}
  
	public boolean addView(CityCard panel, String key) {
    	if(cards.containsKey(key))
    		return false;
    	cards.put(key, panel);
    	this.add(cards.get(key), key);
    	return true;
    }
	
    public void setView(String key) {
    	if (cards.containsKey(key)){ 
    		layout.show(this,key);
    	}
    }

	public void actionPerformed(ActionEvent e) {
		repaint();  //Will have paintComponent called
	}

    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D)g;

        //Clear the screen by painting a rectangle the size of the frame
        g2.setColor(getBackground());
        g2.fillRect(0, 0, getWidth(), getHeight());

        //g2.drawImage(restaurantImage, 0, 0, null);
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

    public void addGui(Gui gui) {
        guis.add(gui);
    }
    
	public String getName() {
		return myName;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
}