import raytracer.*;
import java.rmi.*;
public interface ServiceCalcul extends Remote
{
    public Image calculerRendu(int x0, int y0, int l, int h, Scene scene) throws RemoteException;

    public void ping() throws RemoteException;

    public boolean isActif() throws RemoteException;
}
