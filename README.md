# 📚 Bookstore Management System

## 📖 About The Project
A monolithic REST API designed for the complete management of bookstores. Built with a layered architecture and focused on clean code, comprehensive testing and backend development best practices.

The system handles book inventory control with ISBN tracking, user and role management, a robust reporting system, auditing features, advanced search functionality and secure authentication. It provides a full suite of tools to efficiently manage the operations of a bookstore from a single centralized backend.

## 🎯 Key Features

- Complete CRUD operations for books, authors and customers
- Advanced search system with multiple filters (ISBN, author, title, genre)
- Stock management with real-time availability tracking
- MVC architecture pattern
- Data validation with Bean Validation
- Centralized exception handling
- API documentation with Swagger/OpenAPI
- Comprehensive testing with JUnit and Mockito


## 🛠️ Tech Stack

- Java 17
- Spring Boot 3.5
- Spring Data JPA
- Docker
- PostgreSQL 15
- JUnit 5
- Mockito
- Swagger/OpenAPI

**Design Principles:**

- Separation of concerns with DTOs
- SOLID principles
- Clean code practices
- Dependency Injection
- Test-driven development approach

## 🎯 Roadmap

### Phase 1: Book and Customer ✅
- Book and author management
- Customer management
- Basic API structure
- Database design and implementation

### Phase 2: Inventory System ✅
- Inventory module design
- Stock management complete testing
- Integration with book module
- Inventory tracking implementation

### Phase 3: Sales Module ✅
- Direct sales registration
- Sale details management
- Transaction history
- Integration with inventory and customer modules

### Phase 4: Security & Authentication 🚧
- JWT authentication implementation
- User registration and login
- Role-based access control (Admin, Employee & Customer)

### Phase 5: Refactoring & Enhancements 📋
- Book and author module improvements
- Apply auditing across all modules
- Code optimization and best practices

### Future Enhancements 💡
- Invoice generation
- Automated reports and analytics
- Low stock alerts and notifications
- Customer loyalty programs

## 🚀 Getting Started
### Prerequisites
- Java 17+
- Docker & Docker Compose
- Maven 3.8+

### 1. Clone the repository
```bash
git clone https://github.com/AlonsoPelaezFlores/BookStoreManagementSystem.git
cd BookStoreManagementSystem
```

### 2. Start the database
```bash
docker-compose up bookstore_db -d
```

### 3. Build and run
```bash
mvn clean install
mvn spring-boot:run
```

The API will be available at: `http://localhost:8080`

## 📚 API Documentation
Once the application is running, access the Swagger UI documentation:
http://localhost:8080/swagger-ui.html

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

<!--## 📝 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.-->

## 👤 Author
**Alonso Peláez Flores**

**GitHub:** @AlonsoPelaezFlores  
**LinkedIn:** [Alonso Pelaez](https://www.linkedin.com/in/calonsopf/)  
**Email:** alonso18pf@gmail.com
 


