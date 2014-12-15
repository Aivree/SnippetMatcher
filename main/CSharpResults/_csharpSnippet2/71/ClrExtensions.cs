// Copyright (c) Mpcr Enterprises. All rights reserved.
//
// This source file is part of Mpcr Enterprises software, and is protected 
// by copyright laws and international treaties. Unauthorized reproduction or 
// distribution of this source file, or any portion of it, is strictly prohibited 
// and may result in severe civil and criminal penalties. 

using System;
using System.Collections;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Collections.Specialized;
using System.ComponentModel;
using System.Diagnostics;
using System.Globalization;
using System.Linq;
using System.Security;
using System.Text;
using System.Text.RegularExpressions;
using Mpcr.Core.Collections;
using Mpcr.Core.Language;

namespace Mpcr.Core {

   /// <summary>A static class that provides a collection of extension methods for Common Language Runtime (CLR) classes.</summary>
   [DebuggerNonUserCode]
   public static class ClrExtensions {

      #region Bool Extensions
      /// <summary>Checks whether this nullable value is <c>false</c>.</summary>
      /// <param name="value">The value to check.</param>
      /// <returns><c>true</c> if the value is <c>false</c>, or <c>false</c> if the value is <c>true</c> or <c>null</c>.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsFalse(this bool? value) {
         return (value.HasValue && !value.Value);
      }

      /// <summary>Checks whether this nullable value is <c>true</c>.</summary>
      /// <param name="value">The value to check.</param>
      /// <returns><c>true</c> if the value is <c>true</c>, or <c>false</c> if the value is <c>false</c> or <c>null</c>.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsTrue(this bool? value) {
         return (value.HasValue && value.Value);
      }
      #endregion

      #region DateTime Extensions
      /// <summary>Checks whether two values are close enough to be considered equal by taking into account precision and the chance for floating-point errors.</summary>
      /// <param name="value1">The first value to check.</param>
      /// <param name="value2">The second value to check.</param>
      /// <param name="precision">The check precision.</param>
      /// <returns><c>true</c> if the values are close enough to be considered equal; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool AlmostEquals(this DateTime value1, DateTime value2, TimeSpan precision) { 
         return Math.Abs((value1 - value2).TotalMilliseconds).AlmostEquals(precision.TotalMilliseconds); 
      }

      /// <summary>Checks whether this datetime value is empty.</summary>
      /// <param name="value">The datetime value to check.</param>
      /// <returns><c>true</c> if the datetime value is empty; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsEmpty(this DateTime value) {
         return value == DateTime.MinValue;
      }

      /// <summary>Returns time span, elapsed from the other DateTime.</summary>
      /// <param name="value">The datetime value to check.</param>
      /// <param name="start">The starting DateTime.</param>
      /// <returns>Elapsed time, since <c>start</c>, or <see cref="TimeSpan.Zero"/> if <c>start</c> occurs later than this DateTime.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan ElapsedFrom(this DateTime value, DateTime start) {
         if (value < start) return TimeSpan.Zero;
         return value.Subtract(start);
      }

      /// <summary>Returns time span, elapsed from the other DateTimeOffset.</summary>
      /// <param name="value">The DateTimeOffset value to check.</param>
      /// <param name="start">The starting DateTimeOffset.</param>
      /// <returns>Elapsed time, since <c>start</c>, or <see cref="TimeSpan.Zero"/> if <c>start</c> occurs later than this DateTime.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan ElapsedFrom(this DateTimeOffset value, DateTimeOffset start) {
         if (value < start) return TimeSpan.Zero;
         return value.Subtract(start);
      }
      #endregion

      #region Dictionary Extensions
      /// <summary>Tries to get the value with the specified key in this dictionary.</summary>
      /// <param name="dictionary">The dictionary to get the value from.</param>
      /// <param name="key">The key of the value to get.</param>
      /// <returns>The requested value, or the default value if not found.</returns>
      public static V TryGet<K,V>(this Dictionary<K, V> dictionary, K key) {
         return dictionary.TryGet(key, default(V));
      }

      /// <summary>Tries to get the value with the specified key in this dictionary.</summary>
      /// <param name="dictionary">The dictionary to get the value from.</param>
      /// <param name="key">The key of the value to get.</param>
      /// <param name="defaultValue">The default value to return in case the requested value was not found.</param>
      /// <returns>The requested value, or the specified default value if not found.</returns>
      public static V TryGet<K, V>(this Dictionary<K, V> dictionary, K key, V defaultValue) {
         V value;
         if (dictionary != null && dictionary.TryGetValue(key, out value)) {
            return value;
         } else {
            return defaultValue;
         }
      }
      #endregion

      #region Double Extensions
      // the smallest value considered not equal to zero
      // according to number theory should be 2.2204460492503131E-15, but this is real-life.
      private const double _zeroEpsilon = 1E-6;    

      /// <summary>Checks whether two numbers are close enough to be considered equal. Use this method instead of strict 
      /// equality check to reduce the chance for floating-point errors.</summary>
      /// <param name="value1">The first number to check.</param>
      /// <param name="value2">The second number to check.</param>
      /// <returns><c>true</c> if the numbers are close enough to be considered equal; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool AlmostEquals(this double value1, double value2) {
         if (Object.Equals(value1, value2)) return true;
         if (Double.IsNaN(value1) || Double.IsNaN(value2)) return false;
         if (Double.IsInfinity(value1) || Double.IsInfinity(value2)) return false;
         return Math.Abs(Math.Abs(value1) - Math.Abs(value2)) < _zeroEpsilon;
      }

      /// <summary>Checks whether two numbers are close enough to be considered equal. Use this method instead of strict 
      /// equality check to reduce the chance for floating-point errors.</summary>
      /// <param name="value1">The first number to check.</param>
      /// <param name="value2">The second number to check.</param>
      /// <returns><c>true</c> if the numbers are close enough to be considered equal; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool AlmostEquals(this double? value1, double? value2) {
         if (value1.HasValue && value2.HasValue) {
            return value1.Value.AlmostEquals(value2.Value);
         } else {
            return !value1.HasValue && !value2.HasValue;
         }
      }

      /// <summary>Checks whether two numbers are far enough to be considered different. Use this method instead of strict 
      /// equality check to reduce the chance for floating-point errors.</summary>
      /// <param name="value1">The first number to check.</param>
      /// <param name="value2">The second number to check.</param>
      /// <returns><c>true</c> if the numbers are far enough to be considered different; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool CertainlyDifferent(this double value1, double value2) {
         return !AlmostEquals(value1, value2);
      }

      /// <summary>Checks whether two numbers are far enough to be considered different. Use this method instead of strict 
      /// equality check to reduce the chance for floating-point errors.</summary>
      /// <param name="value1">The first number to check.</param>
      /// <param name="value2">The second number to check.</param>
      /// <returns><c>true</c> if the numbers are far enough to be considered different; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool CertainlyDifferent(this double? value1, double? value2) {
         return !AlmostEquals(value1, value2);
      }

      /// <summary>Checks whether this number is big enough enough to be considered greater than the second number.
      /// Use this method instead of strict comparison to reduce the chance for floating-point errors.</summary>
      /// <param name="value1">The first number to check.</param>
      /// <param name="value2">The second number to check.</param>
      /// <returns><c>true</c> if the first number is big enough to be considered greater than the second number; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool CertainlyGreater(this double value1, double value2) {
         if (value1 > value2) {
            return !value1.AlmostEquals(value2);
         }
         return false;
      }

      /// <summary>Checks whether this number is small enough enough to be considered smaller than the second number.
      /// Use this method instead of strict comparison to reduce the chance for floating-point errors.</summary>
      /// <param name="value1">The first number to check.</param>
      /// <param name="value2">The second number to check.</param>
      /// <returns><c>true</c> if the first number is small enough to be considered less than the second number; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool CertainlySmaller(this double value1, double value2) {
         if (value1 < value2) {
            return !value1.AlmostEquals(value2);
         }
         return false;
      }

      /// <summary>Returns the smallest whole number that is greater than or equal to this number.</summary>
      /// <param name="value">The number to find the ceiling for.</param>
      /// <returns>The ceiling value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double Ceiling(this double value) {
         return Math.Ceiling(value);
      }

      /// <summary>Returns the smallest number, at the specified precision, that is greater than or equal to this number.</summary>
      /// <param name="value">The number to find the ceiling for.</param>
      /// <param name="precision">The ceiling precision.</param>
      /// <returns>The ceiling value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double Ceiling(this double value, int precision) {
         return (precision == 0) ? Math.Ceiling(value) : Math.Ceiling(value * Math.Pow(10, precision)) / Math.Pow(10, precision);
      }

      /// <summary>Returns ceiling of this number to the specified fractional multiple, using specified number of multple's digits.</summary>
      /// <param name="value">The number to find the ceiling for.</param>
      /// <param name="precision">Number of digits, after the decimal point, of the multiple to use.</param>
      /// <param name="multiple">The multiple to use during ceiling.</param>
      /// <returns>The ceiling value.</returns>
      /// <remarks>This method allows to find ceiling to closest fractional value. See <see cref="Round(double, int, double)"/> for details.
      /// This method never throws an exception.</remarks>
      public static double Ceiling(this double value, int precision, double multiple) {
         var pow = Math.Pow(10, precision);
         var val = value * pow;
         var mul = (int)(multiple * pow);
         var res = (mul == 0) ? 0 : (int)(Math.Ceiling((double)val / mul) * mul);
         return res / pow;
      }

      /// <summary>Computes the five-number summary of the given value set.</summary>
      /// <param name="values">The value set.</param>
      /// <returns>The five-number summary array, or an array of five <c>undefined</c> numbers in case the value set is empty.</returns>
      /// <remarks>The five-number summary is an array containing the minimum, the first quartile, the median, the third quartile and the maximum value of the set. Undefined values are skipped.</remarks>
      public static double[] FiveNumberSummary(this IEnumerable<double> values) {
         if (values == null) return new double[5] { double.NaN, double.NaN, double.NaN, double.NaN, double.NaN };

         var A = values.Where(v => !double.IsNaN(v)).ToArray();
         var N = A.Length;
         if (N == 0) return new double[5] { double.NaN, double.NaN, double.NaN, double.NaN, double.NaN };
         if (N == 1) {
            var a = A[0];
            return new double[5] { a, a, a, a, a };
         }
         Array.Sort(A);

         Func<double, double> at = (k) => {
            var i0 = (int)k;
            if (i0 == k) return A[i0];

            var i1 = (i0 + 1);
            var r = i1 - k;
            return (A[i0] * r) + A[i1] * (1 - r);
         };

         // There is no aggreement, between statisticans, on how first/third quartiles should be computed
         // - Excel (until 2010) uses "N-1 method" (used below)
         // - Excel 2010 allows to chose between "N-1 method" and "N+1 method"
         // - WolframAlpha uses "N" method:
         // - TI-83 calculator uses "N method" for even N and "N+1 method" for odd N:
         //
         // More info:
         // - http://dsearls.org/other/CalculatingQuartiles/CalculatingQuartiles.htm
         // - http://en.wikipedia.org/wiki/Quartile
         // - http://mathforum.org/library/drmath/view/60969.html
         // - http://mathworld.wolfram.com/Quartile.html
         //
         // The method used below is "N-1 method", because it gives the most intuitive results
         // when looking at quartiles and min/max valus together. For example:
         // - "N-1" (1, 2, 3, 4, 5) = {1, 2, 3, 4, 5}
         // - "N" (1, 2, 3, 4, 5)   = {1, 1.75, 3, 4.25, 5} (unequal distance from min to q1 vs. q1 to median)
         // - "N-1" (1, 2, 3) = {1, 1.5, 2, 2.5, 3}
         // - "N" (1, 2, 3)   = {1, 1, 2, 3, 3} (min = q1, q3 = max)
         //
         var len = N - 1;
         return new double[5] { at(0), at(_q1 * len), at(_q2 * len), at(_q3 * len), at(len) };
      }
      private const double _q1 = 1.0 / 4.0;
      private const double _q2 = 2.0 / 4.0;
      private const double _q3 = 3.0 / 4.0;

      /// <summary>Returns the largest whole number that is smaller than or equal to this number.</summary>
      /// <param name="value">The number to find the ceiling for.</param>
      /// <returns>The floor value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double Floor(this double value) {
         return Math.Floor(value);
      }

      /// <summary>Returns the largest number, at the specified precision, that is less than or equal to this number.</summary>
      /// <param name="value">The number to find the floor for.</param>
      /// <param name="precision">The floor precision.</param>
      /// <returns>The floor value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double Floor(this double value, int precision) {
         return (precision == 0) ? Math.Floor(value) : Math.Floor(value * Math.Pow(10, precision)) / Math.Pow(10, precision);
      }

      /// <summary>Floors this number to the specified fractional multiple, using specified number of multple's digits.</summary>
      /// <param name="value">The number to floor.</param>
      /// <param name="precision">Number of digits, after the decimal point, of the multiple to use.</param>
      /// <param name="multiple">The multiple to floor to.</param>
      /// <returns>The floor value.</returns>
      /// <remarks>This method allows to floor to closest fractional value. See <see cref="Round(double, int, double)"/> for details.
      /// This method never throws an exception.</remarks>
      public static double Floor(this double value, int precision, double multiple) {
         var pow = Math.Pow(10, precision);
         var val = value * pow;
         var mul = (int)(multiple * pow);
         var res = (mul == 0) ? 0 : (int)(Math.Floor((double)val / mul) * mul);
         return res / pow;
      }

      /// <summary>Checks whether this number is valid, finite, and equal to an integer. Use this method instead of strict comparison with
      /// zero to reduce the chance for floating-point errors.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is valid, finite, and equal to an integer; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsInteger(this double value) {
         return value.IsValid() && Math.Abs(Math.Round(value) - value) < _zeroEpsilon;
      }

      /// <summary>Checks whether this number is valid, finite, and negative. Use this method instead of strict comparison with
      /// zero to reduce the chance for floating-point errors.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is valid, finite, and negative; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsNegative(this double value) {
         return value.IsValid() && value < -_zeroEpsilon;
      }

      /// <summary>Checks whether this number is valid, finite, and negative or zero. Use this method instead of strict comparison with
      /// zero to reduce the chance for floating-point errors.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is valid, finite, and negative or zero; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsNegativeOrZero(this double value) {
         return value.IsValid() && value <= _zeroEpsilon;
      }

      /// <summary>Checks whether this number is equal to one. Use this method instead of strict equality check to reduce 
      /// the chance for floating-point errors.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is equal to one; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsOne(this double value) {
         return (Math.Abs((double)(value - 1.0)) < _zeroEpsilon);
      }

      /// <summary>Checks whether this number is valid, finite, and positive. Use this method instead of strict comparison with
      /// zero to reduce the chance for floating-point errors.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is valid, finite, and positive; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsPositive(this double value) {
         return value.IsValid() && value > _zeroEpsilon;
      }

      /// <summary>Checks whether this number is valid, finite, and positive or zero. Use this method instead of strict comparison with
      /// zero to reduce the chance for floating-point errors.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is valid, finite, and positive or zero; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsPositiveOrZero(this double value) {
         return value.IsValid() && value >= -_zeroEpsilon;
      }

      /// <summary>Checks whether this number is valid and finite.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is a valid and finite; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsValid(this double value) {
         return !Double.IsNaN(value) && !Double.IsInfinity(value);
      }

      /// <summary>Checks whether this number is valid and whole.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is a valid and whole; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsWhole(this double value) {
         return IsValid(value) && (value - Math.Round(value)).IsZero();
      }

      /// <summary>Checks whether this number is equal to zero. Use this method instead of strict equality check to reduce 
      /// the chance for floating-point errors.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is equal to zero; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsZero(this double value) {
         return (Math.Abs(value) < _zeroEpsilon);
      }

      /// <summary>Constrains this number to the specified lower and upper limits.</summary>
      /// <param name="value">The number to constrain.</param>
      /// <param name="lower">The lower limit (inclusive).</param>
      /// <param name="upper">The upper limit (inclusive).</param>
      /// <returns>The constrained value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double Limit(this double value, double lower, double upper) {
         if (Double.IsNaN(value)) return Double.NaN;
         if (Double.IsNaN(lower)) lower = Double.NegativeInfinity;
         if (Double.IsNaN(upper)) upper = Double.PositiveInfinity;
         if (upper < lower) upper = lower;
         if (value > upper) return upper;
         if (value < lower) return lower;
         return value;
      }

      /// <summary>Gets the largest value among this value and the other given value(s).</summary>
      /// <param name="value">The value to check.</param>
      /// <param name="otherValue">The other value to check.</param>
      /// <param name="moreValues">More values to check.</param>
      /// <returns>The largest value among all given values.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double Max(this double value, double otherValue, params double[] moreValues) {
         var max = Math.Max(value, otherValue);
         if (moreValues.Length > 0) {
            foreach (var v in moreValues) {
               max = Math.Max(max, v);
            }
         }
         return max;
      }

      /// <summary>Converts the given number of milliseconds to the corresponding timespan object.</summary>
      /// <param name="value">The number of milliseconds.</param>
      /// <returns>The timespan object that represents the given number of milliseconds.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan MilliSeconds(this double value) {
         return TimeSpan.FromMilliseconds(value);
      }

      /// <summary>Gets the smallest value among this value and the other given value(s).</summary>
      /// <param name="value">The value to check.</param>
      /// <param name="otherValue">The other value to check.</param>
      /// <param name="moreValues">More values to check.</param>
      /// <returns>The smallest value among all given values.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double Min(this double value, double otherValue, params double[] moreValues) {
         var min = Math.Min(value, otherValue);
         if (moreValues.Length > 0) {
            foreach (var v in moreValues) {
               min = Math.Min(min, v);
            }
         }
         return min;
      }

      /// <summary>Computes the median value of the given value set.</summary>
      /// <param name="values">The value set.</param>
      /// <returns>The median value, or <c>undefined</c> in case the value set is empty.</returns>
      /// <remarks>The median is the value that separates the upper half of values from the lower half of values. Undefined values are skipped.</remarks>
      public static double Median(this IEnumerable<double> values) {
         if (values == null) return Double.NaN;

         var A = values.Where(v => v.IsValid()).ToArray();
         var N = A.Length;
         if (N == 0) return Double.NaN;
         if (N == 1) return A[0];
         if (N == 2) return Math.Min(A[0], A[1]);
         
         var k = (N / 2) + (N % 2) - 1;
         var l = 0;
         var m = N - 1;

         // N. Wirth algorithm
         while (l < m) {
            var x = A[k];
            var i = l;
            var j = m;
            do {
               while (A[i] < x) i++;
               while (x < A[j]) j--;
               if (i <= j) {
                  var t = A[i];
                  A[i] = A[j];
                  A[j] = t;
                  i++;
                  j--;
               }
            } while (i <= j);
            if (j < k) l = i;
            if (k < i) m = j;
         }
         return A[k];
      }

      /// <summary>Computes the median and the median absolute deviation (MAD) of the given value set.</summary>
      /// <param name="values">The value set.</param>
      /// <param name="deviation">The median absolute deviation (MAD), or <c>undefined</c> in case the value set is empty.</param>
      /// <returns>The median value, or <c>undefined</c> in case the value set is empty.</returns>
      /// <remarks>The median is the value that separates the upper half of values from the lower half of values. The median absolute deviation (MAD) is a measure of the 
      /// statistical dispersion of the value set with respect to the median value. It is a robust statistic that behaves better than the standard deviation, being more 
      /// resilient to outliers in the value set.</remarks>
      public static double Median(this IEnumerable<double> values, out double deviation) {
         if (values == null) {
            deviation = Double.NaN;
            return Double.NaN;
         }
         var median = Median(values);
         deviation = values.Select(v => Math.Abs(v - median)).Median();
         return median;
      }

      /// <summary>Returns this number if it is valid and finite. Otherwise, returns the specified default value.</summary>
      /// <param name="value">The numeric value.</param>
      /// <param name="defaultValue">The default value.</param>
      /// <returns>The numeric value or its default value.</returns>
      public static double Nvl(this double value, double defaultValue) {
         if (value.IsValid()) return value;
         return defaultValue;
      }

      /// <summary>Rounds this number to the nearest whole value.</summary>
      /// <param name="value">The number to round.</param>
      /// <returns>The rounded value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double Round(this double value) {
         return Math.Round(value);
      }

      /// <summary>Rounds this number to the specified precision.</summary>
      /// <param name="value">The number to round.</param>
      /// <param name="precision">The rounding precision.</param>
      /// <returns>The rounded value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double Round(this double value, int precision) {
         return (precision == 0) ? Math.Round(value) : Math.Round(value * Math.Pow(10, precision)) / Math.Pow(10, precision);
      }

      /// <summary>Rounds this number to the specified fractional multiple, using specified number of multple's digits.</summary>
      /// <param name="value">The number to round.</param>
      /// <param name="precision">Number of digits, after the decimal point, of the multiple to use.</param>
      /// <param name="multiple">The multiple to round to.</param>
      /// <returns>The rounded value.</returns>
      /// <remarks>This method allows to round to closest fractional value.
      /// For example, 
      /// <list>
      ///   <item>Multiple = 0.1, Digits = 1 will round to 0.0, 0.1, 0.2, etc.</item>
      ///   <item>Multiple = 0.12, Digits = 2 will round to 0.0, 0.12, 0.24, etc.</item>
      ///   <item>Multiple = 0.1, Digits = 2 will round to 0.0, 0.1, 0.2, etc.</item>
      ///   <item>Multiple = 0.12, Digits = 1 will round to 0.0, 0.1, 0.2, etc.</item>
      ///   <item>Multiple = 5.12, Digits = 2 will round to 0.0, 5.12, 10.24, 15.36, etc.</item>
      ///   <item>Round(2.5, 1, 0.3) = 2.4 (closest 0.3-multiple of 2.5).</item>
      ///   <item>Round(70, 0, 25) = 75 (closest 25-multiple of 70).</item>
      /// </list>
      /// This method never throws an exception.</remarks>
      public static double Round(this double value, int precision, double multiple) {
         var pow = Math.Pow(10, precision);
         var val = value * pow;
         var mul = (int)(multiple * pow);
         var res = (mul == 0) ? 0 : (int)(Math.Round((double)val / mul) * mul);
         return res / pow;
      }

      /// <summary>Converts the given number of seconds to the corresponding timespan object.</summary>
      /// <param name="value">The number of seconds.</param>
      /// <returns>The timespan object that represents the given number of seconds.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan Seconds(this double value) {
         return TimeSpan.FromSeconds(value);
      }

      /// <summary>Computes the standard deviation of the given value set.</summary>
      /// <param name="values">The value set.</param>
      /// <returns>The standard deviation, or <c>undefined</c> in case the value set is empty.</returns>
      public static double StdDev(this double[] values) {
         if (values == null || values.Length == 0) return Double.NaN;
         var N = values.Length;
         if (N == 1) return 0;
         var A = values.Average();
         var B = values.Sum(v => (v - A) * (v - A));
         return Math.Sqrt(B / N);
      }

      /// <summary>Formats a number to string using standard BPL notation.</summary>
      public static string ToBplString(this double value) {
         if (!value.IsValid() || value == Double.MaxValue || value == Double.MinValue) {
            return value.ToString("r", CultureInfo.InvariantCulture);
         } else {
            return value.ToString("0.0######", CultureInfo.InvariantCulture);
         }
      }

      /// <summary>Converts an angle from radians to degrees.</summary>
      /// <param name="value">The angle in radians.</param>
      /// <returns>The angle in degrees.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double ToDegrees(this double value) {
         return value * 180.0 / Math.PI;
      }

      /// <summary>Converts an angle from degrees to radians.</summary>
      /// <param name="value">The angle in degrees.</param>
      /// <returns>The angle in radians.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static double ToRadians(this double value) {
         return value * Math.PI / 180.0;
      }
      #endregion

      #region Single extensions
      /// <summary>Checks whether this number is valid and finite.</summary>
      /// <param name="value">The number to check.</param>
      /// <returns><c>true</c> if the number is a valid and finite; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsValid(this float value) {
         return !Single.IsNaN(value) && !Single.IsInfinity(value);
      }

      /// <summary>Constrains this number to the specified lower and upper limits.</summary>
      /// <param name="value">The number to constrain.</param>
      /// <param name="lower">The lower limit (inclusive).</param>
      /// <param name="upper">The upper limit (inclusive).</param>
      /// <returns>The constrained value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static float Limit(this float value, float lower, float upper) {
         if (Single.IsNaN(value)) return Single.NaN;
         if (Single.IsNaN(lower)) lower = Single.NegativeInfinity;
         if (Single.IsNaN(upper)) upper = Single.PositiveInfinity;
         if (upper < lower) upper = lower;
         if (value > upper) return upper;
         if (value < lower) return lower;
         return value;
      }
      #endregion

      #region Enum Extensions
      /// <summary>Determines whether this enum value matches the specified mask.</summary>
      /// <typeparam name="T">The type of the enum value to check</typeparam>
      /// <param name="value">The enum value to check.</param>
      /// <param name="mask">The mask to check the enum value against.</param>
      /// <returns><c>true</c> if the enum value matches the specified mask; <c>false</c> otherwise.</returns>
      /// <remarks>This method might throw exceptions.</remarks>
      public static bool Has<T>(this Enum value, T mask) where T : struct {
         var enumType = typeof(T);
         if (enumType != value.GetType()) {
            throw new InvalidOperationException("Mask is not compatible with enum type");
         }
         var v1 = (IConvertible)value;
         var v2 = (IConvertible)mask;
         var inv = CultureInfo.InvariantCulture;
         if (enumType == typeof(int)) {
            // covers common case where underlying type is int
            var f1 = v1.ToInt32(inv);
            var f2 = v2.ToInt32(inv);
            return (f1 & f2) == f2;
         } else {
            // covers all other cases
            var f1 = v1.ToInt64(inv);
            var f2 = v2.ToInt64(inv);
            return (f1 & f2) == f2;
         }
      }

      /// <summary>Cleans the specified bit mask on this enum value.</summary>
      /// <typeparam name="T">The type of the enum value to clear.</typeparam>
      /// <param name="value">The enum value to clear.</param>
      /// <param name="mask">The bit mask to clear.</param>
      /// <returns>The new enum value with the cleared bit mask.</returns>
      /// <remarks>This method might throw exceptions.</remarks>
      public static T Clear<T>(this Enum value, T mask) where T : struct {
         var enumType = typeof(T);
         if (enumType != value.GetType()) {
            throw new InvalidOperationException("Mask is not compatible with enum type");
         }
         var v1 = (IConvertible)value;
         var v2 = (IConvertible)mask;
         var inv = CultureInfo.InvariantCulture;
         if (enumType == typeof(int)) {
            // covers common case where underlying type is int
            var f1 = v1.ToInt32(inv);
            var f2 = v2.ToInt32(inv);
            return (T)Enum.ToObject(enumType, (f1 & ~f2));
         } else {
            // covers all other cases
            var f1 = v1.ToInt64(inv);
            var f2 = v2.ToInt64(inv);
            return (T)Enum.ToObject(enumType, (f1 & ~f2));
         }
      }

      /// <summary>Sets the specified bit mask on this enum value.</summary>
      /// <typeparam name="T">The type of the enum value to set.</typeparam>
      /// <param name="value">The enum value to set.</param>
      /// <param name="mask">The bit mask to set.</param>
      /// <returns>The new enum value with the set bit mask.</returns>
      /// <remarks>This method might throw exceptions.</remarks>
      public static T Set<T>(this Enum value, T mask) where T : struct {
         var enumType = typeof(T);
         if (enumType != value.GetType()) {
            throw new InvalidOperationException("Mask is not compatible with enum type");
         }
         var v1 = (IConvertible)value;
         var v2 = (IConvertible)mask;
         var inv = CultureInfo.InvariantCulture;
         if (enumType == typeof(int)) {
            // covers common case where underlying type is int
            var f1 = v1.ToInt32(inv);
            var f2 = v2.ToInt32(inv);
            return (T)Enum.ToObject(enumType, (f1 | f2));
         } else {
            // covers all other cases
            var f1 = v1.ToInt64(inv);
            var f2 = v2.ToInt64(inv);
            return (T)Enum.ToObject(enumType, (f1 | f2));
         }
      }

      /// <summary>Toggle the specified bit mask on this enum value.</summary>
      /// <typeparam name="T">The type of the enum value to toggle.</typeparam>
      /// <param name="value">The enum value to toggle.</param>
      /// <param name="mask">The bit mask to toggle.</param>
      /// <returns>The new enum value with the toggled bit mask.</returns>
      /// <remarks>This method might throw exceptions.</remarks>
      public static T Toggle<T>(this Enum value, T mask) where T : struct {
         var enumType = typeof(T);
         if (enumType != value.GetType()) {
            throw new InvalidOperationException("Mask is not compatible with enum type");
         }
         var v1 = (IConvertible)value;
         var v2 = (IConvertible)mask;
         var inv = CultureInfo.InvariantCulture;
         if (enumType == typeof(int)) {
            // covers common case where underlying type is int
            var f1 = v1.ToInt32(inv);
            var f2 = v2.ToInt32(inv);
            return (T)Enum.ToObject(enumType, (f1 ^ f2));
         } else {
            // covers all other cases
            var f1 = v1.ToInt64(inv);
            var f2 = v2.ToInt64(inv);
            return (T)Enum.ToObject(enumType, (f1 ^ f2));
         }
      }
      #endregion

      #region EventHandler Extensions
      /// <summary>Raises a parameterless event.</summary>
      /// <param name="handler">The event handler to invoke.</param>
      /// <param name="sender">The source of the event.</param>
      public static void Raise(this EventHandler handler, object sender) {
         if (handler != null) {
            handler(sender, EventArgs.Empty);
         }
      }

      /// <summary>Raises an event that can be handled completely in an event handler.</summary>
      /// <param name="handler">The event handler to invoke.</param>
      /// <param name="sender">The source of the event.</param>
      /// <returns><c>true</c> if the event was completely handled; <c>false</c> otherwise.</returns>
      public static bool Raise(this HandledEventHandler handler, object sender) {
         if (handler != null) {
            var e = new HandledEventArgs();
            handler(sender, e);
            return e.Handled;
         }
         return false;
      }

      /// <summary>Raises a property changed event.</summary>
      /// <param name="handler">The event handler to invoke.</param>
      /// <param name="sender">The source of the event.</param>
      /// <param name="propertyName">The name of the property that has changed.</param>
      public static void Raise(this PropertyChangedEventHandler handler, object sender, string propertyName) {
         if (handler != null) {
            handler(sender, new PropertyChangedEventArgs(propertyName));
         }
      }

      /// <summary>Raises a property changed event.</summary>
      /// <param name="handler">The event handler to invoke.</param>
      /// <param name="sender">The source of the event.</param>
      /// <param name="args">The property changed event arguments.</param>
      public static void Raise(this PropertyChangedEventHandler handler, object sender, PropertyChangedEventArgs args) {
         if (handler != null) {
            handler(sender, args);
         }
      }
      #endregion

      #region ICollection Extensions
      /// <summary>Adds the given values to the end of this collection.</summary>
      /// <typeparam name="T">The type of elements in the collection.</typeparam>
      /// <param name="collection">The collection to add the values to.</param>
      /// <param name="values">The values to add to the collection.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static void AddRange<T>(this ICollection<T> collection, IEnumerable<T> values) {
         if (collection == null || values == null) return;
         // if values is a BPL collection, then it's a move operation, so we have to copy aside first
         if (values.IsA(typeof(BplCollection<>))) {
            values.ToArray().Apply(value => collection.Add(value));
         } else {
            values.Apply(value => collection.Add(value));
         }
      }

      /// <summary>Adds the given values to the end of this collection.</summary>
      /// <typeparam name="T">The type of elements in the collection.</typeparam>
      /// <param name="collection">The collection to add the values to.</param>
      /// <param name="values">The values to add to the collection.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static void AddRange<T>(this ICollection<T> collection, params T[] values) {
         if (collection == null) return;
         for (int i = 0; i < values.Length; i++) {
            collection.Add(values[i]);
         }
      }

      /// <summary>Moves given item to new index inside this collection.</summary>
      /// <param name="collection">The collection to move the intem inside.</param>
      /// <param name="item">The item to move.</param>
      /// <param name="newIndex">The new index of the item for insertion.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static void MoveTo<T>(this Collection<T> collection, T item, int newIndex) {
         if (collection == null || !collection.Contains(item)) return;
         collection.Remove(item);
         collection.Insert(newIndex, item);         
      } 
      #endregion

      #region IEnumerable Extensions
      /// <summary>Appends a singleton item to the end of this sequence.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="sequence">The sequence to append the item to.</param>
      /// <param name="item">The singleton item to append to the sequence.</param>
      /// <returns>The appended sequence.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static IEnumerable<T> Append<T>(this IEnumerable<T> sequence, T item) {
         var singleton = new T[] { item };
         return (sequence == null ? singleton : sequence.Concat(singleton));
      }

      /// <summary>Applies the specified action to each item of this sequence.</summary>
      /// <param name="sequence">The sequence to apply the action to.</param>
      /// <param name="action">The action to apply to each item.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static void Apply(this IEnumerable sequence, Action<object> action) {
         if (sequence == null) return;
         foreach (object item in sequence) {
            action(item);
         }
      }

      /// <summary>Applies the specified action to each item of this sequence by incorporating the element's index.</summary>
      /// <param name="sequence">The sequence to apply the action to.</param>
      /// <param name="action">The action to apply to each item. The second parameter
      /// of the action represents the index of the source element.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static void Apply(this IEnumerable sequence, Action<object, int> action) {
         if (sequence == null) return;
         int i = 0;
         foreach (object item in sequence) {
            action(item, i++);
         }
      }

      /// <summary>Applies the specified action to each item of this sequence.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="sequence">The sequence to apply the action to.</param>
      /// <param name="action">The action to apply to each item.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static void Apply<T>(this IEnumerable<T> sequence, Action<T> action) {
         if (sequence == null) return;
         foreach (T item in sequence) {
            action(item);
         }
      }

      /// <summary>Applies the specified action to each item of this sequence by incorporating the element's index.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="sequence">The sequence to apply the action to.</param>
      /// <param name="action">The action to apply to each item. The second parameter
      /// of the action represents the index of the source element.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static void Apply<T>(this IEnumerable<T> sequence, Action<T, int> action) {
         if (sequence == null) return;
         int i = 0;
         foreach (T item in sequence) {
            action(item, i++);
         }
      }

      /// <summary>Converts a given enumerator to a corresponding sequence.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="enumerator">The enumerator to convert.</param>
      /// <returns>The converted sequence.</returns>
      public static IEnumerable<T> AsEnumerable<T>(this IEnumerator<T> enumerator) {
         if (enumerator == null) yield break;
         while (enumerator.MoveNext()) {
            yield return enumerator.Current;
         }
      }

      /// <summary>Returns the distinct items in this sequence, based on the specified key selector.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <typeparam name="TKey">The distinct key type.</typeparam>
      /// <param name="sequence">The sequence to select the distinct items from.</param>
      /// <param name="selector">A function that extracts the distinct key for each item.</param>
      /// <returns>The distinct items in this sequence.</returns>
      /// <seealso cref="M:System.Linq.Enumerable{T}.Distinct"/>
      /// <remarks>This method never throws an exception.</remarks>
      public static IEnumerable<T> Distinct<T, TKey>(this IEnumerable<T> sequence, Func<T, TKey> selector) {
         return sequence.GroupBy(selector).Select(g => g.FirstOrDefault());
      }

      /// <summary>Searches for the first item in this sequence that satisfies the given predicate and returns its index. If 
      /// no matching item is found, returns <c>-1</c>.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="sequence">The sequence to search.</param>
      /// <param name="predicate">The predicate to use for searching.</param>
      /// <returns>The index of the first matching item, or <c>-1</c> if no matching item was found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int FirstIndexOf<T>(this IEnumerable<T> sequence, Func<T, bool> predicate) {
         if (sequence == null) return -1;
         var found = sequence.FirstOrDefault(predicate);
         if (found == null) return -1;

         if (sequence is Collection<T>) {
            return ((Collection<T>)sequence).IndexOf(found);
         } else {
            int idx = 0;
            foreach (var item in sequence) {
               if (item.Equals(found)) return idx;
               idx++;
            }
         }
         return -1;
      }

      /// <summary>Returns the first item of the specified type in this sequence.</summary>
      /// <typeparam name="T">The type of item to look for.</typeparam>
      /// <param name="sequence">The sequence to search.</param>
      /// <returns>The first item of the specified type in this sequence, or the default value if no such item was found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static T FirstOfType<T>(this IEnumerable sequence) {
         if (sequence == null) return default(T);
         return sequence.OfType<T>().FirstOrDefault();
      }

      /// <summary>Returns the first item of the specified type in this sequence that satisfies the given predicate.</summary>
      /// <typeparam name="T">The type of item to look for.</typeparam>
      /// <param name="sequence">The sequence to search.</param>
      /// <param name="predicate">The predicate to use for searching.</param>
      /// <returns>The first item of the specified type in this sequence that satisfies the given predicate, 
      /// or the default value if no such item was found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static T FirstOfType<T>(this IEnumerable sequence, Func<T, bool> predicate) {
         if (sequence == null) return default(T);
         return sequence.OfType<T>().FirstOrDefault(predicate);
      }

      /// <summary>Returns the index of the first item int this sequence that satisfies the specified predicate.</summary>
      /// <param name="sequence">The sequence to search.</param>
      /// <param name="predicate">The predicate to use for searching.</param>
      /// <returns>The zero-based index of the first matching item, or <c>-1</c> if no matching item was found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int IndexOf(this IEnumerable sequence, Func<object, bool> predicate) {
         if (sequence == null) return -1;
         var i = 0;
         foreach (var item in sequence) {
            if (predicate(item)) return i;
            i++;
         }
         return -1;
      }

      /// <summary>Returns the index of the first item int this sequence that satisfies the specified predicate.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="sequence">The sequence to search.</param>
      /// <param name="predicate">The predicate to use for searching.</param>
      /// <returns>The zero-based index of the first matching item, or <c>-1</c> if no matching item was found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int IndexOf<T>(this IEnumerable<T> sequence, Func<T, bool> predicate) {
         if (sequence == null) return -1;
         var i = 0;
         foreach (T item in sequence) {
            if (predicate(item)) return i;
            i++;
         }
         return -1;
      }

      /// <summary>Returns the index of the last item int this sequence that satisfies the specified predicate.</summary>
      /// <param name="sequence">The sequence to search.</param>
      /// <param name="predicate">The predicate to use for searching.</param>
      /// <returns>The zero-based index of the last matching item, or <c>-1</c> if no matching item was found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int LastIndexOf(this IEnumerable sequence, Func<object, bool> predicate) {
         if (sequence == null) return -1;
         var i = 0;
         var k = -1;
         foreach (var item in sequence) {
            if (predicate(item)) k = i;
            i++;
         }
         return k;
      }

      /// <summary>Returns the index of the last item int this sequence that satisfies the specified predicate.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="sequence">The sequence to search.</param>
      /// <param name="predicate">The predicate to use for searching.</param>
      /// <returns>The zero-based index of the last matching item, or <c>-1</c> if no matching item was found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int LastIndexOf<T>(this IEnumerable<T> sequence, Func<T, bool> predicate) {
         if (sequence == null) return -1;
         var i = 0;
         var k = -1;
         foreach (T item in sequence) {
            if (predicate(item)) k = i;
            i++;
         }
         return k;
      }

      /// <summary>Returns the last item of the specified type in this sequence.</summary>
      /// <typeparam name="T">The type of element to return.</typeparam>
      /// <param name="sequence">The sequence to search.</param>
      /// <returns>The last element of the specified type in this sequence, or the default value if no such element was found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static T LastOfType<T>(this IEnumerable sequence) {
         if (sequence == null) return default(T);
         return sequence.OfType<T>().LastOrDefault();
      }

      /// <summary>Returns the last item of the specified type in this sequence that satisfies the given predicate.</summary>
      /// <typeparam name="T">The type of element to return.</typeparam>
      /// <param name="sequence">The sequence to search.</param>
      /// <param name="predicate">The predicate to use for searching.</param>
      /// <returns>The last item of the specified type in this sequence that satisfies the given predicate, 
      /// or the default value if no such item was found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static T LastOfType<T>(this IEnumerable sequence, Func<T, bool> predicate) {
         if (sequence == null) return default(T);
         return sequence.OfType<T>().LastOrDefault(predicate);
      }

      /// <summary>Returns all the items in sequence which are not null.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="sequence">The sequence to search.</param>
      /// <returns>The non-null items in the sequence.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static IEnumerable<T> NotNull<T>(this IEnumerable<T> sequence) {
         if (sequence == null) return Enumerable.Empty<T>();
         return sequence.Where(item => item != null);
      }

      /// <summary>Prepends a singleton item to the beginning of this sequence.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="sequence">The sequence to prepend the item to.</param>
      /// <param name="item">The singleton item to prepend to the sequence.</param>
      /// <returns>The prepended sequence.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static IEnumerable<T> Prepend<T>(this IEnumerable<T> sequence, T item) {
         var singleton = new T[] { item };
         return (sequence == null ? singleton : singleton.Concat(sequence));
      }

      /// <summary>Converts a given sequence to a corresponding <see cref="T:HashSet{T}"/>.</summary>
      /// <typeparam name="T">The type of items in the sequence.</typeparam>
      /// <param name="sequence">The sequence to convert.</param>
      /// <returns>The converted hashset.</returns>
      public static HashSet<T> ToHashSet<T>(this IEnumerable<T> sequence) {
         if (sequence == null) return null;
         if (sequence.IsA<HashSet<T>>()) return (HashSet<T>)sequence;
         return new HashSet<T>(sequence);
      }
      #endregion

      #region Int Extensions
      /// <summary>Increments this value by the given delta within the specified lower and upper bounds.</summary>
      /// <param name="value">The value to constrain.</param>
      /// <param name="delta">The amount by which to increment or decrement the value.</param>
      /// <param name="lower">The lower limit (inclusive).</param>
      /// <param name="upper">The upper limit (inclusive).</param>
      /// <returns>The incremented value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int Increment(this int value, int delta, int lower, int upper) {
         if (upper <= lower) return lower;
         value += delta;
         if (value < lower) return upper;
         if (value > upper) return lower;
         return value;
      }

      /// <summary>Constrains this value to the specified lower and upper limits.</summary>
      /// <param name="value">The value to constrain.</param>
      /// <param name="lower">The lower limit (inclusive).</param>
      /// <param name="upper">The upper limit (inclusive).</param>
      /// <returns>The constrained value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int Limit(this int value, int lower, int upper) {
         if (upper < lower) upper = lower;
         if (value > upper) return upper;
         if (value < lower) return lower;
         return value;
      }

      /// <summary>Converts the given number of milliseconds to the corresponding timespan object.</summary>
      /// <param name="value">The number of milliseconds.</param>
      /// <returns>The timespan object that represents the given number of milliseconds.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan MilliSeconds(this int value) {
         return TimeSpan.FromMilliseconds(value);
      }

      /// <summary>Converts the given number of minutes to the corresponding timespan object.</summary>
      /// <param name="value">The number of minutes.</param>
      /// <returns>The timespan object that represents the given number of minutes.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan Minutes(this int value) {
         return TimeSpan.FromMinutes(value);
      }

      /// <summary>Converts the given number of seconds to the corresponding timespan object.</summary>
      /// <param name="value">The number of seconds.</param>
      /// <returns>The timespan object that represents the given number of seconds.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan Seconds(this int value) {
         return TimeSpan.FromSeconds(value);
      }
      #endregion

      #region Long Extensions
      /// <summary>Constrains this value to the specified lower and upper limits.</summary>
      /// <param name="value">The value to constrain.</param>
      /// <param name="lower">The lower limit (inclusive).</param>
      /// <param name="upper">The upper limit (inclusive).</param>
      /// <returns>The constrained value.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static long Limit(this long value, long lower, long upper) {
         if (upper < lower) upper = lower;
         if (value > upper) return upper;
         if (value < lower) return lower;
         return value;
      }

      /// <summary>Converts the given number of milliseconds to the corresponding timespan object.</summary>
      /// <param name="value">The number of milliseconds.</param>
      /// <returns>The timespan object that represents the given number of milliseconds.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan MilliSeconds(this long value) {
         return TimeSpan.FromMilliseconds(value);
      }

      /// <summary>Converts the given number of seconds to the corresponding timespan object.</summary>
      /// <param name="value">The number of seconds.</param>
      /// <returns>The timespan object that represents the given number of seconds.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan Seconds(this long value) {
         return TimeSpan.FromSeconds(value);
      }

      /// <summary>Converts the given number of ticks to the corresponding timespan object.</summary>
      /// <param name="value">The number of ticks.</param>
      /// <returns>The timespan object that represents the given number of seconds.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static TimeSpan Ticks(this long value) {
         return TimeSpan.FromTicks(value);
      }
      #endregion

      #region Object Extensions
      /// <summary>Concatenates and flattering jagged array.</summary>
      /// <typeparam name="T">The type of the elements of the input array.</typeparam>
      /// <param name="array">The array to concatenate.</param>
      /// <returns>An array that contains the concatenated elements of the input jagged array.</returns>
      public static T[] Concat<T>(this T[][] array) {
         if (array == null) return null;
         var result = new List<T>();
         array.Apply(item => {
            result.AddRange(item);
         });
         return result.ToArray();
      }

      /// <summary>Checks whether the given value is the default value for its type.</summary>
      /// <typeparam name="T">The type of value to check.</typeparam>
      /// <param name="value">The value to check</param>
      /// <returns><c>true</c> if the value is the default value for its type; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsDefault<T>(this T value) where T : struct {
         return Object.Equals(value, default(T));
      }

      /// <summary>Checks whether the object is <c>null</c>.</summary>
      /// <typeparam name="T">The type of object to check.</typeparam>
      /// <param name="value">The object to check.</param>
      /// <returns><c>true</c> if the object is <c>null</c>; <c>false</c>, otherwise.</returns>
      /// <remarks>The check is performed without using the object's equality operator. This method never throws an exception.</remarks>
      public static bool IsNull<T>(this T value) where T : class {
         return Object.Equals(value, null);
      }

      /// <summary>Gets the array item at the specified index, if any.</summary>
      /// <param name="array">The array to get the item from.</param>
      /// <param name="index">The index of the item to get.</param>
      /// <returns>The item at the given index, or <c>null</c> if the index is out of bounds.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static object Item(this object[] array, int index) {
         if (array == null) return null;
         if (index < 0 || index >= array.Length) return null;
         return array[index];
      }

      /// <summary>Checks whether the object is not <c>null</c>.</summary>
      /// <typeparam name="T">The type of object to check.</typeparam>
      /// <param name="value">The object to check.</param>
      /// <returns><c>true</c> if the object is not <c>null</c>; <c>false</c>, otherwise.</returns>
      /// <remarks>The check is performed without using the object's equality operator. This method never throws an exception.</remarks>
      public static bool NotNull<T>(this T value) where T : class {
         return !Object.Equals(value, null);
      }

      /// <summary>Trims the null entries from the both sides of the array.</summary>
      /// <param name="array">The array to trim.</param>
      /// <returns>The trimmed array.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static object[] Trim(this object[] array) {
         return array.TrimStart().TrimEnd();
      }

      /// <summary>Trims the null entries from the end of the array.</summary>
      /// <param name="array">The array to trim.</param>
      /// <returns>The trimmed array.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static object[] TrimEnd(this object[] array) {
         if (array == null) return null;
         var len = array.Length;
         if (len == 0 || array[len - 1] != null) return array;
         for (int i=len-2; i >= 0; i--) {
            if (array[i] != null) {
               return array.Take(i + 1).ToArray();
            }
         }
         return new object[0];
      }

      /// <summary>Trims the null entries from the beginning of the array.</summary>
      /// <param name="array">The array to trim.</param>
      /// <returns>The trimmed array.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static object[] TrimStart(this object[] array) {
         if (array == null) return null;
         var len = array.Length;
         if (len == 0 || array[0] != null) return array;
         for (int i=1; i<len; i++) {
            if (array[i] != null) {
               return array.Skip(i).ToArray();
            }
         }
         return new object[0];
      }
      #endregion

      #region Random Extensions
      /// <summary>Gets new random number generator with a unique seed.</summary>
      public static Random GetRandomGenerator() {
         return new Random(BitConverter.ToInt32(Guid.NewGuid().ToByteArray(), 0));
      }
      #endregion

      #region Stack<T> Extensions
      /// <summary>Pushes the given list of items into this stack.</summary>
      /// <typeparam name="T">The type of items in the stack.</typeparam>
      /// <param name="stack">The stack.</param>
      /// <param name="items">The items to push, in order.</param>
      public static void Push<T>(this Stack<T> stack, IEnumerable<T> items) {
         if (stack == null || items == null) return;
         items.Apply(item => stack.Push(item));
      }
      #endregion

      #region String Extensions
      /// <summary>Appends a list of strings after this string.</summary>
      /// <param name="source">The first string</param>
      /// <param name="args">The list of strings to append after this string.</param>
      /// <returns>The concatenated string.</returns>
      public static string Append(this string source, params string[] args) {
         //TK: Looks bad, but this is the most efficient method!
         var sb = new StringBuilder();
         if (source.NotEmpty()) sb.Append(source);
         for (int i = 0; i < args.Length; i++) {
            if (args[i].NotEmpty()) sb.Append(args[i]);
         }
         return sb.ToString();
      }

      /// <summary>Extracts the character at the given position.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pos">The position from which to extract the character. Use a negative number to indicate a 
      /// position relative to the end of the string.</param>
      /// <returns>The character at the given position.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string At(this string value, int pos) {
         return value.Between(pos, pos);
      }

      /// <summary>Extracts the string at the specified position in the array.</summary>
      /// <param name="array">The string array.</param>
      /// <param name="pos">The position from which to extract the string.</param>
      /// <returns>The string at the specified position, or the empty string if the position is out of bounds.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string At(this string[] array, int pos) {
         if (array == null || pos < 0 || pos >= array.Length) return String.Empty;
         return array[pos].Nvl();
      }

      /// <summary>Extracts the substring after the given position (exclusive).</summary>
      /// <param name="value">The input string.</param>
      /// <param name="start">The position after which to start extracting the substring (exclusive).
      /// Use a negative number to indicate a position relative to the end of the string.</param>
      /// <returns>The substring extracted from the start position (exclusive) and until the end of the string.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string After(this string value, int start) {
         return value.Between(start + 1, Int32.MaxValue);
      }

      /// <summary>Extracts the substring before the given position (exclusive).</summary>
      /// <param name="value">The input string.</param>
      /// <param name="end">The position before which to end extracting the substring (exclusive).
      /// Use a negative number to indicate a position relative to the end of the string.</param>
      /// <returns>The substring extracted from the start of the string and until the end position (exclusive).</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Before(this string value, int end) {
         return (end == 0 ? String.Empty : value.Between(0, end - 1));
      }

      /// <summary>Extracts the substring between the given start and end positions (inclusive).</summary>
      /// <param name="value">The input string.</param>
      /// <param name="start">The position from which to start extracting the substring (inclusive).
      /// Use a negative number to indicate position relative to the end of the string.</param>
      /// <param name="end">The position at which to end extracting the substring (inclusive).
      /// Use a negative number to indicate position relative to the end of the string.</param>
      /// <returns>The substring extracted from the start position and until the end position.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Between(this string value, int start, int end) {
         int len = (String.IsNullOrEmpty(value) ? 0 : value.Length);
         if (start < 0) start += len;
         if (end < 0) end += len;
         if (len == 0 || start > len - 1 || end < start) {
            return String.Empty;
         } else {
            if (start < 0) start = 0;
            if (end >= len) end = len - 1;
            return value.Substring(start, end - start + 1);
         }
      }

      /// <summary>Compacts this string by replacing sequences of whitespace inside the string with single spaces.</summary>
      /// <param name="value">The string to compact.</param>
      /// <returns>The compacted string.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Compact(this string value) {
         if (String.IsNullOrEmpty(value)) return String.Empty;
         return _reWhitespaceSequence.Replace(value, " ");
      }
      private static Regex _reWhitespaceSequence = new Regex(@"\s{2,}");

      /// <summary>Checks whether the specified substring occurs within this string, optionally ignoring case.</summary>
      /// <param name="value">The string to check.</param>
      /// <param name="substring">The substring to look for.</param>
      /// <param name="ignoreCase">Indicates whether to perform a case-insensitive search.</param>
      /// <returns><c>true</c> if this string contains the specified substring; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool Contains(this string value, string substring, bool ignoreCase) {
         if (value.IsEmpty()) return substring.IsEmpty();
         if (substring.IsEmpty()) return true;
         if (!ignoreCase) return value.Contains(substring);
         return value.ToUpper().Contains(substring.ToUpper());
      }

      /// <summary>Checks whether this string collection contains the specified item using case-insenstive search.</summary>
      /// <param name="collection">The string collection to search.</param>
      /// <param name="item">The item to look for.</param>
      /// <param name="ignoreCase">Indicates whether the search should be case-insensitive.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool Contains(this StringCollection collection, string item, bool ignoreCase) {
         if (collection == null) return false;
         if (ignoreCase) {
            for (var i = 0; i < collection.Count; i++) {
               if (item.EqualsIgnoreCase(collection[i])) return true;
            }
            return false;
         } else {
            return collection.Contains(item);
         }
      }

      /// <summary>Checks whether this string collection contains the specified item using case-insenstive search.</summary>
      /// <param name="collection">The string collection to search.</param>
      /// <param name="item">The item to look for.</param>
      /// <param name="ignoreCase">Indicates whether the search should be case-insensitive.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool Contains(this ReadOnlyStringCollection collection, string item, bool ignoreCase) {
         if (collection == null) return false;
         if (ignoreCase) {
            for (var i = 0; i < collection.Count; i++) {
               if (item.EqualsIgnoreCase(collection[i])) return true;
            }
            return false;
         } else {
            return collection.Contains(item);
         }
      } 

      /// <summary>Returns the number of characters in this string.</summary>
      /// <param name="value">The input string.</param>
      /// <returns>The number of characters in this string, or <c>0</c> if the string is empty.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int Count(this string value) {
         return (String.IsNullOrEmpty(value) ? 0 : value.Length);
      }

      /// <summary>Returns the number of times that the given pattern appears in this string.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The number of times the pattern appears in this string, or <c>0</c> if the string is empty or the pattern was not found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int Count(this string value, string pattern) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern));
         return value.Count(regex);
      }

      /// <summary>Returns the number of times that the given pattern appears in this string.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The number of times the pattern appears in this string, or <c>0</c> if the string is empty or the pattern was not found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static int Count(this string value, Regex pattern) {
         if (String.IsNullOrEmpty(value) || (pattern == null)) return 0;
         var matches = pattern.Matches(value);
         return matches.Count;
      }

      /// <summary>Returns this string if it is not <c>null</c>. Otherwise, returns the <c>empty</c> string.</summary>
      /// <param name="value">The string value.</param>
      /// <returns>The given string, or the <c>empty</c> string if the given string is <c>null</c>.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Nvl(this string value) {
         if (!String.IsNullOrEmpty(value)) return value;
         return string.Empty;
      }

      /// <summary>Returns this string if it is not <c>null</c> or <c>empty</c>. Otherwise, returns the specified default value.</summary>
      /// <param name="value">The string value.</param>
      /// <param name="defaultValue">The default string value.</param>
      /// <returns>The string or its default value. If the default value is also <c>null</c>, returns the <c>empty</c> string.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Nvl(this string value, string defaultValue) {
         if (!String.IsNullOrEmpty(value)) return value;
         if (!String.IsNullOrEmpty(defaultValue)) return defaultValue;
         return string.Empty;
      }

      /// <summary>Checks whether this string ends with the specified substring, optionally ignoring case.</summary>
      /// <param name="value">The string to check.</param>
      /// <param name="substring">The substring to look for.</param>
      /// <param name="ignoreCase">Indicates whether to perform a case-insensitive search.</param>
      /// <returns><c>true</c> if this string ends with the specified substring; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool EndsWith(this string value, string substring, bool ignoreCase) {
         if (value.IsEmpty()) return substring.IsEmpty();
         if (substring.IsEmpty()) return true;
         if (!ignoreCase) return value.EndsWith(substring);
         return value.EndsWith(substring, StringComparison.OrdinalIgnoreCase);
      }

      /// <summary>Replaces invalid XML characters in a string with their valid XML equivalent.</summary>
      /// <param name="value">The input string.</param>
      /// <returns>The escaped string.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string EscapeXML(this string value) {
         if (String.IsNullOrEmpty(value)) return String.Empty;
         return SecurityElement.Escape(value);
      }

      /// <summary>Checks whether this string equals another string, ignoring case.</summary>
      /// <param name="value">The string to check.</param>
      /// <param name="other">The other string to compare against.</param>
      /// <returns><c>true</c> if this string equals the other string; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool EqualsIgnoreCase(this string value, string other) {
         if (value.IsEmpty()) return other.IsEmpty();
         if (other.IsEmpty()) return false;
         return value.Equals(other, StringComparison.OrdinalIgnoreCase);
      }

      /// <summary>Extracts one or more substrings from this string based on the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>Returns substrings based on the first match of the given pattern. If the pattern contains capturing groups, then all 
      /// captured groups within the match are returned. Otherwise, the complete match is returned. Returns <c>null</c> if no match is found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string[] Extract(this string value, string pattern) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern));
         return Extract(value, regex);
      }

      /// <summary>Extracts one or more substrings from this string based on the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>Returns substrings based on the first match of the given pattern. If the pattern contains capturing groups, then all 
      /// captured groups within the match is returned. Otherwise, the complete match is returned. Returns <c>null</c> if no match is found.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string[] Extract(this string value, Regex pattern) {
         if (String.IsNullOrEmpty(value) || (pattern == null)) return null;
         var match = pattern.Match(value);
         if (!match.Success) return null;
         var count = match.Groups.Count;
         if (count > 1) {
            var strings = new string[count-1];
            for (int i = 1; i < count; i++) {
               strings[i-1] = match.Groups[i].ToString();
            }
            return strings;
         } else {
            return new [] { match.Groups[0].ToString() };
         }
      }

      /// <summary>Creates a new string filled by repeating this string until reaching the specified length.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="length">The filled string length.</param>
      /// <returns>The filled string.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Fill(this string value, int length) {
         if (String.IsNullOrEmpty(value) || length <= 0) return String.Empty;
         while (value.Length < length) value += value;
         return value.Before(length);
      }

      /// <summary>Finds the first substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The first substring that matches the given pattern. If the pattern contains capturing groups, then the first 
      /// captured group within the match is returned. Otherwise, the complete match is returned.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string FindFirst(this string value, string pattern) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern));
         return FindFirst(value, regex);
      }

      /// <summary>Finds the first substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The first substring that matches the given pattern. If the pattern contains capturing groups, then the first 
      /// captured group within the match is returned. Otherwise, the complete match is returned.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string FindFirst(this string value, Regex pattern) {
         if (String.IsNullOrEmpty(value) || (pattern == null)) return String.Empty;
         var match = pattern.Match(value);
         if (!match.Success) return String.Empty;
         var index = (match.Groups.Count > 1 ? 1 : 0);
         return match.Groups[index].ToString();
      }

      /// <summary>Finds all substrings in this string that match the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>All substrings that match the given pattern. If the pattern contains capturing groups, then all captured
      /// groups within each match are returned. Otherwise, the complete matches are returned.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string[] FindAll(this string value, string pattern) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern));
         return FindAll(value, regex);
      }

      /// <summary>Finds all substrings in this string that match the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>All substrings that match the given pattern. If the pattern contains capturing groups, then all captured
      /// groups within each match are returned. Otherwise, the complete matches are returned.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string[] FindAll(this string value, Regex pattern) {
         if (String.IsNullOrEmpty(value) || (pattern == null)) return new string[0];
         var matches  = pattern.Matches(value);
         var ngroups  = pattern.GetGroupNumbers().Length - 1;
         var nmatches = matches.Count;
         if (nmatches == 0) return new string[0];
         var strings = new string[nmatches * (ngroups == 0 ? 1 : ngroups)];
         for (int i = 0; i < nmatches; i++) {
            if (ngroups == 0) {
               strings[i] = matches[i].Groups[0].ToString();
            } else {
               for (int j = 0; j < ngroups; j++) {
                  strings[i*ngroups + j] = matches[i].Groups[j+1].ToString();
               }
            }
         }
         return strings;
      }

      /// <summary>Gets the string array item at the specified position.</summary>
      /// <param name="value">The string array.</param>
      /// <param name="position">The position of the item to get.</param>
      /// <returns>The requested string array item, or the <c>Empty</c> string in case the array is empty or the specified position is out of range.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string GetValueAt(this string[] value, int position) {
         if (value != null && position >= 0 && position < value.Length) {
            return value[position];
         }
         return String.Empty;
      }

      /// <summary>Checks whether this string array has value at defined position.</summary>
      /// <param name="value">The string array to check.</param>
      /// <param name="position">Positions to check</param>
      /// <returns><c>true</c> if this string array has values at all defined positions; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool HasValueAt(this string[] value, params int[] position) {
         if (value == null || position == null) return false;
         foreach (var pos in position) {
            if (pos < 0 || pos >= value.Length) return false;
            if (value[pos].IsEmpty()) return false;
         }
         return true;
      }

      /// <summary>Checks whether this string is <c>null</c> or <c>empty</c>.</summary>
      /// <param name="value">The string to check.</param>
      /// <returns><c>true</c> if this string is <c>null</c> or <c>empty</c>; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsEmpty(this string value) {
         return String.IsNullOrEmpty(value);
      }

      /// <summary>Joins this array of strings without a delimiter.</summary>
      /// <param name="value">The strings array to join.</param>
      /// <returns>The joined string.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Join(this string[] value) {
         if (value == null || value.Length == 0) return String.Empty;
         return String.Join(null, value);
      }

      /// <summary>Joins this array of strings using the given delimiter.</summary>
      /// <param name="value">The strings array to join.</param>
      /// <param name="delimiter">The delimiter string.</param>
      /// <returns>The joined string, consisting of the string array items interspersed with the given delimiter.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Join(this string[] value, string delimiter) {
         if (value == null || value.Length == 0) return String.Empty;
         return String.Join(delimiter, value);
      }

      /// <summary>Joins this array of strings using the given delimiter. Parameters specify the first array item and 
      /// number of items to use.</summary>
      /// <param name="value">The strings array to join.</param>
      /// <param name="delimiter">The delimiter string.</param>
      /// <param name="start">The index of the first array item to use.</param>
      /// <param name="count">The number of array items to use.</param>
      /// <returns>The joined string, consisting of the string array items interspersed with the given delimiter.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Join(this string[] value, string delimiter, int start, int count) {
         if (value == null || value.Length == 0) return String.Empty;
         return String.Join(delimiter, value, start, count);
      }

      /// <summary>Checks whether this string matches the given pattern.</summary>
      /// <param name="value">The string to check.</param>
      /// <param name="pattern">The expected pattern.</param>
      /// <returns><c>true</c> if this string matches the expected pattern; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool Matches(this string value, string pattern) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern));
         return Matches(value, regex);
      }

      /// <summary>Checks whether this string matches the given pattern.</summary>
      /// <param name="value">The string to check.</param>
      /// <param name="pattern">The expected pattern.</param>
      /// <returns><c>true</c> if this string matches the expected pattern; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool Matches(this string value, Regex pattern) {
         bool f1 = String.IsNullOrEmpty(value), f2 = (pattern == null);
         if (f1) return f2;
         if (f2) return false;
         return pattern.IsMatch(value);
      }

      /// <summary>Checks whether this string is not <c>null</c> or <c>empty</c>.</summary>
      /// <param name="value">The string to check.</param>
      /// <returns><c>true</c> if this string is not <c>null</c> or <c>empty</c>; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool NotEmpty(this string value) {
         return !String.IsNullOrEmpty(value);
      }

      /// <summary>Removes all substrings in this string that match the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The string after removal of all occurences of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string RemoveAll(this string value, string pattern) {
         return ReplaceAll(value, pattern, "");
      }

      /// <summary>Removes all substrings in this string that match the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The string after removal of all occurences of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string RemoveAll(this string value, Regex pattern) {
         return ReplaceAll(value, pattern, "");
      }

      /// <summary>Removes the first substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The string after removal of the first occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string RemoveFirst(this string value, string pattern) {
         return ReplaceFirst(value, pattern, "");
      }

      /// <summary>Removes the first substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The string after removal of the first occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string RemoveFirst(this string value, Regex pattern) {
         return ReplaceFirst(value, pattern, "");
      }

      /// <summary>Removes the last substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The string after removal of the last occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string RemoveLast(this string value, string pattern) {
         return ReplaceLast(value, pattern, "");
      }

      /// <summary>Removes the last substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <returns>The string after removal of the last occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string RemoveLast(this string value, Regex pattern) {
         return ReplaceLast(value, pattern, "");
      }

      /// <summary>Replaces all substrings in this string that match the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacement">The replacement string.</param>
      /// <returns>The string after replacement of all occurences of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceAll(this string value, string pattern, string replacement) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern));
         return ReplaceAll(value, regex, replacement);
      }

      /// <summary>Replaces all substrings in this string that match the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacement">The replacement string.</param>
      /// <returns>The string after replacement of all occurences of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceAll(this string value, Regex pattern, string replacement) {
         if (String.IsNullOrEmpty(value)) return String.Empty;
         if (pattern == null) return value;
         return pattern.Replace(value, replacement);
      }

      /// <summary>Replaces all substrings in this string that match the given pattern using a dynamic replacement delegate.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacer">A delegate that examines each match and returns the replacement string.</param>
      /// <returns>The string after replacement of all occurences of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceAll(this string value, string pattern, MatchEvaluator replacer) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern));
         return ReplaceAll(value, regex, replacer);
      }

      /// <summary>Replaces all substrings in this string that match the given pattern using a dynamic replacement delegate.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacer">A delegate that examines each match and returns the replacement string.</param>
      /// <returns>The string after replacement of all occurences of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceAll(this string value, Regex pattern, MatchEvaluator replacer) {
         if (String.IsNullOrEmpty(value)) return String.Empty;
         if (pattern == null) return value;
         return pattern.Replace(value, replacer);
      }

      /// <summary>Replaces the first substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacement">The replacement string.</param>
      /// <returns>The string after replacement of the first occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceFirst(this string value, string pattern, string replacement) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern));
         return ReplaceFirst(value, regex, replacement);
      }

      /// <summary>Replaces the first substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacement">The replacement string.</param>
      /// <returns>The string after replacement of the first occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceFirst(this string value, Regex pattern, string replacement) {
         if (String.IsNullOrEmpty(value)) return String.Empty;
         if (pattern == null) return value;
         return pattern.Replace(value, replacement, 1);
      }

      /// <summary>Replaces the first substring in this string that matches the given pattern using a dynamic replacement delegate.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacer">A delegate that examines each match and returns the replacement string.</param>
      /// <returns>The string after replacement of the first occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceFirst(this string value, string pattern, MatchEvaluator replacer) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern));
         return ReplaceFirst(value, regex, replacer);
      }

      /// <summary>Replaces the first substring in this string that matches the given pattern using a dynamic replacement delegate.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacer">A delegate that examines each match and returns the replacement string.</param>
      /// <returns>The string after replacement of the first occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceFirst(this string value, Regex pattern, MatchEvaluator replacer) {
         if (String.IsNullOrEmpty(value)) return String.Empty;
         if (pattern == null) return value;
         return pattern.Replace(value, replacer, 1);
      }

      /// <summary>Replaces the last substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacement">The replacement string.</param>
      /// <returns>The string after replacement of the last occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceLast(this string value, string pattern, string replacement) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern, RegexOptions.RightToLeft));
         return ReplaceFirst(value, regex, replacement);
      }

      /// <summary>Replaces the last substring in this string that matches the given pattern.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacement">The replacement string.</param>
      /// <returns>The string after replacement of the last occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceLast(this string value, Regex pattern, string replacement) {
         if (String.IsNullOrEmpty(value)) return String.Empty;
         if (pattern == null) return value;
         if ((pattern.Options & RegexOptions.RightToLeft) == 0) {
            pattern = new Regex(pattern.ToString(), pattern.Options | RegexOptions.RightToLeft);
         }
         return pattern.Replace(value, replacement, 1);
      }

      /// <summary>Replaces the last substring in this string that matches the given pattern using a dynamic replacement delegate.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacer">A delegate that examines each match and returns the replacement string.</param>
      /// <returns>The string after replacement of the last occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceLast(this string value, string pattern, MatchEvaluator replacer) {
         var regex = (String.IsNullOrEmpty(pattern) ? null : new Regex(pattern, RegexOptions.RightToLeft));
         return ReplaceFirst(value, regex, replacer);
      }

      /// <summary>Replaces the last substring in this string that matches the given pattern using a dynamic replacement delegate.</summary>
      /// <param name="value">The input string.</param>
      /// <param name="pattern">The search pattern.</param>
      /// <param name="replacer">A delegate that examines each match and returns the replacement string.</param>
      /// <returns>The string after replacement of the last occurence of the specified pattern.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string ReplaceLast(this string value, Regex pattern, MatchEvaluator replacer) {
         if (String.IsNullOrEmpty(value)) return String.Empty;
         if (pattern == null) return value;
         if ((pattern.Options & RegexOptions.RightToLeft) == 0) {
            pattern = new Regex(pattern.ToString(), RegexOptions.RightToLeft);
         }
         return pattern.Replace(value, replacer, 1);
      }

      /// <summary>Sorts the contents of this string collection.</summary>
      /// <param name="collection">The collection to sort.</param>
      /// <remarks>The collection is sorted in-place.</remarks>
      /// <remarks>This method never throws an exception.</remarks>
      public static void Sort(this StringCollection collection) {
         if (collection == null) return;
         var sortedList = collection.OfType<string>().OrderBy(s => s).ToArray();
         collection.Clear();
         collection.AddRange(sortedList);
      }

      /// <summary>Returns a reversed form of this string.</summary>
      /// <param name="value">The string to reverse.</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Reverse(this String value) {
         if (value == null) return null;
         return new String(value.ToCharArray().Reverse().ToArray());
      }

      /// <summary>Splits this string using the given delimiter pattern.</summary>
      /// <param name="value">The string to split.</param>
      /// <param name="delimiter">The delimiter pattern.</param>
      /// <returns>The resulting array of split sub-strings.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string[] Split(this string value, string delimiter) {
         if (String.IsNullOrEmpty(value) || String.IsNullOrEmpty(delimiter)) return new string[0];
         return Regex.Split(value, delimiter);
      }

      /// <summary>Splits this string using the given delimiter pattern.</summary>
      /// <param name="value">The string to split.</param>
      /// <param name="delimiter">The delimiter pattern.</param>
      /// <param name="trim">Indicates whether to trim the results.</param>
      /// <returns>The resulting array of split sub-strings.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string[] Split(this string value, string delimiter, bool trim) {
         if (String.IsNullOrEmpty(value) || String.IsNullOrEmpty(delimiter)) return new string[0];
         if (trim) {
            value = value.Trim();
            if (String.IsNullOrEmpty(value)) return new string[0];
            delimiter = @"\s*" + delimiter + @"\s*";
         }
         return Regex.Split(value, delimiter);
      }

      /// <summary>Splits this string using the given delimiter pattern.</summary>
      /// <param name="value">The string to split.</param>
      /// <param name="delimiter">The delimiter pattern.</param>
      /// <returns>The resulting array of split sub-strings.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string[] Split(this string value, Regex delimiter) {
         if (String.IsNullOrEmpty(value) || delimiter == null) return new string[0];
         return delimiter.Split(value);
      }

      /// <summary>Splits this string using the characters that satisfies the specified predicate.</summary>
      /// <param name="value">The string to split.</param>
      /// <param name="predicate">The predicate to use for splitting.</param>
      /// <returns>The resulting array of split sub-strings.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string[] Split(this string value, Func<char, bool> predicate) {
         if (String.IsNullOrEmpty(value) || predicate == null) return new string[0];
         var ix = 0;
         var lx = 0;
         var result = new List<string>();
         foreach (var ch in value) {
            if (predicate(ch) && ix != lx) {
               result.Add(value.Between(lx, ix));
               lx = ix;
            }
            ix++;
         }
         result.Add(value.Between(lx, ix));
         return result.ToArray();
      }

      /// <summary>Checks whether this string starts with the specified substring, optionally ignoring case.</summary>
      /// <param name="value">The string to check.</param>
      /// <param name="substring">The substring to look for.</param>
      /// <param name="ignoreCase">Indicates whether to perform a case-insensitive search.</param>
      /// <returns><c>true</c> if this string starts with the specified substring; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool StartsWith(this string value, string substring, bool ignoreCase) {
         if (value.IsEmpty()) return substring.IsEmpty();
         if (substring.IsEmpty()) return true;
         if (!ignoreCase) return value.StartsWith(substring);
         return value.StartsWith(substring, StringComparison.OrdinalIgnoreCase);
      }

      /// <summary>Substitutes the placeholders in this formatting string with the corresponding arguments.</summary>
      /// <param name="format">The formatting string.</param>
      /// <param name="args">The list of arguments to substitute in the formatting string.</param>
      /// <returns>The formatted string.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static string Substitute(this string format, params object[] args) {
         string value = String.Empty;
         if (String.IsNullOrEmpty(format)) return value;
         if (args.Length == 0) return format;
         try {
            return String.Format(format, args);
         } catch (FormatException) {
            return format;
         } catch {
            return "***";
         }
      }

      /// <summary>Converts a BPL-compliant, culture-invariant string representation of a double to a double value.</summary>
      /// <remarks>This method never throws an exception and prevents first-chance CLR formatting exceptions.</remarks>
      public static double ToBplDouble(this string value) {
         var d = Double.NaN;
         if (Double.TryParse(value, NumberStyles.Float | NumberStyles.AllowExponent, CultureInfo.InvariantCulture, out d)) {
            return d;
         } else {
            return Double.NaN;
         }
      }

      /// <summary>Writes a formatted string message to the system trace listeners.</summary>
      /// <param name="value">The string value to send to the trace listeners.</param>
      /// <param name="args">The format placeholder arguments</param>
      /// <remarks>This method never throws an exception.</remarks>
      public static void Trace(this string value, params object[] args) {
         System.Diagnostics.Trace.WriteLine(value.Substitute(args));
      }
      #endregion

      #region TimeSpan Extensions
      /// <summary>Returns the smallest timespan, at the specified precision, that is greater than or equal to this value.</summary>
      /// <param name="value">The value to find the ceiling for.</param>
      /// <param name="precision">The ceiling precision.</param>
      /// <returns>The ceiling value.</returns>
      /// <remarks>See <see cref="Round(TimeSpan, TimeSpan)"/> for an explanation about the rounding precision.
      /// This method never throws an exception.</remarks>
      public static TimeSpan Ceiling(this TimeSpan value, TimeSpan precision) {
         return _roundTimeSpan(value, precision, (num, pre) => {
            return ((int)pre == 0) ? (int)Math.Ceiling(num) : (int)Math.Ceiling(Math.Ceiling(num / (int)pre) * (int)pre);
         });
      }

      /// <summary>Returns the largest timespan, at the specified precision, that is smaller than or equal to this value.</summary>
      /// <param name="value">The value to find the floor for.</param>
      /// <param name="precision">The floor precision.</param>
      /// <returns>The floor value.</returns>
      /// <remarks>See <see cref="Round(TimeSpan, TimeSpan)"/> for an explanation about the rounding precision.
      /// This method never throws an exception.</remarks>
      public static TimeSpan Floor(this TimeSpan value, TimeSpan precision) {
         return _roundTimeSpan(value, precision, (num, pre) => {
            return ((int)pre == 0) ? (int)Math.Floor(num) : (int)Math.Floor(Math.Floor(num / (int)pre) * (int)pre);
         });
      }

      /// <summary>Checks whether this timespan is equal to zero.</summary>
      /// <param name="timespan">The time span to check.</param>
      /// <returns><c>true</c> if this timespan is zero; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsZero(this TimeSpan timespan) {
         return (timespan.Ticks == 0);
      }

      /// <summary>Rounds a timespan to a specified precision.</summary>
      /// <param name="value">The timespan to round.</param>
      /// <param name="precision">The rounding precision.</param>
      /// <returns>The rounded value.</returns>
      /// <remarks>This method rounds the value to the most granular non-zero part of precision (leading zero-parts are ignored).
      /// For example, 
      /// <list>
      ///   <item>Precision = 1:15:00 will round to 00:00:00, 1:15:00, 2:30:00 etc.</item>
      ///   <item>Precision = 1:00:00 will round to 0:0:0, 1:00:00, 2:00:00 etc.</item>
      ///   <item>Precision = 0:10:20 will round to 0:0:0, 0:10:20, 0:20:40, 0:31:00 (0:20:40 + 10:20), etc.</item>
      /// </list>
      /// This method never throws an exception.</remarks>
      public static TimeSpan Round(this TimeSpan value, TimeSpan precision) {
         return _roundTimeSpan(value, precision, (num, pre) => {
            return ((int)pre == 0) ? (int)Math.Round(num) : (int)Math.Round(Math.Round(num / (int)pre) * (int)pre);
         });
      }

      // finds finest-granular part of precision and rounds input to this part, using given "rounder" (e.g. floor, round, ceil)
      private static TimeSpan _roundTimeSpan(TimeSpan input, TimeSpan precision, Func<double, double, int> rounder) {
         if (precision.Days != 0) {
            return TimeSpan.FromDays(rounder(input.TotalDays, precision.TotalDays));
         } else if (precision.Hours != 0) {
            return TimeSpan.FromHours(rounder(input.TotalHours, precision.TotalHours));
         } else if (precision.Minutes != 0) {
            return TimeSpan.FromMinutes(rounder(input.TotalMinutes, precision.TotalMinutes));
         } else if (precision.Seconds != 0) {
            return TimeSpan.FromSeconds(rounder(input.TotalSeconds, precision.TotalSeconds));
         } else if (precision.Milliseconds != 0) {
            return TimeSpan.FromMilliseconds(rounder(input.TotalMilliseconds, precision.TotalMilliseconds));
         }
         return input;
      }
      #endregion

      #region Queue<T> Extensions
      /// <summary>Enqueues the given list of items into this queue.</summary>
      /// <typeparam name="T">The type of items in the queue.</typeparam>
      /// <param name="queue">The queue.</param>
      /// <param name="items">The items to push, in order.</param>
      public static void Enqueue<T>(this Queue<T> queue, IEnumerable<T> items) {
         if (queue == null || items == null) return;
         items.Apply(item => queue.Enqueue(item));
      }
      #endregion

      #region Version Extensions
      /// <summary>Determines whether this version number is compatible with another version number (see <see cref="BplVersionMatch.Compatible"/>)</summary>
      /// <param name="version">The version number to check.</param>
      /// <param name="otherVersion">The version number to check against.</param>
      /// <returns><c>true</c> if this version is compatible with the other version; <c>false</c> otherwise.</returns>
      public static bool IsCompatible(this Version version, Version otherVersion) {
         return Matches(version, otherVersion, BplVersionMatch.Compatible);
      }

      /// <summary>Determines whether this version number is <c>null</c> or <c>empty</c>. A version number is considered empty if both its 
      /// major and minor components are zero (the build and revision components are ignored for this check).</summary>
      /// <param name="version">The version number to check.</param>
      /// <returns><c>true</c> if this version is empty; <c>false</c> otherwise.</returns>
      /// <remarks>This method never throws an exception.</remarks>
      public static bool IsEmpty(this Version version) {
         if (version == null) return true;
         if (version.Major <= 0 && version.Minor <= 0 && version.Build <= 0 && version.Revision <= 0) return true;
         return false;
      }

      /// <summary>Determines whether this version number matches another version number, based on the specified matching criteria</summary>
      /// <param name="version">The version number to check.</param>
      /// <param name="otherVersion">The version number to check against.</param>
      /// <param name="versionMatch">A flag that specifies the version matching criteria.</param>
      /// <returns><c>true</c> if this version matches the other version based on the specified VersionMatch criteria; <c>false</c> otherwise.</returns>
      public static bool Matches(this Version version, Version otherVersion, BplVersionMatch versionMatch) {
         if (versionMatch == BplVersionMatch.None) return true;

         if (version == null) return (otherVersion == null);
         if (otherVersion == null) return false;

         if (version < otherVersion) return false;
         if (versionMatch == BplVersionMatch.Compatible) return true;

         if (version.Major != otherVersion.Major) return false;
         if (versionMatch == BplVersionMatch.Major) return true;

         if (version.Minor != otherVersion.Minor) return false;
         if (versionMatch == BplVersionMatch.Minor) return true;

         if (version.Build != otherVersion.Build) return false;
         if (versionMatch == BplVersionMatch.Build) return true;

         return (version == otherVersion);
      }
      #endregion

   }
}
