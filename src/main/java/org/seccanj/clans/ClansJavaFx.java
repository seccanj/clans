package org.seccanj.clans;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.engine.EngineServiceOld;
import org.seccanj.clans.gui.FrameRate;
import org.seccanj.clans.gui.GuiContext;
import org.seccanj.clans.gui.Sprite;
import org.seccanj.clans.model.Being;
import org.seccanj.clans.model.World;

import eu.hansolo.medusa.Gauge;
import eu.hansolo.medusa.GaugeBuilder;
import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.Tile.SkinType;
import eu.hansolo.tilesfx.TileBuilder;
import eu.hansolo.tilesfx.tools.FlowGridPane;
import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClansJavaFx extends Application {

	public static final double SQUARE3 = Math.sqrt(3);
	public static final double SCREEN_WIDTH = 1440;
	public static final double SCREEN_HEIGHT = 950;
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

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Clans");

		this.world = createWorld();

		Group root = new Group();
		Canvas canvas = new Canvas(SCREEN_WIDTH, SCREEN_HEIGHT);
		gc = canvas.getGraphicsContext2D();

		root.getChildren().add(canvas);

        plantsGaugeTile = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(SkinType.STOCK)
                //.averagingPeriod(50)
                
                .maxValue(50000)
                //.numberOfValuesForTrendCalculation(50)
                .title("Plants")
                .unit("num")
                
//                .gradientStops(new Stop(0, Tile.GREEN),
//                               new Stop(0.5, Tile.YELLOW),
//                               new Stop(1.0, Tile.RED))
//                .strokeWithGradient(true)
                
                .build();
        plantsGaugeTile.setValue(Configuration.NUM_INITIAL_PLANTS);
        
        individualsGauge = createGauge(Gauge.SkinType.DASHBOARD);
        individualsGauge.setValue(0);
        individualsGaugeTile  = TileBuilder.create()
                                    .prefSize(TILE_SIZE, TILE_SIZE)
                                    .skinType(SkinType.CUSTOM)
                                    .title("Individuals")
                                    .text("Percentage")
                                    .graphic(individualsGauge)
                                    .build();
        
        simpleDigitalGauge = createGauge(Gauge.SkinType.SIMPLE_DIGITAL);
        simpleDigitalGauge.setValue(0);
        simpleDigitalGaugeTile  = TileBuilder.create()
                .prefSize(TILE_SIZE, TILE_SIZE)
                .skinType(SkinType.CUSTOM)
                .maxValue(30)
                .title("Frame Rate")
                .text("FPS")
                .graphic(simpleDigitalGauge)
                .build();

        FlowGridPane pane = new FlowGridPane(2, 6, plantsGaugeTile, individualsGaugeTile, simpleDigitalGaugeTile);
		pane.setHgap(5);
		pane.setVgap(5);
		pane.setPadding(new Insets(5));
		pane.setBackground(new Background(new BackgroundFill(Tile.BACKGROUND.darker(), CornerRadii.EMPTY, Insets.EMPTY)));

		root.getChildren().add(pane);
		
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		// load up the knowledge base
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kContainer = kieServices.getKieClasspathContainer();

		kSession = kContainer.newKieSession("ksession-rules");

		// go !
		kSession.insert(World.getWorld());

		Set<FactHandle> entityHandles = new HashSet<>();

		for (Being e : World.getWorld().entities) {
			entityHandles.add(kSession.insert(e));
			// kSession.insert(new Cell(e.getPosition(), e));
		}

		EngineServiceOld service = new EngineServiceOld(world, kSession, this);
        service.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent t) {
                System.out.println("****************** Done. ********************");
                
                /*
                List<Sprite> sprites = (List<Sprite>) t.getSource().getValue();
                for (Sprite s : sprites) {
                	drawSprite(s);
                }
                */
                
                GuiContext guiContext = (GuiContext) t.getSource().getValue();
                
                if (!guiContext.isEndOfLife()) {
                	service.restart();
                } else {
                	System.out.println("End of (non vegetal) life.");
                }
            }
        });

        frameRate = new FrameRate();

        service.start();
	}

	public static World createWorld() {
		World world = World.getWorld();

		for (int i = 0; i < Configuration.NUM_INITIAL_INDIVIDUALS; i++) {
			world.createEntity("individual");
		}

		for (int i = 0; i < Configuration.NUM_INITIAL_PLANTS; i++) {
			world.createEntity("plant");
		}

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
		clearMap();
		drawMap();
		
        for (Sprite s : guiContext.getSprites()) {
        	drawSprite(s);
        }

    	frameRate.newFrame();

    	updateGauges(guiContext);
	}
	
	private void updateGauges(GuiContext guiContext) {
		plantsGaugeTile.setValue(guiContext.getPlantsNum());
        individualsGauge.setValue(guiContext.getIndividualsNum() / Configuration.NUM_INITIAL_INDIVIDUALS * 100);
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

	private double getPointyX(long row, long column) {
		return column * height + row * halfHeight;
	}

	private double getPointyY(long row, long column) {
		return row * (edge + halfEdge);
	}

	private double getFlatX(long row, long column) {
		return column * (edge + halfEdge);
	}

	private double getFlatY(long row, long column) {
		long alternate = column % 2;
		return row * height + alternate * halfHeight;
	}

	private double[] offsetMapX(double[] x) {
		return Arrays.stream(x).map(d -> MAP_START_X + d).toArray();
	}
	
	private double[] offsetMapY(double[] y) {
		return Arrays.stream(y).map(d -> MAP_START_Y + d).toArray();
	}
	
	private double offsetMapX(double x) {
		return MAP_START_X + x;
	}
	
	private double offsetMapY(double y) {
		return MAP_START_Y + y;
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
		double vertexX[] = offsetMapX(new double[]{ x, x + halfHeight, x + halfHeight, x, x - halfHeight, x - halfHeight });
		double vertexY[] = offsetMapY(new double[]{ y, y - halfEdge, y - edge - halfEdge, y - doubleEdge, y - edge - halfEdge, y - halfEdge });
		
		gc.strokePolygon(vertexX, vertexY, 6);
		gc.fillPolygon(vertexX, vertexY, 6);
	}

	private void drawHexagonFlat(GraphicsContext gc, double x, double y) {
		double vertexX[] = offsetMapX(new double[]{ x, x + edge, x + edge + halfEdge, x + edge, x, x - halfEdge });
		double vertexY[] = offsetMapY(new double[]{ y, y, y - halfHeight, y - height, y - height, y - halfHeight });

		gc.strokePolygon(vertexX, vertexY, 6);
		gc.fillPolygon(vertexX, vertexY, 6);
	}

    private Gauge createGauge(final Gauge.SkinType TYPE) {
        return GaugeBuilder.create()
                           .skinType(TYPE)
                           .prefSize(TILE_SIZE, TILE_SIZE)
                           .animated(true)
                           //.title("")
                           .unit("\u00B0C")
                           .valueColor(Tile.FOREGROUND)
                           .titleColor(Tile.FOREGROUND)
                           .unitColor(Tile.FOREGROUND)
                           .barColor(Tile.BLUE)
                           .needleColor(Tile.FOREGROUND)
                           .barColor(Tile.BLUE)
                           .barBackgroundColor(Tile.BACKGROUND.darker())
                           .tickLabelColor(Tile.FOREGROUND)
                           .majorTickMarkColor(Tile.FOREGROUND)
                           .minorTickMarkColor(Tile.FOREGROUND)
                           .mediumTickMarkColor(Tile.FOREGROUND)
                           .build();
    }
}
