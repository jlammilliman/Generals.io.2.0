package generals2;

import java.util.Random;

/**
 * This class generates a nested integer array [ int[][] map ] for a map of tiles.
 */
public class Automata {

	private int width;
	private int height;
	private int[][] map;

	private int smoothingIterations = 5;	// How many times the smoothMap should be called
	private final int RANGE = 5;			// How many different tile types do you want
	
	public Automata() {
		this(37, 25);
	}

	public Automata(int mapWidth, int mapHeight) {
		this.width = mapWidth;
		this.height = mapHeight;
		generateMap(width, height);
	}

	private void generateMap(int width, int height) {
		this.map = new int[width][height];
		Random rand = new Random();
		
		// Fill the map with random tiles between 0 - RANGE
		for (int i = 0; i < width; i++) {
			for (int j = 0; j < height; j++) {				
				if (i == 0 || i == width - 1 || j == 0 || j == height - 1) { map[i][j] = 1; }
				else { map[i][j] = rand.nextInt((int)(1)); }
			}
		}
		
		// Run as many specified smoothing operations as requested
		for (int i = 0; i < smoothingIterations; i++) {
			smoothMap();
		}
		
	}
	
	/**
	 * 
	 * @return : returns the map[][] array of 0's 1's, in general any Automata object will
	 * create its own map, so to refresh this create a new automata object, or call the 
	 * "generateNewMap" function followed by another call to this function 
	 */
	public int[][] getMap(){
		return map;
	}
	
	/**
	 * This will generate a new map with the predefined settings
	 */
	public void generateNewMap() {
		generateMap(width, height);
	}
	
	
	/**
	 * We want to run over the map, get the neighbors, and modify the type of tile 
	 * accordingly. There are only default empty tiles[0], and obstacles tiles[1]
	 */
	private void smoothMap() {
		for (int x = 0; x < width; x++) {
			for (int y= 0; y < height; y++) {
				int neighbors = getSurroundingWallCount(x, y);
				
				if (neighbors > 4) { map[x][y] = 1; }		// If it has a lot of neighbors, make it an obstacle
				else if (neighbors < 4) { map[x][y] = 0; }	// If it is alone, kill it
			}
		}
	}
	
	
	/*
	 *  If the cell in question has neighbors count them up, and return that count
	 *  A neighbor is defined as being within the 8 immediate squares next to the
	 *  (x, y) location in question
	 */
	private int getSurroundingWallCount(int gridX, int gridY) {
		int countN = 0;
		for (int neighborX = gridX - 1; neighborX <= gridX + 1; neighborX++) {
			for (int neighborY = gridY - 1; neighborY <= gridY + 1; neighborY++) {
				if (neighborX >= 0 && neighborX < width && neighborY >= 0 && neighborY < height) {
                    if (neighborX != gridX || neighborY != gridY) {
                    	countN += map[neighborX] [neighborY];
                    }
                } else { countN++; }
			}
		}
		return countN;
	}
	
	
}
