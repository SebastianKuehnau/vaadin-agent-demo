package dev.vaadin.agentdemo.message.ui.view;

import com.vaadin.flow.component.AbstractField;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Shows the text entered in the field as a success notification in the bottom
 * right corner. Empty or blank input is rejected with a field-level error
 * instead of an empty notification.
 */
@Route("")
@PageTitle("Message")
@Menu(title = "Message", order = 1, icon = "vaadin:comment")
public class MessageView extends VerticalLayout {

    static final String EMPTY_INPUT_ERROR = "Please enter a message";
    private static final int NOTIFICATION_DURATION_MS = 5000;

    final TextField message;
    final Button show;

    public MessageView() {
        message = new TextField("Message");
        message.setPlaceholder("Type something");
        message.setWidth(16, Unit.EM);
        message.setRequiredIndicatorVisible(true);
        message.setErrorMessage(EMPTY_INPUT_ERROR);
        // The view validates on click, so the field must not validate itself.
        message.setManualValidation(true);
        message.setValueChangeMode(ValueChangeMode.EAGER);
        message.addValueChangeListener(this::onMessageValueChange);

        show = new Button("Show notification", this::onShowButtonClicked);
        show.addThemeVariants(ButtonVariant.PRIMARY);

        var controls = new HorizontalLayout(message, show);
        controls.setAlignItems(Alignment.BASELINE);

        add(controls);
    }

    private void onShowButtonClicked(ClickEvent<Button> buttonClickEvent) {
        var text = message.getValue().trim();
        if (text.isEmpty()) {
            message.setInvalid(true);
            message.focus();
            return;
        }

        showNotification(text);
    }

    private void showNotification(String text) {
        var notification = Notification.show(text,
                NOTIFICATION_DURATION_MS, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.SUCCESS);
    }

    private void onMessageValueChange(AbstractField.ComponentValueChangeEvent<TextField, String> textFieldStringComponentValueChangeEvent) {
        message.setInvalid(false);
    }
}
