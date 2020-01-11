package org.seccanj.clans.model.control;

import org.seccanj.clans.model.being.Being;
import org.seccanj.clans.model.being.BeingType;
import org.seccanj.clans.model.being.Individual;

public class PartnerFilter implements BeingFilter {

	private Individual subject;
	
	public PartnerFilter(Object... params) {
		if (params != null && params.length >= 1) {
			subject = (Individual)params[0];
		}
	}
	
	@Override
	public boolean filter(Being other) {
		if (other == null || other.getBeingType() != BeingType.individual) {
			return false;
		}
		
		Individual otherSubject = (Individual)other;
		
		return (otherSubject.getGender() != subject.getGender() && otherSubject.getAge() >= 100 && otherSubject.getEnergy() >= 250);
	}
}
