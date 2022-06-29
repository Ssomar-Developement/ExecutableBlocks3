package com.ssomar.executableblocks.configs.messages;

import com.ssomar.score.configs.messages.MessageInterface;
import lombok.Getter;
import lombok.Setter;

public enum Message implements MessageInterface{

	/* commands */
	RECEIVE_BLOCK_MESSAGE ("receiveBlock"),
	GIVE_MESSAGE ("giveMessage"),
	FULL_INVENTORY ("fullInventory"),
	REQUIRED_LEVEL ("requiredLevel"),
	NOT_WHITELISTED_WORLD ("notWhitelistedWorld"),
	REQUIRED_ITEMS_FIRST_PART ("requiredItemsFirstPart"),
	REQUIRED_ITEMS_QUANTITY ("requiredItemsQuantity"),
	REQUIRED_ITEMS_SEPARATOR ("requiredItemsSeparator"),
	PERMISSION_MESSAGE("permissionMessage");

	@Setter
	private String name;

	Message(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return name;
	}

}
