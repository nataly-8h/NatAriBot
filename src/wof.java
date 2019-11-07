import java.awt.*;
import javax.swing.*;
public class wof extends JFrame{
	public wof() {
		super("WOOOF OVERDRIVE");
		this.setSize(720, 700);
		
//		JTextField e = new JTextField();
//		add(e);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setVisible(true);

	}
	
	public void paint(Graphics g) {
		super.paint(g);
		g.drawOval(100,200, 100, 100);
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("WOF");
		wof a = new wof();
	
	}

}
