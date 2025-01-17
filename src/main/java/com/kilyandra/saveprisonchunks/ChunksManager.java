package com.kilyandra.saveprisonchunks;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.*;
import org.slf4j.Logger;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;


public class ChunksManager {
    private static final Logger LOGGER = SavePrisonChunksMod.LOGGER;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isPrison = false;

    public ChunksManager() {
        ClientPlayConnectionEvents.JOIN.register(this::onConnect);
    }

    private void onConnect(ClientPacketListener listener, PacketSender sender, Minecraft minecraft) {
        minecraft.executeBlocking(() -> {
            if (minecraft.getCurrentServer() == null || !minecraft.getCurrentServer().ip.endsWith("mineland.net") || !this.isPrison) return;

            FriendlyByteBuf[] chunks = FilesManager.loadChunks();

            LOGGER.info("Chunk input started...");
            listener.handleChunkBatchStart(new ClientboundChunkBatchStartPacket());

            int j = 0;
            int batchSize = 8;
            for (int i = 0; i < chunks.length; i++) {
                listener.handleLevelChunkWithLight(new ClientboundLevelChunkWithLightPacket(chunks[i]));
                j++;

                if ((i + 1) % batchSize == 0 || i == chunks.length - 1) {
                    listener.handleChunkBatchFinished(new ClientboundChunkBatchFinishedPacket(j));
                    j = 0;

                    if (i < chunks.length - 1) {
                        listener.handleChunkBatchStart(new ClientboundChunkBatchStartPacket());
                    }
                }
            }

            LOGGER.info("Chunk input finished.");
        });
    }

    public void saveChunk(ClientboundLevelChunkWithLightPacket packet) {
        if (!this.isPrison || !onNettyThread()) return;

        executorService.submit(() -> FilesManager.saveChunkToFile(packet));
    }

    public void checkScoreboardName(ClientboundSetObjectivePacket packet) {
        if (!onNettyThread()) return;

        String scoreboardName = packet.getDisplayName().getString();
        if (scoreboardName.equals("lvl") || scoreboardName.equals("sidebar")) {
            return;
        }

        this.isPrison = scoreboardName.equals("PRISON");
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean onNettyThread(){
        return Thread.currentThread().getName().startsWith("Netty Client IO");
    }
}
