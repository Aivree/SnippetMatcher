#include <iostream>
#include <fstream>
#include <string>
#include <sstream>
#include <vector>
#include <stdlib.h>
#include <time.h>

using namespace std;
const string policy_strings[] = {"Scarlet", "Grey", "Black"};
int total_changes = 0;

struct transition {
    int from_state;
    int to_state;
    int action;
    float probability;
} transition_table[19];

struct description {
    int id;
    string name;
    string pic;
    float reward;
    string remark;
    bool is_terminating;
} place_descriptions[10];

struct state_utility {
    int id;
    float utility;
    int policy;
} policy_table[10];


vector<string> &split(const string &s, char delim, vector<string> &elems) {
    stringstream ss(s);
    string item;
    while(getline(ss, item, delim)) {
        elems.push_back(item);
    }
    return elems;
}


vector<string> split(const string &s ) {
    char delim = ',';
    vector<string> elems;
    return split(s, delim, elems);
}


void update_transitions(string line, int i){
    vector<string> trans = split(line);
    transition_table[i].from_state = atoi(trans.at(0).c_str());
    if(trans.at(1) == "S")
        transition_table[i].action = 0;
    else if(trans.at(1) == "G")
        transition_table[i].action = 1;
    else
        transition_table[i].action = 2;

    transition_table[i].to_state = atoi(trans.at(2).c_str());
    transition_table[i].probability = atof(trans.at(3).c_str());
}


void update_desc(string line, int i){
    vector<string> descs = split(line);
    place_descriptions[i].id = atoi(descs.at(0).c_str());
    place_descriptions[i].name = descs.at(1);
    place_descriptions[i].pic = descs.at(2);
    place_descriptions[i].reward = atof(descs.at(3).c_str());
    if(descs.size() > 4)
        place_descriptions[i].remark = descs.at(4);
}



void read_file(void){

    string line;
    ifstream transfile ("problem1.transitions");
    ifstream descfile ("problem1.desc");
    if (transfile.is_open())
    {
        int i = 0;
        while ( i < 18 && !transfile.eof())
        {
        getline (transfile,line);
        //cout << line << endl;
        update_transitions(line, i);
        i++;
        }
    transfile.close();
    }

    if (descfile.is_open())
    {
        int i = 0;
        while ( i < 10)
        {
        getline (descfile,line);
        update_desc(line, i);
        //cout << line << endl;
        i++;
        }
    descfile.close();

    }
}
void set_terminal(int state){
    place_descriptions[state-1].is_terminating = true;
}

void init(){
    srand ( time(NULL) );
    read_file();
    for(int i=0; i<10; i++){
        place_descriptions[i].is_terminating = false;
        policy_table[i].id = i;
        policy_table[i].utility = 0;
        policy_table[i].policy = -1;

    }
    set_terminal(1);
    set_terminal(3);
    set_terminal(9);
    set_terminal(10);
}

void print_policy(){
    cout<<endl<<endl;
    for(int i=0; i<10; i++){
        cout<<"State "<< policy_table[i].id+1 << ":\tUtility = " << policy_table[i].utility << "\t\tPolicy = " ;
        if(policy_table[i].policy != -1)
            cout<< policy_strings[policy_table[i].policy]<<endl;
        else
            cout<<"-"<<endl;
    }
    cout<<endl<<endl;
}

int random_action(){
    return rand()%3;
}

int random_state(){
    return rand()%10 +1;
}

int best_move(int state){
    int action =-1;
    float util = -10000.00;
    //cout<< "IN best move"<<endl;
    for(int i=0; i<18;i++){

        if(transition_table[i].from_state == state){
            //cout<< policy_table[transition_table[i].to_state-1].utility<<endl;
            if(policy_table[transition_table[i].to_state-1].utility > util){
                //cout<<i <<"Before: "<< util<<"\t" << action<<"\t"<< policy_table[transition_table[i].to_state-1].utility <<endl;
                util = policy_table[transition_table[i].to_state -1].utility ;
                action = transition_table[i].action;//transition_table[i].to_state;
                //cout << "After : "<< util<<"\t" << action<< "\t"<<policy_table[transition_table[i].to_state-1].utility <<endl;
            }
        }
    }
    //printf("done");
    return action;
}

void update_policy(int state, int policy){
    policy_table[state-1].policy = policy;
}

int find_transition(int init_state, int move){
    int state;
    for(int i=0; i<18;i++){
        if(transition_table[i].from_state == init_state && transition_table[i].action == move) return transition_table[i].to_state;
    }
    return -1;
}

int update_utility(int id){
    float util = 0;
    int bestmove;
    int endstate ;
    util += place_descriptions[id-1].reward;
    //cout <<"Is terminating : " << place_descriptions[id].is_terminating << endl;
    cout<<"State : "<< id;
    if(place_descriptions[id-1].is_terminating ==  false){
        bestmove = best_move(id);
        cout << "  Best: "<< policy_strings[bestmove] <<"  Utility("<<id<<") = ";
        update_policy(id,bestmove);
        endstate = find_transition(id,bestmove);
        cout << util << " + (1 * " << policy_table[endstate-1].utility << ") = ";
        util += policy_table[endstate-1].utility;
        cout<< util <<endl;
        if(policy_table[id-1].utility != util)
            total_changes++;
        policy_table[id-1].utility = util;
        //cout<< "End state: " <<endstate<<endl;
        return endstate;
    };
    cout << "  Utility ("<<id<<") = "<< util << "  Terminating."<<endl;
    policy_table[id-1].utility = util;
    return -1;
}

int main(void) {
    init();
    int current_state;
    int num_random_restarts =0;
    do{
        total_changes = 0;
        current_state = random_state();
        //cout<<"Starting from state: "<<current_state<<endl;
        while(current_state != -1){
            current_state = update_utility(current_state);

        }
        num_random_restarts++;
    }while(total_changes != 0 || num_random_restarts < 100);
    print_policy();
    return 0;
}
