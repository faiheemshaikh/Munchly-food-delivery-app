# Features

## Implemented
**Customer**
- Register (validation, duplicate-email rejection, BCrypt), log in, log out (session destroyed)
- Edit profile: name, phone, default delivery address
- Home page with 20 restaurants loaded from MySQL, live search and cuisine filter, empty and database-error states
- Restaurant menu page showing only that restaurant's available items, grouped by category; friendly 404 for bad ids
- Session cart: add, increase, decrease, remove, clear; badge in the navigation bar; server-side price recalculation
- Checkout with delivery address, transactional order creation with generated keys, order confirmation
- My Orders and order details, with server-side ownership enforcement

**Administrator** (role-protected)
- Dashboard with counts
- Add, edit, activate and deactivate restaurants
- Add, edit and change the price of menu items; mark items available or unavailable
- View all orders and order details; update order status

**Engineering**
- MVC + DAO, interfaces separate from implementations, `PreparedStatement` and try-with-resources throughout
- Filters for encoding, CSRF, authentication, authorization
- JSTL/EL-only JSPs (scriptlets disabled), responsive CSS, image fallback, flash messages, error pages
- Schema and seed scripts, external DB configuration, local-only connection test page

## Intentionally omitted (outside the stated scope)
- Online payments (orders are pay-on-delivery), refunds, coupons, taxes, delivery fees
- Live GPS tracking, delivery-partner management, push or email notifications
- Ratings and reviews written by customers (ratings are set by the admin)
- Multi-restaurant carts (a cart holds one restaurant's items)
- Password reset, email verification, social login, two-factor authentication
- Customer-side order cancellation
- Image upload (admins enter a path to an image file placed in the `images` folder)
- Admin screens for managing users (roles are changed in SQL)
- Connection pooling, caching, pagination, internationalisation
- Automated unit and integration test suite (testing is by the manual checklist)

## Implemented but not used by any screen
`UserDAO.deactivateUser` and `OrderItemDAO.getOrderItemsByOrderId` exist as part of the DAO API (the spec asked for them) but no page calls them yet.
