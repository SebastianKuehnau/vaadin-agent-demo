package dev.vaadin.agentdemo.base.ui;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.Layout;
import com.vaadin.flow.server.menu.MenuConfiguration;

/**
 * The application shell. Its navigation is generated from the {@code @Menu}
 * annotations of the views, so adding a view to the menu requires no change
 * here.
 */
@Layout
public class MainLayout extends AppLayout {

    public MainLayout() {
        H1 title = new H1("Vaadin Agent Demo");
        title.getStyle().set("font-size", "var(--aura-font-size-l)")
                .set("font-weight", "var(--aura-font-weight-semibold)")
                .set("margin", "0");

        addToNavbar(new DrawerToggle(), title);
        addToDrawer(createSideNav());
    }

    private SideNav createSideNav() {
        SideNav nav = new SideNav();
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

}
