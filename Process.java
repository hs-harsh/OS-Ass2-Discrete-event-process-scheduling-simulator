
import java.util.*;
import java.lang.*;
import java.io.*;
public class Process implements Comparable<Process>{
	private int pid;
	private int runTime;
	private int startTime;
	
	public int getPid(){return pid;}
	public int getRunTime(){return runTime;}
	public int getStartTime(){return startTime;}

	public Process(int pid, int startTime, int runTime){
		this.pid = pid;
		this.runTime = runTime;
		this.startTime = startTime;
	}

	public static Comparator<Process> startTimeComparator(){
		return new Comparator<Process>(){
			@Override
			public int compare(Process p1, Process p2){
				return p1.startTime - p2.startTime;
			}
		};
	}

	@Override
	public int compareTo(Process p){
		return this.runTime-p.runTime;
	}
}