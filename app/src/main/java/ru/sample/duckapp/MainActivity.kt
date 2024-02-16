package ru.sample.duckapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import com.squareup.picasso.Picasso

//Да здравствует ассинхронщина
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api
import ru.sample.duckapp.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Вызываем метод для загрузки и отображения случайной утки
        loadRandomDuck()

        val btnNextDuck: Button = findViewById(R.id.btnNextDuck)

        // Назначаем обработчик события для кнопки
        btnNextDuck.setOnClickListener {
            loadRandomDuck()
        }
    }

    private fun loadRandomDuck() {
        val imageViewDuck: ImageView = findViewById(R.id.imageViewDuck)
        // Вызываем API для получения случайной утки
        Api.ducksApi.getRandomDuck().enqueue(object : Callback<Duck> {
            override fun onResponse(call: Call<Duck>, response: Response<Duck>) {
                if (response.isSuccessful) {
                    // получили утку, отображаем её
                    val duck = response.body()

                    // Проверка, что duck не null, и url не пустой
                    if (duck != null && duck.url.isNotEmpty()) {
                        // Используем Picasso для загрузки и отображения картинки
                        Picasso.get().load(duck.url).into(imageViewDuck)
                    } else {
                        // Если утка null или у неё нет URL, то отображаем сообщение об ошибке
                        // TODO: Добавить код об ошибке (потом буду его использовать при отсутствии утки)
                    }
                }
            }

            override fun onFailure(call: Call<Duck>, t: Throwable) {
                // Произошла ошибка при запросе к API ОЙ КАК ТАК??
                // TODO: Обработка ошибки (выкидываем что автор криворукий)
            }
        })
    }
}