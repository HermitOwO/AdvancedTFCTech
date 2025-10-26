package com.hermitowo.advancedtfctech.data;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import com.hermitowo.advancedtfctech.data.blockstates.ATTBlockStates;
import com.hermitowo.advancedtfctech.data.models.ATTItemModels;
import com.hermitowo.advancedtfctech.data.models.DynamicModels;
import com.hermitowo.advancedtfctech.data.providers.ATTItemSizes;
import com.hermitowo.advancedtfctech.data.recipes.ATTRecipes;
import com.hermitowo.advancedtfctech.data.tags.ATTBlockTags;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@EventBusSubscriber(modid = AdvancedTFCTech.MOD_ID)
public class ATTDataGenerator
{
    @SubscribeEvent
    public static void generate(GatherDataEvent event)
    {
        ExistingFileHelper exFileHelper = event.getExistingFileHelper();
        DataGenerator gen = event.getGenerator();
        final PackOutput output = gen.getPackOutput();
        final var lookup = event.getLookupProvider();

        if (event.includeServer())
        {
            gen.addProvider(true, new ATTBlockTags(output, lookup, exFileHelper));
            gen.addProvider(true, new ATTRecipes(output, lookup));

            gen.addProvider(true, new ATTItemSizes(output, lookup));

            ATTBlockStates multiblocks = new ATTBlockStates(output, exFileHelper);
            gen.addProvider(true, multiblocks);
            gen.addProvider(true, new ATTItemModels(output, exFileHelper));
            gen.addProvider(true, new DynamicModels(multiblocks, output, exFileHelper));
        }
    }
}
