package org.seccanj.clans.model;

public interface Entity {

	EntityType getEntityType();
	String getEntityTypeName();
	
	void moveTo(Position p, int distance);
	Position getPosition();
	
	boolean shouldLive();
	void setHasLived();
	void resetHasLived();
	
	void live();
	
}
