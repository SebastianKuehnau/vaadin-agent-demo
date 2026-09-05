package dev.vaadin.agentdemo.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;
import com.vaadin.flow.server.menu.MenuEntry;

/**
 * The main layout of the application, providing the navigation drawer shared by
 * all views. Navigation items are built from the views annotated
 * with {@link com.vaadin.flow.router.Menu}.
 */
@Layout
public class MainLayout extends AppLayout {

    public MainLayout() {
        H1 appName = new H1("Vaadin Agent Demo");
        appName.getStyle().set("font-size", "var(--lumo-font-size-l)")
                .set("margin", "0");
        addToNavbar(new DrawerToggle(), appName);

        SideNav nav = new SideNav();
        for (MenuEntry entry : MenuConfiguration.getMenuEntries()) {
            nav.addItem(new SideNavItem(entry.title(), entry.path()));
        }
        addToDrawer(new Scroller(nav));
    }
}
