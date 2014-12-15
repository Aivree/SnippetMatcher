using System;
using System.Windows;
using System.Windows.Data;

namespace CivilWarriorsWpfApp.Converter
{
    public class DifficultyToBoolean : IValueConverter
    {
        public object Convert( object value, Type targetType, object parameter, System.Globalization.CultureInfo culture )
        {
            if ( parameter == null || string.IsNullOrEmpty( parameter.ToString() ) )
                return DependencyProperty.UnsetValue;

            if ( !Enum.IsDefined( value.GetType(), value ) )
                return DependencyProperty.UnsetValue;

            object parameterValue = Enum.Parse( value.GetType(), parameter.ToString() );

            return parameterValue.Equals( value );
        }

        public object ConvertBack( object value, Type targetType, object parameter, System.Globalization.CultureInfo culture )
        {
            if ( parameter == null || string.IsNullOrEmpty( parameter.ToString() ) )
                return DependencyProperty.UnsetValue;

            return Enum.Parse( targetType, parameter.ToString() );
        }
    }
}
