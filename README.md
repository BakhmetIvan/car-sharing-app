# Car Sharing Service API

## Introduction
**Car Sharing Service API** is a modern online management system for car rentals. This system allows users to rent cars, manage their profiles, and make payments, while managers can oversee cars, rentals, and payments. Built using **Spring Boot**, it leverages modern technologies to streamline the entire car-sharing process, replacing manual systems with an efficient digital solution.

## Inspiration
Many car-sharing services still use outdated methods for tracking car rentals, payments, and user management. This system was developed to provide an automated, secure, and reliable method for managing car rentals, integrating payment gateways, and sending notifications to managers about important events, such as new rentals or overdue returns.

## Technologies Used
- **Java**: Primary programming language used for developing the application.
- **Spring Boot**: Simplifies Java development and provides a flexible framework for building web services.
- **Spring Security**: Manages authentication and authorization using JWT tokens.
- **Stripe API**: Integrated for handling secure payment processing for rentals.
- **Liquibase**: Helps with managing database schema changes and version control.
- **Swagger**: Provides detailed API documentation for easy reference and testing.
- **Telegram API**: Used to send notifications to managers about new rentals, payments, and other system events.
- **Docker**: Provides containerization for easy deployment and consistent development environments.
- **Lombok**: Reduces boilerplate code using annotations.
- **Hibernate**: Simplifies database operations using Object-Relational Mapping (ORM).
- **MySQL**: Relational database to store data on cars, rentals, users, and payments.

## Key Features
- **JWT Token Authentication**: Secures the application, ensuring that only authenticated users have access to certain features.
- **Role-based Access**: Manager users can manage car inventories, payments, and user roles, while regular users can manage their profiles and rentals.
- **Stripe Payment Integration**: Allows users to make secure online payments for their car rentals.
- **Car Inventory Management**: Tracks the availability of cars in real-time.
- **Notifications**: Telegram notifications are sent to managers regarding new rentals and overdue returns.
- **Swagger UI**: Provides interactive API documentation for developers.

## Project Structure

### Authentication Controller
Handles registration and login functionalities using JWT authentication.

| HTTP Method | Endpoint        | Description            |
|-------------|-----------------|------------------------|
| POST        | /register        | Registers a new user.   |
| POST        | /login           | Logs in a user and returns a JWT token. |

### User Profile Controller
Manages user profiles and allows role management for managers.

| HTTP Method | Endpoint                | Description                              |
|-------------|-------------------------|------------------------------------------|
| GET         | /profile/me              | Retrieves the logged-in user's profile.  |
| PUT         | /profile/me              | Updates the user’s profile.              |
| PATCH       | /profile/{id}/role       | Allows managers to update a user’s role.   |

### Car Controller
Allows managers to manage the fleet of cars and users to view available cars.

| HTTP Method | Endpoint          | Description                              |
|-------------|-------------------|------------------------------------------|
| GET         | /cars              | Lists all available cars.                |
| POST        | /cars              | Adds a new car (Manager only).             |
| PUT         | /cars/{id}         | Updates car details (Manager only).        |
| PATCH       | /cars/{id}         | Updates the inventory of a car (Manager only). |
| DELETE      | /cars/{id}         | Deletes a car (Manager only).              |

### Rental Controller
Manages car rentals for users.

| HTTP Method | Endpoint            | Description                              |
|-------------|---------------------|------------------------------------------|
| POST        | /rentals            | Creates a new rental.                    |
| GET         | /rentals            | Lists all rentals with filters for active rentals. |
| GET         | /rentals/{id}       | Retrieves a specific rental by ID.       |
| POST        | /rentals/{id}/return| Marks a rental as returned.              |

### Payment Controller
Handles payments related to car rentals using Stripe.

| HTTP Method | Endpoint                        | Description                                  |
|-------------|---------------------------------|----------------------------------------------|
| POST        | /payments                       | Creates a new payment session via Stripe.    |
| GET         | /payments                       | Retrieves all payments for a user.           |
| GET         | /payments/success/{sessionId}   | Handles successful Stripe payments.          |
| GET         | /payments/cancel/{sessionId}    | Handles canceled Stripe payments.            |

### Notification Service
Sends Telegram notifications to managers about rentals, payments, and overdue returns.

## Setup Instructions

To set up and run the project locally, follow these steps:

1. **Install Docker**: Make sure Docker and Docker Compose are installed on your system.
2. **Clone the Repository**:
    ```bash
    git clone https://github.com/your-repo/car-sharing-service.git
    cd car-sharing-service
    ```
3. **Setting up the application**: Insert your values to `application.properties` and `.env` files for the application to work properly.
4. **Build the Project**:
    ```bash
    mvn clean package
    ```
5. **Run the Application**:
    ```bash
    docker-compose up --build
    ```
6. **Access the Application**: Once the application is running, it will be available at `http://localhost:8090`.

## Preconfigured Users

### Manager:
- **Email**: `manager@gmail.com`
- **Password**: `managerpass`
- **Roles**: `MANAGER`

### User:
- **Email**: `ivan@gmail.com`
- **Password**: `ivanpass`
- **Roles**: `USER`

## Swagger API Documentation
For detailed API documentation, you can access the Swagger UI by navigating to [swagger](http://ec2-3-84-23-2.compute-1.amazonaws.com/swagger-ui/index.html#).

## Postman Collection
A Postman collection is available to quickly interact with the API. Here is the link to the [collection](https://www.postman.com/supply-specialist-25907922/car-sharing-app/collection/pkzsxv6/car-sharing-app?action=share&creator=33020565).

## Author
Created by **Bakhmet Ivan**. 
For any questions or collaboration, visit the [GitHub repository](https://github.com/BakhmetIvan).
