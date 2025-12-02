# SopiaTech Eats – Team I-1

## Équipe projet
- Fares Kobbi – Software Architect and Quality Assurance(SA/QA)
- Youssef Ben Mzoughia – Software Architect and Product Owner (SA/PO)
- Younes Hammoud – Operations (OPS)

## Installer et lancer le projet

### Lancement rapide (Tout-en-un)
Pour lancer tous les services (Backend + Frontend) en une seule commande :
```bash
./launch_all.sh
```
Cela démarrera :
- API Gateway (Port 8080)
- Restaurant Service (Port 8081)
- Student Account Service (Port 8082)
- Order Service (Port 8083)
- Frontend Angular (Port 4200)

### Backend
1. **Prérequis**
    - JDK 17
    - Maven 3.9+
2. **Cloner le dépôt**
   ```bash
   git clone https://github.com/PNS-Conception/ste-25-26-team-i-1.git
   cd ste-25-26-team-i-1
   ```
3. **Construire et exécuter les tests**
   ```bash
   mvn clean package
   mvn test
   ```

### Frontend
1. **Prérequis**
    - Node.js (LTS recommandé)
    - Angular CLI (`npm install -g @angular/cli`)
2. **Installation des dépendances**
   ```bash
   cd front/SophiaTech-Eats
   npm install
   ```
3. **Lancer l'application**
   ```bash
   ng serve
   ```
   L'application sera accessible sur `http://localhost:4200/`.

## Structure du projet
```
├── front/         # Code source du Frontend (Angular)
├── src/
│   ├── main/      # Code applicatif Backend (Java)
│   └── test/      # Jeux de tests JUnit & Cucumber
├── pom.xml        # Configuration Maven (JDK 17, Cucumber 7, JUnit 5)
└── README.md      # Présentation du projet
```

Cette structure standard permet de séparer clairement le code de production et les tests. Les dépendances (Cucumber, JUnit, etc.) sont centralisées dans le `pom.xml`.

## Documentation supplémentaire
- [Visualisation du travail (Captures d'écran)](screenshots.md)
- [Répartition des points](points_distribution.md)

## Tableau Kanban de l'équipe
[Github – Kanban SopiaTech Eats Team I-1](https://github.com/orgs/PNS-Conception/projects/91)