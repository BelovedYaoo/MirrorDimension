package top.prefersmin.mirrordim.registry;

import net.minecraft.world.level.block.Block;

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

import java.util.function.Supplier;

public class PMBlocks {

    //? if forge {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MirrorDimension.MODID);
    //?}
//? if neoforge {
    /*public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(Registries.BLOCK, MirrorDimension.MODID);
    *///?}

    public static final Supplier<Block> CHORD_PORTAL = BLOCKS.register("chord_portal", ChordPortalBlock::new);

}
