package org.seccanj.clans.model;

public class Gene {

	public enum GeneType {
		plantSplitPeriod(25),
		sightDistance(100),
		maxEnergy(100),
		maxHealth(100),
		maxActionPoints(10),
		maxSpeed(5);
		
		public Object defaultValue;
		
		private GeneType(Object defaultValue) {
			this.defaultValue = defaultValue;
		}
		
		public Gene getGene() {
			return new Gene(this.name(), this.defaultValue);
		}
		
	}
	
	public String name;
	public Object value;
	
	public Gene() {
	}
	
	public Gene(String name, Object value) {
		this.name = name;
		this.value = value;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
}
