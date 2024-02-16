package ru.sample.duckapp

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody

//Да здравствует ассинхронщина
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import ru.sample.duckapp.domain.Duck
import ru.sample.duckapp.infra.Api
import ru.sample.duckapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var editTextCode: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextCode = findViewById(R.id.editTextCode)

        // Вызываем метод для загрузки и отображения случайной утки или утки по коду
        loadDuck()

        val btnNextDuck: Button = findViewById(R.id.btnNextDuck)

        // Назначаем обработчик события для кнопки
        btnNextDuck.setOnClickListener {
            loadDuck()
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
                        // Используем Picasso для отображения картинки
                        Picasso.get().load(duck.url).into(imageViewDuck)
                    }
                }
            }

            override fun onFailure(call: Call<Duck>, t: Throwable) {
                println(t)
                if (t is HttpException) {
                    println("HTTP-код: ${t.code()}")
                    println("Тело ответа: ${t.response()?.errorBody()?.string()}")
                }
            }
        })
    }


    private fun loadDuck() {
        val imageViewDuck: ImageView = findViewById(R.id.imageViewDuck)
        val code = editTextCode.text.toString()
        println(code)

        // Проверка валидности кода
        if (code.isNotEmpty() && isValidCode(code)) {
            // Вызываем API для получения утки по коду
            Api.ducksApi.getDuckByCode(code).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        // Успешно получили утку, отображаем её
                        val imageData = response.body()?.bytes()
                        if (imageData != null) {
                            imageViewDuck.setImageBitmap(BitmapFactory.decodeByteArray(imageData, 0, imageData.size))
                        }
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    println(t)
                    if (t is HttpException) {
                        println("HTTP-код: ${t.code()}")
                        println("Тело ответа: ${t.response()?.errorBody()?.string()}")
                    }
                }
            })

        } else if(!isValidCode(code)) {
            Toast.makeText(this, "Невалидный код", Toast.LENGTH_SHORT).show()

        }else {
           // Если строка с кожом пустая, загружаем случайную утку
            loadRandomDuck()
        }
    }

    private fun isValidCode(code: String): Boolean {
        if (code.isEmpty()) {
            return true
        }

        // Список допустимых значений
        val validCodes = setOf("100", "200", "301", "302", "400", "403", "404", "409", "413", "418", "420", "426", "429", "451", "500")

        // Проверка, что код принадлежит списку допустимых значений
        return validCodes.contains(code)
    }

}
