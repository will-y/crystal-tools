package dev.willyelton.crystal_tools.utils.json;

import com.google.gson.*;
import dev.willyelton.crystal_tools.tool.skills.NodeSkillDataRequirement;
import dev.willyelton.crystal_tools.tool.skills.SkillData;
import dev.willyelton.crystal_tools.tool.skills.SkillDataNode;
import dev.willyelton.crystal_tools.tool.skills.SkillDataRequirement;

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
                    // TODO: Add the not node requirement
                    boolean isNodeRequirement = requirement.getAsJsonObject().keySet().contains("node");

                    if (isNodeRequirement) {
                        JsonElement idsJson = requirement.getAsJsonObject().get("node");
                        int[] ids;
                        if (idsJson.isJsonPrimitive()) {
                            ids = new int[] {idsJson.getAsInt()};
                        } else if (idsJson.isJsonArray()) {
                            List<Integer> idsList = new ArrayList<>();
                            idsJson.getAsJsonArray().forEach(id -> {
                                idsList.add(id.getAsInt());
                            });
                            ids= idsList.stream().mapToInt(Integer::intValue).toArray();
                        } else {
                            ids = new int[] {};
                        }
                        requirementsList.add(new NodeSkillDataRequirement(ids));
                    }
                });

                tierList.add(new SkillDataNode(nodeObject.get("id").getAsInt(),
                        nodeObject.get("name").getAsString(),
                        nodeObject.get("description").getAsString(),
                        nodeObject.get("type").getAsString(),
                        0,
                        requirementsList));
            }));

            skillNodes.add(tierList);
        }));


        return new SkillData(skillNodes);
    }
}
