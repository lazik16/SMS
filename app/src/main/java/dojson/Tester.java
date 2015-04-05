/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dojson;

import com.example.michal.smssync.Messenger;
import com.example.michal.smssync.SendSmsByGsm;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 *
 * @author Dominik
 */
public class Tester {

    protected  Messenger messenger;

    public Tester(Messenger messenger) {
        this.messenger = messenger;
    }

    /*
    public static void main(String[] args) {
       /* JSONBroker broker = new JSONBroker();
        ObjectFactory factory = new ObjectFactory();

      /*  MessageCo mesCo
                = factory.createMessage(
                        "+420 775 414 547",
                        "+420 112 445 778",
                        "nooord meeeaaaad",
                        new Date());

        DeviceCo devCo = factory.createDevice(
                "+420 775 414 547");

        ContactCo conCo = factory.createContact(
                "alfa@gmail.com",
                "Martin",
                "Luther",
                "Cartn",
                "+478 542 112");

     /* MsgPack packedContact = factory.createMsgPack(conCo, conCo.hashCode(), MsgPack.ActionType.NEW, MsgPack.ObjectType.MES);
        MsgPack packedDevice = factory.createMsgPack(devCo, devCo.hashCode(), MsgPack.ActionType.NEW, MsgPack.ObjectType.DEV);
        MsgPack packedMessage = factory.createMsgPack(mesCo, mesCo.hashCode(), MsgPack.ActionType.NEW, MsgPack.ObjectType.CON);

        String encodedMessage = broker.messageToJson(packedMessage);
        String encodedContact = broker.contactToJson(packedContact);
        String encodedDevice = broker.deviceToJson(packedDevice);

        System.out.println(encodedMessage);
        System.out.println(encodedContact);
        System.out.println(encodedDevice);

        /* pro rozpoznani zpravy se vyuzije funkce recognize, ktera zpracuje
        * a vyhodnoti prijatou zpravu.*/
       /* recognize(encodedMessage, broker);
        recognize(encodedContact, broker);
        recognize(encodedDevice, broker);*/
   // }

    public void recognize(String encodedMessage, JSONBroker broker) {
        MsgPack extractedPack = broker.extractObject(encodedMessage);
        Type collectionType;
        Gson gson = new Gson();
        switch (extractedPack.getObjectType()) {
            case DEV:
                collectionType = new TypeToken<MsgPack<DeviceCo>>() {}.getType();
                System.out.println(collectionType.toString());
                MsgPack<DeviceCo> extractedPackD = gson.fromJson(encodedMessage, collectionType);
                System.out.println(extractedPackD.toString());
                // Zde se vyuzije vlastni kod pro zpracovani zpravy
                // napr extractedPackD.getObject() vrati objekt typu DeviceCo a ten lze
                // vyuzit k dalsimu zpracovani.

                //ZTM NIC NEMAM NWM JESTE..
                break;
            case MES:
                collectionType = new TypeToken<MsgPack<MessageCo>>() {
                }.getType();
                MsgPack<MessageCo> extractedPackM = gson.fromJson(encodedMessage, collectionType);
                new SendSmsByGsm(extractedPackM.getObject().getReciever(), extractedPackM.getObject().getText());
                System.out.println("Odesila se zprava: "+extractedPackM.getObject().getReciever()+"  "+extractedPackM.getObject().getText());

                break;
            case CON:
                collectionType = new TypeToken<MsgPack<ContactCo>>() {
                }.getType();
                System.out.println(collectionType.toString());
                MsgPack<ContactCo> extractedPackC = gson.fromJson(encodedMessage, collectionType);


                break;
            default:
                break;
        };
    }

}
