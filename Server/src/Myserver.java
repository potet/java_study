import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

//スレッド部(各クライアンントに応じて)
class ClientProcThread extends Thread{
	private int number;
	private Socket incoming;
	private InputStreamReader myIsr;
	private BufferedReader myIn;
	private PrintWriter myOut;
	private String myName;

	//コンストラクタ
	public ClientProcThread(int n,Socket i, InputStreamReader isr, BufferedReader in, PrintWriter out) {
		number = n;
		incoming = i;
		myIsr = isr;
		myIn = in;
		myOut = out;
	}

	//スレッド処理部
	public void run(){
		try{
			myOut.println("Hello, client No." + number + "! Enter 'Bye' to exit.");//初回だけ呼ばれる

			myName = myIn.readLine();//初めて接続したときの1行目の名前

			while(true) {
				String str = myIn.readLine();
				System.out.println("Received from client No."+number+"("+myName+"), Messages: "+str);

				if(str != null) {
					if(str.toUpperCase().equals("BYE")) {
						myOut.println("Good bye!");
						break;
					}
					Myserver.SendAll(str,myName);
				}
			}

		}catch(Exception e) {
			System.out.println("Disconnect from client No."+number+"("+myName+")");
			Myserver.SetFlag(number, false);//接続が切れたのでフラグを下げる
		}
	}
}

public class Myserver {
	private static int maxConnection=100;//最大接続数
	private static Socket[] incoming;//受付用のソケット
	private static boolean[] flag;//接続中かどうかのフラグ
	private static InputStreamReader[] isr;//入力ストリーム用の配列
	private static BufferedReader[] in;//バッファリングをによりテキスト読み込み用の配列
	private static PrintWriter[] out;//出力ストリーム用の配列
	private static ClientProcThread[] myClientProcThread;//スレッド用の配列
	private static int member;//接続しているメンバーの数

	//全員にメッセージを送る
	public static void SendAll(String str, String myName){
		//送られた来たメッセージを接続している全員に配る
		for(int i=1;i<=member;i++){
			if(flag[i] == true){
				out[i].println(str);
				out[i].flush();//バッファをはき出す＝＞バッファにある全てのデータをすぐに送信する
				System.out.println("Send messages to client No."+i);
			}
		}
	}

	//フラグの設定を行う
	public static void SetFlag(int n, boolean value){
		flag[n] = value;
	}

	//mainプログラム
	public static void main(String[] args) {
		//必要な配列を確保する
		incoming = new Socket[maxConnection];
		flag = new boolean[maxConnection];
		isr = new InputStreamReader[maxConnection];
		in = new BufferedReader[maxConnection];
		out = new PrintWriter[maxConnection];
		myClientProcThread = new ClientProcThread[maxConnection];

		int n = 1;
		member = 0;//誰も接続していないのでメンバー数は０

		try {
			System.out.println("The server has launched!");
			ServerSocket server = new ServerSocket(10000);//10000番ポートを利用する
			while (true) {
				incoming[n] = server.accept();
				flag[n] = true;
				System.out.println("Accept client No." + n);
				//必要な入出力ストリームを作成する
				isr[n] = new InputStreamReader(incoming[n].getInputStream());
				in[n] = new BufferedReader(isr[n]);
				out[n] = new PrintWriter(incoming[n].getOutputStream(), true);

				myClientProcThread[n] = new ClientProcThread(n, incoming[n], isr[n], in[n], out[n]);//必要なパラメータを渡しスレッドを作成
				myClientProcThread[n] .start();//スレッドを開始する
				member = n;//メンバーの数を更新する
				n++;
			}
		} catch (Exception e) {
			System.err.println("ソケット作成時にエラーが発生しました: " + e);
		}

	}

}
