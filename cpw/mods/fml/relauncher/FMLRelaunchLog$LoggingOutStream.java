package cpw.mods.fml.relauncher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

class FMLRelaunchLog$LoggingOutStream extends ByteArrayOutputStream
{
    private Logger log;
    private StringBuilder currentMessage;

    public FMLRelaunchLog$LoggingOutStream(Logger var1)
    {
        this.log = var1;
        this.currentMessage = new StringBuilder();
    }

    public void flush() throws IOException
    {
        Class var2 = FMLRelaunchLog.class;

        synchronized (FMLRelaunchLog.class)
        {
            super.flush();
            String var1 = this.toString();
            super.reset();
            this.currentMessage.append(var1.replace(FMLLogFormatter.LINE_SEPARATOR, "\n"));

            if (this.currentMessage.lastIndexOf("\n") >= 0)
            {
                if (this.currentMessage.length() > 1)
                {
                    this.currentMessage.setLength(this.currentMessage.length() - 1);
                    this.log.log(Level.INFO, this.currentMessage.toString());
                }

                this.currentMessage.setLength(0);
            }
        }
    }
}
