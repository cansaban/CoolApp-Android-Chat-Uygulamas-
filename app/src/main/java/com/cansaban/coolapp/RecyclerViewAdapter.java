package com.cansaban.coolapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> { // bir Adapter  sınıfı oluşturdum

    private List<String> chatMessages;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) { //burda adapter sınıfı içinde her bir row sırası nasıl olucak burada belirlediğimiz XML'le birbirine bağladım

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyler_list_row,parent,false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) { // bu view da hangi string'in görüneceğini söyledik o da biz paslanan chatpassages arraylisti'nden alınacak olan pozisyondaki mesaj denk geliyorsa onu göster dedik

        String chatMessage = chatMessages.get(position);
        holder.chatMessage.setText(chatMessage);

    }

    @Override
    public int getItemCount() {  //bunu da yaptığımız süre boyunca kullan dedim
        return chatMessages.size();
    }

    public RecyclerViewAdapter(List<String> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView chatMessage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chatMessage = itemView.findViewById(R.id.recycler_view_text_view);
        }
    }




}
