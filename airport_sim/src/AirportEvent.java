//YOUR NAME HERE

public class AirportEvent extends Event {
    public static final int PLANE_ARRIVES = 0;
    public static final int PLANE_LANDED = 1;
    public static final int PLANE_TAXII = 2;
    public static final int PLANE_UNLOADED = 3;
    public static final int PLANE_DEPARTS = 4;
    public static final int PLANE_TAKEOFF = 5;
    public static final int PLANE_MAINTENANCE = 6;
    //---- change the 2 lines below -----
    //AirportEvent(double delay, EventHandler handler, int eventTyp) {
    AirportEvent(double delay, EventHandler handler, int eventType, Airplane airplaneType , 
		Airport[] Airports, double arrivalTime, double departureTime, int nextDest) {
        super(delay, handler, eventType, airplaneType, Airports, arrivalTime, departureTime, nextDest);
    }
}
