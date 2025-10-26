package com.hermitowo.advancedtfctech.data.tags;

import java.util.concurrent.CompletableFuture;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.common.blocks.ATTBlocks;
import com.hermitowo.advancedtfctech.common.multiblocks.logic.ATTMultiblockLogic;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;

public class ATTBlockTags extends BlockTagsProvider
{
    public ATTBlockTags(PackOutput output, CompletableFuture<HolderLookup.Provider> provider, ExistingFileHelper existingFileHelper)
    {
        super(output, provider, AdvancedTFCTech.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(@NotNull HolderLookup.Provider provider)
    {
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ATTMultiblockLogic.THRESHER.block().get())
            .add(ATTMultiblockLogic.GRIST_MILL.block().get())
            .add(ATTMultiblockLogic.POWER_LOOM.block().get())
            .add(ATTMultiblockLogic.BEAMHOUSE.block().get())
            .add(ATTBlocks.FLESHING_MACHINE.get());
    }
}
