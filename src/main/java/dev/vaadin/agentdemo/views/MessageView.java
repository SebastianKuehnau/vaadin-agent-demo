package dev.vaadin.agentdemo.views;

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
 * A view that shows the text entered by the user as a notification.
 */
@Route("")
@PageTitle("Message")
@Menu(title = "Message", icon = "vaadin:comment", order = 0)
public class MessageView extends VerticalLayout {

    private static final int NOTIFICATION_DURATION = 3000;

    final TextField message;
    final Button showButton;

    public MessageView() {
        message = new TextField("Message");
        message.setPlaceholder("Type something");
        message.setRequiredIndicatorVisible(true);
        message.setErrorMessage("Please enter a message");
        message.addValueChangeListener(event -> message.setInvalid(false));

        showButton = new Button("Show notification", event -> showMessage());
        showButton.addThemeVariants(ButtonVariant.PRIMARY);

        add(message, showButton);
    }

    private void showMessage() {
        var value = message.getValue().trim();
        if (value.isEmpty()) {
            message.setInvalid(true);
            message.focus();
            showNotification("Please enter a message", NotificationVariant.ERROR);
        } else {
            message.setInvalid(false);
            showNotification(value, NotificationVariant.SUCCESS);
        }
    }

    private void showNotification(String text, NotificationVariant variant) {
        var notification = new Notification(text, NOTIFICATION_DURATION,
                Notification.Position.BOTTOM_END);
        notification.addThemeVariants(variant);
        notification.open();
    }
}
