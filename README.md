# MMTR-Test-Task
Тестовое задание для прохождения практики в автоматизации тестирования

1. Запуск эмулятора
```shell
java -jar testService-1.0-SNAPSHOT.jar 
```

2. Запуск тестов
```shell
mvn clean test
```

3. Запуск отчета о тестах
```shell
allure serve .\target\allure-results
```
Требования:
1. Установленная JDK 8
2. ОС Windows, Linux

Рекомендации:
1. Склонируйте данный репозиторий
2. Откройте проект в IntelIji IDEA
3. Выполните команды из README.md

Используемый стэк:
Java 8, TestNG, Allure report