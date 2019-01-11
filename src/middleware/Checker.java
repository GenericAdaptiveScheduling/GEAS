/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package middleware;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;

import rule.Rule;

/**
 *
 * @author why
 * Abstract Checker for all scheduling strategies
 */
public abstract class Checker {
    /**
     * @param args the command line arguments
     */
	
	protected ArrayList<Rule> rules = new ArrayList<Rule>();
    protected String technique;
    protected String strategy;
    protected String in;
	protected String outFile;
	protected FileOutputStream out = null;
	protected int checkNum;
	protected double pTime;
	protected double allTime;

	
	//protected  HashMap<Integer, Integer> cancelTable = new HashMap<Integer, Integer>();
	
	protected int nLinks;
	protected String id;
	
    public void initial(ArrayList<Rule> rules_,String strategy_,String technique_,String id_) throws Exception {
    	rules = rules_;
    	strategy = strategy_;
    	technique = technique_;
    	id = id_;
    	pTime = 0;
        checkNum = 0;
        nLinks = 0;
        in = "data/changes/" + id + ".txt";
    	
    }
    
    protected abstract void doCheck() throws Exception;
    
    protected abstract void setOut() throws Exception;
    
    public FileOutputStream getOut() {
    	return out;
    }
    
    public void stop() throws IOException {
    	double sum = 0;int num = 0;
    	for(int i = 0;i < rules.size();i++) {//handling all constraints
        	Rule rule = rules.get(i);
            if(rule.getBuffer().size() > 0) {
            	rule.record(rule.getBuffer().size());
            	rule.setBuffer_(BatchChecker.filter(rule.getBuffer()));
            	checkNum++;
            	long start = Calendar.getInstance().getTimeInMillis();
            	long bs = Calendar.getInstance().getTimeInMillis();
            	
            	if(technique.matches("non-incremental") || technique.matches("ECC")) {
    				nLinks += rule.Ecc(out);
    			}
    			if(technique.matches("PCC")) {
    				nLinks += rule.Pcc(out);
    			}
    			long sTime = Calendar.getInstance().getTimeInMillis() - bs;
                rule.addTime(sTime);
                pTime += sTime;
                long end = Calendar.getInstance().getTimeInMillis();
                allTime += end - start;
                rule.clearBuffer();
            }
    	}
    	for(int i = 0;i < rules.size();i++) {
        	Rule rule = rules.get(i);
            LinkedList<Integer> buffer = rule.getBufferSize();
            num += buffer.size();
            for(int n : buffer) {
            	sum += n;
            }
    	}
    	double average = sum / num;	
    	
    	//output necessary log information for measuring
    	System.out.println("processing time: " + (double)(pTime / 1000));
    	out.write(("processing time: " + pTime / 1000 + "\n").getBytes());
    	System.out.println("processing all time: " + (double)(allTime / 1000));
    	out.write(("processing all time: " + allTime / 1000 + "\n").getBytes());
    	System.out.println("inconsistency number: " + nLinks);
		out.write(("inconsistency number: " + nLinks + "\n").getBytes());
    	System.out.println("checkNum: " + checkNum);
		out.write(("checkNum: " + checkNum + "\n").getBytes());
		System.out.println("average k: " + average);
    	out.write(("average k: " + average + "\n").getBytes());
    	
    	//System.out.println(cancelTable.toString());
    	//out.write(cancelTable.toString().getBytes());
    }
}
