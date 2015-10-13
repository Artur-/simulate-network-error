package org.vaadin.artur;

import java.text.DateFormat;
import java.util.Date;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

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
        simulateShortDisconnect.addClickListener(e -> {
            statusLabel.setValue("Simulating 10s disconnect");
            statusLabel.setStyleName("red");
            getUI().setPollInterval(10200);
            getUI().errorCodeResponder.enableError(10000, () -> {
                resetToDefaultState();
            });
        });
        simulateLongDisconnect.addClickListener(e -> {
            statusLabel.setValue("Simulating 30s disconnect");
            statusLabel.setStyleName("red");
            getUI().setPollInterval(30200);
            getUI().errorCodeResponder.enableError(30000, () -> {
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

        /* Hello world app */
        sayHelloToTheServer.addClickListener(e -> {
            logLayout.addComponent(new Label(++labelCount
                    + ". Server says hello!"));
        });

        /* Email app */
        toField.addValidator(new EmailValidator(
                "Must be a valid e-mail address"));
        ccField.addValidator(new EmailValidator(
                "Must be a valid e-mail address"));
        subjectField.addValidator(new StringLengthValidator(
                "Must be at least 3 characters", 3, null, false));

        sendButton.addClickListener(e -> {
            if (!toField.isValid() || !ccField.isValid()
                    || !subjectField.isValid() || !messageField.isValid()) {
                Notification.show("Please fix your input before sending",
                        Type.ERROR_MESSAGE);
                return;
            }
            String email = "To: " + toField.getValue() + "\n";
            email += "Cc: " + ccField.getValue() + "\n";
            email += "Subject: " + subjectField.getValue() + "\n";
            email += "Message: " + messageField.getValue() + "\n";
            email += "\n";
            email += "Sent at "
                    + DateFormat.getDateTimeInstance(DateFormat.SHORT,
                            DateFormat.SHORT, getLocale()).format(new Date());

            toField.clear();
            ccField.clear();
            subjectField.clear();
            messageField.clear();
            Notification.show(email, Type.TRAY_NOTIFICATION);
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
