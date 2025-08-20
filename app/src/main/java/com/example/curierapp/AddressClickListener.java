package com.example.curierapp;


import androidx.cardview.widget.CardView;

import com.example.curierapp.Models.Address;

// interface в Java — это контракт, который говорит:
//«Если кто-то реализует этот интерфейс — он обязан реализовать ВСЕ его методы.»
//В млем случае, интерфейс описывает, что должно происходить при нажатии на элемент списка адресов (Address).
public interface AddressClickListener {

    public void onClick(Address address); //Этот метод будет вызываться, когда пользователь нажимает (обычным кликом) на заметку.

    public void onLongClick(Address address, CardView cardView); //Этот метод будет вызываться, когда пользователь нажимает и держит на элементе (долгое нажатие). Обычно используют для удаления, выделения или других «долгих» действий. CardView cardView позволяет управлять отдельной карточкой адреса(внешним видом)
}
