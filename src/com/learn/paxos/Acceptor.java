package com.learn.paxos;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Created by Greeting on 2017/3/6.
 * Acceptor类，扮演Acceptor的角色
 */
public class Acceptor extends VirtualServer implements Runnable {

    private Integer maxProposalId;
    private Integer acceptedProposalId;
    private Integer acceptedValue;

    /**
     * 初始化Acceptor
     * @param id 该Acceptor在虚拟网络中的id
     * @param network 绑定一个虚拟网络
     */
    public Acceptor(int id,Network network){
        super(id,network);
        maxProposalId = -1;
        acceptedProposalId = null;
        acceptedValue = null;
    }

    /**
     * 生成Promise
     * 写入承诺不会再回应proposeId小于自己收到过的最大值的任何请求
     * @return
     */
    private Map<String,String> makePromise(){
        Map<String,String> message = new HashMap<>();
        message.put("type","promise");
        message.put("fromRole","acceptor");
        message.put("fromId",id.toString());
        message.put("promiseId",maxProposalId.toString());
        if(acceptedProposalId != null){
            message.put("acceptedProposalId",acceptedProposalId.toString());
            message.put("acceptedValue",acceptedValue.toString());
        }
        return message;
    }

    /**
     * 处理准备阶段的工作
     * 只回应proposeId不小于当前收到过的最大值的请求
     * @param message
     */
    private void processPrepare(Map<String,String> message){
        String fromRole = message.get("fromRole");
        if(!fromRole.equals("proposer")){
            return;
        }
        Integer proposalId = Integer.parseInt(message.get("proposalId"));
        if(proposalId < maxProposalId){
            return;
        }
        maxProposalId = proposalId;
        Integer fromId = Integer.parseInt(message.get("fromId"));
        send(fromId,makePromise());
    }

    /**
     * 生成Accept回应
     * @return
     */
    private Map<String,String> makeAccept(){
        Map<String,String> message = new HashMap<>();
        message.put("type","accept");
        message.put("fromRole","acceptor");
        message.put("fromId",id.toString());
        message.put("value",acceptedValue.toString());
        return message;
    }

    /**
     * 处理Proposer的propose请求，判断是否要接受，自己是否已经有确定的值
     * 没有的话要把自己的值设置成propose请求里的value值（这里只要判断proposeId和之前准备的一样就行，因为Proposer会确保提交的值不会乱）
     * @param message
     */
    private void processPropose(Map<String,String> message){
        String fromRole = message.get("fromRole");
        if(!fromRole.equals("proposer")){
            return;
        }
        Integer proposalId = Integer.parseInt(message.get("proposalId"));
        if(!proposalId.equals(maxProposalId)){
            return;
        }
        if(acceptedProposalId == null){
            acceptedProposalId = proposalId;
            acceptedValue = Integer.parseInt(message.get("value"));
            show( "accept the value:"+acceptedValue);
        }
        Integer fromId = Integer.parseInt(message.get("fromId"));
        send(fromId,makeAccept());
    }

    /**
     * Acceptor的处理总循环，根据请求类型，分别交给不同的函数处理
     */
    private void process(){
        Queue<Map<String,String>> messages =  receive();
        while(!messages.isEmpty()){
            Map<String,String> message = messages.remove();
            String messageType = message.get("type");
            if(messageType.equals("prepare")){
                processPrepare(message);
            }else if(messageType.equals("propose")){
                processPropose(message);
            }
        }
    }

    /**
     * 处理加等待
     */
    @Override
    public void run(){
        while(true){
            process();
            waitAMoment();
        }
    }

    @Override
    public void show(String info){
        System.out.println("Acceptor id:"+id + " info ->  " + info + "  ");
    }
}
