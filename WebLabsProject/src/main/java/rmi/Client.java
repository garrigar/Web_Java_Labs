package rmi;

import entities.Order;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Client {

    public static void main(String[] args) {

        try {
            // getting remote objects registry
            final Registry registry = LocateRegistry.getRegistry(5050);

            // getting object (proxy object)
            UniqueSorter remoteSorter = (UniqueSorter) registry.lookup(UniqueSorter.BINDING_NAME);

            // read old order from input file
            Order oldOrder;
            try (FileReader fileReader = new FileReader(args[0])) {
                oldOrder = Order.readProducts(fileReader);
            }

            // INVOKING REMOTE METHOD
            Order newOrder = remoteSorter.sortAndSaveUnique(oldOrder);

            // write new order to output file
            try (FileWriter fileWriter = new FileWriter(args[1])) {
                newOrder.writeProducts(fileWriter);
            }

        } catch (/*RemoteException |*/ IOException | NotBoundException e) { // RemoteException is a subclass of IOException

            e.printStackTrace();

            // write error message to output file
            try (FileWriter fileWriter = new FileWriter(args[1])) {
                fileWriter.write(e.getLocalizedMessage());
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }

    }

}
