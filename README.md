# 🚇 Metro Rail Ticket Vending System (MRTVS)

![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Swing](https://img.shields.io/badge/Java_Swing-007396?style=for-the-badge&logo=java&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-blue.svg?style=for-the-badge)

A comprehensive, modern desktop application built in Java that simulates a real-world Metro Rail Ticket Vending System. Designed with a sleek glassmorphism UI, this project demonstrates core Object-Oriented Programming (OOP) concepts while providing a rich, interactive user experience.

---

## 📖 Project Overview and Purpose

The **Metro Rail Ticket Vending System** is an automated kiosk application designed to streamline the ticketing process for metro commuters. The project aims to simulate a complete end-to-end ticketing workflow—from user registration and station selection to payment processing and digital ticket generation. 

It serves as an excellent educational showcase for applying **Core Java** and **Java Swing** to build a modern, robust, and functional desktop application.

---

## ✨ Features

*   **🔒 Secure Authentication:** User Registration, Login, and Password Recovery.
*   **👮 Administrator Dashboard:** Centralized control for managing system status (opening/closing services), marking stations for maintenance, and viewing logs.
*   **🎫 Smart Ticketing:** Intuitive origin and destination selection with dynamic fare calculation.
*   **💳 Multi-Channel Payment System:**
    *   **Cash:** Interactive coin/note insertion with automatic change calculation.
    *   **Digital Wallet:** Mock integrations for bKash, Nagad, and Credit Cards.
    *   **MRT Pass:** Direct balance deduction for frequent commuters.
*   **🪪 MRT Pass Management:** Dedicated portal for users to recharge and check their pass balance.
*   **📄 PDF Ticket Generation:** Automatically generates a professional, downloadable digital PDF ticket upon successful payment.
*   **⏱️ Live Arrival Estimation:** Real-time countdown timers for incoming trains based on peak and off-peak schedules.
*   **🗺️ Interactive Route Map & Timings:** Visual display of station schedules and routes.
*   **🎨 Modern UI/UX:** Built with custom Java Swing components featuring rounded corners, gradient backgrounds, and a premium glassmorphism aesthetic.

---

## 🛠️ Technology Stack

*   **Language:** Java (JDK 8 or higher)
*   **GUI Framework:** Java Swing, AWT
*   **Data Persistence:** Flat-file Database (Text files for users, logs, and passes)
*   **Architecture:** Custom Utility Classes (`UIHelper`, `ServiceStateManager`, `MRTPassManager`)

---

## 🧠 OOP Concepts Demonstrated

This project heavily leverages Object-Oriented principles to maintain clean, scalable, and modular code:

*   **Encapsulation:** Data and methods are tightly coupled within domain-specific classes (e.g., `MRTPassManager`, `PDFTicketGenerator`), hiding internal states from the outside world.
*   **Inheritance:** Custom UI components extend core Java Swing classes (e.g., `PaymentPage extends JFrame`, `UIHelper.RoundedPanel extends JPanel`) to inherit and expand upon base functionalities.
*   **Polymorphism:** Heavy use of method overriding (e.g., overriding `paintComponent(Graphics g)` for custom graphics rendering) and interface implementation (e.g., `ActionListener`, `MouseListener`) to handle dynamic user interactions.
*   **Abstraction:** Complex sub-systems, such as file handling for system states and ticket generation, are abstracted away behind simple method calls, keeping the front-end clean.

---

## 📂 Project Architecture

```
MRTVS/
├── Start.java                   # Main application entry point
├── Classes/                     # Core application logic and UI pages
│   ├── WelcomePage.java         # Initial landing and login screen
│   ├── AdminPage.java           # Administrator dashboard and controls
│   ├── MetroTicketVendingSystemPage.java # Station selection and live timing
│   ├── PaymentPage.java         # Multi-channel payment processing
│   ├── UIHelper.java            # Custom UI components (Buttons, Panels, Fields)
│   ├── PDFTicketGenerator.java  # Handles PDF ticket creation
│   └── ... (Other UI and Manager classes)
├── Data/                        # Flat-file database storage (Users, Transactions)
├── Images/                      # UI assets, backgrounds, and icons
├── Tickets/                     # Directory where generated PDF and TXT tickets are saved
└── run.bat                      # Windows batch script to compile and run the project
```

---

## 🚀 Installation & Running Instructions

### Prerequisites
*   Ensure you have **Java Development Kit (JDK) 8** or higher installed.
*   Add Java to your system's PATH.

### Steps to Run

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/s21gem/MRTVS.git
    cd MRTVS
    ```

2.  **Compile and Execute:**
    *   **On Windows:** Simply double-click the `run.bat` file, or run it via Command Prompt:
        ```cmd
        run.bat
        ```
    *   **Manual Compilation (Any OS):**
        ```bash
        javac Start.java Classes/*.java
        java Start
        ```

---

## 🔑 Demo Credentials

To explore the application without registering, use the following built-in accounts:

**Administrator Access:**
*   **Admin ID:** `admin`
*   **Password:** `admin`

**User Access:**
*   You can create a new user instantly by clicking the **Register** button on the Welcome Page.

---

## 🔮 Future Improvements

*   **Database Integration:** Migrate from flat-file storage to a relational database (e.g., MySQL, SQLite) for better data integrity and concurrency.
*   **Hardware Integration API:** Add modular interfaces to connect with physical coin acceptors and ticket printers.
*   **Localization:** Add support for multiple languages (e.g., Bengali and English).
*   **Admin Analytics:** Integrate charts and graphs in the Admin dashboard for revenue and traffic analysis.

---

## 👨‍💻 Author

**s21gem**
*   GitHub: [@s21gem](https://github.com/s21gem)

---
*If you like this project, please consider giving it a ⭐ on GitHub!*
