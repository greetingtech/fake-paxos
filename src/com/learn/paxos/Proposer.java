package com.learn.paxos;

import java.util.*;

/**
 * Created by Greeting on 2017/3/6.
 * Proposer类，扮演Proposer角色
 */
public class Proposer extends VirtualServer implements Runnable{

    private Integer currentProposalId;
    private Integer acceptorMajorityNum;
    private Integer valueShouldBeProposed;
    private Integer finalValue;

    /**
     * 初始化
     * @param id 该Proposer在虚拟网络中的id
     * @param network 绑定虚拟网络对象
     */
    public Proposer(int id,Network network){
        super(id,network);
        this.currentProposalId = id;
        this.acceptorMajorityNum = network.getAcceptorIds().size()/2+1;
        this.finalValue = null;
    }


    /**
     * 生成下一轮的提议id 或者可以叫做epoch
     * @return
     */
    private Integer getNextProposeId(){
        Integer proposerNum = getProposerIds().size();
        currentProposalId = currentProposalId + proposerNum;
        return currentProposalId;
    }

    /**
     * 生成准备请求的消息
     * @param proposalId
     * @return
     */
    private Map<String,String> makePrepareMessage(Integer proposalId){
        Map<String,String> message = new HashMap<>();
        message.put("type","prepare");
        message.put("fromRole","proposer");
        message.put("fromId",id.toString());
        message.put("proposalId",proposalId.toString());
        return message;
    }

    /**
     * 获取一个0~999的随机值，作为Proposer发现Acceptor没有值的时候的提案值
     * @return
     */
    private int getRandomValue(){
        return random.nextInt(1000);
    }

    /**
     * Proposer在准备阶段的工作
     * 向所有Acceptor发送信息
     * 等待回应
     * 判断是否收到了占比大多数的Acceptor的回应，若是，则返回true，否则返回false
     * @return 准备是否成功
     */
    private boolean prepare(){
        List<Integer> acceptorIds = getAcceptorIds();
        currentProposalId = getNextProposeId();
        for(Integer acceptorId : acceptorIds){
            send(acceptorId,makePrepareMessage(currentProposalId));
        }
        show("sent prepare");

        waitAMoment();
        Queue<Map<String,String>> messages = receive();
        Integer maxAcceptedProposalId = -1;
        valueShouldBeProposed = null;
        Set<Integer> acceptorIdSet = new HashSet<>();
        while(!messages.isEmpty()){
            Map<String,String> message = messages.remove();
            if(!message.get("type").equals("promise")){
                continue;
            }
            if(!message.get("fromRole").equals("acceptor")){
                continue;
            }
            if(!(Integer.parseInt(message.get("promiseId")) == currentProposalId)){
                continue;
            }
            Integer fromId = Integer.parseInt(message.get("fromId"));
            acceptorIdSet.add(fromId);
            if(!message.containsKey("acceptedProposalId")){
                continue;
            }
            Integer acceptedProposalId = Integer.parseInt(message.get("acceptedProposalId"));
            if(acceptedProposalId > maxAcceptedProposalId){
                maxAcceptedProposalId = acceptedProposalId;
                valueShouldBeProposed = Integer.parseInt(message.get("acceptedValue"));
            }
        }
        if(acceptorIdSet.size() < acceptorMajorityNum){
            return false;
        }
        if(valueShouldBeProposed == null){
            valueShouldBeProposed = getRandomValue();
        }
        return true;
    }

    /**
     * 生成提议请求的消息
     * @return
     */
    private Map<String,String> makeProposeMessage(){
        Map<String,String> message = new HashMap<>();
        message.put("type","propose");
        message.put("fromRole","proposer");
        message.put("fromId",id.toString());
        message.put("proposalId",currentProposalId.toString());
        message.put("value",valueShouldBeProposed.toString());
        return message;
    }

    /**
     * 正式处理提议的工作
     * 向大多数Acceptor（可以随机选）发送提议请求（这里为了简单，直接向所有Acceptor发送了请求）
     * 然后等待回应，若得到大多数Acceptor的回应，而且回应的value就是自己提交的value就返回true
     * 否则返回false
     * @return 提议是否成功
     */
    private boolean propose(){
        Map<String,String> proposeMessage = makeProposeMessage();
        List<Integer> acceptorIds = getAcceptorIds();
        for(Integer acceptorId : acceptorIds){
            send(acceptorId,proposeMessage);
        }
        show("sent propose");
        waitAMoment();
        Queue<Map<String,String>> messages = receive();
        Set<Integer> acceptorIdSet = new HashSet<>();
        while(!messages.isEmpty()) {
            Map<String,String> message = messages.remove();
            if(!message.get("type").equals("accept")){
                continue;
            }
            if(!message.get("fromRole").equals("acceptor")){
                continue;
            }
            if(! (Integer.parseInt(message.get("value")) == valueShouldBeProposed) ){
                continue;
            }

            Integer acceptorId = Integer.parseInt(message.get("fromId"));
            acceptorIdSet.add(acceptorId);
        }
        if(acceptorIdSet.size() < acceptorMajorityNum){
            return false;
        }
        finalValue = valueShouldBeProposed;
        return true;
    }

    /**
     * 主循环，不断准备和提交，直到成功获得大多数Acceptor的接受
     */
    @Override
    public void run(){
        while(true){
            boolean isPrepared = prepare();
            if(!isPrepared){
                show("prepared failed");
                continue;
            }
            show("prepared success");
            boolean isAccepted = propose();
            if(!isAccepted){
                show("accepted failed");
                continue;
            }
            show("has been accepted finalValue is "+finalValue);
            break;
        }
    }

    @Override
    public void show(String info){
        System.out.println("Proposer id:"+id + " info ->  " + info + "  ");
    }
}
