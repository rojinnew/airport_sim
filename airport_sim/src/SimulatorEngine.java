//YOUR NAME HERE
import java.util.TreeSet;
import mpi.*;
import java.io.*;
import java.nio.*;
import java.util.concurrent.TimeUnit;
import java.util.PriorityQueue;
import java.util.*;

public class SimulatorEngine implements EventHandler {
    private double m_currentTime;
    private TreeSet<Event> m_eventList;
    private boolean m_running;

    SimulatorEngine() {
        m_running = false;
        m_currentTime = 0.0;
        m_eventList = new TreeSet<Event>();
    }
    void run() throws MPIException{
       	m_running = true;
       	while(m_running && !m_eventList.isEmpty()) 
	{
         	Event ev = m_eventList.pollFirst();
            	m_currentTime = ev.getTime();
            	ev.getHandler().handle(ev);
        }
   }


    void nullRun() throws MPIException{
        m_running = true;

	int size=0;
	int rank=-1;
	size = MPI.COMM_WORLD.getSize();
	rank = MPI.COMM_WORLD.getRank();
	//System.out.println("here "+rank);
	Request request[] = new Request [size];
	Request request1[] = new Request [size];
	Request request2[] = new Request [size];
	Request nullRequest[] = new Request [size];
	Request nullSend[] = new Request [size];
        Request terRequest0[] = new Request [size];
        Request ackSend0[] = new Request [size];
        Request arrivalRequest = null ;
        Request[] terSend = new Request[size];
        Request[] tempRequest=new Request[size];
        Request[] ackRequest=new Request[size];
	boolean toggle[] = new boolean [size];
	boolean toggle2[] = new boolean [size];
	boolean nullToggle[] = new boolean [size];
    	double[] tCh = new double[size]; 
	boolean[] tempToggle=new boolean[size];;
	boolean[] ackToggle=new boolean[size];;
	boolean[] checked = new boolean[size];
	boolean[] ackCheck =  new boolean[size];
	DoubleBuffer iNullMessage = MPI.newDoubleBuffer(3);
        //ArrayList<PriorityQueue<recMessage>> inChs = new ArrayList<PriorityQueue<recMessage>>(size);
	for(int index =0; index<size; index++)
	{
		if(index != rank)
		{
				double t_f_time2 = AirportSim.dis[rank][index]*60/AirportSim.fastest;
                		double lookahead  = (int) t_f_time2*100.00/100.00;

				// 0:time , 1:idRecv, 2:type	
				iNullMessage.put(0,0.0+lookahead); 		
				iNullMessage.put(1,-5); 		
				iNullMessage.put(2,0.0); 		
				//send initial null message to all neighbors
            			nullSend[index] = MPI.COMM_WORLD.iSend(iNullMessage, 3, MPI.DOUBLE, index,50); 
                     		while(nullSend[index].testStatus() == null);
				nullSend[index] = null;
		}
	}
        //System.out.println("Rank " + rank + " Done Sending All Init NULL");
	//System.out.println("Rank " + rank + " name of airport is "+AirportSim.dict.get(rank));
		
	for(int i=0;i<size;i++)
	{
			tCh[i] = 0.0;
			toggle[i] = false;
			toggle2[i] = false;
			//temToggle[i] = false;
			ackToggle[i] = false;
			checked[i] = false;
			request[i] = null;
			terRequest0[i] = null;
			ackRequest[i] = null;
			terSend[i] = null;
	}
	double t_old =-1.0;
	int arrivalTag=200;
        int terTag = 300;
        int ackTag = 400;
	double delay= -1;
	double timeRecv = -1.0;
	int idRecv = -1;
	int type = -1;
	int ret=-1;
	int retAck=-1;
	int pCounter =0;
	int y = 0;
	boolean isTakeoff = false; 
	int TakeoffCounter = 0; 
	boolean terminationFlag = false;
		/*
                IntBuffer terMessage0 = MPI.newIntBuffer(1);
                IntBuffer ackMessage0 = MPI.newIntBuffer(1);
		*/
                //IntBuffer[] terMessage0 = new IntBuffer[size];
        DoubleBuffer[] ackMessage0 = new DoubleBuffer[size];
	DoubleBuffer[] imessage = new DoubleBuffer[size];
        DoubleBuffer[] iTerMessage0 = new DoubleBuffer[size] ;
	for(int j=0; j<size; j++)
	{
		   imessage[j] = MPI.newDoubleBuffer(3);
		   imessage[j].put(0,-1.0);
		   imessage[j].put(1,-1.0);
		   imessage[j].put(2,-1.0);
		   ackMessage0[j] = MPI.newDoubleBuffer(3);
		   ackMessage0[j].put(0,-1.0);
		   ackMessage0[j].put(1,-1.0);
		   ackMessage0[j].put(2,-1.0);
		   iTerMessage0[j] = MPI.newDoubleBuffer(3);
		   iTerMessage0[j].put(0,-1.0);
		   iTerMessage0[j].put(1,-1.0);
		   iTerMessage0[j].put(2,-1.0);
	}
      	DoubleBuffer imessage2 = MPI.newDoubleBuffer(3);
        DoubleBuffer tempMessage = MPI.newDoubleBuffer(3);
	       	DoubleBuffer iNullSMessage = MPI.newDoubleBuffer(3);
	double localTime = 0.0;

	while(m_running) { // && !m_eventList.isEmpty()) {
		
		//******************************
	   	   try{	
		        pCounter =0; 
			for(int v=0; v<size;v++)
			{
				if(AirportSim.terminate1[v] == true)
				{
					//System.out.println("rank " + rank+", v "+v);
					pCounter ++;
				}
			}
			//System.out.println("rank " +rank+ "counter "+pCounter);
			if(pCounter==size)
			{
        			Event ev = m_eventList.pollFirst();
          			m_currentTime = ev.getTime();
            			ev.getHandler().handle(ev);
                        }

			for(int from=0;from<size;from++)
			{
		   	   if(from != rank)
			   { 
				if(!toggle[from]) 
				{
            			    request[from] = MPI.COMM_WORLD.iRecv(imessage[from], 3, MPI.DOUBLE, from, 50);
				    toggle[from] = true;
                		    //System.out.println("Rank " + rank + " Wait to iRecv NULL OR Arrival Mesg from " + from);
				}
	    			if(((request[from].testStatus()) !=null) && (toggle[from] == true))
				{
			       	   timeRecv = (double) imessage[from].get(0);
			           idRecv = (int) imessage[from].get(1);
				   type = (int) imessage[from].get(2);
				   imessage[from].put(0,-1.0);
				   imessage[from].put(1,-1.0);
				   imessage[from].put(2,-1.0);
				   request[from] = null;	
				   if(type==1)
				   {	
                		//	System.out.println("Rank " + rank + 
				//			" Recved Arrival Mesg from " + from + " and it is type " + type);
            	 	       // 	System.out.println("Rank: " + rank +
				//			" received an arrival with arrival time of"
				//		 	+ timeRecv + " and " + idRecv + " from rank " + from);
					//if(Simulator.getCurrentTime() > timeRecv)
						//System.out.println("wrong");
					delay = timeRecv- Simulator.getCurrentTime();
					//System.out.println("Rank " + rank + " delay " + delay);
					AirportEvent ArrivalEvent = new AirportEvent(delay,AirportSim.Airports[rank],
						AirportEvent.PLANE_ARRIVES,AirportSim.Airplanes[idRecv],
						AirportSim.Airports,0.0,0.0,rank);
					Simulator.schedule(ArrivalEvent);
					ArrivalEvent = null;
				    }
				    else if (type ==0)
				    {
            	 	        	//System.out.println("Rank: " + rank + " Recved NULL message with time stamp "
					//	+ timeRecv +" from Rank "+  from+ ", Now time is " 
					//	+ Simulator.getCurrentTime());
				    	tCh[from]=timeRecv;
				    }
				    // tremination from others
				    else if(type == 2)
				    {
                                   	AirportSim.terminate1[from]=true;
                		   	//System.out.println("Rank " + rank + " Recved Term Mesg from " + from);
				   	//if(timeRecv!= AirportSim.termination) 
						//System.out.println("false alarm");
                                   	tCh[from]=AirportSim.termination;
                                   	ackMessage0[from].put(0,-5.0);
                                   	ackMessage0[from].put(1,-5);
                                   	ackMessage0[from].put(2,3.0);
                                   	ackSend0[from] = MPI.COMM_WORLD.iSend(ackMessage0[from], 3, MPI.DOUBLE, from,50);
                		   	//System.out.println("Rank " + rank + " Sending Term Mesg Ack to " + from);
                                   	while(ackSend0[from].testStatus() == null);
                		   	//System.out.println("Rank " + rank + " Sent Term Mesg Ack to " + from);

				    }
				    else if(type == 3)
				    {
					ackCheck[from] = true;
					AirportSim.terminate1[from] = true;
					y++;
                                    	if(y==size-1)	AirportSim.terminate1[rank]=true;
				    }

			            timeRecv = -1.0;
			            idRecv =-1;
				    type =-1;
				    toggle[from] = false;
				}
		  	    }
	        	}

			int minTCh = -1;
			if(rank==0){
				minTCh = 1;
				for(int k=2;k<size;k++){
                                	if(k != rank){
						if (tCh[k]< tCh[minTCh])
						minTCh = k; 
		     			}	
				}	
			}
			else{
				minTCh = 0;
				for(int k=1;k<size;k++){
                                	if(k != rank){
						if (tCh[k]< tCh[minTCh])
						minTCh = k; 
		     			}	
				}	
			}
			localTime = tCh[minTCh];
			//System.out.println("Rank: "+ rank+ " channel time: "+ tCh[minTCh]);
			if (AirportSim.terminate1[rank]==false)
			{
				while((m_eventList.first().getTime() <= tCh[minTCh]) && 
					(m_eventList.first().getTime()<AirportSim.termination) )
				{
            				Event event = m_eventList.pollFirst();
					AirportEvent airEvent = (AirportEvent)event;

					if(airEvent.getType()==AirportEvent.PLANE_TAKEOFF)
					{
                                            checked[airEvent.getNextDest()] = true;
						
					}
					/*
					if(Simulator.getCurrentTime() > event.getTime())
						System.out.println("wrong");
					*/
            				m_currentTime = event.getTime();
            				event.getHandler().handle(event);
					localTime = m_currentTime;
				}
			}


			if (terminationFlag==false)
			{
			   if((m_eventList.first().getTime()==AirportSim.termination)&&(tCh[minTCh]>=AirportSim.termination))
			   {
				//System.out.println("termination rank " + rank +" and min time"+tCh[minTCh]); 
                                for(int to=0; to<size; to++)
				{
                                     if((to != rank))
				     {
                                        iTerMessage0[to].put(0,-5.0);
                                        iTerMessage0[to].put(1,-5.0);
                                        iTerMessage0[to].put(2,3.0);
                                        terSend[to] = MPI.COMM_WORLD.iSend(iTerMessage0[to], 3, MPI.DOUBLE, to,50);
                			//System.out.println("Rank " + rank + " Sending Term Msgs to " + to);
                                        while(terSend[to].testStatus() == null);
                			//System.out.println("Rank " + rank + " Sent Term Msgs to " + to);
                                     }
                                }
				terminationFlag = true;	
				//while(terminationFlag==false) terminationFlag = true;	
				localTime = AirportSim.termination;
			  }
			}
			// Send null event to processor that you didn't send any airplane  
			//System.out.println("testing"+ tCh[minTCh] + " and "+t_old);
			if ((terminationFlag==false) )
			{	
		         if((localTime !=t_old )&& (localTime!=0.0)){	
			  for(int to=0;to<size;to++)
			  {
                                if((to != rank) && (AirportSim.terminate1[to]==false)){
					//if(checked[to] == false){
						double t_f_time3 = AirportSim.dis[rank][to]*60/AirportSim.fastest;
                				double lookahead  = (int) t_f_time3*100.00/100.00;
                				//double lookahead  = 1.0;
						double total =localTime+lookahead; 
						iNullSMessage.put(0,total); 		
						iNullSMessage.put(1,-5.0); 		
						iNullSMessage.put(2,0.0); 		
            					nullSend[to] = MPI.COMM_WORLD.iSend(iNullSMessage, 3, MPI.DOUBLE, to,50); 
                     				while(nullSend[to].testStatus() == null);
                				//System.out.println("Rank " + rank + " Sent NULL to " + to +"which has"+total );
					//}
				}
	        	   }
			 }
			}
			t_old = localTime;  

	   	   } catch (MPIException mpiex) { System.out.println("Ouch in SimEngine"); }
	}

   }

    void yawnsRun() throws MPIException{
        m_running = true;
	int size=0;
	int rank=-1;
	size = MPI.COMM_WORLD.getSize();
	rank = MPI.COMM_WORLD.getRank();
	//if(size>1)
	//{

		Request recRequest[] = new Request [size];
		Request reportRequest= null;
                DoubleBuffer[] recMessage = new DoubleBuffer[size] ;
                DoubleBuffer lbtsMessage = MPI.newDoubleBuffer(3) ;
		DoubleBuffer inReport = MPI.newDoubleBuffer(3 * size);
		boolean[] toggle = new boolean[size];
                for(int j=0; j<size; j++)
                {
                           recMessage[j] = MPI.newDoubleBuffer(3);
			   toggle[j] = false;
		}
		double minLookahead = calculateLookahead(rank,size);
		double m_grantedTime = minLookahead;
		double m_sendCount =0.0;
		double m_recCount =0.0;
		double m_diffCount = 0.0;
		double m_next =0.0;
		double m_termination = 0.0;	
		double allDiffCount = 0.0;
		boolean allTerminate = false;
		double minNext=0.0;
                double timeRecv = 0.0;
                int idRecv = 0;
                int type =  0;
		double delay =0.0;
		double zero =0.0;
		while(m_running) { // && !m_eventList.isEmpty()) {
		
		//******************************
	   	   try{	
            		   //request[from] = MPI.COMM_WORLD.iRecv(imessage[from], 3, MPI.DOUBLE, from, 50);
 		          //terSend[to] = MPI.COMM_WORLD.iSend(iTerMessage0[to], 3, MPI.DOUBLE, to,50);
			  //(m_eventList.first().getTime() 
			// DoubleBuffer iNullSMessage = MPI.newDoubleBuffer(3);
			
                        for(int from=0;from<size;from++)
                        {
                           if(from != rank)
                           {
                                if(!toggle[from])
                                {
                                    recRequest[from] = MPI.COMM_WORLD.iRecv(recMessage[from], 3, MPI.DOUBLE, from, 50);
                                    toggle[from] = true;
                                    //System.out.println("Rank " + rank + " Wait to iRecv NULL OR Arrival Mesg from " + from);
                                }
                                if(((recRequest[from].testStatus()) !=null) && (toggle[from] == true))
                                {
				   //System.out.println("rank " +rank+" tttyyyyyyyyyyyyyyyyyyyyyyyyyy"+m_recCount);
				   AirportSim.recCount = AirportSim.recCount+1.0;
				   m_recCount =AirportSim.recCount; 
				   //System.out.println("rank "+rank+" tttttttwwww"+m_recCount);
                                   timeRecv = (double) recMessage[from].get(0);
                                   idRecv = (int) recMessage[from].get(1);
                                   type = (int) recMessage[from].get(2);
                                   recMessage[from].put(0,-1.0);
                                   recMessage[from].put(1,-1.0);
                                   recMessage[from].put(2,-1.0);
                                   recRequest[from] = null;
                                   //if(type==1)
                                   //{
                                        delay = timeRecv- Simulator.getCurrentTime();
					/*
                                        if(Simulator.getCurrentTime() > timeRecv)
					{
                                                //System.out.println("wrong 1, delay"+delay+" on rank "+rank);
					}
					*/
                                        //System.out.println("Rank " + rank + " delay " + delay);
                                        AirportEvent ArrivalEvent = new AirportEvent(delay,AirportSim.Airports[rank],
                                                AirportEvent.PLANE_ARRIVES,AirportSim.Airplanes[idRecv],
                                                AirportSim.Airports,0.0,0.0,rank);
                                        Simulator.schedule(ArrivalEvent);
                                        ArrivalEvent = null;
                                    //}
					
                                    timeRecv = -1.0;
                                    idRecv =-1;
                                    type =-1;
                                    toggle[from] = false;

				}
			   }
			}
			m_next = m_eventList.first().getTime();
			//System.out.println("eventList size "+m_eventList.size());
			//System.out.println("m_next "+m_next + " on rank "+rank+" type is "+ m_eventList.first().getType());
			if( m_eventList.first().getTime() == AirportSim.termination){   
				m_termination = 1.0;
			}
			else{
				m_termination = 0;
			}
			m_sendCount = AirportSim.sentCount;
			m_diffCount = m_recCount - m_sendCount ;	
		        //System.out.println("rank "+rank+" tttttttmmmm"+m_recCount);
			//System.out.println("m_termination "+m_termination);
			//System.out.println("rank "+rank+"m_recCount "+m_recCount);
			//System.out.println("rank "+rank+"m_sentCount "+m_sendCount);
			//System.out.println("m_diffCount "+m_diffCount);
			//System.out.println("m_next "+m_next);
			lbtsMessage.put(0,m_diffCount);				
			lbtsMessage.put(1,m_termination);				
			lbtsMessage.put(2,m_next);				
			reportRequest = MPI.COMM_WORLD.iAllGather(lbtsMessage, 3 , MPI.DOUBLE, inReport, 3, MPI.DOUBLE);
			// do other work here
			reportRequest.waitFor();
			reportRequest.free();
			//System.out.println("here 1");		
			allDiffCount = inReport.get(0); 
			allTerminate = valueCheck(inReport.get(1)); 
			minNext = inReport.get(2); 
			for (int i = 1; i < size; i++) {
				allDiffCount = allDiffCount+ inReport.get(i*3);
				allTerminate = allTerminate && valueCheck(inReport.get((i*3)+1)); 
				if(inReport.get((i*3)+2) < minNext)
					minNext = inReport.get((i*3)+2);
			}
			//System.out.println("allDiffCount "+allDiffCount);
			//System.out.println("allTermination "+allTerminate);
			//System.out.println("minnext "+minNext+" on rank "+rank );
			zero = 0.0;
			if(Double.compare(allDiffCount, zero)==0)
			{
				
				if(allTerminate == true){
					m_grantedTime = AirportSim.termination;	
                                       	Event event = m_eventList.pollFirst();
					/*
                                        if(Simulator.getCurrentTime() > event.getTime())
                                               	System.out.println("rank "+rank+", wrong 2");
					*/
                                        m_currentTime = event.getTime();
                                        event.getHandler().handle(event);
				}
				else{
					m_grantedTime = minNext+minLookahead;
					//System.out.println("here 2 ");		
					//System.out.println("minNext "+minNext+"on rank "+rank);		
					//System.out.println("minLookahead "+minLookahead+ "on rank " + rank);		
					//System.out.println("eventList size "+m_eventList.size());
					while((m_eventList.first().getTime() <= m_grantedTime)&&(m_eventList.first().getTime() <AirportSim.termination ) ){
                                        	Event event = m_eventList.pollFirst();
					/*
                                        	if(Simulator.getCurrentTime() > event.getTime())
                                                	System.out.println("wrong");
					*/
                                        	m_currentTime = event.getTime();
                                        	event.getHandler().handle(event);
                               		}
				}
							
			}
	
		     
		

	   	   } catch (MPIException mpiex) { System.out.println("Ouch in SimEngine"); }
		}
	//}	
	/*	
	else if(size==1) //Sequential version
	{
        	m_running = true;
        	while(m_running && !m_eventList.isEmpty()) 
		{
            		Event ev = m_eventList.pollFirst();
            		m_currentTime = ev.getTime();
            		ev.getHandler().handle(ev);
        	}
    	}
	*/

}







   public double calculateLookahead(int myRank,int size){
	double t_f_time = 0.0;
        double lookahead  = 0.0;
	double temp_lookahead=0.0;
	double  temp_f = 0.0;
	if(myRank == 0){ 
		temp_f =  AirportSim.dis[1][myRank]*60/AirportSim.fastest;
            	lookahead  = (int) t_f_time*100.00/100.00;
		for(int destination=2; destination < size ;destination++){
	    		t_f_time = AirportSim.dis[destination][myRank]*60/AirportSim.fastest;
            		temp_lookahead  = (int) t_f_time*100.00/100.00;
			if(temp_lookahead < lookahead)
				lookahead = temp_lookahead;
		}
	}
	else
	{
		temp_f =  AirportSim.dis[0][myRank]*60/AirportSim.fastest;
            	lookahead  = (int) t_f_time*100.00/100.00;
		for(int destination=1; destination < size ;destination++){
			if(destination !=myRank){
	    			t_f_time = AirportSim.dis[destination][myRank]*60/AirportSim.fastest;
            			temp_lookahead  = (int) t_f_time*100.00/100.00;
				if(temp_lookahead < lookahead)
					lookahead = temp_lookahead;
			}
		}
	}
	return lookahead;

   }
  public boolean valueCheck(double value)
  {
	double m = 0.0;
	if(Double.compare(value, m) == 0)
		return false;
	else
		return true;
	
		
  }

   public void handle(Event event) {
        SimulatorEvent ev = (SimulatorEvent)event;
	int rank=-1;
	try{	
		rank = MPI.COMM_WORLD.getRank();

	} catch (MPIException mpiex) {
                System.out.println("Ouch in SimEngine");
	}

        switch(ev.getType()) {
            case SimulatorEvent.STOP_EVENT:
                m_running = false;
                System.out.println("Simulator on rank " + rank + " stopping at time: " + ev.getTime());
                break;
            case SimulatorEvent.PRINT_EVENT:
		Airport[] airports = ev.getAirports();			
		for(int i=0;i<airports.length;i++)
		{ 	
                   System.out.println(ev.getTime()/60+", "+airports[i].getCirclingTime()+
					","+airports[i].getPassArrive()+
					","+airports[i].getPassDepart()+","+ airports[i].getName());
	            //System.out.println(ev.getTime()/60+", "+airports[i].getPassArrive()+","+ airports[i].getName());
                   //System.out.println(ev.getTime()/60+", "+airports[i].getPassDepart()+","+ airports[i].getName());
                   //System.out.println(ev.getTime()+": #arrival "+ airports[i].getPassArrive() +" "+ airports[i].getName());  
                   //System.out.println(ev.getTime()+": #departure "+ airports[i].getPassDepart() +" "+ airports[i].getName());  
			/*
                	System.out.println(ev.getTime()+": Total ciricing time at "+ airports[i].getName()+
							" at is "+airports[i]. getCirclingTime());
	
                	System.out.println(ev.getTime()+": Total number of passengers arrived at "+
						airports[i].getName()+" is "+airports[i]. getPassArrive());
                	System.out.println(ev.getTime()+": Total number of passengers departed from " +
						 airports[i].getName()+" is "+airports[i]. getPassDepart());
			*/
		}
                break;
            default:
                System.out.println("Invalid event type");
        }
    }

    public void schedule(Event event) {
        m_eventList.add(event);
    }

    public void stop() {
        m_running = false;
    }

    public double getCurrentTime() {
        return m_currentTime;
    }
}
