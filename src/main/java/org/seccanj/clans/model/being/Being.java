package org.seccanj.clans.model.being;

import java.util.Set;

import org.seccanj.clans.model.State;
import org.seccanj.clans.model.dna.Dna;
import org.seccanj.clans.model.movement.Direction;
import org.seccanj.clans.model.movement.Position;
import org.seccanj.clans.model.movement.Direction.Directions;

public interface Being {

	BeingType getBeingType();
	String getBeingTypeName();
	
	boolean moveTo(Position p, int distance);
	Position getPosition();
	
	void resetActionPoints();
	Dna getDna();
	void setDna(Dna dna);
	String getName();
	void setAge(long age);
	void setSightDistance(double sightDistance);
	double getSightDistance();
	void setMaxSightDistance(double maxSightDistance);
	double getMaxSightDistance();
	void setActionPoints(int actionPoints);
	int getActionPoints();
	void setMaxHealth(double maxHealth);
	double getMaxHealth();
	void setMaxEnergy(double maxEnergy);
	double getMaxEnergy();
	void setMaxActionPoints(int maxActionPoints);
	int getMaxActionPoints();
	void setPosition(Position position);
	void setStates(Set<State> states);
	Set<State> getStates();
	long getAge();
	void addTurn();
	void setBirthTurn(long birthTurn);
	long getBirthTurn();
	Directions pickRandomDirections();
	Direction pickRandomDirection();
	boolean move(int distance);
	boolean move(Direction d, int distance);
	void setEntityType(BeingType entityType);
	void setSpeed(int speed);
	int getSpeed();
	void setMaxSpeed(int maxSpeed);
	int getMaxSpeed();
	void removeState(String state);
	void addState(String state);
	boolean hasState(String state);
	void setDirection(Direction direction);
	Direction getDirection();
	void useActionPoints(int points);
	void increaseHealth(double delta);
	void decreaseHealth(double delta);
	void setHealth(double health);
	double getHealth();
	void useEnergy(double delta);
	void addEnergy(double energy);
	void setEnergy(double energy);
	double getEnergy();
	void setName(String name);
}
