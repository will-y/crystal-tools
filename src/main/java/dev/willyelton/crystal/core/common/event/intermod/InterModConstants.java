package dev.willyelton.crystal.core.common.event.intermod;

import dev.willyelton.crystal.core.common.levelable.condition.LevelableCondition;

public class InterModConstants {
    private InterModConstants() {}

    /**
     * This method will create a new levelable condition using {@link net.neoforged.fml.InterModComms#sendTo}
     * <br/>
     * The object you should send should extend {@link LevelableCondition}
     */
    public static final String CREATE_CONDITION = "create_condition";
    public static final String REGISTER_SKILL_BLACKLIST = "register_skill_blacklist";
}
