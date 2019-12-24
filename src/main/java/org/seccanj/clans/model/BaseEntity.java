package org.seccanj.clans.model;

import java.util.HashSet;
import java.util.Set;

import org.seccanj.clans.configuration.Configuration;

import com.google.gson.Gson;

public abstract class BaseEntity implements Entity {

	public Set<State> states = new HashSet<>();
	public Position position;
	private Direction direction;
	private double maxSpeed = Configuration.INDIVIDUAL_DEFAULT_MAX_SPEED;
	private double speed;
	public boolean lived;
	public int totalActionPoints = Configuration.INDIVIDUAL_DEFAULT_ACTION_POINTS;
	public int leftActionPoints = totalActionPoints;
	
	@Override
	public void moveTo(Position p) {
		this.position = p.clone();
	}
	
	@Override
	public Position getPosition() {
		return position.clone();
	}
	
	@Override
	public boolean shouldLive() {
		return !lived;
	}
	
	public void setHasLived() {
		lived = true;
	}

	public void resetHasLived() {
		lived = false;
		leftActionPoints = totalActionPoints;
	}

	public void useActionPoints(int points) {
		if (leftActionPoints >= points) {
			leftActionPoints -= points;
		} else {
			throw new RuntimeException("Insufficient action points ("+leftActionPoints+"). Can't consume "+points+" more points.");
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

	public double getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(double maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
	
	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
