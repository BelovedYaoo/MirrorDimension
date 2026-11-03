package top.prefersmin.mirrordim.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import top.prefersmin.mirrordim.util.MinecraftCompat;

public class PMDimensions {

    private static final ResourceKey<Registry<Level>> DIMENSION_REGISTRY =
            ResourceKey.createRegistryKey(MinecraftCompat.id("minecraft", "dimension"));

    public static final ResourceKey<Level> CHORD_LEVEL = ResourceKey.create(DIMENSION_REGISTRY, name("chord"));

    private static ResourceLocation name(String name) {
        return MinecraftCompat.id("prefersmin", name);
    }

}
