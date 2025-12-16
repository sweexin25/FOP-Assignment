GEMINI STEP BY STEP
📢 Team Project Setup Instructions
Hey guys, I have set up the base project on GitHub with the main folder structure and the Login system.
DO NOT code on the master branch. If we all code on master, we will overwrite each other's work and crash the project.
Follow these steps exactly to start your part:
1️⃣ First Time Setup (Do this ONCE)
Open IntelliJ.
Go to File > New > Project from Version Control.
Paste our Repo URL: [INSERT YOUR GITHUB LINK HERE]
Click Clone.
Wait for the project to open. You should see folders like model, service, data and the employees.csv file.
2️⃣ How to Start Coding (Daily Routine)
Step A: Get the latest code
Make sure you are on master branch (bottom right corner).
Click the Blue Down Arrow (Pull) at the top right to get my latest changes.
Step B: Create YOUR workspace
Click the branch name master at the bottom right.
Select "New Branch from 'master'".
Name it following this format: feature-[yourname]-[task].
Example: feature-yy-stock or feature-jk-storage.
Only code inside this branch.
3️⃣ Your Assigned Files (Don't touch others!)
To avoid conflicts, please stick to your files:
yy (Stock/Sales):
Create service/StockService.java
Create service/SalesService.java
Create model/Product.java
jk (Storage/Edit):
Work in data/DataStorage.java (I started it, you add more methods!)
Create service/EditService.java
xj (GUI/Extra):
Create a new package gui and work there.
yc (Reports/Email):
Create service/ReportService.java
4️⃣ "Saving" Your Work (Pushing)
When you finish a small task (e.g., "Created Product class"):
Press Ctrl + K (Commit).
Write a message: "Added Product class".
Click Commit and Push.
Go to the GitHub Website.
You will see a green button "Compare & Pull Request". Click it.
Click "Create Pull Request".
🛑 STOP THERE. Let me (sx) know in the group, and I  merge it into the main project.
