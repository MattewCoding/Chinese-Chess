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
	Thread gameThread;	/**
	 * Finds the ideal move that the computer can make.
	 * Uses alpha-beta pruning to speed up the search
	 * @param depth How many moves ahead we want to search
	 * @param alpha The current player's best possible score
	 * @param beta The opponent's best possible score
	 * @return How good a move is
	 */
	public int findIdealMove(int depth, int alpha, int beta) {
		if(depth == 0) {
			return searchAllCaptures(alpha, beta);
		}

		List<Piece> oneSidePieces;
		oneSidePieces = currentBoard.getAllPieces(playingBlack);
		PointVisitor searchValidMoves = new PointVisitor(currentBoard);
		int moveAmount = 0;

		for(Piece movingPiece : oneSidePieces) {
			ArrayList<Integer[]> legalMovesPiece = movingPiece.accept(searchValidMoves);
			moveAmount += legalMovesPiece.size();

			// For each piece, move it and test how good that position is
			for(Integer[] legalDest : legalMovesPiece) {

				int pieceX = movingPiece.getX(), pieceY = movingPiece.getY();
				int destX = legalDest[0], destY = legalDest[1];
				Piece capturedPiece = currentBoard.getPiece(destX, destY);
				Move testMove = new Move(movingPiece, pieceX, pieceY, destX, destY);

				currentBoard.doMove(testMove);
				int evaluation = (capturedPiece == null)? 0 : 10 * capturedPiece.getWorth() - movingPiece.getWorth();
				evaluation -= findIdealMove(depth - 1, -beta, -alpha);
				currentBoard.undoMove(testMove, capturedPiece);

				if(evaluation >= beta) {
					// Move was too good, opponent will avoid this position
					return beta;
				}

				// If eval > alpha, then we have a move that's better than all previous, so we don't care about the previous anymore
				if(evaluation >= alpha) {
					if(evaluation > alpha) {
						alpha = evaluation;
						bestMoves.clear();
					}

					boolean isIncluded = false;
					for(Move m: bestMoves) {
						if(m.isEqual(testMove)) {
							isIncluded = true;
							break;
						}
					}
					if(!isIncluded && depth == maxDepth) { // Add for first-order moves
						bestMoves.add(testMove);
					}
				}

			}
		}

		if(moveAmount == 0) { // Is checkmate or stalemate
			if(currentBoard.checkMate(playingBlack)) { // Checkmate
				return NEGATIVE_INFINITY;
			}
			return 0;
		}
		return alpha;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	


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
