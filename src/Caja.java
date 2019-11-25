/* EQUIPO: ARI VALENZUELA (A01635584)
 * 	NATALY HERNANDEZ (A01631314)
 * NOMBRE DEL JUEGO: NATARIBOT
 * NOMBRE DE LA CLASE: Caja.java
 * FECHA: 25/11/19
 * COMENTARIOS Y OBSERVACIONES: Es la clase donde se manejan las cajas del juego.
 */

import java.awt.Color;

import javax.swing.ImageIcon;

public class Caja {
	private int x,
				y,
				color;
	
	public Caja(int color) {
		
		this.color = color;
		
	}
	public int getColor() {
		return color;
	}
	public void setColor(int color) {
		this.color = color;
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
	
	
	
}
