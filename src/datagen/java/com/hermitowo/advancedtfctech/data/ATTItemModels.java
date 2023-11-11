package com.hermitowo.advancedtfctech.data;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import javax.annotation.Nullable;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class ATTItemModels extends ModelProvider<TRSRModelBuilder>
{
    public ATTItemModels(PackOutput output, ExistingFileHelper exHelper)
    {
        super(output, AdvancedTFCTech.MOD_ID, ITEM_FOLDER, TRSRModelBuilder::new, exHelper);
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
        TRSRModelBuilder model = obj(ATTMultiblockLogic.THRESHER.blockItem().get(), "block/multiblock/thresher.obj")
            .texture("texture", modLoc("block/multiblock/thresher"));

        float scale = 0.25F;

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), scale);
        doTransform(trans, ItemDisplayContext.GUI, new Vector3f(0, -2, 0), new Vector3f(30, 45, 0), scale);
        doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(0, 1, 0), null, scale);
        doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(0, -8, 0), null, scale);
    }

    private void gristMillItem()
    {
        TRSRModelBuilder model = obj(ATTMultiblockLogic.GRIST_MILL.blockItem().get(), "block/multiblock/grist_mill.obj")
            .texture("texture", modLoc("block/multiblock/grist_mill"));

        float scale = 0.185F;

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), scale);
        doTransform(trans, ItemDisplayContext.GUI, new Vector3f(1, -1, 1), new Vector3f(30, 45, 0), scale);
        doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(0, 1, 0), null, scale);
        doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(0, -8, 0), null, scale);
    }

    private void powerLoomItem()
    {
        TRSRModelBuilder model = obj(ATTMultiblockLogic.POWER_LOOM.blockItem().get(), "block/multiblock/power_loom.obj")
            .texture("texture", modLoc("block/multiblock/power_loom"));

        float scale = 0.185F;

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), scale);
        doTransform(trans, ItemDisplayContext.GUI, new Vector3f(-1, -2, -1), new Vector3f(30, 45, 0), scale);
        doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(0, 1, 0), null, scale);
        doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(0, -8, 0), null, scale);
    }

    private void beamhouseItem()
    {
        TRSRModelBuilder model = obj(ATTMultiblockLogic.BEAMHOUSE.blockItem().get(), "block/multiblock/beamhouse.obj")
            .texture("texture", modLoc("block/multiblock/beamhouse"));

        float scale = 0.165F;

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemDisplayContext.FIRST_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.FIRST_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.THIRD_PERSON_LEFT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.THIRD_PERSON_RIGHT_HAND, null, null, scale);
        doTransform(trans, ItemDisplayContext.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), scale);
        doTransform(trans, ItemDisplayContext.GUI, new Vector3f(0, -1, 0), new Vector3f(30, 225, 0), scale);
        doTransform(trans, ItemDisplayContext.GROUND, new Vector3f(0, 1, 0), null, scale);
        doTransform(trans, ItemDisplayContext.FIXED, new Vector3f(0, -8, 0), null, scale);
    }

    private void fleshingMachineItem()
    {
        obj(ATTBlocks.FLESHING_MACHINE.get(), "block/metal_device/fleshing_machine.obj")
            .transforms(modLoc("item/fleshing_machine"));
    }

    private TRSRModelBuilder obj(ItemLike item, String model)
    {
        return getBuilder(ForgeRegistries.ITEMS.getKey(item.asItem()).getPath())
            .customLoader(ObjModelBuilder::begin)
            .modelLocation(modLoc("models/" + model)).flipV(true).end();
    }

    private void doTransform(ModelBuilder<?>.TransformsBuilder transform, ItemDisplayContext type, @Nullable Vector3f translation, @Nullable Vector3f rotationAngle, float scale)
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
