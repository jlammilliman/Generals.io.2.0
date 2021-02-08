import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import gfx.ImageUtils;

import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{

	static final Color BLACK 		= new Color(0, 0, 0);
	static final Color GRAY 		= new Color(174, 174, 174);
	static final Color LIGHTGRAY 	= new Color(222, 222, 222);
	static final Color DARKGRAY 	= new Color(80, 80, 80);
	static final Color RED 			= new Color(255, 0, 0);
	static final Color BLUE			= new Color(0, 0, 240);


	static final int WIDTH		= 1000;
	static final int HEIGHT 	= 700;
	static final int TILESIZE	= 25;
	static final int MAP_WIDTH	= 37; 	// # of tiles along the x axis
	static final int MAP_HEIGHT	= 25;	// # of tiles along the y axis

	static final boolean drawgrid = true;
	static final boolean showTroopCount = true;
	boolean doDebug = false;


	static final int NUMBER_OF_MAPTILES	= (MAP_WIDTH * MAP_HEIGHT);
	static final int DELAY		= 60;	// the higher this number, the slower the game
	boolean running = false; 			// game starts as off
	double timePassed = 0;
	long currentTime;
	long lastUpdate;
	double updateRate = 0.001; // updates per second 
	Tile[][] map;
	Timer timer;
	Random random;

	String path = "C:\\Users\\jlamm\\eclipse-workspace\\Generals\\";
	Image mountain = ImageUtils.loadImage(path + "mountain.png");


	// Tile Spread statistics    TODO: Player should be able to adjust these during game creation menu
	int totalObstacles 	= (int)(NUMBER_OF_MAPTILES * 0.2); 
	int enemyAI 		= 2;
	int numOfMountains	= (int)(totalObstacles * 0.7);
	int numOfSwamps		= totalObstacles - numOfMountains;
	int totalVillages	= 5;


	private int playerFocusX = -10; // Default player to not be on any specific tile
	private int playerFocusY = -10;
	private int playerControlledTroopCount = 0; // Does not control any troops by default
	private String playerID = "player";
	private Color playerColor = BLUE;
	char direction = ' ';  // set to ' ' for default


	GamePanel() {
		map = new Tile[MAP_WIDTH][MAP_HEIGHT];
		random = new Random();
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));	
		this.setBackground(DARKGRAY);							
		this.setFocusable(true);								// Grab Focus
		this.addKeyListener(new MyKeyAdapter());				// Allows for user input
		this.addMouseListener(new MyMouseAdapter());

		newGame();												// Make a Game
	}

	public Tile[][] generateMap() {
		/**
		 * 	Populate the map with empty null tiles, then perform spreading algorithm to disperse extra tiles:
		 * 	Spreading method operates on several constants, we want a percentage of total tiles to be certain
		 * 	types. We also need the player spawn location to be determined, as well as any enemy spawn tiles. 
		 * 	See [Tile Spread Statistics] to modify
		 * 
		 * 		Rules:
		 * 			1) If > 4 immediate neighbors are an obstacle, delete current mountain
		 * 			2) If < 2 immediate neighbors is a obstacle, make a mountain
		 * 			3) If > 3 immediate neighbors is a obstacle, do not place player/entity spawn, find a new spot
		 */

		// Step 1: Populate the map with empty tiles
		for (int i = 0; i < MAP_WIDTH; i++) {
			for (int j = 0; j < MAP_HEIGHT; j++) {

				map[i][j] = new Tile(i, j); 

				// If the tile is at an edge, make it an obstacle
				if (i == 0 || i == MAP_WIDTH - 1 || j == 0 || j == MAP_HEIGHT - 1) 		  { 
					map[i][j].setTileType(1); 				// 1 = Mountain
					map[i][j].setBackgroundColor(DARKGRAY);
					map[i][j].setIcon(mountain);
				} else { // If it is not an edge, do cellular automata
					
					// TODO Cellular automata
					int type = random.nextInt((int)(2));
					if (type == 10) {
						map[i][j].setTileType(1); 				// 1 = Mountain
						map[i][j].setBackgroundColor(DARKGRAY);
						map[i][j].setIcon(mountain);
					}
				}

			}
		}
		map[1][1].setTroopCount(40);
		map[1][1].setOwnedBy(playerID);
		map[1][1].setBackgroundColor(BLUE);

		map[10][1].setTroopCount(5);
		map[10][1].setOwnedBy("bruh");
		map[10][1].setBackgroundColor(RED);


		System.out.println("Map Generated");
		return map;
	}



	public void newGame() {
		loadAssets();
		map = generateMap();			// Make a new map
		running = true; 	
		timer = new Timer(DELAY, this); // Pass in the desired time delay: "this" is because we are using the ActionListener
		timer.start(); 					// start the clock
		System.out.println("New Game has started! Good Luck");
	}


	public void loadAssets() {
		String imageFileExtension = "/assets/textures/";
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}



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
							
							g.drawString("" + map[i][j].getTroopCount(), 						// Add the "" to fool java 
									((map[i][j].getX() * TILESIZE) ),	// x coordinate
									(map[i][j].getY() * TILESIZE) + g.getFont().getSize());	
						}
					}
				}
			}

			if (drawgrid){//To make things easier to see in this game, we are going to draw a grid
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


	public void run() {
		if (running) {
			//checkCollisions();
			//if (doDebug) { debug(); }
			currentTime = timer.getDelay();
			double lastRenderTimeInSeconds = (currentTime - lastUpdate); 
			timePassed += lastRenderTimeInSeconds;
			lastUpdate = currentTime;

			if (timePassed > updateRate) {
				update();
				timePassed -= updateRate;
				if (doDebug) { System.out.println("ADDING TROOPS>>>"); }
				
				
				if (true) {
					for (int i = 0; i < map.length; i++) {
						for (int j = 0; j < map[i].length; j++) {
							if(map[i][j].getOwnedBy() != null && map[i][j].getTroopCount() > 0) {
								map[i][j].setTroopCount(map[i][j].getTroopCount() + 1);
								if (doDebug) { System.out.println("Adding Troops"); }
							}
						}
					}
				}
			} 
		}
		repaint();
	}



	public void update() {

		checkCollisions();
		if (doDebug) { debug(); }
		// Once every game tick, move player, move enemy, increment generators

		// Once every fourth game tick, increment any tile that has a troop on it

	}



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



	public void gameOver(Graphics g) {}



	@Override
	public void actionPerformed(ActionEvent e) {
		run();
	}


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

			doDebug = true;
		}
	}






	public void debug() {
		System.out.println("Current Tile owned by:		" + map[playerFocusX][playerFocusY].getOwnedBy());
		System.out.println("Current Tile Troop Count:	" + map[playerFocusX][playerFocusY].getTroopCount());
		System.out.println("Current playerControlledTroops: " + playerControlledTroopCount);
		System.out.println("Current Tile Type:	" + map[playerFocusX][playerFocusY].getTileType());
		if (doDebug) { System.out.println("Called Update at: " + timer.getDelay()); }

		System.out.println();
	}
}











