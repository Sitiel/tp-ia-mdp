package agent.planningagent;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import util.HashMapUtil;

import java.util.HashMap;

import environnement.Action;
import environnement.Etat;
import environnement.IllegalActionException;
import environnement.MDP;
import environnement.Action2D;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration 
 * et choisit ses actions selon la politique calculee.
 * @author laetitiamatignon
 *
 */
public class ValueIterationAgent extends PlanningValueAgent{
	/**
	 * discount facteur
	 */
	protected double gamma;

	/**
	 * fonction de valeur des etats
	 */
	protected HashMap<Etat,Double> V;
	
	/**
	 * 
	 * @param gamma
	 * @param mdp
	 */
	public ValueIterationAgent(double gamma,  MDP mdp) {
		super(mdp);
		this.gamma = gamma;
		
		this.V = new HashMap<Etat,Double>();
		for (Etat etat:this.mdp.getEtatsAccessibles()){
			V.put(etat, 0.0);
		}
	}
	
	
	
	
	public ValueIterationAgent(MDP mdp) {
		this(0.9,mdp);

	}
	
	
	private double bellmanEquationOnState(Etat i, ArrayList<Action> bestActions) {
		double bestR = -999999;
		Map<Etat, Double> etatProba;
		List<Action> actions = this.mdp.getActionsPossibles(i);
		for(Action a : actions) {
			try {
				etatProba = this.mdp.getEtatTransitionProba(i, a);
				double r = 0;
				for(Etat j : etatProba.keySet()) {
					r += etatProba.get(j) * (this.mdp.getRecompense(i, a, j) + this.gamma * this.V.get(j));
				}
				if(r > bestR) {
					bestR = r;
					bestActions.clear();
					bestActions.add(a);
				}
				else if(r == bestR) {
					bestActions.add(a);
				}
				
			} catch (IllegalActionException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return bestR;
	}
	
	/**
	 * 
	 * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s'))
	 * et notifie ses observateurs.
	 * Ce n'est pas la version inplace (qui utilise la nouvelle valeur de V pour mettre a jour ...)
	 */
	@Override
	public void updateV(){
		//delta est utilise pour detecter la convergence de l'algorithme
		//Dans la classe mere, lorsque l'on planifie jusqu'a convergence, on arrete les iterations        
		//lorsque delta < epsilon 
		//Dans cette classe, il  faut juste mettre a jour delta 
		this.delta=0.0;
		//*** VOTRE CODE
		this.vmax = 0;
		this.vmin = 999999;
		
		HashMap<Etat, Double> tmpV = new HashMap<Etat,Double>();
		for (Etat i : this.V.keySet()) {
			
			if(this.mdp.estAbsorbant(i)) {
				tmpV.put(i, 0.0);
				continue;
			}
			
			//Best actions not used here but required
			ArrayList<Action> bestActions = new ArrayList<Action>();
			double bestR = bellmanEquationOnState(i, bestActions);
			
				
			
			if(bestR > this.vmax)
				 this.vmax = bestR;
			if(bestR < this.vmin)
				this.vmin = bestR;
			
		
			tmpV.put(i, bestR);
			double tmpDelta = tmpV.get(i) - this.V.get(i);
			if(tmpDelta > this.delta)
				this.delta = tmpDelta;
		}
		
		this.V = tmpV;
		
		
		//mise a jour de vmax et vmin utilise pour affichage du gradient de couleur:
		//vmax est la valeur max de V pour tout s 
		//vmin est la valeur min de V pour tout s
		// ...
		
		//******************* laisser cette notification a la fin de la methode	
		this.notifyObs();
	}
	
	
	/**
	 * renvoi l'action executee par l'agent dans l'etat e 
	 * Si aucune actions possibles, renvoi Action2D.NONE
	 */
	@Override
	public Action getAction(Etat e) {
		//*** VOTRE CODE
		List<Action> actions = this.getPolitique(e);
		if (actions.size()==0)
			return Action2D.NONE;
		int r = rand.nextInt(actions.size());//Random action from best actions
		
		return actions.get(r);
		
	}


	@Override
	public double getValeur(Etat _e) {
                 //Renvoie la valeur de l'Etat _e, c'est juste un getter, ne calcule pas la valeur ici
                 //(la valeur est calculee dans updateV
		//*** VOTRE CODE
		
		return this.V.get(_e);
	}
	/**
	 * renvoi action(s) de plus forte(s) valeur(s) dans etat 
	 * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
	 */
	@Override
	public List<Action> getPolitique(Etat _e) {
		//*** VOTRE CODE
		
		// retourne action de meilleure valeur dans _e selon V, 
		// retourne liste vide si aucune action legale (etat absorbant)
		ArrayList<Action> bestA = new ArrayList<Action>();
		this.bellmanEquationOnState(_e, bestA);
		
		return bestA;
		//return returnactions;
		
	}
	
	@Override
	public void reset() {
		super.reset();
                //reinitialise les valeurs de V 
		//*** VOTRE CODE
		for (Etat etat:this.mdp.getEtatsAccessibles()){
			V.put(etat, 0.0);
		}
		
		this.notifyObs();
	}

	

	

	public HashMap<Etat,Double> getV() {
		return V;
	}
	public double getGamma() {
		return gamma;
	}
	@Override
	public void setGamma(double _g){
		System.out.println("gamma= "+gamma);
		this.gamma = _g;
	}


	
	

	
}