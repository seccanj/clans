package org.seccanj.clans.model;

import org.seccanj.clans.util.Utils;

public class Direction {

	public double x;
	public double y;
	
	public enum Directions {
		north(new Direction(-1, 0), 0),
		northeast(new Direction(-1, 1), 1),
		southeast(new Direction(0, 1), 2),
		south(new Direction(1, 0), 3),
		southwest(new Direction(0, -1), 4),
		northwest(new Direction(-1, -1), 5);
		
		public static Directions[] directions = new Directions[] {
			north,
			northeast,
			southeast,
			south,
			southwest,
			northwest
		};
		
		public Direction d;
		public int order;
		
		private Directions(Direction d, int order) {
			this.d = d;
			this.order = order;
		}
		
		public Directions getPrevious() {
			return order >= 1 ? directions[order - 1] : directions[5];
		}

		public Directions getNext() {
			return order < 5 ? directions[order + 1] : directions[0];
		}
		
		public Directions getClosest(Direction f) {
			// TODO
			return null;
		}
	}
	
	public Direction() {
	}
	
	public Direction(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Direction add(Direction d) {
		x = x + d.x;
		y = y + d.y;
		
		return this;
	}
	
	public Direction subtract(Direction d) {
		x = x - d.x;
		y = y - d.y;
		
		return this;
	}
	
	public Direction multiply(Direction d) {
		x = x * d.x;
		y = y * d.y;
		
		return this;
	}
	
	public Direction divide(Direction d) {
		x = x / d.x;
		y = y / d.y;
		
		return this;
	}

	public double getLength() {
		return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
	}
	
	public Direction normalize() {
		double len = getLength();
		
		if (len > 0) {
			x = x / len;
			y = y / len;
		}
		
		return this;
	}

	public static Directions getRandom() {
		int dir = (int) Math.floor(Utils.RND.nextDouble() * 6);
		return Directions.directions[dir];
	}
	
	@Override
	public String toString() {
		return "->("+y+", "+x+")";
	}
}
