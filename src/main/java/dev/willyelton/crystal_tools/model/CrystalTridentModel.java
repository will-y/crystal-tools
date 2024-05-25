package dev.willyelton.crystal_tools.model;

import net.minecraft.client.model.TridentModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CrystalTridentModel extends TridentModel {
    public CrystalTridentModel(ModelPart pRoot) {
        super(pRoot);
    }
}
