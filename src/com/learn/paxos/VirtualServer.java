package com.learn.paxos;

import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Greeting on 2017/3/7.
 * 只是提供一下 Acceptor 和 Proposer 一些必要的方法和做一些共同的初始化，简便开发
 */
public abstract class VirtualServer {
    protected Integer id;
    protected Random random;
    private Network network;
    public VirtualServer(int id,Network network){
        this.id = id;
        this.network = network;
        this.random = new Random();
    }

    //just wrap the thread.sleep
    protected void waitAMoment(){
        int waitTime = random.nextInt(1000) + 500;
        try{
            Thread.sleep(waitTime);
        }catch(InterruptedException e){
            System.out.println("interrupted");
        }
    }

    protected Queue<Map<String,String>> receive(){
        return network.receive(id);
    }

    protected void send(int id,Map<String,String> message){
        this.network.send(id,message);
    }

    protected List<Integer> getAcceptorIds(){
        return this.network.getAcceptorIds();
    }

    protected List<Integer> getProposerIds(){
        return this.network.getProposerIds();
    }

    abstract void show(String info);
}
