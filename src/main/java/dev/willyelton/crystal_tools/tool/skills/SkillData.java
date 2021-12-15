package dev.willyelton.crystal_tools.tool.skills;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.willyelton.crystal_tools.utils.ListUtils;
import dev.willyelton.crystal_tools.utils.json.SkillDataDeserializer;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


/**
 * Can be created from a string tool type (later resource location) and an int array? of what is upgraded (id -> points in node)
 */
public class SkillData {
    public static SkillData fromResourceLocation(ResourceLocation resourceLocation, int[] skills) {
        GsonBuilder gsonBuilder = new GsonBuilder();

        gsonBuilder.registerTypeAdapter(SkillData.class, new SkillDataDeserializer());

        Gson gson = gsonBuilder.create();

        try {
            InputStream in = Minecraft.getInstance().getResourceManager().getResource(resourceLocation).getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

            SkillData result = gson.fromJson(reader, SkillData.class);

            result.applyPoints(skills);

            return result;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    private final List<List<SkillDataNode>> nodes;
    // Flattened nodes, calculated lazily
    private List<SkillDataNode> flatNodes = null;

    public SkillData(List<List<SkillDataNode>> nodes) {
        this.nodes = nodes;
    }

    public void applyPoints(int[] points) {
        List<SkillDataNode> nodes = getAllNodes();

        for (int i = 0; i < nodes.size(); i++) {
            nodes.get(i).setPoints(points[i]);
        }
    }

    public List<List<SkillDataNode>> getAllNodesByTier() {
        return this.nodes;
    }

    public List<SkillDataNode> getAllNodes() {
        if (flatNodes == null) {
            flatNodes = ListUtils.flattenList(nodes);
        }

        return flatNodes;
    }

}
