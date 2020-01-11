package org.seccanj.clans.model.control;

public class BeingFilterFactory {

	public enum BeingFilterType {
		partner,
		beingType
	}
	
	public static BeingFilter getFilter(String name, Object... params) {
		BeingFilterType filterType = BeingFilterType.valueOf(name);
		
		switch (filterType) {
		case partner:
			return new PartnerFilter(params);
		case beingType:
			return new BeingTypeFilter(params);
		}
		
		return null;
	}
	
}
