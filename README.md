# MMTR-Test-Task
Тестовое задание для прохождения практики в автоматизации тестирования

1. Запуск эмулятора
```shell
java -jar testService.jar 
```

2. Запуск тестов
```shell
mvn clean -Dtest=YotaTests test
```

3. Запуск отчета о тестах
```shell
mvn plugins allure server
```

