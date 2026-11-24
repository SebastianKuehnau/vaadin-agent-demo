package dev.vaadin.agentdemo.hello;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Greets the user by name: entering a name and clicking "Show" opens a
 * success notification saying "Hello &lt;name&gt;!".
 */
@Route("hello-agent-world")
@PageTitle("Hello Agent World")
@Menu(order = 0, icon = "vaadin:hand", title = "Hello Agent World")
public class HelloAgentWorldView extends HorizontalLayout {

    static final String EMPTY_NAME_ERROR = "Please enter a name";

    final TextField name = new TextField("Name");
    final Button show = new Button("Show");

    public HelloAgentWorldView() {
        name.setRequiredIndicatorVisible(true);
        name.setManualValidation(true);
        name.setClearButtonVisible(true);
        name.addValueChangeListener(e -> name.setInvalid(false));

        show.addThemeVariants(ButtonVariant.PRIMARY);
        show.addClickListener(e -> showGreeting());
        show.addClickShortcut(Key.ENTER);

        setPadding(true);
        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        add(name, show);
    }

    private void showGreeting() {
        var value = name.getValue() == null ? "" : name.getValue().strip();
        if (value.isEmpty()) {
            name.setErrorMessage(EMPTY_NAME_ERROR);
            name.setInvalid(true);
            name.focus();
            return;
        }
        name.setInvalid(false);
        var notification = Notification.show("Hello " + value + "!", 3000,
                Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.SUCCESS);
    }
}
