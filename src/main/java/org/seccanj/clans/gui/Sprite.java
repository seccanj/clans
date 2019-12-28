package org.seccanj.clans.gui;

import org.seccanj.clans.model.Position;

public class Sprite {

	public Sprite(Position position, SpriteType type) {
		super();
		this.position = position;
		this.type = type;
	}

	public enum SpriteType {
		plant,
		individual
	}
	
	public Position position;
	
	public SpriteType type;
	
}
