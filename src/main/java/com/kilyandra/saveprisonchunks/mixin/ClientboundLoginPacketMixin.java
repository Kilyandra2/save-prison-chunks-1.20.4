package com.kilyandra.saveprisonchunks.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;

import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientboundLoginPacket.class)
public abstract class ClientboundLoginPacketMixin {

    @Inject(method = "chunkRadius", at = @At(value = "HEAD"), cancellable = true)
    private void changeServerRenderDistance(CallbackInfoReturnable<Integer> cir){
        cir.setReturnValue(128);
    }

}
