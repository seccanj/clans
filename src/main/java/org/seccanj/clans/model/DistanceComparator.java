package org.seccanj.clans.model;

import java.util.Comparator;

import org.seccanj.clans.util.Utils;

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
					Utils.getDistance(source, e1.getPosition()), 
					Utils.getDistance(source, e2.getPosition()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
