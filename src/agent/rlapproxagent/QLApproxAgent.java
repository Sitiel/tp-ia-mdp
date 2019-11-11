package agent.rlapproxagent;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import agent.rlagent.QLearningAgent;
import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;
/**
 * Agent qui apprend avec QLearning en utilisant approximation de la Q-valeur : 
 * approximation lineaire de fonctions caracteristiques 
 * 
 * @author laetitiamatignon
 *
 */
public class QLApproxAgent extends QLearningAgent{
	
	protected FeatureFunction qApproximate;
	protected double[] w;
	
	public QLApproxAgent(double alpha, double gamma, Environnement _env,FeatureFunction _featurefunction) {
		super(alpha, gamma, _env);
		//*** VOTRE CODE
		this.qApproximate = _featurefunction;
		this.w = new double[this.qApproximate.getFeatureNb()];
		
		
	}

	
	@Override
	public double getQValeur(Etat e, Action a) {
		double[] features = this.qApproximate.getFeatures(e, a);
		double r = 0;
		int i = 0;
		//On applique le poids à chaque feature
		for(double f : features) {
			r += f*this.w[i];
			i+=1;
		}
		//Et on retourne le résultat de la somme des features par leurs poids
		return r;

	}
	
	
	
	
	@Override
	public void endStep(Etat e, Action a, Etat esuivant, double reward) {
		if (RLAgent.DISPRL){
			System.out.println("QL: mise a jour poids pour etat \n"+e+" action "+a+" etat' \n"+esuivant+ " r "+reward);
		}
       //inutile de verifier si e etat absorbant car dans runEpisode et threadepisode 
		//arrete episode lq etat courant absorbant	
		
		
		//*** VOTRE CODE
		
		double[] features = this.qApproximate.getFeatures(e,a);
		//On met à jour les poids de chaque feature pour se rapprocher de la fonction Q
        for(int i = 0; i < this.w.length; i++)
        {
            this.w[i] += alpha * (reward + gamma * getValeur(esuivant) - getQValeur(e, a)) * features[i];
        }
	}
	
	@Override
	public void reset() {
		super.reset();
		this.qvaleurs.clear();
	
		//*** VOTRE CODE
		this.w = new double[this.qApproximate.getFeatureNb()];
		this.episodeNb =0;
		this.notifyObs();
	}
	
	
}
