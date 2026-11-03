package top.prefersmin.mirrordim;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import top.prefersmin.mirrordim.compat.CreateCompat;
import top.prefersmin.mirrordim.registry.PMBlocks;
import top.prefersmin.mirrordim.registry.PMPointOfInterests;

@Mod(MirrorDimension.MODID)
public class MirrorDimension {

    public static final String MODID = "mirrordim";

    // 传送门框架，拿鸡蛋右键激活哦
    public static final Block frameBlock = Blocks.SCULK;

    public MirrorDimension() {

        // 事件总线，你懂的
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        // 注册传送门方块
        PMBlocks.BLOCKS.register(bus);

        // 注册POI
        PMPointOfInterests.register(bus);

        // 注册可被列车系统跨越的维度传送门（仅安装机械动力时启用）
        if (ModList.get().isLoaded("create")) {
            CreateCompat.register();
        }

    }

}
