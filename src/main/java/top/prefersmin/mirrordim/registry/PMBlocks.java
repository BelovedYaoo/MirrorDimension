package top.prefersmin.mirrordim.registry;

import net.minecraft.world.level.block.Block;

//? if fabric {
import net.minecraft.core.Registry;
//? if >=1.20 {
import net.minecraft.core.registries.BuiltInRegistries;
//?}
//?}
//? if forge {
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
//?}
//? if neoforge {
/*import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
*///?}

import top.prefersmin.mirrordim.MirrorDimension;
import top.prefersmin.mirrordim.block.ChordPortalBlock;
import top.prefersmin.mirrordim.util.MinecraftCompat;

import java.util.function.Supplier;

public class PMBlocks {

    //? if fabric {
    private static Block chordPortal;

    public static final Supplier<Block> CHORD_PORTAL = () -> {
        if (chordPortal == null) {
            chordPortal = Registry.register(
                    //? if <1.20 {
                    Registry.BLOCK,
                    //?} else {
                    BuiltInRegistries.BLOCK,
                    //?}
                    MinecraftCompat.id(MirrorDimension.MODID, "chord_portal"),
                    new ChordPortalBlock()
            );
        }
        return chordPortal;
    };

    public static void register() {
        CHORD_PORTAL.get();
    }
    //?}

    //? if forge {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MirrorDimension.MODID);

    public static final Supplier<Block> CHORD_PORTAL = BLOCKS.register("chord_portal", ChordPortalBlock::new);
    //?}

    //? if neoforge {
    /*public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, MirrorDimension.MODID);

    public static final Supplier<Block> CHORD_PORTAL = BLOCKS.register("chord_portal", ChordPortalBlock::new);
    *///?}

}
