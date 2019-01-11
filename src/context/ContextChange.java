/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import rule.Pattern;

/**
 *
 * @author why
 * context change
 */
public class ContextChange {
	private String string;
	private int operate;
	private String contextName;
	private String key;
	private ArrayList<Object> content = new ArrayList<Object>();
	private Element element;
	
    //private int state;//safe or not(unstable patterns) 0 safe/1 unsafe
    private HashMap<String,Integer> state = new HashMap<String,Integer>();
    private LinkedList<Pattern> unsafePatterns = new LinkedList<Pattern>();//unsafe pattern list
    
    private HashMap<String,Integer> consider = new HashMap<String,Integer>();
    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(ContextChange.class.getName());
    
    public void setString(String str) {
    	string = str;
    }
    
    public void setNoNeedToConsider(String rule){
    	consider.put(rule, -1);
    }
    
    public int getConsider(String rule){
    	if(consider.containsKey(rule))
    		return consider.get(rule);
    	else 
    		return 0;
    }
    public String toString() {
    	return string;
    }
    
    public void setOperate(int op) {
        operate = op;
    }
    public void setContext(String ct) {
        contextName = ct;
    }
    public void setKey(String k) {
        key = k;
    }
    public void setElement(Element element) {
        this.element = element;
    }
    public void setContent(Object value) {
        content.add(value);
    }
    public int getOperate() {
        return operate;
    } 
    public String getContext() {
        return contextName;
    } 
    public String getKey() {
        return key;
    }
    public Element getElement() {
    	Element e = new Element();
    	e = element;
        return e;
    }
    public ArrayList<Object> getContent() {
    	ArrayList<Object> c = new ArrayList<Object>(content);
        return c;
    } 
    
    public void setState(String ruleId,int state_) {
    	state.put(ruleId,state_);
    }
    
    public int getState(String ruleId) {
    	return state.get(ruleId);
    }
    
    public void addUnsafePattern(Pattern pattern) {
    	unsafePatterns.add(pattern);
    }
    
    public LinkedList<Pattern> getUnsafePatterns() {
    	return unsafePatterns;
    }
    
    public boolean isOpposite(ContextChange change) {
    	if(key.matches(change.getKey())) {//同一个元素
			if(contextName.matches(change.getContext())) {//作用于同一个context
				if(Math.abs(operate - change.getOperate()) == 1) {
					return true;
				}
			}
		}
    	return false;
    }
    
}
