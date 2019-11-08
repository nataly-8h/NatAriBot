import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.ListIterator;
import java.util.Stack;
import java.util.StringTokenizer;

import javax.swing.JPanel;


public class NatAriBotJuego extends JPanel implements Runnable, KeyListener, MouseListener {

	private int[] programas;
	
	private Tool[] toolbox;
	
	private Stack<Caja>[] cajas,
							meta;
	
	private int espacios;
	
	private String nivel;
	
	
	
	private Hashtable<Integer, String> niveles;
	private AVLTree avl;

	public NatAriBotJuego() {
		super();
		this.setPreferredSize(new Dimension(1200,700));
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
			System.out.println("No se localiz� el archivo " + ex);
		}catch(IOException ex) {
			System.out.println("Ocurri� un error de I/O "+ ex);
		}
		nivel = niveles.get(avl.root.getValue());
		StringTokenizer st = new StringTokenizer(nivel);
		int contador = 0;
		this.programas = new int[4];
		while(st.hasMoreTokens()) {
			if(contador<4) {
				this.programas[contador] = Integer.parseInt(st.nextToken());
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
							this.cajas[i].add(new Caja(cajas.charAt(j)));
						}
					}
				}
				for(int i=0;i<this.espacios;i++) {
					String goal = st.nextToken();
					for(int j=0;j<goal.length();j++) {
						if(goal.charAt(j)=='0') {
							continue;
						} else {
							this.meta[i].add(new Caja(goal.charAt(j)));
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
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(Color.green);
		g.fillRect(17, 17, 700, 400);

		g.setColor(Color.DARK_GRAY);
		g.fillRect(17, 434, 700, 249);

		g.setColor(Color.BLUE);
		g.fillRect(734, 17, 449, 400);

		g.setColor(Color.ORANGE);
		g.fillRect(734, 434, 449, 249);
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
