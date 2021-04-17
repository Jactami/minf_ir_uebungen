@ECHO OFF

PUSHD "%~dp0"

SETLOCAL

SET silent=""
IF "%~1" == "--silent" SET silent=%~1
IF "%~2" == "--silent" SET silent=%~2
IF "%~3" == "--silent" SET silent=%~3
SET debug=""
IF "%~1" == "--debug" SET debug=%~1
IF "%~2" == "--debug" SET debug=%~2
IF "%~3" == "--debug" SET debug=%~3
SET clean=""
IF "%~1" == "--clean" SET clean=%~1
IF "%~2" == "--clean" SET clean=%~2
IF "%~3" == "--clean" SET clean=%~3

SET errorstate=0

IF NOT EXIST CreateIndex.bat (
    ECHO The file CreateIndex.bat was not found in the directory:
    ECHO %~dp0
    ECHO please extract the whole ZIP-File in the same folder
    SET errorstate=1
) ELSE (
    ECHO CreateIndex.bat found
)

IF NOT EXIST DeleteIndex.bat (
    IF "%errorstate%"=="1" ECHO.
    ECHO The file DeleteIndex.bat was not found in the directory:
    ECHO %~dp0
    ECHO please extract the whole ZIP-File in the same folder
    SET errorstate=1
) ELSE (
    ECHO DeleteIndex.bat found
)

FOR /F "tokens=* USEBACKQ" %%F IN (`where curl`) DO (
SET var=%%F
)
IF "%var%" == "" (
    IF "%errorstate%"=="1" ECHO.
    ECHO cURL not found, please install cURL and add the bin-Folder to the Path-variables in your enviroment variables
    ECHO For more informations see Task 7 - Hinweis 3
    SET errorstate=1
) ELSE (
    ECHO cURL found
)

IF "%errorstate%"=="0" GOTO DEFINED
GOTO ENDERROR

:DEFINED

REM Check if the shakespeare index allready exists
REM https://www.elastic.co/guide/en/elasticsearch/reference/current/indices-exists.html
ECHO.
ECHO Check if the Shakespeare-Index exists
FOR /F "tokens=* USEBACKQ" %%F IN (`curl localhost:9200/shakespeare --head --silent`) DO (
    SET var=%%F
    GOTO RES
)
:RES
IF "%var%" == "HTTP/1.1 200 OK" (
    ECHO The index already exists.

    IF "%clean%" == "--clean" (
        ECHO Execute CreateIndex.bat to create a clean index
        GOTO CLEAN
    )

    GOTO EXISTS
)

ECHO The index does not exist, execute CreateIndex.bat
:CLEAN
CALL CreateIndex.bat --silent %debug%

:EXISTS
REM Anfragen
ECHO.
ECHO Anfrage 1:
curl localhost:9200/shakespeare/_search?size=50^&pretty=true -H "Content-Type: application/json" -d @anfrage1.json > "%~dp0ergebnis1.json"
ECHO.
ECHO Anfrage 2:
curl localhost:9200/shakespeare/_search?size=50^&pretty=true -H "Content-Type: application/json" -d @anfrage2.json > "%~dp0ergebnis2.json"
ECHO.
ECHO Anfrage 3:
curl localhost:9200/shakespeare/_search?size=50^&pretty=true -H "Content-Type: application/json" -d @anfrage3.json > "%~dp0ergebnis3.json"
ECHO.
ECHO Anfrage 4:
curl localhost:9200/shakespeare/_search?size=50^&pretty=true -H "Content-Type: application/json" -d @anfrage4.json > "%~dp0ergebnis4.json"
ECHO.
ECHO Anfrage 5:
curl localhost:9200/shakespeare/_search?size=50^&pretty=true -H "Content-Type: application/json" -d @anfrage5.json > "%~dp0ergebnis5.json"
ECHO.

:END
ECHO Done
:ENDERROR
IF NOT "%silent%" == "--silent" PAUSE
ENDLOCAL
POPD