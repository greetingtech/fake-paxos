package com.learn.paxos;


import java.util.*;

/**
 * Created by Greeting on 2017/3/6.
 * 这是用来模拟网络交互的类
 */
public class Network {

    List<Queue<Map<String,String>>> netResource;
    List<Integer> proposerIds;
    List<Integer> acceptorIds;
    int netSuccessRate;

    /**
     * 初始化虚拟网络，设置网络收发成功率参数
     * @param netSuccessRate
     */
    public Network(int netSuccessRate){
        this.netSuccessRate = Math.max(netSuccessRate,0);
        this.netSuccessRate = Math.min(this.netSuccessRate,100);
        this.proposerIds = new ArrayList<>();
        this.acceptorIds = new ArrayList<>();
        this.netResource = new ArrayList<>();
    }

    /**
     * 向proposerIds集合添加一个proposer的id
     * @param proposerId
     * @return
     */
    public Network addProposerId(Integer proposerId){
        proposerIds.add(proposerId);
        netResource.add(new LinkedList<>());
        return this;
    }

    /**
     * 向acceptorIds集合添加一个acceptor的id
     * @param acceptorId
     * @return
     */
    public Network addAcceptorId(Integer acceptorId){
        acceptorIds.add(acceptorId);
        netResource.add(new LinkedList<>());
        return this;
    }


    /**
     * 从虚拟网络中获得proposerIds
     * @return
     */
    public List<Integer> getProposerIds() {
        return proposerIds;
    }

    /**
     * 从虚拟网络中获得acceptorIds
     * @return
     */
    public List<Integer> getAcceptorIds() {
        return acceptorIds;
    }

    /**
     * 用随机数来模拟网络是否成功
     * @return
     */
    private boolean isSuccess(){
        Random random = new Random();
        int randNum = random.nextInt(101);
        return randNum <= netSuccessRate;
    }

    /**
     * 向指定id的角色发送信息
     * @param id
     * @param message
     */
    public synchronized void send(int id,Map<String,String> message){
        if(isSuccess()){
            Queue<Map<String,String>> destination = netResource.get(id);
            destination.add(message);
        }
    }

    /**
     * 根据自己的id来获取信息
     * @param id
     * @return
     */
    public synchronized Queue<Map<String,String>> receive(int id){
        Queue<Map<String,String>> messages = new LinkedList<>();
        Queue<Map<String,String>> origin = netResource.get(id);
        while(!origin.isEmpty()){
            if(isSuccess()) {
                messages.add(origin.remove());
            }
        }
        return messages;
    }



}
