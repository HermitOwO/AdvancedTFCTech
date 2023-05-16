package com.hermitowo.advancedtfctech.client.model;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

import static com.hermitowo.advancedtfctech.AdvancedTFCTech.*;

@Mod.EventBusSubscriber(modid = MOD_ID, value = Dist.CLIENT, bus = Bus.MOD)
public class ATTModels
{
    private static final Map<String, ATTModel> MODELS = new HashMap<>();

    public static void add(String id, ATTModel model)
    {
        if (MODELS.containsKey(id))
        {
            LOGGER.error("Duplicate ID, \"{}\" already used by {}. Skipping.", id, MODELS.get(id).getClass());
        }
        else
        {
            MODELS.put(id, model);
        }
    }

    public static Supplier<ATTModel> getSupplier(String id)
    {
        return () -> MODELS.get(id);
    }

    public static Collection<ATTModel> getModels()
    {
        return Collections.unmodifiableCollection(MODELS.values());
    }
}
