package dev.vaadin.agentdemo;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.browserless.ViewPackages;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import dev.vaadin.agentdemo.GreetingView;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ViewPackages(classes = GreetingView.class)
class GreetingViewTest extends SpringBrowserlessTest {

    @Test
    void enteredText_isShownInNotification() {
        GreetingView view = navigate(GreetingView.class);

        test(view.message).setValue("Hallo Vaadin");
        test(view.send).click();

        Notification notification = find(Notification.class).single();
        assertEquals("Hallo Vaadin", test(notification).getText());
        assertTrue(notification.getThemeNames()
                .contains(NotificationVariant.SUCCESS.getVariantName()));
    }

    @Test
    void emptyInput_showsNoNotificationAndMarksFieldInvalid() {
        GreetingView view = navigate(GreetingView.class);

        test(view.send).click();

        assertTrue(find(Notification.class).all().isEmpty());
        assertTrue(view.message.isInvalid());
    }

    @Test
    void blankInput_isTreatedAsEmpty() {
        GreetingView view = navigate(GreetingView.class);

        test(view.message).setValue("   ");
        test(view.send).click();

        assertTrue(find(Notification.class).all().isEmpty());
        assertTrue(view.message.isInvalid());
    }
}