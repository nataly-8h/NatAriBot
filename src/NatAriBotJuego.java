import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class NatAriBotJuego extends JPanel implements Runnable, KeyListener, MouseListener {

	private Queue<Tool>[] programas;
	
	private Image img;
	
	private Tool[] toolbox;
	
	private Stack<Caja>[] cajas,
							meta;
	
	private int espacios,
				sizeMax1,
				sizeMax2,
				sizeMax3,
				sizeMax4;
	
	private String nivel;
	
	
	
	private Hashtable<Integer, String> niveles;
	private AVLTree avl;

	public NatAriBotJuego() {
		super();
		this.setPreferredSize(new Dimension(1200,735));
		this.setOpaque(true);
		this.setBackground(Color.BLACK);
		this.addKeyListener(this);
		this.setFocusable(true);
		this.addMouseListener(this);
		this.avl = new AVLTree();
		this.niveles = new Hashtable<Integer, String>();
		this.toolbox = new Tool[7];
		try {
			int count = 1;
			String linea;
			BufferedReader br = new BufferedReader(new FileReader("nivel.txt"));
			while((linea = br.readLine()) != null) {
				niveles.put(count, linea);
				avl.insert(count);
				count++;
			}
			br.close();
		} catch(FileNotFoundException ex) {
			System.out.println("No se localizï¿½ el archivo " + ex);
		}catch(IOException ex) {
			System.out.println("Ocurriï¿½ un error de I/O "+ ex);
		}
		nivel = niveles.get(avl.root.getValue());
		StringTokenizer st = new StringTokenizer(nivel);
		int contador = 0;
		this.programas = (Queue<Tool>[]) new Queue[4];
		for(int i =0;i<4;i++) {
			this.programas[i] = new LinkedList<Tool>();
		}
		while(st.hasMoreTokens()) {
			if(contador<4) {
				switch(contador) {
					case 0:
						sizeMax1 = Integer.parseInt(st.nextToken());
						break;
					case 1:
						sizeMax2 = Integer.parseInt(st.nextToken());
						break;
					case 2:
						sizeMax3 = Integer.parseInt(st.nextToken());
						break;
					case 3:
						sizeMax4 = Integer.parseInt(st.nextToken());
						break;
				}
			}else if(contador==4) {
				this.espacios = Integer.parseInt(st.nextToken());
				this.cajas = (Stack<Caja>[]) new Stack[this.espacios];
				this.meta = (Stack<Caja>[]) new Stack[this.espacios];
				for(int i = 0;i<this.espacios;i++) {
					this.cajas[i] = new Stack<Caja>();
					this.meta[i] = new Stack<Caja>();
				}
				
				for(int i=0;i<this.espacios;i++) {
					String cajas = st.nextToken();
					for(int j=0;j<cajas.length();j++) {
						if(cajas.charAt(j)=='0') {
							continue;
						} else {
							this.cajas[i].add(new Caja(Character.getNumericValue(cajas.charAt(j))));
						}
					}
				}
				for(int i=0;i<this.espacios;i++) {
					String goal = st.nextToken();
					for(int j=0;j<goal.length();j++) {
						if(goal.charAt(j)=='0') {
							continue;
						} else {
							this.meta[i].add(new Caja(Character.getNumericValue(goal.charAt(j))));
						}
					}
				}
			} else {
					for(int i =0; i<7;i++) {
					String elemento = st.nextToken();
					if(elemento!="0") {
						switch(i) {
						case 0: 
							this.toolbox[i] = new Tool("derecha");
							break;
						case 1: 
							this.toolbox[i] = new Tool("abajo");
							break;
						case 2: 
							this.toolbox[i] = new Tool("izquierda");
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
						default:
							System.out.println("ERROR SUGOIII");
							System.exit(0);
						}
					}
				}
			}
			
			
			contador++;
			
		}
		//HOMBRES DEL PILAR
		//this.img= new ImageIcon("pilarMen.jpg").getImage();
		
		//Initialize Thread
		
		Thread hilo = new Thread(this);
		hilo.start();
	}

	public void paint(Graphics g) {
		super.paint(g);
		//PANEL DE JUEGO
		g.setColor(Color.GREEN);
		g.fillRect(17, 17, 700, 400);
		
		//bordes
		g.setColor(Color.YELLOW);
		g.fillRect(34, 17, 17, 400);
		g.fillRect(683, 17, 17, 400);
		
		//garra
		g.setColor(Color.BLACK);
		g.fillRect(75, 27, 40, 15);
		g.fillRect(59, 47, 76, 17);
		//base de cajitas
		g.setColor(Color.BLUE);
		for(int i = 0; i<7; i++) {
			g.fillRect(59 + 90*i, 383, 76, 17);
		}
		
		
		//cajitas
		g.setColor(Color.BLACK);
		g.fillRect(74, 333, 46, 46);
		g.fillRect(74, 283, 46, 46);
		g.fillRect(74, 233, 46, 46);
		g.fillRect(74, 183, 46, 46);
		g.fillRect(74, 133, 46, 46);
		g.fillRect(74, 83, 46, 46);
		
		//PROGRAMA
		g.setColor(Color.DARK_GRAY);
		g.fillRect(17, 434, 700, 249);
		
		//programas
		g.setColor(Color.RED);
		g.fillRect(34, 451, 666, 47);
		g.fillRect(34, 507, 666, 47);
		g.fillRect(34, 563, 666, 47);
		g.fillRect(34, 619, 666, 47);
		
		
		g.setColor(Color.YELLOW);
		g.fillRect(156, 454, 65, 42);
		
		//Espacios: 68 de separación
		g.setColor(Color.BLACK);
		
		for(int i  = 0; i<this.sizeMax1; i++) {
			g.fillRect(156 + 68*i, 454, 65, 42);
		}
		
		for(int i  = 0; i<this.sizeMax2; i++) {
			g.fillRect(156 + 68*i, 510, 65, 42);
		}
		
		for(int i  = 0; i<this.sizeMax3; i++) {
			g.fillRect(156 + 68*i, 566, 65, 42);
		}
		
		for(int i  = 0; i<this.sizeMax4; i++) {
			g.fillRect(156 + 68*i, 622, 65, 42);
		}
		
		g.setColor(Color.WHITE);
		g.drawString(" P R O G R A M A   1 ", 38, 478);
		g.drawString(" P R O G R A M A   2 ", 38, 534);
		g.drawString(" P R O G R A M A   3 ", 38, 588);
		g.drawString(" P R O G R A M A   4 ", 38, 644);
		
		//GOAL
		g.setColor(Color.BLUE);
		g.fillRect(734, 17, 449, 400);
		
		//TOOLBOX
		g.setColor(Color.ORANGE);
		g.fillRect(734, 434, 449, 249);
		
		//herramientas
		g.setColor(Color.WHITE);
		g.fillRect(751, 451, 65, 42);
		
		for(int i = 0; i<4; i++) {
			g.fillRect(751 + 117*i, 451, 65, 42);
		}
		
		for(int i = 0; i<4; i++) {
			g.fillRect(751 + 117*i, 537, 65, 42);
		}
		
		for(int i = 0; i<4; i++) {
			if(i==3) {
				g.setColor(Color.RED);
			}
			g.fillRect(751 + 117*i, 623, 65, 42);
		}
		
		
		//CIRCULO DE PLAY
		g.setColor(Color.GREEN);
		g.fillOval(545, 683, 110, 110);
		
		
		
		
	}


	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
	}

}
