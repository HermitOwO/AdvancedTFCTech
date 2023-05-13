package com.hermitowo.advancedtfctech.client.model;

import java.util.function.Function;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

public abstract class ATTModel extends Model
{
    public ATTModel(Function<ResourceLocation, RenderType> renderTypeIn){
        super(renderTypeIn);
    }

    public abstract void init();
}
