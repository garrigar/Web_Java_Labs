package rmi;

import entities.Order;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface UniqueSorter extends Remote {

    // will be used as unique name of a remote object
    String BINDING_NAME = "rmi.unique_sorter";

    Order sortAndSaveUnique(Order order) throws RemoteException;

}
