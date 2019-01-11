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
* implementation of GEAS-opt scheduling strategy
*/
public class GEAS_opt  extends Checker{
	
		public static int checktime = 0;
		public static LinkedList<String> goalValue = new LinkedList<String>();
		public ArrayList<Boolean> bfuncValueList = new ArrayList<Boolean>();
		public ArrayList<Boolean> bfuncValueList2 = new ArrayList<Boolean>();
		public boolean flag = false;
		
		public void setOut() throws IOException {
	    	outFile = id + "_" + technique + "_" + strategy;
	    	File file = new File("data/out/" + outFile + ".txt");
	        // if file doesnt exists, then create it
	        if(!file.getParentFile().exists()) {
	        	file.getParentFile().mkdirs();
	        }
	    	if (!file.exists()) {
	        	file.createNewFile();
	        }
			out = new FileOutputStream(file);
		}
		
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
	                	
	               // out.write(("ruleID:"+rule.getId()+"\n"+"Buffer:"+listBuffer.toString()+"\n").getBytes());
	                	////System.out.println("Rule:"+rule.getId()+" Context:"+change.toString());
	                	//out.write(("Rule:"+rule.getId()+" Context:"+change.toString()+"\n").getBytes());
	                	////System.out.println(rule.getId());
	                	
	                	////System.out.println("Change:"+change.toString());
	                	long start = Calendar.getInstance().getTimeInMillis();
	                	//out.write(("ruleID:"+i+"\n").getBytes());
	                	  	
		    	    	if(rule.match_chgBuffer(change)||rule.match_plusBuffer(change)) {//being inc-, and s-condition is matched now
		    	    		////System.out.println("Clear buffer:"+listBuffer.toString());
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
		    	    		;//out.write(("Not match: "+ change.toString()+"\n").getBytes());
		    	    		////System.out.println("Not match: "+ change.toString());
		    	    		
		    	    	}
		    	    	//out.write(("Rule:"+rule.getId()+" Context:"+change.toString()+"\n").getBytes());
		    	    	//System.out.println("Rule:"+ rule.getLink().size()+":"+rule.getLink().toString());
		    	    	rule.handleSelf(change);
	    	    		//out.write(("change type:"+change.getState(rule.getId())+change.getContext()+" "+change.getElement().toString()
	    	    		//		+" "+change.getKey()+"\n").getBytes());//+" "+change.getContent().toString()
		    	    		if(change.getState(ruleId)==1){//&&(ruleId.startsWith("cst_loc")||ruleId.startsWith("rule_"))){//being inc+
		    	    			//flagINC = 1;
		    	    			if((!rule.getBuffer().isEmpty())){//&&areCCRsEmpty.get(i)==0){//batch²»Îª¿Õ,CCRÎª¿Õ
				    	    		ContextChange change2;
				    	    		int flag = 0;
				    	    		int k=0;
				    	    		for(;k<listBuffer.size();k++)//= rule.getBuffer().get(rule.getBuffer().size()-1);
				    	    		{
				    	    			change2 = listBuffer.get(k);
					    	    		if(change2.getState(ruleId)==0){//&&change.getState(ruleId)==1
					    	    			if(change2.getContext().equals(change.getContext())){//&&change2.getKey().equals(change.getKey())){
					    	    				//System.out.println("Evaluating "+change2.toString()+"---"+change.toString());
						    	    			if(isEffectCancellableEvaluated(rule, change.getContext(), change2, change)){    					
						    	    				listBuffer.remove(k);
						    	    				
						    	    				//out.write(("Evaluating "+change2.toString()+"---"+change.toString()+"\n").getBytes());
						    	    				//System.out.println("evaluate true");
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
				//System.out.println("addValue: "+addValue.toString());
				//System.out.println("pat_A2:"+rule.getContexts().get("pat_A").getElements().toString());
				//System.out.println("pat_C2:"+rule.getContexts().get("pat_C").getElements().toString());
				if(!delValue.equals(addValue)){
					
					//System.out.println("is not equal:"+delValue.toString()+addValue.toString());
					//cancellable, side-effect resolution
					sideEffectResolution(rule, rule.getFormula(),context,addElement,deleElement);
					//System.out.println("replace: "+deleElement.toString()+"----"+addElement.toString());
					//System.out.println("pat_A:"+rule.getContexts().get("pat_A").getElements().toString());
					//System.out.println("pat_C:"+rule.getContexts().get("pat_C").getElements().toString());
					return false;
				}
				else return true;
			}
			return false;
		}
/*		private void isEqualPattern(ArrayList<Boolean> addValue, ArrayList<Boolean> delValue){
			
		}*/
		private void sideEffectResolution(Rule rule, Formula formula, String contextName,Element deleElement, Element addElement) {
			// TODO Auto-generated method stub
			//RuntimeNode runNode = new RuntimeNode();
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
/*
		private void retBfunValues2(Rule rule, Formula formula, String contextName,Element deleElement, Element addElement, RuntimeNode nodeRun, boolean del_add) {
			// TODO Auto-generated method stub
			//RuntimeNode runNode = new RuntimeNode();
			if(formula.getKind().matches("forall")) {
				if(((ForallFormula)formula).getContext().equals(contextName)){
					flag = true;
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
							Element sc;
							if(del_add == true)//evaluate for deletion change
								sc = map.get(node.getKind());
							else 
								sc = addElement;
					        String variable = ((ForallFormula)formula).getVariable();
					        ////System.out.println("setVar:"+variable+"---"+sc.toString());
					        if(sc!=null){
						        nodeRun.setVar(variable,sc);
						        boolean temp = node.evaluateEcc(rule.getContexts(), nodeRun);
								nodeRun.deleteVar(variable);
								if(flag == true)
									bfuncValueList.add(temp);
					        }
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					boolean temp = true;
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						Element sc = rule.getContexts().get(((ForallFormula)formula).getContext()).getElements().get(node.getKind());

					    String variable = ((ForallFormula)formula).getVariable();
					    ////System.out.println("setVar:"+variable+"---"+sc.toString());
					    if(sc!=null){
						    nodeRun.setVar(variable,sc);
						    retBfunValues2(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
							nodeRun.deleteVar(variable);
							
					    }
					}
					if(flag == true)
						bfuncValueList.add(temp);
				}
			}
			else if(formula.getKind().matches("exists")){
				if(((ExistsFormula)formula).getContext().equals(contextName)){
					flag = true;
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
							Element sc;
							if(del_add == true)//evaluate for deletion change
								sc = map.get(node.getKind());
							else 
								sc = addElement;
					        String variable = ((ExistsFormula)formula).getVariable();
					        ////System.out.println("setVar:"+variable+"---"+sc.toString());
					        if(sc!=null){
						        nodeRun.setVar(variable,sc);
						        boolean temp = node.evaluateEcc(rule.getContexts(), nodeRun);
								nodeRun.deleteVar(variable);
								if(flag == true)
									bfuncValueList.add(temp);
					        }
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					boolean temp = false;
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						//HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						Element sc = rule.getContexts().get(((ExistsFormula)formula).getContext()).getElements().get(node.getKind());

					    String variable = ((ExistsFormula)formula).getVariable();
					    if(sc!=null){
						    //System.out.println("setVar:"+variable+"---"+sc.toString());
						    nodeRun.setVar(variable,sc);
						    retBfunValues2(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
							nodeRun.deleteVar(variable);
							
					    }
					}
					if(flag == true)
						bfuncValueList.add(temp);
				}
			}
			else if(formula.getKind().matches("not")){
				UnaryFormula unaryFormula = (UnaryFormula)formula;
				retBfunValues2(rule, (unaryFormula).getFormula(), contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("and")){
				AndFormula andFormula = (AndFormula)formula;
				retBfunValues2(rule, (andFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValues2(rule, (andFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("or")){
				OrFormula orFormula = (OrFormula)formula;
				retBfunValues2(rule, (orFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValues2(rule, (orFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}
			else if(formula.getKind().matches("implies")){
				ImpliesFormula impliesFormula = (ImpliesFormula)formula;
				retBfunValues2(rule, (impliesFormula).getFirst(),contextName, deleElement, addElement, nodeRun, del_add);
				retBfunValues2(rule, (impliesFormula).getSecond(),contextName, deleElement, addElement, nodeRun, del_add);
			}

		}*/
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
/*		private void retBfunValuesDel(Rule rule, Formula formula, String contextName,Element deleElement, Element addElement, RuntimeNode nodeRun) {
			// TODO Auto-generated method stub
			//RuntimeNode runNode = new RuntimeNode();
			if(formula.getKind().matches("forall")) {
				if(((ForallFormula)formula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
					        Element sc = map.get(node.getKind());
					        String variable = ((ForallFormula)formula).getVariable();
					        ////System.out.println("setVar:"+variable+"---"+sc.toString());
					        if(sc!=null){
					        nodeRun.setVar(variable,sc);
							retBfunValuesDel(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun);
							nodeRun.deleteVar(variable);
					        }
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						Element sc = map.get(node.getKind());
					    String variable = ((ForallFormula)formula).getVariable();
					    ////System.out.println("setVar:"+variable+"---"+sc.toString());
					    if(sc!=null){
					    nodeRun.setVar(variable,sc);
						retBfunValuesDel(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun);
						nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("exists")){
				if(((ExistsFormula)formula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
					        Element sc = map.get(node.getKind());
					        String variable = ((ExistsFormula)formula).getVariable();
					        ////System.out.println("setVar:"+variable+"---"+sc.toString());
					        if(sc!=null){
						        nodeRun.setVar(variable,sc);
								retBfunValuesDel(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun);
								nodeRun.deleteVar(variable);
					        }
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						Element sc = map.get(node.getKind());
					    String variable = ((ExistsFormula)formula).getVariable();
					    if(sc!=null){
						    //System.out.println("setVar:"+variable+"---"+sc.toString());
						    nodeRun.setVar(variable,sc);
							retBfunValuesDel(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun);
							nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("not")){
				retBfunValuesDel(rule, ((UnaryFormula)formula).getFormula(), contextName, deleElement, addElement, nodeRun);
			}
			else if(formula.getKind().matches("and")){
				retBfunValuesDel(rule, ((AndFormula)formula).getFirst(),contextName, deleElement, addElement, nodeRun);
				retBfunValuesDel(rule, ((AndFormula)formula).getSecond(),contextName, deleElement, addElement, nodeRun);
			}
			else if(formula.getKind().matches("or")){
				retBfunValuesDel(rule, ((OrFormula)formula).getFirst(),contextName, deleElement, addElement, nodeRun);
				retBfunValuesDel(rule, ((OrFormula)formula).getSecond(),contextName, deleElement, addElement, nodeRun);
			}
			else if(formula.getKind().matches("implies")){
				retBfunValuesDel(rule, ((ImpliesFormula)formula).getFirst(),contextName, deleElement, addElement, nodeRun);
				retBfunValuesDel(rule, ((ImpliesFormula)formula).getSecond(),contextName, deleElement, addElement, nodeRun);
			}
			else{
				//System.out.println("Bfunc value evaluating..."+formula.getKind());
				//System.out.println(nodeRun.getVar().toString());
				boolean value = ((BFunc)formula).evaluateEcc(rule.getContexts(), nodeRun);
				bfuncValueList.add(value);
			}
		}

		private void retBfunValuesAdd(Rule rule, Formula formula, String contextName,Element deleElement, Element addElement, RuntimeNode nodeRun) {
			// TODO Auto-generated method stub
			//RuntimeNode runNode = new RuntimeNode();
			if(formula.getKind().matches("forall")) {
				if(((ForallFormula)formula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
							//node.setKind(addElement.getKey());
							
					        Element sc = map.get(addElement.getKey());//replace deleElement with addElement in evaluating bfuncValueList
					        String variable = ((ForallFormula)formula).getVariable();
					        ////System.out.println("setVar:"+variable+"---"+sc.toString());
					        if(sc!=null){
					        nodeRun.setVar(variable,sc);
							retBfunValuesAdd(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun);
							nodeRun.deleteVar(variable);
					        }
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						Element sc = map.get(node.getKind());
					    String variable = ((ForallFormula)formula).getVariable();
					    ////System.out.println("setVar:"+variable+"---"+sc.toString());
					    if(sc!=null){
					    nodeRun.setVar(variable,sc);
						retBfunValuesAdd(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun);
						nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("exists")){
				if(((ExistsFormula)formula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deleElement.getKey())){
					        Element sc = map.get(addElement.getKey());//replace deleElement with addElement in evaluating bfuncValueList
					        String variable = ((ExistsFormula)formula).getVariable();
					        ////System.out.println("setVar:"+variable+"---"+sc.toString());
					        if(sc!=null){
						        nodeRun.setVar(variable,sc);
								retBfunValuesAdd(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun);
								nodeRun.deleteVar(variable);
					        }
						}
					}
				}
				else {
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						Element sc = map.get(node.getKind());
					    String variable = ((ExistsFormula)formula).getVariable();
					    if(sc!=null){
						    //System.out.println("setVar:"+variable+"---"+sc.toString());
						    nodeRun.setVar(variable,sc);
							retBfunValuesAdd(rule, node.getFormula(), contextName, deleElement, addElement, nodeRun);
							nodeRun.deleteVar(variable);
					    }
					}
				}
			}
			else if(formula.getKind().matches("not")){
				retBfunValuesAdd(rule, ((UnaryFormula)formula).getFormula(), contextName, deleElement, addElement, nodeRun);
			}
			else if(formula.getKind().matches("and")){
				retBfunValuesAdd(rule, ((AndFormula)formula).getFirst(),contextName, deleElement, addElement, nodeRun);
				retBfunValuesAdd(rule, ((AndFormula)formula).getSecond(),contextName, deleElement, addElement, nodeRun);
			}
			else if(formula.getKind().matches("or")){
				retBfunValuesAdd(rule, ((OrFormula)formula).getFirst(),contextName, deleElement, addElement, nodeRun);
				retBfunValuesAdd(rule, ((OrFormula)formula).getSecond(),contextName, deleElement, addElement, nodeRun);
			}
			else if(formula.getKind().matches("implies")){
				retBfunValuesAdd(rule, ((ImpliesFormula)formula).getFirst(),contextName, deleElement, addElement, nodeRun);
				retBfunValuesAdd(rule, ((ImpliesFormula)formula).getSecond(),contextName, deleElement, addElement, nodeRun);
			}
			else{
				//System.out.println("Bfunc value evaluating..."+formula.getKind());
				//System.out.println(nodeRun.getVar().toString());
				boolean value = ((BFunc)formula).evaluateEcc(rule.getContexts(), nodeRun);
				bfuncValueList.add(value);
			}
		}
		*/
		private boolean islLinkAssociated(Rule rule, String context, Element element1,
				Element element2) {
			// TODO Auto-generated method stub
			//System.out.println("Link"+rule.getLastLink());
			//System.out.println("Link2"+rule.getLink());
			
			ArrayList<Link> links = rule.getLink();
			
			for (int i = 0; i<links.size(); i++){
				//System.out.println("Link"+links.get(i).toString());
				//System.out.println("Link"+links.get(i).getBinding().toString());
				//System.out.println("Elements"+element1.toString()+"---"+element2.toString());
				if(links.get(i).containElement(element1)||links.get(i).containElement(element2))
					return true;
			}
			//System.out.println(rule.getLink().toString());
			return false;
		}

		/*private boolean isEffectCancellable(Rule rule, String context,
				ContextChange change1, ContextChange change2) throws IOException {
			// TODO Auto-generated method stub
			goalValue.clear();
			//out.write(("Begin to compare"+ change1.toString()+"-and-"+change2.toString()+"\n").getBytes());
			String goalString = rule.returnGoalForCtx(context);
			////System.out.println("Rule:"+rule.getId()+"---Context"+context+"===="+goalString);
			Element element1 = change1.getElement();
			Element element2 = change2.getElement();
			RuntimeNode nodeRun = new RuntimeNode();
			if(change1.getOperate()==2){//deletion 1
				//System.out.println("Deletion: "+ change1.toString());
				//System.out.println("Addition: "+ change2.toString());
				long update1 = Calendar.getInstance().getTimeInMillis();
				retFormulaValue(rule.getFormula(),context,element1.getKey(), element2.getKey(), goalString);
				cc_updateTime = Calendar.getInstance().getTimeInMillis() - update1 + cc_updateTime;
				if(goalValue.contains("False")){
					////System.out.println("Original false");
					return false;
				}
				
				//System.out.println("goalValue"+goalValue.toString());
				//System.out.println("goalValue"+goalValue.size());
				if(goalValue.size() == 0)
					return false;
				
				goalValue.clear();
				pretendFormulaValue(rule, rule.getFormula(),context,element1, element2, goalString, nodeRun);
				if(goalValue.contains("False")){
					////System.out.println("1After false");
					pretendFormulaValue(rule, rule.getFormula(),context,element2, element1, goalString, nodeRun);
					return false;
				}
			}
			if(change2.getOperate()==2){//deletion 2
				//System.out.println("Deletion: "+ change2.toString());
				//System.out.println("Addition: "+ change1.toString());
				long update1 = Calendar.getInstance().getTimeInMillis();
				retFormulaValue(rule.getFormula(),context,element2.getKey(), element1.getKey(), goalString);
				cc_updateTime = Calendar.getInstance().getTimeInMillis() - update1 + cc_updateTime;
				if(goalValue.contains("False")){
					//System.out.println("Original false");
					return false;
					
				}
				
				if(goalValue.size() == 0)
					return false;
				goalValue.clear();
				
				//System.out.println("==========================="+element2.toString());
				pretendFormulaValue(rule, rule.getFormula(),context,element2, element1, goalString, nodeRun);
				
				//map = rule.getContexts().get(change1.getContext()).getElements();
				
				
				//System.out.println("....");
				if(goalValue.contains("False")){
					//System.out.println("2After false");
					pretendFormulaValue(rule, rule.getFormula(),context,element1, element2, goalString, nodeRun);
					return false;
				}
			}
			return true;
		}

		private boolean pretendFormulaValue(Rule rule, Formula formula, String contextName,
				Element deletion, Element addition, String cancelGoal,RuntimeNode nodeRun) throws IOException {
			// TODO Auto-generated method stub
			if(formula.getKind().matches("forall")) {
				if(((ForallFormula)formula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						//System.out.println("ForallBeforeNode:"+node.getKind()+"-"+node.getValue()+"key"+deletion.getKey());
						boolean beforeValue = node.getValue();
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						if(node.getKind().equals(deletion.getKey())){
							node.setKind(addition.getKey());
							
							//rule.getContexts().
							rule.changeContext(contextName, deletion,addition);
							//rule.getContexts().get(contextName).getElements().remove(deletion.getKey());
							//rule.getContexts().get(contextName).getElements().put(addition.getKey(), addition);
							
							Element sc = map.get(addition.getKey());
							nodeRun.setVar(((ForallFormula)formula).getVariable(),sc);
							boolean result = node.evaluateEcc(rule.getContexts(), nodeRun);
							node.setValue(result);
							//System.out.println("ForallAfterNode:"+node.getKind()+"-"+node.getValue());
							if(node.getKind().equals(addition.getKey())){
								if(node.getValue()){
									////System.out.println(node.getKind()+"-"+node.getValue());
									if(cancelGoal.equalsIgnoreCase("True"))
										goalValue.add("True");
									else {
										if(cancelGoal.equalsIgnoreCase("Any")&& beforeValue==true)
											goalValue.add("True");
										else{
											goalValue.add("False");
											return false;
										}
									}
								}
								else {
									////System.out.println(node.getKind()+"2-"+node.getValue());
									if(cancelGoal.equalsIgnoreCase("False"))
										goalValue.add("True");
									else {
										if(cancelGoal.equalsIgnoreCase("Any")&& beforeValue==false)
											goalValue.add("True");
										else {
											goalValue.add("False");
											return false;	
										}
									}
								}
							}
							break;//return true;
						}
						
					}
					return false;	
				}
				else{
					////System.out.println("not find!" + ((ForallFormula)formula).getContext());
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode element = elementList.get(i);
				        Element sc = rule.getContexts().get(((ForallFormula)formula).getContext()).getElements().get(element.getKind());
				        String variable = ((ForallFormula)formula).getVariable();
				        ////System.out.println("setVar:"+variable+"---"+sc.toString());
				        nodeRun.setVar(variable,sc);
						pretendFormulaValue(rule, elementList.get(i).getFormula(), contextName, deletion, addition, cancelGoal, nodeRun);
						nodeRun.deleteVar(variable);
					}
				}
			}
			else if(formula.getKind().matches("exists")) {
				if(((ExistsFormula)formula).getContext().equals(contextName)){
					//System.out.println("find"+((ExistsFormula)formula).getContext());
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						boolean beforeValue = node.getValue();
						//System.out.println("BeforeNode1:"+node.getKind()+"-"+node.getValue());
						//System.out.println("BeforeNode2:"+deletion.getKey()+deletion.getFields().toString());
						
					//	out.write(("BeforeNode1:"+node.getKind()+"-"+node.getValue()+"\n").getBytes());
					//	out.write(("BeforeNode2:"+deletion.getKey()+deletion.getFields().toString()+"\n").getBytes());
						
						HashMap<String, Element> map = rule.getContexts().get(contextName).getElements();
						
						
						if(node.getKind().equals(deletion.getKey())){
							
							node.setKind(addition.getKey());
							//rule.getContexts().
							//RuntimeNode nodeRun = new RuntimeNode();
							rule.changeContext(contextName, deletion,addition);
							//rule.getContexts().get(contextName).getElements().remove(deletion.getKey());
							//rule.getContexts().get(contextName).getElements().put(addition.getKey(), addition);
							
							Element sc = rule.getContexts().get(contextName).getElements().get(addition.getKey());
							nodeRun.setVar(((ExistsFormula)formula).getVariable(),sc);
							////System.out.println("setVar:"+((ExistsFormula)formula).getVariable()+"---"+sc.toString());
							boolean result = node.evaluateEcc(rule.getContexts(), nodeRun);
							node.setValue(result);
						//	//System.out.println("AfterNode:"+node.getKind()+"-"+node.getValue());
							if(node.getKind().equals(addition.getKey())){
								if(node.getValue()){
									////System.out.println(node.getKind()+"-"+node.getValue());
									if(cancelGoal.equalsIgnoreCase("True"))
										goalValue.add("True");
									else {
										if(cancelGoal.equalsIgnoreCase("Any")&& beforeValue==true)
											goalValue.add("True");
										else{
											goalValue.add("False");
											return false;
										}
									}
								}
								else {
									////System.out.println(node.getKind()+"2-"+node.getValue());
									if(cancelGoal.equalsIgnoreCase("False"))
										goalValue.add("True");
									else {
										if(cancelGoal.equalsIgnoreCase("Any")&& beforeValue==false)
											goalValue.add("True");
										else {
											goalValue.add("False");
											return false;	
										}
									}
								}
							}
							break;//return true;
						}
					}
					//return false;
				}
				else{
					////System.out.println("not find!" + ((ForallFormula)formula).getContext());
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode element = elementList.get(i);
				        Element sc = rule.getContexts().get(((ExistsFormula)formula).getContext()).getElements().get(element.getKind());
				        String variable = ((ExistsFormula)formula).getVariable();
				        ////System.out.println("setVar:"+variable+"---"+sc.toString());
				        nodeRun.setVar(variable,sc);
						pretendFormulaValue(rule, elementList.get(i).getFormula(), contextName, deletion, addition, cancelGoal, nodeRun);
						nodeRun.deleteVar(variable);
					}
				}
			}
			else if(formula.getKind().matches("not")) {
				pretendFormulaValue(rule, ((UnaryFormula)formula).getFormula(), contextName, deletion, addition, cancelGoal, nodeRun);
			}
			else if(formula.getKind().matches("and")) {
				pretendFormulaValue(rule, ((AndFormula)formula).getFirst(),contextName, deletion, addition, cancelGoal, nodeRun);
				pretendFormulaValue(rule, ((AndFormula)formula).getSecond(),contextName, deletion, addition, cancelGoal, nodeRun);
			}
			else if(formula.getKind().matches("or")) {
				pretendFormulaValue(rule, ((OrFormula)formula).getFirst(),contextName, deletion, addition, cancelGoal, nodeRun);
				pretendFormulaValue(rule, ((OrFormula)formula).getSecond(),contextName, deletion, addition, cancelGoal, nodeRun);
			}
			else if(formula.getKind().matches("implies")) {
				pretendFormulaValue(rule, ((ImpliesFormula)formula).getFirst(),contextName, deletion, addition, cancelGoal, nodeRun);
				pretendFormulaValue(rule, ((ImpliesFormula)formula).getSecond(),contextName, deletion, addition, cancelGoal, nodeRun);
			}
			return true;
		}

		private boolean retFormulaValue(Formula formula, String contextName, String deletion,
				String addition, String cancelGoal) {
			// TODO Auto-generated method stub
			////System.out.println(rule.getId()+"-----"+contextName+"-----"+rule.returnGoalForCtx(contextName))
			
			if(formula.getKind().matches("forall")) {
				if(((ForallFormula)formula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode sub = elementList.get(i);
						if(sub.getKind().equals(deletion)){
							if(sub.getValue()){
								////System.out.println(sub.getKind()+"-"+sub.getValue()+"---"+cancelGoal);
								if(cancelGoal.equalsIgnoreCase("True")||cancelGoal.equalsIgnoreCase("Any"))
									goalValue.add("True");
								else {
									goalValue.add("False");
									return false;
								}
							}
							else {
								////System.out.println(sub.getKind()+"-"+sub.getValue()+"---"+cancelGoal);
								if(cancelGoal.equalsIgnoreCase("False")||cancelGoal.equalsIgnoreCase("Any"))
									goalValue.add("True");
								else {
									goalValue.add("False");
									return false;
								}
							}
							break;
						}
					}
				}
				else{
					////System.out.println("not find!" + ((ForallFormula)formula).getContext());
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						retFormulaValue(elementList.get(i).getFormula(), contextName, deletion, addition, cancelGoal);
					}
				}
			}
			else if(formula.getKind().matches("exists")) {
				if(((ExistsFormula)formula).getContext().equals(contextName)){
					////System.out.println("find"+((ExistsFormula)formula).getContext());
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						if(node.getKind().equals(deletion)){
							if(node.getValue()){
								////System.out.println(node.getKind()+"-"+node.getValue());
								if(cancelGoal.equalsIgnoreCase("True")||cancelGoal.equalsIgnoreCase("Any"))
									goalValue.add("True");
								else {
									goalValue.add("False");
									return false;
								}
							}
							else {
								////System.out.println(node.getKind()+"-"+node.getValue());
								if(cancelGoal.equalsIgnoreCase("False")||cancelGoal.equalsIgnoreCase("Any"))
									goalValue.add("True");
								else {
									goalValue.add("False");
									return false;
								}
							}
							break;
						}
					}
				}
				else{
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						retFormulaValue(elementList.get(i).getFormula(), contextName,  deletion, addition, cancelGoal);
					}
				}
			}
			else if(formula.getKind().matches("not")) {
				retFormulaValue(((UnaryFormula)formula).getFormula(), contextName, deletion, addition, cancelGoal);
			}
			else if(formula.getKind().matches("and")) {
				retFormulaValue(((AndFormula)formula).getFirst(),contextName, deletion, addition, cancelGoal);
				retFormulaValue(((AndFormula)formula).getSecond(),contextName, deletion, addition, cancelGoal);
			}
			else if(formula.getKind().matches("or")) {
				retFormulaValue(((OrFormula)formula).getFirst(),contextName, deletion, addition, cancelGoal);
				retFormulaValue(((OrFormula)formula).getSecond(),contextName, deletion, addition, cancelGoal);
			}
			else if(formula.getKind().matches("implies")) {
				retFormulaValue(((ImpliesFormula)formula).getFirst(),contextName, deletion, addition, cancelGoal);
				retFormulaValue(((ImpliesFormula)formula).getSecond(),contextName, deletion, addition, cancelGoal);
			}
			return true;
		}
		
		
		private void updateSideEffect(Rule rule, String contextName, ContextChange change2,
				ContextChange change) {
			// TODO Auto-generated method stub
			Formula formula = rule.getFormula();
			
		}

		public void updateRuleEffect(Formula formula, String contextName, String deletion, String addition){
			//context_1.renameElement(element1, element2);
		//Formula formula = rule.getFormula();
		if(formula.getKind().matches("forall")) {
			if(((ForallFormula)formula).getContext().equals(contextName)){
				LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
				for(int i = 0; i< elementList.size(); i++){
					SubNode node = elementList.get(i);
					if(node.getKind().equals(deletion)){
						node.setKind(addition);
						break;//return true;
					}
				}
			}
			else{
				////System.out.println("not find!" + ((ForallFormula)formula).getContext());
				LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
				for(int i = 0; i< elementList.size(); i++){
					updateRuleEffect(elementList.get(i).getFormula(), contextName, deletion, addition);
				}
			}
		}
		else if(formula.getKind().matches("exists")) {
			if(((ExistsFormula)formula).getContext().equals(contextName)){
				////System.out.println("find"+((ExistsFormula)formula).getContext());
				LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
				for(int i = 0; i< elementList.size(); i++){
					SubNode node = elementList.get(i);
					if(node.getKind().equals(deletion)){
						node.setKind(addition);
						break;//return true;
					}
				}
			}
			else{
				LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
				for(int i = 0; i< elementList.size(); i++){
					updateRuleEffect(elementList.get(i).getFormula(), contextName, deletion, addition);
				}
			}
		}
		else if(formula.getKind().matches("not")) {
			updateRuleEffect(((UnaryFormula)formula).getFormula(), contextName, deletion, addition);
		}
		else if(formula.getKind().matches("and")) {
			updateRuleEffect(((AndFormula)formula).getFirst(),contextName, deletion, addition);
				updateRuleEffect(((AndFormula)formula).getSecond(),contextName, deletion, addition);
		}
		else if(formula.getKind().matches("or")) {
			updateRuleEffect(((OrFormula)formula).getFirst(),contextName, deletion, addition);
				updateRuleEffect(((OrFormula)formula).getSecond(),contextName, deletion, addition);
		}
		else if(formula.getKind().matches("implies")) {
			updateRuleEffect(((ImpliesFormula)formula).getFirst(),contextName, deletion, addition);
				updateRuleEffect(((ImpliesFormula)formula).getSecond(),contextName, deletion, addition);
		}
		//return false;
	}*/
}