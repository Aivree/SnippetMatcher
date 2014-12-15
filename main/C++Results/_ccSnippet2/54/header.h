#include <iostream>
#include<vector>
#include<stack>
#include<map>
#include<cstring>
#include<fstream>
#include <cstdlib>
using namespace std;

string parseTable[1000][1000];
string longest_sub;
map<string,set<string> > first_set;
map<string,set<string> > follow_set;
vector<string> nonTerminals_vector;
vector<string> Terminals_vector;
map< string,vector<string> > grammar;

set<string> first_expr(string);

vector<string> split(string s, char delim, vector<string> &elems) {
	stringstream ss(s);
	string item;
	while(getline(ss, item, delim)) {
		if(item != "")
			elems.push_back(item);
	}
	return elems;
}

//first

bool is_nonTerminal(string nonT){
	vector<string>::iterator iter;
	for(iter = nonTerminals_vector.begin();iter != nonTerminals_vector.end();iter++){
		if(*iter == nonT)
			return 1;
	}
	return 0;
}

bool has_ep(string nonT){
	vector<string>::iterator iter_v;
	for(iter_v = grammar[nonT].begin();iter_v != grammar[nonT].end();iter_v++){
		if(*iter_v == "#"){
			return 1;
		}
	}
	for(iter_v = grammar[nonT].begin();iter_v != grammar[nonT].end();iter_v++){
		int has;
		vector<string> temp;
		temp.clear();
		vector<string>::iterator it_temp;
		split(*iter_v,' ',temp);
		for(it_temp = temp.begin();it_temp != temp.end();it_temp++){
			has = 0;
			if(is_nonTerminal(*it_temp)){
				if(!has_ep(*it_temp)){
					return 0;
				}
				else
					has = 1;
			}
			else
				break;
		}
		if(has)
			return 1;
	}
	return 0;
}

void first(){

	vector<string>::iterator iter;
	vector<string>::iterator iter_v;
	map<string,set<string> >::iterator it_f;
	int flag_i = 1;

	for(iter = nonTerminals_vector.begin();iter != nonTerminals_vector.end();iter++){
		for(iter_v = grammar[*iter].begin();iter_v != grammar[*iter].end();iter_v++){
			vector<string> temp;
			vector<string>::iterator it_temp;
			split(*iter_v,' ',temp);
			//for(it_temp = temp.begin();it_temp != temp.end();it_temp++){cout << *it_temp << "--"; }
			//cout << endl;
			for(it_temp = temp.begin();it_temp != temp.end();it_temp++){
				if(!is_nonTerminal(*it_temp)){
					if(*it_temp != ""){
						//cout << *iter << " ter " << *it_temp << endl;
						if(first_set[*iter].find(*it_temp) == first_set[*iter].end()){
							vector<string>::iterator it_ter;
							int exist = 0;
							for(it_ter = Terminals_vector.begin();it_ter != Terminals_vector.end();it_ter++){
								if(*it_ter == *it_temp)
									exist = 1;
							}
							if(!exist)
							{
								//cout << *it_temp << "  ";
								first_set[*it_temp].insert(*it_temp);
								Terminals_vector.push_back(*it_temp);
							}
						}
					}
				}
			}
		}
	}
	while(flag_i){
		flag_i = 0;
		for(iter = nonTerminals_vector.begin();iter != nonTerminals_vector.end();iter++){
			if(has_ep(*iter)){
				if(first_set[*iter].find("#") == first_set[*iter].end())
					flag_i = 1;
				first_set[*iter].insert("#");
			}
			for(iter_v = grammar[*iter].begin();iter_v != grammar[*iter].end();iter_v++){
				vector<string> temp;
				vector<string>::iterator it_temp;
				split(*iter_v,' ',temp);
				for(it_temp = temp.begin();it_temp != temp.end();it_temp++){
					//cout << *iter << "--" << *it_temp << endl;
					if(is_nonTerminal(*it_temp)){
						if(!has_ep(*it_temp)){
							//cout << "has ep " << *it_temp << endl;
							set<string>::iterator it_s;
							if(first_set.find(*it_temp)!=first_set.end()){
								for(it_s = first_set[*it_temp].begin();it_s != first_set[*it_temp].end();it_s++){
									if(first_set[*iter].find(*it_s) == first_set[*iter].end())
										flag_i = 1;
									//cout << *iter << " nonter " << *it_s << endl;
									first_set[*iter].insert(*it_s);
								}
							}
							break;
						}
					}
					else{
						if(*it_temp != ""){
							if(first_set[*iter].find(*it_temp) == first_set[*iter].end())
								flag_i = 1;
							first_set[*iter].insert(*it_temp);
							break;
						}
					}
				}
			}
		}
	}
	for(it_f = first_set.begin();it_f != first_set.end();it_f++){
		cout << it_f->first << "  ";
		set<string>::iterator it_s;
		for(it_s = it_f->second.begin();it_s != it_f->second.end();it_s++){
			cout << *it_s << "-->";
		}
		cout << endl;
	}
	vector<string>::iterator it_ter;
	for(it_ter = Terminals_vector.begin();it_ter != Terminals_vector.end();it_ter++){
		cout << *it_ter << "--";
	}
	cout << endl;
}

//follow

void follow(){
	follow_set[nonTerminals_vector[0]].insert("$");
	vector<string>::iterator iter;
	vector<string>::iterator iter_v;
	int flag_i = 1;
	while(flag_i){
		flag_i = 0;
		for(iter = nonTerminals_vector.begin();iter != nonTerminals_vector.end();iter++){
			//cout << "------------------" << *iter << endl;
			for(iter_v = grammar[*iter].begin();iter_v != grammar[*iter].end();iter_v++){
				vector<string> temp;
				vector<string>::iterator it_temp;
				vector<string>::iterator it_next;
				split(*iter_v,' ',temp);
				for(it_temp = temp.begin(),it_next=++temp.begin();it_temp != temp.end();it_temp++,it_next++){
					if(*it_temp != ""){
						if(is_nonTerminal(*it_temp)){
							//cout << *it_temp << "--" << *it_next << endl;
							if(it_next!=temp.end()){
								set<string>::iterator it_s;
								//cout << "first" << *it_temp;
								for(it_s = first_set[*it_next].begin();it_s != first_set[*it_next].end();it_s++){
									if(*it_s != "#"){
										if(follow_set[*it_temp].find(*it_s) == follow_set[*it_temp].end())
											flag_i = 1;
										follow_set[*it_temp].insert(*it_s);
										//cout << "-->" << *it_s;
									}
								}
								//cout << endl;
								set<string> first_e;
								vector<string>::iterator it_v;
								string tmp = "";
								for(it_v = it_next;it_v!=temp.end();it_v++){
									tmp += *it_v + " ";
								}
								first_e = first_expr(tmp);
								if(first_e.find("#") != first_e.end()){
									set<string>::iterator it_s;
									if(follow_set.find(*iter)!=follow_set.end()){
										//cout << "foll" << *it_temp;
										for(it_s=follow_set[*iter].begin();it_s!=follow_set[*iter].end();it_s++){
											if(follow_set[*it_temp].find(*it_s) == follow_set[*it_temp].end())
												flag_i = 1;
											follow_set[*it_temp].insert(*it_s);
											//cout << "-->" << *it_s;
										}
										//cout << endl;
									}
								}
							}
							else{
								set<string>::iterator it_s;
								if(follow_set.find(*iter)!=follow_set.end()){
									//cout << "last" << *it_temp;
									for(it_s=follow_set[*iter].begin();it_s!=follow_set[*iter].end();it_s++){
										if(follow_set[*it_temp].find(*it_s) == follow_set[*it_temp].end())
											flag_i = 1;
										follow_set[*it_temp].insert(*it_s);
										//cout << "-->" << *it_s;
									}
									//cout << endl;
								}
							}
						}
					}
				}
			}
		}
	}
	map<string,set<string> >::iterator it_f;
	for(it_f = follow_set.begin();it_f != follow_set.end();it_f++){
		cout << it_f->first << "  ";
		set<string>::iterator it_s;
		for(it_s = it_f->second.begin();it_s != it_f->second.end();it_s++){
			cout << *it_s << "-->";
		}
		cout << endl;
	}
}

//left_recursion

string getNonterminal(string line,int &linePos)
{
	int i=0;
	string str="";
	while(line[i]!='-')
	{
		str+=line[i];
		i++;
	}
	linePos=i+2;
	return str;
}

string getRHS(string line,int &linePos)
{
	int i=linePos;
	int len=line.length();
	string str="";
	while(i!=len && line[i]!='|')
	{
		str+=line[i];
		i++;
	}
	if(i!=len)
		linePos=i+1;
	else
		linePos=i;
	return str;
}

bool nonTerminal_notExist(string nonTerminal)
{
	int vec_size=nonTerminals_vector.size();
	for(int i=0;i<vec_size;i++)
	{
		if(nonTerminals_vector[i]==nonTerminal)
			return 0;
	}
	return 1;
}

bool str_match(int j,int i,int k)
{
	string str2=grammar[nonTerminals_vector[i]][k];
	if(str2.compare(0,nonTerminals_vector[j].length(),nonTerminals_vector[j]) == 0)
	{
		//cout << nonTerminals_vector[j]<<"\t"<<str2<<endl;
		return 1;
	}
	return 0;
}

bool check_direct_recursion(int i)
{
	int vec_size=grammar[nonTerminals_vector[i]].size();
	for(int j=0;j<vec_size;j++)
	{
		if(str_match(i,i,j))
		{
			return 1;
		}
	}
	return 0;
}

void direct_recursion(int i)
{
	int vec_size=grammar[nonTerminals_vector[i]].size();

	if(check_direct_recursion(i))
	{

		string newNonTerminal=nonTerminals_vector[i]+"_dash";
		nonTerminals_vector.push_back(newNonTerminal);
		vector<string> temp_vec;
		for(int j=0;j<vec_size;j++)
		{
			string originalRHS=grammar[nonTerminals_vector[i]][j];
			if(str_match(i,i,j))
			{
				string changedRHS=originalRHS.substr(nonTerminals_vector[i].length(),originalRHS.length());	//alpha1
				grammar[newNonTerminal].push_back(changedRHS+" "+newNonTerminal);
				//grammar[nonTerminals_vector[i]].erase(grammar[nonTerminals_vector[i]].begin()+j);
			}
			else
			{
				//cout << originalRHS+newNonTerminal<<endl;
				temp_vec.push_back(originalRHS+" "+newNonTerminal);
				//grammar[nonTerminals_vector[i]][j]=originalRHS+newNonTerminal;
			}
		}
		grammar[nonTerminals_vector[i]].erase(grammar[nonTerminals_vector[i]].begin(),grammar[nonTerminals_vector[i]].end());
		grammar[nonTerminals_vector[i]].insert(grammar[nonTerminals_vector[i]].begin(),temp_vec.begin(),temp_vec.end());
		temp_vec.erase(temp_vec.begin(),temp_vec.end());
		//cout << newNonTerminal<<endl;
		grammar[newNonTerminal].push_back("#");
		//for(int l=0;l<grammar[newNonTerminal].size();l++)
		//	cout << grammar[newNonTerminal][l]<<"\t";
		//exit(1);

	}
}

void indirect_recursion()
{
	int i,j,k,l;
	int vec_size=nonTerminals_vector.size();
	for(i=0;i<vec_size;i++)
	{
		for(j=0;j<i;j++)
		{
			int tmp=grammar[nonTerminals_vector[i]].size();
			for(k=0;k<tmp;k++)
			{
				if(str_match(j,i,k))
				{
					//cout <<grammar[nonTerminals_vector[i]][k]<<"\t : \t";
					string originalRHS=grammar[nonTerminals_vector[i]][k];
					string changedRHS;
					changedRHS=originalRHS.substr(nonTerminals_vector[j].length(),originalRHS.length());
					grammar[nonTerminals_vector[i]].erase(grammar[nonTerminals_vector[i]].begin()+k);
					int tmp1=grammar[nonTerminals_vector[j]].size();

					for(l=0;l<tmp1;l++)
					{
						grammar[nonTerminals_vector[i]].push_back(grammar[nonTerminals_vector[j]][l]+changedRHS);
						//cout << grammar[nonTerminals_vector[j]][l]+changedRHS<<"\t";
					}
					//cout<<endl;
				}
			}
		}
		direct_recursion(i);
	}
}



void left()
{
	string line;
	ifstream fp;
	fp.open("in");
	int linePos;
	while(getline (fp,line))
	{
		linePos=0;
		string nonTerminal=getNonterminal(line,linePos);
		if(nonTerminal_notExist(nonTerminal))
		{
			nonTerminals_vector.push_back(nonTerminal);
		}
		string RHS;
		int len=line.length();
		while(linePos!=len)
		{
			RHS=getRHS(line,linePos);
			grammar[nonTerminal].push_back(RHS);
		}
		/*
		   for(int j=0;j<grammar[nonTerminal].size();j++)
		   {
		   if(str_match(nonTerminal,grammar[nonTerminal][j]))
		   {
		   grammar[nonTerminal+"_dash"].push_back()
		   }
		   }
		   */
	}
	indirect_recursion();
	//direct_recursion(0);
	//cout << nonTerminals_vector.size()<<endl;

	for(int i=0;i<nonTerminals_vector.size();i++)
	{
		cout <<nonTerminals_vector[i]<<"\t : \t";
		int len=grammar[nonTerminals_vector[i]].size();
		//cout << len<<endl;
		for(int j=0;j<len;j++)
		{
			cout << grammar[nonTerminals_vector[i]][j]<<"\t|\t";
		}
		cout << endl;
	}
	//exit(1);
	fp.close();
}


//parsing

int Terminal_count;
int nonTerminal_count;

bool check_hash(string checkHash)
{
	set<string>::iterator it;
	it=first_set[checkHash].find("#");
	if(it!=first_set[checkHash].end())
		return 1;
	return 0;
}

set<string> first_expr(string s)
{
	vector<string> elems;
	split(s,' ',elems);
	set<string> temp;
	int i;
	//cout << elems.size()<<endl;
	for(i=0;i<elems.size() && check_hash(elems[i]);i++)
	{
		temp.insert(first_set[elems[i]].begin(),first_set[elems[i]].end());
	}
	if(i!=elems.size())
	{
		temp.insert(first_set[elems[i]].begin(),first_set[elems[i]].end());
	}
	return temp;
}

int lookTerminal_vector(string s)
{
	for(int i=0;i<Terminals_vector.size();i++)
	{
		if(Terminals_vector[i]==s)
			return i;
	}
	return -1;
}

/*void make_follow()
{
	string temp[]={"$",")"};
	follow_set["E"].insert (temp,temp+2);
	follow_set["E_dash"].insert (temp,temp+2);
	string temp1[]={"+","$",")"};
	follow_set["T"].insert (temp1,temp1+3);
	follow_set["T_dash"].insert (temp1,temp1+3);
	string temp2[]={"*","+","$",")"};
	follow_set["F"].insert (temp2,temp2+4);
}*/

int parse()
{
	Terminals_vector.push_back("$");
	Terminal_count=Terminals_vector.size();
	nonTerminal_count=nonTerminals_vector.size();

	//string parseTable[nonTerminal_count][Terminal_count];

	//cout << nonTerminal_count<<"\t"<<Terminal_count<<endl;

	set<string> temp;
	set<string> temp_follow;
//	make_follow();

	set<string>::iterator it;
	/*cout << "printing\n";
	  for(it=temp.begin();it!=temp.end();it++)
	  cout << *it<<endl;

	  set<string>::iterator it;
	  */set<string>::iterator it_follow;
	int l;

	for(int i=0;i<nonTerminals_vector.size();i++)
	{
		int len=grammar[nonTerminals_vector[i]].size();
		for(int j=0;j<len;j++)
		{
			temp=first_expr(grammar[nonTerminals_vector[i]][j]);
			/*cout << grammar[nonTerminals_vector[i]][j]<<"\t:\t";
			  for(it=temp.begin();it!=temp.end();it++)
			  cout << *it<<"\t";
			  cout << endl;
			  */
			for(it=temp.begin();it!=temp.end();it++)
			{
				if(*it!="#")
				{
					l=lookTerminal_vector(*it);
					parseTable[i][l]=grammar[nonTerminals_vector[i]][j];
				}
				else
				{
					temp_follow.insert(follow_set[nonTerminals_vector[i]].begin(),follow_set[nonTerminals_vector[i]].end());
					for(it_follow=temp_follow.begin();it_follow!=temp_follow.end();it_follow++)
					{
						l=lookTerminal_vector(*it_follow);
						parseTable[i][l]=grammar[nonTerminals_vector[i]][j];
					}
					temp_follow.erase(temp_follow.begin(),temp_follow.end());
				}
			}
		}
	}
	cout << "\t ";
	for(int k=0;k<Terminal_count;k++)
	{
		cout << Terminals_vector[k]<<"\t|\t";
	}
	cout << endl;
	for(int i=0;i<nonTerminal_count;i++)
	{
		cout << nonTerminals_vector[i]<<"\t:";
		for(int j=0;j<Terminal_count;j++)
		{
			if(parseTable[i][j].length()==0)
				cout << "empty"<<"\t|\t";
			else
				cout << parseTable[i][j]<<"\t|\t";
		}
		cout << endl<<endl;
	}


}


int lookNonTerminal_vector(string s)
{
	for(int i=0;i<nonTerminals_vector.size();i++)
	{
		if(nonTerminals_vector[i]==s)
			return i;
	}
	return -1;
}

void stack_parsing()
{
	string line;
	getline(cin,line);
	line+=" $";
	vector<string> elems;
	vector<string> st_push;
	split(line,' ',elems);
	stack<string> stk;
	stack<string> temp_stk;
	stk.push("$");
	stk.push(nonTerminals_vector[0]);
	int i=0;
	int l,m;
	string temp;
	bool flag=0;
	while(stk.top()!="$")
	{
		if(stk.top()!=elems[i])
		{
			temp_stk=stk;
			while(!temp_stk.empty())
			{
				cout<<temp_stk.top()<<"\t";
				temp_stk.pop();
			}
			cout<<endl;
			temp=stk.top();
			stk.pop();
			l=lookNonTerminal_vector(temp);
			m=lookTerminal_vector(elems[i]);
			if(l==-1 || m==-1)
			{
				flag=1;
				break;
			}
			if(parseTable[l][m]=="#")
				continue;
			split(parseTable[l][m],' ',st_push);
			for(int j=st_push.size()-1;j>=0;j--)
			{
				stk.push(st_push[j]);
			}
			st_push.erase(st_push.begin(),st_push.end());

		}
		else
		{
			temp_stk=stk;
			while(!temp_stk.empty())
			{
				cout<<temp_stk.top()<<"\t";
				temp_stk.pop();
			}
			cout<<endl;
			temp=stk.top();
			stk.pop();
			i++;
		}
	}
	if(!flag)
	{
		temp_stk=stk;
		while(!temp_stk.empty())
		{
			cout<<temp_stk.top()<<"\t";
			temp_stk.pop();
		}
		cout<<endl;
		if(stk.top()==elems[i] && elems[i]=="$")
			cout << "accepted\n";
		else
			cout << "error\n";
	}
	else
		cout << "error\n";
}

//left factoring
int longest_subsequence(string nonT){
	int l_size = grammar[nonT].size();
	int old_len = 0,new_len = 0;
	longest_sub = "";
	string seq = "";
	for(int i=0;i<l_size;i++){
		for(int j=i+1;j<l_size;j++){
			new_len = 0;
			seq = "";
			for(int k=0;k<grammar[nonT][i].size();k++){
				if(grammar[nonT][i][k] == grammar[nonT][j][k]){
					seq += grammar[nonT][i][k];
					new_len++;
				}
				else
					break;
			}
			if(old_len < new_len){
				old_len = new_len;
				longest_sub = seq;
				//cout << longest_sub << "-->" << grammar[nonT][j] << endl;
			}
		}
	}
	return old_len;
}

void left_fac(){
	int long_sub;
	for(int i=0;i<nonTerminals_vector.size();i++){
		int count = 0;
		while(1){
			longest_sub = "";
			long_sub = longest_subsequence(nonTerminals_vector[i]);
			string seq = longest_sub;
			longest_sub = "";
			if(long_sub > 0){
				string newNT = nonTerminals_vector[i]+"_fac";
				for(int j=0;j<count;j++){
					newNT += "_fac";
				}
				vector<string> n_push;
				n_push.clear();
				int flag = 0;
				for(int j=0;j<grammar[nonTerminals_vector[i]].size();j++){
					string str = grammar[nonTerminals_vector[i]][j];
					if(str.compare(0,seq.length(),seq)==0){
						string old_seq = grammar[nonTerminals_vector[i]][j];
						if(flag == 0){
							grammar[nonTerminals_vector[i]][j] = seq + newNT + " ";
							flag = 1;
						}
						else{
							grammar[nonTerminals_vector[i]].erase(grammar[nonTerminals_vector[i]].begin()+j);
							j--;
						}
						string change = old_seq.substr(seq.length(),old_seq.length());
						if(change==""){
							change = "#";
						}
						n_push.push_back(change);
					}
				}
				nonTerminals_vector.push_back(newNT);
				grammar[newNT] = n_push;
				count++;
			}
			else
				break;
		}
	}
	
	for(int i=0;i<nonTerminals_vector.size();i++)
	{
		cout <<nonTerminals_vector[i]<<"\t : \t";
		int len=grammar[nonTerminals_vector[i]].size();
		//cout << len<<endl;
		for(int j=0;j<len;j++)
		{
			cout << grammar[nonTerminals_vector[i]][j]<<"\t|\t";
		}
		cout << endl;
	}
}
