package structurewaypoints.locate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import seedengine.api.FoundStructure;
import seedengine.api.StructureCandidate;
import structurewaypoints.Constants;
import waypointsapi.api.Place;
import waypointsapi.api.WaypointScope;

/**
 * Works out the structures around a place in each dimension on the mod's own threads and gives them to the Waypoints
 * API scope that was current when the work started. Candidates are listed over the whole area first (placement only,
 * cheap), then checked with world generation's rules, nearest first (the heavy part).
 * <p>
 * One round at a time: the first job of {@link #update} whose dimension has not been worked out around its place yet.
 * Jobs are given in order of importance (the player's own dimension first).
 */
public final class Locator {
    /** What to work out in one dimension: within {@code radius} chunks of {@code center}, with {@code source}. */
    public record Job(ResourceKey<Level> dimension, WorldSource source, ChunkPos center, int radius) {
    }
    /** Work out a dimension again once its center has moved this many chunks. */
    static final int REDO_DISTANCE = 8;

    private static final class Done {
        final WorldSource source;
        ChunkPos center;
        int radius;
        /** The candidates checked (or queued), by set and chunk, so a later round does not check them again. */
        final Set<String> checked = ConcurrentHashMap.newKeySet();

        Done(WorldSource source) {
            this.source = source;
        }
    }

    private static final ExecutorService POOL = Executors.newFixedThreadPool(threads(), new ThreadFactory() {
        private final AtomicInteger count = new AtomicInteger();

        @Override
        public Thread newThread(Runnable task) {
            Thread thread = new Thread(task, "Structure Waypoints Locate " + count.incrementAndGet());
            thread.setDaemon(true);
            thread.setPriority(Thread.MIN_PRIORITY + 1);
            return thread;
        }
    });

    private static final AtomicInteger GENERATION = new AtomicInteger();
    /** Tasks of the round going on (its listing and its checks). */
    private static final AtomicInteger PENDING = new AtomicInteger();
    private static final Map<ResourceKey<Level>, Done> DONE = new HashMap<>();
    private static WaypointScope scope;

    private Locator() {
    }

    private static int threads() {
        return Math.max(1, Math.min(3, Runtime.getRuntime().availableProcessors() / 2 - 1));
    }

    /** Called now and then from the client thread, with the current scope and what to work out. */
    public static synchronized void update(WaypointScope current, List<Job> jobs) {
        if (!current.equals(scope)) {
            reset();
            scope = current;
        }
        if (PENDING.get() > 0) return;
        for (Job job : jobs) {
            Done done = DONE.get(job.dimension());
            if (done == null || !done.source.equals(job.source())) {
                done = new Done(job.source());
                DONE.put(job.dimension(), done);
            }
            if (done.center != null && distance(done.center, job.center()) < REDO_DISTANCE && done.radius == job.radius()) continue;
            done.center = job.center();
            done.radius = job.radius();
            start(current, GENERATION.get(), job, done);
            return;
        }
    }

    /** Stops the work going on and forgets what was worked out (another world or seed, the player leaving, turned off). */
    public static synchronized void reset() {
        if (scope == null && DONE.isEmpty()) return;
        GENERATION.incrementAndGet();
        scope = null;
        DONE.clear();
    }

    private static void start(WaypointScope target, int generation, Job job, Done done) {
        PENDING.incrementAndGet();
        POOL.execute(() -> {
            try {
                if (GENERATION.get() != generation) return;
                ChunkPos center = job.center();
                int radius = job.radius();
                List<StructureCandidate> candidates = new ArrayList<>(job.source().candidates(
                        new ChunkPos(center.x() - radius, center.z() - radius), new ChunkPos(center.x() + radius, center.z() + radius)));
                candidates.sort(Comparator.comparingLong(candidate -> candidate.distanceSquared(center)));
                for (StructureCandidate candidate : candidates) {
                    if (!done.checked.add(Places.id(candidate.set(), candidate.chunk()))) continue;
                    PENDING.incrementAndGet();
                    POOL.execute(() -> check(target, generation, job, candidate));
                }
            } catch (RuntimeException | LinkageError e) {
                Constants.LOG.error("Could not list the structures around {} in {}", job.center(), job.dimension().identifier(), e);
            } finally {
                PENDING.decrementAndGet();
            }
        });
    }

    private static void check(WaypointScope target, int generation, Job job, StructureCandidate candidate) {
        try {
            if (GENERATION.get() != generation || !target.isCurrent()) return;
            List<FoundStructure> found = job.source().check(candidate);
            // The scope takes nothing once the client is on another server or seed; the generation stops the rest sooner.
            if (GENERATION.get() != generation) return;
            List<Place> places = found.stream().map(structure -> Places.of(job.dimension(), structure)).toList();
            if (!places.isEmpty()) {
                target.putAll(places);
            } else {
                // Not there: a waypoint of this candidate was worked out with other generation (another world type, an
                // older version) and is dropped. Where a set may place a structure does not depend on the world type
                // (but for the rings of strongholds, which follow the biomes).
                target.remove(job.dimension(), Places.id(candidate.set(), candidate.chunk()));
            }
        } catch (RuntimeException | LinkageError e) {
            Constants.LOG.error("Could not check {} at chunk {} in {}", candidate.set(), candidate.chunk(), job.dimension().identifier(), e);
        } finally {
            PENDING.decrementAndGet();
        }
    }

    private static int distance(ChunkPos a, ChunkPos b) {
        return Math.max(Math.abs(a.x() - b.x()), Math.abs(a.z() - b.z()));
    }
}
