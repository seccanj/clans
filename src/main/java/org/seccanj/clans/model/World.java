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
	
	public long currentTurn = 0;

	public static World getWorld() {
		return world;
	}

	public Entity getEntity(Position p) {
		if (ModelUtils.inWorld(p.row, p.column)) {
			return map[p.row][p.column];
		} else {
			return null;
		}
	}
	
	public void setEntity(Position p, Entity e) {
		if (ModelUtils.inWorld(p.row, p.column)) {
			map[p.row][p.column] = e;
			entities.add(e);
		} else {
			throw new RuntimeException("Coordinates "+p.toString()+" are out of the world.");
		}
	}
	
	public boolean isFree(Position p) {
		if (ModelUtils.inWorld(p.row, p.column)) {
			return map[p.row][p.column] == null;
		} else {
			return false;
		}
	}

	public boolean moveEntity(Position a, Position b) {
		return moveEntity(getEntity(a), b);
	}

	public boolean moveEntity(Entity e, Position p) {
		boolean result = false;
		
		System.out.println("moveEntity "+e.toString()+" to "+p.toString());
		
		if (ModelUtils.inWorld(p.row, p.column)) {
			if (map[p.row][p.column] == null) {
				map[p.row][p.column] = e;
				e.moveTo(p);
				
				result = true;
			}
		}
		
		return result;
	}

	public void removeEntity(Entity e) {
		map[e.getPosition().row][e.getPosition().column] = null;
		entities.remove(e);
	}

	public RelativeCell scanFirst(Position observer, Direction dir, double upToDistance) {
		return scanFirst(observer, ModelUtils.getClosestDirection(dir), upToDistance);
	}
	
	public RelativeCell scanFirst(Position observer, Directions dir, double upToDistance) {
		System.out.println("  Scanning "+dir.name()+"...");
		
		RelativeCell result = null;

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
						prevPos.move(nextDir.d, j-1);
						result = new RelativeCell(prevPos, e, nextDir.d, j);
						break;
					}
				}
				
				if (result == null) {
					thisPos.move(lastDir.d, j);
					Entity e = world.getEntity(thisPos);
					if (e != null) {
						prevPos.move(lastDir.d, j-1);
						result = new RelativeCell(prevPos, e, lastDir.d, j);
						break;
					}
				}
			}
		}		
		
		System.out.println("     Found "+(result != null ? result.position.toString() : "nothing."));
		
		return result;
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
		
		List<Position> positions = ModelUtils.getAdjacentPositions2(p);
		
		for (Position q : positions) {
			result.add(new Cell(q, map[q.row][q.column]));
		}
		
		return result;
	}

	public void executeRuleProcess(String ruleProcess) {
        // load up the knowledge base
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kContainer = kieServices.getKieClasspathContainer();
	    
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
