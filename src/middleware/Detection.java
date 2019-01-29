package middleware;

import java.io.IOException;
import java.util.ArrayList;

import rule.Rule;
import context.ContextChange;
import formula.Link;
import formula.RuntimeNode;
/**
*
* @author why
* implementation of ECC and PCC
*/
public class Detection {
	
    public static ArrayList<Link> differenceSet(ArrayList<Link> link1,ArrayList<Link> link2) {//link1-link2
        ArrayList<Link> differenceLink = new ArrayList<Link>(link1);
        

        for(int i = 0;i < link1.size();i++) {
            for(int j = 0;j < link2.size();j++) {     	
                if(link1.get(i).equals(link2.get(j))) {
                    differenceLink.remove(link1.get(i));
                    break;
                }
            }
        }
        return differenceLink;
    }
    
    public static ArrayList<Link> detectPccM(Rule rule,SameContextChange group) {
    	//ArrayList<Link> differenceLink = new ArrayList<Link>();
    	ArrayList<Link> link = new ArrayList<Link>();
        RuntimeNode node = new RuntimeNode();
        boolean result;
            result = rule.getFormula().evaluatePcc(rule.getContexts(),node,group);//evaluate得到boolean值
            rule.setValue(result);
           
            link = new ArrayList<Link>(rule.getFormula().linkGenerationPcc(rule.getContexts(),node,group));
        return link;
    }
    
    public static ArrayList<Link> detectEcc(Rule rule) throws IOException {
    	ArrayList<Link> link = new ArrayList<Link>();
    	//ArrayList<Link> differenceLink = new ArrayList<Link>();
        RuntimeNode node = new RuntimeNode();
        boolean result;
		
            result = rule.getFormula().evaluateEcc(rule.getContexts(),node);//evaluate 得到boolean值
            rule.setValue(result);
        	
            link = new ArrayList<Link>(rule.getFormula().linkGenerationEcc(rule.getContexts(),node));
        return link;
    }
    
    
    //ECC/PCC
    public static ArrayList<Link> detectPccS(Rule rule,ContextChange change) throws IOException {
        //ArrayList<Link> differenceLink = new ArrayList<Link>();
        ArrayList<Link> link = new ArrayList<Link>();
        RuntimeNode node = new RuntimeNode();
        boolean result;
        	result = rule.getFormula().evaluatePcc(rule.getContexts(),node,change);//evaluate得到boolean值
            rule.setValue(result);
//           
            link = new ArrayList<Link>(rule.getFormula().linkGenerationPcc(rule.getContexts(),node,change));

        return link;
    }
	
	
	
}
