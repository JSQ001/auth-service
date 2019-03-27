package com.hand.hcf.app.workflow.brms.util.cache;

import java.util.HashMap;
import java.util.Map;

public abstract class CacheNames {
	public static final String BRMS_ENTITY_RULE_APPROVAL_CHAIN = "BRMS_ENTITY:RULE_APPROVAL_CHAIN";
	public static final String BRMS_ENTITY_RULE_APPROVAL_NODE = "BRMS_ENTITY:RULE_APPROVAL_NODE";
	public static final String BRMS_ENTITY_RULE_APPROVER = "BRMS_ENTITY:RULE_APPROVER";
	public static final String BRMS_ENTITY_RULE_CONDITION = "BRMS_ENTITY:RULE_CONDITION";
	public static final String BRMS_ENTITY_RULE_CONDITION_RELATION = "BRMS_ENTITY:RULE_CONDITION_RELATION";
	public static final String BRMS_ENTITY_RULE_SCENE = "BRMS_ENTITY:RULE_SCENE";
	public static final String BRMS_CONSTANTS_RULE_APPROVAL_MODE = "BRMS_CONSTANTS:RULE_APPROVAL_MODE";
	public static final String BRMS_ARTEMIS_APPROVAL_ROLE = "BRMS_ARTEMIS:APPROVAL_ROLE";
	public static final String BRMS_ARTEMIS_CUSTOMFORM_FIELD = "BRMS_ARTEMIS:CUSTOMFORM_FIELD";


	public static final Map<String, Long> cacheExpireMap = new HashMap<>();
	static {
		Long entityExpire = 300L;
		cacheExpireMap.put(BRMS_ENTITY_RULE_APPROVAL_CHAIN, entityExpire);
		cacheExpireMap.put(BRMS_CONSTANTS_RULE_APPROVAL_MODE, 24*60*60L);
		cacheExpireMap.put(BRMS_ARTEMIS_APPROVAL_ROLE, 60L);
		cacheExpireMap.put(BRMS_ENTITY_RULE_APPROVAL_NODE, 60L);
		cacheExpireMap.put(BRMS_ENTITY_RULE_APPROVER, entityExpire);
		cacheExpireMap.put(BRMS_ENTITY_RULE_CONDITION, entityExpire);
		cacheExpireMap.put(BRMS_ENTITY_RULE_SCENE, entityExpire);
		cacheExpireMap.put(BRMS_ENTITY_RULE_CONDITION_RELATION, entityExpire);
	}
}
