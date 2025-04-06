package dev.willyelton.crystal_tools.utils.constants;

public class SkillTreeDescriptions {
    private final String toolName;

    public SkillTreeDescriptions(String toolName) {
        this.toolName = toolName;
    }

    public String miningSpeed() {
        return String.format("Increases the %s's Mining Speed", toolName);
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

    public String veinMining() {
        return "Mines all ores in a vein when pressing the vein mining key";
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
}
