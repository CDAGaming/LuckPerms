/*
 * Copyright (c) 2016 Lucko (Luck) <luck@lucko.me>
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

package me.lucko.luckperms.common.data;

import me.lucko.luckperms.api.Node;
import me.lucko.luckperms.common.commands.sender.Sender;
import me.lucko.luckperms.common.commands.utils.Util;
import me.lucko.luckperms.common.constants.Message;
import me.lucko.luckperms.common.core.NodeFactory;
import me.lucko.luckperms.common.core.model.Group;
import me.lucko.luckperms.common.core.model.Track;
import me.lucko.luckperms.common.core.model.User;
import me.lucko.luckperms.common.plugin.LuckPermsPlugin;
import me.lucko.luckperms.common.storage.Storage;
import me.lucko.luckperms.common.utils.ProgressLogger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Handles export operations
 */
public class Exporter implements Runnable {
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss z");

    // number of users --> the value to divide the list by. base value of 1000 per thread, with a max of 10 threads. so, 3000 = 3 threads, 10000+ = 10 threads
    private static final Function<Integer, Integer> THREAD_COUNT_FUNCTION = usersCount -> {
        // how many threads to make. must be 1 <= x <= 15
        int i = Math.max(1, Math.min(15, usersCount / 1000));

        // then work out the value to split at. e.g. if we have 1,000 users to export and 2 threads, we should split at every 500 users.
        return Math.max(1, usersCount / i);
    };

    private static void write(BufferedWriter writer, String s) {
        try {
            writer.write(s);
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final LuckPermsPlugin plugin;
    private final Sender executor;
    private final Path filePath;
    private final ProgressLogger log;

    public Exporter(LuckPermsPlugin plugin, Sender executor, Path filePath) {
        this.plugin = plugin;
        this.executor = executor;
        this.filePath = filePath;

        log = new ProgressLogger(null, Message.EXPORT_LOG, Message.EXPORT_LOG_PROGRESS);
        log.addListener(plugin.getConsoleSender());
        log.addListener(executor);
    }

    @Override
    public void run() {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8)) {
            log.log("Starting.");

            write(writer, "# LuckPerms Export File");
            write(writer, "# Generated by " + executor.getName() + " at " + DATE_FORMAT.format(new Date(System.currentTimeMillis())));
            write(writer, "");

            // Export Groups
            log.log("Starting group export.");

            // Create the actual groups first
            write(writer, "# Create groups");
            for (Group group : plugin.getGroupManager().getAll().values()) {
                write(writer, "/luckperms creategroup " + group.getName());
            }
            write(writer, "");

            AtomicInteger groupCount = new AtomicInteger(0);
            for (Group group : plugin.getGroupManager().getAll().values()) {
                write(writer, "# Export group: " + group.getName());
                for (Node node : group.getNodes().values()) {
                    write(writer, NodeFactory.nodeAsCommand(node, group.getName(), true));
                }
                write(writer, "");
                log.logAllProgress("Exported {} groups so far.", groupCount.incrementAndGet());
            }
            log.log("Exported " + groupCount.get() + " groups.");

            write(writer, "");
            write(writer, "");

            // Export tracks
            log.log("Starting track export.");

            Collection<? extends Track> tracks = plugin.getTrackManager().getAll().values();
            if (!tracks.isEmpty()) {

                // Create the actual tracks first
                write(writer, "# Create tracks");
                for (Track track : tracks) {
                    write(writer, "/luckperms createtrack " + track.getName());
                }

                write(writer, "");

                AtomicInteger trackCount = new AtomicInteger(0);
                for (Track track : plugin.getTrackManager().getAll().values()) {
                    write(writer, "# Export track: " + track.getName());
                    for (String group : track.getGroups()) {
                        write(writer, "/luckperms track " + track.getName() + " append " + group);
                    }
                    write(writer, "");
                    log.logAllProgress("Exported {} tracks so far.", trackCount.incrementAndGet());
                }

                write(writer, "");
                write(writer, "");
            }

            log.log("Exported " + tracks.size() + " tracks.");


            // Users are migrated in separate threads.
            // This is because there are likely to be a lot of them, and because we can.
            // It's a big speed improvement, since the database/files are split up and can handle concurrent reads.

            log.log("Starting user export. Finding a list of unique users to export.");

            // Find all of the unique users we need to export
            Storage ds = plugin.getStorage();
            Set<UUID> users = ds.getUniqueUsers().join();
            log.log("Found " + users.size() + " unique users to export.");

            write(writer, "# Export users");

            List<List<UUID>> subUsers;
            AtomicInteger userCount = new AtomicInteger(0);

            // not really that many users, so it's not really worth spreading the load.
            if (users.size() < 1500) {
                subUsers = Collections.singletonList(new ArrayList<>(users));
            } else {
                subUsers = Util.divideList(users, THREAD_COUNT_FUNCTION.apply(users.size()));
            }

            log.log("Split users into " + subUsers.size() + " threads for export.");

            // Setup a file writing lock. We don't want multiple threads writing at the same time.
            // The write function accepts a list of strings, as we want a user's data to be grouped together.
            // This means it can be processed and added in one go.
            ReentrantLock lock = new ReentrantLock();
            Consumer<List<String>> writeFunction = strings -> {
                lock.lock();
                try {
                    for (String s : strings) {
                        write(writer, s);
                    }
                } finally {
                    lock.unlock();
                }
            };

            // A set of futures, which are really just the threads we need to wait for.
            Set<CompletableFuture<Void>> futures = new HashSet<>();

            // iterate through each user sublist.
            for (List<UUID> subList : subUsers) {

                // register and start a new thread to process the sublist
                futures.add(CompletableFuture.runAsync(() -> {

                    // iterate through each user in the sublist, and grab their data.
                    for (UUID uuid : subList) {
                        try {
                            // actually export the user. this output will be fed to the writing function when we have all of the user's data.
                            List<String> output = new ArrayList<>();

                            plugin.getStorage().loadUser(uuid, "null").join();
                            User user = plugin.getUserManager().get(uuid);
                            output.add("# Export user: " + user.getUuid().toString() + " - " + user.getName());

                            boolean inDefault = false;
                            for (Node node : user.getNodes().values()) {
                                if (node.isGroupNode() && node.getGroupName().equalsIgnoreCase("default")) {
                                    inDefault = true;
                                    continue;
                                }

                                output.add(NodeFactory.nodeAsCommand(node, user.getUuid().toString(), false));
                            }

                            if (!user.getPrimaryGroup().getStoredValue().equalsIgnoreCase("default")) {
                                output.add("/luckperms user " + user.getUuid().toString() + " switchprimarygroup " + user.getPrimaryGroup());
                            }

                            if (!inDefault) {
                                output.add("/luckperms user " + user.getUuid().toString() + " parent remove default");
                            }

                            plugin.getUserManager().cleanup(user);
                            writeFunction.accept(output);

                            log.logProgress("Exported {} users so far.", userCount.incrementAndGet());

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, plugin.getScheduler().getAsyncExecutor()));
            }

            // all of the threads have been scheduled now and are running. we just need to wait for them all to complete
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[futures.size()])).join();

            log.log("Exported " + userCount.get() + " users.");

            writer.flush();
            log.getListeners().forEach(l -> Message.LOG_EXPORT_SUCCESS.send(l, filePath.toFile().getAbsolutePath()));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
