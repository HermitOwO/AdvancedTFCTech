package com.hermitowo.advancedtfctech.client;

import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ATTSounds
{
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, AdvancedTFCTech.MOD_ID);

    public static final RegistryObject<SoundEvent> THRESHER = create("thresher");
    public static final RegistryObject<SoundEvent> GRIST_MILL = create("grist_mill");
    //public static final RegistryObject<SoundEvent> POWER_LOOM = create("power_loom");
    public static final RegistryObject<SoundEvent> BEAMHOUSE = create("beamhouse");
    public static final RegistryObject<SoundEvent> FLESHING_MACHINE = create("fleshing_machine");

    private static RegistryObject<SoundEvent> create(String name)
    {
        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(AdvancedTFCTech.rl(name)));
    }
}
