# 💸 SplitXpence

> **A Group Expense Sharing and Settlement Management System built with Java, Spring Boot, Spring MVC, Spring Data JPA, Hibernate, MySQL, HTML, CSS, and JavaScript.**

SplitXpence is a college mini-project designed to simplify shared expense management between friends, groups, trips, flatmates, and small teams. It allows users to **create groups, add friends and members, record shared expenses, split costs equally, calculate balances, settle payments, search expense history, and view reports** through a modern browser-based interface.

The project uses a **Spring Boot backend**, **Hibernate / JPA persistence**, **MySQL database**, and a responsive frontend built with **HTML, CSS, and vanilla JavaScript**.

---

## ✨ Highlights

- 👤 User registration, login, logout, and profile management
- 🤝 Add, view, and remove friends
- 👥 Create, edit, view, select, and delete expense groups
- 👯 Add and remove group members
- 💳 Add, edit, view, search, and delete expenses
- ➗ Equal expense splitting between selected participants
- ⚖️ Group balance and personal balance calculation
- 💸 "Who owes whom" simplified settlement suggestions
- 🤝 Record full or partial settlements
- 📜 Settlement history
- 📊 Group summary, category report, and monthly report
- 🧾 Expense history and recent expense tracking
- 🏠 Dashboard with total groups, expenses, amount owed, and amount receivable
- 🔎 Search and filter expenses by title, group, category, and date range
- 🎨 Responsive colorful frontend with modal-based results
- 🗄️ MySQL database integration using Spring Data JPA and Hibernate
- 🚀 Embedded Tomcat through Spring Boot
- 🖥️ Windows `.bat` launcher for easy startup

---

# 📌 Table of Contents

- [About the Project](#-about-the-project)
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Project Structure](#-project-structure)
- [Application Flow](#-application-flow)
- [Database Entities](#-database-entities)
- [Requirements](#-requirements)
- [Database Setup](#-database-setup)
- [Configuration](#-configuration)
- [Running the Application](#-running-the-application)
- [Windows Launcher](#-windows-launcher)
- [Major Application Modules](#-major-application-modules)
- [Main Backend Endpoints](#-main-backend-endpoints)
- [Testing Checklist](#-testing-checklist)
- [Security Notes](#-security-notes)
- [Future Improvements](#-future-improvements)
- [GitHub Setup](#-github-setup)
- [Project Status](#-project-status)
- [Author](#-author)

---

# 📖 About the Project

**SplitXpence** is a Java-based web application for managing shared expenses.

The application allows a user to:

- Register and login
- Create and manage expense groups
- Maintain a personal friend list
- Add users to groups
- Record expenses
- Select expense participants
- Split expenses equally
- Calculate group balances
- Check personal balances
- View who owes whom
- Record settlements
- Search expenses
- View expense history
- Generate simple spending reports

The backend is developed using **Spring Boot**, **Spring MVC**, **Spring Data JPA**, and **Hibernate**.

The frontend uses:

- HTML5
- CSS3
- Vanilla JavaScript
- Fetch API

Data is stored permanently in **MySQL**.

---

# 🚀 Features

## 👤 Authentication

SplitXpence includes a simple session-based authentication system.

### Available operations

- Register account
- Login
- Logout
- Session tracking
- View profile

After successful login, the user is redirected to the main dashboard.

---

## 🤝 Friends

Users can maintain their own friend list.

### Features

- Add friend using email
- View added friends
- Remove friend
- Select friends while creating a group

The friend system helps simplify group creation.

---

## 👥 Groups

Groups are used to organize shared expenses.

Examples:

- Goa Trip
- Flatmates
- College Friends
- Dinner Group
- Office Team

### Available operations

- Create group
- Add description
- Add selected friends while creating a group
- View groups
- Select active group
- Edit group
- Delete group
- Add members
- Remove members

Groups containing existing expenses or settlements are protected from direct deletion.

---

## 💳 Expense Management

Users can record shared expenses inside a group.

### Expense information

- Title
- Amount
- Date
- Category
- Group
- Paid by
- Participants

### Available operations

- Add expense
- View expenses by group
- Edit expense
- Delete expense
- View expense split
- Search expenses
- Filter expenses

### Current categories

- Food
- Travel
- Rent
- Shopping
- Entertainment
- Other

---

## ➗ Expense Splitting

SplitXpence currently supports **equal splitting**.

Example:

```text
Expense Amount = ₹1200
Participants   = 3

Share per participant = ₹400
```

Each selected participant receives an equal share of the expense.

The payer is credited with the amount paid, while each participant is charged their calculated share.

---

## ⚖️ Balance Calculation

SplitXpence calculates balances using recorded expenses, splits, and settlements.

### Balance meaning

```text
Positive balance = User should receive money
Negative balance = User owes money
```

### Available views

- Group Balance
- My Balance
- Who Owes Whom

The **Who Owes Whom** feature simplifies the final debt relationships between members.

---

## 🤝 Settlements

Users can record payments made between group members.

### Features

- Record payer
- Record receiver
- Enter amount
- Enter settlement date
- Support partial settlements
- View settlement history

Settlements automatically affect the calculated group balances.

---

## 🏠 Dashboard

The dashboard provides a quick overview of the logged-in user's activity.

### Dashboard information

- Total groups
- Total expenses
- Amount you owe
- Amount owed to you
- Recent expenses
- Quick navigation

The dashboard acts as the main home screen after login.

---

## 🔍 Search and Filter

Expenses can be searched or filtered using:

- Expense title
- Group
- Category
- Start date
- End date

This allows users to quickly find previous transactions.

---

## 📜 Expense History

The expense history feature displays recorded expenses for a selected group.

Expenses are arranged with the most recent activity first.

---

## 📊 Reports

SplitXpence includes simple reports for selected groups.

### Available reports

- Group Summary
- Category Report
- Monthly Report
- Expense History

These reports provide an overview of spending patterns without requiring external analytics software.

---

# 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Programming Language | Java 17 |
| Backend Framework | Spring Boot |
| Web Layer | Spring MVC |
| ORM | Hibernate |
| Persistence | Spring Data JPA |
| Database | MySQL |
| Frontend | HTML5 |
| Styling | CSS3 |
| Client Logic | Vanilla JavaScript |
| HTTP Requests | Fetch API |
| Build Tool | Maven |
| Application Server | Embedded Tomcat |
| Primary IDE | NetBeans |
| Database Tool | MySQL Workbench |
| Version Control | Git |
| Repository Hosting | GitHub |
| Target Platform | Windows |

---

# 📁 Project Structure

```text
SplitXpence/
│
├── pom.xml
├── README.md
├── .gitignore
├── Start-SplitXpence.bat
│
├── src/
│   └── main/
│       │
│       ├── java/
│       │   └── com/
│       │       └── splitxpence/
│       │           │
│       │           ├── SplitXpenceApplication.java
│       │           ├── SplitXpence.java
│       │           │
│       │           ├── controller/
│       │           │   ├── UserController.java
│       │           │   ├── GroupController.java
│       │           │   ├── FriendController.java
│       │           │   ├── ExpenseController.java
│       │           │   ├── BalanceController.java
│       │           │   ├── SettlementController.java
│       │           │   ├── DashboardController.java
│       │           │   ├── ProfileController.java
│       │           │   ├── HistoryController.java
│       │           │   └── ReportController.java
│       │           │
│       │           ├── model/
│       │           │   ├── User.java
│       │           │   ├── Friend.java
│       │           │   ├── ExpenseGroup.java
│       │           │   ├── GroupMember.java
│       │           │   ├── Expense.java
│       │           │   ├── ExpenseSplit.java
│       │           │   └── Settlement.java
│       │           │
│       │           └── repository/
│       │               ├── UserRepository.java
│       │               ├── FriendRepository.java
│       │               ├── GroupRepository.java
│       │               ├── GroupMemberRepository.java
│       │               ├── ExpenseRepository.java
│       │               ├── ExpenseSplitRepository.java
│       │               └── SettlementRepository.java
│       │
│       └── resources/
│           │
│           ├── application.properties
│           ├── application.properties.example
│           │
│           └── static/
│               │
│               ├── index.html
│               │
│               ├── css/
│               │   └── style.css
│               │
│               ├── js/
│               │   └── app.js
│               │
│               └── html/
│                   ├── login.html
│                   ├── register.html
│                   ├── dashboard.html
│                   ├── groups.html
│                   ├── group-members.html
│                   ├── add-expense.html
│                   ├── balance.html
│                   ├── settlement.html
│                   ├── reports.html
│                   └── profile.html
│
└── target/
```

> `target/` and local environment-specific configuration files should normally be excluded from Git.

---

# 🔄 Application Flow

```text
User
 │
 ▼
Register / Login
 │
 ▼
Spring MVC Controller
 │
 ▼
Repository Layer
 │
 ▼
Spring Data JPA / Hibernate
 │
 ▼
MySQL Database
 │
 ▼
Controller Response
 │
 ▼
JavaScript Fetch API
 │
 ▼
HTML / Modal / Dashboard
```

### Expense flow

```text
User selects Group
        │
        ▼
Adds Expense
        │
        ▼
ExpenseController
        │
        ├── Saves Expense
        │
        └── Creates Equal ExpenseSplit records
                │
                ▼
             MySQL
                │
                ▼
       Balance Calculation
                │
                ▼
     Group Balance / Who Owes Whom
```

---

# 🗄️ Database Entities

SplitXpence currently uses the following main entities.

| Entity | Purpose |
|---|---|
| `User` | Stores registered users |
| `Friend` | Stores a user's friend relationships |
| `ExpenseGroup` | Stores expense groups |
| `GroupMember` | Connects users to groups |
| `Expense` | Stores shared expense information |
| `ExpenseSplit` | Stores each participant's expense share |
| `Settlement` | Stores payments between members |

Hibernate automatically manages the database tables based on the entity classes.

---

# ✅ Requirements

## Java

Recommended:

```text
Java 17
```

Check installation:

```powershell
java -version
```

and:

```powershell
javac -version
```

---

## MySQL

Install and run:

```text
MySQL Server
MySQL Workbench
```

The local database used by the project is:

```text
splitxpence_db
```

---

## Maven

The project uses Maven through NetBeans.

If Maven is installed globally, you can check:

```powershell
mvn -version
```

A global Maven installation is not required when building directly through NetBeans.

---

# 🗄️ Database Setup

Open MySQL Workbench and create the database:

```sql
CREATE DATABASE splitxpence_db;
```

Then verify:

```sql
SHOW DATABASES;
```

Spring Boot and Hibernate will create or update the required tables automatically when the application starts.

---

# ⚙️ Configuration

The local configuration file is:

```text
src/main/resources/application.properties
```

Example configuration:

```properties
spring.application.name=SplitXpence

spring.datasource.url=jdbc:mysql://localhost:3306/splitxpence_db
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

server.port=8080
```

For GitHub, keep real database credentials out of the repository.

An example configuration can be stored as:

```text
application.properties.example
```

---

# ▶️ Running the Application

## Option 1 — NetBeans

1. Open the SplitXpence project in NetBeans.
2. Make sure MySQL Server is running.
3. Run the project.
4. Wait for Spring Boot to start.
5. Open:

```text
http://localhost:8080
```

---

## Option 2 — Maven

If Maven is available on the system PATH:

```powershell
mvn spring-boot:run
```

Then open:

```text
http://localhost:8080
```

---

# 🖥️ Windows Launcher

The project includes:

```text
Start-SplitXpence.bat
```

Before using it:

1. Perform **Clean and Build** in NetBeans.
2. Make sure MySQL Server is running.
3. Double-click the launcher.

The launcher:

- Opens the project folder
- Finds the generated JAR inside `target/`
- Starts the Spring Boot server
- Waits for startup
- Opens the application in the default browser

The application opens at:

```text
http://localhost:8080
```

---

# 🧩 Major Application Modules

```text
SplitXpence
│
├── Authentication
│   ├── Register
│   ├── Login
│   └── Logout
│
├── Profile
│   └── Friends
│
├── Groups
│   ├── Create
│   ├── Edit
│   ├── Delete
│   └── Members
│
├── Expenses
│   ├── Add
│   ├── Edit
│   ├── Delete
│   ├── Search
│   └── Equal Split
│
├── Balances
│   ├── Group Balance
│   ├── My Balance
│   └── Who Owes Whom
│
├── Settlements
│   ├── Record Payment
│   └── Settlement History
│
├── Dashboard
│
├── History
│
└── Reports
    ├── Group Summary
    ├── Category Report
    └── Monthly Report
```

---

# 🌐 Main Backend Endpoints

| Endpoint | Purpose |
|---|---|
| `/register` | Register user |
| `/login` | Login user |
| `/logout` | Logout user |
| `/profile` | View profile |
| `/friends` | View friends |
| `/add-friend` | Add friend |
| `/remove-friend` | Remove friend |
| `/create-group` | Create group |
| `/groups` | View groups |
| `/edit-group` | Edit group |
| `/delete-group` | Delete group |
| `/add-member` | Add group member |
| `/group-members` | View group members |
| `/remove-member` | Remove group member |
| `/add-expense` | Add expense |
| `/expenses` | View group expenses |
| `/expense-splits` | View expense split |
| `/edit-expense` | Edit expense |
| `/delete-expense` | Delete expense |
| `/search-expenses` | Search expenses |
| `/group-balance` | View group balances |
| `/my-balance` | View personal group balance |
| `/who-owes-whom` | View simplified debts |
| `/record-settlement` | Record settlement |
| `/settlement-history` | View settlement history |
| `/dashboard-data` | Dashboard statistics |
| `/expense-history` | View expense history |
| `/group-summary` | Group report |
| `/category-report` | Category report |
| `/monthly-report` | Monthly report |

---

# 🧪 Testing Checklist

Before demonstration or submission, test:

- Application launch
- MySQL connection
- Registration
- Login
- Logout
- Profile loading
- Add friend
- Remove friend
- Create group
- Edit group
- Delete empty group
- Add group member
- Remove group member
- Add expense
- Equal split creation
- View expenses
- Edit expense
- Delete expense
- Expense search
- Category filter
- Date filter
- Group balance
- Personal balance
- Who owes whom
- Record settlement
- Partial settlement
- Settlement history
- Dashboard statistics
- Recent expenses
- Expense history
- Group summary
- Category report
- Monthly report
- Modal result windows
- Navigation between all frontend pages
- Logout redirect
- Windows `.bat` launcher

---

# 🔒 Security Notes

SplitXpence is currently an **academic mini-project**.

The current implementation focuses on application logic, database integration, Spring Boot concepts, and expense management.

For a production version, the following should be improved:

- Password hashing using BCrypt or another secure password encoder
- Stronger authentication and authorization
- CSRF protection
- Request validation
- Role-based permissions
- Safer API response objects
- Database credential management using environment variables or secrets
- Improved session security

The local `application.properties` file containing database credentials should not be committed to GitHub.

---

# 🔮 Future Improvements

Possible future additions include:

- Custom percentage split
- Custom amount split
- Automatic friend suggestions
- Bidirectional friend relationships
- Email invitations
- Better group-member selection using checkboxes
- Expense receipt upload
- Profile pictures
- Recurring expenses
- Budget limits
- Group spending limits
- Downloadable PDF reports
- CSV export
- Charts and advanced analytics
- Notifications
- Email reminders
- Mobile responsive improvements
- Spring Security authentication
- Password hashing
- Cloud deployment
- REST API versioning
- Multi-currency support

These are outside the current mini-project scope.

---

# 🌐 GitHub Setup

Useful Git commands:

```powershell
git status
git add .
git commit -m "Your commit message"
git push
```

For the completed SplitXpence project:

```powershell
git add .
git commit -m "Finalize SplitXpence expense management application"
git push
```

Check the repository state:

```powershell
git status
```

Expected result:

```text
nothing to commit, working tree clean
```

---

# 🏁 Project Status

### ✅ Core mini-project development complete

SplitXpence currently demonstrates:

- Java programming
- Object-oriented programming
- Spring Boot
- Spring MVC
- REST-style HTTP endpoints
- Session handling
- Spring Data JPA
- Hibernate ORM
- MySQL integration
- Entity relationships
- CRUD operations
- Form handling
- Fetch API
- JSON responses
- HTML / CSS / JavaScript frontend development
- Search and filtering
- Expense splitting
- Balance calculation
- Settlement tracking
- Dashboard statistics
- Reporting
- Responsive UI design
- Git and GitHub workflow
- Windows launcher packaging

The project is now in its **final testing, documentation, GitHub, and academic submission stage**.

---

# 👨‍💻 Development Notes

SplitXpence was created as an academic mini-project with emphasis on building a practical Java web application using Spring Boot, Hibernate, MySQL, and frontend web technologies.

It is intended for:

- Academic demonstration
- College mini-project submission
- Learning Spring Boot
- Learning Hibernate / JPA
- Practicing database integration
- Portfolio use

---

<div align="center">

## 💸 SplitXpence

**Groups • Expenses • Splits • Balances • Settlements • Reports**

Built with **Java + Spring Boot + Hibernate + MySQL**

</div>

---

# 👨‍💻 Author

### Tushar Thakur

**MCA Data Science**  
**Chandigarh University**

[![GitHub](https://img.shields.io/badge/GitHub-tushar--011-181717?logo=github)](https://github.com/tushar-011)

[![LinkedIn](https://img.shields.io/badge/LinkedIn-Tushar%20Thakur-0A66C2?logo=linkedin)](https://www.linkedin.com/in/tushar-thakur-8848a7396)

[![Email](https://img.shields.io/badge/Email-artificial.thakur%40gmail.com-EA4335?logo=gmail)](mailto:artificial.thakur@gmail.com)
