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
* implementation of GEAS-ori scheduling strategy
*/
public class GEAS_ori extends Checker {

	public static int checktime = 0;
	
	public void setOut() throws IOException {
    	outFile = id + "_" + technique + "_" + strategy;
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
	
	public void doCheck() throws Exception {
        ContextChange change = new ContextChange();
        Demo demo = new Demo(in);
        int line = -1;
    	while(demo.hasNextChange()) {//循环处理changes
            change = demo.nextChange();
            line++;
        	//System.out.println("Context:"+change.toString());

            for(int i = 0;i < rules.size();i++) {//循环检测所有的约束
            	Rule rule = rules.get(i);
            	//if(!rule.getId().startsWith("cst_loc_"))
            	//	continue;
            	if(!(rule.getId().equalsIgnoreCase("rule_01")))
            		continue;         	
            	
            	LinkedList<ContextChange> listBuffer = rule.getBuffer();
                if(rule.affect(change)) {
                	//out.write(("ruleID:"+rule.getId()+"\n"+"Buffer:"+listBuffer.toString()+"\n").getBytes());
                	//System.out.println("Rule:"+rule.getId()+" Context:"+change.toString());
                	//System.out.println("Buffer:"+listBuffer.toString());
                	//out.write(("Rule:"+rule.getId()+" Context:"+change.toString()+"\n").getBytes());
                	long start = Calendar.getInstance().getTimeInMillis();
    	    		if(rule.match_chgBuffer(change)) {//与之前的changes组成unstable pattern
    	    			checkNum++;
        	    		rule.record(rule.getBuffer().size());
    	    			long bs = Calendar.getInstance().getTimeInMillis();
    	    			int linkbefore = nLinks;
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
    	    			//System.out.println("Check, checked incs: "+(nLinks) +" - " +(linkbefore) + " = "+ (nLinks-linkbefore));
    	    			//System.out.println("Line: "+line);
    	    			
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
