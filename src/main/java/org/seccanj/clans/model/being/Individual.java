package org.seccanj.clans.model.being;

import org.seccanj.clans.model.dna.Dna;
import org.seccanj.clans.model.dna.Gene.GeneType;
import org.seccanj.clans.model.movement.RelativeCell;

public class Individual extends BaseBeing implements Food {

	public enum Gender {
		male,
		female;
		
		public static Gender getRandom() {
			if (Math.round(Math.random()) == 0) {
				return male;
			}
			
			return female;
		}
	}
	
	public Gender gender;
	public RelativeCell target;

	public Individual() {
		super(getDefaultDna());
		setEntityType(BeingType.individual);
		gender = Gender.getRandom();
	}

	public Individual(Dna dna) {
		super(dna);
		setEntityType(BeingType.individual);
		gender = Gender.getRandom();
	}	
	
	@Override
	protected void setCapabilities() {
		setMaxSightDistance((int) dna.get(GeneType.sightDistance).getValue());
		setMaxEnergy((int) dna.get(GeneType.maxEnergy).getValue());
		setMaxHealth((int) dna.get(GeneType.maxHealth).getValue());
		setMaxActionPoints((int) dna.get(GeneType.maxActionPoints).getValue());
		setMaxSpeed((int) dna.get(GeneType.maxSpeed).getValue());
	}
	
	@Override
	protected void initVitals() {
		setSightDistance(getMaxSightDistance());
		setEnergy(getMaxEnergy());
		setHealth(getMaxHealth());
		setActionPoints(getMaxActionPoints());
	}
	
	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public void eat(Food food) {
		System.out.println("   --- Eating "+food.getEnergy());
	}

	public RelativeCell getTarget() {
		return target;
	}

	public void setTarget(RelativeCell target) {
		this.target = target;
	}
	
	public static Dna getDefaultDna() {
		Dna result = new Dna();
		
		result.put(GeneType.sightDistance, GeneType.sightDistance.getGene());
		result.put(GeneType.maxEnergy, GeneType.maxEnergy.getGene());
		result.put(GeneType.maxHealth, GeneType.maxHealth.getGene());
		result.put(GeneType.maxActionPoints, GeneType.maxActionPoints.getGene());
		result.put(GeneType.maxSpeed, GeneType.maxSpeed.getGene());

		return result;
	}
}
