# Discrete-event-process-scheduling-simulator

In this assignment you will build your own discrete-event process scheduling simulator for a general OS.
This assignment can be done in groups consisting of at most 3 persons. It must have the following
properties
1. Processes are generated randomly in time. The interarrival time between two processes is
chosen from an exponential distribution, whose mean time can be set as a parameter of the
simulator.
2. Each process must have (at least) the following attributes
a. PID
b. Time generated
c. Expected time to completion
3. Implement the following scheduling strategies to organize process execution
a. First come first serve
b. Round robin (with parametrized time-slice)
c. Shortest Job first
d. Shortest remaining time first
e. Multi-level feedback queue (with parametrized time-slices, 3 queues)
4. Maintain a Gantt chart for process execution
Use appropriate visualization tools to demonstrate the execution of processes, their turnaround times
and average turnaround time, average latency and any other property you feel is striking. Experiment
with choosing different values of parameters. Give detailed insights to explain your observations.
In your submission on moodle, submit a single zip file containing:
1. Source code for the simulator
2. A report detailing your findings
