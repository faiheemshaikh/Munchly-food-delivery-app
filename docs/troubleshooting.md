# Troubleshooting

## Eclipse and Maven
| Problem | Fix |
|---|---|
| Red errors on `jakarta.servlet` imports | *Maven → Update Project* (Force Update). Check `pom.xml` has `jakarta.servlet-api` 6.0.0 |
| "Dynamic Web Module 6.0" not offered | Eclipse is too old. Use 2022-12 or newer, or install the Jakarta EE 10 tooling |
| Project has a red X but no visible error | *Project → Clean*, then *Maven → Update Project* |
| Tomcat v10.1 missing in the server list | *Window → Preferences → Server → Runtime Environments → Add → Apache Tomcat v10.1* and point it to the unzipped Tomcat folder |
| "Java 17 not available" | *Preferences → Java → Installed JREs*: add a JDK 17 (not a JRE) |
| Changes to Java or JSP files don't appear | *Project → Clean*; in the Servers view right-click the server → *Clean...*, then restart |
| Changes to `web.xml` ignored | Restart Tomcat fully (not a hot reload) |

## Tomcat
| Problem | Fix |
|---|---|
| `java.lang.ClassNotFoundException: javax.servlet...` | Wrong imports or an old library. Everything here must use `jakarta.servlet.*` |
| `Port 8080 already in use` | Another program uses the port: stop it, or change the Tomcat HTTP port in the Servers view (double-click the server) |
| HTTP 404 for every URL | Wrong context root. Check *Properties → Web Project Settings* (default `food-delivery-app`) and use that in the URL |
| HTTP 404 only for one page | The servlet is not deployed (clean and restart), or the URL has a typo. Compare with the route table in the README |
| HTTP 403 "Not allowed" after submitting a form | CSRF token missing or session expired. Reload the page and try again |
| HTTP 500 page | Details are in the Eclipse Console / `logs/catalina.out`. Search for `SEVERE` |
| `The absolute uri: jakarta.tags.core cannot be resolved` | JSTL jars missing. *Maven → Update Project*, then re-publish. Both JSTL dependencies must be in `pom.xml` |
| JSP error mentioning scripting elements | A scriptlet (`<% ... %>`) is used. Scriptlets are disabled on purpose: use JSTL/EL |
| `NoClassDefFoundError` / `ExceptionInInitializerError` in `LoginServlet` | BCrypt library not on the classpath: update Maven dependencies and republish |
| Weird characters (`â‚¹`) | A file was saved in a non-UTF-8 encoding. Re-save as UTF-8 (*Properties → Resource → Text file encoding*) |

## MySQL and JDBC
| Problem | Fix |
|---|---|
| `Configuration file 'db.properties' not found` | Create `src/main/resources/db.properties` (copy the example), then clean and republish |
| `Access denied for user 'x'@'localhost'` | Wrong username or password in `db.properties`. Test them in MySQL Workbench first |
| `Unknown database 'food_delivery_db'` | Run `db/schema.sql` |
| `Communications link failure` | MySQL is not running, or host/port is wrong. Check the MySQL service |
| `Public Key Retrieval is not allowed` | Keep `allowPublicKeyRetrieval=true` in the JDBC parameters (default), or set `db.params` yourself |
| `Table '...' doesn't exist` | `schema.sql` did not complete. Run it again from the top |
| `Cannot add or update a child row: a foreign key constraint fails` | Data inserted out of order, or a menu id that does not exist. Re-run `schema.sql` then `seed.sql` |
| `Duplicate entry ... for key 'users.uq_users_email'` | That email is already registered (the app shows a friendly message) |
| `Data truncation: Data too long for column` | A value exceeds the column size. Shorten it |
| `Check constraint ... is violated` | A negative price/quantity or a rating outside 0 to 5 |
| Workbench "safe update mode" error (1175) | Include a key column in your `WHERE`, for example `WHERE email = '...'` |
| `Too many connections` | A connection leaked. Every DAO closes connections with try-with-resources; look for code you modified |

## Application behaviour
| Symptom | Cause and fix |
|---|---|
| Admin link missing after promoting yourself | Role is read at login. Log out and in again |
| Can log in but every admin page is 403 | `role` must be exactly `ADMIN` and `active` must be 1 |
| Cart is empty after login | By design: the cart lives in the session, and logout or timeout (30 minutes) destroys it |
| Prices use `,` as decimal separator | `web.xml` must contain the `jakarta.servlet.jsp.jstl.fmt.locale` setting (value `en_US`) |
| Every image is the placeholder | Add real image files with the exact names from `docs/image-manifest.md` (case-sensitive on Linux/macOS) |
| Add to cart says "another restaurant" | The cart holds one restaurant. Clear it first |
| `/db-test` gives 403 | It only answers requests from the server computer |
