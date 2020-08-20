This visualization tool is based on flight animation
code by Tom Noda (Tnoda.com). We modified it to accept
outputs from our simulation program as inputs. We also
modified it to visualize the flights according to the
timing given in our input file.

You will need python to start a simple webserver.

If you are using python2, you can start the local 
webserver with "python2 -m SimpleHTTPServer 8888"
("python3 -m http.server 8888" for python3) at the
prompt while your current working directory is
"flightanimation" (not this viz folder).

Then, using a web browser (Firefox recommended),
type "localhost:8888" as the URL to load the
visualization tool. There is a 10-second delay
before the visualization of flights is started.

By default, it will load the sample input file,
which shows the following flights departing at
the specific timestep.

clock,src,dest
5.0,AMS,NRT
5.0,AMS,LAX
20.0,ATL,NRT
20.0,ATL,AMS
35.0,LAX,AMS
35.0,LAX,TPE

To load the real log file from simulation output,
replacing data in the default input file by going
into the "flightanimation" folder and executing
"cp allinput.csv input.csv" command. Then, refresh
the web browser.
