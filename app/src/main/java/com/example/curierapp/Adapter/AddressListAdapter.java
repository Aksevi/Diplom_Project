package com.example.curierapp.Adapter;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.curierapp.AddressClickListener;
import com.example.curierapp.DataBase.RoomDB;
import com.example.curierapp.Models.Address;
import com.example.curierapp.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

//1️⃣ Это адаптер для RecyclerView — типа как официант для списка заметок: он берёт данные (список заметок), создаёт визуальные карточки, расставляет их по экрану и реагирует на клики.
// Это посредник между  данными (списком Address) и RecyclerView.
//Он знает, сколько у тебя записей.
//Он умеет создавать карточки (View) для каждой записи.
//Он умеет заполнять их данными.
//Он передаёт события клика обратно (через AddressClickListener).
//**************************************************************************************************
// Этот класс расширяет RecyclerView.Adapter и указывает, что будет использовать кастомный ViewHolder — NotesViewHolder.
public class AddressListAdapter extends RecyclerView.Adapter<AddressViewHolder> {


    // 2️⃣ Переменные и конструктор
    Context context; // Контекст, нужен для доступа к ресурсам, разметке и т.д.
    List<Address> list; // список адресов
    AddressClickListener listener; // Интерфейс — слушатель нажатий


    // конструктор (alt+ins)
    public AddressListAdapter(Context context, List<Address> list, AddressClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;

    }

    @Override
    public long getItemId(int position) {
        return list.get(position).getId(); // возвращаем уникальный id
    }


    public List<Address> getList() {
        return list;
    }

    // метод для перетаскивания  - не работает
    public void moveItem(int fromPos, int toPos) {
        if (fromPos < 0 || toPos < 0 || fromPos >= list.size() || toPos >= list.size()) return;

        if (fromPos < toPos) {
            for (int i = fromPos; i < toPos; i++) {
                Collections.swap(list, i, i + 1);
            }
        } else {
            for (int i = fromPos; i > toPos; i--) {
                Collections.swap(list, i, i - 1);
            }
        }
        notifyItemMoved(fromPos, toPos);

        for (int i = 0; i < list.size(); i++) {
            list.get(i).setPosition(i);
        }
        // сохраняем новые позиции в базе
        new Thread(() -> {
            RoomDB db = RoomDB.getInstance(context);
            for (Address a : list) {
                db.mainDAO().update(a);
            }
        }).start();
    }


    //3️⃣ Создание карточки
    @NonNull
    @Override
    public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AddressViewHolder(LayoutInflater.from(context).inflate(R.layout.addres_list, parent, false)); // Создаёт новую "карточку заметки" из XML-файла notes_list.xml (твой шаблон заметки), но не прикрепляет её к родителю сразу (вот для чего false в конце).
    }

    //4️⃣ Заполнение карточки данными. На этом шаге мы берём данные из list.get(position) и вставляем их в соответствующие элементы карточки.
    @Override
    public void onBindViewHolder(@NonNull AddressViewHolder holder, int position) {


        //Устанавливаем заголовок, текст и дату:
        holder.textAddress.setSelected(true);
        holder.textAddress.setText(list.get(position).getAddress());
        holder.textPhone.setText(list.get(position).getPhone());
        holder.textComment.setText(list.get(position).getComment());
        holder.textDate.setText(list.get(position).getDate());
        holder.textDate.setSelected(true); //setSelected(true) используется для анимации бегущей строки (marquee).

        //Галочка "выполнено"
        if (list.get(position).isChecked()) {
            holder.imageCheck.setImageResource(R.drawable.checkbox);
        } else {
            holder.imageCheck.setImageResource(0); //Если адрес помечен как "checked", то показываем иконку галочки.
        }
        //Цвет карточки. Выбираем случайный цвет из заранее заданных (color1–color5) и красим фон карточки.
        int colorCode = getRandomColor(); // getRandomColor() метод выбора случайного цвета (сделали его ниже). colorCode - переменная куда будем этот цвет класть
        holder.addressContainer.setCardBackgroundColor(holder.itemView.getResources().getColor(colorCode, null));


        //5️⃣ Обработка кликов
        // Клик по карточке
        holder.addressContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(list.get(holder.getAdapterPosition()));  //Вызываем listener.onClick(...) — метод из интерфейса. То есть внешний код (например, MainActivity) будет знать, что кто-то кликнул.
            }
        });
        //Долгий клик
        holder.addressContainer.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                listener.onLongClick(list.get(holder.getAdapterPosition()), holder.addressContainer);

                return false;
            }
        });

        // обрабатываем нажатие на номер телефона для звонка
        holder.textPhone.setText(list.get(position).getPhone());
        holder.textPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = list.get(position).getPhone();
                Intent intent = new Intent(Intent.ACTION_DIAL); //Открывает приложение звонков с уже подставленным номером.
                intent.setData(Uri.parse("tel:" + phoneNumber));
                context.startActivity(intent);
            }
        });
    }

    public Context getContext() {
        return context;
    }

    public void updatePositionsInDB() {
        RoomDB db = RoomDB.getInstance(getContext());
        new Thread(() -> {
            List<Address> currentList = getList();
            for (int i = 0; i < currentList.size(); i++) {
                Address a = currentList.get(i);
                a.setPosition(i);   // обновляем поле позиции
                db.mainDAO().update(a); // пишем в базу
            }
        }).start();
    }



    // 6️⃣ Метод выбора случайного цвета
    private int getRandomColor() {

        List<Integer> colorCode = new ArrayList<>(); // создаем список цветок
        colorCode.add(R.color.color1); // добавляем цвета созданные в color
        colorCode.add(R.color.color2);
        colorCode.add(R.color.color3);
        colorCode.add(R.color.color4);
        colorCode.add(R.color.color5);

        Random random = new Random();
        int randomColor = random.nextInt(colorCode.size());
        return colorCode.get(randomColor);
    }

    //Возвращает, сколько заметок в списке. RecyclerView понимает, сколько карточек ему нужно создать.
    @Override
    public int getItemCount() {
        return list.size();
    }

    public void deleteTask(Address address) { //deleteTask(Address address) — удаляет элемент из базы. пока не использую. надо подумать
        RoomDB db = RoomDB.getInstance(getContext());
        db.mainDAO().delete(address);


    }

    public void editItems(int position) { // пока пустой, можно будет использовать для редактирования.
    }

    public void setList(List<Address> newList) { //setList(List<Address> newList) — обновляет список и перерисовывает все карточки (notifyDataSetChanged()).
        this.list = newList;
        notifyDataSetChanged();
    }
}

//мини-контейнер, который держит ссылки на все элементы в твоей заметке (TextView, ImageView, и т.д.)
class AddressViewHolder extends RecyclerView.ViewHolder {

    // переменные для доступа к элементам в address_list.xml
    CardView addressContainer;
    TextView textAddress;
    TextView textPhone;
    TextView textComment;
    TextView textDate;
    ImageView imageCheck;

    public AddressViewHolder(@NonNull View itemView) {
        super(itemView);

        addressContainer = itemView.findViewById(R.id.address_container); // itemView — самый вид карточки (View)(см принимаемые агрументы метода), который  получили в onCreateViewHolder. Он представляет  разметку address_list.xml, и именно из него  можено доставать элементы.
        textAddress = itemView.findViewById(R.id.text_address);
        textPhone = itemView.findViewById(R.id.text_phone);
        textComment = itemView.findViewById(R.id.text_comment);
        textDate = itemView.findViewById(R.id.text_date);
        imageCheck = itemView.findViewById(R.id.image_check);
    }
}





