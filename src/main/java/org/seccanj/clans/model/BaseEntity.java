package org.seccanj.clans.model;

import java.util.HashSet;
import java.util.Set;

import org.seccanj.clans.util.Utils;

import com.google.gson.Gson;

public abstract class BaseEntity implements Entity {

	public EntityType entityType;
	public Dna dna = new Dna();

	public Set<State> states = new HashSet<>();
	public String name;
	public Position position;
	public Direction direction;
	private long birthTurn;

	public int maxActionPoints;
	private int actionPoints;
	
	public double maxEnergy;
	public double energy;
	
	public double maxHealth;
	public double health;
	
	public int maxSpeed;
	public int speed;

	public double maxSightDistance;
	public double sightDistance;
	
	public BaseEntity(Dna dna) {
		this.dna = dna;
	}

	public void init(String name, Position position, long birthTurn) {
		setCapabilities();
		initVitals();

		setBirthTurn(birthTurn);
		setName(name);
		setPosition(position);
	}
	
	protected abstract void setCapabilities();
	
	protected abstract void initVitals();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Position getPosition() {
		return position.clone();
	}
	
	public double getEnergy() {
		return energy;
	}
	
	public void setEnergy(double energy) {
		this.energy = energy;
	}

	public void addEnergy(double energy) {
		this.energy += energy;
		System.out.println("    --- Adding energy: "+energy+". Energy left: "+this.energy);
	}

	public void useEnergy(double delta) {
		if (energy >= delta) {
			energy -= delta;
			System.out.println("    --- Using energy: "+delta+". Energy left: "+energy);
		} else {
			energy = 0;
			System.out.println("    --- Energy is 0");
		}
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public void decreaseHealth(double delta) {
		if (health > delta) {
			this.health -= delta;
			System.out.println("    --- Decreasing health: "+delta+". Health left: "+this.health);
		} else {
			health = 0;
			System.out.println("    --- Health is 0");
		}
	}

	public void increaseHealth(double delta) {
		this.health += delta;
		System.out.println("    --- Increasing health: "+delta+". Health left: "+this.health);
	}

	@Override
	public void resetActionPoints() {
		setActionPoints(maxActionPoints);
	}

	public void useActionPoints(int points) {
		if (getActionPoints() >= points) {
			setActionPoints(getActionPoints() - points);
		} else {
			throw new RuntimeException("Insufficient action points ("+getActionPoints()+"). Can't consume "+points+" more points.");
		}
	}
	
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	public boolean hasState(String state) {
		return states.contains(State.valueOf(state));
	}

	public void addState(String state) {
		states.add(State.valueOf(state));
	}

	public void removeState(String state) {
		states.remove(State.valueOf(state));
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	@Override
	public EntityType getEntityType() {
		return entityType;
	}

	@Override
	public String getEntityTypeName() {
		return entityType.name();
	}

	public void setEntityType(EntityType entityType) {
		this.entityType = entityType;
	}

	@Override
	public void moveTo(Position p, int distance) {
		direction = Utils.getDirection(position, p);
		double targetDistance = Utils.getDistance(position, p);
		
		if (targetDistance < distance) {
			distance = 1;
		}
		
		move(distance);
	}

	public void move(Direction d, int distance) {
		direction = d;
		move(distance);
	}
	
	public void move(int distance) {
		boolean moved = false;
		int trials = 0;
		
		do {
			Position p = position.simulateMove(direction, distance);
			
			if (World.getWorld().isFree(p)) {
				position.setTo(p);
				World.getWorld().moveEntity(this, position);
				moved = true;
			} else {
				direction = Direction.getRandom().d;
				trials++;
			}
		} while (!moved || trials > 10);
	}

	public long getBirthTurn() {
		return birthTurn;
	}

	public void setBirthTurn(long birthTurn) {
		this.birthTurn = birthTurn;
	}
	
	public long getAge() {
		return World.getWorld().currentTurn - birthTurn;
	}

	@Override
	public Dna getDna() {
		return dna;
	}

	@Override
	public void setDna(Dna dna) {
		this.dna = dna;
	}

	public Set<State> getStates() {
		return states;
	}

	public void setStates(Set<State> states) {
		this.states = states;
	}

	public void setPosition(Position position) {
		this.position = position;
	}
	
	public int getMaxActionPoints() {
		return maxActionPoints;
	}

	public void setMaxActionPoints(int maxActionPoints) {
		this.maxActionPoints = maxActionPoints;
	}

	public double getMaxEnergy() {
		return maxEnergy;
	}

	public void setMaxEnergy(double maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getActionPoints() {
		return actionPoints;
	}

	public void setActionPoints(int actionPoints) {
		this.actionPoints = actionPoints;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}

	public double getMaxSightDistance() {
		return maxSightDistance;
	}

	public void setMaxSightDistance(double maxSightDistance) {
		this.maxSightDistance = maxSightDistance;
	}

	public double getSightDistance() {
		return sightDistance;
	}

	public void setSightDistance(double sightDistance) {
		this.sightDistance = sightDistance;
	}
}
