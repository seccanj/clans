package org.seccanj.clans.engine;

import org.seccanj.clans.model.Entity;
import org.seccanj.clans.model.World;

public class Engine {

	private World world;
	
	public Engine(World world) {
		this.world = world;
	}
	
	public void start() {
		while(true) {
			for (Entity e : world.entities) {
				e.live();
			}
		}
	}
	
}
