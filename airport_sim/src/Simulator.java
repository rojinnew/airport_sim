// Rojin Aliehyaei 
import java.util.TreeSet;
import mpi.*;
//singleton
public class Simulator {

    //singleton
    private static SimulatorEngine instance = null;
    public static SimulatorEngine getSim() {
        if(instance == null) {
            instance = new SimulatorEngine();
        }
        return instance;
    }
    private static Airplane AirplaneType = null; 
    private static Airport[] Airports = null; 
    public static void stopAt(double time) {
        Event stopEvent = new SimulatorEvent(time, getSim(), SimulatorEvent.STOP_EVENT, 
				AirplaneType, Airports,0.0,0.0,-1);
        schedule(stopEvent);
    }

    public static void printAt(double time , Airport[] airports) {
        Event printEvent = new SimulatorEvent(time, getSim(), SimulatorEvent.PRINT_EVENT, 
		AirplaneType, airports,0.0,0.0,-1);
        schedule(printEvent);
    }

    public static void run() throws MPIException{
        getSim().run();
    }
    public static void nullRun() throws MPIException{
        getSim().nullRun();
    }
    public static void yawnsRun() throws MPIException{
        getSim().yawnsRun();
    }

    public static double getCurrentTime() {
        return getSim().getCurrentTime();
    }
    public static void schedule(Event event) {
        event.setTime(event.getTime() + getSim().getCurrentTime());
        getSim().schedule(event);
    }
}
