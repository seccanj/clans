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

import javafx.concurrent.Task;

public class EngineTask extends Task<GuiContext> {

	private World world;

	private KieSession kSession;

	public EngineTask(World world, KieSession kSession) {
		this.world = world;
		this.kSession = kSession;
	}

	@Override
	protected GuiContext call() throws Exception {
		System.out.println(">>>>>>>>>>>>>>>>>> START NEW TURN: " + world.currentTurn++);

		GuiContext result = new GuiContext();
		List<Sprite> sprites = new ArrayList<>();
		
		result.setSprites(sprites);
		
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
			result.setPlantsNum(allPlants.size());
	
			QueryResults allIndividuals = kSession.getQueryResults("All Individuals");
			System.out.println("we have " + allIndividuals.size() + " Individuals left");
			result.setIndividualsNum(allIndividuals.size());
			
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
				System.out.println("   [Individual left is: " + e.toString());
				
				e.resetActionPoints();
				kSession.update(row.getFactHandle("$individual"), e, "actionPoints");
			}
	
			/*
			 * entityHandles.stream().forEach(f -> { Entity e = (Entity)
			 * kSession.getObject(f);
			 * 
			 * if (e instanceof Individual) {
			 * clansJavaFx.drawIndividual(e.getPosition().row, e.getPosition().column);
			 * 
			 * ((Individual) e).resetHasLived(); kSession.update(f, e, "leftActionPoints");
			 * } else if (e instanceof Plant) { clansJavaFx.drawPlant(e.getPosition().row,
			 * e.getPosition().column); } })
			 */;
	
			removeEndOfTurns();
	
			removeActionDones();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return result;
	}

	@Override
	protected void succeeded() {
		super.succeeded();
		updateMessage("Done!");
	}

	@Override
	protected void cancelled() {
		super.cancelled();
		updateMessage("Cancelled!");
	}

	@Override
	protected void failed() {
		super.failed();
		updateMessage("Failed!");
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

}
