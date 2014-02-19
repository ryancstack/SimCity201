package city.helpers;

import home.LandlordRole;
import city.PersonAgent;
import city.UnemployedRole;
import market.MarketCustomerRole;
import market.MarketWorkerRole;
import agent.Role;
import bank.BankTellerRole;
import restaurant.nakamuraRestaurant.NakamuraCookRole;
import restaurant.nakamuraRestaurant.NakamuraCustomerRole;
import restaurant.nakamuraRestaurant.NakamuraWaiterNormalRole;
import restaurant.nakamuraRestaurant.NakamuraWaiterSharedRole;
import restaurant.shehRestaurant.ShehCookRole;
import restaurant.shehRestaurant.ShehCustomerRole;
import restaurant.shehRestaurant.ShehWaiterNormalRole;
import restaurant.shehRestaurant.ShehWaiterRole;
import restaurant.shehRestaurant.ShehWaiterSharedRole;
import restaurant.stackRestaurant.*;
import restaurant.huangRestaurant.HuangCookRole;
import restaurant.huangRestaurant.HuangCustomerRole;
import restaurant.huangRestaurant.HuangWaiterNormalRole;
import restaurant.huangRestaurant.HuangWaiterSharedRole;
import restaurant.stackRestaurant.StackCookRole;
import restaurant.stackRestaurant.StackWaiterNormalRole;
import restaurant.stackRestaurant.StackWaiterSharedRole;
import restaurant.tanRestaurant.TanCookRole;
import restaurant.tanRestaurant.TanCustomerRole;
import restaurant.tanRestaurant.TanWaiterNormalRole;
import restaurant.tanRestaurant.TanWaiterRole;
import restaurant.tanRestaurant.TanWaiterSharedRole;


public class RoleFactory {
	Role newRole;
	public RoleFactory() {
		newRole = null;
	}
	public Role createRole(String role, PersonAgent p) {
		if(role.equals("StackRestaurant")) {
			newRole = new StackCustomerRole("StackRestaurant");
		}
		else if(role.equals("HuangRestaurant")) {
			newRole = new HuangCustomerRole("HuangRestaurant");
		}
		else if(role.equals("NakamuraRestaurant")) {
			newRole = new NakamuraCustomerRole("NakamuraRestaurant");
		}
		else if(role.equals("ShehRestaurant")) {
			newRole = new ShehCustomerRole("ShehRestaurant");
		}
		else if(role.equals("TanRestaurant")) {
			newRole = new TanCustomerRole("TanRestaurant");
		}
		else if(role.equals("MarketCust")) {
			newRole = new MarketCustomerRole(p.getGroceriesList(), "Market");
		}
		else if(role.equals("Market2Cust")) {
			newRole = new MarketCustomerRole(p.getGroceriesList(), "Market2");
		}
		else if(role.equals("Market")) {
			newRole = new MarketWorkerRole("Market");
			Directory.sharedInstance().marketDirectory.get("Market").setWorker((MarketWorkerRole)newRole);
			((MarketWorkerRole) newRole).setMarket(Directory.sharedInstance().marketDirectory.get("Market"));
		}
		else if(role.equals("Market2")) {
			newRole = new MarketWorkerRole("Market2");
			Directory.sharedInstance().marketDirectory.get("Market2").setWorker((MarketWorkerRole)newRole);
			((MarketWorkerRole) newRole).setMarket(Directory.sharedInstance().marketDirectory.get("Market2"));
		}
		else if(role.equals("HuangWaiterNormal")) {
			newRole = new HuangWaiterNormalRole("HuangRestaurant");
			return newRole;
		}
		else if (role.equals("HuangWaiterShared")) {
			newRole = new HuangWaiterSharedRole("HuangRestaurant");
			return newRole;
		}
		else if (role.equals("HuangCook")) {
			newRole = new HuangCookRole("HuangRestaurant");
			return newRole;
		}
		else if(role.equals("StackWaiterNormal")) {
			newRole = new StackWaiterNormalRole("StackRestaurant");
			return newRole;
		}
		else if (role.equals("StackWaiterShared")) {
			newRole = new StackWaiterSharedRole("StackRestaurant");
			return newRole;
		}
		else if (role.equals("StackCook")) {
			newRole = new StackCookRole("StackRestaurant");
			return newRole;
		}
		else if(role.equals("ShehWaiterNormal")) {
			newRole = new ShehWaiterNormalRole("ShehRestaurant");
			return newRole;
		}
		else if (role.equals("ShehWaiterShared")) {
			newRole = new ShehWaiterSharedRole("ShehRestaurant");
			return newRole;
		}
		else if (role.equals("ShehCook")) {
			newRole = new ShehCookRole("ShehRestaurant");
			return newRole;
		}
		else if(role.equals("TanWaiterNormal")) {
			newRole = new TanWaiterNormalRole("TanRestaurant");
			return newRole;
		}
		else if(role.equals("TanWaiter")) {
			newRole = new TanWaiterRole("TanRestaurant");
			return newRole;
		}
		else if (role.equals("TanWaiterShared")) {
			newRole = new TanWaiterSharedRole("TanRestaurant");
			return newRole;
		}
		else if (role.equals("TanCook")) {
			newRole = new TanCookRole("TanRestaurant");
			return newRole;
		}
		else if(role.equals("NakamuraWaiterNormal")) {
			newRole = new NakamuraWaiterNormalRole("NakamuraRestaurant");
			return newRole;
		}
		else if (role.equals("NakamuraWaiterShared")) {
			newRole = new NakamuraWaiterSharedRole("NakamuraRestaurant");
			return newRole;
		}
		else if (role.equals("NakamuraCook")) {
			newRole = new NakamuraCookRole("NakamuraRestaurant");
			return newRole;
		}
//		else if(role.equals("PhillipsWaiterNormal")) {
//			newRole = new PhillipsWaiterNormalRole("PhillipsRestaurant");
//			return newRole;
//		}
//		else if (role.equals("PhillipsWaiterShared")) {
//			newRole = new PhillipsWaiterSharedRole("PhillipsRestaurant");
//			return newRole;
//		}
//		else if (role.equals("PhillipsCook")) {
//			newRole = new PhillipsCookRole("PhillipsRestaurant");
//			return newRole;
//		}
		else if (role.equals("BankTeller")) {
			newRole = new BankTellerRole("Bank");
			return newRole;
		}
		else if (role.equals("BankTeller2")) {
			newRole = new BankTellerRole("Bank2");
			return newRole;
		}
		else if (role.equals("Unemployed")) {
			newRole = new UnemployedRole();
			return newRole;
		}
		else if (role.equals("LandlordA")) {
			newRole = new LandlordRole("ApartmentA", 1);
			return newRole;
		}
		else if (role.equals("LandlordB")) {
			newRole = new LandlordRole("ApartmentB", 2);
			return newRole;
		}
		else if (role.equals("LandlordC")) {
			newRole = new LandlordRole("ApartmentC", 3);
			return newRole;
		}
		newRole.setPerson(p);
		return newRole;
	}
};