package com.hermitowo.advancedtfctech.common.recipes.outputs;

import com.google.gson.JsonObject;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.recipes.outputs.ItemStackModifier;

public record AddTagModifier(String name) implements ItemStackModifier
{
    @Override
    public ItemStack apply(ItemStack stack, ItemStack input)
    {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean(name, true);
        stack.setTag(tag);
        return stack;
    }

    @Override
    public Serializer serializer()
    {
        return Serializer.INSTANCE;
    }

    public enum Serializer implements ItemStackModifier.Serializer<AddTagModifier>
    {
        INSTANCE;

        @Override
        public AddTagModifier fromJson(JsonObject json)
        {
            final String name = GsonHelper.getAsString(json, "tag");
            return new AddTagModifier(name);
        }

        @Override
        public AddTagModifier fromNetwork(FriendlyByteBuf buffer)
        {
            final String name = buffer.readUtf();
            return new AddTagModifier(name);
        }

        @Override
        public void toNetwork(AddTagModifier modifier, FriendlyByteBuf buffer)
        {
            buffer.writeUtf(modifier.name);
        }
    }
}
