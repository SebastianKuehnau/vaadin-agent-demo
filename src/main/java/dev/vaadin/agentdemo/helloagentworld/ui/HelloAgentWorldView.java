package dev.vaadin.agentdemo.helloagentworld.ui;

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
 * Greets the user: enter a name, press Show, and a success notification
 * says hello. An empty name marks the field invalid instead.
 */
@Route("hello-agent-world")
@PageTitle("Hello Agent World")
@Menu(title = "Hello Agent World", icon = "vaadin:hand", order = 1)
public class HelloAgentWorldView extends HorizontalLayout {

    static final String EMPTY_NAME_ERROR = "Please enter a name";

    private final TextField name = new TextField("Name");

    public HelloAgentWorldView() {
        setPadding(true);
        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        name.setErrorMessage(EMPTY_NAME_ERROR);
        name.addValueChangeListener(event -> name.setInvalid(false));

        var show = new Button("Show", event -> greet());
        show.addThemeVariants(ButtonVariant.PRIMARY);
        show.addClickShortcut(Key.ENTER);

        add(name, show);
    }

    private void greet() {
        var enteredName = name.getValue().strip();
        if (enteredName.isEmpty()) {
            name.setInvalid(true);
            name.focus();
            return;
        }
        var notification = Notification.show("Hello " + enteredName + "!",
                3000, Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.SUCCESS);
    }
}
