package com.hermitowo.advancedtfctech.common.recipes.outputs;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;

public record DoubleIfHasTagModifier(String name) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        CompoundTag inputTag = input.getTag();
        if (inputTag != null && inputTag.contains(name))
            stack.grow(stack.getCount());
        return stack;
    }

    @Override
    public Serializer serializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements ItemStackModifier.Serializer<DoubleIfHasTagModifier>
    {
        INSTANCE;

        @Override
        public DoubleIfHasTagModifier fromJson(JsonObject json)
        {
            final String name = GsonHelper.getAsString(json, "tag");
            return new DoubleIfHasTagModifier(name);
        }

        @Override
        public DoubleIfHasTagModifier fromNetwork(FriendlyByteBuf buffer)
        {
            final String name = buffer.readUtf();
            return new DoubleIfHasTagModifier(name);
        }

        @Override
        public void toNetwork(DoubleIfHasTagModifier modifier, FriendlyByteBuf buffer)
        {
            buffer.writeUtf(modifier.name);
        }
    }
}
