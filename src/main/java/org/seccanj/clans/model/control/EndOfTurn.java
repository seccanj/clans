package org.seccanj.clans.model.control;

import org.seccanj.clans.model.entities.Individual;

public class EndOfTurn {

	private Individual individual;

	public EndOfTurn(Individual individual) {
		this.individual = individual;
	}
	
	public Individual getIndividual() {
		return individual;
	}

	public void setIndividual(Individual individual) {
		this.individual = individual;
	}
	
}
