#ifndef __UTILITY_H__
#define __UTILITY_H__

#include "Header.h"

// -----------------------
// -----画像の読み込み-----
Mat loadImg(string imgFile)
{
	Mat img = imread(imgFile, colorType);
	if (img.empty())
	{
		cout << "couldn't load the image." << endl;
		exit(1);
	}
	return img;
};

// -------------------------
// -----現在の時刻を取得------
string getCurrentTime()
{
	// 時刻取得に用いる変数
	time_t current;
	struct tm *local;
	string currentTime;	// 時刻をstring型で出力するための変数

	current = time(NULL);	// 時刻を取得
	local = localtime(&current);	// 時刻をローカルタイムに変換

	// 時刻をstringstreamに流し，string型に変換
	stringstream streamYear;
	streamYear << setw(4) << setfill('0') << local->tm_year + 1900;
	string tmpYear = streamYear.str();
	stringstream streamMonth;
	streamMonth << setw(2) << setfill('0') << local->tm_mon + 1;
	string tmpMonth = streamMonth.str();
	stringstream streamDay;
	streamDay << setw(2) << setfill('0') << local->tm_mday;
	string tmpDay = streamDay.str();
	stringstream streamHour;
	streamHour << setw(2) << setfill('0') << local->tm_hour;
	string tmpHour = streamHour.str();
	stringstream streamMinute;
	streamMinute << setw(2) << setfill('0') << local->tm_min;
	string tmpMinute = streamMinute.str();

	// 時刻の要素を全て結合したものを作成 (例)201410201920
	currentTime = tmpYear + tmpMonth + tmpDay + tmpHour + tmpMinute;

	return currentTime;
}

// ---------------------------------
// -----csvファイルを読み出す処理-----
vector<myLink> mycsvDataLoader(string csvFileName)
{
	string str;
	stringstream ss;

	//以下の形式で格納されているデータを読み取る．
	/*
	344
	0,0-000,0,0-000,-1,0
	0,0-000,1,0-000,1,2.37E+10
	0,0-000,2,0-000,1,2.19E+10
		  .
		  .
		  .
	*/

	// ifstreamによって，csvFileNameに指定されたファイルを開く．
	ifstream csvFile(csvFileName);
	size_t N;    // 使用する要素数を格納

	// 要素数を1行目から取得
	getline(csvFile.seekg(0, ios_base::cur), str, '\n');
	ss.str(str);    //stringstreamに読みだしたstringを流す。

	//stringstreamからNに流す。
	ss >> N;
	//stringstreamを以下の２行のコードでクリアする。
	//これを行わないと前の文字が残って想定通りの数値が配列に格納できない。
	ss.str("");
	ss.clear(stringstream::goodbit);

	// 以下の配列にCSVファイルのデータを格納する
	vector<myLink> data;
	//string tmp[5];
	string tmp[6];

	//elements行分の読み出し。
	for (size_t row = 1; row < N + 1; row++)
	{
		//5列分読み出し。
		//for (size_t col = 0; col < 4; col++)
		for (size_t col = 0; col < 5; col++)
		{
			//gelineの第1引数は読み出し開始位置を表します。
			//以下のコードの第1引数は現在の読み出し位置を表します。
			//第2引数は読み出し先を指定します。
			//第3引数は終端とする文字を指定します。
			getline(csvFile.seekg(0, ios_base::cur), str, ',');

			//stringstreamに読みだしたstringを流す。
			ss.str(str);

			//stringstreamから配列に流す。
			//この時にstring型からdouble型の変換が暗黙的に行われる。
			ss >> tmp[col];
			//stringstreamを以下の２行のコードでクリアする。
			//これを行わないと前の文字が残って想定通りの数値が配列に格納できない。
			ss.str("");

			ss.clear(stringstream::goodbit);
		}

		//改行コードまで読み込む。すなわち一番最後の列の数値を読み込む。
		getline(csvFile.seekg(0, ios_base::cur), str, '\n');
		ss.str(str);
		//ss >> tmp[4];
		ss >> tmp[5];
		ss.str("");

		ss.clear(stringstream::goodbit);
		//data.push_back(myLink(std::stoull(tmp[0]), tmp[1], tmp[2], std::stoi(tmp[3]), std::stod(tmp[4])));
		data.push_back(myLink(tmp[0], tmp[1], tmp[2], tmp[3], std::stoi(tmp[4]), std::stod(tmp[5])));
	}

	//CSVファイルを閉じてファイルへのアクセス権を開放する。
	csvFile.close();
	return data;
}

// -----------------------------------
// -----DBのプロパティを読み出す処理-----
map<string, int> myDBLoader(string DBPropetyFileName)
{
	string str;
	stringstream ss;
	size_t N;

	//以下の形式で格納されているデータを読み取る．
	/*
	103
	0-000	344
	0-001	512
	0-002	596
		.
		.
		.
	*/

	cout << "Now Loading...  " << DBPropetyFileName << endl;
	// ifstreamによって，DBProperyFileNameに指定されたファイルを開く．
	ifstream csvFile(DBPropetyFileName);

	// 要素数を1行目から取得
	getline(csvFile.seekg(0, ios_base::cur), str, '\n');
	ss.str(str);    //stringstreamに読みだしたstringを流す。
	
	//stringstreamからNに流す。
	ss >> N;
	//cout << "N:" << N << endl;
	//stringstreamを以下の２行のコードでクリアする。
	//これを行わないと前の文字が残って想定通りの数値が配列に格納できない。
	ss.str("");
	ss.clear(stringstream::goodbit);

	// 以下の配列にCSVファイルのデータを格納する
	map<string, int> DBProperty;
	string tmp[2];

	//elements行分の読み出し。
	for (size_t row = 1; row < N + 1; row++)
	{
		//2列分読み出し。
		
		//gelineの第1引数は読み出し開始位置を表します。
		//以下のコードの第1引数は現在の読み出し位置を表します。
		//第2引数は読み出し先を指定します。
		//第3引数は終端とする文字を指定します。
		getline(csvFile.seekg(0, ios_base::cur), str, ',');

		//stringstreamに読みだしたstringを流す。
		ss.str(str);

		//stringstreamから配列に流す。
		//この時にstring型からdouble型の変換が暗黙的に行われる。
		ss >> tmp[0];
		//cout << "tmp[0] : " << tmp[0] << endl;
		//stringstreamを以下の２行のコードでクリアする。
		//これを行わないと前の文字が残って想定通りの数値が配列に格納できない。
		ss.str("");
		ss.clear(stringstream::goodbit);

		//改行コードまで読み込む。すなわち一番最後の列の数値を読み込む。
		getline(csvFile.seekg(0, ios_base::cur), str, '\n');
		ss.str(str);
		ss >> tmp[1];
		//cout << "tmp[1] : " << tmp[1] << endl;
		ss.str("");
		ss.clear(stringstream::goodbit);
		
		DBProperty.insert(map<string, int>::value_type(tmp[0], stoi(tmp[1])));
	}

	//CSVファイルを閉じてファイルへのアクセス権を開放する。
	csvFile.close();
	return DBProperty;
}



// ----------------------
// -----splitの実装 -----
std::vector<std::string> split(const std::string& input, char delimiter)
{
	std::istringstream stream(input);

	std::string field;
	std::vector<std::string> result;
	while (std::getline(stream, field, delimiter)) {
		result.push_back(field);
	}
	return result;
}

// -------------------------------------
// -----隣接ノードか否かを判別する処理-----
//int isNeighborNode(string src_, string dst_)
//{
//	vector<string> srcInfo;
//	for (const std::string& s : split(src_, '-')) {
//		srcInfo.push_back(s);
//	}
//
//	vector<string> dstInfo;
//	for (const std::string& s : split(dst_, '-')) {
//		dstInfo.push_back(s);
//	}
//
//	if ((srcInfo[1] == dstInfo[1]) && (abs(stoi(srcInfo[2]) - stoi(dstInfo[2])) == 1))
//		return 1;
//	else return 0;
//}
bool isNeighborNode(string srcLocus, string src, string dstLocus, string dst)
{
	if (stoi(srcLocus) == stoi(dstLocus))
	{
		if (abs(stoi(src) - stoi(dst)) == 1)
		{
			//cout << "srcLocus : " << srcLocus << " dstLocus : " << dstLocus << endl;
			//cout << "src : " << src << " dst : " << dst << endl;
			return true;
		}
		else return false;
	}
	else return false;
}

bool isWithinthreshold(double distance, double threshold)
{
	if (distance > threshold) 
	{
			return true;
	}
	else return false;
}

#endif