package org.seccanj.clans.model;

public interface Entity {

	void moveTo(Position p);
	Position getPosition();
	
	boolean shouldLive();
	void setHasLived();
	void resetHasLived();
	
	void live();
	
}
