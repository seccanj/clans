package org.seccanj.clans;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.event.rule.AfterMatchFiredEvent;
import org.kie.api.event.rule.DefaultAgendaEventListener;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.QueryResults;
import org.kie.api.runtime.rule.QueryResultsRow;
import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.gui.FrameRate;
import org.seccanj.clans.gui.GuiContext;
import org.seccanj.clans.gui.Hexagons;
import org.seccanj.clans.model.World;
import org.seccanj.clans.model.being.Being;
import org.seccanj.clans.model.being.BeingType;
import org.seccanj.clans.model.being.Individual;
import org.seccanj.clans.model.being.Plant;
import org.seccanj.clans.model.control.Parent;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.geometry.Insets;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Clans extends GameApplication {

	private World world;
	private KieSession kSession;
	private FrameRate frameRate;

	private Tile turnsGaugeTile;
	private Tile plantsGaugeTile;
	private Tile individualsGaugeTile;
	private Gauge simpleDigitalGauge;
	private Tile fpsSimpleDigitalGaugeTile;
	
	private Map<String, Entity> individuals = new HashMap<>();
	private Map<String, Entity> plants = new HashMap<>();

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected void initSettings(GameSettings settings) {
		settings.setWidth(Configuration.SCREEN_WIDTH);
		settings.setHeight(Configuration.SCREEN_HEIGHT);
		settings.setTitle("Clans");
		settings.setVersion("0.1");
	}

	@Override
	protected void initGame() {
		initRuleEngine();
		createWorld();
	}
	
	@Override
	protected void initUI() {
		var hexagonsTexture = FXGL.getAssetLoader().loadTexture("hexagons.png");
		FXGL.getGameScene().setBackgroundRepeat(hexagonsTexture.getImage());
		
		drawGauges();

        frameRate = new FrameRate();
	}

	@Override
	protected void onUpdate(double tpf) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>> On update - New turn");

		frameRate.newFrame();
		
		world.incrementTurn();
		GuiContext guiContext = new GuiContext();
		
		System.out.println("Firing all rules...");
		kSession.fireAllRules();

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
			Plant plant = (Plant) row.get("$plant");

			plant.addTurn();
			kSession.update(row.getFactHandle("$plant"), plant, "age");
			
			if (plant.shouldSplit()) {
				Plant newPlant = (Plant) world.createEntityNear("plant", plant.getPosition());
				
				if (newPlant != null) {
					addBeingToContext(newPlant);
				}
			}
		}
		
		// Handle individuals
		for (QueryResultsRow row : allIndividuals) {
			Individual individual = (Individual) row.get("$individual");
			//System.out.println("   [Individual left is: " + individual.toString());
			
			individual.resetActionPoints();
			individual.addTurn();
			kSession.update(row.getFactHandle("$individual"), individual, "actionPoints", "age");
		}

		removeEndOfTurns();

		removeActionDones();

		handleBirthsAndDeaths();
		
		updateGauges(guiContext);
	}
	
	private void handleBirthsAndDeaths() {
		Set<Being> newBorns = world.getNewBorn();
		
		if (!newBorns.isEmpty()) {
			System.out.println("   >>> Inserting new Being into context.");
			newBorns.forEach(b -> {
				addBeingToContext(b);
				addParentFacts(b);
			});
		} else {
			System.out.println("   >>> No new borns.");
		}
		
		world.resetNewBorn();

		Set<Being> dead = world.getDead();
		
		if (!dead.isEmpty()) {
			System.out.println("   >>> Removing dead Being from context.");
			dead.forEach(b -> removeBeingFromContext(b));
		} else {
			System.out.println("   >>> No dead.");
		}
		
		world.resetDead();
	}

	private void addParentFacts(Being b) {
		if (b.getBeingType() == BeingType.individual) {
			Individual individual = (Individual)b;
			
			if (individual.getParents() != null && individual.getParents().size() > 0) {
				individual.getParents().stream()
					.forEach(p -> kSession.insert(new Parent(p, individual)));
			}
		}
	}

	private void addBeingToContext(Being b) {
		if (b != null) {
			BeingType beingType = BeingType.valueOf(b.getBeingTypeName());
			Color color = getColorForBeing(b);
			
			kSession.insert(b);
			
			Entity e = FXGL.entityBuilder()
			        .type(beingType)
			        .at(
		        		Hexagons.getActualX(b.getPosition()),
		        		Hexagons.getActualY(b.getPosition()))
			        .viewWithBBox(new Circle(3, color))
			        .buildAndAttach();
	
			switch (beingType) {
			case individual:
				individuals.put(b.getName(), e);
			case plant:
				plants.put(b.getName(), e);
			}
		}
	}
	
	private void removeBeingFromContext(Being b) {
		if (b != null) {
			BeingType beingType = BeingType.valueOf(b.getBeingTypeName());
			Entity e = null;
			
			switch (beingType) {
			case individual:
				e = individuals.remove(b.getName());
			case plant:
				e = plants.remove(b.getName());
			}
		
			if (e != null) {
				e.removeFromWorld();
			}
		}
	}
	
	private Color getColorForBeing(Being b) {
		switch (b.getBeingType()) {
		case individual:
			return Color.RED;
		case plant:
			return b.hasCharacteristics("red", "bitter") ? Color.PURPLE : Color.GREEN;
		}
		
		return null;
	}

	private void initRuleEngine() {
		// load up the knowledge base
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kContainer = kieServices.getKieClasspathContainer();

		kSession = kContainer.newKieSession("ksession-rules");

		kSession.addEventListener(new DefaultAgendaEventListener() {
            //this event will be executed after the rule matches with the model data
            public void afterMatchFired(AfterMatchFiredEvent event) {
                super.afterMatchFired(event);
                //System.out.println(event.getMatch().getRule().getName());//prints the rule name that fires the event
                
                if (event.getMatch().getRule().getName().equalsIgnoreCase("Move to target")) {
                	System.out.println("Matching 'Move to target'");
                	
                	Individual individual = (Individual) event.getMatch().getObjects().get(0);
                	Entity e = individuals.get(individual.getName());
                	e.translate(Hexagons.getActualX(individual.getPosition()) - e.getX(), Hexagons.getActualY(individual.getPosition()) - e.getY());
                	
                } else if (event.getMatch().getRule().getName().equalsIgnoreCase("Eat plant") || 
                		event.getMatch().getRule().getName().equalsIgnoreCase("Eat poisoned plant")) {
                	System.out.println("Matching 'Eat plant' or 'Eat poisoned plant'");

                	Plant plant = (Plant) event.getMatch().getObjects().get(2);
                	removeBeingFromContext(plant);
                }
            }
        });
	}
	
	private World createWorld() {
		world = World.getWorld();

		for (int i = 0; i < Configuration.NUM_INITIAL_INDIVIDUALS; i++) {
			Individual individual = (Individual) world.createEntity("individual");

			addBeingToContext(individual);
		}

		for (int i = 0; i < Configuration.NUM_INITIAL_PLANTS; i++) {
			Plant plant = (Plant) world.createEntity("plant");

			addBeingToContext(plant);
		}

		System.out.println("  -- Individual entities: "+individuals.size());
		System.out.println("  -- Plant entities: "+plants.size());

		kSession.insert(world);
		
		return world;
	}

	private Gauge createGauge(final Gauge.SkinType TYPE) {
		return GaugeBuilder.create().skinType(TYPE).prefSize(Configuration.TILE_SIZE, Configuration.TILE_SIZE).animated(true)
				// .title("")
				.unit("\u00B0C").valueColor(Tile.FOREGROUND).titleColor(Tile.FOREGROUND).unitColor(Tile.FOREGROUND)
				.barColor(Tile.BLUE).needleColor(Tile.FOREGROUND).barColor(Tile.BLUE)
				.barBackgroundColor(Tile.BACKGROUND.darker()).tickLabelColor(Tile.FOREGROUND)
				.majorTickMarkColor(Tile.FOREGROUND).minorTickMarkColor(Tile.FOREGROUND)
				.mediumTickMarkColor(Tile.FOREGROUND).build();
	}

	private void drawGauges() {
		turnsGaugeTile = TileBuilder.create().prefSize(Configuration.TILE_SIZE, Configuration.TILE_SIZE).skinType(SkinType.STOCK)
				// .averagingPeriod(50)
				.maxValue(100000)
				.title("Turns").unit("num")
				.build();
		turnsGaugeTile.setValue(0);

		plantsGaugeTile = TileBuilder.create().prefSize(Configuration.TILE_SIZE, Configuration.TILE_SIZE).skinType(SkinType.STOCK)
				// .averagingPeriod(50)
				.maxValue(50000)
				// .numberOfValuesForTrendCalculation(50)
				.title("Plants").unit("num")
//                .gradientStops(new Stop(0, Tile.GREEN),
//                               new Stop(0.5, Tile.YELLOW),
//                               new Stop(1.0, Tile.RED))
//                .strokeWithGradient(true)
				.build();
		plantsGaugeTile.setValue(Configuration.NUM_INITIAL_PLANTS);

		individualsGaugeTile = TileBuilder.create().prefSize(Configuration.TILE_SIZE, Configuration.TILE_SIZE).skinType(SkinType.STOCK)
				// .averagingPeriod(50)
				.maxValue(100000)
				.title("Individuals").unit("num")
				.build();
		individualsGaugeTile.setValue(0);

		simpleDigitalGauge = createGauge(Gauge.SkinType.SIMPLE_DIGITAL);
		simpleDigitalGauge.setValue(0);
		fpsSimpleDigitalGaugeTile = TileBuilder.create().prefSize(Configuration.TILE_SIZE, Configuration.TILE_SIZE).skinType(SkinType.CUSTOM)
				.maxValue(30).title("Frame Rate").text("FPS").graphic(simpleDigitalGauge).build();

		FlowGridPane pane = new FlowGridPane(2, 6, turnsGaugeTile, fpsSimpleDigitalGaugeTile, plantsGaugeTile, individualsGaugeTile);
		pane.setHgap(5);
		pane.setVgap(5);
		pane.setPadding(new Insets(5));
		pane.setBackground(
				new Background(new BackgroundFill(Tile.BACKGROUND.darker(), CornerRadii.EMPTY, Insets.EMPTY)));

		FXGL.getGameScene().addUINode(pane);
	}

	private void updateGauges(GuiContext guiContext) {
		turnsGaugeTile.setValue(world.currentTurn);
		plantsGaugeTile.setValue(guiContext.getPlantsNum());
		individualsGaugeTile.setValue(guiContext.getIndividualsNum());
		simpleDigitalGauge.setValue(frameRate.getFrameRate());
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
