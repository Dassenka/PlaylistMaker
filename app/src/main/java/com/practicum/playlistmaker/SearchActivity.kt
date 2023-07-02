package com.practicum.playlistmaker

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    private var inputText: String = ""
    private val baseUrl = "http://itunes.apple.com"

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackSearchApi = retrofit.create(TrackSearchAPI::class.java)

    private lateinit var inputEditText: EditText
    private lateinit var trackRecyclerView: RecyclerView
    private lateinit var placeholderMessageError: LinearLayout
    private lateinit var buttonUpdate: Button
    private lateinit var searchError: View
    private lateinit var internetError: View
    private lateinit var errorMessageText: TextView
    private lateinit var searchHistoryLay: LinearLayout
    private lateinit var searchHistoryRecyclerView: RecyclerView
    private lateinit var buttonClearHistory: Button
    private lateinit var progressBar: ProgressBar
    private var trackSet = ArrayList<Track>()
    private val adapter = TrackAdapter(trackSet)
    private var historyList = ArrayList<Track>()
    private val adapterHistory = TrackAdapter(historyList)
    private var searchHistory = SearchHistory(historyList)

    private val searchRunnable = Runnable { searchTrack() } //поиск через каждые 2 сек после ввода(Debounce)
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        val back: LinearLayout = findViewById(R.id.backToMainActivity)

        back.setOnClickListener {
            finish()
        }

        inputEditText = findViewById(R.id.InputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)

        clearButton.setOnClickListener {
            inputEditText.setText("") // Очищаем поле ввода
            val inputMethodManager =
                getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(
                inputEditText.windowToken,
                0
            ) //убираем клавиатуру
            trackSet.clear() // очищаем список песен
            placeholderMessageError.visibility = View.GONE
            progressBar.visibility = View.GONE
            adapter.notifyDataSetChanged() // очищвем RecyclerView
        }

        inputEditText.setOnEditorActionListener { _, actionId, _ -> //заменена кнопки переноса в клавиатуре на кнопку Done
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                searchTrack()
                true
            }
            false
        }

        searchHistoryLay = findViewById(R.id.search_history)
        inputEditText.setOnFocusChangeListener { _, hasFocus -> // отслеживание состояния фокуса для отображения истории поиска
            historyList =
                searchHistory.createTrackListListFromJson()// перезапись истории из файла, на слуйчай если это первый поиск и не выходили из активити
            if (hasFocus && inputEditText.text.isEmpty() && historyList.isNotEmpty()) {
                showHistoryList()
            } else searchHistoryLay.visibility = View.GONE
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                inputText = inputEditText.text.toString()
                clearButton.visibility = clearButtonVisibility(s)
                historyList =
                    searchHistory.createTrackListListFromJson()// перезапись истории из файла, на слуйчай если это первый поиск и не выходили из активити

                searchDebounce()
                if (inputEditText.hasFocus() && s?.isEmpty() == true && historyList.isNotEmpty()) {
                    showHistoryList()
                } else {
                    searchHistoryLay.visibility = View.GONE
                    progressBar.visibility = View.GONE
                    trackRecyclerView.visibility = View.GONE
                    placeholderMessageError.visibility = View.GONE
                    searchError.visibility = View.GONE
                    internetError.visibility = View.GONE
                    buttonUpdate.visibility = View.GONE
                } // добавляем отображение/скрытие истории поиска если пользователь вручную стер запрос

                trackRecyclerView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true)
                        View.GONE else View.VISIBLE // скрываем список найденных трэков
            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

        trackRecyclerView = findViewById(R.id.recyclerView)
        adapter.track = trackSet
        trackRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecyclerView.adapter = adapter

        searchHistoryRecyclerView = findViewById(R.id.recyclerView_search_history)
        searchHistoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        placeholderMessageError = findViewById(R.id.placeholder_message_error)
        searchError = findViewById(R.id.search_error)
        internetError = findViewById(R.id.internet_error)
        buttonUpdate = findViewById(R.id.button_update)
        errorMessageText = findViewById(R.id.error_message)

        buttonUpdate.setOnClickListener {
            searchTrack()
        }

        buttonClearHistory = findViewById(R.id.button_clear_history)

        buttonClearHistory.setOnClickListener {
            searchHistoryLay.visibility = View.GONE
            searchHistory.clearHistory()
            historyList.clear()
        }

        progressBar= findViewById(R.id.progressBar)
    }

    private fun showHistoryList() {
        adapterHistory.track = historyList
        searchHistoryRecyclerView.adapter = adapterHistory
        adapterHistory.notifyDataSetChanged()
        searchHistoryLay.visibility = View.VISIBLE
    }

    private fun searchTrack() {
        if (inputEditText.text.isNotEmpty()) {

            // Меняем видимость элементов перед выполнением запроса
            progressBar.visibility = View.VISIBLE
            trackRecyclerView.visibility = View.GONE
            placeholderMessageError.visibility = View.GONE
            searchError.visibility = View.GONE
            internetError.visibility = View.GONE
            buttonUpdate.visibility = View.GONE

            trackSearchApi.search(inputEditText.text.toString()).enqueue(object :
                Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    if (response.code() == 200) {
                        trackSet.clear()
                        if (response.body()?.results?.isNotEmpty() == true) {
                            progressBar.visibility = View.GONE // Прячем ProgressBar после успешного выполнения запроса
                            trackRecyclerView.visibility = View.VISIBLE
                            trackSet.addAll(response.body()?.results!!)
                            adapter.notifyDataSetChanged()
                        }
                        if (trackSet.isEmpty()) {
                            progressBar.visibility = View.GONE
                            placeholderMessageError.visibility = View.VISIBLE
                            searchError.visibility = View.VISIBLE
                            errorMessageText.text = getString(R.string.error_message)
                        }
                    }
                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    progressBar.visibility = View.GONE // Прячем ProgressBar после выполнения запроса с ошибкой
                    placeholderMessageError.visibility = View.VISIBLE
                    internetError.visibility = View.VISIBLE
                    buttonUpdate.visibility = View.VISIBLE
                    errorMessageText.text = getString(R.string.internet_error)
                }
            })
        }
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(INPUT_EDIT_TEXT, inputText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        inputText = savedInstanceState.getString(INPUT_EDIT_TEXT).toString()
    }

    private fun searchDebounce() { //поиск через каждые 2 сек после ввода
        handler.removeCallbacks(searchRunnable)
        handler.postDelayed(searchRunnable, SEARCH_DEBOUNCE_DELAY)
    }

    companion object {
        private const val INPUT_EDIT_TEXT = "INPUT_EDIT_TEXT"
        const val PLAY_TRACK = "PLAY_TRACK"

        private const val SEARCH_DEBOUNCE_DELAY = 2000L //отслеживание строки поля ввода - две секунды с момента последнего его изменения

        //функция вызова PlayerActivity для TrackAdapter
        fun startActivity(track: Track, context: Context) {
                val playerIntent = Intent(context, PlayerActivity::class.java)
                playerIntent.putExtra(PLAY_TRACK, track) // передаем track в новую активити
                context.startActivity(playerIntent)
        }
    }
}

