package com.ssomar.executableblocks.executableblocks.activators;

import com.ssomar.score.sobject.sactivator.SOption;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public enum Option implements SOption {

	CROP_GROW("CROP_GROW"),
	CROP_PLACE_BLOCK("CROP_PLACE_BLOCK"),
	ENTITY_WALK_ON ("ENTITY_WALK_ON"),
	EXPLOSION_HIT ("EXPLOSION_HIT"),
	LOOP ("LOOP"),
	PLAYER_RIGHT_CLICK_ON ("PLAYER_RIGHT_CLICK_ON"),
	PLAYER_LEFT_CLICK_ON ("PLAYER_LEFT_CLICK_ON"),
	PLAYER_ALL_CLICK_ON ("PLAYER_ALL_CLICK_ON"),
	PLAYER_JUMP_ON ("PLAYER_JUMP_ON"),
	PLAYER_WALK_ON ("PLAYER_WALK_ON"),
	PLAYER_BREAK ("PLAYER_BREAK"),
	PLAYER_PLACE ("PLAYER_PLACE"),
	PLAYER_PRESS ("PLAYER_PRESS"),
	PLAYER_DEATH_ON ("PLAYER_DEATH_ON"),
	PLAYER_EAT_ON ("PLAYER_EAT_ON"),
	PLAYER_SNEAK_ON ("PLAYER_SNEAK_ON"),
	PLAYER_FALL_ON ("PLAYER_FALL_ON"),
	PLAYER_SPRINT_ON ("PLAYER_SPRINT_ON"),
	PROJECTILE_HIT ("PROJECTILE_HIT");

	private String[] names;

	Option(String... names) {
		this.names = names;
	}

	public List<SOption> getPremiumOption() {
		return getPremiumOptionSt();
	}

	public static List<SOption> getPremiumOptionSt() {
		List<SOption> result = new ArrayList<>();
		result.add(CROP_GROW);
		result.add(CROP_PLACE_BLOCK);
		result.add(LOOP);
		result.add(PROJECTILE_HIT);
		result.add(EXPLOSION_HIT);
		result.add(ENTITY_WALK_ON);
		result.add(PLAYER_DEATH_ON);
		result.add(PLAYER_SPRINT_ON);
		result.add(PLAYER_PRESS);
		return result;
	}

	public static String getPremiumOptionAsString() {
		StringBuilder sb = new StringBuilder("");
		for(SOption option : getPremiumOptionSt()) {
			sb.append(option.toString());
			sb.append(" ");
		}
		sb.substring(0, sb.length()-1);
		return sb.toString();
	}

	public static List<SOption> getOptionWithoutOwner() {
		List<SOption> result = new ArrayList<>();
		result.add(PLAYER_PLACE);

		return result;
	}

	@Override
	public List<SOption> getOptionWithOwner() {
		List<SOption> result = new ArrayList<>();
		for (Option option : values()) {
			if(getOptionWithoutOwner().contains(option)) continue;
			else result.add(option);
		}
		return result;
	}

	public static List<SOption> getOptWithEntity() {
		List<SOption> result = new ArrayList<>();
		result.add(PROJECTILE_HIT);
		result.add(ENTITY_WALK_ON);
		return result;
	}

	@Override
	public List<SOption> getOptionWithTargetEntity() {
		return getOptWithEntity();
	}

	public static List<SOption> getOptWithPlayer() {
		List<SOption> result = new ArrayList<>();
		result.add(PLAYER_JUMP_ON);
		result.add(PLAYER_WALK_ON);
		result.add(PLAYER_RIGHT_CLICK_ON);
		result.add(PLAYER_LEFT_CLICK_ON);
		result.add(PLAYER_ALL_CLICK_ON);
		result.add(PLAYER_BREAK);
		result.add(PLAYER_DEATH_ON);
		result.add(PLAYER_EAT_ON);
		result.add(PLAYER_SNEAK_ON);
		result.add(PLAYER_FALL_ON);
		result.add(PLAYER_SPRINT_ON);
		result.add(PROJECTILE_HIT);
		result.add(PLAYER_PRESS);
		result.add(PLAYER_PLACE);

		return result;
	}

	public static List<SOption> getOptWithDamageCause() {
		List<SOption> result = new ArrayList<>();

		return result;
	}

	public static List<SOption> getOptWithCommand() {
		List<SOption> result = new ArrayList<>();

		return result;
	}

	@Override
	public List<SOption> getOptionWithPlayer() {
		return getOptWithPlayer();
	}

	@Override
	public List<SOption> getOptionWithEntity() {
		List<SOption> result = new ArrayList<>();
		return result;
	}

	@Override
	public List<SOption> getOptionWithBlock() {
		List<SOption> result = new ArrayList<>();
		for (Option option : values()) {
			result.add(option);
		}
		return result;
	}

	@Override
	public List<SOption> getOptionWithTargetBlock() {
		return getOptionWithTargetBlock();
	}

	public static List<SOption> getOptWithTargetBlock() {
		List<SOption> result = new ArrayList<>();
		result.add(CROP_PLACE_BLOCK);
		return result;
	}

	@Override
	public List<SOption> getOptionWithTargetPlayer() {
		return new ArrayList<>();
	}

	@Override
	public List<SOption> getOptionWithWorld() {
		List<SOption> result = new ArrayList<>();
		for (Option option : values()) {
			result.add(option);
		}
		return result;
	}

	public static List<SOption> getOptionWithRequiredThings() {
		List<SOption> result = new ArrayList<>();
		for (Option option : values()) {
			result.add(option);
		}
		return result;
	}

	@Override
	public boolean isValidOption(String entry) {
		for (Option option : values()) {
			for (String name : option.getNames()) {
				if (name.equalsIgnoreCase(entry)) {
					return true;
				}
			}
		}
		return false;
	}

	public Option getPrev() {
		Option opt = values()[values().length-1];
		for(Option o : values()) {
			if(this.equals(o)) {
				return opt;
			}
			else opt = o;
		}
		return opt;
	}

	public Option getNext() {
		Option opt = values()[0];
		boolean next = false;
		for(Option o : values()) {
			if(next) {
				opt = o;
				break;
			}
			if(this.equals(o)) {
				next = true;
			}
		}
		return opt;
	}

	@Override
	public Option getOption(String entry) {
		for (Option option : values()) {
			for (String name : option.getNames()) {
				if (name.equalsIgnoreCase(entry)) {
					return option;
				}
			}
		}
		return null;
	}

	@Override
	public List<SOption> getValues() {
		return new ArrayList<>(Arrays.asList(Option.values()));
	}

	@Override
	public SOption getDefaultValue() {
		return Option.PLAYER_ALL_CLICK_ON;
	}

	public boolean containsThisName(String entry) {
		for (String name : getNames()) {
			if (name.equalsIgnoreCase(entry))
				return true;
		}
		return false;
	}

	public String[] getNames() {
		return names;
	}

	public void setNames(String[] names) {
		this.names = names;
	}

	@Override
	public List<SOption> getOptionWithItem() {
		return new ArrayList<>();
	}

}
