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
        
        //System.out.println("Link1:"+link1.size());
        //System.out.println("differenceLink:"+differenceLink.toString());
        //System.out.println("Link2:"+link2.toString());
        for(int i = 0;i < link1.size();i++) {
        	//System.out.println("Link1 now:"+i+"---"+link1.get(i));
            for(int j = 0;j < link2.size();j++) {
            	//System.out.println("Link1-Link2 now:"+link1.get(i).toString()+"---"+link2.get(j).toString());
                if(link1.get(i).equals(link2.get(j))) {
                	//System.out.println("RM:"+link1.get(i).toString());
                    differenceLink.remove(link1.get(i));
                    //System.out.println("DlINEK:"+differenceLink.toString());
                    break;
                }
            }
        }
        //System.out.println("RM end:");
        return differenceLink;
    }
    
    public static ArrayList<Link> detectPccM(Rule rule,SameContextChange group) {
    	//ArrayList<Link> differenceLink = new ArrayList<Link>();
    	ArrayList<Link> link = new ArrayList<Link>();
        RuntimeNode node = new RuntimeNode();
        boolean result;
        //if(Configuration.getConfigStr("checkingStragegy").matches("PCC")) {
            //rule.setFormula(createTreePcc(rule.getFormula(),change));
            result = rule.getFormula().evaluatePcc(rule.getContexts(),node,group);//evaluate得到boolean值
            rule.setValue(result);
            //ArrayList<Link> lastLink = rule.getLink();
            //ArrayList<Link> link = new ArrayList<Link>(rule.getFormula().linkGenerationPcc(node,group));             
            link = new ArrayList<Link>(rule.getFormula().linkGenerationPcc(rule.getContexts(),node,group));             
            //differenceLink = differenceSet(link,lastLink);
            //rule.setLink(link);   
        //}
        return link;
    }
    
    public static ArrayList<Link> detectEcc(Rule rule) throws IOException {
    	ArrayList<Link> link = new ArrayList<Link>();
    	//ArrayList<Link> differenceLink = new ArrayList<Link>();
        RuntimeNode node = new RuntimeNode();
        boolean result;
		//if(Configuration.getConfigStr("checkingStragegy").matches("ECC")) {
			//rule.setFormula(createTree(rule.getFormula()));
            result = rule.getFormula().evaluateEcc(rule.getContexts(),node);//evaluate 得到boolean值
            rule.setValue(result);
        	//ArrayList<Link> link = new ArrayList<Link>(rule.getFormula().linkGenerationEcc(node));
            //ArrayList<Link> lastLink = rule.getLink();
            //differenceLink = differenceSet(link,lastLink);
            link = new ArrayList<Link>(rule.getFormula().linkGenerationEcc(rule.getContexts(),node));
            //rule.setLink(link);
            //System.out.println(result);
//            if(!result) {   
//                for(int x = 0;x < differenceLink.size();x++)
//                	MiddleWare.out.write((rule.getId() + differenceLink.get(x).toString() + "\n").getBytes());
//
//            }
        //}
        return link;
    }
    
    
    //ECC/PCC检测
    public static ArrayList<Link> detectPccS(Rule rule,ContextChange change) throws IOException {
        //ArrayList<Link> differenceLink = new ArrayList<Link>();
        ArrayList<Link> link = new ArrayList<Link>();
        RuntimeNode node = new RuntimeNode();
        boolean result;
        	result = rule.getFormula().evaluatePcc(rule.getContexts(),node,change);//evaluate得到boolean值
            rule.setValue(result);
//            for (Map.Entry entry : contexts.entrySet()) {
//                String name = (String)entry.getKey();
//                Context context = (Context)entry.getValue();
//                System.out.println(name + ":");
//                for (Map.Entry entry2 : context.getElements().entrySet()) {
//                    System.out.print(" " + entry2.getKey());
//                }
//                System.out.println("");
//            }
            //System.out.println(result);
//            ArrayList<Link> lastLink = rule.getLink();
//            ArrayList<Link> link = new ArrayList<Link>(rule.getFormula().linkGenerationPcc(node,change));             
//            differenceLink = differenceSet(link,lastLink);
            link = new ArrayList<Link>(rule.getFormula().linkGenerationPcc(rule.getContexts(),node,change));
            //rule.setLink(link);
            
//            if(!result) {
//                /*System.out.println("lastLink");
//                for(int x = 0;x < lastLink.size();x++)
//                    System.out.println(rule.getId() + lastLink.get(x).toString());
//                System.out.println("link");
//                for(int x = 0;x < link.size();x++)
//                    System.out.println(rule.getId() + link.get(x).toString());
//                System.out.println("differenceLink");*/
//                for(int x = 0;x < differenceLink.size();x++)
//                	MiddleWare.out.write((rule.getId() + differenceLink.get(x).toString() + "\n").getBytes());                       
//            }
        return link;
    }
	
	
	
}
