/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package formula; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import middleware.SameContextChange;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rule.Rule;
import context.Context;
import context.ContextChange;
import context.Element;
import dataLoader.Configuration;

/**
 *
 * @author why
 * forall formula
 */
public class ForallFormula extends Formula {
    private String variable;
    private String context;
    private Formula subFormula;
    private LinkedList<SubNode> elements = new LinkedList<SubNode>();
    
    public static Log logger = LogFactory.getLog(ForallFormula.class.getName());
    
    
    public ForallFormula(String name) {
        super(name);
        value = true;
    }
    
    public String getVariable() {
    	return variable;
    }
    
    public String getContext() {
    	return context;
    }
    
    public void setContext(String var,String ctx) {
        variable = var;
        context = ctx;
    }
    
    public Formula getSub() {
    	return subFormula;
    }
    
    public void setSubFormula(Formula sub_formula) {
        subFormula = sub_formula;
    }
    
    public LinkedList<SubNode> getSubnodes() {
    	return elements;
    }
    
    public void setElements(LinkedList<SubNode> subNodes) {
    	elements = subNodes;
    }
    
    public void setElements(SubNode element) {
        elements.add(element);
    }
    @Override
    public boolean evaluateEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        boolean result = true;
        Context ct = contexts.get(this.context);
        for(int i = 0;i < elements.size();i++) {
            SubNode element = elements.get(i);
        	//System.out.println("forall:"+element.kind);
            Element sc = ct.getElements().get(element.kind);
            node.setVar(variable,sc);
            boolean newValue = element.evaluateEcc(contexts,node);
            element.value = newValue;
            result = newValue && result;
            node.deleteVar(variable);
        }
        value = result;
        return result;
    }

    @Override
    public ArrayList<Link> linkGenerationEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        /*if(Configuration.getConfigStr("optimizingStrategy").matches("ON")) {
            if(!goalLink.matches("false")) {
                return new ArrayList<Link>();
            }
        }*/
        
        ArrayList<Link> result = new ArrayList<Link>();
        Context ct = contexts.get(this.context);
        for(int i = 0;i < elements.size();i++) {
            SubNode element = elements.get(i);
            Element sc = ct.getElements().get(element.kind);
            node.setVar(variable,sc);
            boolean truthValue = element.evaluateEcc(contexts,node);
            ArrayList<Link> l = new ArrayList<Link>();
            if(truthValue == false) {
                Link lk = new Link();
                HashMap<String,Element> binding = new HashMap<String,Element>();
                binding.put(variable,sc);
                lk.setViolated(true);
                lk.setBinding(binding);
                l = Link.cartesianList(lk,element.linkGenerationEcc(contexts,node));
            }
            element.link = l;
            result = Link.union(result, l);
            node.deleteVar(variable);
        }
        link = result;
        return result;
    }

    @Override
    public boolean affect(ContextChange change) {
        if(subFormula.affect(change) == true)
            return true;
        if(change.getContext().matches(context))
            return true;
        return false;
    }
    
	@Override
	public boolean affect(String context) {
		if(subFormula.affect(context) == true)
            return true;
        if(context.matches(this.context))
            return true;
        return false;
	}

	@Override
    public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change) {
        if(!affect(change)) {}
        else if(subFormula.affect(change)) {
            boolean result = true;
            Context ct = contexts.get(this.context);
            for(int i = 0;i < elements.size();i++) { 
                SubNode element = elements.get(i);
                Element sc = ct.getElements().get(element.kind);
                node.setVar(variable,sc);
                //System.out.println("node:"+variable+"-"+sc.toString()+"size:"+elements.size()+"-"+i);
                boolean newValue = element.evaluatePcc(contexts,node,change);
                element.value = newValue;
                result = newValue && result;
                node.deleteVar(variable);
            }
            value = result;
        }
        else if(change.getOperate() == 1) {//add
            SubNode element = elements.getLast();
            Context ct = contexts.get(this.context);
            Element sc = ct.getElements().get(element.kind);
            node.setVar(variable,sc);
            
            boolean newValue = element.evaluateEcc(contexts,node);//,change);//old: ECC
            element.value = newValue;
            boolean result = newValue && value;
            value = result; 
            node.deleteVar(variable);
        }
        else if(change.getOperate() == 2) {//delete
            boolean result = true;
            for(int i = 0;i < elements.size();i++) {
                SubNode element = elements.get(i);     
                result = element.value && result;
            }
            value = result;
        }
        else if(change.getOperate() == 3) {//update
            
        }
        return value;
    }

	@Override
    public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change) {
		/*if(Configuration.getConfigStr("optimizingStrategy").matches("ON")) {
            if(!goalLink.matches("false")) {
                return new ArrayList<Link>();
            }
        }*/
        if(!affect(change)) {}
        else if(subFormula.affect(change)) {
            ArrayList<Link> result = new ArrayList<Link>();
            Context ct = contexts.get(this.context);
            
            for(int i = 0;i < elements.size();i++) {
                SubNode element = elements.get(i);
                Element sc = ct.getElements().get(element.kind);
                node.setVar(variable,sc);
//                if(subFormula.kind.matches("forall") || subFormula.kind.matches("exists")) {
//                    if(change.getOperate() != 1)
//                        element.linkGenerationPcc(node,change);
//                }
                ArrayList<Link> subLink = new ArrayList<Link>(element.linkGenerationPcc(contexts,node,change));
                boolean truthValue = element.value;
                ArrayList<Link> l = new ArrayList<Link>();
                if(truthValue == false) {
                    Link lk = new Link();
                    HashMap<String,Element> binding = new HashMap<String,Element>();
                    binding.put(variable,sc);
                    lk.setViolated(true);
                    lk.setBinding(binding);              
                    l = Link.cartesianList(lk,subLink);
                }
                element.link = l;
                result = Link.union(result,l);
                node.deleteVar(variable);
            }
            link = result;
        }
        else if(change.getOperate() == 1) {//add
            SubNode element = elements.getLast();
            Context ct = contexts.get(this.context);
            Element sc = ct.getElements().get(element.kind);
            node.setVar(variable,sc);
            
            ArrayList<Link> l = new ArrayList<Link>();
            boolean truthValue = element.value;
            if(truthValue == false) {
                Link lk = new Link();
                HashMap<String,Element> binding = new HashMap<String,Element>();
                binding.put(variable,sc);
                lk.setViolated(true);
                lk.setBinding(binding);
                l = Link.cartesianList(lk,element.linkGenerationEcc(contexts,node));
            }
            element.link = l;
            ArrayList<Link> result = Link.union(l,link);
            link = result;
            node.deleteVar(variable);
        }
        else if(change.getOperate() == 2) {//delete
            ArrayList<Link> result = new ArrayList<Link>();
            for(int i = 0;i < elements.size();i++) {
                SubNode element = elements.get(i);
                result = Link.union(result,element.link);
            }
            link = result;
        }
        else if(change.getOperate() == 3) {//update
            
        }
        return link;
    }

    @Override
    public void setGoal(String goal) {
        goalLink = goal;
        if(goalLink.matches("false")) {
            subFormula.setGoal(goalLink);
        }
        else {
            subFormula.setGoal("null");
        }
    }

	@Override
	public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node, SameContextChange group) {
		String context = group.getContext();
		ArrayList<ContextChange> addChanges = group.getAddChanges();
		ArrayList<ContextChange> deleteChanges = group.getDeleteChanges();
		int add = addChanges.size();
		//System.out.println("add:"+addChanges.toString());
		int delete = deleteChanges.size();
		if(!affect(context)) {}
		else if(this.context.matches(context)) {
            boolean result = true;
            Context ct = contexts.get(this.context);
            if(add != 0) {
				//System.out.println("add:" + add);
            	result = value;
	            for(int i = 1;i <= add;i++) {
	            	//System.out.println(elements.size()+":"+i);
	            	SubNode element = elements.get(elements.size() - i);
	            	
	            	Element sc = ct.getElements().get(element.kind);
		            node.setVar(variable,sc);
		            boolean newValue = element.evaluateEcc(contexts,node);
		            element.value = newValue;
		            result = result && newValue;
		            node.deleteVar(variable);
	            }
            }
            if(delete != 0) {
            	result = true;
	            for(int i = 0;i < elements.size();i++) {
	                SubNode element = elements.get(i);
	                result = element.value && result;
	            }
            }
            value = result;
		}
        else if(subFormula.affect(context)) {
            boolean result = true;
            Context ct = contexts.get(this.context);
            //System.out.println(elements.size());
            for(int i = 0;i < elements.size();i++) {
                SubNode element = elements.get(i);
                //System.out.println(element.kind);
                Element sc = ct.getElements().get(element.kind);
                //System.out.println(sc);
                node.setVar(variable,sc);
//                for(String key : node.getVar().keySet()) {
//	            	System.out.println("~~" + key + "==" + node.getVar().get(key));
//	            }
                boolean newValue = element.evaluatePcc(contexts,node,group);
                element.value = newValue;
                result = newValue && result;
                node.deleteVar(variable);
            }
            value = result;
        }
        return value;
	}

	@Override
	public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,
			SameContextChange group) {
		String context = group.getContext();
		ArrayList<ContextChange> addChanges = group.getAddChanges();
		ArrayList<ContextChange> deleteChanges = group.getDeleteChanges();
		int add = addChanges.size();
		int delete = deleteChanges.size();
		/*if(Configuration.getConfigStr("optimizingStrategy").matches("ON")) {
            if(!goalLink.matches("false")) {
                return new ArrayList<Link>();
            }
        }*/
        if(!affect(context)) {}
		else if(this.context.matches(context)) {
			ArrayList<Link> result = new ArrayList<Link>();
            Context ct = contexts.get(this.context);
			if(add != 0) {
				//System.out.println("add:" + add);
				result = new ArrayList<Link>(link);
				for(int i = 1;i <= add;i++) {
		            //SubNode element = elements.getLast();
					SubNode element = elements.get(elements.size() - i);
		            Element sc = ct.getElements().get(element.kind);
		            node.setVar(variable,sc);
		            //System.out.println(element.kind + "==" + sc);
		            ArrayList<Link> l = new ArrayList<Link>();
		            boolean truthValue = element.value;
		            //System.out.println(truthValue);
		            if(truthValue == false) {
		                Link lk = new Link();
		                HashMap<String,Element> binding = new HashMap<String,Element>();
		                binding.put(variable,sc);
		                lk.setViolated(true);
		                lk.setBinding(binding);
		                l = Link.cartesianList(lk,element.linkGenerationEcc(contexts,node));
		            }
		            element.link = l;
		            //System.out.println("lllllllll:" + l);
		            node.deleteVar(variable);
		            result = Link.union(result,l);
		            //System.out.println("result:" + result);
				}
			}
			if(delete != 0) {
				//System.out.println("delete:" + delete);
				result = new ArrayList<Link>();
	            for(int i = 0;i < elements.size();i++) {
	                SubNode element = elements.get(i);
	                result = Link.union(result,element.link);
	            }
				//System.out.println(result);
			}
			link = result;
		}
        else if(subFormula.affect(context)) {
            ArrayList<Link> result = new ArrayList<Link>();
            Context ct = contexts.get(this.context);
            
            for(int i = 0;i < elements.size();i++) {
                SubNode element = elements.get(i);
                Element sc = ct.getElements().get(element.kind);
            	//System.out.println("********" + sc);
                node.setVar(variable,sc);
//                if(subFormula.kind.matches("forall") || subFormula.kind.matches("exists")) {
//                    if(delete != 0)
//                        element.linkGenerationPcc(node,group);
//                }
                ArrayList<Link> subLink = new ArrayList<Link>(element.linkGenerationPcc(contexts,node,group));
                boolean truthValue = element.value;
                ArrayList<Link> l = new ArrayList<Link>();
                if(truthValue == false) {
                	//System.out.println("***" + sc);
                	//System.out.println(subLink);
                    Link lk = new Link();
                    HashMap<String,Element> binding = new HashMap<String,Element>();
                    binding.put(variable,sc);
                    lk.setViolated(true);
                    lk.setBinding(binding);
                    l = Link.cartesianList(lk,subLink);
                }
                element.link = l;
                result = Link.union(result,l);
                node.deleteVar(variable);
            }
            link = result;
        }

        //System.out.println("link:" + link);
        return link;
	}

	@Override
	public Formula createTreeNew(HashMap<String, Context> contexts) {
		// TODO Auto-generated method stub
		ForallFormula result = new ForallFormula("forall");
        result.setContext(variable,context);
        result.setSubFormula(subFormula);
        Context ct = contexts.get(getContext());
        for(Map.Entry entry : ct.getElements().entrySet()) {
            Element sc = (Element)entry.getValue();
            SubNode element = new SubNode(sc.getKey());
            element.setFormula(subFormula.createTreeNew(contexts));
            result.setElements(element);
        }
        return result;
	}

	@Override
	public Formula formulaProcess(SameContextChange group,HashMap<String,Context> _contexts) {
		// TODO Auto-generated method stub
		String context = group.getContext();
		ArrayList<ContextChange> addChanges = group.getAddChanges();
		ArrayList<ContextChange> deleteChanges = group.getDeleteChanges();
		
		 ForallFormula result = new ForallFormula("forall");
         result.setContext(this.getVariable(),this.getContext());
         result.setSubFormula(this.getSub());
         //LinkedList<SubNode> subNodes = new LinkedList<SubNode>(((ForallFormula)formula).getSubnodes());
         //result.setElements(subNodes);
         result.setValue(this.getValue());
         result.setLink(this.getLink());
         //if(formula.affect(context)) {
     	String name = this.getContext(); 
         if(name.matches(context)) {//node is affected
         	LinkedList<SubNode> subNodes = new LinkedList<SubNode>(this.getSubnodes());
             result.setElements(subNodes);
             if(addChanges.size() != 0) {
	                for(ContextChange change : addChanges) {
		                SubNode element = new SubNode(change.getKey());
		                element.setFormula(new Rule(_contexts).createTree(this.getSub()));
		                result.setElements(element);
	                }
	                result.setSubFormula(new Rule(_contexts).createTree(this.getSub()));
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
         else if(this.getSub().affect(context)) {//subnode is affected
             for(int i = 0;i < this.getSubnodes().size();i++) {
             	SubNode node = this.getSubnodes().get(i);
             	SubNode element = new SubNode(node.getKind());
             	element.setValue(node.getValue());
             	element.setLink(node.getLink());
                 Formula temp = new Rule(_contexts).createTreePcc(node.getFormula(),group);
             	element.setFormula(temp);
                 result.setElements(element);
             }
             result.setSubFormula(new Rule(_contexts).createTreePcc(this.getSub(),group));
         }
         //}
         return result;
	}
    
}
