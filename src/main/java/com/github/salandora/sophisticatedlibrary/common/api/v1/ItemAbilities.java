package com.github.salandora.sophisticatedlibrary.common.api.v1;

import com.google.common.collect.Sets;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemAbilities {
	public static final ItemAbility SHEARS_DIG = ItemAbility.get("shears_dig");

	public static final ItemAbility PICKAXE_DIG = ItemAbility.get("pickaxe_dig");

	public static final ItemAbility AXE_DIG = ItemAbility.get("axe_dig");
	public static final ItemAbility AXE_STRIP = ItemAbility.get("axe_strip");
	public static final ItemAbility AXE_SCRAPE = ItemAbility.get("axe_scrape");
	public static final ItemAbility AXE_WAX_OFF = ItemAbility.get("axe_wax_off");

	public static final ItemAbility SHOVEL_DIG = ItemAbility.get("shovel_dig");
	public static final ItemAbility SHOVEL_FLATTEN = ItemAbility.get("shovel_flatten");
	public static final ItemAbility SHOVEL_DOUSE = ItemAbility.get("shovel_douse");

	public static final ItemAbility SWORD_SWEEP = ItemAbility.get("sword_sweep");

	public static final ItemAbility SHEARS_HARVEST = ItemAbility.get("shears_harvest");
	public static final ItemAbility SHEARS_REMOVE_ARMOR = ItemAbility.get("shears_remove_armor");
	public static final ItemAbility SHEARS_CARVE = ItemAbility.get("shears_carve");
	public static final ItemAbility SHEARS_DISARM = ItemAbility.get("shears_disarm");
	public static final ItemAbility SHEARS_TRIM = ItemAbility.get("shears_trim");

	public static final ItemAbility HOE_DIG = ItemAbility.get("hoe_dig");
	public static final ItemAbility HOE_TILL = ItemAbility.get("till");

	// Default actions supported by each tool type
	public static final Set<ItemAbility> DEFAULT_AXE_ACTIONS = of(AXE_DIG, AXE_STRIP, AXE_SCRAPE, AXE_WAX_OFF);
	public static final Set<ItemAbility> DEFAULT_HOE_ACTIONS = of(HOE_DIG, HOE_TILL);
	public static final Set<ItemAbility> DEFAULT_SHOVEL_ACTIONS = of(SHOVEL_DIG, SHOVEL_FLATTEN, SHOVEL_DOUSE);
	public static final Set<ItemAbility> DEFAULT_PICKAXE_ACTIONS = of(PICKAXE_DIG);
	public static final Set<ItemAbility> DEFAULT_SHEARS_ACTIONS = of(SHEARS_DIG, SHEARS_HARVEST, SHEARS_REMOVE_ARMOR, SHEARS_CARVE, SHEARS_DISARM, SHEARS_TRIM);

	private static Set<ItemAbility> of(ItemAbility... actions) {
		return Stream.of(actions).collect(Collectors.toCollection(Sets::newIdentityHashSet));
	}
}
