package org.seccanj.clans.model;

import java.util.ArrayList;
import java.util.List;

import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.model.Direction.Directions;
import org.seccanj.clans.model.entities.Individual;

public class ModelUtils {

	public static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	
	public static final int BORDERS[][] = {
			{ -1, 0 },
			{ -1, +1},
			{ -1, 0 }
	};
	
	public static List<Position> getAdjacentPositions(Position p) {
		List<Position> result = new ArrayList<>();
		
		for (int i=0; i<BORDERS.length; i++) {
			int row = p.row - 1 + i;
			int deltas[] = BORDERS[i];
			for (int j=0; j<deltas.length; j++) {
				int column = p.column + deltas[j];
				
				if (inWorld(row, column) && !(row == p.row && column == p.column)) {
					result.add(new Position(row, column));
				}
			}
		}
		
		return result;
	}
	
	public static List<Position> getAdjacentPositions2(Position p) {
		List<Position> result = new ArrayList<>();
		
		for (int i=0; i<Directions.directions.length; i++) {
			Position q = p.clone();
			q.move(Directions.directions[i].d, 1);
			
			if (inWorld(q.row, q.column)) {
				result.add(q);
			}
		}
		
		return result;
	}
	
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
		return true;
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

	public static String getRandomName(Individual individual) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(individual.getClass().getName());
		sb.append(' ');
		
		for (int i=0; i<10; i++) {
			int index = (int)(Math.random()*36);
			
			sb.append(ALPHABET.substring(index, index+1));
		}
		
		return sb.toString();
	}
	
}
