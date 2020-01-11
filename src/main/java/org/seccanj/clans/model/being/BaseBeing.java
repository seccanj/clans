package org.seccanj.clans.model.being;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.seccanj.clans.model.State;
import org.seccanj.clans.model.World;
import org.seccanj.clans.model.dna.Dna;
import org.seccanj.clans.model.movement.Direction;
import org.seccanj.clans.model.movement.Position;
import org.seccanj.clans.model.movement.Direction.Directions;
import org.seccanj.clans.util.Utils;

public abstract class BaseBeing implements Being {

	public BeingType entityType;
	public Dna dna = new Dna();

	public Set<State> states = new HashSet<>();
	public String name;
	public Position position;
	public Direction direction;
	private long birthTurn;
	private long age;

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
	
	public BaseBeing(Dna dna) {
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

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public Position getPosition() {
		return position.clone();
	}
	
	@Override
	public double getEnergy() {
		return energy;
	}
	
	@Override
	public void setEnergy(double energy) {
		this.energy = energy;
	}

	@Override
	public void addEnergy(double energy) {
		this.energy += energy;
		
		if (this.energy > maxEnergy) {
			this.energy = maxEnergy;
		}
		
		System.out.println("    --- Adding energy: "+energy+". Energy left: "+this.energy);
	}

	@Override
	public void useEnergy(double delta) {
		if (energy >= delta) {
			energy -= delta;
			System.out.println("    --- Using energy: "+delta+". Energy left: "+energy);
		} else {
			energy = 0;
			System.out.println("    --- Energy is 0");
		}
	}

	@Override
	public double getHealth() {
		return health;
	}

	@Override
	public void setHealth(double health) {
		this.health = health;
	}

	@Override
	public void decreaseHealth(double delta) {
		if (health > delta) {
			this.health -= delta;
			System.out.println("    --- Decreasing health: "+delta+". Health left: "+this.health);
		} else {
			health = 0;
			System.out.println("    --- Health is 0");
		}
	}

	@Override
	public void increaseHealth(double delta) {
		this.health += delta;
		System.out.println("    --- Increasing health: "+delta+". Health left: "+this.health);
	}

	@Override
	public void resetActionPoints() {
		setActionPoints(maxActionPoints);
	}

	@Override
	public void useActionPoints(int points) {
		if (getActionPoints() >= points) {
			setActionPoints(getActionPoints() - points);
		} else {
			throw new RuntimeException("Insufficient action points ("+getActionPoints()+"). Can't consume "+points+" more points.");
		}
	}
	
	@Override
	public Direction getDirection() {
		return direction;
	}

	@Override
	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
	@Override
	public boolean hasState(String state) {
		return states.contains(State.valueOf(state));
	}

	public boolean hasStates(String... stateNames) {
		return Arrays.stream(stateNames)
			.map(n -> State.valueOf(n))
			.allMatch(s -> states.contains(s));
	}

	
	
	@Override
	public void addState(String state) {
		states.add(State.valueOf(state));
	}

	@Override
	public void removeState(String state) {
		states.remove(State.valueOf(state));
	}

	@Override
	public int getMaxSpeed() {
		return maxSpeed;
	}

	@Override
	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	@Override
	public int getSpeed() {
		return speed;
	}

	@Override
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	@Override
	public BeingType getBeingType() {
		return entityType;
	}

	@Override
	public String getBeingTypeName() {
		return entityType.name();
	}

	@Override
	public void setEntityType(BeingType entityType) {
		this.entityType = entityType;
	}

	@Override
	public boolean moveTo(Position p, int distance) {
		direction = Utils.getDirection(position, p);
		double targetDistance = Utils.getDistance(position, p);
		
		if (targetDistance < distance) {
			distance = 2;
		}
		
		return move(distance);
	}

	@Override
	public boolean move(Direction d, int distance) {
		direction = d;
		return move(distance);
	}
	
	@Override
	public boolean move(int distance) {
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
		} while (!moved && trials < 10);
		
		return moved;
	}

	@Override
	public Direction pickRandomDirection() {
		return Directions.directions[(int)Math.floor(Math.random() * 6)].d;
	}
	
	@Override
	public Directions pickRandomDirections() {
		return Directions.directions[(int)Math.floor(Math.random() * 6)];
	}
	
	@Override
	public long getBirthTurn() {
		return birthTurn;
	}

	@Override
	public void setBirthTurn(long birthTurn) {
		this.birthTurn = birthTurn;
	}
	
	@Override
	public void addTurn() {
		age++;
	}
	
	@Override
	public long getAge() {
		return age;
	}

	@Override
	public Dna getDna() {
		return dna;
	}

	@Override
	public void setDna(Dna dna) {
		this.dna = dna;
	}

	@Override
	public Set<State> getStates() {
		return states;
	}

	@Override
	public void setStates(Set<State> states) {
		this.states = states;
	}

	@Override
	public void setPosition(Position position) {
		this.position = position;
	}
	
	@Override
	public int getMaxActionPoints() {
		return maxActionPoints;
	}

	@Override
	public void setMaxActionPoints(int maxActionPoints) {
		this.maxActionPoints = maxActionPoints;
	}

	@Override
	public double getMaxEnergy() {
		return maxEnergy;
	}

	@Override
	public void setMaxEnergy(double maxEnergy) {
		this.maxEnergy = maxEnergy;
	}

	@Override
	public double getMaxHealth() {
		return maxHealth;
	}

	@Override
	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}

	@Override
	public int getActionPoints() {
		return actionPoints;
	}

	@Override
	public void setActionPoints(int actionPoints) {
		this.actionPoints = actionPoints;
	}

	@Override
	public double getMaxSightDistance() {
		return maxSightDistance;
	}

	@Override
	public void setMaxSightDistance(double maxSightDistance) {
		this.maxSightDistance = maxSightDistance;
	}

	@Override
	public double getSightDistance() {
		return sightDistance;
	}

	@Override
	public void setSightDistance(double sightDistance) {
		this.sightDistance = sightDistance;
	}

	@Override
	public void setAge(long age) {
		this.age = age;
	}

	@Override
	public String toString() {
		//Gson gson = new Gson();
		return getName(); //gson.toJson(this);
	}
}
