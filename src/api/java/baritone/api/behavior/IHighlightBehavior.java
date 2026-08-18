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

package baritone.api.behavior;

import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.function.Predicate;

/**
 * Renders highlights for entities matching a predicate without taking control of pathing.
 */
public interface IHighlightBehavior extends IBehavior {

    /**
     * Highlight any visible entities matching this predicate.
     *
     * @param filter the predicate
     */
    void highlight(Predicate<Entity> filter);

    /**
     * Clear the current highlight target.
     */
    void clearHighlights();

    /**
     * @return The entities that are currently highlighted.
     */
    List<Entity> highlighted();

    Predicate<Entity> currentFilter();
}
