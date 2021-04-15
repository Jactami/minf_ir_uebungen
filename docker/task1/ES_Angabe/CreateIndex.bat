@ECHO OFF

PUSHD "%~dp0"

SETLOCAL

SET silent=""
IF "%~1" == "--silent" SET silent=%~1
IF "%~2" == "--silent" SET silent=%~2
SET debug=""
IF "%~1" == "--debug" SET debug=%~1
IF "%~2" == "--debug" SET debug=%~2

SET errorstate=0

IF NOT EXIST DeleteIndex.bat (
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

ECHO Delete the index if it exists
CALL DeleteIndex.bat --silent %debug%

IF NOT EXIST shakespeare_6.0.json (
    ECHO Missing shakespeare_6.0.json, try to load it manually
    curl https://download.elastic.co/demos/kibana/gettingstarted/shakespeare_6.0.json --output shakespeare_6.0.json
)

IF NOT EXIST mapping.json (
    ECHO Missing mapping.json, try to create it manually
    ECHO {"properties": {"speaker": {"type": "text","fields": {"keyword": {"type": "keyword"}}},"play_name": {"type": "keyword","fields": {"text": {"type": "text"}}},"line_id": {"type": "integer"},"speech_number": {"type": "integer"},"text_entry": {"type": "text"}}} > mapping.json
)

IF "%debug%" == "--debug" GOTO DEBUG

REM Create index
ECHO Creating the index
1>NUL curl -X PUT localhost:9200/shakespeare

REM Mapping laden
ECHO Loading the mapping for the index
1>NUL curl -X PUT localhost:9200/shakespeare/_mapping -H "Content-Type: application/json" --data @mapping.json

REM JSON laden
ECHO Loading the data for the index
1>NUL curl -XPOST localhost:9200/shakespeare/_bulk?pretty -H "Content-Type: application/x-ndjson" --data-binary @shakespeare_6.0.json

GOTO END

:DEBUG

REM Create index
ECHO Creating the index
curl -X PUT localhost:9200/shakespeare > %~dp0DEBUG_CREATE.log 2>&1

REM Mapping laden
ECHO Loading the mapping for the index
curl -X PUT localhost:9200/shakespeare/_mapping -H "Content-Type: application/json" --data @mapping.json >> "%~dp0DEBUG_CREATE.log" 2>&1

REM JSON laden
ECHO Loading the data for the index
curl -XPOST localhost:9200/shakespeare/_bulk?pretty -H "Content-Type: application/x-ndjson" --data-binary @shakespeare_6.0.json >> "%~dp0DEBUG_CREATE.log" 2>&1

:END
ECHO Done
:ENDERROR
IF NOT "%silent%" == "--silent" PAUSE
ENDLOCAL
POPD