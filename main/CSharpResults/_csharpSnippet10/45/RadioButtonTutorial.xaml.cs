using System;
using System.Globalization;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Data;

namespace WPFTesting.Tutorials.Windows
{
    /// <summary>
    /// Interaction logic for RadioButtonTutorial.xaml
    /// </summary>
    public partial class RadioButtonTutorial
    {
        private string _currentOption;
        public string SelectedValue
        {
            get { return _currentOption; }
            set
            {
                var asdf = "";
                _currentOption = value;
            }
        }

        public RadioButtonTutorial()
        {
            InitializeComponent();
        }
    }

    
    public enum MyLovelyEnum
    {
        FirstSelection,
        TheOtherSelection,
        YetAnotherOne
    };

    /// <summary>
    /// This converts the enum to a string so the radio can use it
    /// </summary>
    public class EnumBooleanConverter : IValueConverter
    {
        #region IValueConverter Members
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            string parameterString = parameter as string;
            if (parameterString == null)
                return DependencyProperty.UnsetValue;

            if (Enum.IsDefined(value.GetType(), value) == false)
                return DependencyProperty.UnsetValue;

            object parameterValue = Enum.Parse(value.GetType(), parameterString);

            return parameterValue.Equals(value);
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            string parameterString = parameter as string;
            if (parameterString == null)
                return DependencyProperty.UnsetValue;

            return Enum.Parse(targetType, parameterString);
        }
        #endregion
    }
}
