package org.seccanj.clans;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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
import org.seccanj.clans.gui.Sprite;
import org.seccanj.clans.model.Being;
import org.seccanj.clans.model.BeingType;
import org.seccanj.clans.model.Position;
import org.seccanj.clans.model.World;
import org.seccanj.clans.model.entities.Individual;
import org.seccanj.clans.model.entities.Plant;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.UserAction;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.geometry.Insets;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Clans extends GameApplication {

	public static final double SQUARE3 = Math.sqrt(3);
	public static final int SCREEN_WIDTH = 1440;
	public static final int SCREEN_HEIGHT = 950;
	public static final double DASHBOARD_WIDTH = 160;
	public static final double MAP_START_X = DASHBOARD_WIDTH * 2 + 25;
	public static final double MAP_START_Y = 10;
	public static final double TILE_SIZE = 160;

	public static Color COLOR_MAP_BACKGROUND = Color.WHITE;
	public static Color COLOR_MAP_LINE = Color.BLUE;

	public static double edge = 3;
	public static double halfEdge = edge / 2;
	public static double doubleEdge = edge * 2;
	public static double height = edge * SQUARE3;
	public static double halfHeight = edge * SQUARE3 / 2;

	private World world;
	private KieSession kSession;
	private FrameRate frameRate;

	private GraphicsContext gc;
	private Tile plantsGaugeTile;
	private Gauge individualsGauge;
	private Tile individualsGaugeTile;
	private Gauge simpleDigitalGauge;
	private Tile simpleDigitalGaugeTile;
	
	private Map<String, Entity> individuals = new HashMap<>();
	private Map<String, Entity> plants = new HashMap<>();

	private Object drawingSemaphore = new Object();
	private boolean drawing = false;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	protected void initSettings(GameSettings settings) {
		settings.setWidth(SCREEN_WIDTH);
		settings.setHeight(SCREEN_HEIGHT);
		settings.setTitle("Clans");
		settings.setVersion("0.1");
	}

	@Override
	protected void initGame() {
		/*
		player = FXGL.entityBuilder()
	            .at(300, 300)
	            .view(new Rectangle(25, 25, Color.BLUE))
	            .buildAndAttach();
		*/

		this.world = createWorld();
		initRuleEngine();
	}
	
	@Override
	protected void initUI() {
		var hexagonsTexture = FXGL.getAssetLoader().loadTexture("hexagons.png");
		FXGL.getGameScene().setBackgroundRepeat(hexagonsTexture.getImage());
		
		/*
		Text textPixels = new Text();
		textPixels.setTranslateX(50); // x = 50
		textPixels.setTranslateY(100); // y = 100
		textPixels.textProperty().bind(FXGL.getGameState().intProperty("pixelsMoved").asString());

		FXGL.getGameScene().addUINode(textPixels); // add to the scene graph
		*/
		
		drawGauges();

        frameRate = new FrameRate();
	}

	@Override
	protected void initInput() {
		Input input = FXGL.getInput();

		input.addAction(new UserAction("Move Right") {
			@Override
			protected void onAction() {
				//player.translateX(5); // move right 5 pixels
		        FXGL.getGameState().increment("pixelsMoved", +5);    
			}
		}, KeyCode.D);
	}

	@Override
	protected void initGameVars(Map<String, Object> vars) {
	    vars.put("pixelsMoved", 0);
	}

	@Override
	protected void onPreInit() {
		System.out.println("On pre-init");
	}
	
	@Override
	protected void onUpdate(double tpf) {
		System.out.println("On update");

		frameRate.newFrame();
		
		World.getWorld().currentTurn++;
		GuiContext guiContext = new GuiContext();
		
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
			
			if (plant.shouldSplit()) {
				Plant newPlant = (Plant) world.createEntityNear("plant", plant.getPosition());
				
				if (newPlant != null) {
					kSession.insert(newPlant);
	
					Entity e = FXGL.entityBuilder()
					        .type(BeingType.plant)
					        .at(
					        	getActualX(newPlant.getPosition()),
					        	getActualY(newPlant.getPosition()))
					        .viewWithBBox(new Circle(3, Color.GREEN))
					        .buildAndAttach();
	
					plants.put(newPlant.getName(), e);
				}
			}
		}
		
		// Handle individuals
		for (QueryResultsRow row : allIndividuals) {
			Individual e = (Individual) row.get("$individual");
			//System.out.println("   [Individual left is: " + e.toString());
			
			e.resetActionPoints();
			kSession.update(row.getFactHandle("$individual"), e, "actionPoints");
		}

		removeEndOfTurns();

		removeActionDones();

		updateGauges(guiContext);
	}
	
	private void drawGauges() {
		plantsGaugeTile = TileBuilder.create().prefSize(TILE_SIZE, TILE_SIZE).skinType(SkinType.STOCK)
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

		individualsGauge = createGauge(Gauge.SkinType.DASHBOARD);
		individualsGauge.setValue(0);
		individualsGaugeTile = TileBuilder.create().prefSize(TILE_SIZE, TILE_SIZE).skinType(SkinType.CUSTOM)
				.title("Individuals").text("Percentage").graphic(individualsGauge).build();

		simpleDigitalGauge = createGauge(Gauge.SkinType.SIMPLE_DIGITAL);
		simpleDigitalGauge.setValue(0);
		simpleDigitalGaugeTile = TileBuilder.create().prefSize(TILE_SIZE, TILE_SIZE).skinType(SkinType.CUSTOM)
				.maxValue(30).title("Frame Rate").text("FPS").graphic(simpleDigitalGauge).build();

		FlowGridPane pane = new FlowGridPane(2, 6, plantsGaugeTile, individualsGaugeTile, simpleDigitalGaugeTile);
		pane.setHgap(5);
		pane.setVgap(5);
		pane.setPadding(new Insets(5));
		pane.setBackground(
				new Background(new BackgroundFill(Tile.BACKGROUND.darker(), CornerRadii.EMPTY, Insets.EMPTY)));

		FXGL.getGameScene().addUINode(pane);
	}

	private void initRuleEngine() {
		// load up the knowledge base
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kContainer = kieServices.getKieClasspathContainer();

		kSession = kContainer.newKieSession("ksession-rules");

		kSession.insert(World.getWorld());

		for (Being e : World.getWorld().entities) {
			kSession.insert(e);
			// kSession.insert(new Cell(e.getPosition(), e));
		}
		
		QueryResults allPlants = kSession.getQueryResults("All Plants");
		System.out.println("we have " + allPlants.size() + " Plants left");

		QueryResults allIndividuals = kSession.getQueryResults("All Individuals");
		System.out.println("we have " + allIndividuals.size() + " Individuals left");
		
		kSession.addEventListener(new DefaultAgendaEventListener() {
            //this event will be executed after the rule matches with the model data
            public void afterMatchFired(AfterMatchFiredEvent event) {
                super.afterMatchFired(event);
                //System.out.println(event.getMatch().getRule().getName());//prints the rule name that fires the event
                
                if (event.getMatch().getRule().getName().equalsIgnoreCase("Move to target")) {
                	System.out.println("Matching 'Move to target'");
                	
                	Individual individual = (Individual) event.getMatch().getObjects().get(0);
                	Entity e = individuals.get(individual.getName());
                	e.translate(getActualX(individual.getPosition()) - e.getX(), getActualY(individual.getPosition()) - e.getY());
                } else if (event.getMatch().getRule().getName().equalsIgnoreCase("Eat plant")) {
                	System.out.println("Matching 'Eat plant'");

            		System.out.println("  -- Plant entities: "+plants.size());
            		plants.keySet().forEach(k -> System.out.println("   ------ Plant key: "));

                	Plant plant = (Plant) event.getMatch().getObjects().get(2);
                	Entity e = plants.remove(plant.getName());
                	if (e != null) {
                		e.removeFromWorld();
                	} else {
                		System.err.println("Plant '"+plant.getName()+"' not found!");
                	}
                }
            }
        });
		
		//EngineService service = new EngineService(world, kSession, this);

		//service.start();
	}
	
	public World createWorld() {
		World world = World.getWorld();

		for (int i = 0; i < Configuration.NUM_INITIAL_INDIVIDUALS; i++) {
			Individual individual = (Individual) world.createEntity("individual");

			Entity e = FXGL.entityBuilder()
		        .type(BeingType.individual)
		        .at(
	        		getActualX(individual.getPosition()),
	        		getActualY(individual.getPosition()))
		        .viewWithBBox(new Circle(3, Color.RED))
		        .buildAndAttach();

			individuals.put(individual.getName(), e);
		}

		for (int i = 0; i < Configuration.NUM_INITIAL_PLANTS; i++) {
			Plant plant = (Plant) world.createEntity("plant");

			Entity e = FXGL.entityBuilder()
		        .type(BeingType.plant)
		        .at(
	        		getActualX(plant.getPosition()),
	        		getActualY(plant.getPosition()))
		        .viewWithBBox(new Circle(3, Color.GREEN))
		        .buildAndAttach();

			plants.put(plant.getName(), e);
		}

		System.out.println("  -- Individual entities: "+individuals.size());
		System.out.println("  -- Plant entities: "+plants.size());
		
		return world;
	}

	public void drawMap() {
		gc.setFill(COLOR_MAP_BACKGROUND);
		gc.setStroke(COLOR_MAP_LINE);
		gc.setLineWidth(1);

		for (int i = 0; i < Configuration.WORLD_MAX_ROWS; i++) {
			for (int j = 0; j < Configuration.WORLD_MAX_COLUMNS; j++) {
				double x = getFlatX(i, j);
				double y = getFlatY(i, j);
				drawHexagonFlat(gc, x, y);
			}
		}
	}

	public void clearMap() {
		gc.setFill(COLOR_MAP_BACKGROUND);
		gc.fillRect(offsetMapX(0), offsetMapY(0), SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	public void drawFrame(GuiContext guiContext) {
		boolean isDrawing = false;
		synchronized (drawingSemaphore) {
			isDrawing = drawing;
			drawing = true;
		}

		if (!isDrawing) {
			//clearMap();
			//drawMap();

			for (Sprite s : guiContext.getSprites()) {
				drawSprite(s);
			}

			updateGauges(guiContext);

			frameRate.newFrame();

			synchronized (drawingSemaphore) {
				drawing = false;
			}
		} else {
			System.out.println("Skipping frame...");
		}
	}

	private void updateGauges(GuiContext guiContext) {
		plantsGaugeTile.setValue(guiContext.getPlantsNum());
		individualsGauge.setValue(((double)guiContext.getIndividualsNum() / (double)Configuration.NUM_INITIAL_INDIVIDUALS * 100));
		simpleDigitalGauge.setValue(frameRate.getFrameRate());
	}

	public void clearHexagon(long row, long column) {
		gc.setFill(COLOR_MAP_BACKGROUND);
		gc.setStroke(COLOR_MAP_LINE);
		gc.setLineWidth(1);

		double x = getFlatX(row, column);
		double y = getFlatY(row, column);
		drawHexagonFlat(gc, x, y);
	}

	public void drawPlant(long row, long column) {
		gc.setFill(Color.GREEN);
		gc.setStroke(COLOR_MAP_LINE);
		gc.setLineWidth(1);

		double x = getFlatX(row, column);
		double y = getFlatY(row, column);
		drawHexagonFlat(gc, x, y);
	}

	public void drawIndividual(long row, long column) {
		gc.setFill(Color.RED);
		gc.setStroke(Color.DARKRED);
		gc.setLineWidth(2);

		double x = getFlatX(row, column);
		double y = getFlatY(row, column);
		drawHexagonFlat(gc, x, y);
	}

	private static double getPointyX(long row, long column) {
		return column * height + row * halfHeight;
	}

	private static double getPointyY(long row, long column) {
		return row * (edge + halfEdge);
	}

	private static double getFlatX(long row, long column) {
		return column * (edge + halfEdge);
	}

	private static double getFlatY(long row, long column) {
		long alternate = column % 2;
		return row * height + alternate * halfHeight;
	}

	private static double[] offsetMapX(double[] x) {
		return Arrays.stream(x).map(d -> MAP_START_X + d).toArray();
	}

	private static double[] offsetMapY(double[] y) {
		return Arrays.stream(y).map(d -> MAP_START_Y + d).toArray();
	}

	private static double offsetMapX(double x) {
		return MAP_START_X + x;
	}

	private static double offsetMapY(double y) {
		return MAP_START_Y + y;
	}

	private static int getActualX(Position p) {
		return (int) Math.round(offsetMapX(getFlatX(p.row, p.column)));
	}
	
	private static int getActualY(Position p) {
		return (int) Math.round(offsetMapY(getFlatY(p.row, p.column)));
	}
	
	public void drawSprite(Sprite s) {
		switch (s.type) {
		case individual:
			drawIndividual(s.position.row, s.position.column);
			break;
		case plant:
			drawPlant(s.position.row, s.position.column);
			break;
		default:
			break;
		}
	}

	/*
	 * 
	 * gc.strokeLine(40, 10, 10, 40); gc.fillOval(10, 60, 30, 30);
	 * //gc.strokeOval(60, 60, 30, 30); gc.fillRoundRect(110, 60, 30, 30, 10, 10);
	 * gc.strokeRoundRect(160, 60, 30, 30, 10, 10); gc.fillArc(10, 110, 30, 30, 45,
	 * 240, ArcType.OPEN); gc.fillArc(60, 110, 30, 30, 45, 240, ArcType.CHORD);
	 * gc.fillArc(110, 110, 30, 30, 45, 240, ArcType.ROUND); gc.strokeArc(10, 160,
	 * 30, 30, 45, 240, ArcType.OPEN); gc.strokeArc(60, 160, 30, 30, 45, 240,
	 * ArcType.CHORD); gc.strokeArc(110, 160, 30, 30, 45, 240, ArcType.ROUND);
	 * gc.fillPolygon(new double[]{10, 40, 10, 40}, new double[]{210, 210, 240,
	 * 240}, 4); gc.strokePolygon(new double[]{60, 90, 60, 90}, new double[]{210,
	 * 210, 240, 240}, 4); gc.strokePolyline(new double[]{110, 140, 110, 140}, new
	 * double[]{210, 210, 240, 240}, 4);
	 */

	private void drawHexagonPointy(GraphicsContext gc, double x, double y) {
		double vertexX[] = offsetMapX(
				new double[] { x, x + halfHeight, x + halfHeight, x, x - halfHeight, x - halfHeight });
		double vertexY[] = offsetMapY(new double[] { y, y - halfEdge, y - edge - halfEdge, y - doubleEdge,
				y - edge - halfEdge, y - halfEdge });

		gc.strokePolygon(vertexX, vertexY, 6);
		gc.fillPolygon(vertexX, vertexY, 6);
	}

	private void drawHexagonFlat(GraphicsContext gc, double x, double y) {
		double vertexX[] = offsetMapX(new double[] { x, x + edge, x + edge + halfEdge, x + edge, x, x - halfEdge });
		double vertexY[] = offsetMapY(new double[] { y, y, y - halfHeight, y - height, y - height, y - halfHeight });

		gc.strokePolygon(vertexX, vertexY, 6);
		gc.fillPolygon(vertexX, vertexY, 6);
	}

	private Gauge createGauge(final Gauge.SkinType TYPE) {
		return GaugeBuilder.create().skinType(TYPE).prefSize(TILE_SIZE, TILE_SIZE).animated(true)
				// .title("")
				.unit("\u00B0C").valueColor(Tile.FOREGROUND).titleColor(Tile.FOREGROUND).unitColor(Tile.FOREGROUND)
				.barColor(Tile.BLUE).needleColor(Tile.FOREGROUND).barColor(Tile.BLUE)
				.barBackgroundColor(Tile.BACKGROUND.darker()).tickLabelColor(Tile.FOREGROUND)
				.majorTickMarkColor(Tile.FOREGROUND).minorTickMarkColor(Tile.FOREGROUND)
				.mediumTickMarkColor(Tile.FOREGROUND).build();
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
