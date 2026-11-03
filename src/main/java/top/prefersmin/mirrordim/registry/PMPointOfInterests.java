package top.prefersmin.mirrordim.registry;

import com.google.common.collect.ImmutableSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

//? if fabric {
import net.minecraft.core.Registry;
//? if >=1.20 {
import net.minecraft.core.registries.BuiltInRegistries;
//?}
//?}
//? if forge {
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
//?}
//? if neoforge {
/*import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

import top.prefersmin.mirrordim.MirrorDimension;
import top.prefersmin.mirrordim.util.MinecraftCompat;

import java.util.Set;
import java.util.function.Supplier;

public class PMPointOfInterests {

    public static final ResourceLocation CHORD_PORTAL_ID =
            MinecraftCompat.id(MirrorDimension.MODID, "chord_portal");

    //? if fabric {
    public static final Supplier<PoiType> CHORD_PORTAL = () -> Registry.register(
            //? if <1.20 {
            Registry.POINT_OF_INTEREST_TYPE,
            //?} else {
            BuiltInRegistries.POINT_OF_INTEREST_TYPE,
            //?}
            CHORD_PORTAL_ID,
            new PoiType(getBlockStates(PMBlocks.CHORD_PORTAL.get()), 0, 1)
    );

    public static void register() {
        CHORD_PORTAL.get();
    }
    //?}

    //? if forge {
    public static final DeferredRegister<PoiType> POI =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, MirrorDimension.MODID);

    public static final Supplier<PoiType> CHORD_PORTAL = POI.register(
            "chord_portal",
            () -> new PoiType(getBlockStates(PMBlocks.CHORD_PORTAL.get()), 0, 1)
    );

    public static void register(IEventBus eventBus) {
        POI.register(eventBus);
    }
    //?}

    //? if neoforge {
    /*public static final DeferredRegister<PoiType> POI =
            DeferredRegister.create(Registries.POINT_OF_INTEREST_TYPE, MirrorDimension.MODID);

    public static final Supplier<PoiType> CHORD_PORTAL = POI.register(
            "chord_portal",
            () -> new PoiType(getBlockStates(PMBlocks.CHORD_PORTAL.get()), 0, 1)
    );

    public static void register(IEventBus eventBus) {
        POI.register(eventBus);
    }
    *///?}

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

}
