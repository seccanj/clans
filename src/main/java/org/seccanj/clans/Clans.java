package org.seccanj.clans;

import java.util.Date;

import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.engine.Engine;
import org.seccanj.clans.model.ModelUtils;
import org.seccanj.clans.model.Position;
import org.seccanj.clans.model.World;
import org.seccanj.clans.model.entities.Individual;
import org.seccanj.clans.model.entities.Individual.Gender;
import org.seccanj.clans.model.entities.Plant;

/**
 * Clans
 *
 */
public class Clans {
	
	private static World createWorld() {
		World world = World.getWorld();
		
		Date now = new Date();
		
		for (int i=0; i<Configuration.NUM_INITIAL_INDIVIDUALS; i++) {
			Individual individual = new Individual();
			individual.birth = now;
			individual.name = ModelUtils.getRandomName(individual);
			individual.position = Position.getRandom();
			//individual.energy = Math.random() * Configuration.INDIVIDUAL_DEFAULT_ENERGY;
			individual.gender = Gender.getRandom();
			
			world.setEntity(individual.position, individual);
		}
		
		for (int i=0; i<Configuration.NUM_INITIAL_PLANTS; i++) {
			Plant plant = new Plant();
			plant.position = Position.getRandom();
			//plant.energy = Math.random() * Configuration.PLANT_DEFAULT_ENERGY;
			
			world.setEntity(plant.position, plant);
		}
		
		return world;
	}
	
    public static void main( String[] args ) {
        try {
        	World world = createWorld();
        	Engine engine = new Engine(world);
        	
        	engine.start();
      
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
