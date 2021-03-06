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

package me.lucko.luckperms;

import me.lucko.luckperms.api.LuckPermsApi;

import javax.annotation.Nonnull;
import java.util.Optional;

/**
 * Provides static access to the {@link LuckPermsApi}.
 *
 * <p>Ideally, the ServiceManager for the platform should be used to obtain an instance,
 * however, this provider can be used if you need static access.</p>
 */
public final class LuckPerms {
    private static LuckPermsApi instance = null;

    /**
     * Gets an instance of the {@link LuckPermsApi},
     * throwing {@link IllegalStateException} if an instance is not yet loaded.
     *
     * <p>Will never return null.</p>
     *
     * @return an api instance
     * @throws IllegalStateException if the api is not loaded
     */
    @Nonnull
    public static LuckPermsApi getApi() {
        if (instance == null) {
            throw new IllegalStateException("API is not loaded.");
        }
        return instance;
    }

    /**
     * Gets an instance of {@link LuckPermsApi}, if it is loaded.
     *
     * <p>Unlike {@link LuckPerms#getApi}, this method will not throw an
     * {@link IllegalStateException} if an instance is not yet loaded, rather return
     * an empty {@link Optional}.
     *
     * @return an optional api instance
     */
    @Nonnull
    public static Optional<LuckPermsApi> getApiSafe() {
        return Optional.ofNullable(instance);
    }

    /**
     * Registers an instance of the {@link LuckPermsApi} with this provider.
     *
     * @param instance the instance
     */
    static void registerProvider(LuckPermsApi instance) {
        LuckPerms.instance = instance;
    }

    /**
     * Removes the current instance from this provider.
     */
    static void unregisterProvider() {
        LuckPerms.instance = null;
    }

    private LuckPerms() {
        throw new UnsupportedOperationException("This class cannot be instantiated.");
    }

}
