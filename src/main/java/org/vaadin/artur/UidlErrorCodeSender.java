package org.vaadin.artur;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinResponse;
import com.vaadin.server.VaadinSession;
import com.vaadin.server.communication.UidlRequestHandler;

public class UidlErrorCodeSender extends UidlRequestHandler {

    private boolean failRequests = false;
    private ScheduledExecutorService executor = Executors
            .newScheduledThreadPool(3);

    @Override
    public boolean synchronizedHandleRequest(VaadinSession session,
            VaadinRequest request, VaadinResponse response) throws IOException {
        if (failRequests) {
            response.sendError(500, "Intentionally failed");
            return true;
        }

        return false;

    }

    public void enableError(int time, Runnable atEnd) {
        failRequests = true;
        executor.schedule(() -> {
            failRequests = false;
            atEnd.run();
        }, time, TimeUnit.MILLISECONDS);
    }

    public void enableBadConnection(int timeOn, int timeOff, int totalTime,
            Runnable atEnd) {
        Runnable makeItWork = (() -> {
            failRequests = false;
        });
        Runnable makeItFail = (() -> {
            failRequests = true;
        });

        ScheduledFuture<?> makeItWorkFuture = executor.scheduleWithFixedDelay(
                makeItWork, 0, timeOn + timeOff, TimeUnit.MILLISECONDS);
        ScheduledFuture<?> makeItFailFuture = executor.scheduleWithFixedDelay(
                makeItFail, timeOn, timeOn + timeOff, TimeUnit.MILLISECONDS);

        executor.schedule(() -> {
            makeItWorkFuture.cancel(true);
            makeItFailFuture.cancel(true);
            failRequests = false;
            atEnd.run();
        }, totalTime, TimeUnit.MILLISECONDS);

    }

}
