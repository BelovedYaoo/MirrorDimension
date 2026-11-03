package top.prefersmin.mirrordim;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

//? if fabric {
import net.fabricmc.api.ModInitializer;
//?}
//? if forge {
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
//?}
//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
*///?}

import top.prefersmin.mirrordim.registry.PMBlocks;
import top.prefersmin.mirrordim.registry.PMPointOfInterests;

//? if fabric {
public class MirrorDimension implements ModInitializer {
//?}
//? if forge {
@Mod(MirrorDimension.MODID)
public class MirrorDimension {
//?}
//? if neoforge {
/*@Mod(MirrorDimension.MODID)
public class MirrorDimension {
*///?}

    public static final String MODID = "mirrordim";

    // 传送门框架，拿鸡蛋右键激活哦
    public static final Block frameBlock = Blocks.SCULK;

    //? if fabric {
    @Override
    public void onInitialize() {
        PMBlocks.register();
        PMPointOfInterests.register();
    }
    //?}

    //? if neoforge {
    /*public MirrorDimension(IEventBus modEventBus) {
        PMBlocks.BLOCKS.register(modEventBus);
        PMPointOfInterests.register(modEventBus);
        net.neoforged.neoforge.common.NeoForge.EVENT_BUS.register(top.prefersmin.mirrordim.event.PMEvents.class);
        if (ModList.get().isLoaded("create")) {
            //? if >=1.21.1 {
            top.prefersmin.mirrordim.compat.CreateCompatNeoForge.register();
            //?}
        }
    }
    *///?}

    //? if forge {
    public MirrorDimension() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        PMBlocks.BLOCKS.register(bus);
        PMPointOfInterests.register(bus);

        if (ModList.get().isLoaded("create")) {
            //? if neoforge_legacy {
            top.prefersmin.mirrordim.compat.CreateCompatNeoForge.register();
            //?} else {
            top.prefersmin.mirrordim.compat.CreateCompat.register();
            //?}
        }

        net.minecraftforge.common.MinecraftForge.EVENT_BUS.register(top.prefersmin.mirrordim.event.PMEvents.class);
    }
    //?}

}
