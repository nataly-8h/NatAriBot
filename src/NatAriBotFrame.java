/* EQUIPO: ARI VALENZUELA (A01635584)
 * 	NATALY HERNANDEZ (A01631314)
 * NOMBRE DEL JUEGO: NATARIBOT
 * NOMBRE DE LA CLASE: NatAriBotFrame.java
 * FECHA: 25/11/19
 * COMENTARIOS Y OBSERVACIONES: Esta es la clase donde se crea el frame que contiene el panel del juego.
 */

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
