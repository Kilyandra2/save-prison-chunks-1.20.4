package com.kilyandra.saveprisonchunks.mixin;

import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundForgetLevelChunkPacket;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;

import com.kilyandra.saveprisonchunks.ChunkTestMod;

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {

	@Inject(method = "handleForgetLevelChunk", at = @At(value = "HEAD"), cancellable = true)
	private void cancelForgetLevelChunk(ClientboundForgetLevelChunkPacket packet, CallbackInfo ci){
		ci.cancel();
	}

	@Inject(method = "handleLevelChunkWithLight", at = @At(value = "HEAD"))
	private void saveLevelChunkWithLight(ClientboundLevelChunkWithLightPacket packet, CallbackInfo ci) {
		ChunkTestMod.MANAGER.saveChunk(packet);
	}

	@Inject(method = "setTitleText", at = @At(value = "HEAD"))
	private void getTitleText(ClientboundSetTitleTextPacket packet, CallbackInfo ci) {
		ChunkTestMod.MANAGER.checkTitle(packet.getText().getSiblings().get(0).getString());
	}

}