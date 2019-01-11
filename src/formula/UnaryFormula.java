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
 * not formula
 */
public class UnaryFormula extends Formula {
    private Formula formula;
    
    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(UnaryFormula.class.getName());
    
    public UnaryFormula(String name) {
        super(name);
    
    }
    
    public void setSubFormula(Formula sub_formula) {
        formula = sub_formula;
        value = !(formula.value);
    }
    
    public Formula getFormula() {
    	return formula;
    }
    
    @Override
    public boolean evaluateEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        boolean result = !(formula.evaluateEcc(contexts,node));
        value = result;
        return result;
    }

    @Override
    public ArrayList<Link> linkGenerationEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        ArrayList<Link> result = Link.flipSet(formula.linkGenerationEcc(contexts,node));
        return result;
    }

    @Override
    public boolean affect(ContextChange change) {
        return formula.affect(change);
    }
    
	@Override
	public boolean affect(String context) {
		return formula.affect(context);
	}

    @Override
    public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change) {
        if(!affect(change)) {}
        else {
            boolean result = !(formula.evaluatePcc(contexts,node,change));
            value = result;
        }
        return value;
    }

    @Override
    public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change) {
        if(!affect(change)) {}
        else {
            ArrayList<Link> subLink = new ArrayList<Link>(formula.linkGenerationPcc(contexts,node,change));
            ArrayList<Link> result = Link.flipSet(subLink);
            link = result;
        }
        return link;
    }

    @Override
    public void setGoal(String goal) {
        goalLink = goal;
        if(!goalLink.matches("null")) {
            formula.setGoal(Boolean.toString(!Boolean.parseBoolean(goalLink)));
        }
        else {
            formula.setGoal("null");
        }
    }

	@Override
	public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node, SameContextChange group) {
		String context = group.getContext();
		if(!affect(context)) {}
        else {
            boolean result = !(formula.evaluatePcc(contexts,node,group));
            value = result;
        }
        return value;
	}

	@Override
	public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,
			SameContextChange group) {
		String context = group.getContext();
		if(!affect(context)) {}
        else {
            ArrayList<Link> subLink = new ArrayList<Link>(formula.linkGenerationPcc(contexts,node,group));
            ArrayList<Link> result = Link.flipSet(subLink);
            link = result;
        }
        return link;
	}

	@Override
	public Formula createTreeNew(HashMap<String, Context> contexts) {
		// TODO Auto-generated method stub
		UnaryFormula result = new UnaryFormula("not");
        result.setSubFormula(formula.createTreeNew(contexts));
        return result;
	}

	@Override
	public Formula formulaProcess(SameContextChange group,HashMap<String,Context> _contexts) {
		UnaryFormula result = new UnaryFormula("not");
        result.setValue(this.getValue());
        result.setLink(this.getLink());
        result.setSubFormula(new Rule("").createTreePcc(this.getFormula(),group));
        return result;
	}

}
