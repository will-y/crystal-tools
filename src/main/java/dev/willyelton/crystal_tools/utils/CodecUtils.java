package dev.willyelton.crystal_tools.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.willyelton.crystal_tools.CrystalTools;

public class CodecUtils {
    public static <T> T parseOrThrow(Codec<T> codec, JsonElement jsonElement) {
        DataResult<T> dataResult = codec.parse(JsonOps.INSTANCE, jsonElement);

        return dataResult.getOrThrow(s -> {
            CrystalTools.LOGGER.error("Failed to Parse JSON: {}", jsonElement.toString());
            throw new JsonParseException(s);
        });
    }

    public static <T> JsonElement encodeOrThrow(Codec<T> codec, T object) {
        DataResult<JsonElement> dataResult = codec.encodeStart(JsonOps.INSTANCE, object);

        return dataResult.getOrThrow(s -> {
            throw new JsonParseException(s);
        });
    }
}
