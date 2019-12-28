package org.seccanj.clans.model;

public class Cell {

	private Position position;
	private Entity entity;
	
	public Cell() {
	}
	
	public Cell(Position position, Entity entity) {
		this.setPosition(position);
		this.setEntity(entity);
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}	
}
