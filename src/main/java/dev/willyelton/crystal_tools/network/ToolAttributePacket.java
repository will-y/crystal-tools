package dev.willyelton.crystal_tools.network;

import dev.willyelton.crystal_tools.utils.ItemStackUtils;
import dev.willyelton.crystal_tools.utils.NBTUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class ToolAttributePacket {
    private final String key;
    private final int value;

    public ToolAttributePacket(String key, int value) {
        this.key = key;
        this.value = value;
    }

    public static void encode(ToolAttributePacket msg, FriendlyByteBuf buffer) {
        buffer.writeInt(msg.key.length());
        buffer.writeCharSequence(msg.key, Charset.defaultCharset());
        buffer.writeInt(msg.value);
    }

    public static ToolAttributePacket decode(FriendlyByteBuf buffer) {
        int keyLen = buffer.readInt();
        String key = buffer.readCharSequence(keyLen, Charset.defaultCharset()).toString();
        return new ToolAttributePacket(key, buffer.readInt());
    }

    public static class Handler {
        public static void handle(final ToolAttributePacket msg, Supplier<NetworkEvent.Context> ctx) {
            ServerPlayer playerEntity = ctx.get().getSender();
            ItemStack heldTool = ItemStackUtils.getHeldLevelableTool(playerEntity);

            if (!heldTool.isEmpty()) {
                NBTUtils.setValue(heldTool, msg.key, msg.value);
            }
        }
    }
}
