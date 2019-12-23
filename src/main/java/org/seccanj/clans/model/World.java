package org.seccanj.clans.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.model.Direction.Directions;

public class World {
	
	public static World world = new World();
	
	public Entity[][] map = new Entity[Configuration.WORLD_MAX_ROWS][Configuration.WORLD_MAX_COLUMNS];
	
	public Set<Entity> entities = new HashSet<Entity>();

	public static World getWorld() {
		return world;
	}

	public Entity getEntity(Position p) {
		return map[p.row][p.column];
	}
	
	public void setEntity(Position p, Entity e) {
		map[p.row][p.column] = e;
		entities.add(e);
	}
	
	public boolean moveEntity(Position a, Position b) {
		return moveEntity(getEntity(a), b);
	}

	public boolean moveEntity(Entity e, Position p) {
		boolean result = false;
		
		System.out.println("moveEntity "+e.toString()+" to "+p.toString());
		
		if (map[p.row][p.column] == null) {
			map[p.row][p.column] = e;
			e.moveTo(p);
			
			result = true;
		}
		
		return result;
	}

	public void removeEntity(Entity e) {
		map[e.getPosition().row][e.getPosition().column] = null;
	}

	public List<RelativeCell> scan(Position observer, Directions dir, double upToDistance) {
		List<RelativeCell> result = new ArrayList<>();

		Directions prevDir = dir.getPrevious();
		Directions nextDir = dir.getNext();
		Directions lastDir = nextDir.getNext();
		
		for (int i=1; i<upToDistance; i++) {
			Position prevPos = observer.clone().move(prevDir.d, i);
			Position thisPos = observer.clone().move(dir.d, i);

			for (int j=0; j<=upToDistance; j++) {
				if (j < upToDistance) {
					prevPos.move(nextDir.d, j);
					Entity e = world.getEntity(prevPos);
					if (e != null) {
						result.add(new RelativeCell(prevPos, e, nextDir.d, j));
					}
				}
				
				thisPos.move(lastDir.d, j);
				Entity e = world.getEntity(thisPos);
				if (e != null) {
					result.add(new RelativeCell(prevPos, e, lastDir.d, j));
				}
			}
		}		
		
		return result;
	}
	
	public List<Cell> getAdjacentCells(Position p) {
		List<Cell> result = new ArrayList<>();
		
		List<Position> positions = ModelUtils.getAdjacentPositions(p);
		
		for (Position q : positions) {
			result.add(new Cell(q, map[q.row][q.column]));
		}
		
		return result;
	}

	public void executeRuleProcess(String ruleProcess) {
        // load up the knowledge base
        KieServices ks = KieServices.Factory.get();
	    KieContainer kContainer = ks.getKieClasspathContainer();
	    
    	KieSession kSession = kContainer.newKieSession("ksession-rules");

        // go !
        kSession.insert(World.getWorld());
        
        for (Entity e : World.getWorld().entities) {
   			kSession.insert(e);
        }

        kSession.startProcess(ruleProcess);
        kSession.fireAllRules();
	}
	
}
