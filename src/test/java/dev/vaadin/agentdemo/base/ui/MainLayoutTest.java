package dev.vaadin.agentdemo.base.ui;

import java.util.stream.Stream;

import com.vaadin.browserless.SpringBrowserlessTest;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;
import dev.vaadin.agentdemo.helloagentworld.ui.view.HelloAgentWorldView;
import dev.vaadin.agentdemo.message.ui.view.MessageView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MainLayoutTest extends SpringBrowserlessTest {

    static Stream<Class<? extends Component>> views() {
        return Stream.of(MessageView.class, HelloAgentWorldView.class);
    }

    @ParameterizedTest
    @MethodSource("views")
    void navigateToView_navbarShowsTheNameFromTheSideNavigation(
            Class<? extends Component> view) {
        navigate(view);

        String menuName = menuEntry(view).title();

        Assertions.assertEquals(menuName, viewTitle(),
                "Navbar title should be the view's name from the side navigation");
    }

    @ParameterizedTest
    @MethodSource("views")
    void navigateToView_navbarTitleMatchesTheSideNavItem(
            Class<? extends Component> view) {
        navigate(view);

        SideNavItem item = sideNavItem(view);

        Assertions.assertEquals(item.getLabel(), viewTitle(),
                "Navbar title and side navigation label should be the same text");
    }

    /**
     * The navbar title. Selected by its class name, because the drawer holds
     * the application name in an H1 as well.
     */
    private String viewTitle() {
        return find(H1.class).withClassName("view-title").single().getText();
    }

    private MenuEntry menuEntry(Class<? extends Component> view) {
        return MenuConfiguration.getMenuEntries().stream()
                .filter(entry -> entry.menuClass().equals(view)).findFirst()
                .orElseThrow();
    }

    /**
     * The side navigation item for a view, matched on the route it links to.
     * A {@code SideNavItem} reports its path without the leading slash that
     * a {@link MenuEntry} carries.
     */
    private SideNavItem sideNavItem(Class<? extends Component> view) {
        var path = menuEntry(view).path();
        return find(SideNav.class).single().getItems().stream()
                .filter(item -> ("/" + item.getPath()).equals(path))
                .reduce((first, second) -> {
                    throw new AssertionError(
                            "More than one side navigation item links to "
                                    + path);
                })
                .orElseThrow(() -> new AssertionError(
                        "No side navigation item links to " + path));
    }

}
