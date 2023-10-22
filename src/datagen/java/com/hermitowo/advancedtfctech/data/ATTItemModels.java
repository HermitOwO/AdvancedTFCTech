package com.hermitowo.advancedtfctech.data;

import java.util.Objects;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.mojang.math.Vector3f;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.loaders.OBJLoaderBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTItemModels extends ModelProvider<TRSRModelBuilder>
{
    public ATTItemModels(DataGenerator gen, ExistingFileHelper exHelper)
    {
        super(gen, MOD_ID, ITEM_FOLDER, TRSRModelBuilder::new, exHelper);
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Item Models";
    }

    @Override
    protected void registerModels()
    {
        thresherItem();
        gristMillItem();
        powerLoomItem();
        beamhouseItem();
        fleshingMachineItem();
    }

    private void thresherItem()
    {
        TRSRModelBuilder model = obj(ATTBlocks.Multiblocks.THRESHER.get(), "multiblock/thresher.obj")
            .texture("texture", modLoc("multiblock/thresher"));

        float scale = 0.25F;

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), scale);
        doTransform(trans, ItemTransforms.TransformType.GUI, new Vector3f(0, -2, 0), new Vector3f(30, 45, 0), scale);
        doTransform(trans, ItemTransforms.TransformType.GROUND, new Vector3f(0, 1, 0), null, scale);
        doTransform(trans, ItemTransforms.TransformType.FIXED, new Vector3f(0, -8, 0), null, scale);
    }

    private void gristMillItem()
    {
        TRSRModelBuilder model = obj(ATTBlocks.Multiblocks.GRIST_MILL.get(), "multiblock/grist_mill.obj")
            .texture("texture", modLoc("multiblock/grist_mill"));

        float scale = 0.185F;

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), scale);
        doTransform(trans, ItemTransforms.TransformType.GUI, new Vector3f(-1, -2, -1), new Vector3f(30, 45, 0), scale);
        doTransform(trans, ItemTransforms.TransformType.GROUND, new Vector3f(0, 1, 0), null, scale);
        doTransform(trans, ItemTransforms.TransformType.FIXED, new Vector3f(0, -8, 0), null, scale);
    }

    private void powerLoomItem()
    {
        TRSRModelBuilder model = obj(ATTBlocks.Multiblocks.POWER_LOOM.get(), "multiblock/power_loom.obj")
            .texture("texture", modLoc("multiblock/power_loom"));

        float scale = 0.185F;

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), scale);
        doTransform(trans, ItemTransforms.TransformType.GUI, new Vector3f(-1, -2, -1), new Vector3f(30, 45, 0), scale);
        doTransform(trans, ItemTransforms.TransformType.GROUND, new Vector3f(0, 1, 0), null, scale);
        doTransform(trans, ItemTransforms.TransformType.FIXED, new Vector3f(0, -8, 0), null, scale);
    }

    private void beamhouseItem()
    {
        TRSRModelBuilder model = obj(ATTBlocks.Multiblocks.BEAMHOUSE.get(), "multiblock/beamhouse.obj")
            .texture("texture", modLoc("multiblock/beamhouse"));

        float scale = 0.165F;

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemTransforms.TransformType.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), scale);
        doTransform(trans, ItemTransforms.TransformType.GUI, new Vector3f(2, -2, 2), new Vector3f(30, 225, 0), scale);
        doTransform(trans, ItemTransforms.TransformType.GROUND, new Vector3f(0, 1, 0), null, scale);
        doTransform(trans, ItemTransforms.TransformType.FIXED, new Vector3f(0, -8, 0), null, scale);
    }

    private void fleshingMachineItem()
    {
        obj(ATTBlocks.Blocks.FLESHING_MACHINE.get(), "metal_device/fleshing_machine.obj")
            .texture("texture", modLoc("metal_device/fleshing_machine"))
            .transforms(modLoc("item/fleshing_machine"));
    }

    private TRSRModelBuilder obj(ItemLike item, String model)
    {
        return getBuilder(Objects.requireNonNull(item.asItem().getRegistryName()).toString())
            .customLoader(OBJLoaderBuilder::begin)
            .modelLocation(modLoc("models/" + model)).flipV(true).end();
    }

    private void doTransform(ModelBuilder<?>.TransformsBuilder transform, ItemTransforms.TransformType type, @Nullable Vector3f translation, @Nullable Vector3f rotationAngle, float scale)
    {
        ModelBuilder<?>.TransformsBuilder.TransformVecBuilder trans = transform.transform(type);
        if (translation != null)
            trans.translation(translation.x(), translation.y(), translation.z());
        if (rotationAngle != null)
            trans.rotation(rotationAngle.x(), rotationAngle.y(), rotationAngle.z());
        trans.scale(scale);
        trans.end();
    }
}
