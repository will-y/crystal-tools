package dev.willyelton.crystal_tools.levelable.skill.requirement;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.willyelton.crystal_tools.levelable.skill.SkillData;
import dev.willyelton.crystal_tools.utils.CodecUtils;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.List;

public interface SkillDataRequirement {
    boolean canLevel(SkillData data, Player player);

    RequirementType getRequirementType();

    JsonElement toJson();

    static SkillDataRequirement fromJson(JsonElement jsonElement) {
        JsonObject requirementObject = jsonElement.getAsJsonObject();

        if (requirementObject.keySet().contains("node")) {
            return CodecUtils.parseOrThrow(NodeSkillDataRequirement.CODEC, jsonElement);
        } else if (requirementObject.keySet().contains("not_node")) {
            return CodecUtils.parseOrThrow(NotNodeSkillDataRequirement.CODEC, jsonElement);
        } else if (requirementObject.keySet().contains("or_node")) {
            return CodecUtils.parseOrThrow(NodeOrSkillDataRequirement.CODEC, jsonElement);
        } else if (requirementObject.keySet().contains("item")) {
            return CodecUtils.parseOrThrow(SkillItemRequirement.CODEC, jsonElement);
        }

        throw new JsonParseException("Bad Requirement: " + jsonElement);
    }

    Codec<SkillDataRequirement> CODEC = ExtraCodecs.JSON.xmap(SkillDataRequirement::fromJson, SkillDataRequirement::toJson);
}
