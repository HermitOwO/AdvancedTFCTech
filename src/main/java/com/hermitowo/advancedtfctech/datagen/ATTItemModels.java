package com.hermitowo.advancedtfctech.datagen;

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

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

public class ATTItemModels extends ModelProvider<TRSRModelBuilder>
{
    public ATTItemModels(DataGenerator gen, ExistingFileHelper exHelper)
    {
        super(gen, MOD_ID, ITEM_FOLDER, TRSRModelBuilder::new, exHelper);
    }

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
    }

    private void thresherItem()
    {
        TRSRModelBuilder model = obj(ATTBlocks.Multiblocks.THRESHER.get(), "multiblock/obj/thresher.obj")
            .texture("texture", modLoc("multiblock/thresher"));

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, null, null, 0.25F);
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, null, null, 0.25F);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, null, null, 0.25F);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, null, null, 0.25F);
        doTransform(trans, ItemTransforms.TransformType.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), 0.25F);
        doTransform(trans, ItemTransforms.TransformType.GUI, new Vector3f(0, -2, 0), new Vector3f(30, 45, 0), 0.25F);
        doTransform(trans, ItemTransforms.TransformType.GROUND, new Vector3f(0, 1, 0), null, 0.25F);
        doTransform(trans, ItemTransforms.TransformType.FIXED, new Vector3f(0, -8, 0), null, 0.25F);
    }

    private void gristMillItem()
    {
        TRSRModelBuilder model = obj(ATTBlocks.Multiblocks.GRIST_MILL.get(), "multiblock/obj/grist_mill.obj")
            .texture("texture", modLoc("multiblock/grist_mill"));

        ModelBuilder<?>.TransformsBuilder trans = model.transforms();
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, null, null, 0.185F);
        doTransform(trans, ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, null, null, 0.185F);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND, null, null, 0.185F);
        doTransform(trans, ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, null, null, 0.185F);
        doTransform(trans, ItemTransforms.TransformType.HEAD, new Vector3f(0, 7.6F, 0), new Vector3f(0, 180, 0), 0.185F);
        doTransform(trans, ItemTransforms.TransformType.GUI, new Vector3f(0, -2, 0), new Vector3f(30, 45, 0), 0.185F);
        doTransform(trans, ItemTransforms.TransformType.GROUND, new Vector3f(0, 1, 0), null, 0.185F);
        doTransform(trans, ItemTransforms.TransformType.FIXED, new Vector3f(0, -8, 0), null, 0.185F);
    }

    private TRSRModelBuilder obj(ItemLike item, String model)
    {
        return getBuilder(item.asItem().getRegistryName().toString())
            .customLoader(OBJLoaderBuilder::begin)
            .modelLocation(modLoc("models/" + model)).flipV(true).end();
    }

    private void doTransform(ModelBuilder<?>.TransformsBuilder transform, ItemTransforms.TransformType type, @Nullable Vector3f translation, @Nullable Vector3f rotationAngle, float scale)
    {
        ModelBuilder<?>.TransformsBuilder.TransformVecBuilder trans = transform.transform(type);
        if(translation != null)
            trans.translation(translation.x(), translation.y(), translation.z());
        if(rotationAngle != null)
            trans.rotation(rotationAngle.x(), rotationAngle.y(), rotationAngle.z());
        trans.scale(scale);
        trans.end();
    }
}
