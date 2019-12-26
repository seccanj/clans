package org.seccanj.clans.model;

import org.seccanj.clans.configuration.Configuration;

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
	
	public Position move(Direction d, double distance) {
		//System.out.println("move() to direction "+d.toString());
		
		row = (int)Math.round(row + d.y * distance);
		column = (int)Math.round(column + d.x * distance);
		
		if (row >= Configuration.WORLD_MAX_ROWS) {
			row = Configuration.WORLD_MAX_ROWS;
		}
		
		if (row < 0) {
			row = 0;
		}
		
		if (column >= Configuration.WORLD_MAX_COLUMNS) {
			column = Configuration.WORLD_MAX_COLUMNS;
		}
		
		if (column < 0) {
			column = 0;
		}
		
		return this;
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
