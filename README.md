# 🔗 One-to-One Unidirectional Mapping with JPA/Hibernate in Spring Boot

![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.5-brightgreen?style=flat&logo=spring) ![JPA](https://img.shields.io/badge/JPA-Jakarta%20Persistence-blue) ![Hibernate](https://img.shields.io/badge/Hibernate-6.2-orange) ![MySQL](https://img.shields.io/badge/MySQL-8.0-blue?logo=mysql) ![Maven](https://img.shields.io/badge/Maven-3.9-red?logo=apache-maven)

This project demonstrates a **unidirectional one-to-one relationship** between two JPA entities (`Instructor` and `InstructorDetail`) using Spring Boot and Hibernate. It follows the standard layered architecture with a DAO (Data Access Object) pattern.

---

## 📋 Table of Contents

- [Technologies Used](#technologies-used)
- [Project Structure](#project-structure)
- [Database Schema](#database-schema)
- [Key Concepts](#key-concepts)
    - [Entity Mapping](#entity-mapping)
    - [@OneToOne Unidirectional](#onetoone-unidirectional)
    - [Cascade Types](#cascade-types)
    - [DAO Pattern & @Transactional](#dao-pattern--transactional)
- [Code Walkthrough](#code-walkthrough)
    - [Entities](#entities)
    - [DAO Layer](#dao-layer)
    - [Application Runner](#application-runner)
- [How to Run](#how-to-run)
- [Conclusion](#conclusion)

---

## 🛠️ Technologies Used

| Technology       | Description                                 | Icon |
|------------------|---------------------------------------------|------|
| **Spring Boot**  | Framework for rapid development             | 🌱 |
| **Spring Data JPA** | Abstraction over JPA                       | 📦 |
| **Hibernate**    | JPA implementation (ORM)                    | 🏢 |
| **MySQL**        | Relational database                         | 🐬 |
| **Maven**        | Build and dependency management             | 📦 |
| **Java 21**      | Programming language                        | ☕ |

---

## 📁 Project Structure

```
01-jpa-one-to-one-uni/
├── pom.xml
├── src/main/java/dev/jaqs/cruddemo/
│   ├── CruddemoApplication.java          # Main class
│   ├── dao/
│   │   ├── AppDAO.java                    # DAO interface
│   │   └── AppDAOImpl.java                 # DAO implementation
│   └── entity/
│       ├── Instructor.java                  # Instructor entity
│       └── InstructorDetail.java            # InstructorDetail entity
└── src/main/resources/
    └── application.properties               # DB & logging config
```

---

## 🗄️ Database Schema

The database consists of two tables: `instructor` and `instructor_detail`, with a **foreign key** in `instructor` referencing the primary key of `instructor_detail`.

```
+-------------------+          +----------------------+
|   instructor      |          |  instructor_detail   |
+-------------------+          +----------------------+
| id (PK)           |◄---------| id (PK)              |
| first_name        |          | youtube_channel      |
| last_name         |          | hobby                 |
| email             |          +----------------------+
| instructor_detail_id (FK)    |
+-------------------+
```

- **Primary keys** are auto-generated using `IDENTITY` strategy.
- The `instructor_detail_id` column in `instructor` is a foreign key to `instructor_detail(id)`.
- **Cascade all** operations: when you save/delete an `Instructor`, the associated `InstructorDetail` is also saved/deleted.

---

## 🔑 Key Concepts

### 🧩 Entity Mapping

JPA annotations are used to map Java classes to database tables:

- `@Entity` – marks a class as a JPA entity.
- `@Table` – specifies the table name (optional if name matches class).
- `@Id` – designates the primary key.
- `@GeneratedValue(strategy = GenerationType.IDENTITY)` – uses auto-increment column.
- `@Column` – maps a field to a table column (optional if names match).

### 🔗 @OneToOne Unidirectional

The relationship is defined **only from the `Instructor` side**. The `InstructorDetail` entity has no reference back to `Instructor`.

```java
@OneToOne(cascade = CascadeType.ALL)
@JoinColumn(name = "instructor_detail_id")
private InstructorDetail instructorDetail;
```

- `@JoinColumn` specifies the foreign key column in the `instructor` table.
- Because it's unidirectional, you can navigate from `Instructor` to `InstructorDetail`, but not the other way around.

### 💥 Cascade Types

`CascadeType.ALL` means that any operation (persist, merge, remove, refresh, detach) performed on the `Instructor` will be cascaded to its associated `InstructorDetail`. For example:

- When you `persist` an `Instructor`, Hibernate will also `persist` the `InstructorDetail`.
- When you `remove` an `Instructor`, the linked `InstructorDetail` will also be deleted.

### 🧠 DAO Pattern & @Transactional

- **DAO Interface** (`AppDAO`) defines CRUD methods.
- **DAO Implementation** (`AppDAOImpl`) uses `EntityManager` to interact with the database.
- `@Repository` – Spring stereotype for DAO, enables exception translation.
- `@Transactional` – ensures that database operations are executed within a transaction. In this project, it's placed on methods that modify data (`save`, `delete`). (Best practice: move to service layer, but used here for simplicity.)

---

## 🔍 Code Walkthrough

### 1️⃣ Entities

#### `Instructor.java`
```java
@Entity
@Table(name = "instructor")
public class Instructor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    private String email;   // column name defaults to "email"

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "instructor_detail_id")
    private InstructorDetail instructorDetail;

    // constructors, getters, setters, toString()
}
```

#### `InstructorDetail.java`
```java
@Entity
@Table(name = "instructor_detail")
public class InstructorDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "youtube_channel")
    private String youtubeChannel;

    private String hobby;

    // constructors, getters, setters, toString()
}
```

### 2️⃣ DAO Layer

#### `AppDAO.java` – Interface
```java
public interface AppDAO {
    Instructor findInstructorById(Long id);
    void saveInstructor(Instructor instructor);
    void deleteInstructorById(Long id);
}
```

#### `AppDAOImpl.java` – Implementation
```java
@Repository
public class AppDAOImpl implements AppDAO {

    private final EntityManager entityManager;

    @Autowired
    public AppDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Instructor findInstructorById(Long id) {
        return entityManager.find(Instructor.class, id);
    }

    @Override
    @Transactional
    public void saveInstructor(Instructor instructor) {
        entityManager.persist(instructor);  // cascades to InstructorDetail
    }

    @Override
    @Transactional
    public void deleteInstructorById(Long id) {
        Instructor instructor = findInstructorById(id);
        entityManager.remove(instructor);    // cascades delete to detail
    }
}
```

### 3️⃣ Application Runner

`CruddemoApplication` uses a `CommandLineRunner` bean to execute demo methods:

```java
@Bean
public CommandLineRunner commandLineRunner(AppDAO appDAO) {
    return runner -> {
        // createInstructor(appDAO);
        // findInstructor(appDAO);
        deleteInstructor(appDAO);
    };
}
```

Three sample operations are provided (commented/uncommented as needed):

- **createInstructor** – creates an `Instructor` and its `InstructorDetail`, then saves them (cascade).
- **findInstructor** – retrieves an `Instructor` by id, prints both entities.
- **deleteInstructor** – deletes an `Instructor` by id (cascade deletes the detail).

---

## 🚀 How to Run

### Prerequisites

- JDK 21 or later
- MySQL Server running locally
- Maven 3.9+

### Steps

1. **Clone the repository** (if applicable) or navigate to the project folder.
2. **Create the database** in MySQL:
   ```sql
   CREATE DATABASE hb-01-one-to-one-uni;
   ```
3. **Update database credentials** in `application.properties` if needed:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/hb-01-one-to-one-uni
   spring.datasource.username=springstudent
   spring.datasource.password=springstudent
   ```
4. **Build the project**:
   ```bash
   mvn clean install
   ```
5. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
6. **Observe the console output** – Hibernate will log SQL statements (thanks to logging level `trace`).

### Sample Console Output (Create)
```
Saving instructor: Instructor{id=null, firstName='John', lastName='Doe', email='johndoe@luv2code.com', instructorDetail=null}
Hibernate: insert into instructor_detail (hobby, youtube_channel) values (?, ?)
Hibernate: insert into instructor (email, first_name, instructor_detail_id, last_name) values (?, ?, ?, ?)
Done!
```

---

## ✅ Conclusion

This project illustrates a clean and simple example of a **unidirectional one-to-one mapping** using Spring Boot and JPA/Hibernate. Key takeaways:

- Use `@OneToOne` with `@JoinColumn` to define the foreign key.
- `CascadeType.ALL` simplifies lifecycle management of associated entities.
- The DAO pattern with `EntityManager` provides a straightforward data access layer.
- Transaction management with `@Transactional` ensures data consistency.

Feel free to experiment by enabling other methods in the `CommandLineRunner` or extending the functionality (e.g., bidirectional mapping, custom queries).

---

📌 **Note**: For production applications, consider moving `@Transactional` to a dedicated service layer and adding proper exception handling. The logging level is set to `warn` for the root logger to reduce noise, but SQL binding is shown for learning purposes.

**Happy Coding!** 🎉