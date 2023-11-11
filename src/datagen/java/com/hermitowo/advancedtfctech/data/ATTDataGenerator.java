package com.hermitowo.advancedtfctech.data;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = AdvancedTFCTech.MOD_ID, bus = Bus.MOD)
public class ATTDataGenerator
{
    @SubscribeEvent
    public static void generate(GatherDataEvent event)
    {
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();
        DataGenerator gen = event.getGenerator();
        final PackOutput output = gen.getPackOutput();

        if (event.includeServer())
        {
            ATTBlockStates multiblocks = new ATTBlockStates(output, exFileHelper);
            gen.addProvider(true, multiblocks);
            gen.addProvider(true, new ATTItemModels(output, exFileHelper));
            gen.addProvider(true, new DynamicModels(multiblocks, output, exFileHelper));
        }
    }
}
