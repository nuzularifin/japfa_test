# Simple CRUD Application

This is a simple CRUD application built using **Kotlin**, following the **MVVM (Model-View-ViewModel)** architecture pattern with **Clean Architecture** principles. The application uses **Room** for local database management and includes features for user login, managing user data, and exporting data as CSV.

## Features
- **CRUD User Data**: Users can create, read, update, and delete user information stored in a Room database.
- **Export Data to CSV**: Export the user data from the Room database to a CSV file.
- **Login Functionality**: Simple local login feature.
- **MVVM Clean Architecture**: Implements the MVVM architecture pattern with clean architecture principles for better separation of concerns, scalability, and testability.

## Tech Stack
- **Kotlin**: Programming language used for building the application.
- **Room**: Local database management system for persisting user data.
- **Dagger-Hilt**: Dependency injection for managing dependencies across the app.
- **MVVM**: Architecture pattern used for separating concerns between UI and business logic.
- **Coroutines**: For managing background threads and asynchronous tasks.
- **LiveData**: Observes data changes and updates the UI automatically.
- **ViewModel**: Manages the UI-related data in a lifecycle-conscious way.

## Application Structure

The application is organized into the following layers:

### 1. **Data Layer**
   - **Entities**: Defines the database entities (e.g., `UserEntity`) for Room.
   - **DAOs**: Contains the Data Access Objects (DAOs) for querying the Room database.
   - **Repositories**: Provides an abstraction over the data sources (Room).

### 2. **Domain Layer**
   - **Use Cases**: Business logic for handling operations (e.g., CRUD operations, export to CSV).

### 3. **Presentation Layer**
   - **ViewModels**: Acts as the mediator between the UI and the domain layer.
   - **Views**: Activity and fragment classes responsible for displaying the UI.

## How to Run the Application

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/simple-crud-app.git

2. Login username and password static 
   ```bash
   username : admin
   password : admin123

3. For Export CSV on 
   ```bash
   storage/emulated/0/app/~/ExportCSV (sample)


