
public class Garra {
	private int posX, posY, largo, derX, derY, izqX, izqY, barraX;

	private boolean open, arriba;

	public Garra(int posX) {
		super();
		this.posX = posX;
		this.posY = 47;
		this.largo = posY - 17;
		this.barraX = posX + 29;
		this.derX = posX - 7;
		this.derY = posY - 4;
		this.izqX = posX + 69;
		this.izqY = posY - 4;
		this.open = true;
		this.arriba = true;
	}

	public boolean isArriba() {
		return arriba;
	}

	public void setArriba(boolean arriba) {
		this.arriba = arriba;
	}

	public void setDerX(int derX) {
		this.derX = derX;
	}

	public void setDerY(int derY) {
		this.derY = derY;
	}

	public void setIzqX(int izqX) {
		this.izqX = izqX;
	}

	public void setIzqY(int izqY) {
		this.izqY = izqY;
	}

	public int getPosX() {
		return posX;
	}

	public void setPosX(int posX) {
		if (this.open) {
			this.derX = posX - 7;
			this.izqX = posX + 69;
		} else {
			this.derX = posX;
			this.izqX = posX + 61;
		}

		this.barraX = posX + 29;
		this.posX = posX;
	}

	public int getPosY() {
		return posY;
	}

	public void setPosY(int posY) {
		this.largo = posY - 17;
		this.derY = posY - 4;
		this.izqY = posY - 4;
		this.posY = posY;
	}

	public int getLargo() {
		return largo;
	}

	public void setLargo(int cajas) {
		this.largo = largo - 50 * cajas;
	}

	public int getDerX() {
		return derX;
	}

	public int getBarraX() {
		return barraX;
	}

	public int getDerY() {
		return derY;
	}

	public int getIzqX() {
		return izqX;
	}

	public int getIzqY() {
		return izqY;
	}

	public boolean isOpen() {
		return open;
	}

	public void setOpen(boolean open) {
		this.open = open;
	}

}
