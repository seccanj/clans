package org.seccanj.clans.model.control;

import org.seccanj.clans.model.being.Being;
import org.seccanj.clans.model.being.BeingType;

public class BeingTypeFilter implements BeingFilter {

	private BeingType beingType;
	
	public BeingTypeFilter(Object... params) {
		if (params != null && params.length >= 1) {
			beingType = BeingType.valueOf((String)params[0]);
		}
	}
	
	@Override
	public boolean filter(Being other) {
		return other.getBeingType() == beingType;
	}
}
