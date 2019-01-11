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
 * implies formula
 */
public class ImpliesFormula extends Formula {
    private Formula first; 
    private Formula second;
    
    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(AndFormula.class.getName());
    
    public ImpliesFormula(String name) {
        super(name);
    }
    
    public void setSubFormula(Formula left,Formula right) {
        first = left;
        second = right;
        value = (!first.value) || second.value;
    }

    public Formula getFirst() {
    	return first;
    }

    public Formula getSecond() {
    	return second;
    }
    
    @Override
    public boolean evaluateEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        boolean result =  !(first.evaluateEcc(contexts,node)) || second.evaluateEcc(contexts,node);
        value = result;
        return result;
    }

    @Override
    public ArrayList<Link> linkGenerationEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        boolean result1 = first.evaluateEcc(contexts,node);
        boolean result2 = second.evaluateEcc(contexts,node);
        if(result1 == true && result2 == true)
            return second.linkGenerationEcc(contexts,node);
        else if(result1 == false && result2 == false)
            return Link.flipSet(first.linkGenerationEcc(contexts,node));
        else if(result1 == true && result2 == false)
            return Link.cartesianList(Link.flipSet(first.linkGenerationEcc(contexts,node)),second.linkGenerationEcc(contexts,node));
        else if(result1 == false && result2 == true) 
            return Link.union(Link.flipSet(first.linkGenerationEcc(contexts,node)),second.linkGenerationEcc(contexts,node));
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
            result = !(first.evaluatePcc(contexts,node,change)) || second.evaluatePcc(contexts,node,change);
            value = result;
        }
        else if(first.affect(change) && !second.affect(change)) {
            boolean temp;
            temp = second.value;
            result = !(first.evaluatePcc(contexts,node,change)) || temp;
            value = result;
        }
        else if(!first.affect(change) && second.affect(change)) {
            boolean temp;
            temp = first.value;
            result = !(temp) || second.evaluatePcc(contexts,node,change);
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
                ArrayList<Link> firstLink = new ArrayList<Link>(first.linkGenerationPcc(contexts,node,change));
                ArrayList<Link> secondLink = new ArrayList<Link>(second.linkGenerationPcc(contexts,node,change));
                if(result1 == true && result2 == true)
                    result = secondLink;
                else if(result1 == false && result2 == false)
                    result = Link.flipSet(firstLink);
                else if(result1 == true && result2 == false)
                    result = Link.cartesianList(Link.flipSet(firstLink),secondLink);
                else if(result1 == false && result2 == true) 
                    result = Link.union(Link.flipSet(firstLink),secondLink);
                link = result;
            }
            else if(first.affect(change) && !second.affect(change)) { 
                ArrayList<Link> temp;
                temp = new ArrayList<Link>(second.link);
                ArrayList<Link> firstLink = new ArrayList<Link>(first.linkGenerationPcc(contexts,node,change));
                if(result1 == true && result2 == true)
                    result = temp;
                else if(result1 == false && result2 == false)
                    result = Link.flipSet(firstLink);
                else if(result1 == true && result2 == false) {
                    ArrayList<Link> li = Link.flipSet(firstLink);
                    result = Link.cartesianList(li,temp);
                }
                else if(result1 == false && result2 == true) 
                    result = Link.union(Link.flipSet(firstLink),temp);
                link = result;
            }
            else if(!first.affect(change) && second.affect(change)) { 
                ArrayList<Link> temp;
                temp = new ArrayList<Link>(first.link);
                ArrayList<Link> secondLink = new ArrayList<Link>(second.linkGenerationPcc(contexts,node,change));
                if(result1 == true && result2 == true)
                    result = secondLink;
                else if(result1 == false && result2 == false)
                    result = Link.flipSet(temp);
                else if(result1 == true && result2 == false) {
                    result = Link.cartesianList(Link.flipSet(temp),secondLink);
                }
                else if(result1 == false && result2 == true) 
                    result = Link.union(Link.flipSet(temp),secondLink);
                link = result;
            }
        }
        return link;
    }

    @Override
    public void setGoal(String goal) {
        goalLink = goal;
        if(!goalLink.matches("null")) {
            first.setGoal(Boolean.toString(!Boolean.parseBoolean(goalLink)));
            second.setGoal(goalLink);
        }
        else {
            first.setGoal("null");
            second.setGoal("null");
        }
    }

	@Override
	public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node, SameContextChange group) {
		String context = group.getContext();
		boolean result = false;
        if(!affect(context)) {}
        else if(first.affect(context) && second.affect(context)) {
            result = !(first.evaluatePcc(contexts,node,group)) || second.evaluatePcc(contexts,node,group);
            value = result;
        }
        else if(first.affect(context) && !second.affect(context)) {
            boolean temp;
            temp = second.value;
            result = !(first.evaluatePcc(contexts,node,group)) || temp;
            value = result;
        }
        else if(!first.affect(context) && second.affect(context)) {
            boolean temp;
            temp = first.value;
            result = !(temp) || second.evaluatePcc(contexts,node,group);
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
                ArrayList<Link> firstLink = new ArrayList<Link>(first.linkGenerationPcc(contexts,node,group));
                ArrayList<Link> secondLink = new ArrayList<Link>(second.linkGenerationPcc(contexts,node,group));
                if(result1 == true && result2 == true)
                    result = secondLink;
                else if(result1 == false && result2 == false)
                    result = Link.flipSet(firstLink);
                else if(result1 == true && result2 == false)
                    result = Link.cartesianList(Link.flipSet(firstLink),secondLink);
                else if(result1 == false && result2 == true) 
                    result = Link.union(Link.flipSet(firstLink),secondLink);
                link = result;
            }
            else if(first.affect(context) && !second.affect(context)) { 
                ArrayList<Link> temp;
                temp = new ArrayList<Link>(second.link);
                ArrayList<Link> firstLink = new ArrayList<Link>(first.linkGenerationPcc(contexts,node,group));
                if(result1 == true && result2 == true)
                    result = temp;
                else if(result1 == false && result2 == false)
                    result = Link.flipSet(firstLink);
                else if(result1 == true && result2 == false) {
                    ArrayList<Link> li = Link.flipSet(firstLink);
                    result = Link.cartesianList(li,temp);
                }
                else if(result1 == false && result2 == true) 
                    result = Link.union(Link.flipSet(firstLink),temp);
                link = result;
            }
            else if(!first.affect(context) && second.affect(context)) { 
                ArrayList<Link> temp;
                temp = new ArrayList<Link>(first.link);
                ArrayList<Link> secondLink = new ArrayList<Link>(second.linkGenerationPcc(contexts,node,group));
                if(result1 == true && result2 == true)
                    result = secondLink;
                else if(result1 == false && result2 == false)
                    result = Link.flipSet(temp);
                else if(result1 == true && result2 == false) {
                    result = Link.cartesianList(Link.flipSet(temp),secondLink);
                }
                else if(result1 == false && result2 == true) 
                    result = Link.union(Link.flipSet(temp),secondLink);
                link = result;
            }
        }
        return link;
	}

	@Override
	public Formula createTreeNew(HashMap<String, Context> contexts) {
		// TODO Auto-generated method stub
		ImpliesFormula result = new ImpliesFormula("implies");
        result.setSubFormula(first.createTreeNew(contexts),second.createTreeNew(contexts));
        return result;
	}

	@Override
	public Formula formulaProcess(SameContextChange group,HashMap<String,Context> _contexts) {
		ImpliesFormula result = new ImpliesFormula("implies");
        result.setValue(this.getValue());
        result.setLink(this.getLink());
        result.setSubFormula(new Rule(_contexts).createTreePcc(this.getFirst(),group),new Rule(_contexts).createTreePcc(this.getSecond(),group));
        return result;
	}

}
