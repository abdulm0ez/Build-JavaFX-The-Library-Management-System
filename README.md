# Campus Knowledge Hub

A professional JavaFX Library Management System built with Java 21, Maven, JavaFX controls, CSS styling, OOP models, a service layer, and file handling.

## Login

- Admin: `admin` / `admin123`
- Librarian: `librarian` / `lib123`

## Features

- Role based authentication and logout
- Student registration page from the login screen
- Student login accounts with admin approval
- Modern sidebar GUI with dashboard, books, students, issue, return, history, and reports
- Book add, edit, delete, table view, search, and automatic `AVAILABLE` / `ISSUED` status
- Search suggestions for title, author, ISBN, and category
- Student add, update, delete, and institute filters
- Separate school, college, and university student structures
- School students: student ID, name, class, section, school name, address, phone, email
- College/university students: student ID, name, department/program, semester/year, institution name, address, phone, email
- Issue book with date selection and automatic available copy decrement
- Return book with late fine calculation: `late days * 25`
- Borrow history table with fine status
- Dashboard metric cards, pie chart, and bar chart
- Librarian daily summary with issued, returned, overdue, fine collected, and active loan counts
- Librarian overdue books page with late days and estimated fine
- Librarian read-only book availability and student profile access
- Issue/return search, warnings, fine preview, and receipt popups
- Student profile page with details and borrow history
- Fine payment management for unpaid fines
- Activity log for important admin/librarian/student actions
- Automatic backup files before CSV saves
- Strong validation for empty fields, email, phone, and duplicate IDs
- Availability rules for duplicate active book loans and maximum active loans per student
- Security deposit / guarantee management with deposit status and refund status
- Book issue is blocked until a student has a verified, non-refunded security deposit
- Book issue records connect student, book, admin/staff manager, and security deposit
- Students can issue available books from the student `Issue Book` page and collect the issued book from the librarian desk
- Students can return active books from the student `Return Book` page
- Student issue/return actions create pending requests for librarian approval
- Librarian approves or rejects requests from `Book Requests`
- Students request guarantee deposits from `Guarantee Request`
- Admin verifies/rejects guarantee requests from `Deposits`; admin does not directly create guarantees for students
- Student overdue notifications appear in the student `Overdue Notifications` page
- Overdue notifications are librarian-facing; admin dashboard focuses on whole-system control
- Table-based reports for issued, returned, overdue, fines, deposits, refunds, and student activity
- JavaFX alerts and confirmation dialogs
- CSV file storage in the `data` folder

## Java Tutorial Concepts Covered

## Guarantee / Security Deposit

Students must submit a guarantee/security deposit before issuing books. Admin manages deposits from:

```text
Admin -> Deposits
```

Deposit attributes:

```text
depositId
studentId
depositAmount
depositDate
depositStatus
refundStatus
```

Deposit status:

```text
PENDING
VERIFIED
REJECTED
```

Refund status:

```text
NOT_REFUNDED
REFUNDED
PARTIALLY_REFUNDED
DEDUCTED_FOR_FINE
```

Issue rule:

```text
Deposit Status = VERIFIED
Refund Status = NOT_REFUNDED
```

Return/refund rule:

```text
No fine -> deposit can be refunded
Fine exists -> fine can be deducted from deposit
Active books still exist -> deposit cannot be refunded yet
```

Deposit data is saved in:

```text
data/deposits.csv
```

This project demonstrates syntax, output through GUI alerts, comments, variables, data types, type casting through numeric values, operators, strings, math, booleans, if/else, switch expressions, loops, arrays/lists, methods, method parameters, overloading-ready structure, scope, classes, objects, attributes, constructors, `this`, modifiers, encapsulation, packages/API, inheritance-ready MVC structure, polymorphism via JavaFX controls, enums, user input, dates, exceptions, multiple exception handling style, and file create/write/read handling.

## Run

Install Maven or open the folder in IntelliJ IDEA as a Maven project, then run:

```bash
mvn javafx:run
```

Main class:

```text
com.example.thelibrarymanagementsystem.MainLibrary
```
