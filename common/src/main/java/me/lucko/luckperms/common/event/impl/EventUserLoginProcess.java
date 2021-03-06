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

package me.lucko.luckperms.common.event.impl;

import me.lucko.luckperms.api.User;
import me.lucko.luckperms.api.event.user.UserLoginProcessEvent;
import me.lucko.luckperms.common.event.AbstractEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EventUserLoginProcess extends AbstractEvent implements UserLoginProcessEvent {

    private final UUID uuid;
    private final String username;
    private final User user;

    public EventUserLoginProcess(UUID uuid, String username, User user) {
        this.uuid = uuid;
        this.username = username;
        this.user = user;
    }

    @Nonnull
    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Nonnull
    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public User getUser() {
        return this.user;
    }

    @Override
    public String toString() {
        return "UserLoginProcessEvent(uuid=" + this.getUuid() + ", username=" + this.getUsername() + ", user=" + this.getUser() + ")";
    }
}
