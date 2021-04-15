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

FOR /F "tokens=* USEBACKQ" %%F IN (`where curl`) DO (
SET var=%%F
)
IF "%var%" == "" (
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
ECHO Check if the index exists
FOR /F "tokens=* USEBACKQ" %%F IN (`curl localhost:9200/shakespeare --head --silent`) DO (
    SET var=%%F
    goto RES
)
:RES

IF NOT "%var%" == "HTTP/1.1 200 OK" (
    ECHO The index does not exist
    GOTO END
)
IF "%debug%" == "--debug" GOTO DEBUG

REM Altes shakespeare loeschen
ECHO Deleting the index
1>NUL curl -X DELETE localhost:9200/shakespeare

GOTO END

:DEBUG
REM Altes shakespeare loeschen
ECHO Deleting the index
curl -X DELETE localhost:9200/shakespeare > "%~dp0DEBUG_DELETE.log" 2>&1


:END
ECHO Done
:ENDERROR
IF NOT "%silent%" == "--silent" PAUSE
ENDLOCAL
POPD