package agent.rlapproxagent;

import pacman.elements.ActionPacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import pacman.environnementRL.EnvironnementPacmanMDPClassic;

import java.util.ArrayList;
import java.util.HashMap;

import environnement.Action;
import environnement.Etat;
/**
 * Vecteur de fonctions caracteristiques pour jeu de pacman: 4 fonctions phi_i(s,a)
 *  
 * @author laetitiamatignon
 *
 */
public class FeatureFunctionPacman implements FeatureFunction{
	private double[] vfeatures ;
	
	private static int NBACTIONS = 4;//5 avec NONE possible pour pacman, 4 sinon 
	//--> doit etre coherent avec EnvironnementPacmanRL::getActionsPossibles


	public FeatureFunctionPacman() {
		
	}

	@Override
	public int getFeatureNb() {
		return 3;
	}
	
	
	private int getDistanceToNearestFood(StateGamePacman state, int x, int y) {
		
		int[][] directions = new int[][]{
			  { 1,0 },
			  { 0,1 },
			  { -1,0 },
			  { 0,-1 }
		};
		int width = state.getMaze().getSizeX();
		int step = 0;
		
		HashMap<Integer, Boolean> closeMap = new HashMap<Integer, Boolean>();
		
		ArrayList<Integer[]> nextPositions = new ArrayList<Integer[]>();
		nextPositions.add(new Integer[]{x, y, 0});
		closeMap.put(width*y+x, true);
		
		
		
		while(true) {
			if(nextPositions.size() == 0) {
				return -1;
			}
			Integer[] pos = nextPositions.get(0);
			nextPositions.remove(0);
			x = pos[0];
			y = pos[1];
			step = pos[2];
			
			if(state.getMaze().isFood(x, y) || state.getMaze().isCapsule(x, y)) {
				return step;
			}
			
			for (int[] d : directions) {
				int nextX = x+d[0];
				int nextY = y+d[1];
				int posIn1D = width * nextY + nextX;
				if(!closeMap.containsKey(posIn1D) && !state.getMaze().isWall(nextX, nextY) ) {
					closeMap.put(posIn1D, true);
					nextPositions.add(new Integer[]{nextX, nextY, step+1});
				}
				
			}
		}
		
	}

	@Override
	public double[] getFeatures(Etat e, Action a) {
		vfeatures = new double[3];
		StateGamePacman stategamepacman ;
		//EnvironnementPacmanMDPClassic envipacmanmdp = (EnvironnementPacmanMDPClassic) e;

		//calcule pacman resulting position a partir de Etat e
		if (e instanceof StateGamePacman){
			stategamepacman = (StateGamePacman)e;
		}
		else{
			System.out.println("erreur dans FeatureFunctionPacman::getFeatures n'est pas un StateGamePacman");
			return vfeatures;
		}
	
		StateAgentPacman pacmanstate_next= stategamepacman.movePacmanSimu(0, new ActionPacman(a.ordinal()));
		 
		//*** VOTRE CODE
		
		int nb_fantomes_can_kill = 0;
		
		for(int i = 0 ; i < stategamepacman.getNumberOfGhosts() ; i++) {
			int ghostX = stategamepacman.getGhostState(i).getX();
			int ghostY = stategamepacman.getGhostState(i).getY();
			
			if(Math.abs(ghostX - pacmanstate_next.getX()) + Math.abs(ghostY - pacmanstate_next.getY()) <= 1) {
				nb_fantomes_can_kill++;
			}
		}
		
		
		int distNewPos = getDistanceToNearestFood(stategamepacman, pacmanstate_next.getX(), pacmanstate_next.getY());
		int distOldPos = getDistanceToNearestFood(stategamepacman, stategamepacman.getPacmanState(0).getX(), stategamepacman.getPacmanState(0).getY());
		
		vfeatures[0] = 1; //Biais
		vfeatures[1] = nb_fantomes_can_kill;
		// Optimisation, plutôt que de vérifier la distance manhattan utilisation de la véritable distance pour s'y rendre et ainsi éviter que le pacman se coince
		vfeatures[2] = distNewPos/distOldPos;
		
		return vfeatures;
	}

	public void reset() {
		vfeatures = new double[3];
		
	}

}
