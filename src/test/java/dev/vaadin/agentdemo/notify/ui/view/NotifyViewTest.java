package dev.vaadin.agentdemo.notify.ui.view;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class NotifyViewTest extends SpringBrowserlessTest {

    private static MenuEntry menuEntry() {
        return MenuConfiguration.getMenuEntries().stream()
                .filter(entry -> entry.menuClass().equals(NotifyView.class))
                .findFirst()
                .orElseThrow(() -> new AssertionError(
                        "View should have a menu entry"));
    }

    @Test
    void view_isListedInSideNavigation() {
        navigate(NotifyView.class);

        var title = menuEntry().title();
        Assertions.assertTrue(
                find(SideNavItem.class).all().stream()
                        .anyMatch(item -> title.equals(item.getLabel())),
                "Side navigation should contain an item for the view");
    }

    @Test
    void view_titleShownInNavbar() {
        navigate(NotifyView.class);

        var viewTitle = find(H1.class).withClassName("view-title").single();
        Assertions.assertEquals(menuEntry().title(), viewTitle.getText(),
                "Navbar should show the menu title of the current view");
    }

    @Test
    void enterText_clickButton_successNotificationShownInBottomRight() {
        var view = navigate(NotifyView.class);

        test(view.message).setValue("Hello Vaadin");
        test(view.show).click();

        var notification = find(Notification.class).single();
        Assertions.assertEquals("Hello Vaadin", test(notification).getText(),
                "Notification should show the entered text");
        Assertions.assertEquals(Notification.Position.BOTTOM_END,
                notification.getPosition(),
                "Notification should appear in the bottom right");
        Assertions.assertTrue(notification.getThemeNames()
                        .contains(NotificationVariant.SUCCESS.getVariantName()),
                "Notification should use the success theme variant");
        Assertions.assertFalse(view.message.isInvalid(),
                "Field should not be marked invalid for valid input");
    }

    @Test
    void button_isPrimary() {
        var view = navigate(NotifyView.class);

        Assertions.assertTrue(view.show.getThemeNames().contains("primary"),
                "Button should use the primary theme variant");
    }

    @Test
    void emptyInput_clickButton_noNotificationAndFieldInvalid() {
        var view = navigate(NotifyView.class);

        test(view.show).click();

        Assertions.assertFalse(find(Notification.class).exists(),
                "No notification should be shown for empty input");
        Assertions.assertTrue(view.message.isInvalid(),
                "Field should be marked invalid for empty input");
        Assertions.assertEquals(NotifyView.EMPTY_INPUT_ERROR,
                view.message.getErrorMessage(),
                "Field should explain why the input was rejected");
    }

    @Test
    void blankInput_clickButton_noNotificationAndFieldInvalid() {
        var view = navigate(NotifyView.class);

        test(view.message).setValue("   ");
        test(view.show).click();

        Assertions.assertFalse(find(Notification.class).exists(),
                "No notification should be shown for whitespace-only input");
        Assertions.assertTrue(view.message.isInvalid(),
                "Field should be marked invalid for whitespace-only input");
    }

    @Test
    void invalidField_typeText_errorCleared() {
        var view = navigate(NotifyView.class);
        test(view.show).click();

        test(view.message).setValue("Hello again");

        Assertions.assertFalse(view.message.isInvalid(),
                "Typing should clear the error");
    }
}
