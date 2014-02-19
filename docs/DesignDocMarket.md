#Design Doc: Market

##Data
	enum orderState {Ordered, CantFill, Filled, Billed, Paid}
	class Order {
		CustomerAgent customer
		Map<String, Integer> groceryList
		Map<String, Integer> retrievedGroceries
		orderState state
		double price
	}
	List<Order> MyOrders

	class Food {
		String name
		int supply
	}
	Map<String, Food> inventory
	
##Scheduler
	if ∃ Order o in MyOrders ∋ o.state == Ordered
		then FillOrder(o)
	if ∃ Order o in MyOrders ∋ o.state == CantFill
		then TurnAwayCustomer(o)
	if ∃ Order o in MyOrders ∋ o.state == Filled
		then BillCustomer(o)
	if ∃ Order o in MyOrders ∋ o.state == Paid
		then GiveGroceries(o)

##Messages
	msgGetGroceries(CustomerAgent customer, Map<String, Integer> groceryList) {
		MyOrders.add(new Order(customer, groceryList))
	}
	msgHereIsMoney(CustomerAgent customer, double money) {
		if ∃ Order o in MyOrders ∋ o.customer == customer
			then o.state = Paid		
	}
	msgCantAffordGroceries(CustomerAgent customer) {
		if ∃ Order o in MyOrders ∋ o.customer == customer
			then MyOrders.remove(o)		
	}

##Actions	
	FillOrder(Order o) {
		if ∃ String s in groceryList ∋ inventory.get(s) >= groceryList.get(s)
			then retrievedGroceries.put(s, groceryList.get(s))
			     groceryList.remove(s)
			     DoGetItem(s) //GUI
	
		if retrievedGroceries.isEmpty()
			then o.state = CantFill
		else
			o.state = Filled
	}
	TurnAwayCustomer(Order o) {
		o.customer.msgCantFillOrder(o.groceryList)
	}
	BillCustomer(Order o) {
		//Calculate price
		o.customer.msgHereIsBill(o.price)
		o.state = Billed
	}
	GiveGroceries(Order o) {
		o.customer.msgHereAreYourGroceries(o.retrievedGroceries)
		MyOrders.remove(o)
	}
