package city.helpers;
import java.awt.List;
import java.io.File;  
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;  
import javax.xml.parsers.DocumentBuilderFactory;  

import market.MarketWorkerRole;

import org.w3c.dom.Document;  
import org.w3c.dom.Element;  
import org.w3c.dom.Node;  
import org.w3c.dom.NodeList;  

import bank.BankTellerRole;
import restaurant.stackRestaurant.StackCookRole;
import restaurant.stackRestaurant.StackWaiterRole;
import city.PersonAgent;

public class XMLReader {

	private ArrayList<PersonAgent> persons = new ArrayList<PersonAgent>();
	
	public ArrayList<PersonAgent> initializePeople(String source) {
		populateCity(source);
		return persons;
	}
	
	public void populateCity(String source) {  
		 try {  

			 File xmlFile = new File(source);
			 DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();  
			 DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();  
			 Document doc = documentBuilder.parse(xmlFile);  
	  
			 doc.getDocumentElement().normalize();  
			 NodeList nodeList = doc.getElementsByTagName("Person");  
			 
			 
			 for (int i = 0; i < nodeList.getLength(); i++) {  
				 String name = "";
				 String job = "";
				 int aggressiveness = 0;
				 int initialFunds = 0;
				 String housing = "";
				 String transportation = "";
				 
				 Node node = nodeList.item(i);  
				 if (node.getNodeType() == Node.ELEMENT_NODE) {  
					 Element person = (Element) node;   
					 name = person.getElementsByTagName("name").item(0).getTextContent();
					 job = person.getElementsByTagName("job").item(0).getTextContent();
					 aggressiveness = Integer.parseInt(person.getElementsByTagName("aggressiveness").item(0).getTextContent());
					 initialFunds = Integer.parseInt(person.getElementsByTagName("startingFunds").item(0).getTextContent());
					 housing = person.getElementsByTagName("housingStatus").item(0).getTextContent();
					 transportation = person.getElementsByTagName("vehicleStatus").item(0).getTextContent();
				 }  
				 PersonAgent person;
				 if (job!= null) {
					 person = new PersonAgent(job, name, aggressiveness, initialFunds, housing, transportation);
					 persons.add(person);
				 }
				 else {
					 person = new PersonAgent("", name, aggressiveness, initialFunds, housing, transportation);
					 persons.add(person);
				 } 
			 } 
		 } catch (Exception e) {  
			 e.printStackTrace();  
		 }  
	 }   
}
