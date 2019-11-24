import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;

public class MyPanelControles extends JPanel implements ActionListener, Runnable {
	private NatAriBotJuego pd;
	private JButton btnStop;
	
	public MyPanelControles(NatAriBotJuego pd) {
		super();
		this.pd = pd;
		this.setPreferredSize(new Dimension(800,40));
		this.btnStop=new JButton("STOP");
		this.btnStop.addActionListener(this);
		this.add(btnStop);
		Thread hilo = new Thread(this);
		hilo.start();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource().equals(this.btnStop)) {
			this.pd.tryAgain();
		}
		
	}

	@Override
	public void run() {
		if()
		
	}
}
