package net.wyveria.levelbasedauraskillxp.listeners;

import dev.aurelium.auraskills.api.event.skill.EntityXpGainEvent;
import net.wyveria.levelbasedauraskillxp.LevelBasedAuraSkillXP;
import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;


public class XPSkillGainListener implements Listener {

    private final LevelBasedAuraSkillXP plugin;
    // precompiled expression (variables: x = source xp, l = mob level, g = growth rate)
    private final Expression xpExpression;
    // hardcoded growth rate as requested
    private final double growthRate = 5;

    public XPSkillGainListener(LevelBasedAuraSkillXP plugin) {
        this.plugin = plugin;
        // Formula: x * pow(1 + l/100, g)
        // Using exp4j: pow(1 + l/100, g) * x
        xpExpression = new ExpressionBuilder("x * pow(1 + l/100, g)")
                .variables("x", "l", "g")
                .build();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onXpGain(EntityXpGainEvent event) {
        // Get the mob from the event
        LivingEntity entity = event.getAttacked();
        if (entity == null) return;

        // Get the mob level
        int mobLevel = plugin.getMobLevel(entity);
        if (mobLevel <= 0) return;

        double eventXP = event.getAmount();

        double modifiedXP;
        try {
            // Set variables and evaluate the precompiled expression
            xpExpression.setVariable("x", eventXP);
            xpExpression.setVariable("l", mobLevel);
            xpExpression.setVariable("g", growthRate);
            modifiedXP = xpExpression.evaluate();
            if (Double.isNaN(modifiedXP) || Double.isInfinite(modifiedXP)) {
                modifiedXP = eventXP;
            }
        } catch (Exception e) {
            modifiedXP = eventXP;
            plugin.getLogger().warning("Failed to evaluate XP expression: " + e.getMessage());
        }

        event.setAmount(modifiedXP);
    }
}
