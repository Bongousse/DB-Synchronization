# DB-Synchronization
This project includes makeDDL from eXERD and DB Synchronization between DDL and DB

In order to use this project, there are two steps.
1. Make a DDL for eXERD
  1) You can see {home_direcoty}/exerd.utilizing.com/script/DdlMaker.xs and open it.
  2) Execute this script file (You have to execute xscripte file. That is not run button in eclipse, but run XScript button)
  3) You should choose a eXERD file for making a DDL. (eXERD project must be included in your project explorer) 
  4) Finally, you can see ddl file. (Default path : {home_directory}/result/ddl.txt)
  
2. Compare between DB and DDL
  1) First of all, you execute UiMain class file.
  2) You set a ddl path and db info.
  3) Click the Compare! button.
  4) If the compare process is done, you can see a table list below. The table name in the table list indicates the number of columns to modify.
  5) The Generate Query! button is used to generate query to modify columns.
