package com.hermitowo.advancedtfctech.datagen;

import blusunrize.immersiveengineering.common.blocks.multiblocks.StaticTemplateManager;
import blusunrize.immersiveengineering.data.blockstates.MultiblockStates;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

@Mod.EventBusSubscriber(modid = MOD_ID, bus = Bus.MOD)
public class ATTDataGenerator
{
    @SubscribeEvent
    public static void generate(GatherDataEvent event)
    {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();
        StaticTemplateManager.EXISTING_HELPER = exFileHelper;

        if(event.includeClient())
        {
            MultiblockStates multiblocks = new MultiblockStates(gen, exFileHelper);
            gen.addProvider(new ATTBlockStates(gen, exFileHelper));
            gen.addProvider(new ATTItemModels(gen, exFileHelper));
            gen.addProvider(new DynamicModels(multiblocks, gen, exFileHelper));
        }
    }
}
