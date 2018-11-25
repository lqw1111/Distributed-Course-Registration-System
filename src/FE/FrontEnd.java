package FE;

import FrontEndCorba.FrontEndHelper;
import ReplicaHost1.Log.LoggerFormatter;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrontEnd {
    private FrontEndImpl servent;
    public Logger logger;

    public FrontEnd(Logger logger, FrontEndImpl servent){
        this.servent = servent;
        this.logger = logger;
    }

    public void startCorbaServer(String department){
        try{
            // create and initialize the ORB
            ORB orb = ORB.init(new String[]{"-ORBInitialHost", "localhost", "-ORBInitialPort", "1050"}, null);

            // get reference to rootpoa & activate the POAManager
            POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootpoa.the_POAManager().activate();

            // create servant and register it with the ORB
            FrontEndImpl dcrsImpl = this.servent;
            dcrsImpl.setORB(orb);

            // get object reference from the servant
            org.omg.CORBA.Object ref = rootpoa.servant_to_reference(dcrsImpl);
            FrontEndCorba.FrontEnd href = FrontEndHelper.narrow(ref);

            // get the root naming context
            // NameService invokes the name service
            org.omg.CORBA.Object objRef =
                    orb.resolve_initial_references("NameService");
            // Use NamingContextExt which is part of the Interoperable
            // Naming Service (INS) specification.
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            // bind the Object Reference in Naming
            String name = department;
            NameComponent path[] = ncRef.to_name( name );
            ncRef.rebind(path, (org.omg.CORBA.Object) href);

            logger.info(department + "server ready and waiting ...");

            // wait for invocations from clients
            orb.run();
        }

        catch (Exception e) {
            System.err.println("ERROR: " + e);
            e.printStackTrace(System.out);
        }

        System.out.println("Server Exiting ...");
    }

    public static void configLogger(String department , Logger logger) throws IOException {
        logger.setLevel(Level.ALL);
        FileHandler compFileHandler = new FileHandler(department + ".log");
        compFileHandler.setFormatter(new LoggerFormatter());
        logger.addHandler(compFileHandler);
    }

    public static void main(String[] args) throws IOException {
        Logger compLogger = Logger.getLogger("Client.log");
        configLogger("Client",compLogger);

        FrontEndImpl frontEnd = new FrontEndImpl();
        FrontEnd server = new FrontEnd(compLogger,frontEnd);

        server.startCorbaServer( "frontEnd");
    }
}
