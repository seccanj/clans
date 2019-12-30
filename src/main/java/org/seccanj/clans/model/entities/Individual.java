package org.seccanj.clans.model.entities;

import java.util.HashMap;
import java.util.Map;

import org.seccanj.clans.model.BaseEntity;
import org.seccanj.clans.model.Direction;
import org.seccanj.clans.model.Direction.Directions;
import org.seccanj.clans.model.EntityType;
import org.seccanj.clans.model.Food;
import org.seccanj.clans.model.Gene;
import org.seccanj.clans.model.Gene.GeneType;
import org.seccanj.clans.model.RelativeCell;

public class Individual extends BaseEntity implements Food {

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
	public String name;
	public RelativeCell target;

	public Individual() {
		super(getDefaultDna());
		setEntityType(EntityType.individual);
	}

	public Individual(Map<GeneType, Gene> dna) {
		super(dna);
		setEntityType(EntityType.individual);
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void eat(Food food) {
		System.out.println("   --- Eating "+food.getEnergy());
	}

	public Direction pickRandomDirection() {
		return Directions.directions[(int)Math.floor(Math.random() * 6)].d;
	}
	
	public Directions pickRandomDirections() {
		return Directions.directions[(int)Math.floor(Math.random() * 6)];
	}
	
	public RelativeCell getTarget() {
		return target;
	}

	public void setTarget(RelativeCell target) {
		this.target = target;
	}
	
	public static Map<GeneType, Gene> getDefaultDna() {
		Map<GeneType, Gene> result = new HashMap<>();
		
		result.put(GeneType.sightDistance, GeneType.sightDistance.getGene());
		result.put(GeneType.maxEnergy, GeneType.maxEnergy.getGene());
		result.put(GeneType.maxHealth, GeneType.maxHealth.getGene());
		result.put(GeneType.maxActionPoints, GeneType.maxActionPoints.getGene());
		result.put(GeneType.maxSpeed, GeneType.maxSpeed.getGene());

		return result;
	}
	
}
