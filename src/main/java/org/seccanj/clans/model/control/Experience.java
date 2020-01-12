package org.seccanj.clans.model.control;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.seccanj.clans.model.World;
import org.seccanj.clans.model.being.Being;
import org.seccanj.clans.model.control.ActionDone.ActionType;

public class Experience {

	private boolean good;
	private Being bearer;
	private ActionType actionType;
	private String actionTypeName;
	private long turn = World.getWorld().currentTurn;
	private Set<String> characteristics = new HashSet<>();
	
	public Experience() {
	}

	public Experience(boolean good, Being bearer, ActionType actionType) {
		this.good = good;
		this.bearer = bearer;
		setActionType(actionType);
	}

	public Experience(boolean good, Being bearer, ActionType actionType, String... characteristics) {
		this.good = good;
		this.bearer = bearer;
		setActionType(actionType);
		addCharacteristics(characteristics);
	}

	public Experience(boolean good, Being bearer, String actionTypeName) {
		this.good = good;
		this.bearer = bearer;
		setActionTypeName(actionTypeName);
	}

	public Experience(boolean good, Being bearer, String actionTypeName, String... characteristics) {
		this.good = good;
		this.bearer = bearer;
		setActionTypeName(actionTypeName);
		addCharacteristics(characteristics);
	}

	public Experience(boolean good, Being bearer, String actionTypeName, Set<String> characteristics) {
		this.good = good;
		this.bearer = bearer;
		setActionTypeName(actionTypeName);
		setCharacteristics(characteristics);
	}

	public boolean isGood() {
		return good;
	}
	
	public void setGood(boolean good) {
		this.good = good;
	}
	
	public Being getBearer() {
		return bearer;
	}
	
	public void setBearer(Being bearer) {
		this.bearer = bearer;
	}
	
	public ActionType getActionType() {
		return actionType;
	}
	
	public void setActionType(ActionType actionType) {
		this.actionType = actionType;
		actionTypeName = actionType.name();
	}
	
	public long getTurn() {
		return turn;
	}

	public void setTurn(long turn) {
		this.turn = turn;
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
	
	public boolean hasCharacteristics(String... chars) {
		return Arrays.stream(chars)
			.allMatch(c -> characteristics.contains(c));
	}

	public boolean hasCharacteristicsAsSet(Set<String> chars) {
		return characteristics.containsAll(chars);
	}

	public String getActionTypeName() {
		return actionTypeName;
	}

	public void setActionTypeName(String actionTypeName) {
		this.actionTypeName = actionTypeName;
		this.actionType = ActionType.valueOf(actionTypeName);
	}
}
