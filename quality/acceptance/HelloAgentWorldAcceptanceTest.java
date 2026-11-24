package dev.vaadin.agentdemo.acceptance;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Independent acceptance check for the benchmark prompt. Not written by an agent and
 * never shown to one: it is copied into each run's working tree afterwards and run
 * there. It locates the generated view by name and works only through public Vaadin
 * API, so it does not depend on the package, field names or route the agent chose.
 *
 * R1–R9 are the requirements of the prompt; B1–B2 are extras the prompt does not ask
 * for and are reported separately.
 */
@SpringBootTest
class HelloAgentWorldAcceptanceTest extends SpringBrowserlessTest {

    private static final String NAME = "Sebastian";

    // ---- R1–R4: structure -------------------------------------------------------

    @Test
    void r1_viewNamedHelloAgentWorldWithRoute() {
        assertNotNull(viewClass(), "no @Route class whose name contains HelloAgentWorld");
    }

    @Test
    void r2_viewListedInRenderedSideNavigation() {
        var view = open();
        var url = RouteConfiguration.forApplicationScope().getUrl(view.getClass());
        var items = descendants(UI.getCurrent(), SideNavItem.class);
        // SideNavItems may be held by a SideNav without being in the child stream.
        descendants(UI.getCurrent(), SideNav.class).forEach(nav -> items.addAll(nav.getItems()));
        assertTrue(items.stream().anyMatch(i -> url.equals(strip(i.getPath()))),
                "no SideNavItem links to /" + url + " (items: "
                        + items.stream().map(SideNavItem::getPath).toList() + ")");
    }

    @Test
    void r3_textFieldAndShowButton() {
        var view = open();
        assertEquals(1, descendants(view, TextField.class).size(), "exactly one TextField in the view");
        showButton(view);
    }

    @Test
    void r4_buttonIsPrimary() {
        assertTrue(showButton(open()).getThemeNames().contains("primary"), "button lacks theme 'primary'");
    }

    // ---- R5–R7: valid input -------------------------------------------------------

    @Test
    void r5_greetingTextIsHelloNameExclamation() {
        assertEquals("Hello " + NAME + "!", test(submit(NAME)).getText());
    }

    @Test
    void r6_notificationIsSuccess() {
        assertTrue(submit(NAME).getThemeNames().contains("success"), "notification lacks theme 'success'");
    }

    @Test
    void r7_notificationBottomRight() {
        assertEquals(Notification.Position.BOTTOM_END, submit(NAME).getPosition());
    }

    // ---- R8–R9: empty input -------------------------------------------------------

    @Test
    void r8_emptyInputShowsNoGreeting() {
        var view = open();
        test(showButton(view)).click();
        assertTrue(openNotifications().stream().noneMatch(this::isGreeting),
                "a greeting/success notification was shown for empty input");
    }

    @Test
    void r9_emptyInputGivesVisibleFeedback() {
        var view = open();
        var field = descendants(view, TextField.class).getFirst();
        test(showButton(view)).click();
        var fieldFlagged = field.isInvalid();
        var errorNotification = openNotifications().stream().anyMatch(n -> !isGreeting(n));
        assertTrue(fieldFlagged || errorNotification,
                "empty input: field not invalid and no error notification");
    }

    // ---- B1–B2: extras the prompt does not require ----------------------------------

    @Test
    void b1_blankInputShowsNoGreeting() {
        var view = open();
        test(descendants(view, TextField.class).getFirst()).setValue("   ");
        test(showButton(view)).click();
        assertTrue(openNotifications().stream().noneMatch(this::isGreeting),
                "greeting shown for whitespace-only input");
    }

    @Test
    void b2_nameIsTrimmed() {
        assertEquals("Hello " + NAME + "!", test(submit("  " + NAME + "  ")).getText());
    }

    // ---- helpers -------------------------------------------------------------------

    private Class<? extends Component> viewClass() {
        var scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Route.class));
        var found = scanner.findCandidateComponents("dev.vaadin.agentdemo").stream()
                .map(bd -> bd.getBeanClassName())
                .filter(n -> n.substring(n.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT).contains("helloagentworld"))
                .toList();
        if (found.size() != 1) {
            return null;
        }
        try {
            @SuppressWarnings("unchecked")
            var c = (Class<? extends Component>) Class.forName(found.getFirst());
            return c;
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    private Component open() {
        var c = viewClass();
        if (c == null) {
            fail("view not found");
        }
        return navigate(c);
    }

    private Button showButton(Component view) {
        var buttons = descendants(view, Button.class).stream()
                .filter(b -> b.getText() != null && b.getText().trim().toLowerCase(Locale.ROOT).contains("show"))
                .toList();
        assertEquals(1, buttons.size(), "exactly one button labelled 'Show' in the view");
        return buttons.getFirst();
    }

    private Notification submit(String value) {
        var view = open();
        test(descendants(view, TextField.class).getFirst()).setValue(value);
        test(showButton(view)).click();
        var shown = openNotifications();
        assertEquals(1, shown.size(), "expected exactly one open notification, got " + shown.size());
        return shown.getFirst();
    }

    private List<Notification> openNotifications() {
        return descendants(UI.getCurrent(), Notification.class).stream().filter(Notification::isOpened).toList();
    }

    private boolean isGreeting(Notification n) {
        var text = test(n).getText();
        return n.getThemeNames().contains("success") || (text != null && text.startsWith("Hello"));
    }

    private static String strip(String path) {
        return path == null ? null : path.replaceAll("^/+", "");
    }

    private static <T> List<T> descendants(Component root, Class<T> type) {
        var out = new ArrayList<T>();
        walk(root).filter(type::isInstance).map(type::cast).forEach(out::add);
        return out;
    }

    private static Stream<Component> walk(Component c) {
        return Stream.concat(Stream.of(c), c.getChildren().flatMap(HelloAgentWorldAcceptanceTest::walk));
    }
}
