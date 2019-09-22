//YOUR NAME HERE
public class Event implements Comparable<Event> {
    private EventHandler m_handler;
    private double m_time;
    private int m_eventId;
    private int m_eventType;
    // ---- The line below is added 
    private Airplane m_airplaneType;
    private Airport[] m_airports;
    private double m_arrivalTime;
    private double m_departureTime;
    private int m_nextDest; 
    static private int m_nextId = 0;
    
    Event() {
        m_eventId = m_nextId++;
	AirportSim.numEvents= AirportSim.numEvents+1;
    }
    // ---- The line below is changed 
    Event(double delay, EventHandler handler, int eventType, Airplane airplaneType,
			Airport[] airports, double arrivalTime, double departureTime,int nextDest) {
        this();
        m_time = delay;
        m_handler = handler;
        m_eventType = eventType;
	m_airplaneType = airplaneType;
	m_airports = airports;
	m_arrivalTime = arrivalTime;
	m_departureTime = departureTime;
	m_nextDest = nextDest; 
    	//System.out.println("Inside  Event");
    }

    public int getId() {
        return m_eventId;
    }

    public double getTime() {
        return m_time;
    }

    public Airplane getAirplaneType(){
	return m_airplaneType;
    }

    public Airport[] getAirports(){
	return m_airports;
    }

    public EventHandler getHandler() {
        return m_handler;
    }

    public int getNextDest(){
        return m_nextDest;
    }

   public double getArrivalTime(){
        return m_arrivalTime;
        }

    public void setTime(double time) {
        m_time = time;
    }
   public void setArrivalTime(double a_time){
         m_arrivalTime = a_time;
        }

   public double getDepartureTime(){
        return m_departureTime;
        }

   public void setDepartureTime(double d_time){
         m_departureTime = d_time;
        }

    public int getType() { 
	return m_eventType; 
    }


    public void setHandler(EventHandler handler) {
        m_handler = handler;
    }
    public void setNextDest(int n_dest){
        m_nextDest = n_dest;
    }

    @Override
    public int compareTo(final Event ev) {
        int timeCmp = Double.compare(m_time, ev.getTime());
	    //System.out.println("jjjj"+m_time);
        if(timeCmp != 0) {
            return timeCmp;
        }
        else{
            return Integer.compare(m_eventId, ev.getId());
	}
    }
}
