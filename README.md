# Distributed Simulation of Airport Systems

In this work, we modeled and simulated an airport system with 64 airports and 2,560 airplanes for parallel execution in distributed environment using OpenMPI. The simulated events focused on airport operations (i.e., arrival, landing, taxiing, unloading, maintenance, departing, and taking-off). To support synchronization between processors in the distributed simulation, we implemented the scheduling algorithms.

#### Visualization

![Image](https://github.com/rojinnew/airport_simulation/tree/master/vis/image.png)

The simulation outputs are using a customized visualization tool. The tool reads the simulation's log file. Each row of the log file contains the simulation timestamp, the airplane ID, the originating airport, and the destination airport. This visualization tool was adapted from the ![tutorial](https://www.tnoda.com/blog/2014-04-02/) published by Tom Noda. 

#### Running

airport_sim folder has three folders (doc, src, and run) in the tarball.
The source codes are located in the "src" folder.
The report in PDF format is located in the "doc" folder.
The script for running the simulation in batch mode on Jinx
cluster is located in the "run" folder.

The following steps are necessary for compiling and running the
distributed airport system simulation.

(1) Install JVM and Java Compiler

(2) Install OpenMPI with Java Interface Support

(3) Compile Distributed Airport System Simulation with mpijavac

(4) Run the Simulation with mpirun

Interactive run command:
mpirun -np 1 java AirportSim 64 (for sequential execution with 64 airports)

mpirun -np 64 java AirportSim 64 1 (for distributed execution using null message with 64 airports)

mpirun -np 64 java Airport 64 2 (for distributed execution using YAWNS with 64 airports)

Script for batch job submission on Jinx cluster (from attached pbs.sh file):

#PBS -q class

#PBS -l nodes=6:sixcore

#PBS -l walltime=00:15:00

#PBS -N hw3

OMPI_MCA_mpi_yield_when_idle=0
