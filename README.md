# TapBite: Food Delivery Web Application

A Swiggy/Zomato-style food ordering site built with **Java Servlets, JSP/JSTL, JDBC and MySQL** using **MVC + DAO**.
College project: no Spring, Hibernate or front-end frameworks.

| | |
|---|---|
| Language / build | Java 17, Maven (WAR) |
| Server | Apache Tomcat **10.1** (Jakarta Servlet 6.0, JSP 3.1, JSTL 3.0, uses `jakarta.*`) |
| Database | MySQL 8.0+ via MySQL Connector/J 8.4.0 |
| Passwords | BCrypt (`at.favre.lib:bcrypt:0.10.2`, cost 12) |
| IDE | Eclipse IDE for Enterprise Java and Web Developers (2022-12 or newer) |

## 1. Quick start

### 1.1 Database
1. Start MySQL.
2. Run the two scripts with your own MySQL account (Workbench or command line):
   ```
   mysql -u YOUR_USER -p < db/schema.sql
   mysql -u YOUR_USER -p < db/seed.sql
   ```
   `schema.sql` **drops and recreates** the tables, so re-running it resets everything.
3. Check: `SELECT COUNT(*) FROM restaurants;` gives 21 (20 active) and `SELECT COUNT(*) FROM menu_items;` gives 122.

### 1.2 Database configuration (never commit this file)
Copy `src/main/resources/db.properties.example` to `src/main/resources/db.properties` and fill in **your** values:
```
db.host=localhost
db.port=3306
db.name=food_delivery_db
db.username=YOUR_MYSQL_USERNAME
db.password=YOUR_MYSQL_PASSWORD
```
`.gitignore` already excludes it.

### 1.3 Import into Eclipse
1. *File → Import → Maven → Existing Maven Projects* and select the project folder. Wait for dependencies.
2. Right-click the project → *Maven → Update Project* (tick *Force Update*).
3. *Properties → Project Facets*: **Dynamic Web Module 6.0** and **Java 17**. *Targeted Runtimes*: **Apache Tomcat v10.1**.
4. Servers view: add Tomcat v10.1 if needed (*Window → Preferences → Server → Runtime Environments*).

### 1.4 Run
Right-click the project → *Run As → Run on Server* → Tomcat 10.1.
Open `http://localhost:8080/food-delivery-app/` (the context root is in *Properties → Web Project Settings*).

### 1.5 Check the connection
Open `/db-test`. You should see `Connected to MySQL: YES`, 20 restaurants, 122 menu items.
This page answers only requests from the same computer. **Delete `DbTestServlet.java` before showing or deploying the app anywhere else.**

### 1.6 Accounts
No users are seeded (a hash I could not verify would be unsafe). Create accounts in the app:
- **Customer:** *Sign up* in the browser.
- **Administrator:** sign up normally, then run
  ```sql
  UPDATE users SET role = 'ADMIN' WHERE email = 'your-email@example.com';
  ```
  and log in again (the role is stored in the session at login).

### 1.7 Images
The pages work with no photos (a placeholder is shown). To add real pictures, put files named as in
`docs/image-manifest.md` into `src/main/webapp/images/restaurants/` and `src/main/webapp/images/food/`.
Use only images you are licensed to use.

### 1.8 Build a WAR (optional)
`mvn clean package` produces `target/food-delivery-app.war`. Copy it to Tomcat's `webapps` folder.
Make sure `db.properties` exists in `src/main/resources` **before** building, because it is packaged into the WAR.

## 2. Architecture

```
Browser → Filters → Controller (Servlet) → [CartService] → DAO interface → DAO impl (JDBC) → MySQL
                          ↓ request attributes
                      JSP view (JSTL + EL, no scriptlets) → HTML
```

| Layer | Package / folder | Role |
|---|---|---|
| Model | `com.tap.model` | POJOs: `User`, `Restaurant`, `MenuItem`, `FoodOrder`, `OrderItem` + view-models `OrderLine`, `CartLine`, `CartItem` |
| DAO | `com.tap.dao` / `com.tap.daoimpl` | Interfaces and JDBC implementations (`PreparedStatement`, try-with-resources) |
| Service | `com.tap.service` | `CartService`: cart rules shared by two servlets |
| Util | `com.tap.util` | `DBConnection`, `PasswordUtil`, `ValidationUtil`, `CsrfUtil`, `SessionUtil` |
| Controller | `com.tap.controller`, `.controller.admin` | Servlets |
| Filter | `com.tap.filter` | `EncodingFilter`, `CsrfFilter`, `AuthFilter`, `AdminFilter` |
| View | `src/main/webapp/WEB-INF/views` | JSPs (not reachable by URL) and `.jspf` fragments |
| Assets | `src/main/webapp/css`, `js`, `images` | Static files |

### Routes
| URL | Method | Access | Servlet |
|---|---|---|---|
| `/` , `/home` | GET | public | `HomeServlet` |
| `/menu?restaurantId=N` | GET | public | `MenuServlet` |
| `/register`, `/login` | GET, POST | public | `RegisterServlet`, `LoginServlet` |
| `/logout` | POST | logged in | `LogoutServlet` |
| `/profile` | GET, POST | customer | `ProfileServlet` |
| `/cart` | GET, POST | customer | `CartServlet` |
| `/checkout` | GET, POST | customer | `CheckoutServlet` |
| `/order-confirmation`, `/orders`, `/orders/details` | GET | customer, owner only | `OrderServlet` |
| `/admin` | GET | admin | `AdminDashboardServlet` |
| `/admin/restaurants`, `/admin/restaurants/form` | GET, POST | admin | `AdminRestaurantServlet` |
| `/admin/menu`, `/admin/menu/form` | GET, POST | admin | `AdminMenuServlet` |
| `/admin/orders`, `/admin/orders/details` | GET, POST | admin | `AdminOrderServlet` |
| `/db-test` | GET | local machine only | `DbTestServlet` (setup helper) |

### Database
`users` 1—N `orders` 1—N `order_items` N—1 `menu_items` N—1 `restaurants`.
Foreign keys use `RESTRICT` (so history is never deleted by accident) except `order_items → orders`, which cascades.
Money is `DECIMAL(10,2)` in MySQL and `BigDecimal` in Java.

## 3. How the key flows work

**Login.** `LoginServlet` loads the user by email, verifies the BCrypt hash, changes the session id (prevents session fixation), stores only `userId`, `username`, `role`, then redirects.

**Cart.** Stored in the session as `Map<menuId, CartItem>`: ids and quantities only. Names, prices and totals are re-read from MySQL on every page (`CartService.buildView`), so the browser can never set a price. Rules: one restaurant per cart, 1 to 20 of each item, only available items. Logging out or a session timeout destroys the session and so the cart.

**Checkout and order creation.** `CheckoutServlet` (1) locks the session cart, (2) rebuilds it from the database, (3) compares the total the customer saw with the real total, (4) validates the address, (5) builds the order from trusted prices, (6) calls `OrderDAO.createOrder`, which turns auto-commit off, inserts the order, reads the generated id, inserts every item on the same connection, then commits (or rolls back on any failure), (7) clears the cart and redirects to the confirmation page (so refreshing cannot create a second order).

**Ownership.** Customer order lookups use `WHERE order_id = ? AND user_id = ?` with the user id taken from the session. Someone else's order and a nonexistent order give the same 404.

## 4. Security summary
Prepared statements everywhere · BCrypt hashes · server-side validation · session id regenerated at login · HttpOnly session cookie · CSRF token on every POST · output escaped with `<c:out>` · scriptlets forbidden by `web.xml` · POST + redirect-after-POST for all changes · role checks in `AdminFilter` (re-verified in the database) · open-redirect protection for `?next=` · image paths restricted to `images/...` · generic error pages with details only in server logs · no-cache headers on private pages.

**Remaining limitations (be honest about these in your presentation):** no HTTPS (session cookie not marked Secure) · no login rate-limiting or account lockout · no email verification or password reset · no Content-Security-Policy or other security headers · connection-per-request instead of a connection pool · admin status changes are unrestricted · the cart lock works for one Tomcat instance only.

## 5. Features
See [`docs/features.md`](docs/features.md) for the implemented and intentionally omitted list,
[`docs/test-checklist.md`](docs/test-checklist.md) for the full test run,
[`docs/troubleshooting.md`](docs/troubleshooting.md) for common errors, and
[`docs/presentation-and-interview.md`](docs/presentation-and-interview.md) for the presentation outline and interview answers.
