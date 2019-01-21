# Overview

This is GEAS implementation (including GEAS-ori and GEAS-opt). In order to make it ease for users, we applied GEAS to two typical constraint checking techniques ECC and PCC, and gave a runable example (i.e., a partial replication package including one hour of taxi data in the form of context changes and 22 consistency constraints annotated with s-conditions). 

Note that the consistency constraints are already annotated with s-conditions (statically analyzed according to certain deduction rules in advance) for ease of GEAS's usage. If anyone is interested, he/she can reach to Project S-conditionGenTool under the same GitHub page to know how s-conditions are generated and annotated for a raw consistency constraint file.

If there is any question, feel free to contact us (cocowhy1013@gmail.com).

# Project description

The next is some descriptions of this project for better understanding.

Important folders/files' descriptions:
	data/changes: context changes saved (as demo)
	data/out: for logging the inc+metric information in the detection
	
	src/config: saving constraint files and other configuration files
	src/middleware/Main.java: the entrance of the whole project
	src/middleware/GEAS-ori: key part of GEAS-ori implementation
	src/middleware/GEAS-opt: key part of GEAS-opt implementation
	
# Project execution

The next is how to execute the whole project
Execution:
	src/middleware/Main.java is the main entering. This is an eclipse project. One can easily import the whole project into Eclipse and click "Run" for middleware/Main.
	
	There are three parameters for the Main.java, i.e., #data, #scheduling, and #checking.
	
	#data: we provide a demo file for execution
	#scheduling: one of the four built-in scheduling strategies, i.e., Imd, Bat, GEAS-ori, and GEAS-opt
	#checking: one of the two built-in constraint checking techniques, i.e., ECC and PCC
	
	
	
Huiyan Wang
cocowhy1013@gmail
2019/1/11
