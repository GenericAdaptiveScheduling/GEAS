/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package formula;

import java.util.ArrayList;
import java.util.HashMap;

import middleware.SameContextChange;
import context.Context;
import context.ContextChange;

/**
 *
 * @author why
 * for each element in the context in evaluating the CCT
 */
public class SubNode extends Formula{
    private Formula subFormula;

    public SubNode(String name) {
        super(name);//
    }
    
    public void setKind(String kind){
    	this.kind = kind;
    }
    public void setFormula(Formula subFormula) {
    	this.subFormula = subFormula;
    }
    
    public Formula getFormula() {
    	return subFormula;
    }
    
    @Override
    public boolean evaluateEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        return subFormula.evaluateEcc(contexts,node);
    }

    @Override
    public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node, ContextChange change) {
        return subFormula.evaluatePcc(contexts,node,change);
    }

	@Override
	public boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node, SameContextChange group) {
		return subFormula.evaluatePcc(contexts,node,group);
	}
    
    @Override
    public ArrayList<Link> linkGenerationEcc(HashMap<String,Context> contexts,RuntimeNode node) {
        return subFormula.linkGenerationEcc(contexts,node);
    }

    @Override
    public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node, ContextChange change) {
        return subFormula.linkGenerationPcc(contexts,node,change);
    }

	@Override
	public ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,
			SameContextChange group) {
		return subFormula.linkGenerationPcc(contexts,node,group);
	}

    @Override
    public boolean affect(ContextChange change) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setGoal(String goal) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

	@Override
	public boolean affect(String context) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	public String toString(){
		return "<"+this.subFormula.getKind()+","+this.subFormula.getValue()+">";
	}

	@Override
	public Formula createTreeNew(HashMap<String, Context> contexts) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Formula formulaProcess(SameContextChange group,HashMap<String,Context> _contexts) {
		// TODO Auto-generated method stub
		return null;
	}
}
