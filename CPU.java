import java.util.*;
import java.lang.*;
import java.io.*;
public class CPU implements Comparable<CPU>{
	public int startTime;
	public int endTime;
	private int pid;

	public CPU(int startTime, int endTime){
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public CPU(int pid, int startTime, int endTime){
		this.pid = pid;
		this.startTime = startTime;
		this.endTime = endTime;
	}

	public int compareTo(CPU p){
		return this.startTime-p.startTime;
	}

}