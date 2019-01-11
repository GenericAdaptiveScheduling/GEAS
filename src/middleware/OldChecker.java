package middleware;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import rule.Rule;
import context.ContextChange;
import dataLoader.Demo;
/**
*
* @author why
* implementation of IMD scheduling strategy
*/
public class OldChecker extends Checker{
	
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
        //Demo demo = new Demo("data/new2 data/17.txt");
        //String strategy = Configuration.getConfigStr("resolutionStrategy");
        //long bs = Calendar.getInstance().getTimeInMillis();
        Demo demo = new Demo(in);
        int line = -1;
    	while(demo.hasNextChange()) {//循环处理changes
            change = demo.nextChange();
            
            line++;
            for(int i = 0;i < rules.size();i++) {//循环检测所有的约束
            	Rule rule = rules.get(i);
            	//if(rule.getId().startsWith("rule_equal_GI"))
            	//	continue;
            	//System.out.println("Line: "+change);
            	if(!(rule.getId().equalsIgnoreCase("rule_in_AC")))
            		continue;         	
            	if(rule.affect(change)) {
            		out.write(("Change: "+change.toString()+"\n").getBytes());
                	//System.out.println("rule: "+rule.getId());
                	long start = Calendar.getInstance().getTimeInMillis();
                	checkNum++;
                    rule.setBuffer(change);
					rule.record(rule.getBuffer().size());
                	long bs = Calendar.getInstance().getTimeInMillis();
                	int linkbefore = nLinks;
	    			if(technique.matches("ECCNew")) {
	    				nLinks += rule.EccNew(out);
	    			}
	    			if(technique.matches("ECC")) {
	    				nLinks += rule.Ecc(out);
	    			}
	    			if(technique.matches("PCC")) {
	    				nLinks += rule.Pcc(out);
	    			}
	    			//if((nLinks-linkbefore)>0){
	    				//System.out.println("Line: "+line);
	    			
	    			//}
	    			long sTime = Calendar.getInstance().getTimeInMillis() - bs;
	                rule.addTime(sTime);
					pTime += sTime;
	    			rule.clearBuffer();
	    			long end = Calendar.getInstance().getTimeInMillis();
	    	        allTime += end - start;
                }
            }
           
        }
    }
}
