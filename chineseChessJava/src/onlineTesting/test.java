package onlineTesting;

import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

public class test extends JPanel implements Runnable{

	public class ExtendedObject{
		
		public boolean getRecup_action_button() {
			return true;
		}
		
		public void update() {
			return;
		}
		
		public void dessin(Graphics2D graphique) {
			return;
		}
		
		public void draw(Graphics2D graphique) {
			return;
		}
		
		public void dessin1(Graphics2D graphique) {
			return;
		}
		
		public void update1() {
			return;
		}
	}
	
	ExtendedObject player;
	ExtendedObject nouveauPlayerCree;
	ExtendedObject terrain;
	ExtendedObject golum;
	ExtendedObject d;
	Thread gameThread;
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	


	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	public void update() {
		//player.update();
		if (d.getRecup_action_button()) { // Le joueur à appyué le bouton
			nouveauPlayerCree.update1();
		}
		player.update();
		// terrain.chargementdelamap();
		// golum.update2();
		
	}
	
	public void painComponent(Graphics graph) {
		super.paintComponent(graph);
		Graphics2D graphique = (Graphics2D) graph;
		if (d.getRecup_action_button()) {
			nouveauPlayerCree.dessin(graphique);
		}
		// System.out.println("recuperationaction"+d.getRecup_action_button());
		
		terrain.draw(graphique);
		player.dessin(graphique);
		golum.dessin1(graphique);
		// golum.dessin1(R);
		graphique.dispose();
		
	}
	
	public void commencerjeu() {
		gameThread = new Thread(this);
		gameThread.start();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
