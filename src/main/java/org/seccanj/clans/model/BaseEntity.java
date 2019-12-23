package org.seccanj.clans.model;

public abstract class BaseEntity implements Entity {

	public Position position;
	public Direction direction;
	public double speed;
	
	@Override
	public void moveTo(Position p) {
		this.position = p.clone();
	}
	
	@Override
	public Position getPosition() {
		return position.clone();
	}
}
