import java.time.Instant;
import java.time.Duration;

import raytracer.*;

import java.util.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.lang.Math;
import java.lang.Thread;

public class LancerRaytracer implements ServiceCentral {
    int l = 0;
    int h = 0;
    int sommehaut;
    int hautReelle;
    Scene scene = null;
    ServiceClient sCli = null;

    public ArrayList<ServiceCalcul> noeuds = new ArrayList<ServiceCalcul>();
    public HashMap<Image, ArrayList<Integer>> rendus = new HashMap();

    public static String aide = "Raytracer : synthèse d'image par lancé de rayons (https://en.wikipedia.org/wiki/Ray_tracing_(graphics))\n\nUsage : java LancerRaytracer [fichier-scène] [largeur] [hauteur]\n\tfichier-scène : la description de la scène (par défaut simple.txt)\n\tlargeur : largeur de l'image calculée (par défaut 512)\n\thauteur : hauteur de l'image calculée (par défaut 512)\n";

    public static void main(String[] args) {

        try {
            Registry reg = LocateRegistry.createRegistry(1099);
            ServiceCentral sc = (ServiceCentral) UnicastRemoteObject.exportObject(new LancerRaytracer(), 0);
            reg.rebind("DistRaytracer", sc);
            Scanner s = new Scanner(System.in);
            while (!s.nextLine().equals("q")) {

            }
        } catch (UnknownHostException e) {
            System.out.println("Hôte inconnu");
        } catch (ConnectException e) {
            System.out.println("La connexion refuse de s'héberger");
        } catch (ConnectIOException e) {
            System.out.println("Problème d'entrée et de sortie lors de la connexion");
        } catch (NoSuchObjectException e) {
            System.out.println("La méthode DonnerTicket n'est pas trouvée");
        } catch (StubNotFoundException e) {
            System.out.println("Stub n'a pas été exporté");
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("Erreur : " + e.getMessage());
        }
    }

    public void enregistrerServCalcul(ServiceCalcul var1) throws RemoteException {
        noeuds.add(var1);
        System.out.println("Enregistrement service");
    }


    public void distribuerCalcul(ServiceClient sCli) throws RemoteException {
        int nbservActif = 0;
        ArrayList<ServiceCalcul> err = new ArrayList();
        for (ServiceCalcul s : noeuds) {
            try {
                s.ping();
            } catch (RemoteException e) {
                err.add(s);
                System.out.println("serveur non dispo");
            }
        }
        for (ServiceCalcul s : err) {
            noeuds.remove(s);
        }
        nbservActif = noeuds.size();
        if (nbservActif == 0) {
            throw new RemoteException("Il n'y a aucun service de calcul disponible");
        }
        RayThreader r = null;
        ArrayList<Integer> coor = new ArrayList();
        this.hautReelle = (int) (Math.floor(h / nbservActif));
        int surplus = h - hautReelle * nbservActif;
        this.sommehaut = 0;
        for (int i = 0; i < nbservActif; i++) {
            if (i == nbservActif - 1) this.hautReelle += surplus;
            System.out.println(sommehaut);
            r = new RayThreader(this, i, this.sommehaut);
            r.start();
            System.out.println("fait ici");
            this.sommehaut += hautReelle;
            coor.clear();
        }

    }

    public int changerServ() throws RemoteException{
        boolean servTrouve = false;
        int i = 0;
        while (!servTrouve) {
            for (i = 0; i < this.noeuds.size(); i++) {
                try {
                    if (noeuds.get(i).isActif()) {
                        servTrouve = true;
                        System.out.println("CHANGEMENT DE SERVICECALCUL");
                        return i;
                    }
                } catch (RemoteException e) {
                    System.out.println("Serveur impossible à contacter, suppression");
                    this.noeuds.remove(i);
                }
            }
        }
        return 0;
    }


    public void rendreScene(ServiceClient sCli, int l, int h, Scene var1) throws RemoteException {
        rendus.clear();
        this.sCli = sCli;
        this.l = l;
        this.h = h;
        this.scene = var1;
        distribuerCalcul(this.sCli);
    }

    public class RayThreader extends Thread {
        LancerRaytracer lr;
        int nbService;
        int sommHaut;

        public RayThreader(LancerRaytracer lr, int nbService, int sh) {
            this.lr = lr;
            this.nbService = nbService;
            this.sommHaut = sh;
        }

        public void run() {
            ArrayList<Integer> coor = new ArrayList<>();
            coor.add(0);
            coor.add(this.sommHaut);
            System.out.println("Lancé");
            boolean rendu = false;
            Image i = null;
            while (!rendu) {
                try {
                    i = this.lr.noeuds.get(nbService).calculerRendu(0, this.sommHaut, this.lr.l, this.lr.hautReelle, this.lr.scene);
                    rendu = true;

                } catch (RemoteException e) {
                    System.out.println("non");
                    this.lr.noeuds.remove(nbService);
                    try{
                        this.nbService = this.lr.changerServ();
                    }
                    catch(RemoteException e2)
                    {
                        System.out.println("Erreur de changement de serviceCalcul");
                    }

                }
            }
            try {
                HashMap<Image, ArrayList<Integer>> h = new HashMap<>();
                h.put(i, coor);
                this.lr.sCli.rendreImage(h);
                this.stop();
            } catch (RemoteException e) {
                System.out.println("Client impossible à contacter");
            }
        }
    }
}
