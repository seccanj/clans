package org.seccanj.clans.model;

import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.model.Direction.Directions;

public class ModelUtils {

	public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static double getDistance(Position a, Position b) {
		return Math.sqrt(Math.pow(a.row - b.row, 2) + Math.pow(a.column - b.column, 2));
	}
	
	public static Direction getDirection(Position a, Position b) {
		return new Direction(b.column - a.column, b.row - a.row).normalize();
	}
	
	public static Position move(Position p, Direction d, double distance) {
		return p.clone().move(d, distance);
	}
	
	public boolean isBetween(Direction d, Direction from, Direction to) {
		// TODO
		throw new UnsupportedOperationException();
	}
	
	public static Direction addDirection(Direction a, Direction d) {
		return new Direction(a.x + d.x, a.y + d.y);
	}
	
	public static Direction subtractDirection(Direction a, Direction d) {
		return new Direction(a.x - d.x, a.y - d.y);
	}
	
	public static Direction multiplyDirection(Direction a, Direction d) {
		return new Direction(a.x * d.x, a.y * d.y);
	}
	
	public static Direction divideDirection(Direction a, Direction d) {
		return new Direction(a.x / d.x, a.y / d.y);
	}
	
	public static boolean inWorld(int row, int column) {
		return (row >= 0 && row < Configuration.WORLD_MAX_ROWS && column >= 0 && column < Configuration.WORLD_MAX_COLUMNS);
	}

	public static String getRandomName(BaseEntity entity) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(entity.getClass().getName());
		sb.append(' ');
		
		for (int i=0; i<10; i++) {
			int index = (int)(Math.random()*36);
			
			sb.append(ALPHABET.substring(index, index+1));
		}
		
		return sb.toString();
	}

	public static Directions getClosestDirection(Direction dir) {
		if (dir.x < 0) {
			if (dir.y < 0) {
				return Directions.southwest;
			} else {
				return Directions.northwest;
			}
		} else if (dir.x == 0) {
			if (dir.y < 0) {
				return Directions.south;
			} else {
				return Directions.north;
			}
		} else {
			if (dir.y < 0) {
				return Directions.southeast;
			} else {
				return Directions.northeast;
			}
		}
	}
	
}
