import raytracer.*;
import java.util.*;
import java.rmi.registry.*;
import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
class LancerCalcul implements ServiceCalcul {

    public boolean actif = false;

    public static void main(String[] args)
    {
        try{
            Registry reg = LocateRegistry.getRegistry("localhost");
            ServiceCentral sc = (ServiceCentral) reg.lookup("DistRaytracer");
            ServiceCalcul calc = (ServiceCalcul) UnicastRemoteObject.exportObject(new LancerCalcul(), 0);
            sc.enregistrerServCalcul(calc);
        }
        catch(RemoteException e)
        {
            e.printStackTrace();
        }
        catch(NotBoundException e)
        {
            e.printStackTrace();
        }
    }


    public Image calculerRendu(int x0, int y0, int l, int h, Scene scene) throws RemoteException
    {
        actif = true;
        Image i =  scene.compute(x0, y0, l, h);
        actif = false;
        return i;
    }

    public boolean isActif() throws RemoteException{
        return actif;
    }

    public void ping() throws RemoteException
    {
        return;
    }
}