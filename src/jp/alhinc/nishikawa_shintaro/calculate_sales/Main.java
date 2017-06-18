
package jp.alhinc.nishikawa_shintaro.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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

		//branchファイルの読み込み処理
		if(!reader(args[0],"branch.lst",branchMap,branchSaleMap,"^[0-9]{3}$","支店")){
			return;
		}
		//commodityファイルの読み込み
		if(!reader(args[0],"commodity.lst",commodityMap,commoditySaleMap,"^[A-Z a-z 0-9]{8}$","商品")){
			return;
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
						return;
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
					return;
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
					return;
				}
				FileReader fr=new FileReader(file);
				BufferedReader br=new BufferedReader(fr);
				try{
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
					if(!branchMap.containsKey(rcdData.get(0))){
						System.out.println(file.getName()+"のフォーマットが不正です");
						return;
					}
					//不正な商品コードをチェックする
					if(!commodityMap.containsKey(rcdData.get(1))){
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
					br.close();
				}
			}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return;
		}
		//集計結果出力処理
		if(!output( branchMap,args[0],"branch.out",branchSaleMap)){
			return;
		}
		//集計結果出力処理
		if(!output(commodityMap,args[0],"commodity.out",commoditySaleMap)){
			return;
		}
	}
	//ファイル出力メソッド
	public static boolean output(HashMap<String,String> name,String dir,String fileName,HashMap<String,Long> sales){
		//支店別集計ファイルの作成/出力を行う処理
		PrintWriter pw = null;
		try{
			File file = new File(dir,fileName);

			if(!file.exists()){
				file.createNewFile(); 	//ファイルが存在しなければ新しく作成する
			}
			//branchSaleMapを降順にソートする
			List<Map.Entry <String,Long> > sortbranchSaleMap =new ArrayList <Map.Entry <String,Long> >
			(sales.entrySet());
			Collections.sort(sortbranchSaleMap, new Comparator<Map.Entry <String,Long> >() {
				public int compare(
						Entry <String,Long> entry1, Entry <String,Long> entry2) {
				return ((Long)entry2.getValue() ).compareTo((Long)entry1.getValue() );
			}
			});
			FileWriter fw=new FileWriter(file);
			BufferedWriter bw= new BufferedWriter(fw);
			pw = new PrintWriter(bw);
				//支店別集計ファイルに出力する
			for (Entry<String,Long> s : sortbranchSaleMap) {
				pw.println(s.getKey()+","+name.get(s.getKey())+","+s.getValue());
				//出力内容を確認する
				System.out.println(s.getKey()+","+name.get(s.getKey())+","+s.getValue());
			}
		} catch (IOException e) {
			System.out.println("予期せぬエラーが発生しました。");
			return false;
		}finally{
			pw.close();
		}
		return true;
	}
	//ファイルreaderメソッド
	public static boolean reader(String dir,String fileName,HashMap<String,String>
	read,HashMap<String,Long> sales,String condition,String code){
		BufferedReader br=null;
		try{
				File file=new File(dir,fileName);
				if(!file.exists()){
				System.out.println(code+"定義ファイルが存在しません");
				return false;
				}
				FileReader fr=new FileReader(file);
				br=new BufferedReader(fr);
				String s;
				while((s=br.readLine())!=null){
					String[]copy=s.split(",",0);
					// 分割した文字列が2つであることを確認する
					if( copy.length != 2 ){
						System.out.println(code+"定義ファイルのフォーマットが不正です");
						return false;
					}
					if(!copy[0].matches(condition)){
						System.out.println(code+"定義ファイルのフォーマットが不正です");
						return false;
					}
					read.put(copy[0],copy[1]);
					sales.put(copy[0],(long)0);
					System.out.println(s);
				}
		}catch(IOException e){
			System.out.println("予期せぬエラーが発生しました");
			return false;
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				System.out.println("予期せぬエラーが発生しました");
				return false;
			}
		}
		return true;
	}
}
