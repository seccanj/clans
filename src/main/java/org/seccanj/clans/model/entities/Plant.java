package org.seccanj.clans.model.entities;

import org.seccanj.clans.model.BaseEntity;
import org.seccanj.clans.model.Food;

public class Plant extends BaseEntity implements Food {

	private static final double DEFAULT_PLANT_ENERGY = 10;
	
	public double energy = DEFAULT_PLANT_ENERGY;
	
	@Override
	public double getEnergy() {
		return energy;
	}

	@Override
	public void live() {
		// TODO
		System.out.println(toString() + " - Living...");
	}
}
