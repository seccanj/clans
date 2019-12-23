package org.seccanj.clans.model;

public interface Entity {

	void moveTo(Position p);
	Position getPosition();
	
	boolean shouldMove();
	void setHasMoved();
	void resetHasMoved();
	
	void live();
	
}
