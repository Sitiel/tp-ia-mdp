package agent.rlagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.util.Pair;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
/**
 * Renvoi 0 pour valeurs initiales de Q
 * @author laetitiamatignon
 *
 */
public class QLearningAgent extends RLAgent {
	/**
	 *  format de memorisation des Q valeurs: utiliser partout setQValeur car cette methode notifie la vue
	 */
	protected HashMap<Etat,HashMap<Action,Double>> qvaleurs;
	
	//AU CHOIX: vous pouvez utiliser une Map avec des Pair pour clés si vous préférez
	//protected HashMap<Pair<Etat,Action>,Double> qvaleurs;

	
	/**
	 * 
	 * @param alpha
	 * @param gamma
	 * @param Environnement
	 */
	public QLearningAgent(double alpha, double gamma,
			Environnement _env) {
		super(alpha, gamma,_env);
		
		qvaleurs = new HashMap<Etat,HashMap<Action,Double>>();
		this.vmin = 99999999;
		this.vmax = -99999999;
	}


	
	
	/**
	 * renvoi action(s) de plus forte(s) valeur(s) dans l'etat e
	 *  (plusieurs actions sont renvoyees si valeurs identiques)
	 *  renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)
	 */
	@Override
	public List<Action> getPolitique(Etat e) {
		// retourne action de meilleures valeurs dans e selon Q : utiliser getQValeur()
		// retourne liste vide si aucune action legale (etat terminal)
		List<Action> returnactions = new ArrayList<Action>();
		if (this.getActionsLegales(e).size() == 0){//etat  absorbant; impossible de le verifier via environnement
			System.out.println("aucune action legale");
			return new ArrayList<Action>();
		}
		
		double bestQValue = 0;
		boolean assigned = false;
		List<Action> actions = this.getActionsLegales(e);
		for(Action a : actions) {
			double qValue = this.getQValeur(e, a);
			if(qValue > bestQValue || !assigned) {
				assigned = true;
				bestQValue = qValue;
				returnactions.clear();
				returnactions.add(a);
			}
			else if(qValue == bestQValue) {
				returnactions.add(a);
			}
		}
		
		return returnactions;
		
		
	}
	
	@Override
	public double getValeur(Etat e) {
		List<Action> actions = this.getActionsLegales(e);
		double maxQ = 0;
		boolean assigned = false;
		for(Action a : actions) {
			double qtmp = this.getQValeur(e, a);
			if(qtmp > maxQ || !assigned) {
				assigned = true;
				maxQ = qtmp;
			}
		}
		return maxQ;
	}

	@Override
	public double getQValeur(Etat e, Action a) {
		if(!this.qvaleurs.containsKey(e) || !this.qvaleurs.get(e).containsKey(a))
			return 0;
		return this.qvaleurs.get(e).get(a);
	}
	
	
	
	@Override
	public void setQValeur(Etat e, Action a, double d) {
		//*** VOTRE CODE
		if(d < this.vmin)
			this.vmin = d;
		if(d > this.vmax)
			this.vmax = d;
		
		if(!this.qvaleurs.containsKey(e)) {
			HashMap<Action,Double> possibleActions = new HashMap<Action,Double>();
			for(Action ta : this.getActionsLegales(e)) {
				possibleActions.put(ta, 0.0);
			}
			this.qvaleurs.put(e, possibleActions);
		}
		this.qvaleurs.get(e).put(a, d);
		
		// mise a jour vmax et vmin pour affichage du gradient de couleur:
				//vmax est la valeur de max pour tout s de V
				//vmin est la valeur de min pour tout s de V
				// ...
		
		
		this.notifyObs();
		
	}
	
	
	/**
	 * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
	 * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
	 * @param e
	 * @param a
	 * @param esuivant
	 * @param reward
	 */
	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		if (RLAgent.DISPRL)
			System.out.println("QL mise a jour etat "+e+" action "+a+" etat' "+esuivant+ " r "+reward);
		
		double maxQOfNextStep = 0;
		boolean assigned = false;
		for(Action asuivant : this.getActionsLegales(esuivant)) {
			double qNext = this.getQValeur(esuivant, asuivant);
			if(qNext > maxQOfNextStep || !assigned) {
				maxQOfNextStep = qNext;
				assigned = true;
			}
			
		}
		
		double newQ = (1-this.alpha)*this.getQValeur(e, a) + this.alpha * (reward + this.gamma * maxQOfNextStep);
		//System.out.println(this.qvaleurs);
		this.setQValeur(e, a, newQ);
	}

	@Override
	public Action getAction(Etat e) {
		this.actionChoisie = this.stratExplorationCourante.getAction(e);
		return this.actionChoisie;
	}

	@Override
	public void reset() {
		super.reset();
		//*** VOTRE CODE
		this.vmin = 99999999;
		this.vmax = -99999999;
		this.qvaleurs.clear();
		
		this.episodeNb =0;
		this.notifyObs();
	}
}
