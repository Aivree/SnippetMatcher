#pragma once

//-----------------------------------------------------------------------------
// Some convenient utility functions that have geneneral applicability.
// Eventually, all of these should be moved into a more discriminative file
// within the utilities subdirectory, but for now this file contains a
// hodgepodge of convenient mothods and classes.
//-----------------------------------------------------------------------------

// ROS includes
#include <geometry_msgs/WrenchStamped.h>

// MLR code includes
//#include <Core/array.h>

// Standard includes
#include <exception>
#include <iostream>
#include <sstream>
#include <string>
#include <vector>

namespace amd_robotics {


//-----------------------------------------------------------------------------
// User input.
//-----------------------------------------------------------------------------


inline void QueryUserKeypress() {
  std::cout << "Press any key to continue..." << std::flush;
  std::cin.get();
}

inline char QueryUserContinue() {
  std::cout << "E(x)it or (c)ontinue: " << std::flush;
  return std::cin.get();
}

//-----------------------------------------------------------------------------
// String manipulation.
//-----------------------------------------------------------------------------

// Trim from start (operates on the input parameter).
static inline std::string &ltrim(std::string &s) {
  s.erase(s.begin(), std::find_if(s.begin(), s.end(), std::not1(std::ptr_fun<int, int>(std::isspace))));
  return s;
}

// Trim from end (operates on the input parameter).
static inline std::string &rtrim(std::string &s) {
  s.erase(std::find_if(s.rbegin(), s.rend(), std::not1(std::ptr_fun<int, int>(std::isspace))).base(), s.end());
  return s;
}

// Trim from both ends (operates on the input parameter).
static inline std::string &trim(std::string &s) {
  return ltrim(rtrim(s));
}

//-----------------------------------------------------------------------------
// Some computational dynamics utilities.
//-----------------------------------------------------------------------------


// Returns the Euclidean norm of the linear force components of the wrench.
double NormLinearForce(const geometry_msgs::Wrench& wrench);

/*
// Finite differenced velocities with specific time profiles.
void FiniteDifferenceVelocities(arr& v, const arr& q, const std::vector<double>& times);

// Finite differenced accelerations with specific time profiles.
void FiniteDifferenceAccelerations(arr& a, const arr& q, const std::vector<double>& times);
*/

void Interpolate(double start_value, double end_value, double count,
    std::vector<double>* values);

// Calculate the cumulative sum of the provided values. Cumulative values are *appended#
// to the provided cumulative_values vector.
void CumulativeSum(const std::vector<double>& values, std::vector<double>* cumulative_values);


//-----------------------------------------------------------------------------
// Some simple convolution tools. These tools cater more toward readability and 
// ease of use than efficiency.
//-----------------------------------------------------------------------------

class GaussianKernel {
public:
  GaussianKernel() {}
  GaussianKernel(double std_dev) {
    Initialize(std_dev);
  }

  // The final kernel will always be 2*ceil(3*std_dev) + 1 elements in width So that 
  // 3 standard deviations of the gaussian  are accounted for.  By construction, the 
  // standard deviations are in units of cells.
  void Initialize(double std_dev) {
    std_dev_ = std_dev;
    elements_.resize(2*ceil(3*std_dev) + 1);

    for (int i = MinIndex(); i <= MaxIndex(); ++i) {
      operator[](i) = exp(-0.5 * i*i / (std_dev_*std_dev_));
    }

    NormalizeElements();
  }

  double SumElements() const {
    double sum = 0.;
    for (unsigned int i = 0; i < elements_.size(); ++i) {
      sum += elements_[i];
    }
    return sum;
  }

  void DivideElements(double divisor) {
    for (unsigned int i = 0; i < elements_.size(); ++i) {
      elements_[i] /= divisor;
    }
  }

  void NormalizeElements() {
    DivideElements(SumElements());
  }

  int MaxIndex() const {
    return elements_.size()/2;
  }
  int MinIndex() const {
    return -MaxIndex();
  }

  void PrintElements() const {
    std::cout << "Gaussian kernel elements:" << std::endl;
    for (int i = MinIndex(); i <= MaxIndex(); ++i) {
      std::cout << "index " << i << " kernel value: " << (*this)[i] << std::endl;
    }
    std::cout << std::endl;
  }

  // Index should range from negative elements to the same magnitude positive value.
  // The center of the kernel is at 0.
  double operator[](int i) const {
    int index = i + elements_.size()/2;
    return elements_[index];
  }
  double& operator[](int i) {
    return elements_[i + elements_.size()/2];
  }
protected:
  std::vector<double> elements_;
  double std_dev_;
};

// Provides out of range access to a vector by returning end point values for
// out of range accesses.
class BufferedVector {
public:
  BufferedVector() {}
  BufferedVector(std::vector<double>* vec) {
    Initialize(vec);
  }

  void Initialize(std::vector<double>* vec) {
    vec_ = vec;
  }

  const std::vector<double>& vec() const { return *vec_; }

  double operator[](int i) const {
    if (i < 0) { 
      return vec_->front();
    }
    if (i >= static_cast<int>(vec_->size())) {
      return vec_->back();
    }
    return (*vec_)[i];
  }

  unsigned int size() const { return vec_->size(); }

  // Out of range mutable accesses are possible, and the returned reference
  // will be referring to a variable storing the value of the corresponding
  // vector end-point, but modifying the variable doesn't affect the internal
  // vector, itself. However, in-range accesses do provide direct access to the
  // internal vector.
  double& operator[](int i) {
    if (i < 0) { 
      dummy_value_ = vec_->front();
      return dummy_value_;
    }
    if (i >= static_cast<int>(vec_->size())) {
      dummy_value_ = vec_->back();
      return dummy_value_;
    }
    return (*vec_)[i];
  }

protected:
  std::vector<double>* vec_;
  double dummy_value_;  // For out-of-range mutable accesses.
};

// Performs a one-dimensional convolution inline on the provided values vector.
void InlineConvolve(const GaussianKernel& kernel, std::vector<double>* values);


//-----------------------------------------------------------------------------
// Print formatting convenience functions and classes.
//-----------------------------------------------------------------------------

// The sole purpose of this class is to use it in printing with an overloaded
// stream operator (operator<<). It's convenient for modularizing common fancy
// formatting.
class BracketFormatter {
public:
  BracketFormatter(int index, const std::string& suffix) :
      index_(index), suffix_(suffix) {}
  int index() const { return index_; }
  const std::string& suffix() const { return suffix_; }

  std::ostream& Print(std::ostream& os) const {
    os << "[" << index_ << "]" << suffix_;
    return os;
  }
private:
  int index_;
  std::string suffix_;
};

inline std::ostream& operator<<(std::ostream& os, const BracketFormatter& index_formatter) {
  return index_formatter.Print(os);
}

inline std::ostream& operator<<(std::ostream& os, const std::vector<double>& v) {
  for (unsigned int i = 0; i < v.size(); ++i) {
    os << "[" << v[i] << "]";
  }
  return os;
}

}  // namespace amd_robotics
