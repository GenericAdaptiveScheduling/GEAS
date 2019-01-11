/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package formula;

import java.util.ArrayList;
import java.util.HashMap;

import middleware.SameContextChange;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rule.Rule;
import context.Context;
import context.ContextChange;

/**
 *
 * @author why
 * or formula
 */
public class OrFormula extends Formula {
    private Formula first; 
    private Formula second;
    
    public static Log logger = LogFactory.getLog(AndFormula.class.getName());
    
    public OrFormula(String name) {
        super(name);
    }
    public void setSubFormula(Formula left,Formula right) {
        first = left;
        second = right;
        value = first.value || second.value;
    }

    public Formula getFirst() {
    	return first;
    }

    public Formula getSecond() {
    	return second;
    }
    
    @Override
    public boolean evaluateEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        boolean result = first.evaluateEcc(contexts,node) || second.evaluateEcc(contexts,node);
        value = result;
        return result;
    }

    @Override
    public ArrayList<Link> linkGenerationEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        boolean result1 = first.evaluateEcc(contexts,node);
        boolean result2 = second.evaluateEcc(contexts,node);
        if(result1 == true && result2 == true)
            return Link.union(first.linkGenerationEcc(contexts,node),second.linkGenerationEcc(contexts,node));
        else if(result1 == false && result2 == false)
            return Link.cartesianList(first.linkGenerationEcc(contexts,node),second.linkGenerationEcc(contexts,node));
        else if(result1 == true && result2 == false)
            return first.linkGenerationEcc(contexts,node);
        else if(result1 == false && result2 == true) 
            return second.linkGenerationEcc(contexts,node);
        return null;
    }

    @Override
    public boolean affect(ContextChange change) {
        return first.affect(change) || second.affect(change);
    }
    
	@Override
	public boolean affect(String context) {
		return first.affect(context) || second.affect(context);
	}

    @Override
    public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change) {
        boolean result = false;
        if(!affect(change)) {}
        else if(first.affect(change) && second.affect(change)) {
            result = first.evaluatePcc(contexts,node,change) || second.evaluatePcc(contexts,node,change);
            value = result;
        }
        else if(first.affect(change) && !second.affect(change)) {
            result = first.evaluatePcc(contexts,node,change) || second.value;
            value = result;
        }
        else if(!first.affect(change) && second.affect(change)) {
            result = first.value || second.evaluatePcc(contexts,node,change);
            value = result;
        }
        return value;
    }

    @Override
    public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change) {
        ArrayList<Link> result = new ArrayList<Link>();
        if(!affect(change)) {}
        else {
            boolean result1 = first.evaluateEcc(contexts,node);
            boolean result2 = second.evaluateEcc(contexts,node);
            if(first.affect(change) && second.affect(change)) {    
                if(result1 == true && result2 == true)
                    result = Link.union(first.linkGenerationPcc(contexts,node,change),second.linkGenerationPcc(contexts,node,change));
                else if(result1 == false && result2 == false)
                    result = Link.cartesianList(first.linkGenerationPcc(contexts,node,change),second.linkGenerationPcc(contexts,node,change));
                else if(result1 == true && result2 == false)
                    result = first.linkGenerationPcc(contexts,node,change);
                else if(result1 == false && result2 == true) 
                    result = second.linkGenerationPcc(contexts,node,change);
                link = result;
            }
            else if(first.affect(change) && !second.affect(change)) {
                ArrayList<Link> temp = new ArrayList<Link>();
                temp = second.link;
                if(result1 == true && result2 == true)
                    result = Link.union(first.linkGenerationPcc(contexts,node,change),temp);
                else if(result1 == false && result2 == false)
                    result = Link.cartesianList(first.linkGenerationPcc(contexts,node,change),temp);
                else if(result1 == true && result2 == false)
                    result = first.linkGenerationPcc(contexts,node,change);
                else if(result1 == false && result2 == true) 
                    result = temp;
                link = result;
            }
            else if(!first.affect(change) && second.affect(change)) {
                ArrayList<Link> temp = new ArrayList<Link>();
                temp = first.link;
                if(result1 == true && result2 == true)
                    result = Link.union(temp,second.linkGenerationPcc(contexts,node,change));
                else if(result1 == false && result2 == false)
                    result = Link.cartesianList(temp,second.linkGenerationPcc(contexts,node,change));
                else if(result1 == true && result2 == false)
                    result = temp;
                else if(result1 == false && result2 == true) 
                    result = second.linkGenerationPcc(contexts,node,change);
                link = result;
            }
        }
        return link;
    }

    @Override
    public void setGoal(String goal) {
        goalLink = goal;
        first.setGoal(goalLink);
        second.setGoal(goalLink);
    }
	@Override
	public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node, SameContextChange group) {
		String context = group.getContext();
		boolean result = false;
        if(!affect(context)) {}
        else if(first.affect(context) && second.affect(context)) {
            result = first.evaluatePcc(contexts,node,group) || second.evaluatePcc(contexts,node,group);
            value = result;
        }
        else if(first.affect(context) && !second.affect(context)) {
            result = first.evaluatePcc(contexts,node,group) || second.value;
            value = result;
        }
        else if(!first.affect(context) && second.affect(context)) {
            result = first.value || second.evaluatePcc(contexts,node,group);
            value = result;
        }
        return value;
	}
	@Override
	public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,
			SameContextChange group) {
		String context = group.getContext();
		ArrayList<Link> result = new ArrayList<Link>();
        if(!affect(context)) {}
        else {
            boolean result1 = first.evaluateEcc(contexts,node);
            boolean result2 = second.evaluateEcc(contexts,node);
            if(first.affect(context) && second.affect(context)) {    
                if(result1 == true && result2 == true)
                    result = Link.union(first.linkGenerationPcc(contexts,node,group),second.linkGenerationPcc(contexts,node,group));
                else if(result1 == false && result2 == false)
                    result = Link.cartesianList(first.linkGenerationPcc(contexts,node,group),second.linkGenerationPcc(contexts,node,group));
                else if(result1 == true && result2 == false)
                    result = first.linkGenerationPcc(contexts,node,group);
                else if(result1 == false && result2 == true) 
                    result = second.linkGenerationPcc(contexts,node,group);
                link = result;
            }
            else if(first.affect(context) && !second.affect(context)) {
                ArrayList<Link> temp = new ArrayList<Link>();
                temp = second.link;
                if(result1 == true && result2 == true)
                    result = Link.union(first.linkGenerationPcc(contexts,node,group),temp);
                else if(result1 == false && result2 == false)
                    result = Link.cartesianList(first.linkGenerationPcc(contexts,node,group),temp);
                else if(result1 == true && result2 == false)
                    result = first.linkGenerationPcc(contexts,node,group);
                else if(result1 == false && result2 == true) 
                    result = temp;
                link = result;
            }
            else if(!first.affect(context) && second.affect(context)) {
                ArrayList<Link> temp = new ArrayList<Link>();
                temp = first.link;
                if(result1 == true && result2 == true)
                    result = Link.union(temp,second.linkGenerationPcc(contexts,node,group));
                else if(result1 == false && result2 == false)
                    result = Link.cartesianList(temp,second.linkGenerationPcc(contexts,node,group));
                else if(result1 == true && result2 == false)
                    result = temp;
                else if(result1 == false && result2 == true) 
                    result = second.linkGenerationPcc(contexts,node,group);
                link = result;
            }
        }
        return link;
	}
	@Override
	public Formula createTreeNew(HashMap<String, Context> contexts) {
		// TODO Auto-generated method stub
		OrFormula result = new OrFormula("or");
        result.setSubFormula(first.createTreeNew(contexts),second.createTreeNew(contexts));
        return result;
	}
	@Override
	public Formula formulaProcess(SameContextChange group,HashMap<String,Context> _contexts) {
		OrFormula result = new OrFormula("or");
        result.setValue(this.getValue());
        result.setLink(this.getLink());
        result.setSubFormula(new Rule(_contexts).createTreePcc(this.getFirst(),group),new Rule(_contexts).createTreePcc(this.getSecond(),group));
        return result;
	}
}
