package com.hermitowo.advancedtfctech.common.recipes.outputs;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;

public record CopyTagModifier(String name) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        CompoundTag inputTag = input.getTag();
        if (inputTag != null && inputTag.contains(name))
        {
            CompoundTag tag = new CompoundTag();
            tag.putBoolean(name, true);
            stack.setTag(tag);
        }
        return stack;
    }

    @Override
    public Serializer serializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements ItemStackModifier.Serializer<CopyTagModifier>
    {
        INSTANCE;

        @Override
        public CopyTagModifier fromJson(JsonObject json)
        {
            final String name = GsonHelper.getAsString(json, "tag");
            return new CopyTagModifier(name);
        }

        @Override
        public CopyTagModifier fromNetwork(FriendlyByteBuf buffer)
        {
            final String name = buffer.readUtf();
            return new CopyTagModifier(name);
        }

        @Override
        public void toNetwork(CopyTagModifier modifier, FriendlyByteBuf buffer)
        {
            buffer.writeUtf(modifier.name);
        }
    }
}
