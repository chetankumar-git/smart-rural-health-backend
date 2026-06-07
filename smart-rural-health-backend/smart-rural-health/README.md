# 🏥 Smart Rural Health Link — Backend

Java Spring Boot backend for the Smart Rural Health Link platform.

---

## Tech Stack
- Java 17
- Spring Boot 3.2
- Spring Security + JWT
- MySQL
- Maven

---

## Project Structure

```
src/main/java/com/ruralhealth/
├── SmartRuralHealthApplication.java   ← Main entry point
├── model/
│   ├── User.java                      ← Patient, Doctor, Admin
│   ├── Doctor.java                    ← Doctor profile
│   ├── Appointment.java               ← Appointments
│   ├── Prescription.java              ← Digital prescriptions
│   └── Feedback.java                  ← Ratings
├── repository/
│   └── Repositories.java              ← All JPA repos
├── controller/
│   ├── AuthController.java            ← /api/auth/register, /login
│   ├── DoctorController.java          ← /api/doctors/*
│   ├── AppointmentPrescriptionController.java
│   ├── SymptomController.java         ← /api/symptoms/check
│   └── AdminController.java           ← /api/admin/*
├── security/
│   ├── JwtUtil.java                   ← Token generation/validation
│   └── JwtAuthFilter.java             ← Request filter
└── config/
    ├── SecurityConfig.java            ← CORS, roles, routes
    └── CustomUserDetailsService.java  ← Load user for auth
```

---

## API Endpoints

### Auth (Public)
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Register new user |
| POST | `/api/auth/login` | Login, returns JWT token |

### Symptoms (Public)
| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/symptoms/check` | Get specialist suggestion |

### Doctors (Public)
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/doctors/nearby?lat=17.9&lng=79.6&radiusKm=10` | Nearby doctors |
| GET | `/api/doctors/search?specialization=cardio` | Search by specialty |
| GET | `/api/doctors/available` | Currently available doctors |

### Appointments (Authenticated)
| Method | URL | Role |
|--------|-----|------|
| POST | `/api/appointments/book` | PATIENT |
| GET | `/api/appointments/patient/{id}` | PATIENT |
| GET | `/api/appointments/doctor/{id}` | DOCTOR |
| PUT | `/api/appointments/{id}/status` | DOCTOR |

### Prescriptions (Authenticated)
| Method | URL | Role |
|--------|-----|------|
| POST | `/api/prescriptions/create` | DOCTOR |
| GET | `/api/prescriptions/patient/{id}` | PATIENT |
| GET | `/api/prescriptions/verify/{rxNumber}` | Public (pharmacy QR) |

### Admin
| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/admin/dashboard` | Stats |
| GET | `/api/admin/users` | All users |
| PUT | `/api/admin/users/{id}/disable` | Disable user |
| PUT | `/api/doctors/{id}/verify` | Verify doctor |

---

## Local Setup (Your Computer)

### Step 1 — Install Requirements
- Java 17: https://adoptium.net
- Maven: https://maven.apache.org/download.cgi
- MySQL: https://dev.mysql.com/downloads/installer/

### Step 2 — Create Database
```sql
CREATE DATABASE rural_health_db;
```

### Step 3 — Configure
Edit `src/main/resources/application.properties`:
```
spring.datasource.username=root
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

### Step 4 — Run
```bash
mvn spring-boot:run
```
Backend starts at: http://localhost:8080

---

## Free Deployment on Render

1. Push this project to GitHub
2. Go to https://render.com → New → Web Service
3. Connect your GitHub repo
4. Set:
   - **Build Command:** `mvn clean install -DskipTests`
   - **Start Command:** `java -jar target/smart-rural-health-1.0.0.jar`
5. Add Environment Variables:
   ```
   SPRING_DATASOURCE_URL=jdbc:mysql://your-db-host/rural_health_db
   SPRING_DATASOURCE_USERNAME=your_db_user
   SPRING_DATASOURCE_PASSWORD=your_db_password
   APP_JWT_SECRET=your_long_random_secret_here
   APP_CORS_ALLOWED_ORIGINS=https://your-frontend.vercel.app
   ```
6. Click Deploy → Free URL with no watermark ✅

### Free MySQL on Railway
1. Go to https://railway.app
2. New Project → Add MySQL
3. Copy the connection URL into Render env variables

---

## Connect Frontend to Backend

In your `index.html`, add this at the top of your `<script>`:
```javascript
const API_BASE = "https://your-backend.onrender.com";

// Example: Login
async function doLogin() {
  const res = await fetch(API_BASE + "/api/auth/login", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ mobile: "9876543210", password: "doctor123" })
  });
  const data = await res.json();
  localStorage.setItem("token", data.token);
}

// Example: Check symptoms
async function checkSymptomsAPI(symptoms) {
  const res = await fetch(API_BASE + "/api/symptoms/check", {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ symptoms })
  });
  return await res.json();
}

// Example: Nearby doctors
async function getNearbyDoctors(lat, lng) {
  const res = await fetch(API_BASE + `/api/doctors/nearby?lat=${lat}&lng=${lng}&radiusKm=15`);
  return await res.json();
}
```

---

## JWT Usage

After login, include token in all authenticated requests:
```javascript
headers: {
  "Authorization": "Bearer " + localStorage.getItem("token"),
  "Content-Type": "application/json"
}
```

---

## Team
- 23951A1203: Tatipadigala Abhilash Goud
- 23951A1227: G Chetan Kumar
- 23951A1262: M Manivarsh

Department of Information Technology
