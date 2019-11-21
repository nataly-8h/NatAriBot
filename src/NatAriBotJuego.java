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
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class NatAriBotJuego extends JPanel implements Runnable, KeyListener, MouseListener, MouseMotionListener {

	private Image img,
					derTrue,
					derFalse,
					downTrue,
					downFalse,
					izqTrue,
					izqFalse;

	private Tool[] toolbox,
					programa1,
					programa2,
					programa3,
					programa4;
	
	private BinaryNode nodeCurrent,
						nodePrevious;

	private Stack<Caja>[] cajas,
							meta;

	private int espacios,
				maxCajas,
				coorToolx,
				coorTooly;

	private String nivel;

	private boolean play,
					flagDer,
					flagIzq,
					flagDown;



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
		this.addMouseMotionListener(this);
		this.avl = new AVLTree();
		this.niveles = new Hashtable<Integer, String>();
		this.toolbox = new Tool[13];
		this.play = false;
		this.maxCajas = 6;
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
		this.nodeCurrent = avl.root;
		this.nodePrevious = avl.root;
		StringTokenizer st = new StringTokenizer(nivel);
		int contador = 0;
		while(st.hasMoreTokens()) {
			if(contador<4) {
				switch(contador) {
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
					for(int i =0; i<13;i++) {
					String elemento = st.nextToken();
					if(elemento.equals("1")) {
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
						case 7:
							this.toolbox[i] = new Tool("programa5");
							break;
						case 8:
							this.toolbox[i] = new Tool("programa6");
							break;
						case 9:
							this.toolbox[i] = new Tool("programa7");
							break;
						case 10:
							this.toolbox[i] = new Tool("programa8");
							break;
						case 11:
							this.toolbox[i] = new Tool("programa8");
							break;
						case 12:
							this.toolbox[i] = new Tool("programa8");
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
		this.derTrue = new ImageIcon("DerTrue.png").getImage();
		this.derFalse = new ImageIcon("DerFalsepng.png").getImage();
		this.downTrue = new ImageIcon("DownTrue.png").getImage();
		this.downFalse = new ImageIcon("DownFalse.png").getImage();
		this.izqTrue = new ImageIcon("IzqTrue.png").getImage();
		this.izqFalse = new ImageIcon("IzqFalse.png").getImage();
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
		if(this.espacios==2) {
			g.fillRect(214, 17, 17, 400);
			g.fillRect(413, 17, 17, 400);
		} else if(this.espacios==3) {
			g.fillRect(214, 17, 17, 400);
			g.fillRect(503, 17, 17, 400);
		} else if(this.espacios==4) {
			g.fillRect(214, 17, 17, 400);
			g.fillRect(593, 17, 17, 400);
		} else if(this.espacios==5) {
			g.fillRect(124, 17, 17, 400);
			g.fillRect(593, 17, 17, 400);
		} else if(this.espacios==6) {
			g.fillRect(34, 17, 17, 400);
			g.fillRect(593, 17, 17, 400);
		} else if(this.espacios==7) {
			g.fillRect(34, 17, 17, 400);
			g.fillRect(683, 17, 17, 400);
		}


		//garra
		g.setColor(Color.BLACK);
		if(this.espacios==2) {
			g.fillRect(255, 27, 40, 15);
			g.fillRect(239, 47, 76, 17);
		} else if(this.espacios==3) {
			g.fillRect(255, 27, 40, 15);
			g.fillRect(239, 47, 76, 17);
		} else if(this.espacios==4) {
			g.fillRect(255, 27, 40, 15);
			g.fillRect(239, 47, 76, 17);
		} else if(this.espacios==5) {
			g.fillRect(165, 27, 40, 15);
			g.fillRect(149, 47, 76, 17);
		} else if(this.espacios==6) {
			g.fillRect(75, 27, 40, 15);
			g.fillRect(59, 47, 76, 17);
		} else if(this.espacios==7) {
			g.fillRect(75, 27, 40, 15);
			g.fillRect(59, 47, 76, 17);
		}

		//base de cajitas
		g.setColor(Color.BLUE);
		for(int i = 0; i<this.espacios; i++) {
			if(this.espacios==2) {
				g.fillRect(239 + 90*i, 383, 76, 17);
			} else if(this.espacios==3) {
				g.fillRect(239 + 90*i, 383, 76, 17);
			} else if(this.espacios==4) {
				g.fillRect(239 + 90*i, 383, 76, 17);
			} else if(this.espacios==5) {
				g.fillRect(149 + 90*i, 383, 76, 17);
			} else if(this.espacios==6) {
				g.fillRect(59 + 90*i, 383, 76, 17);
			} else if(this.espacios==7) {
				g.fillRect(59 + 90*i, 383, 76, 17);
			}
		}


		//cajitas
		g.setColor(Color.BLACK);
		for(int i=0;i<this.espacios;i++) {
			for(int j=0;j<this.cajas[i].size();j++) {
				if(this.espacios==2) {
					g.fillRect(254+i*90, 333 - j*50, 46, 46);
				} else if(this.espacios==3) {
					g.fillRect(254+i*90, 333 - j*50, 46, 46);
				}else if(this.espacios==4) {
					g.fillRect(254+i*90, 333 - j*50, 46, 46);
				}else if(this.espacios==5) {
					g.fillRect(164+i*90, 333 - j*50, 46, 46);
				}else if(this.espacios==6) {
					g.fillRect(74+i*90, 333 - j*50, 46, 46);
				}else if(this.espacios==7) {
					g.fillRect(74+i*90, 333 - j*50, 46, 46);
				}
			}
		}

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

		//Espacios: 68 de separaci�n
		g.setColor(Color.BLACK);

		for(int i  = 0; i<this.programa1.length; i++) {
			g.fillRect(156 + 68*i, 454, 65, 42);
		}

		for(int i  = 0; i<this.programa2.length; i++) {
			g.fillRect(156 + 68*i, 510, 65, 42);
		}

		for(int i  = 0; i<this.programa3.length; i++) {
			g.fillRect(156 + 68*i, 566, 65, 42);
		}

		for(int i  = 0; i<this.programa4.length; i++) {
			g.fillRect(156 + 68*i, 622, 65, 42);
		}

		g.setColor(Color.WHITE);
		g.drawString(" P R O G R A M A   1 ", 38, 478);
		g.drawString(" P R O G R A M A   2 ", 38, 534);
		g.drawString(" P R O G R A M A   3 ", 38, 588);
		g.drawString(" P R O G R A M A   4 ", 38, 644);


		for(int i = 0; i<this.programa1.length;i++) {
			if(this.programa1[i]!=null) {
				if(this.programa1[i].getAccion()=="der") {
					g.drawImage(this.derTrue, 156+68*i, 454, 65, 42, this);
				} else if(this.programa1[i].getAccion()=="izq") {
					g.drawImage(this.izqTrue,156+68*i,454,65,42,this);
				} else if (this.programa1[i].getAccion()=="down") {
					g.drawImage(this.downTrue,156+68*i,454,65,42,this);
				}
			} else {
				continue;
			}
		}

		for(int i = 0; i<this.programa2.length;i++) {
			if(this.programa2[i]!=null) {
				if(this.programa2[i].getAccion()=="der") {
					g.drawImage(this.derTrue, 156+68*i, 510, 65, 42, this);
				} else if(this.programa2[i].getAccion()=="izq") {
					g.drawImage(this.izqTrue,156+68*i,510,65,42,this);
				} else if (this.programa2[i].getAccion()=="down") {
					g.drawImage(this.downTrue,156+68*i,510,65,42,this);
				}
			} else {
				continue;
			}
		}

		for(int i = 0; i<this.programa3.length;i++) {
			if(this.programa3[i]!=null) {
				if(this.programa3[i].getAccion()=="der") {
					g.drawImage(this.derTrue, 156+68*i, 566, 65, 42, this);
				} else if(this.programa3[i].getAccion()=="izq") {
					g.drawImage(this.izqTrue,156+68*i,566,65,42,this);
				} else if (this.programa3[i].getAccion()=="down") {
					g.drawImage(this.downTrue,156+68*i,566,65,42,this);
				}
			} else {
				continue;
			}
		}

		for(int i = 0; i<this.programa4.length;i++) {
			if(this.programa4[i]!=null) {
				if(this.programa4[i].getAccion()=="der") {
					g.drawImage(this.derTrue, 156+68*i, 622, 65, 42, this);
				} else if(this.programa4[i].getAccion()=="izq") {
					g.drawImage(this.izqTrue,156+68*i,622,65,42,this);
				} else if (this.programa4[i].getAccion()=="down") {
					g.drawImage(this.downTrue,156+68*i,622,65,42,this);
				}
			} else {
				continue;
			}
		}


		//GOAL
		g.setColor(Color.BLUE);
		g.fillRect(734, 17, 449, 400);
		g.setColor(Color.WHITE);
		for(int i = 0; i<this.espacios; i++) {
			if(this.espacios==2) {
				g.fillRect(922 + 64*i, 383, 50, 17);
			} else if(this.espacios==3) {
				g.fillRect(922 + 64*i, 383, 50, 17);
			} else if(this.espacios==4) {
				g.fillRect(922 + 64*i, 383, 50, 17);
			} else if(this.espacios==5) {
				g.fillRect(832 + 64*i, 383, 50, 17);
			} else if(this.espacios==6) {
				g.fillRect(742 + 64*i, 383, 50, 17);
			} else if(this.espacios==7) {
				g.fillRect(742 + 64*i, 383, 50, 17);
			}
		}

		g.setColor(Color.BLACK);
		for(int i=0;i<this.espacios;i++) {
			for(int j=0;j<this.meta[i].size();j++) {
				if(this.espacios==2) {
					g.fillRect(922+i*64, 333 - j*50, 46, 46);
				} else if(this.espacios==3) {
					g.fillRect(922+i*64, 333 - j*50, 46, 46);
				}else if(this.espacios==4) {
					g.fillRect(922+i*64, 333 - j*50, 46, 46);
				}else if(this.espacios==5) {
					g.fillRect(832+i*64, 333 - j*50, 46, 46);
				}else if(this.espacios==6) {
					g.fillRect(742+i*64, 333 - j*50, 46, 46);
				}else if(this.espacios==7) {
					g.fillRect(742+i*64, 333 - j*50, 46, 46);
				}
			}
		}

		//TOOLBOX
		g.setColor(new Color(19,79,158));
		g.fillRect(734, 434, 449, 249);

		//herramientas
		g.setColor(Color.WHITE);
		g.fillRect(751, 451, 65, 42);


		//CHECAR
		for(int i = 0; i<5; i++) {
			if(i==0 && this.toolbox[0]!=null) {
				g.drawImage(this.derTrue, 751+87*i, 451, 65, 42, this);
				if(this.flagDer) {
					g.drawImage(this.derTrue, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			} else if(i==0) {
				g.drawImage(this.derFalse, 751+87*i, 451, 65, 42, this);
			}else if(i==1 && this.toolbox[1]!=null){
				g.drawImage(this.izqTrue,751+87*i,451,65,42,this);
				if(this.flagIzq) {
					g.drawImage(this.izqTrue, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			} else if(i==1) {
				g.drawImage(this.izqFalse, 751+87*i, 451, 65, 42, this);
			} else if(i==2 && this.toolbox[2]!=null){
				g.drawImage(this.downTrue,751+87*i,451,65,42,this);
				if(this.flagDown) {
					g.drawImage(this.downTrue, this.coorToolx, this.coorTooly, 65, 42, this);
				}
			} else if(i==2) {
				g.drawImage(this.downFalse, 751+87*i, 451, 65, 42, this);
			}
		}

		//CHECAR
		for(int i = 0; i<5; i++) {
			g.fillRect(751 + 87*i, 537, 65, 42);
		}

		//CHECAR
		for(int i = 0; i<5; i++) {
			if(i==3) {
				g.setColor(Color.RED);
			}else if(i==4) {
				g.setColor(Color.BLUE);
			}
			g.fillRect(751 + 87*i, 623, 65, 42);
		}


		//CIRCULO DE PLAY
		g.setColor(Color.GREEN);
		g.fillOval(545, 683, 110, 110);




	}
	
	public void changeLevel() {
		this.nivel = this.niveles.get(this.nodeCurrent.getLeft().getValue());
		StringTokenizer st = new StringTokenizer(nivel);
		int contador = 0;
		while(st.hasMoreTokens()) {
			if(contador<4) {
				switch(contador) {
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
					for(int i =0; i<13;i++) {
					String elemento = st.nextToken();
					if(elemento.equals("1")) {
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
						case 7:
							this.toolbox[i] = new Tool("programa5");
							break;
						case 8:
							this.toolbox[i] = new Tool("programa6");
							break;
						case 9:
							this.toolbox[i] = new Tool("programa7");
							break;
						case 10:
							this.toolbox[i] = new Tool("programa8");
							break;
						case 11:
							this.toolbox[i] = new Tool("programa8");
							break;
						case 12:
							this.toolbox[i] = new Tool("programa8");
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
		this.repaint();
	}



	@Override
	public void mouseClicked(MouseEvent e) {
		if(e.getX()<655 && e.getX()>540 && e.getY()<735 && e.getY()>680) {
			this.play = !this.play;
			if(this.play) {
				this.run();
			}
			
		//CLEAR
		} else if(e.getX()>1009 && e.getX()<1074 && e.getY()>620 && e.getY()<665 && !this.play) {
			this.programa1 = new Tool[this.programa1.length];
			this.programa2 = new Tool[this.programa2.length];
			this.programa3 = new Tool[this.programa3.length];
			this.programa4 = new Tool[this.programa4.length];
		}
		
		//SKIP
		else if(e.getX()>1096 && e.getX()<1161 && e.getY()>620 && e.getY()<665 && !this.play) {
			this.changeLevel();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getX()>750 && e.getX()<815 && e.getY()>450 && e.getY()<495 && this.toolbox[0]!=null && !this.play) {
			this.flagDer = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if(e.getX()>835 && e.getX()<905 && e.getY()>450 && e.getY()<495 && this.toolbox[1]!=null && !this.play) {
			this.flagIzq = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if(e.getX()>922 && e.getX()<987 && e.getY()>450 && e.getY()<495 && this.toolbox[2]!=null && !this.play) {
			this.flagDown = true;
			this.coorToolx = e.getX();
			this.coorTooly = e.getY();
			this.repaint();
		} else if(e.getX()>1009 && e.getX()<1074 && e.getY()>450 && e.getY()<495 && this.toolbox[3]!=null && !this.play) {

		} else if(e.getX()>1096 && e.getX()<1161 && e.getY()>450 && e.getY()<495 && this.toolbox[4]!=null && !this.play) {

		}
		//NUEVA LINEA
		else if(e.getX()>750 && e.getX()<815 && e.getY()>535 && e.getY()<580 && this.toolbox[5]!=null && !this.play) {

		} else if(e.getX()>835 && e.getX()<905 && e.getY()>535 && e.getY()<580 && this.toolbox[6]!=null && !this.play) {

		} else if(e.getX()>922 && e.getX()<987 && e.getY()>535 && e.getY()<580 && this.toolbox[7]!=null && !this.play) {

		} else if(e.getX()>1009 && e.getX()<1074 && e.getY()>535 && e.getY()<580 && this.toolbox[8]!=null && !this.play)  {

		} else if(e.getX()>1096 && e.getX()<1161 && e.getY()>535 && e.getY()<580 && this.toolbox[9]!=null && !this.play) {

		}
		//NUEVA LINEA
		else if(e.getX()>750 && e.getX()<815 && e.getY()>620 && e.getY()<665 && this.toolbox[10]!=null && !this.play) {

		} else if(e.getX()>835 && e.getX()<905 && e.getY()>620 && e.getY()<665 && this.toolbox[11]!=null && !this.play) {

		} else if(e.getX()>922 && e.getX()<987 && e.getY()>620 && e.getY()<665 && this.toolbox[12]!=null && !this.play) {

		} else if(e.getX()>1009 && e.getX()<1074 && e.getY()>620 && e.getY()<665 && !this.play) {
			this.programa1 = new Tool[this.programa1.length];
			this.programa2 = new Tool[this.programa2.length];
			this.programa3 = new Tool[this.programa3.length];
			this.programa4 = new Tool[this.programa4.length];
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(e.getX()>121 && e.getX()<186 && e.getY()<475 && e.getY()>433) {
			if(this.flagDer && 0<this.programa1.length) {
				this.programa1[0] = new Tool("der");
			} else if(this.flagIzq && 0<this.programa1.length) {
				this.programa1[0] = new Tool("izq");
			} else if(this.flagDown && 0<this.programa1.length) {
				this.programa1[0] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>194 && e.getX()<259 && e.getY()<475 && e.getY()>433) {
			if(this.flagDer && 1<this.programa1.length) {
				this.programa1[1] = new Tool("der");
			} else if(this.flagIzq && 1<this.programa1.length) {
				this.programa1[1] = new Tool("izq");
			} else if(this.flagDown && 1<this.programa1.length) {
				this.programa1[1] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>262 && e.getX()<327 && e.getY()<475 && e.getY()>433) {
			if(this.flagDer && 2<this.programa1.length) {
				this.programa1[2] = new Tool("der");
			} else if(this.flagIzq && 2<this.programa1.length) {
				this.programa1[2] = new Tool("izq");
			} else if(this.flagDown && 2<this.programa1.length) {
				this.programa1[2] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		} else if(e.getX()>330 && e.getX()<395 && e.getY()<475 && e.getY()>433) {
			if(this.flagDer && 3<this.programa1.length) {
				this.programa1[3] = new Tool("der");
			} else if(this.flagIzq && 3<this.programa1.length) {
				this.programa1[3] = new Tool("izq");
			} else if(this.flagDown && 3<this.programa1.length) {
				this.programa1[3] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>398 && e.getX()<463 && e.getY()<475 && e.getY()>433) {
			if(this.flagDer && 4<this.programa1.length) {
				this.programa1[4] = new Tool("der");
			} else if(this.flagIzq && 4<this.programa1.length) {
				this.programa1[4] = new Tool("izq");
			} else if(this.flagDown && 4<this.programa1.length) {
				this.programa1[4] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}


		else if(e.getX()>466 && e.getX()<531 && e.getY()<475 && e.getY()>433) {
			if(this.flagDer && 5<this.programa1.length) {
				this.programa1[5] = new Tool("der");
			} else if(this.flagIzq && 5<this.programa1.length) {
				this.programa1[5] = new Tool("izq");
			} else if(this.flagDown && 5<this.programa1.length) {
				this.programa1[5] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>534 && e.getX()<599 && e.getY()<475 && e.getY()>433) {
			if(this.flagDer && 6<this.programa1.length) {
				this.programa1[6] = new Tool("der");
			} else if(this.flagIzq && 6<this.programa1.length) {
				this.programa1[6] = new Tool("izq");
			} else if(this.flagDown && 6<this.programa1.length) {
				this.programa1[6] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>602 && e.getX()<667 && e.getY()<475 && e.getY()>433) {
			if(this.flagDer && 7<this.programa1.length) {
				this.programa1[7] = new Tool("der");
			} else if(this.flagIzq && 7<this.programa1.length) {
				this.programa1[7] = new Tool("izq");
			} else if(this.flagDown && 7<this.programa1.length) {
				this.programa1[7] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/

		//PROGRAMA 2

		} else if(e.getX()>121 && e.getX()<186 && e.getY()<531 && e.getY()>489) {
			if(this.flagDer && 0<this.programa2.length) {
				this.programa2[0] = new Tool("der");
			} else if(this.flagIzq && 0<this.programa2.length) {
				this.programa2[0] = new Tool("izq");
			} else if(this.flagDown && 0<this.programa2.length) {
				this.programa2[0] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/



		}else if(e.getX()>194 && e.getX()<259 && e.getY()<531 && e.getY()>489) {
			if(this.flagDer && 1<this.programa2.length) {
				this.programa2[1] = new Tool("der");
			} else if(this.flagIzq && 1<this.programa2.length) {
				this.programa2[1] = new Tool("izq");
			} else if(this.flagDown && 1<this.programa2.length) {
				this.programa2[1] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>262 && e.getX()<327 && e.getY()<531 && e.getY()>489) {
			if(this.flagDer && 2<this.programa2.length) {
				this.programa2[2] = new Tool("der");
			} else if(this.flagIzq && 2<this.programa2.length) {
				this.programa2[2] = new Tool("izq");
			} else if(this.flagDown && 2<this.programa2.length) {
				this.programa2[2] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>330 && e.getX()<395 && e.getY()<531 && e.getY()>489) {
			if(this.flagDer && 3<this.programa2.length) {
				this.programa2[3] = new Tool("der");
			} else if(this.flagIzq && 3<this.programa2.length) {
				this.programa2[3] = new Tool("izq");
			} else if(this.flagDown && 3<this.programa2.length) {
				this.programa2[3] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>398 && e.getX()<463 && e.getY()<531 && e.getY()>489) {
			if(this.flagDer && 4<this.programa2.length) {
				this.programa2[4] = new Tool("der");
			} else if(this.flagIzq && 4<this.programa2.length) {
				this.programa2[4] = new Tool("izq");
			} else if(this.flagDown && 4<this.programa2.length) {
				this.programa2[4] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}


		else if(e.getX()>466 && e.getX()<531 && e.getY()<531 && e.getY()>489) {
			if(this.flagDer && 5<this.programa2.length) {
				this.programa2[5] = new Tool("der");
			} else if(this.flagIzq && 5<this.programa2.length) {
				this.programa2[5] = new Tool("izq");
			} else if(this.flagDown && 5<this.programa2.length) {
				this.programa2[5] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>534 && e.getX()<599 && e.getY()<531 && e.getY()>489) {
			if(this.flagDer && 6<this.programa2.length) {
				this.programa2[6] = new Tool("der");
			} else if(this.flagIzq && 6<this.programa2.length) {
				this.programa2[6] = new Tool("izq");
			} else if(this.flagDown && 6<this.programa2.length) {
				this.programa2[6] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>602 && e.getX()<667 && e.getY()<531 && e.getY()>489) {
			if(this.flagDer && 7<this.programa2.length) {
				this.programa2[7] = new Tool("der");
			} else if(this.flagIzq && 7<this.programa2.length) {
				this.programa2[7] = new Tool("izq");
			} else if(this.flagDown && 7<this.programa2.length) {
				this.programa2[7] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/


		//PROGRAMA 3

		}

		else if(e.getX()>121 && e.getX()<186 && e.getY()<587 && e.getY()>545) {
			if(this.flagDer && 0<this.programa3.length) {
				this.programa3[0] = new Tool("der");
			} else if(this.flagIzq && 0<this.programa3.length) {
				this.programa3[0] = new Tool("izq");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			}
			/*
			else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			} else if(this.flagDown && 0<this.programa3.length) {
				this.programa3[0] = new Tool("down");
			}*/

		}

		else if(e.getX()>194 && e.getX()<259 && e.getY()<587 && e.getY()>545) {
			if(this.flagDer && 1<this.programa3.length) {
				this.programa3[1] = new Tool("der");
			} else if(this.flagIzq && 1<this.programa3.length) {
				this.programa3[1] = new Tool("izq");
			} else if(this.flagDown && 1<this.programa3.length) {
				this.programa3[1] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/

		}

		else if(e.getX()>262 && e.getX()<327 && e.getY()<587 && e.getY()>545) {
			if(this.flagDer && 2<this.programa3.length) {
				this.programa3[2] = new Tool("der");
			} else if(this.flagIzq && 2<this.programa3.length) {
				this.programa3[2] = new Tool("izq");
			} else if(this.flagDown && 2<this.programa3.length) {
				this.programa3[2] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>330 && e.getX()<395 && e.getY()<587 && e.getY()>545) {
			if(this.flagDer && 3<this.programa3.length) {
				this.programa3[3] = new Tool("der");
			} else if(this.flagIzq && 3<this.programa3.length) {
				this.programa3[3] = new Tool("izq");
			} else if(this.flagDown && 3<this.programa3.length) {
				this.programa3[3] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>398 && e.getX()<463 && e.getY()<587 && e.getY()>545) {
			if(this.flagDer && 4<this.programa3.length) {
				this.programa3[4] = new Tool("der");
			} else if(this.flagIzq && 4<this.programa3.length) {
				this.programa3[4] = new Tool("izq");
			} else if(this.flagDown && 4<this.programa3.length) {
				this.programa3[4] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}


		else if(e.getX()>466 && e.getX()<531 && e.getY()<587 && e.getY()>545) {
			if(this.flagDer && 5<this.programa3.length) {
				this.programa3[5] = new Tool("der");
			} else if(this.flagIzq && 5<this.programa3.length) {
				this.programa3[5] = new Tool("izq");
			} else if(this.flagDown && 5<this.programa3.length) {
				this.programa3[5] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>534 && e.getX()<599 && e.getY()<587 && e.getY()>545) {
			if(this.flagDer && 6<this.programa3.length) {
				this.programa3[6] = new Tool("der");
			} else if(this.flagIzq && 6<this.programa3.length) {
				this.programa3[6] = new Tool("izq");
			} else if(this.flagDown && 6<this.programa3.length) {
				this.programa3[6] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>602 && e.getX()<667 && e.getY()<587 && e.getY()>545) {
			if(this.flagDer && 7<this.programa3.length) {
				this.programa3[7] = new Tool("der");
			} else if(this.flagIzq && 7<this.programa3.length) {
				this.programa3[7] = new Tool("izq");
			} else if(this.flagDown && 7<this.programa3.length) {
				this.programa3[7] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/

			//PROGRAMA 4


		} else if(e.getX()>121 && e.getX()<186 && e.getY()<643 && e.getY()>601) {
			if(this.flagDer && 0<this.programa4.length) {
				this.programa4[0] = new Tool("der");
			} else if(this.flagIzq && 0<this.programa4.length) {
				this.programa4[0] = new Tool("izq");
			} else if(this.flagDown && 0<this.programa4.length) {
				this.programa4[0] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/

		}

		else if(e.getX()>194 && e.getX()<259 && e.getY()<643 && e.getY()>601) {
			if(this.flagDer && 1<this.programa4.length) {
				this.programa4[1] = new Tool("der");
			} else if(this.flagIzq && 1<this.programa4.length) {
				this.programa4[1] = new Tool("izq");
			} else if(this.flagDown && 1<this.programa4.length) {
				this.programa4[1] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>262 && e.getX()<327 && e.getY()<643 && e.getY()>601) {
			if(this.flagDer && 2<this.programa4.length) {
				this.programa4[2] = new Tool("der");
			} else if(this.flagIzq && 2<this.programa4.length) {
				this.programa4[2] = new Tool("izq");
			} else if(this.flagDown && 2<this.programa4.length) {
				this.programa4[2] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>330 && e.getX()<395 && e.getY()<643 && e.getY()>601) {
			if(this.flagDer && 3<this.programa4.length) {
				this.programa4[3] = new Tool("der");
			} else if(this.flagIzq && 3<this.programa4.length) {
				this.programa4[3] = new Tool("izq");
			} else if(this.flagDown && 3<this.programa4.length) {
				this.programa4[3] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>398 && e.getX()<463 && e.getY()<643 && e.getY()>601) {
			if(this.flagDer && 4<this.programa4.length) {
				this.programa4[4] = new Tool("der");
			} else if(this.flagIzq && 4<this.programa4.length) {
				this.programa4[4] = new Tool("izq");
			} else if(this.flagDown && 4<this.programa4.length) {
				this.programa4[4] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}


		else if(e.getX()>466 && e.getX()<531 && e.getY()<643 && e.getY()>601) {
			if(this.flagDer && 5<this.programa4.length) {
				this.programa4[5] = new Tool("der");
			} else if(this.flagIzq && 5<this.programa4.length) {
				this.programa4[5] = new Tool("izq");
			} else if(this.flagDown && 5<this.programa4.length) {
				this.programa4[5] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>534 && e.getX()<599 && e.getY()<643 && e.getY()>601) {
			if(this.flagDer && 6<this.programa4.length) {
				this.programa4[6] = new Tool("der");
			} else if(this.flagIzq && 6<this.programa4.length) {
				this.programa4[6] = new Tool("izq");
			} else if(this.flagDown && 6<this.programa4.length) {
				this.programa4[6] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		else if(e.getX()>602 && e.getX()<667 && e.getY()<643 && e.getY()>601) {
			if(this.flagDer && 7<this.programa4.length) {
				this.programa4[7] = new Tool("der");
			} else if(this.flagIzq && 7<this.programa4.length) {
				this.programa4[7] = new Tool("izq");
			} else if(this.flagDown && 7<this.programa4.length) {
				this.programa4[7] = new Tool("down");
			}/*
			else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		} else if(this.flagDown && 0<this.programa3.length) {
			this.programa3[0] = new Tool("down");
		}*/
		}

		this.flagDer = false;
		this.flagIzq = false;
		this.flagDown = false;
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
		// TODO Auto-generated method stub
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void run() {
		while(this.play) {
			try {
			System.out.println("HOLAAAAAAAAAAAAAA");
				Thread.sleep(40);
			} catch(InterruptedException ex) {
				System.out.println("Terrible");
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
