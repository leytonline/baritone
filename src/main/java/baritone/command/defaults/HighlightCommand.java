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

package baritone.command.defaults;

import baritone.KeepName;
import baritone.api.IBaritone;
import baritone.api.command.Command;
import baritone.api.command.argument.IArgConsumer;
import baritone.api.command.datatypes.EntityClassById;
import baritone.api.command.exception.CommandException;
import baritone.api.command.helpers.TabCompleteHelper;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Stream;

public class HighlightCommand extends Command {

    public HighlightCommand(IBaritone baritone) {
        super(baritone, "highlight");
    }

    @Override
    public void execute(String label, IArgConsumer args) throws CommandException {
        args.requireMin(1);
        HighlightAction action = args.getEnum(HighlightAction.class);

        switch (action) {
            case ENTITY:
                args.requireMin(1);
                List<EntityType> classes = new ArrayList<>();
                while (args.hasAny()) {
                    classes.add(args.getDatatypeFor(EntityClassById.INSTANCE));
                }
                baritone.getHighlightBehavior().highlight(
                        e -> classes.stream().anyMatch(c -> e.getType().equals(c))
                );
                logDirect("Highlighting these types of entities:");
                classes.stream()
                        .map(BuiltInRegistries.ENTITY_TYPE::getKey)
                        .map(Objects::requireNonNull)
                        .map(Identifier::toString)
                        .forEach(this::logDirect);
                break;
            case RAID:
                args.requireMax(0);
                baritone.getHighlightBehavior().highlight(
                        e -> e.getType().builtInRegistryHolder().is(EntityTypeTags.RAIDERS)
                );
                logDirect("Highlighting raid mobs");
                break;
            case CLEAR:
                args.requireMax(0);
                baritone.getHighlightBehavior().clearHighlights();
                logDirect("Cleared entity highlights");
                break;
            default:
                throw new IllegalStateException("Unexpected highlight action " + action);
        }
    }

    @Override
    public Stream<String> tabComplete(String label, IArgConsumer args) throws CommandException {
        if (args.hasExactlyOne()) {
            return new TabCompleteHelper()
                    .append(HighlightAction.class)
                    .filterPrefix(args.getString())
                    .stream();
        }

        try {
            if (args.getEnum(HighlightAction.class) != HighlightAction.ENTITY) {
                return Stream.empty();
            }
        } catch (NullPointerException e) {
            return Stream.empty();
        }

        while (args.has(2)) {
            if (args.peekDatatypeOrNull(EntityClassById.INSTANCE) == null) {
                return Stream.empty();
            }
            args.get();
        }
        return args.tabCompleteDatatype(EntityClassById.INSTANCE);
    }

    @Override
    public String getShortDesc() {
        return "Highlight entity things";
    }

    @Override
    public List<String> getLongDesc() {
        return Arrays.asList(
                "The highlight command renders goal-style highlights around matching entities without moving or pathing.",
                "",
                "Usage:",
                "> highlight entity <entity1> <entity2> <...> - Highlight certain entity types (for example 'skeleton', 'horse' etc.)",
                "> highlight raid - Highlight raid mob types",
                "> highlight clear - Clear entity highlights"
        );
    }

    @KeepName
    private enum HighlightAction {
        ENTITY,
        RAID,
        CLEAR
    }
}
