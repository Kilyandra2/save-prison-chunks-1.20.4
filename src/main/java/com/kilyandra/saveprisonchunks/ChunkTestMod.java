package com.kilyandra.saveprisonchunks;

import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ChunkTestMod implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("chunk-test");
	public static final ChunksManager MANAGER = new ChunksManager();

	@Override
	public void onInitializeClient() {
		FilesManager.createDirs();
		LOGGER.info("Mod loaded.");
	}
}