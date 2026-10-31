# Как выпустить релиз

Инструкция для публикации новой версии FastClickPearl на GitHub.

## 1. Подготовьте версию

Обновите `mod_version` в трёх файлах:

- `1.21.11/gradle.properties`
- `26.1/gradle.properties`
- `26.2/gradle.properties`

Версия должна совпадать во всех проектах:

```properties
mod_version=1.0.0
```

Добавьте запись в [CHANGELOG.md](CHANGELOG.md) и при необходимости обновите README.

## 2. Проверьте сборку

Соберите все три проекта локально:

```powershell
$env:JAVA_HOME="C:\Program Files\Java\jdk-25"

foreach ($version in @("1.21.11", "26.1", "26.2")) {
    Push-Location $version
    .\gradlew.bat build --console=plain
    Pop-Location
}
```

Проверьте, что появились файлы:

```
1.21.11/build/libs/fastclickpearl-1.21.11-1.0.0.jar
26.1/build/libs/fastclickpearl-26.1-1.0.0.jar
26.2/build/libs/fastclickpearl-26.2-1.0.0.jar
```

Запустите игру с каждым jar и проверьте:

- бросок жемчуга из хотбара;
- бросок жемчуга из основного инвентаря;
- бросок жемчуга из второй руки;
- настройки и переключатель включения;
- кнопку мода на экране заголовка и в меню паузы;
- отсутствие ошибок в `latest.log`.

## 3. Закоммитьте изменения

```bash
git add .
git commit -m "Release 1.0.0"
git push
```

## 4. Поставьте тег

Тег должен начинаться с `v`:

```bash
git tag -a v1.0.0 -m "FastClickPearl 1.0.0"
git push origin v1.0.0
```

После отправки тега сработает workflow `.github/workflows/build.yml`:

1. Соберутся все три версии.
2. Jar-файлы загрузятся как артефакты.
3. Создастся GitHub Release с прикреплёнными jar и автоматическими заметками.

## 5. Проверьте релиз

Откройте раздел **Releases** и убедитесь, что:

- в релизе три jar-файла;
- версия в названии совпадает с тегом;
- заметки релиза содержат нужные изменения.

## Ручной релиз

Если Actions недоступны, соберите проекты локально и прикрепите файлы вручную:

1. Откройте **Releases → Draft a new release**.
2. Укажите тег `v1.0.0`.
3. Прикрепите три jar-файла из `build/libs`.
4. Опубликуйте релиз.

## Настройки репозитория

Для автоматической публикации проверьте:

- **Settings → Actions → General → Workflow permissions** — разрешение **Read and write permissions**;
- workflow использует `permissions: contents: write` для job `release`;
- теги формата `v*` не заблокированы правилами репозитория.

## Чек-лист

- [ ] Версия обновлена во всех трёх `gradle.properties`.
- [ ] CHANGELOG дополнен.
- [ ] Все три проекта собираются без ошибок.
- [ ] Мод проверен в игре на каждой версии.
- [ ] В `latest.log` нет ошибок мода.
- [ ] Изменения закоммичены и отправлены.
- [ ] Тег `vX.Y.Z` создан и отправлен.
- [ ] GitHub Release содержит три jar-файла.
