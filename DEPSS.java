import java.util.*;
import java.lang.*;
import java.io.*;

public class DEPSS{
	public static void main(String[] args) {
		Scanner cin = new Scanner(System.in);
		System.out.println("Enter Two lambda1 (lambda1, lambda2): ");
		Simulator s = new Simulator(cin.nextFloat() ,cin.nextFloat());
		System.out.println("Enter number of processes- ");
		s.createProcesses(cin.nextInt());
		s.showProcesses();
		List<Map<Integer, List<CPU>>> Simulators = new LinkedList<>();
		String names[];
		System.out.println("Choose scheduler Below :");
		System.out.println("1. FCFS");
		System.out.println("2. SJF");
		System.out.println("3. RR");
		System.out.println("4. STC");
		System.out.println("5. MLFQ");
		System.out.println("6. All schedulers");
		switch(cin.nextInt()){
			case 1:
				names= new String[]{"FCFS"};
				Simulators.add(s.scheduleFCFS());
				break;
			case 2:
				names= new String[]{"SJF"};
				Simulators.add(s.scheduleSJF());
				break;
			case 3:
				names= new String[]{"RR"};
				System.out.println("Enter RR slice time");
				Simulators.add(s.scheduleRR(cin.nextInt()));
				break;
			case 4:
				names= new String[]{"STC"};
				Simulators.add(s.scheduleSTC());
				break;
			case 5:
				names= new String[]{"MLFQ"};
				System.out.println("Enter Q1, Q2, Q3 slice time and boost time");
				Simulators.add(s.scheduleMLFQ(cin.nextInt(), cin.nextInt(), cin.nextInt(), cin.nextInt()));
				break;
			case 6:
				names = new String[]{"FCFS", "SJF", "RR", "STC", "MLFQ"};
				Simulators.add(s.scheduleFCFS());
				Simulators.add(s.scheduleSJF());
				System.out.println("Enter RR TS");
				Simulators.add(s.scheduleRR(cin.nextInt()));
				Simulators.add(s.scheduleSTC());
				System.out.println("Enter Q1, Q2, Q3 & BT");
				Simulators.add(s.scheduleMLFQ(cin.nextInt(), cin.nextInt(), cin.nextInt(), cin.nextInt()));
				break;
			default:
				return;
		}
		Analyzer.plot(Simulators, s.getProcesses(), names);
	}
}



