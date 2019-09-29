import java.util.*;
import java.lang.*;
import java.io.*;

import java.text.SimpleDateFormat;
import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;


public class Analyzer extends JFrame {

    public Analyzer(List<Map<Integer, List<CPU>>> map, List<Process> processes, String[] type) {
		super("Gantt Chart");
		JFreeChart chart = ChartFactory.createGanttChart("Schedulers", "Processes", "Time", createDataset(map, processes, type));
		CategoryPlot plot = chart.getCategoryPlot();
    	DateAxis axis = (DateAxis) plot.getRangeAxis();
		axis.setDateFormatOverride(new SimpleDateFormat("S"));
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(1000, 500));
		setContentPane(chartPanel);
    }

    private IntervalCategoryDataset createDataset(List<Map<Integer, List<CPU>>> map, List<Process> processes, String[] names){
        TaskSeriesCollection dataset = new TaskSeriesCollection();
        TaskSeries entry = new TaskSeries("Entry");
        int[][] striking = new int[processes.size()][4];int ct=0;
        for(Process p : processes){
        	striking[ct][0]=p.getPid();
        	striking[ct++][1]=p.getStartTime();
        	Task t = new Task("P "+p.getPid(), new SimpleTimePeriod(p.getStartTime(), p.getStartTime()+p.getRunTime()));
        	entry.add(t);
        }
        dataset.add(entry);
        for(int i=0;i<names.length;i++){
        	TaskSeries unavailable = new TaskSeries(names[i]);
        	ct=0;
        	for(Map.Entry<Integer, List<CPU>> it : map.get(i).entrySet()){
        		List<CPU> list = it.getValue();
        		Collections.sort(list);
        		striking[ct][2]=list.get(0).startTime;
        		striking[ct++][3]=list.get(list.size()-1).endTime;
        		Task t = new Task("P "+it.getKey(), 
        			new SimpleTimePeriod(list.get(0).startTime, list.get(list.size()-1).endTime));
        		for(CPU c : list)	t.addSubtask(new Task("", new SimpleTimePeriod(c.startTime, c.endTime)));
        		unavailable.add(t);
        	}
        	strike(striking, names[i]);
        	dataset.add(unavailable);
    	}
        return dataset;
    }

    private void strike(int[][] striking, String name){
    	System.out.println("---------------"+name+"-------------------");
		System.out.println("PID\tLatency\tTurnaround Time");
		int lt=0, tt=0;
		for(int[] it:striking){
			lt+=(it[2]-it[1]);
			tt+=(it[3]-it[1]);
			System.out.println(it[0]+"\t"+(it[2]-it[1])+"\t"+(it[3]-it[1]));
		}
		System.out.println("Avg\t"+(lt/striking.length)+"\t"+(tt/striking.length));
    }

    public static void plot(List<Map<Integer, List<CPU>>> map, List<Process> processes, String[] type) {
		Analyzer chart = new Analyzer(map, processes, type);
		chart.pack();
		chart.setVisible(true);
    }
}