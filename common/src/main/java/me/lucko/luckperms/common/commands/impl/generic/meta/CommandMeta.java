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

package me.lucko.luckperms.common.commands.impl.generic.meta;

import com.google.common.collect.ImmutableList;
import me.lucko.luckperms.api.ChatMetaType;
import me.lucko.luckperms.common.commands.abstraction.SharedMainCommand;
import me.lucko.luckperms.common.commands.abstraction.SharedSubCommand;
import me.lucko.luckperms.common.locale.CommandSpec;
import me.lucko.luckperms.common.locale.LocaleManager;
import me.lucko.luckperms.common.model.PermissionHolder;

public class CommandMeta<T extends PermissionHolder> extends SharedMainCommand<T> {
    public CommandMeta(LocaleManager locale, boolean user) {
        super(CommandSpec.META.spec(locale), "Meta", user, ImmutableList.<SharedSubCommand>builder()
                .add(new MetaInfo(locale))
                .add(new MetaSet(locale))
                .add(new MetaUnset(locale))
                .add(new MetaSetTemp(locale))
                .add(new MetaUnsetTemp(locale))
                .add(new MetaAddChatMeta(locale, ChatMetaType.PREFIX))
                .add(new MetaAddChatMeta(locale, ChatMetaType.SUFFIX))
                .add(new MetaRemoveChatMeta(locale, ChatMetaType.PREFIX))
                .add(new MetaRemoveChatMeta(locale, ChatMetaType.SUFFIX))
                .add(new MetaAddTempChatMeta(locale, ChatMetaType.PREFIX))
                .add(new MetaAddTempChatMeta(locale, ChatMetaType.SUFFIX))
                .add(new MetaRemoveTempChatMeta(locale, ChatMetaType.PREFIX))
                .add(new MetaRemoveTempChatMeta(locale, ChatMetaType.SUFFIX))
                .add(new MetaClear(locale))
                .build());
    }
}
