package org.seccanj.clans.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.model.Direction.Directions;
import org.seccanj.clans.model.control.BeingFilter;
import org.seccanj.clans.model.entities.EntityFactory;
import org.seccanj.clans.util.Utils;

public class World {
	
	public static World world = new World();
	
	public Being[][] map = new Being[Configuration.WORLD_MAX_ROWS][Configuration.WORLD_MAX_COLUMNS];
	
	private Set<Being> entities = new HashSet<Being>();
	private Set<Being> newBorn = new HashSet<Being>();
	
	public long currentTurn = 0;

	public static World getWorld() {
		return world;
	}

	public Being createEntity(String entityType) {
		return createEntityNear(entityType, Position.getRandom());
	}
	
	public Being createEntityNear(String entityType, Position position) {
		BaseEntity result = null;
		
		Position newPosition = getFreePositionNear(position);
		
		if (newPosition != null) {
			result = EntityFactory.createEntity(BeingType.valueOf(entityType));
			result.init(Utils.getRandomName(result), newPosition, currentTurn);
			
			setEntity(newPosition, result);
//		} else {
//			System.err.println("Unable to create entity because no cell free near "+position.toString());
		}
		
		return result;
	}
	
	public Being generateEntityNear(String entityType, Position position, Being parent1, Being parent2) {
		BaseEntity result = null;
		
		Position newPosition = getFreePositionNear(position);
		
		if (newPosition != null) {
			result = EntityFactory.createEntity(BeingType.valueOf(entityType), parent1, parent2);
			result.init(Utils.getRandomName(result), newPosition, currentTurn);
			
			setEntity(newPosition, result);
			getNewBorn().add(result);
//		} else {
//			System.err.println("Unable to generate entity because no cell free near "+position.toString());
		}
		
		return result;
	}
	
	public Position getFreePositionNear(Position position) {
		Position result = new Position();
		int trials = 0;
		do {
			result.row = position.row + (int) Math.round(Math.random() * Configuration.NEAR_DISTANCE);
			result.column = position.column + (int) Math.round(Math.random() * Configuration.NEAR_DISTANCE);
		} while (!isFree(result) && trials++ <= Configuration.NEAR_DISTANCE_SQUARE);
	
		if (trials < Configuration.NEAR_DISTANCE_SQUARE) {
			return result;
		}
		
		return null;
	}
	
	public Being getEntity(Position p) {
		if (Utils.inWorld(p.row, p.column)) {
			return map[p.row][p.column];
		} else {
			return null;
		}
	}
	
	public void setEntity(Position p, Being e) {
		if (Utils.inWorld(p.row, p.column)) {
			map[p.row][p.column] = e;
			entities.add(e);
		} else {
			throw new RuntimeException("Coordinates "+p.toString()+" are out of the world.");
		}
	}
	
	public Set<Being> getEntities() {
		return entities;
	}

	public void setEntities(Set<Being> entities) {
		this.entities = entities;
	}
	
	public boolean isFree(Position p) {
		if (Utils.inWorld(p.row, p.column)) {
			return map[p.row][p.column] == null;
		} else {
			return false;
		}
	}

	public boolean moveEntity(Position a, Position b) {
		return moveEntity(getEntity(a), b);
	}

	public boolean moveEntity(Being e, Position p) {
		boolean result = false;
		
		System.out.println("moveEntity "+e.toString()+" to "+p.toString());
		
		if (Utils.inWorld(p.row, p.column)) {
			if (map[p.row][p.column] == null) {
				map[p.row][p.column] = e;
				
				result = true;
			}
		}
		
		return result;
	}

	public void removeEntity(Being e) {
		map[e.getPosition().row][e.getPosition().column] = null;
		entities.remove(e);
	}

	public RelativeCell scanFirst(Position observer, Direction dir, double upToDistance) {
		return scanFirst(null, observer, Utils.getClosestDirection(dir), upToDistance);
	}
	
	public RelativeCell scanFirst(String entityType, Position observer, Direction dir, double upToDistance) {
		return scanFirst(entityType, observer, Utils.getClosestDirection(dir), upToDistance);
	}
	
	public RelativeCell scanFirst2(BeingFilter beingFilter, Position observer, double upToDistance) {
		System.out.println("  Scanning nearest entity...");
		
		RelativeCell result = new RelativeCell();
		
		try {
			DistanceComparator d = new DistanceComparator(observer);
			
			Optional<Being> optionalBeing = entities.stream()
				.filter(e -> beingFilter.filter(e))
				.min(d);
			
			if (optionalBeing.isPresent()) {
				Being nearest = optionalBeing.get();
			
				double distance = Utils.getDistance(observer, nearest.getPosition());
				
				System.out.println("   Found nearest: Distance="+distance+" to: "+nearest.toString());
				
				if (distance > 0.6 && distance <= upToDistance) {
					result.setValid(true);
					result.setPosition(nearest.getPosition());
					result.setEntity(nearest);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	public RelativeCell scanFirst(String entityType, Position observer, Directions dir, double upToDistance) {
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
					Being e = getEntity(prevPos);
					if (e != null && (entityType == null || entityType.equals(e.getBeingType().name()))) {
						prevPos.move(nextDir.d, j-1);
						result = new RelativeCell(prevPos, e, nextDir.d, j);
						break;
					}
				}
				
				if (result == null) {
					thisPos.move(lastDir.d, j);
					Being e = getEntity(thisPos);
					if (e != null && (entityType == null || entityType.equals(e.getBeingType().name()))) {
						prevPos.move(lastDir.d, j-1);
						result = new RelativeCell(prevPos, e, lastDir.d, j);
						break;
					}
				}
			}
		}		
		
		System.out.println("     Found "+(result != null ? result.getPosition().toString() : "nothing."));
		
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
					Being e = getEntity(prevPos);
					if (e != null) {
						result.add(new RelativeCell(prevPos, e, nextDir.d, j));
					}
				}
				
				thisPos.move(lastDir.d, j);
				Being e = getEntity(thisPos);
				if (e != null) {
					result.add(new RelativeCell(prevPos, e, lastDir.d, j));
				}
			}
		}		
		
		return result;
	}
	
	public List<Cell> getAdjacentCells(Position p) {
		List<Cell> result = new ArrayList<>();
		
		List<Position> positions = p.getAdjacentPositions();
		
		for (Position q : positions) {
			result.add(new Cell(q, map[q.row][q.column]));
		}
		
		return result;
	}

	public long incrementTurn() {
		return ++currentTurn;
	}
	
	public void executeRuleProcess(String ruleProcess) {
        // load up the knowledge base
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kContainer = kieServices.getKieClasspathContainer();
	    
    	KieSession kSession = kContainer.newKieSession("ksession-rules");

        // go !
        kSession.insert(World.getWorld());
        
        for (Being e : World.getWorld().entities) {
   			kSession.insert(e);
        }

        kSession.startProcess(ruleProcess);
        kSession.fireAllRules();
	}

	public Set<Being> getNewBorn() {
		return newBorn;
	}

	public void setNewBorn(Set<Being> newBorn) {
		this.newBorn = newBorn;
	}

	public void resetNewBorn() {
		newBorn.clear();
	}

}
