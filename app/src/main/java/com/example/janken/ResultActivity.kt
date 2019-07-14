package com.example.janken

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    val gu = 0
    val choki = 1
    val pa = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        val id = intent.getIntExtra("MY_HAND" , 0)

        //myHandについて
        val myHand : Int
        myHand = when(id) {
            R.id.gu -> {
                myHandImage.setImageResource(R.drawable.gu)
                gu
            }
            R.id.choki -> {
                myHandImage.setImageResource(R.drawable.choki)
                choki
            }
            R.id.pa -> {
                myHandImage.setImageResource(R.drawable.pa)
                pa
            }
            else -> gu
        }

        //コンピュータの手を決める→getHandにて決まる（初期の手を負けて表示しておく）
        val comHand = getHand(myHand)
            when(myHand){
                gu -> comHandImage.setImageResource(R.drawable.com_choki)
                choki -> comHandImage.setImageResource(R.drawable.com_pa)
                pa -> comHandImage.setImageResource(R.drawable.com_gu)
            }

        //勝敗を判定する
        val gameResult = (comHand - myHand + 3) % 3

        //ディレイ処理のHandlerをインポート（後出しとして勝つ手に変更表示させる）
        Handler().postDelayed(Runnable {
            when(comHand){
                gu -> comHandImage.setImageResource(R.drawable.com_gu)
                choki -> comHandImage.setImageResource(R.drawable.com_choki)
                pa -> comHandImage.setImageResource(R.drawable.com_pa)
            }
            atodasiPon.setText(R.string.pon_text)
        }, 800)

        //勝敗メッセージはさらに遅めに表示する
        Handler().postDelayed(Runnable {
            when(gameResult){
                0 -> resultLabel.setText(R.string.result_draw)    //引き分け
                1 -> resultLabel.setText(R.string.result_win)    //勝った場合
                2 -> resultLabel.setText(R.string.result_lose)    //負けた場合
            }
        }, 1600)

        //画面のどこからでも戻れるようにしている
        backButton.setOnClickListener { finish()}
        screen.setOnClickListener { finish()}
        myHandImage.setOnClickListener { finish()}
        comHandImage.setOnClickListener { finish()}

        //セーブは後で
        saveData(myHand , comHand , gameResult)

        //セーブ後にカウントをする
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        loseCount.setText("${gameCount}回目")
    }

    //ゲームカウントについて、小さなデータの保存
    private fun saveData (myHand : Int , comHand : Int , gameResult : Int){
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT", 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT" ,0)
        val lastComHand = pref.getInt("LAST_COM_HAND" ,0)
        val lastGameResult = pref.getInt("GAME_RESULT" ,-1)

        val editor = pref.edit()
        editor.putInt("GAME_COUNT" , gameCount+1)
              .putInt("WINNING_STREAK_COUNT" ,
                      if (lastGameResult == 2 && gameResult == 2)
                      winningStreakCount + 1
              else
                  0)
              .putInt("LAST_MY_HAND" , myHand)
                    .putInt("LAST_COM_HAND" , comHand)
              .putInt("BEFORE_LAST_COM_HAND" , lastComHand)
              .putInt("GAME_RESULT" , gameResult)
              .apply()
    }

    //getHandメソッド（必ず勝利する手に変更する）
    private fun getHand(myHand : Int) : Int{
        val hand = (myHand + 2 ) % 3
        val pref = PreferenceManager.getDefaultSharedPreferences(this)
        val gameCount = pref.getInt("GAME_COUNT" , 0)
        val winningStreakCount = pref.getInt("WINNING_STREAK_COUNT" , 0)
        val lastMyHand = pref.getInt("LAST_MY_HAND" ,0)
        val lastComHand = pref.getInt("LAST_COM_HAND" , 0)
        val beforeLastComHand = pref.getInt("BEFORE_LAST_COM_HAND" , 0)
        val gameResult = pref.getInt("GAME_RESULT" , -1)

        return hand
    }
}