package org.seccanj.clans;

import java.util.Date;

/*
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
*/

import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.engine.Engine;
import org.seccanj.clans.model.ModelUtils;
import org.seccanj.clans.model.Position;
import org.seccanj.clans.model.World;
import org.seccanj.clans.model.entities.Individual;
import org.seccanj.clans.model.entities.Individual.Gender;

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
			individual.energy = Math.random() * Configuration.INDIVIDUAL_DEFAULT_ENERGY;
			individual.gender = Gender.getRandom();
			
			world.setEntity(individual.position, individual);
		}
		
		return world;
	}
	
    public static void main( String[] args ) {
        try {
        	/*
	        // load up the knowledge base
	        KieServices ks = KieServices.Factory.get();
		    KieContainer kContainer = ks.getKieClasspathContainer();
		    
	    	KieSession kSession = kContainer.newKieSession("ksession-rules");
	
	        // go !
	        kSession.insert(new World());
            for (int row = 0; row < 9; row++) {
            	for (int column = 0; column < 9; column++) {
           			kSession.insert(new Position(row, column));
            	}
            }
            
            kSession.startProcess("sudoku");
            kSession.fireAllRules();
            */
        	
        	World world = createWorld();
        	Engine engine = new Engine(world);
        	
        	engine.start();
      
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
