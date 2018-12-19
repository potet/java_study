import java.awt.Container;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;


/*
 * オセロの8方向の判定
 * どうやってターンを決めるかの判定
 * ひっくり返す処理
 * 駒の個数をカウント
 * 勝敗判定
 * パス判定
 */
public class Othello extends JFrame implements MouseListener,MouseMotionListener{
		//定数部
		public static final String BLACKTURN = "黒のターンです";
		public static final String WHITETURN = "白のターンです";

		//変数部
		private static JButton buttonArray[][];					//ボタン用の配列
		private static ImageIcon blackIcon;
		private static ImageIcon whiteIcon;
		private static ImageIcon boardIcon;
		private static ImageIcon yourIcon;
		private static ImageIcon myIcon;
		private Container c;

		int myTurn = 0;										//0(先攻),1(後攻)

		public Othello() {
			// ウィンドウを作成をする
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);//ウィンドウを閉じるときに、正しく閉じるように設定する
			this.setTitle("Gui Test");		//ウィンドウのタイトルを設定する
			this.setSize(420,450);			//ウィンドウのサイズを取得する
			c = this.getContentPane();		//フレームのペインを取得する
			c.setLayout(null);				//自動レイアウトの設定を行わない

			//アイコンの設定
			whiteIcon = new ImageIcon("White.jpg");
			blackIcon = new ImageIcon("Black.jpg");
			boardIcon = new ImageIcon("GreenFrame.jpg");
			yourIcon  = whiteIcon;
			myIcon    = blackIcon;

			//ボタンの生成
			buttonArray = new JButton[8][8];
			for(int i = 0; i<buttonArray.length; i++){
				for(int j = 0; j < buttonArray.length; j++) {
					buttonArray[j][i] = new JButton(boardIcon);					//ボタンにiconを貼り付ける
					c.add(buttonArray[j][i]);										//ペインにはりつける
					buttonArray[j][i].setBounds(i*50,10+j*50,50,50); 				//ボタンの大きさと位置を設定する(x座標,y座標,xの幅,yの幅)
					buttonArray[j][i].addMouseListener(this);						//ボタンをマウスで触ったときに反応する
					buttonArray[j][i].addMouseMotionListener(this);				//ボタンをマウスで動かそうとしたときに反応する
					buttonArray[j][i].setActionCommand(Integer.toString(j*8+i));	//ボタンに配列の情報を付加する(ネットワークを介してオブジェクトを識別するため)
				}
			}
			//ボード初期化
			boardInit();
		}

		public static void boardInit() {
			//ゲームスタート時ともう一度ゲームをするときに使う
			buttonArray[3][3].setIcon(blackIcon);
			buttonArray[4][4].setIcon(blackIcon);
			buttonArray[3][4].setIcon(whiteIcon);
			buttonArray[4][3].setIcon(whiteIcon);

			System.out.println("ゲームスタートです\n"+BLACKTURN);
		}

		//8方向判定
		public static boolean judegPieces(int y ,int x) {
			boolean judge = false;
			int	count = 0;
			//引数でボタンの場所取得
			for(int i = -1;i<2;i++){
				for(int j = -1;j<2;j++){
					//8方向判断
					count = flipButtons( y , x , j ,i);
					if( count > 0){
						//ひっくり返す処理
						for(int dy=y+j, dx=x+i, k=0; k< count; k++, dx+=j, dy+=i){
							//まだひっくり返す処理が上手くいかない
						  //ボタンの位置情報を作る
//							  int msgy = y + dy;
//							  int msgx = x + dx;
//							  int theArrayIndex = msgy*8 + msgx;
						  buttonArray[y][x].setIcon(myIcon);
						  buttonArray[dy][dx].setIcon(myIcon);
						}
						judge = true;
						break;
					}
				}
			}
			return judge;
		}

		//ひっくり返す判定
		public static int flipButtons(int y ,int x , int j , int i){
			int othelloCount = 0;

			for( int dy = y+j, dx = x+i;;dy += j, dx += i){
				//スイッチ文のほうがいい？後でリファクタリング」
				if( dy < 0 || dx < 0  || 7 < dy || 7 < dx){
					//場外だったら0を返す
					break;
				}

				//ここの条件は可変にする後で
				if(buttonArray[dy][dx].getIcon() == yourIcon){
					//yourIconなら、flipNumを1増やす(連鎖が続く)
					//駒のカウントしていないからバグる
//					buttonArray[y][x].setIcon(myIcon);
//					buttonArray[dy][dx].setIcon(myIcon);
					othelloCount++;

				}else if(buttonArray[dy][dx].getIcon() == myIcon){
					//myIconなら、この関数はflipNumを返す(連鎖ストップ)
					break;

				//ここの条件は可変にする後で
				}else if(buttonArray[dy][dx].getIcon() == boardIcon){
					//boardIconなら、この関数を0を返す(判定終了)
					break;

				}

			}

			return	othelloCount;
		}

	//メインルーチン
	public static void main(String[] args) {
		Othello gui = new Othello();
		gui.setVisible(true);
	}


	@Override
	public void mouseDragged(MouseEvent e) {
	}


	@Override
	public void mouseMoved(MouseEvent e) {//マウスがオブジェクト上で移動したときの処理
	}


	@Override
	public void mouseClicked(MouseEvent e) {//ボタンをクリックしたときの処理
		JButton theButton = (JButton)e.getComponent();//クリックしたオブジェクトを得る．型が違うのでキャストする
		int  theArrayIndex = Integer.parseInt(theButton.getActionCommand());//ボタンの配列の番号を取り出す

		System.out.println(theArrayIndex);
		Icon theIcon = theButton.getIcon();//theIconには、現在のボタンに設定されたアイコンが入る
		System.out.println(theIcon);//デバッグ（確認用）に，クリックしたアイコンの名前を出力する

		//駒ひっくり返す
		if( theIcon == boardIcon) {
			//judegPiecesを使う相手の色は,第三引数に相手の駒のいろ
			//ボタンの座標取得


			if( judegPieces(theArrayIndex / 8 ,theArrayIndex % 8) ){
				//置ける
				System.out.println("そこには配置できます");
			}else{
				//置けない
				System.out.println("そこには配置できません");
			}
		}

		//ターン交代処理
		if( myTurn == 0) {
			//judegPiecesを使う相手の色は,第三引数に相手の駒のいろ
			//白に交代
			yourIcon = blackIcon;
			myIcon = whiteIcon;
			myTurn = 1;
			System.out.println(WHITETURN);

		}else {
			//黒に交代
			yourIcon = whiteIcon;
			myIcon = blackIcon;
			myTurn = 0;
			System.out.println(BLACKTURN);
		}

		repaint();//画面のオブジェクトを描画し直す

	}


	@Override
	public void mousePressed(MouseEvent e) {//マウスでオブジェクトを押したときの処理（クリックとの違いに注意）
	}


	@Override
	public void mouseReleased(MouseEvent e) {//マウスで押していたオブジェクトを離したときの処理
	}


	@Override
	public void mouseEntered(MouseEvent e) {//マウスがオブジェクトに入ったときの処理
		/*JButton theButton = (JButton)e.getComponent();
		Icon theIcon = theButton.getIcon();
		System.out.println(theIcon);
		*/
	}


	@Override
	public void mouseExited(MouseEvent e) {//マウスがオブジェクトから出たときの処理
	}

}
