package org.seccanj.clans.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.seccanj.clans.model.Gene.GeneType;

public class Dna {
	
	private static Random RND = new Random();
	
	private Map<GeneType, Gene> genes;
	
	public Dna() {
		genes = new HashMap<GeneType, Gene>();
	}
	
	public Dna(Map<GeneType, Gene> genes) {
		this.genes = genes;
	}

	public static Dna getMixedDna(Dna parent1, Dna parent2) {
		Dna result = new Dna();
		
		parent1.genes.forEach((geneType, gene) -> result.put(geneType, Math.round(RND.nextDouble()) == 0 ? gene : parent2.get(geneType)));
		
		return result;
	}

	public void put(GeneType geneType, Gene gene) {
		genes.put(geneType, gene);
	}

	public Gene get(GeneType geneType) {
		return genes.get(geneType);
	}
	
}
