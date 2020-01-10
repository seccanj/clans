package org.seccanj.clans.model;

public interface Being {

	BeingType getEntityType();
	String getEntityTypeName();
	
	boolean moveTo(Position p, int distance);
	Position getPosition();
	
	void resetActionPoints();
	Dna getDna();
	void setDna(Dna dna);
	String getName();
}
