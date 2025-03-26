package it.unitn.ds1;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;

import it.unitn.ds1.Chatter.JoinGroupMsg;
import it.unitn.ds1.Chatter.StartChatMsg;
import it.unitn.ds1.Chatter.PrintHistoryMsg;

public class CausalDelivery {
    final private static int N_LISTENERS = 10; // number of listening actors

    public static void main(String[] args) {

        // create the 'helloakka' actor system
        final ActorSystem system = ActorSystem.create("helloakka");

        // list of references for actors populating the system
        List<ActorRef> group = new ArrayList<>();
        int id = 0;

        // the first two peers will be participating in a conversation
        group.add(system.actorOf(
                Chatter.props(id++, "a"), // this one will start the topic "a"
                "chatter0"));

        group.add(system.actorOf(
                Chatter.props(id++, "a"), // this one will catch up the topic "a"
                "chatter1"));

        // DONE 1: create additional actors in a different conversation
        group.add(system.actorOf(
                Chatter.props(id++, "b"), // this one will start the topic "b"
                "chatter2"));

        group.add(system.actorOf(
                Chatter.props(id++, "b"), // this one will catch up the topic "b"
                "chatter3"));

        // the rest are silent listeners: they have no topics to discuss
        for (int i = 0; i < N_LISTENERS; i++) {
            group.add(system.actorOf(Chatter.props(id++, null), "listener" + i));
        }

        // ensure that no one can modify the group
        group = Collections.unmodifiableList(group);

        // send the group member list to everyone in the group
        JoinGroupMsg join = new JoinGroupMsg(group);
        for (ActorRef peer : group) {
            peer.tell(join, null);
        }

        // tell the first chatter of a to start conversation
        group.get(0).tell(new StartChatMsg(), null);
        // tell the first chatter of b to start conversation
        group.get(2).tell(new StartChatMsg(), null);
        try {
            System.out.println(">>> Wait for the chats to stop and press ENTER <<<");
            System.in.read();

            // after chats stop, send actors a message to print their logs
            PrintHistoryMsg msg = new PrintHistoryMsg();
            for (ActorRef peer : group) {
                peer.tell(msg, null);
            }
            System.out.println(">>> Press ENTER to exit <<<");
            System.in.read();
        } catch (IOException ioe) {
        }
        system.terminate();
    }
}
