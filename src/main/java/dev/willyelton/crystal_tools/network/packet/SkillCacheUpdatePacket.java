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

    public SkillCacheUpdatePacket(FriendlyByteBuf buffer) {
        this.tool = buffer.readUtf();
        this.data = buffer.readJsonWithCodec(SkillData.CODEC);
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.tool);
        buffer.writeJsonWithCodec(SkillData.CODEC, this.data);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        CrystalTools.LOGGER.log(Level.TRACE, "Adding tool to cache: " + this.tool);

        SkillTreeRegistry.SKILL_TREES.put(this.tool, this.data);
    }
}
