package agent.strategy;

import java.util.List;
import java.util.Random;

import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Etat;
/**
 * Strategie qui renvoit un choix aleatoire avec proba epsilon, un choix glouton (suit la politique de l'agent) sinon
 * @author lmatignon
 *
 */
public class StrategyGreedy extends StrategyExploration{
	/**
	 * parametre pour probabilite d'exploration
	 */
	protected double epsilon;
	private Random rand=new Random();
	
	
	
	public StrategyGreedy(RLAgent agent,double epsilon) {
		super(agent);
		this.epsilon = epsilon;
	}

	@Override
	public Action getAction(Etat _e) {//renvoi null si _e absorbant
		double d =rand.nextDouble();
		List<Action> actions;
		if (this.agent.getActionsLegales(_e).isEmpty()){
			return null;
		}
		
		actions = this.agent.getActionsLegales(_e);
	
		if(d < 1 - this.epsilon) {
			Action bestAction = null;
			double bestQ = 0;
			boolean assigned = false;
			for(Action a : actions) {
				double qtmp = this.agent.getQValeur(_e, a);
				if(qtmp > bestQ || !assigned) {
					assigned = true;
					bestQ = qtmp;
					bestAction = a;
				}
				else if(qtmp == bestQ && rand.nextBoolean()) {
					bestAction = a;
				}
			}
			//System.out.println("state : " + _e + " action : " + bestAction + " -> " + bestQ);
			return bestAction;
		}
		
		
		return actions.get(rand.nextInt(actions.size()));
	}

	public double getEpsilon() {
		return epsilon;
	}

	public void setEpsilon(double epsilon) {
		this.epsilon = epsilon;
		System.out.println("epsilon:"+epsilon);
	}

/*	@Override
	public void setAction(Action _a) {
		// TODO Auto-generated method stub
		
	}*/

}
