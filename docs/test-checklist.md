# Test Checklist

Tick each item. The numbered acceptance criteria (A1-A16) come from the project brief.
Use two customer accounts (A and B), and one admin.

## Setup
- [ ] MySQL running; `schema.sql` then `seed.sql` executed without errors
- [ ] `db.properties` created with your own credentials
- [ ] Project imports into Eclipse; facets: Dynamic Web Module 6.0, Java 17; Tomcat 10.1 runtime
- [ ] `/db-test` shows connected, 20 restaurants, 122 menu items

## Acceptance criteria
- [ ] **A1** MySQL and Tomcat start with no errors in the console
- [ ] **A2** `http://localhost:8080/food-delivery-app/` opens the home page
- [ ] **A3** Register a new customer: weak password rejected, duplicate email rejected, valid data redirects to login
- [ ] **A4** Log in; nav shows "Hi, name"; `SELECT password_hash FROM users` shows a `$2a$12$...` hash
- [ ] **A5** Home page shows 20 restaurant cards (inactive "Old Town Diner" absent)
- [ ] **A6** Open Dosa Corner then Pizza Piazza: each menu shows only its own 6 items
- [ ] **A7** Add several items; cart badge updates
- [ ] **A8** Cart: increase, decrease (to 0 removes), remove, clear
- [ ] **A9** Cart total equals the sum of price x quantity
- [ ] **A10** Checkout: address under 10 characters is rejected; valid address places the order
- [ ] **A11** Database: exactly one new `orders` row and matching `order_items` rows with correct `unit_price`, `quantity`, `subtotal`; `orders.total_amount` = SUM of the items
- [ ] **A12** Confirmation page shows order number, items and total; F5 does not create a second order
- [ ] **A13** My Orders lists the new order; details page shows its items
- [ ] **A14** Log out; then `/cart`, `/checkout`, `/orders`, `/profile` redirect to the login page; Back button shows no private data; after logging in you return to the page you wanted
- [ ] **A15** User B opening A's `/orders/details?orderId=N` and `/order-confirmation?orderId=N` gets the 404 page
- [ ] **A16** Stop MySQL and reload `/home`: friendly message, no stack trace. Invalid input (`/menu?restaurantId=abc`, `/menu?restaurantId=9999`, bad forms) never shows a stack trace

## Cart and checkout integrity
- [ ] Adding from a second restaurant is refused with a message
- [ ] Quantity cannot exceed 20 per item
- [ ] Change a price in MySQL: the cart shows the new price; an already-placed order keeps the old `unit_price`
- [ ] Mark an item unavailable while it is in the cart: it is removed with a notice
- [ ] Price changes between viewing checkout and pressing Place order: "Prices ... have changed" and no order is created
- [ ] Rollback demo: make the second item insert fail temporarily (see Phase 6 notes); no row remains in `orders`
- [ ] Logging out empties the cart

## Security
- [ ] Edit the hidden `csrfToken` in a form and submit: 403 page
- [ ] `/login?next=//evil.com` then log in: lands on `/home`
- [ ] A name like `<script>alert(1)</script>` in the profile or an admin form is displayed as text, not executed
- [ ] Customer visiting `/admin`, `/admin/orders`, `/admin/restaurants`: 403 page
- [ ] Demoted admin (`UPDATE users SET role='CUSTOMER'`) loses `/admin` access on the next request without logging out
- [ ] Admin image path `images/../WEB-INF/web.xml` or `http://x/y.jpg` is rejected
- [ ] `/WEB-INF/views/home.jsp` cannot be opened directly (404)
- [ ] Passwords never appear in logs or pages

## Administrator
- [ ] Dashboard counts look right
- [ ] Deactivate a restaurant: disappears from home, `/menu?restaurantId=N` gives 404; activate: returns
- [ ] Add restaurant (validation errors on blank name, rating 7); edit restaurant
- [ ] Add menu item (price `abc`, `-5`, `10.999` rejected); edit price; mark unavailable and available
- [ ] Update an order's status; customer sees the new status on My Orders
- [ ] Admin can open any order's details

## Responsive and visual
- [ ] Narrow the browser to phone width: cards, menu, cart and tables remain usable (tables scroll sideways)
- [ ] Missing images show the placeholder, never a broken icon
- [ ] Empty states: no orders yet, empty cart, restaurant without items
