Employee
Employee Survey Data Analysis

Execute main in /src/main/java/org/employee/application/Application.java, possible arguments to control the data sort order and employee sort order are: [ORIGINAL/ID/LASTNAME/FIRSTNAME/BIRTHDATE] [ASC/DESC]

Run Instructions

gradle build // Builds the project

gradle run // Run the project with default sort order

gradle run -PappArgs="['ID']"

gradle run -PappArgs="['LASTNAME','DESC']"

Tests: gradle test

Time complexity is linear or O(n) without sorting, i.e. ORIGINAL, each line of the CSV file is processed once, map insertions and gets are O(1) for unsorted hash maps or O(log n) for sorted maps. space complexity is O(n).
Use ID command-line argument to obtain all data sorted by ID, the argument ORIGINAL (default) will use CSV data order. gradle run -PappArgs="['ID']"
Use ID, LASTNAME, FIRSTNAME or BIRTHDATE arguments to set sort order of employees, optionally with ASC or DESC to set sort direction, the default is ascending. Example birthdate, gradle run -PappArgs="['ID']"
The program will output some calculated average ages for the company and one sample division, team and manager at the end. I didn't add command-line arguments for this.
