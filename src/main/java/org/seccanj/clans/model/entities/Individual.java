package org.seccanj.clans.model.entities;

import java.util.Date;

import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.model.BaseEntity;
import org.seccanj.clans.model.Direction;
import org.seccanj.clans.model.EntityType;
import org.seccanj.clans.model.Direction.Directions;
import org.seccanj.clans.model.Food;
import org.seccanj.clans.model.RelativeCell;
import org.seccanj.clans.model.World;

public class Individual extends BaseEntity implements Food {

	public enum Gender {
		male,
		female;
		
		public static Gender getRandom() {
			if (Math.round(Math.random() * 2) == 0) {
				return male;
			}
			
			return female;
		}
	}
	
	public Gender gender;
	public String name;
	public Date birth;
	public double health = Configuration.INDIVIDUAL_DEFAULT_HEALTH;
	public double energy = Configuration.INDIVIDUAL_DEFAULT_ENERGY;
	public boolean me;
	private RelativeCell target;

	public Individual() {
		setEntityType(EntityType.individual);
	}
	
	@Override
	public double getEnergy() {
		return energy;
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

	public Date getBirth() {
		return birth;
	}

	public void setBirth(Date birth) {
		this.birth = birth;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public void decreaseHealth(double delta) {
		this.health -= delta;
		System.out.println("    --- Decreasing health: "+delta+". Health left: "+this.health);
	}

	public void increaseHealth(double delta) {
		this.health += delta;
		System.out.println("    --- Increasing health: "+delta+". Health left: "+this.health);
	}

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public void addEnergy(double energy) {
		this.energy += energy;
		System.out.println("    --- Adding energy: "+energy+". Energy left: "+this.energy);
	}

	public void useEnergy(double energy) {
		this.energy -= energy;
		System.out.println("    --- Using energy: "+energy+". Energy left: "+this.energy);
	}

	public void eat(Food food) {
		System.out.println("   --- Eating "+food.getEnergy());
	}

	public Direction pickRandomDirection() {
		return Directions.directions[(int)Math.floor(Math.random() * 6)].d;
	}
	
	@Override
	public void live() {
        me = true;
        
		System.out.println(" - Living...: "+toString());

		World.getWorld().executeRuleProcess("clans");

        me = false;
	}

	public RelativeCell getTarget() {
		return target;
	}

	public void setTarget(RelativeCell target) {
		this.target = target;
	}
}
