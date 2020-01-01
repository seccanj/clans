package org.seccanj.clans.model.entities;

import org.seccanj.clans.model.BaseEntity;
import org.seccanj.clans.model.Dna;
import org.seccanj.clans.model.EntityType;
import org.seccanj.clans.model.Food;
import org.seccanj.clans.model.Gene.GeneType;

public class Plant extends BaseEntity implements Food {

	private int splitPeriod;
	
	public Plant() {
		super(getDefaultDna());
		setEntityType(EntityType.plant);
	}
	
	public Plant(Dna dna) {
		super(dna);
		setEntityType(EntityType.plant);
	}

	public boolean shouldSplit() {
		return getAge() % splitPeriod == 0;
	}

	@Override
	protected void setCapabilities() {
		setMaxEnergy((int) dna.get(GeneType.maxEnergy).getValue());
		setMaxHealth((int) dna.get(GeneType.maxHealth).getValue());
		setSplitPeriod((int) dna.get(GeneType.plantSplitPeriod).getValue());
		setMaxActionPoints((int) dna.get(GeneType.maxActionPoints).getValue());
		setMaxSpeed((int) dna.get(GeneType.maxSpeed).getValue());
	}
	
	@Override
	protected void initVitals() {
		setEnergy(getMaxEnergy());
		setHealth(getMaxHealth());
		setActionPoints(getMaxActionPoints());
	}

	public static Dna getDefaultDna() {
		Dna result = new Dna();
		
		result.put(GeneType.sightDistance, GeneType.sightDistance.getGene(0));
		result.put(GeneType.plantSplitPeriod, GeneType.plantSplitPeriod.getGene());
		result.put(GeneType.maxEnergy, GeneType.maxEnergy.getGene());
		result.put(GeneType.maxHealth, GeneType.maxHealth.getGene());
		result.put(GeneType.maxActionPoints, GeneType.maxActionPoints.getGene());
		result.put(GeneType.maxSpeed, GeneType.maxSpeed.getGene(0));

		return result;
	}

	public int getSplitPeriod() {
		return splitPeriod;
	}

	public void setSplitPeriod(int splitPeriod) {
		this.splitPeriod = splitPeriod;
	}
	
}
