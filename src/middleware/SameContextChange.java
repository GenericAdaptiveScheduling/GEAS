package middleware;

import java.util.ArrayList;

import context.ContextChange;

public class SameContextChange {
	private String context;
	ArrayList<ContextChange> changes = new ArrayList<ContextChange>();
	ArrayList<ContextChange> addChanges = new ArrayList<ContextChange>();
	ArrayList<ContextChange> deleteChanges = new ArrayList<ContextChange>();
	
	public SameContextChange(String name) {
		context = name;
		changes.clear();
		addChanges.clear();
		deleteChanges.clear();
	}
	public SameContextChange(ContextChange change){
		context = change.getContext();
		changes.clear();
		addChanges.clear();
		deleteChanges.clear();
		changes.add(change);
		if(change.getOperate() == 1)
			addChanges.add(change);
		else if(change.getOperate() == 2)
			deleteChanges.add(change);
	}
	public void addChange(ContextChange change) {
		changes.add(change);
		if(change.getOperate() == 1)
			addChanges.add(change);
		else if(change.getOperate() == 2)
			deleteChanges.add(change);
	}
	
	public String getContext() {
		return context;
	}
	
	public ArrayList<ContextChange> getChanges() {
		return changes;
	}
	
	public ArrayList<ContextChange> getAddChanges() {
		return addChanges;
	}
	
	public ArrayList<ContextChange> getDeleteChanges() {
		return deleteChanges;
	}
	public String toString(){
		
		return "change:"+changes.toString()+"\n"+"add:"+addChanges.toString()+"\n"+"del:"+deleteChanges.toString()+"\n";
		
	}
}
