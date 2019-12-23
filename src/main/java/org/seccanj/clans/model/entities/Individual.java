package org.seccanj.clans.model.entities;

import java.util.Date;

import org.seccanj.clans.model.BaseEntity;
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
	public double health = 100;
	public double energy;
	public boolean me;
	private RelativeCell target;

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

	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public void addEnergy(double energy) {
		this.energy += energy;
	}

	public void eat(Food food) {
		System.out.println("   --- Eating "+food.getEnergy());
		addEnergy(food.getEnergy());
	}
	
	@Override
	public String toString() {
		return getName();
	}

	@Override
	public void live() {
		// TODO
		System.out.println(toString() + " - Living... ("+position.row+", "+position.column+")");
        
        me = true;
        
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
