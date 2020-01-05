package org.seccanj.clans.model;

import java.util.Comparator;

import org.seccanj.clans.util.Utils;

public class DistanceComparator implements Comparator<Being>{

	Position source;
	
	public DistanceComparator(Position source) {
		this.source = source;
	}
	
	@Override
	public int compare(Being e1, Being e2) {
		
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
