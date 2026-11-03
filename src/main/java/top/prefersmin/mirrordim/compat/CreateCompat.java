package top.prefersmin.mirrordim.compat;

import com.simibubi.create.content.trains.track.AllPortalTracks;
import net.minecraft.resources.ResourceLocation;
import top.prefersmin.mirrordim.MirrorDimension;
import top.prefersmin.mirrordim.world.PortalTracksSupport;

/**
 * 机械动力兼容入口。
 * <p>
 * 这个类只会在确认 Create 已加载后才会被类加载器读取，避免未安装 Create 时
 * 因直接引用 Create 的类而触发 {@link NoClassDefFoundError}。
 */
public final class CreateCompat {

    private CreateCompat() {
    }

    public static void register() {
        // 此处建议使用 new ResourceLocation 而非直接传入 Block，否则可能有意外错误
        AllPortalTracks.registerIntegration(
                new ResourceLocation(MirrorDimension.MODID, "chord_portal"),
                PortalTracksSupport::mirrordim
        );
    }
}
