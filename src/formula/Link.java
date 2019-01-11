/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package formula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import context.Element;

/**
 *
 * @author why
 * link for inconsistency
 */
public class Link {
    private boolean violated;
    private HashMap<String,Element> binding = new HashMap<String, Element>();
    
    public void setViolated(boolean violated) {
    	this.violated = violated;
    }
    
    public boolean getViolated() {
    	return violated;
    }
    
    public void setBinding(HashMap<String,Element> binding) {
    	this.binding = binding;
    }
    
    public HashMap<String,Element> getBinding() {
    	return binding;
    }
    public boolean containElement(Element a) {
		if(binding.containsValue(a))
			return true;
		else 
			return false;
	}
    public Link flip() {
        Link newLink = new Link();
        newLink.violated = !violated;
        newLink.binding = binding;
        return newLink;
    }
    
    public Link cartesian(Link l) {
        Link newLink = new Link();
        newLink.violated = violated;
        newLink.binding.putAll(binding);
        newLink.binding.putAll(l.binding);
        return newLink;
    }
    
    public static ArrayList<Link> flipSet(ArrayList<Link> l) {
        ArrayList<Link> temp = new ArrayList<Link>(l);
        for(int i = 0;i < temp.size();i++) {
            Link flip = temp.get(i).flip();
            temp.remove(i);
            temp.add(i,flip);
        }
        return temp;
    }
    
    public static ArrayList<Link> cartesianList(ArrayList<Link> l1,ArrayList<Link> l2) {
        ArrayList<Link> result = new ArrayList<Link>();
        if(l1.isEmpty())
            return l2;
        if(l2.isEmpty())
            return l1;
        for(int i = 0;i < l1.size();i++) {
            for(int j = 0;j < l2.size();j++) {
                result.add(l1.get(i).cartesian(l2.get(j)));
            }
        }
        return result;
    }
    
    public static ArrayList<Link> cartesianList(Link l1,ArrayList<Link> l2) {
        ArrayList<Link> result = new ArrayList<Link>();
        if(l2.isEmpty()) {
            result.add(l1);
            return result;
        }
        for(int j = 0;j < l2.size();j++) {
            result.add(l1.cartesian(l2.get(j)));
        }
        return result;
    }
    
    public static ArrayList<Link> union(ArrayList<Link> l1,ArrayList<Link> l2) {
        ArrayList<Link> temp = new ArrayList<Link>(l1);
        if(l1 == null)
            return l2;
        else if(l2 == null)
            return l1;
        else
            temp.addAll(l2);
        return temp;
    }
    
	@SuppressWarnings("rawtypes")
	@Override
    public String toString() {
        String str = new String();
        str += "(";
        if(violated == true)
            str += "violated";
        else
            str += "satisfy";
        str += ",{";
        for (Map.Entry entry : binding.entrySet()) {
            str += "(";
            str += entry.getKey();
            str += ",";
            str += ((Element)entry.getValue()).toString();
            str += ")";
            str += ",";
        }
        str = str.substring(0,str.length()-1);
        str += "})";
        return str;
    }
    
	@SuppressWarnings("rawtypes")
	public boolean equals(Link link) {
        if(violated != link.violated) {
            return false;
        }
        else if(binding.size() != link.binding.size()) {
            return false;
        }
        else {
            for (Map.Entry entry : binding.entrySet()) {
                String name = (String)entry.getKey();
                Element content = (Element)entry.getValue();
                if(!link.binding.containsKey(name)) {
                    return false;
                }
                else {
                    if(!link.binding.get(name).equals(content))
                        return false;
                }
            }
        }
        return true;
    }
}
