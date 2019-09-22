#PBS -q class
#PBS -l nodes=6:sixcore
#PBS -l walltime=00:15:00
#PBS -N hw3
OMPI_MCA_mpi_yield_when_idle=0
/nethome/raliehyaei3/bin/mpirun --hostfile $PBS_NODEFILE -np 64 /nethome/raliehyaei3/jre8/bin/java -cp /nethome/raliehyaei3/openmpi-2.0.2/examples AirportSim 64 1;
/nethome/raliehyaei3/bin/mpirun --hostfile $PBS_NODEFILE -np 64 /nethome/raliehyaei3/jre8/bin/java -cp /nethome/raliehyaei3/openmpi-2.0.2/examples AirportSim 64 2;
/nethome/raliehyaei3/bin/mpirun --hostfile $PBS_NODEFILE -np 1 /nethome/raliehyaei3/jre8/bin/java -cp /nethome/raliehyaei3/openmpi-2.0.2/examples AirportSim 64;
