/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package formula;

import java.util.HashMap;

import org.omg.PortableServer.ID_ASSIGNMENT_POLICY_ID;

import context.Element;

/**
 *
 * @author why
 *  for saving arguments (actual parameters) to evaluate the whole CCT
 */
public class RuntimeNode {
    private HashMap<String, Element> varEnv;
    
    public RuntimeNode() {
        varEnv = new HashMap<String, Element>();
    }
    
    public RuntimeNode(RuntimeNode node) {
        varEnv = new HashMap<String,Element>(node.varEnv);
    }
    
    public HashMap<String, Element> getVar() {
        return varEnv;
    }
    
    public void setVar(String variable,Element element) {
    	//if(!varEnv.containsKey(variable))
    		varEnv.put(variable,element);
    	//else {
    	//	varEnv.put(variable+"?",element);
		//}
    }
    
    public void deleteVar(String variable) {
        varEnv.remove(variable);
    }
}
