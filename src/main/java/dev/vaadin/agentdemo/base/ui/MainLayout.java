package dev.vaadin.agentdemo.base.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

/**
 * The application shell. The drawer holds the application name above the
 * navigation, which is generated from the {@code @Menu} annotations of the
 * views. The navbar sits next to the drawer and shows the drawer toggle
 * followed by the name of the current view.
 */
@Layout
public class MainLayout extends AppLayout implements AfterNavigationObserver {

    private final H1 viewTitle = new H1();

    public MainLayout() {
        // Makes the drawer the full-height section, so the navbar starts at
        // the left edge of the content area instead of the viewport.
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        var toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle.addClassName("view-title");

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        var appName = new H1("Vaadin Agent Demo");
        appName.addClassName("app-name");

        addToDrawer(new Header(appName), new Scroller(createNavigation()));
    }

    private SideNav createNavigation() {
        var nav = new SideNav();
        MenuConfiguration.getMenuEntries().forEach(entry -> {
            SideNavItem item = new SideNavItem(entry.title(),
                    entry.menuClass());
            if (entry.icon() != null) {
                item.setPrefixComponent(new Icon(entry.icon()));
            }
            nav.addItem(item);
        });
        return nav;
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        viewTitle.setText(currentViewName());
    }

    /**
     * The name of the current view as listed in the side navigation. Views
     * without a {@code @Menu} entry fall back to their page title.
     */
    private String currentViewName() {
        var content = getContent();
        if (content == null) {
            return "";
        }
        return MenuConfiguration.getMenuEntries().stream()
                .filter(entry -> entry.menuClass().equals(content.getClass()))
                .map(MenuEntry::title)
                .findFirst()
                .orElseGet(() -> MenuConfiguration.getPageHeader(content)
                        .orElse(""));
    }

}
