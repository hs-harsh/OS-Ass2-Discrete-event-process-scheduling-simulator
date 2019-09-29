import java.util.*;
import java.lang.*;
import java.io.*;
public class Simulator{
	private double lambda1;
	private double lambda2;

	private List<Process> processes;
	private int timeNow;

	public Simulator(double lambda1, double lambda2){
		this.lambda1 = lambda1;
		this.lambda2 = lambda2;

		this.processes = new LinkedList<>();
		timeNow=0;
	}

	public List<Process> getProcesses(){
		return this.processes;
	}

	public Process createProcess(int pid){
		timeNow+=(int)(-Math.floor(Math.log(1-Math.random())/lambda1));
		return new Process(pid, timeNow, (int)(-Math.floor(Math.log(1-Math.random())/lambda2)) );
	}

	public void addProcess(Process p){
		processes.add(p);
		Collections.sort(processes, Process.startTimeComparator());
	}

	public void showProcesses(){
		System.out.println("PID\tEntryTime\tTimeToCompletion");
		for(Process p: processes) System.out.println(p.getPid()+"\t"+p.getStartTime()+"\t\t"+p.getRunTime());
	}

	public void createProcesses(int n){
		clearProcesses();
		for(int i=1;i<=n;i++) this.processes.add(createProcess(i));
	}

	public void clearProcesses(){
		processes.clear();
	}

	public Map<Integer, List<CPU>> scheduleFCFS(){
		Map<Integer, List<CPU>> schedule = new HashMap<>();
		int time=0;
		for(Process p:processes){
			time=Math.max(time, p.getStartTime());
			if(schedule.get(p.getPid())==null) schedule.put(p.getPid(), new LinkedList<CPU>());
			schedule.get(p.getPid()).add(new CPU(time,time+=p.getRunTime()));	
		}
		return schedule;
	}

	public Map<Integer, List<CPU>> scheduleSJF(){
		Map<Integer, List<CPU>> schedule = new HashMap<>();
		PriorityQueue<Process> heap = new PriorityQueue<>();
		int i=0, time=0;
		while(i<processes.size() || heap.size()>0){
			if(heap.size()==0) time = Math.max(time, processes.get(i).getStartTime());
			for(;i<processes.size() && processes.get(i).getStartTime()<=time;i++) heap.add(processes.get(i));			
			Process p = heap.poll();
			time = Math.max(time, p.getStartTime());
			if(schedule.get(p.getPid())==null) schedule.put(p.getPid(), new LinkedList<CPU>());
			schedule.get(p.getPid()).add(new CPU(time, time+=p.getRunTime()));
		}
		return schedule;
	}

	public Map<Integer, List<CPU>> scheduleRR(int slice){
		Map<Integer, List<CPU>> schedule = new HashMap<>();
		Queue<Process> queue = new LinkedList<>();
		int i=0, time = 0;
		while(queue.size()>0 || i<processes.size()){
			if(queue.size()==0) time = Math.max(time, processes.get(i).getStartTime());
			for(;i<processes.size() && processes.get(i).getStartTime()<=time+slice;i++) queue.add(processes.get(i));
			Process p = queue.poll();
			time = Math.max(time, p.getStartTime());
			if(schedule.get(p.getPid())==null) schedule.put(p.getPid(), new LinkedList<CPU>());
			schedule.get(p.getPid()).add(new CPU(time, time+=Math.min(slice, p.getRunTime())));
			if(p.getRunTime()>slice) queue.add(new Process(p.getPid(), p.getStartTime(), p.getRunTime()-slice));
		}
		return schedule;
	}

	public Map<Integer, List<CPU>> scheduleSTC(){
		Map<Integer, List<CPU>> schedule = new HashMap<>();
		PriorityQueue<Process> heap = new PriorityQueue<>();
		int i=0, time=0;
		while(i<processes.size() || heap.size()>0){
			if(heap.size()==0) time = Math.max(time, processes.get(i).getStartTime());
			for(;i<processes.size() && processes.get(i).getStartTime()<=time;i++) heap.add(processes.get(i));			
			Process p = heap.poll();
			for(;i<processes.size() && 	processes.get(i).getStartTime()<time+p.getRunTime() && 	processes.get(i).getRunTime()+processes.get(i).getStartTime()>=time+p.getRunTime(); i++) 
				heap.add(processes.get(i));
			if(schedule.get(p.getPid())==null) schedule.put(p.getPid(), new LinkedList<CPU>());
			if(i<processes.size() && processes.get(i).getStartTime()<time+p.getRunTime()){
				Process temp = processes.get(i++);
				schedule.get(p.getPid()).add(new CPU(time, temp.getStartTime()));
				heap.add(temp);
				heap.add(new Process(p.getPid(), temp.getStartTime(), time+p.getRunTime()-temp.getStartTime()));
				time = temp.getStartTime();
			}
			else{
				time = Math.max(time, p.getStartTime());
				schedule.get(p.getPid()).add(new CPU(time, time+=p.getRunTime()));
			}
		}
		return schedule;
	}

	public Map<Integer, List<CPU>> scheduleMLFQ(int slice1, int slice2, int slice3, int boost){
		Map<Integer, List<CPU>> schedule = new HashMap<>();
		Map<Integer, Integer> sl2 = new HashMap<>();
		Map<Integer, Integer> sl3 = new HashMap<>();
		List<Process> q1=new LinkedList<>(), q2=new LinkedList<>(), q3=new LinkedList<>();
		int i=0, time = 0;
		while(i<processes.size() || q1.size()>0 || q2.size()>0 || q3.size()>0){
			if(q1.size()==0 && q2.size()==0 && q3.size()==0){
				time = Math.max(time, processes.get(i).getStartTime());
				q1.add(processes.get(i++));
			}
			if(time>=boost){
				q1.addAll(q2); q2.clear();
				q1.addAll(q3); q3.clear();
				boost+=boost;
			}
			while(q1.size()>0){
				Process p = q1.remove(0);
				time = Math.max(time, p.getStartTime());
				for(;i<processes.size() && processes.get(i).getStartTime()<=time+slice1;i++) q1.add(processes.get(i));
				if(schedule.get(p.getPid())==null) schedule.put(p.getPid(), new LinkedList<CPU>());
				if(time+Math.min(slice1, p.getRunTime())>boost){
					schedule.get(p.getPid()).add(new CPU(time, boost));
					q1.add(0, new Process(p.getPid(), boost, time+p.getRunTime()-boost));
					q1.addAll(q2); q2.clear();
					q1.addAll(q3); q3.clear();
					time=boost; boost+=boost;
				}
				else{
					schedule.get(p.getPid()).add(new CPU(time, time+=Math.min(slice1, p.getRunTime())));
					if(p.getRunTime()>slice1) q2.add(new Process(p.getPid(), time, p.getRunTime()-slice1));
				}
			}
			while(q2.size()>0 && q1.size()==0){
				Process p = q2.remove(0);
				if(!sl2.containsKey(p.getPid())) sl2.put(p.getPid(), slice2);
				time = Math.max(time, p.getStartTime());
				if(i<processes.size() && processes.get(i).getStartTime()<time+Math.min(p.getRunTime(), sl2.get(p.getPid())))
					q1.add(processes.get(i++));

				if(q1.size()==0){
					if(time+Math.min(sl2.get(p.getPid()), p.getRunTime())>boost){
						schedule.get(p.getPid()).add(new CPU(time, boost));
						q2.add(0, new Process(p.getPid(), boost, time+p.getRunTime()-boost));
						q1.addAll(q2); q2.clear();
						q1.addAll(q3); q3.clear();
						sl2.clear();
						time=boost; boost+=boost;
					}
					else{
						schedule.get(p.getPid()).add(new CPU(time, time+=Math.min(sl2.get(p.getPid()), p.getRunTime())));
						if(p.getRunTime()>sl2.get(p.getPid())) q3.add(new Process(p.getPid(), time, p.getRunTime()-sl2.get(p.getPid())));
						sl2.remove(p.getPid());
					}
				}
				else{
					Process t = q1.get(0);
					if(boost<t.getStartTime()){
						schedule.get(p.getPid()).add(new CPU(time, boost));
						q2.add(0, new Process(p.getPid(), boost, time+p.getRunTime()-boost));
						q2.addAll(q3); q3.clear();
						q2.addAll(q1); q1.clear();
						q1.addAll(q2); q2.clear();
						sl2.clear();
						time=boost; boost+=boost;
					}
					else{
						schedule.get(p.getPid()).add(new CPU(time, t.getStartTime()));
						q2.add(0, new Process(p.getPid(), t.getStartTime(), time+p.getRunTime()-t.getStartTime()));
						sl2.put(p.getPid(), sl2.get(p.getPid())-t.getStartTime()+time);
					}
				}
			}

			while(q3.size()>0 && q1.size()==0 && q2.size()==0){	
				Process p = q3.remove(0);
				if(!sl3.containsKey(p.getPid())) sl3.put(p.getPid(), slice3);
				time = Math.max(time, p.getStartTime());
				if(i<processes.size() && processes.get(i).getStartTime()<=time+Math.min(sl3.get(p.getPid()), p.getRunTime())) 
					q1.add(processes.get(i++));

				if(q1.size()==0){
					if(time+Math.min(sl3.get(p.getPid()), p.getRunTime())>boost){
						schedule.get(p.getPid()).add(new CPU(time, boost));
						q3.add(0, new Process(p.getPid(), boost, time+p.getRunTime()-boost));
						q1.addAll(q2); q2.clear();
						q1.addAll(q3); q3.clear();
						sl3.clear();
						time=boost; boost+=boost;
					}
					else{
						schedule.get(p.getPid()).add(new CPU(time, time+=Math.min(sl3.get(p.getPid()), p.getRunTime())));
						if(p.getRunTime()>sl3.get(p.getPid())) q3.add(new Process(p.getPid(), time, p.getRunTime()-sl3.get(p.getPid())));
						sl3.remove(p.getPid());
					}
				}
				else{
					Process t = q1.get(0);
					if(boost<t.getStartTime()){
						schedule.get(p.getPid()).add(new CPU(time, boost));
						q3.add(0, new Process(p.getPid(), boost, time+p.getRunTime()-boost));
						q2.addAll(q3); q3.clear();
						q2.addAll(q1); q1.clear();
						q1.addAll(q2); q2.clear();
						sl3.clear();
						time=boost; boost+=boost;
					}
					else{
						schedule.get(p.getPid()).add(new CPU(time, t.getStartTime()));
						q3.add(0, new Process(p.getPid(), t.getStartTime(), time+p.getRunTime()-t.getStartTime()));
						sl3.put(p.getPid(), sl3.get(p.getPid())+time-t.getStartTime());
					}
				}
			}
		}
		return schedule;
	}
}