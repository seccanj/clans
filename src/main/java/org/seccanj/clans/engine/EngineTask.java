package org.seccanj.clans.engine;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.seccanj.clans.gui.GuiContext;
import org.seccanj.clans.gui.Sprite;
import org.seccanj.clans.gui.Sprite.SpriteType;
import org.seccanj.clans.model.World;
import org.seccanj.clans.model.entities.Individual;
import org.seccanj.clans.model.entities.Plant;

public class EngineTask implements Runnable {

	private World world;

	private KieSession kSession;

	GuiContext guiContext;
	
	public EngineTask(World world, KieSession kSession) {
		this.world = world;
		this.kSession = kSession;
	}

	@Override
	public void run() {
		System.out.println(">>>>>>>>>>>>>>>>>> START NEW TURN: " + world.currentTurn++);

		guiContext = new GuiContext();
		List<Sprite> sprites = new ArrayList<>();
		
		guiContext.setSprites(sprites);
		
		try {
			kSession.fireAllRules();
			/*
			 * for (Entity e : World.getWorld().entities) { if (e instanceof Individual) {
			 * clansJavaFx.drawIndividual(e.getPosition().row, e.getPosition().column); }
			 * else if (e instanceof Plant) { clansJavaFx.drawPlant(e.getPosition().row,
			 * e.getPosition().column); } }
			 */
	
			QueryResults allPlants = kSession.getQueryResults("All Plants");
			System.out.println("we have " + allPlants.size() + " Plants left");
			guiContext.setPlantsNum(allPlants.size());
	
			QueryResults allIndividuals = kSession.getQueryResults("All Individuals");
			System.out.println("we have " + allIndividuals.size() + " Individuals left");
			guiContext.setIndividualsNum(allIndividuals.size());
			
			if (allIndividuals.size() == 0) {
				guiContext.setEndOfLife(true);
			}
			
			// Handle plants
			for (QueryResultsRow row : allPlants) {
				Plant e = (Plant) row.get("$plant");
				sprites.add(new Sprite(e.position, SpriteType.plant));
				
				if (e.shouldSplit()) {
					kSession.insert(world.createEntityNear("plant", e.getPosition()));
				}
			}
	
			// Handle individuals
			for (QueryResultsRow row : allIndividuals) {
				Individual e = (Individual) row.get("$individual");
				sprites.add(new Sprite(e.position, SpriteType.individual));
				//System.out.println("   [Individual left is: " + e.toString());
				
				e.resetActionPoints();
				kSession.update(row.getFactHandle("$individual"), e, "actionPoints");
			}
	
			removeEndOfTurns();
	
			removeActionDones();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void removeEndOfTurns() {
		QueryResults results = kSession.getQueryResults("End of turns");
		System.out.println("we have " + results.size() + " End of turns");

		for (QueryResultsRow row : results) {
			kSession.delete(row.getFactHandle("$endOfTurn"));
		}
	}

	private void removeActionDones() {
		QueryResults results = kSession.getQueryResults("Action dones");
		System.out.println("we have " + results.size() + " Action dones");

		for (QueryResultsRow row : results) {
			kSession.delete(row.getFactHandle("$actionDone"));
		}
	}

	public GuiContext getGuiContext() {
		return guiContext;
	}

}
