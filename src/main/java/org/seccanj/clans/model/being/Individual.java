package org.seccanj.clans.model.being;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.seccanj.clans.model.control.ActionDone;
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
	
	private Set<Individual> parents = new HashSet<>();

	public Individual() {
		this(getDefaultDna());
	}

	public Individual(Dna dna) {
		super(dna);
		setEntityType(BeingType.individual);
		gender = Gender.getRandom();
	}	
	
	public Individual(Dna dna, Individual... parents) {
		this(dna);
		setParents(parents);
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
		double foodEnergy = food.getFoodEnergy();
		
		System.out.println("   --- Eating "+foodEnergy);
		
		addEnergy(foodEnergy);
		
		getActionLog().add(new ActionDone(this, "eat", food.getCharacteristics()));
	}

	@Override
	public void decreaseHealth(double delta) {
		super.decreaseHealth(delta);

		if (delta > 10) {
			getActionLog().add(new ActionDone(this, "decreaseHealth"));
		}
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

	@Override
	public double getFoodEnergy() {
		return getEnergy();
	}

	@Override
	public double getFoodHealth() {
		return 0;
	}

	public Set<Individual> getParents() {
		return parents;
	}

	public void setParents(Set<Individual> parents) {
		this.parents = parents;
	}

	public void setParents(Individual... parents) {
		this.parents.addAll(Arrays.asList(parents));
	}

}
