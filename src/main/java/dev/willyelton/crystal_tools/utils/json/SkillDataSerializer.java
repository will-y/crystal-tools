package dev.willyelton.crystal_tools.utils.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import dev.willyelton.crystal_tools.tool.skills.SkillData;

import java.lang.reflect.Type;

public class SkillDataSerializer implements JsonSerializer<SkillData> {
    @Override
    public JsonElement serialize(SkillData src, Type typeOfSrc, JsonSerializationContext context) {
        return null;
    }
}
