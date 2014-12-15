#ifndef __UTILITY_H__
#define __UTILITY_H__

#include "Header.h"

// -----------------------
// -----�摜�̓ǂݍ���-----
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
// -----���݂̎������擾------
string getCurrentTime()
{
	// �����擾�ɗp����ϐ�
	time_t current;
	struct tm *local;
	string currentTime;	// ������string�^�ŏo�͂��邽�߂̕ϐ�

	current = time(NULL);	// �������擾
	local = localtime(&current);	// ���������[�J���^�C���ɕϊ�

	// ������stringstream�ɗ����Cstring�^�ɕϊ�
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

	// �����̗v�f��S�Č����������̂��쐬 (��)201410201920
	currentTime = tmpYear + tmpMonth + tmpDay + tmpHour + tmpMinute;

	return currentTime;
}

// ---------------------------------
// -----csv�t�@�C����ǂݏo������-----
vector<myLink> mycsvDataLoader(string csvFileName)
{
	string str;
	stringstream ss;

	//�ȉ��̌`���Ŋi�[����Ă���f�[�^��ǂݎ��D
	/*
	344
	0,0-000,0,0-000,-1,0
	0,0-000,1,0-000,1,2.37E+10
	0,0-000,2,0-000,1,2.19E+10
		  .
		  .
		  .
	*/

	// ifstream�ɂ���āCcsvFileName�Ɏw�肳�ꂽ�t�@�C�����J���D
	ifstream csvFile(csvFileName);
	size_t N;    // �g�p����v�f�����i�[

	// �v�f����1�s�ڂ���擾
	getline(csvFile.seekg(0, ios_base::cur), str, '\n');
	ss.str(str);    //stringstream�ɓǂ݂�����string�𗬂��B

	//stringstream����N�ɗ����B
	ss >> N;
	//stringstream���ȉ��̂Q�s�̃R�[�h�ŃN���A����B
	//������s��Ȃ��ƑO�̕������c���đz��ʂ�̐��l���z��Ɋi�[�ł��Ȃ��B
	ss.str("");
	ss.clear(stringstream::goodbit);

	// �ȉ��̔z���CSV�t�@�C���̃f�[�^���i�[����
	vector<myLink> data;
	//string tmp[5];
	string tmp[6];

	//elements�s���̓ǂݏo���B
	for (size_t row = 1; row < N + 1; row++)
	{
		//5�񕪓ǂݏo���B
		//for (size_t col = 0; col < 4; col++)
		for (size_t col = 0; col < 5; col++)
		{
			//geline�̑�1�����͓ǂݏo���J�n�ʒu��\���܂��B
			//�ȉ��̃R�[�h�̑�1�����͌��݂̓ǂݏo���ʒu��\���܂��B
			//��2�����͓ǂݏo������w�肵�܂��B
			//��3�����͏I�[�Ƃ��镶�����w�肵�܂��B
			getline(csvFile.seekg(0, ios_base::cur), str, ',');

			//stringstream�ɓǂ݂�����string�𗬂��B
			ss.str(str);

			//stringstream����z��ɗ����B
			//���̎���string�^����double�^�̕ϊ����ÖٓI�ɍs����B
			ss >> tmp[col];
			//stringstream���ȉ��̂Q�s�̃R�[�h�ŃN���A����B
			//������s��Ȃ��ƑO�̕������c���đz��ʂ�̐��l���z��Ɋi�[�ł��Ȃ��B
			ss.str("");

			ss.clear(stringstream::goodbit);
		}

		//���s�R�[�h�܂œǂݍ��ށB���Ȃ킿��ԍŌ�̗�̐��l��ǂݍ��ށB
		getline(csvFile.seekg(0, ios_base::cur), str, '\n');
		ss.str(str);
		//ss >> tmp[4];
		ss >> tmp[5];
		ss.str("");

		ss.clear(stringstream::goodbit);
		//data.push_back(myLink(std::stoull(tmp[0]), tmp[1], tmp[2], std::stoi(tmp[3]), std::stod(tmp[4])));
		data.push_back(myLink(tmp[0], tmp[1], tmp[2], tmp[3], std::stoi(tmp[4]), std::stod(tmp[5])));
	}

	//CSV�t�@�C������ăt�@�C���ւ̃A�N�Z�X�����J������B
	csvFile.close();
	return data;
}

// -----------------------------------
// -----DB�̃v���p�e�B��ǂݏo������-----
map<string, int> myDBLoader(string DBPropetyFileName)
{
	string str;
	stringstream ss;
	size_t N;

	//�ȉ��̌`���Ŋi�[����Ă���f�[�^��ǂݎ��D
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
	// ifstream�ɂ���āCDBProperyFileName�Ɏw�肳�ꂽ�t�@�C�����J���D
	ifstream csvFile(DBPropetyFileName);

	// �v�f����1�s�ڂ���擾
	getline(csvFile.seekg(0, ios_base::cur), str, '\n');
	ss.str(str);    //stringstream�ɓǂ݂�����string�𗬂��B
	
	//stringstream����N�ɗ����B
	ss >> N;
	//cout << "N:" << N << endl;
	//stringstream���ȉ��̂Q�s�̃R�[�h�ŃN���A����B
	//������s��Ȃ��ƑO�̕������c���đz��ʂ�̐��l���z��Ɋi�[�ł��Ȃ��B
	ss.str("");
	ss.clear(stringstream::goodbit);

	// �ȉ��̔z���CSV�t�@�C���̃f�[�^���i�[����
	map<string, int> DBProperty;
	string tmp[2];

	//elements�s���̓ǂݏo���B
	for (size_t row = 1; row < N + 1; row++)
	{
		//2�񕪓ǂݏo���B
		
		//geline�̑�1�����͓ǂݏo���J�n�ʒu��\���܂��B
		//�ȉ��̃R�[�h�̑�1�����͌��݂̓ǂݏo���ʒu��\���܂��B
		//��2�����͓ǂݏo������w�肵�܂��B
		//��3�����͏I�[�Ƃ��镶�����w�肵�܂��B
		getline(csvFile.seekg(0, ios_base::cur), str, ',');

		//stringstream�ɓǂ݂�����string�𗬂��B
		ss.str(str);

		//stringstream����z��ɗ����B
		//���̎���string�^����double�^�̕ϊ����ÖٓI�ɍs����B
		ss >> tmp[0];
		//cout << "tmp[0] : " << tmp[0] << endl;
		//stringstream���ȉ��̂Q�s�̃R�[�h�ŃN���A����B
		//������s��Ȃ��ƑO�̕������c���đz��ʂ�̐��l���z��Ɋi�[�ł��Ȃ��B
		ss.str("");
		ss.clear(stringstream::goodbit);

		//���s�R�[�h�܂œǂݍ��ށB���Ȃ킿��ԍŌ�̗�̐��l��ǂݍ��ށB
		getline(csvFile.seekg(0, ios_base::cur), str, '\n');
		ss.str(str);
		ss >> tmp[1];
		//cout << "tmp[1] : " << tmp[1] << endl;
		ss.str("");
		ss.clear(stringstream::goodbit);
		
		DBProperty.insert(map<string, int>::value_type(tmp[0], stoi(tmp[1])));
	}

	//CSV�t�@�C������ăt�@�C���ւ̃A�N�Z�X�����J������B
	csvFile.close();
	return DBProperty;
}



// ----------------------
// -----split�̎��� -----
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
// -----�אڃm�[�h���ۂ��𔻕ʂ��鏈��-----
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