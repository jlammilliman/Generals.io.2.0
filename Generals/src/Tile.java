import java.awt.Color;
import java.awt.Image;

public class Tile {
	private int x;
	private int y;
	private int tileType = 0; //[0 = Empty : 1 = Mountain : 2 = City : 3 = Swamp : 6 = Capitol]
	private String ownedBy = "noOne";
	private int troopCount = 0;
	private Color backgroundColor = new Color(222, 222, 222);
	private Image icon = null;	// Null by Default
	
	
	Tile(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	Tile(int x, int y, String ownedBy){
		this.x = x;
		this.y = y;
		setOwnedBy(ownedBy);
	}
	
	/**
	 * 	This method will take in the tileType, new ownerID and new Color to update the current tile
	 * 
	 * @param type : pass in an int for the tile type [Default = 0 : Mountain = 1 : City = 2 : Swamp = 3 : Capitol = 6]
	 * @param owner : the new string for the ID you want this tile to hold
	 * @param backgroundColor : the new background color, most likely the player color
	 * @param troops : this integer value will be set to the currentTroop count on the tile
	 */
	public void updateTile(int type, String ID, Color color, int troops, Image icon) {
		if (type != -1) { this.tileType = type; }
		this.ownedBy = ID;
		this.backgroundColor = color;
		this.troopCount = troops;
		if (icon != null) { this.icon = icon; }
	}
	
	
//############################################################
//		Nasty Nasty Getters and Settlers
//############################################################
	
	public Image getIcon() {
		return icon;
	}
	public void setIcon(Image icon) {
		this.icon = icon;
	}
	public int getTroopCount() {
		return troopCount;
	}
	public void setTroopCount(int troopCount) {
		this.troopCount = troopCount;
	}
	public Color getBackgroundColor() {
		return backgroundColor;
	}
	public void setBackgroundColor(Color backgroundColor) {
		this.backgroundColor = backgroundColor;
	}
	public String getOwnedBy() {
		return ownedBy;
	}
	public void setOwnedBy(String ownedBy) {
		this.ownedBy = ownedBy;
	}
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public int getTileType() {
		return tileType;
	}
	public void setTileType(int tileType) {
		this.tileType = tileType;
	}
}

