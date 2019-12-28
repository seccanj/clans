package org.seccanj.clans.model;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Entity>{

	Position source;
	
	public DistanceComparator(Position source) {
		this.source = source;
	}
	
	@Override
	public int compare(Entity e1, Entity e2) {
		
		int result = -1;
		
		try {
			result = Double.compare(
					ModelUtils.getDistance(source, e1.getPosition()), 
					ModelUtils.getDistance(source, e2.getPosition()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
