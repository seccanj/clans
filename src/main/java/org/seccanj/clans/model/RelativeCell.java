package org.seccanj.clans.model;

public class RelativeCell extends Cell {

	public Direction direction;
	public double distance;

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

	public RelativeCell(Position position, Entity entity, Direction direction, double distance) {
		super(position, entity);
		this.direction = direction;
		this.distance = distance;
	}

	
}
