import javax.swing.JFrame;

public class NatAriBotFrame extends JFrame {
	public NatAriBotFrame() {
		super("NatAriBot el juego");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		NatAriBotJuego natari = new NatAriBotJuego();
		this.add(natari);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		
	}

	public static void main(String[] args) {
		NatAriBotFrame na = new NatAriBotFrame();
	}

}
