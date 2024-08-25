package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.inventory.container.CrystalFurnaceContainerMenu;
import dev.willyelton.crystal_tools.levelable.block.entity.CrystalFurnaceBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;

import java.nio.charset.Charset;
import java.util.function.Supplier;

public class BlockAttributePacket {
    private final String key;
    private final float value;
    private final int id;
    private final int pointsToSpend;

    public BlockAttributePacket(String key, float value, int id, int pointsToSpend) {
        this.key = key;
        this.value = value;
        this.id = id;
        this.pointsToSpend = pointsToSpend;
    }

    public BlockAttributePacket(FriendlyByteBuf buffer) {
        int keyLen = buffer.readInt();
        this.key = buffer.readCharSequence(keyLen, Charset.defaultCharset()).toString();
        this.value = buffer.readFloat();
        this.id = buffer.readInt();
        this.pointsToSpend = buffer.readInt();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(this.key.length());
        buffer.writeCharSequence(this.key, Charset.defaultCharset());
        buffer.writeFloat(this.value);
        buffer.writeInt(this.id);
        buffer.writeInt(this.pointsToSpend);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer player = ctx.get().getSender();

        if (player != null) {
            AbstractContainerMenu container = player.containerMenu;

            if (container instanceof CrystalFurnaceContainerMenu crystalFurnaceContainerMenu) {
                CrystalFurnaceBlockEntity blockEntity = crystalFurnaceContainerMenu.getBlockEntity();
                int skillPoints = blockEntity.getSkillPoints();
                int pointsToAdd = Math.min(skillPoints, this.pointsToSpend);

                blockEntity.addToData(this.key, this.value * pointsToAdd);
                if (this.id != -1) {
                    blockEntity.addToPoints(this.id, pointsToAdd);
                }
            }
        }
    }
}
