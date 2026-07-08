
# my-market-app

Задание на 5 спринт для Яндекс.Практикум

  

# Запуск в IDE

Открыть в IntelliJIdea, проект сразу запускается, точка входа - MyMarketAppApplication.

  

# Запуск в Docker
Соберите проект с помощью maven (должен появиться файл .jar в каталоге target):
`mvn clean package`  

Соберите образ:
`docker build -t my-market-app .`  

Запустите образ на порте 8080:
`docker run -p 8080:8080 my-market-app`
  

В обоих случаях, приложение будет доступно по адресу http://localhost:8080/items