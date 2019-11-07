import java.awt.Color;

public class Caja {
	private String color;
	private int x,
				y;
	public Caja(String color) {
		this.x = 0;
		this.y = 0;
		this.color = color;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
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
