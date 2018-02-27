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

import me.lucko.luckperms.api.LogEntry;
import me.lucko.luckperms.api.event.log.LogReceiveEvent;
import me.lucko.luckperms.common.event.AbstractEvent;

import javax.annotation.Nonnull;
import java.util.UUID;

public class EventLogReceive extends AbstractEvent implements LogReceiveEvent {

    private final UUID logId;
    private final LogEntry entry;

    public EventLogReceive(UUID logId, LogEntry entry) {
        this.logId = logId;
        this.entry = entry;
    }

    @Nonnull
    @Override
    public UUID getLogId() {
        return this.logId;
    }

    @Nonnull
    @Override
    public LogEntry getEntry() {
        return this.entry;
    }

    @Override
    public String toString() {
        return "LogReceiveEvent(logId=" + this.getLogId() + ", entry=" + this.getEntry() + ")";
    }
}
