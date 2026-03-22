# SUDRF Scraper
### Интерактивный поиск дел и текстов судебных решений судов общей юрисдикции первой, апелляционной и кассационной инстанций

Проект реализован **Андреем Сударкиным** совместно с [**Если быть точным**](https://tochno.st/).

🤖 Сайты некоторых судов блокируют запросы с зарубежных IP-адресов. Рекомендуем использовать российские IP-адреса.

## Что умеет SUDRF Scraper

Среди российских исследователей и журналистов официальный агрегатор судебных данных bsr.sudrf.ru (также известный как ГАС «Правосудие») ассоциируется с болью и разочарованием 🤕. А с недавнего времени он вообще перестал работать.

**SUDRF Scraper** помогает решить эту проблему — он ищет данные о судебных делах и тексты судебных решений прямо на сайтах **судов общей юрисдикции (включая гарнизонные военные суды)**. Сейчас парсер умеет работать с **пятью типами судебного производства**:

- уголовное
- административное
- об административных правонарушениях
- гражданское
- производства по материалам

И с **тремя инстанциями**:

- первая
- апелляционная
- кассационная

Некоторые суды используют капчу, поэтому иногда вам придется вводить ее вручную. Мы постарались минимизировать требуемое количество вводов капчи, а также добавили звуковое уведомление 🔔 на появление капчи.

> Инструмент осуществляет поиск только по открытым данным, размещенным на сайтах судов. Деятельность по предоставлению доступа к судебным решениям осуществляется, исходя из основных принципов обеспечения доступа к информации о деятельности судов, установленных статьей 4 Федерального закона от 22.12.2008 N 262-ФЗ "Об обеспечении доступа к информации о деятельности судов в Российской Федерации".

## Как установить и запустить

📷 Посмотрите [видео-инструкцию](https://www.youtube.com/watch?v=TDDejU0ap14&feature=youtu.be) 

1. Перейдите в https://github.com/tochno-st/sudrfscraper/releases/. Найдите последнюю версию релиза и скачайте архив, который соответствует вашей операционной системе.
2. Разархивируйте.
3. Если у вас Windows, то приложение можно запустить, два раза кликнув на файл **run.bat**. Если у вас Linux или MacOS, то введите в терминале полный путь до файла run.sh (`/path/to/run.sh`). Если появилась ошибка Permission dinied, убедитесь, что у исполняемых файлов `run.sh` и `<ваша операционная система>/jre/bin/java` установлено свойство Allow this file to run as a program. Если у вас MacOS и появилась ошибка  «Приложение java нельзя открыть, так как не удалось проверить разработчика», в настройках в разделе «Защита и безопасность» — «Основные» выберите в блоке «Разрешить использование программ, загруженных из:» опцию «App Store и от установленных разработчиков».

## На что обратить внимание

**SUDRF Scraper** использует драйвер **FireFox**, поэтому у вас на устройстве должен быть установлен браузер Firefox. Если вы видите сообщения об ошибках, связанных с WebDriver, попробуйте:

- Скачать драйвер для вашей операционной системы со страницы https://github.com/mozilla/geckodriver/releases
- Указать путь к geckodriver через переменную окружения `GECKODRIVER_PATH` или свойство `selenium.geckodriver.path`
- При необходимости указать путь к браузеру через `FIREFOX_BINARY` или `selenium.firefox.binary`
- При необходимости отключить headless-режим через `SELENIUM_HEADLESS=false` или `selenium.headless=false`
- Если явные пути не заданы, приложение попробует использовать драйвер из папки `./src/main/resources/<имя вашей операционной системы>/`.

Не рекомендуется осуществлять поиск дел только по тексту из судебного решения. Многие суды не поддерживают такой формат запроса, поэтому поиск будет осуществляться среди всех опубликованных к данному моменту решений. Задавайте дополнительные параметры поиска в других полях.

Поиск судебных дел можно осуществлять только по одному из пяти типов судебного производства. Если вы не задали тип судебного производства, то по умолчанию поиск осуществляется по уголовным делам.

Также не рекомендуется запускать неограниченный поиск по административным или гражданским делам. Выберите конкретную статью или задайте другие критерии поиска. 

## Как сформировать запрос

Можно выбрать один из двух форматов выгрузки собранных данных:
- **Таблица MySQL**
- **Текстовый файл JSON/CSV (создается сразу два файла)**, в котором в каждую строку записывается информация по отдельному делу

Для поиска судебных дел можно использовать следующие фильтры:

- **Уровень суда**: Все, Районный, Региональный, Гарнизонный или Мосгорсуд
- **Регион** (обратите внимание, что военные гарнизонные суды не имеют строгой региональной привязки, поэтому, если вас интересуют дела именно этих судов, лучше не ограничивать выгрузку конкретными регионами)
- **Формат поиска**: Мягкий (собирает подстатьи для заданной статьи, например, для статьи 20.3 КОАП РФ собирает и дела по статье 20.3.3) или Строгий (собирает только дела по заданной статье)
- **Период, в который дело рассмотрено (с, по)**: даты начала и конца периода, в который было рассмотрено дело. Если задать эти параметры, то еще незавершенные дела не попадут в выгрузку. Чтобы этого избежать, оставьте эти поля пустыми.
- **Период, в который дело поступило в суд (с, по)**: даты начала и конца периода, в который дело поступило в суд.
- **Поиск по тексту документа**: поиск по тексту из судебного решения.
- **Тип судебного производства**: Уголовное, Административное, Об административных правонарушениях, Гражданское и Производства по материалам
- **Инстанция**: Первая, Апелляционная, Кассационная. Поиск по делам в кассационной инстанции осуществляется по всем кассационным судам сразу и не зависит от того, выбрали вы конкретные регионы или нет.

В зависимости от выбранного типа судебного производства вам будет предложено указать конкретную статью, по которой вы хотите осуществить поиск. Для статей по уголовному производству важно не путать подстатью и часть статьи. Например, в уголовном кодексе есть Статья 286.1 «Неисполнение сотрудником органов внутренних дел приказа». В данном случае «Статья» — 286, «Подстатья» — 1. А если необходимо осуществить поиск по части 4 статьи 286 «Превышение должностных полномочий», то 4 нужно указать в поле «Часть», а не в поле «Подстатья».   

Также вы можете управлять следующими опциями:

- **Директория проекта**, в которую необходимо сохранить все результаты. Именно здесь их нужно искать после завершения сбора данных.
- Опция **«Продолжить предыдущий сбор данных»**: если вы хотите еще раз запустить ранее завершенный сбор данных, то поставьте галочку в соответствующее поле. Важно, чтобы имя проекта и директория проекта совпадали с теми, которые вы использовали ранее (если это не так, то будет использована информация о последней завершенной сессии). Такая опция, например, полезна, когда сайт какого-то суда не отвечал во время первой итерации сбора данных — можно вернуться к этому суду через несколько дней. Собранные результаты будут добавлены в ранее созданную выгрузку.

## Где найти результаты

После завершения сбора данных они появятся в указанной вами директории проекта. Файл называется по имени проекта и имеет расширение, которое вы выбрали. Кроме того, вместе с результатами парсинга будут доступны конфигурационные файлы и файлы с записями ошибок. Используйте информацию из них, если решите написать нам о проблемах в работе приложения.

Обратите внимание, что при парсинге в формат JSON/CSV все собранные данные сохраняются в файл JSON, а в CSV — основная информация о делах без этапов рассмотрения дела и участвующих сторон. Чтобы открыть CSV файл в Excel, используйте формат Данные -> Получить данные -> Из файла. Если вы попробуете открыть файл CSV, кликнув на него мышкой, то он может открыться в неправильной кодировке.

## Частые вопросы

- **На Linux или MacOS после ввода в терминале пути до исполняемого файла ничего не происходит**.
- Убедитесь, что у вас установлен браузер Firefox. Если установлен, то попробуйте в адресной строке указать `localhost:8080` — должен открыться интерфейс приложения.
- **На MacOS появляется ошибка ошибка  «Приложение java нельзя открыть, так как не удалось проверить разработчика»**.
- В настройках в разделе «Защита и безопасность» — «Основные» выберите в блоке «Разрешить использование программ, загруженных из:» опцию «App Store и от установленных разработчиков».
-----------------------------------------------------------------------------------------------------------------------------------------------
## SUDRF Scraper (User-friendly interactive interface to parse Russian court decisions)

Project is made by **Andrew Sudarkin** in collaboration with [**To Be Precise**](https://tochno.st/).

> Sometimes requests from foreign IPs are blocked. So using a Russian IP (inside Russia or via proxy server / VPN) is needed.

### DESCRIPTION

Among Russian independent journalists, the official aggregator bsr.sudrf.ru, known as "Pravosudie", is associated with only one feeling — pain 🤕.
It is script-based, it is slow, it is down half of the time.

So the solution was as obvious as it was necessary. Just redo the aggregation from the courts' websites instead of using a script built on our taxes.

The websites of the courts use different interfaces. Some are using scripts, so selenium is needed anyway. More important for a user: some courts have CAPTCHA, though its implementation cannot possibly be dumber (basically, you need to enter one captcha for a whole region).

Nevertheless, SUDRFScraper wins in comparison with "Pravosudie".
It is faster due to its mainly request-based nature and multi-threading.
It is more stable: there are always courts which are down, but it is bearable considering that the entire "Pravosudie" tends to be down.

Also, SUDRFScraper **provides a UI, so there's no need to do any coding**.

### SETUP DETAILS


📷 Watch the [instructional video](https://www.youtube.com/watch?v=TDDejU0ap14&feature=youtu.be) 

1. Go to https://github.com/tochno-st/sudrfscraper/releases/. Find the latest release and download the version corresponding to your Operating System.
2. Unzip the archive.
3. If you are a Windows user, you can start the application with a double-click on the run.bat script. 
Otherwise, you should open the terminal and enter /path/to/run.sh (full path to file run.sh)

### IMPORTANT THINGS TO KNOW BEFORE USING

Scraper uses Firefox WebDriver, so you should have the Firefox browser. If you see errors about WebDriver in logs, follow these instruction that may help:

- Visit [https://github.com/mozilla/geckodriver/releases](https://github.com/mozilla/geckodriver/releases) and download the driver for your OS.
- Point the application to geckodriver with `GECKODRIVER_PATH` or `selenium.geckodriver.path`.
- If Firefox is installed outside the default lookup paths, set `FIREFOX_BINARY` or `selenium.firefox.binary`.
- If you need a visible browser window, disable headless mode with `SELENIUM_HEADLESS=false` or `selenium.headless=false`.
- If explicit paths are not set, the application falls back to the bundled driver path in `./src/main/resources/"name-of-your-OS"/`.

Scraped cases are from courts of general jurisdiction.

You are highly not recommended to use the "Text to Search Within Acts" field. 
Most of the courts don't support this field, so search will be executed among ALL cases with published decision up to this very day.
Only use this field in combination with others.

You can only search through one type of articles. If it is not set, the search will run for criminal articles.

You are highly not recommended to use the bold administrative article search.
Administrative and Civil cases are aggregated simultaneously in many courts, so you need to specify the CAS article or filter your results.

### EXECUTION INFO

As for execution you have these search options:

1. *Start and end dates*. "Result" means the case is finished, though there might not be a published decision. These are the only available date params for now. I do, however, understand how important the entry date can be for some cases. Don't use these fields if you want unfinished cases (for some reason).
2. *Text-in-Decision field*. Obvious. Just don't forget the info covered in the previous chapter.
3. The main option: *article*. SUDRFScraper supports Criminal Articles, Administrative Offense Articles, Administrative Proceedings, Casefile Proceedings and Civil Proceedings. Just don't forget the info covered in the previous chapter (x2).

There are only two dump types supported: MySQL-table and line-by-line JSON document.

You are also able to configure these options:

1. *Directory path*. The path to the desirable result directory.
2. *Continue*. If you want to continue a previous session (scrap previously inactive courts or continue an aborted scraping), you can check this option. You should enter the project name and directory of the previous scraping. If there is no project set, scraping will continue from the most recent session results.
3. *Cases filter*. Use SOFT for collecting subArticles for entered Articles, e.g., 20.3 collects 20.3.3. Use STRICT for collecting only the specified article, e.g., 20.3 doesn't collect 20.3.3.
4. *Court Level* and *Court Region filter*.

By the end of execution, you are given a summary. It is a list of issues that occurred, so you may know how many cases you could miss during the scraping. You can also check the logs to find information about the issues. If there are many issues that do not include server problems (Inactive court, Connection error), like possible different interfaces, I will be glad if you contact me for further improvement of the system.

### UPDATES

ver 0.3.0 Add cassation and serch by entry date.

ver.0.1.4 CAS-UPDATE. Feat: implementation of search based on administrative articles. Small bug fixes and refactoring.

ver 0.1.5 configuration-update. You can change results dump directory modifying application.properties. Small bug fixes and refactoring.

ver.0.1.6 configuration-update. Modified configuration with "use.court.history" configuration param. Now user won't be trashed with thousands of files. Added some tests.
Massive refactoring.

ver.0.1.7 configuration-update. Modified configuration with default sql connection parameters. Massive refactoring.

ver.0.1.8 captcha-update. Feat: added regions to configuration. Fixed captcha-related bugs. Updated strategies and court configuration for solving new issues. 

ver.0.1.9 content-update. Feat: fixed broken courts, removed courts that ceased to exist, replaced renamed courts, added garrison courts and mosgorsud. Configs: added sorting by court level.

ver.0.1.10 flexibility-update. Feat: Storing configs in results and reusing them in future continuing scraping to increase flexibility. Refactoring and bug fixes.

ver.0.2.0 UI-update. Feat: added new HTML-based user interface. Migrated to Spring Boot.

ver.0.2.1 fix-update. UI bug fixes. Article-related bug fixes.

ver.0.2.2 material-proceeding-update. Added Casefile ("Material") Proceeding.

ver.0.2.3 cas-fix-update. Fixed bugs with mosgorsud cas code. Added lawbook for CAS.

ver.0.2.4 civil-proceeding-update. Added Civil Proceeding.

ver.0.2.4-1 performance enhancement. Enhanced performance for VPN environment.

ver.0.2.5 appellation-update. Added appellation instance.

v0.2.5-1 MGS-fix. Removed scraping duplicate cases for MGS.

v0.2.6 Add Appellation instance, CUI parsing, sides and process parsing.

CONTACT ME:

Email: sudarkinandrew@gmail.com
