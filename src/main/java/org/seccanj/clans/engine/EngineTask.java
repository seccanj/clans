package org.seccanj.clans.engine;

import java.util.ArrayList;
import java.util.List;

import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.seccanj.clans.gui.Sprite;
import org.seccanj.clans.gui.Sprite.SpriteType;
import org.seccanj.clans.model.World;
import org.seccanj.clans.model.entities.Individual;
import org.seccanj.clans.model.entities.Plant;

import javafx.concurrent.Task;

public class EngineTask extends Task<List<Sprite>> {

	private World world;

	private KieSession kSession;

	public EngineTask(World world, KieSession kSession) {
		this.world = world;
		this.kSession = kSession;
	}

	@Override
	protected List<Sprite> call() throws Exception {
		System.out.println(">>>>>>>>>>>>>>>>>> START NEW TURN: " + world.currentTurn++);

		kSession.fireAllRules();
		/*
		 * for (Entity e : World.getWorld().entities) { if (e instanceof Individual) {
		 * clansJavaFx.drawIndividual(e.getPosition().row, e.getPosition().column); }
		 * else if (e instanceof Plant) { clansJavaFx.drawPlant(e.getPosition().row,
		 * e.getPosition().column); } }
		 */

		List<Sprite> result = new ArrayList<>();

		QueryResults allPlants = kSession.getQueryResults("All Plants");
		QueryResults allIndividuals = kSession.getQueryResults("All Individuals");

		// Handle plants
		for (QueryResultsRow row : allPlants) {
			Plant e = (Plant) row.get("$plant");
			result.add(new Sprite(e.position, SpriteType.plant));
		}

		// Handle individuals
		for (QueryResultsRow row : allIndividuals) {
			Individual e = (Individual) row.get("$individual");
			result.add(new Sprite(e.position, SpriteType.individual));

			e.resetHasLived();
			kSession.update(row.getFactHandle("$individual"), e, "leftActionPoints");
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
