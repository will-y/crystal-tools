package dev.willyelton.crystal_tools.network.packet;

import dev.willyelton.crystal_tools.CrystalTools;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.levelable.skill.SkillTreeRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.apache.logging.log4j.Level;

import java.util.function.Supplier;

public class SkillCacheUpdatePacket {

    private final String tool;
    private final SkillData data;

    public SkillCacheUpdatePacket(String tool, SkillData data) {
        this.tool = tool;
        this.data = data;
    }

    public static void encode(SkillCacheUpdatePacket msg, FriendlyByteBuf buffer) {
        buffer.writeUtf(msg.tool);
        buffer.writeJsonWithCodec(SkillData.CODEC, msg.data);
    }

    public static SkillCacheUpdatePacket decode(FriendlyByteBuf buffer) {
        return new SkillCacheUpdatePacket(buffer.readUtf(), buffer.readJsonWithCodec(SkillData.CODEC));
    }

    public static class Handler {
        public static void handle(final SkillCacheUpdatePacket msg, Supplier<NetworkEvent.Context> ctx) {
            CrystalTools.LOGGER.log(Level.TRACE, "Adding tool to cache: " + msg.tool);

            SkillTreeRegistry.SKILL_TREES.put(msg.tool, msg.data);
        }
    }
}
