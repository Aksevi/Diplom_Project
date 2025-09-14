package com.example.curierapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.curierapp.Adapter.AddressListAdapter;
import com.example.curierapp.DataBase.RoomDB;
import com.example.curierapp.Models.Address;

import java.util.ArrayList;
import java.util.List;

//это главный экран приложения. Он наследуется от AppCompatActivity, что даёт доступ ко всем фишкам Android-активности.
// Этот код — это каркас главного экрана приложения для хранения адресов. Он:  Подключает базу данных Room.  Загружает список адресов.  Показывает их в RecyclerView через адаптер.  Обрабатывает добавление, переход на карту, статистику и свайпы
public class MainActivity extends AppCompatActivity {

    RecyclerView addressRecyclerView; //это список на экране (RecyclerView), где будут все адреса.
    Button btnAdd; // кнопки
    Button btnMap;
    Button btnStatistics;

    private AddressListAdapter addressListAdapter; //адаптер, который будет соединять список данных и RecyclerView.
    RoomDB dataBase; // объект базы данных ROOM
    private List<Address> addressList = new ArrayList<>(); // список адресов, который мы получаем из базы.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Подключаем элементы интерфейса с экрана: список и кнопки с базой данных.
        addressRecyclerView = findViewById(R.id.address_RecyclerView);
        btnAdd = findViewById(R.id.btn_Add);
        btnMap = findViewById(R.id.btn_Map);
        btnStatistics = findViewById(R.id.btn_Statistics);

        dataBase = RoomDB.getInstance(this); //Подключаемся к базе данных.
        addressList = dataBase.mainDAO().getAllSorted(); //Загружаем все адреса из базы.  getAllSorted() получает отсортированные по позиции


        // Создаём адаптер и передаём:
        addressListAdapter = new AddressListAdapter(MainActivity.this, addressList, addressClickListener); //контекст (MainActivity.this), слушатель кликов (addressClickListener), список (addressList)

        addressRecyclerView.setAdapter(addressListAdapter); //Устанавливаем адаптер в список.

        updateRecycler(addressList); // Обновляем список на экране с помощью метода updateRecycler ( ниже).


        // Подключаем TouchHelper после того, как адаптер создан. TouchHelper — отдельный класс, который обрабатывает свайпы.
        //Swipe-события - Подключается помощник свайпа — можно тянуть задачи влево/вправо  для удаления или редактирования. сами свайпы у нас реализованы в RecycletViewTouchHelper.java
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new TouchHelper(addressListAdapter, this));
        itemTouchHelper.attachToRecyclerView(addressRecyclerView); //прикрепляем обработчик свайпов к списку.


        //Запускаем AddAddressActivity (добавление адреса) и ждём результат.
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddAddressActivity.class);
                startActivityFromChild(getParent(), intent, 101);

            }
        });

        // жмем кнопку карта
        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
            }
        });
        // жмем кнопку статистика
        btnStatistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        // Заново загружаем список из базы
       List<Address> newList = dataBase.mainDAO().getAllSorted();
        addressListAdapter.setList(newList); // теперь список будет перерисован

    }

    //Интерфейс обработки кликов. Это анонимная реализация интерфейса AddressClickListener. ее создаем чтобы работал метод private void updateRecycler(List<Address> address). Создавать лучше после того как в методе все прописали
    private final AddressClickListener addressClickListener = new AddressClickListener() {
        @Override
        public void onClick(Address address) {
            Intent intent = new Intent(MainActivity.this, AddAddressActivity.class);
            intent.putExtra("old_address", address);
            startActivityFromChild(getParent(), intent, 102);
        }

        @Override
        public void onLongClick(Address address, CardView cardView) {

        }
    };

    //метод для обновления элементов
    private void updateRecycler(List<Address> address) {
        addressRecyclerView.setHasFixedSize(true); // фиксируем размер элемента. setHasFixedSize(true) — ускоряет работу списка, если размеры элементов не меняются.
//        addressRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(1, LinearLayoutManager.VERTICAL)); // setLayoutManager(...) — говорит, как отображать элементы: StaggeredGridLayoutManager — отображает заметки в виде плиток, как Pinterest. 2 — это две колонки.

        addressRecyclerView.setLayoutManager(new LinearLayoutManager(this)); // new LinearLayoutManager(this) — говорит, как отображать элементы: здесь будет на всю ширину одна под другой

//        addressListAdapter = new AddressListAdapter(MainActivity.this, address, addressClickListener); //Создаём адаптер notesListAdapter, который отображает каждую заметку. addressClickListener - это будет красным поэтому выше создали экземпляр интерфейса
//        addressRecyclerView.setAdapter(addressListAdapter); //Устанавливаем его (адаптер) в addressRecyclerView.  addressRecyclerView это мой RecyclerView из activity_main.xml
        addressListAdapter.setList(address);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // здесь обрабатывается условие если короткий клик по карточке (метод public void onClick(Notes notes)) то есть приходит код 102 и тогда происходит следующее
        if (requestCode == 102) { //Для редактирования (requestCode == 102):
            if (resultCode == MainActivity.RESULT_OK) {
                Address newAddress = (Address) data.getSerializableExtra("address"); // Из Intent забираем объект Notes, который мы передавали из другой Activity.
//                dataBase.mainDAO().update(newAddress.getAddress(), newAddress.getPhone(), newAddress.getComment(), newAddress.getDate(), newAddress.isChecked(), newAddress.getId()); //Обновляем существующую запись в базе по ID.
                dataBase.mainDAO().update(newAddress);
                //Обновление списка на экране
                addressList.clear();
                addressList.addAll(dataBase.mainDAO().getAllSorted());

                addressListAdapter.notifyDataSetChanged();

            }

        } else { //Для добавления новой заметки (else):

            if (resultCode == MainActivity.RESULT_OK) {
                Address newAddress = (Address) data.getSerializableExtra("address"); // Забираем объект Notes, который передали из другой Activity через Intent.  (Условие — класс Notes должен реализовывать Serializable.)
                dataBase.mainDAO().insert(newAddress); // Вставляем новую запись в базу.
                // Обновление списка на экране
                addressList.clear();
                addressList.addAll(dataBase.mainDAO().getAll());

                addressListAdapter.notifyDataSetChanged();
            }
        }
    }
}



