package dev.willyelton.crystal_tools.utils.json;

import com.google.gson.*;
import dev.willyelton.crystal_tools.item.skill.SkillData;
import dev.willyelton.crystal_tools.item.skill.SkillDataNode;
import dev.willyelton.crystal_tools.item.skill.SkillNodeType;
import dev.willyelton.crystal_tools.item.skill.requirement.NodeOrSkillDataRequirement;
import dev.willyelton.crystal_tools.item.skill.requirement.NodeSkillDataRequirement;
import dev.willyelton.crystal_tools.item.skill.requirement.SkillDataRequirement;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SkillDataDeserializer implements JsonDeserializer<SkillData> {

    @Override
    public SkillData deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        List<List<SkillDataNode>> skillNodes = new ArrayList<>();
        JsonObject rootElement = json.getAsJsonObject();
        JsonArray tiers = rootElement.getAsJsonArray("tiers");
        tiers.forEach((tier -> {
            List<SkillDataNode> tierList = new ArrayList<>();
            tier.getAsJsonArray().forEach((node -> {
                JsonObject nodeObject = node.getAsJsonObject();
                JsonArray requirements = nodeObject.getAsJsonArray("requirements");
                List<SkillDataRequirement> requirementsList = new ArrayList<>();
                requirements.forEach(requirement -> {
                    JsonObject requirementObject = requirement.getAsJsonObject();
                    JsonElement jsonElement;
                    if (requirementObject.keySet().contains("node")) {
                        jsonElement = requirementObject.get("node");
                        int[] ids = this.getNodesFromJson(jsonElement);
                        requirementsList.add(new NodeSkillDataRequirement(ids));
                    } else if (requirementObject.keySet().contains("not_node")) {
                        jsonElement = requirementObject.get("not_node");
                        int[] ids = this.getNodesFromJson(jsonElement);
                        requirementsList.add(new NodeSkillDataRequirement(ids, true));
                    } else if (requirementObject.keySet().contains("or_node")) {
                        jsonElement = requirementObject.get("or_node");
                        int[] ids = this.getNodesFromJson(jsonElement);
                        requirementsList.add(new NodeOrSkillDataRequirement(ids));
                    }
                });

                tierList.add(new SkillDataNode(nodeObject.get("id").getAsInt(),
                        nodeObject.get("name").getAsString(),
                        nodeObject.get("description").getAsString(),
                        SkillNodeType.fromString(nodeObject.get("type").getAsString()),
                        0,
                        nodeObject.get("key").getAsString(),
                        nodeObject.get("value").getAsFloat(),
                        requirementsList));
            }));

            skillNodes.add(tierList);
        }));


        return new SkillData(skillNodes);
    }

    private int[] getNodesFromJson(JsonElement jsonElement) {
        int[] ids;
        if (jsonElement.isJsonPrimitive()) {
            ids = new int[] {jsonElement.getAsInt()};
        } else if (jsonElement.isJsonArray()) {
            List<Integer> idsList = new ArrayList<>();
            jsonElement.getAsJsonArray().forEach(id -> idsList.add(id.getAsInt()));
            ids = idsList.stream().mapToInt(Integer::intValue).toArray();
        } else {
            ids = new int[] {};
        }

        return ids;
    }
}
