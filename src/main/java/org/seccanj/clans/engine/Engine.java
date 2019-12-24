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
		
		System.out.println(">>>>>>>>>>>>>>>>>> START NEW TURN: "+world.currentTurn);
		
		while(true) {
			System.out.println("Number of plants: " +world.entities.stream()
				.filter(d -> d.getClass().equals(Plant.class))
				.count());
			
			Optional<Entity> e = world.entities.stream()
				.filter(Entity::shouldLive)
				.findFirst();
			
			if (e.isPresent()) {
				Entity en = e.get();

				en.live();
				en.setHasLived();
				
			} else {
				world.entities.stream()
					.forEach(f -> f.resetHasLived());
				world.currentTurn++;
				
				System.out.println(">>>>>>>>>>>>>>>>>> START NEW TURN: "+world.currentTurn);
			}
		}
	}
	
}
