package net.atcore.utils;

import net.atcore.AviaTerraCore;
import net.atcore.messages.MessagesManager;
import net.atcore.messages.TypeMessages;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AviaTerraScheduler {

    private Thread workerThread;

    private AviaTerraScheduler(){
        workerThread = new Thread(this::processQueue);
        workerThread.setName("AviaTerraCore WorkerThread");
    }

    public void stop() {
        taskQueue.clear();
        workerThread.interrupt();
        workerThread = null;
    }

    public void start() {
        workerThread.start();
    }

    public static AviaTerraScheduler threadNew(){
        return new AviaTerraScheduler();
    }

    public static void runSync(Runnable runnable){
        if (Bukkit.isPrimaryThread()) {
            runnable.run();
        }else {
            Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), runnable);
        }
    }

    public static BukkitTask runTask(Runnable task){
        return Bukkit.getScheduler().runTask(AviaTerraCore.getInstance(), task);
    }

    public static BukkitTask runTaskLater(long delay, Runnable task){
        return Bukkit.getScheduler().runTaskLater(AviaTerraCore.getInstance(), task, delay);
    }

    public static BukkitTask runTaskLaterAsynchronously(long delay, Runnable task){
        return Bukkit.getScheduler().runTaskLaterAsynchronously(AviaTerraCore.getInstance(), task, delay);
    }

    public static BukkitTask runTaskTimer(long delay, long period,Runnable task){
        return Bukkit.getScheduler().runTaskTimer(AviaTerraCore.getInstance(), task, delay, period);
    }

    public static BukkitTask runTaskTimerAsynchronously(long delay, long period, Runnable task){
        return Bukkit.getScheduler().runTaskTimerAsynchronously(AviaTerraCore.getInstance(), task, delay, period);
    }

    /**
     * @see #enqueueTaskAsynchronously(boolean, Runnable)
     * @param task La tarea que se va añadir
     */

    public static void enqueueTaskAsynchronously(Runnable task) {
        enqueueTaskAsynchronously(false, task);
    }

    private static final ExecutorService EXECUTOR = Executors.newCachedThreadPool();
    public static final BlockingQueue<AviaRunnable> taskQueue = new LinkedBlockingQueue<>();
    public static volatile LinkedList<telemetryTask> telemetryTasks = new LinkedList<>();
    public static volatile ArrayList<Integer> amountTask = new ArrayList<>();
    public static volatile AtomicInteger currentAoumt = new AtomicInteger(0);

    /**
     * Realiza tareas de manera asincrónica y lo añade a una cola para evitar problemas de sincronización y que se haga
     * los proceso de manera consecutiva.
     * <p>
     * Si la tarea tarda mucho en realizarse mucho en realize (<1000 ms) salta una excepción indicando el problema
     * </p>
     * @param task El proceso que va a realizar
     * @param isHeavyProcess indica si la tarea es pesada haciendo una omisión del waring que se
     *                       produce cuando la tarea tarda en completable
     */

    public static void enqueueTaskAsynchronously(boolean isHeavyProcess, Runnable task) {
        if (!taskQueue.add(new AviaRunnable(task, isHeavyProcess))){
            MessagesManager.logConsole("Error al añadir una tarea la cola", TypeMessages.ERROR);
        }
        if (taskQueue.size() >= 20){
            MessagesManager.logConsole(String.format("Hay <|%s|> tareas en cola, Hilo sobre cargador \n" +
                    Arrays.stream(Thread.currentThread().getStackTrace()).toList().get(3), taskQueue.size()), TypeMessages.WARNING);
        }
    }


    private void processQueue() {
        new BukkitRunnable(){
            @Override
            public void run() {
                if (amountTask.size() >= 5) {
                    amountTask.removeFirst(); // Elimina el más antiguo
                }
                amountTask.add(currentAoumt.get());
                currentAoumt.set(0);
            }
        }.runTaskTimerAsynchronously(AviaTerraCore.getInstance(), 0, 20*60*2);
        try {
            while (!Thread.currentThread().isInterrupted()) {
                AviaRunnable task = taskQueue.take();
                long startTime = System.nanoTime();

                Future<?> future = EXECUTOR.submit(task);
                try {
                    future.get(1000*45, TimeUnit.MILLISECONDS);
                    long elapsedNanos = System.nanoTime() - startTime;
                    /*if (elapsedNanos > 1_000_000_000L && !task.isHeavyProcess()) { // 1s en nanosegundos
                        StringBuilder builder = getStackTrace(new Exception().getStackTrace());
                        AviaTerraCore.getInstance().getLogger().warning(
                                String.format("La tarea tardó %s ms en procesarse", elapsedNanos * 0.000001D) + "\n" + builder
                        );
                    }*/
                    if (telemetryTasks.size() >= 200) {
                        telemetryTasks.removeFirst(); // Elimina el más antiguo
                    }
                    telemetryTasks.add(new telemetryTask(System.currentTimeMillis(), elapsedNanos * 0.000001D,  taskQueue.size()));
                    currentAoumt.set(currentAoumt.get() + 1);
                } catch (TimeoutException e) {
                    StringBuilder builder = getStackTrace(e.getCause().getStackTrace());
                    future.cancel(true); // Cancelamos la tarea si tarda demasiado
                    AviaTerraCore.getInstance().getLogger().severe("La tarea fue cancelada por que tardo mucho en procesarse" + "\n" + builder);
                } catch (ExecutionException e) {
                    StringBuilder builder = getStackTrace(e.getCause().getStackTrace());
                    AviaTerraCore.getInstance().getLogger().severe("Hubo un error al iniciar la tarea [" + e.getMessage() + "]" + "\n" + builder);
                }

            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            if (!Bukkit.isStopping()) MessagesManager.sendErrorException("Hilo del AviaTerra hubo una excepción de interrupción", e);
        }
    }

    public static @NotNull StringBuilder getStackTrace(StackTraceElement[] traceElements) {
        StringBuilder builder = new StringBuilder();
        for (StackTraceElement element : traceElements) {
            builder.append(element.toString()).append("\n\t");
        }
        return builder;
    }

    public record telemetryTask(long currentTime, double elapsedProcess, int queue) {}
}
