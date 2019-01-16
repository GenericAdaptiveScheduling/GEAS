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
        rules = loader.parserXml(Configuration.constraintRuleString);

        //System.out.println("===" + rules.size());
        
        for(int i = 0;i < rules.size();i++) {
        	//obtain contexts
            ArrayList<Context> contextsList = new ArrayList<Context>
            (ContextLoader.parserXml(Configuration.contextString));
            
        	/*if(Configuration.getConfigStr("optimizingStrategy").matches("ON")) {
                String temp = Configuration.getConfigStr("goalLink" + (i+1));
                rules.get(i).getFormula().setGoal(temp);
            }*/
        	int contextsNum = contextsList.size();
        	for(int k = 0;k < contextsNum;k++) {
        		rules.get(i).setContext(contextsList.get(k).getContextname(),contextsList.get(k));
        	}
        }
        return rules;
	}
	
	public static Checker getChecker(String strategy) throws Exception {
    	if(strategy.matches("Imd")) {
    		return new ImdChecker();
    	} else if(strategy.matches("Bat")) {
    		return new BatchChecker();
    	} else if(strategy.matches("GEAS_ori")||strategy.matches("GEAS")) {
    		return new GEAS_ori();
    	} else if(strategy.matches("GEAS_opt")){
    		return new GEAS_opt();
    	} 
    	return null;
	}
	
	public static void single(String strategy,String technique,String id,int N) throws Exception {
		ArrayList<Rule> rules = initialRules();

		System.out.println("Running the " + strategy+"-"+technique+" combination for "+id +".txt");
		
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
		//Run all built-in scheduling-strategy combinations for demo.txt data

        //single("NewGEAS4","PCC","08",0);  
         
        String data = "demo";
        //single("GEAS_ori","ECC",id,0);
        single("Imd","PCC",data,0);
        single("Bat","PCC",data,4);//set batch size to be 4
        single("GEAS_ori","PCC",data,0);
        single("GEAS_opt","PCC",data,0);
        
        single("Imd","ECC",data,0);
        single("Bat","ECC",data,4);//set batch size to be 4
        single("GEAS_ori","ECC",data,0);
        single("GEAS_opt","ECC",data,0);

	}

}