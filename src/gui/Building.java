package gui;

import java.awt.geom.*;

public class Building extends Rectangle2D.Double {
	String buildingName;
	BuildingPanel myBuildingPanel;
	MicroAnimationPanel myMicroAnimationPanel;
	String buildingType;

	public Building( int x, int y, int width, int height ) {
		super( x, y, width, height );
	}
	
	public void displayBuilding() {
		myBuildingPanel.displayBuildingPanel();
		//myMicroAnimationPanel.displayMicroAnimationPanel();
	}
	
	public void setName(String name) {
		buildingName = name;
	}
	
	public String getBuildingType() {
		return buildingType;
	}
	
	public String getName() {
		return buildingName;
	}

	public void setBuildingPanel( BuildingPanel bp ) {
		myBuildingPanel = bp;
	}
	
	public boolean hasBuildingPanel() {
		if(myBuildingPanel != null)
			return true;
		else
			return false;
	}
	
	public BuildingPanel getBuildingPanel() {
		return myBuildingPanel;
	}
	
	public void setMicroAnimationPanel( MicroAnimationPanel ma) {
		myMicroAnimationPanel = ma;
	}
	public void addGui(Gui gui) {
		myBuildingPanel.addGui(gui);
	}
}