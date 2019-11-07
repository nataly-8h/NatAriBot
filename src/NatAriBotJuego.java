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
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.swing.JPanel;


public class NatAriBotJuego extends JPanel implements Runnable, KeyListener, MouseListener {
	
	private int[] programas,
				inicial,
				meta,
				toolbox;
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
		try {
			int count = 1;
			String linea;
			BufferedReader br = new BufferedReader(new FileReader("nivel.txt"));
			br.readLine();
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
