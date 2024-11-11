# Blood Donor View App
The **Blood Donor View App** is a foundational Android application designed for managing and viewing blood donor and donation event data. While the app is functionally complete, it is set up as a flexible prototype, allowing future developers to customize and expand its functionality. The app includes two main pages — a **Database Page** and a **User View Page** — which could be further adapted for separate access by admins and general users.

## Features
### Database Management (Admin Functionality):
- Allows adding, updating, and deleting donor profiles and blood donation event records.
- Uses a Room database for offline storage, which can be easily configured for an online database if required.

### User View:
- Provides an interface to browse the user list and select specific profiles.
- Displays details like the donor’s donation history, last donation date, and location.
- Interactive calendar for viewing and filtering donation events by date and city.

## Future Development Potential
- Role-Based Access: Currently, both the Database and User View pages are accessible within the same app interface. This setup allows easy switching between admin and user views but can be further developed to restrict access (e.g., through login authentication).
- Online Database Integration: The app is built with Room for offline database management, but developers can transition to an online database, enhancing real-time updates and multi-user access.

## Installation
1. Clone this repository:
```bash
git clone https://github.com/jodayyy/Blood-Donor-View-App.git
```

2. Open the project in Android Studio.

3. Build and run the app on an emulator or Android device.

## Usage
Refer to the Manual for detailed guidance. Here are the basic steps:
1. **Database Page:** Accessible to manage donor and event data with options to insert, update, and delete.
2. **User View Page:** Allows users to browse donor profiles and view details of donation events.
