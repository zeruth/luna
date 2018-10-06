package io.luna.game.model.mob.inter;

import io.luna.game.model.mob.Player;

import java.util.OptionalInt;

/**
 * An abstraction model representing an interface that can be opened and closed on a Player's screen.
 *
 * @author lare96 <http://github.com/lare96>
 */
public abstract class AbstractInterface {

    /**
     * The interface identifier.
     */
    final OptionalInt id;

    /**
     * The interface type.
     */
    final InterfaceType type;

    /**
     * Creates a new {@link AbstractInterface}.
     *
     * @param id The interface identifier.
     * @param type The interface type.
     */
    private AbstractInterface(OptionalInt id, InterfaceType type) {
        this.id = id;
        this.type = type;
    }

    /**
     * Creates a new {@link AbstractInterface} with {@code id} wrapped in an optional.
     *
     * @param id The interface identifier.
     * @param type The interface type.
     */
    AbstractInterface(int id, InterfaceType type) {
        this(OptionalInt.of(id), type);
    }

    /**
     * Creates a new {@link AbstractInterface} with no interface identifier.
     *
     * @param type The interface type.
     */
    AbstractInterface(InterfaceType type) {
        this(OptionalInt.empty(), type);
    }

    /**
     * Determines if this interface closes upon action initialization and movement.
     *
     * @param player The player.
     * @return {@code true} if this interface auto-closes.
     */
    public abstract boolean isAutoClose(Player player);

    /**
     * Opens this interface.
     *
     * @param player The player to open for.
     */
    public abstract void open(Player player);

    /**
     * A function called when this interface is closed.
     *
     * @param player The player to apply to listener for.
     */
    public void onClose(Player player) {
    }

    /**
     * Determines if this interface is walkable.
     *
     * @return {@code true} if this interface is walkable.
     */
    public final boolean isWalkable() {
        return type == InterfaceType.WALKABLE;
    }

    /**
     * Determines if this interface is input.
     *
     * @return {@code true} if this interface is input.
     */
    public final boolean isInput() {
        return type == InterfaceType.INPUT;
    }

    /**
     * Determines if this interface is standard.
     *
     * @return {@code true} if this interface is standard.
     */
    public final boolean isStandard() {
        return type == InterfaceType.STANDARD;
    }

    /**
     * Retrieves the identifier within {@link #id}. If it doesn't exist, returns {@code -1}.
     *
     * @return The identifier, or {@code -1} if none exist.
     */
    public final int unsafeGetId() {
        return id.orElse(-1);
    }

    /**
     * @return The interface identifier.
     */
    public final OptionalInt getId() {
        return id;
    }

    /**
     * @return The interface type.
     */
    public InterfaceType getType() {
        return type;
    }
}