import raytracer.Image;
import raytracer.Scene;
import java.util.*;
import java.rmi.*;

public interface ServiceCentral extends Remote
{
    public void enregistrerServCalcul(ServiceCalcul sc) throws RemoteException;

    public void distribuerCalcul(ServiceClient sCli) throws RemoteException;

    public void rendreScene(ServiceClient sCli, int l, int h, Scene s) throws RemoteException;

    public int changerServ() throws RemoteException;

}
