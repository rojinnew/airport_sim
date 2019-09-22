//YOUR NAME HERE
//TODO add number of passengers, speed
import java.util.*;
public class Airplane {
    private String m_name;
    private int m_maxNumberPassengers;
    private double m_speed;
    private int m_dest;
    private int m_id;
    private int m_numberPassengers;
    private int m_runway;
    private double m_mtcTime;
    public Airplane(int id, String name, int max_passengers,double speed, int dest, 
			int num_passengers,int runway,  double mtc_time) {
        m_name = name;
        m_numberPassengers = num_passengers;
        m_maxNumberPassengers = max_passengers;
        m_speed = speed;
	m_dest = dest;
	m_id = id;
	m_runway = runway;
    	m_mtcTime = mtc_time;
    }

    public String getName() {
        return m_name;
    }
    public int getId() {
        return m_id;
    }

    //---- Added 3 lines -----
    public int getNumPassengers() {
        return m_numberPassengers;
    }
    public int getMaxNumPassengers() {
        return m_maxNumberPassengers;
    }
    public void setNumPassengers(int numberPassengers) {
        m_numberPassengers = numberPassengers;
   } 

    //---- Added 3 lines -----
    public double getSpeed() {
        return m_speed;
    }
	/*
   public double getArrivalTime(){
	return m_arrivalTime;
	}

   public void setArrivalTime(double a_time){
	 m_arrivalTime = a_time;
	}

   public double getDepartureTime(){
	return m_departureTime;
	}

   public void setDepartureTime(double d_time){
	 m_arrivalTime = d_time;
	}
	*/
   public int getRunway(){
	return m_runway;
	}

   public void setRunway(int runway){
	 m_runway = runway;
	}

   public void setMtcTime(double mtc_time){
	m_mtcTime = mtc_time; 
	}
 
   public double getMtcTime(){
	return m_mtcTime ; 
	}

}
