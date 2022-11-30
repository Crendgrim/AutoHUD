package mod.crend.autohud;

import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Jankson;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonObject;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.JsonPrimitive;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.api.SyntaxError;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

public class AutoHudMixinPlugin implements IMixinConfigPlugin {

    /*
     * Slight hack: the mixins required for "fade" to work are a bit hackish and can
     * cause issues with other mods. Therefore, we want to only load them when we're
     * in "fade" mode. However, since mixin plugins get executed before any mods get
     * initialized, our config is not available yet. We thus need to load the config
     * file manually here and parse the animation type from the JSON.
     *
     * When switching to fade mode, there are some visual glitches, unless the whole
     * game gets restarted. Since a restart is necessary, we can opt to not load the
     * mixins required for fade mode unless it is enabled at launch.
     */
    private boolean fadeMode;
    private boolean loaded = false;

    private boolean load() {
        Jankson jankson = Jankson.builder().build();
        try {
            JsonObject json = jankson.load(new File("config/autohud.json5"));
            if (json.get("animationType") instanceof JsonPrimitive jp) {
                return jp.asString().equals("Fade");
            }
        } catch (IOException | SyntaxError ignored) {
        }
        return false;
    }
    private boolean isFadeMode() {
       if (!loaded) {
           fadeMode = load();
           loaded = true;
       }
       return fadeMode;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String[] split = mixinClassName.split("\\.mixin\\.render\\.");
        if (split.length == 2) {
            return isFadeMode();
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
