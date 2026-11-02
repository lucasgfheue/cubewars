package com.proyectox.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.server.level.ServerLevel;

/** Tareas diferidas en el servidor (p. ej. el impacto orbital del TRUENO, 2 s después de designarlo). */
public final class Scheduler {
    private Scheduler() {}

    private record Task(ServerLevel level, long due, Runnable action) {}

    private static final List<Task> TASKS = new ArrayList<>();

    public static synchronized void schedule(ServerLevel level, int delayTicks, Runnable action) {
        TASKS.add(new Task(level, level.getGameTime() + delayTicks, action));
    }

    /** Se llama al final de cada tick de cada nivel del servidor. */
    public static void tick(ServerLevel level) {
        List<Task> due = new ArrayList<>();
        synchronized (Scheduler.class) {
            Iterator<Task> it = TASKS.iterator();
            while (it.hasNext()) {
                Task t = it.next();
                if (t.level() == level && level.getGameTime() >= t.due()) {
                    due.add(t);
                    it.remove();
                }
            }
        }
        for (Task t : due) t.action().run();
    }

    public static synchronized void clear() {
        TASKS.clear();
    }
}
