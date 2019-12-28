package org.seccanj.clans;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.FactHandle;
import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.engine.EngineService;
import org.seccanj.clans.gui.Sprite;
import org.seccanj.clans.model.Entity;
import org.seccanj.clans.model.World;

import javafx.application.Application;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class ClansJavaFx extends Application {

	public static final double SQUARE3 = Math.sqrt(3);
	public static final double SCREEN_WIDTH = 1500;
	public static final double SCREEN_HEIGHT = 900;

	public static Color COLOR_MAP_BACKGROUND = Color.WHITE;
	public static Color COLOR_MAP_LINE = Color.BLUE;

	public static double edge = 3;
	public static double halfEdge = edge / 2;
	public static double doubleEdge = edge * 2;
	public static double height = edge * SQUARE3;
	public static double halfHeight = edge * SQUARE3 / 2;

	private World world;
	private KieSession kSession;
	private GraphicsContext gc;

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
		primaryStage.setScene(new Scene(root));
		primaryStage.show();

		// load up the knowledge base
		KieServices kieServices = KieServices.Factory.get();
		KieContainer kContainer = kieServices.getKieClasspathContainer();

		kSession = kContainer.newKieSession("ksession-rules");

		// go !
		kSession.insert(World.getWorld());

		Set<FactHandle> entityHandles = new HashSet<>();

		for (Entity e : World.getWorld().entities) {
			entityHandles.add(kSession.insert(e));
			// kSession.insert(new Cell(e.getPosition(), e));
		}

		EngineService service = new EngineService(world, kSession, this);
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
                
                service.restart();
            }
        });
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
		gc.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
	}

	public void drawFrame(List<Sprite> sprites) {
		clearMap();
		drawMap();
		
        for (Sprite s : sprites) {
        	drawSprite(s);
        }
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
		double vertexX[] = { x, x + halfHeight, x + halfHeight, x, x - halfHeight, x - halfHeight };
		double vertexY[] = { y, y - halfEdge, y - edge - halfEdge, y - doubleEdge, y - edge - halfEdge, y - halfEdge };

		gc.strokePolygon(vertexX, vertexY, 6);
		gc.fillPolygon(vertexX, vertexY, 6);
	}

	private void drawHexagonFlat(GraphicsContext gc, double x, double y) {
		double vertexX[] = { x, x + edge, x + edge + halfEdge, x + edge, x, x - halfEdge };
		double vertexY[] = { y, y, y - halfHeight, y - height, y - height, y - halfHeight };

		gc.strokePolygon(vertexX, vertexY, 6);
		gc.fillPolygon(vertexX, vertexY, 6);
	}
}
