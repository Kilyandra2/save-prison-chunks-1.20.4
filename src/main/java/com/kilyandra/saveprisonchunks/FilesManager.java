package com.kilyandra.saveprisonchunks;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

public class FilesManager {
    private static final String MOD_DIR = Minecraft.getInstance().gameDirectory.getAbsolutePath() + "/prison_chunks/";

    public static void saveChunkToFile(ClientboundLevelChunkWithLightPacket packet) {
        FriendlyByteBuf chunk = unpackData(packet);
        int X = packet.getX();
        int Z = packet.getZ();

        writeToFile(chunk, MOD_DIR + "chunk_" + X + "_" + Z + ".data");
    }

    public static FriendlyByteBuf[] loadChunks(){
        File[] files = Objects.requireNonNull(new File(MOD_DIR).listFiles());
        FriendlyByteBuf[] chunks = new FriendlyByteBuf[files.length];

        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            FriendlyByteBuf chunk = loadFromFile(file);
            chunks[i] = chunk;
        }
        return chunks;
    }

    private static FriendlyByteBuf loadFromFile(File file){
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] bytes = fis.readAllBytes();
            return new FriendlyByteBuf(Unpooled.wrappedBuffer(bytes));

        } catch (IOException e) {
            System.err.println("Ошибка при чтении файла: " + file.getAbsolutePath());
            return null;
        }
    }

    private static void writeToFile(FriendlyByteBuf buf, String fileName) {
        try (FileOutputStream fos = new FileOutputStream(fileName)) {
            byte[] bytes = new byte[buf.readableBytes()];
            buf.readBytes(bytes);
            fos.write(bytes);

        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + e.getMessage());

        }
    }


    private static FriendlyByteBuf unpackData(ClientboundLevelChunkWithLightPacket packet) {
        FriendlyByteBuf chunk = new FriendlyByteBuf(Unpooled.buffer());
        packet.write(chunk);

        return chunk;
    }

    public static void createDirs(){
        File file = new File(MOD_DIR);

        if (!file.mkdirs() && !file.exists()) {
            System.err.println("Не удалось создать директории: " + MOD_DIR);
        }
    }
}
