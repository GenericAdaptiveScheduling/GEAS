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

import rule.Rule;
import context.ContextChange;
import context.Element;
import dataLoader.Demo;
import formula.AndFormula;
import formula.BFunc;
import formula.ExistsFormula;
import formula.ForallFormula;
import formula.Formula;
import formula.ImpliesFormula;
import formula.OrFormula;
import formula.RuntimeNode;
import formula.SubNode;
import formula.UnaryFormula;
/**
*
* @author why
* GEAS-opt scheduling strategy
*/
public class GEAS_cancel  extends Checker{
	
		public static int checktime = 0;
		public static LinkedList<String> goalValue = new LinkedList<String>();
		public ArrayList<Boolean> bfuncValueList = new ArrayList<Boolean>();
		
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
	    	while(demo.hasNextChange()) {//循环处理changes
	            change = demo.nextChange();      
	            line++;
	            for(int i = 0;i < rules.size();i++) {//循环检测所有的约束
	            	Rule rule = rules.get(i);          	
	            	//if(!(rule.getId().equalsIgnoreCase("rule_in_DF")))
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
	                	  	
		    	    	if(rule.match_chgBuffer(change)||rule.match_plusBuffer(change)) {//与之前的changes组成unstable pattern: 当前为inc-
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
		    	    		rule.clearplusBuffer();
		    	    		
		    	    		
		    	    	}
		    	    	else {
		    	    		;//out.write(("Not match: "+ change.toString()+"\n").getBytes());
		    	    		////System.out.println("Not match: "+ change.toString());
		    	    		
		    	    	}
		    	    	rule.handleSelf(change);
	    	    		if(change.getState(ruleId)==1){//&&(ruleId.startsWith("cst_loc")||ruleId.startsWith("rule_"))){//当前为inc+
		    	    			//flagINC = 1;
		    	    			if((!rule.getBuffer().isEmpty())){//&&areCCRsEmpty.get(i)==0){//batch不为空,CCR为空
				    	    		ContextChange change2;
				    	    		int flag = 0;
				    	    		int k=0;
				    	    		for(;k<listBuffer.size();k++)//= rule.getBuffer().get(rule.getBuffer().size()-1);
				    	    		{
				    	    			change2 = listBuffer.get(k);
					    	    		if(change2.getState(ruleId)==0){//&&change.getState(ruleId)==1
					    	    			if(change2.getContext().equals(change.getContext())){//&&change2.getKey().equals(change.getKey())){
					    	    			
					    	    				//long exmaine1 = Calendar.getInstance().getTimeInMillis();
						    	    				if(isEffectCancellable(rule, change.getContext(), change2, change)){    					
						    	    					listBuffer.remove(k);
						    	    					
						    	    					flag = 1;
							    	    				break;
						    	    				}
						    	    			//cc_examineTime = Calendar.getInstance().getTimeInMillis()-exmaine1 + cc_examineTime;
					    	    			}
					    	    		}
				    	    		}
				    	    		/*if(flag == 0){
				    	    			if(cancelTable.containsKey(-1))
				    	    				cancelTable.put(-1, cancelTable.get(-1)+1);
				    	    			else 
				    	    				cancelTable.put(-1, 1);
				    	    		}
				    	    		else{
				    	    			if(cancelTable.containsKey(k+1))
				    	    				cancelTable.put(k+1, cancelTable.get(k+1)+1);
				    	    			else 
				    	    				cancelTable.put(k+1, 1);
				    	    		}*/
				    	    		//for(Entry entry: rule.getBuffer().)
				    	    		if(flag == 0){
				    	    			rule.setBuffer(change);
				    	    		}
			    	    		}
		    	    			else{ //batch为空
		        	    			rule.setBuffer(change);
		        	    			//areCCRsEmpty.set(i, 1);
		        	    		}
		    	    		}
		    	    		else{ //当前不为inc+
		    	    			rule.setBuffer(change);
		    	    		}
		    	    	
	    	    		long end = Calendar.getInstance().getTimeInMillis();
	    	            allTime += end - start;
	    	            
	                }
	            }
	            
	        }
	    }

		private boolean isEffectCancellableEvaluated(Rule rule, String context, ContextChange change1, ContextChange change2){
			
			Element element1 = change1.getElement();
			Element element2 = change2.getElement();
			
			if(!islLinkAssociated(rule,context,element1,element2)){
				if(change1.getOperate()==2){//<-,+> 2:delete
					bfuncValueList.clear();
					retBfunValuesDel(rule.getFormula(),context,element1,element2);
					System.out.println(bfuncValueList.toString());
				}
				else if (change2.getOperate()==2){//<+,-> 2:delete
					bfuncValueList.clear();
					retBfunValuesAdd(rule.getFormula(),context,element2,element1);
					System.out.println(bfuncValueList.toString());
				}
			}
			
			return false;
			
			
		}
		private void retBfunValuesDel(Formula formula,  String contextName,Element deleElement, Element addElement) {
			// TODO Auto-generated method stub
			//RuntimeNode runNode = new RuntimeNode();
			if(formula.getKind().matches("forall")) {
				if(((ForallFormula)formula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						if(node.getKind().equals(deleElement.getKey())){
							retBfunValuesDel(node.getFormula(), contextName, deleElement, addElement);
						}
					}
				}
			}
			else if(formula.getKind().matches("exists")){
				if(((ExistsFormula)formula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = ((ExistsFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						if(node.getKind().equals(deleElement.getKey())){
							retBfunValuesDel(node.getFormula(), contextName, deleElement, addElement);
						}
					}
				}
			}
			else if(formula.getKind().matches("not")){
				retBfunValuesDel(((UnaryFormula)formula).getFormula(), contextName, deleElement, addElement);
			}
			else if(formula.getKind().matches("and")){
				retBfunValuesDel(((AndFormula)formula).getFirst(),contextName, deleElement, addElement);
				retBfunValuesDel(((AndFormula)formula).getSecond(),contextName, deleElement, addElement);
			}
			else if(formula.getKind().matches("or")){
				retBfunValuesDel(((OrFormula)formula).getFirst(),contextName, deleElement, addElement);
				retBfunValuesDel(((OrFormula)formula).getSecond(),contextName, deleElement, addElement);
			}
			else if(formula.getKind().matches("implies")){
				retBfunValuesDel(((ImpliesFormula)formula).getFirst(),contextName, deleElement, addElement);
				retBfunValuesDel(((ImpliesFormula)formula).getSecond(),contextName, deleElement, addElement);
			}
			else if(formula.getKind().matches("bfunction")){
				boolean value = ((BFunc)formula).getValue();
				bfuncValueList.add(value);
			}
		}
		private void retBfunValuesAdd(Formula formula,  String contextName,Element deleElement, Element addElement) {
			// TODO Auto-generated method stub
			RuntimeNode runNode = new RuntimeNode();
			//Formula formula = rule.getFormula();
			if(formula.getKind().matches("forall")) {
				if(((ForallFormula)formula).getContext().equals(contextName)){
					LinkedList<SubNode> elementList = ((ForallFormula)formula).getSubnodes();
					for(int i = 0; i< elementList.size(); i++){
						SubNode node = elementList.get(i);
						if(node.getKind().equals(deleElement.getKey())){
							
						}
					}
				}
			}
			else if(formula.getKind().matches("exists")){
				
			}
			else if(formula.getKind().matches("not")){
				
			}
			else if(formula.getKind().matches("and")){
				
			}
			else if(formula.getKind().matches("or")){
				
			}
			else if(formula.getKind().matches("implies")){
				
			}
			else if(formula.getKind().matches("bfunction")){
				
			}
		}
		private boolean islLinkAssociated(Rule rule, String context, Element element1,
				Element element2) {
			// TODO Auto-generated method stub
			System.out.println(rule.getLink().toString());

			return false;
		}

		private boolean isEffectCancellable(Rule rule, String context,
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
				//cc_updateTime = Calendar.getInstance().getTimeInMillis() - update1 + cc_updateTime;
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
				//cc_updateTime = Calendar.getInstance().getTimeInMillis() - update1 + cc_updateTime;
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
	}
	}