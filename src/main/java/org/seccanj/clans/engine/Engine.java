package org.seccanj.clans.engine;

import java.util.HashSet;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.seccanj.clans.model.Cell;
import org.seccanj.clans.model.Entity;
import org.seccanj.clans.model.World;
import org.seccanj.clans.model.entities.Individual;

public class Engine {

	private World world;
	
	private KieSession kSession;
	
	public Engine(World world) {
		this.world = world;
	}
	
	public void start() {
		
        // load up the knowledge base
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kContainer = kieServices.getKieClasspathContainer();
	    
    	kSession = kContainer.newKieSession("ksession-rules");

        // go !
        kSession.insert(World.getWorld());
        
        Set<FactHandle> individualHandles = new HashSet<>();
        
        for (Entity e : World.getWorld().entities) {
        	individualHandles.add(kSession.insert(e));
   			kSession.insert(new Cell(e.getPosition(), e));
        }

		while(true) {
			System.out.println(">>>>>>>>>>>>>>>>>> START NEW TURN: "+world.currentTurn++);

	        kSession.fireAllRules();
	        
	        individualHandles.stream()
	        	.forEach(f -> {
	        		Entity e = (Entity)kSession.getObject(f);
	        		
	        		if (e instanceof Individual) {
		        		((Individual)e).resetHasLived();
		        		kSession.update(f, e, "leftActionPoints");
	        		}
	        	});
	        
	        removeEndOfTurns();
	        
	        removeActionDones();
		}
	}

	private void removeEndOfTurns() {
        QueryResults results = kSession.getQueryResults( "End of turns" );
        System.out.println( "we have " + results.size() + " End of turns" );

        for ( QueryResultsRow row : results ) {
        	kSession.delete(row.getFactHandle("$endOfTurn"));
        }
	}
	
	private void removeActionDones() {
        QueryResults results = kSession.getQueryResults( "Action dones" );
        System.out.println( "we have " + results.size() + " Action dones" );

        for ( QueryResultsRow row : results ) {
        	kSession.delete(row.getFactHandle("$actionDone"));
        }
	}
	
}
