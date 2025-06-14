package dev.willyelton.crystal_tools.common.levelable;

import com.mojang.serialization.Codec;
import dev.willyelton.crystal_tools.utils.StringUtils;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

// TODO: Include more of the stuff here?
public record LevelableTooltip(Map<String, Float> skills) {
    public static Codec<LevelableTooltip> CODEC = Codec.unboundedMap(Codec.STRING, Codec.FLOAT)
            .xmap(LevelableTooltip::new, LevelableTooltip::skills);
    public static StreamCodec<ByteBuf, LevelableTooltip> STREAM_CODEC =
            ByteBufCodecs.map(i -> (Map<String, Float>) new HashMap<String, Float>(), ByteBufCodecs.STRING_UTF8, ByteBufCodecs.FLOAT)
                    .map(LevelableTooltip::new, LevelableTooltip::skills);

    public void addTooltips(List<Component> toolTips, Item.TooltipContext context, TooltipFlag flag, int startingIndex) {
        if (!flag.hasShiftDown()) {
            toolTips.add(startingIndex++, Component.literal("<Hold Shift For Skills>"));
        } else {
            toolTips.add(startingIndex++, Component.literal("\u00A76Skills:"));

            for (Map.Entry<String, Float> entry : skills().entrySet()) {
                toolTips.add(startingIndex++, Component.literal(String.format("\u00A76     %s: %s", StringUtils.formatKey(entry.getKey()), StringUtils.formatFloat(entry.getValue()))));
            }
        }
    }
}
