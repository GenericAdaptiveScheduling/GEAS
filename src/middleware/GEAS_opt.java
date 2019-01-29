package middleware;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.text.ChangedCharSetException;

import org.omg.CORBA.portable.ValueBase;

import rule.Rule;
import context.Context;
import context.ContextChange;
import context.Element;
import dataLoader.Demo;
import formula.AndFormula;
import formula.BFunc;
import formula.ExistsFormula;
import formula.ForallFormula;
import formula.Formula;
import formula.ImpliesFormula;
import formula.Link;
import formula.OrFormula;
import formula.RuntimeNode;
import formula.SubNode;
import formula.UnaryFormula;
/**
*
* @author why
* implementation of the GEAS-opt scheduling strategy
*/
public class GEAS_opt  extends Checker{
	
		public static int checktime = 0;
		public static LinkedList<String> goalValue = new LinkedList<String>();
		public ArrayList<Boolean> bfuncValueList = new ArrayList<Boolean>();
		public ArrayList<Boolean> bfuncValueList2 = new ArrayList<Boolean>();
		public boolean flag = false;
		
		
		
		public void doCheck() throws Exception {
	        ContextChange change = new ContextChange();
	        Demo demo = new Demo(in);
	        //int flagINC = 0;
	        int line = -1;
	    	while(demo.hasNextChange()) {//handling all changes
	            change = demo.nextChange();      
	            line++;
	            for(int i = 0;i < rules.size();i++) {//handling all constraints
	            	Rule rule = rules.get(i);          	
	            	//if(!(rule.getId().equalsIgnoreCase("rule_in_AC")))
	            	//	continue;         	
	            	
	            	LinkedList<ContextChange> listBuffer = rule.getBuffer();
	            	
	            	
	            	String ruleId = rule.getId();
	                if(rule.affect(change)) {
	                	
						long start = Calendar.getInstance().getTimeInMillis();
	                	 	
		    	    	if(rule.match_chgBuffer(change)||rule.match_plusBuffer(change)) {//being inc-, and 
		    	    		checkNum++;
		        	    	rule.record(listBuffer.size());
		    	    		long bs = Calendar.getInstance().getTimeInMillis();
		    	    		rule.setBuffer_(BatchChecker.filter(rule.getBuffer()));
		    	    		int linkbefore = nLinks;
		    	    		if(technique.matches("ECC")) {
		    	    			nLinks += rule.Ecc(out);
		    	    		}
		    	    		if(technique.matches("PCC")) {
		    	    			nLinks += rule.Pcc(out);
		    	    		}
		    	    		long sTime = Calendar.getInstance().getTimeInMillis() - bs;
		    	    		rule.addTime(sTime);
		    	    		pTime += sTime;
		    	    		rule.clearBuffer();
		    	    		//System.out.println("clear buffer...."+ruleId);
		    	    		rule.clearplusBuffer();
		    	    		//out.write(("Check for pat_A"+rule.getContexts().get("pat_A").getElements().toString()+"\n").getBytes());
		    	    		//out.write(("Check for pat_C"+rule.getContexts().get("pat_C").getElements().toString()+"\n").getBytes());
		    	    		
		    	    	}
		    	    	else {
		    	    		;
		    	    	}
		    	    	
		    	    	rule.handleSelf(change);
		    	    		if(change.getState(ruleId)==1){//&&(ruleId.startsWith("cst_loc")||ruleId.startsWith("rule_"))){//being inc+
		    	    			//flagINC = 1;
		    	    			if((!rule.getBuffer().isEmpty())){
				    	    		ContextChange change2;
				    	    		int flag = 0;
				    	    		int k=0;
				    	    		for(;k<listBuffer.size();k++)//= rule.getBuffer().get(rule.getBuffer().size()-1);
				    	    		{
				    	    			change2 = listBuffer.get(k);
					    	    		if(change2.getState(ruleId)==0){//&&change.getState(ruleId)==1
					    	    			if(change2.getContext().equals(change.getContext())){
						    	    			if(isEffectCancellableEvaluated(rule, change.getContext(), change2, change)){    					
						    	    				listBuffer.remove(k);
						    	    				
						    	    				flag = 1;
							    	    			break;
						    	    			}
						    	    			
					    	    			}
					    	    		}
				    	    		}
				    	    		if(flag == 0){
				    	    			rule.setBuffer(change);
				    	    		}
			    	    		}
		    	    			else{ //batch is empty
		        	    			rule.setBuffer(change);
		        	    			//areCCRsEmpty.set(i, 1);
		        	    		}
		    	    		}
		    	    		else{ //not inc+ change now
		    	    			rule.setBuffer(change);
		    	    		}
		    	    	
	    	    		long end = Calendar.getInstance().getTimeInMillis();
	    	            allTime += end - start;
	    	            
	                }
	            }
	            
	        }
	    }

		private boolean isEffectCancellableEvaluated(Rule rule, String context, ContextChange change1, ContextChange change2){
			
			//System.out.println("Evaluating ruleID: "+ rule.getId());
			
			Element deleElement;
			Element addElement;
			
			if(change1.getOperate()==2)
			{//-,+
				deleElement = change1.getElement();
				addElement = change2.getElement();
			}
			else {//+,-
				deleElement = change2.getElement();
				addElement = change1.getElement();
			}
			
			if(!islLinkAssociated(rule,context,deleElement,addElement)){
				ArrayList<Boolean> addValue = new ArrayList<Boolean>();
				ArrayList<Boolean> delValue = new ArrayList<Boolean>();
				//System.out.println("pat_A1:"+rule.getContexts().get("pat_A").getElements().toString());
				//System.out.println("pat_C1:"+rule.getContexts().get("pat_C").getElements().toString());
				
				RuntimeNode nodeRun = new RuntimeNode();
				bfuncValueList.clear();
				retBfunValuesDel(rule, rule.getFormula(),context,deleElement,addElement, nodeRun, true);
				delValue = (ArrayList<Boolean>) bfuncValueList.clone();
				
				if(delValue.isEmpty())
					return false;
				
				nodeRun = new RuntimeNode();
				//System.out.println("delValue: "+delValue.toString());
				bfuncValueList.clear();
				retBfunValuesAdd(rule, rule.getFormula(),context,deleElement,addElement, nodeRun, false);
				addValue = (ArrayList<Boolean>) bfuncValueList.clone();
				if(!delValue.equals(addValue)){
					sideEffectResolution(rule, rule.getFormula(),context,addElement,deleElement);
					return false;
				}
				else return true;
			}
			return false;
		}
/*		private void isEqualPattern(ArrayList<Boolean> addValue, ArrayList<Boolean> delValue){
			
		}*/
		private void sideEffectResolution(Rule rule, Formula formula, String contextName,Element deleElement, Element addElement) {
			/*if(technique.matches("ECC")){
				rule.getContexts().get(contextName).renameElement(deleElement, addElement);
				return;
			}
			else if(technique.matches("PCC")){*/
				if(formula.getKind().matches("forall")) {
					ForallFormula forallFormula = (ForallFormula)formula;
					if((forallFormula).getContext().equals(contextName)){
						LinkedList<SubNode> elementList = (forallFormula).getSubnodes();
						for(int i = 0; i< elementList.size(); i++){
							SubNode node = elementList.get(i);
							//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();

							//Context context = rule.getContexts().get(contextName);
							//System.out.println(contextName+"before:"+context.getElements().toString());
							rule.getContexts().get(contextName).renameElement(deleElement, addElement);
							//System.out.println(contextName+"after"+context.getElements().toString());
								
							//System.out.println(deleElement.getKey());
								
							if(node.getKind().equals(deleElement.getKey())){
							    node.setKind(addElement.getKey());
							    //System.out.println(deleElement.getKey()+"=---"+addElement.getKey());
							    break;	
							}
						}
					}
					else {
						LinkedList<SubNode> elementList = (forallFormula).getSubnodes();
						for(int i = 0; i< elementList.size(); i++){
							SubNode node = elementList.get(i);
							sideEffectResolution(rule, node.getFormula(), contextName, deleElement, addElement);
						}
					}
				}
				else if(formula.getKind().matches("exists")){
					ExistsFormula existsFormula = (ExistsFormula)formula;
					if((existsFormula).getContext().equals(contextName)){
						LinkedList<SubNode> elementList = (existsFormula).getSubnodes();
						for(int i = 0; i< elementList.size(); i++){
							SubNode node = elementList.get(i);
							//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
								
							//Context context = rule.getContexts().get(contextName);
							//System.out.println(contextName+"before:"+context.getElements().toString());
							rule.getContexts().get(contextName).renameElement(deleElement, addElement);
							//System.out.println(contextName+"after"+context.getElements().toString());

							if(node.getKind().equals(deleElement.getKey())){
							    node.setKind(addElement.getKey());
							    //System.out.println(deleElement.getKey()+"=---"+addElement.getKey());
							    break;	
							}
						}
					}
					else {
						LinkedList<SubNode> elementList = (existsFormula).getSubnodes();
						for(int i = 0; i< elementList.size(); i++){
							SubNode node = elementList.get(i);
							sideEffectResolution(rule, node.getFormula(), contextName, deleElement, addElement);
						}
					}
				}
				else if(formula.getKind().matches("not")){
					UnaryFormula unaryFormula = (UnaryFormula)formula;
					sideEffectResolution(rule, (unaryFormula).getFormula(), contextName, deleElement, addElement);
				}
				else if(formula.getKind().matches("and")){
					AndFormula andFormula = (AndFormula)formula;
					sideEffectResolution(rule, (andFormula).getFirst(),contextName, deleElement, addElement);
					sideEffectResolution(rule, (andFormula).getSecond(),contextName, deleElement, addElement);
				}
				else if(formula.getKind().matches("or")){
					OrFormula orFormula = (OrFormula)formula;
					sideEffectResolution(rule, (orFormula).getFirst(),contextName, deleElement, addElement);
					sideEffectResolution(rule, (orFormula).getSecond(),contextName, deleElement, addElement);
				}
				else if(formula.getKind().matches("implies")){
					ImpliesFormula impliesFormula = (ImpliesFormula)formula;
					sideEffectResolution(rule, (impliesFormula).getFirst(),contextName, deleElement, addElement);
					sideEffectResolution(rule, (impliesFormula).getSecond(),contextName, deleElement, addElement);
				}
			
		}
		private void retBfunValuesAdd(Rule rule, Formula formula, String contextName,Element deleElement, Element addElement, RuntimeNode nodeRun, boolean del_add) {
			// TODO Auto-generated method stub
			//RuntimeNode runNode = new RuntimeNode();
			if(formula.getKind().matches("forall")) {
				ForallFormula forallFormula = (ForallFormula)formula;
				if(forallFormula.getContext().equals(contextName)){
					LinkedList<SubNode> elementList = (forallFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
							
							Element sc;
							if(del_add == true)//evaluate for deletion change
								sc = map.get(node.getKind());
							else 
								sc = addElement;
							node.setKind(addElement.getKey());
					        String variable = (forallFormula).getVariable();
					        rule.changeContext(contextName, deleElement, addElement);
					        nodeRun.setVar(((ForallFormula)formula).getVariable(),sc);
					        boolean result = node.evaluateEcc(rule.getContexts(), nodeRun);
					        bfuncValueList.add(result);
					        nodeRun.deleteVar(variable);
					        ////System.out.println("setVar:"+variable+"---"+sc.toString());
					        /*if(sc!=null){
						        nodeRun.setVar(variable,sc);
						        retBfunValuesAdd(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
								nodeRun.deleteVar(variable);
					        }*/
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = (forallFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						Element sc = rule.getContexts().get((forallFormula).getContext()).getElements().get(node.getKind());

					    String variable = (forallFormula).getVariable();
					    ////System.out.println("setVar:"+variable+"---"+sc.toString());
					    if(sc!=null){
						    nodeRun.setVar(variable,sc);
						    retBfunValuesAdd(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
							nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("exists")){
				ExistsFormula existsFormula = (ExistsFormula)formula;
				if((existsFormula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = (existsFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
							Element sc;
							if(del_add == true)//evaluate for deletion change
								sc = map.get(node.getKind());
							else 
								sc = addElement;
							node.setKind(addElement.getKey());
					        String variable = (existsFormula).getVariable();
					        rule.changeContext(contextName, deleElement, addElement);
					        nodeRun.setVar(((ExistsFormula)formula).getVariable(),sc);
					        boolean result = node.evaluateEcc(rule.getContexts(), nodeRun);
					        bfuncValueList.add(result);
					        nodeRun.deleteVar(variable);
					        /*if(sc!=null){
						        nodeRun.setVar(variable,sc);
						        retBfunValuesAdd(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
								nodeRun.deleteVar(variable);
					        }*/
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = (existsFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						Element sc = rule.getContexts().get((existsFormula).getContext()).getElements().get(node.getKind());

					    String variable = (existsFormula).getVariable();
					    if(sc!=null){
						    //System.out.println("setVar:"+variable+"---"+sc.toString());
						    nodeRun.setVar(variable,sc);
						    retBfunValuesAdd(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
							nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("not")){
				UnaryFormula unaryFormula = (UnaryFormula)formula;
				retBfunValuesAdd(rule, (unaryFormula).getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("and")){
				AndFormula andFormula = (AndFormula)formula;
				retBfunValuesAdd(rule, (andFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValuesAdd(rule, (andFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("or")){
				OrFormula orFormula = (OrFormula)formula;
				retBfunValuesAdd(rule, (orFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValuesAdd(rule, (orFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("implies")){
				ImpliesFormula impliesFormula = (ImpliesFormula)formula;
				retBfunValuesAdd(rule, (impliesFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValuesAdd(rule, (impliesFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else{
				//System.out.println("Bfunc value evaluating..."+formula.getKind());
				//System.out.println(nodeRun.getVar().toString());
				
				//if(del_add==true)
				//	bfuncValueList.add(((BFunc)formula).getValue());
				//else {
					boolean value = ((BFunc)formula).evaluateEcc(rule.getContexts(), nodeRun);
					bfuncValueList.add(value);
				//}
			}
		}
		private void retBfunValuesDel(Rule rule, Formula formula, String contextName,Element deleElement, Element addElement, RuntimeNode nodeRun, boolean del_add) {
			// TODO Auto-generated method stub
			//RuntimeNode runNode = new RuntimeNode();
			if(formula.getKind().matches("forall")) {
				ForallFormula forallFormula = (ForallFormula)formula;
				if(forallFormula.getContext().equals(contextName)){
					LinkedList<SubNode> elementList = (forallFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
							Element sc;
							if(del_add == true)//evaluate for deletion change
								sc = map.get(node.getKind());
							else 
								sc = addElement;
					        String variable = (forallFormula).getVariable();
					        ////System.out.println("setVar:"+variable+"---"+sc.toString());
					        boolean value = node.getValue();
					        bfuncValueList.add(value);
					        /*if(sc!=null){
						        nodeRun.setVar(variable,sc);
						        retBfunValuesDel(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
								nodeRun.deleteVar(variable);
					        }*/
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = (forallFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						Element sc = rule.getContexts().get((forallFormula).getContext()).getElements().get(node.getKind());

					    String variable = (forallFormula).getVariable();
					    ////System.out.println("setVar:"+variable+"---"+sc.toString());
					    if(sc!=null){
						    nodeRun.setVar(variable,sc);
						    retBfunValuesDel(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
							nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("exists")){
				ExistsFormula existsFormula = (ExistsFormula)formula;
				if((existsFormula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = (existsFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
							Element sc;
							if(del_add == true)//evaluate for deletion change
								sc = map.get(node.getKind());
							else 
								sc = addElement;
							boolean value = node.getValue();
					        bfuncValueList.add(value);
					        String variable = (existsFormula).getVariable();
					        
					        /*if(sc!=null){
						        nodeRun.setVar(variable,sc);
						        retBfunValuesDel(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
								nodeRun.deleteVar(variable);
					        }*/
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = (existsFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						Element sc = rule.getContexts().get((existsFormula).getContext()).getElements().get(node.getKind());

					    String variable = (existsFormula).getVariable();
					    if(sc!=null){
						    //System.out.println("setVar:"+variable+"---"+sc.toString());
						    nodeRun.setVar(variable,sc);
						    retBfunValuesDel(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
							nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("not")){
				UnaryFormula unaryFormula = (UnaryFormula)formula;
				retBfunValuesDel(rule, (unaryFormula).getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("and")){
				AndFormula andFormula = (AndFormula)formula;
				retBfunValuesDel(rule, (andFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValuesDel(rule, (andFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("or")){
				OrFormula orFormula = (OrFormula)formula;
				retBfunValuesDel(rule, (orFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValuesDel(rule, (orFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("implies")){
				ImpliesFormula impliesFormula = (ImpliesFormula)formula;
				retBfunValuesDel(rule, (impliesFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValuesDel(rule, (impliesFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
		}
		private void retBfunValues(Rule rule, Formula formula, String contextName,Element deleElement, Element addElement, RuntimeNode nodeRun, boolean del_add) {
			// TODO Auto-generated method stub
			//RuntimeNode runNode = new RuntimeNode();
			if(formula.getKind().matches("forall")) {
				ForallFormula forallFormula = (ForallFormula)formula;
				if(forallFormula.getContext().equals(contextName)){
					LinkedList<SubNode> elementList = (forallFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
							Element sc;
							if(del_add == true)//evaluate for deletion change
								sc = map.get(node.getKind());
							else 
								sc = addElement;
					        String variable = (forallFormula).getVariable();
					        ////System.out.println("setVar:"+variable+"---"+sc.toString());
					        if(sc!=null){
						        nodeRun.setVar(variable,sc);
						        retBfunValues(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
								nodeRun.deleteVar(variable);
					        }
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = (forallFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						Element sc = rule.getContexts().get((forallFormula).getContext()).getElements().get(node.getKind());

					    String variable = (forallFormula).getVariable();
					    ////System.out.println("setVar:"+variable+"---"+sc.toString());
					    if(sc!=null){
						    nodeRun.setVar(variable,sc);
						    retBfunValues(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
							nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("exists")){
				ExistsFormula existsFormula = (ExistsFormula)formula;
				if((existsFormula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = (existsFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
							Element sc;
							if(del_add == true)//evaluate for deletion change
								sc = map.get(node.getKind());
							else 
								sc = addElement;
					        String variable = (existsFormula).getVariable();
					        
					        if(sc!=null){
						        nodeRun.setVar(variable,sc);
						        retBfunValues(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
								nodeRun.deleteVar(variable);
					        }
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = (existsFormula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						Element sc = rule.getContexts().get((existsFormula).getContext()).getElements().get(node.getKind());

					    String variable = (existsFormula).getVariable();
					    if(sc!=null){
						    //System.out.println("setVar:"+variable+"---"+sc.toString());
						    nodeRun.setVar(variable,sc);
						    retBfunValues(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
							nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("not")){
				UnaryFormula unaryFormula = (UnaryFormula)formula;
				retBfunValues(rule, (unaryFormula).getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("and")){
				AndFormula andFormula = (AndFormula)formula;
				retBfunValues(rule, (andFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValues(rule, (andFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("or")){
				OrFormula orFormula = (OrFormula)formula;
				retBfunValues(rule, (orFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValues(rule, (orFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("implies")){
				ImpliesFormula impliesFormula = (ImpliesFormula)formula;
				retBfunValues(rule, (impliesFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValues(rule, (impliesFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else{
				//System.out.println("Bfunc value evaluating..."+formula.getKind());
				//System.out.println(nodeRun.getVar().toString());
				
				//if(del_add==true)
				//	bfuncValueList.add(((BFunc)formula).getValue());
				//else {
					boolean value = ((BFunc)formula).evaluateEcc(rule.getContexts(), nodeRun);
					bfuncValueList.add(value);
				//}
			}
		}

}