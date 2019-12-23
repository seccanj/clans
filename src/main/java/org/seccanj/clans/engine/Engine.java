package org.seccanj.clans.engine;

import java.util.Optional;

import org.seccanj.clans.model.Entity;
import org.seccanj.clans.model.World;
import org.seccanj.clans.model.entities.Plant;

public class Engine {

	private World world;
	
	public Engine(World world) {
		this.world = world;
	}
	
	public void start() {
		while(true) {
			System.out.println("  -- Number of plants: " +world.entities.stream()
				.filter(d -> d.getClass().equals(Plant.class))
				.count());
			
			Optional<Entity> e = world.entities.stream()
				.filter(Entity::shouldMove)
				.findFirst();
			
			if (e.isPresent()) {
				e.get().live();
				e.get().setHasMoved();
			} else {
				world.entities.stream()
					.forEach(f -> f.resetHasMoved());
			}
		}
	}
	
}
