package com.practicum.playlistmaker.search.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.model.Track
import com.practicum.playlistmaker.player.ui.PlayerActivity

class SearchActivity : AppCompatActivity() {

    private val adapter = TrackAdapter(
        object : TrackAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                startActivity(track)
            }
        }
    )

    private val adapterHistory = TrackAdapter(
        object : TrackAdapter.TrackClickListener {
            override fun onTrackClick(track: Track) {
                startActivity(track)
            }
        }
    )

    private lateinit var backButton: LinearLayout
    private lateinit var clearButton: ImageView
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
    private lateinit var textWatcher: TextWatcher
    private lateinit var viewModel: SearchActivityViewModel
    private var inputText: String = ""
    private val handler = Handler(Looper.getMainLooper())
    private var isClickAllowed = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        progressBar = findViewById(R.id.progressBar)
        backButton = findViewById(R.id.backToMainActivity)
        inputEditText = findViewById(R.id.InputEditText)
        clearButton = findViewById(R.id.clearIcon)
        searchHistoryLay = findViewById(R.id.search_history)
        placeholderMessageError = findViewById(R.id.placeholder_message_error)
        searchError = findViewById(R.id.search_error)
        internetError = findViewById(R.id.internet_error)
        buttonUpdate = findViewById(R.id.button_update)
        errorMessageText = findViewById(R.id.error_message)
        trackRecyclerView = findViewById(R.id.recyclerView)
        searchHistoryRecyclerView = findViewById(R.id.recyclerView_search_history)
        buttonClearHistory = findViewById(R.id.button_clear_history)

        //Создаем ViewModel
        viewModel = ViewModelProvider(
            this,
            SearchActivityViewModel.getViewModelFactory()
        )[SearchActivityViewModel::class.java]

        //Подписываемся на состояние ViewModel
        viewModel.observeState().observe(this) {
            render(it)
        }

        //Кнопка назад
        backButton.setOnClickListener {
            finish()
        }

        //список найденных трэков
        trackRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        trackRecyclerView.adapter = adapter

        //список трэков из истории прослушанных
        searchHistoryRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        searchHistoryRecyclerView.adapter = adapterHistory

        //Очистка поля ввода
        clearButton.setOnClickListener {
            inputEditText.setText("") // Очищаем поле ввода
            hideKeyBoard() //Скрываем клавиатуру
            adapter.track.clear() // очищаем список песен
            placeholderMessageError.visibility = View.GONE
            progressBar.visibility = View.GONE
            adapter.notifyDataSetChanged() // обновляем RecyclerView
        }

        // отслеживание состояния фокуса для отображения истории поиска
        inputEditText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus && inputEditText.text.isEmpty()) {
                viewModel.getHistoryList()
            } else searchHistoryLay.visibility = View.GONE
        }

        textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.getHistoryList()
                placeholderMessageError.visibility = View.GONE
                clearButton.visibility = clearButtonVisibility(s)
                viewModel.searchDebounce(changedText = s?.toString() ?: inputText, false)

                trackRecyclerView.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true)
                        View.GONE else View.VISIBLE // скрываем список найденных трэков

                searchHistoryLay.visibility =
                    if (inputEditText.hasFocus() && s?.isEmpty() == true)
                        View.VISIBLE else View.GONE

            }

            override fun afterTextChanged(s: Editable?) {
                // empty
            }
        }

        textWatcher?.let { inputEditText.addTextChangedListener(it) }

        // кнопка обновления при ошибке интернета
        buttonUpdate.setOnClickListener {
            viewModel.searchDebounce(inputEditText.text.toString(), true)
        }

        buttonClearHistory.setOnClickListener {
            searchHistoryLay.visibility = View.GONE
            viewModel.clearHistory()
            adapterHistory.track.clear()
            adapterHistory.notifyDataSetChanged()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        textWatcher?.let { inputEditText.removeTextChangedListener(it) }
    }

    // видимость крестика - очистки строки
    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    //функция скрытия клавиатуры
    private fun hideKeyBoard() {
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(inputEditText.windowToken, 0)
    }

    private fun render(state: SearchActivityScreenState) {
        when (state) {
            is SearchActivityScreenState.Content -> showContent(state.tracks)
            is SearchActivityScreenState.Empty -> showEmpty()
            is SearchActivityScreenState.Error -> showError()
            is SearchActivityScreenState.Loading -> showLoading()
            is SearchActivityScreenState.ContentHistoryList -> showHistoryList(state.historyList)
            is SearchActivityScreenState.EmptyHistoryList -> showEmptyHistoryList()
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        searchHistoryLay.visibility = View.GONE
        trackRecyclerView.visibility = View.GONE
        placeholderMessageError.visibility = View.GONE
    }

    private fun showError() {
        searchHistoryLay.visibility = View.GONE
        progressBar.visibility = View.GONE
        placeholderMessageError.visibility = View.VISIBLE
        searchError.visibility = View.GONE
        buttonUpdate.visibility = View.VISIBLE
        internetError.visibility = View.VISIBLE
        errorMessageText.text = getString(R.string.internet_error)
        hideKeyBoard() //Скрываем клавиатуру
    }

    private fun showEmpty() {
        searchHistoryLay.visibility = View.GONE
        progressBar.visibility = View.GONE
        placeholderMessageError.visibility = View.VISIBLE
        internetError.visibility = View.GONE
        searchError.visibility = View.VISIBLE
        errorMessageText.text = getString(R.string.error_message)
        buttonUpdate.visibility = View.GONE
        hideKeyBoard() //Скрываем клавиатуру
    }

    private fun showContent(foundTracks: List<Track>) {
        progressBar.visibility = View.GONE
        hideKeyBoard() //Скрываем клавиатуру
        placeholderMessageError.visibility = View.GONE
        trackRecyclerView.visibility = View.VISIBLE
        adapter.track.clear()
        adapter.track.addAll(foundTracks)
        adapter.notifyDataSetChanged()
    }

    private fun showHistoryList(historyList: List<Track>) {
        searchHistoryLay.visibility = View.VISIBLE
        adapterHistory.track.clear()
        adapterHistory.track.addAll(historyList)
        adapterHistory.notifyDataSetChanged()
    }

    private fun showEmptyHistoryList() {
        searchHistoryLay.visibility = View.GONE

    }

    //ограничение нажатия на элементы списка не чаще одного раза в секунду
    fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, CLICK_DEBOUNCE_DELAY)
        }
        return current
    }

    //функция вызова PlayerActivity для TrackAdapter
    fun startActivity(track: Track) {
        if (clickDebounce()) {
            viewModel.addTrackInHistory(track) //функция добавления трэка в историю
            val intent = Intent(this@SearchActivity, PlayerActivity::class.java)
            intent.putExtra(PLAY_TRACK, track)
            startActivity(intent)
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
        const val PLAY_TRACK = "PLAY_TRACK"
    }
}