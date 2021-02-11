import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gfx.ImageUtils;

import java.util.Random;
import java.util.TimerTask;

public class GamePanel extends JPanel implements ActionListener{

	//#####################################################################
	// Game Settings
	//#####################################################################

	// Define some RGB
	static final Color BLACK 	= new Color(0, 0, 0);
	static final Color GRAY 	= new Color(174, 174, 174);
	static final Color LIGHTGRAY= new Color(222, 222, 222);
	static final Color DARKGRAY = new Color(80, 80, 80);
	static final Color RED 		= new Color(255, 0, 0);
	static final Color BLUE		= new Color(0, 0, 240);

	// Map Stuff
	static final int WIDTH		= 1000;
	static final int HEIGHT 	= 700;
	static final int TILESIZE	= 25;
	static final int MAP_WIDTH	= 37; 	// # of tiles along the x axis
	static final int MAP_HEIGHT	= 25;	// # of tiles along the y axis
	static final int NUMBER_OF_MAPTILES	= (MAP_WIDTH * MAP_HEIGHT);
	Tile[][] map;

	// Booleans of Game
	static final boolean drawgrid = true;
	static final boolean showTroopCount = true;
	static final boolean doTroopGen = true;
	boolean doDebug = true;
	boolean running = false; 		// game starts as off

	// Update and Time Control
	double currentTime = System.currentTimeMillis();
	double accumulator = 0.0;
	static final int TDELAY = 60;	// Event firing delay
	static final int DELAY 	= 1000;	// Tile-Update tick
	private int tickDelay 	= 4;		// How many updates should we wait to call this update
	private int currentTick = 0;
	Timer timer;

	// Extra Utilities
	Random random;
	String path = "./src/gfx/";

	// Images : TODO Setup sprite sheet
	Image capitol;
	Image city;
	Image field;
	Image mountain;
	Image swamp;
	Image windmill;


	// Tile Spread statistics    TODO: Player should be able to adjust these during game creation menu
	int totalObstacles 	= (int)(NUMBER_OF_MAPTILES * 0.2); 
	int enemyAI 		= 2;
	int numOfMountains	= (int)(totalObstacles * 0.7);
	int numOfSwamps		= totalObstacles - numOfMountains;
	int totalVillages	= 5;

	// Controller variables		TODO This should be handled by whatever handles server
	private int playerFocusX = 1; // Default player should be moved to capitol
	private int playerFocusY = 1;
	private int playerControlledTroopCount = 0; // Does not control any troops by default
	private String playerID = "player";
	private Color playerColor = BLUE;
	char direction = ' ';  // set to ' ' for default




	//#####################################################################
	//	Useful Methods
	//#####################################################################
	/**
	 *  This generates everything you need for a new game
	 */
	GamePanel() {
		map = new Tile[MAP_WIDTH][MAP_HEIGHT];
		random = new Random();
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));	
		this.setBackground(DARKGRAY);							
		this.setFocusable(true);						// Grab Focus
		this.addKeyListener(new MyKeyAdapter());		// Allows for user input
		this.addMouseListener(new MyMouseAdapter());
		newGame();										// Make a Game
	}


	/** This method will generate a new map based on the configured game settings
	 * 
	 * @return returns the map for the game
	 */
	public Tile[][] generateMap() {
		// Step 1: Populate the map with empty tiles
		for (int i = 0; i < MAP_WIDTH; i++) {
			for (int j = 0; j < MAP_HEIGHT; j++) {

				map[i][j] = new Tile(i, j); 

				// If the tile is at an edge, make it an obstacle
				if (i == 0 || i == MAP_WIDTH - 1 || j == 0 || j == MAP_HEIGHT - 1) 		  { 
					map[i][j].setTileType(1); 			// 1 = Mountain
					map[i][j].setBackgroundColor(DARKGRAY);
					map[i][j].setIcon(mountain);
				} else { // If it is not an edge, do cellular automata

					// TODO Cellular automata
					int type = random.nextInt((int)(5));
					if (type == 1) {
						map[i][j].setTileType(1); 				// 1 = Mountain
						map[i][j].setBackgroundColor(DARKGRAY);
						map[i][j].setIcon(mountain);
					}
				}
			}
		}
		map[1][1].setOwnedBy(playerID);
		map[1][1].setBackgroundColor(playerColor);
		map[1][1].setTroopCount(1);
		map[1][1].setTileType(6);
		map[1][1].setIcon(capitol);

		System.out.println("Map Generated");
		return map;
	}


	/**
	 *  Generate the assets, calls a new map, resets the timer, and loads all players
	 */
	public void newGame() {
		loadAssets();
		map = generateMap();			// Make a new map
		running = true; 	
		timer = new Timer(TDELAY, this); // Pass in the desired time delay: "this" is because we are using the ActionListener
		timer.start(); 					// start the clock
		System.out.println("New Game has started! Good Luck");
	}

	/**
	 *  A helper method to initialize images, sprites, etc.
	 */
	public void loadAssets() {
		capitol = ImageUtils.loadImage(path + "capital.png");
		city 	= ImageUtils.loadImage(path + "city.png");
		field 	= ImageUtils.loadImage(path + "field.png");
		mountain= ImageUtils.loadImage(path + "mountain.png");
		swamp 	= ImageUtils.loadImage(path + "muk.png");
		windmill= ImageUtils.loadImage(path + "windmill.png");
	}

	/**
	 *  Paints a pretty picture to the window
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}


	/**
	 * This method handles which elements of the game that need to be drawn
	 * 
	 * @param g : send in the graphics object to draw with
	 */
	public void draw(Graphics g) {
		if (running) { //Only draw the game itself if the game is running
			//Draw the map
			for (int i = 0; i < map.length; i++) {
				for (int j = 0; j < map[i].length; j++) {
					g.setColor(map[i][j].getBackgroundColor());
					g.fillRect(
							map[i][j].getX() * TILESIZE,// + (TILESIZE), 
							map[i][j].getY() * TILESIZE,// + (TILESIZE), 
							TILESIZE, 
							TILESIZE
							);
					g.drawImage(map[i][j].getIcon(), i * TILESIZE, j * TILESIZE, null);
					if (showTroopCount) {
						if (map[i][j].getTroopCount() != 0) {
							g.setColor(BLACK);
							g.setFont(new Font("Ink Free", Font.BOLD, 11));

							g.drawString("" + map[i][j].getTroopCount(),// Add the "" to fool java 
									((map[i][j].getX() * TILESIZE) + g.getFont().getSize()),	// x coordinate
									(map[i][j].getY() * TILESIZE) + g.getFont().getSize());	
						}
					}
				}
			}

			if (drawgrid){//To make things easier to see, we are going to draw a grid
				g.setColor(BLACK);
				for(int i = 0; i < WIDTH/ TILESIZE; i++) {
					g.drawLine(i * TILESIZE, 0, i * TILESIZE, HEIGHT);  // Draw vertical lines for every unit^2
				}
				for(int i = 0; i < HEIGHT / TILESIZE; i++) {
					g.drawLine(0, i * TILESIZE, WIDTH, i * TILESIZE); // Draw horizontal lines for every unit^2
				}
			}

			if(playerFocusX > -1 && playerFocusY > -1 ) { // -1 is the null case

				//Draw a cute little Black box around the area you are looking at
				g.setColor(BLACK);
				g.drawRect(
						map[playerFocusX][playerFocusY].getX() * TILESIZE - 1, 
						map[playerFocusX][playerFocusY].getY() * TILESIZE - 1, 
						TILESIZE + 2, 
						TILESIZE + 2
						);
			}

		} else { gameOver(g); }
	}





	/**
	 *  All game logic that requires refreshing is called in here
	 *  This includes any tile updates, collision updates, and printing the debugging
	 */
	public void update() {
		checkCollisions();
		if (doDebug) { debug(); }
		currentTick += 1;
		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[i].length; j++) {
				
				Tile t = map[i][j]; // Grab the current tile
				
				// If the tile has an owner and some troops on it, increment
				if (map[i][j].getOwnedBy() != null && map[i][j].getTroopCount() > 0) {
					// If its a city, add a troop every time update is called
					if(map[i][j].getTileType() == 2 || map[i][j].getTileType() == 3) {
						t.setTroopCount(t.getTroopCount() + 1);
					}
					// If its a natural tile with troops on it, add a troop every tickDelay
					else if (doTroopGen && currentTick >= tickDelay) { 
						t.setTroopCount(t.getTroopCount() + 1); 
					}
				}
			}
		}
		if (currentTick >= tickDelay) { currentTick -= tickDelay; }
	}


	/**
	 *  Make sure the player cannot escape the map, or enter any obstacles
	 */
	public void checkCollisions() {
		if(playerFocusX != -10 && playerFocusY != -10) { //Only do this if the null condition is not true
			if (playerFocusX < 0 ) { 
				playerFocusX = 0;
			}
			//check right border
			if (playerFocusX >= MAP_WIDTH) { 
				playerFocusX = MAP_WIDTH - 1;
			}
			//check upper border
			if (playerFocusY < 0) {
				playerFocusY = 0;
			}
			//check lower border
			if (playerFocusY >= MAP_HEIGHT) { 
				playerFocusY = MAP_HEIGHT - 1;
			}
		}


		if (!running) { timer.stop(); }
	}


	/**
	 *  Change the game to off, display game over menu
	 * 
	 * @param g : pass in the graphics object the game is running on
	 */
	public void gameOver(Graphics g) {}


	/**
	 *  This method keeps track of the timer object and ensures the game continues updating 
	 *  between events
	 */
	@Override
	public void actionPerformed(ActionEvent e) {		
		double newTime = System.currentTimeMillis();
		double frameTime = newTime - currentTime;
		currentTime = newTime;
		accumulator += frameTime;

		if (timer.isRunning()) {
			while(accumulator >= DELAY) {
				update();
				accumulator -= DELAY;
			}
		}
		repaint();
	}

	/**
	 *  Update the current tile to any troop movements, and move the playerFocus around
	 */
	public void move(int x, int y) {
		// Increment to new Tile now that we have the previous tile all taken care of
		// If the player is trying to move into a tile that has a mountain or other obstacle, don't let it
		boolean checkTile = false;
		if (map[playerFocusX + x][playerFocusY + y].getTileType() != 1) {
			if (playerFocusX + x != 0 || playerFocusX + x != MAP_WIDTH)  { playerFocusX += x; checkTile = true; }
			if (playerFocusY + y != 0 || playerFocusY + y != MAP_HEIGHT) { playerFocusY += y; checkTile = true; }
		}


		if (checkTile) {
			// Make sure we collect all the troops but one before we move, only if we own them tho
			if (map[playerFocusX - x][playerFocusY - y].getOwnedBy().compareTo(playerID) == 0 ){
				playerControlledTroopCount = map[playerFocusX - x][playerFocusY - y].getTroopCount() - 1;

				// If the playerFocus is moved to a new tile, check previous tile for ownership
				if (map[playerFocusX - x][playerFocusY - y].getOwnedBy().compareTo(playerID) == 0) {
					if (playerControlledTroopCount >= 1) { 						// Only Move troops if the player actually has any
						map[playerFocusX - x][playerFocusY - y].setTroopCount(1); 		// Leave one guy to defend, he's got this
					}
				}
			}
			// Only do this is we actually have enough troops
			if(playerControlledTroopCount >= 1) {
				// Now to check the new Tile, first we check to see if we own it or not
				// If we do own the tile, move our troops into it
				if (map[playerFocusX][playerFocusY].getOwnedBy().compareTo(playerID) == 0) {
					map[playerFocusX][playerFocusY].setTroopCount(map[playerFocusX][playerFocusY].getTroopCount() + playerControlledTroopCount);
				} 
				else { // If we do not own the tile, we need to check if there are troops on it and subtract from our own
					if (playerControlledTroopCount > map[playerFocusX][playerFocusY].getTroopCount()) {
						playerControlledTroopCount -= map[playerFocusX][playerFocusY].getTroopCount();
						map[playerFocusX][playerFocusY].setOwnedBy(playerID);
						map[playerFocusX][playerFocusY].setBackgroundColor(playerColor);
						map[playerFocusX][playerFocusY].setTroopCount(playerControlledTroopCount);
					} else { // If we do not have enough troops, our count is set to zero, we do not take over the tile
						map[playerFocusX][playerFocusY].setTroopCount(map[playerFocusX][playerFocusY].getTroopCount() - playerControlledTroopCount);
						playerControlledTroopCount = 0;
					}
				}
			}
		}
	}


	/**
	 *  Reads in key input and calls appropriate actions
	 */
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			if(getComponentCount() <= 1) { 
				switch(e.getKeyCode()) {
				case KeyEvent.VK_A:					
					move(-1,0);
					break;
				case KeyEvent.VK_D:
					move(1,0);
					break;
				case KeyEvent.VK_W:
					move(0,-1);
					break;
				case KeyEvent.VK_S:
					move(0,1);
					break;
				}
			}
		}
	}


	/**
	 *  Allows the player to click on tiles, ensures no troop teleport
	 */
	public class MyMouseAdapter extends MouseAdapter {
		@Override
		public void mouseClicked(MouseEvent e) {
			playerFocusX = e.getX() / TILESIZE ;
			playerFocusY = e.getY() / TILESIZE ;

			// If we own the tile we clicked on, switch the controlled troops to the number of troops on that tile
			if (map[playerFocusX][playerFocusY].getOwnedBy().compareTo(playerID) == 0) {
				playerControlledTroopCount = map[playerFocusX][playerFocusY].getTroopCount();
			} else {
				playerControlledTroopCount = 0;
			}
		}
	}



	/**
	 *  Prints out useful info on game data to help with testing
	 */
	public void debug() {
		System.out.println(">>: Calling Update... :<<");
		System.out.println("Current Tile owned by:		" + map[playerFocusX][playerFocusY].getOwnedBy());
		System.out.println("Current Tile Troop Count:	" + map[playerFocusX][playerFocusY].getTroopCount());
		System.out.println("Current playerControlledTroops: " + playerControlledTroopCount);
		System.out.println("Current Tile Type:	" + map[playerFocusX][playerFocusY].getTileType());
		System.out.println();
	}
}











