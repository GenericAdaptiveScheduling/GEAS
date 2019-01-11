
This is GEAS implementation (including GEAS-ori and GEAS-opt). In order to make it ease for users, we applied GEAS to two typical constraint checking techniques ECC and PCC, and gave a runable example (including necessary context changes and consistency constraints).

If there is any question, feel free to contact us (cocowhy1013@gmail.com).

The next is some descriptions of this project for better understanding.

Important folders/files' descriptions:
	data/changes: context changes saved (as demo)
	data/out: for logging the inc+metric information in the detection
	
	src/config: saving constraint files and other configuration files
	src/middleware/Main.java: the entrance of the whole project
	src/middleware/GEAS-ori: key part of GEAS-ori implementation
	src/middleware/GEAS-opt: key part of GEAS-opt implementation
	
	
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