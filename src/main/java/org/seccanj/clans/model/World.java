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
import org.seccanj.clans.model.entities.Individual;
import org.seccanj.clans.model.entities.Individual.Gender;
import org.seccanj.clans.model.entities.Plant;

public class World {
	
	public static World world = new World();
	
	public Entity[][] map = new Entity[Configuration.WORLD_MAX_ROWS][Configuration.WORLD_MAX_COLUMNS];
	
	public Set<Entity> entities = new HashSet<Entity>();
	
	public long currentTurn = 0;

	public static World getWorld() {
		return world;
	}

	public Entity createEntity(String entityType) {
		return createEntityNear(entityType, Position.getRandom());
	}
	
	public Entity createEntityNear(String entityType, Position position) {
		Entity result = null;
		
		Position newPosition = new Position();
		int trials = 0;
		do {
			newPosition.row = position.row + (int) Math.round(Math.random() * Configuration.NEAR_DISTANCE);
			newPosition.column = position.column + (int) Math.round(Math.random() * Configuration.NEAR_DISTANCE);
		} while (!World.getWorld().isFree(newPosition) && trials++ <= Configuration.NEAR_DISTANCE_SQUARE);
		
		if (trials < Configuration.NEAR_DISTANCE_SQUARE) {
			switch (EntityType.valueOf(entityType)) {
			case individual:
				Individual individual = new Individual();
				individual.setBirthTurn(currentTurn);
				individual.name = ModelUtils.getRandomName(individual);
				individual.position = newPosition;
				// individual.energy = Math.random() * Configuration.INDIVIDUAL_DEFAULT_ENERGY;
				individual.gender = Gender.getRandom();
	
				World.getWorld().setEntity(individual.position, individual);
				
				result = individual;
				
				break;
			case plant:
				Plant plant = new Plant();
				plant.position = newPosition;
				// plant.energy = Math.random() * Configuration.PLANT_DEFAULT_ENERGY;
				plant.setBirthTurn(currentTurn);
	
				World.getWorld().setEntity(plant.position, plant);
				
				result = plant;
				
				break;
			default:
				break;
			}
		}
		
		return result;
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
				e.moveTo(p, 1);
				
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
		return scanFirst(null, observer, ModelUtils.getClosestDirection(dir), upToDistance);
	}
	
	public RelativeCell scanFirst(String entityType, Position observer, Direction dir, double upToDistance) {
		return scanFirst(entityType, observer, ModelUtils.getClosestDirection(dir), upToDistance);
	}
	
	public RelativeCell scanFirst2(String entityType, Position observer, double upToDistance) {
		System.out.println("  Scanning nearest entity...");
		
		RelativeCell result = new RelativeCell();
		
		try {
			DistanceComparator d = new DistanceComparator(observer);
			EntityType et = EntityType.valueOf(entityType);
			
			Entity nearest = entities.stream()
				.filter(e -> e.getEntityType() == et)
				.min(d)
				.get();
			
			if (nearest != null) {
				double distance = ModelUtils.getDistance(observer, nearest.getPosition());
				
				System.out.println("   Found nearest: Distance="+distance+" to: "+nearest.toString());
				
				if (distance <= upToDistance) {
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
					Entity e = world.getEntity(prevPos);
					if (e != null && (entityType == null || entityType.equals(e.getEntityType().name()))) {
						prevPos.move(nextDir.d, j-1);
						result = new RelativeCell(prevPos, e, nextDir.d, j);
						break;
					}
				}
				
				if (result == null) {
					thisPos.move(lastDir.d, j);
					Entity e = world.getEntity(thisPos);
					if (e != null && (entityType == null || entityType.equals(e.getEntityType().name()))) {
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
