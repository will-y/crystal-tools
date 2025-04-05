package dev.willyelton.crystal_tools.common.levelable.skill;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Object that is actually stored in itemstacks (maybe block entities too?)
 */
public class SkillPoints {
    // Json doesn't like integer key maps
    private static final Codec<Integer> STRINT = Codec.STRING.xmap(Integer::parseInt, String::valueOf);
    public static final Codec<SkillPoints> CODEC = Codec.unboundedMap(STRINT, Codec.INT).xmap(SkillPoints::new, SkillPoints::getPointMap);
    public static final StreamCodec<ByteBuf, SkillPoints> STREAM_CODEC = ByteBufCodecs
            .map(SkillPoints::mapGetter, ByteBufCodecs.INT, ByteBufCodecs.INT)
            .map(SkillPoints::new, SkillPoints::getPointMap);

    private final Map<Integer, Integer> pointMap;
    private int totalPoints;

    public SkillPoints() {
        this.pointMap = new HashMap<>();
        this.totalPoints = 0;
    }

    private SkillPoints(Map<Integer, Integer> pointMap) {
        this.pointMap = pointMap;
        this.totalPoints = pointMap.values().stream().mapToInt(integer -> integer).sum();
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void addPoints(int id, int points) {
        totalPoints += points;
        pointMap.compute(id, (k, v) -> v == null ? points : v + points);
    }

    public int getPoints(int id) {
        return pointMap.get(id) == null ? 0 : pointMap.get(id);
    }

    public SkillPoints copy() {
        HashMap<Integer, Integer> newPointMap = new HashMap<>(pointMap);

        return new SkillPoints(newPointMap);
    }

    private Map<Integer, Integer> getPointMap() {
        return pointMap;
    }

    public static Map<Integer, Integer> mapGetter(int i) {
        return new HashMap<>(i);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SkillPoints that = (SkillPoints) o;
        return totalPoints == that.totalPoints && Objects.equals(pointMap, that.pointMap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pointMap, totalPoints);
    }
}
