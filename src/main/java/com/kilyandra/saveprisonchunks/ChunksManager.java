package com.kilyandra.saveprisonchunks;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundChunkBatchFinishedPacket;
import net.minecraft.network.protocol.game.ClientboundChunkBatchStartPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ChunksManager {
    private static final Logger LOGGER = SavePrisonChunksMod.LOGGER;
    private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private boolean isPrison = false;

    public ChunksManager() {
        ClientPlayConnectionEvents.JOIN.register(this::onConnect);
    }

    private void onConnect(ClientPacketListener listener, PacketSender packetSender, Minecraft minecraft) {
        if (!this.isPrison) return;

        minecraft.executeBlocking(() -> {
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
        if (!this.isPrison) return;

        if (!Thread.currentThread().getName().startsWith("Netty Client IO")) return;

        executorService.submit(() -> FilesManager.saveChunkToFile(packet));
        //LOGGER.info("Chunk {} {} saved.", packet.getX(), packet.getZ());
    }

    public void checkTitle(String title) {
        this.isPrison = Objects.equals(title, "PRISON");
    }
}
