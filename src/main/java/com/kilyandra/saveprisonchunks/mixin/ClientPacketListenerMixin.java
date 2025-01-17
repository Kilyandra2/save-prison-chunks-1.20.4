package com.kilyandra.saveprisonchunks.mixin;

import com.kilyandra.saveprisonchunks.SavePrisonChunksMod;

import net.minecraft.network.protocol.game.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientPacketListener;


@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

	@Inject(method = "handleForgetLevelChunk", at = @At(value = "HEAD"), cancellable = true)
	private void cancelForgetLevelChunk(ClientboundForgetLevelChunkPacket packet, CallbackInfo ci) {
		ci.cancel();
	}

	@Inject(method = "handleLevelChunkWithLight", at = @At(value = "HEAD"))
	private void saveLevelChunkWithLight(ClientboundLevelChunkWithLightPacket packet, CallbackInfo ci) {
		SavePrisonChunksMod.MANAGER.saveChunk(packet);
	}

	@Inject(method = "handleAddObjective", at = @At(value = "HEAD"))
	private void scoreboardTest(ClientboundSetObjectivePacket packet, CallbackInfo ci) {
		SavePrisonChunksMod.MANAGER.checkScoreboardName(packet);
	}
}