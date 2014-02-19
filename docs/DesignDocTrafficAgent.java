#Design Doc: Traffic Agent

##Messages

##Data
	public List<Vehicle> vehiclesAtStop
	= Collections.synchronizedList(new ArrayList<Vehicle>());
	public List<Transportation> crossingPedestrians
	= Collections.synchronizedList(new ArrayList<Transportation>());
	Timer timer = new Timer();

	public enum lightState
	{red, green, none}
	public lightState currentLight= lightState.none;

##Scheduler	
	while(!crossingPedestrians.isEmpty()){ //if there are pedestrians then no greenLight	
	}		
	if(crossingPedestrians.isEmpty() && !vehiclesAtStop.isEmpty()){ //if no one's there, stop to be sure then go
		timer.schedule(new TimerTask() {
			public void run() {
				greenLight();
			}
		},
		400);
		return true;
	}

	if(currentLight==lightState.red && !vehiclesAtStop.isEmpty()){
		timer.schedule(new TimerTask() {
			public void run() {
				greenLight();
			}
		},
		400);
		return true;
	}
	
	return false;

##Actions
	protected void greenLight(){
		synchronized(vehiclesAtStop){
		for(Vehicle v:vehiclesAtStop){
			v.msgGreenLight(this);
		}
		}
		currentLight= lightState.green;
	}
	
	public void msgAtIntersection(Vehicle v) {
		synchronized(vehiclesAtStop){
			vehiclesAtStop.add(v);
		}
			currentLight= lightState.red;
			stateChanged();
	}

	public void msgRemoveFromIntersection(Vehicle v) {
		synchronized(vehiclesAtStop){
		vehiclesAtStop.remove(v);	
		}

	