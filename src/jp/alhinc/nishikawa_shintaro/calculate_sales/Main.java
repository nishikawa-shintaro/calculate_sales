
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
			return;
		}
		//branchファイルデータを格納するmapの宣言
		HashMap<String, String>branchMap= new HashMap<String,String>();
		//commodityファイルデータを格納するmapの宣言
		HashMap<String, String>commodityMap= new HashMap<String,String>();
		//支店ごとの売り上げを集計するMapの宣言
		HashMap<String, Long>branchSaleMap= new HashMap<String,Long>();
		//商品ごとに売り上げを集計すファイルの宣言
		HashMap<String,Long>commoditySaleMap= new HashMap<String,Long>();

		InputStreamReader in =new InputStreamReader(System.in);
		BufferedReader reader=new BufferedReader(in);
		//支店定義ファイルの読み込み処理
		try{
			File file01=new File(args[0],"branch.lst");
			if(!file01.exists()){
				System.out.println("支店定義ファイルが存在しません");   
				return;
			}
			FileReader fr01=new FileReader(file01);
			BufferedReader br01=new BufferedReader(fr01);
			String s01;
			try{
				while((s01=br01.readLine())!=null){	   							
					String[]copy=s01.split(",",0);                     		
					// 分割した文字列が2つであることを確認する
					if( copy.length != 2 ){
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					//支店コードが3桁の数字であることを確認する
					if(!copy[0].matches(("^[0-9]{3}$"))){
						System.out.println("支店定義ファイルのフォーマットが不正です");
						return;
					}
					branchMap.put(copy[0],copy[1]);
					branchSaleMap.put(copy[0],(long)0);
					//System.out.println(s01);
				}
			}
			catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
			}
			finally{
				br01.close();
			}
		//商品定義ファイルの処理
		File file02=new File(args[0],"commodity.lst");
		if(!file02.exists()){
			System.out.println("商品定義ファイルが存在しません");
		}
		FileReader fr02=new FileReader(file02);
		BufferedReader br02=new BufferedReader(fr02);
		try{
			String s02;
			while((s02=br02.readLine())!=null){
				String[]copy=s02.split(",",0);
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
					commodityMap.put(copy[0],copy[1]);
					commoditySaleMap.put(copy[0],(long)0);
					//System.out.println(s02);
			}
		}catch(IOException e){
				System.out.println("予期せぬエラーが発生しました");
		}finally{
			br02.close();
		}
		}
		catch(IOException e){
		System.out.println("予期せぬエラーが発生しました");
		}	
		//売り上げファイルを選別し読み込む処理
		File folder=new File(args[0]);
		String[] list=folder.list();
		ArrayList<String>tempList=new ArrayList<String>();
		//連番判定用のInteger型のListを作る
		List<Integer>numList=new ArrayList<Integer>();
		for(String str :list){
			if(str.matches("^.+.\\.rcd$")){
					if(!(str.matches("^[0-9]{8}\\.rcd$"))){
						System.out.println("売り上げファイル名のフォーマットが不正です");
					}
					//rcdファイルをArrayListクラスに格納
					tempList.add(str);
					//文字列の8桁を取り出しListに格納する
					numList.add(Integer.parseInt(str.substring(0,8)));
				}
			}
		//TempListを昇順に並べ替える
		Collections.sort(tempList);
		Collections.sort(numList);
		//連番の判定処理を行う//変数iの数値と(i-1)+1がすべて成立するならば連番である
		for(int i=1;i<numList.size();i++){
				if(numList.get(i)!=numList.get(i-1)+1){
					System.out.println("売上ファイル名が連番になっていません");
				}
			}
		//売り上げファイルを読み込む処理
		//支店ごとの売り上げを集計するファイルの処理
		//商品ごとの売り上げを集計するファイルの処理
		try{
			for(int i=0;i<tempList.size();i++) {
				File file03=new File(args[0],tempList.get(i));
				if(!file03.exists()){
					System.out.println("ファイルが存在しません");
					return;
				}
				FileReader fr03=new FileReader(file03);
				BufferedReader br03=new BufferedReader(fr03);
				try{
					String str;
					ArrayList<String>rcdData=new ArrayList<String>();       //rdcファイル中身を格納するListを作る
					while((str=br03.readLine())!=null){
						rcdData.add(str);									//１行ずつファイルを読み込む
					}
					// 売上ファイルが3行であることを確認する
					if( rcdData.size() != 3 ){
						System.out.println(file03.getName() + "のフォーマットが不正です");
						return;
					}
					//不正な支店コードをチェックする
					if(!branchMap.containsKey(rcdData.get(0))){
						System.out.println(file03.getName()+"のフォーマットが不正です");
						return;
					}
					//不正な商品コードをチェックする
					if(!commodityMap.containsKey(rcdData.get(1))){
						System.out.println(file03.getName()+"のフォーマットが不正です");
						return;
					}
					//支店毎の売り上げ集計を行う
					branchSaleMap.put(rcdData.get(0),Long.parseLong(rcdData.get(2))+branchSaleMap.get(rcdData.get(0)));
					//商品毎の売り上げ集計を行う
					commoditySaleMap.put
							(rcdData.get(1),Long.parseLong(rcdData.get(2))+commoditySaleMap.get(rcdData.get(1)));
					//10桁を超えたら読み込みを中断する
					if (branchSaleMap.get(rcdData.get(0)) > (long)999999999) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}
					//10桁を超えたら読み込みを中断する
					if (commoditySaleMap.get(rcdData.get(1)) > (long)999999999) {
						System.out.println("合計金額が10桁を超えました");
						return;
					}
				}catch(IOException e){
					System.out.println("予期せぬエラーが発生しました");
					return;
				}finally{
					br03.close();
				}
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
		}
		//処理内容4
		//支店別集計ファイルの作成/出力を行う処理
		try{
			File file04=new File(args[0],"branch.out");
				if(!file04.exists()){
					file04.createNewFile(); 	//ファイルが存在しなければ新しく作成する
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
				FileWriter fw04=new FileWriter(file04);
				BufferedWriter bw04= new BufferedWriter(fw04);
				PrintWriter pw04 = new PrintWriter(bw04);
				try{
					//支店別集計ファイルに出力する
					for (Entry<String,Long> s : sortbranchSaleMap) {
						pw04.println(s.getKey()+","+branchMap.get(s.getKey())+","+s.getValue());
						//出力内容を確認する
						//System.out.println(s.getKey()+","+branchMap.get(s.getKey())+","+s.getValue());
					}
				}
				finally{
					pw04.close();
				}
			//商品別集計ファイルの作成/出力を行う処理
			File file05=new File(args[0],"commodity.out");
			if(!file05.exists()){
				file05.createNewFile(); 		//ファイルが存在しなければ新しく作成する
			}
			//branchSaleMapを降順にソートする
			List<Map.Entry <String,Long> > sortcommoditySaleMap =new ArrayList <Map.Entry <String,Long> >
																						(commoditySaleMap.entrySet() );
			Collections.sort(sortcommoditySaleMap, new Comparator<Map.Entry <String,Long> >() {
			public int compare(	Entry <String,Long> entry1, Entry <String,Long> entry2) {
					return ((Long)entry2.getValue() ).compareTo((Long)entry1.getValue() );
				}
			});
			FileWriter fw05=new FileWriter(file05);
			BufferedWriter bw05= new BufferedWriter(fw05);
			PrintWriter pw05 = new PrintWriter(bw05);
			try{
				////商品別集計ファイルに出力する
				for (Entry<String,Long> s : sortcommoditySaleMap) {
					pw05.println(s.getKey()+","+commodityMap.get(s.getKey())+","+s.getValue());
					//出力内容を確認する
					//System.out.println(s.getKey()+","+commodityMap.get(s.getKey())+","+s.getValue());
				}
			}
			finally{
				pw05.close();
			}
		}
		catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
	}
}
