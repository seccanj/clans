package org.seccanj.clans.model;

import org.seccanj.clans.util.Utils;

public class Gene {

	public enum GeneType {
		plantSplitPeriod(100),
		sightDistance(150),
		maxEnergy(500),
		maxHealth(100),
		maxActionPoints(10),
		maxSpeed(5);
		
		public Object defaultValue;
		
		private GeneType(Object defaultValue) {
			this.defaultValue = defaultValue;
		}
		
		public Gene getGene() {
			return new Gene(this.name(), mutate(this.defaultValue));
		}
		
		Object mutate(Object value) {
			if (value instanceof Integer) {
				if (Math.round(Utils.RND.nextDouble()) == 0) {
					long numericValue = (int) value;
					numericValue = Math.round(numericValue + 
						Math.round((1 - Utils.RND.nextDouble() * 2)) * (Utils.RND.nextDouble() * numericValue / 10));

					return (int) numericValue;
				}
			}
			
			return value;
		}

		public Gene getGene(Object value) {
			return new Gene(this.name(), value);
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
	
	public Gene setName(String name) {
		this.name = name;
		return this;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Gene setValue(Object value) {
		this.value = value;
		return this;
	}
	
}
