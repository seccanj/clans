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
		
		result.row = (int) Math.round(Math.random() * (Configuration.WORLD_MAX_ROWS - 1));
		result.column = (int) Math.round(Math.random() * (Configuration.WORLD_MAX_COLUMNS - 1));
		
		return result;
	}
	
	public Position move(Direction d, double distance) {
		System.out.println("move() to direction "+d.toString());
		
		row = (int)Math.round(row + d.y * distance);
		column = (int)Math.round(column + d.x * distance);
		
		return this;
	}
	
	public Position clone() {
		return new Position(row, column);
	}
	
	@Override
	public String toString() {
		return "["+row+", "+column+"]";
	}
}
