package dev.willyelton.crystal_tools.utils.constants;

public class SkillTreeDescriptions {
    private final String toolName;

    public SkillTreeDescriptions(String toolName) {
        this.toolName = toolName;
    }

    public String miningSpeed() {
        return String.format("Increases the %s's Mining Speed", toolName);
    }

    public String attackDamage() {
        return String.format("Increases the %s's Attack Damage", toolName);
    }

    public String durability() {
        return String.format("Increases the %s's Durability", toolName);
    }

    public String unbreaking() {
        return "Adds 10% to the chance not use durability";
    }

    public String reach() {
        return String.format("Increases the %s's Reach Distance", toolName);
    }

    public String silkTouch() {
        return String.format("Gives Silk Touch to the %s", toolName);
    }

    public String fortune() {
        return String.format("Gives Fortune III to the %s", toolName);
    }

    public String autoPickup() {
        return String.format("When you mine block with this %s, the drops will automatically go into your inventory if there is space", toolName);
    }

    public String autoRepair() {
        return String.format("Automatically repairs the %s over time", toolName);
    }

    public String mining3x3() {
        return String.format("Allows the %s to mine in a 3x3 area", toolName);
    }

    public String veinMining(boolean axe) {
        if (axe) {
            return "Hold the vein miner key to chop or strip an entire tree!";
        } else {
            return "Mines all ores in a vein when pressing the vein mining key";
        }
    }

    public String autoSmelting() {
        return "Automatically smelts the blocks that you mine";
    }

    public String torch() {
        return "Right-Click to place a torch";
    }

    public String mineMode() {
        return "Press the mode_switch key to change the mining mode between unlocked modes (Silk Touch or Fortune). Press shift + mode_switch to change the breaking mode (3x3 or 1x1)";
    }

    public String leafMiner() {
        return String.format("The %s now efficiently mines leaves and other similar blocks", toolName);
    }

    public String nutrition() {
        return String.format("Increases the number of food points the %s restores", toolName);
    }

    public String saturation() {
        return String.format("Increases the saturation of the %s", toolName);
    }

    public String eatSpeed() {
        return String.format("Increases how fast you can eat the %s", toolName);
    }

    public String alwaysEat() {
        return String.format("You can eat the %s even when you are full", toolName);
    }

    public String effect(String effectName, int duration) {
        if (toolName.equalsIgnoreCase("apple")) {
            return String.format("Adds the %s effect for %d ticks when you eat the %s", effectName, duration, toolName);
        } else if (toolName.equalsIgnoreCase("bow")) {
            return String.format("Adds the %s effect to shot arrows", effectName);
        } else if (toolName.equalsIgnoreCase("shield")) {
            return String.format("Mobs blocked by this shield will get the %s effect", effectName);
        } else {
            return "TODO: Wrong Description";
        }
    }

    public String enchantment(String enchantmentName) {
        return String.format("Gives the enchantment %s to the %s", enchantmentName, toolName);
    }

    public String baseArmor() {
        return String.format("Gives the %s more base armor", toolName);
    }

    public String toughness() {
        return String.format("Gives the %s more armor toughness", toolName);
    }

    public String nightVision() {
        return String.format("Gives you night vision while wearing the %s", toolName);
    }

    public String healthBonus() {
        return String.format("Gives you bonus health while wearing the %s", toolName);
    }

    public String moveSpeed() {
        return String.format("Gives you bonus speed while wearing the %s", toolName);
    }

    public String creativeFlight() {
        return "Gives you Creative Flight!";
    }

    public String arrowDamage() {
        return String.format("Arrows shot from the %s will do more damage", toolName);
    }

    public String arrowSpeed() {
        return String.format("Arrows shot from the %s will have higher speed", toolName);
    }

    public String drawSpeed() {
        return String.format("The %s will shoot arrows faster", toolName);
    }

    public String autoTarget(boolean shield) {
        if (shield) {
            return "Reflected projectiles will auto-target mob";
        }

        return "Fired projectiles automatically track targeted mobs";
    }

    public String flightDuration() {
        return "Increases flight time";
    }

    public String doubleItems() {
        return String.format("Adds a chance to double drops from the %s", toolName);
    }

    public String thorns() {
        return String.format("Mobs blocked by this %s will take damage", toolName);
    }

    public String shieldCooldown() {
        return String.format("Reduces the %s cooldown when it gets disabled", toolName);
    }

    public String flamingShield() {
        return "Mobs blocked by this shield will catch on fire";
    }

    public String totemSlot() {
        return String.format("Adds 1 slot for a Totem of Undying to the %s", toolName);
    }

    public String shieldKnockback() {
        return String.format("Mobs blocked by this %s will be knocked back", toolName);
    }
}
