# API for *E-Commerce Application*

E-commerce API built with Java, Spring Boot, Hibernate, MySQL, OpenAPI, and JWT. The main purpose of creating this application was to learn Java, API development, Spring, and unit and integration testing.

## Used Tools

The following tools and technologies were used for the implementation:

* Java 21
* Spring Boot v3.4.0
* Spring Data JPA
* Spring Security
* JWT Authentication
* Maven v4.0.0
* MySQL
* H2 Database
* Hibernate
* Liquibase
* JUnit 5
* Mockito 2
* AssertJ v3.26.0
* IntelliJ IDEA

## Functionalities

### The application provides the following functionalities, allowing users to manage their activity easily:

* Register in the application and log in to obtain a valid session token (JWT authentication).
* Update user data.
* Update user credentials.
* Display, add, and delete user addresses.
* Display and sort all products with or without pagination.
* Display and sort all products with pagination from a chosen category.
* Display the quantity of a chosen product.
* Search for a product by name, description, category name, or other keywords, and sort and paginate results.
* Create, check, empty, and delete the cart.
* Display, add, and delete products from the shopping cart.
* Update product quantity in the cart, increase/decrease products by one.
* Create an order from the cart.
* Display an order.
* HATEOAS for Products: Added HATEOAS support for product-related endpoints. 

### Admins have access to the following functionalities:

* Add a new product.
* Update a product.
* Delete a product.
* Add a new product category.
* Update a product category.
* Delete a product category.
* Delete an order.
* Display a list of users and details about user orders.
* Delete a user account.
* Update order status.
* Add a new delivery method.
* Update a delivery method.
* Delete a delivery method.

## Entity Relationship Diagram for the Application

![E-R Diagram](/schema.png)

## API Module Endpoints

### Product Controller
- **GET** `/api/v1/products/{id}` - Get a product by its ID
- **PUT** `/api/v1/products/{id}` - Replace a product
- **DELETE** `/api/v1/products/{id}` - Delete a product
- **GET** `/api/v1/products` - Get all products
- **POST** `/api/v1/products` - Create a new product
- **GET** `/api/v1/products/{id}/quantity` - Get the quantity of a product
- **GET** `/api/v1/products/search` - Search for products with pagination
- **GET** `/api/v1/products/page/{pageNo}` - Get all products with pagination
- **GET** `/api/v1/products/page/{pageNo}/category` - Get paginated products from a chosen category

### Category Controller
- **PUT** `/api/v1/categories/{id}` - Replace a category
- **DELETE** `/api/v1/categories/{id}` - Delete a category
- **GET** `/api/v1/categories` - Get all categories
- **POST** `/api/v1/categories` - Create a new category

### Order Controller
- **POST** `/api/v1/orders` - Create a new order based on the cart
- **POST** `/api/v1/orders/{orderId}/payments` - Process a payment for an order
- **PATCH** `/api/v1/orders/{id}/success` - Mark order as SUCCESS
- **PATCH** `/api/v1/orders/{id}/cancel` - Cancel an order
- **GET** `/api/v1/orders/{id}` - Get an order by its ID
- **DELETE** `/api/v1/orders/{id}` - Delete an order
- **GET** `/api/v1/orders/page` - Get all orders with pagination

### Delivery Controller
- **GET** `/api/v1/deliveries` - Get all deliveries
- **POST** `/api/v1/deliveries` - Create a new delivery
- **GET** `/api/v1/deliveries/{id}` - Get a delivery by its ID
- **DELETE** `/api/v1/deliveries/{id}` - Delete a delivery
- **PATCH** `/api/v1/deliveries/{id}` - Update delivery details

### Cart Controller
- **GET** `/api/v1/carts` - Get details about the user cart
- **POST** `/api/v1/carts` - Create a new cart
- **DELETE** `/api/v1/carts` - Delete a cart
- **POST** `/api/v1/carts/clear` - Empty a cart

### Cart Item Controller
- **POST** `/api/v1/cartItems` - Create a new cart item
- **GET** `/api/v1/cartItems/{id}` - Get a cart item by its ID
- **DELETE** `/api/v1/cartItems/{id}` - Delete a cart item
- **PATCH** `/api/v1/cartItems/{id}` - Update cart item quantity
- **PATCH** `/api/v1/cartItems/{id}/quantity/increment` - Increase cart item quantity by 1
- **PATCH** `/api/v1/cartItems/{id}/quantity/decrement` - Decrease cart item quantity by 1

### Authentication Controller
- **POST** `/api/v1/auth/register` - Register a new user
- **POST** `/api/v1/auth/login` - Login user

### Address Controller
- **POST** `/api/v1/addresses` - Create a new address
- **GET** `/api/v1/addresses/{id}` - Get an address by its ID
- **DELETE** `/api/v1/addresses/{id}` - Delete an address
- **PATCH** `/api/v1/addresses/{id}` - Update address details

### User Controller
- **PATCH** `/api/v1/users/password` - Update user password
- **PATCH** `/api/v1/users/details` - Update user details
- **GET** `/api/v1/users/user/{id}` - Get a user by their ID
- **GET** `/api/v1/users/orders` - Get user orders
- **GET** `/api/v1/users/addresses` - Get user addresses
- **DELETE** `/api/v1/users/{id}` - Delete a user

## Who Can Use It?

This API can be used by anyone interested in working with it.

## How to Use It?

The API has not been hosted or deployed yet. It runs on localhost for now. To use it:

1. Install MySQL.
2. Open Git Bash.
3. Navigate to the directory where you want to clone the repository.
4. Execute the command:
   ```bash
   git clone https://github.com/BartKempa/api-ecommerce.git
   ```
5. Press Enter to create your local clone.
6. To use the production profile, configure the datasource in `src/main/resources/application-prod.yml`:
   ```yaml
   spring:
     datasource:
       url: jdbc:mysql://localhost:3306/apiecommerce
       username: root
       password: password
   ```
7. The server runs on `localhost:8080`.

## Testing

The application includes unit and integration tests using JUnit, Mockito, and AssertJ. Tests cover various functionalities such as user registration, cart management, order management, product management, and more.

## Future Work / Improvements

1. User account activation via email after registration.


