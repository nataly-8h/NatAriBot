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
