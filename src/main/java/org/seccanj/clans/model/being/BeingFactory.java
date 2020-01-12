package org.seccanj.clans.model.being;

import org.seccanj.clans.model.dna.Dna;

public class BeingFactory {

	public static BaseBeing createEntity(BeingType entityType) {
		switch (entityType) {
		case individual:
			return new Individual();
		case plant:
			return new Plant();
		default:
			return null;
		}
	}
	
	public static BaseBeing createEntity(BeingType entityType, Being parent1, Being parent2) {
		
		Dna dna = Dna.getMixedDna(parent1.getDna(), parent2.getDna());
		
		switch (entityType) {
		case individual:
			return new Individual(dna, (Individual)parent1, (Individual)parent2);
		case plant:
			return new Plant(dna);
		default:
			return null;
		}
	}
	
}
