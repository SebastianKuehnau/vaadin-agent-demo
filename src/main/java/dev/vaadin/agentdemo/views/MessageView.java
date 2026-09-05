package dev.vaadin.agentdemo.views;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Shows the content of a text field as a notification.
 */
@Route("")
@PageTitle("Message")
@Menu(order = 0, title = "Message")
public class MessageView extends VerticalLayout {

    static final String EMPTY_MESSAGE_ERROR = "Please enter a message first";
    private static final int DURATION_MS = 3000;

    private final TextField messageField = new TextField("Message");
    private final Button showButton = new Button("Show notification");

    public MessageView() {
        setPadding(true);
        setSpacing(true);

        messageField.setPlaceholder("Type a message");
        messageField.setClearButtonVisible(true);
        messageField.setErrorMessage(EMPTY_MESSAGE_ERROR);
        messageField.setWidth("20em");
        // Clear the error state as soon as the user starts typing again
        messageField.addValueChangeListener(event -> messageField.setInvalid(false));

        showButton.addThemeVariants(ButtonVariant.PRIMARY);
        showButton.addClickListener(event -> showMessage());

        add(new H2("Message"), messageField, showButton);
    }

    private void showMessage() {
        String message = messageField.getValue().trim();
        if (message.isEmpty()) {
            messageField.setInvalid(true);
            messageField.focus();
            show(EMPTY_MESSAGE_ERROR, NotificationVariant.ERROR);
            return;
        }
        messageField.setInvalid(false);
        show(message, NotificationVariant.SUCCESS);
    }

    private static void show(String text, NotificationVariant variant) {
        Notification notification = new Notification(text, DURATION_MS,
                Notification.Position.BOTTOM_END);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
