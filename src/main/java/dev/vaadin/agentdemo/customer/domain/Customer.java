package dev.vaadin.agentdemo.customer.domain;

import java.time.LocalDate;

/**
 * A customer as listed in the customer management grid.
 *
 * <p>The project has no persistence layer yet, so this mirrors exactly the
 * four columns of the Figma design (node 1:825): name, email, last purchase
 * date and organization.
 */
public record Customer(String name, String email, LocalDate lastPurchase,
                       String organization) {
}
