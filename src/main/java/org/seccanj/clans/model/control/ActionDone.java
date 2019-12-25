package org.seccanj.clans.model.control;

import org.seccanj.clans.model.entities.Individual;

public class ActionDone {

	private Individual individual;
	private String actionType;
	
	public ActionDone(Individual individual, String actionType) {
		super();
		this.individual = individual;
		this.actionType = actionType;
	}
	
	public Individual getIndividual() {
		return individual;
	}
	public void setIndividual(Individual individual) {
		this.individual = individual;
	}
	public String getActionType() {
		return actionType;
	}
	public void setActionType(String actionType) {
		this.actionType = actionType;
	}
	
}
