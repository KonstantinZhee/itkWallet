# Wallet Service API

RESTful сервис для управления балансом кошельков. Поддерживает операции пополнения (DEPOSIT) и снятия (WITHDRAW) средств, а также получение текущего баланса.

## Стек технологий

- **Java 17**
- **Spring Boot 3**
- **PostgreSQL**
- **Maven**
- **Docker** 

## API Endpoints

### 1. Изменение баланса кошелька
Выполняет операцию пополнения или снятия средств.
**Тело запроса:**
```json
{
  "valletId": "UUID",
  "operationType": "DEPOSIT | WITHDRAW",
  "amount": 1000
}