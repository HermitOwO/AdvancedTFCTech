package com.hermitowo.advancedtfctech.common;

import net.minecraft.world.level.Level;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

public class CommonProxy
{
    public void completed(ParallelDispatchEvent event)
    {
    }


    public Level getClientWorld()
    {
        return null;
    }
}
