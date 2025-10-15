# 📚 Bookstore Management System

REST API for comprehensive bookstore management built with Java and Spring Boot.


## 📖 About The Project
A monolithic REST API designed for the complete management of bookstores. Built with a layered architecture and focused on clean code, comprehensive testing and backend development best practices.

The system handles book inventory control with ISBN tracking, user and role management, a robust reporting system, auditing features, advanced search functionality and secure authentication. It provides a full suite of tools to efficiently manage the operations of a bookstore from a single centralized backend.

## 🎯 Key Features

- Complete CRUD operations for books, authors and customers
- Advanced search system with multiple filters (ISBN, author, title, genre)
- Stock management with real-time availability tracking
- Layered architecture (Controller-Service-Repository-Entity)
- Data validation with Bean Validation
- Centralized exception handling
- API documentation with Swagger/OpenAPI
- Comprehensive testing with JUnit and Mockito


## 🛠️ Tech Stack
### Backend

- Java 17
- Spring Boot 3.x
- Spring Data JPA
- Hibernate ORM

### Database

- PostgreSQL 15

### Testing

- JUnit 5
- Mockito

### Documentation

- Swagger/OpenAPI

### Build Tool

- Maven

### Development Tools

- IntelliJ IDEA
- Postman
- Git/GitHub


## 🏗️ Project Architecture
<pre>
┌─────────────────────────────────────────┐
│           Controller Layer              │
│         (REST Endpoints, DTOs)          │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│            Service Layer                │
│          (Business Logic)               │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│          Repository Layer               │
│        (Database Operations)            │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         PostgreSQL Database             │
└─────────────────────────────────────────┘
</pre>
**Design Principles:**

- Separation of concerns with DTOs
- SOLID principles
- Clean code practices
- Test-driven development approach


## 📦 Modules
### ✅ Completed & Tested
**📖 Book and Author Management**

- Full CRUD operations for books
- ISBN tracking and validation
- Author relationships and management
- Advanced search capabilities:
  - Search by ISBN
  - Search by author
  - Search by title
  - Search by category
  - Combined filters

**👥 Customer Management**

- Full CRUD operations
- Customer data validation

## 🚧 In Active Development
### 📊 Inventory Control

- Stock management system
- Real-time availability tracking
- Low stock alerts (planned)
- Inventory auditing (in testing phase)

## 📋 Planned Features

- 🔐 Authentication System: JWT-based security
- 👤 User & Role Management: Role-based access control (RBAC)
- 📝 Audit System: Spring Data Auditing for tracking changes
- 💰 Sales Module: Complete sales system with transaction history
- 📊 Reporting System: Automated weekly reports

## 🚀 Getting Started
### Prerequisites
Before running this project, make sure you have:

- Java 17 or higher installed
- PostgreSQL 15 installed and running
- Maven 3.8+ installed
- Git for version control

### Installation

- Clone the repository  
```bash
git clone https://github.com/AlonsoPelaezFlores/BookStoreManagementSystem.git
cd BookStoreManagementSystem
```

### Configure the database

- Create a PostgreSQL database:  
```sql
CREATE DATABASE bookstore_db;
```

### Update application properties

Edit `src/main/resources/application.properties:`
```properties
propertiesspring.datasource.url=jdbc:postgresql://localhost:5432/bookstore_db
spring.datasource.username=your_username
spring.datasource.password=your_password

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Build the project

```bash
mvn clean install
```

### Run the application

```bash
mvn spring-boot:run
```
The API will be available at: `http://localhost:8080`

<!-- ## 📚 API Documentation
Once the application is running, access the Swagger UI documentation:
http://localhost:8080/swagger-ui.html-->

## 📁 Project Structure
<pre>src/
├── main/
│   ├── java/
│   │   └── com/bookstore/management/
│   │       ├── config/                          # Configuraciones globales (Security, OpenAPI, DatabaseConfig, CorsConfig)
│   │       ├── shared/                          # Componentes comunes / reutilizables
│   │       │   └── exception/
│   │       │       ├── custom/                  # Excepciones personalizadas
│   │       │       ├── handler/                 # Manejadores globales de excepciones
│   │       │       └── response/                # Clases de respuesta de error / wrapper
│   │       ├── book/                            # Módulo de gestión de libros
│   │       │   ├── controller/                  # Controladores REST
│   │       │   ├── dto/                         # Data Transfer Objects
│   │       │   ├── mapper/                      # MapStruct / Conversión entre entidades y DTOs
│   │       │   ├── model/                       # Entidades JPA
│   │       │   ├── repository/                  # Interfaces de acceso a datos
│   │       │   ├── service/                     # Lógica de negocio
│   │       │   └── validation/                  # Validaciones personalizadas (anotaciones, validadores)
│   │       ├── customer/                        # Módulo de clientes
│   │       │   ├── controller/
│   │       │   ├── dto/
│   │       │   ├── mapper/
│   │       │   ├── model/
│   │       │   ├── repository/
│   │       │   └── service/
│   │       ├── inventory/                       # Módulo de inventario
│   │       │   ├── controller/
│   │       │   ├── dto/
│   │       │   ├── mapper/
│   │       │   ├── model/
│   │       │   ├── repository/
│   │       │   └── service/
│   │       └── sales/                           # Módulo de ventas
│   │           ├── controller/
│   │           ├── dto/
│   │           ├── mapper/
│   │           ├── model/
│   │           ├── repository/
│   │           └── service/
│   └── resources/
│       └── application.properties                # Configuración principal
└── test/
    └── java/
        └── com/bookstore/management/
            ├── book/
            │   ├── controller/                   # Tests de controladores
            │   ├── repository/                   # Tests de repositorios
            │   ├── service/                      # Tests de servicios
            │   └── validation/                   # Tests de validaciones
            ├── customer/                         # Tests para clientes
            │   ├── controller/
            │   └── service/
            │── inventory/                        # Tests para inventario
            │   ├── controller/                   
            │   └── service/
            └── sales/                            # Tests para ventas
                ├── controller/                   
                └── service/
</pre>

## 🎯 Roadmap
### Phase 1: Core Functionality ✅

 - Book and Author Management
 - Customer Management
 - Basic API structure
 - Database design and implementation

### Phase 2: Inventory System 🚧

 - Inventory entity design
 - Stock management complete testing
 - Integration with book module

### Phase 3: Security & Users 📋

 - JWT authentication implementation
 - User registration and login
 - Role-based access control (Admin, Employee, Customer)
 - Password encryption

### Phase 4: Audit & Tracking 📋

 - Spring Data Auditing implementation
 - Track who created/modified records
 - Change history logging

### Phase 5: Sales Module 📋

 - Shopping cart functionality
 - Order processing
 - Transaction history
 - Invoice generation

### Phase 6: Reporting & Notifications 📋

 - Automated weekly reports
 - Email notifications
 - Low stock alerts
 - Sales analytics

## 🤝 Contributing
This is a personal learning project but suggestions and feedback are welcome!
If you have ideas for improvements:

- Fork the repository
- Create a feature branch (git checkout -b feature/AmazingFeature)
- Commit your changes (git commit -m )
- Push to the branch (git push origin)
- Open a Pull Request

<!--## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.-->

## 👤 Author
**Alonso Peláez Flores**

**GitHub:** @AlonsoPelaezFlores  
**LinkedIn:** [Alonso Pelaez](https://www.linkedin.com/in/calonsopf/)  
**Email:** alonso18pf@gmail.com
 
