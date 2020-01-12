package org.seccanj.clans.model.control;

import java.util.Arrays;
import java.util.Set;

import org.seccanj.clans.model.World;
import org.seccanj.clans.model.being.Being;

public class ActionDone {
	
	public enum ActionType {
		starving,
		recovering,
		mating,
		generate,
		setTarget,
		eat,
		move,
		decreaseHealth,
		learn
	}

	private Being being;
	private ActionType actionType;
	private String actionTypeName;
	private long turn = World.getWorld().currentTurn;
	private Set<String> characteristics;
	
	public ActionDone(Being being, String actionTypeName) {
		this.being = being;
		this.actionTypeName = actionTypeName;
		this.actionType = ActionType.valueOf(actionTypeName);
	}
	
	public ActionDone(Being being, String actionTypeName, Set<String> characteristics) {
		this.being = being;
		this.actionTypeName = actionTypeName;
		this.actionType = ActionType.valueOf(actionTypeName);
		setCharacteristics(characteristics);
	}
	
	public Being getBeing() {
		return being;
	}

	public void setBeing(Being being) {
		this.being = being;
	}
	
	public ActionType getActionType() {
		return actionType;
	}

	public void setActionType(String actionTypeName) {
		this.actionType = ActionType.valueOf(actionTypeName);
		this.actionTypeName = actionTypeName;
	}

	public long getTurn() {
		return turn;
	}

	public void setTurn(long turn) {
		this.turn = turn;
	}

	public String getActionTypeName() {
		return actionTypeName;
	}

	public void setActionTypeName(String actionTypeName) {
		this.actionTypeName = actionTypeName;
		this.actionType = ActionType.valueOf(actionTypeName);
	}

	public Set<String> getCharacteristics() {
		return characteristics;
	}

	public void setCharacteristics(Set<String> characteristics) {
		this.characteristics = characteristics;
	}

	public void addCharacteristics(String... characteristics) {
		this.characteristics.addAll(Arrays.asList(characteristics));
	}
	
}
