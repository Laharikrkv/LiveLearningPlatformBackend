# Live Learning Platform — Course Booking Backend

A Spring Boot microservice for managing course offerings, schedules, and concurrent seat bookings. Handles regional timezone scheduling, normalizes all timestamps to UTC, and prevents overbooking using pessimistic database locking.

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language & Framework | Java 17, Spring Boot 3.x (Web, Data JPA) |
| Database | MySQL 8.0 |
| Testing | JUnit 5, Mockito |
| Build Tool | Maven |
| Containerization | Docker, Docker Compose |
| Utilities | Lombok |

---

## Database Schema

```
+-------------------+              +-------------------+
|       USER        |              |      COURSE       |
+-------------------+              +-------------------+
| id (PK)           |              | id (PK)           |
| name              |              | name              |
| timezone          |              | description       |
+--------+----------+              +---------+---------+
         |                                   |
         | 1                                 | 1
         |                                   |
         | N                                 | N
+--------v-----------------------------------v---------+
|                      OFFERING                        |
+------------------------------------------------------+
| id (PK)                                              |
| course_id (FK)                                       |
| teacher_id (FK)                                      |
| title                                                |
| max_capacity                                         |
| current_bookings_count                               |
| start_time (Instant — UTC)                           |
| end_time   (Instant — UTC)                           |
+------------------------+-----------------------------+
                         |
                         | 1
                         |
                         | N
                +--------v----------+
                |      SESSION      |
                +-------------------+
                | id (PK)           |
                | offering_id (FK)  |
                | teacher_id (FK)   |
                | session_date      |
                | start_time (UTC)  |
                | end_time (UTC)    |
                +-------------------+
```

**Entity summary:**

- **User** — Represents teachers and parents, stores local timezone preference.
- **Course** — Master catalog item (the "what").
- **Offering** — A scheduled batch of a course assigned to a teacher with a capacity cap (the "when").
- **Session** — Individual daily class occurrences within an offering.
- **Booking** — A confirmed student seat reservation linked to a specific offering.

---

## Concurrency Handling

High-traffic booking windows are handled with **Pessimistic Locking (`SELECT ... FOR UPDATE`)**.

1. A custom repository method annotated with `@Lock(LockModeType.PESSIMISTIC_WRITE)` acquires a row-level lock on the target offering.
2. The first thread to enter the transaction locks that row; all concurrent requests for the same `offeringId` queue at the database level.
3. Once the active thread increments `currentBookingsCount`, persists the booking record, and commits, the lock releases.
4. The next waiting thread wakes up, reads the updated count, and throws an `IllegalStateException` immediately if capacity is exhausted.

---

## Timezone Handling

1. **Input** — Teachers specify their class window in local time (e.g., Mon/Wed 10:00–11:30 AM `Asia/Kolkata`).
2. **UTC normalization** — The service reads the teacher's profile timezone, iterates day-by-day through the requested date range, pairs each local date with the given time, and converts it to an absolute `Instant` (UTC).
3. **Date persistence** — A plain `yyyy-MM-dd` string is stored on each session record so that midnight timezone shifts never bleed into neighbouring calendar views.

---

## Assumptions

- Individual live sessions do not cross midnight in local time.
- A teacher's profile timezone does not change during an offering creation workflow.
- Once an offering's sessions are committed, their dates are immutable.
- Parent conflict detection is validated against the absolute UTC timelines of confirmed bookings.

---

## API Reference

### Offerings

#### Create a Batch Offering

```
POST /api/v1/offerings
```

**Request body:**

```json
{
  "title": "Advanced Java BootCamp",
  "courseId": 1,
  "teacherId": 2,
  "maxCapacity": 15,
  "startDate": "2026-06-01",
  "endDate": "2026-06-15",
  "startTime": "10:00:00",
  "endTime": "11:30:00",
  "daysOfWeek": ["MONDAY", "WEDNESDAY"]
}
```

#### Get Upcoming Teacher Batches

```
GET /api/v1/offerings/teacher/{teacherId}/upcoming
```

---

### Bookings

#### Confirm a Seat

```
POST /api/v1/bookings/create
```

**Request body:**

```json
{
  "parentId": 5,
  "offeringId": 10
}
```

**Response:** `201 Created` — returns the booking ID as a `Long`.

```json
1042
```

---

## Environment Variables

| Variable | Purpose |
|---|---|
| `SERVER_PORT` | Port the server listens on |
| `SPRING_DATASOURCE_URL` | JDBC connection URL for the database |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |

---

## Running Locally

### Option 1 — Docker Compose (Recommended)

```bash
git clone https://github.com/Laharikrkv/live-learning-platform.git
cd live-learning-platform
docker-compose up --build
```

The server starts at `http://localhost:8080`.

### Option 2 — Manual Setup

1. Start a local MySQL instance on port `3302`.

2. Create the database:

```sql
CREATE DATABASE live_learning_platform;
```

3. Build the project:

```bash
mvn clean package -DskipTests
```

4. Run the jar:

```bash
java -jar target/*.jar
```

---

## Testing

Unit tests cover the service layer in full isolation using JUnit 5 and Mockito.

**Booking tests:** success path, duplicate booking (`IllegalStateException`), capacity exceeded (`IllegalStateException`), schedule overlap (`IllegalArgumentException`).

**Offering tests:** correct UTC conversion for dynamic timezone offsets (e.g., `Asia/Kolkata`), error on reversed date/time inputs.
