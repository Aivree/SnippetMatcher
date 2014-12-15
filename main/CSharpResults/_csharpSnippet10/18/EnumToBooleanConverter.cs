using System;
using System.Globalization;
using System.Windows;
using System.Windows.Data;

namespace Petroledger.Converters
{
    public class EnumToBooleanConverter : IValueConverter
    {
        #region IValueConverter Members

        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            var enumValue = parameter as string;

            if (enumValue == null
                || Enum.IsDefined(value.GetType(), value) == false)
                return DependencyProperty.UnsetValue;

            return Enum.Parse(value.GetType(), enumValue, true).Equals(value);
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            if (value.Equals(false)) return DependencyProperty.UnsetValue;
            var parameterString = parameter as string;
            return parameterString == null
                       ? DependencyProperty.UnsetValue
                       : Enum.Parse(targetType, parameterString, true);
        }

        #endregion
    }
}