
# my-market-app

Мультимодульный проект (задание на 5 спринт Яндекс.Практикум).

## Модули

- **mymarketapp** — существующее веб-приложение (reactive, Spring WebFlux + R2DBC, Thymeleaf).
- **paymentservice** — RESTful-сервис платежей (reactive, Spring WebFlux + R2DBC).

## Запуск в IDE

Откройте корень проекта в IntelliJ IDEA. Точки входа:
- `mymarketapp/src/main/java/org/yap/mymarketapp/MyMarketAppApplication`
- `paymentservice/src/main/java/org/yap/paymentservice/PaymentServiceApplication`

## Сборка

Сборка обоих модулей из корня:
`mvn clean package`

## Запуск в Docker

Каждый модуль собирается в отдельный образ из своей директории:

```
mymarketapp:
  cd mymarketapp
  mvn clean package
  docker build -t my-market-app .
  docker run -p 8080:8080 my-market-app

paymentservice:
  cd paymentservice
  mvn clean package
  docker build -t payment-service .
  docker run -p 8081:8081 payment-service
```

После запуска:
- веб-приложение доступно по адресу http://localhost:8080/items
- платежный сервис доступен по адресу http://localhost:8081/api/payments
