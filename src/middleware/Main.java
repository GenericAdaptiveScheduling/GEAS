package middleware;

import java.util.ArrayList;

import rule.Rule;
import context.Context;
import dataLoader.Configuration;
import dataLoader.ConstrainLoader;
import dataLoader.ContextLoader;

public class Main {
	
	public static ArrayList<Rule> initialRules() {
		ArrayList<Rule> rules = new ArrayList<Rule>();

        //System.out.println("----" + rules.size());
        
        //reading constraint for rules
        ConstrainLoader loader = new ConstrainLoader();
        rules = loader.parserXml("src/config/consistency_rules-online.xml");

        //System.out.println("===" + rules.size());
        
        for(int i = 0;i < rules.size();i++) {
        	//obtain contexts
            ArrayList<Context> contextsList = new ArrayList<Context>
            (ContextLoader.parserXml("src/config/context_3.xml"));
            
        	if(Configuration.getConfigStr("optimizingStrategy").matches("ON")) {
                String temp = Configuration.getConfigStr("goalLink" + (i+1));
                rules.get(i).getFormula().setGoal(temp);
            }
        	int contextsNum = contextsList.size();
        	for(int k = 0;k < contextsNum;k++) {
        		rules.get(i).setContext(contextsList.get(k).getContextname(),contextsList.get(k));
        	}
        }
        return rules;
	}
	
	public static Checker getChecker(String strategy) throws Exception {
    	if(strategy.matches("Imd")) {
    		return new OldChecker();
    	} else if(strategy.matches("Bat")) {
    		return new BatchChecker();
    	} else if(strategy.matches("GEAS_ori")||strategy.matches("GEAS")) {
    		return new NewBatch();
    	} else if(strategy.matches("GEAS_opt")){
    		return new GEAS_opt();
    	} else if(strategy.matches("GEAS_cancel")){
    		return new GEAS_cancel();
    	}
    	return null;
	}
	
	public static void single(String strategy,String technique,String id,int N) throws Exception {
		ArrayList<Rule> rules = initialRules();

        Checker checker = getChecker(strategy);
        checker.initial(rules,strategy,technique,id);
        if(strategy.matches("Bat")) {
        	((BatchChecker)checker).setN(N);
        }
        checker.setOut();

        checker.doCheck();

        checker.stop();
	}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		Configuration c = new Configuration();
        c.init("/config/System.properties");

        //single("NewGEAS4","PCC","08",0);  
         
        for(int i =0;i <= 23;i++) {
        	String id = "";
        	if(i < 10)
        		id = "0" + i;
        	else 
        		id = String.valueOf(i);
        	//single("GEAS_ori","ECC",id,0);
        	
        	//single("GEAS_ori","ECC",id,0);
        	single("GEAS_opt","ECC",id,0);
        	single("GEAS_opt","PCC",id,0);
        	single("GEAS_ori","ECC",id,0);
        	single("GEAS_ori","PCC",id,0);
        	//single("GEAS_ori","PCC",id,0);
        	//single("GEAS_opt","PCC",id,0);

        	//single("Imd","PCC",id,0);
        }
	}

}