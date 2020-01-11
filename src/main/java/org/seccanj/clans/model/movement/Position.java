package org.seccanj.clans.model.movement;

import java.util.ArrayList;
import java.util.List;

import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.model.World;

public class Position {

	public static final Position ORIGIN = new Position(0, 0);
	
	public int row;
	public int column;

	public Position() {
	}

	public Position(int row, int column) {
		this.row = row;
		this.column = column;
	}

	public static Position getRandom() {
		Position result = new Position();
		
		do {
			result.row = (int) Math.round(Math.random() * (Configuration.WORLD_MAX_ROWS - 1));
			result.column = (int) Math.round(Math.random() * (Configuration.WORLD_MAX_COLUMNS - 1));
		} while (!World.getWorld().isFree(result));
		
		return result;
	}

	public void setTo(Position p) {
		row = p.row;
		column = p.column;
	}
	
	public Position move(Direction d, double distance) {
		Position p = simulateMove(d, distance);
		
		row = p.row;
		column = p.column;
		
		return this;
	}
	
	public Position simulateMove(Direction d, double distance) {
		Position p = new Position();

		p.row = (int)Math.round(row + d.y * distance);
		p.column = (int)Math.round(column + d.x * distance);
		
		if (p.row >= Configuration.WORLD_MAX_ROWS) {
			p.row = Configuration.WORLD_MAX_ROWS;
		}
		
		if (p.row < 0) {
			p.row = 0;
		}
		
		if (p.column >= Configuration.WORLD_MAX_COLUMNS) {
			p.column = Configuration.WORLD_MAX_COLUMNS;
		}
		
		if (p.column < 0) {
			p.column = 0;
		}
		
		return p;
	}
	
	public List<Position> getAdjacentPositions() {
		List<Position> result = new ArrayList<>();

		long alternate = column % 2;

		int upper = 0;
		int lower = 0;

		if (alternate == 0) {
			lower = -1;
		} else {
			upper = -1;
		}

		result.add(new Position(row-1, column));
		result.add(new Position(row + upper, column-1));
		result.add(new Position(row + upper, column+1));
		result.add(new Position(row + lower, column-1));
		result.add(new Position(row + lower, column+1));
		result.add(new Position(row+1, column));
		
		return result;
	}
	
	public boolean isAdjacent(Position p) {
		boolean result = false;
		
		long alternate = column % 2;

		if (alternate == 0) {
			if (p.row == row) {
				return (p.column == column) || (p.column == column-1) || (p.column == column+1);
			} else if (p.row == row-1) {
				return p.column == column;
			} else if (p.row == row+1) {
				return (p.column == column) || (p.column == column-1) || (p.column == column+1);
			}
		} else {
			if (p.row == row) {
				return (p.column == column) || (p.column == column-1) || (p.column == column+1);
			} else if (p.row == row-1) {
				return (p.column == column) || (p.column == column-1) || (p.column == column+1);
			} else if (p.row == row+1) {
				return p.column == column;
			}
		}

		return result;
	}
	
	public Position clone() {
		return new Position(row, column);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Position)) {
			return false;
		}
		
		return row == ((Position)obj).row && column == ((Position)obj).column;
	}

	@Override
	public int hashCode() {
		return row * 10000000 + column;
	}
	
	@Override
	public String toString() {
		return "["+row+", "+column+"]";
	}
}
