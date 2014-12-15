using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Data;
using System.Windows;
using System.Windows.Markup;

namespace TestBug
{
    /// <summary>
    /// an IValueConverter for checking if a bound enum is equal to a value passed in as a parameter.
    /// a true/false is returned for xaml
    /// This class can be used three ways:
    ///  * as a standard IValueConverter (declare in xaml resources)
    ///  * as a markup extension (declare as Converter={ns:BoolToEnumConverter})
    ///  * as a static reference (as Converter={x:Static BoolToEnumConverter.Instance})
    ///    * this way uses it as a singleton, the other ways instantiate a new instance for each binding
    /// </summary>
    public class BoolToEnumConverter : MarkupExtension, IValueConverter
    {
        #region IValueConverter Members

        public object Convert( object value, Type targetType, object parameter, System.Globalization.CultureInfo culture )
        {
            System.Diagnostics.Debug.WriteLine( string.Format( "wtf: {0}, {1}", value, parameter ) );

            var enumString = parameter as string;

            if( enumString == null )
                return DependencyProperty.UnsetValue;

            if( Enum.IsDefined( value.GetType(), value ) == false )
                return DependencyProperty.UnsetValue;

            var enumValue = Enum.Parse( value.GetType(), enumString );

            return enumValue.Equals( value );
        }

        /// <summary>
        /// This function simply returns the enum value of the parameter, it does not take the passed in variable (bool)
        /// into account.
        /// </summary>
        /// <param name="value"></param>
        /// <param name="targetType"></param>
        /// <param name="parameter"></param>
        /// <param name="culture"></param>
        /// <returns></returns>
        public object ConvertBack( object value, Type targetType, object parameter, System.Globalization.CultureInfo culture )
        {
            System.Diagnostics.Debug.WriteLine( string.Format( "wtfback: {0}, {1}", value, parameter ) );

            var enumString = parameter as string;

            if( enumString == null )
                return DependencyProperty.UnsetValue;

            if( (bool)value )
                return Enum.Parse( targetType, enumString );
            else
                return Binding.DoNothing;
        }

        #endregion

        static BoolToEnumConverter()
        {
        }

        /// <summary>
        /// lazy instance is created on first access
        /// </summary>
        private static Lazy<BoolToEnumConverter> instance = new Lazy<BoolToEnumConverter>( () => new BoolToEnumConverter() );

        /// <summary>
        /// With this we can use Converter={x:Static ns:BoolToEnumConverter.Instance} to get an instance of this IValueConverter.
        /// This way makes all xaml references use a singleton instance instead of creating multiple instances.
        /// </summary>
        public static BoolToEnumConverter Instance { get { return instance.Value; } }

        public override object ProvideValue( IServiceProvider serviceProvider )
        {
            return this;
        }
    }
}
