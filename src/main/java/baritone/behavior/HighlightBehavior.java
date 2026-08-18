/*
 * This file is part of Baritone.
 *
 * Baritone is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Baritone is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Baritone.  If not, see <https://www.gnu.org/licenses/>.
 */

package baritone.behavior;

import baritone.Baritone;
import baritone.api.BaritoneAPI;
import baritone.api.behavior.IHighlightBehavior;
import baritone.api.event.events.RenderEvent;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalComposite;
import baritone.utils.PathRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.dimension.DimensionType;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public final class HighlightBehavior extends Behavior implements IHighlightBehavior {

    private Predicate<Entity> filter;
    private List<Entity> cache = Collections.emptyList();

    public HighlightBehavior(Baritone baritone) {
        super(baritone);
    }

    @Override
    public void onRenderPass(RenderEvent event) {
        if (filter == null || ctx.world() == null) {
            return;
        }

        if (BaritoneAPI.getProvider().getPrimaryBaritone().getPlayerContext().world() == null) {
            return;
        }

        DimensionType thisPlayerDimension = ctx.world().dimensionType();
        DimensionType currentRenderViewDimension = BaritoneAPI.getProvider().getPrimaryBaritone().getPlayerContext().world().dimensionType();

        if (thisPlayerDimension != currentRenderViewDimension) {
            return;
        }

        scanWorld();
        if (cache.isEmpty()) {
            return;
        }

        Goal goal = new GoalComposite(cache.stream()
                .map(Entity::blockPosition)
                .map(GoalBlock::new)
                .toArray(Goal[]::new));
        PathRenderer.drawGoal(event.getModelViewStack(), ctx, goal, event.getPartialTicks(), Baritone.settings().colorGoalBox.value);
    }

    private boolean highlightable(Entity entity) {
        return entity != null && entity.isAlive() && !entity.equals(ctx.player());
    }

    private void scanWorld() {
        cache = ctx.entitiesStream()
                .filter(this::highlightable)
                .filter(this.filter)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public void highlight(Predicate<Entity> filter) {
        this.filter = filter;
    }

    @Override
    public void clearHighlights() {
        this.filter = null;
        this.cache = Collections.emptyList();
    }

    @Override
    public List<Entity> highlighted() {
        return cache;
    }

    @Override
    public Predicate<Entity> currentFilter() {
        return filter;
    }
}
