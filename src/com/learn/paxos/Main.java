package com.learn.paxos;

import java.util.ArrayList;
import java.util.List;

/**
 * run some threads to test the Paxos program
 */
public class Main {

    public static void main(String[] args) {
        List<Runnable> threads = new ArrayList<>();
        Network network = new Network(95);
        for(int id = 0; id < 3; ++id){
            threads.add(new Proposer(id,network));
            network.addProposerId(id);
        }
        for(int id = 3; id < 6; ++id){
            threads.add(new Acceptor(id,network));
            network.addAcceptorId(id);
        }
        //start threads
        for(Runnable thread : threads){
            new Thread(thread).start();
        }



    }
}
