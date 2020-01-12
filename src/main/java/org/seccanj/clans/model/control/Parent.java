package org.seccanj.clans.model.control;

import org.seccanj.clans.model.being.Individual;

public class Parent {

	private Individual parent;
	private Individual child;
	
	public Individual getParent() {
		return parent;
	}

	public void setParent(Individual parent) {
		this.parent = parent;
	}

	public Individual getChild() {
		return child;
	}

	public void setChild(Individual child) {
		this.child = child;
	}

	public Parent(Individual parent, Individual child) {
		super();
		this.parent = parent;
		this.child = child;
	}
	
	
}
