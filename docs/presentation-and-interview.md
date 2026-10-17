# Presentation Outline and Interview Preparation

## Part 1: Presentation outline (about 10 to 12 minutes)

1. **Title and problem (1 min).** TapBite: order food from local restaurants online. Built with Servlets, JSP, JDBC and MySQL, with no frameworks, to understand how web apps really work.
2. **Features (1 min).** Customer flow (register, browse, cart, checkout, orders) and the admin panel. Mention what is deliberately out of scope: payments, GPS tracking, delivery partners.
3. **Tech stack and why (1 min).** Java 17, Tomcat 10.1 (Jakarta), MySQL, Maven. Why no Spring: to practise the fundamentals that frameworks hide.
4. **Architecture (2 min).** Draw the flow: Browser, Filters, Servlet (controller), DAO, MySQL, back to the JSP. Explain MVC and why SQL lives only in DAOs.
5. **Database design (1 min).** Show the 5 tables and relationships; `DECIMAL` for money; foreign keys with `RESTRICT`; `unit_price` stored in `order_items`.
6. **Live demo (4 min).** Follow the script below.
7. **Security and correctness (1.5 min).** BCrypt, prepared statements, CSRF tokens, session fixation protection, ownership check in SQL, server-side price recalculation, the JDBC transaction.
8. **Limitations and future work (1 min).** HTTPS, rate limiting, connection pool, payment gateway, automated tests, moving to Spring Boot.
9. **Q&A.**

### Demo script
1. Home page: scroll the 20 restaurants, use the search box and a cuisine chip.
2. Open *Dosa Corner*: show only its items. Mention the SQL filter `WHERE restaurant_id = ?`.
3. Click **Log in to order**, sign up as a new user, and show you are returned to the menu.
4. Add 3 items. Show the cart badge, then the cart: change quantities, remove one.
5. Checkout: enter an address, place the order, and show the confirmation page.
6. Switch to MySQL Workbench: run the three queries from the Phase 6 notes to show the `orders` row and `order_items` rows, and that the totals match.
7. **My Orders** and details. Then open another customer's order URL and show the 404.
8. Log out, then open `/orders`: redirected to login. Press Back: nothing private is shown.
9. Admin: change a menu price and mark an item unavailable; show the customer menu update; update an order's status.
10. (Optional, impressive) Show the rollback demo: break the second insert and show no orphan order row is left.

### What to have ready
- MySQL Workbench open with the verification queries saved.
- Two browser windows (or one normal and one private): customer A and customer B / admin.
- A few real images in the `images` folders so the screenshots look good.

---

## Part 2: Interview questions and answers

**1. Explain MVC in your project.**
The model is the POJOs (`User`, `MenuItem`, ...) plus the DAOs that load them. The view is the JSP pages, which only display data using JSTL and EL. The controllers are servlets: they read and validate the request, call DAOs or `CartService`, put results in request attributes, then forward to a JSP or redirect. JSPs never contain SQL or business logic.

**2. What is the DAO pattern and why use it?**
A DAO hides all database access behind an interface (`MenuDAO`) with a JDBC implementation (`MenuDAOImpl`). Servlets don't know SQL. If the database or queries change, only the DAO layer changes, and the interface makes the code easier to test and swap.

**3. Trace a request for `/menu?restaurantId=5`.**
The browser sends GET. `EncodingFilter` sets UTF-8, `CsrfFilter` puts a token in the request. `MenuServlet` parses the id with `parsePositiveInt`, loads the restaurant (404 if missing or inactive), loads that restaurant's available items with `WHERE restaurant_id = ? AND available = 1`, groups them by category, stores them as request attributes, and forwards to `menu.jsp`, which loops with `<c:forEach>` and writes the HTML.

**4. Forward vs redirect?**
A forward happens on the server inside the same request: the URL doesn't change and request attributes are kept. A redirect tells the browser to make a new request to another URL, so attributes are lost (that's why I use session "flash" messages). I forward to JSPs and redirect after every POST.

**5. What is Redirect-after-POST and why use it?**
After handling a POST, the servlet redirects to a GET page. If the user refreshes, the browser repeats the harmless GET instead of re-submitting the form. It prevents duplicate orders and duplicate cart additions.

**6. Why are the JSPs inside `WEB-INF`?**
Files under `WEB-INF` can't be requested directly by URL. Every page must go through a servlet, so filters run and the data is loaded first.

**7. How do you prevent SQL injection?**
Every query with parameters uses `PreparedStatement` with `?` placeholders, so user input is sent as data, never concatenated into SQL.

**8. How are passwords stored?**
With BCrypt (cost 12). BCrypt is deliberately slow and adds a random salt, so identical passwords get different hashes and brute force is expensive. The hash string includes the salt and cost, so nothing else is stored. Login uses `BCrypt.verifyer().verify(...)`.

**9. What is session fixation and how do you stop it?**
An attacker plants a known session id in the victim's browser, then reuses it after the victim logs in. I call `request.changeSessionId()` at login so the id changes at the moment of authentication.

**10. What is CSRF and how did you defend against it?**
A malicious site makes the victim's browser send a request to my site with the victim's cookies. I give each session a random token, include it as a hidden field in every form, and `CsrfFilter` rejects any POST whose token doesn't match. Another site can send the request but can't read the token.

**11. How do you prevent XSS?**
All dynamic text is output with `<c:out>`, which HTML-escapes it, including text in attributes. Scriptlets are disabled in `web.xml`, and image paths and `?next=` values are validated.

**12. Explain the order-creation transaction.**
`OrderDAOImpl.createOrder` gets one connection, calls `setAutoCommit(false)`, inserts the order with `RETURN_GENERATED_KEYS`, reads the new order id, inserts each order item with that id on the same connection, then `commit()`. If any statement throws, it calls `rollback()`, so there are never orders without their items. `finally` restores auto-commit and the try-with-resources closes the connection.

**13. Why must the items use the same connection as the order?**
A transaction belongs to one connection. If items were inserted through a different connection, they would be in a different transaction and couldn't be rolled back together. That's why `OrderItemDAO` has an `addOrderItem(Connection, OrderItem)` overload.

**14. What does ACID mean, and which part does your transaction show?**
Atomicity, Consistency, Isolation, Durability. Atomicity: all-or-nothing order creation. Consistency: foreign keys and CHECK constraints keep data valid. Durability: after commit the data survives a crash. Isolation: other users don't see a half-written order.

**15. Why do you not trust prices from the browser?**
A user can edit any form field. The cart stores only menu ids and quantities. On every cart page and at checkout, `CartService.buildView` reads the current names and prices from MySQL and computes totals. The total the browser sends is only used to detect that prices changed since the page was shown, never as the amount.

**16. Why store `unit_price` in `order_items` when `menu_items` has a price?**
Prices change over time, but an old order must show what was actually charged. So the price is copied into the order item at purchase time.

**17. Why `BigDecimal` and `DECIMAL`, not `double`?**
Binary floating point can't represent values like 0.10 exactly, which causes rounding errors in money. `BigDecimal` and SQL `DECIMAL` store exact decimal values.

**18. How do you make sure one customer can't see another's order?**
The user id comes from the session, never from the request. The DAO query is `WHERE order_id = ? AND user_id = ?`, so another user's order simply isn't found, and I return the same 404 as for a nonexistent order. This prevents IDOR (insecure direct object reference).

**19. How are admin pages protected?**
`AdminFilter` is mapped to `/admin/*`. It requires a logged-in user whose session role is `ADMIN`, and it also re-reads the user from the database to make sure the account is still an active admin. Admin servlets also validate every id and read an item's restaurant from the database, not from the form.

**20. What does a filter do, and how does the order work?**
A filter runs before (and after) a servlet and can stop the request. In `web.xml` the order of `<filter-mapping>` elements decides the order: encoding, CSRF, then auth or admin checks. I used `web.xml` for filters because annotation-based filter order isn't defined.

**21. Why Jakarta, and what changed from `javax`?**
Tomcat 10+ implements Jakarta EE 9+, where the packages were renamed from `javax.servlet` to `jakarta.servlet`. Code or libraries using `javax` won't run on Tomcat 10.1. That's why all imports and the JSTL URI (`jakarta.tags.core`) use Jakarta.

**22. Why is `jakarta.servlet-api` marked `provided`?**
Tomcat already contains the Servlet API. Packaging it in the WAR could cause class conflicts, so it's only used at compile time.

**23. Why use try-with-resources?**
`Connection`, `PreparedStatement` and `ResultSet` hold database resources. Try-with-resources closes them automatically, in reverse order, even when an exception happens, so connections don't leak.

**24. How do you handle errors without showing stack traces?**
DAOs catch `SQLException`, log the full details on the server, and throw a `DAOException` with a safe message. Servlets show friendly messages or send 404/500. `web.xml` maps 403, 404, 500 and `Throwable` to `error.jsp`, which shows plain text only.

**25. How does the cart work and what happens when the session ends?**
It's a `Map<menuId, CartItem>` in the HTTP session. Logging out invalidates the session, and a timeout (30 minutes) does the same, so the cart is lost. A database-backed cart would survive that, but is more complex; I chose the simpler option the brief allowed.

**26. How would you scale this application?**
Add a connection pool (HikariCP), store carts in the database or Redis, run several Tomcat instances behind a load balancer with shared sessions, add caching for the restaurant list, pagination, HTTPS, and automated tests. Moving to Spring Boot would reduce boilerplate.

**27. What are the weaknesses of your project?**
No HTTPS or secure cookie flag, no login rate-limiting, no email verification, no connection pool, no automated tests, and the cart double-click lock only works on a single server. I listed these in the README.

**28. What would you do differently next time?**
Write automated tests for the DAOs and `CartService`, use a connection pool from the start, and add an idempotency key to checkout.

## Part 3: Quick glossary
- **POJO:** plain Java class with fields, getters and setters.
- **JDBC:** Java API for talking to databases.
- **JSTL / EL:** tag library and expression language used in JSP instead of Java code.
- **Session:** server-side storage tied to one browser by a cookie.
- **Filter:** code that runs before servlets to inspect or block requests.
- **Transaction:** a group of database operations that succeed or fail together.
- **IDOR:** accessing someone else's data by changing an id in the URL.
- **CSRF:** tricking a logged-in user's browser into sending an unwanted request.
