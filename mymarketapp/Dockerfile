# Используем официальный образ OpenJDK 17
FROM eclipse-temurin:21-jdk-jammy
LABEL authors="jurahf"

# Устанавливаем рабочую директорию
WORKDIR /app

# Создаем пользователя для запуска приложения
RUN groupadd -r appuser && useradd -r -g appuser appuser

# Копируем JAR файл в контейнер
COPY target/*.jar app.jar

# Даем права на файл
RUN chown appuser:appuser app.jar

# Переключаемся на пользователя appuser
USER appuser

# Открываем порт 8080
EXPOSE 8080

# Запускаем приложение
ENTRYPOINT ["java", "-jar", "app.jar"]