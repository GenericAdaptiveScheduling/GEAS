/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package context;


import java.security.KeyStore.Entry;
import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

//import com.sun.jndi.url.iiopname.iiopnameURLContextFactory;



/**
 *
 * @author why
 * one kind of contexts
 */
public class Context {
    public String name;
    private ArrayList<String> fields = new ArrayList<String>();
    private HashMap<String,Element> elements = new HashMap<String, Element>();
    
    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(Context.class.getName());
    
    public Context(String contextsName) {
        name = contextsName;
    }
    
    public int getSize() {//该类contexts中context的个数
    	return elements.size();
    }
    
    public String getContextname() {
    	return name;
    }
    
    public ArrayList<String> getFields() {
    	return fields;
    }
    
    public HashMap<String,Element> getElements() {
    	return elements;
    }
    
    public void addField(String field) {
        fields.add(field);
    }
    
    public void addElement (String key,Element e) {
    	Element temp = new Element(e);
        elements.put(key,temp);
    }
    
    public boolean deleteElement(String key) {
        if(elements.get(key) != null) {
            elements.remove(key);
            return true;
        }
        else
            return false;
    }
    public void renameChange(ContextChange a, ContextChange b){
    	if(!elements.isEmpty()){
    		//System.out.println("Before:"+elements.size()+elements.toString());
	    	Element temp = elements.get(a.getKey());
	    	
	    	if(temp != null) {
	    		elements.remove(a.getKey());
	    		elements.put(b.getKey(), b.getElement());
		    	temp.setKey(b.getKey());
		    	temp.setContext(b.getContext());
		    	temp.setState(b.getElement().getState());//?
		    	HashMap<String,Object> fields = temp.getFields();
		    	for (java.util.Map.Entry<String, Object> entry : fields.entrySet()) { 
		    		//System.out.println("entry field -"+entry.getKey() +"- from "+entry.getValue()+" to "+b.get(entry.getKey()));
		    		entry.setValue(b.getElement().get(entry.getKey()));
		    		
		    	}
	    	}
	    	else {
	    		;//System.out.println("null now");
	    	}
	    	//System.out.println("After:"+elements.toString());
	    }
    	else {
    		System.out.println("Elements are empty now");
    	}
    }
    public void renameElement(Element del, Element add){
    	//System.out.println("Rename: "+a.getKey()+" to "+b.getKey()+"|||||||||||"+a.getContext());
    	if(!elements.isEmpty()){
    		//System.out.println("Before:"+elements.size()+elements.toString());
	    	Element temp = elements.get(del.getKey());
	    	
	    	if(temp != null) {
	    		elements.remove(del.getKey());
	    		addElement(add.getKey(), add);
		    	
	    	}
	    	else {
	    		;//System.out.println("null now");
	    	}
	    	//System.out.println("After:"+elements.toString());
	    }
    	else {
    		System.out.println("Elements are empty now");
    	}
    }
   
    @Override
    public String toString() {
        String str = new String();
        str += " ";
        str += name;
        return str;
    }
}
