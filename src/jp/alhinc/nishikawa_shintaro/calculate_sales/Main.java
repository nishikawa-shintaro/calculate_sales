package jp.alhinc.nishikawa_shintaro.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Main {
	public static void main(String[] args) {
		//コマンドライン引数チェック
		if(args.length !=1){
			System.out.println("予期せぬエラーが発生しました");
		}

		//branchファイルデータを格納するmapの宣言
		HashMap<String, String>branchData= new HashMap<String,String>();
		//commodityファイルデータを格納するmapの宣言
		HashMap<String, String>commodityData= new HashMap<String,String>();
		//支店ごとの売り上げを集計するMapの宣言
		HashMap<String, Long>branchSaleMap= new HashMap<String,Long>();
		//商品ごとに売り上げを集計すファイルの宣言
		HashMap<String,Long>commoditySaleMap= new HashMap<String,Long>();


		//処理内容1↓
		//コマンドライン引数でディレクトリを指定
		//支店定義ファイルの処理
		InputStreamReader in =new InputStreamReader(System.in);
		BufferedReader reader=new BufferedReader(in);
		try{
			File file=new File(args[0],"branch.lst");
			if(!file.exists()){
				System.out.println("支店定義ファイルが存在しません");   			 //ファイルの判定処理を行う
			}
			String s;
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
			while((s=br.readLine())!=null){	   							//ファイルデータを一列ずつ読み込む
				String[]copy=s.split(",",0);                     		//配列で格納し読み込まれた行を，で区切る
				// 分割した文字列が2つであることを確認する
				if( copy.length != 2 ){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					System.exit(1);
				}
				//支店コードが3桁の数字であることを確認する
				if(!copy[0].matches(("^[0-9]{3}$"))){
					System.out.println("支店定義ファイルのフォーマットが不正です");
					return;										//	処理を異常終了とする
				}
				branchData.put(copy[0],copy[1]);
				branchSaleMap.put(copy[0],(long)0);
				System.out.println(s);									//支店定義ファイルの読み込み内容を確認
			}
			br.close();
			}
			catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
			}

		//処理内容2↓
		//商品定義ファイルの処理
		try{
			File file=new File(args[0],"commodity.lst");
			if(!file.exists()){
				System.out.println("商品定義ファイルが存在しません");    			//ファイルの判定処理を行う
			}
			String s;
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
			while((s=br.readLine())!=null){	   							//ファイルデータを一列ずつ読み込む
				String[]copy=s.split(",",0);                     	 	//配列で格納し読み込まれた行を，で区切る
				// 分割した文字列が2つであることを確認する
				if( copy.length != 2 ){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				//商品コードが8桁の半角英数字であることを確認する
				if(!copy[0].matches(("^\\w{8}$"))){
					System.out.println("商品定義ファイルのフォーマットが不正です");
					return;
				}
				commodityData.put(copy[0],copy[1]);
				commoditySaleMap.put(copy[0],(long)0);
				System.out.println(s);								//商品定義ファイル読み込んだ内容確認
			}
			br.close();
			}
		catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			}

	//処理内容3↓
	//売り上げファイルを選別し読み込む処理
		File folder=new File(args[0]);
		String[] list=folder.list();      						//fileListにargs引数内フォルダの一覧を格納する
		ArrayList<String>tempList=new ArrayList<String>();
		List<Integer>numList=new ArrayList<Integer>();			//連番判定用のInteger型のListを作る
		for(String str :list)
		{
			if(str.matches("^.+.\\.rcd$"))                          //.rdc拡張子を持つファイルを抽出
			{
				if(!(str.matches("^[0-9]{8}\\.rcd$")))    			//rcdかつ8文字列を持っているファイルを抽出
				{
					System.out.println("売り上げファイル名のフォーマットが不正です");
				}
					tempList.add(str);								//rcdファイルをArrayListクラスに格納
																	//文字列の8桁を取り出しListに格納する
					numList.add(Integer.parseInt(str.substring(0,8)));
			}
		}
		//TempListを昇順に並べ替える
		Collections.sort(tempList);
		Collections.sort(numList);
		//連番の判定処理を行う
		for(int i=1;i<numList.size();i++)				//変数iの数値と(i-1)+1がすべて成立するならば連番である
		{
			if(numList.get(i)!=numList.get(i-1)+1)
			{
				System.out.println("売り上げファイル名が連番になっていません");
			}
		}
		//売り上げファイルを読み込む処理
		//支店ごとの売り上げを集計するファイルの処理
		//商品ごとの売り上げを集計するファイルの処理
		try{
			for(int i=0;i<tempList.size();i++) {
				File file=new File(args[0],tempList.get(i));
				if(!file.exists()){
					System.out.println("ファイルが存在しません");
				}
				FileReader fr=new FileReader(file);
				BufferedReader br=new BufferedReader(fr);
				String str;
				ArrayList<String>rcdData=new ArrayList<String>();       //rdcファイル中身を格納するListを作る
				while((str=br.readLine())!=null){
					rcdData.add(str);									//１行ずつファイルを読み込む
				}
				// 売上ファイルが3行であることを確認する
				if( rcdData.size() != 3 ){
					System.out.println(file.getName() + "のフォーマットが不正です");
					return;
				}
				//不正な支店コードをチェックする
				if(!branchData.containsKey(rcdData.get(0))){
					System.out.println(file.getName()+"のフォーマットが不正です");
					return;
				}
				//不正な商品コードをチェックする
				if(!commodityData.containsKey(rcdData.get(1))){
					System.out.println(file.getName()+"のフォーマットが不正です");
					return;
				}
				//支店毎の売り上げ集計を行う
				branchSaleMap.put(rcdData.get(0),Long.parseLong(rcdData.get(2))+branchSaleMap.get(rcdData.get(0)));
				//商品毎の売り上げ集計を行う
				commoditySaleMap.put
							(rcdData.get(1),Long.parseLong(rcdData.get(2))+commoditySaleMap.get(rcdData.get(1)));
				//10桁を超えたら読み込みを中断する
				if (branchSaleMap.get(rcdData.get(0)) > (long)999999999) {
					System.out.println("合計金額が十桁を超えました");
					return;
				}
				//10桁を超えたら読み込みを中断する
				if (commoditySaleMap.get(rcdData.get(1)) > (long)999999999) {
					System.out.println("合計金額が十桁を超えました");
					return;
				}
			br.close();
			}
		}
		catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			}


		//処理内容4
		//支店別集計ファイルの作成/出力を行う処理
		File file=new File(args[0],"branch.out");
		try{
			if(!file.exists()){
				file.createNewFile(); 	//ファイルが存在しなければ新しく作成する

			}
			//branchSaleMapを降順にソートする
			List<Map.Entry <String,Long> > sortbranchSaleMap =new ArrayList <Map.Entry <String,Long> >
																						(branchSaleMap.entrySet());
			Collections.sort(sortbranchSaleMap, new Comparator<Map.Entry <String,Long> >() {
			public int compare(
						Entry <String,Long> entry1, Entry <String,Long> entry2) {
					return ((Long)entry2.getValue() ).compareTo((Long)entry1.getValue() );
				}
			});
			FileWriter fw=new FileWriter(file);
			BufferedWriter bw= new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);
			//支店別集計ファイルに出力する
			for (Entry<String,Long> s : sortbranchSaleMap) {
	            pw.println(s.getKey()+","+branchData.get(s.getKey())+","+s.getValue());
	            //出力内容を確認する
	            System.out.println(s.getKey()+","+branchData.get(s.getKey())+","+s.getValue());
	        }
			pw.close();
		}
		catch(IOException e){
		System.out.println("予期せぬエラーが発生しました");
		}
		//商品別集計ファイルの作成/出力を行う処理
		File file2=new File(args[0],"commodity.out");
		try{
			if(!file2.exists()){
				file2.createNewFile(); 		//ファイルが存在しなければ新しく作成する

			}
			//branchSaleMapを降順にソートする
			List<Map.Entry <String,Long> > sortcommoditySaleMap =new ArrayList <Map.Entry <String,Long> >
																						(commoditySaleMap.entrySet() );
			Collections.sort(sortcommoditySaleMap, new Comparator<Map.Entry <String,Long> >() {
			public int compare(
						Entry <String,Long> entry1, Entry <String,Long> entry2) {
					return ((Long)entry2.getValue() ).compareTo((Long)entry1.getValue() );
				}
			});

			FileWriter fw=new FileWriter(file2);
			BufferedWriter bw= new BufferedWriter(fw);
			PrintWriter pw = new PrintWriter(bw);

			////商品別集計ファイルに出力する
			for (Entry<String,Long> s : sortcommoditySaleMap) {
	            pw.println(s.getKey()+","+commodityData.get(s.getKey())+","+s.getValue());
	            //出力内容を確認する
	            System.out.println(s.getKey()+","+commodityData.get(s.getKey())+","+s.getValue());
	        }
			pw.close();
		}
		catch(IOException e){
		System.out.println("予期せぬエラーが発生しました");
		}
	}
}

