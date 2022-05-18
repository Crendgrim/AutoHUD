package mod.crend.autohud;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class AutoHudMixinPlugin implements IMixinConfigPlugin {

    private static final List<String> MOD_COMPAT = List.of(
            "armor_hud",
            "dehydration",
            "detailab",
            "inventoryprofilesnext"
    );

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        for (String modid : MOD_COMPAT) {
            if (mixinClassName.contains(modid)) return FabricLoader.getInstance().isModLoaded(modid);
        }
        return true;
    }

    @Override
    public void onLoad(String mixinPackage) {
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
