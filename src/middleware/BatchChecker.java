package middleware;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;

import rule.Rule;
import context.ContextChange;
import dataLoader.Demo;
/**
*
* @author why
* implementation of BAT scheduling strategy
*/
public class BatchChecker extends Checker {

	private int N;

	public void setN(int n) {
		N = n;
	}
	
	public void setOut() throws IOException {
    	outFile = id + "_" + technique + "_" + strategy + "_" + N;
    	File file = new File("data/out/" + outFile + ".txt");
        // if file doesnt exists, then create it
        if(!file.getParentFile().exists()) {
        	file.getParentFile().mkdirs();
        }
    	if (!file.exists()) {
        	file.createNewFile();
        }
		out = new FileOutputStream(file);
	}
    
	public static LinkedList<ContextChange> filter(LinkedList<ContextChange> changes) {
		LinkedList<ContextChange> result = new LinkedList<ContextChange>(changes);
		for(int i = 0;i < changes.size();i++) {
			ContextChange change = changes.get(i);
			for(int j = i + 1;j < changes.size();j++) {
				ContextChange change_ = changes.get(j);
				if(change.isOpposite(change_)) {
					result.remove(changes.get(i));
					result.remove(changes.get(j));
				}
			}	
		}
		return result;
	}
	
    public void doCheck() throws Exception {
        ContextChange change = new ContextChange();
        Demo demo = new Demo(in);
        
    	while(demo.hasNextChange()) {//handling all changes
            change = demo.nextChange();
            
            
            for(int i = 0;i < rules.size();i++) {//handling all constraints
            	Rule rule = rules.get(i);
                if(rule.affect(change)) {
                	
                	long start = Calendar.getInstance().getTimeInMillis();
                	rule.setBuffer(change);
                	if(rule.getBuffer().size() >= N) {
                		checkNum++;
        	    		rule.record(rule.getBuffer().size());
            			long bs = Calendar.getInstance().getTimeInMillis();
            			rule.setBuffer_(filter(rule.getBuffer()));
            			if(technique.matches("ECC")) {
            				nLinks += rule.Ecc(out);
            			}
            			if(technique.matches("PCC")) {
            				nLinks += rule.Pcc(out);
            			}
            			long sTime = Calendar.getInstance().getTimeInMillis() - bs;
                        rule.addTime(sTime);
						pTime += sTime;
            	        rule.clearBuffer();
            		}
                	long end = Calendar.getInstance().getTimeInMillis();
                    allTime += end - start;
                }
            }
            
        }
    } 
}
