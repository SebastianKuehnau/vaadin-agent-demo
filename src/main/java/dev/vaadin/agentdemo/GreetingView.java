package dev.vaadin.agentdemo;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("greeting")
@PageTitle("Greeting")
@Menu(title = "Greeting", order = 10, icon = "vaadin:comment")
public class GreetingView extends VerticalLayout {

    final TextField message = new TextField("Message");
    final Button send = new Button("Send");

    public GreetingView() {
        message.setId("message-field");
        message.setPlaceholder("What should be displayed?");
        message.setRequiredIndicatorVisible(true);
        message.setValueChangeMode(ValueChangeMode.EAGER);
        message.setErrorMessage("Please enter a message.");
        // Resolve the error as soon as the user starts typing again
        message.addValueChangeListener(event -> message.setInvalid(false));

        send.setId("send-button");
        send.addThemeVariants(ButtonVariant.PRIMARY);
        send.addClickShortcut(Key.ENTER);
        send.addClickListener(event -> showMessage());

        add(message, send);
    }

    private void showMessage() {
        String value = message.getValue().trim();

        if (value.isEmpty()) {
            message.setInvalid(true);
            message.focus();
            return;
        }

        message.setInvalid(false);
        Notification notification = Notification.show(
                value, 3000, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.SUCCESS);
    }
}