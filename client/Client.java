import raytracer.Image;
import raytracer.Disp;
import raytracer.Scene;

import java.util.*;
import java.rmi.registry.*;
import java.rmi.*;
import java.util.Arrays;
import java.rmi.server.UnicastRemoteObject;



class Client implements ServiceClient {

    Disp disp = null;

    public static void main(String[] args) {
        String aide = "Aucun argument donné, valeurs par défaut";
        // Le fichier de description de la scène si pas fournie
        String fichier_description = "simple.txt";

        // largeur et hauteur par défaut de l'image à reconstruire
        int largeur = 800, hauteur = 800;

        if (args.length > 0) {
            fichier_description = args[0];
            if (args.length > 1) {
                largeur = Integer.parseInt(args[1]);
                if (args.length > 2)
                    hauteur = Integer.parseInt(args[2]);
            }
        } else {
            System.out.println(aide);
        }

        Client c = new Client();


        // création d'une fenêtre
        c.disp = new Disp("Raytracer", largeur, hauteur);

        // Initialisation d'une scène depuis le modèle
        Scene scene = new Scene(fichier_description, largeur, hauteur);

        // Calcul de l'image de la scène les paramètres :
        // - x0 et y0 : correspondant au coin haut à gauche
        // - l et h : hauteur et largeur de l'image calculée
        // Ici on calcule toute l'image (0,0) -> (largeur, hauteur)

        int x0 = 0, y0 = 0;

        ServiceCentral sc = null;
        try {
            Registry reg = LocateRegistry.getRegistry("localhost");
            sc = (ServiceCentral) reg.lookup("DistRaytracer");
            ServiceClient sCli = (ServiceClient) UnicastRemoteObject.exportObject(c, 0);
            sc.rendreScene(sCli, largeur, hauteur, scene);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        }
    }

    public void rendreImage(HashMap<Image, ArrayList<Integer>> images) throws RemoteException {
        for (Image i : images.keySet())
        {
            System.out.println(images.get(i).get(1));
            disp.setImage(i, images.get(i).get(0), images.get(i).get(1));
        }
    }
}