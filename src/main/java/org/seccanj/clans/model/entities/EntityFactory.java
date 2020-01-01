package org.seccanj.clans.model.entities;

import org.seccanj.clans.model.BaseEntity;
import org.seccanj.clans.model.Dna;
import org.seccanj.clans.model.Entity;
import org.seccanj.clans.model.EntityType;

public class EntityFactory {

	public static BaseEntity createEntity(EntityType entityType) {
		switch (entityType) {
		case individual:
			return new Individual();
		case plant:
			return new Plant();
		default:
			return null;
		}
	}
	
	public static BaseEntity createEntity(EntityType entityType, Entity parent1, Entity parent2) {
		
		Dna dna = Dna.getMixedDna(parent1.getDna(), parent2.getDna());
		
		switch (entityType) {
		case individual:
			return new Individual(dna);
		case plant:
			return new Plant(dna);
		default:
			return null;
		}
	}
	
}
