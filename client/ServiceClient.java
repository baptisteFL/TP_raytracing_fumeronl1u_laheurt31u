import raytracer.Image;
import java.util.*;
import java.rmi.*;
import java.util.Arrays;
public interface ServiceClient extends Remote {
    void rendreImage(HashMap<Image, ArrayList<Integer>> map) throws RemoteException;
}