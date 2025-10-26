package com.hermitowo.advancedtfctech.client;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import net.dries007.tfc.client.TFCSounds.Id;

public class ATTSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(Registries.SOUND_EVENT, AdvancedTFCTech.MOD_ID);

    public static final Id THRESHER = register("thresher");
    public static final Id GRIST_MILL = register("grist_mill");
    //public static final Id POWER_LOOM = register("power_loom");
    public static final Id BEAMHOUSE = register("beamhouse");
    public static final Id FLESHING_MACHINE = register("fleshing_machine");

    private static Id register(String name)
    {
        return new Id(SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(AdvancedTFCTech.rl(name))));
    }
}
