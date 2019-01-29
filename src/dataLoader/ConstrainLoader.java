package dataLoader;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList; 
import java.util.HashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Attr; 
import org.w3c.dom.Document; 
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node; 
import org.w3c.dom.NodeList; 
import org.xml.sax.SAXException; 

import rule.Pattern;
import rule.Rule;
import formula.AndFormula;
import formula.BFunc;
import formula.ExistsFormula;
import formula.ForallFormula;
import formula.Formula;
import formula.ImpliesFormula;
import formula.OrFormula;
import formula.UnaryFormula;
/** 
* 
* @author why
* 
 * load and parse the context configuration files 
*/ 

public class ConstrainLoader {
    //Rule rules;
	private ArrayList<Rule> rules = new ArrayList<Rule>();
    
    @SuppressWarnings("unused")
	private Log logger = LogFactory.getLog(ConstrainLoader.class.getName());
    
    private Object parseElement(Element element) {
        String tagName = element.getNodeName();
        NodeList children = element.getChildNodes();
	
        if(tagName.matches("rules")) {//set of rules
            //process the sub-node for "rules"
            for(int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    //obtain node type
                    short nodeType = node.getNodeType();
                    if(nodeType == Node.ELEMENT_NODE) {
                        Rule rule = (Rule)parseElement((Element)node);
                        rules.add(rule);
                    }
            }
            return rules;
        }
        if(tagName.matches("rule")) {//each rule
            //obtain rule ID
            Node idNode = children.item(1);
            NodeList subChildren = idNode.getChildNodes();
            String name = subChildren.item(0).getNodeValue();
            Rule rule = new Rule(name);
            //formula
            Node formulaNode = children.item(3);
            Formula formula = (Formula)parseElement((Element)formulaNode);
            rule.setFormula(formula);
            //patterns
            LinkedList<Pattern> patterns = new LinkedList<Pattern>();
            Node patternsNode = children.item(5);
            NodeList patternsNodes = patternsNode.getChildNodes();
            for(int i = 0; i < patternsNodes.getLength(); i++) {
                Node node = patternsNodes.item(i);
                //obtain node type
                short nodeType = node.getNodeType();
                if(nodeType == Node.ELEMENT_NODE) {
                    Pattern pattern = (Pattern)parseElement((Element)node);
                    pattern.setRuleId(name);
                    patterns.add(pattern);
                }
            }
            rule.setPatterns(patterns);
            
            /*Node goals = children.item(7);
            HashMap<String,String> goalMap = new HashMap<String,String>();
            NodeList goalList = goals.getChildNodes();
            Node first = goalList.item(1);
            NamedNodeMap map = first.getAttributes();
            if(map != null) {
            	String nameString = "";
            	String goalString = "";
                for(int i = 0; i < map.getLength(); i++) {
                    Attr attr = (Attr)map.item(i);	
                    String attrName = attr.getName();
                    String attrValue = attr.getValue();
                    if(attrName.matches("name")) {
                        nameString = attrValue;
                    }
                    if(attrName.matches("cancelgoal")) {
                        goalString = attrValue;
                    }
                }
                goalMap.put(nameString, goalString);
            }
           
	            Node second = goalList.item(3);
	            NamedNodeMap smap = second.getAttributes();
	            if(smap != null) {
	            	String nameString = "";
	            	String goalString = "";
	                for(int i = 0; i < smap.getLength(); i++) {
	                    Attr attr = (Attr)smap.item(i);	
	                    String attrName = attr.getName();
	                    String attrValue = attr.getValue();
	                    if(attrName.matches("name")) {
	                        nameString = attrValue;
	                    }
	                    if(attrName.matches("cancelgoal")) {
	                        goalString = attrValue;
	                    }
	                }
	                goalMap.put(nameString, goalString);
	            }
	            rule.setGoals(goalMap);*/
            
            return rule;
        }
        if(tagName.matches("pattern")) {
        	Pattern pattern = new Pattern();
            Node first = children.item(1);
            NamedNodeMap map = first.getAttributes();
            if(map != null) {
                for(int i = 0; i < map.getLength(); i++) {
                    Attr attr = (Attr)map.item(i);	
                    String attrName = attr.getName();
                    String attrValue = attr.getValue();
                    if(attrName.matches("type")) {
                        pattern.setfType(attrValue);
                    }
                    if(attrName.matches("name")) {
                        pattern.setfName(attrValue);
                    }
                }
            }
            
            Node second = children.item(3);
            NamedNodeMap smap = second.getAttributes();
            if(smap != null) {
                for(int i = 0; i < smap.getLength(); i++) {
                    Attr attr = (Attr)smap.item(i);	
                    String attrName = attr.getName();
                    String attrValue = attr.getValue();
                    if(attrName.matches("type")) {
                        pattern.setsType(attrValue);
                    }
                    if(attrName.matches("name")) {
                        pattern.setsName(attrValue);
                    }
                }
            }
            return pattern;
        }
        if(tagName.matches("goal")) {
        	Pattern pattern = new Pattern();
            Node first = children.item(1);
            NamedNodeMap map = first.getAttributes();
            if(map != null) {
                for(int i = 0; i < map.getLength(); i++) {
                    Attr attr = (Attr)map.item(i);	
                    String attrName = attr.getName();
                    String attrValue = attr.getValue();
                    if(attrName.matches("type")) {
                        pattern.setfType(attrValue);
                    }
                    if(attrName.matches("name")) {
                        pattern.setfName(attrValue);
                    }
                }
            }
            
            Node second = children.item(3);
            NamedNodeMap smap = second.getAttributes();
            if(smap != null) {
                for(int i = 0; i < smap.getLength(); i++) {
                    Attr attr = (Attr)smap.item(i);	
                    String attrName = attr.getName();
                    String attrValue = attr.getValue();
                    if(attrName.matches("type")) {
                        pattern.setsType(attrValue);
                    }
                    if(attrName.matches("name")) {
                        pattern.setsName(attrValue);
                    }
                }
            }
            return pattern;
        }
        if(tagName.matches("formula")) {//FOL fomula for a rule
            Node kindNode = children.item(1);
            Formula formula = (Formula)parseElement((Element)kindNode);
            return formula;
        }
        if(tagName.matches("forall")) {//forall formula
            ForallFormula formula = new ForallFormula(tagName);
                
            NamedNodeMap map = element.getAttributes();
            String var = null;
            String ctx = null;
            if(map != null) {
                    for(int i = 0; i < map.getLength(); i++) {
                            Attr attr = (Attr)map.item(i);	
                            String attrName = attr.getName();
                            String attrValue = attr.getValue();
                            if(attrName.matches("var")) {
                                var = attrValue;
                            }
                            if(attrName.matches("in")) {
                                ctx = attrValue;
                            }
                    }
            }
            formula.setContext(var,ctx);
            
            NodeList subChildren = element.getChildNodes();
            Node sub = subChildren.item(1);
            Formula subFormula = (Formula)parseElement((Element)sub);
            formula.setSubFormula(subFormula);
            return formula;
        }
        if(tagName.matches("exists")) {//exists formula
            ExistsFormula formula = new ExistsFormula(tagName);
                
            NamedNodeMap map = element.getAttributes();
            String var = null;
            String ctx = null;
            if(map != null) {
                    for(int i = 0; i < map.getLength(); i++) {
                            Attr attr = (Attr)map.item(i);	
                            String attrName = attr.getName();
                            String attrValue = attr.getValue();
                            if(attrName.matches("var")) {
                                var = attrValue;
                            }
                            if(attrName.matches("in")) {
                                ctx = attrValue;
                            }
                    }
            }
            formula.setContext(var, ctx);
            
            NodeList subChildren = element.getChildNodes();
            Node sub = subChildren.item(1);
            Formula subFormula = (Formula)parseElement((Element)sub);
            formula.setSubFormula(subFormula);
            return formula;
        }
        if(tagName.matches("and")) {
            AndFormula formula = new AndFormula(tagName);
            NodeList subChildren = element.getChildNodes();
            Node first = subChildren.item(1);
            Formula firstFormula = (Formula)parseElement((Element)first);
            Node second = subChildren.item(3);
            Formula secondFormula = (Formula)parseElement((Element)second);
            formula.setSubFormula(firstFormula,secondFormula);
            return formula;
        }
        if(tagName.matches("or")) {
            OrFormula formula = new OrFormula(tagName);
            NodeList subChildren = element.getChildNodes();
            Node first = subChildren.item(1);
            Formula firstFormula = (Formula)parseElement((Element)first);
            Node second = subChildren.item(3);
            Formula secondFormula = (Formula)parseElement((Element)second);
            formula.setSubFormula(firstFormula,secondFormula);
            return formula;
        }
        if(tagName.matches("implies")) {
            ImpliesFormula formula = new ImpliesFormula(tagName);
            NodeList subChildren = element.getChildNodes();
            Node first = subChildren.item(1);
            Formula firstFormula = (Formula)parseElement((Element)first);
            Node second = subChildren.item(3);
            Formula secondFormula = (Formula)parseElement((Element)second);
            formula.setSubFormula(firstFormula,secondFormula);
            return formula;
        }
        if(tagName.matches("not")) {
            UnaryFormula formula = new UnaryFormula(tagName);
            NodeList subChildren = element.getChildNodes();
            Node sub = subChildren.item(1);
            Formula subFormula = (Formula)parseElement((Element)sub);
            formula.setSubFormula(subFormula);
            return formula;
        }
        if(tagName.matches("bfunc")) {
            String name = new String();
            NamedNodeMap map = element.getAttributes();
            if(map != null) {
                    for(int i = 0; i < map.getLength(); i++) {
                            Attr attr = (Attr)map.item(i);	
                            String attrName = attr.getName();
                            String attrValue = attr.getValue();
                            if(attrName.matches("name")) {
                                name = attrValue;
                            }
                    }
            }
            BFunc formula = new BFunc(name);
            for(int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    short nodeType = node.getNodeType();
                    if(nodeType == Node.ELEMENT_NODE) {
                        String[] param = (String[])parseElement((Element)node);
                        formula.setParam(param[0],param[1],param[2]);
                    }
            }
            return formula;
        }
        if(tagName.matches("param")) {
            String[] param = new String[3];
            NamedNodeMap map = element.getAttributes();
            if(map != null) {
                for(int i = 0; i < map.getLength(); i++) {
                        Attr attr = (Attr)map.item(i);	
                        String attrName = attr.getName();
                        String attrValue = attr.getValue();
                        if(attrName.matches("pos")) {
                            param[0] = attrValue;
                        }
                        if(attrName.matches("var")) {
                            param[1] = attrValue;
                        }	
                        if(attrName.matches("field")) {
                            param[2] = attrValue;
                        }
                }
            }
            return param;
        }
        return null;
    }
    public ArrayList<Rule> parserXml(String fileName) { 
        try { 
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder db = dbf.newDocumentBuilder(); 
            Document document = db.parse(fileName); 
            
            //obtain the root note
            Element root = document.getDocumentElement();
            parseElement(root);
            
        } catch (FileNotFoundException e) { 
            System.out.println(e.getMessage()); 
        } catch (ParserConfigurationException e) { 
            System.out.println(e.getMessage()); 
        } catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return rules;
    } 
} 