package org.seccanj.clans.gui;

import java.util.List;

public class GuiContext {

	private List<Sprite> sprites;
	private long plantsNum;
	private long individualsNum;
	
	public List<Sprite> getSprites() {
		return sprites;
	}
	public void setSprites(List<Sprite> sprites) {
		this.sprites = sprites;
	}
	public long getPlantsNum() {
		return plantsNum;
	}
	public void setPlantsNum(long plantsNum) {
		this.plantsNum = plantsNum;
	}
	public long getIndividualsNum() {
		return individualsNum;
	}
	public void setIndividualsNum(long individualsNum) {
		this.individualsNum = individualsNum;
	}
	
}
