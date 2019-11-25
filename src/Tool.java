/* EQUIPO: ARI VALENZUELA (A01635584)
 * 	NATALY HERNANDEZ (A01631314)
 * NOMBRE DEL JUEGO: NATARIBOT
 * NOMBRE DE LA CLASE: Tool.java
 * FECHA: 25/11/19
 * COMENTARIOS Y OBSERVACIONES: Esta es la clase donde se manejan las herramientas.
 */

public class Tool {
	private String accion;
	private int x,
				y;
	
	public Tool(String accion) {
		this.x = 0;
		this.y = 0;
		this.accion = accion;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
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
