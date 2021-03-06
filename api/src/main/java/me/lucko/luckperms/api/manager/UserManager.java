/*
 * This file is part of LuckPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.api.manager;

import me.lucko.luckperms.api.Storage;
import me.lucko.luckperms.api.User;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Represents the object responsible for managing {@link User} instances.
 *
 * <p>Note that User instances are automatically loaded for online players.
 * It's likely that offline players will not have an instance pre-loaded.</p>
 *
 * @since 4.0
 */
public interface UserManager {

    /**
     * Loads a user from the plugin's storage provider into memory.
     *
     * <p>This method is effectively the same as
     * {@link Storage#loadUser(UUID, String)}, however, the Future returns the
     * resultant user instance instead of a boolean flag.</p>
     *
     * <p>Unlike the method in {@link Storage}, when a user cannot be loaded,
     * the future will be {@link CompletableFuture completed exceptionally}.</p>
     *
     * @param uuid the uuid of the user
     * @param username the username, if known
     * @return the resultant user
     * @throws NullPointerException if the uuid is null
     * @since 4.1
     */
    @Nonnull
    CompletableFuture<User> loadUser(@Nonnull UUID uuid, @Nullable String username);

    /**
     * Loads a user from the plugin's storage provider into memory.
     *
     * <p>This method is effectively the same as {@link Storage#loadUser(UUID)},
     * however, the Future returns the resultant user instance instead of a
     * boolean flag.</p>
     *
     * <p>Unlike the method in {@link Storage}, when a user cannot be loaded,
     * the future will be {@link CompletableFuture completed exceptionally}.</p>
     *
     * @param uuid the uuid of the user
     * @return the resultant user
     * @throws NullPointerException if the uuid is null
     * @since 4.1
     */
    @Nonnull
    default CompletableFuture<User> loadUser(@Nonnull UUID uuid) {
        return loadUser(uuid, null);
    }

    /**
     * Saves a user's data back to the plugin's storage provider.
     *
     * <p>You should call this after you make any changes to a user.</p>
     *
     * <p>This method is effectively the same as {@link Storage#saveUser(User)},
     * however, the Future returns void instead of a boolean flag.</p>
     *
     * <p>Unlike the method in {@link Storage}, when a user cannot be saved,
     * the future will be {@link CompletableFuture completed exceptionally}.</p>
     *
     * @param user the user to save
     * @return a future to encapsulate the operation.
     * @throws NullPointerException  if user is null
     * @throws IllegalStateException if the user instance was not obtained from LuckPerms.
     * @since 4.1
     */
    @Nonnull
    CompletableFuture<Void> saveUser(@Nonnull User user);

    /**
     * Gets a loaded user.
     *
     * @param uuid the uuid of the user to get
     * @return a {@link User} object, if one matching the uuid is loaded, or null if not
     * @throws NullPointerException if the uuid is null
     */
    @Nullable
    User getUser(@Nonnull UUID uuid);

    /**
     * Gets a loaded user.
     *
     * @param uuid the uuid of the user to get
     * @return an optional {@link User} object
     * @throws NullPointerException if the uuid is null
     */
    @Nonnull
    default Optional<User> getUserOpt(@Nonnull UUID uuid) {
        return Optional.ofNullable(getUser(uuid));
    }

    /**
     * Gets a loaded user.
     *
     * @param name the username of the user to get
     * @return a {@link User} object, if one matching the uuid is loaded, or null if not
     * @throws NullPointerException if the name is null
     */
    @Nullable
    User getUser(@Nonnull String name);

    /**
     * Gets a loaded user.
     *
     * @param name the username of the user to get
     * @return an optional {@link User} object
     * @throws NullPointerException if the name is null
     */
    @Nonnull
    default Optional<User> getUserOpt(@Nonnull String name) {
        return Optional.ofNullable(getUser(name));
    }

    /**
     * Gets a set of all loaded users.
     *
     * @return a {@link Set} of {@link User} objects
     */
    @Nonnull
    Set<User> getLoadedUsers();

    /**
     * Check if a user is loaded in memory
     *
     * @param uuid the uuid to check for
     * @return true if the user is loaded
     * @throws NullPointerException if the uuid is null
     */
    boolean isLoaded(@Nonnull UUID uuid);

    /**
     * Unload a user from the internal storage, if they're not currently online.
     *
     * @param user the user to unload
     * @throws NullPointerException if the user is null
     */
    void cleanupUser(@Nonnull User user);

}
