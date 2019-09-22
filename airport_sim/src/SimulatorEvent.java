//Rojin Aliehyaei

public class SimulatorEvent extends Event {
    public static final int STOP_EVENT = 0;
    public static final int PRINT_EVENT = 1;

    SimulatorEvent(double delay, EventHandler handler, int eventType, Airplane airplaneType, Airport[] airports, double arrivalTime, double departureTime, int nextDest) {
        super(delay, handler, eventType,  airplaneType, airports, arrivalTime,departureTime,nextDest );
    }
}
