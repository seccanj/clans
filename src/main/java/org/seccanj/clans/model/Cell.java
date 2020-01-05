package org.seccanj.clans.model;

public class Cell {

	private Position position;
	private Being entity;
	
	public Cell() {
	}
	
	public Cell(Position position, Being entity) {
		this.setPosition(position);
		this.setEntity(entity);
	}

	public Position getPosition() {
		return position;
	}

	public void setPosition(Position position) {
		this.position = position;
	}

	public Being getEntity() {
		return entity;
	}

	public void setEntity(Being entity) {
		this.entity = entity;
	}	
}
