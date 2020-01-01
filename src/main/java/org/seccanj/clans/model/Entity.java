package org.seccanj.clans.model;

public interface Entity {

	EntityType getEntityType();
	String getEntityTypeName();
	
	void moveTo(Position p, int distance);
	Position getPosition();
	
	void resetActionPoints();
	Dna getDna();
	void setDna(Dna dna);
}
