# How2Config
1. Erstelle eine File textX.json (X = Task Nummer)
2. Fülle die entsprechenden Daten auf:
   1. Task1.json
````json
{
  "pathToHandInExcel": "<Pfad zur Excel File>",
  // The path where you store your anfrageX.json files
  "pathToHandIn":"./docker/task1/ES_Angabe",
  // Set true to reset the index
  "resetIndex": false,
  // Configure your elasic search information, usually the data below
  "indexName": "shakespeare",
  "host": "localhost",
  "port": 9200,
}
````
   2. Task2.json
````json
{
  "pathToHandInExcel": "<Pfad zur Excel File>"
}
````
   3. Task3.json
````json
{
  "pathToHandInExcel": "<Pfad zur Excel File>"
}
````