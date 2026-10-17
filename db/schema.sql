-- =====================================================================
-- schema.sql  |  Food Delivery App  |  MySQL 8.0+
-- WARNING: the DROP TABLE lines delete existing data. Re-running this
-- file resets the database. Remove them if you want to keep your data.
-- =====================================================================

CREATE DATABASE IF NOT EXISTS food_delivery_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE food_delivery_db;

-- Drop in reverse dependency order (children first)
DROP TABLE IF EXISTS order_items;
DROP TABLE IF EXISTS orders;
DROP TABLE IF EXISTS menu_items;
DROP TABLE IF EXISTS restaurants;
DROP TABLE IF EXISTS users;

-- ---------------------------------------------------------------------
-- users
-- ---------------------------------------------------------------------
CREATE TABLE users (
    user_id        INT           NOT NULL AUTO_INCREMENT,
    username       VARCHAR(50)   NOT NULL,
    email          VARCHAR(100)  NOT NULL,
    password_hash  VARCHAR(255)  NOT NULL,              -- BCrypt hash, never plaintext
    phone          VARCHAR(15)   NULL,
    address        VARCHAR(255)  NULL,
    role           ENUM('CUSTOMER','ADMIN') NOT NULL DEFAULT 'CUSTOMER',
    active         TINYINT(1)    NOT NULL DEFAULT 1,
    created_at     TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uq_users_email (email)                   -- blocks duplicate accounts
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- restaurants
-- ---------------------------------------------------------------------
CREATE TABLE restaurants (
    restaurant_id  INT           NOT NULL AUTO_INCREMENT,
    name           VARCHAR(100)  NOT NULL,
    description    VARCHAR(500)  NULL,
    cuisine        VARCHAR(100)  NOT NULL,
    address        VARCHAR(255)  NOT NULL,
    image_path     VARCHAR(255)  NULL,
    rating         DECIMAL(2,1)  NOT NULL DEFAULT 0.0,
    active         TINYINT(1)    NOT NULL DEFAULT 1,
    PRIMARY KEY (restaurant_id),
    KEY idx_restaurants_active (active),
    CONSTRAINT chk_restaurants_rating CHECK (rating >= 0 AND rating <= 5)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- menu_items
-- ---------------------------------------------------------------------
CREATE TABLE menu_items (
    menu_id        INT            NOT NULL AUTO_INCREMENT,
    restaurant_id  INT            NOT NULL,
    name           VARCHAR(100)   NOT NULL,
    description    VARCHAR(500)   NULL,
    price          DECIMAL(10,2)  NOT NULL,
    image_path     VARCHAR(255)   NULL,
    category       VARCHAR(50)    NULL,
    available      TINYINT(1)     NOT NULL DEFAULT 1,
    PRIMARY KEY (menu_id),
    KEY idx_menu_restaurant (restaurant_id),
    CONSTRAINT fk_menu_restaurant FOREIGN KEY (restaurant_id)
        REFERENCES restaurants (restaurant_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_menu_price CHECK (price >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- orders
-- ---------------------------------------------------------------------
CREATE TABLE orders (
    order_id          INT            NOT NULL AUTO_INCREMENT,
    user_id           INT            NOT NULL,
    order_date        TIMESTAMP      NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivery_address  VARCHAR(255)   NOT NULL,
    total_amount      DECIMAL(10,2)  NOT NULL,
    status            ENUM('PLACED','PREPARING','OUT_FOR_DELIVERY','DELIVERED','CANCELLED')
                      NOT NULL DEFAULT 'PLACED',
    PRIMARY KEY (order_id),
    KEY idx_orders_user (user_id),
    KEY idx_orders_status (status),
    CONSTRAINT fk_orders_user FOREIGN KEY (user_id)
        REFERENCES users (user_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_orders_total CHECK (total_amount >= 0)
) ENGINE=InnoDB;

-- ---------------------------------------------------------------------
-- order_items
-- ---------------------------------------------------------------------
CREATE TABLE order_items (
    order_item_id  INT            NOT NULL AUTO_INCREMENT,
    order_id       INT            NOT NULL,
    menu_id        INT            NOT NULL,
    quantity       INT            NOT NULL,
    unit_price     DECIMAL(10,2)  NOT NULL,   -- price at the time of ordering
    subtotal       DECIMAL(10,2)  NOT NULL,   -- quantity * unit_price
    PRIMARY KEY (order_item_id),
    KEY idx_order_items_order (order_id),
    KEY idx_order_items_menu (menu_id),
    CONSTRAINT fk_oi_order FOREIGN KEY (order_id)
        REFERENCES orders (order_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_oi_menu FOREIGN KEY (menu_id)
        REFERENCES menu_items (menu_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT chk_oi_quantity CHECK (quantity > 0)
) ENGINE=InnoDB;
