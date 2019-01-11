/*
  * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dataLoader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import context.Context;
import context.ContextChange;
import context.Element;


/**
 *
 * @author why
 * obtain context changes
 */
public class Demo extends Changes{
    private int pointer;
    private LinkedList<String> list = new LinkedList<String>();
	private HashMap<String,Context> contexts = new HashMap<String,Context>();
     
    public Demo(String filename) {
    	ArrayList<Context> content = new ArrayList<Context>
        (ContextLoader.parserXml("src/config/context_3.xml"));
        
        int num = content.size();
    	for(int k = 0;k < num;k++) {
    		contexts.put(content.get(k).getContextname(),content.get(k));
    	}
    	
        pointer = 0;
        File file = new File(filename);
        
        if (file.exists() && file.isFile()) {
            try{
                @SuppressWarnings("resource")
				BufferedReader input = new BufferedReader(new FileReader(file));
                String text;
                while((text = input.readLine()) != null)
                    list.add(text);
            }  
            catch(IOException ioException){
                System.err.println("File Error!");
            }
        }
    }

    @Override
    public boolean hasNextChange() {
        if(pointer < list.size())
            return true;
        else
            return false;
    }

    @Override
    public ContextChange nextChange() {
        String changeString = list.get(pointer);
        pointer++;
        ContextChange change = new ContextChange();
        change.setString(changeString);
        String[] s = changeString.split(",");
        if(s[0].matches("[+]"))
            change.setOperate(1);
        else if(s[0].matches("[-]"))
            change.setOperate(2);
        else if(s[0].matches("[#]"))
            change.setOperate(3);
        change.setContext(s[1]);
        change.setKey(s[2]);
        
        ArrayList<Object> content = new ArrayList<Object>();
        
        Context ct = contexts.get(s[1]);
        Element sct = new Element();
        for(int i = 0;i < ct.getFields().size();i ++) {
            sct.add_field(ct.getFields().get(i),s[i + 2]);
            content.add(s[i + 2]);
        }
        sct.setContext(s[1]);
        sct.setKey(s[2]);
        
        change.setContent(content);
        change.setElement(sct);
        
        if(pointer % 1000 == 0) {
        	System.out.println("------" + pointer + "------");
        }
        
        return change;
    }

    
    @Override
    public void close() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getChanges() {
        return list.size();
    }
    
    public int getPoint() {
    	return pointer;
    }
}
