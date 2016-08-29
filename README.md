# Proto3-ScalaTcpServer
[Ru]

1. **Клиент Scalafx.** Тут нечего добавить, кроме того, что в коде все очень уныло. 

2. **Обмен между клиентом и сервером.** Используем независимый от языка, платформо-независимый, расширяемый механизм сериализации данных - Protocol Buffer 3. В нем, как известно, все поля optional по умолчанию и есть repeated. Для компиляции **Proto** используется либа **scalapb**. Сгенерируемые файлы попадают в **исходники-> папка generated**

3. **Серверная часть.** TcpServer построен на akka.io, akka.actors. На каждый коннект создается отдельный актор. При подключении на клиента создается scheduler, который каждые **10сек** отправляет сообщение **Ping** на клиент. В зависимости от сообщения, а в данном примере это либо запрос **Auth**, **TaskService** определяет кому перенаправить на выполнение. Он отправляет в **AuthService**, который затем идет в базу **DBStub** (заглушка) для проверки на наличие. Если в базе есть, то **AuthService** возвращает **SomePlayer** с **Id** из базы. Если нет, то возвращает **AuthErr**.

[Eng]

1. **Client Scalafx.** Nothing to say, the code is ugly.

2. **Exchanging data between client and server** We use **Google Protocol buffers** that is language-neutral, platform-neutral, extensible mechanism for serializing structured data. In v3 only optional and repeated fields as we know. For compilation  **Proto** we used **scalapb**. Generated files are moved to **main -> generated** folder

3. **Server side.** TcpServer is using akka.io, akka.actors. For each connection we create a new actor. After client connection we create for him a scheduler, that each **10s** sends **Ping** to client. Depends on message type, in this example we use only **Auth** type, **TaskService** determines who need to do work with this message. In our case **TaskService** sends **Auth** msg to **AuthService**, that will going to **DBStub** (stub) for checking player. If player exists, then **AuthService** returens **SomePlayer** with **Id** from database. If not then returns **AuthErr**.


## Несколько скринов для нагялдности / Several screenshots for clarity
1.До компиляции / Before compilation  
![1](http://storage7.static.itmages.ru/i/16/0829/h_1472457091_3569160_65389010b6.png)

### cmd: **sbt compile**

2.После компиляции появляются сгенерированные исходники / After compilation we can see generated sources
![2](http://storage7.static.itmages.ru/i/16/0829/h_1472457091_3008160_9bd39688fa.png)

### Test with wrong auth data (any,any) check DBStub

3 Получаем AuthErr сообщение / Received AuthErr message
![3](http://storage8.static.itmages.ru/i/16/0829/h_1472457091_8831056_8ca5f490f0.png)

### Test with correct auth data (Tester1,test) check DBStub

4 Получаем AuthResp сообщение с LoginId / Received AuthResp message with LoginId
![4](http://storage9.static.itmages.ru/i/16/0829/h_1472457092_3619926_fefe5d473d.png)
