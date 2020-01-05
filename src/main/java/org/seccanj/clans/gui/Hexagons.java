package org.seccanj.clans.gui;

import java.util.Arrays;

import org.seccanj.clans.configuration.Configuration;
import org.seccanj.clans.model.Position;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Hexagons {

	public static final double SQUARE3 = Math.sqrt(3);
	public static double edge = 3;
	public static double halfEdge = edge / 2;
	public static double doubleEdge = edge * 2;
	public static double height = edge * SQUARE3;
	public static double halfHeight = edge * SQUARE3 / 2;

	public static void drawMap(GraphicsContext gc) {
		gc.setFill(Configuration.COLOR_MAP_BACKGROUND);
		gc.setStroke(Configuration.COLOR_MAP_LINE);
		gc.setLineWidth(1);

		for (int i = 0; i < Configuration.WORLD_MAX_ROWS; i++) {
			for (int j = 0; j < Configuration.WORLD_MAX_COLUMNS; j++) {
				double x = getFlatX(i, j);
				double y = getFlatY(i, j);
				drawHexagonFlat(gc, x, y);
			}
		}
	}

	public static void clearMap(GraphicsContext gc) {
		gc.setFill(Configuration.COLOR_MAP_BACKGROUND);
		gc.fillRect(offsetMapX(0), offsetMapY(0), Configuration.SCREEN_WIDTH, Configuration.SCREEN_HEIGHT);
	}

	public static void clearHexagon(GraphicsContext gc, long row, long column) {
		gc.setFill(Configuration.COLOR_MAP_BACKGROUND);
		gc.setStroke(Configuration.COLOR_MAP_LINE);
		gc.setLineWidth(1);

		double x = getFlatX(row, column);
		double y = getFlatY(row, column);
		drawHexagonFlat(gc, x, y);
	}

	public static void drawPlant(GraphicsContext gc, long row, long column) {
		gc.setFill(Color.GREEN);
		gc.setStroke(Configuration.COLOR_MAP_LINE);
		gc.setLineWidth(1);

		double x = getFlatX(row, column);
		double y = getFlatY(row, column);
		drawHexagonFlat(gc, x, y);
	}

	public static void drawIndividual(GraphicsContext gc, long row, long column) {
		gc.setFill(Color.RED);
		gc.setStroke(Color.DARKRED);
		gc.setLineWidth(2);

		double x = getFlatX(row, column);
		double y = getFlatY(row, column);
		drawHexagonFlat(gc, x, y);
	}

	public static double getPointyX(long row, long column) {
		return column * height + row * halfHeight;
	}

	public static double getPointyY(long row, long column) {
		return row * (edge + halfEdge);
	}

	public static double getFlatX(long row, long column) {
		return column * (edge + halfEdge);
	}

	public static double getFlatY(long row, long column) {
		long alternate = column % 2;
		return row * height + alternate * halfHeight;
	}

	public static double[] offsetMapX(double[] x) {
		return Arrays.stream(x).map(d -> Configuration.MAP_START_X + d).toArray();
	}

	public static double[] offsetMapY(double[] y) {
		return Arrays.stream(y).map(d -> Configuration.MAP_START_Y + d).toArray();
	}

	public static double offsetMapX(double x) {
		return Configuration.MAP_START_X + x;
	}

	public static double offsetMapY(double y) {
		return Configuration.MAP_START_Y + y;
	}

	public static int getActualX(Position p) {
		return (int) Math.round(offsetMapX(getFlatX(p.row, p.column)));
	}
	
	public static int getActualY(Position p) {
		return (int) Math.round(offsetMapY(getFlatY(p.row, p.column)));
	}
	
	public static void drawSprite(GraphicsContext gc, Sprite s) {
		switch (s.type) {
		case individual:
			drawIndividual(gc, s.position.row, s.position.column);
			break;
		case plant:
			drawPlant(gc, s.position.row, s.position.column);
			break;
		default:
			break;
		}
	}

	public static void drawHexagonPointy(GraphicsContext gc, double x, double y) {
		double vertexX[] = offsetMapX(
				new double[] { x, x + halfHeight, x + halfHeight, x, x - halfHeight, x - halfHeight });
		double vertexY[] = offsetMapY(new double[] { y, y - halfEdge, y - edge - halfEdge, y - doubleEdge,
				y - edge - halfEdge, y - halfEdge });

		gc.strokePolygon(vertexX, vertexY, 6);
		gc.fillPolygon(vertexX, vertexY, 6);
	}

	public static void drawHexagonFlat(GraphicsContext gc, double x, double y) {
		double vertexX[] = offsetMapX(new double[] { x, x + edge, x + edge + halfEdge, x + edge, x, x - halfEdge });
		double vertexY[] = offsetMapY(new double[] { y, y, y - halfHeight, y - height, y - height, y - halfHeight });

		gc.strokePolygon(vertexX, vertexY, 6);
		gc.fillPolygon(vertexX, vertexY, 6);
	}
}
