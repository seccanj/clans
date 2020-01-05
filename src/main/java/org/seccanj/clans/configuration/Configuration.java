package org.seccanj.clans.configuration;

import javafx.scene.paint.Color;

public class Configuration {

	public static int NUM_INITIAL_INDIVIDUALS = 10;
	public static int NUM_INITIAL_PLANTS = 20;
	public static int SIGHT_DISTANCE = 300;
	public static int NEAR_DISTANCE = 5;
	public static int NEAR_DISTANCE_SQUARE = NEAR_DISTANCE * NEAR_DISTANCE;

	public static int WORLD_MAX_ROWS = 180;
	public static int WORLD_MAX_COLUMNS = 240;	

	public static double INDIVIDUAL_DEFAULT_HEALTH = 100;
	public static double INDIVIDUAL_DEFAULT_ENERGY = 100;
	public static double PLANT_DEFAULT_ENERGY = 10;
	public static int INDIVIDUAL_DEFAULT_ACTION_POINTS = 10;
	public static double INDIVIDUAL_DEFAULT_MAX_SPEED = 3;

	public static final int SCREEN_WIDTH = 1440;
	public static final int SCREEN_HEIGHT = 950;
	public static final double DASHBOARD_WIDTH = 160;
	public static final double MAP_START_X = DASHBOARD_WIDTH * 2 + 25;
	public static final double MAP_START_Y = 10;
	public static final double TILE_SIZE = 160;

	public static Color COLOR_MAP_BACKGROUND = Color.WHITE;
	public static Color COLOR_MAP_LINE = Color.BLUE;
}
