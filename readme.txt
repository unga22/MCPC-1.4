THE MANUAL BUILD TODO LIST

Make a bin folder where you build ur files from eclipse or build script.
Then turn output folder scrubbing off from eclipse.
Next extract the whole bukkit into the bin folder.

!! Then delete the com/google folder !! because otherways it will load
those libs and FML wont work on old ones.

If you plan on making a jar then edit the meta info and remove the sealed tags.


FOR DEBUG RUN
Just set the working dir manually to something else. like MCP-1.4/debug was for me.

RUNNING IT
java -classpath lib/*.jar;. -jar mcpc.jar

nuff said that