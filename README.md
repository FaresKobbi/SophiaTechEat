# SophiaTech Eats – Team I-1

## Project Team
- Fares Kobbi – Software Architect and Quality Assurance (SA/QA)
- Youssef Ben Mzoughia – Software Architect and Product Owner (SA/PO)
- Younes Hammoud – Operations (OPS)

## Prerequisites
- **Java**: JDK 17
- **Maven**: 3.9+
- **Node.js**: LTS recommended
- **Angular CLI**: `npm install -g @angular/cli`
- **Frontend Dependencies**: Run `npm install` in `front/SophiaTech-Eats`

## Install and Run the Project

### Quick Start (All-in-One)
To launch all services (Backend + Frontend) with a single command:
```bash
./launch_all.sh
```
This will start:
- API Gateway (Port 8080)
- Restaurant Service (Port 8081)
- Student Account Service (Port 8082)
- Order Service (Port 8083)
- Frontend Angular (Port 4200)

### Backend
1. **Clone the repository**
   ```bash
   git clone https://github.com/PNS-Conception/ste-25-26-team-i-1.git
   cd ste-25-26-team-i-1
   ```
2. **Build and run tests**
   ```bash
   mvn clean package
   mvn test
   ```

### Frontend
1. **Install dependencies**
   ```bash
   cd front/SophiaTech-Eats
   npm install
   ```
2. **Run the application**
   ```bash
   ng serve
   ```
   The application will be accessible at `http://localhost:4200/`.

## Project Structure
```
├── front/         # Frontend source code (Angular)
├── src/
│   ├── main/      # Backend application code (Java)
│   └── test/      # JUnit & Cucumber test suites
├── pom.xml        # Maven configuration (JDK 17, Cucumber 7, JUnit 5)
└── README.md      # Project presentation
```

This standard structure allows for clear separation of production code and tests. Dependencies (Cucumber, JUnit, etc.) are centralized in `pom.xml`.

## Additional Documentation
- [Work Visualization (Screenshots)](screenshots.md)
- [Points Distribution](points_distribution.md)

## Team Kanban Board
The original GitHub repository was created by my professors; it's private, so I can't show the Team Kanban Board.
