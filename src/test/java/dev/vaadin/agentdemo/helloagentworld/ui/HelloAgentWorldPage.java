package dev.vaadin.agentdemo.helloagentworld.ui;

import com.vaadin.browserless.BrowserlessUIContext;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.sidenav.SideNavItem;

/** Where things are on {@link HelloAgentWorldView}. */
class HelloAgentWorldPage {

    private final BrowserlessUIContext window;

    HelloAgentWorldPage(BrowserlessUIContext window) {
        this.window = window;
    }

    void open() {
        window.navigate(HelloAgentWorldView.class);
    }

    void enterName(String name) {
        window.findTextField().withLabel("Name").setValue(name);
    }

    void clickShow() {
        window.findButton().withText("Show").click();
    }

    boolean showButtonIsPrimary() {
        return window.findButton().withText("Show").component()
                .getThemeNames().contains("primary");
    }

    boolean hasNotification() {
        return window.findNotification().exists();
    }

    String notification() {
        return window.findNotification().getText();
    }

    boolean notificationIsSuccessInBottomRight() {
        Notification notification = window.findNotification()
                .withTheme(NotificationVariant.SUCCESS).component();
        return notification.getPosition() == Notification.Position.BOTTOM_END;
    }

    boolean nameIsMarkedInvalidWith(String message) {
        var field = window.findTextField().withLabel("Name").component();
        return field.isInvalid() && message.equals(field.getErrorMessage());
    }

    boolean navigationOffers(String title) {
        return window.find(SideNavItem.class)
                .withCondition(item -> title.equals(item.getLabel())).exists();
    }
}
