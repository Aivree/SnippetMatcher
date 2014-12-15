/* 
 * File:   MassiveSort.h
 * Author: cthaw
 *
 * Created on March 26, 2014, 9:45 PM
 */

#ifndef MASSIVESORT_H
#define	MASSIVESORT_H
#include <string.h>
#include <fstream>
#include <vector>
#include <cstdlib>
class MassiveSort {
    
public:
    
    MassiveSort(char * outfile, int count); 
    bool sort(char ** sortFiles, int n);
    bool validate_sort(char * file);
    
// private:
    std::vector<std::string> values;
    char * outfile;
    int count;
    
    bool gt(std::string val, std::string cmp);
    bool lt(std::string val, std::string cmp);
    bool isInteger(const char * value);
    bool write(char * ofile);
    void display_values();
    bool selection_sort();
    bool m_bubble_sort();
    bool bubble_sort();
    
    
    
};

MassiveSort::MassiveSort(char * outfile, int count) {
    this->outfile = outfile;
    this->count = count;
    
}

bool MassiveSort::validate_sort(char * file) {
    
    bool status = true, gt=false, lt=false;
    std::ifstream in;
    in.open(file, std::ios::in);
    std::string data, tmp;
    tmp.assign("\0");
    
    if (in.is_open()) {
        
        while (!in.eof() && status) {
            if (tmp == "\0") {
                in >> tmp;
                status = isInteger(tmp.c_str());
            }
            
            else {
                in >> data;
                status = isInteger(data.c_str());
                if (!gt && !lt) {
                    gt = this->gt(data, tmp);
                    lt = this->lt(data, tmp);
                }
                else if (tmp == data) { ; /* Do Nothing */ }
                else {
                    status = (gt) ? this->gt(data.c_str(), tmp.c_str()) : this->lt(data.c_str(), tmp.c_str());
                }
                
                tmp.assign(data);
            }
        }
        
    }
    else {
        status = false;
    }
    
    return status;
}

bool MassiveSort::bubble_sort() {
    bool status = true;
    int length = values.size();
    
    for (int i=0; i < length && status; i++) {
        for (int j=0; j < length-1 && status; j++) {
        //    status = isInteger(values[j].c_str()) && isInteger(values[j+1].c_str());
            if (status && lt(values[j], values[j+1])) {
                std::string tmp = values[j];
                values[j] = values[j+1];
                values[j+1] = tmp;
            }
        }
    }
    
    return status;
}

bool MassiveSort::selection_sort() {
    
    bool status = true;
    int length = values.size();
    for (int i=0; i < length && status; i++) {
        int index = i;
        for (int j=i; j < length && status; j++) {
            // status = isInteger(values[i].c_str()) && isInteger(values[j].c_str());      
            if (status) {
                index = (lt(values[index], values[j]) ? j : index);
            }
        }
        
        std::string tmp = values[index];
        values[index] = values[i];
        values[i] = tmp;
    } 
    
    return status;
}

void MassiveSort::display_values() {
    
    int length = values.size();
    for (int i=0; i < length; i++) {
        printf("%s ", values[i].c_str());
    }
}

bool MassiveSort::sort(char ** sortFiles, int n) {
    
    bool status = true;
    int i=0;
    
    values.reserve(count);
    std::ifstream input;
    
    while (i < n) {
        
        input.open(sortFiles[i], std::ios::in);
        if (input.is_open()) {
            int j=0;
            
            while (j < count && !input.eof()) {
                std::string data;
                input >> data;
                
                values.push_back(data);
                j++;
            }

            input.close();
        }
        else { return false; }
        char * tmpfile = new char[10];
        strcpy(tmpfile, "outfile");
        strcat(tmpfile, "1");
        printf("File completely loaded. Starting Sorting Algorithm\n");
        // SORT METHOD SELECTION
        status = selection_sort();
        
        printf("%s\n", sortFiles[i]);
        this->write(outfile);
        i++;
    }
    
    return status;
}

bool MassiveSort::write(char * ofile) {
    bool status = true;
    std::ofstream output;
    output.open(ofile, std::ios::app);
    
    if (output.is_open()) {
        int length = values.size();
        for (int i=0; i < length; i++) {
            output << values[i] << " ";
        }
        
        output.close();
    }
    
    else {
        status = false;
    }
    
    return status;
}

bool MassiveSort::gt(std::string val, std::string cmp) {
    
    bool gt = true;
    
    if (val == cmp) { return false;}
    
    const char * v = val.c_str();
    const char * c = cmp.c_str();
    /* *
    int vlen = val.size();
    int clen = cmp.size();
    int len = (vlen > clen) ? vlen : clen;
    
    for (int i=0; i < len && gt; i++) {
        
        if (v[i] != c[i] && (int)v[i] > (int)c[i]) { return true; }
        else if (v[i] != c[i] && (int)v[i] < (int)c[i]) { gt = false; }
    }
    /* */
    int value = atoi(v);
    int comp = atoi(c);
    gt = (value > comp);
    /* */
    return gt;
}

bool MassiveSort::lt(std::string val, std::string cmp) {
    
    if (val == cmp) { return false; }
    return atoi(val.c_str()) < atoi(cmp.c_str());
}

bool MassiveSort::isInteger(const char * value) {
    
    int length = strlen(value);
    bool ret=true;
    int i=0;
    
    while (i < length && ret) {
        ret = isdigit(value[i++]);
    }
    
    return ret;
}
#endif	/* MASSIVESORT_H */