# UrPizza — Настільна система керування піцерією

## Вимоги до запуску

- **Java 21+** (перевір: `java -version`)
- **Docker Desktop** (для PostgreSQL)

## Як запустити (для розробки)

1. Запустити PostgreSQL:

   ```bash
   docker-compose up -d
   ```

2. Запустити програму:

   ```bash
   mvn clean compile javafx:run
   ```

   Або відкрити в IntelliJ IDEA та запустити `Launcher.java`.

## Встановлення (реліз)

1. Запустити `UrPizza-1.1.0.msi`
2. Встановити Docker Desktop, якщо ще не встановлений
3. Відкрити **PowerShell** або **CMD** від адміністратора і виконати:

   ```bash
   docker-compose up -d
   ```

   (команду виконувати в папці, де лежить `docker-compose.yml`, або скопіювати його в зручне місце)
4. Запустити `UrPizza.exe`

## Акаунти (за замовчуванням)

| Email | Пароль | Роль |
|-------|--------|------|
| admin@urpizza.local | 123456 | Адміністратор |
| chef@urpizza.local | 123456 | Кухар |
| courier@urpizza.local | 123456 | Кур'єр |
| зареєструвати самостійно | свій пароль | Користувач |
