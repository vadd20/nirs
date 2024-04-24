# Запуск приложения
1. Для начала просто клонируйте репу, тут проблемы вряд ли будут  
Java Version = 20. Я скачал SDK 20 Oracle. Ну и нажимайте Run на EqualizerApp.

2. Вообще, может все нормально запуститься без всякой хуеты. Но если не запускается по причине  
`Error: JavaFX runtime components are missing, and are required to run this application` , 
то добро пожаловать во второй раунд запуска. Хз че тут не так, но нужно будет скачать библиотеку 
javafx sdk -- `https://gluonhq.com/products/javafx/` . Я скачал 21.0.2.
3. Далее нужно будет настроить параметры конфигурации. В новом UI слева от Run находится кнопка , скорее всего ,
`Current File`. Вот на нее нажать и нажать `Edit configuration`. Там по базе -- указать версию жавы 20,
Запускаемый файл и вот , что не очевидно , указать VM Options (доп. настройка там же, будет кнопка типа добавить Options).
Указать надо будет вот примерно это `--module-path /Users/kbuckovskij/Downloads/javafx-sdk-21.0.2/lib --add-modules javafx.controls,javafx.fxml`
то есть по факту путь до скачанной либы. Ну вот так должно запуститься.