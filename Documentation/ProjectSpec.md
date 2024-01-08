# Final Project: E-Commerce Shop RESTful API

You are tasked with creating a simple yet functional e-commerce shop. The application will serve as a backend system, handling everything from product management to order processing. Your API will interact with front-end applications, allowing users to browse products, add them to a cart, and place orders.

## Requirements

### 1. Project Setup
- **Framework:** Spring Boot
- **Java Version:** Use the latest stable release
- **Build Tool:** Maven
- **Database:** MySQL
- **Version Control:** Git (Ensure to maintain a repository with regular commits)

### 2. Core Features

#### 2.1. User Management
- **Registration:** Allow new users to register with basic information (name, email, password).
- **Authentication:** Implement JWT Auth
- **Authorization:** Define roles (e.g., customer, admin) and permissions.

#### 2.2. Product Management
- **Product Listing:** Enable the listing of products with details (name, price, description, stock).
- **Product Addition:** Allow authorized users (e.g., admins) to add new products.
- **Product Update & Deletion:** Admins should be able to update and delete products.

#### 2.3. Shopping Cart
- **Cart Creation:** Users can create a cart and add items to it.
- **Item Addition & Removal:** Users can add or remove products from the cart.
- **Cart View:** Users can view all items in their cart along with the total price.

#### 2.4. Order Processing
- **Order Placement:** Convert cart items into an order.
- **Order History:** Users can view their past orders with statuses (placed, shipped, delivered).

### 3. Additional Components

#### 3.1. API Documentation
- **Swagger or OpenAPI:** Provide detailed documentation for your API endpoints.

#### 3.2. Validation
- Ensure data validation for user inputs and provide meaningful error messages.

#### 3.3. Error Handling
- Implement global error handling to return appropriate responses for various error scenarios.

#### 3.4. Logging
- Set up logging for tracking errors and important application events.

### 4. Testing
- **Unit Testing:** Write unit tests for your business logic.
- **Integration Testing:** Test the integration between various components of your application.

### 5. Deployment (Optional)
- **Docker:** Containerize your application.
- **Cloud Provider:** Consider deploying your application to a cloud provider like Heroku or AWS.

## Deliverables
- Source code pushed to a Git repository (e.g., GitHub, Bitbucket).
- Documentation covering:
    - Setup instructions
    - Usage examples for all API endpoints
    - Any assumptions or decisions made during development

## Evaluation Criteria
- **Functionality:** Does the application work as required?
- **Code Quality:** Is the code clean, well-organized, and properly commented?
- **Error Handling:** How well does the application handle unexpected situations?
- **Testing:** Adequacy and completeness of tests.
- **Documentation:** Clarity and completeness of documentation.

## Conclusion

This project is your opportunity to showcase your skills in building a real-world Java backend application using Spring Boot. Approach it with creativity and attention to detail. Good luck, and we look forward to seeing your completed e-commerce shop!

## Detailed Requirements

### Project Setup

**Description:** Set up the initial Spring Boot project with the necessary dependencies and establish a connection to the chosen database. Ensure the project structure follows best practices.

**Technical Tasks:**
- Initialize a Spring Boot project with appropriate dependencies (Spring Web, Security, JPA, Database Driver).
- Configure application properties for database connection.
- Setup OpenAPI (Swagger) for API documentation.
- Set up a Git repository and document the setup process.
- Initialize basic logging setup to track application behavior from the start.

### User Registration & Authentication

**Description:** Users should be able to register and authenticate securely.

**Technical Tasks:**
- Implement a user model with fields for name, email, password, and roles.
- Set up a registration endpoint to create new users with encrypted passwords.
- Configure Spring Security and JWT for secure authentication.
- Create endpoints for registration and login, returning a JWT upon successful authentication.
- Begin writing unit/integration tests for user registration, authentication

**Optional:** Use testcontainers for integration testing.

### Initial Error Handling

**Description:** Set up a basic global exception handler to manage unexpected issues smoothly.

**Technical Tasks:**
- Implement a simple global exception handler that catches and logs various exceptions.
- Ensure that user-friendly error messages are returned to the client.

### Product Listing

**Description:** Users can view a list of available products. The system should support paging to handle large sets of products efficiently.

**Technical Tasks:**
- Create a product model with fields like ID, name, description, price, and stock quantity.
- Set up a database table for products and add some initial mock data.
- Implement a GET endpoint for listing products with pagination support.
- Use Pageable in the Spring Data repository to handle server-side pagination.

### Advanced Product Management

**Description:** Admins should be able to add, update, and delete products.

**Technical Tasks:**
- Secure product management endpoints with Spring Security, ensuring only admins can access them.
-

Implement POST, PUT, and DELETE endpoints for products.
- Ensure proper validation and error handling for product creation and updates.

### Shopping Cart

**Description:** Users should be able to add and remove products from their shopping cart.

**Technical Tasks:**
- Create a cart model associated with the user and containing a list of products.
- Implement endpoints for adding products to the cart, removing them, and viewing the current cart.
- Handle calculations for the total cost of the cart items.

### Basic Order Processing

**Description:** Users can place orders from their shopping carts.

**Technical Tasks:**
- Create an order model with fields like ID, date, user, total price, and status.
- Implement an endpoint to convert a cart into an order, marking the cart items as purchased.
- Add basic order validation to check stock availability and user details.

### Complete Order Processing

**Description:** Users can view their order history and track the status of their orders.

**Technical Tasks:**
- Implement an endpoint for users to view their past orders with details.
- Add functionality to update order status (e.g., processing, shipped, delivered).
- Ensure secure access so users can only see their orders.

### Dockerizing the Application

**Description:** Students will learn to package the application into a Docker container, which simplifies deployment and ensures consistency across different environments.

**Technical Tasks:**
1. Create a Dockerfile:
    - Write a Dockerfile that specifies the Java environment, copies the built application into the container, and defines how to run it.
    - Include comments explaining each step for educational purposes.
2. Build and Test the Docker Image:
    - Use the Dockerfile to build an image of the application.
    - Test the Docker image locally by creating a container out of it to ensure the application starts and runs correctly.

### Final Testing & Documentation

**Description:** Ensure the application is robust through thorough testing and well-documented for future users and developers.

**Technical Tasks:**
- Ensure unit and integration tests are covering key functionalities.
- Document each endpoint, including its purpose, input, output, and any error responses.
- Finalize the README with setup instructions, usage examples, and an overview of the project.

## Setting Up Your JIRA Project for Agile Development

### Step 1: Create a New Project
- **Action:** Log into JIRA and create a new project.
- **Details:** Choose a template that suits Agile development, like Scrum.

### Step 2: Define Epics
- **Action:** Create Epics to group your work into larger, meaningful segments.
- **Details:**
    - Epic 1: Project Setup & Basic Functionality - This will include all tasks related to setting up the project, basic error handling, and initial API endpoints.
    - Epic 2: Core Features Development - Encompasses user stories for advanced product management, shopping cart, and order processing.
    - Epic 3: Finalization & Deployment - Covers completing order processing, dockerizing the application, and final testing/documentation.

### Step 3: Create User Stories
- **Action:** Break down each Epic into User Stories. These should be the same as the ones that have been outlined in this document.
- **Details:** For each user story, provide a clear and concise description. The description should tell what needs to be done and why it's important. Remember, a user story typically follows this template: "As a [type of user], I want [an action] so that [a benefit/a value]."

### Step 4: Break Down User Stories into Tasks
- **Action:** For each User Story, create specific tasks that detail the work needed to accomplish the story.
- **Details:** Tasks are the smallest units of work and should be very specific and actionable. For example, "Implement a user model with fields for name, email, password, and roles" is a task under the "User Registration & Authentication" user story.

### Step 5: Plan Sprints
- **Action:** Organize your work into Sprints, 2-week periods where you focus on a set of tasks.
- **Details:** Drag and drop user stories (with their associated tasks) into your active Sprint. The goal is to complete these items by the end of the Sprint.

### Step 6: Track Progress
- **Action:** Regularly update task status and use JIRA's boards to visualize progress.
- **Details:** Move tasks to "In Progress," "Review," and "Done" as you work on them. Use the burndown chart to ensure your Sprint is on track.

### Step 7: Hold Agile Ceremonies
- **Action:** Conduct regular stand-ups, sprint planning, reviews, and retrospectives.
- **Details:** Use these meetings to update each other on progress, plan upcoming work, demo completed stories, and discuss what went well and what can be improved.

### Step 8: Continuous Improvement
- **Action:** Reflect on your process and find ways to improve.
- **Details:** After each Sprint, discuss what worked, what didn't, and how the team can improve in the next iteration. Implement changes gradually and continuously.

### General Tips:
- **Be Flexible:** Agile is all about adapting to change. Don't be afraid to reprioritize or shift focus as needed.
- **Collaborate:** Work closely with your trainers. Pair programming, code reviews, and regular communication are key.
- **Stay Lean:** Don't get bogged down in unnecessary details. Keep tasks small, and don't be afraid to adjust as you go.