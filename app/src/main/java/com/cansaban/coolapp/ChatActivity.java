package com.cansaban.coolapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class ChatActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    EditText messageText;

    FirebaseDatabase database;
    DatabaseReference databaseReference;

    private ArrayList<String> chatMessages = new ArrayList<>();


    @Override
    public boolean onCreateOptionsMenu(Menu menu) { // burda menüyü bağlıyorum

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) { // menüye tıklandığında neler yapacağız
        if (item.getItemId() == R.id.options_menu_sign_out){

            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(intent);

        }
        else if (item.getItemId() == R.id.options_menu_profile){
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.catch_me_menu_profile){
            Intent intent = new Intent(getApplicationContext(),CatchTheBunny.class);
            startActivity(intent);
        }
        else if (item.getItemId() == R.id.photo_save_menu){
            Intent intent = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        /*chatMessages.add("sss");
        chatMessages.add("1234");
        chatMessages.add("asdfghj");
        chatMessages.add("üçşğ");*/

        messageText = findViewById(R.id.chat_activity_message_text);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerViewAdapter = new RecyclerViewAdapter(chatMessages);

        RecyclerView.LayoutManager recyclerViewManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(recyclerViewManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(recyclerViewAdapter);

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        getData();

        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();

        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(final String s, String s1) {
                //System.out.println("userId: " + s);

                UUID uuid = UUID.randomUUID();
                final String uudiString = uuid.toString();

                DatabaseReference newReference = database.getReference("KullaniciIDleri");
                newReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        ArrayList<String> playerIDsFromServer = new ArrayList<>();

                        for (DataSnapshot ds: dataSnapshot.getChildren()){

                            HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                            String currentPlayerID =hashMap.get("kullanıcıID");

                            playerIDsFromServer.add(currentPlayerID);

                        }

                        if(!playerIDsFromServer.contains(s)){
                            databaseReference.child("KullaniciIDleri").child(uudiString).child("kullanıcıID").setValue(s);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });


    }

    public void sendMessage(View view){

        final String messageToSend = messageText.getText().toString();

        UUID uuid = UUID.randomUUID(); // random child atıyor
        String uuidString = uuid.toString();

        FirebaseUser user = mAuth.getCurrentUser();
        final String userEmail = user.getEmail().toString();

        //databaseReference.child("Chats").child("Chat1").child("Test Chat").child("Test1").setValue(messageToSend);
        databaseReference.child("Sohbetler").child(uuidString).child("KullaniciMesaji").setValue(messageToSend);
        databaseReference.child("Sohbetler").child(uuidString).child("KullaniciEmail").setValue(userEmail);
        databaseReference.child("Sohbetler").child(uuidString).child("KullaniciZamani").setValue(ServerValue.TIMESTAMP); // zaman stamp i sıralı yazdırmak için yolladığım zaman göre çekmek için önemli

        messageText.setText("");

        getData();

        //onesignal

        DatabaseReference newReference = database.getReference("KullaniciIDleri");
        newReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot ds : dataSnapshot.getChildren()){

                    HashMap<String,String> hashMap = (HashMap<String, String>) ds.getValue();

                    String playerID = hashMap.get("kullanıcıID");

                    //System.out.println("playerId server : " +playerID);

                    try {
                        OneSignal.postNotification(new JSONObject("{'contents': {'en':'"+userEmail+":"+messageToSend+"'}, 'include_player_ids': ['" + playerID + "']}"), null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



    }

    public void getData(){

        DatabaseReference newdatabaseReference = database.getReference("Sohbetler"); //gel referansla sohbetlerle ilgilen

        Query query = newdatabaseReference.orderByChild("KullaniciZamani");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) { //realtime database'i dinleyen ve burdaki verileri alan methodum

               /* System.out.println("dataSnaphot Children: " +dataSnapshot.getChildren());
                System.out.println("dataSnaphot value: " +dataSnapshot.getValue());
                System.out.println("dataSnaphot Key: " +dataSnapshot.getKey()); */

               chatMessages.clear();

               for (DataSnapshot ds : dataSnapshot.getChildren()){

                   HashMap<String, String> hashMap = (HashMap<String, String>) ds.getValue();
                   String kullaniciEmail = hashMap.get("KullaniciEmail");
                   String kullaniciMesaji = hashMap.get("KullaniciMesaji");

                   chatMessages.add(kullaniciEmail + ": " + kullaniciMesaji);

                   recyclerViewAdapter.notifyDataSetChanged(); // yeni bir şey ekledim git onu güncelle
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { //V.t a bağlantım kopsa yapcağım toast göstermek güzel bence kullanıcıya

                Toast.makeText(getApplicationContext(),databaseError.getMessage().toString(),Toast.LENGTH_LONG).show();;


            }
        });

    }


}
