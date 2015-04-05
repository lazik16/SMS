package com.example.michal.smssync;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Timer;
import java.util.TimerTask;

import Contact.ContactObserver;
import Contact.GetContactsDemo;
import library.SMSSyncLibrary;


public class MainActivity extends Activity {
    IntentFilter intentFilter;
    Messenger me;
    // private Client c;
    public static MainActivity mainActivity = null;
    NumberPicker np1;
    NumberPicker np2;
    NumberPicker np3;
    int timer;
    EditText cislo;
    int casSynchronizace = 15; //nacist z gui
    Queue<String> queue = new PriorityQueue<String>();
    public static String testNumber = "+420 773 048 598";

    private BroadcastReceiver intentReciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            TextView intTxt = (TextView) findViewById(R.id.textMsg);
            intTxt.setText(intent.getExtras().getString("sms"));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Registrace kontakt observeru
        getContentResolver().registerContentObserver(
                ContactsContract.Contacts.CONTENT_URI, true, new ContactObserver());
        setContentView(R.layout.activity_main);

        intentFilter = new IntentFilter();
        intentFilter.addAction("SMS_RECEIVED_ACTION");


        cislo = (EditText) findViewById(R.id.cislo);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        setMainActivity();
        synchronizace();
    }

    public void setMainActivity() {
        this.mainActivity = this;
    }

    public static MainActivity getMainActivity() {
        return mainActivity;
    }

    public void Time(View v) {//dodelat
        np1 = (NumberPicker) findViewById(R.id.numberPicker1);
        np2 = (NumberPicker) findViewById(R.id.numberPicker2);
        np3 = (NumberPicker) findViewById(R.id.numberPicker3);
        this.timer = ((np1.getValue() * 60 * 60) + (np2.getValue() * 60) + np3.getValue());
        System.out.println(this.timer);
    }

    public void setNumber(View v) { //doresit hashovani
        //this.testNumber=cislo.getText().toString();  //nefunkcni
        EditText setNumberText = (EditText) findViewById(R.id.cislo);
        testNumber = setNumberText.getText().toString();


        System.out.println("Registrace cisla: " + testNumber);
        new Messenger("registerDevice{\"action\":\"NEW\",\"objectType\":\"DEV\",\"object\":{\"phone\":\"" + this.testNumber + "\"},\"hash\":396873410}", true);
    }


    public void synchronizace() { //zkontrolovat jestli se zmeni doba poslani resync kdyz ho zmenim v gui

        final Timer timer = new Timer();
        TimerTask resync = new TimerTask() {
            public void run() {
                new Messenger("rsync", false);
                if (queue.size() != 0) {
                    for (int i = 0; i < queue.size(); i++) {
                        if (Messenger.isConnected() == true) {
                            new Messenger(queue.poll().toString(), false);// dalo by se udelat tak ze by overoval jestli dosel prikaz na server a pak ho vymazal z kolekce
                        }
                    }
                }
            }
        };
        timer.scheduleAtFixedRate(resync, 0, casSynchronizace * 1000);

    }

    public void loadSMS(View v) {               //nejspise nepouzijem
        //nacteni sms od kontaktu z telefonu
        Uri uri = Uri.parse("content://sms/");
        ContentResolver contentResolver = getContentResolver();
        String phoneNumber = "0123456789";
        String sms = "address='" + phoneNumber + "'";
        Cursor cursor = contentResolver.query(uri, new String[]{"_id", "body"}, sms, null, null);
        System.out.println(cursor.getCount());
        while (cursor.moveToNext()) {
            String strbody = cursor.getString(cursor.getColumnIndex("body"));
            System.out.println(strbody);
        }
        cursor.close();//tady nekde dat cursor.close()
    }

    public void loadContact(View v) {
        //nacteni vsech kontaktu
        GetContactsDemo contactsDemo = new GetContactsDemo();
        contactsDemo.readContacts(getContentResolver());
    }

    public void loadContact() {
        //nacteni vsech kontaktu
        GetContactsDemo contactsDemo = new GetContactsDemo();
        contactsDemo.readContacts(getContentResolver());
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }


    public void startContactList(View v){
        Intent i = new Intent(this, ContactActivity.class);
        startActivityForResult(i, SMSSyncLibrary.CONTACT_LIST_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case SMSSyncLibrary.CONTACT_LIST_REQUEST_CODE :
                if(data != null){
                    String number = data.getStringExtra("PHONE_NUMBER");
                    EditText editTextNumber = (EditText) findViewById(R.id.numberFromContactList);
                    editTextNumber.setText(number);
                }
                break;



            default:
        }
    }

}

