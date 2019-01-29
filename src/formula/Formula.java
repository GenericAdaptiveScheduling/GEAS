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

import context.Context;
import context.ContextChange;

/**
 *
 * @author why
 * formula
 */
public abstract class Formula {
    protected String kind;//type of formula: forall, exists, and, or, implies, bfunc
    //saving evaluated values
    protected boolean value;
    //saving links
    protected ArrayList<Link> link = new ArrayList<Link>();
    protected String goalLink;
    
    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(Formula.class.getName());

    public Formula(String fName) {
        kind = fName;
    }
    
    public String getKind() {
    	return kind;
    }
    
    public boolean getValue() {
    	return value;
    }
    
    public void setValue(boolean value) {
    	this.value = value;
    }
    
    public ArrayList<Link> getLink() {
    	return link;
    }
    
    public void setLink(ArrayList<Link> link) {
    	this.link = link;
    }
    
    public abstract boolean evaluateEcc(HashMap<String,Context> contexts,RuntimeNode node);
    
    public abstract boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change);
    public abstract boolean evaluatePcc(HashMap<String,Context> contexts,RuntimeNode node,SameContextChange group);
    
    public abstract ArrayList<Link> linkGenerationEcc(HashMap<String,Context> contexts,RuntimeNode node);
    
    public abstract ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,ContextChange change);
    public abstract ArrayList<Link> linkGenerationPcc(HashMap<String,Context> contexts,RuntimeNode node,SameContextChange group);
    
    public abstract boolean affect(ContextChange change);
    public abstract boolean affect(String context);

    public abstract void setGoal(String goal);
	public abstract Formula createTreeNew(HashMap<String, Context> contexts);

	public abstract Formula formulaProcess(SameContextChange group,HashMap<String,Context> contexts);
}
