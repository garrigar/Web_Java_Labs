package rmi;

import entities.Product;
import entities.Order;

import java.rmi.AlreadyBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.TreeSet;

public class Server {

    public static void main(String[] args) {

        try {

            // object implementing UniqueSorter (an object for remote access), anonymous
            UniqueSorter uniqueSorter = new UniqueSorter() {
                @Override
                public Order sortAndSaveUnique(Order order) throws RemoteException {

                    try {

                        TreeSet<Product> set = new TreeSet<>((o1, o2) -> {
                            int totalDif = Double.compare(o1.getTotal(), o2.getTotal());
                            // if totals are different, products are obviously different and must be sorted by total
                            if (totalDif != 0) {
                                return totalDif;
                            }
                            // totals are equal if we are here
                            // if everything other is equal, then it is a duplicate
                            if (o1.equals(o2)) {
                                return 0;
                            }
                            // here we are with two different products with same total
                            // let the first one be "less"
                            return -1;
                        });

                        for (int i = 0; i < order.getProductsCount(); i++) {
                            set.add(order.getProduct(i));
                        }

                        Order ans = new Order();
                        for (Product product : set) {
                            ans.addProduct(product);
                        }

                        return ans;

                    } catch (Exception e) {
                        throw new RemoteException("exception at rmi", e);
                    }
                }
            };

            // creating a registry of shared objects
            final Registry registry = LocateRegistry.createRegistry(5050);

            // creating a stub - it receives remote calls
            Remote stub = UnicastRemoteObject.exportObject(uniqueSorter, 0);

            // register the stub in the registry by unique name
            registry.bind(UniqueSorter.BINDING_NAME, stub);

            // stop program by typing "shutdown"
            System.out.println("Type 'shutdown' to shut the rmi down");
            Scanner input = new Scanner(System.in);
            System.out.print("<rmi># ");
            while (!"shutdown".equals(input.nextLine())) {
                System.out.print("<rmi># ");
                Thread.sleep(10);
            }

            // Unexporting all UnicastRemoteObjects within a running JVM is sufficient to close all RMI threads.
            UnicastRemoteObject.unexportObject(uniqueSorter, true);

        } catch (RemoteException | AlreadyBoundException | InterruptedException e) {
            e.printStackTrace();
            System.err.println(e.getLocalizedMessage());
        }

    }

}
