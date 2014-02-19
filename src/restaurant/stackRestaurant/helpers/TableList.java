package restaurant.stackRestaurant.helpers;

import java.awt.Point;
import java.util.ArrayList;

public class TableList {	
	@SuppressWarnings("serial")
	public static ArrayList<Point> tableLocations = new ArrayList<Point>() {
		{
            add(new Point(52, 45));
            add(new Point(52, 181));
            add(new Point(52, 317));
            add(new Point(180, 45));
            add(new Point(180, 181));
            add(new Point(180, 317));
            add(new Point(300, 45));
            add(new Point(300, 181));
            add(new Point(300, 317));
        }
    };
    
    public ArrayList<Point> getTables() {
    	return tableLocations;
    }
    
    public Point getLastTable() {
    	return getTables().get(getTables().size()-1);
    }
}
