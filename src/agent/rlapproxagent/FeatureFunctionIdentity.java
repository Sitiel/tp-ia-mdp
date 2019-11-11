package agent.rlapproxagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environnement.Action;
import environnement.Action2D;
import environnement.Etat;
import javafx.util.Pair;
/**
 * Vecteur de fonctions caracteristiques phi_i(s,a): autant de fonctions caracteristiques que de paire (s,a),
 * <li> pour chaque paire (s,a), un seul phi_i qui vaut 1  (vecteur avec un seul 1 et des 0 sinon).
 * <li> pas de biais ici 
 * 
 * @author laetitiamatignon
 *
 */
public class FeatureFunctionIdentity implements FeatureFunction {
	//*** VOTRE CODE
	
	protected ArrayList<double[]> phi;
	
	public FeatureFunctionIdentity(int _nbEtat, int _nbAction){
		//*** VOTRE CODE
		
		phi = new ArrayList<double[]>();
		
		//In our case too much state so it's not doable
		if(_nbEtat + _nbAction > 10000) {
			System.out.println("Trop d'états ou d'actions.");
			return;
		}
		
		for(int i = 0 ; i < _nbEtat ; i++) {
			
			for(int j = 0 ; j < _nbAction ; j++) {
				double[] identity = new double[_nbAction * _nbEtat];
				identity[i*_nbEtat + j] = 1;
				phi.add(identity);
			}
		}
	}
	
	@Override
	public int getFeatureNb() {
		//*** VOTRE CODE
		return phi.size();
	}

	@Override
	public double[] getFeatures(Etat e,Action a){
		//*** VOTRE CODE
		//not done because will never be used I've done directly the pacman feature function
		
		return null;
	}
	

}
