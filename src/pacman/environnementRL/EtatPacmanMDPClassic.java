package pacman.environnementRL;

import java.util.ArrayList;
import java.util.Arrays;

import pacman.elements.MazePacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import environnement.Etat;

/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning
 * tabulaire
 * 
 */
public class EtatPacmanMDPClassic implements Etat, Cloneable {
	private static final int EMPTY = 1;
	private static final int GHOST = 2;
	private static final int FOOD = 3;
	private static final int CAPSULE = 4;
	private static final int WALL = 5;

	private String hash;
	private int hash_code;

	public EtatPacmanMDPClassic(StateGamePacman _stategamepacman) {
		StateAgentPacman pacman = _stategamepacman.getPacmanState(0);
		MazePacman maze = _stategamepacman.getMaze();
		int x = pacman.getX();
		int y = pacman.getY();
		


		ArrayList<Integer> states = new ArrayList<Integer>();
		for (int i = 0; i < 12; i++) {
			int tx = 0;
			int ty = 0;
			int beforeSize = states.size();
			while (true) {
				switch (i) {
				case 0:
					tx += 1;
					break;
				case 1:
					ty += 1;
					break;
				case 2:
					tx -= 1;
					break;
				case 3:
					ty -= 1;
					break;
				case 4:
					tx += 1;
					ty += 1;
					break;
				case 5:
					ty += 1;
					tx -= 1;
					break;
				case 6:
					tx -= 1;
					ty -= 1;
					break;
				case 7:
					ty -= 1;
					tx += 1;
					break;
				case 8:
					tx += 1;
					break;
				case 9:
					ty += 1;
					break;
				case 10:
					tx -= 1;
					break;
				case 11:
					ty -= 1;
					break;
				}
				boolean capsule = maze.isCapsule(x + tx, y + ty);
				boolean food = maze.isFood(x + tx, y + ty);
				boolean wall = maze.isWall(x + tx, y + ty);
				boolean ghost = _stategamepacman.isGhost(x + tx, y + ty);
				if (ghost) {
					// hash += EtatPacmanMDPClassic.GHOST * Math.pow(10, i);
					states.add(EtatPacmanMDPClassic.GHOST);
					break;

				} else if (food) {
					// hash += EtatPacmanMDPClassic.FOOD * Math.pow(10, i);
					states.add(EtatPacmanMDPClassic.FOOD);
					break;
				} else if (capsule) {
					// hash += EtatPacmanMDPClassic.CAPSULE * Math.pow(10, i);
					states.add(EtatPacmanMDPClassic.CAPSULE);
					break;
				} else if (wall) {
					// hash += EtatPacmanMDPClassic.WALL * Math.pow(10, i);
					states.add(EtatPacmanMDPClassic.WALL);
					break;
				}
				if (x + tx >= maze.getSizeX() || x + tx < 0 || y + ty >= maze.getSizeY() || y + ty < 0) {
					break;
				}
				if (i < 8)
					break;
			}
			if (states.size() == beforeSize) {
				// hash += EtatPacmanMDPClassic.EMPTY * Math.pow(10, i);
				states.add(EtatPacmanMDPClassic.EMPTY);
			}
		}
		this.hash = "";
		this.hash_code = 0;
		int j = 0;
		for (int i : states) {
			this.hash += Integer.toString(i);
			this.hash_code += (int) (i * Math.pow(10, j));
			j+=1;
		}

		//System.out.println("Hash : " + hash);

	}
	
	public int getDimensions() {
		//5^12 = 244 140 625 <- matrice trop grande
		return (int) Math.pow(5, this.hash.length());
	}
	
	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		//System.out.println("Hash ->" + this.hash_code);
		return this.hash.hashCode();
	}

	@Override
	public String toString() {
		return this.hash;
	}

	public Object clone() {
		EtatPacmanMDPClassic clone = null;
		try {
			// On recupere l'instance a renvoyer par l'appel de la
			// methode super.clone()
			clone = (EtatPacmanMDPClassic) super.clone();
			clone.hash = this.hash;
		} catch (CloneNotSupportedException cnse) {
			// Ne devrait jamais arriver car nous implementons
			// l'interface Cloneable
			cnse.printStackTrace(System.err);
		}

		// on renvoie le clone
		return clone;
	}

	@Override
	public boolean equals(Object obj) {
		EtatPacmanMDPClassic etat_obj = (EtatPacmanMDPClassic) obj;
		return this.hash.compareTo(etat_obj.hash) == 0;
	}

}
