package dev.vaadin.agentdemo.message.ui;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Lets the user type a message and show it as a notification.
 */
@Route("")
@PageTitle("Message")
@Menu(title = "Message", icon = "vaadin:comment", order = 0)
public class MessageView extends VerticalLayout {

    static final String EMPTY_MESSAGE_ERROR = "Please enter a message first";
    private static final int NOTIFICATION_DURATION_MS = 3000;

    // Package-private so browserless tests in the same package can drive them.
    final TextField message;
    final Button showMessage;

    public MessageView() {
        message = new TextField("Message");
        message.setPlaceholder("Type something…");
        message.setRequiredIndicatorVisible(true);
        message.setErrorMessage(EMPTY_MESSAGE_ERROR);
        // Clear the error as soon as the user starts typing again.
        message.addValueChangeListener(event -> message.setInvalid(false));

        showMessage = new Button("Show message", event -> showMessage());
        showMessage.addThemeVariants(ButtonVariant.PRIMARY);

        add(message, showMessage);
    }

    private void showMessage() {
        var text = message.getValue().trim();

        if (text.isEmpty()) {
            message.setInvalid(true);
            message.focus();
            show(EMPTY_MESSAGE_ERROR, NotificationVariant.ERROR);
            return;
        }

        message.setInvalid(false);
        show(text, NotificationVariant.SUCCESS);
    }

    private void show(String text, NotificationVariant variant) {
        var notification = Notification.show(text, NOTIFICATION_DURATION_MS,
                Notification.Position.BOTTOM_END);
        notification.addThemeVariants(variant);
    }
}
