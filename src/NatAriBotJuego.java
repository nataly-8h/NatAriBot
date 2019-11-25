import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Queue;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Timer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class NatAriBotJuego extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	private Image img, derTrue, derFalse, downTrue, downFalse, izqTrue, izqFalse, borde, gameBox, progBox, pro1, pro2,
			pro3, pro4, ifRed, ifYell, ifGreen, ifBlue, ifAll, ifNone, barraGarra, garraDer, garraIzq;

	private Image[] cajaImage = new Image[4];

	private Caja caja;

	private Garra garra;

	private Tool[] toolbox, programa1, programa2, programa3, programa4;

	private BinaryNode nodeCurrent, nodePrevious;

	private Stack<Caja>[] cajas, meta;

	private int espacios, maxCajas, coorToolx, coorTooly, posGarra, prevPosGarra, maxAcciones, numAcciones;

	private String nivel;

	private boolean play, gameOver, flagDer, flagIzq, flagDown, flagProg1, flagProg2, flagProg3, flagProg4, flagIfRed,
			flagIfYell, flagIfGreen, flagIfBlue, flagIfAll, flagIfNone, hasCaja, win;

	private Hashtable<Integer, String> niveles;
	private AVLTree avl;

	private Thread th;

	public NatAriBotJuego() {
		super();
		this.setPreferredSize(new Dimension(1200, 735));
		this.setOpaque(true);
		this.setBackground(Color.BLACK);
		this.addKeyListener(this);
		this.setFocusable(true);
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		this.maxAcciones = 1000;
		this.numAcciones = 0;
		this.avl = new AVLTree();
		this.prevPosGarra = 0;
		this.posGarra = 0;
		this.niveles = new Hashtable<Integer, String>();
		this.toolbox = new Tool[13];
		this.play = false;
		this.maxCajas = 6;
		try {
			int count = 1;
			String linea;
			BufferedReader br = new BufferedReader(new FileReader("nivel.txt"));
			while ((linea = br.readLine()) != null) {
				niveles.put(count, linea);
				avl.insert(count);
				count++;
			}
			br.close();
		} catch (FileNotFoundException ex) {
			System.out.println("No se localiz� el archivo " + ex);
		} catch (IOException ex) {
			System.out.println("Ocurri� un error de I/O " + ex);
		}
		nivel = niveles.get(avl.root.getValue());
		this.nodeCurrent = avl.root;
		this.nodePrevious = avl.root;

		this.paintLevel();

		this.derTrue = new ImageIcon("DerTrue.png").getImage();
		this.derFalse = new ImageIcon("DerFalse.png").getImage();
		this.downTrue = new ImageIcon("DownTrue.png").getImage();
		this.downFalse = new ImageIcon("DownFalse.png").getImage();
		this.izqTrue = new ImageIcon("IzqTrue.png").getImage();
		this.izqFalse = new ImageIcon("IzqFalse.png").getImage();
		this.borde = new ImageIcon("borde.png").getImage();

		this.gameBox = new ImageIcon("gameBox.png").getImage();
		this.progBox = new ImageIcon("progbox.png").getImage();

		this.pro1 = new ImageIcon("P1.png").getImage();
		this.pro2 = new ImageIcon("P2.png").getImage();
		this.pro3 = new ImageIcon("P3.png").getImage();
		this.pro4 = new ImageIcon("P4.png").getImage();

		this.ifRed = new ImageIcon("ifRed.png").getImage();
		this.ifYell = new ImageIcon("ifYell.png").getImage();
		this.ifGreen = new ImageIcon("ifGreen.png").getImage();
		this.ifBlue = new ImageIcon("ifBlue.png").getImage();
		this.ifAll = new ImageIcon("ifAll.png").getImage();
		this.ifNone = new ImageIcon("ifNone.png").getImage();

		this.barraGarra = new ImageIcon("barragarra.png").getImage();
		this.garraDer = new ImageIcon("garraDer.png").getImage();
		this.garraIzq = new ImageIcon("garraIzq.png").getImage();

		this.cajaImage[0] = new ImageIcon("redbox.png").getImage();
		this.cajaImage[1] = new ImageIcon("yellbox.png").getImage();
		this.cajaImage[2] = new ImageIcon("greenbox.png").getImage();
		this.cajaImage[3] = new ImageIcon("bluebox.png").getImage();

		this.img = new ImageIcon("pilarMen.jpg").getImage();

		// Initialize Thread

		// garra
		if (this.espacios == 2 || this.espacios == 3 || this.espacios == 4) {
			garra = new Garra(239);
		} else if (this.espacios == 5) {
			garra = new Garra(149);
		} else if (this.espacios == 6 || this.espacios == 7) {
			garra = new Garra(59);
		}

		// cajitas

		for (int i = 0; i < this.espacios; i++) {
			for (int j = 0; j < this.cajas[i].size(); j++) {
				if (this.espacios == 2 || this.espacios == 3 || this.espacios == 4) {
					this.cajas[i].get(j).setX(254 + i * 90);
				} else if (this.espacios == 5) {
					this.cajas[i].get(j).setX(164 + i * 90);
				} else if (this.espacios == 6 || this.espacios == 7) {
					this.cajas[i].get(j).setX(74 + i * 90);
				}

				this.cajas[i].get(j).setY(333 - j * 50);

				// System.out.println(this.cajas[i].get(j).getX() + " " +
				// this.cajas[i].get(j).getY());

			}
		}

		this.th = new Thread(this);
		this.th.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		// PANEL DE JUEGO
		g.drawImage(this.gameBox, 17, 17, this);
		// g.fillRect(17, 17, 700, 400);

		// bordes
		if (this.espacios == 2) {
			g.drawImage(this.borde, 214, 17, 17, 400, this);
			g.drawImage(this.borde, 413, 17, 17, 400, this);
		} else if (this.espacios == 3) {
			g.drawImage(this.borde, 214, 17, 17, 400, this);
			g.drawImage(this.borde, 503, 17, 17, 400, this);

		} else if (this.espacios == 4) {
			g.drawImage(this.borde, 214, 17, 17, 400, this);
			g.drawImage(this.borde, 593, 17, 17, 400, this);

		} else if (this.espacios == 5) {
			g.drawImage(this.borde, 124, 17, 17, 400, this);
			g.drawImage(this.borde, 593, 17, 17, 400, this);

		} else if (this.espacios == 6) {
			g.drawImage(this.borde, 34, 17, 17, 400, this);
			g.drawImage(this.borde, 593, 17, 17, 400, this);

		} else if (this.espacios == 7) {
			g.drawImage(this.borde, 34, 17, 17, 400, this);
			g.drawImage(this.borde, 683, 17, 17, 400, this);

		}

		// garra

		g.drawImage(borde, garra.getBarraX(), 17, 17, garra.getLargo(), this);
		g.drawImage(barraGarra, garra.getPosX(), garra.getPosY(), 76, 17, this);
		g.drawImage(garraDer, garra.getDerX(), garra.getDerY(), 15, 60, this);
		g.drawImage(garraIzq, garra.getIzqX(), garra.getIzqY(), 15, 60, this);

		// base de cajitas
		for (int i = 0; i < this.espacios; i++) {
			if (this.espacios == 2) {
				g.fillRect(239 + 90 * i, 383, 76, 17);
			} else if (this.espacios == 3) {
				g.fillRect(239 + 90 * i, 383, 76, 17);
			} else if (this.espacios == 4) {
				g.fillRect(239 + 90 * i, 383, 76, 17);
			} else if (this.espacios == 5) {
				g.fillRect(149 + 90 * i, 383, 76, 17);
			} else if (this.espacios == 6) {
				g.fillRect(59 + 90 * i, 383, 76, 17);
			} else if (this.espacios == 7) {
				g.fillRect(59 + 90 * i, 383, 76, 17);
			}
		}

		// cajitas

		for (int i = 0; i < this.espacios; i++) {
			for (int j = 0; j < this.cajas[i].size(); j++) {
				Stack<Caja> cajaCopia = (Stack<Caja>) this.cajas[i].clone();
				if (this.cajas[i].get(j).getColor() == 1) {
					g.drawImage(this.cajaImage[0], this.cajas[i].get(j).getX(), this.cajas[i].get(j).getY(), 46, 46,
							this);
				} else if (this.cajas[i].get(j).getColor() == 2) {
					g.drawImage(this.cajaImage[1], this.cajas[i].get(j).getX(), this.cajas[i].get(j).getY(), 46, 46,
							this);
				} else if (this.cajas[i].get(j).getColor() == 3) {
					g.drawImage(this.cajaImage[2], this.cajas[i].get(j).getX(), this.cajas[i].get(j).getY(), 46, 46,
							this);
				} else if (this.cajas[i].get(j).getColor() == 4) {
					g.drawImage(this.cajaImage[3], this.cajas[i].get(j).getX(), this.cajas[i].get(j).getY(), 46, 46,
							this);
				}
			}
		}

		// PROGRAMA
		g.drawImage(progBox, 17, 434, this);

		// programas
		g.setColor(Color.gray);
		g.fillRect(34, 451, 666, 47);
		g.fillRect(34, 507, 666, 47);
		g.fillRect(34, 563, 666, 47);
		g.fillRect(34, 619, 666, 47);

		g.setColor(Color.YELLOW);
		g.fillRect(156, 454, 65, 42);

		// Espacios: 68 de separaci�n
		g.setColor(new Color(19, 79, 158));

		for (int i = 0; i < this.programa1.length; i++) {
			g.fillRect(156 + 68 * i, 454, 65, 42);
		}

		for (int i = 0; i < this.programa2.length; i++) {
			g.fillRect(156 + 68 * i, 510, 65, 42);
		}

		for (int i = 0; i < this.programa3.length; i++) {
			g.fillRect(156 + 68 * i, 566, 65, 42);
		}

		for (int i = 0; i < this.programa4.length; i++) {
			g.fillRect(156 + 68 * i, 622, 65, 42);
		}

		g.setColor(Color.WHITE);
		g.drawString(" P R O G R A M A   1 ", 38, 478);
		g.drawString(" P R O G R A M A   2 ", 38, 534);
		g.drawString(" P R O G R A M A   3 ", 38, 588);
		g.drawString(" P R O G R A M A   4 ", 38, 644);

		for (int i = 0; i < this.programa1.length; i++) {
			if (this.programa1[i] != null) {
				if (this.programa1[i].getAccion() == "der") {
					g.drawImage(this.derTrue, 156 + 68 * i, 454, 65, 42, this);
				} else if (this.programa1[i].getAccion() == "izq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 454, 65, 42, this);
				} else if (this.programa1[i].getAccion() == "down") {
					g.drawImage(this.downTrue, 156 + 68 * i, 454, 65, 42, this);
				} else if (this.programa1[i].getAccion() == "prog1") {
					g.drawImage(this.pro1, 156 + 68 * i, 454, 65, 42, this);
				} else if (this.programa1[i].getAccion() == "prog2") {
					g.drawImage(this.pro2, 156 + 68 * i, 454, 65, 42, this);
				} else if (this.programa1[i].getAccion() == "prog3") {
					g.drawImage(this.pro3, 156 + 68 * i, 454, 65, 42, this);
				} else if (this.programa1[i].getAccion() == "prog4") {
					g.drawImage(this.pro4, 156 + 68 * i, 454, 65, 42, this);
				}

				else if (this.programa1[i].getAccion() == "ifRed") {
					g.drawImage(this.ifRed, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "ifYell") {
					g.drawImage(this.ifYell, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "ifGreen") {
					g.drawImage(this.ifGreen, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "ifBlue") {
					g.drawImage(this.ifBlue, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "ifNone") {
					g.drawImage(this.ifNone, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "ifAll") {
					g.drawImage(this.ifAll, 156 + 68 * i, 454, this);
				}

				else if (this.programa1[i].getAccion() == "redIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "yellIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "greenIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "blueIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "noneIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "allIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 454, this);
				}

				else if (this.programa1[i].getAccion() == "redIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "yellIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "greenIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "blueIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "noneIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "allIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 454, this);
				}

				else if (this.programa1[i].getAccion() == "redIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "yellIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "greenIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "blueIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "noneIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "allIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 454, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 454, this);
				}

				else if (this.programa1[i].getAccion() == "redIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 454, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "yellIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 454, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "greenIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 454, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "blueIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 454, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "noneIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 454, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "allIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 454, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 454, this);
				}

				else if (this.programa1[i].getAccion() == "redIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 454, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "yellIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 454, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "greenIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 454, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "blueIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 454, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "noneIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 454, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "allIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 454, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 454, this);
				}

				else if (this.programa1[i].getAccion() == "redIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 454, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "yellIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 454, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "greenIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 454, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "blueIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 454, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "noneIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 454, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "allIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 454, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 454, this);
				}

				else if (this.programa1[i].getAccion() == "redIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 454, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "yellIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 454, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "greenIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 454, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "blueIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 454, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "noneIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 454, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 454, this);
				} else if (this.programa1[i].getAccion() == "allIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 454, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 454, this);
				}
			} else {
				continue;
			}
		}

		for (int i = 0; i < this.programa2.length; i++) {
			if (this.programa2[i] != null) {
				if (this.programa2[i].getAccion() == "der") {
					g.drawImage(this.derTrue, 156 + 68 * i, 510, 65, 42, this);
				} else if (this.programa2[i].getAccion() == "izq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 510, 65, 42, this);
				} else if (this.programa2[i].getAccion() == "down") {
					g.drawImage(this.downTrue, 156 + 68 * i, 510, 65, 42, this);
				} else if (this.programa2[i].getAccion() == "prog1") {
					g.drawImage(this.pro1, 156 + 68 * i, 510, 65, 42, this);
				} else if (this.programa2[i].getAccion() == "prog2") {
					g.drawImage(this.pro2, 156 + 68 * i, 510, 65, 42, this);
				} else if (this.programa2[i].getAccion() == "prog3") {
					g.drawImage(this.pro3, 156 + 68 * i, 510, 65, 42, this);
				} else if (this.programa2[i].getAccion() == "prog4") {
					g.drawImage(this.pro4, 156 + 68 * i, 510, 65, 42, this);
				}

				else if (this.programa2[i].getAccion() == "ifRed") {
					g.drawImage(this.ifRed, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "ifYell") {
					g.drawImage(this.ifYell, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "ifGreen") {
					g.drawImage(this.ifGreen, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "ifBlue") {
					g.drawImage(this.ifBlue, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "ifNone") {
					g.drawImage(this.ifNone, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "ifAll") {
					g.drawImage(this.ifAll, 156 + 68 * i, 510, this);
				}

				else if (this.programa2[i].getAccion() == "redIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "yellIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "greenIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "blueIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "noneIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "allIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 510, this);
				}

				else if (this.programa2[i].getAccion() == "redIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "yellIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "greenIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "blueIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "noneIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "allIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 510, this);
				}

				else if (this.programa2[i].getAccion() == "redIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "yellIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "greenIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "blueIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "noneIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "allIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 510, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 510, this);
				}

				else if (this.programa2[i].getAccion() == "redIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 510, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "yellIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 510, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "greenIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 510, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "blueIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 510, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "noneIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 510, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "allIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 510, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 510, this);
				}

				else if (this.programa2[i].getAccion() == "redIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 510, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "yellIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 510, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "greenIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 510, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "blueIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 510, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "noneIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 510, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "allIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 510, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 510, this);
				}

				else if (this.programa2[i].getAccion() == "redIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 510, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "yellIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 510, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "greenIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 510, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "blueIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 510, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "noneIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 510, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "allIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 510, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 510, this);
				}

				else if (this.programa2[i].getAccion() == "redIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 510, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "yellIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 510, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "greenIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 510, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "blueIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 510, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "noneIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 510, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 510, this);
				} else if (this.programa2[i].getAccion() == "allIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 510, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 510, this);
				}
			} else {
				continue;
			}
		}

		for (int i = 0; i < this.programa3.length; i++) {
			if (this.programa3[i] != null) {
				if (this.programa3[i].getAccion() == "der") {
					g.drawImage(this.derTrue, 156 + 68 * i, 566, 65, 42, this);
				} else if (this.programa3[i].getAccion() == "izq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 566, 65, 42, this);
				} else if (this.programa3[i].getAccion() == "down") {
					g.drawImage(this.downTrue, 156 + 68 * i, 566, 65, 42, this);
				} else if (this.programa3[i].getAccion() == "prog1") {
					g.drawImage(this.pro1, 156 + 68 * i, 566, 65, 42, this);
				} else if (this.programa3[i].getAccion() == "prog2") {
					g.drawImage(this.pro2, 156 + 68 * i, 566, 65, 42, this);
				} else if (this.programa3[i].getAccion() == "prog3") {
					g.drawImage(this.pro3, 156 + 68 * i, 566, 65, 42, this);
				} else if (this.programa3[i].getAccion() == "prog4") {
					g.drawImage(this.pro4, 156 + 68 * i, 566, 65, 42, this);
				}

				else if (this.programa3[i].getAccion() == "ifRed") {
					g.drawImage(this.ifRed, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "ifYell") {
					g.drawImage(this.ifYell, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "ifGreen") {
					g.drawImage(this.ifGreen, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "ifBlue") {
					g.drawImage(this.ifBlue, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "ifNone") {
					g.drawImage(this.ifNone, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "ifAll") {
					g.drawImage(this.ifAll, 156 + 68 * i, 566, this);
				}

				else if (this.programa3[i].getAccion() == "redIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "yellIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "greenIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "blueIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "noneIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "allIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 566, this);
				}

				else if (this.programa3[i].getAccion() == "redIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "yellIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "greenIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "blueIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "noneIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "allIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 566, this);
				}

				else if (this.programa3[i].getAccion() == "redIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "yellIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "greenIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "blueIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "noneIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "allIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 566, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 566, this);
				}

				else if (this.programa3[i].getAccion() == "redIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 566, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "yellIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 566, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "greenIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 566, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "blueIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 566, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "noneIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 566, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "allIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 566, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 566, this);
				}

				else if (this.programa3[i].getAccion() == "redIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 566, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "yellIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 566, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "greenIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 566, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "blueIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 566, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "noneIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 566, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "allIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 566, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 566, this);
				}

				else if (this.programa3[i].getAccion() == "redIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 566, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "yellIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 566, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "greenIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 566, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "blueIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 566, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "noneIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 566, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "allIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 566, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 566, this);
				}

				else if (this.programa3[i].getAccion() == "redIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 566, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "yellIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 566, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "greenIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 566, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "blueIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 566, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "noneIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 566, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 566, this);
				} else if (this.programa3[i].getAccion() == "allIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 566, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 566, this);
				}
			} else {
				continue;
			}
		}

		for (int i = 0; i < this.programa4.length; i++) {
			if (this.programa4[i] != null) {
				if (this.programa4[i].getAccion() == "der") {
					g.drawImage(this.derTrue, 156 + 68 * i, 622, 65, 42, this);
				} else if (this.programa4[i].getAccion() == "izq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 622, 65, 42, this);
				} else if (this.programa4[i].getAccion() == "down") {
					g.drawImage(this.downTrue, 156 + 68 * i, 622, 65, 42, this);
				} else if (this.programa4[i].getAccion() == "prog1") {
					g.drawImage(this.pro1, 156 + 68 * i, 622, 65, 42, this);
				} else if (this.programa4[i].getAccion() == "prog2") {
					g.drawImage(this.pro2, 156 + 68 * i, 622, 65, 42, this);
				} else if (this.programa4[i].getAccion() == "prog3") {
					g.drawImage(this.pro3, 156 + 68 * i, 622, 65, 42, this);
				} else if (this.programa4[i].getAccion() == "prog4") {
					g.drawImage(this.pro4, 156 + 68 * i, 622, 65, 42, this);
				}

				else if (this.programa4[i].getAccion() == "ifRed") {
					g.drawImage(this.ifRed, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "ifYell") {
					g.drawImage(this.ifYell, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "ifGreen") {
					g.drawImage(this.ifGreen, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "ifBlue") {
					g.drawImage(this.ifBlue, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "ifNone") {
					g.drawImage(this.ifNone, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "ifAll") {
					g.drawImage(this.ifAll, 156 + 68 * i, 622, this);
				}

				else if (this.programa4[i].getAccion() == "redIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "yellIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "greenIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "blueIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "noneIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "allIfDer") {
					g.drawImage(this.derTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 622, this);
				}

				else if (this.programa4[i].getAccion() == "redIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "yellIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "greenIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "blueIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "noneIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "allIfIzq") {
					g.drawImage(this.izqTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 622, this);
				}

				else if (this.programa4[i].getAccion() == "redIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "yellIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "greenIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "blueIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "noneIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "allIfDown") {
					g.drawImage(this.downTrue, 156 + 68 * i, 622, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 622, this);
				}

				else if (this.programa4[i].getAccion() == "redIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 622, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "yellIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 622, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "greenIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 622, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "blueIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 622, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "noneIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 622, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "allIfProg1") {
					g.drawImage(this.pro1, 156 + 68 * i, 622, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 622, this);
				}

				else if (this.programa4[i].getAccion() == "redIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 622, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "yellIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 622, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "greenIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 622, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "blueIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 622, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "noneIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 622, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "allIfProg2") {
					g.drawImage(this.pro2, 156 + 68 * i, 622, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 622, this);
				}

				else if (this.programa4[i].getAccion() == "redIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 622, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "yellIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 622, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "greenIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 622, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "blueIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 622, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "noneIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 622, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "allIfProg3") {
					g.drawImage(this.pro3, 156 + 68 * i, 622, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 622, this);
				}

				else if (this.programa4[i].getAccion() == "redIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 622, this);
					g.drawImage(this.ifRed, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "yellIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 622, this);
					g.drawImage(this.ifYell, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "greenIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 622, this);
					g.drawImage(this.ifGreen, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "blueIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 622, this);
					g.drawImage(this.ifBlue, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "noneIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 622, this);
					g.drawImage(this.ifNone, 156 + 68 * i, 622, this);
				} else if (this.programa4[i].getAccion() == "allIfProg4") {
					g.drawImage(this.pro4, 156 + 68 * i, 622, this);
					g.drawImage(this.ifAll, 156 + 68 * i, 622, this);
				}

			} else {
				continue;
			}

		}

		// GOAL
		g.setColor(Color.BLUE);
		g.fillRect(734, 17, 449, 400);
		g.setColor(Color.WHITE);
		for (int i = 0; i < this.espacios; i++) {
			if (this.espacios == 2) {
				g.fillRect(922 + 64 * i, 383, 50, 17);
			} else if (this.espacios == 3) {
				g.fillRect(922 + 64 * i, 383, 50, 17);
			} else if (this.espacios == 4) {
				g.fillRect(922 + 64 * i, 383, 50, 17);
			} else if (this.espacios == 5) {
				g.fillRect(832 + 64 * i, 383, 50, 17);
			} else if (this.espacios == 6) {
				g.fillRect(742 + 64 * i, 383, 50, 17);
			} else if (this.espacios == 7) {
				g.fillRect(742 + 64 * i, 383, 50, 17);
			}
		}

		for (int i = 0; i < this.espacios; i++) {
			for (int j = 0; j < this.meta[i].size(); j++) {
				Stack<Caja> metaCopia = (Stack<Caja>) this.meta[i].clone();
				if (this.espacios == 2) {
					if (this.meta[i].get(j).getColor() == 1) {
						g.drawImage(this.cajaImage[0], 922 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 2) {
						g.drawImage(this.cajaImage[1], 922 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 3) {
						g.drawImage(this.cajaImage[2], 922 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 4) {
						g.drawImage(this.cajaImage[3], 922 + i * 64, 333 - j * 50, 46, 46, this);
					}
				} else if (this.espacios == 3) {
					if (this.meta[i].get(j).getColor() == 1) {
						g.drawImage(this.cajaImage[0], 922 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 2) {
						g.drawImage(this.cajaImage[1], 922 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 3) {
						g.drawImage(this.cajaImage[2], 922 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 4) {
						g.drawImage(this.cajaImage[3], 922 + i * 64, 333 - j * 50, 46, 46, this);
					}
				} else if (this.espacios == 4) {
					if (this.meta[i].get(j).getColor() == 1) {
						g.drawImage(this.cajaImage[0], 922 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 2) {
						g.drawImage(this.cajaImage[1], 922 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 3) {
						g.drawImage(this.cajaImage[2], 922 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 4) {
						g.drawImage(this.cajaImage[3], 922 + i * 64, 333 - j * 50, 46, 46, this);
					}
				} else if (this.espacios == 5) {
					while (!metaCopia.isEmpty()) {
						if (this.meta[i].get(j).getColor() == 1) {
							g.drawImage(this.cajaImage[0], 922 + i * 64, 333 - j * 50, 46, 46, this);
						} else if (this.meta[i].get(j).getColor() == 2) {
							g.drawImage(this.cajaImage[1], 922 + i * 64, 333 - j * 50, 46, 46, this);
						} else if (this.meta[i].get(j).getColor() == 3) {
							g.drawImage(this.cajaImage[2], 922 + i * 64, 333 - j * 50, 46, 46, this);
						} else if (this.meta[i].get(j).getColor() == 4) {
							g.drawImage(this.cajaImage[3], 922 + i * 64, 333 - j * 50, 46, 46, this);
						}
						metaCopia.pop();
					}
				} else if (this.espacios == 6) {
					if (this.meta[i].get(j).getColor() == 1) {
						g.drawImage(this.cajaImage[0], 742 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 2) {
						g.drawImage(this.cajaImage[1], 742 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 3) {
						g.drawImage(this.cajaImage[2], 742 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 4) {
						g.drawImage(this.cajaImage[3], 742 + i * 64, 333 - j * 50, 46, 46, this);
					}
				} else if (this.espacios == 7) {
					if (this.meta[i].get(j).getColor() == 1) {
						g.drawImage(this.cajaImage[0], 742 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 2) {
						g.drawImage(this.cajaImage[1], 742 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 3) {
						g.drawImage(this.cajaImage[2], 742 + i * 64, 333 - j * 50, 46, 46, this);
					} else if (this.meta[i].get(j).getColor() == 4) {
						g.drawImage(this.cajaImage[3], 742 + i * 64, 333 - j * 50, 46, 46, this);
					}
				}
			}
		}

		// TOOLBOX
		g.setColor(new Color(19, 79, 158));
		g.fillRect(734, 434, 449, 249);

		// herramientas
		g.setColor(Color.WHITE);
		g.fillRect(751, 451, 65, 42);

		// izq der down
		for (int i = 0; i < 5; i++) {
			if (i == 0 && this.toolbox[0] != null) {
				g.drawImage(this.derTrue, 751 + 87 * i, 451, 65, 42, this);
				if (this.flagDer) {
					g.drawImage(this.derTrue, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			} else if (i == 0) {
				g.drawImage(this.derFalse, 751 + 87 * i, 451, 65, 42, this);
			} else if (i == 1 && this.toolbox[1] != null) {
				g.drawImage(this.izqTrue, 751 + 87 * i, 451, 65, 42, this);
				if (this.flagIzq) {
					g.drawImage(this.izqTrue, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			} else if (i == 1) {
				g.drawImage(this.izqFalse, 751 + 87 * i, 451, 65, 42, this);
			} else if (i == 2 && this.toolbox[2] != null) {
				g.drawImage(this.downTrue, 751 + 87 * i, 451, 65, 42, this);
				if (this.flagDown) {
					g.drawImage(this.downTrue, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			} else if (i == 2) {
				g.drawImage(this.downFalse, 751 + 87 * i, 451, 65, 42, this);
			}
		}
		// programa 1 2 3 4
		for (int i = 0; i < 5; i++) {
			if (i == 0) {
				g.drawImage(this.pro1, 751 + 87 * i, 537, this);
				if (this.flagProg1) {
					g.drawImage(this.pro1, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			} else if (i == 1) {
				g.drawImage(this.pro2, 751 + 87 * i, 537, this);
				if (this.flagProg2) {
					g.drawImage(this.pro2, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			} else if (i == 2) {
				g.drawImage(this.pro3, 751 + 87 * i, 537, this);
				if (this.flagProg3) {
					g.drawImage(this.pro3, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			} else if (i == 3) {
				g.drawImage(this.pro4, 751 + 87 * i, 537, this);
				if (this.flagProg4) {
					g.drawImage(this.pro4, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			}
		}

		// ifs

		for (int i = 0; i < 6; i++) {
			if (i == 0 && this.toolbox[7] != null) {
				g.drawImage(this.ifRed, 751 + 45 * i, 623, this);
				if (this.flagIfRed) {
					g.drawImage(this.ifRed, this.coorToolx, this.coorTooly, this);
				}
			} else if (i == 1 && this.toolbox[8] != null) {
				g.drawImage(this.ifBlue, 751 + 45 * i, 623, this);
				if (this.flagIfBlue) {
					g.drawImage(this.ifBlue, this.coorToolx, this.coorTooly, this);
				}
			} else if (i == 2 && this.toolbox[9] != null) {
				g.drawImage(this.ifGreen, 751 + 45 * i, 623, this);
				if (this.flagIfGreen) {
					g.drawImage(this.ifGreen, this.coorToolx, this.coorTooly, this);
				}
			} else if (i == 3 && this.toolbox[10] != null) {
				g.drawImage(this.ifYell, 751 + 45 * i, 623, this);
				if (this.flagIfYell) {
					g.drawImage(this.ifYell, this.coorToolx, this.coorTooly, this);
				}
			} else if (i == 4 && this.toolbox[11] != null) {
				g.drawImage(this.ifNone, 751 + 45 * i, 623, this);
				if (this.flagIfNone) {
					g.drawImage(this.ifNone, this.coorToolx, this.coorTooly, this);
				}
			} else if (i == 5 && this.toolbox[12] != null) {
				g.drawImage(this.ifAll, 751 + 45 * i, 623, this);
				if (this.flagIfAll) {
					g.drawImage(this.ifAll, this.coorToolx, this.coorTooly, this);
				}
			}
		}

		g.setColor(Color.RED);
		g.fillRect(1012, 623, 65, 42);
		g.setColor(Color.BLUE);
		g.fillRect(1099, 623, 65, 42);

		// CIRCULO DE PLAY
		if (this.play) {
			g.setColor(Color.RED);
		} else if (!this.play) {
			g.setColor(Color.GREEN);
		}
		g.fillOval(545, 683, 110, 110);

		if (win) {
			try {
				Thread.sleep(1000);
				g.drawImage(this.img, 0, 0, 100, 100, this);
			} catch (InterruptedException e) {
				System.out.println("ERROR");
			}
		}

	}

	public void paintLevelAgain() {
		StringTokenizer st = new StringTokenizer(nivel);
		int contador = 0;
		while (st.hasMoreTokens()) {
			if (contador < 4) {
				switch (contador) {
				case 0:
					st.nextToken();
					break;
				case 1:
					st.nextToken();
					break;
				case 2:
					st.nextToken();
					break;
				case 3:
					st.nextToken();
					break;
				}
			} else if (contador == 4) {
				this.espacios = Integer.parseInt(st.nextToken());
				this.cajas = (Stack<Caja>[]) new Stack[this.espacios];
				this.meta = (Stack<Caja>[]) new Stack[this.espacios];
				for (int i = 0; i < this.espacios; i++) {
					this.cajas[i] = new Stack<Caja>();
					this.meta[i] = new Stack<Caja>();
				}

				for (int i = 0; i < this.espacios; i++) {
					String cajas = st.nextToken();
					for (int j = 0; j < cajas.length(); j++) {
						if (cajas.charAt(j) == '0') {
							continue;
						} else {
							this.cajas[i].add(new Caja(Character.getNumericValue(cajas.charAt(j))));
						}
					}
				}
				for (int i = 0; i < this.espacios; i++) {
					String goal = st.nextToken();
					for (int j = 0; j < goal.length(); j++) {
						if (goal.charAt(j) == '0') {
							continue;
						} else {
							this.meta[i].add(new Caja(Character.getNumericValue(goal.charAt(j))));
						}
					}
				}
			} else {
				for (int i = 0; i < 13; i++) {
					String elemento = st.nextToken();
					if (elemento.equals("1")) {
						switch (i) {
						case 0:
							this.toolbox[i] = new Tool("derecha");
							break;
						case 1:
							this.toolbox[i] = new Tool("izquierda");
							break;
						case 2:
							this.toolbox[i] = new Tool("abajo");
							break;
						case 3:
							this.toolbox[i] = new Tool("programa1");
							break;
						case 4:
							this.toolbox[i] = new Tool("programa2");
							break;
						case 5:
							this.toolbox[i] = new Tool("programa3");
							break;
						case 6:
							this.toolbox[i] = new Tool("programa4");
							break;
						case 7:
							this.toolbox[i] = new Tool("ifRed");
							break;
						case 8:
							this.toolbox[i] = new Tool("ifYell");
							break;
						case 9:
							this.toolbox[i] = new Tool("ifGreen");
							break;
						case 10:
							this.toolbox[i] = new Tool("ifBlue");
							break;
						case 11:
							this.toolbox[i] = new Tool("ifAll");
							break;
						case 12:
							this.toolbox[i] = new Tool("ifNone");
							break;
						default:
							System.out.println("ERROR SUGOIII");
							System.exit(0);
						}
					}
				}
			}

			if (this.espacios == 2 || this.espacios == 3 || this.espacios == 4) {
				garra = new Garra(239);
			} else if (this.espacios == 5) {
				garra = new Garra(149);
			} else if (this.espacios == 6 || this.espacios == 7) {
				garra = new Garra(59);
			}

			// cajitas

			for (int i = 0; i < this.espacios; i++) {
				for (int j = 0; j < this.cajas[i].size(); j++) {
					if (this.espacios == 2 || this.espacios == 3 || this.espacios == 4) {
						this.cajas[i].get(j).setX(254 + i * 90);
					} else if (this.espacios == 5) {
						this.cajas[i].get(j).setX(164 + i * 90);
					} else if (this.espacios == 6 || this.espacios == 7) {
						this.cajas[i].get(j).setX(74 + i * 90);
					}

					this.cajas[i].get(j).setY(333 - j * 50);
				}
			}

			contador++;
		}
	}

	public void paintLevel() {
		StringTokenizer st = new StringTokenizer(nivel);
		int contador = 0;
		while (st.hasMoreTokens()) {
			if (contador < 4) {
				switch (contador) {
				case 0:
					this.programa1 = new Tool[Integer.parseInt(st.nextToken())];
					break;
				case 1:
					this.programa2 = new Tool[Integer.parseInt(st.nextToken())];
					break;
				case 2:
					this.programa3 = new Tool[Integer.parseInt(st.nextToken())];
					break;
				case 3:
					this.programa4 = new Tool[Integer.parseInt(st.nextToken())];
					break;
				}
			} else if (contador == 4) {
				this.espacios = Integer.parseInt(st.nextToken());
				this.cajas = (Stack<Caja>[]) new Stack[this.espacios];
				this.meta = (Stack<Caja>[]) new Stack[this.espacios];
				for (int i = 0; i < this.espacios; i++) {
					this.cajas[i] = new Stack<Caja>();
					this.meta[i] = new Stack<Caja>();
				}

				for (int i = 0; i < this.espacios; i++) {
					String cajas = st.nextToken();
					for (int j = 0; j < cajas.length(); j++) {
						if (cajas.charAt(j) == '0') {
							continue;
						} else {
							this.cajas[i].add(new Caja(Character.getNumericValue(cajas.charAt(j))));
						}
					}
				}
				for (int i = 0; i < this.espacios; i++) {
					String goal = st.nextToken();
					for (int j = 0; j < goal.length(); j++) {
						if (goal.charAt(j) == '0') {
							continue;
						} else {
							this.meta[i].add(new Caja(Character.getNumericValue(goal.charAt(j))));
						}
					}
				}
			} else {
				for (int i = 0; i < 13; i++) {
					String elemento = st.nextToken();
					if (elemento.equals("1")) {
						switch (i) {
						case 0:
							this.toolbox[i] = new Tool("derecha");
							break;
						case 1:
							this.toolbox[i] = new Tool("izquierda");
							break;
						case 2:
							this.toolbox[i] = new Tool("abajo");
							break;
						case 3:
							this.toolbox[i] = new Tool("programa1");
							break;
						case 4:
							this.toolbox[i] = new Tool("programa2");
							break;
						case 5:
							this.toolbox[i] = new Tool("programa3");
							break;
						case 6:
							this.toolbox[i] = new Tool("programa4");
							break;
						case 7:
							this.toolbox[i] = new Tool("ifRed");
							break;
						case 8:
							this.toolbox[i] = new Tool("ifYell");
							break;
						case 9:
							this.toolbox[i] = new Tool("ifGreen");
							break;
						case 10:
							this.toolbox[i] = new Tool("ifBlue");
							break;
						case 11:
							this.toolbox[i] = new Tool("ifAll");
							break;
						case 12:
							this.toolbox[i] = new Tool("ifNone");
							break;
						default:
							System.out.println("ERROR SUGOIII");
							System.exit(0);
						}
					}
				}
			}

			if (this.espacios == 2 || this.espacios == 3 || this.espacios == 4) {
				garra = new Garra(239);
			} else if (this.espacios == 5) {
				garra = new Garra(149);
			} else if (this.espacios == 6 || this.espacios == 7) {
				garra = new Garra(59);
			}

			// cajitas

			for (int i = 0; i < this.espacios; i++) {
				for (int j = 0; j < this.cajas[i].size(); j++) {
					if (this.espacios == 2 || this.espacios == 3 || this.espacios == 4) {
						this.cajas[i].get(j).setX(254 + i * 90);
					} else if (this.espacios == 5) {
						this.cajas[i].get(j).setX(164 + i * 90);
					} else if (this.espacios == 6 || this.espacios == 7) {
						this.cajas[i].get(j).setX(74 + i * 90);
					}

					this.cajas[i].get(j).setY(333 - j * 50);
				}
			}

			contador++;
		}
	}

	public void nextLevel() {
		this.posGarra = 0;
		this.hasCaja = false;
		this.caja = null;
		this.prevPosGarra = 0;
		this.play = false;
		this.nivel = this.niveles.get(this.nodeCurrent.getRight().getValue());
		this.nodePrevious = this.nodeCurrent;
		this.nodeCurrent = this.nodeCurrent.getRight();
		this.paintLevel();
		this.repaint();
	}

	public void changeLevel() {
		this.posGarra = 0;
		this.hasCaja = false;
		this.caja = null;
		this.prevPosGarra = 0;
		this.nivel = this.niveles.get(this.nodeCurrent.getLeft().getValue());
		this.nodeCurrent = this.nodeCurrent.getLeft();
		this.paintLevel();
		this.repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getX() < 655 && e.getX() > 540 && e.getY() < 735 && e.getY() > 680) {
			boolean flag = false;
			for (int i = 0; i < this.programa1.length; i++) {
				if (this.programa1[i] == null) {
					continue;
				} else {
					flag = true;
					break;
				}
			}
			if (flag) {
				if (this.play) {
					this.tryAgain();
				}
				this.play = !this.play;
				this.paintImmediately(0, 0, 1300, 1300);
			}

			// CLEAR
		} else if (e.getX() > 1009 && e.getX() < 1074 && e.getY() > 620 && e.getY() < 665 && !this.play) {
			this.programa1 = new Tool[this.programa1.length];
			this.programa2 = new Tool[this.programa2.length];
			this.programa3 = new Tool[this.programa3.length];
			this.programa4 = new Tool[this.programa4.length];
		}

		// SKIP
		else if (e.getX() > 1096 && e.getX() < 1161 && e.getY() > 620 && e.getY() < 665 && !this.play) {
			this.changeLevel();
		}
	}

	public void actionComprobation(Tool herramienta) {
		if (herramienta.getAccion().equals("der")) {
			this.flagDer = true;
		} else if (herramienta.getAccion().equals("izq")) {
			this.flagIzq = true;
		} else if (herramienta.getAccion().equals("down")) {
			this.flagDown = true;
		} else if (herramienta.getAccion().equals("prog1")) {
			this.flagProg1 = true;
		} else if (herramienta.getAccion().equals("prog2")) {
			this.flagProg2 = true;
		} else if (herramienta.getAccion().equals("prog3")) {
			this.flagProg3 = true;
		} else if (herramienta.getAccion().equals("prog4")) {
			this.flagProg4 = true;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getX() > 750 && e.getX() < 815 && e.getY() > 450 && e.getY() < 495 && this.toolbox[0] != null
				&& !this.play) {
			this.flagDer = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 835 && e.getX() < 905 && e.getY() > 450 && e.getY() < 495 && this.toolbox[1] != null
				&& !this.play) {
			this.flagIzq = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 922 && e.getX() < 987 && e.getY() > 450 && e.getY() < 495 && this.toolbox[2] != null
				&& !this.play) {
			this.flagDown = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		}
		// NUEVA LINEA
		else if (e.getX() > 750 && e.getX() < 815 && e.getY() > 535 && e.getY() < 580 && this.toolbox[3] != null
				&& !this.play) {
			this.flagProg1 = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 835 && e.getX() < 905 && e.getY() > 535 && e.getY() < 580 && this.toolbox[4] != null
				&& !this.play) {
			this.flagProg2 = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 922 && e.getX() < 987 && e.getY() > 535 && e.getY() < 580 && this.toolbox[5] != null
				&& !this.play) {
			this.flagProg3 = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 1009 && e.getX() < 1074 && e.getY() > 535 && e.getY() < 580 && this.toolbox[6] != null
				&& !this.play) {
			this.flagProg4 = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		}
		// NUEVA LINEA
		else if (e.getX() > 750 && e.getX() < 765 && e.getY() > 620 && e.getY() < 665 && this.toolbox[7] != null
				&& !this.play) {
			this.flagIfRed = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 795 && e.getX() < 810 && e.getY() > 620 && e.getY() < 665 && this.toolbox[8] != null
				&& !this.play) {
			this.flagIfBlue = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 840 && e.getX() < 855 && e.getY() > 620 && e.getY() < 665 && this.toolbox[9] != null
				&& !this.play) {
			this.flagIfGreen = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 885 && e.getX() < 900 && e.getY() > 620 && e.getY() < 665 && this.toolbox[10] != null
				&& !this.play) {
			this.flagIfYell = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 930 && e.getX() < 945 && e.getY() > 620 && e.getY() < 665 && this.toolbox[11] != null
				&& !this.play) {
			this.flagIfNone = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 975 && e.getX() < 990 && e.getY() > 620 && e.getY() < 665 && this.toolbox[12] != null
				&& !this.play) {
			this.flagIfAll = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if (e.getX() > 1009 && e.getX() < 1074 && e.getY() > 620 && e.getY() < 665 && !this.play) {
			this.programa1 = new Tool[this.programa1.length];
			this.programa2 = new Tool[this.programa2.length];
			this.programa3 = new Tool[this.programa3.length];
			this.programa4 = new Tool[this.programa4.length];
		}

		// NUEVA LINEA
		/*
		 * else if(e.getX()>1009 && e.getX()<1074 && e.getY()>620 && e.getY()<665 &&
		 * this.programa1[0]!=null && !this.play) {
		 * this.actionComprobation(this.programa1[0]); this.coorToolx = e.getX();
		 * this.coorTooly = e.getY(); this.repaint(); }
		 */

	}

	public void ifChecker(Tool[] programa, int pos, Tool herramienta, Tool ifTool) {
		// DERECHA IFS

		if (herramienta.getAccion().equals("der") && ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfDer");
		} else if (herramienta.getAccion().equals("der") && ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfDer");
		} else if (herramienta.getAccion().equals("der") && ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfDer");
		} else if (herramienta.getAccion().equals("der") && ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfDer");
		} else if (herramienta.getAccion().equals("der") && ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfDer");
		} else if (herramienta.getAccion().equals("der") && ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfDer");
		}

		// IZQUIERDA IFS
		else if (herramienta.getAccion().equals("izq") && ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfIzq");
		} else if (herramienta.getAccion().equals("izq") && ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfIzq");
		} else if (herramienta.getAccion().equals("izq") && ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfIzq");
		} else if (herramienta.getAccion().equals("izq") && ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfIzq");
		} else if (herramienta.getAccion().equals("izq") && ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfIzq");
		} else if (herramienta.getAccion().equals("izq") && ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfIzq");
		}

		// DOWN IFS
		if (herramienta.getAccion().equals("down") && ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfDown");
		} else if (herramienta.getAccion().equals("down") && ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfDown");
		} else if (herramienta.getAccion().equals("down") && ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfDown");
		} else if (herramienta.getAccion().equals("down") && ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfDown");
		} else if (herramienta.getAccion().equals("down") && ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfDown");
		} else if (herramienta.getAccion().equals("down") && ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfDown");
		}

		// PROG 1 IFS
		else if (herramienta.getAccion().equals("prog1") && ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfProg1");
		} else if (herramienta.getAccion().equals("prog1") && ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfProg1");
		} else if (herramienta.getAccion().equals("prog1") && ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfProg1");
		} else if (herramienta.getAccion().equals("prog1") && ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfProg1");
		} else if (herramienta.getAccion().equals("prog1") && ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfProg1");
		} else if (herramienta.getAccion().equals("prog1") && ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfProg1");
		}

		// PROG 2 IFS
		else if (herramienta.getAccion().equals("prog2") && ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfProg2");
		} else if (herramienta.getAccion().equals("prog2") && ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfProg2");
		} else if (herramienta.getAccion().equals("prog2") && ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfProg2");
		} else if (herramienta.getAccion().equals("prog2") && ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfProg2");
		} else if (herramienta.getAccion().equals("prog2") && ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfProg2");
		} else if (herramienta.getAccion().equals("prog2") && ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfProg2");
		}

		// PROG 3 IFS
		else if (herramienta.getAccion().equals("prog3") && ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfProg3");
		} else if (herramienta.getAccion().equals("prog3") && ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfProg3");
		} else if (herramienta.getAccion().equals("prog3") && ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfProg3");
		} else if (herramienta.getAccion().equals("prog3") && ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfProg3");
		} else if (herramienta.getAccion().equals("prog3") && ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfProg3");
		} else if (herramienta.getAccion().equals("prog3") && ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfProg3");
		}

		// PROG 4
		else if (herramienta.getAccion().equals("prog4") && ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfProg4");
		} else if (herramienta.getAccion().equals("prog4") && ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfProg4");
		} else if (herramienta.getAccion().equals("prog4") && ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfProg4");
		} else if (herramienta.getAccion().equals("prog4") && ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfProg4");
		} else if (herramienta.getAccion().equals("prog4") && ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfProg4");
		} else if (herramienta.getAccion().equals("prog4") && ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfProg4");
		}

		// CASOS ESPECIALES
		// RED
		else if ((herramienta.getAccion().equals("redIfDer") || herramienta.getAccion().equals("yellIfDer")
				|| herramienta.getAccion().equals("greenIfDer") || herramienta.getAccion().equals("blueIfDer")
				|| herramienta.getAccion().equals("allIfDer") || herramienta.getAccion().equals("noneIfDer"))
				&& ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfDer");
		} else if ((herramienta.getAccion().equals("redIfIzq") || herramienta.getAccion().equals("yellIfIzq")
				|| herramienta.getAccion().equals("greenIfIzq") || herramienta.getAccion().equals("blueIfIzq")
				|| herramienta.getAccion().equals("allIfIzq") || herramienta.getAccion().equals("noneIfIzq"))
				&& ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfIzq");
		} else if ((herramienta.getAccion().equals("redIfDown") || herramienta.getAccion().equals("yellIfDown")
				|| herramienta.getAccion().equals("greenIfDown") || herramienta.getAccion().equals("blueIfDown")
				|| herramienta.getAccion().equals("allIfDown") || herramienta.getAccion().equals("noneIfDown"))
				&& ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfDown");
		} else if ((herramienta.getAccion().equals("redIfProg1") || herramienta.getAccion().equals("yellIfProg1")
				|| herramienta.getAccion().equals("greenIfProg1") || herramienta.getAccion().equals("blueIfProg1")
				|| herramienta.getAccion().equals("allIfProg1") || herramienta.getAccion().equals("noneIfProg1"))
				&& ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfProg1");
		} else if ((herramienta.getAccion().equals("redIfProg2") || herramienta.getAccion().equals("yellIfProg2")
				|| herramienta.getAccion().equals("greenIfProg2") || herramienta.getAccion().equals("blueIfProg2")
				|| herramienta.getAccion().equals("allIfProg2") || herramienta.getAccion().equals("noneIfProg2"))
				&& ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfProg2");
		} else if ((herramienta.getAccion().equals("redIfProg3") || herramienta.getAccion().equals("yellIfProg3")
				|| herramienta.getAccion().equals("greenIfProg3") || herramienta.getAccion().equals("blueIfProg3")
				|| herramienta.getAccion().equals("allIfProg3") || herramienta.getAccion().equals("noneIfProg3"))
				&& ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfProg3");
		} else if (herramienta.getAccion().equals("redIfProg4") || herramienta.getAccion().equals("yellIfProg4")
				|| herramienta.getAccion().equals("greenIfProg4") || herramienta.getAccion().equals("blueIfProg4")
				|| herramienta.getAccion().equals("allIfProg4")
				|| herramienta.getAccion().equals("noneIfProg4") && ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("redIfProg4");
		}

		// BLUE
		else if ((herramienta.getAccion().equals("redIfDer") || herramienta.getAccion().equals("yellIfDer")
				|| herramienta.getAccion().equals("greenIfDer") || herramienta.getAccion().equals("blueIfDer")
				|| herramienta.getAccion().equals("allIfDer") || herramienta.getAccion().equals("noneIfDer"))
				&& ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfDer");
		} else if ((herramienta.getAccion().equals("redIfIzq") || herramienta.getAccion().equals("yellIfIzq")
				|| herramienta.getAccion().equals("greenIfIzq") || herramienta.getAccion().equals("blueIfIzq")
				|| herramienta.getAccion().equals("allIfIzq") || herramienta.getAccion().equals("noneIfIzq"))
				&& ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfIzq");
		} else if ((herramienta.getAccion().equals("redIfDown") || herramienta.getAccion().equals("yellIfDown")
				|| herramienta.getAccion().equals("greenIfDown") || herramienta.getAccion().equals("blueIfDown")
				|| herramienta.getAccion().equals("allIfDown") || herramienta.getAccion().equals("noneIfDown"))
				&& ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfDown");
		} else if ((herramienta.getAccion().equals("redIfProg1") || herramienta.getAccion().equals("yellIfProg1")
				|| herramienta.getAccion().equals("greenIfProg1") || herramienta.getAccion().equals("blueIfProg1")
				|| herramienta.getAccion().equals("allIfProg1") || herramienta.getAccion().equals("noneIfProg1"))
				&& ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfProg1");
		} else if ((herramienta.getAccion().equals("redIfProg2") || herramienta.getAccion().equals("yellIfProg2")
				|| herramienta.getAccion().equals("greenIfProg2") || herramienta.getAccion().equals("blueIfProg2")
				|| herramienta.getAccion().equals("allIfProg2") || herramienta.getAccion().equals("noneIfProg2"))
				&& ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfProg2");
		} else if ((herramienta.getAccion().equals("redIfProg3") || herramienta.getAccion().equals("yellIfProg3")
				|| herramienta.getAccion().equals("greenIfProg3") || herramienta.getAccion().equals("blueIfProg3")
				|| herramienta.getAccion().equals("allIfProg3") || herramienta.getAccion().equals("noneIfProg3"))
				&& ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfProg3");
		} else if (herramienta.getAccion().equals("redIfProg4") || herramienta.getAccion().equals("yellIfProg4")
				|| herramienta.getAccion().equals("greenIfProg4") || herramienta.getAccion().equals("blueIfProg4")
				|| herramienta.getAccion().equals("allIfProg4")
				|| herramienta.getAccion().equals("noneIfProg4") && ifTool.getAccion().equals("ifBlue")) {
			programa[pos] = new Tool("blueIfProg4");
		}

		// Green
		else if ((herramienta.getAccion().equals("redIfDer") || herramienta.getAccion().equals("yellIfDer")
				|| herramienta.getAccion().equals("greenIfDer") || herramienta.getAccion().equals("blueIfDer")
				|| herramienta.getAccion().equals("allIfDer") || herramienta.getAccion().equals("noneIfDer"))
				&& ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfDer");
		} else if ((herramienta.getAccion().equals("redIfIzq") || herramienta.getAccion().equals("yellIfIzq")
				|| herramienta.getAccion().equals("greenIfIzq") || herramienta.getAccion().equals("blueIfIzq")
				|| herramienta.getAccion().equals("allIfIzq") || herramienta.getAccion().equals("noneIfIzq"))
				&& ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfIzq");
		} else if ((herramienta.getAccion().equals("redIfDown") || herramienta.getAccion().equals("yellIfDown")
				|| herramienta.getAccion().equals("greenIfDown") || herramienta.getAccion().equals("blueIfDown")
				|| herramienta.getAccion().equals("allIfDown") || herramienta.getAccion().equals("noneIfDown"))
				&& ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfDown");
		} else if ((herramienta.getAccion().equals("redIfProg1") || herramienta.getAccion().equals("yellIfProg1")
				|| herramienta.getAccion().equals("greenIfProg1") || herramienta.getAccion().equals("blueIfProg1")
				|| herramienta.getAccion().equals("allIfProg1") || herramienta.getAccion().equals("noneIfProg1"))
				&& ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfProg1");
		} else if ((herramienta.getAccion().equals("redIfProg2") || herramienta.getAccion().equals("yellIfProg2")
				|| herramienta.getAccion().equals("greenIfProg2") || herramienta.getAccion().equals("blueIfProg2")
				|| herramienta.getAccion().equals("allIfProg2") || herramienta.getAccion().equals("noneIfProg2"))
				&& ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfProg2");
		} else if ((herramienta.getAccion().equals("redIfProg3") || herramienta.getAccion().equals("yellIfProg3")
				|| herramienta.getAccion().equals("greenIfProg3") || herramienta.getAccion().equals("blueIfProg3")
				|| herramienta.getAccion().equals("allIfProg3") || herramienta.getAccion().equals("noneIfProg3"))
				&& ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfProg3");
		} else if (herramienta.getAccion().equals("redIfProg4") || herramienta.getAccion().equals("yellIfProg4")
				|| herramienta.getAccion().equals("greenIfProg4") || herramienta.getAccion().equals("blueIfProg4")
				|| herramienta.getAccion().equals("allIfProg4")
				|| herramienta.getAccion().equals("noneIfProg4") && ifTool.getAccion().equals("ifGreen")) {
			programa[pos] = new Tool("greenIfProg4");
		}

		// Yellow
		else if ((herramienta.getAccion().equals("redIfDer") || herramienta.getAccion().equals("yellIfDer")
				|| herramienta.getAccion().equals("greenIfDer") || herramienta.getAccion().equals("blueIfDer")
				|| herramienta.getAccion().equals("allIfDer") || herramienta.getAccion().equals("noneIfDer"))
				&& ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfDer");
		} else if ((herramienta.getAccion().equals("redIfIzq") || herramienta.getAccion().equals("yellIfIzq")
				|| herramienta.getAccion().equals("greenIfIzq") || herramienta.getAccion().equals("blueIfIzq")
				|| herramienta.getAccion().equals("allIfIzq") || herramienta.getAccion().equals("noneIfIzq"))
				&& ifTool.getAccion().equals("ifRed")) {
			programa[pos] = new Tool("yellIfIzq");
		} else if ((herramienta.getAccion().equals("redIfDown") || herramienta.getAccion().equals("yellIfDown")
				|| herramienta.getAccion().equals("greenIfDown") || herramienta.getAccion().equals("blueIfDown")
				|| herramienta.getAccion().equals("allIfDown") || herramienta.getAccion().equals("noneIfDown"))
				&& ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfDown");
		} else if ((herramienta.getAccion().equals("redIfProg1") || herramienta.getAccion().equals("yellIfProg1")
				|| herramienta.getAccion().equals("greenIfProg1") || herramienta.getAccion().equals("blueIfProg1")
				|| herramienta.getAccion().equals("allIfProg1") || herramienta.getAccion().equals("noneIfProg1"))
				&& ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfProg1");
		} else if ((herramienta.getAccion().equals("redIfProg2") || herramienta.getAccion().equals("yellIfProg2")
				|| herramienta.getAccion().equals("greenIfProg2") || herramienta.getAccion().equals("blueIfProg2")
				|| herramienta.getAccion().equals("allIfProg2") || herramienta.getAccion().equals("noneIfProg2"))
				&& ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("redIfProg2");
		} else if ((herramienta.getAccion().equals("redIfProg3") || herramienta.getAccion().equals("yellIfProg3")
				|| herramienta.getAccion().equals("greenIfProg3") || herramienta.getAccion().equals("blueIfProg3")
				|| herramienta.getAccion().equals("allIfProg3") || herramienta.getAccion().equals("noneIfProg3"))
				&& ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfProg3");
		} else if (herramienta.getAccion().equals("redIfProg4") || herramienta.getAccion().equals("yellIfProg4")
				|| herramienta.getAccion().equals("greenIfProg4") || herramienta.getAccion().equals("blueIfProg4")
				|| herramienta.getAccion().equals("allIfProg4")
				|| herramienta.getAccion().equals("noneIfProg4") && ifTool.getAccion().equals("ifYell")) {
			programa[pos] = new Tool("yellIfProg4");
		}

		// ALL
		else if ((herramienta.getAccion().equals("redIfDer") || herramienta.getAccion().equals("yellIfDer")
				|| herramienta.getAccion().equals("greenIfDer") || herramienta.getAccion().equals("blueIfDer")
				|| herramienta.getAccion().equals("allIfDer") || herramienta.getAccion().equals("noneIfDer"))
				&& ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfDer");
		} else if ((herramienta.getAccion().equals("redIfIzq") || herramienta.getAccion().equals("yellIfIzq")
				|| herramienta.getAccion().equals("greenIfIzq") || herramienta.getAccion().equals("blueIfIzq")
				|| herramienta.getAccion().equals("allIfIzq") || herramienta.getAccion().equals("noneIfIzq"))
				&& ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfIzq");
		} else if ((herramienta.getAccion().equals("redIfDown") || herramienta.getAccion().equals("yellIfDown")
				|| herramienta.getAccion().equals("greenIfDown") || herramienta.getAccion().equals("blueIfDown")
				|| herramienta.getAccion().equals("allIfDown") || herramienta.getAccion().equals("noneIfDown"))
				&& ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfDown");
		} else if ((herramienta.getAccion().equals("redIfProg1") || herramienta.getAccion().equals("yellIfProg1")
				|| herramienta.getAccion().equals("greenIfProg1") || herramienta.getAccion().equals("blueIfProg1")
				|| herramienta.getAccion().equals("allIfProg1") || herramienta.getAccion().equals("noneIfProg1"))
				&& ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfProg1");
		} else if ((herramienta.getAccion().equals("redIfProg2") || herramienta.getAccion().equals("yellIfProg2")
				|| herramienta.getAccion().equals("greenIfProg2") || herramienta.getAccion().equals("blueIfProg2")
				|| herramienta.getAccion().equals("allIfProg2") || herramienta.getAccion().equals("noneIfProg2"))
				&& ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfProg2");
		} else if ((herramienta.getAccion().equals("redIfProg3") || herramienta.getAccion().equals("yellIfProg3")
				|| herramienta.getAccion().equals("greenIfProg3") || herramienta.getAccion().equals("blueIfProg3")
				|| herramienta.getAccion().equals("allIfProg3") || herramienta.getAccion().equals("noneIfProg3"))
				&& ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfProg3");
		} else if (herramienta.getAccion().equals("redIfProg4") || herramienta.getAccion().equals("yellIfProg4")
				|| herramienta.getAccion().equals("greenIfProg4") || herramienta.getAccion().equals("blueIfProg4")
				|| herramienta.getAccion().equals("allIfProg4")
				|| herramienta.getAccion().equals("noneIfProg4") && ifTool.getAccion().equals("ifAll")) {
			programa[pos] = new Tool("allIfProg4");
		}

		// NONE
		else if ((herramienta.getAccion().equals("redIfDer") || herramienta.getAccion().equals("yellIfDer")
				|| herramienta.getAccion().equals("greenIfDer") || herramienta.getAccion().equals("blueIfDer")
				|| herramienta.getAccion().equals("allIfDer") || herramienta.getAccion().equals("noneIfDer"))
				&& ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfDer");
		} else if ((herramienta.getAccion().equals("redIfIzq") || herramienta.getAccion().equals("yellIfIzq")
				|| herramienta.getAccion().equals("greenIfIzq") || herramienta.getAccion().equals("blueIfIzq")
				|| herramienta.getAccion().equals("allIfIzq") || herramienta.getAccion().equals("noneIfIzq"))
				&& ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfIzq");
		} else if ((herramienta.getAccion().equals("redIfDown") || herramienta.getAccion().equals("yellIfDown")
				|| herramienta.getAccion().equals("greenIfDown") || herramienta.getAccion().equals("blueIfDown")
				|| herramienta.getAccion().equals("allIfDown") || herramienta.getAccion().equals("noneIfDown"))
				&& ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfDown");
		} else if ((herramienta.getAccion().equals("redIfProg1") || herramienta.getAccion().equals("yellIfProg1")
				|| herramienta.getAccion().equals("greenIfProg1") || herramienta.getAccion().equals("blueIfProg1")
				|| herramienta.getAccion().equals("allIfProg1") || herramienta.getAccion().equals("noneIfProg1"))
				&& ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfProg1");
		} else if ((herramienta.getAccion().equals("redIfProg2") || herramienta.getAccion().equals("yellIfProg2")
				|| herramienta.getAccion().equals("greenIfProg2") || herramienta.getAccion().equals("blueIfProg2")
				|| herramienta.getAccion().equals("allIfProg2") || herramienta.getAccion().equals("noneIfProg2"))
				&& ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfProg2");
		} else if ((herramienta.getAccion().equals("redIfProg3") || herramienta.getAccion().equals("yellIfProg3")
				|| herramienta.getAccion().equals("greenIfProg3") || herramienta.getAccion().equals("blueIfProg3")
				|| herramienta.getAccion().equals("allIfProg3") || herramienta.getAccion().equals("noneIfProg3"))
				&& ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfProg3");
		} else if (herramienta.getAccion().equals("redIfProg4") || herramienta.getAccion().equals("yellIfProg4")
				|| herramienta.getAccion().equals("greenIfProg4") || herramienta.getAccion().equals("blueIfProg4")
				|| herramienta.getAccion().equals("allIfProg4")
				|| herramienta.getAccion().equals("noneIfProg4") && ifTool.getAccion().equals("ifNone")) {
			programa[pos] = new Tool("noneIfProg4");
		}

	}

	public boolean hasIf(Tool herramienta) {
		if (herramienta.getAccion().equals("ifRed")) {
			return true;
		} else if (herramienta.getAccion().equals("ifBlue")) {
			return true;
		} else if (herramienta.getAccion().equals("ifGreen")) {
			return true;
		} else if (herramienta.getAccion().equals("ifYell")) {
			return true;
		} else if (herramienta.getAccion().equals("ifNone")) {
			return true;
		} else if (herramienta.getAccion().equals("ifAll")) {
			return true;
		} else {
			return false;
		}
	}

	public void toolPut(Tool[] programa, int pos) {
		if (this.flagDer && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("der");
			} else {
				if (this.hasIf(programa[pos])) {
					Tool tmp = new Tool("der");
					this.ifChecker(programa, pos, tmp, programa[pos]);
				} else {
					programa[pos] = new Tool("der");
				}
			}
		} else if (this.flagIzq && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("izq");
			} else {
				if (this.hasIf(programa[pos])) {
					Tool tmp = new Tool("izq");
					this.ifChecker(programa, pos, tmp, programa[pos]);
				} else {
					programa[pos] = new Tool("izq");
				}
			}
		} else if (this.flagDown && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("down");
			} else {
				if (this.hasIf(programa[pos])) {
					Tool tmp = new Tool("down");
					this.ifChecker(programa, pos, tmp, programa[pos]);
				} else {
					programa[pos] = new Tool("down");
				}
			}
		}
		// PROGR
		else if (this.flagProg1 && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("prog1");
			} else {
				if (this.hasIf(programa[pos])) {
					Tool tmp = new Tool("prog1");
					this.ifChecker(programa, pos, tmp, programa[pos]);
				} else {
					programa[pos] = new Tool("prog1");
				}
			}
		} else if (this.flagProg2 && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("prog2");
			} else {
				if (this.hasIf(programa[pos])) {
					Tool tmp = new Tool("prog2");
					this.ifChecker(programa, pos, tmp, programa[pos]);
				} else {
					programa[pos] = new Tool("prog2");
				}
			}
		} else if (this.flagProg3 && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("prog3");
			} else {
				if (this.hasIf(programa[pos])) {
					Tool tmp = new Tool("prog3");
					this.ifChecker(programa, pos, tmp, programa[pos]);
				} else {
					programa[pos] = new Tool("prog3");
				}
			}
		} else if (this.flagProg4 && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("prog4");
			} else {
				if (this.hasIf(programa[pos])) {
					Tool tmp = new Tool("prog4");
					this.ifChecker(programa, pos, tmp, programa[pos]);
				} else {
					programa[pos] = new Tool("prog4");
				}
			}

			// IFS

		} else if (this.flagIfRed && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("ifRed");
			} else {
				if (this.hasIf(programa[pos])) {
					programa[pos] = new Tool("ifRed");
				}
				Tool tmp = new Tool("ifRed");
				this.ifChecker(programa, pos, programa[pos], tmp);
			}
		} else if (this.flagIfBlue && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("ifBlue");
			} else {
				if (this.hasIf(programa[pos])) {
					programa[pos] = new Tool("ifBlue");
				}
				Tool tmp = new Tool("ifBlue");
				this.ifChecker(programa, pos, programa[pos], tmp);
			}
		} else if (this.flagIfGreen && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("ifGreen");
			} else {
				if (this.hasIf(programa[pos])) {
					programa[pos] = new Tool("ifGreen");
				}
				Tool tmp = new Tool("ifGreen");
				this.ifChecker(programa, pos, programa[pos], tmp);
			}
		} else if (this.flagIfYell && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("ifYell");
			} else {
				if (this.hasIf(programa[pos])) {
					programa[pos] = new Tool("ifYell");
				}
				Tool tmp = new Tool("ifYell");
				this.ifChecker(programa, pos, programa[pos], tmp);
			}
		} else if (this.flagIfNone && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("ifNone");
			} else {
				if (this.hasIf(programa[pos])) {
					programa[pos] = new Tool("ifNone");
				}
				Tool tmp = new Tool("ifNone");
				this.ifChecker(programa, pos, programa[pos], tmp);
			}
		} else if (this.flagIfAll && pos < programa.length) {
			if (programa[pos] == null) {
				programa[pos] = new Tool("ifAll");
			} else {
				if (this.hasIf(programa[pos])) {
					programa[pos] = new Tool("ifAll");
				}
				Tool tmp = new Tool("ifAll");
				this.ifChecker(programa, pos, programa[pos], tmp);
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getX() > 121 && e.getX() < 186 && e.getY() < 475 && e.getY() > 433) {
			this.toolPut(this.programa1, 0);
		}

		else if (e.getX() > 194 && e.getX() < 259 && e.getY() < 475 && e.getY() > 433) {
			this.toolPut(this.programa1, 1);
		}

		else if (e.getX() > 262 && e.getX() < 327 && e.getY() < 475 && e.getY() > 433) {
			this.toolPut(this.programa1, 2);
		}

		else if (e.getX() > 330 && e.getX() < 395 && e.getY() < 475 && e.getY() > 433) {
			this.toolPut(this.programa1, 3);
		}

		else if (e.getX() > 398 && e.getX() < 463 && e.getY() < 475 && e.getY() > 433) {
			this.toolPut(this.programa1, 4);
		}

		else if (e.getX() > 466 && e.getX() < 531 && e.getY() < 475 && e.getY() > 433) {
			this.toolPut(this.programa1, 5);
		}

		else if (e.getX() > 534 && e.getX() < 599 && e.getY() < 475 && e.getY() > 433) {
			this.toolPut(this.programa1, 6);
		}

		else if (e.getX() > 602 && e.getX() < 667 && e.getY() < 475 && e.getY() > 433) {
			this.toolPut(this.programa1, 7);

			// PROGRAMA 2

		} else if (e.getX() > 121 && e.getX() < 186 && e.getY() < 531 && e.getY() > 489) {
			this.toolPut(this.programa2, 0);

		} else if (e.getX() > 194 && e.getX() < 259 && e.getY() < 531 && e.getY() > 489) {
			this.toolPut(this.programa2, 1);
		}

		else if (e.getX() > 262 && e.getX() < 327 && e.getY() < 531 && e.getY() > 489) {
			this.toolPut(this.programa2, 2);
		}

		else if (e.getX() > 330 && e.getX() < 395 && e.getY() < 531 && e.getY() > 489) {
			this.toolPut(this.programa2, 3);
		}

		else if (e.getX() > 398 && e.getX() < 463 && e.getY() < 531 && e.getY() > 489) {
			this.toolPut(this.programa2, 4);
		}

		else if (e.getX() > 466 && e.getX() < 531 && e.getY() < 531 && e.getY() > 489) {
			this.toolPut(this.programa2, 5);
		}

		else if (e.getX() > 534 && e.getX() < 599 && e.getY() < 531 && e.getY() > 489) {
			this.toolPut(this.programa2, 6);
		}

		else if (e.getX() > 602 && e.getX() < 667 && e.getY() < 531 && e.getY() > 489) {
			this.toolPut(this.programa2, 7);

			// PROGRAMA 3

		}

		else if (e.getX() > 121 && e.getX() < 186 && e.getY() < 587 && e.getY() > 545) {
			this.toolPut(this.programa3, 0);

		}

		else if (e.getX() > 194 && e.getX() < 259 && e.getY() < 587 && e.getY() > 545) {
			this.toolPut(this.programa3, 1);

		}

		else if (e.getX() > 262 && e.getX() < 327 && e.getY() < 587 && e.getY() > 545) {
			this.toolPut(this.programa3, 2);
		}

		else if (e.getX() > 330 && e.getX() < 395 && e.getY() < 587 && e.getY() > 545) {
			this.toolPut(this.programa3, 3);
		}

		else if (e.getX() > 398 && e.getX() < 463 && e.getY() < 587 && e.getY() > 545) {
			this.toolPut(this.programa3, 4);
		}

		else if (e.getX() > 466 && e.getX() < 531 && e.getY() < 587 && e.getY() > 545) {
			this.toolPut(this.programa3, 5);
		}

		else if (e.getX() > 534 && e.getX() < 599 && e.getY() < 587 && e.getY() > 545) {
			this.toolPut(this.programa3, 6);
		}

		else if (e.getX() > 602 && e.getX() < 667 && e.getY() < 587 && e.getY() > 545) {
			this.toolPut(this.programa3, 7);

			// PROGRAMA 4

		} else if (e.getX() > 121 && e.getX() < 186 && e.getY() < 643 && e.getY() > 601) {
			this.toolPut(this.programa4, 0);

		}

		else if (e.getX() > 194 && e.getX() < 259 && e.getY() < 643 && e.getY() > 601) {
			this.toolPut(this.programa4, 1);
		}

		else if (e.getX() > 262 && e.getX() < 327 && e.getY() < 643 && e.getY() > 601) {
			this.toolPut(this.programa4, 2);
		}

		else if (e.getX() > 330 && e.getX() < 395 && e.getY() < 643 && e.getY() > 601) {
			this.toolPut(this.programa4, 3);
		}

		else if (e.getX() > 398 && e.getX() < 463 && e.getY() < 643 && e.getY() > 601) {
			this.toolPut(this.programa4, 4);
		}

		else if (e.getX() > 466 && e.getX() < 531 && e.getY() < 643 && e.getY() > 601) {
			this.toolPut(this.programa4, 5);
		}

		else if (e.getX() > 534 && e.getX() < 599 && e.getY() < 643 && e.getY() > 601) {
			this.toolPut(this.programa4, 6);
		}

		else if (e.getX() > 602 && e.getX() < 667 && e.getY() < 643 && e.getY() > 601) {
			this.toolPut(this.programa4, 7);
		}

		this.flagDer = false;
		this.flagIzq = false;
		this.flagDown = false;
		this.flagProg1 = false;
		this.flagProg2 = false;
		this.flagProg3 = false;
		this.flagProg4 = false;
		this.flagIfAll = false;
		this.flagIfNone = false;
		this.flagIfRed = false;
		this.flagIfGreen = false;
		this.flagIfYell = false;
		this.flagIfBlue = false;
		this.repaint();
	}

	public boolean equalsStacks() {
		for (int i = 0; i < this.cajas.length; i++) {
			Stack<Caja> cajasCopia = (Stack<Caja>) this.cajas[i].clone();
			Stack<Caja> metaCopia = (Stack<Caja>) this.meta[i].clone();
			while (!cajasCopia.isEmpty()) {
				if (!metaCopia.isEmpty()) {
					if (cajasCopia.pop().getColor() == (metaCopia.pop().getColor())) {
						continue;
					} else {
						return false;
					}
				} else {
					return false;
				}
			}
			if (!metaCopia.isEmpty()) {
				return false;
			}
		}
		return true;
	}

	public void tryAgain() {
		this.gameOver = false;
		this.hasCaja = false;
		this.posGarra = 0;
		this.prevPosGarra = 0;
		this.caja = null;
		this.win = false;
		this.paintLevelAgain();
		this.repaint();
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public int ToolIfChecker(Tool herramienta) {
		if (herramienta.getAccion().equals("redIfDer") || herramienta.getAccion().equals("redIfIzq")
				|| herramienta.getAccion().equals("redIfDown") || herramienta.getAccion().equals("redIfProg1")
				|| herramienta.getAccion().equals("redIfProg2") || herramienta.getAccion().equals("redIfProg3")
				|| herramienta.getAccion().equals("redIfProg4")) {
			return 0;
		} else if (herramienta.getAccion().equals("yellIfDer") || herramienta.getAccion().equals("yellIfIzq")
				|| herramienta.getAccion().equals("yellIfDown") || herramienta.getAccion().equals("yellIfProg1")
				|| herramienta.getAccion().equals("yellIfProg2") || herramienta.getAccion().equals("yellIfProg3")
				|| herramienta.getAccion().equals("yellIfProg4")) {
			return 1;
		} else if (herramienta.getAccion().equals("greenIfDer") || herramienta.getAccion().equals("greenIfIzq")
				|| herramienta.getAccion().equals("greenIfDown") || herramienta.getAccion().equals("greenIfProg1")
				|| herramienta.getAccion().equals("greenIfProg2") || herramienta.getAccion().equals("greenIfProg3")
				|| herramienta.getAccion().equals("greenIfProg4")) {
			return 2;
		} else if (herramienta.getAccion().equals("blueIfDer") || herramienta.getAccion().equals("blueIfIzq")
				|| herramienta.getAccion().equals("blueIfDown") || herramienta.getAccion().equals("blueIfProg1")
				|| herramienta.getAccion().equals("blueIfProg2") || herramienta.getAccion().equals("blueIfProg3")
				|| herramienta.getAccion().equals("blueIfProg4")) {
			return 3;
		} else if (herramienta.getAccion().equals("noneIfDer") || herramienta.getAccion().equals("noneIfIzq")
				|| herramienta.getAccion().equals("noneIfDown") || herramienta.getAccion().equals("noneIfProg1")
				|| herramienta.getAccion().equals("noneIfProg2") || herramienta.getAccion().equals("noneIfProg3")
				|| herramienta.getAccion().equals("noneIfProg4")) {
			return 4;
		} else if (herramienta.getAccion().equals("allIfDer") || herramienta.getAccion().equals("allIfIzq")
				|| herramienta.getAccion().equals("allIfDown") || herramienta.getAccion().equals("allIfProg1")
				|| herramienta.getAccion().equals("allIfProg2") || herramienta.getAccion().equals("allIfProg3")
				|| herramienta.getAccion().equals("allIfProg4")) {
			return 5;
		} else {
			return -1;
		}
	}

	public int WhichToolIf(Tool herramienta) {
		if (herramienta.getAccion().equals("redIfDer") || herramienta.getAccion().equals("yellIfDer")
				|| herramienta.getAccion().equals("greenIfDer") || herramienta.getAccion().equals("blueIfDer")
				|| herramienta.getAccion().equals("allIfDer") || herramienta.getAccion().equals("noneIfDer")) {
			return 0;
		} else if (herramienta.getAccion().equals("redIfIzq") || herramienta.getAccion().equals("yellIfIzq")
				|| herramienta.getAccion().equals("greenIfIzq") || herramienta.getAccion().equals("blueIfIzq")
				|| herramienta.getAccion().equals("allIfIzq") || herramienta.getAccion().equals("noneIfIzq")) {
			return 1;
		} else if (herramienta.getAccion().equals("redIfDown") || herramienta.getAccion().equals("yellIfDown")
				|| herramienta.getAccion().equals("greenIfDown") || herramienta.getAccion().equals("blueIfDown")
				|| herramienta.getAccion().equals("allIfDown") || herramienta.getAccion().equals("noneIfDown")) {
			return 2;
		} else if (herramienta.getAccion().equals("redIfProg1") || herramienta.getAccion().equals("yellIfProg1")
				|| herramienta.getAccion().equals("greenIfProg1") || herramienta.getAccion().equals("blueIfProg1")
				|| herramienta.getAccion().equals("allIfProg1") || herramienta.getAccion().equals("noneIfProg1")) {
			return 3;
		} else if (herramienta.getAccion().equals("redIfProg2") || herramienta.getAccion().equals("yellIfProg2")
				|| herramienta.getAccion().equals("greenIfProg2") || herramienta.getAccion().equals("blueIfProg2")
				|| herramienta.getAccion().equals("allIfProg2") || herramienta.getAccion().equals("noneIfProg2")) {
			return 4;
		} else if (herramienta.getAccion().equals("redIfProg3") || herramienta.getAccion().equals("yellIfProg3")
				|| herramienta.getAccion().equals("greenIfProg3") || herramienta.getAccion().equals("blueIfProg3")
				|| herramienta.getAccion().equals("allIfProg3") || herramienta.getAccion().equals("noneIfProg3")) {
			return 5;
		} else if (herramienta.getAccion().equals("redIfProg4") || herramienta.getAccion().equals("yellIfProg4")
				|| herramienta.getAccion().equals("greenIfProg4") || herramienta.getAccion().equals("blueIfProg4")
				|| herramienta.getAccion().equals("allIfProg4") || herramienta.getAccion().equals("noneIfProg4")) {
			return 6;
		} else {
			return -1;
		}
	}

	public boolean ifAccionChecker(Tool herramienta) {
		if (this.ToolIfChecker(herramienta) == 0) {
			if (!this.cajas[posGarra].isEmpty()) {
				if (this.cajas[posGarra].peek().getColor() == 1) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (this.ToolIfChecker(herramienta) == 1) {
			if (!this.cajas[posGarra].isEmpty()) {
				if (this.cajas[posGarra].peek().getColor() == 2) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (this.ToolIfChecker(herramienta) == 2) {
			if (!this.cajas[posGarra].isEmpty()) {
				if (this.cajas[posGarra].peek().getColor() == 3) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (this.ToolIfChecker(herramienta) == 3) {
			if (!this.cajas[posGarra].isEmpty()) {
				if (this.cajas[posGarra].peek().getColor() == 4) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		} else if (this.ToolIfChecker(herramienta) == 4) {
			if (!this.cajas[posGarra].isEmpty()) {
				return false;
			} else {
				return true;
			}
		} else if (this.ToolIfChecker(herramienta) == 5) {
			System.out.println("HOLA");
			if (!this.cajas[posGarra].isEmpty()) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	// ANIMACIONES WUUU
	public void derecha() {
		int n = 0;
		while (n <= 90) {
			n++;
			if (hasCaja) {
				this.caja.setX(this.caja.getX() + 1);
			}
			this.garra.setPosX(this.garra.getPosX() + 1);
			this.paintImmediately(0, 0, 1300, 1300);
		}
	}

	public void izquierda() {
		int n = 0;
		while (n <= 90) {
			n++;
			if (hasCaja) {
				this.caja.setX(this.caja.getX() - 1);
			}
			this.garra.setPosX(this.garra.getPosX() - 1);
			this.paintImmediately(0, 0, 1300, 1300);
		}
	}

	public void bajar() {
		// System.out.println(this.cajas[posGarra].size());
		int n = 0;
		int cantidad = 0;
		if (cajas[posGarra].size() <= 1 && !hasCaja) {
			cantidad = 1;
		} else if (!hasCaja) {
			cantidad = cajas[posGarra].size();
		} else {
			cantidad = cajas[posGarra].size() + 1;
		}
		while (n <= 315 - (cantidad * 50) && this.play) {

			if (hasCaja) {
				caja.setY(this.garra.getPosY() + 18);
			}

			n++;
			this.garra.setPosY(this.garra.getPosY() + 1);
			this.paintImmediately(0, 0, 1300, 1300);
		}
	}

	public void subir() {
		if (this.garra.isOpen() && !this.cajas[prevPosGarra].isEmpty()) {
			this.cajas[prevPosGarra].pop();
			this.abrir();
		}

		int n = this.garra.getPosY();
		while (n >= 47 && this.play) {
			if (n == 47) {
				garra.setArriba(true);
			}

			if (hasCaja) {
				caja.setY(this.garra.getPosY() + 18);
			}

			n--;
			this.garra.setPosY(this.garra.getPosY() - 1);
			this.paintImmediately(0, 0, 1300, 1300);
		}
	}

	public void cerrar() {
		int a = this.garra.getDerX();
		while (a <= this.garra.getPosX() - 1 && this.play) {
			a++;
			this.garra.setDerX(this.garra.getDerX() + 1);
			this.garra.setIzqX(this.garra.getIzqX() - 1);
			this.paintImmediately(0, 0, 1300, 1300);
		}
	}

	public void abrir() {
		int a = 0;
		while (a <= 7 && this.play) {
			a++;
			this.garra.setDerX(this.garra.getDerX() - 1);
			this.garra.setIzqX(this.garra.getIzqX() + 1);
			this.paintImmediately(0, 0, 1300, 1300);
		}
	}

	// ACTION CHECK :)
	public void accionCheck(Tool[] programa) {
		for (int i = 0; i < programa.length; i++) {
			try {
				Thread.sleep(1000);
				this.paintImmediately(0, 0, 1300, 1300);
			} catch (InterruptedException e) {
				System.out.println("TERRIBLE");
			}
			this.numAcciones++;
			if (this.numAcciones > this.maxAcciones) {
				break;
			}
			if (programa[i] == null) {
				continue;
			}
			if (programa[i].getAccion() == "der"
					|| (this.ifAccionChecker(programa[i]) && this.WhichToolIf(programa[i]) == 0)) {
				if (posGarra < this.espacios - 1) {
					this.prevPosGarra = this.posGarra;
					this.posGarra++;
					this.derecha();

				} else {
					this.gameOver = true;
				}
			} else if (programa[i].getAccion() == "izq"
					|| (this.ifAccionChecker(programa[i]) && this.WhichToolIf(programa[i]) == 1)) {
				if (posGarra > 0) {
					this.prevPosGarra = this.posGarra;
					this.posGarra--;
					this.izquierda();

				} else {
					this.gameOver = true;
				}
			} else if (programa[i].getAccion() == "down"
					|| (this.ifAccionChecker(programa[i]) && this.WhichToolIf(programa[i]) == 2)) {

				// System.out.println(cajas[posGarra].size());
				this.bajar();
				this.cerrar();

				if (!this.cajas[posGarra].isEmpty() && !hasCaja) {
					this.garra.setOpen(false);
					hasCaja = true;
					if (!this.cajas[posGarra].isEmpty()) {
						this.caja = this.cajas[posGarra].peek();
					}

				} else if (this.cajas[posGarra].isEmpty() && !hasCaja) {
					this.garra.setOpen(true);
				} else if (hasCaja) {
					this.garra.setOpen(true);
					if (this.cajas[posGarra].size() == this.maxCajas) {
						this.gameOver = true;
					} else {
						this.cajas[posGarra].add(caja);
						this.hasCaja = false;
					}
				}

				this.subir();

			} else if (programa[i].getAccion() == "prog1"
					|| (this.ifAccionChecker(programa[i]) && this.WhichToolIf(programa[i]) == 3)) {
				if (programa.equals(this.programa1)) {
					i = -1;
				} else {
					i = programa.length;
					this.accionCheck(this.programa1);
				}
			} else if (programa[i].getAccion() == "prog2"
					|| (this.ifAccionChecker(programa[i]) && this.WhichToolIf(programa[i]) == 4)) {
				if (programa.equals(this.programa2)) {
					i = -1;
				} else {
					i = programa.length;
					this.accionCheck(this.programa2);
				}
			} else if (programa[i].getAccion() == "prog3"
					|| (this.ifAccionChecker(programa[i]) && this.WhichToolIf(programa[i]) == 5)) {
				if (programa.equals(this.programa3)) {
					i = -1;
				} else {
					i = programa.length;
					this.accionCheck(this.programa3);
				}

			} else if (programa[i].getAccion() == "prog4"
					|| (this.ifAccionChecker(programa[i]) && this.WhichToolIf(programa[i]) == 6)) {
				if (programa.equals(this.programa4)) {
					i = -1;
				} else {
					i = programa.length;
					this.accionCheck(this.programa4);
				}
			}

			if (this.equalsStacks()) {
				win = true;
			}

			if (this.win) {
				try {
					this.paintImmediately(0, 0, 1300, 1300);
					Thread.sleep(500);
					this.nextLevel();
					System.out.println("HOLA");
				} catch (InterruptedException e) {
					System.out.println("ERROR");
				}
				this.play = false;
				this.win = false;
				break;
			}
		}
	}

	@Override
	public void run() {
		while (true) {
			// entra si hay que ejecutar
			if (this.play) {
				this.hasCaja = false;
				this.caja = null;
				this.win = false;
				try {
					Thread.sleep(40);
					this.accionCheck(this.programa1);
					if (!win) {
						this.gameOver = true;
						this.play = false;
						this.tryAgain();
					} else {
						this.changeLevel();
					}
				} catch (InterruptedException ex) {
					System.out.println("Terrible");
				}
				this.play = false;
				this.paintImmediately(0, 0, 1300, 1300);

				// si no hay que ejecutar duerme 50 ms y vuelve a checar
			} else {
				try {
					Thread.sleep(50);
				} catch (InterruptedException ex) {
					System.out.println("Terrible");
				}
				while (this.play) {
					this.hasCaja = false;
					this.caja = null;
					this.win = false;
					try {
						Thread.sleep(40);
						this.accionCheck(this.programa1);
						if (!win) {
							this.gameOver = true;
							this.play = false;
							// this.repaint();
							// this.tryAgain();
							this.tryAgain();
						} else {
							this.changeLevel();
						}
					} catch (InterruptedException e) {
						System.out.println("ERROR");
					}
				}

			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.coorToolx = e.getX();
		this.coorTooly = e.getY();
		this.repaint();

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
