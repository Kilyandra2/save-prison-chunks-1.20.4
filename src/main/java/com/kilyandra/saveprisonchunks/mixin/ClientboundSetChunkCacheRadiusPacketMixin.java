package com.kilyandra.saveprisonchunks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.network.protocol.game.ClientboundSetChunkCacheRadiusPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientboundSetChunkCacheRadiusPacket.class)
public abstract class ClientboundSetChunkCacheRadiusPacketMixin {

    @Inject(method = "getRadius", at = @At(value = "HEAD"), cancellable = true)
    private void changeServerChunkCacheRadius(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(128);
    }

}
