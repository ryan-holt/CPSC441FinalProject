# CPSC 441 Final Project Instructions
# Group 2 - Word Cloud

To run this project, please have the RAC set up. The setup involves having a master, slave, client and administrator client.
* The master is the 'server' of the application that gathers survey results and coordinates processes between slaves.
* The slaves are individual systems that perform the required computations as instructed by the master.
* The client fills in survey questions for analysis.
* The administrator client has extended privileges to trigger correlation calculations, view and list historical correlation data.

To run the project on the Cybera RAC:
1. Add the folder `HistoricalCorrelations` into the project directory.
2. Start by running the 'slave' machine in the JAR `CPSC441FinalProject-Slave.jar`. The slave application must be run before the master or client/admin runs.
  * java -jar CPSC441-FinalProjectSlave.jar
3. Run the 'master' machine in the JAR `CPSC441FinalProject-Master.jar`. The master application must be run before the client or admin runs.
* java -jar CPSC441-FinalProjectMaster.jar
4. Both the 'client' and 'admin' applications can be used in no particular order at this point. These instructions will demonstrate running 'client' first. To run the client, run the JAR `CPSC441-FinalProjectUser.jar`.
  * java -jar CPSC441-FinalProjectUser.jar
5. Once the client starts, please enter your name.
6. A survey will appear. You may select more than one answer for each question (and are encouraged to do so). Please enter the the survey answers, delimited by a space. For example, enter `Python Java` to select the choices Python and Java respectively. 
7. To run the admin, run the JAR `CPSC441-FinalProjectAdmin.jar`
  * java -jar CPSC441-FinalProjectAdmin\.jar
8. (optional) Add the file `SurveyEntries.txt` into the project directory to use a pre-populated set of survey answers.
9. The admin has several options. Type `help` to view all options. Type `calculate` to begin the correlation algorithm.

NOTE: This project currently only supports all applications running on `localhost`, though it can be expanded to operate on different machines.

For extra documenation you may view our github page at: https://github.com/ryan-holt/CPSC441FinalProject
