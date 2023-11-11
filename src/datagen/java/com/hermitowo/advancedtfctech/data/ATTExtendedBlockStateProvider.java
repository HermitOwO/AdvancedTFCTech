package com.hermitowo.advancedtfctech.data;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import blusunrize.immersiveengineering.api.multiblocks.TemplateMultiblock;
import blusunrize.immersiveengineering.data.DataGenUtils;
import blusunrize.immersiveengineering.data.models.MirroredModelBuilder;
import blusunrize.immersiveengineering.data.models.ModelProviderUtils;
import blusunrize.immersiveengineering.data.models.NongeneratedModels;
import blusunrize.immersiveengineering.data.models.SplitModelBuilder;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableMap;
import com.hermitowo.advancedtfctech.AdvancedTFCTech;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.client.model.generators.ModelProvider;
import net.minecraftforge.client.model.generators.loaders.ObjModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

/**
 * Methods from
 * {@link blusunrize.immersiveengineering.data.blockstates.ExtendedBlockstateProvider} and
 * {@link blusunrize.immersiveengineering.data.blockstates.MultiblockStates}
 */
@SuppressWarnings({"unused", "SameParameterValue", "deprecation"})
public abstract class ATTExtendedBlockStateProvider extends BlockStateProvider
{
    protected static final Map<ResourceLocation, String> generatedParticleTextures = new HashMap<>();
    protected final ExistingFileHelper existingFileHelper;
    protected final NongeneratedModels innerModels;

    public ATTExtendedBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper)
    {
        super(output, AdvancedTFCTech.MOD_ID, exFileHelper);
        this.existingFileHelper = exFileHelper;
        this.innerModels = new NongeneratedModels(output, existingFileHelper);
    }

    protected int getAngle(Direction dir, int offset)
    {
        return (int) ((dir.toYRot() + offset) % 360);
    }

    protected ModelFile split(NongeneratedModels.NongeneratedModel loc, TemplateMultiblock mb)
    {
        return split(loc, mb, false);
    }

    protected ModelFile split(NongeneratedModels.NongeneratedModel loc, TemplateMultiblock mb, boolean mirror)
    {
        return split(loc, mb, mirror, false);
    }

    protected ModelFile split(NongeneratedModels.NongeneratedModel loc, TemplateMultiblock mb, boolean mirror, boolean dynamic)
    {
        UnaryOperator<BlockPos> transform = UnaryOperator.identity();
        if (mirror)
        {
            loadTemplateFor(mb);
            Vec3i size = mb.getSize(null);
            transform = p -> new BlockPos(size.getX() - p.getX() - 1, p.getY(), p.getZ());
        }
        return split(loc, mb, transform, dynamic);
    }

    protected ModelFile split(
        NongeneratedModels.NongeneratedModel name, TemplateMultiblock multiblock, UnaryOperator<BlockPos> transform, boolean dynamic
    )
    {
        loadTemplateFor(multiblock);
        final Vec3i offset = multiblock.getMasterFromOriginOffset();
        Stream<Vec3i> partsStream = multiblock.getTemplate(null).blocksWithoutAir()
            .stream()
            .map(info -> info.pos())
            .map(transform)
            .map(p -> p.subtract(offset));
        return split(name, partsStream.collect(Collectors.toList()), dynamic);
    }

    protected <T extends ModelBuilder<T>> T mirror(NongeneratedModels.NongeneratedModel inner, ModelProvider<T> provider)
    {
        return provider.getBuilder(inner.getLocation().getPath() + "_mirrored")
            .customLoader(MirroredModelBuilder::begin)
            .inner(inner)
            .end();
    }

    protected void loadTemplateFor(TemplateMultiblock multiblock)
    {
        final ResourceLocation name = multiblock.getUniqueName();
        if (TemplateMultiblock.SYNCED_CLIENT_TEMPLATES.containsKey(name))
            return;
        final String filePath = "structures/" + name.getPath() + ".nbt";
        int slash = filePath.indexOf('/');
        String prefix = filePath.substring(0, slash);
        ResourceLocation shortLoc = new ResourceLocation(
            name.getNamespace(),
            filePath.substring(slash + 1)
        );
        try
        {
            final Resource resource = existingFileHelper.getResource(shortLoc, PackType.SERVER_DATA, "", prefix);
            try (final InputStream input = resource.open())
            {
                final CompoundTag nbt = NbtIo.readCompressed(input);
                final StructureTemplate template = new StructureTemplate();
                template.load(BuiltInRegistries.BLOCK.asLookup(), nbt);
                TemplateMultiblock.SYNCED_CLIENT_TEMPLATES.put(name, template);
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException("Failed on " + name, e);
        }
    }

    protected void setRenderType(@Nullable RenderType type, ModelBuilder<?>... builders)
    {
        if (type != null)
        {
            final String typeName = ModelProviderUtils.getName(type);
            for (final ModelBuilder<?> model : builders)
                model.renderType(typeName);
        }
    }

    protected NongeneratedModels.NongeneratedModel innerObj(String loc, @Nullable RenderType layer)
    {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        final var result = obj(loc.substring(0, loc.length() - 4), modLoc(loc), innerModels);
        setRenderType(layer, result);
        return result;
    }

    protected NongeneratedModels.NongeneratedModel innerObj(String loc)
    {
        return innerObj(loc, null);
    }

    protected BlockModelBuilder obj(String loc)
    {
        return obj(loc, (RenderType) null);
    }

    protected BlockModelBuilder obj(String loc, @Nullable RenderType layer)
    {
        final var model = obj(loc, models());
        setRenderType(layer, model);
        return model;
    }

    protected <T extends ModelBuilder<T>>
    T obj(String loc, ModelProvider<T> modelProvider)
    {
        Preconditions.checkArgument(loc.endsWith(".obj"));
        return obj(loc.substring(0, loc.length() - 4), modLoc(loc), modelProvider);
    }

    protected <T extends ModelBuilder<T>>
    T obj(String name, ResourceLocation model, ModelProvider<T> provider)
    {
        return obj(name, model, ImmutableMap.of(), provider);
    }

    protected <T extends ModelBuilder<T>>
    T obj(String name, ResourceLocation model, Map<String, ResourceLocation> textures, ModelProvider<T> provider)
    {
        return obj(provider.withExistingParent(name, mcLoc("block")), model, textures);
    }

    protected <T extends ModelBuilder<T>>
    T obj(T base, ResourceLocation model, Map<String, ResourceLocation> textures)
    {
        assertModelExists(model);
        T ret = base
            .customLoader(ObjModelBuilder::begin)
            .automaticCulling(false)
            .modelLocation(addModelsPrefix(model))
            .flipV(true)
            .end();
        String particleTex = DataGenUtils.getTextureFromObj(model, existingFileHelper);
        if (particleTex.charAt(0) == '#')
            particleTex = textures.get(particleTex.substring(1)).toString();
        ret.texture("particle", particleTex);
        generatedParticleTextures.put(ret.getLocation(), particleTex);
        for (Map.Entry<String, ResourceLocation> e : textures.entrySet())
            ret.texture(e.getKey(), e.getValue());
        return ret;
    }

    protected BlockModelBuilder splitModel(String name, NongeneratedModels.NongeneratedModel model, List<Vec3i> parts, boolean dynamic)
    {
        BlockModelBuilder result = models().withExistingParent(name, mcLoc("block"))
            .customLoader(SplitModelBuilder::begin)
            .innerModel(model)
            .parts(parts)
            .dynamic(dynamic)
            .end();
        addParticleTextureFrom(result, model);
        return result;
    }

    protected ModelFile split(NongeneratedModels.NongeneratedModel baseModel, List<Vec3i> parts, boolean dynamic)
    {
        return splitModel(baseModel.getLocation().getPath() + "_split", baseModel, parts, dynamic);
    }

    protected ModelFile split(NongeneratedModels.NongeneratedModel baseModel, List<Vec3i> parts)
    {
        return split(baseModel, parts, false);
    }

    protected ModelFile splitDynamic(NongeneratedModels.NongeneratedModel baseModel, List<Vec3i> parts)
    {
        return split(baseModel, parts, true);
    }

    protected void addParticleTextureFrom(BlockModelBuilder result, ModelFile model)
    {
        String particles = generatedParticleTextures.get(model.getLocation());
        if (particles != null)
        {
            result.texture("particle", particles);
            generatedParticleTextures.put(result.getLocation(), particles);
        }
    }

    protected void assertModelExists(ResourceLocation name)
    {
        String suffix = name.getPath().contains(".") ? "" : ".json";
        Preconditions.checkState(
            existingFileHelper.exists(name, PackType.CLIENT_RESOURCES, suffix, "models"),
            "Model \"" + name + "\" does not exist");
    }

    protected ResourceLocation addModelsPrefix(ResourceLocation in)
    {
        return new ResourceLocation(in.getNamespace(), "models/" + in.getPath());
    }
}
