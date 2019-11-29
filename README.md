# CPSC 441 Final Project Instructions
# Group 2 - Word Cloud

To run this project, please have the RAC set up. The setup involves having a master, slave, client and administrator client.
* The master is the 'server' of the application that gathers survey results and coordinates processes between slaves.
* The slaves are individual systems that perform the required computations as instructed by the master.
* The client fills in survey questions for analysis.
* The administrator client has extended privileges to trigger correlation calculations, view and list historical correlation data.

To run the project on the Cybera RAC:
1. Add the folder `HistoricalCorrelations` into the project directory.
2. Add the file `SurveyEntries.txt` into the project directory. Optionally, you can use a pre-populated SurveyEntries.txt file.
3. Start by running the 'slave' machine in the JAR `CPSC441FinalProject-Slave.jar`. The slave application must be run before the master or client/admin runs.
  * java -jar CPSC441-FinalProjectSlave.jar
4. Run the 'master' machine in the JAR `CPSC441FinalProject-Master.jar`. The master application must be run before the client or admin runs.
* java -jar CPSC441-FinalProjectMaster.jar
5. Both the 'client' and 'admin' applications can be used in no particular order at this point. These instructions will demonstrate running 'client' first. To run the client, run the JAR `CPSC441-FinalProjectUser.jar`.
  * java -jar CPSC441-FinalProjectUser.jar
6. Once the client starts, please enter your name.
7. A survey will appear. You may select more than one answer for each question (and are encouraged to do so). Please enter the the survey answers, delimited by a space. For example, enter `Python Java` to select the choices Python and Java respectively. 
8. To run the admin, run the JAR `CPSC441-FinalProjectAdmin.jar`
  * java -jar CPSC441-FinalProjectAdmin\.jar
9. The admin has several options. Type `help` to view all options. Type `calculate` to begin the correlation algorithm. Please be advised that the admin can specify either they want correlation scores for 2-tuples or 3-tuples, by entering the number of tuples they want by entering a space after `calculate` followed by the number. For instance, if you want calculations for 3-tuples, you would enter `calculate 3`.

NOTE: You can specify the IP address of the server can connect to by entering it into the command line arguments. If no argument is entered, `localhost` is chosen by default.

For extra documenation you may view our github page at: https://github.com/ryan-holt/CPSC441FinalProject
