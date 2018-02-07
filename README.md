# LibrarySystem

*Building*
The project uses Maven build system.
In order to create an executable jar package just call 'mvn package' in the project root directory.
Two jar packages will appear in the 'target' folder in case of successfull build, with and without dependencies in it.
It is recommended to run the one with dependencies to avoid problems with JDBS packages.

*Running*
Two arguments are required to run the executable - username and password of a user that has access to the database.
After running the executable you should be able to execute commands in command line.
Commands list:
	show [users|books|checkouts] - output the information about given category in database
	checkout USER_CARD_ID ITEM_TITLE - to check out an item from the library
	search - to be implemented yet
	exit - to finish the session and exit
Attention: do not exit the programs by any means but 'exit' command, lest your data will be lost.
