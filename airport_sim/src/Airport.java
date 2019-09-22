// Rojin Aliehyaei
import java.util.*;
import mpi.*;
import java.io.*;
import java.nio.*;
public class Airport implements EventHandler {
    //TODO add landing and takeoff queues, random variables
    private int m_inTheAir;
    private int m_onTheGround;
    //private boolean m_freeToLand;
    private boolean m_runwayFree1;
    private boolean m_runwayFree2;
    private double m_flightTime;
    private double m_runwayTimeToLand;
    private double m_requiredTimeToTaxii;
    private double m_requiredTimeToUnload;
    private double m_requiredTimeOnGround;
    private double m_requiredTimeToTakeOff;
    private double m_mtcCycle;
    private double m_requiredTimeMtc;
    private double m_taxiiDelay;
    private double m_unloadDelay;
    private double m_groundDelay;
    private double m_mtcDelay;
    private int m_airportId;
    private Hashtable<Integer, String> m_dict;
    private double[][] m_dis; 
    private Queue<AirportEvent> m_landing_queue1 ;   
    private Queue<AirportEvent> m_takeoff_queue1 ;   
    private Queue<AirportEvent>[] m_runway_tqs; 
    private List<Queue<AirportEvent>> m_runway_lqs; 
    private double m_circlingTime;    
    private double m_waitingTakeoff;    
    private int m_passArrive;    
    private int m_passDepart;
    private Random m_rand_dest;  
    private Random m_rand_pass;  
    private long m_seed;  
    private Request request1 = null; 
    ArrayList <ArrayList<Integer>> m_occupancy = new ArrayList<ArrayList<Integer>>(); 
    public Airport(int id, double runwayTimeToLand,double requiredTimeToTaxii,double requiredTimeToUnload, 
	double requiredTimeOnGround, double requiredTimeToTakeOff, Hashtable<Integer,String> dict,double[][] dis,
	double circling_time, int passArrive,int passDepart) {
        m_airportId = id;
        m_inTheAir =  0;
        m_onTheGround = 0;
    	m_runwayFree1 = true;
    	m_runwayFree2 = true;
        m_runwayTimeToLand = runwayTimeToLand;
        m_requiredTimeToTaxii = requiredTimeToTaxii;
        m_requiredTimeToUnload = requiredTimeToUnload;
        m_requiredTimeOnGround = requiredTimeOnGround;
        m_requiredTimeToTakeOff = requiredTimeToTakeOff;
	m_requiredTimeMtc = 10*60;
	//m_requiredTimeMtc = 30*60;
    	m_mtcCycle = 65*60;
    	//m_mtcCycle = 65*8000*60;
        m_taxiiDelay = 0.0;
        m_unloadDelay = 0.0;
        m_groundDelay = 0.0;
        m_mtcDelay = 0.0;
	m_dict = dict; 
	m_dis = dis; 
        m_landing_queue1 = new LinkedList<AirportEvent>();   
        m_takeoff_queue1 = new LinkedList<AirportEvent>();   
        ArrayList<Integer> m_occupancy_1 = new ArrayList<Integer>(); 
        m_circlingTime = 0.0;    
        m_waitingTakeoff = 0.0;    
     	m_passArrive = 0;    
    	m_passDepart = 0;  
	m_seed = (long) m_airportId;  
       	m_rand_dest = new Random(m_seed);
       	m_rand_pass = new Random(m_airportId*500);
        request1 = null; 
	for (int i=0;i<2;i++)
	{
        	m_occupancy.add( new ArrayList<Integer>()); 
	}
    }
    public String getName() { 
        //return m_airportName;
        return m_dict.get(m_airportId);
    }
   
    public int getId() { 
        return m_airportId;
    }

    public double getCirclingTime(){
        return m_circlingTime;
    }

    public double getWaitingTakeoff(){
        return m_waitingTakeoff;
    }
    public int getPassArrive(){
        return  m_passArrive; 
    }

    public int getPassDepart(){
        return  m_passDepart;
    }
    public double getGroundDelay(){
        return m_groundDelay;
    }
    public void setGroundDelay(double delayTime){
         m_groundDelay= delayTime;
    }
    public double getUnloadDelay(){
        return m_unloadDelay;
    }
    public void setUnloadDelay(double delayTime){
         m_unloadDelay= delayTime;
    }
    public double getTaxiiDelay(){
        return m_taxiiDelay;
    }
    public void setTaxiiDelay(double delayTime){
         m_taxiiDelay= delayTime;
    }
    public double getMtcDelay(){
        return m_mtcDelay;
    }
    public void setMtcDelay(double delayTime){
         m_mtcDelay = delayTime;
    }
   
    public void handle(Event event) {
	long seed;
	Random rand;  
        AirportEvent airEvent = (AirportEvent)event;
	Airplane m_airplaneType = airEvent.getAirplaneType();
	Airport[] m_airports = airEvent.getAirports();
	int m_nextDest = airEvent.getNextDest();
	int m_runway = m_airplaneType.getRunway();
        DoubleBuffer imessage = MPI.newDoubleBuffer(3);
        IntBuffer ackmessage = MPI.newIntBuffer(1);

                int size =-1;
		int rank =-1;
                try {
                        size = MPI.COMM_WORLD.getSize();
                        rank = MPI.COMM_WORLD.getRank();
                } catch (MPIException mpiex) {
                        System.out.println("Ouch in Airport Take off");
                }

        switch(airEvent.getType()) {
            case AirportEvent.PLANE_ARRIVES:
		double arrival_time= Simulator.getCurrentTime();
                AirportEvent landedEvent = new AirportEvent(m_runwayTimeToLand,this,
						AirportEvent.PLANE_LANDED,m_airplaneType,
							m_airports, arrival_time,0.0, m_airportId);
                if(m_runwayFree1 == true) 
		{
                    System.out.println(Simulator.getCurrentTime() + ": Plane "+m_airplaneType.getId() 
					+" arrived at "+m_dict.get(m_airportId)+" airport and runway 1 is free, so began the landing process using runway 1");
		    m_occupancy.get(0).add(m_airplaneType.getId());
		    m_runwayFree1 = false; 
		    m_airplaneType.setRunway(1);
                    Simulator.schedule(landedEvent);
			
                }
		else if(m_runwayFree2 == true)
                {
                    System.out.println(Simulator.getCurrentTime() + ": Plane "+m_airplaneType.getId() 
					+" arrived at "+m_dict.get(m_airportId)+" airport and runway 2 is free, so began the landing process using runway 2");
		    m_occupancy.get(1).add(m_airplaneType.getId());
                    m_runwayFree2 = false; 
                    m_airplaneType.setRunway(2);
                    Simulator.schedule(landedEvent);
                }
		else
		{
                    	System.out.println(Simulator.getCurrentTime() + ": Plane "+m_airplaneType.getId() 
				+" arrived at "+m_dict.get(m_airportId)+" airport and no runways are available, and began circling");
		    	m_landing_queue1.add(landedEvent);
		}
                break;
            case AirportEvent.PLANE_LANDED:
		assert ((m_occupancy.get(m_airplaneType.getRunway()-1).size() ==1 ) && 
			( m_occupancy.get(m_airplaneType.getRunway()-1).get(0) == m_airplaneType.getId() )) == true;

               	System.out.println(Simulator.getCurrentTime() + ": Plane "+
					m_airplaneType.getId() +" completed the landing process "+
					" and began the taxiing process at "+
					m_dict.get(m_airportId)+" airport");

		m_occupancy.get(m_airplaneType.getRunway()-1).remove(0);	
		double land_time= Simulator.getCurrentTime();
		double  c_time=land_time - airEvent.getArrivalTime() - m_runwayTimeToLand;
		m_circlingTime = m_circlingTime +  c_time;
                AirportEvent taxiiEvent = new AirportEvent((m_requiredTimeToTaxii+m_taxiiDelay), 
							this, AirportEvent.PLANE_TAXII, m_airplaneType,m_airports,0,0, m_airportId);
		Simulator.schedule(taxiiEvent);
		int lqueue_size1 = m_landing_queue1.size(); 
		int tqueue_size1 = m_takeoff_queue1.size(); 
		if((lqueue_size1 !=0) && (tqueue_size1 !=0))
		{
			AirportEvent temp_l1 = m_landing_queue1.peek();
			AirportEvent temp_t1 = m_takeoff_queue1.peek();
			if(temp_t1.getDepartureTime()< temp_l1.getArrivalTime())	
			{
		    		AirportEvent NextAirEvent = m_takeoff_queue1.remove();
		    		m_occupancy.get(m_airplaneType.getRunway()-1).add(NextAirEvent.getAirplaneType().getId());
				NextAirEvent.getAirplaneType().setRunway(m_airplaneType.getRunway());
				System.out.println(Simulator.getCurrentTime() +
					": Plane "+NextAirEvent.getAirplaneType().getId()+ 
					" is done waiting for runway and began the taking-off process from runway "+
					m_airplaneType.getRunway()+" toward " + m_dict.get(NextAirEvent.getNextDest())+ " airport");
		    		Simulator.schedule(NextAirEvent);

			}
			else
			{
		    		AirportEvent NextAirEvent = m_landing_queue1.remove();
		    		m_occupancy.get(m_airplaneType.getRunway()-1).add(NextAirEvent.getAirplaneType().getId());
				NextAirEvent.getAirplaneType().setRunway(m_airplaneType.getRunway());
                                System.out.println(Simulator.getCurrentTime() +
                                        ": Plane "+NextAirEvent.getAirplaneType().getId()+
                                        " done circling/waiting and began the landing process using runway "+
					m_airplaneType.getRunway()+" at "+ m_dict.get(m_airportId)+" airport");
		    		Simulator.schedule(NextAirEvent);
			}
		}
		else if(lqueue_size1 !=0)
		{
		    	AirportEvent NextAirEvent = m_landing_queue1.remove();
		    	m_occupancy.get(m_airplaneType.getRunway()-1).add(NextAirEvent.getAirplaneType().getId());
			NextAirEvent.getAirplaneType().setRunway(m_airplaneType.getRunway());
                        System.out.println(Simulator.getCurrentTime() +
                                        ": Plane "+NextAirEvent.getAirplaneType().getId()+
                                       " done circling/waiting and began the landing process using runway "+
				m_airplaneType.getRunway()+" at "+ m_dict.get(m_airportId)+" airport");
		    	Simulator.schedule(NextAirEvent);
		}
		else if(tqueue_size1 !=0)
		{
		    	AirportEvent NextAirEvent = m_takeoff_queue1.remove();
		    	m_occupancy.get(m_airplaneType.getRunway()-1).add(NextAirEvent.getAirplaneType().getId());
			NextAirEvent.getAirplaneType().setRunway(m_airplaneType.getRunway());
			System.out.println(Simulator.getCurrentTime() +
					": Plane "+NextAirEvent.getAirplaneType().getId()+ 
					" is done waiting for runway and began the taking-off process from runway "+
					m_airplaneType.getRunway()+" toward " + m_dict.get(NextAirEvent.getNextDest())+ " airport");
		    	Simulator.schedule(NextAirEvent);
		}
                else
                {
			if (m_airplaneType.getRunway() ==1)
			{
                    		m_runwayFree1 = true;
			}
			else
			{
                    		m_runwayFree2 = true;
			}
                }

                break;

            case AirportEvent.PLANE_TAXII:
                System.out.println(Simulator.getCurrentTime() + ": Plane "+m_airplaneType.getId() 
					+" completed the taxiing process and began the unloading of "+  m_airplaneType.getNumPassengers()+
					 " passengers at "+m_dict.get(m_airportId)+" airport");
                AirportEvent unloadEvent = new AirportEvent(m_requiredTimeToUnload+m_unloadDelay,this,
					AirportEvent.PLANE_UNLOADED,m_airplaneType ,m_airports,0.0,0.0,m_airportId);
                Simulator.schedule(unloadEvent);
		break;
		
            case AirportEvent.PLANE_UNLOADED:
     		m_passArrive = m_passArrive + m_airplaneType.getNumPassengers() ;    
		// check the maintenance time 

	         int num = AirportSim.num_airports;

       		int random_nextDest = m_rand_dest.nextInt(num) ;
		while(random_nextDest==m_airportId)
		{
			random_nextDest = m_rand_dest.nextInt(num);
		}
	        double t_f_time = m_dis[m_airportId][random_nextDest]*60/m_airplaneType.getSpeed();
                double m_flightTime  = (int) t_f_time*100.00/100.00;
	
		if((m_airplaneType.getMtcTime()+ m_flightTime)>= m_mtcCycle)
		{
                	System.out.println(Simulator.getCurrentTime() + ": Plane "+m_airplaneType.getId()
				 +" completed the unloading of "+ m_airplaneType.getNumPassengers()
				 +" passengers and began the maintenance process at "+m_dict.get(m_airportId));
                	AirportEvent mtcEvent = new AirportEvent((m_requiredTimeMtc+m_mtcDelay), this,
					 AirportEvent.PLANE_MAINTENANCE,m_airplaneType ,m_airports,0.0,0.0,random_nextDest);
                	Simulator.schedule(mtcEvent);
		}
		else
		{
                	System.out.println(Simulator.getCurrentTime() + ": Plane "+m_airplaneType.getId()
				 +" completed the unloading of "+  m_airplaneType.getNumPassengers()
				 + " passengers and began the departure process from "+m_dict.get(m_airportId)+" airport");
                	AirportEvent departEvent = new AirportEvent((m_requiredTimeOnGround+m_groundDelay),this,
						 AirportEvent.PLANE_DEPARTS,m_airplaneType ,m_airports,0.0,0.0, random_nextDest);
                	Simulator.schedule(departEvent);
		}
		break;

            case AirportEvent.PLANE_MAINTENANCE:
                System.out.println(Simulator.getCurrentTime() + ": Plane "+m_airplaneType.getId() 
				+" completed the maintenance process and began the departure process from "+m_dict.get(m_airportId)+" airport");
		m_airplaneType.setMtcTime(0.0);
               	AirportEvent departEvent = new AirportEvent((m_requiredTimeOnGround+m_groundDelay),this,
		 AirportEvent.PLANE_DEPARTS, m_airplaneType,m_airports,0.0,0.0, airEvent.getNextDest());
               	Simulator.schedule(departEvent);
	    break;

            case AirportEvent.PLANE_DEPARTS:
				
		double departureTime= Simulator.getCurrentTime();
		//airEvent.setDepartureTime(departureTime);
		int maxP = m_airplaneType.getMaxNumPassengers();
		int halfmaxP  =maxP/2; 
        	int random_integer = m_rand_pass.nextInt(maxP - halfmaxP) + halfmaxP;	
		m_airplaneType.setNumPassengers(random_integer);
		//m_passDepart = m_passDepart + random_integer;
               	m_onTheGround--;
               	AirportEvent takeoffEvent = new AirportEvent(m_requiredTimeToTakeOff,this,
						AirportEvent.PLANE_TAKEOFF, m_airplaneType, m_airports,0.0,departureTime, airEvent.getNextDest());
		if(m_runwayFree1 == true)
                {
                    m_airplaneType.setRunway(1);
                    System.out.println(Simulator.getCurrentTime() + ": Plane "+
				m_airplaneType.getId() +" completed the departure process at "+m_dict.get(m_airportId)+
				" airport and began the taking-off process from runway 1 to " + m_dict.get(m_nextDest)+" airport");
                    m_runwayFree1 = false;
                    //m_inTheAir1++;
		    m_occupancy.get(0).add(m_airplaneType.getId());
                    Simulator.schedule(takeoffEvent);
                }
                else if(m_runwayFree2 == true)
                {
                    m_airplaneType.setRunway(2);
                    System.out.println(Simulator.getCurrentTime() + ": Plane "+
				m_airplaneType.getId() +" completed the departure process at "+m_dict.get(m_airportId)+
				" airport and began the taking-off process from runway 2 to " + m_dict.get(m_nextDest)+" airport");
                    m_runwayFree2 = false;
                    //m_inTheAir2++;
		    m_occupancy.get(1).add(m_airplaneType.getId());
                    Simulator.schedule(takeoffEvent);
                }
                else
                {
                    	System.out.println(Simulator.getCurrentTime() + ": Plane "+
			m_airplaneType.getId() +" completed the departure process at "+m_dict.get(m_airportId)+
			" airport and waiting for a runway  to be free for take-off");
			m_takeoff_queue1.add(takeoffEvent);	
		 }

		break;
            case AirportEvent.PLANE_TAKEOFF:
	        assert ((m_occupancy.get(m_airplaneType.getRunway()-1).size() ==1 ) && 
			( m_occupancy.get(m_airplaneType.getRunway()-1).get(0) == m_airplaneType.getId() )) == true;

               	System.out.println(Simulator.getCurrentTime()+ ": Plane "
				    +m_airplaneType.getId() +" completed the taking-off process from "  
				    +m_dict.get(m_airportId)+" on runway "+m_runway+" to "+ m_dict.get(m_nextDest)+" airport");

		m_passDepart = m_passDepart + m_airplaneType.getNumPassengers();
		//System.out.println("check "+m_passDepart);
		m_occupancy.get(m_airplaneType.getRunway()-1).remove(0);	

		double  w_time=Simulator.getCurrentTime() - airEvent.getDepartureTime() - m_requiredTimeToTakeOff;
		m_waitingTakeoff = m_waitingTakeoff +  w_time;
        	double t_f_time2 = m_dis[m_airportId][m_nextDest]*60/m_airplaneType.getSpeed();
        	double m_flightTime2  = (int) t_f_time2*100.00/100.00;
		// updtae travel time 
		double travel_time2 = m_airplaneType.getMtcTime()+m_flightTime2;
                m_airplaneType.setMtcTime(travel_time2);
                //   **********************************

                //m_inTheAir--;

                if(size >1){
                        try  {
                                int tag = 50;
                                double timeSent = Simulator.getCurrentTime()+m_flightTime2;
                                double aidSent = (double) m_airplaneType.getId();
                                imessage.put(0, timeSent);
                                imessage.put(1, aidSent);
                                imessage.put(2, 1.0);
				boolean flag = false;
                                System.out.println("Rank " + rank + " AirportID " + m_airportId + " preparing to send Take-off" +
                                imessage.get(0) + " and " + imessage.get(1) + " to rank " + m_nextDest);
                                Request request = MPI.COMM_WORLD.iSend(imessage, 3, MPI.DOUBLE, m_nextDest, tag);
                                System.out.println("Rank " + rank + " AirportID " + m_airportId + " iSending Take-off " +
                                         imessage.get(0) + " and " + imessage.get(1) + " to rank " + m_nextDest);
                                while((request.testStatus() == null) &&(AirportSim.terminate1[m_nextDest]==false)  ) 
				{
					flag = true;
				}
                                System.out.println("Rank " + rank + " AirportID " + m_airportId + " Sent Take-off" +
                                         imessage.get(0) + " and " + imessage.get(1) + " to rank " + m_nextDest);
				AirportSim.sentCount =  AirportSim.sentCount+1.0;
                        }  catch (MPIException mpiex) { System.out.println("ouch! in airport");}

                }
                else if (size ==1)
                {

			Airport newAirport = m_airports[m_nextDest];
		//	m_airplaneType.setMtcTime(travel_time2);
               		AirportEvent arrivalEvent = new AirportEvent(m_flightTime2,newAirport
				,AirportEvent.PLANE_ARRIVES, m_airplaneType, m_airports,0.0,0.0, m_nextDest);
              		Simulator.schedule(arrivalEvent);
		}

		lqueue_size1 = m_landing_queue1.size(); 
		tqueue_size1 = m_takeoff_queue1.size(); 
		if((lqueue_size1 !=0) && (tqueue_size1 !=0))
		{
			AirportEvent temp_l1 = m_landing_queue1.peek();
			AirportEvent temp_t1 = m_takeoff_queue1.peek();
			if(temp_t1.getDepartureTime()< temp_l1.getArrivalTime())	
			{
		    		AirportEvent NextAirEvent = m_takeoff_queue1.remove();
		    		m_occupancy.get(m_airplaneType.getRunway()-1).add(NextAirEvent.getAirplaneType().getId());
				NextAirEvent.getAirplaneType().setRunway(m_airplaneType.getRunway());
				System.out.println(Simulator.getCurrentTime() +
					": Plane "+NextAirEvent.getAirplaneType().getId()+ 
					" is done waiting for runway and began the taking-off process from runway "+
					m_airplaneType.getRunway()+" toward " + m_dict.get(NextAirEvent.getNextDest())+ " airport");
		    		Simulator.schedule(NextAirEvent);

			}
			else
			{
		    		AirportEvent NextAirEvent = m_landing_queue1.remove();
		    		m_occupancy.get(m_airplaneType.getRunway()-1).add(NextAirEvent.getAirplaneType().getId());
				NextAirEvent.getAirplaneType().setRunway(m_airplaneType.getRunway());
                                System.out.println(Simulator.getCurrentTime() +
                                        ": Plane "+NextAirEvent.getAirplaneType().getId()+
                                        " done circling/waiting and began the landing process using runway "+
					m_airplaneType.getRunway()+" at "+ m_dict.get(m_airportId)+" airport");

		    		Simulator.schedule(NextAirEvent);
			}
		}
		else if(lqueue_size1 !=0)
		{
		    	AirportEvent NextAirEvent = m_landing_queue1.remove();
		    	m_occupancy.get(m_airplaneType.getRunway()-1).add(NextAirEvent.getAirplaneType().getId());
			NextAirEvent.getAirplaneType().setRunway(m_airplaneType.getRunway());
                        System.out.println(Simulator.getCurrentTime() +
                                        ": Plane "+NextAirEvent.getAirplaneType().getId()+
                                       " done circling/waiting and began the landing process using runway "+
				m_airplaneType.getRunway()+" at "+ m_dict.get(m_airportId)+" airport");
		    	Simulator.schedule(NextAirEvent);
		}
		else if(tqueue_size1 !=0)
		{
		    	AirportEvent NextAirEvent = m_takeoff_queue1.remove();
		    	m_occupancy.get(m_airplaneType.getRunway()-1).add(NextAirEvent.getAirplaneType().getId());
			NextAirEvent.getAirplaneType().setRunway(m_airplaneType.getRunway());
			System.out.println(Simulator.getCurrentTime() +
					": Plane "+NextAirEvent.getAirplaneType().getId()+ 
					" is done waiting for runway and began the taking-off process from runway "+
					m_airplaneType.getRunway()+" toward " + m_dict.get(NextAirEvent.getNextDest())+ " airport");
		    	Simulator.schedule(NextAirEvent);
		}
                else
                {
			if (m_airplaneType.getRunway() ==1)
			{
                    		m_runwayFree1 = true;
			}
			else
			{
                    		m_runwayFree2 = true;
			}
                }
                break;
        }
    }
}



