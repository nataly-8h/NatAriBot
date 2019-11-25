/* EQUIPO: ARI VALENZUELA (A01635584)
 * 	NATALY HERNANDEZ (A01631314)
 * NOMBRE DEL JUEGO: NATARIBOT
 * NOMBRE DE LA CLASE: BinaryNode.java
 * FECHA: 25/11/19
 * COMENTARIOS Y OBSERVACIONES: Esta es la clase de los nodos del árbol AVL.
 */

public class BinaryNode {
	private int value;
	private int height;
	private BinaryNode left;
	private BinaryNode right;

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public BinaryNode getLeft() {
		return left;
	}

	public void setLeft(BinaryNode left) {
		this.left = left;
	}

	public BinaryNode getRight() {
		return right;
	}

	public void setRight(BinaryNode right) {
		this.right = right;
	}

	@Override
	public String toString() {
		return value + "";
	}

}
