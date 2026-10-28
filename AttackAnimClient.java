package com.example.attackanim;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AttackAnimClient implements ClientModInitializer {
	public static final String MOD_ID = "attackanim";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitializeClient() {
		AttackAnimConfig.load();
		LOGGER.info("Third-Person Attack Animation đã sẵn sàng");
	}
}
