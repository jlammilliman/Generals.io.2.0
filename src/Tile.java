import java.awt.Color;
import java.awt.Image;

public class Tile {
	private int x;
	private int y;
	private int tileType = 0; //[0 = Empty : 1 = Mountain : 2 = Capitol]
	private String ownedBy = "noOne";
	private int troopCount = 0;
	private Color backgroundColor = new Color(222, 222, 222);
	private Image icon = null;	// Null by Default
	
	
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

	Tile(int x, int y){
		this.x = x;
		this.y = y;
	}
	
	Tile(int x, int y, String ownedBy){
		this.x = x;
		this.y = y;
		setOwnedBy(ownedBy);
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

