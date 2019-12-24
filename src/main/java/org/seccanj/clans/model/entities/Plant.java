package org.seccanj.clans.model.entities;

import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.model.BaseEntity;
import org.seccanj.clans.model.EntityType;
import org.seccanj.clans.model.Food;

public class Plant extends BaseEntity implements Food {

	public double energy = Configuration.PLANT_DEFAULT_ENERGY;
	
	public Plant() {
		setEntityType(EntityType.plant);
	}

	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public void live() {
		// TODO
		System.out.println("Plant - Living...");
	}
}
