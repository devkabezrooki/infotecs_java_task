# infotecs_java_task
Тестовое задание для стажера на позицию «Программист на языке Java» в Infotecs Academy
# Текст задания:
Необходимо разработать простой сервис который будет хранить данные в оперативной памяти по текстовому ключу. 
https://ru.wikipedia.org/wiki/База_данных_«ключ-значение»

## Необходимая функциональность:

1. Операция чтения (get)

	Принимает следующие параметры:
		a) ключ для хранилища.
		
	Возвращает данные, хранящиеся по переданному ключу или метку отсутствия данных.
	
2. Операция записи (set)

	Принимает следующие параметры:
		a) ключ для хранилища;
		b) данные для хранилища, которые будут ассоциированы с переданным ключом;
		c) опциональный параметр ttl (продолжительность жизни записи), 
			по истечении данного временного промежутка данная пара ключ-значение должна автоматически удаляться из хранилища.
			Если параметр не передан - использовать ttl по-умолчанию.

	Если по переданному ключу уже хранятся данные - их нужно заменять, а также обновлять ttl у данной записи.
	Возвращает метку успешности или неуспешности операции.

3. Операция удаления (remove)

	Принимает следующие параметры:
		a) ключ для хранилища.

	Удаляет данные, хранящиеся по переданному ключу.
	Возвращает данные, хранившиеся по переданному ключу или метку отсутствия данных.

4. Операция сохранения текущего состояния (dump)

	Сохраняет текущее состояние хранилища и возвращает его в виде загружаемого файла.
	
5. Операция загрузки состояния хранилища (load)

	Загружает состояние хранилища из файла, созданного операцией dump (пункт 4).

# Описание реализации:
Для выполнения задания использовались SpringBoot 2.5.4, Java 11 и сборщик Maven.

Для сборки проекта через консоль можно вызвать из корневой папки проекта команду **mvn compile**, а для запуска **mvn spring-boot:run**. Для запуска через среду разработки необходимо использовать класс **KeyValueApplication**.

## Структура проекта:

Проект состоит из четырех основных пакетов:

• Model: в данном пакете содержится класс **KeyValueModel**, описывающий сущность "ключ-значение".

• Repository: данный пакет содержит интерфейс **KeyValueRepository**, описывающий репозиторий для хранения пар ключ-значение. Наследуется от интерфейса JpaRepository (Spring Data JPA 2.5.4 API) для выполнения CRUD операций и дополнительных методов не содержит.

• Service: данный пакет содержит интерфейс **KeyValueService**, описывающий сервис-хранилище, содержит методы, описанные в задании. Также в данном пакете находится реализация сервиса **KeyValueServiceImpl**.

• Controller: данный пакет содержит класс **KeyValueController**, реализующий контролер для обработки запросов к хранилищу.

Для класса **KeyValueServiceImpl** написан юнит-тест **KeyValueServiceTest** (key-value/src/test/java/com/infotecsjava/keyvalue/service). Запустить можно, вызвав **mvn test** из корневой папки проекта.

Подробное описание реализованных методов можно найти в коде классов в комментариях.

## Работа с сервисом:

Проект запускается по адресу 127.0.0.1 и порту 8090, после чего можно обращаться к описанным ниже методам.

### Операция чтения:
Первый метод доступен по адресу:
```
127.0.0.1:8090/get/{key}
```
GET-метод, принимает единственный параметр - ключ для хранилища.
Если введенный ключ обнаружен в хранилище, метод возвращает связанное с ним значение  и HTTP-статус 200 OK, иначе - сообщение об ошибке и статус 404 Not Found.

### Операция записи:
К операции записи можно обращаться двумя способами:
```
127.0.0.1:8090/set/{key}/{value}
```
или
```
127.0.0.1:8090/set/{key}/{value}/{ttl}
```
В первом случае нужно указать ключ и значение, а для параметра ttl будет использоваться значение по умолчанию. Во втором - также необходимо явно указать значение параметра ttl.
Оба метода обрабатывают POST-запросы.
Возвращает сообщение об ошибке и статус 409 Conflict, если введенные данные некорректны, или сообщение "Ok" и HTTP-статус 201 Created, если запись успешна.

### Операция удаления:
Третий метод доступен по адресу:
```
127.0.0.1:8090/remove/{key}
```
PUT-метод, принимает единственный параметр - ключ для хранилища.
Если введенный ключ обнаружен в хранилище, метод возвращает связанное с ним значение  и HTTP-статус 200 OK, иначе - сообщение об ошибке и статус 404 Not Found.

### Операция сохранения текущего состояния:
Четвертый метод доступен по адресу:
```
127.0.0.1:8090/dump
```
Обрабатывает GET-запросы.
Возвращает текущее состояние проекта в формате json и HTTP-статус 200 OK, или сообщение об ошибке и статус 404 Not Found, если данные не обнаружены, или сообщение об ошибке и статус 409 Conflict при проблемах с обработкой файла или перевода объектов в json.

### Операция загрузки состояния хранилища:
Пятый метод досутпен по адресу:
```
127.0.0.1:8090/load
```
Обрабатывает POST-запросы.
Возвращает сообщение "Ok" и HTTP-статус 201 Created Или сообщение об ошибке и статус 409 Conflict при проблемах с обработкой файла или json-объектов.
