package com.g3.sppts.swing;

import java.awt.Component;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

/**
 * Runs database work on a background thread (SwingWorker) so the window never freezes,
 * then hands the result back on the Event Dispatch Thread.
 */
public final class Async {

    /** When true, work runs immediately on the calling thread (used only by automated tests). */
    public static boolean sync = false;

    private Async() { }

    public static <T> void run(Component parent, Callable<T> work, Consumer<T> onDone) {
        run(parent, work, onDone, null);
    }

    public static <T> void run(Component parent, Callable<T> work, Consumer<T> onDone, Runnable onFail) {
        if (sync) {
            try {
                onDone.accept(work.call());
            } catch (Exception e) {
                error(parent, e);
                if (onFail != null) onFail.run();
            }
            return;
        }
        new SwingWorker<T, Void>() {
            @Override
            protected T doInBackground() throws Exception {
                return work.call(); // background thread: JDBC happens here
            }

            @Override
            protected void done() { // back on the Event Dispatch Thread
                try {
                    onDone.accept(get());
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (ExecutionException e) {
                    error(parent, e);
                    if (onFail != null) onFail.run();
                }
            }
        }.execute();
    }

    public static void error(Component parent, Throwable e) {
        Throwable cause = (e instanceof ExecutionException && e.getCause() != null) ? e.getCause() : e;
        String msg = cause.getMessage() == null ? cause.toString() : cause.getMessage();
        JOptionPane.showMessageDialog(parent, msg, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
