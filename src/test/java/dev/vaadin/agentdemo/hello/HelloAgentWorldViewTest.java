package dev.vaadin.agentdemo.hello;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class HelloAgentWorldViewTest extends SpringBrowserlessTest {

    @Test
    void enterName_clickShow_successNotificationShown() {
        var view = navigate(HelloAgentWorldView.class);

        test(view.name).setValue("Agent");
        test(view.show).click();

        Notification notification = find(Notification.class).single();
        assertEquals("Hello Agent!", test(notification).getText());
        assertEquals(Notification.Position.BOTTOM_END, notification.getPosition());
        assertTrue(notification.hasThemeName(NotificationVariant.SUCCESS.getVariantName()));
        assertFalse(view.name.isInvalid());
    }

    @Test
    void nameIsTrimmed() {
        var view = navigate(HelloAgentWorldView.class);

        test(view.name).setValue("  Agent  ");
        test(view.show).click();

        assertEquals("Hello Agent!", test(find(Notification.class).single()).getText());
    }

    @Test
    void emptyName_noNotification_fieldInvalid() {
        var view = navigate(HelloAgentWorldView.class);

        test(view.show).click();

        assertTrue(find(Notification.class).all().isEmpty());
        assertTrue(view.name.isInvalid());
        assertEquals(HelloAgentWorldView.EMPTY_NAME_ERROR, view.name.getErrorMessage());
    }

    @Test
    void blankName_noNotification_fieldInvalid() {
        var view = navigate(HelloAgentWorldView.class);

        test(view.name).setValue("   ");
        test(view.show).click();

        assertTrue(find(Notification.class).all().isEmpty());
        assertTrue(view.name.isInvalid());
    }

    @Test
    void showButtonIsPrimary() {
        var view = navigate(HelloAgentWorldView.class);

        assertTrue(view.show.hasThemeName(ButtonVariant.PRIMARY.getVariantName()));
    }
}
