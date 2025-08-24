package com.example.curierapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.curierapp.Adapter.AddressListAdapter;
import com.example.curierapp.DataBase.RoomDB;
import com.example.curierapp.Models.Address;

import java.util.ArrayList;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

// 1️⃣ TouchHelper — это класс, который расширяет ItemTouchHelper.SimpleCallback
//и добавляет функционал свайпа (влево и вправо) для элементов RecyclerView.
public class TouchHelper extends ItemTouchHelper.SimpleCallback {

    AddressListAdapter adapter; // adapter — нужен, чтобы работать со списком (получить элемент, вызвать удаление или редактирование).
    Context context; //context — нужен для доступа к ресурсам (цвета, иконки) и создания диалогов.
    RecyclerView recyclerView;

    List<Address> address = new ArrayList<>();
    boolean selectedAddress = false;

    // Конструктор
    public TouchHelper(AddressListAdapter adapter, Context context) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT); //super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) — говорит, что перетаскивание (drag & drop) выключено (0),  а свайпы разрешены в обе стороны.
        this.adapter = adapter;
        this.context = context;
    }

    //Этот метод нужен для перетаскивания элементов (drag & drop).
    //Нам это пока не нужно, поэтому возвращаем false.
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    // метод обработки свайпа.
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        final int position = viewHolder.getAdapterPosition(); //position — позиция элемента, по которому был свайп.

        if (direction == ItemTouchHelper.RIGHT) { // Свайп вправо (удаление) (ItemTouchHelper.RIGHT):
            AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext()); //Открываем AlertDialog с вопросом.
            builder.setTitle("Delete Address"); // Показываем диалог с вопросом: "Удалить задачу?"
            builder.setMessage("Are you sure?"); // Показываем диалог с вопросом: "отмена"

            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() { // Если пользователь нажал "Yes" — удаляем задачу из БД и из списка
                @Override
                public void onClick(DialogInterface dialog, int which) {

/*                    adapter.deleteTask(adapter.getList().get(position)); // метод в AddressListAdepter будет
                    // Сообщаем адаптеру, что элемент удалился
                    adapter.notifyItemRemoved(position);
                }
            });*/
                   final Address addressToDelete = adapter.getList().get(position);

                    // Запускаем отдельный поток (new Thread(...)) — так база не блокирует UI.
                    new Thread(() -> {
                            // Удаляем его через RoomDB.
                            RoomDB db = RoomDB.getInstance(adapter.getContext());
                            db.mainDAO().delete(addressToDelete);

//                            // Получаем обновлённый список из базы
//                            List<Address> updatedList = db.mainDAO().getAll();
                            // Обновляем список адаптера на главном потоке
                            ((Activity) adapter.getContext()).runOnUiThread(() -> {
                                adapter.getList().remove(addressToDelete); // удаляем из текущего списка
                                adapter.notifyItemRemoved(position); // уведомляем RecyclerView
                            });
                    }).start();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // Если нажал "Cancel" — отменяем свайп и возвращаем элемент в список (иначе он исчезнет с экрана)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(position);
                }
            });
            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

            } else { //Если свайп был в левую сторону
            final Address addressToCheck = adapter.getList().get(position); //final используется, потому что мы будем использовать эту переменную внутри вложенного потока (new Thread()). Мы берём конкретный объект Address с именем addressToCheck, который нужно отметить/снять отметку. adapter.getList() — это список всех адресов, который используется в RecyclerView. position — это позиция элемента, на который сделали свайп

            // переключаем значение isChecked
            addressToCheck.setChecked(!addressToCheck.isChecked()); // Переключаем значение флага isChecked. ! — это оператор логического отрицания, он инвертирует true/false.

            // обновляем базу в отдельном потоке
            new Thread(() -> { //Мы создаём новый поток, чтобы не тормозить главный UI-поток
                RoomDB db = RoomDB.getInstance(adapter.getContext()); //получаем экземпляр базы данных Room (RoomDB.getInstance)
                db.mainDAO().update(addressToCheck); //вызываем update(), чтобы записать новые данные в базу.



                // обновляем UI на главном потоке
                ((Activity) adapter.getContext()).runOnUiThread(() -> { //После обновления базы данных нужно обновить UI. Так как мы в другом потоке, делаем это через runOnUiThread()
                    adapter.setList(db.mainDAO().getAll()); /// метод setList() нужно реализовать в адаптере, чтобы заменить список (реализован)
                    adapter.notifyDataSetChanged(); //// перерисовываем RecyclerView

                    String msg = addressToCheck.isChecked() ? "Адрес отмечен ✅" : "Отметка снята ⭕"; //Потом создаём сообщение Toast:
                    Toast.makeText(adapter.getContext(), msg, Toast.LENGTH_SHORT).show();

                });
            }).start();
        /*    AlertDialog.Builder builder = new AlertDialog.Builder(adapter.getContext()); //Открываем AlertDialog с вопросом.
            builder.setTitle("Edit Address"); // Показываем диалог с вопросом: "Редактировать задачу?"
            builder.setMessage("Are you sure?"); // Показываем диалог с вопросом: "отмена"
//            adapter.editItems(position); // метод в AddressListAdepter будет

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() { // Если нажал "Cancel" — отменяем свайп и возвращаем элемент в список (иначе он исчезнет с экрана)
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    adapter.notifyItemChanged(position);
                }
            });*/
/*            AlertDialog dialog = builder.create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();*/
        }
    }

    // метод срабатывает каждый раз, когда пользователь тянет (свайпает) элемент в списке. Здесь мы используем RecyclerViewSwipeDecorator, чтобы нарисовать:
    //фон зелёный + иконка редактирования при свайпе влево;
    //фон красный + иконка удаления при свайпе вправо.
    //super.onChildDraw(...) обязательно вызывать, чтобы элемент реально двигался, а не просто рисовалась картинка.
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
// Swipe влево (например, редактирование)
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.green)) //задаёт фон, который появляется при свайпе влево (например, зелёный цвет).
                .addSwipeLeftActionIcon(R.drawable.pinned) // рисует иконку поверх этого фона (например, иконку редактирования
                .create()
                .decorate();
//Swipe вправо (например, удаление)
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeRightBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.red)) //задаёт фон при свайпе вправо
                .addSwipeRightActionIcon(R.drawable.delete_icon) //добавляет иконку
                .create()
                .decorate();
// Почему super.onChildDraw(...) в конце?
//Это вызов родительского метода, который физически двигает сам ViewHolder (то есть карточку вправо/влево) — а наша декорация как бы просто подрисовывается на фоне.
//
//Если бы не было super, свайп бы вообще не двигал карточку — только украшения, а сам элемент остался бы на месте.
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

/*//**************************************************************************************************
//************************************Маленький бонус***********************************************
//**********************Можно немного оптимизировать код — чтобы не создавать два Builder-а подряд, а делать всё в одном. Будет проще и быстрее ✌️:***********************************************
        new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                .addSwipeLeftBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.green))
                .addSwipeLeftActionIcon(R.drawable.edit_icon)
                .addSwipeRightBackgroundColor(ContextCompat.getColor(adapter.getContext(), R.color.red))
                .addSwipeRightActionIcon(R.drawable.delete_icon)
                .create()
                .decorate();

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

//**************************************************************************************************
//**************************************************************************************************
    }*/
    }
}

