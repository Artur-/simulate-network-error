package org.vaadin.artur;

import com.vaadin.ui.Label;

public class ReconnectConfigPanelForReal extends ReconnectConfigPanel {
    private int labelCount = 0;

    public ReconnectConfigPanelForReal() {
    }

    @Override
    public void attach() {
        super.attach();
        allowInteractionWhileReconnecting.setValue(!getUI()
                .getReconnectDialogConfiguration().isDialogModal());

        timeoutBeforeShowingDialog.setConverter(Integer.class);
        timeoutBeforeShowingDialog.setConvertedValue(getUI()
                .getReconnectDialogConfiguration().getDialogGracePeriod());
        timeoutBeforeShowingDialog.addValueChangeListener(e -> {
            getUI().getReconnectDialogConfiguration().setDialogGracePeriod(
                    (int) timeoutBeforeShowingDialog.getConvertedValue());
        });

        reconnectInterval.setConverter(Integer.class);
        reconnectInterval.setConvertedValue(getUI()
                .getReconnectDialogConfiguration().getReconnectInterval());
        reconnectInterval.addValueChangeListener(e -> {
            getUI().getReconnectDialogConfiguration().setReconnectInterval(
                    (int) reconnectInterval.getConvertedValue());

        });

        allowInteractionWhileReconnecting.addValueChangeListener(e -> {
            getUI().getReconnectDialogConfiguration().setDialogModal(
                    !allowInteractionWhileReconnecting.getValue());
        });
        // simulateShortDisconnect.addClickListener(e -> {
        // statusLabel.setValue("Simulating 1s disconnect");
        // getUI().setPollInterval(1200);
        // statusLabel.setStyleName("red");
        // getUI().errorCodeResponder.enableError(1000, () -> {
        // resetToDefaultState();
        // });
        // });
        simulateLongDisconnect.addClickListener(e -> {
            statusLabel.setValue("Simulating 10s disconnect");
            statusLabel.setStyleName("red");
            getUI().setPollInterval(5200);
            getUI().errorCodeResponder.enableError(5000, () -> {
                resetToDefaultState();
            });
        });
        simulateBadConnection.addClickListener(e -> {
            statusLabel.setValue("Simulating bad connection");
            statusLabel.setStyleName("red");
            getUI().errorCodeResponder.enableBadConnection(200, 800, 15000,
                    () -> {
                        resetToDefaultState();
                    });
        });

        sayHelloToTheServer.addClickListener(e -> {
            logLayout.addComponent(new Label(++labelCount
                    + ". Server says hello!"));
        });
    }

    private void resetToDefaultState() {
        // Called from servlet timer, outside locking
        getUI().access(() -> {
            statusLabel.setValue("Normal connection");
            statusLabel.setStyleName("colored bold");
            getUI().setPollInterval(-1);
        });
    }

    @Override
    public ErrorSimulatorUI getUI() {
        return (ErrorSimulatorUI) super.getUI();
    }
}
