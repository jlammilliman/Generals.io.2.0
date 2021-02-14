package generals2;
import javax.swing.JFrame;

public class GameFrame extends JFrame{
	
	GameFrame() {
		this.add(new GamePanel()); //Shortcut method of adding the game panel to the frame
		this.setTitle("Generals");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setResizable(false);;
		this.pack(); // This will fit the JFrame around all of the other components that we add to the frame
		this.setVisible(true);
		this.setLocationRelativeTo(null);//Set the window to appear in the center of the screen
	}
}
