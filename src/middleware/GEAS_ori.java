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
* implementation of the GEAS-ori scheduling strategy
*/
public class GEAS_ori extends Checker {

	public static int checktime = 0;
	
	
	
	public void doCheck() throws Exception {
        ContextChange change = new ContextChange();
        Demo demo = new Demo(in);
        int line = -1;
    	while(demo.hasNextChange()) {//handle changes by loop
            change = demo.nextChange();
            line++;
        	//System.out.println("Context:"+change.toString());

            for(int i = 0;i < rules.size();i++) {//handle rules by loop
            	Rule rule = rules.get(i);
            	//if(!(rule.getId().equalsIgnoreCase("rule_in_DF")))
            	//	continue;  
            	//if(!(rule.getId().equalsIgnoreCase("rule_in_AC")))
            	//	continue;         	
            	       	
            	
            	LinkedList<ContextChange> listBuffer = rule.getBuffer();
                if(rule.affect(change)) {
             
                	long start = Calendar.getInstance().getTimeInMillis();
    	    		if(rule.match_chgBuffer(change)) {//compose s-conditions
    	    			checkNum++;
        	    		rule.record(rule.getBuffer().size());
    	    			long bs = Calendar.getInstance().getTimeInMillis();
    	    			rule.setBuffer_(BatchChecker.filter(rule.getBuffer()));
    	    			int linkbefore = nLinks;
    	    			if(technique.matches("ECC")) {
    	    				nLinks += rule.Ecc(out);
    	    			}
    	    			if(technique.matches("ECCNew")) {
    	    				nLinks += rule.EccNew(out);
    	    			}
    	    			if(technique.matches("PCC")) {
    	    				nLinks += rule.Pcc(out);
    	    			}
    	    			long sTime = Calendar.getInstance().getTimeInMillis() - bs;
    	    			rule.addTime(sTime);
    	    			pTime += sTime;
    	    			rule.clearBuffer();
    	    		}
    	    		rule.handleSelf(change);
    	    		rule.setBuffer(change);
    	    		long end = Calendar.getInstance().getTimeInMillis();
    	            allTime += end - start;
                }
                
            }
        }
    }

}
