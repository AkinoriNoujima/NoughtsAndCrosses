package com.example.noughtsandcrosses;

import java.util.Arrays;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements android.view.View.OnClickListener {

	// 012
	// 345
	// 678
	private String[] board = { "-", "-", "-", "-", "-", "-", "-", "-", "-" };
	private String[] player = { "o", "x" };
	//ゲームターン数
	private int turn = 0;
	//ターン数と結果の表示用
	private TextView turnText;
	private TextView resultText;
	//9マスのボタンと再勝負ボタン
	private Button[] gridBtns;
	private Button retryBtn;

	/*-----------------------------------------------------------------------*/
	//onCreate
	/*-----------------------------------------------------------------------*/
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		//TextView
		turnText = (TextView) findViewById(R.id.turnText);
		resultText = (TextView) findViewById(R.id.resultText);

		//ButtonView
		gridBtns = new Button[] {
				(Button) findViewById(R.id.btn0),
				(Button) findViewById(R.id.btn1),
				(Button) findViewById(R.id.btn2),
				(Button) findViewById(R.id.btn3),
				(Button) findViewById(R.id.btn4),
				(Button) findViewById(R.id.btn5),
				(Button) findViewById(R.id.btn6),
				(Button) findViewById(R.id.btn7),
				(Button) findViewById(R.id.btn8),
		};
		retryBtn = (Button) findViewById(R.id.retryBtn);

		//各ボタンにOnClickListenerをセット
		for (int i = 0; i < gridBtns.length; i++) {
			gridBtns[i].setOnClickListener(this);
		}
		retryBtn.setOnClickListener(MainActivity.this);

		//最初にターンを表示
		turnText.setText("Player： " + player[turn % 2] + " のターン");
	}

	/*-----------------------------------------------------------------------*/
	@SuppressLint("NewApi")
	//onClick  各ボタンが押された時の処理
	/*-----------------------------------------------------------------------*/
	@Override
	public void onClick(View v) {
		//ここではif文を使うが授業で習ったswitch文で処理を分けてもOK
		if (Arrays.asList(gridBtns).contains(v)) {//押されたボタンがgridBtnsに含まれていたら
			int input = 0;
			//①、②のどちらでも押したボタンの情報を取得
			//①押されたボタンがxmlで定義しているtagを取得してint型に変換
			input = Integer.parseInt(v.getTag().toString());

			//②tagを使わずに押されたボタンの配列添字をinputに代入するやり方
//			for (int i = 0; i < gridBtns.length; i++) {
//				if (v.equals(gridBtns[i])) {
//					input = i;
//				}
//			}

			if (!board[input].equals("-")) {//ボタンの表示が"-"ではないなら
				//結果表示欄に選択済み情報出す
				resultText.setText("既に選択済みです");
			} else {//そうでなければ
				//結果表示欄はプレイ中
				resultText.setText("プレイ中");
				//配列boardの内容を変更
				board[input] = player[turn % 2];
				//ボタンの表示を"-"から○か☓に変更
				((Button) v).setText(player[turn % 2]);
				//プレーヤーによってボタン表示の色を変更(ここではプレーヤー○は青、プレーヤー☓は赤)
				if (turn % 2 == 0) {
					((Button) v).setTextColor(Color.rgb(0, 0, 255));
				} else {
					((Button) v).setTextColor(Color.rgb(255, 0, 0));
				}
				//決着が付いているかをチェック
				String winner = checkBoard(board);
				if (winner != null) {//winnerにnull以外が返ってきたら
					//決着済み
					//ひとまず9ますのボタンを押せなくする
					for (int i = 0; i < gridBtns.length; i++) {
						gridBtns[i].setOnClickListener(null);
					}
					//結果表示欄に勝負結果を表示
					resultText.setText("「Player： " + winner + " の勝ちです」");

					//いじくった戻り値で遊ぶなら
//					resultText.setText("「Player： " + winner.substring(0, 1) + " の勝ちです」");
					//揃ったラインの色を変えてみる
//					for (int i = 0; i < winner.length() - 1; i++) {
//						gridBtns[Integer.parseInt(winner.substring(i + 1, i + 2))].setBackgroundColor(Color.GREEN);
//					}

				} else if (turn < gridBtns.length - 1) {//ターン数を最大で引き分けになるまでは1足していく
					turn++;
					//その都度ターン表示欄を更新
					turnText.setText("Player： " + player[turn % 2] + " のターン");
				} else {//turnが一定値を超えると引き分け(ここでは9以上)
					for (int i = 0; i < gridBtns.length; i++) {
						gridBtns[i].setOnClickListener(null);
					}
					//結果表示欄を更新
					resultText.setText("「引き分けです」");
				}
			}
		} else {//押されたボタンがgridBtnsに含まれていない場合(ここでは再勝負ボタン)
			//すべての情報を初期値に戻して再勝負
			turn = 0;
			turnText.setText("Player： " + player[turn % 2] + " のターン");
			resultText.setText("プレイ中");
			for (int i = 0; i < gridBtns.length; i++) {
				gridBtns[i].setOnClickListener(this);
				gridBtns[i].setText("-");
				gridBtns[i].setTextColor(Color.rgb(0, 0, 0));
				board[i] = "-";
				//いじくった戻り値で遊ぶなら
//				gridBtns[i].setBackgroundColor(Color.rgb(204, 204, 204));
			}
		}
	}

	/*-----------------------------------------------------------------------*/
	//checkLine
	/*-----------------------------------------------------------------------*/
	/**
	 * １ラインについて、勝利条件判定を行います
	 * @param box1 判定対象ラインの1マス目
	 * @param box2 判定対象ラインの2マス目
	 * @param box3 判定対象ラインの3マス目
	 * @return 勝った方の文字列("o","x")、勝敗がついていない場合はnull
	 */
	private static String checkLine(String box1, String box2, String box3) {
		// ■ 1 ■ 
		if (box1.equals("o") && box2.equals("o") && box3.equals("o")) {
			return "o";
		}
		if (box1.equals("x") && box2.equals("x") && box3.equals("x")) {
			return "x";
		}
		return null;
	}

	/*-----------------------------------------------------------------------*/
	//checkBoard
	/*-----------------------------------------------------------------------*/
	/**
	 * ゲーム版全体について、勝利条件判定を行います
	 * @param board ゲーム版
	 * @return 勝った方の文字列("o","x")、勝敗がついていない場合はnull
	 */
	private static String checkBoard(String[] board) {
		int[][] checkLines = {
				{ 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 },
				{ 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 },
				{ 0, 4, 8 }, { 2, 4, 6 }
		};
		for (int i = 0; i < checkLines.length; i++) {
			int[] ck = checkLines[i];
			String result = checkLine(board[ck[0]], board[ck[1]], board[ck[2]]);
			if (result != null) {
				return result;

				//戻り値をいじくって遊ぶみる(揃ったラインの情報も持っていく)
//				return result + ck[0] + ck[1] + ck[2];
			}
		}
		return null;
	}
}
