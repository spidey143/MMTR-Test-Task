# MMTR-Test-Task
Тестовое задание для прохождения практики в автоматизации тестирования

1. Запуск эмулятора
```shell
java -jar testService-1.0-SNAPSHOT.jar 
```

2. Запуск тестов
```shell
mvn clean -Dtest=YotaTests test
```

3. Запуск отчета о тестах
```shell
allure serve .\target\allure-results
```

