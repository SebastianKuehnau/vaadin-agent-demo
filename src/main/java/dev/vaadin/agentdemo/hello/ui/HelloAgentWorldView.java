package dev.vaadin.agentdemo.hello.ui;

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
 * Greets the user by name with a success notification.
 */
@Route("hello-agent-world")
@PageTitle("Hello Agent World")
@Menu(title = "Hello Agent World", icon = "vaadin:hand")
public class HelloAgentWorldView extends HorizontalLayout {

    static final String EMPTY_NAME_ERROR = "Please enter a name";

    private final TextField name = new TextField("Name");
    private final Button show = new Button("Show");

    public HelloAgentWorldView() {
        name.setClearButtonVisible(true);
        name.setErrorMessage(EMPTY_NAME_ERROR);
        name.addValueChangeListener(e -> name.setInvalid(false));

        show.addThemeVariants(ButtonVariant.PRIMARY);
        show.addClickShortcut(Key.ENTER);
        show.addClickListener(e -> showGreeting());

        setPadding(true);
        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);
        add(name, show);
    }

    private void showGreeting() {
        var value = name.getValue().strip();
        if (value.isEmpty()) {
            name.setInvalid(true);
            name.focus();
            return;
        }
        name.setInvalid(false);
        var notification = Notification.show("Hello " + value + "!", 5000,
                Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.SUCCESS);
    }
}
