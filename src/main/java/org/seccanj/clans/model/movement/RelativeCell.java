package org.seccanj.clans.model.movement;

import org.seccanj.clans.model.being.Being;

public class RelativeCell extends Cell {

	private Direction direction;
	private double distance;
	private boolean valid;

	public RelativeCell() {
		super();
	}

	public RelativeCell(Cell c) {
		super(c.getPosition(), c.getEntity());
	}
	
	public RelativeCell(Cell cell, Direction direction, double distance) {
		this(cell);
		this.direction = direction;
		this.distance = distance;
	}

	public RelativeCell(Position position, Being entity, Direction direction, double distance) {
		super(position, entity);
		this.direction = direction;
		this.distance = distance;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
