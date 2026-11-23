package dev.vaadin.agentdemo.hello.ui.view;

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
 * Greets the name entered in the field with a success notification
 * ("Hello &lt;name&gt;!") in the bottom right corner. Empty or blank input is
 * rejected with a field-level error instead of a notification.
 */
@Route("hello-agent-world")
@PageTitle("Hello Agent World")
@Menu(title = "Hello Agent World", order = 2, icon = "vaadin:hand")
public class HelloAgentWorldView extends VerticalLayout {

    static final String EMPTY_INPUT_ERROR = "Please enter a name";
    private static final int NOTIFICATION_DURATION_MS = 5000;

    final TextField name;
    final Button show;

    public HelloAgentWorldView() {
        name = new TextField("Name");
        name.setPlaceholder("Your name");
        name.setWidth(16, Unit.EM);
        name.setRequiredIndicatorVisible(true);
        name.setErrorMessage(EMPTY_INPUT_ERROR);
        // The view validates on click, so the field must not validate itself.
        name.setManualValidation(true);
        name.setValueChangeMode(ValueChangeMode.EAGER);
        name.addValueChangeListener(this::onNameValueChange);

        show = new Button("Show", this::onShowButtonClicked);
        show.addThemeVariants(ButtonVariant.PRIMARY);

        var controls = new HorizontalLayout(name, show);
        controls.setAlignItems(Alignment.BASELINE);

        add(controls);
    }

    static String greeting(String name) {
        return "Hello " + name + "!";
    }

    private void onShowButtonClicked(ClickEvent<Button> event) {
        var text = name.getValue().trim();
        if (text.isEmpty()) {
            name.setInvalid(true);
            name.focus();
            return;
        }

        showGreeting(text);
    }

    private void showGreeting(String text) {
        var notification = Notification.show(greeting(text),
                NOTIFICATION_DURATION_MS, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.SUCCESS);
    }

    private void onNameValueChange(
            AbstractField.ComponentValueChangeEvent<TextField, String> event) {
        name.setInvalid(false);
    }
}
