package com.example.onetrysclada
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

//
//Файлы item_user.xml и table.xml не связаны напрямую, но они работают вместе для
// отображения данных в интерфейсе вашего приложения. Вот как они взаимодействуют:
//item_user.xml: Этот файл описывает макет для отдельного элемента пользователя в
// списке, который представляет одну строку в RecyclerView. Здесь у вас, например,
// есть поля для имени и электронной почты пользователя. table.xml (или другой
// основной макет с RecyclerView): Это контейнер для отображения списка элементов
// (например, пользователей), в котором размещается RecyclerView, загружающий
// несколько элементов из item_user.xml.
//Связь через код:
//В вашем адаптере для RecyclerView вы будете "надувать" (inflate) макет item_user.xml
// для каждого элемента списка, чтобы отобразить данные пользователя. Сам RecyclerView,
// который находится, например, в table.xml, будет показывать все эти элементы как
// прокручиваемый список.

class DataAdapter(private val dataList: List<User>) : RecyclerView.Adapter<DataAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userId: TextView = itemView.findViewById(R.id.userId)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val userLogin: TextView = itemView.findViewById(R.id.userLogin)
        val userPassword: TextView = itemView.findViewById(R.id.userPassword)
        val userEmail: TextView = itemView.findViewById(R.id.userEmail)
        val userPhoneNumber: TextView = itemView.findViewById(R.id.userPhoneNumber)
        val userRole: TextView = itemView.findViewById(R.id.userRole)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_user, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = dataList[position]
        holder.userId.text = user.user_id.toString()  // Пример, замените на реальный ID
        holder.userName.text = user.user_name
        holder.userLogin.text = user.login
        holder.userPassword.text = user.password
        holder.userEmail.text = user.email
        holder.userPhoneNumber.text = user.phone_number
        holder.userRole.text = user.role
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}

