/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package rule;

import java.io.FileOutputStream;
import java.io.IOException;
import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import middleware.Detection;
import middleware.GEAS_ori;
import middleware.SameContextChange;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import context.Context;
import context.ContextChange;
import context.Element;
import formula.AndFormula;
import formula.BFunc;
import formula.ExistsFormula;
import formula.ForallFormula;
import formula.Formula;
import formula.ImpliesFormula;
import formula.Link;
import formula.OrFormula;
import formula.SubNode;
import formula.UnaryFormula;

/**
 *
 * @author why
 * rule class
 */
public class Rule {
    private String ruleId;
    private Formula formula;//FOL formula in constraint
    private LinkedList<Pattern> patterns = new LinkedList<Pattern>();
    //private HashMap<String,String> contextList = new HashMap<String, String>();//
    private ArrayList<Link> lastLinkSet = new ArrayList<Link>();
    private ArrayList<Link> linkSet = new ArrayList<Link>();
    private boolean booleanValue;
    private LinkedList<ContextChange> chgBuffer = new LinkedList<ContextChange>();
    private LinkedList<ContextChange> plusBuffer = new LinkedList<ContextChange>();
	private LinkedList<Integer> bufferSize = new LinkedList<Integer>();
	private HashMap<String,Context> contexts = new HashMap<String,Context>();
    private long detect;
    private long all_check_detect;
    private String renameAlready;
    private HashMap<String,String> goalMap = new HashMap<String,String>();
    
    private int flag;
    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(Rule.class.getName());
    
    public int getF(){
    	return flag;
    }
    public void setF(int temp){
    	flag = temp;
    }
    public Rule(String name) {
        booleanValue = true;
        ruleId = name;
        detect = 0;
        all_check_detect = 0;
        renameAlready = "";
        flag = 0;
    }
    public Rule(HashMap<String,Context> _contexts){
    	contexts = _contexts;
    }
    public void setContext(String key,Context context) {
    	contexts.put(key,context);
    }
   
    public void setRename(String name){
    	renameAlready = name;
    }
    public String getRename(){
    	return renameAlready;
    }
    public void deleteRename(){
    	renameAlready = "";
    }
    public HashMap<String,Context> getContexts() {
    	return contexts;
    }
    
    public void addTime(long temp) {
    	detect += temp;
    }
    
    public void addAllTime(long temp){
    	all_check_detect += temp;
    }
    
    public long getDetectTime() {
    	return detect;
    }
    
    public long getAllTime(){
    	return all_check_detect;
    }
    
    //three types of context changes
    public boolean change(ContextChange change) {
        String name = change.getContext();
        Context ct = contexts.get(name);
        Element sct = change.getElement();
        if(ct != null) {
        	//if(change.getState(ruleId) == -1)
        	//	return true;
            if(change.getOperate() == 1) {//add
                ct.addElement(change.getKey(),sct);
                return true;
            }
            else if(change.getOperate() == 2) {//delete
            	ct.deleteElement(change.getKey());
            	return true;
            }
            else if(change.getOperate() == 3) {//update
                if(ct.deleteElement(change.getKey())) {//delete successfully
                    ct.addElement(change.getKey(),sct);
                    return true;
                }
            }
        }
        return false;
    }
    
    public String getId() {
    	return ruleId;
    }
    
    public void setFormula(Formula formula) {
    	this.formula = formula;
    }
    
    public Formula getFormula() {
    	return formula;
    }
    
    public void setPatterns(LinkedList<Pattern> patterns) {
    	this.patterns = patterns;
    }
    public void setGoals(HashMap<String, String> goals){
    	this.goalMap = goals;
    }
    
    public LinkedList<Pattern> getPatterns() {
    	return patterns;
    }
    
    public ArrayList<Link> getLastLink() {
        return lastLinkSet;
    }
    
    public void setTempLink(ArrayList<Link> link) {
        linkSet = link;
    }
    
    public void setLink(ArrayList<Link> link) {
    	lastLinkSet = linkSet;
        linkSet = link;
    }
    
    public ArrayList<Link> getLink() {
        return linkSet;
    }
    
    public void setValue(boolean value) {
        booleanValue = value;
    }
    
    public boolean getValue() {
        return booleanValue;
    }
    
    public void setBuffer_(LinkedList<ContextChange> changes) {
    	chgBuffer.clear();
    	chgBuffer = changes;
    }
    
    public void setBuffer(ContextChange change) {
        chgBuffer.add(change);
    }
    
    public void clearBuffer() {
    	chgBuffer.clear();
    }
    public void setplusBuffer(ContextChange change) {
    	plusBuffer.add(change);
    }
    
    public void clearplusBuffer() {
    	plusBuffer.clear();
    }
    public LinkedList<ContextChange> getBuffer() {
        return chgBuffer;
    }
	
    public LinkedList<Integer> getBufferSize() {
        return bufferSize;
    }
    
    public void record(int size) {
		bufferSize.add(size);
	}
    
    public boolean match(ContextChange change,Pattern pattern,int flag) {
//    	System.out.println("***********");
//    	System.out.println(change.getOperate());
//    	System.out.println(change.getContext());
//    	System.out.println(pattern.getfType());
//    	System.out.println(pattern.getfName());
    	
		if(flag == 1) {//inc+/inc? matched
			int match = 0;
			if(change.getOperate() == 1 && pattern.getfType().matches("add")) {
				match = 1;
			}
			if(change.getOperate() == 2 && pattern.getfType().matches("del")) {
				match = 1;
			}
			if(change.getOperate() == 3 && pattern.getfType().matches("upd")) {
				match = 1;
			}
			if(match == 1) {
				if(change.getContext().matches(pattern.getfName()))
					return true;
			}
			return false;
		}
		else if(flag == 2) {//inc-/inc? matched
			int match = 0;
			if(change.getOperate() == 1 && pattern.getsType().matches("add")) {
				match = 1;
			}
			if(change.getOperate() == 2 && pattern.getsType().matches("del")) {
				match = 1;
			}
			if(change.getOperate() == 3 && pattern.getsType().matches("upd")) {
				match = 1;
			}
			if(match == 1) {
				if(change.getContext().matches(pattern.getsName()))
					return true;
			}
			return false;
		}
		return false;
	}
    
    public boolean match_chgBuffer(ContextChange change) {
		int size = chgBuffer.size();
		//System.out.println("size:" + size);
		boolean match = false;
		for(int i = 0;i < size;i++) {
			ContextChange chg = chgBuffer.get(i);
			if(chg.getConsider(getId())== -1 || chg.getState(getId()) == 0){// || chg.getState(getId()) == -1) {//never s-condition
				continue;
			}
			else {//s-condition or not
				//System.out.println(i + ":" + chg.toString());
				LinkedList<Pattern> patterns = chg.getUnsafePatterns();
				for(int j = 0;j < patterns.size();j++) {
					Pattern pattern = patterns.get(j);
					if(pattern.getRuleId() == getId()) {
						match = match(change,pattern,2);
						if(match == true) {
							//System.out.println("pattern:" + chg.getState(getId())+""+chg.toString()+"===="+ change.getState(getId())+""+change.toString());
							break;
						}
					}
				}
			}
			if(match == true)
				return match;
		}
		return match;
	}
    public boolean match_plusBuffer(ContextChange change) {
		int size = plusBuffer.size();
		//System.out.println("size:" + size);
		boolean match = false;
		for(int i = 0;i < size;i++) {
			ContextChange chg = plusBuffer.get(i);
			if(chg.getState(getId()) == 0){// || chg.getState(getId()) == -1) {//never s-condition
				continue;
			}
			else {//s-condition or not
				//System.out.println(i + ":" + chg.toString());
				LinkedList<Pattern> patterns = chg.getUnsafePatterns();
				for(int j = 0;j < patterns.size();j++) {
					Pattern pattern = patterns.get(j);
					if(pattern.getRuleId() == getId()) {
						match = match(change,pattern,2);
						if(match == true) {
							//System.out.println("pattern:" + chg.getState(getId())+""+chg.toString()+"===="+ change.getState(getId())+""+change.toString());
							break;
						}
					}
				}
			}
			if(match == true)
				return match;
		}
		return match;
	}
	public void handleSelf(ContextChange change) {
		change.setState(getId(),0);
		if(!affect(change)) {
			return;
		}
		//System.out.println("pattern size:" + patterns.size());
		for(int j = 0;j < patterns.size();j++) {
			//System.out.println(patterns.get(j).toString());
            if(match(change,patterns.get(j),1)) {
            	change.addUnsafePattern(patterns.get(j));
            	change.setState(getId(),1);
            }
        }
	}
    
    public boolean affect(LinkedList<ContextChange> changes) {
    	for(int i = 0;i < changes.size();i++) {
    		if(formula.affect(changes.get(i)))
    			return true;
    	}
    	return false;
    }
    
    public boolean affect(String context) {
		return formula.affect(context);
    }
    
    public boolean affect(ContextChange change) {
        return formula.affect(change);
    }
    
    public double hazardPairProb(ContextChange change,ContextChange laterChange) {
        
        return 0.9;
    }
    
    public boolean isHazardPair(ContextChange change,ContextChange laterChange) {
        
        return false;
    }
  	
  	public int Ecc(FileOutputStream out) throws IOException {
  		if(chgBuffer.size() == 0) {
  			;//System.err.println("The change queue is empty!");
  		}
  		else {
  			for(int i = 0;i < chgBuffer.size();i++) {
  	  			if(!change(chgBuffer.get(i))) {
  	  				return 0;
  	  			}
  	  		}

  			setFormula(createTree(getFormula()));
  			GEAS_ori.checktime++;
  			ArrayList<Link> link = Detection.detectEcc(this);//constraint checking
  			setTempLink(link);
  		}
  		ArrayList<Link> lastLink = getLastLink();
		ArrayList<Link> link = getLink();
		ArrayList<Link> differenceLink = Detection.differenceSet(link,lastLink);
		setLink(link);
		if(!getValue()) {
			for(int x = 0;x < differenceLink.size();x++)
				out.write((getId() + differenceLink.get(x).toString() + "\n").getBytes());
			return differenceLink.size();
		}
		return 0;
  	}
  	public int EccNew(FileOutputStream out) throws IOException {
  		if(chgBuffer.size() == 0) {
  			;//System.err.println("The change queue is empty!");
  		}
  		else {
  			for(int i = 0;i < chgBuffer.size();i++) {
  	  			if(!change(chgBuffer.get(i))) {
  	  				return 0;
  	  			}
  	  		}
  			
  			setFormula(formula.createTreeNew(contexts));
  			GEAS_ori.checktime++;
  			ArrayList<Link> link = Detection.detectEcc(this);//constraint checking
  			setTempLink(link);
  		}
  		ArrayList<Link> lastLink = getLastLink();
		ArrayList<Link> link = getLink();
		ArrayList<Link> differenceLink = Detection.differenceSet(link,lastLink);
		setLink(link);
		if(!getValue()) {
			for(int x = 0;x < differenceLink.size();x++)
				out.write((getId() + differenceLink.get(x).toString() + "\n").getBytes());
			return differenceLink.size();
		}
		return 0;
  	}
  	public static ArrayList<SameContextChange> divideChanges(LinkedList<ContextChange> changes) {
  		ArrayList<SameContextChange> result = new ArrayList<SameContextChange>();
  		for(ContextChange change : changes) {
  			int flag = 0;
  			for(SameContextChange temp : result) {
  				if(change.getContext().matches(temp.getContext())) {
  					temp.addChange(change);
  					flag = 1;
  					break;
  				}
  			}
  			if(flag == 0) {
  				SameContextChange c = new SameContextChange(change.getContext());
  				c.addChange(change);
  				result.add(c);
  			}
  		}
  		
  		return result;
  	}	
  	
  	public int Pcc(FileOutputStream out) throws IOException {
  		
  		if(chgBuffer.size() == 0) {
  			;//System.err.println("The change queue is empty!");
  		}
  		if(chgBuffer.size() == 1) {
  			ContextChange change = chgBuffer.get(0);
	  		if(!change(change))
	  			return 0;
	  		setFormula(createTreePcc(getFormula(),new SameContextChange(change)));

	  		GEAS_ori.checktime++;
	  		ArrayList<Link> link = Detection.detectPccM(this,new SameContextChange(change));//constraint checking
	  		setTempLink(link);
  		}
  		else {
  			ArrayList<SameContextChange> result = divideChanges(chgBuffer);
	  		
	  		for(SameContextChange temp : result) {
	  			
	  			for(ContextChange change : temp.getChanges()) {
	  				if(!change(change)) {
	  					return 0;
	  				}
	  			}
	  			ArrayList<Link> link = new ArrayList<Link>();
	  			setFormula(createTreePcc(getFormula(),temp));
	  			GEAS_ori.checktime++;
				//out.write(("Same change group: "+ temp.toString()+'\n').getBytes());
	  			link = Detection.detectPccM(this,temp);
	  			//out.write(("End group: "+ temp.toString()+'\n').getBytes());
	  			setTempLink(link);
	  		}
  		}
  		ArrayList<Link> lastLink = getLastLink();
		ArrayList<Link> link = getLink();
		ArrayList<Link> differenceLink = Detection.differenceSet(link,lastLink);
		setLink(link);
		if(!getValue()) {
			for(int x = 0;x < differenceLink.size();x++)
				out.write((getId() + differenceLink.get(x).toString() + "\n").getBytes());
			return differenceLink.size();
		}
		return 0;
  	}
    

  	@SuppressWarnings({ "unchecked", "rawtypes" })
  	public Formula createTreePcc(Formula formula, SameContextChange group){
  		return formula.formulaProcess(group,contexts);
  	}
  	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Formula createTreePcc2(Formula formula,SameContextChange group) {
		String context = group.getContext();
		ArrayList<ContextChange> addChanges = group.getAddChanges();
		ArrayList<ContextChange> deleteChanges = group.getDeleteChanges();
        if(formula.getKind().matches("forall")) {
            ForallFormula result = new ForallFormula("forall");
            result.setContext(((ForallFormula)formula).getVariable(),((ForallFormula)formula).getContext());
            result.setSubFormula(((ForallFormula)formula).getSub());
            //LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ForallFormula)formula).getSubnodes());
            //result.setElements(subNodes);
            result.setValue(((ForallFormula)formula).getValue());
            result.setLink(((ForallFormula)formula).getLink());
            //if(formula.affect(context)) {
        	String name = ((ForallFormula)formula).getContext(); 
            if(name.matches(context)) {//node being affected
            	LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ForallFormula)formula).getSubnodes());
                result.setElements(subNodes);
                if(addChanges.size() != 0) {
	                for(ContextChange change : addChanges) {
		                SubNode element = new SubNode(change.getKey());
		                element.setFormula(createTree(((ForallFormula)formula).getSub()));
		                result.setElements(element);
	                }
	                result.setSubFormula(createTree(((ForallFormula)formula).getSub()));
                }
                if(deleteChanges.size() != 0) {
	                for(ContextChange change : deleteChanges) {
	                	for(int i = 0;i < result.getSubnodes().size();i++) {
		                    if(result.getSubnodes().get(i).getKind().matches(change.getKey())) {
		                        result.getSubnodes().remove(i);
		                        break;
		                    }
		                }
	                }
                }
            }
            else if(((ForallFormula)formula).getSub().affect(context)) {//subnode being affected
                for(int i = 0;i < ((ForallFormula)formula).getSubnodes().size();i++) {
                	SubNode node = ((ForallFormula)formula).getSubnodes().get(i);
                	SubNode element = new SubNode(node.getKind());
                	element.setValue(node.getValue());
                	element.setLink(node.getLink());
                    Formula temp = createTreePcc(node.getFormula(),group);
                	element.setFormula(temp);
                    result.setElements(element);
                }
                result.setSubFormula(createTreePcc(((ForallFormula)formula).getSub(),group));
            }
            //}
            return result;
        }
        else if(formula.getKind().matches("exists")) {
            ExistsFormula result = new ExistsFormula("exists");
            result.setContext(((ExistsFormula)formula).getVariable(),((ExistsFormula)formula).getContext());
            result.setSubFormula(((ExistsFormula)formula).getSub());
            //LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ExistsFormula)formula).getSubnodes());
            //result.setElements(subNodes);
            result.setValue(((ExistsFormula)formula).getValue());
            result.setLink(((ExistsFormula)formula).getLink());
            //if(formula.affect(context)) {
        	String name = ((ExistsFormula)formula).getContext();
        	if(name.matches(context)) {
        		LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ExistsFormula)formula).getSubnodes());
                result.setElements(subNodes);
                if(addChanges.size() != 0) {
	                for(ContextChange change : addChanges) {
		                SubNode element = new SubNode(change.getKey());
		                element.setFormula(createTree(((ExistsFormula)formula).getSub()));
		                result.setElements(element);
	                }
	                result.setSubFormula(createTree(((ExistsFormula)formula).getSub()));
                }
                for(ContextChange change : deleteChanges) {
	                for(int i = 0;i < result.getSubnodes().size();i++) {
	                    if(result.getSubnodes().get(i).getKind().matches(change.getKey())) {
	                        result.getSubnodes().remove(i);
	                        break;
	                    }
	                }
                }
        	}
        	else if(((ExistsFormula)formula).getSub().affect(context)) {
                for(int i = 0;i < ((ExistsFormula)formula).getSubnodes().size();i++) {
                	SubNode node = ((ExistsFormula)formula).getSubnodes().get(i);
                	SubNode element = new SubNode(node.getKind());
                	element.setValue(node.getValue());
                	element.setLink(node.getLink());
                    Formula temp = createTreePcc(node.getFormula(),group);
                	element.setFormula(temp);
                    result.setElements(element);
                }
                result.setSubFormula(createTreePcc(((ExistsFormula)formula).getSub(),group));
            }
            //}
            return result;
        }
        else if(formula.getKind().matches("and")) {
            AndFormula result = new AndFormula("and");
            result.setValue(((AndFormula)formula).getValue());
            result.setLink(((AndFormula)formula).getLink());
            result.setSubFormula(createTreePcc(((AndFormula)formula).getFirst(),group),createTreePcc(((AndFormula)formula).getSecond(),group));
            return result;
        }
        else if(formula.getKind().matches("or")) {
            OrFormula result = new OrFormula("or");
            result.setValue(((OrFormula)formula).getValue());
            result.setLink(((OrFormula)formula).getLink());
            result.setSubFormula(createTreePcc(((OrFormula)formula).getFirst(),group),createTreePcc(((OrFormula)formula).getSecond(),group));
            return result;
        }
        else if(formula.getKind().matches("implies")) {
            ImpliesFormula result = new ImpliesFormula("implies");
            result.setValue(((ImpliesFormula)formula).getValue());
            result.setLink(((ImpliesFormula)formula).getLink());
            result.setSubFormula(createTreePcc(((ImpliesFormula)formula).getFirst(),group),createTreePcc(((ImpliesFormula)formula).getSecond(),group));
            return result;
        }
        else if(formula.getKind().matches("not")) {
            UnaryFormula result = new UnaryFormula("not");
            result.setValue(((UnaryFormula)formula).getValue());
            result.setLink(((UnaryFormula)formula).getLink());
            result.setSubFormula(createTreePcc(((UnaryFormula)formula).getFormula(),group));
            return result;
        }
        else if(formula.getKind().matches("bfunction")) {
            BFunc result = new BFunc(formula.getKind());
            result.setValue(((BFunc)formula).getValue());
            result.setLink(((BFunc)formula).getLink());
            result.setParam(new HashMap(((BFunc)formula).getParam()));
            return result;
        }
        return formula;
    }
	
	
	//construct CCT for PCC
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Formula createTreePcc(Formula formula,ContextChange change) {
        if(formula.getKind().matches("forall")) {
            ForallFormula result = new ForallFormula("forall");
            result.setContext(((ForallFormula)formula).getVariable(),((ForallFormula)formula).getContext());
            result.setSubFormula(((ForallFormula)formula).getSub());
            //LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ForallFormula)formula).getSubnodes());
            //result.setElements(subNodes);
            result.setValue(((ForallFormula)formula).getValue());
            result.setLink(((ForallFormula)formula).getLink());
            if(formula.affect(change)) {
            	String name = ((ForallFormula)formula).getContext();
	            if(((ForallFormula)formula).getSub().affect(change)) {
	                for(int i = 0;i < ((ForallFormula)formula).getSubnodes().size();i++) {
	                	SubNode node = ((ForallFormula)formula).getSubnodes().get(i);
	                	SubNode element = new SubNode(node.getKind());
	                	element.setValue(node.getValue());
	                	element.setLink(node.getLink());
	                    Formula temp = createTreePcc(node.getFormula(),change);
	                	element.setFormula(temp);
	                    result.setElements(element);
	                }
	                result.setSubFormula(createTreePcc(((ForallFormula)formula).getSub(),change));
	            }
	            else if(change.getOperate() == 1 && change.getContext().matches(name)) {//add
	                LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ForallFormula)formula).getSubnodes());
	                result.setElements(subNodes);
	                SubNode element = new SubNode(change.getKey());
	                element.setFormula(createTree(((ForallFormula)formula).getSub()));
	                result.setElements(element);
	                result.setSubFormula(createTree(((ForallFormula)formula).getSub()));
	            }
	            else if(change.getOperate() == 2 && change.getContext().matches(name)) {//delete
	                LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ForallFormula)formula).getSubnodes());
	                result.setElements(subNodes);
	                for(int i = 0;i < result.getSubnodes().size();i++) {
	                    if(result.getSubnodes().get(i).getKind().matches(change.getKey())) {
	                        result.getSubnodes().remove(i);
	                        break;
	                    }
	                }
	            }
	            else if(change.getOperate() == 3 && change.getContext().matches(name)) {//update     
	            }
            }
            return result;
        }
        else if(formula.getKind().matches("exists")) {
            ExistsFormula result = new ExistsFormula("exists");
            result.setContext(((ExistsFormula)formula).getVariable(),((ExistsFormula)formula).getContext());
            result.setSubFormula(((ExistsFormula)formula).getSub());
            //LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ExistsFormula)formula).getSubnodes());
            //result.setElements(subNodes);
            result.setValue(((ExistsFormula)formula).getValue());
            result.setLink(((ExistsFormula)formula).getLink());
            if(formula.affect(change)) {
            	String name = ((ExistsFormula)formula).getContext();
	            if(((ExistsFormula)formula).getSub().affect(change)) {
	                for(int i = 0;i < ((ExistsFormula)formula).getSubnodes().size();i++) {
	                	SubNode node = ((ExistsFormula)formula).getSubnodes().get(i);
	                	SubNode element = new SubNode(node.getKind());
	                	element.setValue(node.getValue());
	                	element.setLink(node.getLink());
	                    Formula temp = createTreePcc(node.getFormula(),change);
	                	element.setFormula(temp);
	                    result.setElements(element);
	                }
	                result.setSubFormula(createTreePcc(((ExistsFormula)formula).getSub(),change));
	            }
	            else if(change.getOperate() == 1 && change.getContext().matches(name)) {//add
	                LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ExistsFormula)formula).getSubnodes());
	                result.setElements(subNodes);
	                SubNode element = new SubNode(change.getKey());
	                element.setFormula(createTree(((ExistsFormula)formula).getSub()));
	                result.setElements(element);
	                result.setSubFormula(createTree(((ExistsFormula)formula).getSub()));
	            }
	            else if(change.getOperate() == 2 && change.getContext().matches(name)) {//delete
	                LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ExistsFormula)formula).getSubnodes());
	                result.setElements(subNodes);
	                for(int i = 0;i < result.getSubnodes().size();i++) {
	                    if(result.getSubnodes().get(i).getKind().matches(change.getKey())) {
	                        result.getSubnodes().remove(i);
	                        break;
	                    }
	                }
	            }
	            else if(change.getOperate() == 3 && change.getContext().matches(name)) {//update     
	            }
            }
            return result;
        }
        else if(formula.getKind().matches("and")) {
            AndFormula result = new AndFormula("and");
            result.setValue(((AndFormula)formula).getValue());
            result.setLink(((AndFormula)formula).getLink());
            result.setSubFormula(createTreePcc(((AndFormula)formula).getFirst(),change),createTreePcc(((AndFormula)formula).getSecond(),change));
            return result;
        }
        else if(formula.getKind().matches("or")) {
            OrFormula result = new OrFormula("or");
            result.setValue(((OrFormula)formula).getValue());
            result.setLink(((OrFormula)formula).getLink());
            result.setSubFormula(createTreePcc(((OrFormula)formula).getFirst(),change),createTreePcc(((OrFormula)formula).getSecond(),change));
            return result;
        }
        else if(formula.getKind().matches("implies")) {
            ImpliesFormula result = new ImpliesFormula("implies");
            result.setValue(((ImpliesFormula)formula).getValue());
            result.setLink(((ImpliesFormula)formula).getLink());
            result.setSubFormula(createTreePcc(((ImpliesFormula)formula).getFirst(),change),createTreePcc(((ImpliesFormula)formula).getSecond(),change));
            return result;
        }
        else if(formula.getKind().matches("not")) {
            UnaryFormula result = new UnaryFormula("not");
            result.setValue(((UnaryFormula)formula).getValue());
            result.setLink(((UnaryFormula)formula).getLink());
            result.setSubFormula(createTreePcc(((UnaryFormula)formula).getFormula(),change));
            return result;
        }
        else if(formula.getKind().matches("bfunction")) {
            BFunc result = new BFunc(formula.getKind());
            result.setValue(((BFunc)formula).getValue());
            result.setLink(((BFunc)formula).getLink());
            result.setParam(new HashMap(((BFunc)formula).getParam()));
            return result;
        }
        return formula;
    }
	
	//Constructing CCT for ECC
    @SuppressWarnings({ "unchecked", "rawtypes" })
	public Formula createTree(Formula formula) {
        if(formula.getKind().matches("forall")) {
            ForallFormula result = new ForallFormula("forall");
            result.setContext(((ForallFormula)formula).getVariable(),((ForallFormula)formula).getContext());
            result.setSubFormula(((ForallFormula)formula).getSub());
            Context ct = contexts.get(((ForallFormula)formula).getContext());
            for(Map.Entry entry : ct.getElements().entrySet()) {
                Element sc = (Element)entry.getValue();
                SubNode element = new SubNode(sc.getKey());
                element.setFormula(createTree(((ForallFormula)formula).getSub()));
                result.setElements(element);
            }
            return result;
        }
        else if(formula.getKind().matches("exists")) {
            ExistsFormula result = new ExistsFormula("exists");
            result.setContext(((ExistsFormula)formula).getVariable(),((ExistsFormula)formula).getContext());
            result.setSubFormula(((ExistsFormula)formula).getSub());
            Context ct = contexts.get(((ExistsFormula)formula).getContext());
            for (Map.Entry entry : ct.getElements().entrySet()) {
                Element sc = (Element)entry.getValue(); 
                SubNode element = new SubNode(sc.getKey());
                element.setFormula(createTree(((ExistsFormula)formula).getSub()));
                result.setElements(element);
            }
            return result;
        }
        else if(formula.getKind().matches("and")) {
            AndFormula result = new AndFormula("and");
            result.setSubFormula(createTree(((AndFormula)formula).getFirst()),createTree(((AndFormula)formula).getSecond()));
            return result;
        }
        else if(formula.getKind().matches("or")) {
            OrFormula result = new OrFormula("or");
            result.setSubFormula(createTree(((OrFormula)formula).getFirst()),createTree(((OrFormula)formula).getSecond()));
            return result;
        }
        else if(formula.getKind().matches("implies")) {
            ImpliesFormula result = new ImpliesFormula("implies");
            result.setSubFormula(createTree(((ImpliesFormula)formula).getFirst()),createTree(((ImpliesFormula)formula).getSecond()));
            return result;
        }
        else if(formula.getKind().matches("not")) {
            UnaryFormula result = new UnaryFormula("not");
            result.setSubFormula(createTree(((UnaryFormula)formula).getFormula()));
            return result;
        }
        else if(formula.getKind().matches("bfunction")) {
            BFunc result = new BFunc(formula.getKind());
            result.setParam(new HashMap(((BFunc)formula).getParam()));
            return result;
        }
        return formula;
    }
    public String returnGoalForCtx(String contextName){
    	if(goalMap.containsKey(contextName))
    		return goalMap.get(contextName);
    	else return "Null";
    }
  //  HashMap<String,Context> getContexts()
  	public HashMap<String,Context> changeContext(String contextName, Element aElement, Element bElement){

  		contexts.get(contextName).deleteElement(aElement.getKey());
  		contexts.get(contextName).addElement(bElement.getKey(), bElement);
  		return contexts;
  	}
}
