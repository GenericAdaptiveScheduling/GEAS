/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dataLoader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
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


import context.Context;

/**
 *
 * @author why
 * load and parse the context configuration file
 */

public class ContextLoader {
    @SuppressWarnings("unused")
	private static Log logger = LogFactory.getLog(ConstrainLoader.class.getName());
    
    private static Object parseElement(Element element) {
        String tagName = element.getNodeName();
		NodeList children = element.getChildNodes();
		
		if(tagName.matches("contexts")) {//set of contexts
			ArrayList<Context> contexts = new ArrayList<Context>();
            for(int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    short nodeType = node.getNodeType();
                    if(nodeType == Node.ELEMENT_NODE) {
                        Context context = (Context)parseElement((Element)node);
                        contexts.add(context);
                    }
            }
            return contexts;
        }
        if(tagName.matches("context")) {//each context
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
            Context context = new Context(name);
            for(int i = 0; i < children.getLength(); i++) {
                    Node node = children.item(i);
                    short nodeType = node.getNodeType();
                    if(nodeType == Node.ELEMENT_NODE) {
                            String field = (String)parseElement((Element)node);
                            context.addField(field);
                    }
            }
            return context;
        }
        if(tagName.matches("field")) {
            String field = new String();
            NamedNodeMap map = element.getAttributes();
            if(map != null) {
                Attr attr = (Attr)map.item(0);	
                String attrName = attr.getName();
                if(attrName.matches("name")) {
                    field = attr.getValue();
                }
            }
            return field;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
	public static ArrayList<Context> parserXml(String fileName) { 
    	ArrayList<Context> contexts = new ArrayList<Context>();
        try { 
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance(); 
            DocumentBuilder db = dbf.newDocumentBuilder(); 
            Document document = db.parse(fileName); 
            
            //obtain the root node
            Element root = document.getDocumentElement();
            contexts = (ArrayList<Context>)parseElement(root);
            
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
        return contexts;
    } 
    
}
