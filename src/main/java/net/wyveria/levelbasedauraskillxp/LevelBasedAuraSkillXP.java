package net.wyveria.levelbasedauraskillxp;

import dev.aurelium.auraskills.api.AuraSkillsApi;
import net.wyveria.levelbasedauraskillxp.listeners.XPSkillGainListener;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public final class LevelBasedAuraSkillXP extends JavaPlugin {

    AuraSkillsApi auraSkills;

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {
        auraSkills = AuraSkillsApi.get();
        getLogger().info("Plugin enabled.");

        this.getServer().getPluginManager().registerEvents(new XPSkillGainListener(this), this);
        getLogger().info("XP Event listener registered.");
    }

    @Override
    public void onDisable() {
    }

    public AuraSkillsApi getAuraSkills() {
        return auraSkills;
    }

    public int getMobLevel(LivingEntity livingEntity){
        Plugin levelledMobsPlugin = Bukkit.getPluginManager().getPlugin("LevelledMobs");
        if (levelledMobsPlugin == null) return 0;
        NamespacedKey levelKey = new NamespacedKey(levelledMobsPlugin, "level");
        return Objects.requireNonNullElse(
                livingEntity.getPersistentDataContainer().get(levelKey, PersistentDataType.INTEGER),
                0
        );
    }
}
