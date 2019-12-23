package org.seccanj.clans.model;

public abstract class BaseEntity implements Entity {

	public Position position;
	public Direction direction;
	public double speed;
	boolean moved;
	
	@Override
	public void moveTo(Position p) {
		this.position = p.clone();
	}
	
	@Override
	public Position getPosition() {
		return position.clone();
	}
	
	@Override
	public boolean shouldMove() {
		return !moved;
	}
	
	public void setHasMoved() {
		moved = true;
	}

	public void resetHasMoved() {
		moved = false;
	}
}
