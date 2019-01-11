/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package context;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 *
 * @author why
 * element in each context
 */
public class Element {
	private String key;//主键
	private HashMap<String,Object> fields = new HashMap<String,Object>();//各个field以及其对应的值
	private String context;//属于哪个context
    
	private int state;//undecided,consistent,bad,inconsistent
    
    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(Element.class.getName());
    
    public Element() {
        state = -1;
    }
    public Element( Element a ){
    	this.key = a.key;
    	this.fields = a.fields;//(HashMap<String, Object>) a.fields.clone();
    	this.context = a.context;
    }
    public HashMap<String,Object> getFields() {
    	return fields;
    }
    public String get(String field) {
        return (String)fields.get(field);
    }
    
    public void add_field(String fieldName,Object value) {
        fields.put(fieldName,value);
    }
    public void setKey(String key) {
        this.key = key;
    }
    public String getKey() {
        return key;
    }
    //public String getFields1() {
    ///    return fields.toString();
    //}
    public void setContext(String context) {
        this.context = context;
    }
    public String getContext() {
        return context;
    }
    public void setState(int state) {
        this.state = state;
    }
    public int getState() {
        return state;
    }
    
    public boolean equals(Object arg0) {
        /*if(!(fields.equals(e.fields)))
        	return false;*/
    	if(key.matches(((Element)arg0).key))
    		return true;
        return false;
    }
    
    @Override
    public String toString() {
        String str = new String();
        str += " ";
        str += fields.get("id");
        return str;
    }
}
