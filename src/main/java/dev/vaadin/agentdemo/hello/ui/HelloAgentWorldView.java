package dev.vaadin.agentdemo.hello.ui;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

/**
 * Greets the user: enter a name, click Show, and a success notification
 * says "Hello &lt;name&gt;!". An empty name is rejected on the field.
 */
@Route("hello")
@PageTitle("Hello Agent World")
@Menu(order = 0, icon = "vaadin:hand", title = "Hello Agent World")
public class HelloAgentWorldView extends HorizontalLayout {

    static final String EMPTY_NAME_ERROR = "Please enter a name";

    private final TextField name = new TextField("Name");

    public HelloAgentWorldView() {
        setPadding(true);
        setDefaultVerticalComponentAlignment(FlexComponent.Alignment.BASELINE);

        name.setRequiredIndicatorVisible(true);
        name.setClearButtonVisible(true);
        name.addValueChangeListener(e -> name.setInvalid(false));

        var show = new Button("Show", e -> greet());
        show.addThemeVariants(ButtonVariant.PRIMARY);
        show.addClickShortcut(Key.ENTER);

        add(name, show);
    }

    private void greet() {
        var value = name.getValue().strip();
        if (value.isEmpty()) {
            name.setErrorMessage(EMPTY_NAME_ERROR);
            name.setInvalid(true);
            name.focus();
            return;
        }
        var notification = Notification.show("Hello " + value + "!", 3000,
                Notification.Position.BOTTOM_END);
        notification.addThemeVariants(NotificationVariant.SUCCESS);
    }
}
